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
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ApptaCustomersHandler {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public boolean addCustomers(ApptaCustomersRequestModel model,String createdBy){

        String query = "insert into apptaCustomers(customerName,customerId,mobile,email,address,paylater,loyaltyPoints,createdBy)values('"+model.getCustomerName()+"','"+model.getCustomerId()+"','"+model.getMobile()+"','"+model.getEmail()+"','"+model.getAddress()+"',"+model.isPaylater()+",'"+model.getLoyaltyPoints()+"','"+createdBy+"') ";
        jdbcTemplate.execute(query);
        return true;
    }

    public  boolean deleteCustomer(BaseModel model){
        String query = "update apptaCustomers set status=false where id='"+model.getRequestId()+"' ";
        jdbcTemplate.execute(query);
        return true;
    }

    public boolean updateCustomers(ApptaCustomersRequestModel model,String createdBy){
        String query = "update apptaCustomers set customerName='"+model.getCustomerName()+"',customerId='"+model.getCustomerId()+"',mobile='"+model.getMobile()+"',email='"+model.getEmail()+"',address='"+model.getAddress()+"',paylater="+model.isPaylater()+",loyaltyPoints='"+model.getLoyaltyPoints()+"',createdBy='"+createdBy+"' where id="+model.getId()+" ";
        jdbcTemplate.execute(query);
        return true;
    }

    public List<ApptaCustomersResponseModel> getCustomers(String customerName,String customerId){

        StringBuilder query = new StringBuilder("select ac.id,ac.customerName,ac.customerId,ac.mobile,ac.email,ac.address,ac.paylater,ac.loyaltyPoints ,ac.createdBy,ac.createdAt from apptaCustomers ac where isActive=true ") ;

        List<Object> params = new ArrayList<>();

        if (customerName != null && !customerName.trim().isEmpty()) {
            query.append("AND ac.customerName LIKE ? ");
            params.add("%" + customerName + "%");
        }

        if (customerId != null && !customerId.trim().isEmpty()) {
            query.append("AND ac.customerId LIKE ? ");
            params.add("%" + customerId + "%");
        }

        query.append("GROUP BY ac.id ORDER BY ac.id DESC");

        return jdbcTemplate.query(query.toString(), new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                for (int i = 0; i < params.size(); i++) {
                    ps.setObject(i + 1, params.get(i));
                }
            }
        },new ResultSetExtractor<List<ApptaCustomersResponseModel>>() {
            @Override
            public List<ApptaCustomersResponseModel> extractData(ResultSet rs) throws SQLException, DataAccessException {
              if(rs.next()){

                  List<ApptaCustomersResponseModel> apptaCustomersResponseModelList = new ArrayList<>();

                  do {
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

                  }while (rs.next());
                  return apptaCustomersResponseModelList;
              }
                return null;
            }
        });
    }

}
