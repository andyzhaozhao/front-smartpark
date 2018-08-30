package com.iandtop.front.smartpark.pos.dao;

import com.iandtop.front.smartpark.pos.util.PosConstants;
import com.iandtop.front.smartpark.pos.util.PosUtil;
import com.iandtop.front.smartpark.pos.vo.HandleMealMoneyVO;
import com.iandtop.front.smartpark.pub.utils.JDBCOracleClientUtil;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLConnection;
import io.vertx.ext.sql.UpdateResult;

import java.util.Calendar;

public class PosDao {
    public void findCard(Vertx vertx, String cardCode, Handler<ResultSet> resultSetHandler) {
        String sql = "select bd_psnbasdoc.pk_psnbasdoc, bd_psnbasdoc.psnname, bd_psnbasdoc.sex, bd_psnbasdoc.id, bd_psndoc.psncode, " +
                " card_card.* ,substr(card_card.card_ineffectived_ts,0,10) card_ineffectived_date"
                + " from bd_psnbasdoc inner join bd_psndoc on bd_psnbasdoc.pk_psnbasdoc=bd_psndoc.pk_psnbasdoc" +
                " left join card_card on bd_psnbasdoc.pk_psnbasdoc=card_card.pk_psnbasdoc"
                + " where card_card.card_code=? and card_card.card_state in('10','20')";
        JsonArray params = new JsonArray().add(cardCode);
        JDBCOracleClientUtil.executeCommon(vertx, PosConstants.vo, sql, params, resultSetHandler);
    }

    public void mealDeviceRule(Vertx vertx, String deviceCode, Handler<ResultSet> resultSetHandler) {
        String sql = "select pk_device,device_meal_type from device_device where device_code=? ";
        JsonArray params = new JsonArray().add(deviceCode);
        JDBCOracleClientUtil.executeCommon(vertx, PosConstants.vo, sql, params, resultSetHandler);
    }

    //日消费规则
    public void mealDayRule(Vertx vertx, String pk_meal_rule, Handler<ResultSet> resultSetHandler) {
        String sql = "select b.frequency_day frequency_day ,b.money_day money_day from meal_rule b " +
                " where b.PK_MEAL_RULE= ? ";
        JsonArray params = new JsonArray().add(pk_meal_rule);
        JDBCOracleClientUtil.executeCommon(vertx, PosConstants.vo, sql, params, resultSetHandler);
    }

    //日实际消费次数和金额
    public void mealDayFreqAndMoney(Vertx vertx, String pk_card, Handler<ResultSet> resultSetHandler) {
        String mealRecordTableName = getMealRecordTableName();
        String sql = "select sum(meal_money) money,count(*) frequency from " + mealRecordTableName + " a " +
                "where meal_kind = '0' and substr(a.ts, 0, 10)=? and pk_card = ?";
        JsonArray params = new JsonArray().add(PosUtil.getCurrentDate()).add(pk_card);
        JDBCOracleClientUtil.executeCommon(vertx, PosConstants.vo, sql, params, resultSetHandler);
    }

    //时间段消费规则
    public void mealTimeRule(Vertx vertx, String pk_meal_rule, Handler<ResultSet> resultSetHandler) {
        String sql = "select * from (" +
                "    select a.BEGIN_TIME,a.end_time,a.frequency_time,a.money_time from meal_rule_time a " +
                "    where a.PK_MEAL_RULE=? " +
                ") a where to_date('2000-01-01 " + PosUtil.getCurrentTimeWithoutDate() + "','yyyy-mm-dd hh24:mi:ss') " +
                "           >= to_date('2000-01-01 '||a.begin_time,'yyyy-mm-dd hh24:mi:ss') " +
                "and to_date('2000-01-01 " + PosUtil.getCurrentTimeWithoutDate() + "','yyyy-mm-dd hh24:mi:ss') " +
                "           <= to_date('2000-01-01 '||a.end_time,'yyyy-mm-dd hh24:mi:ss') ";

        JsonArray params = new JsonArray().add(pk_meal_rule);
        JDBCOracleClientUtil.executeCommon(vertx, PosConstants.vo, sql, params, resultSetHandler);
    }

    //时间段实际消费次数和金额
    public void mealTimeFreqAndMoney(Vertx vertx, String pk_card, String begin_time, String end_time, Handler<ResultSet> resultSetHandler) {
        String mealRecordTableName = getMealRecordTableName();
        String beginTime = PosUtil.getCurrentDate() + " " + begin_time;
        String endTime = PosUtil.getCurrentDate() + " " + end_time;

        String sql = "select sum(meal_money) money,count(*) frequency from " + mealRecordTableName + " a " +
                " where meal_kind = '0' and pk_card = ? and " +
                " to_date(ts,'yyyy-mm-dd hh24:mi:ss') >= to_date('" + beginTime + "','yyyy-mm-dd hh24:mi:ss') and" +
                " to_date(ts,'yyyy-mm-dd hh24:mi:ss') <= to_date('" + endTime + "','yyyy-mm-dd hh24:mi:ss')";

        JsonArray params = new JsonArray().add(pk_card);
        JDBCOracleClientUtil.executeCommon(vertx, PosConstants.vo, sql, params, resultSetHandler);
    }

