package com.eloiacs.aapta.Inventory.RestAPIs;

import com.eloiacs.aapta.Inventory.DBHandler.AuthHandler;
import com.eloiacs.aapta.Inventory.DBHandler.UsersHandler;
import com.eloiacs.aapta.Inventory.Models.AuthModel;
import com.eloiacs.aapta.Inventory.Models.LoginModel;
import com.eloiacs.aapta.Inventory.Models.UsersRequestModel;
import com.eloiacs.aapta.Inventory.Responses.BaseResponse;
import com.eloiacs.aapta.Inventory.Responses.UsersResponseModel;
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
@RequestMapping("/users")
@SecurityScheme(
        name = "Authorization",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
@SecurityRequirement(name = "Authorization")
@CrossOrigin("*")
public class UsersController {


    @Autowired
    JwtService jwtService;

    @Autowired
    AuthHandler authHandler;

    @Autowired
    UsersHandler usersHandler;



    @RequestMapping(value = "/insertUsers", method = RequestMethod.POST)
    public BaseResponse insertUsers(@RequestBody UsersRequestModel model, HttpServletRequest request) {

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


            Boolean users = usersHandler.addUsers(model, createdBy);

            if (users) {
                baseResponse.setCode(HttpStatus.OK.value());
                baseResponse.setStatus("Success");
                baseResponse.setMessage("Users Added Successfully");
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

    @RequestMapping(value = "/getUsers",method = RequestMethod.POST)
    @ResponseBody
    public BaseResponse getUsers(HttpServletRequest httpServletRequest){

        HashMap<String, Object> claims = jwtService.extractUserInformationFromToken(httpServletRequest.getHeader("Authorization"));
        BaseResponse baseResponse = new BaseResponse();

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
            List<UsersResponseModel> usersResponseModelList = usersHandler.getUsers();

            if (usersResponseModelList!=null && usersResponseModelList.size()>0){
                baseResponse.setCode(HttpStatus.OK.value());
                baseResponse.setStatus("Success");
                baseResponse.setData(usersResponseModelList);
            }else {
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Success");
                baseResponse.setMessage("No data found");
            }
        }else {
            baseResponse.setCode(HttpStatus.FORBIDDEN.value());
            baseResponse.setStatus("Failed");
            baseResponse.setMessage("Please login again");
        }

        return baseResponse;
    }
}
