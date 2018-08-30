package com.iandtop.front.smartpark.visitor.client;

import com.google.gson.Gson;
import com.iandtop.front.smartpark.visitor.vo.RequestResultVO;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;

public class HttpClient {

    private String doorIP ;
    private String doorPort ;
    private String doorSN ;
    private String doorNum ;

    private String uapServerURL;
    private int uapServerPort;
    private String urlPath ;

    public static void main(String arg[]) {

    }

    //上传二维码
    public void uploadDQ(Vertx vertx,Handler<Boolean> handler) {
        vertx.createHttpClient().getNow(uapServerPort, uapServerURL, urlPath, resp -> {
            resp.bodyHandler(body -> {
                try{
                    System.out.println("上传二维码成功");
                    Gson gson = new Gson();
                    RequestResultVO resultVO = gson.fromJson(body.toString("UTF-8"), RequestResultVO.class);

                    handler.handle(true);
                }catch (Exception e){
                    System.out.println("向uap传送二维码数据失败:");
                    handler.handle(false);
                    e.printStackTrace();
                }
            });
        });
    }

    public String getUapServerURL() {
        return uapServerURL;
    }

    public void setUapServerURL(String uapServerURL) {
        this.uapServerURL = uapServerURL;
    }

    public int getUapServerPort() {
        return uapServerPort;
    }

    public void setUapServerPort(int uapServerPort) {
        this.uapServerPort = uapServerPort;
    }

    public String getUrlPath() {
        return urlPath;
    }

    public void setUrlPath(String urlPath) {
        this.urlPath = urlPath;
    }

    public String getDoorIP() {
        return doorIP;
    }

    public void setDoorIP(String doorIP) {
        this.doorIP = doorIP;
    }

    public String getDoorPort() {
        return doorPort;
    }

    public void setDoorPort(String doorPort) {
        this.doorPort = doorPort;
    }

    public String getDoorSN() {
        return doorSN;
    }

    public void setDoorSN(String doorSN) {
        this.doorSN = doorSN;
    }

    public String getDoorNum() {
        return doorNum;
    }

    public void setDoorNum(String doorNum) {
        this.doorNum = doorNum;
    }
}
