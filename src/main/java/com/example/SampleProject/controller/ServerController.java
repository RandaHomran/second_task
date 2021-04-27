package com.example.SampleProject.controller;
import com.example.SampleProject.exception.NegativeNumberException;
import com.example.SampleProject.model.Server;
import com.example.SampleProject.service.ServerService;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/server")
public class ServerController {

    ServerService serverService = new ServerService();

    /**
     * Get request to allocate memory
     * @param size represents the size to allocate in memory
     * @return Server object with server id number and free memory size after memory allocation
     * @throws NegativeNumberException when the size is not a positive number
     */
    @GET
    @Path("/{size}")
    @Produces(MediaType.APPLICATION_JSON)
    public Server allocateServer(@PathParam("size") int size) throws NegativeNumberException {
        if(size <= 0){
            throw new NegativeNumberException("Size must be positive number, not "+size);
        }
        return serverService.allocate(size);
    }

}
