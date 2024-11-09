package com.eloiacs.aapta.Inventory.DBHandler;

import com.eloiacs.aapta.Inventory.Models.BaseModel;
import com.eloiacs.aapta.Inventory.Models.BrandRequestModel;
import com.eloiacs.aapta.Inventory.Models.SupplierRequestModel;
import com.eloiacs.aapta.Inventory.Responses.BrandResponseModel;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class SupplierHandler {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public boolean insertSupplier(SupplierRequestModel model, String createdBy) {
        String insertSupplierQuery = "insert into supplier(supplierCode,name,email,altEmail,phone,altPhone,createdBy)values(?,?,?,?,?,?,?) ";

        String supplierCode = generateSupplierCode();

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(insertSupplierQuery, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, supplierCode);
            ps.setString(2,model.getName());
            ps.setString(3,model.getEmail());
            ps.setString(4,model.getAltEmail());
            ps.setString(5,model.getPhone());
            ps.setString(6,model.getAltPhone());
            ps.setString(7, createdBy);
            return ps;
        }, keyHolder);

        int supplierId = keyHolder.getKey().intValue();
        String eventName = "New supplier created";
        int eventType = 11;
        String eventInsertQuery = "INSERT INTO event (eventName, taskId, eventType, userId) VALUES (?, ?, ?, ?)";

        jdbcTemplate.update(eventInsertQuery, eventName, supplierId, eventType, createdBy);

        return true;
    }

    public boolean deleteSupplier(BaseModel model){
        String query = "update supplier set staus=false where id="+model.getRequestId()+" ";
        jdbcTemplate.execute(query);
        return true;
    }

    public boolean updateSupplier(SupplierRequestModel model, String createdBy) {
        String query = "UPDATE supplier SET  name = ?, email = ?, altEmail = ?, phone = ?, altPhone = ? WHERE id = ? and staus=true";

        int rowsAffected = jdbcTemplate.update(query,
                model.getName(),
                model.getEmail(),
                model.getAltEmail(),
                model.getPhone(),
                model.getAltPhone(),
                model.getId()
        );
        return rowsAffected > 0;
    }

    public List<SupplierResponseModel> getSupplier(String supplier){
        StringBuilder query =new StringBuilder("select s.id,s.supplierCode,s.name,s.email,s.altEmail,s.phone,s.altPhone,s.staus,s.createdBy,s.createdAt  from supplier s where staus=true ");

        if (supplier != null && !supplier.trim().isEmpty()) {
            query.append(" and s.supplierCode LIKE ? ");
        }

        query.append(" group by s.id order by s.id desc");

        return jdbcTemplate.query(query.toString(), new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                if (supplier != null && !supplier.trim().isEmpty()) {
                    ps.setString(1, "%" + supplier + "%");
                }
            }
        },new ResultSetExtractor<List<SupplierResponseModel>>() {
            @Override
            public List<SupplierResponseModel> extractData(ResultSet rs) throws SQLException, DataAccessException {

                if(rs.next()){
                    List <SupplierResponseModel> supplierResponseModels = new ArrayList<>();

                    do {
                        SupplierResponseModel response = new SupplierResponseModel();

                        response.setId(rs.getInt("id"));
                        response.setSupplierCode(rs.getString("supplierCode"));
                        response.setEmail(rs.getString("email"));
                        response.setAltEmail(rs.getString("altEmail"));
                        response.setPhone(rs.getString("phone"));
                        response.setAltPhone(rs.getString("altPhone"));
                        response.setStatus(rs.getBoolean("staus"));
                        response.setCreatedBy(rs.getString("createdBy"));
                        response.setCreatedAt(Utils.convertDateToString(rs.getTimestamp("createdAt")));

                        supplierResponseModels.add(response);

                    }while (rs.next());

                    return supplierResponseModels;
                }
                return null;
            }
        });
    }

    public SupplierResponseModel getSupplierById(int id){

        String query ="select s.supplierCode,s.name,s.email,s.altEmail,s.phone,s.altPhone,s.createdBy,s.createdAt from supplier s where id=? and staus=true ";
            return jdbcTemplate.query(query, new Object[]{id}, new ResultSetExtractor<SupplierResponseModel>() {
            @Override
            public SupplierResponseModel extractData(ResultSet rs) throws SQLException, DataAccessException {
                if(rs.next()){
                    SupplierResponseModel response = new SupplierResponseModel();

                    response.setSupplierCode(rs.getString("supplierCode"));
                    response.setEmail(rs.getString("email"));
                    response.setAltEmail(rs.getString("altEmail"));
                    response.setPhone(rs.getString("phone"));
                    response.setAltPhone(rs.getString("altPhone"));
                    response.setCreatedBy(rs.getString("createdBy"));
                    response.setCreatedAt(Utils.convertDateToString(rs.getTimestamp("createdAt")));

                    return response;
                }
                return null;
            }
        });
    }

    private String generateSupplierCode() {
        int lastSupplierCode = findLastSupplierCode();
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        return "SUP-" + sdf.format(date) + "-" + (lastSupplierCode + 1);
    }

    private int findLastSupplierCode() {
        String query = "SELECT supplierCode FROM supplier ORDER BY id DESC LIMIT 1";

        return jdbcTemplate.query(query, new ResultSetExtractor<Integer>() {
            @Override
            public Integer extractData(ResultSet rs) throws SQLException, DataAccessException {
                if (rs.next()) {
                    String lastSupplier = rs.getString("supplierCode");
                    return Integer.parseInt(lastSupplier.split("-")[2]);
                }
                return 0;
            }
        });
    }

}