    public String getMealSql(HandleMealMoneyVO handleMealMoneyVO){
        String sql = "update card_card set MONEY_CASH=" + (double) handleMealMoneyVO.getMoney_cash() / 100 +
                ",MONEY_CORP_GRANT=" + (double) handleMealMoneyVO.getMoney_corp_grant() / 100 +
                " where PK_CARD='" + handleMealMoneyVO.getPk_card() + "' " +
                " and MONEY_CASH=" + (double) handleMealMoneyVO.getLast_money_cash() / 100 +
                " and MONEY_CORP_GRANT=" + (double) handleMealMoneyVO.getLast_money_corp_grant() / 100;
        return sql;
    }

    public void meal(Vertx vertx, HandleMealMoneyVO handleMealMoneyVO, Handler<UpdateResult> resultSetHandler) {
        String sql = getMealSql(handleMealMoneyVO);
        JDBCOracleClientUtil.executeUpdateCommon(vertx, PosConstants.vo, sql, resultSetHandler);
    }

    public void mealTx(SQLConnection connection, HandleMealMoneyVO handleMealMoneyVO, Handler<UpdateResult> resultSetHandler) {
        String sql = getMealSql(handleMealMoneyVO);
        JDBCOracleClientUtil.execute(connection, sql, resultSetHandler);
    }

    public String getRecordSql(HandleMealMoneyVO handleMealMoneyVO){
        String mealRecordTableName = getMealRecordTableName();
        String pk_meal_record = "83HH000" + System.currentTimeMillis();
        int retain = handleMealMoneyVO.getMoney_corp_grant() + handleMealMoneyVO.getMoney_cash();
        int real_meal_cash_money = handleMealMoneyVO.getLast_money_cash() - handleMealMoneyVO.getMoney_cash();
        int real_meal_grant_Money = handleMealMoneyVO.getLast_money_corp_grant() - handleMealMoneyVO.getMoney_corp_grant();

        String sql = "insert into " + mealRecordTableName + "(pk_meal_record,pk_psnbasdoc,pk_card" +
                ",pk_device,meal_money, REAL_MEAL_MONEY," +
                "MONEY_CASH,MONEY_CORP_GRANT,MONEY_RETAIN," +
                "MEAL_TYPE,meal_batchnum,pk_corp,device_meal_type," +
                "real_meal_cash_money,real_meal_grant_Money,PWD_FOR_BEYOND_QUOTA,MEAL_KIND,meal_way)" +
                " values ('" + pk_meal_record + "','" + handleMealMoneyVO.getPk_psnbasdoc() + "','" + handleMealMoneyVO.getPk_card() +
                "','" + handleMealMoneyVO.getPk_device() + "'," + (double) handleMealMoneyVO.getMealMoney() / 100 + "," + (double) handleMealMoneyVO.getReal_mealMoney() / 100 +
                "," + (double) handleMealMoneyVO.getMoney_cash() / 100 + "," + (double) handleMealMoneyVO.getMoney_corp_grant() / 100 + "," + (double) retain / 100 +
                ",'" + handleMealMoneyVO.getMeal_type() + "',(select NVL(max(meal_batchnum),0) from " + mealRecordTableName
                + ")+1,'" + handleMealMoneyVO.getPk_corp() + "','" + handleMealMoneyVO.getDevice_meal_type() +
                "'," + (double) real_meal_cash_money / 100 + "," + (double) real_meal_grant_Money / 100 + ",'asdfasdf','0','0')";//MEAL_KIND 0正常消费 1冲正

        return sql;
    }

    public void mealRecord(Vertx vertx, HandleMealMoneyVO handleMealMoneyVO, Handler<UpdateResult> resultSetHandler) {
        String sql = getRecordSql(handleMealMoneyVO);
        JDBCOracleClientUtil.executeUpdateCommon(vertx, PosConstants.vo, sql, resultSetHandler);
    }

    public void mealRecordTx(SQLConnection connection, HandleMealMoneyVO handleMealMoneyVO, Handler<UpdateResult> resultSetHandler) {
        String sql = getRecordSql(handleMealMoneyVO);
        JDBCOracleClientUtil.execute(connection, sql, resultSetHandler);
    }

    private String getMealRecordTableName() {
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH) + 1;
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        String mealRecordTableName = "meal_record_" + currentYear + "_" + ((currentMonth < 10) ? ("0" + currentMonth) : currentMonth) + "_" + ((currentDay < 15) ? "01" : "15");
        return mealRecordTableName;
    }

}
