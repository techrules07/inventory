package com.eloiacs.aapta.Inventory.DBHandler;

import com.eloiacs.aapta.Inventory.Models.ProductRequestModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.util.List;

@Service
public class ProductHandler {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public Boolean insertProduct(ProductRequestModel productRequestModel, String createdBy, List<String> imageUrls){

        String insertProductQuery = "insert into products(productName,statusType,category,subCategory,brand,unit,quantity,minPurchaseQuantity,barcodeType,barcodeNo,description,purchasePrice,gstPercentage,salesPrice,mrp,wholesalePrice,wholesaleGSTPercentage,threshold,billOfMaterials,freebie,freebieProduct,isActive,createdAt,createdBy) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,true,current_timestamp(),?)";
        String insertProductImagesQuery = "insert into productImages(productId,category,subCategory,brandId,imageUrl,isActive,createdBy,createdAt) values(?,?,?,?,?,true,?,current_timestamp())";

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
        if (rowsAffected > 0) {

            // Retrieve the generated productId
            int productId = keyHolder.getKey().intValue();

            for(String image : imageUrls) {
                // Insert into productImages table
                jdbcTemplate.update(insertProductImagesQuery,
                        productId,
                        productRequestModel.getCategoryId(),
                        productRequestModel.getSubCategoryId(),
                        productRequestModel.getBrandId(),
                        image,
                        createdBy);

                return true;
            }
        }

        // If product insertion failed, return false
        return false;
    }
}
