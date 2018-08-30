package com.iandtop.front.smartpark.door.util;

import com.iandtop.common.driver.vo.AuthCardVO;
import com.iandtop.front.smartpark.door.vo.DeviceVO;
import com.iandtop.front.smartpark.door.vo.QPAVO;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * vo转换工具
 *
 * @author andyzhaozhao
 */
public class VOUtils {
    public static List<DeviceVO> qpavoToDeviceVO(List<QPAVO> qpavos) {
        List<DeviceVO> deviceVOs = new ArrayList<DeviceVO>();
        if(qpavos==null || qpavos.size()==0){
            return null;
        }

        try {
            Map<String, List<QPAVO>> deviceMap = new LinkedHashMap<>();//按照设备分组,key:pk_device
            for (QPAVO qpavo : qpavos) {
                String key = qpavo.getPk_device();
                List<QPAVO> tmp = deviceMap.get(key);
                if (tmp == null) {
                    deviceMap.put(key, new ArrayList<QPAVO>());
                }
                deviceMap.get(key).add(qpavo);
            }
           // System.out.println("解析服务器获取的数据，按照设备分组成功");
            Map<String, Map> deviceAllMap = new LinkedHashMap<>();
            for (String key : deviceMap.keySet()) {
                Map<String, List<QPAVO>> groupedDatas = new LinkedHashMap<>();//按照卡号分组,key:card_code
                for (QPAVO qpavo : qpavos) {
                    String cardKey = qpavo.getCard_code();
                    List<QPAVO> tmp = groupedDatas.get(cardKey);
                    if (tmp == null) {
                        groupedDatas.put(cardKey, new ArrayList<QPAVO>());
                    }
                    groupedDatas.get(cardKey).add(qpavo);
                }
                deviceAllMap.put(key, groupedDatas);
            }
           // System.out.println("解析服务器获取的数据，按照卡号分组成功");

            //将卡号一样的合并为一个AuthCardVO
            List<AuthCardVO> authCardVOs = new ArrayList<AuthCardVO>();
            //分设备

            for (String key : deviceAllMap.keySet()) {//遍历设备
                DeviceVO deviceVO = new DeviceVO();
                deviceVO.setPk_device(key);//获得设备的sn

                Map<String, List<QPAVO>> groupedDatas = deviceAllMap.get(key);//获得card
                int i = 0;
                for (String cardKey : groupedDatas.keySet()) {//遍历card
                    List<QPAVO> carddoors = groupedDatas.get(cardKey);//每个list最多有四条数据
                    i++;
                    if (i == 1) {
                        deviceVO.setUrl(carddoors.get(0).getDevice_ip());//获得设备
                        deviceVO.setPort(Integer.parseInt(carddoors.get(0).getDevice_port()));//获得设备的
                        deviceVO.setSn(carddoors.get(0).getDevice_serialnum());//获得设备的
                    }
                    AuthCardVO authCardVO = QPAVO.getAuthCardVO(carddoors);
                    authCardVOs.add(authCardVO);
                }

                deviceVO.setCard_doorBytesList(authCardVOs);
                deviceVOs.add(deviceVO);
            }
           // System.out.println("获得的设备个数:" + deviceVOs.size() + "解析服务器获取的数据，生成AuthCardVOs成功");
        } catch (Exception e) {
            System.out.println("获取设备报错:");
            e.printStackTrace();
        }

        return deviceVOs;
    }

    /**
     * 获得所有成功的pk
     * @param sameCodes
     * @return
     */
    public static List<String> getPKList(List<QPAVO> sameCodes) {
        List<String> pks = new ArrayList<>();
        for(QPAVO vo : sameCodes){
            String pk = vo.getPk();
            if(!pks.contains(pk)){
                pks.add(pk);
            }
        }

        return pks;
    }
}
