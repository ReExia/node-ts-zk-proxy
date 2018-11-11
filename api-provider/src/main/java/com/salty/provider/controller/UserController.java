package com.salty.provider.controller;

import com.salty.provider.core.annitations.ZkRegister;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author setsuna
 * 用户类
 */
@RestController
@RequestMapping("/blog/user")
public class UserController {

    @GetMapping("/user1")
    @ZkRegister(requestPath = "/user/user1", serviceName = "user1Service", method = "GET")
    public Object user1(){
        Map map = new HashMap();
        map.put("message","user1");
        return map;
    }

    @GetMapping("/user2")
    @ZkRegister(requestPath = "/user/user2", serviceName = "user2Service", method = "POST")
    public Object user2(){
        Map map = new HashMap();
        map.put("message","user2");
        return map;
    }

    @GetMapping("/user3")
    @ZkRegister(requestPath = "/user/user3", serviceName = "user3Service", method = "POST")
    public Object user3(){
        Map map = new HashMap();
        map.put("message","user3");
        return map;
    }


}
