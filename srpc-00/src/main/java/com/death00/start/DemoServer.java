package com.death00.start;

import com.death00.handler.ExpRequestHandler;
import com.death00.handler.FibRequestHandler;
import com.death00.request.ExpRequest;
import com.death00.server.RPCServer;
import org.apache.log4j.PropertyConfigurator;

/**
 * @author death00
 * @date 2019/9/16
 */
public class DemoServer {

    public static void main(String[] args) {
        String basePath = System.getenv("SERV_BASE");
        PropertyConfigurator.configure(basePath + "/demoServer.properties");
        PropertyConfigurator.configureAndWatch(
                basePath + "/demoServer.properties",
                2000
        );

        RPCServer server = new RPCServer("localhost", 8888, 2, 16);
        server
                .service(
                        "fib",
                        Integer.class,
                        new FibRequestHandler()
                )
                .service(
                        "exp",
                        ExpRequest.class,
                        new ExpRequestHandler()
                );
        server.start();
    }
}
