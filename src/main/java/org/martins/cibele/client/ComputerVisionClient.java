package org.martins.cibele.client;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.martins.cibele.domain.ImageAnalysisRequest;


@RegisterRestClient(configKey = "cv-client")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@ConfigProperty(name = "")
public interface ComputerVisionClient {

    @POST
    @Path("/imageanalysis:analyze")
    String sendImage(  @QueryParam("features") String features,
                       @QueryParam("model-name") String modelName,
                       @QueryParam("language") String language,
                       @QueryParam("smartcrops-aspect-ratios") String smartCropsAspectRatios,
                       @QueryParam("gender-neutral-caption") Boolean genderNeutralCaption,
                       @QueryParam("api-version") String apiVersion,
                       @HeaderParam("Authorization") String auth,
                       ImageAnalysisRequest body);

}
