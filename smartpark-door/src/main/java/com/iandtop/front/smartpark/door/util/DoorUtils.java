package com.iandtop.front.smartpark.door.util;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.iandtop.common.driver.DoorDriver;
import com.iandtop.common.driver.door.DriverTCPClient;
import com.iandtop.common.utils.http.HttpRequester;
import com.iandtop.common.utils.http.HttpRespons;
import com.iandtop.front.smartpark.door.vo.DeviceVO;
import com.iandtop.front.smartpark.door.vo.QPAVO;
import com.iandtop.front.smartpark.door.vo.RequestResultVO;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpMethod;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author andyzhaozhao
 */
public class DoorUtils {
    public static void main(String arg[]) {
        //  byte a = (byte)0xf0;
        //  byte b = (byte) (a & 0xff);

        // int dc = a;
//        for (QPAVO qpavo : sameCodes) {
//            int dc = Integer.parseInt(qpavo.getDoor_code());
//            int timegroupcode = Integer.parseInt(qpavo.getTime_grp_code());
//            openTimeBytes[dc-1] = MyPubUtil.intToBytes2(timegroupcode)[3];
//
//            authAndAuthstrs.replace(dc-1,dc,"1");
//        }

//        String uapServerURL = "192.168.0.7";
//        int uapServerPort = 8080;
//        String urlPath = "/ichannel/jsonrpc/mobile/QPA";
//        new HttpClientUtil().getPsnCardAuths(uapServerURL, uapServerPort, urlPath, qpas -> {
//        });
    }

    /**
     * 从uap获取最新权限信息(普通门禁，会议室，访客)
     * @param uapServerURL
     * @param uapServerPort
     * @param urlPath
     * @param handler
     */
    public static void getAuthFromUap(Vertx vertx, String uapServerURL, int uapServerPort, String urlPath,Handler<List<QPAVO>> handler) {
        vertx.createHttpClient().getNow(uapServerPort, uapServerURL, urlPath, resp -> {
            resp.bodyHandler(body -> {
                try{
                    Gson gson = new Gson();
                    RequestResultVO resultVO = gson.fromJson(body.toString("UTF-8"), RequestResultVO.class);
                    if(resultVO.getSuccess()==true){
                        System.out.println("从服务器获取数据成功");
                        List<QPAVO> datas = resultVO.getResultData();
                        handler.handle(datas);
                    }else{
                        System.out.println("获取门禁权限"+resultVO.getMsg());
                    }
                }catch (Exception e){
                    System.out.println("获取设备报错:");
                    e.printStackTrace();
                }
            });
        });
    }

    /**
     * 提交更新成功的pk(会议室或者访客)
     */
    public static void postDownLoadAuthFromUapSuccess(Vertx vertx, String uapServerURL, int uapServerPort, String urlPath,
                                                     List<String> pks , Handler<Boolean> handler) {
        vertx.executeBlocking(future -> {
            String url = "http://" + uapServerURL + ":" + uapServerPort + urlPath;
            String pksString = new Gson().toJson(pks);

            // 调用接口
            Map<String, String> param = new HashMap<>();
            param.put("pks", pksString);
            try {
                HttpRespons response = new HttpRequester().sendPost(url, param);
                String result = response.getContent();
                RequestResultVO resultVO = new Gson().fromJson(result, RequestResultVO.class);
                future.complete(resultVO.getSuccess());
            } catch (IOException e) {
                e.printStackTrace();
                future.fail(e.getMessage());
            }

        }, issuccess -> {
            handler.handle((Boolean)issuccess.result());
        });
    }

    /**
     * 向设备写权限 ,排序区
     *
     * @param vertx
     * @param deviceVOs
     * @param endHandler
     */
    public static void writePsnCardAuthBatchOrderd(Vertx vertx, List<DeviceVO> deviceVOs, Handler<Boolean> endHandler) {
        int[] completNum = new int[1];//记录已经完成的任务的个数
        if(deviceVOs==null||deviceVOs.size()==0){
            endHandler.handle(false);
        }else {
            for (DeviceVO deviceVO : deviceVOs) {
                DriverTCPClient tcpClient = DoorDriver.getInstance(vertx, deviceVO.getUrl(), deviceVO.getPort(), deviceVO.getSn());
                tcpClient.writePsnCardAuthBatch(deviceVO.getCard_doorBytesList(), isSuccess -> {
                    if(isSuccess){
                        System.out.println(isSuccess);
                        completNum[0]++;
                        if (completNum[0] >= deviceVOs.size()) {
                            endHandler.handle(true);
                        }
                    }else{

                    }
                });
            }
        }
    }

    /**
     * 向设备写权限 ,非排序区
     *
     * @param vertx
     * @param deviceVOs
     * @param endHandler
     */
    public static void writePsnCardAuthBatchUn(Vertx vertx, List<DeviceVO> deviceVOs, Handler<Boolean> endHandler) {
        int[] completNum = new int[1];//记录已经完成的任务的个数
        if(deviceVOs==null||deviceVOs.size()==0){
            endHandler.handle(false);
        }else {
            for (DeviceVO deviceVO : deviceVOs) {
                DriverTCPClient tcpClient = DoorDriver.getInstance(vertx, deviceVO.getUrl(), deviceVO.getPort(), deviceVO.getSn());
                tcpClient.writePsnCardAuthUnBatch(deviceVO.getCard_doorBytesList(), isSuccess -> {
                    if(isSuccess){
                        System.out.println(isSuccess);
                        completNum[0]++;
                        if (completNum[0] >= deviceVOs.size()) {
                            endHandler.handle(true);
                        }
                    }else{

                    }
                });
            }
        }
    }

    /**
     * 删除非排序区中的权限
     *
     * @param vertx
     * @param deviceVOs
     * @param endHandler
     */
    public static void deletePsnCardAuthBatchUn(Vertx vertx, List<DeviceVO> deviceVOs, Handler<Boolean> endHandler) {
        int[] completNum = new int[1];//记录已经完成的任务的个数
        if(deviceVOs==null||deviceVOs.size()==0){
            endHandler.handle(false);
        }else {
            for (DeviceVO deviceVO : deviceVOs) {
                DriverTCPClient tcpClient = DoorDriver.getInstance(vertx, deviceVO.getUrl(), deviceVO.getPort(), deviceVO.getSn());
                tcpClient.deletePsnCardAuthUnBatch(deviceVO.getCard_doorBytesList(), isSuccess -> {
                    if(isSuccess){
                        System.out.println("权限信息写入设备" + deviceVO.getSn() + "成功");
                        System.out.println(isSuccess);
                        completNum[0]++;
                        if (completNum[0] >= deviceVOs.size()) {
                            endHandler.handle(true);
                        }
                    }else{

                    }
                });
            }
        }
    }

    /**
     * byte数组中取int数值，本方法适用于(低位在后，高位在前)的顺序。和intToBytes2（）配套使用
     */
    public static int oneByteToInt2(byte b) {
        int value;
        byte[] src = new byte[]{0x00,0x00,0x00,b};
        value = (int) (((src[0] & 0xFF) << 24)
                | ((src[1] & 0xFF) << 16)
                | ((src[2] & 0xFF) << 8)
                | (src[3] & 0xFF));
        return value;
    }
    public static int twoByteToInt2(byte[] b) {
        int value;
        byte[] src = new byte[]{0x00,0x00,b[0],b[1]};
        value = (int) (((src[0] & 0xFF) << 24)
                | ((src[1] & 0xFF) << 16)
                | ((src[2] & 0xFF) << 8)
                | (src[3] & 0xFF));
        return value;
    }
}


