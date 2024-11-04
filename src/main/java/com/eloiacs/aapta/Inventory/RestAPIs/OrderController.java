package com.eloiacs.aapta.Inventory.RestAPIs;

import com.eloiacs.aapta.Inventory.DBHandler.AuthHandler;
import com.eloiacs.aapta.Inventory.DBHandler.OrderHandler;
import com.eloiacs.aapta.Inventory.Models.AuthModel;
import com.eloiacs.aapta.Inventory.Models.LoginModel;
import com.eloiacs.aapta.Inventory.Models.OrderRequestModel;
import com.eloiacs.aapta.Inventory.Responses.BaseResponse;
import com.eloiacs.aapta.Inventory.Service.JwtService;
import com.eloiacs.aapta.Inventory.utils.Utils;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

@RestController
@RequestMapping("/order")
@SecurityScheme(
        name = "Authorization",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
@SecurityRequirement(name = "Authorization")
@CrossOrigin( origins = "*")
public class OrderController {

    @Autowired
    JwtService jwtService;

    @Autowired
    AuthHandler authHandler;

    @Autowired
    OrderHandler orderHandler;

    @RequestMapping(value = "/addOrder", method = RequestMethod.POST)
    public BaseResponse addOrder(OrderRequestModel orderRequestModel,
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

            Boolean responseStatus = orderHandler.addOrder(orderRequestModel, createdBy);

            if(responseStatus){
                baseResponse.setCode(HttpStatus.OK.value());
                baseResponse.setStatus("Success");
                baseResponse.setMessage("Order created Successfully");
            } else {
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("Order creation Failed");
            }
        } else {
            baseResponse.setCode(HttpStatus.FORBIDDEN.value());
            baseResponse.setStatus("Failed");
            baseResponse.setMessage("Please login again");
        }

        return baseResponse;
    }

    @RequestMapping(value = "/editOrder", method = RequestMethod.POST)
    public BaseResponse editOrder(OrderRequestModel orderRequestModel,
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

            Boolean responseStatus = orderHandler.updateOrder(orderRequestModel, createdBy);

            if(responseStatus){
                baseResponse.setCode(HttpStatus.OK.value());
                baseResponse.setStatus("Success");
                baseResponse.setMessage("Order updated Successfully");
            } else {
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("Order update Failed");
            }
        } else {
            baseResponse.setCode(HttpStatus.FORBIDDEN.value());
            baseResponse.setStatus("Failed");
            baseResponse.setMessage("Please login again");
        }

        return baseResponse;
    }
}
