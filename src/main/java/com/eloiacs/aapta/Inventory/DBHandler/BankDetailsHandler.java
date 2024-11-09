package com.eloiacs.aapta.Inventory.DBHandler;

import com.eloiacs.aapta.Inventory.Models.BankDetailsRequestModel;
import com.eloiacs.aapta.Inventory.Models.BaseModel;
import com.eloiacs.aapta.Inventory.Models.SupplierRequestModel;
import com.eloiacs.aapta.Inventory.Responses.BankDetailsResponseModel;
import com.eloiacs.aapta.Inventory.Responses.SupplierResponseModel;
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
import java.util.ArrayList;
import java.util.List;

@Service
public class BankDetailsHandler {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public boolean insertBankDetails(BankDetailsRequestModel model, String createdBy) {

        String bankDetailsQuery = "insert into bankDetails(customerId,name,accountNo,branch,ifscCode,gstNo,customerType,createdBy)values(?,?,?,?,?,?,?,?) ";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(bankDetailsQuery, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1,model.getCustomerId() );
            ps.setString(2,model.getBankName());
            ps.setString(3,model.getAccountNo());
            ps.setString(4,model.getBranch());
            ps.setString(5,model.getIfscCode());
            ps.setString(6,model.getGstNo());
            ps.setInt(7, model.getCustomerType());
            ps.setString(8,createdBy);
            return ps;
        }, keyHolder);

        int bankDetailsId = keyHolder.getKey().intValue();
        String eventName = "New BankDetails created";
        int eventType = 12;
        String eventInsertQuery = "INSERT INTO event (eventName, taskId, eventType, userId) VALUES (?, ?, ?, ?)";

        jdbcTemplate.update(eventInsertQuery, eventName, bankDetailsId, eventType, createdBy);

        return true;
    }

    public boolean deleteBankDetails(BaseModel model){
        String query = "update bankDetails set isActive = false where id = ? and isActive = true";
        jdbcTemplate.update(query,model.getRequestId());
        return true;
    }

    public List<BankDetailsResponseModel> getBankDetails(String customerName){

        StringBuilder  query= new StringBuilder("select bd.id,bd.customerId,ac.customerId as customerIdCode,ac.customerName, bd.name as bankName,bd.accountNo,bd.branch,bd.ifscCode,bd.gstNo,bd.customerType,ct.customerType as customerTypeName ,bd.isActive,bd.createdBy,bd.createdAt from bankDetails bd left join customerType ct on bd.customerType=ct.id left join apptaCustomers ac on bd.customerId=ac.customerId  where isActive=true ");

            if (customerName != null && !customerName.trim().isEmpty()) {
                query.append(" and LOWER(bd.name) LIKE LOWER(?) ");
            }

        query.append(" order by bd.id desc");

        return jdbcTemplate.query(query.toString(), new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                if (customerName != null && !customerName.trim().isEmpty()) {
                    ps.setString(1, "%" + customerName + "%");
                }
            }
        },new ResultSetExtractor<List<BankDetailsResponseModel>>() {
            @Override
            public List<BankDetailsResponseModel> extractData(ResultSet rs) throws SQLException, DataAccessException {

                if(rs.next()){
                    List <BankDetailsResponseModel> bankDetailsResponseModels = new ArrayList<>();

                    do {
                        BankDetailsResponseModel response = new BankDetailsResponseModel();

                        response.setId(rs.getInt("id"));
                        response.setCustomerId(rs.getInt("customerId"));
                        response.setCustomerIdCode(rs.getString("customerIdCode"));
                        response.setCustomerName(rs.getString("customerName"));
                        response.setBankName(rs.getString("bankName"));
                        response.setAccountNo(rs.getString("accountNo"));
                        response.setBranch(rs.getString("branch"));
                        response.setIfscCode(rs.getString("ifscCode"));
                        response.setGstNo(rs.getString("gstNo"));
                        response.setCustomerType(rs.getInt("customerType"));
                        response.setCustomerTypeName(rs.getString("customerTypeName"));
                        response.setActive(rs.getBoolean("isActive"));
                        response.setCreatedBy(rs.getString("createdBy"));
                        response.setCreatedAt(Utils.convertDateToString(rs.getTimestamp("createdAt")));

                        bankDetailsResponseModels.add(response);

                    }while (rs.next());

                    return bankDetailsResponseModels;
                }
                return null;
            }
        });
    }

    public boolean updateBankDetails(BankDetailsRequestModel model) {

        String query ="update bankDetails set customerId=?,name=?,accountNo=?,branch=?,ifscCode=?,gstNo=?,customerType=? where id=? ";

        int rowsAffected = jdbcTemplate.update(query,
                model.getCustomerId(),
                model.getBankName(),
                model.getAccountNo(),
                model.getBranch(),
                model.getIfscCode(),
                model.getGstNo(),
                model.getCustomerType(),
                model.getId()
        );
        return rowsAffected > 0;
    }

    public BankDetailsResponseModel getBankDetailsById(int id) {
        String query = "SELECT bd.customerId, ac.customerId as customerIdName, bd.name, ac.customerName, bd.accountNo, bd.branch, bd.ifscCode, bd.gstNo, bd.customerType, ct.customerType as customerTypeName, bd.isActive, bd.createdBy, bd.createdAt " +
                "FROM bankDetails bd " +
                "INNER JOIN customerType ct ON bd.customerType = ct.id " +
                "INNER JOIN apptaCustomers ac ON bd.customerId = ac.customerId " +
                "WHERE bd.id = ?";

        return jdbcTemplate.query(query, new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setInt(1, id);
            }
        }, new ResultSetExtractor<BankDetailsResponseModel>() {
            @Override
            public BankDetailsResponseModel extractData(ResultSet rs) throws SQLException, DataAccessException {
                if (rs.next()) {
                    BankDetailsResponseModel response = new BankDetailsResponseModel();
                    response.setCustomerId(rs.getInt("customerId"));
                    response.setCustomerIdCode(rs.getString("customerIdName"));
                    response.setCustomerName(rs.getString("customerName"));
                    response.setBankName(rs.getString("name"));
                    response.setAccountNo(rs.getString("accountNo"));
                    response.setBranch(rs.getString("branch"));
                    response.setIfscCode(rs.getString("ifscCode"));
                    response.setGstNo(rs.getString("gstNo"));
                    response.setCustomerType(rs.getInt("customerType"));
                    response.setCustomerTypeName(rs.getString("customerTypeName"));
                    response.setActive(rs.getBoolean("isActive"));
                    response.setCreatedBy(rs.getString("createdBy"));
                    response.setCreatedAt(Utils.convertDateToString(rs.getTimestamp("createdAt")));
                    return response;
                }
                return null;
            }
        });
    }


}
