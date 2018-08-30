package com.iandtop.front.smartpark.pos.action;

import com.iandtop.common.utils.BinaryUtil;
import com.iandtop.front.smartpark.pos.dao.PosDao;
import com.iandtop.front.smartpark.pos.util.PosUtil;
import com.iandtop.front.smartpark.pos.vo.HandleMealMoneyVO;
import com.iandtop.front.smartpark.pos.vo.PosMessage;
import com.iandtop.front.smartpark.pos.vo.ServerMessage;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

import java.util.List;

public class MealNormalAction extends BaseAction implements IPosAction {

    public void handle(PosMessage msg, Vertx vertx, Handler<ServerMessage> posServerMessageHandler) {
        ServerMessage posServerMessage = getPOSServerMessage(msg, new byte[]{0x38, 0x00});
        HandleMealMoneyVO handleMealMoneyVO = new HandleMealMoneyVO();
        handleMealMoneyVO.setCardCode(getCardCode(msg) + "");
        handleMealMoneyVO.setMealMoney(BinaryUtil.byteToIntLowInF(msg.getMealMoney()));//消费金额
        handleMealMoneyVO.setPosServerMessage(posServerMessage);
        handleMealMoneyVO.setMeal_type(0);// 1是计次消费，0扣款消费
        String deviceCode = getDeviceCode(msg) + "";

        PosDao posDao = new PosDao();
        posDao.findCard(vertx, handleMealMoneyVO.getCardCode(), resultSet -> {
            List<JsonObject> cards = resultSet.getRows();
            if (cards != null && cards.size() > 0) {
                String name = cards.get(0).getString("PSNNAME");
                posServerMessage.setName(setNameBytes(name));

                handleMealMoneyVO.setPk_card(cards.get(0).getString("PK_CARD"));
                handleMealMoneyVO.setPk_corp(cards.get(0).getString("PK_CORP"));
                handleMealMoneyVO.setPk_psnbasdoc(cards.get(0).getString("PK_PSNBASDOC"));
                handleMealMoneyVO.setCard_ineffectived_ts(cards.get(0).getString("CARD_INEFFECTIVED_TS"));//失效时间
                handleMealMoneyVO.setLast_money_cash((int) (cards.get(0).getDouble("MONEY_CASH") * 100));//将单位变为分,现金钱包
                handleMealMoneyVO.setLast_money_corp_grant((int) (cards.get(0).getDouble("MONEY_CORP_GRANT") * 100));//将单位变为分,补贴钱包
                handleMealMoneyVO.setMoney_cash(-1);
                handleMealMoneyVO.setMoney_corp_grant(-1);

                String pk_meal_rule = cards.get(0).getString("PK_MEAL_RULE");//此卡的消费规则
                if (PosUtil.sourceBiggerThanCurrent(handleMealMoneyVO.getCard_ineffectived_ts())) {
                    posDao.mealDeviceRule(vertx, deviceCode, deviceRule -> {
                        List<JsonObject> devices = deviceRule.getRows();
                        if (devices != null && devices.size() > 0) {
                            handleMealMoneyVO.setPk_device(devices.get(0).getString("PK_DEVICE"));
                            handleMealMoneyVO.setDevice_meal_type(Integer.parseInt(devices.get(0).getString("DEVICE_MEAL_TYPE")));//设备消费类型
                            //获取日消费规则
                            posDao.mealDayRule(vertx, pk_meal_rule, mealDayRule -> {
                                List<JsonObject> mealDayRules = mealDayRule.getRows();
                                if (mealDayRules != null && mealDayRules.size() > 0) {
                                    Integer max_frequency_day = mealDayRules.get(0).getInteger("FREQUENCY_DAY");//日限次
                                    double max_money_day = mealDayRules.get(0).getDouble("MONEY_DAY");//日限额 单位元
                                    posDao.mealDayFreqAndMoney(vertx, handleMealMoneyVO.getPk_card(), mealDayFreqAndMoney -> {
                                        List<JsonObject> mealDayFreqAndMoneys = mealDayFreqAndMoney.getRows();
                                        Integer frequency = 0;//日已经刷次数
                                        double money = 0;///日已经刷的额度
                                        if (mealDayFreqAndMoneys != null && mealDayFreqAndMoneys.size() > 0
                                                && mealDayFreqAndMoneys.get(0).getDouble("FREQUENCY") != 0) {
                                            frequency = mealDayFreqAndMoneys.get(0).getInteger("FREQUENCY");//日已经刷次数
                                            money = mealDayFreqAndMoneys.get(0).getDouble("MONEY");//日已经刷的额度
                                        }
                                        if (max_frequency_day > frequency) {
                                            handleMealMoneyVO.setReal_mealMoney(handleMealMoneyVO.getMealMoney());
                                            if (max_money_day >= money + (double) handleMealMoneyVO.getReal_mealMoney() / 100) {//如果消费金额小于日最大消费金额
                                                //获取时间段消费规则
                                                posDao.mealTimeRule(vertx, pk_meal_rule, mealTimeRule -> {
                                                    List<JsonObject> mealTimeRules = mealTimeRule.getRows();
                                                    if (mealTimeRules != null && mealTimeRules.size() > 0) {
                                                        int max_frequency_time = mealTimeRules.get(0).getInteger("FREQUENCY_TIME");//时间段限制次数
                                                        double max_money_time = mealTimeRules.get(0).getDouble("MONEY_TIME");//时间段限额
                                                        String begin_time = mealTimeRules.get(0).getString("BEGIN_TIME");//时间段开始时间
                                                        String end_time = mealTimeRules.get(0).getString("END_TIME");//时间段结束
                                                        posDao.mealTimeFreqAndMoney(vertx, handleMealMoneyVO.getPk_card(), begin_time, end_time, mealTimeFreqAndMoney -> {
                                                            List<JsonObject> mealTimeFreqAndMoneys = mealTimeFreqAndMoney.getRows();
                                                            Integer frequencyTime = 0;//段已经刷次数
                                                            double moneyTime = 0;///段已经刷的额度
                                                            if (mealTimeFreqAndMoneys != null && mealTimeFreqAndMoneys.size() > 0
                                                                    && mealTimeFreqAndMoneys.get(0).getDouble("FREQUENCY") != 0) {
                                                                frequencyTime = mealTimeFreqAndMoneys.get(0).getInteger("FREQUENCY");//日已经刷次数
                                                                moneyTime = mealTimeFreqAndMoneys.get(0).getDouble("MONEY");//日已经刷的额度
                                                            }
                                                            if (max_frequency_time > frequencyTime) {
                                                                handleMealMoneyVO.setReal_mealMoney(handleMealMoneyVO.getMealMoney());
                                                                if (max_money_time >= moneyTime + (double) handleMealMoneyVO.getReal_mealMoney() / 100) {//如果消费金额小于段最大消费金额
                                                                    mealMoney(vertx, handleMealMoneyVO, posServerMessageHandler, posDao);
                                                                } else {
                                                                    //超过此时间段消费最大金额
                                                                    handleMealMoneyVO.setState(new byte[]{0x05});
                                                                    handleMealMoney(vertx, handleMealMoneyVO, posServerMessageHandler, posDao, "超过此时间段消费最大金额");
                                                                }
                                                            } else {
                                                                //超过时间段消费最大次数
                                                                handleMealMoneyVO.setState(new byte[]{0x05});
                                                                handleMealMoney(vertx, handleMealMoneyVO, posServerMessageHandler, posDao, "超过时间段消费最大次数");
                                                            }
                                                        });
                                                    } else {//不在时间段内不可消费
                                                        handleMealMoneyVO.setState(new byte[]{0x01});
                                                        handleMealMoney(vertx, handleMealMoneyVO, posServerMessageHandler, posDao, "不在时间段内不可消费");
                                                    }
                                                });
                                            } else {
                                                //超过日消费最大金额
                                                handleMealMoneyVO.setState(new byte[]{0x05});
                                                handleMealMoney(vertx, handleMealMoneyVO, posServerMessageHandler, posDao, "超过日消费最大金额");
                                            }
                                        } else {
                                            //超过日消费最大次数
                                            handleMealMoneyVO.setState(new byte[]{0x05});
                                            handleMealMoney(vertx, handleMealMoneyVO, posServerMessageHandler, posDao, "超过日消费最大次数");
                                        }

                                    });
                                } else {//没有配置日消费规则
                                    //直接消费
                                    handleMealMoneyVO.setReal_mealMoney(handleMealMoneyVO.getMealMoney());
                                    mealMoney(vertx, handleMealMoneyVO, posServerMessageHandler, posDao);
                                }
                            });
                        } else {//设备没有查找到，设备没有注册登记
                            handleMealMoneyVO.setState(new byte[]{0x05});
                            handleMealMoney(vertx, handleMealMoneyVO, posServerMessageHandler, posDao, "设备没有登记");
                        }
                    });
                } else {//如果卡已经过期
                    handleMealMoneyVO.setState(new byte[]{0x01});
                    handleMealMoney(vertx, handleMealMoneyVO, posServerMessageHandler, posDao, "卡片已经过期");
                }
            } else {//系统中查不到有效卡
                posServerMessage.setName(setNameBytes("未找到"));
                handleMealMoneyVO.setState(new byte[]{0x01});
                handleMealMoney(vertx, handleMealMoneyVO, posServerMessageHandler, posDao, "未找到卡");
            }
        });
    }

}
