package com.eloiacs.aapta.Inventory.RestAPIs;


import com.eloiacs.aapta.Inventory.DBHandler.AuthHandler;
import com.eloiacs.aapta.Inventory.DBHandler.ProductSizeHandler;
import com.eloiacs.aapta.Inventory.Models.AuthModel;
import com.eloiacs.aapta.Inventory.Models.BaseModel;
import com.eloiacs.aapta.Inventory.Models.LoginModel;
import com.eloiacs.aapta.Inventory.Models.ProductSizeRequestModel;
import com.eloiacs.aapta.Inventory.Responses.BaseResponse;
import com.eloiacs.aapta.Inventory.Responses.ProductSizeResponseModel;
import com.eloiacs.aapta.Inventory.Service.JwtService;
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
@RequestMapping("/productSize")
@SecurityScheme(
        name = "Authorization",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
@SecurityRequirement(name = "Authorization")
@CrossOrigin("*")
public class ProductSizeController {

    @Autowired
    JwtService jwtService;

    @Autowired
    ProductSizeHandler productSizeHandler;

    @Autowired
    AuthHandler authHandler;

    @RequestMapping(value = "/addProductSize", method = RequestMethod.POST)
    @ResponseBody
    public BaseResponse addProductSize(@RequestBody ProductSizeRequestModel model, HttpServletRequest httpServletRequest){

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

            int productSizeExist = productSizeHandler.checkProductSizeExist(model.getSizeName());

            if (productSizeExist==1){
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("ProductSize already exist");
                return baseResponse;
            }

            Boolean responseStatus = productSizeHandler.addProductSize(model,createdBy);
            if(responseStatus){
                baseResponse.setCode(HttpStatus.OK.value());
                baseResponse.setStatus("Success");
                baseResponse.setMessage("ProductSize Added Successfully");
            } else {
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("Failed To Add");
            }
        }else {
            baseResponse.setCode(HttpStatus.FORBIDDEN.value());
            baseResponse.setStatus("Failed");
            baseResponse.setMessage("Please login again");
        }

        return baseResponse;
    }

    @RequestMapping(value = "/editProductSize" , method = RequestMethod.POST)
    @ResponseBody
    public BaseResponse editProductSize(@RequestBody ProductSizeRequestModel model, HttpServletRequest httpServletRequest){

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

            int productSizeExist = productSizeHandler.productSizeExist(model.getId());

            if (productSizeExist==0){
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("ProductSize Does Not Exist");
                return baseResponse;
            }

            int updateProductSize = productSizeHandler.updateProductSize(model,createdBy);

            if (updateProductSize==1){
                baseResponse.setCode(HttpStatus.OK.value());
                baseResponse.setStatus("Success");
                baseResponse.setMessage("ProductSize updated Successfully");
            }else {
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("Failed To Edit");
            }
        }else {
            baseResponse.setCode(HttpStatus.FORBIDDEN.value());
            baseResponse.setStatus("Failed");
            baseResponse.setMessage("Please login again");
        }


        return baseResponse;
    }

    @RequestMapping(value = "/deleteProductSize" , method = RequestMethod.POST)
    @ResponseBody
    public BaseResponse deleteProductSize(@RequestBody BaseModel model, HttpServletRequest httpServletRequest){

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
            if (productSizeHandler.productSizeExist(model.getRequestId())==0){
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("ProductSize Does Not Exist");
                return baseResponse;
            }
            int deleteProductSize = productSizeHandler.deleteProductSize(model);

            if (deleteProductSize==1){
                baseResponse.setCode(HttpStatus.OK.value());
                baseResponse.setStatus("Success");
                baseResponse.setMessage("Deleted Successfully");
            }else {
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("Failed To Delete");
            }
        }else {
            baseResponse.setCode(HttpStatus.FORBIDDEN.value());
            baseResponse.setStatus("Failed");
            baseResponse.setMessage("Please login again");
        }


        return baseResponse;
    }

    @RequestMapping(value = "/getProductSize" , method = RequestMethod.POST)
    @ResponseBody
    public BaseResponse getProductSize(HttpServletRequest httpServletRequest){

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

            List<ProductSizeResponseModel> getProductSize = productSizeHandler.getProductSize();

            if (getProductSize==null || getProductSize.isEmpty()){
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("No Data Found");
            }else{
                baseResponse.setCode(HttpStatus.OK.value());
                baseResponse.setStatus("Success");
                baseResponse.setMessage("ProductSize Get Successfully");
                baseResponse.setData(getProductSize);
            }
        }else{
            baseResponse.setCode(HttpStatus.FORBIDDEN.value());
            baseResponse.setStatus("Failed");
            baseResponse.setMessage("Please login again");
        }
        return baseResponse;
    }

}
