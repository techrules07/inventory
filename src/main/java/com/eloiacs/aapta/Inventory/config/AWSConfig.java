package com.eloiacs.aapta.Inventory.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
public class AWSConfig {

    @Value("${accessKey}")
    private String accessKey;

    @Value("${secret}")
    private String secretKey;

    @Value("${bucketName}")
    private String bucketName;

    public AmazonS3 setupS3Client(String accessKey, String secretKey) {

        AWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);

        AmazonS3 client = AmazonS3ClientBuilder.standard()
                .withRegion(Regions.US_EAST_1)
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .build();

        return client;
    }

    public String uploadBase64ImageToS3(String base64String, String name) {

        AmazonS3 s3 = setupS3Client(accessKey, secretKey);

        // Decode Base64 string to byte array
        byte[] decodedBytes = Base64.getDecoder().decode(base64String);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(decodedBytes);

        // Create metadata for the object
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(decodedBytes.length);
        metadata.setContentType("image/jpeg");

        PutObjectRequest request = new PutObjectRequest(bucketName, "appta/supermart/" + name,
                byteArrayInputStream, metadata);
        PutObjectResult result = s3.putObject(request);

        String fileName = s3.getUrl(bucketName, "appta/supermart/" + name).toString();

        return fileName;
    }

    public List<String> uploadMultipleBase64ImagesToS3(List<String> base64Strings, String productName) {

        AmazonS3 s3 = setupS3Client(accessKey, secretKey);
        List<String> filePaths = new ArrayList<>();

        for (int i = 0; i < base64Strings.size(); i++) {
            String base64String = base64Strings.get(i);

            // Decode Base64 string to byte array
            byte[] decodedBytes = Base64.getDecoder().decode(base64String);
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(decodedBytes);

            // Create metadata for the object
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(decodedBytes.length);
            metadata.setContentType("image/jpeg"); // Adjust the content type if needed

            String uniqueFileName = productName + "_" + UUID.randomUUID();
            String key = "bismiAdmin/products/" + uniqueFileName;

            PutObjectRequest request = new PutObjectRequest(bucketName, key,
                    byteArrayInputStream, metadata);
            s3.putObject(request);

            String filePath = s3.getUrl(bucketName, key).toString();
            filePaths.add(filePath);
        }

        return filePaths;
    }

}
