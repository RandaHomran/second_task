package com.example.SampleProject.service;
import com.aerospike.client.*;
import com.aerospike.client.policy.Priority;
import com.aerospike.client.policy.ScanPolicy;
import com.aerospike.client.policy.WritePolicy;
import com.example.SampleProject.model.Server;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ServerService {
    //TODO what if I want to pass another parameters here ?
    static AerospikeClient client = new AerospikeClient("172.28.128.3", 3000);
    //TODO why we need to create synchronized list here?
    static List<Server> serversPool = Collections.synchronizedList(new ArrayList<>());

    public Server allocate(int memorySize) {

        serversPool=scanServersPool();
        Server server;

        //TODO What will happen if I will have 30 parallel request ?

        // to prevent two request from accessing the serverPool list concurrently
        synchronized (serversPool){
            server = serversPool.stream()
                    .filter(s -> (s.getFreeSize() >= memorySize && s.getState().equals("active")))
                    .findAny()
                    .orElse(null);
        }

        //TODO please try to move this part into separate method.

        // if there is enough space in servers pool then allocate memory
        if(server != null){
            server = updateServerFreeMemory(server, memorySize);
        }

        else
        {
            //to check if there is another server in the creating state
            synchronized (serversPool) {
                //TODO what is difference of this line and 27?(CODE DUPLICATION)
                server = serversPool.stream()
                        .filter(s -> (s.getFreeSize() >= memorySize && s.getState().equals("creating")))
                        .findAny()
                        .orElse(null);
            }

            //TODO maybe we need separate method if we need comment the block?

            // if another request come while creating a new server and there is no enough space in servers pool it will wait to make sure that the new server may have space and then allocate memory
            if(server!=null)
            {
                try {
                    Thread.sleep(20000);
                }
                catch (InterruptedException exception) {
                    exception.printStackTrace();
                }
                server= get(server.getServerId());
                //TODO you may pass null here
                server= updateServerFreeMemory(server,memorySize);
            }

            //TODO please explain this part

            //if there is no server in creating state then create new server
            else {
                server = create(memorySize);
                //initially the server is in creating state so wait 20 seconds and then update server to active state
                try {
                    Thread.sleep(20_000);
                } catch (InterruptedException exception) {
                    exception.printStackTrace();
                }
                server = updateState(server);
            }
        }
        return server;
    }

    //TODO access modifier should be first
    static synchronized public Server create(int size) //TODO why not add it as comment?
    // method is synchronized to prevent multi thread to access it concurrently

    {
        int serverId=0;
        Server server;
        if (!serversPool.isEmpty()) {
            serverId = Collections.max(serversPool, Comparator.comparing(s -> s.getServerId())).getServerId() + 1;
        }
        server = new Server(100, "creating", serverId);
        //TODO see line 107 are there any similarities?
        WritePolicy wPolicy = new WritePolicy();
        Key key = new Key("test", "servers", server.getServerId());
        Bin bin = new Bin("server", server);
        client.put(wPolicy, key, bin);
        server = updateServerFreeMemory(server, size);
        return server;
    }

    //TODO what is purpose of this method?
    public static Server updateServerFreeMemory(Server server, int size)
    {
        //TODO what will happen here if server is null?
        server.setFreeSize(server.getFreeSize() - size);
        WritePolicy wPolicy = new WritePolicy();
        Key key = new Key("test", "servers" ,server.getServerId());
        Bin bin = new Bin("server", server);
        client.put(wPolicy, key, bin);
        return server;
    }

    public Server updateState(Server server)
    {
        server.setState("active");
        WritePolicy wPolicy = new WritePolicy();
        Key key = new Key("test", "servers" ,server.getServerId());
        Bin bin = new Bin("server", server);
        client.put(wPolicy, key, bin);
        return server;
    }

    public List<Server> scanServersPool()
    {
        List<Server> serversList= new ArrayList<>();
        try {
            ScanPolicy policy = new ScanPolicy();
            //TODO the default is true
            policy.concurrentNodes = true;
            policy.priority = Priority.LOW;
            policy.includeBinData = true;
            client.scanAll(policy, "test", "servers", (key, record) -> serversList.add((Server)record.getValue("server")));

        } catch (AerospikeException e) {
            System.out.println("EXCEPTION - Message: " + e.getMessage());
        }

        return serversList;
    }

    public Server get(int serverId) {
        Key serverKey = new Key("test", "servers", serverId);
        Record serverRecord = client.get(null, serverKey);
        if(serverRecord != null) {
            return (Server) serverRecord.getValue("server");
        }
        else
            //TODO bad idea, btw where is the braces :) ?
            return null;
    }

}