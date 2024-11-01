package com.eloiacs.aapta.Inventory.DBHandler;

import com.eloiacs.aapta.Inventory.Models.CategoryRequestModel;
import com.eloiacs.aapta.Inventory.Responses.CategoryResponseModel;
import com.eloiacs.aapta.Inventory.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
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
public class CategoryHandler {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public  boolean insertCategory(String filePath,CategoryRequestModel model,String createdBy){

        String categoryQuery = "insert into category(category_name,createdBy,modifiedBy,modifiedAt,image_url)values(?,?,?,Now(),?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(categoryQuery, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, model.getCategoryName());
            ps.setString(2, createdBy);
            ps.setString(3, createdBy);
            ps.setString(4, filePath);
            return ps;
        }, keyHolder);

        int categoryId = keyHolder.getKey().intValue();
        String eventName = "New category created";
        int eventType = 5;
        String eventInsertQuery = "INSERT INTO event (eventName, taskId, eventType, userId) VALUES (?, ?, ?, ?)";

        jdbcTemplate.update(eventInsertQuery, eventName, categoryId, eventType, createdBy);

        return true;
    }

    public  boolean deleteCategory(int id){

        String query = "update category set isActive=false where id="+id+" ";

        jdbcTemplate.execute(query);

        return  true ;
    }

    public boolean updateCategory(String filePath,CategoryRequestModel model,String createdBy){
        String query = "update category set category_name='"+model.getCategoryName()+"' ,image_url='"+filePath+"',modifiedBy='"+createdBy+"',modifiedAt=now() where id= "+model.getId()+" ";
        jdbcTemplate.execute(query);
        return  true;
    }

    public List<CategoryResponseModel> getCategory(){
        String query = "select c.id,c.category_name,c.createdBy,c.modifiedBy,c.createdAt,c.modifiedAt,c.isActive,c.image_url  from category c where isActive=true ";

        return   jdbcTemplate.query(query, new ResultSetExtractor<List<CategoryResponseModel>>() {
            @Override
            public List<CategoryResponseModel> extractData(ResultSet rs) throws SQLException, DataAccessException {

                if(rs.next()) {
                    List<CategoryResponseModel> categoryResponseModels = new ArrayList<>();

                    do {
                        CategoryResponseModel response = new CategoryResponseModel();

                        response.setId(rs.getInt("id"));
                        response.setCategoryName(rs.getString("category_name"));
                        response.setCreatedBy(rs.getString("createdBy"));
                        response.setModifiedBy(rs.getString("modifiedBy"));
                        response.setCreatedAt(Utils.convertDateToString(rs.getTimestamp("createdAt")));
                        response.setModifiedAt(Utils.convertDateToString(rs.getTimestamp("modifiedAt")));
                        response.setActive(rs.getBoolean("isActive"));
                        response.setImageUrl(rs.getString("image_url"));

                        categoryResponseModels.add(response);
                    } while (rs.next());
                    return categoryResponseModels;
                }
                return null;
            }
        });
    }

    public CategoryResponseModel getCategoryById(int id) {
        String query = "select * from category where id=?";
        return jdbcTemplate.query(query, new Object[]{id}, new ResultSetExtractor<CategoryResponseModel>() {
            @Override
            public CategoryResponseModel extractData(ResultSet rs) throws SQLException, DataAccessException {
                if (rs.next()) {
                    CategoryResponseModel response = new CategoryResponseModel();
                    response.setId(rs.getInt("id"));
                    response.setCategoryName(rs.getString("category_name"));
                    response.setCreatedBy(rs.getString("createdBy"));
                    response.setModifiedBy(rs.getString("modifiedBy"));
                    response.setCreatedAt(Utils.convertDateToString(rs.getTimestamp("createdAt")));
                    response.setModifiedAt(Utils.convertDateToString(rs.getTimestamp("modifiedAt")));
                    response.setActive(rs.getBoolean("isActive"));
                    response.setImageUrl(rs.getString("image_url"));
                    return response;
                }
                return null;
            }
        });
    }

}
