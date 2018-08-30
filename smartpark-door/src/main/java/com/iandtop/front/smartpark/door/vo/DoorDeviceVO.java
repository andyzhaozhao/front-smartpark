package com.iandtop.front.smartpark.door.vo;

import com.iandtop.common.driver.vo.DeviceToServerMessageVO;
import com.iandtop.common.driver.vo.TCPParaDataVO;
import com.iandtop.common.utils.BinaryUtil;
import com.iandtop.front.smartpark.door.util.DoorUtils;

public class DoorDeviceVO {

    private static final long serialVersionUID = 1L;

    private String pk_device;
    private String pk_area;
    private String pk_corp;
    private String device_code;
    private String device_name;
    private String device_type;
    private String device_ip;
    private String device_port;
    private String device_serialnum;
    private byte[] device_pass;

    public DoorDeviceVO() {
    }

    public DoorDeviceVO(DeviceToServerMessageVO dtsMsgVO) {
        byte[] buffer = dtsMsgVO.getMessageDataVO().getData();
        TCPParaDataVO ipDataVO = new TCPParaDataVO(buffer);

        int ip1 = DoorUtils.oneByteToInt2(ipDataVO.getIp()[0]);
        int ip2 = DoorUtils.oneByteToInt2(ipDataVO.getIp()[1]);
        int ip3 = DoorUtils.oneByteToInt2(ipDataVO.getIp()[2]);
        int ip4 = DoorUtils.oneByteToInt2(ipDataVO.getIp()[3]);

        int port_tcp = DoorUtils.twoByteToInt2(ipDataVO.getLocal_tcp_port());
        int port_upp = DoorUtils.twoByteToInt2(ipDataVO.getLocal_udp_port());
        String devicepass = BinaryUtil.ascIIToString(dtsMsgVO.getDevicePass());

        device_serialnum = BinaryUtil.ascIIToString(dtsMsgVO.getDeviceSN());
        device_ip = ip1 + "." + ip2 + "." + ip3 + "." + ip4;
        device_port = port_tcp + "";
        device_pass = dtsMsgVO.getDevicePass();
    }

    public String getPk_device() {
        return pk_device;
    }

    public void setPk_device(String pk_device) {
        this.pk_device = pk_device;
    }

    public String getPk_area() {
        return pk_area;
    }

    public void setPk_area(String pk_area) {
        this.pk_area = pk_area;
    }

    public String getPk_corp() {
        return pk_corp;
    }

    public void setPk_corp(String pk_corp) {
        this.pk_corp = pk_corp;
    }

    public String getDevice_code() {
        return device_code;
    }

    public void setDevice_code(String device_code) {
        this.device_code = device_code;
    }

    public String getDevice_name() {
        return device_name;
    }

    public void setDevice_name(String device_name) {
        this.device_name = device_name;
    }

    public String getDevice_type() {
        return device_type;
    }

    public void setDevice_type(String device_type) {
        this.device_type = device_type;
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

    public byte[] getDevice_pass() {
        return device_pass;
    }

    public void setDevice_pass(byte[] device_pass) {
        this.device_pass = device_pass;
    }
}
