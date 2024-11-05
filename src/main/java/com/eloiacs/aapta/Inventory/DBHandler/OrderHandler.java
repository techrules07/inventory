package com.eloiacs.aapta.Inventory.DBHandler;

import com.eloiacs.aapta.Inventory.Models.DeleteOrderItemModel;
import com.eloiacs.aapta.Inventory.Models.OrderItemsRequestModel;
import com.eloiacs.aapta.Inventory.Models.OrderRequestModel;
import com.eloiacs.aapta.Inventory.Responses.OrderItemsResponse;
import com.eloiacs.aapta.Inventory.Responses.OrderResponse;
import com.eloiacs.aapta.Inventory.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class OrderHandler {

    @Autowired
    JdbcTemplate jdbcTemplate;

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

    public Boolean addOrder(OrderRequestModel orderRequestModel, String createdBy){

        String orderId = (orderRequestModel.getOrderId() != null && !orderRequestModel.getOrderId().isEmpty())
                ? orderRequestModel.getOrderId()
                : generateOrderId(findLastOrderId());

        String insertOrderQuery = "insert into orders(orderId,customerId,status,createdBy,createdAt) values(?,?,1,?,current_timestamp())";
        String insertOrderItemsQuery = "insert into orderItems(orderId,productId,unitPrice,quantity,totalAmount,discount,createdBy,createdAt) values(?,?,?,?,?,?,?,current_timestamp())";

        if (orderRequestModel.getOrderId() == null || orderRequestModel.getOrderId().isEmpty()) {
            int rowsAffected = jdbcTemplate.update(insertOrderQuery,
                    orderId,
                    orderRequestModel.getCustomerId(),
                    createdBy);

            if (rowsAffected == 0) {
                return false;
            }
        }

        for (OrderItemsRequestModel orderItem : orderRequestModel.getOrderItemsList()){

            double totalAmount = Math.round(orderItem.getUnitPrice() * orderItem.getQuantity());
            if (orderItem.getDiscount() != 0){
                totalAmount = totalAmount * (1 - (double) orderItem.getDiscount() / 100);
            }
            jdbcTemplate.update(insertOrderItemsQuery,
                    orderId,
                    orderItem.getProductId(),
                    orderItem.getUnitPrice(),
                    orderItem.getQuantity(),
                    totalAmount,
                    orderItem.getDiscount(),
                    createdBy);
        }

        return true;
    }

    public Boolean updateOrder(OrderRequestModel orderRequestModel, String createdBy) {

        String orderId = orderRequestModel.getOrderId();

        String updateOrderQuery = "update orders set customerId = ? where orderId = ?";
        jdbcTemplate.update(updateOrderQuery,
                orderRequestModel.getCustomerId(),
                orderId);

        String updateOrderItemQuery = "update orderItems set unitPrice = ?, quantity = ?, totalAmount = ?, discount = ? where orderId = ? and productId = ?";
        String insertOrderItemQuery = "insert into orderItems(orderId,productId,unitPrice,quantity,totalAmount,discount,createdBy,createdAt) values(?,?,?,?,?,?,?,current_timestamp())";

        for (OrderItemsRequestModel orderItem : orderRequestModel.getOrderItemsList()) {

            double totalAmount = Math.round(orderItem.getUnitPrice() * orderItem.getQuantity());

            if (orderItem.getDiscount() != 0) {
                totalAmount = totalAmount * (1 - (double) orderItem.getDiscount() / 100);
            }

            String checkOrderItemExistsQuery = "select count(*) from orderItems where orderId = ? and productId = ?";
            int orderItemExists = jdbcTemplate.queryForObject(checkOrderItemExistsQuery, Integer.class, orderId, orderItem.getProductId());

            if (orderItemExists > 0) {

                jdbcTemplate.update(updateOrderItemQuery,
                        orderItem.getUnitPrice(),
                        orderItem.getQuantity(),
                        totalAmount,
                        orderItem.getDiscount(),
                        orderId,
                        orderItem.getProductId());
            } else {

                jdbcTemplate.update(insertOrderItemQuery,
                        orderId,
                        orderItem.getProductId(),
                        orderItem.getUnitPrice(),
                        orderItem.getQuantity(),
                        totalAmount,
                        orderItem.getDiscount(),
                        createdBy);
            }
        }

        return true;
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

    public Boolean deleteOrderItem(DeleteOrderItemModel deleteOrderItemModel) {

        String deleteOrderItemQuery = "delete from orderItems where orderId = ? and productId = ?";

        jdbcTemplate.update(deleteOrderItemQuery,
                deleteOrderItemModel.getOrderId(),
                deleteOrderItemModel.getProductId());

        return true;
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
                    orderResponse.setTotalDiscount(totalDiscount);
                }

                return orderResponse;
            }
        });
    }

}
