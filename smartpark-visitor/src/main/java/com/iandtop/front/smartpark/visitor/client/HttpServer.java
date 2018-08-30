package com.iandtop.front.smartpark.visitor.client;

import com.iandtop.front.smartpark.visitor.util.Inspiry532Utils;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;

public class HttpServer {

    public static void main(String arg[]) {

    }

    public void startHttpServer(Vertx vertx ,int port, Handler<Object> handler) {
        vertx.createHttpServer().requestHandler(req -> {
            String qcode = Inspiry532Utils.getDecodeString();
            req.response().putHeader("content-type", "text/html").end("<html><body><h1>Hello from vert.x!</h1></body></html>");
        }).listen(port);
    }
}
