package org.martins.cibele.service;

import com.azure.ai.vision.imageanalysis.ImageAnalysisClient;
import com.azure.ai.vision.imageanalysis.ImageAnalysisClientBuilder;
import com.azure.ai.vision.imageanalysis.models.ImageAnalysisOptions;
import com.azure.ai.vision.imageanalysis.models.ImageAnalysisResult;
import com.azure.ai.vision.imageanalysis.models.VisualFeatures;
import com.azure.core.credential.KeyCredential;
import com.azure.core.exception.HttpResponseException;
import com.azure.core.util.BinaryData;
import com.microsoft.azure.cognitiveservices.vision.customvision.prediction.CustomVisionPredictionClient;
import com.microsoft.azure.cognitiveservices.vision.customvision.prediction.CustomVisionPredictionManager;
import com.microsoft.azure.cognitiveservices.vision.customvision.prediction.models.ImagePrediction;
import com.microsoft.azure.cognitiveservices.vision.customvision.prediction.models.Prediction;
import com.microsoft.azure.cognitiveservices.vision.customvision.training.CustomVisionTrainingClient;
import com.microsoft.azure.cognitiveservices.vision.customvision.training.CustomVisionTrainingManager;
import com.microsoft.azure.cognitiveservices.vision.customvision.training.models.Project;
import com.microsoft.azure.cognitiveservices.vision.customvision.training.models.ProjectSettings;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

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

    @ConfigProperty(name = "my.property.ccv-predicition-id")
    private String projectId;

    public ImageAnalysisResult getTextByImage(FileUpload image){

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


    public List<Prediction> getClassification(FileUpload image) throws Exception {
      try {
          CustomVisionPredictionClient predictor = CustomVisionPredictionManager
                  .authenticate(predictionEndpoint, predictionKey)
                  .withEndpoint(predictionEndpoint);

          Path imagePath = image.filePath();
          byte[] imageBytes = Files.readAllBytes(imagePath);

          ImagePrediction results = predictor.predictions().classifyImage().withProjectId(UUID.fromString(projectId))
                  .withPublishedName("ccv-example").withImageData(imageBytes).execute();

          for (Prediction prediction : results.predictions()) {
              System.out.println(String.format("\t%s: %.2f%%", prediction.tagName(), prediction.probability() * 100.0f));
          }
          return results.predictions();
      }catch (Exception e ){
          throw new Exception("Ocorreu um erro ao classificar a imagem: " + e.getCause() + e.getMessage());
      }
    }


}
