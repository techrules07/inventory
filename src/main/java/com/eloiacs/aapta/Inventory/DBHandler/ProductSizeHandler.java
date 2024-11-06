package com.eloiacs.aapta.Inventory.DBHandler;

import com.eloiacs.aapta.Inventory.Models.BaseModel;
import com.eloiacs.aapta.Inventory.Models.ProductSizeRequestModel;
import com.eloiacs.aapta.Inventory.Responses.ProductSizeResponseModel;
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
public class ProductSizeHandler{

    @Autowired
    JdbcTemplate jdbcTemplate;

    public Boolean addProductSize(ProductSizeRequestModel model, String createdBy) {
        String query =  "Insert INTO  productSize (size,createdBy,modifiedBy,modifiedAt) VALUES ('"+model.getSizeName()+"','"+createdBy+"','"+createdBy+"',now() )";
        try {
            jdbcTemplate.execute(query);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    public int updateProductSize(ProductSizeRequestModel model,String createdBy) {
        String query = "UPDATE productSize set size ='"+model.getSizeName()+"',modifiedBy='"+createdBy+"',modifiedAt=now() where id='"+model.getId()+"' ";
        jdbcTemplate.execute(query);
        return 1;
    }

    public int deleteProductSize(BaseModel model) {
        String query = "Update  productSize set isActive=false where id='"+model.getRequestId()+"'";
        jdbcTemplate.execute(query);
        return 1;
    }

    public int checkProductSizeExist(String name){
        int exist = 0;
        String query = "SELECT * FROM `productSize` WHERE LOWER(size) LIKE LOWER('%"+name+"%')";
        return jdbcTemplate.query(query, new ResultSetExtractor<Integer>() {
            @Override
            public Integer extractData(ResultSet rs) throws SQLException, DataAccessException {
                return rs.next() ? 1 : 0;
            }
        });
    }

    public int productSizeExist(int id){
        int exist = 0;
        String query = "Select * from productSize where id='"+id+"'";
        return jdbcTemplate.query(query, new ResultSetExtractor<Integer>() {
            @Override
            public Integer extractData(ResultSet rs) throws SQLException, DataAccessException {
                return rs.next() ? 1 : 0;
            }
        });
    }

    public List<ProductSizeResponseModel> getProductSize(){

        String query = "select ps.id,ps.size,ps.createdBy,ps.modifiedBy,ps.createdAt,ps.modifiedAt,ps.isActive from productSize ps where isActive=true ";

        return jdbcTemplate.query(query, new ResultSetExtractor<List<ProductSizeResponseModel>>() {
            @Override
            public List<ProductSizeResponseModel> extractData(ResultSet rs) throws SQLException, DataAccessException {

                if(rs.next()){
                    List <ProductSizeResponseModel> productSizeResponseModelList = new ArrayList<>();
                    do {
                        ProductSizeResponseModel responseModel = new ProductSizeResponseModel();

                        responseModel.setId(rs.getInt("id"));
                        responseModel.setSizeName(rs.getString("size"));
                        responseModel.setCreatedBy(rs.getString("createdBy"));
                        responseModel.setModifiedBy(rs.getString("modifiedBy"));
                        responseModel.setCreatedAt(Utils.convertDateToString(rs.getTimestamp("createdAt")));
                        responseModel.setModifiedAt(Utils.convertDateToString(rs.getTimestamp("modifiedAt")));
                        responseModel.setActive(rs.getBoolean("isActive"));

                        productSizeResponseModelList.add(responseModel);

                    }while (rs.next());
                    return productSizeResponseModelList;
                }

                return null;
            }
        });

    }
}
