package org.martins.cibele.service;

import com.azure.ai.vision.imageanalysis.ImageAnalysisClient;
import com.azure.ai.vision.imageanalysis.ImageAnalysisClientBuilder;
import com.azure.ai.vision.imageanalysis.models.ImageAnalysisOptions;
import com.azure.ai.vision.imageanalysis.models.ImageAnalysisResult;
import com.azure.ai.vision.imageanalysis.models.VisualFeatures;
import com.azure.core.credential.KeyCredential;
import com.azure.core.exception.HttpResponseException;
import com.azure.core.util.BinaryData;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.multipart.FileUpload;
import org.martins.cibele.client.ComputerVisionClient;


import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;


@ApplicationScoped
public class ComputerVisionService {


    @ConfigProperty(name = "my.property.azure-base-url")
    private String endpoint;

    @ConfigProperty(name = "my.property-azure-api-key")
    private String key;

    @ConfigProperty(name = "my.property.ccv-prediction-endpoint")
    private String predictionEndpoint;

    @ConfigProperty(name = "my.property.ccv-prediction-key")
    private String predictionKey;


    @Inject
    @RestClient
    ComputerVisionClient computerVisionClient;

    public ImageAnalysisResult getTextByImageSDK(FileUpload image){

        Path imagePath = image.filePath();
        ImageAnalysisResult result = null;

        ImageAnalysisClient client = new ImageAnalysisClientBuilder()
                .endpoint(endpoint)
                .credential(new KeyCredential(key))
                .buildClient();

        BinaryData imageData = BinaryData.fromFile(imagePath);

        ImageAnalysisOptions options = new ImageAnalysisOptions()
                .setLanguage("pt")
                .setGenderNeutralCaption(true);

        List<VisualFeatures> visualFeatures = Arrays.asList(
                VisualFeatures.READ);
        try {
            result = client.analyze(
                    imageData,
                    visualFeatures,
                    options);

            return result;
        } catch (HttpResponseException e) {
            System.out.println("Exception: " + e.getClass().getSimpleName());
            System.out.println("Status code: " + e.getResponse().getStatusCode());
            System.out.println("Message: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Message: " + e.getMessage());
        }
        return result;
    }


    public String getClassificationREST(FileUpload image) throws Exception {
      try {
          byte[] imageBytes = Files.readAllBytes(image.filePath());
          return computerVisionClient.sendImageForClassificaiton(imageBytes, predictionKey, "application/octet-stream");
      }catch (Exception e ){
          throw new Exception("Ocorreu um erro ao classificar a imagem: " + e.getCause() + e.getMessage());
      }
    }


}
