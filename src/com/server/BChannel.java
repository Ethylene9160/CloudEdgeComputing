package com.server;

import web_tools.TransmissionListener;

import java.net.Socket;

public abstract class BChannel implements TransmissionListener {
    private Socket socket;
    public BChannel(Socket socket) {
        this.socket = socket;
    }
}
