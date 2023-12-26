package com.cloud;

import com.profile.Constants;
import com.server.BChannel;
import com.server.BServer;

import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CenterServer extends BServer implements Runnable{
    public static void main(String[] args) {
        new CenterServer(Constants.CLOUD_SERVER_PORT).startServer();
    }
    public static double APPLY = 0.2, ONLINE = 0.1, OVER = 0.25;
    public CenterServer(int edgePort) {
        super(edgePort);
    }

    private static int ownID = 1000;
    public static Map<String, CenterChannel> channalMap = new HashMap<>();
    public static Map<String, Double> rate_map = new HashMap<>();
    @Override
    protected void start() {
        System.out.println("strat!");
        new Thread(this).start();
        try{
            String s = String.valueOf(ownID++);
            CenterChannel channel = new CenterChannel(serverSocket.accept(), s);
//            channalMap.put(channel.toString(), channel);
            channalMap.put(s, channel);
            Double d = 0.0;
            rate_map.put(s, d);
            System.out.println("new channel:"+channel);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        //todo: 可视化
        while(true) {
            ArrayList<Double> doubleList = new ArrayList<>();
            ArrayList<String> idList = new ArrayList<String>();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            for (String s : CenterServer.rate_map.keySet()) {
                doubleList.add(CenterServer.rate_map.get(s));
                idList.add(s);
//                System.out.println("id:" + s + " rate:" + CenterServer.rate_map.get(s));
            }
            sortList(doubleList, idList);
            for (int i = 0; i < idList.size();++i) {
                System.out.printf("id:%s rate:%.3f\n", idList.get(i), doubleList.get(i));
            }

        }
    }

    void sortList(ArrayList<Double> doubleList, ArrayList<String> stringList){
        for(int i = 0; i < doubleList.size(); ++i){
            for(int j = i+1; j < doubleList.size(); ++j){
                if(doubleList.get(i) < doubleList.get(j)){
                    Collections.swap(doubleList, i, j);
                    Collections.swap(stringList, i, j);
                }
            }
        }
    }

    int compareMyString(String s1, String s2){
        int len1 = s1.length(), len2 = s2.length();
        int min = Math.min(len1, len2);
        for(int i = 0; i < min; ++i){
            if(s1.charAt(i) > s2.charAt(i)){
                return 1;
            }else if(s1.charAt(i) < s2.charAt(i)){
                return -1;
            }
        }
        return 0;
    }
}

class CenterChannel extends BChannel<CloudData>{

    private String ownID;
    public CenterChannel(Socket socket, String ownID) {
        super(socket);
        this.ownID = ownID;
    }

    @Override
    public void onTransmissionStart() {
        System.out.println("start send from center channel, whose id is:"+this);
    }

    @Override
    public void onTransmissionEnd() {

    }

    @Override
    public void onTransmissionError(String message, ErrorType errorType) {

    }

    @Override
    public void onTransmissionProgress(CloudData messages) {
//        synchronized (CenterServer.class) {
            Double d = CenterServer.rate_map.get(this.ownID);
//            if(messages.shouldUpdate()){
            d += messages.getScore();
//            }
            System.out.println("d: "+d);
            // update score
            CenterServer.rate_map.put(this.ownID, d);
//        }
    }

    @Override
    public void alertError(String error) {
        System.out.println("error from center channel, whose id is:"+error);
    }

    @Override
    public void run(){

    }
}
