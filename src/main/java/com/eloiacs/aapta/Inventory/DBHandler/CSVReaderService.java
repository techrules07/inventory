package com.eloiacs.aapta.Inventory.DBHandler;

import com.eloiacs.aapta.Inventory.Models.ExcelItem;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Service
public class CSVReaderService {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public String readFile(MultipartFile file) {

        if (!file.getContentType().equals("text/csv")) {
            throw new IllegalArgumentException("Invalid file type. Please upload a CSV file.");
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            CsvToBean<ExcelItem> csvToBean = new CsvToBeanBuilder<ExcelItem>(reader)
                    .withType(ExcelItem.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();

            List<ExcelItem> items = csvToBean.parse();
            csvToDbProduct(items);

            return "Successfully inserted " + items.size() + " records into the database.";

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error reading CSV file: " + e.getMessage(), e);
        }
    }

    public void csvToDbProduct(List<ExcelItem> items){

        String insertProductQuery = "insert into products(productName,HSNCode,statusType,category,subCategory,brand,unit,size,minPurchaseQuantity,barcodeType,barcodeNo,description,billOfMaterials,freebie,freebieProduct,isActive,createdAt,createdBy) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,current_timestamp(),?)";
        String insertProductPriceQuery = "insert into productPrice (productId,category,subCategory,size,mrp,salesPrice,salesPercentage,wholesalePrice,wholesalePercentage) values(?,?,?,?,?,?,?,?,?)";

        for(ExcelItem item : items){

            double purchasePrice = item.getPRate();
            double mrp = item.getMRP();
            double wholesalesPrice = purchasePrice + (purchasePrice * 2 / 100);
            if (wholesalesPrice > mrp) {
                wholesalesPrice = mrp;
            }

            KeyHolder keyHolder = new GeneratedKeyHolder();

            int insertedProduct = jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(insertProductQuery, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, item.getItemName());
                ps.setString(2,"HSNCode");
                ps.setInt(3, 1);
                ps.setInt(4, item.getCategoryID());
                ps.setInt(5, 1);
                ps.setInt(6, item.getManufacturer());
                ps.setInt(7, item.getUnit());
                ps.setInt(8,1);
                ps.setInt(9, 1);
                ps.setInt(10, 1);
                ps.setString(11, item.getMfgBarcode());
                ps.setString(12, item.getDescription());
                ps.setInt(13, 0);
                ps.setInt(14, 0);
                ps.setInt(15, 0);
                ps.setBoolean(16, item.getActiveStatus());
                ps.setInt(17, 3);
                return ps;
            }, keyHolder);

            int insertedProductId = keyHolder.getKey().intValue();

            if (insertedProduct > 0){

                jdbcTemplate.update(insertProductPriceQuery,
                        insertedProductId,
                        item.getCategoryID(),
                        1,
                        1,
                        item.getMRP(),
                        item.getSRate(),
                        10,
                        wholesalesPrice,
                        2);
            }
        }
    }
}
