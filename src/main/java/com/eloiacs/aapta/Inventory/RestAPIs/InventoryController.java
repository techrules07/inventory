package com.eloiacs.aapta.Inventory.RestAPIs;

import com.eloiacs.aapta.Inventory.DBHandler.AuthHandler;
import com.eloiacs.aapta.Inventory.DBHandler.InventoryHandler;
import com.eloiacs.aapta.Inventory.Models.AuthModel;
import com.eloiacs.aapta.Inventory.Models.BrandRequestModel;
import com.eloiacs.aapta.Inventory.Models.InventoryRequestModel;
import com.eloiacs.aapta.Inventory.Models.LoginModel;
import com.eloiacs.aapta.Inventory.Responses.BaseResponse;
import com.eloiacs.aapta.Inventory.Responses.BrandResponseModel;
import com.eloiacs.aapta.Inventory.Responses.InventoryResponseModel;
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
@RequestMapping("/inventory")
@SecurityScheme(
        name = "Authorization",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
@SecurityRequirement(name = "Authorization")
@CrossOrigin( origins = "*")
public class InventoryController {

    @Autowired
    JwtService jwtService;

    @Autowired
    AuthHandler authHandler;

    @Autowired
    InventoryHandler inventoryHandler;


    @RequestMapping(value = "/updateInventory", method = RequestMethod.POST)
    public BaseResponse updateBrand(@RequestBody InventoryRequestModel model, HttpServletRequest request) {

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

            int inventoryResponse = inventoryHandler.inventoryExist(model.getId());

            if (inventoryResponse == 0) {
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("Inventory Does Not Exist");
                return baseResponse;
            }

            Boolean inventoryModel = inventoryHandler.updateInventory(model);

            if (inventoryModel) {
                baseResponse.setCode(HttpStatus.OK.value());
                baseResponse.setStatus("Success");
                baseResponse.setMessage("Inventory Updated Successfully");
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

    @RequestMapping(value = "/getInventory", method = RequestMethod.POST)
    @ResponseBody
    public BaseResponse getBrand(HttpServletRequest httpServletRequest){

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

            List<InventoryResponseModel> inventoryResponseModelList = inventoryHandler.getInventoryList();

            if (inventoryResponseModelList == null || inventoryResponseModelList.isEmpty()) {
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setMessage("No Inventory available");
                baseResponse.setStatus("Failed");
            }
            else {
                baseResponse.setCode(HttpStatus.OK.value());
                baseResponse.setStatus("Success");
                baseResponse.setData(inventoryResponseModelList);
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
