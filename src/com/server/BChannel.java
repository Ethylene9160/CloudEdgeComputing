package com.server;

import web_tools.TransmissionController;
import web_tools.TransmissionListener;

import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;

public abstract class BChannel<T extends Serializable> implements TransmissionListener<T>, Runnable{
    private Socket socket;
    protected TransmissionController controller;
    public BChannel(Socket socket) {
        this.socket = socket;
        System.out.println("init begin");
        try {
            this.controller = new TransmissionController(socket, this);
            System.out.println("controller init finished.");
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("init end");
    }

    @Override
    public String toString() {
        return socket.toString();
    }
}
