package com.server;
import web_tools.*;
public abstract class BServer implements TransmissionListener{
    private int edgePort;
    public BServer(int edgePort) {
        this.edgePort = edgePort;
        System.out.println("BServer constructor");
    }


}
