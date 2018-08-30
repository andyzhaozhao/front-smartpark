package com.iandtop.front.smartpark.door.timetask;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;

/**
 * 定时任务
 * @author andyzhao
 */
public interface ITimeTask {
    void run(Vertx vertx, String uapServerURL, String pk_corp, int uapServerPort, Handler<Boolean> endHandler);
}
