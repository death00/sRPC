package com.death00.start;

import com.death00.client.RPCClient;
import com.death00.exception.RPCException;
import com.death00.request.ExpRequest;
import com.death00.response.ExpResponse;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author death00
 * @date 2019/9/16
 */
public class DemoClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(DemoClient.class);

    private RPCClient client;

    public DemoClient(RPCClient client) {
        this.client = client;
        this.client.rpc("fib_res", Long.class).rpc("exp_res", ExpResponse.class);
    }

    public long fib(int n) {
        return (Long) client.send("fib", n);
    }

    public ExpResponse exp(int base, int exp) {
        return (ExpResponse) client.send("exp", ExpRequest.builder().base(base).exp(exp).build());
    }

    public static void main(String[] args) throws InterruptedException {
        String basePath = System.getenv("SERV_BASE");
        PropertyConfigurator.configure(basePath + "/demoClient.properties");
        PropertyConfigurator.configureAndWatch(
                basePath + "/demoClient.properties",
                2000
        );

        RPCClient client = new RPCClient("localhost", 8888);
        DemoClient demo = new DemoClient(client);
        for (int i = 0; i < 30; i++) {
            try {
                LOGGER.info(
                        "fib({}) = {}",
                        i,
                        demo.fib(i)
                );
                Thread.sleep(100);
            } catch (RPCException e) {
                i--; // retry
            }
        }
        for (int i = 0; i < 30; i++) {
            try {
                ExpResponse res = demo.exp(2, i);
                Thread.sleep(100);
                LOGGER.info(
                        "exp2({}) = {} cost={}ns",
                        i,
                        res.getValue(),
                        res.getCostInNanos()
                );
            } catch (RPCException e) {
                i--; // retry
            }
        }
        client.close();
    }

}
