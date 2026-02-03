package org.martins.cibele.client;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "ccv-client")
@Consumes(MediaType.MULTIPART_FORM_DATA)
@Produces(MediaType.APPLICATION_JSON)
@ConfigProperty(name = "")
public interface ComputerVisionClient {

    @POST
    String sendImageForClassificaiton(byte[] imageBytes, @HeaderParam("Prediction-Key") String predictionKey, @HeaderParam("Content-Type") String contentType);
}
