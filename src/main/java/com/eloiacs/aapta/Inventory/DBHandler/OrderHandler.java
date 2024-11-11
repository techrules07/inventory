package com.eloiacs.aapta.Inventory.DBHandler;

import com.eloiacs.aapta.Inventory.Models.DeleteOrderItemModel;
import com.eloiacs.aapta.Inventory.Models.OrderItemsRequestModel;
import com.eloiacs.aapta.Inventory.Models.OrderRequestModel;
import com.eloiacs.aapta.Inventory.Responses.OrderItemsResponse;
import com.eloiacs.aapta.Inventory.Responses.OrderResponse;
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
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class OrderHandler {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    ProductHandler productHandler;

    public String generateOrderId(int previousId) {
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
        return "APTAOD-" + (9999 + nextId) + dt;
    }

    public int findLastOrderId() {
        String query = "SELECT id FROM orders order BY id DESC LIMIT 1";

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

    public String addOrder(OrderRequestModel orderRequestModel, String createdBy){

        String orderId = generateOrderId(findLastOrderId());

        String insertOrderQuery = "insert into orders(orderId,customerId,status,createdBy,createdAt) values(?,?,5,?,current_timestamp())";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        int orderInserted = jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(insertOrderQuery, new String[]{"id"});
            ps.setString(1,orderId);
            ps.setString(2,orderRequestModel.getCustomerId());
            ps.setString(3,createdBy);
            return ps;
        },keyHolder);

        if (orderInserted != 0 && keyHolder.getKey() != null){
            int insertedOrderId = keyHolder.getKey().intValue();

            String eventInsertQuery = "INSERT INTO event (eventName, taskId, eventType, userId) VALUES (?, ?, ?, ?)";
            String eventName = "New order created";
            int eventType = 7;

            jdbcTemplate.update(eventInsertQuery, eventName, insertedOrderId, eventType, createdBy);

            return orderId;
        }
        return null;
    }

    public Boolean updateOrder(OrderRequestModel orderRequestModel){

        String orderUpdateQuery = "update orders set customerId = ? where orderId = ?";

        jdbcTemplate.update(orderUpdateQuery,
                orderRequestModel.getCustomerId(),
                orderRequestModel.getOrderId());

        return true;
    }

    @Transactional
    public OrderResponse addOrderItem(OrderItemsRequestModel orderItemsRequestModel, String createdBy){

        ProductResponse response = productHandler.getProductById(orderItemsRequestModel.getProductId());

        if (response == null) {
            return getOrderByOrderId(orderItemsRequestModel.getOrderId());
        }

        String insertOrderItemQuery = "insert into orderItems(orderId,productId,unitPrice,quantity,totalAmount,discount,createdBy,createdAt) values(?,?,?,?,?,?,?,current_timestamp())";
        String updateOrderItemQuery = "update orderItems set unitPrice = ?, quantity = ?, totalAmount = ? where orderId = ? and productId = ?";
        String eventInsertQuery = "INSERT INTO event (eventName, taskId, eventType, userId) VALUES (?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        String checkOrderItemExistsQuery = "select count(*) from orderItems where orderId = ? and productId = ?";
        int orderItemExists = jdbcTemplate.queryForObject(checkOrderItemExistsQuery, Integer.class, orderItemsRequestModel.getOrderId(), orderItemsRequestModel.getProductId());

        if (orderItemExists > 0) {

            String getQuantityQuery = "select quantity from orderItems where orderId = ? and productId = ?";
            int existingQuantity = jdbcTemplate.queryForObject(getQuantityQuery, Integer.class, orderItemsRequestModel.getOrderId(), orderItemsRequestModel.getProductId());

            int quantity = existingQuantity + 1;
            if (orderItemsRequestModel.getQuantity()!=0){
                quantity = orderItemsRequestModel.getQuantity();
            }
            double totalAmount = Math.round(response.getWholesalePrice() * quantity);
            if (orderItemsRequestModel.getDiscount() != 0){
                totalAmount = totalAmount * (1 - (double) orderItemsRequestModel.getDiscount() / 100);
            }

            int updateOrderItem = jdbcTemplate.update(updateOrderItemQuery,
                    response.getWholesalePrice(),
                    quantity,
                    totalAmount,
                    orderItemsRequestModel.getOrderId(),
                    orderItemsRequestModel.getProductId());

            return getOrderByOrderId(orderItemsRequestModel.getOrderId());

        } else {

            int quantity = 1;
            if (orderItemsRequestModel.getQuantity()!=0){
                quantity = orderItemsRequestModel.getQuantity();
            }
            double totalAmount = Math.round(response.getWholesalePrice() * quantity);
            if (orderItemsRequestModel.getDiscount() != 0){
                totalAmount = totalAmount * (1 - (double) orderItemsRequestModel.getDiscount() / 100);
            }

            final double amount = totalAmount;
            final int finalQuantity = quantity;

            int insertOrderItem = jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(insertOrderItemQuery, new String[]{"id"});
                ps.setString(1,orderItemsRequestModel.getOrderId());
                ps.setInt(2,orderItemsRequestModel.getProductId());
                ps.setDouble(3, response.getWholesalePrice());
                ps.setInt(4, finalQuantity);
                ps.setDouble(5, amount);
                ps.setInt(6, orderItemsRequestModel.getDiscount());
                ps.setString(7, createdBy);
                return ps;
            },keyHolder);

            if (insertOrderItem != 0 && keyHolder.getKey() != null){

                int insertedOrderItemId = keyHolder.getKey().intValue();

                String eventName = "New order item created";
                int eventType = 8;

                jdbcTemplate.update(eventInsertQuery, eventName, insertedOrderItemId, eventType, createdBy);

                return getOrderByOrderId(orderItemsRequestModel.getOrderId());
            }
        }

        return getOrderByOrderId(orderItemsRequestModel.getOrderId());
    }

    public Boolean holdOrder(String orderId){

        String updateOrderStatusQuery = "update orders set status = 2 where orderId = ?";

        jdbcTemplate.update(updateOrderStatusQuery,
                orderId);

        return true;
    }

    public Boolean cancelOrder(String orderId){

        String updateOrderStatusQuery = "update orders set status = 4 where orderId = ?";

        jdbcTemplate.update(updateOrderStatusQuery,
                orderId);

        return true;
    }

    public Boolean deleteOrderItems(List<DeleteOrderItemModel> deleteOrderItemModelList) {

        String checkOrderCompletedQuery = "select count(*) from orders where orderId = ? and status != 7";
        String deleteOrderItemQuery = "delete from orderItems where orderId = ? and productId = ?";

        List<DeleteOrderItemModel> deletableItems = new ArrayList<>();

        for (DeleteOrderItemModel item : deleteOrderItemModelList) {

            int orderChecked = jdbcTemplate.queryForObject(
                    checkOrderCompletedQuery,
                    new Object[]{item.getOrderId()},
                    Integer.class
            );

            if (orderChecked > 0) {
                deletableItems.add(item);
            }
        }

        if (!deletableItems.isEmpty()) {
            jdbcTemplate.batchUpdate(deleteOrderItemQuery, deletableItems, deletableItems.size(),
                    (ps, deletableItem) -> {
                        ps.setString(1, deletableItem.getOrderId());
                        ps.setInt(2, deletableItem.getProductId());
                    }
            );
            return true;
        }

        return false;
    }

    public List<OrderResponse> getOrders(){

        String getAllOrdersQuery = "select o.id as oId, o.orderId as oOrderId, o.customerId, o.status, os.statusType, o.createdBy as orderCreatedBy, usr.username as orderUsername, o.createdAt as orderCreatedAt, oi.id, oi.orderId, oi.productId, p.productName, oi.unitPrice, oi.quantity, oi.totalAmount, oi.discount, oi.createdBy, usrs.username, oi.createdAt from orders o left join orderItems oi on oi.orderId = o.orderId left join orderStatus os on os.id = o.status left join products p on p.id = oi.productId left join users usr on usr.id = o.createdBy left join users usrs on usrs.id = oi.createdBy order by o.orderId desc";

        return jdbcTemplate.query(getAllOrdersQuery, new ResultSetExtractor<List<OrderResponse>>() {
            @Override
            public List<OrderResponse> extractData(ResultSet rs) throws SQLException, DataAccessException {
                Map<String, OrderResponse> orderMap = new LinkedHashMap<>();

                while (rs.next()) {
                    String orderId = rs.getString("oOrderId");

                    // Check if the order is already in the map
                    OrderResponse orderResponse = orderMap.get(orderId);
                    if (orderResponse == null) {
                        orderResponse = new OrderResponse();
                        orderResponse.setId(rs.getInt("oId"));
                        orderResponse.setOrderId(orderId);
                        orderResponse.setCustomerId(rs.getString("customerId"));
                        orderResponse.setStatusId(rs.getInt("status"));
                        orderResponse.setStatus(rs.getString("statusType"));
                        orderResponse.setCreatedById(rs.getInt("orderCreatedBy"));
                        orderResponse.setCreatedBy(rs.getString("orderUsername"));
                        orderResponse.setCreatedAt(Utils.convertDateToString(rs.getTimestamp("orderCreatedAt")));
                        orderResponse.setOrderItems(new ArrayList<>()); // Initialize order items list

                        orderResponse.setTotalUnitPrice(0.0);
                        orderResponse.setTotalPrice(0.0);
                        orderResponse.setTotalAmount(0.0);
                        orderResponse.setTotalDiscount(0.0);

                        orderMap.put(orderId, orderResponse);
                    }

                    // Create an OrderItemResponse for the current row and add it to the order's item list
                    OrderItemsResponse orderItem = new OrderItemsResponse();
                    orderItem.setOrderItemId(rs.getInt("id"));
                    orderItem.setOrderItemOrderId(orderId);
                    orderItem.setProductId(rs.getInt("productId"));
                    orderItem.setProductName(rs.getString("productName"));
                    orderItem.setUnitPrice(rs.getDouble("unitPrice"));
                    orderItem.setQuantity(rs.getInt("quantity"));
                    orderItem.setTotalAmount(rs.getDouble("totalAmount"));
                    orderItem.setDiscount(rs.getInt("discount"));
                    orderItem.setOrderItemCreatedById(rs.getInt("createdBy"));
                    orderItem.setOrderItemCreatedBy(rs.getString("username"));
                    orderItem.setOrderItemCreatedAt(Utils.convertDateToString(rs.getTimestamp("createdAt")));

                    double unitPrice = rs.getDouble("unitPrice");
                    int quantity = rs.getInt("quantity");
                    double discount = rs.getDouble("discount");

                    double itemTotalPrice = unitPrice * quantity;
                    double itemDiscountAmount = itemTotalPrice * (discount / 100);
                    double itemTotalAmountAfterDiscount = itemTotalPrice - itemDiscountAmount;

                    // Add the item to the list in the corresponding order
                    orderResponse.getOrderItems().add(orderItem);

                    orderResponse.setTotalUnitPrice(orderResponse.getTotalUnitPrice() + unitPrice);
                    orderResponse.setTotalPrice(orderResponse.getTotalPrice() + itemTotalPrice);
                    orderResponse.setTotalAmount(orderResponse.getTotalAmount() + itemTotalAmountAfterDiscount);
                    orderResponse.setTotalDiscount(orderResponse.getTotalDiscount() + itemDiscountAmount);
                }

                // Convert map values to a list and return
                return new ArrayList<>(orderMap.values());
            }
        });
    }

    public List<OrderResponse> getHeldOrders(){

        String getAllOrdersQuery = "select o.id as oId, o.orderId as oOrderId, o.customerId, o.status, os.statusType, o.createdBy as orderCreatedBy, usr.username as orderUsername, o.createdAt as orderCreatedAt, oi.id, oi.orderId, oi.productId, p.productName, oi.unitPrice, oi.quantity, oi.totalAmount, oi.discount, oi.createdBy, usrs.username, oi.createdAt from orders o left join orderItems oi on oi.orderId = o.orderId left join orderStatus os on os.id = o.status left join products p on p.id = oi.productId left join users usr on usr.id = o.createdBy left join users usrs on usrs.id = oi.createdBy where o.status = 2 order by o.orderId desc";

        return jdbcTemplate.query(getAllOrdersQuery, new ResultSetExtractor<List<OrderResponse>>() {
            @Override
            public List<OrderResponse> extractData(ResultSet rs) throws SQLException, DataAccessException {
                Map<String, OrderResponse> orderMap = new LinkedHashMap<>();

                while (rs.next()) {
                    String orderId = rs.getString("oOrderId");

                    // Check if the order is already in the map
                    OrderResponse orderResponse = orderMap.get(orderId);
                    if (orderResponse == null) {
                        orderResponse = new OrderResponse();
                        orderResponse.setId(rs.getInt("oId"));
                        orderResponse.setOrderId(orderId);
                        orderResponse.setCustomerId(rs.getString("customerId"));
                        orderResponse.setStatusId(rs.getInt("status"));
                        orderResponse.setStatus(rs.getString("statusType"));
                        orderResponse.setCreatedById(rs.getInt("orderCreatedBy"));
                        orderResponse.setCreatedBy(rs.getString("orderUsername"));
                        orderResponse.setCreatedAt(Utils.convertDateToString(rs.getTimestamp("orderCreatedAt")));
                        orderResponse.setOrderItems(new ArrayList<>()); // Initialize order items list

                        orderResponse.setTotalUnitPrice(0.0);
                        orderResponse.setTotalPrice(0.0);
                        orderResponse.setTotalAmount(0.0);
                        orderResponse.setTotalDiscount(0.0);

                        orderMap.put(orderId, orderResponse);
                    }

                    // Create an OrderItemResponse for the current row and add it to the order's item list
                    OrderItemsResponse orderItem = new OrderItemsResponse();
                    orderItem.setOrderItemId(rs.getInt("id"));
                    orderItem.setOrderItemOrderId(orderId);
                    orderItem.setProductId(rs.getInt("productId"));
                    orderItem.setProductName(rs.getString("productName"));
                    orderItem.setUnitPrice(rs.getDouble("unitPrice"));
                    orderItem.setQuantity(rs.getInt("quantity"));
                    orderItem.setTotalAmount(rs.getDouble("totalAmount"));
                    orderItem.setDiscount(rs.getInt("discount"));
                    orderItem.setOrderItemCreatedById(rs.getInt("createdBy"));
                    orderItem.setOrderItemCreatedBy(rs.getString("username"));
                    orderItem.setOrderItemCreatedAt(Utils.convertDateToString(rs.getTimestamp("createdAt")));

                    double unitPrice = rs.getDouble("unitPrice");
                    int quantity = rs.getInt("quantity");
                    double discount = rs.getDouble("discount");

                    double itemTotalPrice = unitPrice * quantity;
                    double itemDiscountAmount = itemTotalPrice * (discount / 100);
                    double itemTotalAmountAfterDiscount = itemTotalPrice - itemDiscountAmount;

                    // Add the item to the list in the corresponding order
                    orderResponse.getOrderItems().add(orderItem);

                    orderResponse.setTotalUnitPrice(orderResponse.getTotalUnitPrice() + unitPrice);
                    orderResponse.setTotalPrice(orderResponse.getTotalPrice() + itemTotalPrice);
                    orderResponse.setTotalAmount(orderResponse.getTotalAmount() + itemTotalAmountAfterDiscount);
                    orderResponse.setTotalDiscount(orderResponse.getTotalDiscount() + itemDiscountAmount);
                }

                // Convert map values to a list and return
                return new ArrayList<>(orderMap.values());
            }
        });
    }

    public OrderResponse getOrderByOrderId(String orderId) {

        String getOrderByOrderIdQuery = "select o.id as oId, o.orderId as oOrderId, o.customerId, o.status, os.statusType, o.createdBy as orderCreatedBy, usr.username as orderUsername, o.createdAt as orderCreatedAt, oi.id, oi.orderId, oi.productId, p.productName, oi.unitPrice, oi.quantity, oi.totalAmount, oi.discount, oi.createdBy, usrs.username, oi.createdAt from orders o left join orderItems oi on oi.orderId = o.orderId left join orderStatus os on os.id = o.status left join products p on p.id = oi.productId left join users usr on usr.id = o.createdBy left join users usrs on usrs.id = oi.createdBy where o.orderId = ?";

        return jdbcTemplate.query(getOrderByOrderIdQuery, new Object[]{orderId}, new ResultSetExtractor<OrderResponse>() {
            @Override
            public OrderResponse extractData(ResultSet rs) throws SQLException, DataAccessException {
                OrderResponse orderResponse = null;
                double totalUnitPrice = 0.0;
                double totalPrice = 0.0;
                double totalAmount = 0.0;
                double totalDiscount = 0.0;

                while (rs.next()) {
                    if (orderResponse == null) {
                        orderResponse = new OrderResponse();
                        orderResponse.setId(rs.getInt("oId"));
                        orderResponse.setOrderId(rs.getString("oOrderId"));
                        orderResponse.setCustomerId(rs.getString("customerId"));
                        orderResponse.setStatusId(rs.getInt("status"));
                        orderResponse.setStatus(rs.getString("statusType"));
                        orderResponse.setCreatedById(rs.getInt("orderCreatedBy"));
                        orderResponse.setCreatedBy(rs.getString("orderUsername"));
                        orderResponse.setCreatedAt(Utils.convertDateToString(rs.getTimestamp("orderCreatedAt")));
                        orderResponse.setOrderItems(new ArrayList<>()); // Initialize the list for order items
                    }

                    // Create an OrderItemResponse for the current row and add it to the order's item list
                    OrderItemsResponse orderItem = new OrderItemsResponse();
                    orderItem.setOrderItemId(rs.getInt("id"));
                    orderItem.setOrderItemOrderId(orderId);
                    orderItem.setProductId(rs.getInt("productId"));
                    orderItem.setProductName(rs.getString("productName"));
                    orderItem.setUnitPrice(rs.getDouble("unitPrice"));
                    orderItem.setQuantity(rs.getInt("quantity"));
                    orderItem.setTotalAmount(rs.getDouble("totalAmount"));
                    orderItem.setDiscount(rs.getInt("discount"));
                    orderItem.setOrderItemCreatedById(rs.getInt("createdBy"));
                    orderItem.setOrderItemCreatedBy(rs.getString("username"));
                    orderItem.setOrderItemCreatedAt(Utils.convertDateToString(rs.getTimestamp("createdAt")));

                    double unitPrice = rs.getDouble("unitPrice");
                    int quantity = rs.getInt("quantity");
                    double discount = rs.getDouble("discount");

                    double itemTotalPrice = unitPrice * quantity;
                    double itemDiscountAmount = itemTotalPrice * (discount / 100);
                    double itemTotalAmountAfterDiscount = itemTotalPrice - itemDiscountAmount;

                    // Add the item to the list in the corresponding order
                    orderResponse.getOrderItems().add(orderItem);

                    totalUnitPrice += unitPrice;
                    totalPrice += itemTotalPrice;
                    totalAmount += itemTotalAmountAfterDiscount;
                    totalDiscount += itemDiscountAmount;
                }

                if (orderResponse != null) {
                    orderResponse.setTotalUnitPrice(totalUnitPrice);
                    orderResponse.setTotalPrice(totalPrice);
                    orderResponse.setTotalAmount(totalAmount);
                    orderResponse.setTotalDiscount(Double.parseDouble(new DecimalFormat("##.##").format(totalDiscount)));
                }

                return orderResponse;
            }
        });
    }

    public Boolean orderExistByOrderId(String orderId) {

        String orderExistByOrderIdQuery = "select count(*) from orders where orderId = ?";

        int count = jdbcTemplate.queryForObject(orderExistByOrderIdQuery, new Object[]{orderId}, Integer.class);

        return count > 0;
    }

    public Boolean inventoryExistByProductId(int productId) {

        String orderExistByOrderIdQuery = "select count(*) from inventory where productId = ?";

        int count = jdbcTemplate.queryForObject(orderExistByOrderIdQuery, new Object[]{productId}, Integer.class);

        return count > 0;
    }

    public OrderResponse createOrderByCustomerId(String createdBy) {

        String query = "SELECT * FROM orders WHERE status=6 and createdBy='" + createdBy+ "'";

        OrderResponse response = jdbcTemplate.query(query, new ResultSetExtractor<OrderResponse>() {
            @Override
            public OrderResponse extractData(ResultSet rs) throws SQLException, DataAccessException {
                if (rs.next()) {
                    OrderResponse response1 = new OrderResponse();
                    response1.setOrderId(rs.getString("orderId"));
                    response1.setCustomerId(rs.getString("customerId"));

                    return response1;
                }
                return null;
            }
        });

        if (response == null) {
            String orderId = generateOrderId(findLastOrderId());

            String insertOrderQuery = "insert into orders(orderId,status,createdBy) values(?,6,?)";

            KeyHolder keyHolder = new GeneratedKeyHolder();

            int orderInserted = jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(insertOrderQuery, new String[]{"id"});
                ps.setString(1,orderId);
                ps.setString(2,createdBy);
                return ps;
            },keyHolder);

            if (orderInserted != 0 && keyHolder.getKey() != null){
                int insertedOrderId = keyHolder.getKey().intValue();

                String eventInsertQuery = "INSERT INTO event (eventName, taskId, eventType, userId) VALUES (?, ?, ?, ?)";
                String eventName = "New order created";
                int eventType = 7;

                jdbcTemplate.update(eventInsertQuery, eventName, insertedOrderId, eventType, createdBy);
            }

            return getOrderByOrderId(orderId);
        }
        else {
            return response;
        }
    }

    public boolean checkCreatedOrder(String createdById) {

        String query = "SELECT * FROM orders WHERE status=6";
        return Boolean.TRUE.equals(jdbcTemplate.query(query, new ResultSetExtractor<Boolean>() {
            @Override
            public Boolean extractData(ResultSet rs) throws SQLException, DataAccessException {
                return rs.next();
            }
        }));
    }

    public OrderResponse completeOrder(String orderId, String paymentType, String createdBy) {
        String query = "UPDATE orders SET status=7, paymentType='" + paymentType + "' WHERE orderId = '" + orderId + "'";
        String eventInsertQuery = "INSERT INTO event (eventName, taskId, eventType, userId) VALUES (?, ?, ?, ?)";

        OrderResponse orderResponse = getOrderByOrderId(orderId);
        int orderUpdated = jdbcTemplate.update(query);

        if (orderUpdated > 0){

            for (OrderItemsResponse orderItem : orderResponse.getOrderItems()){
                String updateInventoryQuery = "UPDATE inventory SET count = count - ? WHERE productId = ?";

                jdbcTemplate.update(updateInventoryQuery,
                        orderItem.getQuantity(),
                        orderItem.getProductId());

                String inventoryCountEventName = "Inventory count decreased";
                int inventoryCountEventType = 9;

                String getInventoryIdQuery = "select id from inventory where productId = ?";
                int inventoryId = jdbcTemplate.queryForObject(getInventoryIdQuery, new Object[]{orderItem.getProductId()}, Integer.class);

                jdbcTemplate.update(eventInsertQuery, inventoryCountEventName, inventoryId, inventoryCountEventType, createdBy);
            }
        }

        return orderResponse;
    }

}
