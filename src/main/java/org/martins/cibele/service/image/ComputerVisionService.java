package org.martins.cibele.service.image;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.multipart.FileUpload;
import org.martins.cibele.client.ComputerVisionClient;
import org.martins.cibele.client.CustomComputerVisionClient;
import org.martins.cibele.domain.ImageAnalysisRequest;


import java.nio.file.Files;


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


    public String analyzeImage(FileUpload image, String feature) throws Exception {
        try {
            ImageAnalysisRequest request = new ImageAnalysisRequest();
            request.setUrl(image.filePath().toString());
            String result = computerVisionClient.sendImage(feature, "", "pt", "", true, "2023-04-01", "Bearer " + key, request);
            return result;
        }catch (Exception e){
            throw new Exception("Ocorreu um erro ao analisar a imagem: " + e.getCause() + e.getMessage());
        }
    }


    public String getClassificationREST(FileUpload image) throws Exception {
      try {
          byte[] imageBytes = Files.readAllBytes(image.filePath());
          return customComputerVisionClient.sendImageForClassificaiton(imageBytes, predictionKey, "application/octet-stream");
      }catch (Exception e ){
          throw new Exception("Ocorreu um erro ao classificar a imagem: " + e.getCause() + e.getMessage());
      }
    }


}
