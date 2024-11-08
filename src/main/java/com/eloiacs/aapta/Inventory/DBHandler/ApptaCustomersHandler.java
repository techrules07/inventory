package com.eloiacs.aapta.Inventory.DBHandler;

import com.eloiacs.aapta.Inventory.Models.ApptaCustomersRequestModel;
import com.eloiacs.aapta.Inventory.Models.BaseModel;
import com.eloiacs.aapta.Inventory.Responses.ApptaCustomersResponseModel;
import com.eloiacs.aapta.Inventory.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ApptaCustomersHandler {

    @Autowired
    JdbcTemplate jdbcTemplate;


    public boolean addCustomers(ApptaCustomersRequestModel model, String createdBy) {

        String customerId = generateCustomerId();

        String query = "INSERT INTO apptaCustomers (customerName, customerId, mobile, email, address, paylater, loyaltyPoints, createdBy) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, model.getCustomerName());
            ps.setString(2, customerId);
            ps.setString(3, model.getMobile());
            ps.setString(4, model.getEmail());
            ps.setString(5, model.getAddress());
            ps.setBoolean(6, model.isPaylater());
            ps.setInt(7, model.getLoyaltyPoints());
            ps.setString(8, createdBy);
            return ps;
        }, keyHolder);

        return true;
    }

    public boolean updateCustomers(ApptaCustomersRequestModel model,String createdBy){
        String query = "update apptaCustomers set customerName='"+model.getCustomerName()+"',mobile='"+model.getMobile()+"',email='"+model.getEmail()+"',address='"+model.getAddress()+"',paylater="+model.isPaylater()+",loyaltyPoints='"+model.getLoyaltyPoints()+"',createdBy='"+createdBy+"' where id="+model.getId()+" ";
        jdbcTemplate.execute(query);
        return true;
    }

    public List<ApptaCustomersResponseModel> getCustomers(String customerName, String customerId) {
        StringBuilder query = new StringBuilder("SELECT ac.id, ac.customerName, ac.customerId, ac.mobile, ac.email, ac.address, ac.paylater, ac.loyaltyPoints, ac.createdBy, ac.createdAt FROM apptaCustomers ac WHERE 1=1");

        List<Object> params = new ArrayList<>();

        if (customerName != null && !customerName.trim().isEmpty()) {
            query.append(" AND ac.customerName LIKE ?");
            params.add("%" + customerName + "%");
        }

        if (customerId != null && !customerId.trim().isEmpty()) {
            query.append(" AND ac.customerId LIKE ?");
            params.add("%" + customerId + "%");
        }

        return jdbcTemplate.query(query.toString(), new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                for (int i = 0; i < params.size(); i++) {
                    ps.setObject(i + 1, params.get(i));
                }
            }
        }, new ResultSetExtractor<List<ApptaCustomersResponseModel>>() {
            @Override
            public List<ApptaCustomersResponseModel> extractData(ResultSet rs) throws SQLException, DataAccessException {
                List<ApptaCustomersResponseModel> apptaCustomersResponseModelList = new ArrayList<>();

                while (rs.next()) {
                    ApptaCustomersResponseModel response = new ApptaCustomersResponseModel();

                    response.setId(rs.getInt("id"));
                    response.setCustomerName(rs.getString("customerName"));
                    response.setCustomerId(rs.getString("customerId"));
                    response.setMobile(rs.getString("mobile"));
                    response.setEmail(rs.getString("email"));
                    response.setAddress(rs.getString("address"));
                    response.setPaylater(rs.getBoolean("paylater"));
                    response.setLoyaltyPoints(rs.getInt("loyaltyPoints"));
                    response.setCreatedBy(rs.getString("createdBy"));
                    response.setCreatedAt(Utils.convertDateToString(rs.getTimestamp("createdAt")));

                    apptaCustomersResponseModelList.add(response);
                }

                return apptaCustomersResponseModelList;
            }
        });
    }

    private String generateCustomerId() {
        int lastCustomerIdNumber = findLastCustomerIdNumber(); // Get the last customer ID number
        Date date = new Date(); // Get current date
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd"); // Format date as YYYYMMDD
        String formattedDate = sdf.format(date);

        // Return the generated customer ID, combining "APPTA-" with today's date and incremented ID
        return "APPTA-" + formattedDate + "-" + (lastCustomerIdNumber + 1);
    }

    private int findLastCustomerIdNumber() {
        String query = "SELECT customerId FROM apptaCustomers ORDER BY id DESC LIMIT 1";

        return jdbcTemplate.query(query, new ResultSetExtractor<Integer>() {
            @Override
            public Integer extractData(ResultSet rs) throws SQLException, DataAccessException {
                if (rs.next()) {
                    String lastCustomerId = rs.getString("customerId");

                    // Split the customerId by "-" and ensure it has at least 3 parts
                    String[] parts = lastCustomerId.split("-");

                    if (parts.length >= 3) {
                        try {
                            return Integer.parseInt(parts[2]); // Extract the number part after the date
                        } catch (NumberFormatException e) {
                            // Handle the case where the number part is not a valid integer
                            return 0;
                        }
                    }
                }
                return 0; // Return 0 if no valid customerId is found
            }
        });
    }

}
