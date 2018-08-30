package com.iandtop.front.smartpark.pos.filter;

import com.iandtop.front.smartpark.pos.action.FindCardInfoAction;
import com.iandtop.front.smartpark.pos.action.HeartBeatAction;
import com.iandtop.front.smartpark.pos.action.MealNoRuleAction;
import com.iandtop.front.smartpark.pos.action.MealNormalAction;
import com.iandtop.front.smartpark.pos.util.PosConstants;
import com.iandtop.front.smartpark.pos.vo.PosMessage;
import com.iandtop.front.smartpark.pos.vo.ServerMessage;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;

import java.util.Arrays;

public class PosFilter {

    private static PosFilter instance = null;

    private PosFilter() {

    }

    public static PosFilter getInstance() {
        if (instance == null) {
            instance = new PosFilter();
        }
        return instance;
    }

    public PosMessage buildPOSMessage(PosMessage posMessage) {
        byte[] msgContent = posMessage.getMessageContentStr();
        byte[] msgType = Arrays.copyOfRange(msgContent,2,3);//根据规范，第三个字符代表命令字
        posMessage.setMessageType(msgType);

        switch (posMessage.getMessageType()[0]) {
            case PosMessage.MESSAGE_TYPE_CARD_INFO:
                posMessage.setDeviceCode(Arrays.copyOfRange(msgContent,3, 7));
                posMessage.setOperator(Arrays.copyOfRange(msgContent,7, 11));
                posMessage.setCardCode(Arrays.copyOfRange(msgContent,11, 19));
                posMessage.setRemain(Arrays.copyOfRange(msgContent,19, 23));
                posMessage.setBatchNum(Arrays.copyOfRange(msgContent,23, 27));
                posMessage.setCorpCode(Arrays.copyOfRange(msgContent,27, 31));
                posMessage.setCRC16(Arrays.copyOfRange(msgContent,31, 33));
                break;
            case PosMessage.MESSAGE_TYPE_MEAL:
                posMessage.setDeviceCode(Arrays.copyOfRange(msgContent,3, 7));
                posMessage.setOperator(Arrays.copyOfRange(msgContent,7, 11));
                posMessage.setCardCode(Arrays.copyOfRange(msgContent,11, 19));
                posMessage.setMealMark(Arrays.copyOfRange(msgContent,19, 27));
                posMessage.setMealType(Arrays.copyOfRange(msgContent,27, 28));
                posMessage.setMealMoney(Arrays.copyOfRange(msgContent,28, 32));
                posMessage.setRemain(Arrays.copyOfRange(msgContent,32, 36));
                posMessage.setMealDayMoney(Arrays.copyOfRange(msgContent,36, 40));
                posMessage.setBatchNum(Arrays.copyOfRange(msgContent,40, 44));
                posMessage.setCorpCode(Arrays.copyOfRange(msgContent,44, 48));
                posMessage.setCRC16(Arrays.copyOfRange(msgContent,48, 50));
                break;
            case PosMessage.MESSAGE_TYPE_MEAL_PASSWORD:
                break;
            case PosMessage.MESSAGE_TYPE_STATISTIC_INFO:
                break;
            case PosMessage.MESSAGE_TYPE_CANCEL_MEAL:
                break;
            case PosMessage.MESSAGE_TYPE_UPLOAD_PRE_MONEY:
                break;
            case PosMessage.MESSAGE_TYPE_HEARTBEET:
                posMessage.setDeviceCode(Arrays.copyOfRange(msgContent,3, 7));
                posMessage.setOperator(Arrays.copyOfRange(msgContent,7, 11));
                posMessage.setBlackListVersion(Arrays.copyOfRange(msgContent,11, 15));
                posMessage.setUnUploadDataSize(Arrays.copyOfRange(msgContent,15, 19));
                posMessage.setCorpCode(Arrays.copyOfRange(msgContent,19, 23));
                posMessage.setCRC16(Arrays.copyOfRange(msgContent,23, 25));
                break;
            case PosMessage.MESSAGE_TYPE_CHARGE:
                break;
            case PosMessage.MESSAGE_TYPE_LOGIN:
                break;
        }

        return posMessage;
    }

    public void buildPOSServerMessage(PosMessage message, Vertx vertx, Handler<ServerMessage> posServerMessageHandler) {
        switch (message.getMessageType()[0]) {
            case PosMessage.MESSAGE_TYPE_CARD_INFO:
                new FindCardInfoAction().handle(message,vertx,posServerMessageHandler);
                break;
            case PosMessage.MESSAGE_TYPE_MEAL:
                switch (PosConstants.rule_type){
                    case PosConstants.RULE_TYPE_NULL:
                        new MealNoRuleAction().handle(message,vertx,posServerMessageHandler);
                        break;
                    case PosConstants.RULE_TYPE_NORMAL:
                        new MealNormalAction().handle(message,vertx,posServerMessageHandler);
                        break;
                    default:
                        break;
                }
                break;
            case PosMessage.MESSAGE_TYPE_MEAL_PASSWORD:
                break;
            case PosMessage.MESSAGE_TYPE_STATISTIC_INFO:
                break;
            case PosMessage.MESSAGE_TYPE_CANCEL_MEAL:
                break;
            case PosMessage.MESSAGE_TYPE_UPLOAD_PRE_MONEY:
                break;
            case PosMessage.MESSAGE_TYPE_HEARTBEET:
                new HeartBeatAction().handle(message,vertx,posServerMessageHandler);
                break;
            case PosMessage.MESSAGE_TYPE_CHARGE:
                break;
            case PosMessage.MESSAGE_TYPE_LOGIN:
                break;
        }
    }

}

