package com.iandtop.front.smartpark.door.dao;

import com.iandtop.front.smartpark.door.util.DoorConstants;
import com.iandtop.front.smartpark.door.vo.QPAVO;
import com.iandtop.front.smartpark.pub.utils.JDBCSQLiteClientUtil;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 本地sqlite数据库
 */
public class DoorSQLiteDao {
    private final static String DB_NAME = "spdoor";//sqlite数据库名称

    public static void getDoor_paramValue(Vertx vertx, String key, Handler<String> resultHandler) {
        String sql = "SELECT * FROM door_param where param_key = '" + key + "'";
        try {
            JDBCSQLiteClientUtil.executeCommon(vertx, DB_NAME, sql, data -> {
                if (data.size() > 0) {
                    Map map = (Map) data.get(0);
                    resultHandler.handle((String) map.get("param_value"));
                } else {
                    resultHandler.handle(DoorConstants.NOValue);
                }
            });
        } catch (SQLException e) {
            e.printStackTrace();
            resultHandler.handle(DoorConstants.NOValue);
        }
    }

    public static void updateDoor_param(Vertx vertx, String key, String value, Handler<Integer> resultHandler) {
        String sql = "update door_param set param_value='" + value + "' where param_key ='" + key + "' ";
        try {
            JDBCSQLiteClientUtil.executeUpdateCommon(vertx, DB_NAME, sql, num -> {
                resultHandler.handle(num);
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void insertDoor_param(Vertx vertx, String key, String value, Handler<Integer> resultHandler) {
        String sql = "insert into door_param (param_key,param_value) " +
                "values ('" + key + "','" + value + "')";
        try {
            JDBCSQLiteClientUtil.executeUpdateCommon(vertx, DB_NAME, sql, num -> {
                resultHandler.handle(num);
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

/*    public static void insertDoor_devices(Vertx vertx, DoorDeviceVO vo, Handler<Integer> resultHandler) {
        String sql = "insert into door_device (device_serialnum,device_ip ,device_port,device_pass) " +
                "values ('" + vo.getDevice_serialnum() + "','" + vo.getDevice_ip() + "','" + vo.getDevice_port() + "','" + vo.getDevice_pass() + "')";
        try {
            executeUpdateCommon(vertx, sql, num -> {
                resultHandler.handle(num);
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void getDoor_devices(Vertx vertx, Handler<List> resultHandler) {
        String sql = "SELECT * FROM door_device ";
        try {
            executeCommon(vertx, sql, data -> {
                if (data.size() > 0) {
                    resultHandler.handle(data);
                } else {
                    resultHandler.handle(data);
                }
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }*/

    //获得权限信息本地缓存
    public static void getQPAs(Vertx vertx, String tableName, Handler<List> resultHandler) {
        String sql = "SELECT * FROM " + tableName + " ";
        try {
            JDBCSQLiteClientUtil.executeCommon(vertx, DB_NAME, sql, data -> {
                if (data.size() > 0) {
                    resultHandler.handle(data);
                } else {
                    resultHandler.handle(data);
                }
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //插入权限信息本地缓存
    public static void insertQPA(Vertx vertx, String tableName, QPAVO vo, Handler<Integer> resultHandler) {
        String sql = "insert into " + tableName + " (card_code,card_ineffectived_ts ,password,pk_device,device_ip,device_port,device_serialnum,door_code,time_grp_code,pk) " +
                "values ('" + vo.getCard_code() + "','"
                + vo.getCard_ineffectived_ts() + "','"
                + vo.getPassword() + "','"
                + vo.getPk_device() + "','"
                + vo.getDevice_ip() + "','"
                + vo.getDevice_port() + "','"
                + vo.getDevice_serialnum() + "','"
                + vo.getDoor_code() + "','"
                + vo.getTime_grp_code() + "','"
                + vo.getPk() + "')";
        try {
            JDBCSQLiteClientUtil.executeUpdateCommon(vertx, DB_NAME, sql, num -> {
                resultHandler.handle(num);
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //插入权限信息本地缓存
    public static void insertQPABatch(Vertx vertx, String tableName, List<QPAVO> vos, Handler<Integer> resultHandler) {
        List<String> sqls = new ArrayList<>();
        for (QPAVO vo : vos) {
            String sql = "insert into " + tableName + " (card_code,card_ineffectived_ts ,password,pk_device,device_ip,device_port,device_serialnum,door_code,time_grp_code,pk) " +
                    "values ('" + vo.getCard_code() + "','"
                    + vo.getCard_ineffectived_ts() + "','"
                    + vo.getPassword() + "','"
                    + vo.getPk_device() + "','"
                    + vo.getDevice_ip() + "','"
                    + vo.getDevice_port() + "','"
                    + vo.getDevice_serialnum() + "','"
                    + vo.getDoor_code() + "','"
                    + vo.getTime_grp_code() + "','"
                    + vo.getPk() + "')";
            sqls.add(sql);
        }

        try {
            JDBCSQLiteClientUtil.executeUpdateCommonBatch(vertx, DB_NAME, sqls, num -> {
                resultHandler.handle(num);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //删除权限信息本地缓存
    public static void deleteQPA(Vertx vertx, String tableName, List<QPAVO> card_codes, Handler<Integer> resultHandler) {
        String sql = "delete from " + tableName + " ";
        String whereSQL = "";
        int size = card_codes.size();
        for (int i = 0; i < size; i++) {
            String code = card_codes.get(i).getCard_code();
            whereSQL += "card_code = '" + code + "' ";
            if (i < size - 1) {
                whereSQL += " OR ";
            }
        }
        if (whereSQL != "") {
            whereSQL = " where " + whereSQL;
            sql += whereSQL;
            try {
                JDBCSQLiteClientUtil.executeUpdateCommon(vertx, DB_NAME, sql, num -> {
                    resultHandler.handle(num);
                });
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {//如果没有数据则不进行数据库删除操作
            resultHandler.handle(0);
        }
    }

    //删除所有权限信息本地缓存
    public static void deleteQPAAll(Vertx vertx, String tableName, Handler<Integer> resultHandler) {
        String sql = "delete from " + tableName + " ";
        try {
            JDBCSQLiteClientUtil.executeUpdateCommon(vertx, DB_NAME, sql, num -> {
                resultHandler.handle(num);
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void initTables(Vertx vertx, Handler<Integer> resultHandler) {
        String sysparamSql = "create table IF NOT EXISTS door_param(" +
                "param_key		TEXT primary key  ," +
                "param_value	TEXT not null );";

     /*   String sql = "create table IF NOT EXISTS door_device(" +
                "pk_device		TEXT ," +
                "pk_area		TEXT ," +
                "pk_corp		TEXT ," +
                "device_code	TEXT," +
                "device_name	TEXT," +
                "device_type	TEXT,		" +*//* 1单门，2双门，4四门，*//*
                "device_ip		TEXT not null," +*//* IP *//*
                "device_port	TEXT not null," +*//* 端口号  *//*
                "device_pass	BLOB not null," +*//* 通信密码  *//*
                "device_serialnum TEXT primary key " +   *//* 序列号  *//*
                ");";*/

        String doorqpasql = "create table IF NOT EXISTS door_qpa(" +
                "idqpa         INTEGER PRIMARY KEY autoincrement," +
                "card_code		TEXT ," +
                "card_ineffectived_ts		TEXT ," +
                "password		TEXT ," +
                "pk_device	TEXT," +
                "device_ip	TEXT," +
                "device_port	TEXT,		" +/* 1单门，2双门，4四门，*/
                "device_serialnum		TEXT," +/* IP */
                "door_code	TEXT ," +/* 端口号  */
                "time_grp_code	TEXT , " +/* 通信密码  */
                "pk	TEXT " +/* 会议室或访客的唯一标志  */
                ");";

        String qpasql = "create table IF NOT EXISTS qpa(" +
                "idqpa         INTEGER PRIMARY KEY autoincrement," +
                "card_code		TEXT ," +
                "card_ineffectived_ts		TEXT ," +
                "password		TEXT ," +
                "pk_device	TEXT," +
                "device_ip	TEXT," +
                "device_port	TEXT,		" +/* 1单门，2双门，4四门，*/
                "device_serialnum		TEXT," +/* IP */
                "door_code	TEXT ," +/* 端口号  */
                "time_grp_code	TEXT ," +/* 通信密码  */
                "pk	TEXT " +/* 会议室或访客的唯一标志  */
                ");";

        try {
            JDBCSQLiteClientUtil.executeUpdateCommon(vertx, DB_NAME, doorqpasql, num -> {
                try {
                    JDBCSQLiteClientUtil.executeUpdateCommon(vertx, DB_NAME, sysparamSql, num2 -> {
                        try {
                            JDBCSQLiteClientUtil.executeUpdateCommon(vertx, DB_NAME, qpasql, num3 -> {
                                resultHandler.handle(num3);
                            });
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    });
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void dropTables(Vertx vertx, Handler<Integer> resultHandler) {
        String sysparamSql = "drop TABLE if EXISTS door_param;";
        //String sql = "drop TABLE if EXISTS door_device;";
        String doorqpasql = "drop TABLE if EXISTS door_qpa;";
        String qpasql = "drop TABLE if EXISTS qpa;";
        try {
            JDBCSQLiteClientUtil.executeUpdateCommon(vertx, DB_NAME, doorqpasql, res -> {
                try {
                    JDBCSQLiteClientUtil.executeUpdateCommon(vertx, DB_NAME, sysparamSql, res2 -> {
                        try {
                            JDBCSQLiteClientUtil.executeUpdateCommon(vertx, DB_NAME, qpasql, res3 -> {
                                resultHandler.handle(res3);
                            });
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    });
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
