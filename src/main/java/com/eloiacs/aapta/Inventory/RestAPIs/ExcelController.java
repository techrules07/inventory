package com.eloiacs.aapta.Inventory.RestAPIs;

import com.eloiacs.aapta.Inventory.DBHandler.CSVReaderService;
import com.eloiacs.aapta.Inventory.Responses.BaseResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

        BaseResponse baseResponse = new BaseResponse();

        try {
            String uploadFile = csvReader.readFile(file);
            baseResponse.setCode(HttpStatus.OK.value());
            baseResponse.setStatus("Success");
            baseResponse.setMessage("File uploaded successfully");
            baseResponse.setData(uploadFile);
            return baseResponse;
        } catch (Exception e) {
            baseResponse.setCode(HttpStatus.NO_CONTENT.value());
            baseResponse.setStatus("Failed");
            baseResponse.setMessage("File upload failed: " + e.getMessage());
            return baseResponse;
        }
    }
}
