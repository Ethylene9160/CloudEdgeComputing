package com.edge;

import com.cloud.CloudData;
import com.profile.Constants;
import com.server.BChannel;
import com.server.BServer;
import com.user.Registers;
import web_tools.TransmissionController;
import web_tools.TransmissionListener;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class EdgeServer extends BServer implements TransmissionListener<CloudData> {


    public static Map<Integer, EdgeChannel> listMap = new HashMap<>();
    public static int webID;
    public static Map<Integer, Registers> registers;
    public static CloudData data = new CloudData();


    private static double score = 0.0;

    private TransmissionController controller;
    public EdgeServer(int edgePort) {
        super(edgePort);
        try {
            controller = new TransmissionController(new Socket(Constants.CLOUD_SERVER_IP, Constants.CLOUD_SERVER_PORT), this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    

    @Override
    protected void start() {
        webID  = 100;//从100开始，避免和错误信息代码重合
        System.out.println("start!");
        registers = new HashMap<>();

        while(serverSocket != null){
            webID ++;
            try {
                Socket socket = this.serverSocket.accept();
                System.out.println("someone comes!!!");
                //new ObjectOutputStream(socket.getOutputStream()).writeObject(String.format('s' + Integer.toString(webID)));
                //创建线程对象，此时说明有客户端进来了
                EdgeChannel chessChannel = new EdgeChannel(socket, webID, this);
//                DataInputStream ois = new DataInputStream(socket.getInputStream());
                System.out.println("a newcomer! online: " + (listMap.size() + 1));
                //添加到集合
                listMap.put(webID, chessChannel);
                //启动线程
                //new Thread(chessChannel).start();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    public void sendData(CloudData data){
        synchronized (controller){
            controller.send(data);
        }
    }

    public void updateScore(double d){
        synchronized (this){
            score += d;
            data.setScore(score);
            data.setShouldUpdata(true);
            sendData(data);
            data.setShouldUpdata(false);
        }
    }

    public static boolean isContainsRegisterID(int ID){
        return registers.containsKey(ID);
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
    public void onTransmissionProgress(CloudData messages) {

    }

    @Override
    public void alertError(String error) {

    }


}
