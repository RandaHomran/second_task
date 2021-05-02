package com.example.sampleproject.controller;
import com.example.sampleproject.exception.AppConfigExceptions;
import com.example.sampleproject.exception.NegativeNumberException;
import com.example.sampleproject.service.Impl.ServerServiceImpl;
import com.example.sampleproject.service.ServerService;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/server")
public class ServerController {

    ServerService serverService = new ServerServiceImpl();

    /**
     * Get request to allocate memory
     * @param size represents the size in GB to allocate in memory
     * @return Response which contains Server object with server id number and free memory size after memory allocation.
     *         Message when the size greater than maximum acceptable size(100 GB)
     * @throws NegativeNumberException when the size is not a positive number
     */
    @GET
    @Path("/{size}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response allocateServer(@PathParam("size") int size) throws NegativeNumberException, AppConfigExceptions {
        if(size <= 0){
            throw new NegativeNumberException("Size must be positive number, not "+size);
        }
        if(size>100) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("Size must be less than or equal 100 GB").build();
        }
        return Response.ok(serverService.allocate(size), MediaType.APPLICATION_JSON).build();
    }

}
