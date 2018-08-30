package com.iandtop.front.smartpark.pos.action;

import com.iandtop.common.utils.BinaryUtil;
import com.iandtop.front.smartpark.pos.dao.PosDao;
import com.iandtop.front.smartpark.pos.vo.PosMessage;
import com.iandtop.front.smartpark.pos.vo.ServerMessage;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

import java.util.List;

/**
 * 查找用户处理
 *
 * @author andyzhao
 */
public class FindCardInfoAction extends BaseAction implements IPosAction {

    @Override
    public void handle(PosMessage msg, Vertx vertx, Handler<ServerMessage> posServerMessageHandler) {
        final long cardCode = getCardCode(msg);
        new PosDao().findCard(vertx, cardCode + "", resultSet -> {
            List<JsonObject> cards = resultSet.getRows();
            ServerMessage posServerMessage = getPOSServerMessage(msg, new byte[]{0x30, 0x00});
            if (cards != null && cards.size() > 0) {
                int bash = (int) (cards.get(0).getDouble("MONEY_CASH") * 100);//将单位变为分,现金钱包
                int MONEY_CORP_GRANT = (int) (cards.get(0).getDouble("MONEY_CORP_GRANT") * 100);//将单位变为分,现金钱包
                String name = cards.get(0).getString("PSNNAME");

                byte[] nameBytes = setNameBytes(name);
                byte[] bashBytes = BinaryUtil.intToByteLowInF(bash + MONEY_CORP_GRANT);


                posServerMessage.setCardState(new byte[]{0x00});
                posServerMessage.setName(nameBytes);
                posServerMessage.setRemain(bashBytes);
            } else {
                posServerMessage.setCardState(new byte[]{0x01});
                posServerMessage.setName(setNameBytes("未找到"));
                posServerMessage.setRemain(BinaryUtil.intToByteLowInF(0));
            }

            setCRC16(posServerMessage); //设置crc16
            posServerMessageHandler.handle(posServerMessage);
        });
    }

}
