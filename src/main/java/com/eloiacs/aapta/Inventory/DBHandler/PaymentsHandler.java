package com.eloiacs.aapta.Inventory.DBHandler;

import com.eloiacs.aapta.Inventory.Models.PaymentsRequestModel;
import com.eloiacs.aapta.Inventory.Responses.PaymentTypeResponse;
import com.eloiacs.aapta.Inventory.Responses.PaymentsResponse;
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
public class PaymentsHandler {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public Boolean addPayments(PaymentsRequestModel paymentsRequestModel) {
        String paymentId = generatePaymentId();
        String transactionId = generateTransactionId();
        String receiptNumber = generateReceiptNumber();

        String addPaymentQuery = "INSERT INTO payments(paymentId, paymentType, amount, transactionId, orderId, receiptNumber, customerId, createdAt) VALUES (?, ?, ?, ?, ?, ?, ?, current_timestamp())";

        jdbcTemplate.update(addPaymentQuery,
                paymentId,
                paymentsRequestModel.getPaymentTypeId(),
                paymentsRequestModel.getAmount(),
                transactionId,
                paymentsRequestModel.getOrderId(),
                receiptNumber,
                paymentsRequestModel.getCustomerId());

        return true;
    }

    public Boolean updatePayments(PaymentsRequestModel paymentsRequestModel){

        String updatePaymentQuery = "update payments set paymentType = ?, amount = ?, orderId = ?, customerId = ? where paymentId = ?";

        jdbcTemplate.update(updatePaymentQuery,
                paymentsRequestModel.getPaymentTypeId(),
                paymentsRequestModel.getAmount(),
                paymentsRequestModel.getOrderId(),
                paymentsRequestModel.getCustomerId(),
                paymentsRequestModel.getPaymentId());

        return true;
    }

    public List<PaymentsResponse> getPayments(){

        String getPaymentsQuery = "select pay.*, pt.paymentType as paymentTypeName, ac.customerName from payments pay left join paymentTypes pt on pt.id = pay.paymentType left join apptaCustomers ac on ac.customerId = pay.customerId";

        return jdbcTemplate.query(getPaymentsQuery, new ResultSetExtractor<List<PaymentsResponse>>() {
            @Override
            public List<PaymentsResponse> extractData(ResultSet rs) throws SQLException, DataAccessException {
                if (rs.next()){
                    List<PaymentsResponse> paymentsResponseList = new ArrayList<>();

                    do {
                        PaymentsResponse paymentsResponse = new PaymentsResponse();

                        paymentsResponse.setId(rs.getInt("id"));
                        paymentsResponse.setPaymentId(rs.getString("paymentId"));
                        paymentsResponse.setPaymentTypeId(rs.getInt("paymentType"));
                        paymentsResponse.setPaymentType(rs.getString("paymentTypeName"));
                        paymentsResponse.setAmount(rs.getDouble("amount"));
                        paymentsResponse.setTransactionId(rs.getString("transactionId"));
                        paymentsResponse.setOrderId(rs.getString("orderId"));
                        paymentsResponse.setReceiptNumber(rs.getString("receiptNumber"));
                        paymentsResponse.setCustomerId(rs.getString("customerId"));
                        paymentsResponse.setCustomerName(rs.getString("customerName"));
                        paymentsResponse.setCreatedAt(Utils.convertDateToString(rs.getTimestamp("createdAt")));

                        paymentsResponseList.add(paymentsResponse);
                    }
                    while (rs.next());

                    return paymentsResponseList;
                }
                return null;
            }
        });

    }

    public List<PaymentTypeResponse> getPaymentTypes(){

        String getPaymentTypeQuery = "select * from paymentTypes where isActive = true";

        return jdbcTemplate.query(getPaymentTypeQuery, new ResultSetExtractor<List<PaymentTypeResponse>>() {
            @Override
            public List<PaymentTypeResponse> extractData(ResultSet rs) throws SQLException, DataAccessException {
                if (rs.next()){
                    List<PaymentTypeResponse> paymentTypeResponseList = new ArrayList<>();

                    do {
                        PaymentTypeResponse paymentTypeResponse = new PaymentTypeResponse();

                        paymentTypeResponse.setPaymentTypeId(rs.getInt("id"));
                        paymentTypeResponse.setPaymentType(rs.getString("paymentType"));
                        paymentTypeResponse.setActive(rs.getBoolean("isActive"));

                        paymentTypeResponseList.add(paymentTypeResponse);
                    }
                    while (rs.next());

                    return paymentTypeResponseList;
                }

                return null;
            }
        });
    }

    private String generatePaymentId() {
        String uuid = UUID.randomUUID().toString();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String date = sdf.format(new Date());

        return date + "-" + uuid;
    }

    private String generateTransactionId() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        return "TXN-" + sdf.format(date) + "-" + (int) (Math.random() * 10000);
    }

    private String generateReceiptNumber() {
        int lastReceiptId = findLastReceiptId();
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        return "RCT-" + sdf.format(date) + "-" + (lastReceiptId + 1);
    }

    private int findLastReceiptId() {
        String query = "SELECT receiptNumber FROM payments ORDER BY createdAt DESC LIMIT 1";

        return jdbcTemplate.query(query, new ResultSetExtractor<Integer>() {
            @Override
            public Integer extractData(ResultSet rs) throws SQLException, DataAccessException {
                if (rs.next()) {
                    String lastReceipt = rs.getString("receiptNumber");
                    return Integer.parseInt(lastReceipt.split("-")[2]);
                }
                return 0;
            }
        });
    }

    public Boolean paymentTypeExistById(int paymentTypeId) {

        String paymentTypeExistByIdQuery = "select count(*) from paymentTypes where id = ?";

        int count = jdbcTemplate.queryForObject(paymentTypeExistByIdQuery, new Object[]{paymentTypeId}, Integer.class);

        return count > 0;
    }

    public Boolean customerExistByCustomerId(String customerId) {

        String customerExistByCustomerIdQuery = "select count(*) from apptaCustomers where customerId = ?";

        int count = jdbcTemplate.queryForObject(customerExistByCustomerIdQuery, new Object[]{customerId}, Integer.class);

        return count > 0;
    }

    public Boolean paymentExistByPaymentId(String paymentId) {

        String paymentExistByPaymentIdQuery = "select count(*) from payments where paymentId = ?";

        int count = jdbcTemplate.queryForObject(paymentExistByPaymentIdQuery, new Object[]{paymentId}, Integer.class);

        return count > 0;
    }
}
