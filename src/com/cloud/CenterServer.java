package com.cloud;

import com.server.BServer;

import java.io.Serializable;

public class CenterServer extends BServer {
    public CenterServer(int edgePort) {
        super(edgePort);
    }

    @Override
    public void onTransmissionStart() {

    }

    @Override
    public void onTransmissionEnd() {

    }

    @Override
    public void onTransmissionError(String message, ErrorType errorType) {

    }

    @Override
    public void onTransmissionProgress(Serializable messages) {

    }

    @Override
    public void alertError(String error) {

    }
}
