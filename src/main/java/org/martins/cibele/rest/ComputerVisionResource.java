package org.martins.cibele.rest;


import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;
import org.martins.cibele.service.ComputerVisionService;

@Path("/api/v1/computer-vision")
@Consumes(MediaType.MULTIPART_FORM_DATA)
@Produces(MediaType.APPLICATION_JSON)
public class ComputerVisionResource {

    @Inject
    ComputerVisionService computerVisionService;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response postImage(@RestForm("image") FileUpload image){
        return Response.ok(computerVisionService.getTextByImageSDK(image)).build();
    }

    @POST
    @Path("image-classification")
    @Produces(MediaType.APPLICATION_JSON)
    public Response postImageClassification(@RestForm("image") FileUpload image) throws Exception {
        return Response.ok(computerVisionService.getClassificationREST(image)).build();
    }


}
