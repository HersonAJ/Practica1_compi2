package com.example.practica1_compi2.api;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Path("/test")
public class TestResource {

    @GET
    public Response test() {
        return Response.ok("El proyecto está funcionando correctamente").build();
    }
}