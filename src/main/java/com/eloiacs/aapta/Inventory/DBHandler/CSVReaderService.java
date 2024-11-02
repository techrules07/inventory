package com.eloiacs.aapta.Inventory.DBHandler;

import com.eloiacs.aapta.Inventory.Models.ExcelItem;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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

        String insertProductQuery = "insert into products(productName,statusType,category,subCategory,brand,unit,quantity,minPurchaseQuantity,barcodeType,barcodeNo,description,purchasePrice,gstPercentage,salesPrice,mrp,wholesalePrice,wholesaleGSTPercentage,threshold,billOfMaterials,freebie,freebieProduct,isActive,createdAt,createdBy) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,current_timestamp(),?)";

        for(ExcelItem item : items){

            double purchasePrice = item.getPRate();
            double mrp = item.getMRP();
            double wholesalesPrice = purchasePrice + (purchasePrice * 2 / 100);
            if (wholesalesPrice > mrp) {
                wholesalesPrice = mrp - 1;
            }

            jdbcTemplate.update(insertProductQuery,
                    item.getItemName(),
                    1,
                    item.getCategoryID(),
                    1,
                    item.getManufacturer(),
                    item.getUnit(),
                    1,
                    1,
                    1,
                    item.getMfgBarcode(),
                    item.getDescription(),
                    item.getPRate(),
                    10,
                    item.getSRate(),
                    item.getMRP(),
                    wholesalesPrice,
                    2,
                    1,
                    0,
                    0,
                    0,
                    item.getActiveStatus(),
                    3);
        }
    }
}
