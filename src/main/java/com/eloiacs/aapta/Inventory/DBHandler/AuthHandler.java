package com.eloiacs.aapta.Inventory.DBHandler;

import com.eloiacs.aapta.Inventory.Models.AuthModel;
import com.eloiacs.aapta.Inventory.Models.LoginModel;
import com.eloiacs.aapta.Inventory.Service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;

@Service
public class AuthHandler {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    JwtService jwtService;

    public Boolean createAccount(AuthModel model, String createdBy) {

        String query = "insert into users(username,email,mobileNumber,password,isActive,createdBy,modifiedBy,roleId) values('"+model.getUsername()+"','"+model.getEmail()+"','"+model.getMobileNumber()+"','"+model.getPassword()+"',true,"+createdBy+","+createdBy+","+model.getRoleId()+")";

        jdbcTemplate.execute(query);

        return true;
    }

    public AuthModel accountDetails(LoginModel model) {

        String query = "SELECT * FROM users WHERE email='" + model.getEmail() + "' AND password='" + model.getPassword() + "' AND isActive = true";

        return jdbcTemplate.query(query, new ResultSetExtractor<AuthModel>() {
            @Override
            public AuthModel extractData(ResultSet rs) throws SQLException, DataAccessException {
                if (rs.next()) {
                    AuthModel authModel = new AuthModel();
                    authModel.setId(rs.getInt("id"));
                    authModel.setUsername(rs.getString("username"));
                    authModel.setEmail(rs.getString("email"));
                    authModel.setMobileNumber(rs.getString("mobileNumber"));
                    authModel.setRoleId(rs.getInt("roleId"));

                    return authModel;
                }
                return null;
            }
        });
    }

    public AuthModel getUserbyEmail(String email) {

        String query = "SELECT * FROM users WHERE email='" + email + "' AND isActive = true";

        return jdbcTemplate.query(query, new ResultSetExtractor<AuthModel>() {
            @Override
            public AuthModel extractData(ResultSet rs) throws SQLException, DataAccessException {
                if (rs.next()) {
                    AuthModel authModel = new AuthModel();
                    authModel.setId(rs.getInt("id"));
                    authModel.setUsername(rs.getString("username"));
                    authModel.setEmail(rs.getString("email"));
                    authModel.setMobileNumber(rs.getString("mobileNumber"));
                    authModel.setRoleId(rs.getInt("roleId"));
                    authModel.setPassword(rs.getString("password"));

                    return authModel;
                }
                return null;
            }
        });
    }

    public LoginModel getUserDetails(String id){

        String query = "SELECT * FROM users WHERE id='"+id+"' AND isActive = true";

        return jdbcTemplate.query(query, new ResultSetExtractor<LoginModel>() {
            @Override
            public LoginModel extractData(ResultSet rs) throws SQLException, DataAccessException {
                if (rs.next()) {
                    LoginModel loginModel = new LoginModel();
                    loginModel.setEmail(rs.getString("email"));
                    loginModel.setPassword(rs.getString("password"));

                    return loginModel;
                }
                return null;
            }
        });
    }

}
