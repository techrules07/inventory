package com.eloiacs.aapta.Inventory.DBHandler;

import com.eloiacs.aapta.Inventory.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class BarcodeHandlers {

    @Autowired
    JdbcTemplate jdbcTemplate;


    public int createNewBarcodes() {
        System.out.println(Utils.generateBarcodes());
        return 1;
    }
}
