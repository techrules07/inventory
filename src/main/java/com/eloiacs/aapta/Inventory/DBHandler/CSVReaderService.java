package com.eloiacs.aapta.Inventory.DBHandler;

import com.eloiacs.aapta.Inventory.Models.ExcelItem;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Service
public class CSVReaderService {

    public void readFile(MultipartFile file) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
            CsvToBean<ExcelItem> csvToBean = new CsvToBeanBuilder<ExcelItem>(reader)
                    .withType(ExcelItem.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();

            System.out.println(csvToBean.parse().toString());
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }
}
