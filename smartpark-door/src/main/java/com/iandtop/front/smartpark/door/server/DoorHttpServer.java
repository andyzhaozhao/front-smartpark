package com.iandtop.front.smartpark.door.server;

import com.google.gson.Gson;
import com.iandtop.front.smartpark.door.Starter;
import com.iandtop.front.smartpark.door.dao.DoorSQLiteDao;
import com.iandtop.front.smartpark.door.util.DoorConstants;
import com.iandtop.front.smartpark.door.vo.RequestResultVO;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;

/**
 * 门禁前置机  httpserver服务
 * @author andyzhao
 */
public class DoorHttpServer {

    private static Vertx vertx = null;

    public static void startHttpServer(DoorHttpServer doorHttpServer, Vertx _vertx, int httpServerPort) {
        vertx = _vertx;

        HttpServer server = vertx.createHttpServer();
        Router router = Router.router(vertx);

        //初始化门禁信息并启动
        router.route(HttpMethod.GET, "/door/init/" +
                ":" + DoorConstants.KEY_FRONT_CODE + "/" +
                ":" + DoorConstants.KEY_UAP_PK_CORP + "/" +
                ":" + DoorConstants.KEY_UAP_SERVER_URL + "/" +
                ":" + DoorConstants.KEY_UAP_SERVER_PORT + "/" +
                ":" + DoorConstants.KEY_LOOP_INTERVAL).handler(routingContext -> {

            String front_code = routingContext.request().getParam(DoorConstants.KEY_FRONT_CODE);
            String pk_corp = routingContext.request().getParam(DoorConstants.KEY_UAP_PK_CORP);
            String uap_url = routingContext.request().getParam(DoorConstants.KEY_UAP_SERVER_URL);
            String uap_port = routingContext.request().getParam(DoorConstants.KEY_UAP_SERVER_PORT);
            String uap_interval = routingContext.request().getParam(DoorConstants.KEY_LOOP_INTERVAL);

            doorHttpServer.persistent(DoorConstants.KEY_FRONT_CODE, front_code, rfront_code-> {
                doorHttpServer.persistent(DoorConstants.KEY_UAP_PK_CORP, pk_corp, rpk_corp -> {
                    doorHttpServer.persistent(DoorConstants.KEY_UAP_SERVER_URL, uap_url, rurl -> {
                        doorHttpServer.persistent(DoorConstants.KEY_UAP_SERVER_PORT, uap_port, rport -> {
                            doorHttpServer.persistent(DoorConstants.KEY_LOOP_INTERVAL, uap_interval, rinterval -> {
                                //尝试启动主任务
                                Starter.startMain(vertx, startResult -> {
                                    String rs = doorHttpServer.getResponse("true");
                                    routingContext.response().putHeader("content-type", "text/json").end(rs);
                                });
                            });
                        });
                    });
                });
            });
        });

        //前置机运行状态：正在运行，无响应
        router.route(HttpMethod.GET, "/door/frontstate/" +
                ":" + DoorConstants.KEY_FRONT_CODE + "")
                .handler(routingContext -> {
                    String front_code = routingContext.request().getParam(DoorConstants.KEY_FRONT_CODE);
                    Boolean isRunning = false;
                    //发请求给控制板，确定是否在线
                    if (Starter.timerID != null && isRunning) {//正在运行
                        String rs = doorHttpServer.getResponse("1");
                        routingContext.response().putHeader("content-type", "text/json").end(rs);
                    } else {
                        String rs = doorHttpServer.getResponse("0");
                        routingContext.response().putHeader("content-type", "text/json").end(rs);
                    }
                });

        //设备运行状态：正在运行，无响应
        router.route(HttpMethod.GET, "/door/devicestate/" +
                ":" + DoorConstants.PK_DEVICE + "")
                .handler(routingContext -> {
                    String pk_device = routingContext.request().getParam(DoorConstants.PK_DEVICE);
                    Boolean isRunning = false;
                    //发请求给控制板，确定是否在线
                    if (Starter.timerID != null && isRunning) {//正在运行
                        String rs = doorHttpServer.getResponse("1");
                        routingContext.response().putHeader("content-type", "text/json").end(rs);
                    } else {
                        String rs = doorHttpServer.getResponse("0");
                        routingContext.response().putHeader("content-type", "text/json").end(rs);
                    }
                });

        //远程开门
        router.route(HttpMethod.GET, "/door/open/:deviceid/:doorid")
                .handler(routingContext -> {

                    String productType = routingContext.request().getParam("deviceid");
                    String productID = routingContext.request().getParam("doorid");
                    String name = routingContext.request().getParam("name");
                    routingContext.response().putHeader("content-type", "text/json").end("Hello World!");
                });

        //设置轮询时间间隔
        router.route(HttpMethod.GET, "/door/setinterval/:deviceid/:interval")
                .handler(routingContext -> {

                    String productType = routingContext.request().getParam("productype");
                    String productID = routingContext.request().getParam("productid");
                    String name = routingContext.request().getParam("name");
                    routingContext.response().putHeader("content-type", "text/json").end("Hello World!");
                });

        server.requestHandler(router::accept).listen(httpServerPort);

        System.out.println("启动HTTP服务，端口号为：" + httpServerPort);
    }

    public void persistent(String key, String newValue, Handler<String> resultHandler) {
        if(newValue!=null){//如果有新值
            //获取
            DoorSQLiteDao.getDoor_paramValue(vertx, key, value -> {
                if (value.equals(DoorConstants.NOValue)) {
                    //新增
                    DoorSQLiteDao.insertDoor_param(vertx, key, newValue, imum -> {

                        resultHandler.handle(DoorConstants.s5);
                    });
                }else{
                    //更新
                    DoorSQLiteDao.updateDoor_param(vertx, key, newValue, imum -> {

                        resultHandler.handle(DoorConstants.s5);
                    });
                }
            });
        }else{
            resultHandler.handle(DoorConstants.s7);
        }
    }

    public String getResponse(String msg) {
        Gson gson = new Gson();
        RequestResultVO resultVO = new RequestResultVO();
        resultVO.setSuccess(true);
        resultVO.setMsg(msg);
        String rs = gson.toJson(resultVO);
        return rs;
    }

}
