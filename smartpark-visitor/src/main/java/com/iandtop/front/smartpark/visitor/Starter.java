package com.iandtop.front.smartpark.visitor;

import com.iandtop.front.smartpark.visitor.client.HttpClient;
import com.iandtop.front.smartpark.visitor.client.HttpServer;
import com.iandtop.front.smartpark.visitor.util.Inspiry532Utils;
import io.vertx.core.Vertx;
import org.sqlite.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Starter {

    public static void main(String[] args) {
        InputStream inputStream = Starter.class.getResourceAsStream("/smartparkvisitor.properties");
        Properties p = new Properties();
        try {
            p.load(inputStream);
            inputStream.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        Inspiry532Utils.initInspiry532();
        Vertx clientVertx = Vertx.vertx();
        String uapServerURL = p.getProperty("uapServerURL");//uap服务器的ip
        String uapServerPort = p.getProperty("uapServerPort");//uap服务器的端口
        String period = p.getProperty("period");//执行Inspiry532Utils.getDecodeString()方法间隔时间
        String frontPort = p.getProperty("frontPort");//访客前置机自身的端口号

        String doorIP = p.getProperty("doorIP");//二维码扫描头头控制的门的ip
        String doorPort = p.getProperty("doorPort");//二维码扫描头头控制的门的端口
        String doorSN = p.getProperty("doorSN");//二维码扫描头头控制的门的sn
        String doorNum = p.getProperty("doorNum");//二维码扫描头头控制的门号：四门控制板的门号为1，2,3,4

        Vertx serverVertx = Vertx.vertx();
        new HttpServer().startHttpServer(serverVertx,Integer.parseInt(frontPort),res->{

        });

        //定时扫描二维码，一旦获取二维码成功，则简历httpclient上传二维码
        //uap获得二维码后发送远程开门指令
        HttpClient httpClient = new HttpClient();
        httpClient.setDoorIP(doorIP);
        httpClient.setDoorPort(doorPort);
        httpClient.setDoorSN(doorSN);
        httpClient.setDoorNum(doorNum);
        httpClient.setUapServerURL(uapServerURL);
        httpClient.setUapServerPort(Integer.parseInt(uapServerPort));

        Runnable runnable = new Runnable() {
            public void run() {
                String dstr = Inspiry532Utils.getDecodeString();
                httpClient.setUrlPath("/ichannel/jsonrpc/mobile/VTOD" +
                        "?url=" +doorIP+
                        "&port=" +doorPort+
                        "&sn=" +doorSN+
                        "&doorid=" +doorNum+
                        "&tdcode="+dstr);
                if(dstr!=null && !dstr.trim().equals("")){
                    httpClient.uploadDQ(clientVertx,res->{

                    });
                }
            }
        };

        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        // 第二个参数为首次执行的延时时间，第三个参数为定时执行的间隔时间
        service.scheduleAtFixedRate(runnable, 1, Integer.parseInt(period), TimeUnit.SECONDS);
    }
}