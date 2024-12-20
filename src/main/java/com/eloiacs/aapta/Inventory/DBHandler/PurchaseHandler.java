package com.eloiacs.aapta.Inventory.DBHandler;

import com.eloiacs.aapta.Inventory.Models.PurchaseOrderRequestModel;
import com.eloiacs.aapta.Inventory.Models.PurchaseRequestModel;
import com.eloiacs.aapta.Inventory.Responses.ProductResponse;
import com.eloiacs.aapta.Inventory.Responses.PurchaseItemsResponse;
import com.eloiacs.aapta.Inventory.Responses.PurchaseResponse;
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
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class PurchaseHandler {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    ProductHandler productHandler;

    public String generatePurchaseOrderId(int previousId) {
        Date date = new Date();
        SimpleDateFormat sdf  = new SimpleDateFormat("yyyyMMdd");
        String dt = sdf.format(date);
        int nextId = 0;
        if (previousId == 0) {
            nextId = 1;
        }
        else {
            nextId = previousId + 1;
        }
        return "PUROD-" + (9999 + nextId) + dt;
    }

    public int findLastPurchaseOrderId() {
        String query = "SELECT id FROM purchaseOrder order BY id DESC LIMIT 1";

        return jdbcTemplate.query(query, new ResultSetExtractor<Integer>() {
            @Override
            public Integer extractData(ResultSet rs) throws SQLException, DataAccessException {
                if (rs.next()) {
                    return rs.getInt("id");
                }
                return 0;
            }
        });
    }

    @Transactional
    public String insertPurchaseOrder(PurchaseOrderRequestModel purchaseOrderRequestModel, String createdBy, String invoiceUrl){

        String purchaseOrderId = generatePurchaseOrderId(findLastPurchaseOrderId());

        String insertPurchaseOrderQuery = "insert into purchaseOrder (orderId,supplier,gstAmount,gstPercentage,cgst,sgst,purchaseDate,invoiceId,invoiceUrl,totalAmount,createdby,createdAt) values (?,?,0,0,0,0,?,?,?,?,?,current_timestamp())";
        String insertPurchaseItemsQuery = "insert into purchaseItems(orderId, productId, qty, purchasePrice, createdAt) values(?,?,?,?,current_timestamp())";
        String insertProductPriceQuery = "insert into productPrice(productId,category,subCategory,size,mrp,salesPrice,salesPercentage,wholesalePrice,wholesalePercentage,createdAt) values(?,?,?,?,?,?,?,?,?,current_timestamp())";
        String insertInventoryQuery = "INSERT INTO inventory(productId, category, subCategory, size, count, isActive, createdBy, createdAt) VALUES (?, ?, ?, ?, ?, true, ?, current_timestamp())";
        String updateInventoryQuery = "update inventory set count = count + ?, createdBy = ? where productId = ?";
        String checkInventoryExistQuery = "select count(*) from inventory where isActive = true and productId = ?";
        String eventInsertQuery = "INSERT INTO event (eventName, taskId, eventType, userId) VALUES (?, ?, ?, ?)";

        double totalAmount = 0;

        List<PurchaseRequestModel> purchaseItemsList = purchaseOrderRequestModel.getPurchaseItems();

        if (purchaseItemsList != null) {
            for (PurchaseRequestModel purchaseItem : purchaseItemsList) {
                totalAmount += purchaseItem.getPurchasePrice() * purchaseItem.getQuantity();
            }
        }

        KeyHolder keyHolder = new GeneratedKeyHolder();

        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        DateTimeFormatter targetFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate purchaseDate = LocalDate.parse(purchaseOrderRequestModel.getPurchaseDate().replace("/", "-"), inputFormatter);
        String formattedDate = purchaseDate.format(targetFormatter);

        final double amount = Utils.roundToTwoDecimalPlaces(totalAmount);

        int rowsAffected = jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(insertPurchaseOrderQuery, new String[]{"id"});
            ps.setString(1,purchaseOrderId);
            ps.setInt(2,purchaseOrderRequestModel.getSupplierId());
            ps.setString(3,formattedDate);
            ps.setString(4,purchaseOrderRequestModel.getInvoiceId());
            ps.setString(5,invoiceUrl);
            ps.setDouble(6,amount);
            ps.setString(7,createdBy);
            return ps;
                }, keyHolder);

        if (rowsAffected > 0 && keyHolder.getKey() != null){

            int purchaseId = keyHolder.getKey().intValue();

            String eventName = "New purchase ordered";
            int eventType = 10;

            jdbcTemplate.update(eventInsertQuery, eventName, purchaseId, eventType, createdBy);

            if (purchaseItemsList != null){
                for (PurchaseRequestModel purchaseItem : purchaseItemsList){

                    double purchasePrice = Utils.roundToTwoDecimalPlaces(purchaseItem.getPurchasePrice());
                    double salesPercentage = Utils.roundToTwoDecimalPlaces(purchaseItem.getSalesPercentage());
                    double wholesalesPercentage = Utils.roundToTwoDecimalPlaces(purchaseItem.getWholesalePercentage());
                    double mrp = Utils.roundToTwoDecimalPlaces(purchaseItem.getMrp());

                    int insertedPurchaseItem = jdbcTemplate.update(insertPurchaseItemsQuery,
                            purchaseOrderId,
                            purchaseItem.getProductId(),
                            purchaseItem.getQuantity(),
                            purchasePrice);

                    if (insertedPurchaseItem > 0){

                        double salesPrice = 0;
                        double wholesalesPrice = 0;

                        ProductResponse productResponse = productHandler.getProductById(purchaseItem.getProductId());

                        if (salesPercentage == 0){
                            salesPrice = Utils.roundToTwoDecimalPlaces(purchaseItem.getSalesPrice());
                        }
                        else{
                            salesPrice = purchasePrice + (purchasePrice * salesPercentage /100);
//                            salesPrice = Math.min(salesPrice, mrp);
                        }

                        if (wholesalesPercentage == 0){
                            wholesalesPrice = Utils.roundToTwoDecimalPlaces(purchaseItem.getWholesalePrice());
                        }
                        else{
                            wholesalesPrice = purchasePrice + (purchasePrice * wholesalesPercentage /100);
//                            wholesalesPrice = Math.min(wholesalesPrice, mrp);
                        }

                        salesPrice = Utils.roundToTwoDecimalPlaces(salesPrice);
                        wholesalesPrice = Utils.roundToTwoDecimalPlaces(wholesalesPrice);

                        String checkProductPriceExistQuery = "SELECT COUNT(*) FROM productPrice WHERE productId = ? AND category = ? AND subCategory = ? and size = ?";
                        int productPriceExists = jdbcTemplate.queryForObject(checkProductPriceExistQuery, Integer.class, purchaseItem.getProductId(), productResponse.getCategoryId(), productResponse.getSubCategoryId(), productResponse.getSizeId());

                        if (productPriceExists > 0) {
                            String updateProductPriceQuery = "UPDATE productPrice SET mrp = ?, salesPrice = ?, salesPercentage = ?, wholesalePrice = ?, wholesalePercentage = ? WHERE productId = ? AND category = ? AND subCategory = ? and size = ?";

                            jdbcTemplate.update(updateProductPriceQuery,
                                    Utils.roundToTwoDecimalPlaces(purchaseItem.getMrp()),
                                    salesPrice,
                                    salesPercentage,
                                    wholesalesPrice,
                                    wholesalesPercentage,
                                    purchaseItem.getProductId(),
                                    productResponse.getCategoryId(),
                                    productResponse.getSubCategoryId(),
                                    productResponse.getSizeId());
                        } else {
                            int insertedProductPrice = jdbcTemplate.update(insertProductPriceQuery,
                                    purchaseItem.getProductId(),
                                    productResponse.getCategoryId(),
                                    productResponse.getSubCategoryId(),
                                    productResponse.getSizeId(),
                                    mrp,
                                    salesPrice,
                                    salesPercentage,
                                    wholesalesPrice,
                                    wholesalesPercentage);

                            if (insertedProductPrice > 0){

                                int inventoryExists = jdbcTemplate.queryForObject(checkInventoryExistQuery,
                                        Integer.class, purchaseItem.getProductId());

                                if (inventoryExists == 0){

                                    KeyHolder inventoryKeyHolder = new GeneratedKeyHolder();

                                    int insertedInventory = jdbcTemplate.update(connection -> {
                                        PreparedStatement ps = connection.prepareStatement(insertInventoryQuery, new String[]{"id"});
                                        ps.setInt(1,purchaseItem.getProductId());
                                        ps.setInt(2,productResponse.getCategoryId());
                                        ps.setInt(3,productResponse.getSubCategoryId());
                                        ps.setInt(4,productResponse.getSizeId());
                                        ps.setInt(5,purchaseItem.getQuantity());
                                        ps.setString(6,createdBy);
                                        return ps;
                                    }, inventoryKeyHolder);

                                    if (insertedInventory > 0){
                                        int inventoryId = inventoryKeyHolder.getKey().intValue();

                                        String insertInventoryEvent = "New product added to inventory";
                                        int inventoryEventType = 9;

                                        jdbcTemplate.update(eventInsertQuery, insertInventoryEvent, inventoryId, inventoryEventType, createdBy);
                                    }
                                }
                                else {
                                    jdbcTemplate.update(updateInventoryQuery,
                                            purchaseItem.getQuantity(),
                                            createdBy,
                                            purchaseItem.getProductId());
                                }
                            }
                        }
                    }
                }
            }

            return purchaseOrderId;
        }

        return null;
    }

    public List<PurchaseResponse> getPurchase(){

        String getPurchaseQuery = "select po.*, s.name, usr.username, pi.id as itemId, pi.orderId as itemOrderId, pi.productId, pi.qty, pi.purchasePrice, pi.createdAt as itemCreatedAt from purchaseOrder po left join users usr on usr.id = po.createdBy left join supplier s on s.id = po.supplier left join purchaseItems pi on pi.orderId = po.orderId order by po.orderId desc";

        return jdbcTemplate.query(getPurchaseQuery, new ResultSetExtractor<List<PurchaseResponse>>() {
            @Override
            public List<PurchaseResponse> extractData(ResultSet rs) throws SQLException, DataAccessException {

                Map<String, PurchaseResponse> purchaseResponseMap = new LinkedHashMap<>();

                while (rs.next()) {
                    String orderId = rs.getString("orderId");
                    // If the purchase order does not exist in the map, create a new one
                    PurchaseResponse purchaseResponse = purchaseResponseMap.get(orderId);
                    if (purchaseResponse == null) {
                        purchaseResponse = new PurchaseResponse();
                        purchaseResponse.setId(rs.getInt("id"));
                        purchaseResponse.setOrderId(orderId);
                        purchaseResponse.setSupplierId(rs.getInt("supplier"));
                        purchaseResponse.setSupplierName(rs.getString("name"));
                        purchaseResponse.setPurchaseDate(Utils.convertDateOnlyToString(rs.getDate("purchaseDate")));
                        purchaseResponse.setInvoiceId(rs.getString("invoiceId"));
                        purchaseResponse.setInvoiceUrl(rs.getString("invoiceUrl"));
                        purchaseResponse.setTotalAmount(rs.getDouble("totalAmount"));
                        purchaseResponse.setCreatedBy(rs.getInt("createdBy"));
                        purchaseResponse.setCreatedByUsername(rs.getString("username"));
                        purchaseResponse.setCreatedAt(Utils.convertDateToString(rs.getTimestamp("createdAt")));

                        // Initialize the list of purchase items
                        purchaseResponse.setPurchaseItemsList(new ArrayList<>());

                        // Put the purchaseResponse object into the map
                        purchaseResponseMap.put(orderId, purchaseResponse);
                    }

                    // Add the current purchase item to the list
                    PurchaseItemsResponse purchaseItem = new PurchaseItemsResponse();
                    purchaseItem.setPurchaseItemId(rs.getInt("itemId"));
                    purchaseItem.setPurchaseItemOrderId(rs.getString("itemOrderId"));
                    purchaseItem.setProductId(rs.getInt("productId"));
                    purchaseItem.setQuantity(rs.getInt("qty"));
                    purchaseItem.setPurchasePrice(rs.getDouble("purchasePrice"));
                    purchaseItem.setPurchaseItemCreatedAt(Utils.convertDateToString(rs.getTimestamp("itemCreatedAt")));

                    // Add item to the purchase order's item list
                    purchaseResponse.getPurchaseItemsList().add(purchaseItem);
                }

                // Return all purchase orders with their associated items
                return new ArrayList<>(purchaseResponseMap.values());
            }
        });
    }
}
