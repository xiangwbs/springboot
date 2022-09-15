package com.xwbing.service.demo.designpattern.obServer.demo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xiangwb
 * @date 2020/3/6 21:48
 */
public class WeChatSubject implements AbstractSubject {
    private List<ObServer> obServers = new ArrayList<>();

    @Override
    public void addObServer(ObServer obServer) {
        obServers.add(obServer);
    }

    @Override
    public void removeObServer(ObServer obServer) {
        obServers.remove(obServer);
    }


    @Override
    public void notifyObServer(String message) {
        //群发消息
        obServers.forEach(obServer -> obServer.update(message));
    }
}
