package com.eloiacs.aapta.Inventory.DBHandler;

import com.eloiacs.aapta.Inventory.Models.BaseModel;
import com.eloiacs.aapta.Inventory.Models.BrandRequestModel;
import com.eloiacs.aapta.Inventory.Responses.BrandResponseModel;
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
public class BrandHandler {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public  boolean insertBrand(String filePath, BrandRequestModel model, String createdBy){
        String query = "insert into brand(brandName,createdBy,modifiedBy,modifiedAt,image_url) Values('"+model.getBrandName()+"','"+createdBy+"','"+createdBy+"',Now(),'"+filePath+"')";
        jdbcTemplate.execute(query);
        return true;
    }

    public boolean deleteBrand(BaseModel model){
        String query = "update brand set isActive=false where id="+model.getRequestId()+" ";
        jdbcTemplate.execute(query);
        return true;
    }

    public boolean updateBrand(String filePath,BrandRequestModel model,String createdBy){
        String query = "update brand set brandName='"+model.getBrandName()+"',modifiedBy='"+createdBy+"',modifiedAt=NOW() ,image_url='"+filePath+"' where id="+model.getId()+" ";
        jdbcTemplate.execute(query);
        return true;
    }

    public List<BrandResponseModel> getBrand(){
        String query = "select b.id,b.brandName,b.createdBy,b.modifiedBy,b.createdAt,b.modifiedAt,b.isActive,b.image_url from brand b where b.isActive = true ";
        return jdbcTemplate.query(query, new ResultSetExtractor<List<BrandResponseModel>>() {
            @Override
            public List<BrandResponseModel> extractData(ResultSet rs) throws SQLException, DataAccessException {

                if(rs.next()){
                    List <BrandResponseModel> brandResponseModels = new ArrayList<>();

                    do {
                        BrandResponseModel response = new BrandResponseModel();

                        response.setId(rs.getInt("id"));
                        response.setBrandName(rs.getString("brandName"));
                        response.setCreatedBy(rs.getString("createdBy"));
                        response.setModifiedBy(rs.getString("modifiedBy"));
                        response.setCreatedAt(Utils.convertDateToString(rs.getTimestamp("createdAt")));
                        response.setModifiedAt(Utils.convertDateToString(rs.getTimestamp("modifiedAt")));
                        response.setActive(rs.getBoolean("isActive"));
                        response.setImage_url(rs.getString("image_url"));

                        brandResponseModels.add(response);

                    }while (rs.next());

                    return brandResponseModels;
                }
                return null;
            }
        });
    }

    public BrandResponseModel getBrandById(int id){
        String query = "select b.brandName,b.createdBy,b.modifiedBy,b.createdAt,b.modifiedAt,b.isActive,b.image_url from brand b where b.id = ? ";
        return jdbcTemplate.query(query, new Object[]{id}, new ResultSetExtractor<BrandResponseModel>() {
            @Override
            public BrandResponseModel extractData(ResultSet rs) throws SQLException, DataAccessException {
                if(rs.next()){
                    BrandResponseModel response = new BrandResponseModel();

                    response.setBrandName(rs.getString("brandName"));
                    response.setCreatedBy(rs.getString("createdBy"));
                    response.setModifiedBy(rs.getString("modifiedBy"));
                    response.setCreatedAt(rs.getString("createdAt"));
                    response.setModifiedAt(rs.getString("modifiedAt"));
                    response.setActive(rs.getBoolean("isActive"));
                    response.setImage_url(rs.getString("image_url"));

                    return response;
                }
                return null;
            }
        });
    }
}