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
import java.util.stream.Collectors;

@Service
public class ProductHandler {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Transactional
    public Boolean insertProduct(ProductRequestModel productRequestModel, String createdBy, List<String> imageUrls){

        String insertProductQuery = "insert into products(productName,statusType,category,subCategory,brand,unit,quantity,minPurchaseQuantity,barcodeType,barcodeNo,description,purchasePrice,gstPercentage,salesPrice,mrp,wholesalePrice,wholesaleGSTPercentage,threshold,billOfMaterials,freebie,freebieProduct,isActive,createdAt,createdBy) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,true,current_timestamp(),?)";
        String insertProductImagesQuery = "insert into productImages(productId,category,subCategory,brandId,imageUrl,isActive,createdBy,createdAt) values(?,?,?,?,?,true,?,current_timestamp())";
        String insertBillOfMaterialsQuery = "insert into billOfMaterials(productId,billOfMaterialProductId,quantity,cost,isActive,createdAt) values(?,?,?,?,true,current_timestamp())";

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
            ps.setInt(7, productRequestModel.getQuantity());
            ps.setInt(8, productRequestModel.getMinPurchaseQuantity());
            ps.setInt(9, productRequestModel.getBarcodeType());
            ps.setString(10, productRequestModel.getBarcodeNo());
            ps.setString(11, productRequestModel.getDescription());
            int purchasePrice = productRequestModel.getPurchasePrice();
            int mrp = productRequestModel.getMrp();
            int salesPricePercentage = productRequestModel.getSalesPricePercentage();
            ps.setInt(12, purchasePrice);
            ps.setDouble(13, salesPricePercentage);
            int salesPrice = purchasePrice + (purchasePrice * salesPricePercentage / 100);
            if (salesPrice > mrp) {
                salesPrice = mrp;
            }
            ps.setInt(14, salesPrice);
            ps.setInt(15, mrp);
            int wholeSalePercentage = productRequestModel.getWholesalePricePercentage();
            int wholeSalePrice = purchasePrice + (purchasePrice * wholeSalePercentage / 100);
            if (wholeSalePrice > mrp) {
                wholeSalePrice = mrp;
            }
            ps.setInt(16, wholeSalePrice);
            ps.setDouble(17, wholeSalePercentage);
            ps.setInt(18, productRequestModel.getThreshold());
            ps.setBoolean(19, productRequestModel.getBillOfMaterials());
            ps.setBoolean(20, productRequestModel.getFreebie());
            ps.setInt(21, productRequestModel.getFreebieProductId());
            ps.setString(22, createdBy);
            return ps;
        }, keyHolder);

        // Check if the product was added successfully
        if (rowsAffected > 0 && keyHolder.getKey() != null) {

            // Retrieve the generated productId
            int productId = keyHolder.getKey().intValue();

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

    @Transactional
    public Boolean updateProduct(ProductRequestModel productRequestModel, String createdBy, List<String> imageUrls){

        String updateProductQuery = "update products set productName = ?, statusType = ?, category = ?, subCategory = ?, brand = ?, unit = ?, quantity = ?, minPurchaseQuantity = ?, barcodeType = ?, barcodeNo = ?, description = ?, purchasePrice = ?, gstPercentage = ?, salesPrice = ?, mrp = ?, wholesalePrice = ?, wholesaleGSTPercentage = ?, threshold = ?, billOfMaterials = ?, freebie = ?, freebieProduct = ?, isActive = true where id = ?";

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

        jdbcTemplate.update(updateProductQuery,
                productRequestModel.getProductName(),
                productRequestModel.getStatusTypeId(),
                productRequestModel.getCategoryId(),
                productRequestModel.getSubCategoryId(),
                productRequestModel.getBrandId(),
                productRequestModel.getUnitId(),
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

    public List<ProductResponse> getProducts(){

        String getProductsQuery = "select pd.*, st.statusType as status, c.category_name, sc.subCategoryName, b.brandName, ut.unitName, GROUP_CONCAT(bom.billOfMaterialProductId SEPARATOR ',') as billOfMaterialProductId, GROUP_CONCAT(bom.quantity SEPARATOR ',') as billOfMaterialQuantity, GROUP_CONCAT(bom.cost SEPARATOR ',') as billOfMaterialCost, GROUP_CONCAT(bomProduct.productName SEPARATOR ',') AS billOfMaterialProductName, fp.productName as freebieProductName, user.username as createdByUsername, GROUP_CONCAT(DISTINCT pi.imageUrl SEPARATOR ',') as images from products pd left join statusType st on st.id = pd.statusType left join category c on c.id = pd.category left join subcategory sc on sc.id = pd.subCategory left join brand b on b.id = pd.brand left join unitTable ut on ut.id = pd.unit left join billOfMaterials bom on bom.productId = pd.id and bom.isActive = true left join products bomProduct ON billOfMaterialProductId = bomProduct.id left join products fp on fp.id = pd.freebieProduct left join users user on user.id = pd.createdBy left join productImages pi on pi.productId = pd.id and pi.isActive = true where pd.isActive = true group by pd.id order by pd.id desc";

        return jdbcTemplate.query(getProductsQuery, new ResultSetExtractor<List<ProductResponse>>() {
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
}
