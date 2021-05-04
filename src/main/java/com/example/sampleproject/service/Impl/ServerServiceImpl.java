package com.example.sampleproject.service.Impl;
import com.aerospike.client.*;
import com.aerospike.client.policy.Priority;
import com.aerospike.client.policy.ScanPolicy;
import com.aerospike.client.policy.WritePolicy;
import com.example.sampleproject.config.ApplicationConfig;
import com.example.sampleproject.exception.AppConfigExceptions;
import com.example.sampleproject.exception.AppConfigRuntimeExceptions;
import com.example.sampleproject.model.ServerModel;
import com.example.sampleproject.service.ServerService;
import io.vavr.control.Try;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ServerServiceImpl implements ServerService {

    private final static AerospikeClient client = Try.of(() -> new AerospikeClient(ApplicationConfig.getDbHost(), ApplicationConfig.getDbPort()))
            .getOrElseThrow(e -> new AppConfigRuntimeExceptions("Please make sure that your DB is up & running." +
                    e.getLocalizedMessage()));

    static List<ServerModel> serversPool = Collections.synchronizedList(new ArrayList<>());

    @Override
    public ServerModel allocate(int memorySize) throws AppConfigExceptions {

        serversPool = scanServersPool();
        System.out.println(serversPool.size());
        ServerModel server;
        server = filterServersByStateAndFreeMemory("active",memorySize);

        // if there is enough space in servers pool then allocate memory
        if(server != null){
            server = updateServerFreeMemory(server, memorySize);
        }
        else
        {
            //to check if there is another server in the creating state
            server= filterServersByStateAndFreeMemory("creating",memorySize);

            if(server!=null)
            {
                try {
                    Thread.sleep(20000);
                }
                catch (InterruptedException exception) {
                    exception.printStackTrace();
                }
                server= get(server.getServerId());
                server= updateServerFreeMemory(server,memorySize);
            }

            //if there is no server in creating state then create new server
            else {
                server = create(memorySize);
                //wait until server state change from creating to active
                try {
                    Thread.sleep(20000);
                } catch (InterruptedException exception) {
                    exception.printStackTrace();
                }
                server = updateState(server);
            }
        }
        return server;
    }


    /**
     * to create a new server with initial free size 100GB and then allocate memory size
     * @param size memory size to allocate
     * @return ServerModel object which represent the new server after the memory allocation
     */
    public static synchronized ServerModel create(int size) throws AppConfigExceptions {
        int serverId=0;
        ServerModel server;
        if (!serversPool.isEmpty()) {
            serverId = Collections.max(serversPool, Comparator.comparing(s -> s.getServerId())).getServerId() + 1;
        }
        server = new ServerModel(100, "creating", serverId);
        WritePolicy wPolicy = new WritePolicy();
        Key key = null;
        key = new Key(ApplicationConfig.safeGetDbName(), ApplicationConfig.getUserSetName(), server.getServerId());
        Bin bin = new Bin("server", server);
        client.put(wPolicy, key, bin);
        server = updateServerFreeMemory(server, size);
        return server;
    }

    /**
     *
     * @param server Changes the server free memory size after memory allocation by decrement
     *               the desired size from server free memory.
     * @param size the size to allocate in the server.
     * @return ServerModel object after memory allocation.
     */
    public static ServerModel updateServerFreeMemory(ServerModel server, int size) throws AppConfigExceptions {
        server.setFreeSize(server.getFreeSize()-size);
        WritePolicy wPolicy = new WritePolicy();
        Key key = new Key(ApplicationConfig.safeGetDbName(), ApplicationConfig.getUserSetName(),server.getServerId());
        Bin bin = new Bin("server", server);
        client.put(wPolicy, key, bin);
        return server;
    }

    /**
     * Changes the state of a serverModel object from creating to active
     * @param server serverModel object to update
     * @return serverModel object after update the state.
     */
    public ServerModel updateState(ServerModel server) throws AppConfigExceptions {
        server.setState("active");
        WritePolicy wPolicy = new WritePolicy();
        Key key = new Key(ApplicationConfig.safeGetDbName(), ApplicationConfig.getUserSetName(),server.getServerId());
        Bin bin = new Bin("server", server);
        client.put(wPolicy, key, bin);
        return server;
    }

    /**
     * scan all ServerModel objects from database and add them to serversPool list
     * @return ArrayList contains all servers
     */
    public List<ServerModel> scanServersPool() throws AppConfigExceptions {
        List<ServerModel> serversList= new ArrayList<>();
        try {
            ScanPolicy policy = new ScanPolicy();
            policy.priority = Priority.LOW;
            policy.includeBinData = true;
            client.scanAll(policy, ApplicationConfig.safeGetDbName(), ApplicationConfig.getUserSetName(), (key, record) -> serversList.add((ServerModel)record.getValue("server")));

        } catch (AerospikeException e) {
            System.out.println("EXCEPTION - Message: " + e.getMessage());
        }
        return serversList;
    }

    /**
     * to get a specific server
     * @param serverId id for the server to get
     * @return serverModel object
     */
    public ServerModel get(int serverId) throws AppConfigExceptions {
        Key serverKey = new Key(ApplicationConfig.safeGetDbName(), ApplicationConfig.getUserSetName(), serverId);
        Record serverRecord = client.get(null, serverKey);
        return (ServerModel) serverRecord.getValue(ApplicationConfig.getUserSetName());
    }

    /**
     *
     * @param state server current state(active or creating)
     * @param memorySize server free memory size
     * @return serverModel object if the server is existing and null otherwise
     */
    public synchronized ServerModel filterServersByStateAndFreeMemory(String state, int memorySize){
        return serversPool.stream()
                .filter(s -> (s.getFreeSize() >= memorySize && s.getState().equals(state)))
                .findAny()
                .orElse(null);
    }

}


