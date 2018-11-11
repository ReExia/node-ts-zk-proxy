package com.salty.provider.core.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * @author 保存注册信息的类
 */
@Document(collection = "zkRegisterEntity")
public class ZkRegisterEntity {

    /**
     * 主键
     */
    @Id
    private String id;

    /**
     * 服务名称
     */
    @Field("serviceName")
    private String serviceName;

    /**
     * 服务所属项目名称
     */
    @Field("appName")
    private String appName;

    /**
     * 请求方式
     */
    @Field("method")
    private String method;

    /**
     * 服务所在的ip地址
     */
    @Field("address")
    private String address;

    /**
     * 请求路径 : /user/list 标注上必须带/
     */
    @Field("requestPath")
    private String requestPath;

    /**
     * 服务状态 1:可用 2:停用
     */
    @Field("status")
    private int status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getRequestPath() {
        return requestPath;
    }

    public void setRequestPath(String requestPath) {
        this.requestPath = requestPath;
    }


    @Override
    public String toString() {
        return "ZkRegisterEntity{" +
                "id='" + id + '\'' +
                ", serviceName='" + serviceName + '\'' +
                ", appName='" + appName + '\'' +
                ", method='" + method + '\'' +
                ", address='" + address + '\'' +
                ", requestPath='" + requestPath + '\'' +
                ", status=" + status +
                '}';
    }
}
