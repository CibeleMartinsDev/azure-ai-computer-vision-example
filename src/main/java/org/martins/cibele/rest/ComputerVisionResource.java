package org.martins.cibele.rest;


import com.azure.core.annotation.BodyParam;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.multipart.FileUpload;
import org.martins.cibele.service.image.ComputerVisionSDKService;
import org.martins.cibele.service.image.ComputerVisionService;

@Path("/api/v1/computer-vision")
@Consumes(MediaType.MULTIPART_FORM_DATA)
@Produces(MediaType.APPLICATION_JSON)
public class ComputerVisionResource {

    @Inject
    ComputerVisionService computerVisionService;

    @Inject
    ComputerVisionSDKService computerVisionSDKService;

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public RestResponse postImage(@RestForm("image") FileUpload image, @RestForm("feature") String feature, @RestForm("sdkOrRest")  String sdkOrRest) throws Exception {

        if(sdkOrRest.equalsIgnoreCase("SDK")){
            return RestResponse.ok(computerVisionSDKService.getAnalysisImageSDK(image, feature));
        }else {
            return RestResponse.ok(computerVisionService.analyzeImage(image, feature));
        }

    }

    @POST
    @Path("image-classification")
    @Produces(MediaType.APPLICATION_JSON)
    public Response postImageClassification(@RestForm("image") FileUpload image) throws Exception {
        return Response.ok(computerVisionService.getClassificationREST(image)).build();
    }


}
