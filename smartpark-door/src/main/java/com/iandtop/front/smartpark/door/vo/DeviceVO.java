package com.iandtop.front.smartpark.door.vo;

import com.iandtop.common.driver.vo.AuthCardVO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeviceVO {

    public static int BLength = 18;//byte数组长度
    private String pk_device;
    private String url;
    private int port;
    private String sn;
    //private Map<String,List<QPAVO>> card_doorMap = new HashMap<>();//key :card_code, 每个卡四个门
    private List<AuthCardVO> card_doorBytesList = new ArrayList<>();// 每个卡一条AuthCardVO

    public String getPk_device() {
        return pk_device;
    }

    public void setPk_device(String pk_device) {
        this.pk_device = pk_device;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

   /* public Map<String, List<QPAVO>> getCard_doorMap() {
        return card_doorMap;
    }

    public void setCard_doorMap(Map<String, List<QPAVO>> card_doorMap) {
        this.card_doorMap = card_doorMap;
    }
*/
    public List<AuthCardVO> getCard_doorBytesList() {
        return card_doorBytesList;
    }

    public void setCard_doorBytesList(List<AuthCardVO> card_doorBytesList) {
        this.card_doorBytesList = card_doorBytesList;
    }
}
