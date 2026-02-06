package org.martins.cibele.service.image;

import com.azure.ai.vision.imageanalysis.ImageAnalysisClient;
import com.azure.ai.vision.imageanalysis.ImageAnalysisClientBuilder;
import com.azure.ai.vision.imageanalysis.models.ImageAnalysisOptions;
import com.azure.ai.vision.imageanalysis.models.ImageAnalysisResult;
import com.azure.ai.vision.imageanalysis.models.VisualFeatures;
import com.azure.core.credential.KeyCredential;
import com.azure.core.exception.HttpResponseException;
import com.azure.core.util.BinaryData;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@ApplicationScoped
public class ComputerVisionSDKService {

    @ConfigProperty(name = "my.property.azure.cv.img.analysis.base.url")
    private String endpoint;

    @ConfigProperty(name = "my.property.azure.cv.img.analysis.api.key")
    private String key;

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

//    public String classificationImageSDK(FileUpload image){
//
//    }

//    public String detectionObjectsImageSDK(FileUpload image){
//
//    }

}
