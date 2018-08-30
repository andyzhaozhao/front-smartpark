package com.iandtop.front.smartpark.pos;

import com.iandtop.common.utils.CommonFileUtil;
import com.iandtop.front.smartpark.pos.server.TCPServerManual;
import com.iandtop.front.smartpark.pos.util.PosConstants;
import com.iandtop.front.smartpark.pub.vo.OracleDBVO;
import io.vertx.core.Vertx;
import io.vertx.core.net.NetServer;

import java.util.Properties;

public class Starter {

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();

        Properties p = CommonFileUtil.getPropertyFile(CommonFileUtil.baseDir + "\\smartpark-pos.properties");
        String port = p.getProperty("tcpserverport");//tcp server端口
        String rule_type = p.getProperty("ruletype");//消费规则类型

        OracleDBVO vo = new OracleDBVO();
        vo.setDburl(p.getProperty("dburl"));
        vo.setDbport(p.getProperty("dbport"));
        vo.setDbsid(p.getProperty("dbsid"));
        vo.setDbuser(p.getProperty("dbuser"));
        vo.setDbpassword(p.getProperty("dbpassword"));

        PosConstants.vo = vo;
        PosConstants.server_port = Integer.parseInt(port);
        PosConstants.rule_type = Integer.parseInt(rule_type);

        new TCPServerManual().startTCPServer(vertx);
    }

    /**
     * 利用多个核心
     */
    public static void main2() {
        //TODO vertx的集群配置，消费系统的稳定性
        Vertx vertx = Vertx.vertx();
        for (int i = 0; i < 10; i++) {
            NetServer server = vertx.createNetServer();
            server.connectHandler(socket -> {
                socket.handler(buffer -> {
                    // Just echo back the data
                    socket.write(buffer);
                });
            });
            server.listen(1234, "localhost");
        }
    }
}



