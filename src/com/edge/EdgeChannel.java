package com.edge;


import com.cloud.CenterServer;
import com.cloud.CloudData;
import com.server.BChannel;
import com.user.Registers;
import web_tools.TransmissionController;
import web_tools.TransmissionListener;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Map;

public class EdgeChannel extends BChannel<String> {
    private CloudData data;
    public static final int APPLY = 13, REGISTER = 12, CHANGE_NAME = 10, CHANGE_PASSWARD = 16,
            FIND_YOU = 17, FIND_OPPONENT = 18, CHANGE_HEAD = 19, FIND_PASSWARD = 20, RESET_PASSWARD = 21,
            SET_SIGN = 22, SHENGWANG_SHOP=23, MONEY = 24,
            UPDATE_VIP = -1, DECREASE_MONEY = -2;

    public static final char WP_NO_PERSON_FOUND = 'b', WP_REGIST = 'c',  WP_APPLY_ACCOUNT = 'e',
            WP_SUCCESS_SET_PASSWARD = 'n', WP_FIND_ONLINE = 'q', WP_SURREND = 'r', WP_AGREE_PEACE = 'v', WP_PLAYER_INFO = 'y', WP_WRONG_PASSWARD = 'z',
            WP_SERVER_CLOSE = 'D', WP_RESET_PASSWARD = 'm',
             WP_TO_SHENGWANG ='J', WP_GET_MONEY = 'K';

    private EdgeServer server;
    private boolean flag = true, isRegistered = false;
//    private Socket clientSocekt;
    private String name = "default", password;
    private int allTimes, winTimes;
    private double winRate;
    public int ownID, targetID, findID;
    public int getOwnID(){
        return this.ownID;
    }
    public int getWinTime(){
        return this.winTimes;
    }

    public int getAllTimes(){
        return this.allTimes;
    }

    public String getName(){
        return this.name;
    }
    
    public EdgeChannel(Socket socket, int ownID, EdgeServer server) {
        super(socket);
//        this.clientSocekt = socket;
        data = new CloudData();
        this.server = server;
        controller.send("s"+ownID);
        System.out.println("init end");
        this.ownID = ownID;
        System.out.println("finish init.");

        updateScore(CenterServer.ONLINE);
    }

    private void updateScore(double d){
        data.setScore(d);
        data.setShouldUpdata(true);
        server.sendData(data);
        data.setShouldUpdata(false);
    }

    private void send(String s){
        controller.send(s);
    }

    //todo: fix this.
    public String getAllInfoOfAll(){
        StringBuilder str = new StringBuilder();
        for(Integer key : EdgeServer.listMap.keySet()){
            if(key != ownID) str.append(EdgeServer.listMap.get(key).ownID).append("#").append(EdgeServer.listMap.get(key).getName()).append("&");
        }
        return str.toString();
//        return null;
    }

    @Override
    public void onTransmissionStart() {
        System.out.println("start!");
    }

    @Override
    public void onTransmissionEnd() {
        System.out.println("end!");
    }

    @Override
    public void onTransmissionError(String message, ErrorType errorType) {
        System.out.println("error!" + message + "type is:"+errorType);

    }

    @Override
    public void onTransmissionProgress(String messages) {
        transmit(messages);
    }

    @Override
    public void alertError(String error) {
        System.out.println(error);
    }
    
