package org.martins.cibele.client;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.martins.cibele.domain.ImageAnalysisRequest;


@RegisterRestClient(configKey = "cv-client")
@Consumes(MediaType.APPLICATION_OCTET_STREAM)
@Produces(MediaType.APPLICATION_JSON)
@ConfigProperty(name = "")
public interface ComputerVisionClient {

    @POST
    @Path("/computervision/imageanalysis:analyze")
    String sendImage(    @QueryParam("api-version") String modelVersion,
                         @QueryParam("features") String features,
                       @QueryParam("language") String language,
                       @HeaderParam("Ocp-Apim-Subscription-Key") String auth,
                       ImageAnalysisRequest body);

}
