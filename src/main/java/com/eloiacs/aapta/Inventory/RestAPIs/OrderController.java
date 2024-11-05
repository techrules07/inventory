package com.eloiacs.aapta.Inventory.RestAPIs;

import com.eloiacs.aapta.Inventory.DBHandler.AuthHandler;
import com.eloiacs.aapta.Inventory.DBHandler.OrderHandler;
import com.eloiacs.aapta.Inventory.DBHandler.ProductHandler;
import com.eloiacs.aapta.Inventory.Models.*;
import com.eloiacs.aapta.Inventory.Responses.BaseResponse;
import com.eloiacs.aapta.Inventory.Responses.OrderResponse;
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

    @Autowired
    ProductHandler productHandler;

    @RequestMapping(value = "/addOrder", method = RequestMethod.POST)
    public BaseResponse addOrder(@RequestBody OrderRequestModel orderRequestModel,
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

            if (orderRequestModel.getCustomerId() == null || orderRequestModel.getCustomerId().isEmpty()){
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("CustomerId cannot be null or empty");
                return baseResponse;
            }

            if (orderRequestModel.getOrderId() != null || !orderRequestModel.getOrderId().isEmpty()) {
                Boolean orderIdExist = orderHandler.orderExistByOrderId(orderRequestModel.getOrderId());
                if (!orderIdExist) {
                    baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                    baseResponse.setStatus("Failed");
                    baseResponse.setMessage("Order doesn't exist");
                    return baseResponse;
                }
            }

            if (orderRequestModel.getOrderItemsList().isEmpty()){
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("orderItems cannot be empty");
                return baseResponse;
            }

            for (OrderItemsRequestModel orderItem : orderRequestModel.getOrderItemsList()){
                if (orderItem.getProductId() == 0){
                    baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                    baseResponse.setStatus("Failed");
                    baseResponse.setMessage("Product Id cannot be zero");
                    return baseResponse;
                }
                if (orderItem.getUnitPrice() == 0){
                    baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                    baseResponse.setStatus("Failed");
                    baseResponse.setMessage("Product Unit Price cannot be zero");
                    return baseResponse;
                }
                if (orderItem.getQuantity() == 0){
                    baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                    baseResponse.setStatus("Failed");
                    baseResponse.setMessage("Product Quantity cannot be zero");
                    return baseResponse;
                }

                if (orderItem.getProductId() != 0){
                    Boolean productExist = productHandler.productExistById(orderItem.getProductId());
                    if (!productExist){
                        baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                        baseResponse.setStatus("Failed");
                        baseResponse.setMessage("Product doesn't exist");
                        return baseResponse;
                    }
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
    public BaseResponse editOrder(@RequestBody OrderRequestModel orderRequestModel,
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

            if (orderRequestModel.getCustomerId() == null || orderRequestModel.getCustomerId().isEmpty()){
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("CustomerId cannot be null or empty");
                return baseResponse;
            }

            if (orderRequestModel.getOrderId() == null || orderRequestModel.getOrderId().isEmpty()){
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("OrderId cannot be null or empty");
                return baseResponse;
            }

            Boolean orderIdExist = orderHandler.orderExistByOrderId(orderRequestModel.getOrderId());
            if (!orderIdExist) {
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("Order doesn't exist");
                return baseResponse;
            }

            if (orderRequestModel.getOrderItemsList().isEmpty()){
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("orderItems cannot be empty");
                return baseResponse;
            }

            for (OrderItemsRequestModel orderItem : orderRequestModel.getOrderItemsList()){
                if (orderItem.getProductId() == 0){
                    baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                    baseResponse.setStatus("Failed");
                    baseResponse.setMessage("Product Id cannot be zero");
                    return baseResponse;
                }
                if (orderItem.getUnitPrice() == 0){
                    baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                    baseResponse.setStatus("Failed");
                    baseResponse.setMessage("Product Unit Price cannot be zero");
                    return baseResponse;
                }
                if (orderItem.getQuantity() == 0){
                    baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                    baseResponse.setStatus("Failed");
                    baseResponse.setMessage("Product Quantity cannot be zero");
                    return baseResponse;
                }

                if (orderItem.getProductId() != 0){
                    Boolean productExist = productHandler.productExistById(orderItem.getProductId());
                    if (!productExist){
                        baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                        baseResponse.setStatus("Failed");
                        baseResponse.setMessage("Product doesn't exist");
                        return baseResponse;
                    }
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

    @RequestMapping(value = "/holdOrder", method = RequestMethod.POST)
    public BaseResponse holdOrder(@RequestParam("orderId") String orderId,
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

            if (orderId == null || orderId.isEmpty()){
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("OrderId cannot be null or empty");
                return baseResponse;
            }

            Boolean orderIdExist = orderHandler.orderExistByOrderId(orderId);
            if (!orderIdExist) {
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("Order doesn't exist");
                return baseResponse;
            }

            Boolean responseStatus = orderHandler.holdOrder(orderId);

            if(responseStatus){
                baseResponse.setCode(HttpStatus.OK.value());
                baseResponse.setStatus("Success");
                baseResponse.setMessage("Order hold success");
            } else {
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("Order hold Failed");
            }
        } else {
            baseResponse.setCode(HttpStatus.FORBIDDEN.value());
            baseResponse.setStatus("Failed");
            baseResponse.setMessage("Please login again");
        }

        return baseResponse;
    }

    @RequestMapping(value = "/cancelOrder", method = RequestMethod.POST)
    public BaseResponse cancelOrder(@RequestParam("orderId") String orderId,
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

            if (orderId == null || orderId.isEmpty()){
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("OrderId cannot be null or empty");
                return baseResponse;
            }

            Boolean orderIdExist = orderHandler.orderExistByOrderId(orderId);
            if (!orderIdExist) {
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("Order doesn't exist");
                return baseResponse;
            }

            Boolean responseStatus = orderHandler.cancelOrder(orderId);

            if(responseStatus){
                baseResponse.setCode(HttpStatus.OK.value());
                baseResponse.setStatus("Success");
                baseResponse.setMessage("Order cancelled successfully");
            } else {
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("Order cancelling Failed");
            }
        } else {
            baseResponse.setCode(HttpStatus.FORBIDDEN.value());
            baseResponse.setStatus("Failed");
            baseResponse.setMessage("Please login again");
        }

        return baseResponse;
    }

    @RequestMapping(value = "/deleteOrderItem", method = RequestMethod.POST)
    public BaseResponse deleteOrderItem(@RequestBody DeleteOrderItemModel deleteOrderItemModel,
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

            if (deleteOrderItemModel.getOrderId() == null || deleteOrderItemModel.getOrderId().isEmpty()){
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("OrderId cannot be null or empty");
                return baseResponse;
            }

            Boolean orderIdExist = orderHandler.orderExistByOrderId(deleteOrderItemModel.getOrderId());
            if (!orderIdExist) {
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("Order doesn't exist");
                return baseResponse;
            }

            if (deleteOrderItemModel.getProductId() == 0){
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("Product Id cannot be zero");
                return baseResponse;
            }

            Boolean productExist = productHandler.productExistById(deleteOrderItemModel.getProductId());
            if (!productExist){
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("Product doesn't exist");
                return baseResponse;
            }

            Boolean responseStatus = orderHandler.deleteOrderItem(deleteOrderItemModel);

            if(responseStatus){
                baseResponse.setCode(HttpStatus.OK.value());
                baseResponse.setStatus("Success");
                baseResponse.setMessage("OrderItem removed successfully");
            } else {
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("OrderItem remove Failed");
            }
        } else {
            baseResponse.setCode(HttpStatus.FORBIDDEN.value());
            baseResponse.setStatus("Failed");
            baseResponse.setMessage("Please login again");
        }

        return baseResponse;
    }

    @RequestMapping(value = "/getOrders", method = RequestMethod.POST)
    public BaseResponse getOrders(HttpServletRequest httpServletRequest){

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

            List<OrderResponse> orderResponses = orderHandler.getOrders();

            if (orderResponses!=null && !orderResponses.isEmpty()){
                baseResponse.setCode(HttpStatus.OK.value());
                baseResponse.setStatus("Success");
                baseResponse.setMessage("Orders got successfully");
                baseResponse.setData(orderResponses);
            }else {
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("No Order found");
            }
        }else {
            baseResponse.setCode(HttpStatus.FORBIDDEN.value());
            baseResponse.setStatus("Failed");
            baseResponse.setMessage("Please login again");
        }

        return baseResponse;
    }

    @RequestMapping(value = "/getHeldOrders", method = RequestMethod.POST)
    public BaseResponse getHeldOrders(HttpServletRequest httpServletRequest){

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

            List<OrderResponse> orderResponses = orderHandler.getHeldOrders();

            if (orderResponses!=null && !orderResponses.isEmpty()){
                baseResponse.setCode(HttpStatus.OK.value());
                baseResponse.setStatus("Success");
                baseResponse.setMessage("Held Orders got successfully");
                baseResponse.setData(orderResponses);
            }else {
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("No Order on hold");
            }
        }else {
            baseResponse.setCode(HttpStatus.FORBIDDEN.value());
            baseResponse.setStatus("Failed");
            baseResponse.setMessage("Please login again");
        }

        return baseResponse;
    }

    @RequestMapping(value = "/getOrderByOrderId", method = RequestMethod.POST)
    public BaseResponse getOrderByOrderId(@RequestParam("orderId") String orderId,
                                          HttpServletRequest httpServletRequest){

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

            if (orderId == null || orderId.isEmpty()){
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("OrderId cannot be null or empty");
                return baseResponse;
            }

            Boolean orderIdExist = orderHandler.orderExistByOrderId(orderId);
            if (!orderIdExist) {
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("Order doesn't exist");
                return baseResponse;
            }

            OrderResponse orderResponse = orderHandler.getOrderByOrderId(orderId);

            if (orderResponse!=null){
                baseResponse.setCode(HttpStatus.OK.value());
                baseResponse.setStatus("Success");
                baseResponse.setMessage("Order got successfully");
                baseResponse.setData(orderResponse);
            }else {
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("No order found");
            }
        }else {
            baseResponse.setCode(HttpStatus.FORBIDDEN.value());
            baseResponse.setStatus("Failed");
            baseResponse.setMessage("Please login again");
        }

        return baseResponse;
    }
}
