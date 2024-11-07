package com.eloiacs.aapta.Inventory.DBHandler;

import com.eloiacs.aapta.Inventory.Responses.UnitResponseModel;
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
public class UnitHandler {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public List<UnitResponseModel> getUnitTable(){
        String query = "select ut.id,ut.unitName,ut.unitSmall,ut.createdBy,ut.modifiedBy,ut.createdAt,ut.modifiedAt,ut.isActive from unitTable ut where isActive=true ";
        return jdbcTemplate.query(query, new ResultSetExtractor<List<UnitResponseModel>>() {
            @Override
            public List<UnitResponseModel> extractData(ResultSet rs) throws SQLException, DataAccessException {
                if (rs.next()) {
                    List<UnitResponseModel> unitResponseList = new ArrayList<>();
                    do {
                        UnitResponseModel model = new UnitResponseModel();
                        model.setId(rs.getInt("id"));
                        model.setUnitName(rs.getString("unitName"));
                        model.setUnitSmall(rs.getString("unitSmall"));
                        model.setModifiedAt(Utils.convertDateToString(rs.getTimestamp("modifiedAt")));
                        model.setCreatedAt(Utils.convertDateToString(rs.getTimestamp("createdAt")));
                        model.setCreatedBy(rs.getString("createdBy"));
                        model.setModifiedBy(rs.getString("modifiedBy"));
                        model.setActive(rs.getBoolean("isActive"));

                        unitResponseList.add(model);
                    }
                    while (rs.next());

                    return unitResponseList;
                }
                return null;
            }
        });
    }

}
