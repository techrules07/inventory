package com.eloiacs.aapta.Inventory.DBHandler;

import com.eloiacs.aapta.Inventory.Models.InventoryRequestModel;
import com.eloiacs.aapta.Inventory.Responses.InventoryResponseModel;
import com.eloiacs.aapta.Inventory.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class InventoryHandler {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public boolean updateInventory(InventoryRequestModel model){
        String query = "update inventory set productId='"+model.getProductId()+"',category='"+model.getCategory()+"',subCategory='"+model.getSubCategory()+"',count='"+model.getCount()+"' where id ='"+model.getId()+"' ";
        jdbcTemplate.execute(query);
        return true;
    }

    public List<InventoryResponseModel> getInventoryList(){

        String query = "SELECT iv.id,iv.productId,p.productName,iv.category as categoryId,c.category_name as categoryName,iv.count,iv.subCategory as subCategoryId,sc.subCategoryName,iv.size as sizeId,ps.size AS sizeName,iv.isActive,iv.createdBy,iv.createdAt FROM inventory iv LEFT JOIN products p ON p.id = iv.productId LEFT JOIN category c ON c.id = iv.category LEFT JOIN subcategory sc ON sc.id = iv.subCategory LEFT JOIN productSize ps ON ps.id = iv.size WHERE iv.isActive = true ";

        return jdbcTemplate.query(query, new ResultSetExtractor<List<InventoryResponseModel>>() {
            @Override
            public List<InventoryResponseModel> extractData(ResultSet rs) throws SQLException, DataAccessException {

               if(rs.next()){
                  List<InventoryResponseModel> inventoryResponseModelList = new ArrayList<>();

                  do {
                      InventoryResponseModel responseModel = new InventoryResponseModel();

                      responseModel.setId(rs.getInt("id"));
                      responseModel.setProductId(rs.getInt("productId"));
                      responseModel.setProductName(rs.getString("productName"));
                      responseModel.setCategoryId(rs.getInt("categoryId"));
                      responseModel.setCategoryName(rs.getString("categoryName"));
                      responseModel.setSubCategory(rs.getInt("subCategoryId"));
                      responseModel.setSubCategoryName(rs.getString("subCategoryName"));
                      responseModel.setSizeId(rs.getInt("sizeId"));
                      responseModel.setSizeName(rs.getString("sizeName"));
                      responseModel.setCount(rs.getInt("count"));
                      responseModel.setActive(rs.getBoolean("isActive"));
                      responseModel.setCreatedBy(rs.getString("createdBy"));
                      responseModel.setCreatedAt(Utils.convertDateToString(rs.getTimestamp("createdAt")));

                      inventoryResponseModelList.add(responseModel);

                  }while (rs.next());
                  return  inventoryResponseModelList;
               }return null;
            }
        });
    }

    public int inventoryExist(int id){
        int exist = 0;
        String query = "Select * from inventory where id='"+id+"'";
        return jdbcTemplate.query(query, new ResultSetExtractor<Integer>() {
            @Override
            public Integer extractData(ResultSet rs) throws SQLException, DataAccessException {
                return rs.next() ? 1 : 0;
            }
        });
    }

}
