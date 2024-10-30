package com.eloiacs.aapta.Inventory.DBHandler;

import com.eloiacs.aapta.Inventory.Models.BaseModel;
import com.eloiacs.aapta.Inventory.Models.SubCategoryRequestModel;
import com.eloiacs.aapta.Inventory.Responses.SubCategoryResponseModel;
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
public class SubCategoryHandler {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public  boolean insertSubCategory(String filePath,SubCategoryRequestModel model,String createdBy){
        String query = "insert into subcategory(subCategoryName,category_id,createdBy,modifiedBy,modifiedAt,image_url) Values('"+model.getSubCategoryName()+"','"+ model.getCategoryId()+"','"+createdBy+"','"+createdBy+"',Now(),'"+filePath+"')";
        jdbcTemplate.execute(query);
        return true;
    }

    public boolean deleteSubCategory(BaseModel model){
        String query = "update subcategory set isActive=false where id="+model.getRequestId()+" ";
        jdbcTemplate.execute(query);
        return true;
    }

    public boolean updateSubCategory(String filePath,SubCategoryRequestModel model,String createdBy){
        String query = "update subcategory set subCategoryName='"+model.getSubCategoryName()+"',modifiedBy='"+createdBy+"',modifiedAt=NOW() ,category_id="+model.getCategoryId()+",image_url='"+filePath+"' where id="+model.getId()+" ";
        jdbcTemplate.execute(query);
        return true;
    }

    public SubCategoryResponseModel getSubCategoryById(int id){
        String query = " select sc.subCategoryName,sc.createdBy,sc.modifiedBy,sc.createdAt,sc.modifiedAt,sc.isActive,c.id as category_id,c.category_name ,sc.image_url from subcategory sc left join category c on sc.category_id=c.id where sc.id=? ";
        return jdbcTemplate.query(query, new Object[]{id}, new ResultSetExtractor<SubCategoryResponseModel>() {
            @Override
            public SubCategoryResponseModel extractData(ResultSet rs) throws SQLException, DataAccessException {
                if(rs.next()){
                        SubCategoryResponseModel response = new SubCategoryResponseModel();

                        response.setSubCategoryName(rs.getString("subCategoryName"));
                        response.setCreatedBy(rs.getString("createdBy"));
                        response.setModifiedBy(rs.getString("modifiedBy"));
                        response.setCreatedAt(rs.getString("createdAt"));
                        response.setModifiedAt(rs.getString("modifiedAt"));
                        response.setActive(rs.getBoolean("isActive"));
                        response.setCategoryId(rs.getInt("category_id"));
                        response.setCategoryName(rs.getString("category_name"));
                        response.setImageUrl(rs.getString("image_url"));

                        return response;
                    }
                return null;
            }
        });
    }

    public List<SubCategoryResponseModel> getSubCategory(){
        String query = " select sc.id,sc.subCategoryName,sc.createdBy,sc.modifiedBy,sc.createdAt,sc.modifiedAt,sc.isActive,c.id as category_id,c.category_name ,sc.image_url from subcategory sc left join category c on sc.category_id=c.id where sc.isActive=true ";

        return jdbcTemplate.query(query, new ResultSetExtractor<List<SubCategoryResponseModel>>() {
            @Override
            public List<SubCategoryResponseModel> extractData(ResultSet rs) throws SQLException, DataAccessException {

                if(rs.next()){
                    List <SubCategoryResponseModel> subCategoryResponseModel = new ArrayList<>();

                    do {
                        SubCategoryResponseModel response = new SubCategoryResponseModel();

                        response.setId(rs.getInt("id"));
                        response.setSubCategoryName(rs.getString("subCategoryName"));
                        response.setCreatedBy(rs.getString("createdBy"));
                        response.setModifiedBy(rs.getString("modifiedBy"));
                        response.setCreatedAt(Utils.convertDateToString(rs.getTimestamp("createdAt")));
                        response.setModifiedAt(Utils.convertDateToString(rs.getTimestamp("modifiedAt")));
                        response.setActive(rs.getBoolean("isActive"));
                        response.setCategoryId(rs.getInt("category_id"));
                        response.setCategoryName(rs.getString("category_name"));
                        response.setImageUrl(rs.getString("image_url"));

                        subCategoryResponseModel.add(response);

                    }while (rs.next());

                    return subCategoryResponseModel;
                }
                return null;
            }
        });
    }
}
