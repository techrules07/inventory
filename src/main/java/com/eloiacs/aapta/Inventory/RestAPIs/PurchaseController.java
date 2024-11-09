package com.eloiacs.aapta.Inventory.RestAPIs;

import com.eloiacs.aapta.Inventory.DBHandler.AuthHandler;
import com.eloiacs.aapta.Inventory.DBHandler.PurchaseHandler;
import com.eloiacs.aapta.Inventory.Models.*;
import com.eloiacs.aapta.Inventory.Responses.BaseResponse;
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

@RestController
@RequestMapping("/purchase")
@SecurityScheme(
        name = "Authorization",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
@SecurityRequirement(name = "Authorization")
@CrossOrigin( origins = "*")
public class PurchaseController {

    @Autowired
    JwtService jwtService;

    @Autowired
    AWSConfig awsConfig;

    @Autowired
    AuthHandler authHandler;

    @Autowired
    PurchaseHandler purchaseHandler;

    @RequestMapping(value = "/addPurchase", method = RequestMethod.POST)
    public BaseResponse addPurchase(@RequestBody PurchaseOrderRequestModel purchaseOrderRequestModel,
                                    HttpServletRequest httpServletRequest){

        BaseResponse baseResponse = new BaseResponse();

        HashMap<String, Object> claims = jwtService.extractUserInformationFromToken(httpServletRequest.getHeader("Authorization"));

        if(claims!=null){

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

            String filePath = "";

            if (purchaseOrderRequestModel.getInvoiceImage()!=null && !purchaseOrderRequestModel.getInvoiceImage().isEmpty()){
                filePath = awsConfig.uploadBase64ImageToS3(purchaseOrderRequestModel.getInvoiceImage(), purchaseOrderRequestModel.getInvoiceId());
            }

            String purchaseOrderId = purchaseHandler.insertPurchaseOrder(purchaseOrderRequestModel, createdBy, filePath);

            if(purchaseOrderId != null){
                baseResponse.setCode(HttpStatus.OK.value());
                baseResponse.setStatus("Success");
                baseResponse.setMessage("PurchaseOrder is created");
                baseResponse.setData(purchaseOrderId);
            } else {
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("PurchaseOrder add Failed");
            }
        } else {
            baseResponse.setCode(HttpStatus.FORBIDDEN.value());
            baseResponse.setStatus("Failed");
            baseResponse.setMessage("Please login again");
        }

        return baseResponse;
    }
}
