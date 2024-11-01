package com.eloiacs.aapta.Inventory.DBHandler;

import com.eloiacs.aapta.Inventory.Responses.EventResponseModel;
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
public class EventHandler {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public List<EventResponseModel> getEventList() {
        String query = "SELECT e.id, e.eventName, e.taskId, e.eventType, b.brandName, et.name AS eventTypeName, " +
                "u.id AS userId, u.username, p.productName,e.isActive, e.createdAt, " +
                "c.category_name, sc.subCategoryName " +
                "FROM event e " +
                "LEFT JOIN users u ON e.userId = u.id " +
                "LEFT JOIN eventTypes et ON e.eventType = et.id " +
                "LEFT JOIN brand b ON e.taskId = b.id " +
                "LEFT JOIN category c ON e.taskId = c.id " +
                "LEFT JOIN subcategory sc ON e.taskId = sc.id " +
                "LEFT JOIN products p ON e.taskId = p.id " +
                "WHERE e.isActive = true";

        return jdbcTemplate.query(query, new ResultSetExtractor<List<EventResponseModel>>() {
            @Override
            public List<EventResponseModel> extractData(ResultSet rs) throws SQLException, DataAccessException {
                List<EventResponseModel> responseModel = new ArrayList<>();
                while (rs.next()) {

                    EventResponseModel response = new EventResponseModel();

                    response.setId(rs.getInt("id"));
                    response.setEventName(rs.getString("eventName"));

                    switch (rs.getString("eventName")) {
                        case "New product created":
                            response.setTaskName(rs.getString("productName"));
                            break;
                        case "New category created":
                            response.setTaskName(rs.getString("category_name"));
                            break;
                        case "New subCategory created":
                            response.setTaskName(rs.getString("subCategoryName"));
                            break;
                        case "New brand created":
                            response.setTaskName(rs.getString("brandName"));
                            break;
                        case "User login the page":
                            response.setTaskName(rs.getString("username"));
                            break;
                        case "User registered":
                            response.setTaskName( rs.getString("username"));
                            break;
                        default:
                            response.setTaskId(rs.getInt("taskId"));
                            break;
                    }

                    response.setTaskId(rs.getInt("taskId"));
                    response.setEventType(rs.getInt("eventType"));
                    response.setEventTypeName(rs.getString("eventTypeName"));
                    response.setUserId(rs.getInt("userId"));
                    response.setUserName(rs.getString("username"));
                    response.setActive(rs.getBoolean("isActive"));
                    response.setCreatedAt(Utils.convertDateToString(rs.getTimestamp("createdAt")));

                    responseModel.add(response);
                }
                return responseModel.isEmpty() ? null : responseModel;
            }
        });
    }
}
