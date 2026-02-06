package org.martins.cibele.service.image;

import com.azure.core.util.BinaryData;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.UriInfo;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.multipart.FileUpload;
import org.martins.cibele.client.ComputerVisionClient;
import org.martins.cibele.client.CustomComputerVisionClient;
import org.martins.cibele.domain.ImageAnalysisRequest;


import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;


@ApplicationScoped
public class ComputerVisionService {

//    @ConfigProperty(name = "my.property.azure-base-url")
//    private String endpoint;
//
    @ConfigProperty(name = "my.property.azure.cv.img.analysis.api.key")
    private String key;
    @ConfigProperty(name = "my.property.ccv-prediction-endpoint")
    private String predictionEndpoint;

    @ConfigProperty(name = "my.property.ccv-prediction-key")
    private String predictionKey;

    @Inject
    @RestClient
    CustomComputerVisionClient customComputerVisionClient;

    @Inject
    @RestClient
    ComputerVisionClient computerVisionClient;

    @Inject
    ComputerVisionSDKService computerVisionSDKService;

    public String analyzeImage(FileUpload image, String feature) throws Exception {
        try {

            Path pathImage = image.filePath();
            BinaryData  binary = BinaryData.fromFile(pathImage);
            ImageAnalysisRequest request = new ImageAnalysisRequest();
            request.setUrl(binary.toString());
            String result = computerVisionClient.sendImage("2024-02-01",feature, "pt",  key, request);
            return result;
        }catch (Exception e){
            throw new Exception("Ocorreu um erro ao analisar a imagem: " + e.getCause() + e.getMessage());
        }
    }

    public Object getCustomVision(FileUpload image, String feature, String sdkOrRest) throws Exception {
        if(feature.equalsIgnoreCase("classification") && sdkOrRest.equalsIgnoreCase("sdk") ){
            return this.computerVisionSDKService.classificationImageSDK(image);
        }else if(feature.equalsIgnoreCase("detectionObjects") && sdkOrRest.equalsIgnoreCase("sdk") ){
//            return this.computerVisionSDKService.classificationImageSDK(image);
        } else if(feature.equalsIgnoreCase("classification") && sdkOrRest.equalsIgnoreCase("rest") ){
            return this.classificationImageREST(image);
        }else if(feature.equalsIgnoreCase("detectionObjects") && sdkOrRest.equalsIgnoreCase("rest") ){
//            return this.computerVisionSDKService.classificationImageSDK(image);
        }

        return new Object();
    }

    public String classificationImageREST(FileUpload image) throws Exception {
      try {
          byte[] imageBytes = Files.readAllBytes(image.filePath());
          return customComputerVisionClient.sendImageForClassificaiton(imageBytes, predictionKey, "application/octet-stream");
      }catch (Exception e ){
          throw new Exception("Ocorreu um erro ao classificar a imagem: " + e.getCause() + e.getMessage());
      }
    }



}
