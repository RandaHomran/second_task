package com.example.sampleproject.model;
import java.io.Serializable;

/**
 * this is model class to represent the server.
 */
public class ServerModel implements Serializable,Comparable<ServerModel> {
    private int serverId;
    private int freeSize;
    private int ram;
    private String state;

    public ServerModel(int freeSize, String state, int serverId) {
        this.ram=100;
        this.freeSize = freeSize;
        this.state = state;
        this.serverId = serverId;
    }

    /**
     *  get server id
     * @return Unique integer number to represent the server id
     */
    public int getServerId() {
        return serverId;
    }

    /**
     * set new server id for this server
     * @param serverId server id to set
     */
    public void setServerId(int serverId) {
        this.serverId = serverId;
    }

    /**
     * get the server free size
     * @return free size of this server
     */
    public int getFreeSize() {
        return freeSize;
    }

    /**
     * Changes the free size for this server
     * @param freeSize Integer value for the new free size
     */
    public void setFreeSize(int freeSize) {
        this.freeSize = freeSize;
    }

    /**
     * get ram size
     * @return ram size for this server
     */
    public int getRam() {
        return ram;
    }

    /**
     * Set the Ram size for this server
     * @param ram Integer value to represent ram size
     */
    public void setRam(int ram) {
        this.ram = ram;
    }

    /**
     * get current state for the server
     * @return The current state of this server (creating or active).
     */
    public String getState() {
        return state;
    }

    /**
     * Changes the state of this server
     * @param state String value to represent the server new state (creating or active).
     */
    public void setState(String state) {
        this.state = state;
    }

    @Override
    public int compareTo(ServerModel o) {
        if (this.getServerId() > o.getServerId()) {
            return 1;
        } else if (this.getServerId() < o.getServerId()) {
            return -1;
        }
        return 0;
    }

}
