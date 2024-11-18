package com.eloiacs.aapta.Inventory.DBHandler;

import com.eloiacs.aapta.Inventory.Models.UsersRequestModel;
import com.eloiacs.aapta.Inventory.Responses.UsersResponseModel;
import com.eloiacs.aapta.Inventory.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
public class UsersHandler {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public boolean addUsers(UsersRequestModel model, String createdBy) {

        String query = "INSERT INTO users(username, password, email, roleId, mobileNumber, createdBy, modifiedBy,isActive) VALUES (?, ?, ?, ?, ?, ?, ?,true )";

        jdbcTemplate.update(query, model.getUsername(), model.getPassword(), model.getEmail(),
                model.getRoleId(), model.getMobileNumber(), createdBy, createdBy);

        return true;
    }

    public List<UsersResponseModel> getUsers(){

        String query = "select u.id,u.username,u.password,u.email,u.roleId,u.mobileNumber,u.createdBy,u.modifiedBy,u.createdAt,u.modifiedAt,u.isActive from users u  where isActive = true ";

        return jdbcTemplate.query(query, new ResultSetExtractor<List<UsersResponseModel>>() {
            @Override
            public List<UsersResponseModel> extractData(ResultSet rs) throws SQLException, DataAccessException {

                if(rs.next()){

                    List<UsersResponseModel> usersResponseModelList = new ArrayList<>();

                    do {
                        UsersResponseModel responseModel = new UsersResponseModel();

                        responseModel.setId(rs.getInt("id"));
                        responseModel.setUsername(rs.getString("username"));
                        responseModel.setPassword(rs.getString("password"));
                        responseModel.setEmail(rs.getString("email"));
                        responseModel.setRoleId(rs.getInt("roleId"));
                        responseModel.setMobileNumber(rs.getString("mobileNumber"));
                        responseModel.setCreatedBy(rs.getString("createdBy"));
                        responseModel.setModifiedBy(rs.getString("modifiedBy"));
                        responseModel.setCreatedAt(Utils.convertUTCDateTimeToISTString(rs.getTimestamp("createdAt")));
                        responseModel.setModifiedAt(Utils.convertUTCDateTimeToISTString(rs.getTimestamp("modifiedAt")));
                        responseModel.setActive(rs.getBoolean("isActive"));
                        usersResponseModelList.add(responseModel);
                    }while (rs.next());
                    return usersResponseModelList;
                }

                return null;
            }
        });
    }


}
