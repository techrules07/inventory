package com.eloiacs.aapta.Inventory.DBHandler;

import com.eloiacs.aapta.Inventory.Models.BaseModel;
import com.eloiacs.aapta.Inventory.Models.BillOfMaterialsRequestModel;
import com.eloiacs.aapta.Inventory.Models.ProductRequestModel;
import com.eloiacs.aapta.Inventory.Responses.BillOfMaterialsResponse;
import com.eloiacs.aapta.Inventory.Responses.ProductResponse;
import com.eloiacs.aapta.Inventory.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class ProductHandler {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Transactional
    public Boolean insertProduct(ProductRequestModel productRequestModel, String createdBy, List<String> imageUrls){

        String insertProductQuery = "insert into products(productName,statusType,category,subCategory,brand,unit,size,quantity,minPurchaseQuantity,barcodeType,barcodeNo,description,purchasePrice,gstPercentage,salesPrice,mrp,wholesalePrice,wholesaleGSTPercentage,threshold,billOfMaterials,freebie,freebieProduct,isActive,createdAt,createdBy) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,true,current_timestamp(),?)";
        String insertProductImagesQuery = "insert into productImages(productId,category,subCategory,brandId,imageUrl,isActive,createdBy,createdAt) values(?,?,?,?,?,true,?,current_timestamp())";
        String insertBillOfMaterialsQuery = "insert into billOfMaterials(productId,billOfMaterialProductId,quantity,cost,isActive,createdAt) values(?,?,?,?,true,current_timestamp())";
        String insertInventoryQuery = "INSERT INTO inventory(productId, category, subCategory, size, count, isActive, createdBy, createdAt) VALUES (?, ?, ?, ?, ?, true, ?, current_timestamp())";
        String checkProductQuery = "select id from products where isActive = true and productName = ? and statusType = ? and category = ? and subCategory = ? and brand = ? and unit = ? and size = ?";
        String updateProductQuery = "update products set quantity = quantity + ? where id = ?";
        String updateInventoryQuery = "update inventory set count = count + ?, createdBy = ? where productId = ?";

        // Check if product exists
        Integer existingProductId = null;

        existingProductId = jdbcTemplate.query(checkProductQuery, new Object[]{
                productRequestModel.getProductName(),
                productRequestModel.getStatusTypeId(),
                productRequestModel.getCategoryId(),
                productRequestModel.getSubCategoryId(),
                productRequestModel.getBrandId(),
                productRequestModel.getUnitId(),
                productRequestModel.getSizeId()
        }, new ResultSetExtractor<Integer>() {
            @Override
            public Integer extractData(ResultSet rs) throws SQLException, DataAccessException {
                if (rs.next()){
                    return rs.getInt("id");
                }
                return null;
            }
        });

        if (existingProductId != null) {
            // Product exists, update quantity and inventory
            jdbcTemplate.update(updateProductQuery, productRequestModel.getQuantity(), existingProductId);
            jdbcTemplate.update(updateInventoryQuery, productRequestModel.getQuantity(), createdBy, existingProductId);
            return true;
        } else {
            // Insert product and retrieve generated productId
            KeyHolder keyHolder = new GeneratedKeyHolder();

            int rowsAffected = jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(insertProductQuery, new String[]{"id"});
                ps.setString(1, productRequestModel.getProductName());
                ps.setInt(2, productRequestModel.getStatusTypeId());
                ps.setInt(3, productRequestModel.getCategoryId());
                ps.setInt(4, productRequestModel.getSubCategoryId());
                ps.setInt(5, productRequestModel.getBrandId());
                ps.setInt(6, productRequestModel.getUnitId());
                ps.setInt(7,productRequestModel.getSizeId());
                ps.setInt(8, productRequestModel.getQuantity());
                ps.setInt(9, productRequestModel.getMinPurchaseQuantity());
                ps.setInt(10, productRequestModel.getBarcodeType());
                ps.setString(11, productRequestModel.getBarcodeNo());
                ps.setString(12, productRequestModel.getDescription());
                int purchasePrice = productRequestModel.getPurchasePrice();
                int mrp = productRequestModel.getMrp();
                int salesPricePercentage = productRequestModel.getSalesPricePercentage();
                ps.setInt(13, purchasePrice);
                ps.setDouble(14, salesPricePercentage);
                int salesPrice = purchasePrice + (purchasePrice * salesPricePercentage / 100);
                if (salesPrice > mrp) {
                    salesPrice = mrp;
                }
                ps.setInt(15, salesPrice);
                ps.setInt(16, mrp);
                int wholeSalePercentage = productRequestModel.getWholesalePricePercentage();
                int wholeSalePrice = purchasePrice + (purchasePrice * wholeSalePercentage / 100);
                if (wholeSalePrice > mrp) {
                    wholeSalePrice = mrp;
                }
                ps.setInt(17, wholeSalePrice);
                ps.setDouble(18, wholeSalePercentage);
                ps.setInt(19, productRequestModel.getThreshold());
                ps.setBoolean(20, productRequestModel.getBillOfMaterials());
                ps.setBoolean(21, productRequestModel.getFreebie());
                ps.setInt(22, productRequestModel.getFreebieProductId());
                ps.setString(23, createdBy);
                return ps;
            }, keyHolder);

            if (rowsAffected > 0 && keyHolder.getKey() != null) {
                int productId = keyHolder.getKey().intValue();

                String eventName = "New product created";
                int eventType = 3;
                String eventInsertQuery = "INSERT INTO event (eventName, taskId, eventType, userId) VALUES (?, ?, ?, ?)";
                jdbcTemplate.update(eventInsertQuery, eventName, productId, eventType, createdBy);

                jdbcTemplate.update(insertInventoryQuery,
                        productId,
                        productRequestModel.getCategoryId(),
                        productRequestModel.getSubCategoryId(),
                        productRequestModel.getSizeId(),
                        productRequestModel.getQuantity(),
                        createdBy);


                if (imageUrls != null) {
                    for (String image : imageUrls) {
                        // Insert into productImages table
                        jdbcTemplate.update(insertProductImagesQuery,
                                productId,
                                productRequestModel.getCategoryId(),
                                productRequestModel.getSubCategoryId(),
                                productRequestModel.getBrandId(),
                                image,
                                createdBy);
                    }
                }

                List<BillOfMaterialsRequestModel> billOfMaterialsList = productRequestModel.getBillOfMaterialsList();

                if (billOfMaterialsList != null) {
                    for (BillOfMaterialsRequestModel billOfMaterials : billOfMaterialsList) {

                        jdbcTemplate.update(insertBillOfMaterialsQuery,
                                productId,
                                billOfMaterials.getBillOfMaterialsProductId(),
                                billOfMaterials.getBillOfMaterialsProductQuantity(),
                                billOfMaterials.getBillOfMaterialsProductCost());
                    }
                }

                return true;
            }
            else {
                return false;
            }
        }
    }

    @Transactional
    public Boolean updateProduct(ProductRequestModel productRequestModel, String createdBy, List<String> imageUrls){

        String updateProductQuery = "update products set productName = ?, statusType = ?, category = ?, subCategory = ?, brand = ?, unit = ?,size = ?, quantity = ?, minPurchaseQuantity = ?, barcodeType = ?, barcodeNo = ?, description = ?, purchasePrice = ?, gstPercentage = ?, salesPrice = ?, mrp = ?, wholesalePrice = ?, wholesaleGSTPercentage = ?, threshold = ?, billOfMaterials = ?, freebie = ?, freebieProduct = ?, isActive = true where id = ?";
        String updateInventoryQuery = "update inventory set count=?,createdBy=? where productId=? ";

        int purchasePrice = productRequestModel.getPurchasePrice();
        int mrp = productRequestModel.getMrp();
        int salesPricePercentage = productRequestModel.getSalesPricePercentage();

        int salesPrice = purchasePrice + (purchasePrice * salesPricePercentage / 100);
        if (salesPrice > mrp) {
            salesPrice = mrp;
        }

        int wholeSalePercentage = productRequestModel.getWholesalePricePercentage();

        int wholeSalePrice = purchasePrice + (purchasePrice * wholeSalePercentage / 100);
        if (wholeSalePrice > mrp) {
            wholeSalePrice = mrp;
        }

        int productId = productRequestModel.getProductId();

        jdbcTemplate.update(updateInventoryQuery,
                productRequestModel.getQuantity(),
                createdBy,
                productId
        );

        jdbcTemplate.update(updateProductQuery,
                productRequestModel.getProductName(),
                productRequestModel.getStatusTypeId(),
                productRequestModel.getCategoryId(),
                productRequestModel.getSubCategoryId(),
                productRequestModel.getBrandId(),
                productRequestModel.getUnitId(),
                productRequestModel.getSizeId(),
                productRequestModel.getQuantity(),
                productRequestModel.getMinPurchaseQuantity(),
                productRequestModel.getBarcodeType(),
                productRequestModel.getBarcodeNo(),
                productRequestModel.getDescription(),
                purchasePrice,
                salesPricePercentage,
                salesPrice,
                mrp,
                wholeSalePrice,
                wholeSalePercentage,
                productRequestModel.getThreshold(),
                productRequestModel.getBillOfMaterials(),
                productRequestModel.getFreebie(),
                productRequestModel.getFreebieProductId(),
                productId);

        if (productRequestModel.getImages() != null &&
                productRequestModel.getImages().stream().anyMatch(image -> image != null &&
                        !image.trim().isEmpty())){

            String deleteImagesQuery = "update productImages set isActive = false where productId = ?";

            int rowsAffected = jdbcTemplate.update(deleteImagesQuery,
                    productId);

            if (rowsAffected !=0){


                for (String image : imageUrls) {

                    String insertProductImagesQuery = "insert into productImages(productId,category,subCategory,brandId,imageUrl,isActive,createdBy,createdAt) values(?,?,?,?,?,true,?,current_timestamp())";

                    jdbcTemplate.update(insertProductImagesQuery,
                            productId,
                            productRequestModel.getCategoryId(),
                            productRequestModel.getSubCategoryId(),
                            productRequestModel.getBrandId(),
                            image,
                            createdBy);
                }
            }
        }

        if (productRequestModel.getBillOfMaterialsList() != null &&
                !productRequestModel.getBillOfMaterialsList().isEmpty() &&
                productRequestModel.getBillOfMaterialsList().stream().anyMatch(bom -> bom.getBillOfMaterialsProductId() != 0 ||
                        bom.getBillOfMaterialsProductQuantity() != 0 ||
                        bom.getBillOfMaterialsProductCost() != 0))
        {

            String deleteBillOfMaterialsQuery = "update billOfMaterials set isActive = false where productId = ? ";

            int rowsAffected = jdbcTemplate.update(deleteBillOfMaterialsQuery,
                    productId);

            if (rowsAffected !=0){

                List<BillOfMaterialsRequestModel> billOfMaterialsList = productRequestModel.getBillOfMaterialsList();

                for (BillOfMaterialsRequestModel billOfMaterials : billOfMaterialsList) {

                    String insertBillOfMaterialsQuery = "insert into billOfMaterials(productId,billOfMaterialProductId,quantity,cost,isActive,createdAt) values(?,?,?,?,true,current_timestamp())";

                    jdbcTemplate.update(insertBillOfMaterialsQuery,
                            productId,
                            billOfMaterials.getBillOfMaterialsProductId(),
                            billOfMaterials.getBillOfMaterialsProductQuantity(),
                            billOfMaterials.getBillOfMaterialsProductCost());
                }
            }
        }

        return true;
    }

    @Transactional
    public Boolean deleteProduct(BaseModel baseModel){

        String deleteProductQuery = "update products set isActive = false where id = ?";
        String deleteImagesQuery = "update productImages set isActive = false where productId = ?";
        String deleteBillOfMaterialsQuery = "update billOfMaterials set isActive = false where productId = ? ";

        int productId = baseModel.getRequestId();

        int rowsAffected = jdbcTemplate.update(deleteProductQuery,
                productId);

        if (rowsAffected !=0){

            jdbcTemplate.update(deleteImagesQuery,
                    productId);

            jdbcTemplate.update(deleteBillOfMaterialsQuery,
                    productId);

            return true;
        }
        else {
            return false;
        }
    }

    public List<ProductResponse> getProducts(String productName){

        StringBuilder getProductsQuery =new StringBuilder("select pd.*, st.statusType as status, c.category_name, sc.subCategoryName, b.brandName, ut.unitName,psize.size as sizeName, GROUP_CONCAT(bom.billOfMaterialProductId SEPARATOR ',') as billOfMaterialProductId, GROUP_CONCAT(bom.quantity SEPARATOR ',') as billOfMaterialQuantity, GROUP_CONCAT(bom.cost SEPARATOR ',') as billOfMaterialCost, GROUP_CONCAT(bomProduct.productName SEPARATOR ',') AS billOfMaterialProductName, fp.productName as freebieProductName, user.username as createdByUsername, GROUP_CONCAT(DISTINCT pi.imageUrl SEPARATOR ',') as images from products pd left join statusType st on st.id = pd.statusType left join category c on c.id = pd.category left join subcategory sc on sc.id = pd.subCategory left join brand b on b.id = pd.brand left join unitTable ut on ut.id = pd.unit left join billOfMaterials bom on bom.productId = pd.id and bom.isActive = true left join products bomProduct ON billOfMaterialProductId = bomProduct.id left join productSize psize on psize.id=pd.size left join products fp on fp.id = pd.freebieProduct left join users user on user.id = pd.createdBy left join productImages pi on pi.productId = pd.id and pi.isActive = true where pd.isActive = true ");
        if (productName != null && !productName.trim().isEmpty()) {
            getProductsQuery.append("and pd.productName LIKE ? ");
        }

        getProductsQuery.append("group by pd.id order by pd.id desc");

        return jdbcTemplate.query(getProductsQuery.toString(), new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                if (productName != null && !productName.trim().isEmpty()) {
                    ps.setString(1, "%" + productName + "%");
                }
            }
        }, new ResultSetExtractor<List<ProductResponse>>() {
            @Override
            public List<ProductResponse> extractData(ResultSet rs) throws SQLException, DataAccessException {
                if(rs.next()){

                    List<ProductResponse> productResponseList = new ArrayList<>();

                    do {
                        ProductResponse productResponse = new ProductResponse();

                        productResponse.setProductId(rs.getInt("id"));
                        productResponse.setProductName(rs.getString("productName"));
                        productResponse.setStatusTypeId(rs.getInt("statusType"));
                        productResponse.setStatusType(rs.getString("status"));
                        productResponse.setCategoryId(rs.getInt("category"));
                        productResponse.setCategory(rs.getString("category_name"));
                        productResponse.setSubCategoryId(rs.getInt("subCategory"));
                        productResponse.setSubCategory(rs.getString("subCategoryName"));
                        productResponse.setBrandId(rs.getInt("brand"));
                        productResponse.setBrand(rs.getString("brandName"));
                        productResponse.setUnitId(rs.getInt("unit"));
                        productResponse.setUnit(rs.getString("unitName"));
                        productResponse.setSizeId(rs.getInt("size"));
                        productResponse.setSize(rs.getString("sizeName"));
                        productResponse.setQuantity(rs.getInt("quantity"));
                        productResponse.setMinPurchaseQuantity(rs.getInt("minPurchaseQuantity"));
                        productResponse.setBarcodeType(rs.getInt("barcodeType"));
                        productResponse.setBarcodeNo(rs.getString("barcodeNo"));
                        productResponse.setDescription(rs.getString("description"));
                        productResponse.setPurchasePrice(rs.getInt("purchasePrice"));
                        productResponse.setSalesPricePercentage(rs.getInt("gstPercentage"));
                        productResponse.setSalesPrice(rs.getInt("salesPrice"));
                        productResponse.setMrp(rs.getInt("mrp"));
                        productResponse.setWholesalePrice(rs.getInt("wholesalePrice"));
                        productResponse.setWholesalePricePercentage(rs.getInt("wholesaleGSTPercentage"));
                        productResponse.setThreshold(rs.getInt("threshold"));
                        productResponse.setBillOfMaterials(rs.getBoolean("billOfMaterials"));

                        // Concatenated strings for bill of materials
                        String billOfMaterialsProductIdConcat = rs.getString("billOfMaterialProductId");
                        String billOfMaterialsProductNameConcat = rs.getString("billOfMaterialProductName");
                        String billOfMaterialsProductQuantityConcat = rs.getString("billOfMaterialQuantity");
                        String billOfMaterialsProductCostConcat = rs.getString("billOfMaterialCost");

                        // Initialize arrays to avoid NullPointerException
                        String[] billOfMaterialsProductIds = (billOfMaterialsProductIdConcat != null) ? billOfMaterialsProductIdConcat.split(",") : new String[0];
                        String[] billOfMaterialsProductNames = (billOfMaterialsProductNameConcat != null) ? billOfMaterialsProductNameConcat.split(",") : new String[0];
                        String[] billOfMaterialsProductQuantities = (billOfMaterialsProductQuantityConcat != null) ? billOfMaterialsProductQuantityConcat.split(",") : new String[0];
                        String[] billOfMaterialsProductCosts = (billOfMaterialsProductCostConcat != null) ? billOfMaterialsProductCostConcat.split(",") : new String[0];

                        // Create a list to hold the BillOfMaterialsResponse objects
                        List<BillOfMaterialsResponse> billOfMaterialsList = new ArrayList<>();

                        // Ensure all arrays have the same length before processing
                        if (billOfMaterialsProductIds.length == billOfMaterialsProductNames.length &&
                                billOfMaterialsProductNames.length == billOfMaterialsProductQuantities.length &&
                                billOfMaterialsProductQuantities.length == billOfMaterialsProductCosts.length) {

                            // Loop through the arrays to create BillOfMaterialsResponse objects
                            for (int i = 0; i < billOfMaterialsProductIds.length; i++) {
                                // Create a new instance for each bill of material
                                BillOfMaterialsResponse billOfMaterialsResponse = new BillOfMaterialsResponse();

                                // Set the values for each BillOfMaterialsResponse object
                                billOfMaterialsResponse.setBillOfMaterialsProductId(Integer.parseInt(billOfMaterialsProductIds[i]));
                                billOfMaterialsResponse.setBillOfMaterialsProductName(billOfMaterialsProductNames[i]);
                                billOfMaterialsResponse.setBillOfMaterialsProductQuantity(Integer.parseInt(billOfMaterialsProductQuantities[i]));
                                billOfMaterialsResponse.setBillOfMaterialsProductCost(Integer.parseInt(billOfMaterialsProductCosts[i]));

                                // Add the populated response object to the list
                                billOfMaterialsList.add(billOfMaterialsResponse);
                            }
                        }

                        // Set the populated list to the product response
                        productResponse.setBillOfMaterialsList(billOfMaterialsList);

                        productResponse.setFreebie(rs.getBoolean("freebie"));
                        productResponse.setFreebieProductId(rs.getInt("freebieProduct"));
                        productResponse.setFreebieProductName(rs.getString("freebieProductName"));
                        productResponse.setActive(rs.getBoolean("isActive"));
                        productResponse.setCreatedAt(Utils.convertDateToString(rs.getTimestamp("createdAt")));
                        productResponse.setCreatedById(rs.getInt("createdBy"));
                        productResponse.setCreatedBy(rs.getString("createdByUsername"));
                        String imagesConcat = rs.getString("images");
                        if (imagesConcat != null) {
                            List<String> images = Arrays.asList(imagesConcat.split(","));
                            productResponse.setImages(images);
                        } else {
                            productResponse.setImages(new ArrayList<>());
                        }

                        productResponseList.add(productResponse);
                    }
                    while (rs.next());

                    return productResponseList;
                }
                return null;
            }
        });
    }

    public ProductResponse getProductById(int id){

        String getProductByIdQuery = "select pd.*, st.statusType as status, c.category_name, sc.subCategoryName, b.brandName, ut.unitName, GROUP_CONCAT(bom.billOfMaterialProductId SEPARATOR ',') as billOfMaterialProductId, GROUP_CONCAT(bom.quantity SEPARATOR ',') as billOfMaterialQuantity, GROUP_CONCAT(bom.cost SEPARATOR ',') as billOfMaterialCost, GROUP_CONCAT(bomProduct.productName SEPARATOR ',') AS billOfMaterialProductName, fp.productName as freebieProductName, user.username as createdByUsername, GROUP_CONCAT(DISTINCT pi.imageUrl SEPARATOR ',') as images from products pd left join statusType st on st.id = pd.statusType left join category c on c.id = pd.category left join subcategory sc on sc.id = pd.subCategory left join brand b on b.id = pd.brand left join unitTable ut on ut.id = pd.unit left join billOfMaterials bom on bom.productId = pd.id and bom.isActive = true left join products bomProduct ON billOfMaterialProductId = bomProduct.id left join products fp on fp.id = pd.freebieProduct left join users user on user.id = pd.createdBy left join productImages pi on pi.productId = pd.id and pi.isActive = true where pd.isActive = true and pd.id = ? group by pd.id";

        return jdbcTemplate.query(getProductByIdQuery, new Object[]{id}, new ResultSetExtractor<ProductResponse>() {
            @Override
            public ProductResponse extractData(ResultSet rs) throws SQLException, DataAccessException {
                if (rs.next()){
                    ProductResponse productResponse = new ProductResponse();

                    productResponse.setProductId(rs.getInt("id"));
                    productResponse.setProductName(rs.getString("productName"));
                    productResponse.setStatusTypeId(rs.getInt("statusType"));
                    productResponse.setStatusType(rs.getString("status"));
                    productResponse.setCategoryId(rs.getInt("category"));
                    productResponse.setCategory(rs.getString("category_name"));
                    productResponse.setSubCategoryId(rs.getInt("subCategory"));
                    productResponse.setSubCategory(rs.getString("subCategoryName"));
                    productResponse.setBrandId(rs.getInt("brand"));
                    productResponse.setBrand(rs.getString("brandName"));
                    productResponse.setUnitId(rs.getInt("unit"));
                    productResponse.setUnit(rs.getString("unitName"));
                    productResponse.setQuantity(rs.getInt("quantity"));
                    productResponse.setMinPurchaseQuantity(rs.getInt("minPurchaseQuantity"));
                    productResponse.setBarcodeType(rs.getInt("barcodeType"));
                    productResponse.setBarcodeNo(rs.getString("barcodeNo"));
                    productResponse.setDescription(rs.getString("description"));
                    productResponse.setPurchasePrice(rs.getInt("purchasePrice"));
                    productResponse.setSalesPricePercentage(rs.getInt("gstPercentage"));
                    productResponse.setSalesPrice(rs.getInt("salesPrice"));
                    productResponse.setMrp(rs.getInt("mrp"));
                    productResponse.setWholesalePrice(rs.getInt("wholesalePrice"));
                    productResponse.setWholesalePricePercentage(rs.getInt("wholesaleGSTPercentage"));
                    productResponse.setThreshold(rs.getInt("threshold"));
                    productResponse.setBillOfMaterials(rs.getBoolean("billOfMaterials"));

                    // Concatenated strings for bill of materials
                    String billOfMaterialsProductIdConcat = rs.getString("billOfMaterialProductId");
                    String billOfMaterialsProductNameConcat = rs.getString("billOfMaterialProductName");
                    String billOfMaterialsProductQuantityConcat = rs.getString("billOfMaterialQuantity");
                    String billOfMaterialsProductCostConcat = rs.getString("billOfMaterialCost");

                    // Initialize arrays to avoid NullPointerException
                    String[] billOfMaterialsProductIds = (billOfMaterialsProductIdConcat != null) ? billOfMaterialsProductIdConcat.split(",") : new String[0];
                    String[] billOfMaterialsProductNames = (billOfMaterialsProductNameConcat != null) ? billOfMaterialsProductNameConcat.split(",") : new String[0];
                    String[] billOfMaterialsProductQuantities = (billOfMaterialsProductQuantityConcat != null) ? billOfMaterialsProductQuantityConcat.split(",") : new String[0];
                    String[] billOfMaterialsProductCosts = (billOfMaterialsProductCostConcat != null) ? billOfMaterialsProductCostConcat.split(",") : new String[0];

                    // Create a list to hold the BillOfMaterialsResponse objects
                    List<BillOfMaterialsResponse> billOfMaterialsList = new ArrayList<>();

                    // Ensure all arrays have the same length before processing
                    if (billOfMaterialsProductIds.length == billOfMaterialsProductNames.length &&
                            billOfMaterialsProductNames.length == billOfMaterialsProductQuantities.length &&
                            billOfMaterialsProductQuantities.length == billOfMaterialsProductCosts.length) {

                        // Loop through the arrays to create BillOfMaterialsResponse objects
                        for (int i = 0; i < billOfMaterialsProductIds.length; i++) {
                            // Create a new instance for each bill of material
                            BillOfMaterialsResponse billOfMaterialsResponse = new BillOfMaterialsResponse();

                            // Set the values for each BillOfMaterialsResponse object
                            billOfMaterialsResponse.setBillOfMaterialsProductId(Integer.parseInt(billOfMaterialsProductIds[i]));
                            billOfMaterialsResponse.setBillOfMaterialsProductName(billOfMaterialsProductNames[i]);
                            billOfMaterialsResponse.setBillOfMaterialsProductQuantity(Integer.parseInt(billOfMaterialsProductQuantities[i]));
                            billOfMaterialsResponse.setBillOfMaterialsProductCost(Integer.parseInt(billOfMaterialsProductCosts[i]));

                            // Add the populated response object to the list
                            billOfMaterialsList.add(billOfMaterialsResponse);
                        }
                    }

                    // Set the populated list to the product response
                    productResponse.setBillOfMaterialsList(billOfMaterialsList);
                    productResponse.setFreebie(rs.getBoolean("freebie"));
                    productResponse.setFreebieProductId(rs.getInt("freebieProduct"));
                    productResponse.setFreebieProductName(rs.getString("freebieProductName"));
                    productResponse.setActive(rs.getBoolean("isActive"));
                    productResponse.setCreatedAt(Utils.convertDateToString(rs.getTimestamp("createdAt")));
                    productResponse.setCreatedById(rs.getInt("createdBy"));
                    productResponse.setCreatedBy(rs.getString("createdByUsername"));
                    String imagesConcat = rs.getString("images");
                    if (imagesConcat != null) {
                        List<String> images = Arrays.asList(imagesConcat.split(","));
                        productResponse.setImages(images);
                    } else {
                        productResponse.setImages(new ArrayList<>());
                    }

                    return productResponse;
                }
                return null;
            }
        });
    }

    public ProductResponse getProductByBarcode(String barcode){

        String getProductByIdQuery = "select pd.*, st.statusType as status, c.category_name, sc.subCategoryName, b.brandName, ut.unitName, GROUP_CONCAT(bom.billOfMaterialProductId SEPARATOR ',') as billOfMaterialProductId, GROUP_CONCAT(bom.quantity SEPARATOR ',') as billOfMaterialQuantity, GROUP_CONCAT(bom.cost SEPARATOR ',') as billOfMaterialCost, GROUP_CONCAT(bomProduct.productName SEPARATOR ',') AS billOfMaterialProductName, fp.productName as freebieProductName, user.username as createdByUsername, GROUP_CONCAT(DISTINCT pi.imageUrl SEPARATOR ',') as images from products pd left join statusType st on st.id = pd.statusType left join category c on c.id = pd.category left join subcategory sc on sc.id = pd.subCategory left join brand b on b.id = pd.brand left join unitTable ut on ut.id = pd.unit left join billOfMaterials bom on bom.productId = pd.id and bom.isActive = true left join products bomProduct ON billOfMaterialProductId = bomProduct.id left join products fp on fp.id = pd.freebieProduct left join users user on user.id = pd.createdBy left join productImages pi on pi.productId = pd.id and pi.isActive = true where pd.isActive = true and pd.barcodeNo = ? group by pd.id";

        return jdbcTemplate.query(getProductByIdQuery, new Object[]{barcode}, new ResultSetExtractor<ProductResponse>() {
            @Override
            public ProductResponse extractData(ResultSet rs) throws SQLException, DataAccessException {
                if (rs.next()){
                    ProductResponse productResponse = new ProductResponse();

                    productResponse.setProductId(rs.getInt("id"));
                    productResponse.setProductName(rs.getString("productName"));
                    productResponse.setStatusTypeId(rs.getInt("statusType"));
                    productResponse.setStatusType(rs.getString("status"));
                    productResponse.setCategoryId(rs.getInt("category"));
                    productResponse.setCategory(rs.getString("category_name"));
                    productResponse.setSubCategoryId(rs.getInt("subCategory"));
                    productResponse.setSubCategory(rs.getString("subCategoryName"));
                    productResponse.setBrandId(rs.getInt("brand"));
                    productResponse.setBrand(rs.getString("brandName"));
                    productResponse.setUnitId(rs.getInt("unit"));
                    productResponse.setUnit(rs.getString("unitName"));
                    productResponse.setQuantity(rs.getInt("quantity"));
                    productResponse.setMinPurchaseQuantity(rs.getInt("minPurchaseQuantity"));
                    productResponse.setBarcodeType(rs.getInt("barcodeType"));
                    productResponse.setBarcodeNo(rs.getString("barcodeNo"));
                    productResponse.setDescription(rs.getString("description"));
                    productResponse.setPurchasePrice(rs.getInt("purchasePrice"));
                    productResponse.setSalesPricePercentage(rs.getInt("gstPercentage"));
                    productResponse.setSalesPrice(rs.getInt("salesPrice"));
                    productResponse.setMrp(rs.getInt("mrp"));
                    productResponse.setWholesalePrice(rs.getInt("wholesalePrice"));
                    productResponse.setWholesalePricePercentage(rs.getInt("wholesaleGSTPercentage"));
                    productResponse.setThreshold(rs.getInt("threshold"));
                    productResponse.setBillOfMaterials(rs.getBoolean("billOfMaterials"));

                    // Concatenated strings for bill of materials
                    String billOfMaterialsProductIdConcat = rs.getString("billOfMaterialProductId");
                    String billOfMaterialsProductNameConcat = rs.getString("billOfMaterialProductName");
                    String billOfMaterialsProductQuantityConcat = rs.getString("billOfMaterialQuantity");
                    String billOfMaterialsProductCostConcat = rs.getString("billOfMaterialCost");

                    // Initialize arrays to avoid NullPointerException
                    String[] billOfMaterialsProductIds = (billOfMaterialsProductIdConcat != null) ? billOfMaterialsProductIdConcat.split(",") : new String[0];
                    String[] billOfMaterialsProductNames = (billOfMaterialsProductNameConcat != null) ? billOfMaterialsProductNameConcat.split(",") : new String[0];
                    String[] billOfMaterialsProductQuantities = (billOfMaterialsProductQuantityConcat != null) ? billOfMaterialsProductQuantityConcat.split(",") : new String[0];
                    String[] billOfMaterialsProductCosts = (billOfMaterialsProductCostConcat != null) ? billOfMaterialsProductCostConcat.split(",") : new String[0];

                    // Create a list to hold the BillOfMaterialsResponse objects
                    List<BillOfMaterialsResponse> billOfMaterialsList = new ArrayList<>();

                    // Ensure all arrays have the same length before processing
                    if (billOfMaterialsProductIds.length == billOfMaterialsProductNames.length &&
                            billOfMaterialsProductNames.length == billOfMaterialsProductQuantities.length &&
                            billOfMaterialsProductQuantities.length == billOfMaterialsProductCosts.length) {

                        // Loop through the arrays to create BillOfMaterialsResponse objects
                        for (int i = 0; i < billOfMaterialsProductIds.length; i++) {
                            // Create a new instance for each bill of material
                            BillOfMaterialsResponse billOfMaterialsResponse = new BillOfMaterialsResponse();

                            // Set the values for each BillOfMaterialsResponse object
                            billOfMaterialsResponse.setBillOfMaterialsProductId(Integer.parseInt(billOfMaterialsProductIds[i]));
                            billOfMaterialsResponse.setBillOfMaterialsProductName(billOfMaterialsProductNames[i]);
                            billOfMaterialsResponse.setBillOfMaterialsProductQuantity(Integer.parseInt(billOfMaterialsProductQuantities[i]));
                            billOfMaterialsResponse.setBillOfMaterialsProductCost(Integer.parseInt(billOfMaterialsProductCosts[i]));

                            // Add the populated response object to the list
                            billOfMaterialsList.add(billOfMaterialsResponse);
                        }
                    }

                    // Set the populated list to the product response
                    productResponse.setBillOfMaterialsList(billOfMaterialsList);
                    productResponse.setFreebie(rs.getBoolean("freebie"));
                    productResponse.setFreebieProductId(rs.getInt("freebieProduct"));
                    productResponse.setFreebieProductName(rs.getString("freebieProductName"));
                    productResponse.setActive(rs.getBoolean("isActive"));
                    productResponse.setCreatedAt(Utils.convertDateToString(rs.getTimestamp("createdAt")));
                    productResponse.setCreatedById(rs.getInt("createdBy"));
                    productResponse.setCreatedBy(rs.getString("createdByUsername"));
                    String imagesConcat = rs.getString("images");
                    if (imagesConcat != null) {
                        List<String> images = Arrays.asList(imagesConcat.split(","));
                        productResponse.setImages(images);
                    } else {
                        productResponse.setImages(new ArrayList<>());
                    }

                    return productResponse;
                }
                return null;
            }
        });
    }

    public List<ProductResponse> getProductsByBarcodeOrName(String inputText){

        String getProductsQuery = "select pd.*, st.statusType as status, c.category_name, sc.subCategoryName, b.brandName, ut.unitName, GROUP_CONCAT(bom.billOfMaterialProductId SEPARATOR ',') as billOfMaterialProductId, GROUP_CONCAT(bom.quantity SEPARATOR ',') as billOfMaterialQuantity, GROUP_CONCAT(bom.cost SEPARATOR ',') as billOfMaterialCost, GROUP_CONCAT(bomProduct.productName SEPARATOR ',') AS billOfMaterialProductName, fp.productName as freebieProductName, user.username as createdByUsername, GROUP_CONCAT(DISTINCT pi.imageUrl SEPARATOR ',') as images from products pd left join statusType st on st.id = pd.statusType left join category c on c.id = pd.category left join subcategory sc on sc.id = pd.subCategory left join brand b on b.id = pd.brand left join unitTable ut on ut.id = pd.unit left join billOfMaterials bom on bom.productId = pd.id and bom.isActive = true left join products bomProduct ON billOfMaterialProductId = bomProduct.id left join products fp on fp.id = pd.freebieProduct left join users user on user.id = pd.createdBy left join productImages pi on pi.productId = pd.id and pi.isActive = true where pd.isActive = true and (pd.barcodeNo LIKE CONCAT('%', ?, '%') OR pd.productName LIKE CONCAT('%', ?, '%')) group by pd.id order by pd.id desc";

        return jdbcTemplate.query(getProductsQuery, new Object[]{inputText,inputText}, new ResultSetExtractor<List<ProductResponse>>() {
            @Override
            public List<ProductResponse> extractData(ResultSet rs) throws SQLException, DataAccessException {
                if(rs.next()){

                    List<ProductResponse> productResponseList = new ArrayList<>();

                    do {
                        ProductResponse productResponse = new ProductResponse();

                        productResponse.setProductId(rs.getInt("id"));
                        productResponse.setProductName(rs.getString("productName"));
                        productResponse.setStatusTypeId(rs.getInt("statusType"));
                        productResponse.setStatusType(rs.getString("status"));
                        productResponse.setCategoryId(rs.getInt("category"));
                        productResponse.setCategory(rs.getString("category_name"));
                        productResponse.setSubCategoryId(rs.getInt("subCategory"));
                        productResponse.setSubCategory(rs.getString("subCategoryName"));
                        productResponse.setBrandId(rs.getInt("brand"));
                        productResponse.setBrand(rs.getString("brandName"));
                        productResponse.setUnitId(rs.getInt("unit"));
                        productResponse.setUnit(rs.getString("unitName"));
                        productResponse.setQuantity(rs.getInt("quantity"));
                        productResponse.setMinPurchaseQuantity(rs.getInt("minPurchaseQuantity"));
                        productResponse.setBarcodeType(rs.getInt("barcodeType"));
                        productResponse.setBarcodeNo(rs.getString("barcodeNo"));
                        productResponse.setDescription(rs.getString("description"));
                        productResponse.setPurchasePrice(rs.getInt("purchasePrice"));
                        productResponse.setSalesPricePercentage(rs.getInt("gstPercentage"));
                        productResponse.setSalesPrice(rs.getInt("salesPrice"));
                        productResponse.setMrp(rs.getInt("mrp"));
                        productResponse.setWholesalePrice(rs.getInt("wholesalePrice"));
                        productResponse.setWholesalePricePercentage(rs.getInt("wholesaleGSTPercentage"));
                        productResponse.setThreshold(rs.getInt("threshold"));
                        productResponse.setBillOfMaterials(rs.getBoolean("billOfMaterials"));

                        // Concatenated strings for bill of materials
                        String billOfMaterialsProductIdConcat = rs.getString("billOfMaterialProductId");
                        String billOfMaterialsProductNameConcat = rs.getString("billOfMaterialProductName");
                        String billOfMaterialsProductQuantityConcat = rs.getString("billOfMaterialQuantity");
                        String billOfMaterialsProductCostConcat = rs.getString("billOfMaterialCost");

                        // Initialize arrays to avoid NullPointerException
                        String[] billOfMaterialsProductIds = (billOfMaterialsProductIdConcat != null) ? billOfMaterialsProductIdConcat.split(",") : new String[0];
                        String[] billOfMaterialsProductNames = (billOfMaterialsProductNameConcat != null) ? billOfMaterialsProductNameConcat.split(",") : new String[0];
                        String[] billOfMaterialsProductQuantities = (billOfMaterialsProductQuantityConcat != null) ? billOfMaterialsProductQuantityConcat.split(",") : new String[0];
                        String[] billOfMaterialsProductCosts = (billOfMaterialsProductCostConcat != null) ? billOfMaterialsProductCostConcat.split(",") : new String[0];

                        // Create a list to hold the BillOfMaterialsResponse objects
                        List<BillOfMaterialsResponse> billOfMaterialsList = new ArrayList<>();

                        // Ensure all arrays have the same length before processing
                        if (billOfMaterialsProductIds.length == billOfMaterialsProductNames.length &&
                                billOfMaterialsProductNames.length == billOfMaterialsProductQuantities.length &&
                                billOfMaterialsProductQuantities.length == billOfMaterialsProductCosts.length) {

                            // Loop through the arrays to create BillOfMaterialsResponse objects
                            for (int i = 0; i < billOfMaterialsProductIds.length; i++) {
                                // Create a new instance for each bill of material
                                BillOfMaterialsResponse billOfMaterialsResponse = new BillOfMaterialsResponse();

                                // Set the values for each BillOfMaterialsResponse object
                                billOfMaterialsResponse.setBillOfMaterialsProductId(Integer.parseInt(billOfMaterialsProductIds[i]));
                                billOfMaterialsResponse.setBillOfMaterialsProductName(billOfMaterialsProductNames[i]);
                                billOfMaterialsResponse.setBillOfMaterialsProductQuantity(Integer.parseInt(billOfMaterialsProductQuantities[i]));
                                billOfMaterialsResponse.setBillOfMaterialsProductCost(Integer.parseInt(billOfMaterialsProductCosts[i]));

                                // Add the populated response object to the list
                                billOfMaterialsList.add(billOfMaterialsResponse);
                            }
                        }

                        // Set the populated list to the product response
                        productResponse.setBillOfMaterialsList(billOfMaterialsList);

                        productResponse.setFreebie(rs.getBoolean("freebie"));
                        productResponse.setFreebieProductId(rs.getInt("freebieProduct"));
                        productResponse.setFreebieProductName(rs.getString("freebieProductName"));
                        productResponse.setActive(rs.getBoolean("isActive"));
                        productResponse.setCreatedAt(Utils.convertDateToString(rs.getTimestamp("createdAt")));
                        productResponse.setCreatedById(rs.getInt("createdBy"));
                        productResponse.setCreatedBy(rs.getString("createdByUsername"));
                        String imagesConcat = rs.getString("images");
                        if (imagesConcat != null) {
                            List<String> images = Arrays.asList(imagesConcat.split(","));
                            productResponse.setImages(images);
                        } else {
                            productResponse.setImages(new ArrayList<>());
                        }

                        productResponseList.add(productResponse);
                    }
                    while (rs.next());

                    return productResponseList;
                }
                return null;
            }
        });
    }

    public Boolean productExistByName(String name){

        String productExistByNameQuery = "select count(*) from products where productName = ? and isActive = true";

        int count = jdbcTemplate.queryForObject(productExistByNameQuery, new Object[]{name}, Integer.class);

        return count > 0;
    }

    public Boolean productExistById(int id){

        String productExistByIdQuery = "select count(*) from products where id = ? and isActive = true";

        int count = jdbcTemplate.queryForObject(productExistByIdQuery, new Object[]{id}, Integer.class);

        return count > 0;
    }

    public Boolean statusTypeExistById(int id){

        String statusTypeExistByIdQuery = "select count(*) from statusType where id = ? and isActive = true";

        int count = jdbcTemplate.queryForObject(statusTypeExistByIdQuery, new Object[]{id}, Integer.class);

        return count > 0;
    }

    public Boolean categoryExistById(int id){

        String categoryExistByIdQuery = "select count(*) from category where id = ? and isActive = true";

        int count = jdbcTemplate.queryForObject(categoryExistByIdQuery, new Object[]{id}, Integer.class);

        return count > 0;
    }

    public Boolean subCategoryExistById(int id){

        String subCategoryExistByIdQuery = "select count(*) from subcategory where id = ? and isActive = true";

        int count = jdbcTemplate.queryForObject(subCategoryExistByIdQuery, new Object[]{id}, Integer.class);

        return count > 0;
    }

    public Boolean brandExistById(int id){

        String brandExistByIdQuery = "select count(*) from brand where id = ? and isActive = true";

        int count = jdbcTemplate.queryForObject(brandExistByIdQuery, new Object[]{id}, Integer.class);

        return count > 0;
    }

    public Boolean unitExistById(int id){

        String unitExistByIdQuery = "select count(*) from unitTable where id = ? and isActive = true";

        int count = jdbcTemplate.queryForObject(unitExistByIdQuery, new Object[]{id}, Integer.class);

        return count > 0;
    }
}
