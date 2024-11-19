package com.eloiacs.aapta.Inventory.DBHandler;

import com.eloiacs.aapta.Inventory.Models.AuthModel;
import com.eloiacs.aapta.Inventory.Models.LoginModel;
import com.eloiacs.aapta.Inventory.Responses.UserInfoResponse;
import com.eloiacs.aapta.Inventory.Service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@Service
public class AuthHandler {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    JwtService jwtService;

    public Boolean createAccount(AuthModel model, String createdBy) {

        String userInsertQuery = "INSERT INTO users (username, email, mobileNumber, password, isActive, createdBy, modifiedBy, roleId) VALUES (?, ?, ?, ?, true, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(userInsertQuery, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, model.getUsername());
            ps.setString(2, model.getEmail());
            ps.setString(3, model.getMobileNumber());
            ps.setString(4, model.getPassword());
            ps.setString(5, createdBy);
            ps.setString(6, createdBy);
            ps.setInt(7, model.getRoleId());
            return ps;
        }, keyHolder);

        int createdUserId = keyHolder.getKey().intValue();
        String eventName = "User registered";
        int eventType = 1;
        String eventInsertQuery = "INSERT INTO event (eventName, taskId, eventType, userId) VALUES (?, ?, ?, ?)";

        jdbcTemplate.update(eventInsertQuery, eventName, createdUserId, eventType, createdUserId);

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

    public UserInfoResponse getUserInfo(String userId) {
        String query = "SELECT u.id, u.userName, u.email, u.roleId, u.mobileNumber, ur.role " +
                "FROM userRole ur " +
                "INNER JOIN users u ON ur.id = u.roleId " +
                "WHERE u.id = '" + userId + "'";

            return jdbcTemplate.queryForObject(query, (ResultSet rs, int rowNum) -> {
                UserInfoResponse response = new UserInfoResponse();
                response.setId(rs.getInt("id"));
                response.setRoleId(rs.getInt("roleId"));
                response.setRoleName(rs.getString("role"));
                response.setName(rs.getString("userName"));
                response.setEmail(rs.getString("email"));
                response.setPhone(rs.getString("mobileNumber"));
                return response;
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

    public Boolean checkUserEmailExist(String email) {

        String checkCourseQuery = "SELECT COUNT(*) FROM users WHERE email = ? AND  isActive = true";

        int count = jdbcTemplate.queryForObject(checkCourseQuery, new Object[]{email}, Integer.class);

        if(count!=0){
            return true;
        }
        else{
            return false;
        }
    }

    public Boolean checkUserMobileExist(String mobileNumber) {

        String checkCourseQuery = "SELECT COUNT(*) FROM users WHERE mobileNumber = ? AND  isActive = true";

        int count = jdbcTemplate.queryForObject(checkCourseQuery, new Object[]{mobileNumber}, Integer.class);

        if(count!=0){
            return true;
        }
        else{
            return false;
        }
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

    public void logUserLoginEvent(int createdBy) {
        String eventName = "User login the page";
        int eventType = 2;

        String query = "INSERT INTO event(eventName, taskId, eventType, userId) VALUES (?, ?, ?, ?)";

        jdbcTemplate.update(query, eventName, createdBy, eventType, createdBy);
    }

}
