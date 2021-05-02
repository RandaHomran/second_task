package com.example.sampleproject.service;
import com.example.sampleproject.exception.AppConfigExceptions;
import com.example.sampleproject.model.ServerModel;

public interface ServerService {
    /**
     *  Allocate a memory in a server by given size
     * @param size desired size of the server
     * @return server model object after memory allocation
     */
    ServerModel allocate(int size) throws AppConfigExceptions;
}
