package com.eloiacs.aapta.Inventory.RestAPIs;

import com.eloiacs.aapta.Inventory.DBHandler.AuthHandler;
import com.eloiacs.aapta.Inventory.Models.AuthModel;
import com.eloiacs.aapta.Inventory.Models.LoginModel;
import com.eloiacs.aapta.Inventory.Responses.BaseResponse;
import com.eloiacs.aapta.Inventory.Responses.InventoryResponseModel;
import com.eloiacs.aapta.Inventory.Responses.OrderResponse;
import com.eloiacs.aapta.Inventory.Responses.UserInfoResponse;
import com.eloiacs.aapta.Inventory.Service.JwtService;
import com.eloiacs.aapta.Inventory.utils.Utils;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/auth")
@SecurityScheme(
        name = "Authorization",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
@SecurityRequirement(name = "Authorization")
@CrossOrigin("*")
public class AuthController {

    @Autowired
    JwtService jwtService;

    @Autowired
    UserDetailsService userDetailsService;

    @Autowired
    AuthHandler authHandler;

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String authController(@RequestBody LoginModel loginModel) {
        AuthModel authModel = authHandler.accountDetails(loginModel);

        if (authModel != null) {
            return jwtService.generateJWToken(authModel.getEmail(), authModel);
        } else {
            throw new UsernameNotFoundException("Invalid credential provided");
        }
    }


    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public BaseResponse register(@RequestBody AuthModel authModel, HttpServletRequest request) {

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

            Boolean existingUserEmail = authHandler.checkUserEmailExist(authModel.getEmail());

            if (existingUserEmail) {
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("Email is already registered.");
                return baseResponse;
            }

            Boolean existingUserMobile = authHandler.checkUserMobileExist(authModel.getMobileNumber());

            if (existingUserMobile) {
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("Mobile Number is already registered.");
                return baseResponse;
            }

            Boolean account = authHandler.createAccount(authModel, createdBy);

            if (account) {
                baseResponse.setCode(HttpStatus.OK.value());
                baseResponse.setStatus("Success");
                baseResponse.setMessage("Registered Successfully");
            } else {
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("Failed to register");
            }
        } else {
            baseResponse.setCode(HttpStatus.FORBIDDEN.value());
            baseResponse.setMessage("Please Login again");
            baseResponse.setStatus("Failed");
        }

        return baseResponse;
    }


    @RequestMapping(value = "/getUserInfo", method = RequestMethod.POST)
    public BaseResponse getUserInfo(HttpServletRequest httpServletRequest) {
        BaseResponse baseResponse = new BaseResponse();


        HashMap<String, Object> claims = jwtService.extractUserInformationFromToken(
                httpServletRequest.getHeader("Authorization")
        );

        if (claims == null) {
            baseResponse.setCode(HttpStatus.UNAUTHORIZED.value());
            baseResponse.setStatus("Failed");
            baseResponse.setMessage("Invalid token or session expired");
            return baseResponse;
        }

        String createdBy = claims.get("id") != null ? claims.get("id").toString() : null;
        String expireDate = claims.get("exp") != null ? claims.get("exp").toString() : null;

        if (createdBy == null) {
            baseResponse.setCode(HttpStatus.UNAUTHORIZED.value());
            baseResponse.setStatus("Failed");
            baseResponse.setMessage("User ID missing in token");
            return baseResponse;
        }

        if (Utils.checkExpired(expireDate)) {
            LoginModel loginModel = authHandler.getUserDetails(createdBy);


            AuthModel authModel = authHandler.accountDetails(loginModel);

            if (authModel != null) {
                baseResponse.setAccessToken(jwtService.generateJWToken(authModel.getEmail(), authModel));
            } else {
                baseResponse.setAccessToken("");
                baseResponse.setCode(HttpStatus.FORBIDDEN.value());
                baseResponse.setMessage("Please login again");
                baseResponse.setStatus("Failed");
                return baseResponse;
            }
        }

        UserInfoResponse userInfoResponse = authHandler.getUserInfo(createdBy);

        if (userInfoResponse == null) {
            baseResponse.setCode(HttpStatus.NO_CONTENT.value());
            baseResponse.setMessage("No User Info available ");
            baseResponse.setStatus("Failed");
        } else {
            baseResponse.setCode(HttpStatus.OK.value());
            baseResponse.setStatus("Success");
            baseResponse.setData(userInfoResponse);
        }

        return baseResponse;
    }
}