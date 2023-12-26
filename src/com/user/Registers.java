package com.user;


import java.util.Random;

public class Registers {
    private int ID, allTime, winTime;
    private String name;
    private String password;
    private String question;
    private String head = "default.png";
    private String answare;
    private String signature;
    private int VIP, money;

    @Deprecated
    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getQuestion() {
        return question;
    }

    //设置找回密码的方式
    public void setQuestion(String question) {
        this.question = question;
    }

    public Registers(int ID, int allTime, int winTime, String name, String password, String question) {
        this(ID, allTime, winTime, name, password, question, "default", "default.png");
    }
    public Registers(int ID, int allTime,  int winTime, String name, String password) {
        this(ID,allTime,winTime,name,password, "default", "default", "default.png");
    }

    public String getAnsware() {
        return answare;
    }

    public void setAnsware(String answare) {
        this.answare = answare;
    }

    public Registers(int ID, int allTime, int winTime, String name, String password, String question, String answare, String head) {
        this(ID,allTime,winTime,name,password,question,answare,head,"人懒，没写.");
    }

    public Registers(int ID, int allTime, int winTime, String name, String password, String question, String answare, String head, String signature) {
        this.ID = ID;
        this.allTime = allTime;
        this.winTime = winTime;
        this.name = name;
        this.password = password;
        this.question = question;
        this.head = head;
        this.answare = answare;
        this.signature = signature;
    }

    public Registers(int ID, int allTime, int winTime, String name, String password, String question, String answare, String head, String signature, int VIP, int money) {
        this.ID = ID;
        this.allTime = allTime;
        this.winTime = winTime;
        this.name = name;
        this.password = password;
        this.question = question;
        this.head = head;
        this.answare = answare;
        this.signature = signature;
        this.VIP = VIP;
        this.money = money;
    }



    public Registers(int ID) {
        this(ID, 0, 0, "default", "123456");
    }

    public Registers(int ID, String question, String answare){
        this(ID, 0,0,"default","123456",question,answare,"default.png");
    }

    @Override
    public String toString() {
        return String.format("%d&%d&%d&%s&%s&%s&%s&%s&%s&%d&%d#\n", ID, allTime, winTime, name, password, question, answare, head, signature, VIP, money);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAllTime(int allTime) {
        this.allTime = allTime;
    }

    public void setWinTime(int winTime) {
        this.winTime = winTime;
    }

    public int getID() {
        return ID;
    }

    public int getAllTime() {
        return allTime;
    }

    public int getWinTime() {
        return winTime;
    }

    public String getName() {
        return name;
    }

    private String getWinRate(){
        return String.format("%d%c", 100*winTime/allTime,'%');
    }

    public void winner(){
        this.money += (30+new Random().nextInt(20));
        this.winTime++;
        this.allTime++;
    }

    public void loser(){
        this.money += 5;
        this.allTime++;
    }

    public String getPlayerInfo(){
        if(this.allTime == 0)
            return String.format("%d&%s&进击的新兴大佬&个性签名：%s&【VIP】等级: %d", this.ID, this.name, this.signature, VIP);
        else
            return String.format("%d&%s&胜率: %s&声望: %d&金币：%d&个性签名：%s&【VIP】等级: %d", this.ID, this.name, getWinRate(), allTime, money, signature, VIP);
    }

    public String getHead(){
        return this.head;
    }

    public void setHead(String head){
        this.head = head;
    }

    public String getSecurityQuestions(){
        return this.question + '&' + this.answare;
    }

    public String getShengwang(){
        return String.format("%d", this.allTime);
    }

    public String getMoney(){
        return String.format("%d", money);
    }

    public void decreaseMoney(int var){
        this.money -= var;
    }

    public int getVIP() {
        return VIP;
    }

    public void setVIP(int VIP) {
        this.VIP = VIP;
    }

    public void uVIP(){
        this.money -= 198;
        VIP++;
    }
}