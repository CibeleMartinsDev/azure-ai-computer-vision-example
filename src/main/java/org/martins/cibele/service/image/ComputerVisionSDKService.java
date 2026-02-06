package org.martins.cibele.service.image;

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
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.resteasy.reactive.multipart.FileUpload;
import org.martins.cibele.utils.FileUploadUtils;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@ApplicationScoped
public class ComputerVisionSDKService {

    @ConfigProperty(name = "my.property.azure.cv.img.analysis.base.url")
    private String endpoint;

    @ConfigProperty(name = "my.property.azure.cv.img.analysis.api.key")
    private String key;

    @ConfigProperty(name = "my.property.ccv-prediction-endpoint")
    private String predictionEndpoint;

    @ConfigProperty(name = "my.property.ccv-prediction-key")
    private String predictionKey;

    @ConfigProperty(name = "my.property.ccv-project-id")
    private String predictionProjectId;

    public ImageAnalysisResult analyzeImageSDK(FileUpload image, String feature){

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

        List<VisualFeatures> visualFeatures = Stream.of(
                VisualFeatures.READ,
                VisualFeatures.OBJECTS,
                VisualFeatures.CAPTION,
                VisualFeatures.PEOPLE,
                VisualFeatures.DENSE_CAPTIONS,
                VisualFeatures.SMART_CROPS,
                VisualFeatures.TAGS).filter(vF -> vF.getValue().equalsIgnoreCase(feature)).toList();

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

    public List<Prediction> classificationImageSDK(FileUpload image){
        //get image
        BinaryData binaryData = FileUploadUtils.getBinaryData(image);
        //authenticate client
        CustomVisionPredictionClient predictor = CustomVisionPredictionManager
                .authenticate(predictionEndpoint, predictionKey)
                .withEndpoint(predictionEndpoint);
        //make prediction
        ImagePrediction results = predictor.predictions().classifyImage().withProjectId(UUID.fromString(predictionProjectId))
                .withPublishedName("cv-ia-102").withImageData(binaryData.toBytes()).execute();
        return  results.predictions();
    }

//    public String detectionObjectsImageSDK(FileUpload image){
//
//    }

}
