package com.salty.provider.core.enums;

public enum ZkRegisterEnum {

    /**
     * 存活
     */
    ALIVE(1,"可用"),

    /**
     * 死亡
     */
    DEAD(2,"不可用");

    private int code;

    private String desc;

    ZkRegisterEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode(){
        return this.code;
    }

    public String getDesc(){
        return this.desc;
    }

}
