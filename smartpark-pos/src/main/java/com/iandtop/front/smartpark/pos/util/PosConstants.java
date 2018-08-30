package com.iandtop.front.smartpark.pos.util;

import com.iandtop.front.smartpark.pub.vo.OracleDBVO;

/**
 *
 * @author zhaozhao
 */
public class PosConstants {

    public final static String NULLValue = "null";//有数据但是为null
    public final static String NOValue = "novalue";//没有数据
    public final static int RULE_TYPE_NULL = 0;//没有消费规则
    public final static int RULE_TYPE_NORMAL = 1;//一卡通标准消费规则


    public static int server_port = -1;//TCP服务器监听地址
    public static OracleDBVO vo = null;//数据库链接信息vo
    public static int rule_type = RULE_TYPE_NULL;//消费规则类型

}
