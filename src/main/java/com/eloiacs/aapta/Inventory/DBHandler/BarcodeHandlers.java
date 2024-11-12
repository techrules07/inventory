package com.eloiacs.aapta.Inventory.DBHandler;

import com.eloiacs.aapta.Inventory.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class BarcodeHandlers {

    @Autowired
    JdbcTemplate jdbcTemplate;


//    public int createNewBarcodes() {
//        System.out.println(Utils.generateBarcodes());
//        return 1;
//    }


    public int createNewBarcodes() {
        String checkProductTableBarcodeAlreadyExist = "SELECT barcodeNo FROM products";
        Set<Integer> existingBarcodes = new HashSet<>(jdbcTemplate.queryForList(checkProductTableBarcodeAlreadyExist, Integer.class));

        String checkBarcodesTableBarcodeAlreadyExist = "SELECT barcode FROM barcodes";
        existingBarcodes.addAll(jdbcTemplate.queryForList(checkBarcodesTableBarcodeAlreadyExist, Integer.class));

        String insertBarcodeQuery = "INSERT INTO barcodes (barcode, isUsed, usedBy) VALUES (?, ?, ?)";

        int count = 0;
        while (count < 50) {
            int barcode = Utils.generateBarcodes();
            if (!existingBarcodes.contains(barcode)) {
                jdbcTemplate.update(insertBarcodeQuery, barcode, false, null);
                existingBarcodes.add(barcode);
                count++;
            }
        }
        return count;
    }

}
