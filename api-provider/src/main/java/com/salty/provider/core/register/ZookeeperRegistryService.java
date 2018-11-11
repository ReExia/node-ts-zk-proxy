package com.salty.provider.core.register;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.salty.provider.core.entity.ZkRegisterEntity;
import org.apache.zookeeper.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.CriteriaDefinition;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author setsuna
 * 服务注册实现
 */
@Component
public class ZookeeperRegistryService implements  Watcher {

    private static Logger logger = LoggerFactory.getLogger(ZookeeperRegistryService.class);

    private static CountDownLatch latch = new CountDownLatch(1);

    private ZooKeeper zooKeeper;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MongoTemplate mongoTemplate;

    public ZookeeperRegistryService(){};

    public ZookeeperRegistryService(String zkServers, int timeout){
        try {
            zooKeeper = new ZooKeeper(zkServers, timeout, this);
            latch.await();
            logger.debug("connected to zookeeper");
        } catch (Exception e) {
            //e.printStackTrace();
            logger.error("create zookeeper client failure", e);
        }
    }

    /**
     * 注册服务
     * @param zkRegisterEntity
     */
    public void register(ZkRegisterEntity zkRegisterEntity) {
        try {
            //1.创建根节点(持久节点) 比如 /blog (appName)
            String registryPath = zkRegisterEntity.getAppName();
            if (null == zooKeeper.exists(registryPath, false)){
                zooKeeper.create(registryPath, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                logger.debug("create registry node => {}", registryPath);
            }
            //2.创建服务节点(持久节点) 比如 /helloService (serviceName)
            String servicePath = String.format("%s/%s", registryPath, zkRegisterEntity.getServiceName());
            if (null == zooKeeper.exists(servicePath, false)){
                zooKeeper.create(servicePath,zkRegisterEntity.getServiceName().getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                logger.debug("create service node : {} => {}", registryPath,servicePath);
            }
            //3.创建地址节点(临时顺序节点)
            String addressPath = String.format("%s/%s", servicePath,"address-");
            String data = objectMapper.writeValueAsString(zkRegisterEntity);
            String addressNode = zooKeeper.create(addressPath, data.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            logger.debug("create address node : {} => {}", addressNode, zkRegisterEntity.getAddress());
        } catch (Exception e) {
            logger.error("create node failure", e);
        }
    }

    /**
     * 将注册的节点信息存入mongoDB
     * @param zkRegisterEntityList
     */
    public void insertDataToMongoBatch(List<ZkRegisterEntity> zkRegisterEntityList){
        //1.根据ip清除旧数据
        mongoTemplate.remove(Query.query(Criteria.where("address").is(zkRegisterEntityList.get(0).getAddress())),ZkRegisterEntity.class);
        //2.插入新数据
        mongoTemplate.insert(zkRegisterEntityList,ZkRegisterEntity.class);
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        if (watchedEvent.getState() == Event.KeeperState.SyncConnected){
            latch.countDown();
        }
    }
}
