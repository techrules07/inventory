package com.eloiacs.aapta.Inventory.DBHandler;

import com.eloiacs.aapta.Inventory.Models.PurchaseRequestModel;
import com.eloiacs.aapta.Inventory.Responses.ProductResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class PurchaseHandler {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    ProductHandler productHandler;

    public Boolean insertPurchase(PurchaseRequestModel purchaseRequestModel, String createdBy){

        String insertPurchaseQuery = "insert into purchase(productId,quantity,purchasePrice,totalAmount,mrp,salesPercentage,salesGstPercentage,salesPrice,wholesalePercentage,wholesaleGstPercentage,wholesalePrice,createdBy) values(?,?,?,?,?,?,?,?,?,?,?,?)";
        String insertInventoryQuery = "INSERT INTO inventory(productId, category, subCategory, size, count, isActive, createdBy, createdAt) VALUES (?, ?, ?, ?, ?, true, ?, current_timestamp())";
        String updateInventoryQuery = "update inventory set count = count + ?, createdBy = ? where productId = ?";
        String checkInventoryExistQuery = "select count(*) from inventory where isActive = true and productId = ?";

        double totalAmount = Math.round(purchaseRequestModel.getPurchasePrice() * purchaseRequestModel.getQuantity());

        double salesPrice = 0;
        double wholesalesPrice = 0;

        if (purchaseRequestModel.getSalesPercentage() == 0){
            salesPrice = purchaseRequestModel.getSalesPrice();
        }
        else{
            salesPrice = Math.round(purchaseRequestModel.getPurchasePrice() + (purchaseRequestModel.getPurchasePrice() * purchaseRequestModel.getSalesPercentage() /100));
            salesPrice = Math.min(salesPrice, purchaseRequestModel.getMrp());
        }

        if (purchaseRequestModel.getWholesalePercentage() == 0){
            wholesalesPrice = purchaseRequestModel.getWholesalePrice();
        }
        else{
            wholesalesPrice = Math.round(purchaseRequestModel.getPurchasePrice() + (purchaseRequestModel.getPurchasePrice() * purchaseRequestModel.getWholesalePercentage() /100));
            wholesalesPrice = Math.min(wholesalesPrice, purchaseRequestModel.getMrp());
        }

        int insertedPurchase = jdbcTemplate.update(insertPurchaseQuery,
                purchaseRequestModel.getProductId(),
                purchaseRequestModel.getQuantity(),
                purchaseRequestModel.getPurchasePrice(),
                totalAmount,
                purchaseRequestModel.getMrp(),
                purchaseRequestModel.getSalesPercentage(),
                purchaseRequestModel.getSalesGstPercentage(),
                salesPrice,
                purchaseRequestModel.getWholesalePercentage(),
                purchaseRequestModel.getWholesaleGstPercentage(),
                wholesalesPrice,
                createdBy);

        if (insertedPurchase !=0){

            int inventoryExists = jdbcTemplate.update(checkInventoryExistQuery,
                    purchaseRequestModel.getProductId());

            if (inventoryExists !=0){
                ProductResponse productResponse = productHandler.getProductById(purchaseRequestModel.getProductId());
                jdbcTemplate.update(insertInventoryQuery,
                        purchaseRequestModel.getProductId(),
                        productResponse.getCategoryId(),
                        productResponse.getSubCategoryId(),
                        productResponse.getSizeId(),
                        purchaseRequestModel.getQuantity(),
                        createdBy);
            }
            else {
                jdbcTemplate.update(updateInventoryQuery,
                        purchaseRequestModel.getQuantity(),
                        createdBy,
                        purchaseRequestModel.getProductId());
            }
        }

        return true;
    }
}
