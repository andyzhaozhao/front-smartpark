package com.iandtop.front.smartpark.door.vo;

import com.iandtop.common.driver.vo.AuthCardVO;
import com.iandtop.common.utils.BinaryUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author andyzhao
 */
public class QPAVO {

    private static final long serialVersionUID = 5606774762617700144L;

    private String card_code;
    private String card_ineffectived_ts;
    private String password;
    private String pk_device;
    private String device_ip;
    private String device_port;
    private String device_serialnum;
    private String door_code;
    private String time_grp_code;

    private String pk;//会议室pk或者访客pk，用来返回下发成功的结果的唯一标示

    public String getPk() {
        return pk;
    }

    public void setPk(String pk) {
        this.pk = pk;
    }

    public String getCard_code() {
        return card_code;
    }

    public void setCard_code(String card_code) {
        this.card_code = card_code;
    }

    public String getCard_ineffectived_ts() {
        return card_ineffectived_ts;
    }

    public void setCard_ineffectived_ts(String card_ineffectived_ts) {
        this.card_ineffectived_ts = card_ineffectived_ts;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPk_device() {
        return pk_device;
    }

    public void setPk_device(String pk_device) {
        this.pk_device = pk_device;
    }

    public String getDevice_ip() {
        return device_ip;
    }

    public void setDevice_ip(String device_ip) {
        this.device_ip = device_ip;
    }

    public String getDevice_port() {
        return device_port;
    }

    public void setDevice_port(String device_port) {
        this.device_port = device_port;
    }

    public String getDevice_serialnum() {
        return device_serialnum;
    }

    public void setDevice_serialnum(String device_serialnum) {
        this.device_serialnum = device_serialnum;
    }

    public String getDoor_code() {
        return door_code;
    }

    public void setDoor_code(String door_code) {
        this.door_code = door_code;
    }

    public String getTime_grp_code() {
        return time_grp_code;
    }

    public void setTime_grp_code(String time_grp_code) {
        this.time_grp_code = time_grp_code;
    }

    /**
     * 将业务数据转化为下发到硬件的数据格式
     * qpavo列表转AuthCardVO列表
     *
     * @param sameCodes
     * @return
     */
    public static AuthCardVO getAuthCardVO(List<QPAVO> sameCodes) {
        AuthCardVO rvo = new AuthCardVO();
        if (sameCodes.size() > 0) {
            long keylong = Long.parseLong(sameCodes.get(0).getCard_code());
            byte[] ccbs = BinaryUtil.longToByteHignInF(keylong);
            rvo.setCard_code(new byte[]{ccbs[3], ccbs[4], ccbs[5], ccbs[6], ccbs[7]});
            String passwordStr = sameCodes.get(0).getPassword();
            byte[] passwordBytes = new byte[]{(byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff};
            if (passwordStr != null) {
                byte[] passwords = BinaryUtil.str2Bcd(passwordStr);
                if (passwordStr.length() > 8) {
                    // TODO 报错
                } else {
                    for (int i = 0; i < passwords.length; i++) {
                        passwordBytes[i] = passwords[i];
                    }
                }
            }
            rvo.setPassword(passwordBytes);
            String cstr = sameCodes.get(0).getCard_ineffectived_ts();
            cstr = cstr.substring(2, 4) + cstr.substring(5, 7) + cstr.substring(8, 10) + cstr.substring(11, 13) + cstr.substring(14, 16);

            rvo.setCard_ineffectived_ts(BinaryUtil.str2Bcd(cstr));
            byte[] openTimeBytes = new byte[]{0x00, 0x00, 0x00, 0x00};
            byte authAndAuthstrs = 0x08;
            for (QPAVO qpavo : sameCodes) {
                int dc = Integer.parseInt(qpavo.getDoor_code());
                int timegroupcode = Integer.parseInt(qpavo.getTime_grp_code());
                openTimeBytes[dc - 1] = BinaryUtil.intToByteHignInF(timegroupcode)[3];
                switch (dc) {
                    case 1:
                        authAndAuthstrs = (byte) (authAndAuthstrs | 0x80);//10000000
                        break;
                    case 2:
                        authAndAuthstrs = (byte) (authAndAuthstrs | 0x40);//01000000
                        break;
                    case 3:
                        authAndAuthstrs = (byte) (authAndAuthstrs | 0x20);//00100000
                        break;
                    case 4:
                        authAndAuthstrs = (byte) (authAndAuthstrs | 0x10);//00010000
                        break;
                }
            }
            byte[] tmpec = BinaryUtil.intToByteHignInF(65535);
            rvo.setOpen_time(openTimeBytes);
            rvo.setEffective_count(new byte[]{tmpec[2], tmpec[3]});
            rvo.setAuthAndAuth(new byte[]{authAndAuthstrs});
            rvo.setCard_state(new byte[]{0x00});
            rvo.setHoliday(new byte[]{(byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff});
            rvo.setOutin_flag(new byte[]{(byte) 0xff});
            rvo.setReadCard_time(new byte[]{(byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff});
        }
        return rvo;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof QPAVO) {
            QPAVO vo = (QPAVO) obj;
            if (isEql(this.getCard_code(),vo.getCard_code())
                    && isEql(this.getCard_ineffectived_ts(),vo.getCard_ineffectived_ts())
                    && isEql(this.getPassword(),vo.getPassword())
                    && isEql(this.getPk_device(),vo.getPk_device())
                    && isEql(this.getDevice_ip(),vo.getDevice_ip())
                    && isEql(this.getDevice_port(),vo.getDevice_port())
                    && isEql(this.getDevice_serialnum(),vo.getDevice_serialnum())
                    && isEql(this.getDoor_code(),vo.getDoor_code())
                    && isEql(this.getTime_grp_code(),vo.getTime_grp_code())
                    && isEql(this.getPk(),vo.getPk())) {
                return true;
            }else{
                return false;
            }
        } else {
            return false;
        }
    }

    private boolean isEql(String t ,String o){
        if(t == null || o == null || t.equals("null") || o.equals("null")){
            return true;
        }else {
            if(t.equals(o)){
                return true;
            }
            return false;
        }
    }
}


