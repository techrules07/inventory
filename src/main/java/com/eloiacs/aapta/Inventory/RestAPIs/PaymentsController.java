package com.eloiacs.aapta.Inventory.RestAPIs;

import com.eloiacs.aapta.Inventory.DBHandler.AuthHandler;
import com.eloiacs.aapta.Inventory.DBHandler.OrderHandler;
import com.eloiacs.aapta.Inventory.DBHandler.PaymentsHandler;
import com.eloiacs.aapta.Inventory.Models.AuthModel;
import com.eloiacs.aapta.Inventory.Models.LoginModel;
import com.eloiacs.aapta.Inventory.Models.PaymentsRequestModel;
import com.eloiacs.aapta.Inventory.Responses.BaseResponse;
import com.eloiacs.aapta.Inventory.Responses.PaymentTypeResponse;
import com.eloiacs.aapta.Inventory.Responses.PaymentsResponse;
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
@RequestMapping("/payments")
@SecurityScheme(
        name = "Authorization",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
@SecurityRequirement(name = "Authorization")
@CrossOrigin( origins = "*")
public class PaymentsController {

    @Autowired
    JwtService jwtService;

    @Autowired
    AuthHandler authHandler;

    @Autowired
    PaymentsHandler paymentsHandler;

    @Autowired
    OrderHandler orderHandler;

    @RequestMapping(value = "/addPayments", method = RequestMethod.POST)
    public BaseResponse addPayments(@RequestBody PaymentsRequestModel paymentsRequestModel,
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

            if (paymentsRequestModel.getPaymentTypeId() == 0){
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("PaymentTypeId cannot be zero");
                return baseResponse;
            }

            if (paymentsRequestModel.getAmount() == 0){
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("Amount cannot be zero");
                return baseResponse;
            }

            if (paymentsRequestModel.getOrderId() == null || paymentsRequestModel.getOrderId().isEmpty()){
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("OrderId cannot be null or empty");
                return baseResponse;
            }

            if (paymentsRequestModel.getCustomerId() == null || paymentsRequestModel.getCustomerId().isEmpty()){
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("CustomerId cannot be null or empty");
                return baseResponse;
            }

            Boolean paymentTypeExist = paymentsHandler.paymentTypeExistById(paymentsRequestModel.getPaymentTypeId());
            if (!paymentTypeExist){
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("PaymentType does not exist");
                return baseResponse;
            }

            Boolean orderIdExist = orderHandler.orderExistByOrderId(paymentsRequestModel.getOrderId());
            if (!orderIdExist){
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("Order does not exist");
                return baseResponse;
            }

            Boolean customerExist = paymentsHandler.customerExistByCustomerId(paymentsRequestModel.getCustomerId());
            if (!customerExist){
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("Customer does not exist");
                return baseResponse;
            }

            Boolean responseStatus = paymentsHandler.addPayments(paymentsRequestModel);

            if(responseStatus){
                baseResponse.setCode(HttpStatus.OK.value());
                baseResponse.setStatus("Success");
                baseResponse.setMessage("Payments created Successfully");
            } else {
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("Payments creation Failed");
            }
        } else {
            baseResponse.setCode(HttpStatus.FORBIDDEN.value());
            baseResponse.setStatus("Failed");
            baseResponse.setMessage("Please login again");
        }

        return baseResponse;
    }

    @RequestMapping(value = "/editPayments", method = RequestMethod.POST)
    public BaseResponse editPayments(@RequestBody PaymentsRequestModel paymentsRequestModel,
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

            if (paymentsRequestModel.getPaymentId() == null || paymentsRequestModel.getPaymentId().isEmpty()){
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("PaymentId cannot be null or empty");
                return baseResponse;
            }

            if (paymentsRequestModel.getPaymentTypeId() == 0){
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("PaymentTypeId cannot be zero");
                return baseResponse;
            }

            if (paymentsRequestModel.getAmount() == 0){
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("Amount cannot be zero");
                return baseResponse;
            }

            if (paymentsRequestModel.getOrderId() == null || paymentsRequestModel.getOrderId().isEmpty()){
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("OrderId cannot be null or empty");
                return baseResponse;
            }

            if (paymentsRequestModel.getCustomerId() == null || paymentsRequestModel.getCustomerId().isEmpty()){
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("CustomerId cannot be null or empty");
                return baseResponse;
            }

            Boolean paymentExist = paymentsHandler.paymentExistByPaymentId(paymentsRequestModel.getPaymentId());
            if (!paymentExist){
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("Payment does not exist");
                return baseResponse;
            }

            Boolean paymentTypeExist = paymentsHandler.paymentTypeExistById(paymentsRequestModel.getPaymentTypeId());
            if (!paymentTypeExist){
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("PaymentType does not exist");
                return baseResponse;
            }

            Boolean orderIdExist = orderHandler.orderExistByOrderId(paymentsRequestModel.getOrderId());
            if (!orderIdExist){
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("Order does not exist");
                return baseResponse;
            }

            Boolean customerExist = paymentsHandler.customerExistByCustomerId(paymentsRequestModel.getCustomerId());
            if (!customerExist){
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("Customer does not exist");
                return baseResponse;
            }

            Boolean responseStatus = paymentsHandler.updatePayments(paymentsRequestModel);

            if(responseStatus){
                baseResponse.setCode(HttpStatus.OK.value());
                baseResponse.setStatus("Success");
                baseResponse.setMessage("Payments updated Successfully");
            } else {
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("Payments update Failed");
            }
        } else {
            baseResponse.setCode(HttpStatus.FORBIDDEN.value());
            baseResponse.setStatus("Failed");
            baseResponse.setMessage("Please login again");
        }

        return baseResponse;
    }

    @RequestMapping(value = "/getPayments", method = RequestMethod.POST)
    public BaseResponse getPayments(HttpServletRequest httpServletRequest){

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

            List<PaymentsResponse> paymentsResponses = paymentsHandler.getPayments();

            if (paymentsResponses!=null && !paymentsResponses.isEmpty()){
                baseResponse.setCode(HttpStatus.OK.value());
                baseResponse.setStatus("Success");
                baseResponse.setMessage("Payments got successfully");
                baseResponse.setData(paymentsResponses);
            }else {
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("No Payment found");
            }
        }else {
            baseResponse.setCode(HttpStatus.FORBIDDEN.value());
            baseResponse.setStatus("Failed");
            baseResponse.setMessage("Please login again");
        }

        return baseResponse;
    }

    @RequestMapping(value = "/getPaymentTypes", method = RequestMethod.POST)
    public BaseResponse getPaymentTypes(HttpServletRequest httpServletRequest){

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

            List<PaymentTypeResponse> paymentTypeResponses = paymentsHandler.getPaymentTypes();

            if (paymentTypeResponses!=null && !paymentTypeResponses.isEmpty()){
                baseResponse.setCode(HttpStatus.OK.value());
                baseResponse.setStatus("Success");
                baseResponse.setMessage("PaymentTypes got successfully");
                baseResponse.setData(paymentTypeResponses);
            }else {
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("No PaymentType found");
            }
        }else {
            baseResponse.setCode(HttpStatus.FORBIDDEN.value());
            baseResponse.setStatus("Failed");
            baseResponse.setMessage("Please login again");
        }

        return baseResponse;
    }
}
