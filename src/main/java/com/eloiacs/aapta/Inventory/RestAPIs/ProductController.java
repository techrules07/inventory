package com.eloiacs.aapta.Inventory.RestAPIs;

import com.eloiacs.aapta.Inventory.DBHandler.AuthHandler;
import com.eloiacs.aapta.Inventory.DBHandler.ProductHandler;
import com.eloiacs.aapta.Inventory.Models.*;
import com.eloiacs.aapta.Inventory.Responses.BaseResponse;
import com.eloiacs.aapta.Inventory.Responses.ProductResponse;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/products")
@SecurityScheme(
        name = "Authorization",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
@SecurityRequirement(name = "Authorization")
@CrossOrigin( origins = "*")
public class ProductController {

    @Autowired
    JwtService jwtService;

    @Autowired
    AWSConfig awsConfig;

    @Autowired
    AuthHandler authHandler;

    @Autowired
    ProductHandler productHandler;

    @RequestMapping(value = "/addProduct", method = RequestMethod.POST)
    public BaseResponse addProduct(@RequestBody ProductRequestModel productRequestModel,
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

            List<String> filePaths = new ArrayList<>();

            if (productRequestModel.getImages() != null && productRequestModel.getImages().stream().anyMatch(image -> image != null && !image.trim().isEmpty())){
                filePaths = awsConfig.uploadMultipleBase64ImagesToS3(productRequestModel.getImages(), productRequestModel.getProductName());
            }

            if (productRequestModel.getProductName() == null || productRequestModel.getProductName().isEmpty()){
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("Product name cannot be null or empty");
                return baseResponse;
            }

            if (productRequestModel.getStatusTypeId() == 0){
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("StatusType Id cannot be zero");
                return baseResponse;
            }

            if (productRequestModel.getCategoryId() == 0){
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("Category Id cannot be zero");
                return baseResponse;
            }

            if (productRequestModel.getSubCategoryId() == 0){
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("SubCategory Id cannot be zero");
                return baseResponse;
            }

            if (productRequestModel.getBrandId() == 0){
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("Brand Id cannot be zero");
                return baseResponse;
            }

            if (productRequestModel.getUnitId() == 0){
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("Unit Id cannot be zero");
                return baseResponse;
            }

            if (productRequestModel.getQuantity() == 0){
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("Quantity cannot be zero");
                return baseResponse;
            }

            if (productRequestModel.getMinPurchaseQuantity() == 0){
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("Minimum Purchase Quantity cannot be zero");
                return baseResponse;
            }

            if (productRequestModel.getBarcodeType() == 0){
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("BarcodeType Id cannot be zero");
                return baseResponse;
            }

            if (productRequestModel.getBarcodeNo() == null || productRequestModel.getBarcodeNo().isEmpty()){
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("Barcode Number cannot be null or empty");
                return baseResponse;
            }

            if (productRequestModel.getDescription() == null || productRequestModel.getDescription().isEmpty()){
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("Description cannot be null or empty");
                return baseResponse;
            }

            if (productRequestModel.getPurchasePrice() == 0){
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("Purchase Price cannot be zero");
                return baseResponse;
            }

            if (productRequestModel.getSalesPricePercentage() == 0){
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("Sales Price Percentage cannot be zero");
                return baseResponse;
            }

            if (productRequestModel.getMrp() == 0){
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("MRP cannot be zero");
                return baseResponse;
            }

            if (productRequestModel.getWholesalePricePercentage() == 0){
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("Wholesale Price Percentage cannot be zero");
                return baseResponse;
            }

            if (productRequestModel.getThreshold() == 0){
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("Threshold cannot be zero");
                return baseResponse;
            }

            if (productRequestModel.getFreebie()){
                if (productRequestModel.getFreebieProductId() == 0){
                    baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                    baseResponse.setStatus("Failed");
                    baseResponse.setMessage("Freebie Product Id cannot be zero when freebie is true");
                    return baseResponse;
                }
            }

            if (productRequestModel.getBillOfMaterials()) {
                if (productRequestModel.getBillOfMaterialsList().isEmpty()) {
                    baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                    baseResponse.setStatus("Failed");
                    baseResponse.setMessage("Bill of Materials cannot be empty when billOfMaterials is true");
                    return baseResponse;
                }

                for (BillOfMaterialsRequestModel item : productRequestModel.getBillOfMaterialsList()) {
                    if (item.getBillOfMaterialsProductId() == 0) {
                        baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                        baseResponse.setStatus("Failed");
                        baseResponse.setMessage("Bill Of Materials Product Id cannot be zero");
                        return baseResponse;
                    }

                    if (item.getBillOfMaterialsProductQuantity() <= 0) {
                        baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                        baseResponse.setStatus("Failed");
                        baseResponse.setMessage("Bill Of Materials Quantity cannot be zero");
                        return baseResponse;
                    }

                    if (item.getBillOfMaterialsProductCost() <= 0) {
                        baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                        baseResponse.setStatus("Failed");
                        baseResponse.setMessage("Bill Of Materials Cost cannot be zero");
                        return baseResponse;
                    }
                }
            }

//            Boolean productNameExist = productHandler.productExistByName(productRequestModel.getProductName());
//            if (productNameExist){
//                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
//                baseResponse.setStatus("Failed");
//                baseResponse.setMessage("Product Name Already Exists");
//                return baseResponse;
//            }

            Boolean statusTypeExist = productHandler.statusTypeExistById(productRequestModel.getStatusTypeId());
            if (!statusTypeExist){
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("StatusType doesn't exist");
                return baseResponse;
            }

            Boolean categoryExist = productHandler.categoryExistById(productRequestModel.getCategoryId());
            if (!categoryExist){
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("Category doesn't exist");
                return baseResponse;
            }

            Boolean subCategoryExist = productHandler.subCategoryExistById(productRequestModel.getSubCategoryId());
            if (!subCategoryExist){
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("SubCategory doesn't exist");
                return baseResponse;
            }

            Boolean brandExist = productHandler.brandExistById(productRequestModel.getBrandId());
            if (!brandExist){
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("Brand doesn't exist");
                return baseResponse;
            }

            Boolean unitExist = productHandler.unitExistById(productRequestModel.getUnitId());
            if (!unitExist){
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("Unit doesn't exist");
                return baseResponse;
            }

            if (productRequestModel.getFreebie() && productRequestModel.getFreebieProductId()!=0) {
                Boolean freebieProductExist = productHandler.productExistById(productRequestModel.getFreebieProductId());
                if (!freebieProductExist) {
                    baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                    baseResponse.setStatus("Failed");
                    baseResponse.setMessage("Freebie Product doesn't exist");
                    return baseResponse;
                }
            }

            for (BillOfMaterialsRequestModel billOfMaterials : productRequestModel.getBillOfMaterialsList()){
                if (billOfMaterials.getBillOfMaterialsProductId() != 0){
                    Boolean billOfMaterialsProductExist = productHandler.productExistById(billOfMaterials.getBillOfMaterialsProductId());
                    if (!billOfMaterialsProductExist){
                        baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                        baseResponse.setStatus("Failed");
                        baseResponse.setMessage("BillOfMaterials Product doesn't exist");
                        return baseResponse;
                    }
                }
            }

            Boolean responseStatus = productHandler.insertProduct(productRequestModel, createdBy, filePaths);

            if(responseStatus){
                baseResponse.setCode(HttpStatus.OK.value());
                baseResponse.setStatus("Success");
                baseResponse.setMessage("Product Added Successfully");
            } else {
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("Product Addition Failed");
            }
        } else {
            baseResponse.setCode(HttpStatus.FORBIDDEN.value());
            baseResponse.setStatus("Failed");
            baseResponse.setMessage("Please login again");
        }

        return baseResponse;
    }

    @RequestMapping(value = "/editProduct", method = RequestMethod.POST)
    public BaseResponse editProduct(@RequestBody ProductRequestModel productRequestModel,
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

            ProductResponse existingProduct = productHandler.getProductById(productRequestModel.getProductId());

            List<String> filePaths = existingProduct.getImages();

            if (productRequestModel.getImages() != null && productRequestModel.getImages().stream().anyMatch(image -> image != null && !image.trim().isEmpty())){
                filePaths = awsConfig.uploadMultipleBase64ImagesToS3(productRequestModel.getImages(), productRequestModel.getProductName());
            }

            if (productRequestModel.getProductId() == 0){
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("Product Id cannot be zero");
                return baseResponse;
            }

            if (productRequestModel.getProductName() == null || productRequestModel.getProductName().isEmpty()){
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("Product name cannot be null or empty");
                return baseResponse;
            }

            if (productRequestModel.getStatusTypeId() == 0){
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("StatusType Id cannot be zero");
                return baseResponse;
            }

            if (productRequestModel.getCategoryId() == 0){
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("Category Id cannot be zero");
                return baseResponse;
            }

            if (productRequestModel.getSubCategoryId() == 0){
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("SubCategory Id cannot be zero");
                return baseResponse;
            }

            if (productRequestModel.getBrandId() == 0){
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("Brand Id cannot be zero");
                return baseResponse;
            }

            if (productRequestModel.getUnitId() == 0){
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("Unit Id cannot be zero");
                return baseResponse;
            }

            if (productRequestModel.getQuantity() == 0){
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("Quantity cannot be zero");
                return baseResponse;
            }

            if (productRequestModel.getMinPurchaseQuantity() == 0){
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("Minimum Purchase Quantity cannot be zero");
                return baseResponse;
            }

            if (productRequestModel.getBarcodeType() == 0){
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("BarcodeType Id cannot be zero");
                return baseResponse;
            }

            if (productRequestModel.getBarcodeNo() == null || productRequestModel.getBarcodeNo().isEmpty()){
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("Barcode Number cannot be null or empty");
                return baseResponse;
            }

            if (productRequestModel.getDescription() == null || productRequestModel.getDescription().isEmpty()){
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("Description cannot be null or empty");
                return baseResponse;
            }

            if (productRequestModel.getPurchasePrice() == 0){
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("Purchase Price cannot be zero");
                return baseResponse;
            }

            if (productRequestModel.getSalesPricePercentage() == 0){
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("Sales Price Percentage cannot be zero");
                return baseResponse;
            }

            if (productRequestModel.getMrp() == 0){
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("MRP cannot be zero");
                return baseResponse;
            }

            if (productRequestModel.getWholesalePricePercentage() == 0){
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("Wholesale Price Percentage cannot be zero");
                return baseResponse;
            }

            if (productRequestModel.getThreshold() == 0){
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("Threshold cannot be zero");
                return baseResponse;
            }

            if (productRequestModel.getFreebie()){
                if (productRequestModel.getFreebieProductId() == 0){
                    baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                    baseResponse.setStatus("Failed");
                    baseResponse.setMessage("Freebie Product Id cannot be zero when freebie is true");
                    return baseResponse;
                }
            }

            if (productRequestModel.getBillOfMaterials()) {
                if (productRequestModel.getBillOfMaterialsList().isEmpty()) {
                    baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                    baseResponse.setStatus("Failed");
                    baseResponse.setMessage("Bill of Materials cannot be empty when billOfMaterials is true");
                    return baseResponse;
                }

                for (BillOfMaterialsRequestModel item : productRequestModel.getBillOfMaterialsList()) {
                    if (item.getBillOfMaterialsProductId() == 0) {
                        baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                        baseResponse.setStatus("Failed");
                        baseResponse.setMessage("Bill Of Materials Product Id cannot be zero");
                        return baseResponse;
                    }

                    if (item.getBillOfMaterialsProductQuantity() <= 0) {
                        baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                        baseResponse.setStatus("Failed");
                        baseResponse.setMessage("Bill Of Materials Quantity cannot be zero");
                        return baseResponse;
                    }

                    if (item.getBillOfMaterialsProductCost() <= 0) {
                        baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                        baseResponse.setStatus("Failed");
                        baseResponse.setMessage("Bill Of Materials Cost cannot be zero");
                        return baseResponse;
                    }
                }
            }

            Boolean productExist = productHandler.productExistById(productRequestModel.getProductId());
            if (!productExist){
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("Product doesn't exist");
                return baseResponse;
            }

//            Boolean productNameExist = productHandler.productExistByName(productRequestModel.getProductName());
//            if (productNameExist){
//                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
//                baseResponse.setStatus("Failed");
//                baseResponse.setMessage("Product Name Already Exists");
//                return baseResponse;
//            }

            Boolean statusTypeExist = productHandler.statusTypeExistById(productRequestModel.getStatusTypeId());
            if (!statusTypeExist){
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("StatusType doesn't exist");
                return baseResponse;
            }

            Boolean categoryExist = productHandler.categoryExistById(productRequestModel.getCategoryId());
            if (!categoryExist){
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("Category doesn't exist");
                return baseResponse;
            }

            Boolean subCategoryExist = productHandler.subCategoryExistById(productRequestModel.getSubCategoryId());
            if (!subCategoryExist){
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("SubCategory doesn't exist");
                return baseResponse;
            }

            Boolean brandExist = productHandler.brandExistById(productRequestModel.getBrandId());
            if (!brandExist){
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("Brand doesn't exist");
                return baseResponse;
            }

            Boolean unitExist = productHandler.unitExistById(productRequestModel.getUnitId());
            if (!unitExist){
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("Unit doesn't exist");
                return baseResponse;
            }

            if (productRequestModel.getFreebie() && productRequestModel.getFreebieProductId()!=0) {
                Boolean freebieProductExist = productHandler.productExistById(productRequestModel.getFreebieProductId());
                if (!freebieProductExist) {
                    baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                    baseResponse.setStatus("Failed");
                    baseResponse.setMessage("Freebie Product doesn't exist");
                    return baseResponse;
                }
            }

            for (BillOfMaterialsRequestModel billOfMaterials : productRequestModel.getBillOfMaterialsList()){
                if (billOfMaterials.getBillOfMaterialsProductId() != 0){
                    Boolean billOfMaterialsProductExist = productHandler.productExistById(billOfMaterials.getBillOfMaterialsProductId());
                    if (!billOfMaterialsProductExist){
                        baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                        baseResponse.setStatus("Failed");
                        baseResponse.setMessage("BillOfMaterials Product doesn't exist");
                        return baseResponse;
                    }
                }
            }

            Boolean responseStatus = productHandler.updateProduct(productRequestModel, createdBy, filePaths);

            if(responseStatus){
                baseResponse.setCode(HttpStatus.OK.value());
                baseResponse.setStatus("Success");
                baseResponse.setMessage("Product Updated Successfully");
            } else {
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("Product Update Failed");
            }
        } else {
            baseResponse.setCode(HttpStatus.FORBIDDEN.value());
            baseResponse.setStatus("Failed");
            baseResponse.setMessage("Please login again");
        }

        return baseResponse;
    }

    @RequestMapping(value = "/deleteProduct", method = RequestMethod.POST)
    public BaseResponse deleteProduct(@RequestBody BaseModel baseModel,
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

            if (baseModel.getRequestId() == 0){
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("Product Id cannot be zero");
                return baseResponse;
            }

            Boolean productExist = productHandler.productExistById(baseModel.getRequestId());
            if (!productExist){
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("Product doesn't exist");
                return baseResponse;
            }

            Boolean responseStatus = productHandler.deleteProduct(baseModel);

            if(responseStatus){
                baseResponse.setCode(HttpStatus.OK.value());
                baseResponse.setStatus("Success");
                baseResponse.setMessage("Product Deleted Successfully");
            } else {
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("Product Deletion Failed");
            }
        } else {
            baseResponse.setCode(HttpStatus.FORBIDDEN.value());
            baseResponse.setStatus("Failed");
            baseResponse.setMessage("Please login again");
        }

        return baseResponse;
    }

    @RequestMapping(value = "/getProducts", method = RequestMethod.POST)
    public BaseResponse getProducts(@RequestParam(value ="productName" ,required = false) String productName, HttpServletRequest httpServletRequest){

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

            List<ProductResponse> productResponses = productHandler.getProducts(productName);

            if (productResponses!=null && !productResponses.isEmpty()){
                baseResponse.setCode(HttpStatus.OK.value());
                baseResponse.setStatus("Success");
                baseResponse.setMessage("Products got successfully");
                baseResponse.setData(productResponses);
            }else {
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("No product found");
            }
        }else {
            baseResponse.setCode(HttpStatus.FORBIDDEN.value());
            baseResponse.setStatus("Failed");
            baseResponse.setMessage("Please login again");
        }

        return baseResponse;
    }

    @RequestMapping(value = "/getProductById", method = RequestMethod.POST)
    public BaseResponse getProductsById(@RequestParam int productId, HttpServletRequest httpServletRequest){

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

            ProductResponse productResponses = productHandler.getProductById(productId);

            if (productResponses!=null){
                baseResponse.setCode(HttpStatus.OK.value());
                baseResponse.setStatus("Success");
                baseResponse.setMessage("Products got successfully");
                baseResponse.setData(productResponses);
            }else {
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("No product found");
            }
        }else {
            baseResponse.setCode(HttpStatus.FORBIDDEN.value());
            baseResponse.setStatus("Failed");
            baseResponse.setMessage("Please login again");
        }

        return baseResponse;
    }


    @RequestMapping(value = "/getProductByBarcode", method = RequestMethod.POST)
    public BaseResponse getProductByBarcode(@RequestParam("barcode") String barcode,
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

            if (barcode == null || barcode.isEmpty()){
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("barcode cannot be null or empty");
                return baseResponse;
            }

            ProductResponse productResponse = productHandler.getProductByBarcode(barcode);

            if (productResponse!=null){
                baseResponse.setCode(HttpStatus.OK.value());
                baseResponse.setStatus("Success");
                baseResponse.setMessage("Product got successfully");
                baseResponse.setData(productResponse);
            }else {
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("No product found");
            }
        }else {
            baseResponse.setCode(HttpStatus.FORBIDDEN.value());
            baseResponse.setStatus("Failed");
            baseResponse.setMessage("Please login again");
        }

        return baseResponse;
    }

    @RequestMapping(value = "/getProductsByBarcodeOrName", method = RequestMethod.POST)
    public BaseResponse getProductsByBarcodeOrName(@RequestParam("inputText") String inputText,
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

            if (inputText == null || inputText.isEmpty()){
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("Search cannot be null or empty");
                return baseResponse;
            }

            List<ProductResponse> productResponses = productHandler.getProductsByBarcodeOrName(inputText);

            if (productResponses!=null && !productResponses.isEmpty()){
                baseResponse.setCode(HttpStatus.OK.value());
                baseResponse.setStatus("Success");
                baseResponse.setMessage("Products got successfully");
                baseResponse.setData(productResponses);
            }else {
                baseResponse.setCode(HttpStatus.NO_CONTENT.value());
                baseResponse.setStatus("Failed");
                baseResponse.setMessage("No product found");
            }
        }else {
            baseResponse.setCode(HttpStatus.FORBIDDEN.value());
            baseResponse.setStatus("Failed");
            baseResponse.setMessage("Please login again");
        }

        return baseResponse;
    }
}
