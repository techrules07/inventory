package com.eloiacs.aapta.Inventory.RestAPIs;

import com.eloiacs.aapta.Inventory.DBHandler.CSVReaderService;
import com.eloiacs.aapta.Inventory.Responses.BaseResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@CrossOrigin("*")
@RequestMapping("upload")
public class ExcelController {

    @Autowired
    CSVReaderService csvReader;

    @PostMapping(value = "uploadFile")
    @ResponseBody
    public BaseResponse uploadExcel(@RequestParam("file") MultipartFile file) {

        csvReader.readFile(file);
        return new BaseResponse();
    }
}
