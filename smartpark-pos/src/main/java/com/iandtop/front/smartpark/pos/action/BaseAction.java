package com.iandtop.front.smartpark.pos.action;

import com.iandtop.common.utils.BinaryUtil;
import com.iandtop.front.smartpark.pos.dao.PosDao;
import com.iandtop.front.smartpark.pos.util.PosConstants;
import com.iandtop.front.smartpark.pos.util.PosUtil;
import com.iandtop.front.smartpark.pos.vo.HandleMealMoneyVO;
import com.iandtop.front.smartpark.pos.vo.PosMessage;
import com.iandtop.front.smartpark.pos.vo.ServerMessage;
import com.iandtop.front.smartpark.pub.utils.JDBCOracleClientUtil;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

/**
 * action公共类
 *
 * @author andyzhao
 */
public class BaseAction {

    //POSServerMessage拼接，公共部分
    protected ServerMessage getPOSServerMessage(PosMessage msg, byte[] messageHead) {
        ServerMessage posServerMessage = new ServerMessage();
        posServerMessage.setMessageHead(messageHead);
        posServerMessage.setMessageType(msg.getMessageType());
        posServerMessage.setDeviceCode(msg.getDeviceCode());
        posServerMessage.setOperator(msg.getOperator());
        posServerMessage.setCardCode(msg.getCardCode());
        posServerMessage.setMealMark(msg.getMealMark());
        posServerMessage.setLimitMoneyDay(new byte[]{0x00, 0x00, 0x00, 0x00});
        posServerMessage.setLimitMoneyOne(new byte[]{0x00, 0x00, 0x00, 0x00});
        posServerMessage.setCardLogicCode(new byte[]{0x00, 0x00, 0x00, 0x00});
        posServerMessage.setCorpCode(msg.getCorpCode());
        return posServerMessage;
    }

    /**
     * 设置CRC16的值，检测报文的CRC16位前面的所有位数,生成CRC16值
     *
     * @param posServerMessage
     */
    protected void setCRC16(ServerMessage posServerMessage) {
        byte[] mbytes = posServerMessage.getBytes();
        byte[] needToCrc16Check = Arrays.copyOfRange(mbytes, 0, mbytes.length - 2);
        byte[] crc16byte = BinaryUtil.getCRC16(needToCrc16Check);
        posServerMessage.getCRC16()[0] = crc16byte[0];
        posServerMessage.getCRC16()[1] = crc16byte[1];
    }

    protected long getCardCode(PosMessage msg) {
        byte[] cardCodes = msg.getCardCode();
        //目前只用四个字节
        byte[] tcardCodes = new byte[]{cardCodes[4], cardCodes[5], cardCodes[6], cardCodes[7]};
        long cardCode = BinaryUtil.byteToIntLowInF(tcardCodes);
        cardCode = BinaryUtil.longIntToLong(cardCode);
        return cardCode;
    }

    protected long getDeviceCode(PosMessage msg) {
        byte[] deviceCodes = msg.getDeviceCode();
        long cardCode = BinaryUtil.byteToIntLowInF(deviceCodes);
        return cardCode;
    }