    void transmit(String str){
        //updateScore(0.001);
        int p1 = str.indexOf("#");
        if(str.length() < 3){
            if(str.equals("-1")){
                System.out.println(ownID + "输了");
                setWinCondition(targetID,ownID);
            }
            else if(str.equals("98")) EdgeServer.listMap.get(ownID).controller.send(WP_FIND_ONLINE + getAllInfoOfAll());

        }else {
            targetID = Integer.parseInt(str.substring(0, p1));
            System.out.println(targetID);
            String position = str.substring(p1 + 1);
            //遍历集合
            if (targetID < 0) {
                switch (targetID) {
                    case -1:
                        EdgeServer.registers.get(ownID).uVIP();
                        break;
                    case -2:
                        EdgeServer.registers.get(ownID).decreaseMoney(Integer.parseInt(position));
                        break;
                }
            } else if (targetID == 11) {
                for (Integer key : EdgeServer.listMap.keySet()) EdgeServer.listMap.get(key).controller.send(WP_SERVER_CLOSE + "");
            } else if (targetID == CHANGE_NAME) {
                //改名
                this.name = position;
                if (isRegistered) {
                    EdgeServer.registers.get(ownID).setName(this.name);
                }
            } else if (targetID == REGISTER) {
                //登录    private void setWinCondition(int winner, int loser){
                //        if(winner > 10000)
                //            ChessServer.registers.get(winner).winner();
                //        if(loser > 10000)
                //            ChessServer.registers.get(loser).loser();
                //
                //    }
                isRegistered = true;
                String[] strs = position.split("&");
                int newID = Integer.parseInt(strs[0]);
                if (EdgeServer.isContainsRegisterID(newID) && !EdgeServer.listMap.containsKey(newID)) {
                    Registers register = EdgeServer.registers.get(newID);
                    if (!strs[1].equals(register.getPassword())) {
                        System.out.println("密码错误");
                        EdgeServer.listMap.get(ownID).controller.send(Character.toString(WP_WRONG_PASSWARD));//密码错误
                    } else {
                        this.name = register.getName();
                        this.winTimes = register.getWinTime();
                        this.allTimes = register.getAllTime();
                        //需要把新信息发给旧的地方
                        EdgeServer.listMap.get(ownID).controller.send(Character.toString(WP_REGIST) + newID + "#" + name + '#' + register.getHead());
                        EdgeServer.listMap.put(newID, this);
                        EdgeServer.listMap.remove(ownID);
                        ownID = newID;
                        System.out.println("登陆成功！");
                        System.out.println(EdgeServer.listMap);

                        updateScore(CenterServer.ONLINE+0.001);
                    }
                } else {
                    EdgeServer.listMap.get(ownID).controller.send(Character.toString(WP_NO_PERSON_FOUND));//查无此人
                }
            } else if (targetID == APPLY) {
                //注册
                System.out.println("apply-1");
                System.out.println(EdgeServer.registers);
                int newID = EdgeServer.registers.size() + 10001;
                System.out.println("apply-2");
                int p = position.indexOf("&");
                EdgeServer.registers.put(newID, new Registers(newID, position.substring(0, p++), position.substring(p)));
                EdgeServer.listMap.get(ownID).controller.send(WP_APPLY_ACCOUNT + "" + newID);
                System.out.println("apply-3");

                updateScore(CenterServer.APPLY+0.001);
            } else if (targetID == 14) {//退出登录
                EdgeServer.listMap.remove(ownID);
                System.out.println("remove");
            } else if (targetID == 15) {//写入档案
                System.out.println("准备写入");
                System.out.println(EdgeServer.registers);
                //RegisterUtil.writeRegisters(Constants.CHESS_REGIST, EdgeServer.registers);
                System.out.println("写入档案");
            } else if (targetID == CHANGE_PASSWARD) {
                //改密码
                System.out.println(name + "【改密码】");
                String strs[] = position.split("&");
                Registers r = EdgeServer.registers.get(ownID);
                if (strs[0].equals(r.getPassword())) {
                    r.setPassword(strs[1]);
                    EdgeServer.listMap.get(ownID).controller.send(Character.toString(WP_SUCCESS_SET_PASSWARD));
                } else EdgeServer.listMap.get(ownID).controller.send(Character.toString(WP_WRONG_PASSWARD));
            } else if (targetID == FIND_YOU) {//查询自己
                EdgeServer.listMap.get(ownID).controller.send(WP_PLAYER_INFO + EdgeServer.registers.get(ownID).getPlayerInfo());
            } else if (targetID == FIND_OPPONENT) {//查询对手信息
                EdgeServer.listMap.get(ownID).controller.send(WP_PLAYER_INFO + EdgeServer.registers.get(Integer.parseInt(position)).getPlayerInfo());
            } else if (targetID == CHANGE_HEAD) {//改头像
                if (isRegistered) EdgeServer.registers.get(ownID).setHead(position);
                System.out.println("改头像");
            } else if (targetID == -1) {//群发消息
                for (Integer key : EdgeServer.listMap.keySet()) EdgeServer.listMap.get(key).controller.send(position);
            } else if (targetID == FIND_PASSWARD) {
                findID = Integer.parseInt(position);
                if (EdgeServer.isContainsRegisterID(findID)) {
                    EdgeServer.listMap.get(ownID).controller.send(WP_RESET_PASSWARD + EdgeServer.registers.get(findID).getSecurityQuestions());
                }
            } else if (targetID == EdgeChannel.RESET_PASSWARD) {
                EdgeServer.registers.get(findID).setPassword(position);
                controller.send(Character.toString(WP_SUCCESS_SET_PASSWARD));
            } else if (targetID == SET_SIGN) {
                EdgeServer.registers.get(ownID).setSignature(position);
            } else if (targetID == SHENGWANG_SHOP) {
                send(WP_TO_SHENGWANG + "" + EdgeServer.registers.get(ownID).getAllTime());
            } else if (targetID == MONEY) {
                send(WP_GET_MONEY + "" + EdgeServer.registers.get(ownID).getMoney());
            } else {
                if (position.equals(Character.toString(WP_SURREND))) {
                    System.out.println(ownID + "认输");
                    setWinCondition(targetID, ownID);
                } else if (position.equals(Character.toString(WP_AGREE_PEACE))) {
                    if (targetID > 10000) EdgeServer.registers.get(targetID).loser();
                    if (ownID > 10000) EdgeServer.registers.get(ownID).loser();
                }
                System.out.println("坐标：" + position);
                Map<Integer, EdgeChannel> listMap = EdgeServer.listMap;
                listMap.get(targetID).send(position);

                updateScore(CenterServer.OVER+0.001);
            }
        }
    }

    private void setWinCondition(int winner, int loser){
        if(winner > 10000)
            EdgeServer.registers.get(winner).winner();
        if(loser > 10000)
            EdgeServer.registers.get(loser).loser();
    }

    @Override
    public void run() {

    }
}
