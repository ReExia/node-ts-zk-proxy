package com.salty.provider.core.listener;

import com.salty.provider.core.annitations.ZkRegister;
import com.salty.provider.core.entity.ZkRegisterEntity;
import com.salty.provider.core.enums.ZkRegisterEnum;
import com.salty.provider.core.register.ZookeeperRegistryService;
import com.salty.provider.core.register.config.ServerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * @author setsuna
 * 初始化监听器,注册zk,向mongo写入数据
 */
@Component
public class ZooKeeperRegisterInitListener implements ServletContextListener {

    private static Logger logger = LoggerFactory.getLogger(ZooKeeperRegisterInitListener.class);

    @Autowired
    ServerConfig serverConfig;

    //@Autowired
    //ZookeeperRegistryService zookeeperRegistryService;

    @Autowired
    ZookeeperRegistryService registryService;


    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        //1.获取controller类
        ServletContext servletContext = servletContextEvent.getServletContext();
        ApplicationContext applicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
        Map<String, Object> controllers = applicationContext.getBeansWithAnnotation(Controller.class);

        //2.获取注册类ZkRegisterEntity列表
        List<ZkRegisterEntity> zkRegisters = getZkRegisters(controllers);
        logger.info("ZooKeeperRegisterInitListener init success");

        //3.todo 注册服务到zk上
        Iterator<ZkRegisterEntity> iterator = zkRegisters.iterator();
        while (iterator.hasNext()){
            ZkRegisterEntity zkRegisterEntity = iterator.next();
            registryService.register(zkRegisterEntity);
        }
        registryService.insertDataToMongoBatch(zkRegisters);
        logger.info("ZooKeeperRegisterInitListener register success zookeeper");
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
    }

    /**
     * 返回注册类列表
     *
     * @param map
     * @return
     */
    public List<ZkRegisterEntity> getZkRegisters(Map<String, Object> map){
        //返回注册类列表
        List<ZkRegisterEntity> zkRegisterEntities = null;

        try {
            if (!CollectionUtils.isEmpty(map)) {
                zkRegisterEntities = new ArrayList<>();
                for (String key : map.keySet()) {
                    Object o = map.get(key);
                    //获取对象所有方法
                    Method[] methods = o.getClass().getDeclaredMethods();
                    for (int i = 0; i < methods.length; i++) {
                        ZkRegister zkRegister = methods[i].getDeclaredAnnotation(ZkRegister.class);
                        if (null != zkRegister) {
                            ZkRegisterEntity zkRegisterEntity = getZkRegisterEntity(zkRegister);
                            zkRegisterEntities.add(zkRegisterEntity);
                        }
                    }
                }
            }
        } catch (Exception e) {
            //e.printStackTrace();
            logger.error("read zkRegister annotations failure",e);
        }
        return zkRegisterEntities;
    }


    /**
     * 封装注册类
     */
    public ZkRegisterEntity getZkRegisterEntity(ZkRegister zkRegister) {
        ZkRegisterEntity zkRegisterEntity = new ZkRegisterEntity();


        //1.设置项目名称
        zkRegisterEntity.setAppName(serverConfig.getAppName());

        //2.设置服务名称
        String serviceName = zkRegister.serviceName();
        if (!StringUtils.isEmpty(serviceName)) {
            zkRegisterEntity.setServiceName(serviceName);
        } else {
            zkRegisterEntity.setServiceName("");
        }

        //3.设置请求路径 /user/list
        String requestPath = zkRegister.requestPath();
        if (!StringUtils.isEmpty(requestPath)) {
            zkRegisterEntity.setRequestPath(requestPath);
        } else {
            zkRegisterEntity.setRequestPath("");
        }

        //4.设置请求方法,例如 GET
        String method = zkRegister.method();
        if (!StringUtils.isEmpty(method)) {
            zkRegisterEntity.setMethod(method);
        } else {
            zkRegisterEntity.setMethod("");
        }

        //5.设置请求地址 ip:port => 192.168.11.31:8080
        String address = String.format("%s:%s", serverConfig.getAddress(), serverConfig.getPort());
        zkRegisterEntity.setAddress(address);

        //6.设置状态可用
        zkRegisterEntity.setStatus(ZkRegisterEnum.ALIVE.getCode());
        return zkRegisterEntity;
    }
}
