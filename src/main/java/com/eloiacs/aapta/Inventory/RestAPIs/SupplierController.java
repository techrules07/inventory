package com.eloiacs.aapta.Inventory.RestAPIs;

import com.eloiacs.aapta.Inventory.DBHandler.AuthHandler;
import com.eloiacs.aapta.Inventory.DBHandler.SupplierHandler;
import com.eloiacs.aapta.Inventory.Models.*;
import com.eloiacs.aapta.Inventory.Responses.BaseResponse;
import com.eloiacs.aapta.Inventory.Responses.BrandResponseModel;
import com.eloiacs.aapta.Inventory.Responses.SupplierResponseModel;
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
@RequestMapping("/supplier")
@SecurityScheme(
        name = "Authorization",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
@SecurityRequirement(name = "Authorization")
@CrossOrigin("*")
public class SupplierController {

    @Autowired
    JwtService jwtService;

    @Autowired
    SupplierHandler supplierHandler;

    @Autowired
    AuthHandler authHandler;


    @RequestMapping(value = "/insertSupplier", method = RequestMethod.POST)
    public BaseResponse insertSupplier(@RequestBody SupplierRequestModel model, HttpServletRequest request) {

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

            Boolean supplierModel = supplierHandler.insertSupplier(model, createdBy);

            if (supplierModel) {
                baseResponse.setCode(HttpStatus.OK.value());
                baseResponse.setStatus("Success");
                baseResponse.setMessage("Supplier Added Successfully");
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

    @RequestMapping(value = "/deleteSupplier", method = RequestMethod.POST)
    public BaseResponse deleteSupplier(@RequestBody BaseModel model, HttpServletRequest request) {

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

            Boolean supplierModel = supplierHandler.deleteSupplier(model);

            if (supplierModel) {
                baseResponse.setCode(HttpStatus.OK.value());
                baseResponse.setStatus("Success");
                baseResponse.setMessage("Supplier Deleted Successfully");
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

    @RequestMapping(value = "/updateSupplier", method = RequestMethod.POST)
    public BaseResponse updateSupplier(@RequestBody SupplierRequestModel model, HttpServletRequest request) {

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

            SupplierResponseModel supplierRequestModel = supplierHandler.getSupplierById(model.getId());

            if (supplierRequestModel == null) {
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("Supplier Id Does Not Exist");
                return baseResponse;
            }

            Boolean supplierModel = supplierHandler.updateSupplier(model,createdBy);

            if (supplierModel) {
                baseResponse.setCode(HttpStatus.OK.value());
                baseResponse.setStatus("Success");
                baseResponse.setMessage("Supplier Updated Successfully");
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

    @RequestMapping(value = "/getSupplier", method = RequestMethod.POST)
    @ResponseBody
    public BaseResponse getSupplier(@RequestParam(value = "supplierCode",required = false)String supplierCode, HttpServletRequest httpServletRequest){

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

            List<SupplierResponseModel> supplierResponseModelList = supplierHandler.getSupplier(supplierCode);

            if (supplierResponseModelList == null || supplierResponseModelList.isEmpty()) {
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setMessage("No Supplier available");
                baseResponse.setStatus("Failed");
            }
            else {
                baseResponse.setCode(HttpStatus.OK.value());
                baseResponse.setStatus("Success");
                baseResponse.setData(supplierResponseModelList);
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
