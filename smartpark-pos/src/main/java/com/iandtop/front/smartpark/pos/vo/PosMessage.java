package com.iandtop.front.smartpark.pos.vo;

public class PosMessage {

    //消息命令字（消息类型)
    public static final byte MESSAGE_TYPE_CARD_INFO = (byte) (0xa1 & 0xff);//卡信息查询
    public static final byte MESSAGE_TYPE_MEAL = (byte) (0xa2 & 0xff);//消费请求
    public static final byte MESSAGE_TYPE_MEAL_PASSWORD = (byte) (0xa3 & 0xff);//带密码的消费请求
    public static final byte MESSAGE_TYPE_STATISTIC_INFO = (byte) (0xa4 & 0xff);//统计查询
    public static final byte MESSAGE_TYPE_CANCEL_MEAL = (byte) (0xa5 & 0xff);//消费撤销
    public static final byte MESSAGE_TYPE_UPLOAD_PRE_MONEY = (byte) (0xa6 & 0xff);//预扣费数据上传
    public static final byte MESSAGE_TYPE_HEARTBEET = (byte) (0xa7 & 0xff);//心跳接口
    public static final byte MESSAGE_TYPE_CHARGE = (byte) (0xa9 & 0xff);//联机充值接口
    public static final byte MESSAGE_TYPE_LOGIN = (byte) (0xa0 & 0xff);//操作员联机登录接口

    //消息接收状态
    public static final int MESSAGE_RECEIVE_STATE_PRE = 0;//未接收
    public static final int MESSAGE_RECEIVE_STATE_LENGTH = 4;//已经获取到消息长度
    public static final int MESSAGE_RECEIVE_STATE_RECEIVING = 8;//正在读取后序信息
    public static final int MESSAGE_RECEIVE_STATE_RECEIVED = 16;//读取完成
    public static final int MESSAGE_RECEIVE_STATE_CRC16 = 32;//进行crc16校验成功

    public static final int MSG_HEAD_LENGTH = 2; //消息头长度

    private int messageReceiveState;//接收消息当前状态

    private byte[] messageContentStr ;//消息字符串，包括head和body

    private byte[] messageHead = new byte[2];//消息头
    private int messgeBodyLength;//消息体长度

    private byte[] messageType;//消息命令字

    private byte[] deviceCode = new byte[4];//机号

    private byte[] operator = new byte[4];//POS机操作员

    private byte[] cardCode = new byte[8];//物理卡号

    private byte[] mealMark = new byte[8];//交易标识

    private byte[] mealType = new byte[4];//交易类型 0 : 消费 1 : 计次交易

    private byte[] mealMoney = new byte[4];//消费金额（左低右高）

    private byte[] remain = new byte[4];//当前卡余额（左低右高）

    private byte[] mealDayMoney = new byte[4];//当前卡日累计额（左低右高）

    private byte[] batchNum = new byte[4];//当前卡流水

    private byte[] corpCode = new byte[4];//企业号（数字，左低右高）

    private byte[] CRC16 = new byte[2];//CRC16

    private byte[] blackListVersion = new byte[4];//黑名单版本号

    private byte[] unUploadDataSize = new byte[4];//未上传数据数量（左低右高）

    public int getMessageReceiveState() {
        return messageReceiveState;
    }

    public void setMessageReceiveState(int messageReceiveState) {
        this.messageReceiveState = messageReceiveState;
    }

    public byte[] getMessageHead() {
        return messageHead;
    }

    public void setMessageHead(byte[] messageHead) {
        this.messageHead = messageHead;
    }

    public byte[] getDeviceCode() {
        return deviceCode;
    }

    public void setDeviceCode(byte[] deviceCode) {
        this.deviceCode = deviceCode;
    }

    public int getMessgeBodyLength() {
        return messgeBodyLength;
    }

    public void setMessgeBodyLength(int messgeBodyLength) {
        this.messgeBodyLength = messgeBodyLength;
    }

    public byte[] getMessageContentStr() {
        return messageContentStr;
    }

    public void setMessageContentStr(byte[] messageContentStr) {
        this.messageContentStr = messageContentStr;
    }

    public byte[] getMessageType() {
        return messageType;
    }

    public void setMessageType(byte[] messageType) {
        this.messageType = messageType;
    }

    public byte[] getOperator() {
        return operator;
    }

    public void setOperator(byte[] operator) {
        this.operator = operator;
    }

    public byte[] getCardCode() {
        return cardCode;
    }

    public void setCardCode(byte[] cardCode) {
        this.cardCode = cardCode;
    }

    public byte[] getRemain() {
        return remain;
    }

    public void setRemain(byte[] remain) {
        this.remain = remain;
    }

    public byte[] getBatchNum() {
        return batchNum;
    }

    public void setBatchNum(byte[] batchNum) {
        this.batchNum = batchNum;
    }

    public byte[] getCorpCode() {
        return corpCode;
    }

    public void setCorpCode(byte[] corpCode) {
        this.corpCode = corpCode;
    }

    public byte[] getCRC16() {
        return CRC16;
    }

    public void setCRC16(byte[] CRC16) {
        this.CRC16 = CRC16;
    }

    public byte[] getUnUploadDataSize() {
        return unUploadDataSize;
    }

    public void setUnUploadDataSize(byte[] unUploadDataSize) {
        this.unUploadDataSize = unUploadDataSize;
    }

    public byte[] getBlackListVersion() {
        return blackListVersion;
    }

    public void setBlackListVersion(byte[] blackListVersion) {
        this.blackListVersion = blackListVersion;
    }

    public byte[] getMealDayMoney() {
        return mealDayMoney;
    }

    public byte[] getMealType() {
        return mealType;
    }

    public void setMealType(byte[] mealType) {
        this.mealType = mealType;
    }

    public void setMealDayMoney(byte[] mealDayMoney) {
        this.mealDayMoney = mealDayMoney;
    }

    public byte[] getMealMoney() {
        return mealMoney;
    }

    public void setMealMoney(byte[] mealMoney) {
        this.mealMoney = mealMoney;
    }

    public byte[] getMealMark() {
        return mealMark;
    }

    public void setMealMark(byte[] mealMark) {
        this.mealMark = mealMark;
    }
}
