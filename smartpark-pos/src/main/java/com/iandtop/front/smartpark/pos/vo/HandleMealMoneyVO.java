package com.iandtop.front.smartpark.pos.vo;


public class HandleMealMoneyVO {
    private String card_ineffectived_ts;
    private String pk_card;
    private String pk_corp;//刷卡人所在的公司
    private String pk_psnbasdoc;
    private String pk_device;
    private int meal_type;
    private int device_meal_type;
    private String cardCode;
    private byte[] state;
    private Integer last_money_cash;
    private Integer last_money_corp_grant;
    private Integer money_cash;
    private Integer money_corp_grant;
    private int mealMoney;
    private int real_mealMoney;
    private int real_meal_cash_Money;//实际扣除现金
    private int real_meal_grant_Money;//实际扣除补贴
    private ServerMessage posServerMessage;


    public int getReal_meal_cash_Money() {
        return real_meal_cash_Money;
    }

    public void setReal_meal_cash_Money(int real_meal_cash_Money) {
        this.real_meal_cash_Money = real_meal_cash_Money;
    }

    public int getReal_meal_grant_Money() {
        return real_meal_grant_Money;
    }

    public void setReal_meal_grant_Money(int real_meal_grant_Money) {
        this.real_meal_grant_Money = real_meal_grant_Money;
    }

    public String getPk_corp() {
        return pk_corp;
    }

    public void setPk_corp(String pk_corp) {
        this.pk_corp = pk_corp;
    }

    public int getDevice_meal_type() {
        return device_meal_type;
    }

    public void setDevice_meal_type(int device_meal_type) {
        this.device_meal_type = device_meal_type;
    }

    public int getMeal_type() {
        return meal_type;
    }

    public void setMeal_type(int meal_type) {
        this.meal_type = meal_type;
    }

    public String getPk_card() {
        return pk_card;
    }

    public String getPk_device() {
        return pk_device;
    }

    public void setPk_device(String pk_device) {
        this.pk_device = pk_device;
    }

    public String getPk_psnbasdoc() {
        return pk_psnbasdoc;
    }

    public void setPk_psnbasdoc(String pk_psnbasdoc) {
        this.pk_psnbasdoc = pk_psnbasdoc;
    }

    public void setPk_card(String pk_card) {
        this.pk_card = pk_card;
    }

    public int getReal_mealMoney() {
        return real_mealMoney;
    }

    public void setReal_mealMoney(int real_mealMoney) {
        this.real_mealMoney = real_mealMoney;
    }

    public String getCard_ineffectived_ts() {
        return card_ineffectived_ts;
    }

    public void setCard_ineffectived_ts(String card_ineffectived_ts) {
        this.card_ineffectived_ts = card_ineffectived_ts;
    }

    public String getCardCode() {
        return cardCode;
    }

    public void setCardCode(String cardCode) {
        this.cardCode = cardCode;
    }

    public byte[] getState() {
        return state;
    }

    public void setState(byte[] state) {
        this.state = state;
    }

    public Integer getLast_money_cash() {
        return last_money_cash;
    }

    public void setLast_money_cash(Integer last_money_cash) {
        this.last_money_cash = last_money_cash;
    }

    public Integer getLast_money_corp_grant() {
        return last_money_corp_grant;
    }

    public void setLast_money_corp_grant(Integer last_money_corp_grant) {
        this.last_money_corp_grant = last_money_corp_grant;
    }

    public Integer getMoney_cash() {
        return money_cash;
    }

    public void setMoney_cash(Integer money_cash) {
        this.money_cash = money_cash;
    }

    public Integer getMoney_corp_grant() {
        return money_corp_grant;
    }

    public void setMoney_corp_grant(Integer money_corp_grant) {
        this.money_corp_grant = money_corp_grant;
    }

    public int getMealMoney() {
        return mealMoney;
    }

    public void setMealMoney(int mealMoney) {
        this.mealMoney = mealMoney;
    }

    public ServerMessage getPosServerMessage() {
        return posServerMessage;
    }

    public void setPosServerMessage(ServerMessage posServerMessage) {
        this.posServerMessage = posServerMessage;
    }
}
