package com.eloiacs.aapta.Inventory.RestAPIs;

import com.eloiacs.aapta.Inventory.DBHandler.AuthHandler;
import com.eloiacs.aapta.Inventory.DBHandler.BrandHandler;
import com.eloiacs.aapta.Inventory.Models.*;
import com.eloiacs.aapta.Inventory.Responses.BaseResponse;
import com.eloiacs.aapta.Inventory.Responses.BrandResponseModel;
import com.eloiacs.aapta.Inventory.Service.JwtService;
import com.eloiacs.aapta.Inventory.config.AWSConfig;
import com.eloiacs.aapta.Inventory.utils.Utils;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/brand")
@SecurityScheme(
        name = "Authorization",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
@SecurityRequirement(name = "Authorization")
@CrossOrigin("*")
public class BrandController {

    @Autowired
    JwtService jwtService;

    @Autowired
    BrandHandler brandHandler;

    @Autowired
    AWSConfig awsConfig;

    @Autowired
    AuthHandler authHandler;


    @RequestMapping(value = "/insertBrand", method = RequestMethod.POST)
    public BaseResponse insertBrand(@RequestBody BrandRequestModel model, HttpServletRequest request) {

        BaseResponse baseResponse = new BaseResponse();

        HashMap<String, Object> claims = jwtService.extractUserInformationFromToken(request.getHeader("Authorization"));

        if (claims != null) {

            String createdBy = claims.get("id").toString();
            String expireDate = claims.get("exp").toString();

            if (Utils.checkExpired(expireDate)) {

                LoginModel loginModel = authHandler.getUserDetails(createdBy);
                AuthModel model1 = authHandler.accountDetails(loginModel);

                if (model1 != null) {
                    baseResponse.setAccessToken(jwtService.generateJWToken(model1.getEmail(), model1));
                } else {
                    baseResponse.setAccessToken("");
                }
            }

            String filePath = "";

            if (model.getImageUrl()!=null && !model.getImageUrl().isEmpty()){
                filePath = awsConfig.uploadBase64ImageToS3(model.getImageUrl(), model.getBrandName());
            }

            Boolean brandModel = brandHandler.insertBrand(filePath,model, createdBy);

            if (brandModel) {
                baseResponse.setCode(HttpStatus.OK.value());
                baseResponse.setStatus("Success");
                baseResponse.setMessage("Brand Added Successfully");
            }
            else {
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("Failed To Add");
            }
        }
        else {
            baseResponse.setCode(HttpStatus.FORBIDDEN.value());
            baseResponse.setMessage("Please Login again");
            baseResponse.setStatus("Failed");
        }

        return baseResponse;
    }

    @RequestMapping(value = "/deleteBrand", method = RequestMethod.POST)
    public BaseResponse deleteBrand(@RequestBody BaseModel model, HttpServletRequest request) {

        BaseResponse baseResponse = new BaseResponse();

        HashMap<String, Object> claims = jwtService.extractUserInformationFromToken(request.getHeader("Authorization"));

        if (claims != null) {

            String createdBy = claims.get("id").toString();
            String expireDate = claims.get("exp").toString();

            if (Utils.checkExpired(expireDate)) {

                LoginModel loginModel = authHandler.getUserDetails(createdBy);
                AuthModel model1 = authHandler.accountDetails(loginModel);

                if (model1 != null) {
                    baseResponse.setAccessToken(jwtService.generateJWToken(model1.getEmail(), model1));
                } else {
                    baseResponse.setAccessToken("");
                }
            }

            Boolean brandModel = brandHandler.deleteBrand(model);

            if (brandModel) {
                baseResponse.setCode(HttpStatus.OK.value());
                baseResponse.setStatus("Success");
                baseResponse.setMessage("Brand Deleted Successfully");
            }
            else {
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("Failed To Delete");
            }
        }
        else {
            baseResponse.setCode(HttpStatus.FORBIDDEN.value());
            baseResponse.setMessage("Please Login again");
            baseResponse.setStatus("Failed");
        }

        return baseResponse;
    }

    @RequestMapping(value = "/updateBrand", method = RequestMethod.POST)
    public BaseResponse updateBrand(@RequestBody BrandRequestModel model, HttpServletRequest request) {

        BaseResponse baseResponse = new BaseResponse();

        HashMap<String, Object> claims = jwtService.extractUserInformationFromToken(request.getHeader("Authorization"));

        if (claims != null) {

            String createdBy = claims.get("id").toString();
            String expireDate = claims.get("exp").toString();

            if (Utils.checkExpired(expireDate)) {

                LoginModel loginModel = authHandler.getUserDetails(createdBy);
                AuthModel model1 = authHandler.accountDetails(loginModel);

                if (model1 != null) {
                    baseResponse.setAccessToken(jwtService.generateJWToken(model1.getEmail(), model1));
                } else {
                    baseResponse.setAccessToken("");
                }
            }

            BrandResponseModel brandResponse = brandHandler.getBrandById(model.getId());
            if (brandResponse == null) {
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("Brand Does Not Exist");
                return baseResponse;
            }

            BrandResponseModel brandResponseModel = brandHandler.getBrandById(model.getId());

            String filePath = brandResponseModel.getImageUrl();

            if (model.getImageUrl()!=null && !model.getImageUrl().isEmpty()){
                filePath = awsConfig.uploadBase64ImageToS3(model.getImageUrl(), model.getBrandName());
            }

            Boolean brandModel = brandHandler.updateBrand(filePath,model,createdBy);

            if (brandModel) {
                baseResponse.setCode(HttpStatus.OK.value());
                baseResponse.setStatus("Success");
                baseResponse.setMessage("Brand Updated Successfully");
            }
            else {
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("Failed To Update");
            }
        }
        else {
            baseResponse.setCode(HttpStatus.FORBIDDEN.value());
            baseResponse.setMessage("Please Login again");
            baseResponse.setStatus("Failed");
        }

        return baseResponse;
    }

    @RequestMapping(value = "/getBrand", method = RequestMethod.POST)
    @ResponseBody
    public BaseResponse getBrand(@RequestParam (value = "brand",required = false)String brand,HttpServletRequest httpServletRequest){

        BaseResponse baseResponse = new BaseResponse();
        HashMap<String, Object> claims = jwtService.extractUserInformationFromToken(httpServletRequest.getHeader("Authorization"));

        if (claims != null) {
            String createdBy = claims.get("id").toString();
            String expireDate = claims.get("exp").toString();

            if (Utils.checkExpired(expireDate)){
                LoginModel loginModel = authHandler.getUserDetails(createdBy);
                AuthModel model1 = authHandler.accountDetails(loginModel);
                if (model1 != null) {
                    baseResponse.setAccessToken(jwtService.generateJWToken(model1.getEmail(), model1));
                }
                else {
                    baseResponse.setAccessToken("");
                }
            }
            List<BrandResponseModel> brandResponseModelList = brandHandler.getBrand(brand);
            if (brandResponseModelList == null || brandResponseModelList.isEmpty()) {
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setMessage("No Brand available");
                baseResponse.setStatus("Failed");
            }
            else {
                baseResponse.setCode(HttpStatus.OK.value());
                baseResponse.setStatus("Success");
                baseResponse.setData(brandResponseModelList);
            }
        }
        else {
            baseResponse.setCode(HttpStatus.NO_CONTENT.value());
            baseResponse.setStatus("Failed");
            baseResponse.setMessage("Please login again");
        }
        return baseResponse;
    }

}
