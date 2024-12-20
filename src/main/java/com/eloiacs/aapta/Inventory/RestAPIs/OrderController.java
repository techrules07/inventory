package com.eloiacs.aapta.Inventory.RestAPIs;

import com.eloiacs.aapta.Inventory.DBHandler.*;
import com.eloiacs.aapta.Inventory.Models.*;
import com.eloiacs.aapta.Inventory.Responses.BaseResponse;
import com.eloiacs.aapta.Inventory.Responses.OrderItemsResponse;
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
import java.util.Map;

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

    @Autowired
    PaymentsHandler paymentsHandler;

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

            if (orderRequestModel.getCustomerId() != null && !orderRequestModel.getCustomerId().isEmpty()){
                Boolean customerExist = paymentsHandler.customerExistByCustomerId(orderRequestModel.getCustomerId());
                if (!customerExist){
                    baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                    baseResponse.setStatus("Failed");
                    baseResponse.setMessage("Customer does not exist");
                    return baseResponse;
                }
            }

            String orderId = orderHandler.addOrder(orderRequestModel, createdBy);

            if(orderId != null && !orderId.isEmpty()){
                baseResponse.setCode(HttpStatus.OK.value());
                baseResponse.setStatus("Success");
                baseResponse.setMessage("Order created Successfully");
                baseResponse.setData(orderId);
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

            if (orderRequestModel.getCustomerId() != null && !orderRequestModel.getCustomerId().isEmpty()){
                Boolean customerExist = paymentsHandler.customerExistByCustomerId(orderRequestModel.getCustomerId());
                if (!customerExist){
                    baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                    baseResponse.setStatus("Failed");
                    baseResponse.setMessage("Customer does not exist");
                    return baseResponse;
                }
            }

            Boolean responseStatus = orderHandler.updateOrder(orderRequestModel);

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

    @RequestMapping(value = "/addOrderItem", method = RequestMethod.POST)
    public BaseResponse addOrderItem(@RequestBody OrderItemsRequestModel orderItemsRequestModel,
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

            if (orderItemsRequestModel.getOrderId() == null || orderItemsRequestModel.getOrderId().isEmpty()){
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("OrderId cannot be null or empty");
                return baseResponse;
            }

            if (orderItemsRequestModel.getProductId() == 0){
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("Product Id cannot be zero");
                return baseResponse;
            }

            Boolean orderIdExist = orderHandler.orderExistByOrderId(orderItemsRequestModel.getOrderId());
            if (!orderIdExist) {
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("Order doesn't exist");
                return baseResponse;
            }

            Boolean productExist = productHandler.productExistById(orderItemsRequestModel.getProductId());
            if (!productExist){
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("Product doesn't exist");
                return baseResponse;
            }

//            Boolean inventoryExistByProductId = orderHandler.inventoryExistByProductId(orderItemsRequestModel.getProductId());
//            if (!inventoryExistByProductId){
//                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
//                baseResponse.setStatus("Failed");
//                baseResponse.setMessage("Product doesn't exist in Inventory");
//                return baseResponse;
//            }

            OrderResponse responseStatus = orderHandler.addOrderItem(orderItemsRequestModel, createdBy);

            if(responseStatus != null){
                baseResponse.setCode(HttpStatus.OK.value());
                baseResponse.setStatus("Success");
                baseResponse.setMessage("OrderItem Added Successfully");
                baseResponse.setData(responseStatus);
            } else {
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("OrderItem Add Failed");
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

    @RequestMapping(value = "/deleteOrderItems", method = RequestMethod.POST)
    public BaseResponse deleteOrderItems(@RequestBody List<DeleteOrderItemModel> deleteOrderItemModelList,
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

            if (deleteOrderItemModelList == null || deleteOrderItemModelList.isEmpty()) {
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("No items provided for deletion");
                return baseResponse;
            }

            for (DeleteOrderItemModel deleteOrderItemModel : deleteOrderItemModelList) {

                if (deleteOrderItemModel.getOrderId() == null || deleteOrderItemModel.getOrderId().isEmpty()) {
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

                if (deleteOrderItemModel.getProductId() == 0) {
                    baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                    baseResponse.setStatus("Failed");
                    baseResponse.setMessage("Product Id cannot be zero");
                    return baseResponse;
                }

                Boolean productExist = productHandler.productExistById(deleteOrderItemModel.getProductId());
                if (!productExist) {
                    baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                    baseResponse.setStatus("Failed");
                    baseResponse.setMessage("Product doesn't exist");
                    return baseResponse;
                }
            }

            OrderResponse orderResponse = orderHandler.deleteOrderItems(deleteOrderItemModelList);

            if(orderResponse != null){
                baseResponse.setCode(HttpStatus.OK.value());
                baseResponse.setStatus("Success");
                baseResponse.setMessage("OrderItems removed successfully");
                baseResponse.setData(orderResponse);
            } else {
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("OrderItems remove Failed");
            }
        } else {
            baseResponse.setCode(HttpStatus.FORBIDDEN.value());
            baseResponse.setStatus("Failed");
            baseResponse.setMessage("Please login again");
        }

        return baseResponse;
    }

    @RequestMapping(value = "/getOrders", method = RequestMethod.POST)
    public BaseResponse getOrders(HttpServletRequest httpServletRequest,
                                  @RequestParam(value = "createdBy",required = false)String createdBy,
                                  @RequestParam(value = "startDate", required = false) String startDate,
                                  @RequestParam(value = "endDate", required = false) String endDate) {

        BaseResponse baseResponse = new BaseResponse();


        HashMap<String, Object> claims = jwtService.extractUserInformationFromToken(httpServletRequest.getHeader("Authorization"));

        if (claims != null) {
            String currentUserId = claims.get("id").toString();
            String expireDate = claims.get("exp").toString();
            String userRole = claims.get("role") != null ? claims.get("role").toString() : "UNKNOWN";

            if (Utils.checkExpired(expireDate)) {
                LoginModel loginModel = authHandler.getUserDetails(createdBy);
                AuthModel model1 = authHandler.accountDetails(loginModel);
                if (model1 != null) {
                    baseResponse.setAccessToken(jwtService.generateJWToken(model1.getEmail(), model1));
                } else {
                    baseResponse.setAccessToken("");
                }
            }
            List<OrderResponse> orderResponses = orderHandler.getOrders(currentUserId,createdBy, userRole, startDate, endDate);

            if (orderResponses != null && !orderResponses.isEmpty()) {
                baseResponse.setCode(HttpStatus.OK.value());
                baseResponse.setStatus("Success");
                baseResponse.setMessage("Orders retrieved successfully");
                baseResponse.setData(orderResponses);
            } else {
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("No orders found");
            }
        } else {
            baseResponse.setCode(HttpStatus.FORBIDDEN.value());
            baseResponse.setStatus("Failed");
            baseResponse.setMessage("Please login again");
        }

        return baseResponse;
    }

    @RequestMapping(value = "/getHoldOrders", method = RequestMethod.POST)
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

    @RequestMapping(value = "/createOrder", method = RequestMethod.POST)
    public BaseResponse createOrder(HttpServletRequest httpServletRequest) {

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

            OrderResponse response = orderHandler.createOrderByCustomerId(createdBy);
            if (response != null){
                baseResponse.setCode(HttpStatus.OK.value());
                baseResponse.setStatus("Success");
                baseResponse.setMessage("Order successfully created");
                baseResponse.setData(response);
            }
            else {
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("Order failed to create");
            }
        }
        else {
            baseResponse.setCode(HttpStatus.FORBIDDEN.value());
            baseResponse.setStatus("Failed");
            baseResponse.setMessage("Please login again");
        }

        return baseResponse;
    }

    @RequestMapping(value = "/completeOrder", method = RequestMethod.POST)
    public BaseResponse completeOrder(HttpServletRequest httpServletRequest,
                                      @RequestParam("orderId") String orderId,
                                      @RequestParam("paymentType") String paymentType) {

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

            OrderResponse orderResponse = orderHandler.completeOrder(orderId, paymentType, createdBy);

            if (orderResponse != null) {
                baseResponse.setCode(HttpStatus.OK.value());
                baseResponse.setStatus("Success");
                baseResponse.setData(orderResponse);
            }
            else {
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("Order Not available");
            }
        }else {
            baseResponse.setCode(HttpStatus.FORBIDDEN.value());
            baseResponse.setStatus("Failed");
            baseResponse.setMessage("Please login again");
        }

        return baseResponse;
    }

    @RequestMapping(value = "/initializePayments", method = RequestMethod.POST)
    public BaseResponse initializePayments(HttpServletRequest httpServletRequest,
                                           @RequestParam("orderId") String orderId,
                                           @RequestParam("customerId") String customerId){

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

            OrderResponse orderResponse = orderHandler.getOrderByOrderId(orderId);
            for(OrderItemsResponse orderItemsResponse : orderResponse.getOrderItems()){
                if (orderItemsResponse.getProductId() != 0){
                    Boolean inventoryStockExist = orderHandler.inventoryStockExistByProductId(orderItemsResponse.getProductId());
                    if (!inventoryStockExist){
                        baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                        baseResponse.setStatus("Failed");
                        baseResponse.setMessage("No Stock in Inventory");
                        return baseResponse;
                    }
                }
            }

            Map<String,Object> map = orderHandler.initializePayments(orderId, customerId);

            if (map != null){
                baseResponse.setCode(HttpStatus.OK.value());
                baseResponse.setStatus("Success");
                baseResponse.setMessage("Payment Initialized");
                baseResponse.setData(map);
            }
            else {
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("Order Initialization failed");
            }
        }
        else {
            baseResponse.setCode(HttpStatus.FORBIDDEN.value());
            baseResponse.setStatus("Failed");
            baseResponse.setMessage("Please login again");
        }

        return baseResponse;
    }
}
