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
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import java.io.FileInputStream;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

@ApplicationScoped
public class ComputerVisionService {


    @ConfigProperty(name = "my.property.azure-base-url")
    private String endpoint;

    @ConfigProperty(name = "my.property-azure-api-key")
    private String key;

    public ImageAnalysisResult getContactsByImage(FileUpload image){

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
}