    /**
     * 左对齐
     */
    protected byte[] setNameBytes(String name) {
        byte[] nameBytes = new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
        try {
            byte[] realNameBytes = name.getBytes("GBK");
            for (int i = 0; i < realNameBytes.length; i++) {
                nameBytes[i] = realNameBytes[i];
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return nameBytes;
    }


    /**右对齐
     */
//    protected byte[] setNameBytes(String name) {
//        byte[] nameBytes = new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
//        try {
//            byte[] realNameBytes = name.getBytes("GBK");
//            int startPos = nameBytes.length - realNameBytes.length;//
//            int tmppos = 0;
//            for (int i = startPos; i < nameBytes.length; i++) {
//                nameBytes[i] = realNameBytes[tmppos];
//                tmppos++;
//            }
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        return nameBytes;
//    }


    protected void mealMoney(Vertx vertx, HandleMealMoneyVO handleMealMoneyVO, Handler<ServerMessage> posServerMessageHandler, PosDao posDao) {
        int retain = -1;//剩余金额
        switch (handleMealMoneyVO.getMeal_type()) {
            case 1://计次
                break;
            case 0://扣款
                //计算扣款金额
                switch (handleMealMoneyVO.getDevice_meal_type()) {//判断设备规则
                    case 0://仅现金
                        retain = handleMealMoneyVO.getLast_money_cash() - handleMealMoneyVO.getMealMoney();
                        handleMealMoneyVO.setMoney_cash(retain);
                        handleMealMoneyVO.setMoney_corp_grant(handleMealMoneyVO.getLast_money_corp_grant());
                        break;
                    case 10://仅补贴
                        retain = handleMealMoneyVO.getLast_money_corp_grant() - handleMealMoneyVO.getMealMoney();
                        handleMealMoneyVO.setMoney_cash(handleMealMoneyVO.getLast_money_cash());
                        handleMealMoneyVO.setMoney_corp_grant(retain);
                        break;
                    case 20://先现金后补贴
                        retain = handleMealMoneyVO.getLast_money_cash() - handleMealMoneyVO.getMealMoney();
                        if (retain < 0) {//如果现金不够，继续扣补贴
                            retain = handleMealMoneyVO.getLast_money_corp_grant() + retain;
                            handleMealMoneyVO.setMoney_cash(0);
                            handleMealMoneyVO.setMoney_corp_grant(retain);
                        } else {
                            handleMealMoneyVO.setMoney_cash(retain);
                            handleMealMoneyVO.setMoney_corp_grant(handleMealMoneyVO.getLast_money_corp_grant());
                        }
                        break;
                    case 40://先补贴后现金
                        retain = handleMealMoneyVO.getLast_money_corp_grant() - handleMealMoneyVO.getMealMoney();
                        if (retain < 0) {//如果补贴不够，继续扣款
                            retain = handleMealMoneyVO.getLast_money_cash() + retain;
                            handleMealMoneyVO.setMoney_cash(retain);
                            handleMealMoneyVO.setMoney_corp_grant(0);
                        } else {
                            handleMealMoneyVO.setMoney_corp_grant(retain);
                            handleMealMoneyVO.setMoney_cash(handleMealMoneyVO.getLast_money_cash());
                        }
                        break;
                }
                if (retain < 0) {//如果仍然不够，余额不足
                    handleMealMoneyVO.setState(new byte[]{0x04});
                    handleMealMoney(vertx, handleMealMoneyVO, posServerMessageHandler, posDao, "如果仍然不够，余额不足");
                } else {
                    handleMealMoneyVO.setState(new byte[]{0x00});
                    handleMealMoney(vertx, handleMealMoneyVO, posServerMessageHandler, posDao, "消费成功");
                }
                break;
        }
    }

    protected void handleMealMoney(Vertx vertx, HandleMealMoneyVO handleMealMoneyVO, Handler<ServerMessage> posServerMessageHandler,
                                 PosDao posDao, String stateMessage) {
        handleMealMoneyVO.getPosServerMessage().setMealState(handleMealMoneyVO.getState());
        //设置crc16
        setCRC16(handleMealMoneyVO.getPosServerMessage());
        if (handleMealMoneyVO.getState()[0] == 0x00) {//可以成功扣款
            int retain = handleMealMoneyVO.getMoney_cash() + handleMealMoneyVO.getMoney_corp_grant();
            handleMealMoneyVO.getPosServerMessage().setRemain(BinaryUtil.intToByteLowInF(retain));//实际卡余额
            handleMealMoneyVO.getPosServerMessage().setMealMoney(BinaryUtil.intToByteLowInF(handleMealMoneyVO.getMealMoney()));//实际消费金额
            //更改card_card信息
          /*  posDao.meal(vertx, handleMealMoneyVO, meal -> {
                //插入消费记录表
                posDao.mealRecord(vertx, handleMealMoneyVO, mealRecord -> {
                    posServerMessageHandler.handle(handleMealMoneyVO.getPosServerMessage());
                });
            });*/
            JDBCOracleClientUtil.executeUpdateTX(vertx, PosConstants.vo, taskHandler->{
                posDao.mealTx(taskHandler.getConnection(), handleMealMoneyVO, meal -> {
                    //插入消费记录表
                    posDao.mealRecordTx(taskHandler.getConnection(), handleMealMoneyVO, mealRecord -> {
                        taskHandler.getResultHandler().handle(true);
                    });
                });
            },result->{
                if(result){
                    posServerMessageHandler.handle(handleMealMoneyVO.getPosServerMessage());
                }else{
                    System.out.println("插入数据库失败");
                }
            });

        } else {
            handleMealMoneyVO.getPosServerMessage().setRemain(BinaryUtil.intToByteLowInF(0));//实际卡余额
            handleMealMoneyVO.getPosServerMessage().setMealMoney(BinaryUtil.intToByteLowInF(0));//实际消费金额
            posServerMessageHandler.handle(handleMealMoneyVO.getPosServerMessage());
        }
        System.out.println(stateMessage);
    }
}


