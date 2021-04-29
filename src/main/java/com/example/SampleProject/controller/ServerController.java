package com.example.SampleProject.controller; //TODO please consider changing the package name.
import com.example.SampleProject.exception.NegativeNumberException;
import com.example.SampleProject.model.Server;
import com.example.SampleProject.service.ServerService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/server")
public class ServerController {
    //TODO what if we need change this service later, like have server service for VIP clients?
    // Design an interface not a class
    //https://tuhrig.de/programming-to-an-interface/
    //https://dzone.com/articles/programming-to-an-interface
    ServerService serverService = new ServerService();

    /**
     * Get request to allocate memory
     * @param size represents the size to allocate in memory //TODO size represented of which measure?
     * @return Server object with server id number and free memory size after memory allocation
     * @throws NegativeNumberException when the size is not a positive number
     */
    //TODO GET is for request data from a specified resource
    //TODO POST send data to a server to create/update a resource
    @GET
    @Path("/{size}")
    @Produces(MediaType.APPLICATION_JSON)
    public Server allocateServer(@PathParam("size") int size) throws NegativeNumberException {
        //TODO what if someone at one go wants to request 1,000,000 space
        if(size <= 0){
            throw new NegativeNumberException("Size must be positive number, not "+size);
        }
        return serverService.allocate(size);
    }

}
