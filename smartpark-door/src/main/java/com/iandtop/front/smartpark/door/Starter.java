package com.iandtop.front.smartpark.door;

import com.iandtop.common.utils.CommonFileUtil;
import com.iandtop.front.smartpark.door.dao.DoorSQLiteDao;
import com.iandtop.front.smartpark.door.server.DoorHttpServer;
import com.iandtop.front.smartpark.door.timetask.ConferenceDoorTimeTask;
import com.iandtop.front.smartpark.door.timetask.DoorTimeTask;
import com.iandtop.front.smartpark.door.timetask.VisitorDoorTimeTask;
import com.iandtop.front.smartpark.door.util.DoorConstants;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;

import java.util.Properties;


public class Starter {

    public static Long timerID = null;

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();

        //System.out.println(CommonFileUtil.baseDir);
        Properties p = CommonFileUtil.getPropertyFile(CommonFileUtil.baseDir + "\\smartpark-door.properties");
        String port = p.getProperty("httpport");//http server端口

        DoorSQLiteDao.initTables(vertx, r -> {//初始化本地数据库
            //获取端口号
            DoorSQLiteDao.getDoor_paramValue(vertx, DoorConstants.KEY_HTTP_SERVER_PORT, value -> {
                if (value.equals(DoorConstants.NOValue)) {
                    //新增端口号
                    DoorSQLiteDao.insertDoor_param(vertx, DoorConstants.KEY_HTTP_SERVER_PORT, port, imum -> {
                        //启动HttpServer
                        DoorHttpServer.startHttpServer(new DoorHttpServer(), vertx, Integer.parseInt(port));
                        //尝试启动主任务
                        startMain(vertx, startResult -> {
                            System.out.println(startResult);
                        });

                    });
                } else {
                    //更新端口
                    DoorSQLiteDao.updateDoor_param(vertx, DoorConstants.KEY_HTTP_SERVER_PORT, port, imum -> {
                        //启动HttpServer
                        DoorHttpServer.startHttpServer(new DoorHttpServer(), vertx, Integer.parseInt(port));
                        //尝试启动主任务
                        startMain(vertx, startResult -> {
                            System.out.println(startResult);
                        });

                    });
                }
            });
        });
    }

    /**
     * 开启定时任务(主任务)
     *
     * @param vertx
     * @param resultHandler
     */
    public static void startMain(Vertx vertx, Handler<String> resultHandler) {

        if (timerID != null) {//如果之前存在轮训定时任务，则先关闭
            vertx.cancelTimer(timerID);
        }

        DoorSQLiteDao.getDoor_paramValue(vertx, DoorConstants.KEY_FRONT_CODE, front_code -> {
            if (isValueWrong(front_code)) {
                //如果获取前置机唯一id失败
                resultHandler.handle(DoorConstants.ss);
            } else {
                DoorSQLiteDao.getDoor_paramValue(vertx, DoorConstants.KEY_UAP_PK_CORP, pk_corp -> {
                    if (isValueWrong(pk_corp)) {
                        //如果获取公司失败
                        resultHandler.handle(DoorConstants.s0);
                    } else {
                        DoorSQLiteDao.getDoor_paramValue(vertx, DoorConstants.KEY_UAP_SERVER_URL, uapserverurl -> {
                            if (isValueWrong(uapserverurl)) {
                                //如果获取服务器ip失败
                                resultHandler.handle(DoorConstants.s1);
                            } else {
                                DoorSQLiteDao.getDoor_paramValue(vertx, DoorConstants.KEY_UAP_SERVER_PORT, uapport -> {
                                    if (isValueWrong(uapport)) {
                                        //如果获取服务器port失败
                                        resultHandler.handle(DoorConstants.s2);
                                    } else {
                                        DoorSQLiteDao.getDoor_paramValue(vertx, DoorConstants.KEY_LOOP_INTERVAL, interval -> {
                                            if (isValueWrong(interval)) {
                                                //如果获取interval失败
                                                resultHandler.handle(DoorConstants.s3);
                                            } else {
                                                //开始定时任务
                                                timerID = vertx.setPeriodic(Long.parseLong(interval), timerID -> {
                                                    //定时同步门禁权限
                                                    new DoorTimeTask().run(vertx, uapserverurl, front_code, Integer.parseInt(uapport), h -> {
                                                        if (h) {
                                                            //定时同步会议室预定权限，同步过的权限不再次下发
                                                            new ConferenceDoorTimeTask().run(vertx, uapserverurl, front_code, Integer.parseInt(uapport), h1 -> {
                                                                if (h1) {
                                                                    //定时同步访客预定权限，同步过的权限不再次下发
                                                                    new VisitorDoorTimeTask().run(vertx, uapserverurl, front_code, Integer.parseInt(uapport), h2 -> {});
                                                                }
                                                            });
                                                        }
                                                    });
                                                });
                                                resultHandler.handle(DoorConstants.s4);
                                            }
                                        });//end of interval
                                    }
                                });//end of port
                            }
                        });//end of url
                    }
                });//end of pk_corp
            }
        });//end of frontcode
    }

    public static Boolean isValueWrong(String value) {
        if (value.equals(DoorConstants.NULLValue) || value.equals(DoorConstants.NOValue)) {
            return true;//错误数据
        }

        return false;//正确数据
    }
}


