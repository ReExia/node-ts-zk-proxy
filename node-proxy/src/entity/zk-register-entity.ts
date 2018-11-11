export default class ZkRegisterEntity{

    /**
     * 主键
     */
    public id : string | any;

    /**
     * 服务名称
     */
    public serviceName : string | any;

    /**
     * 服务所属项目名称
     */
    public appName : string | any;

    /**
     * 请求方式
     */
    public method : string | any;

    /**
     * 服务所在的ip地址
     */
    public address : string | any;

    /**
     * 请求路径 : /user/list
     */
    public requestPath : string | any;

    /**
     * 服务状态 1:可用 2:停用
     */
    public status : string | any;
    
    
}