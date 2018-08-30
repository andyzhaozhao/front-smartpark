package com.iandtop.front.smartpark.door.vo;

import java.util.List;

public class RequestResultVO {
    private Boolean success ;
    private String msg;
    private String detailMessage;
    private String statusCode;
    private List<QPAVO> resultData;
    private String resultDataType;
    private Boolean debug;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getDetailMessage() {
        return detailMessage;
    }

    public void setDetailMessage(String detailMessage) {
        this.detailMessage = detailMessage;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getResultDataType() {
        return resultDataType;
    }

    public void setResultDataType(String resultDataType) {
        this.resultDataType = resultDataType;
    }

    public Boolean getDebug() {
        return debug;
    }

    public void setDebug(Boolean debug) {
        this.debug = debug;
    }

    public List<QPAVO> getResultData() {
        return resultData;
    }

    public void setResultData(List<QPAVO> resultData) {
        this.resultData = resultData;
    }
}
