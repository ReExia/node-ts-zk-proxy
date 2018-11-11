import express from 'express';
import log4js, { Logger } from 'log4js';
import Zookeeper, { Client, Stat, Exception } from 'node-zookeeper-client';
import httpProxy from 'http-proxy';
import { Server } from 'http';
import ZkRegisterEntity from '../entity/zk-register-entity';

/**
 * 测试
 */
export default class ProxyController {

    public router: express.Router;
    public logger: Logger;
    public zkClient: Client;
    public proxy: any;

    constructor() {
        //注入日志
        this.logger = log4js.getLogger('default');
        //todo 之后换成配置文件形式 注入zookeeper客户端
        let CONNECTION_STRING = '192.168.11.134:2181';
        this.zkClient = Zookeeper.createClient(CONNECTION_STRING);
        this.zkClient.connect();

        //代理
        this.proxy = httpProxy.createProxyServer();
        this.proxy.on('error', (err: Error, req: any, res: any) => {
            res.end('proxy error ,' + err.message);
        });
        //注入 router
        this.router = express.Router();
        //写入methods
        this.routes();
    }

    /**
     * 路由
     */
    public routes(): void {

        //所有的get方法
        this.router.get('*', (req, res, next) => {
            this.sendResponse(req, res, next);
        });

        //所有的post方法
        this.router.post('*', (req, res, next) => {
            this.sendResponse(req, res, next);
        });

        //所有的put方法
        this.router.put('*', (req, res, next) => {
            this.sendResponse(req, res, next);
        });

        //所有的delete方法
        this.router.delete('*', (req, res, next) => {
            this.sendResponse(req, res, next);
        })
    }

    /**
     * 转发
     * @param req 
     * @param res 
     * @param next 
     */
    public sendResponse(req: any, res: any, next: any): void {
        // 去除域名剩下的部分 originalUrl => /blog/user/user1
        let originalUrl = req.originalUrl;
        //取请求服务名称
        let serviceName = req.get('Service-Name');

        //取app根节点名称
        let requestNodeRoot = '/' + originalUrl.split('/')[1];

        let servicePath = requestNodeRoot + '/' + serviceName;

        this.zkClient.exists(servicePath, (error: Error | Exception, stat: Stat) => {
            if (error) {
                this.logger.error("zk exists error", error);
                res.end('zk exists error');
            }
            //返回状态不为空,取值
            if (null != stat && undefined != stat) {
                //子代节点
                this.zkClient.getChildren(servicePath, (error: Error | Exception, children: string[], stat: Stat) => {
                    if (error) {
                        this.logger.error('get %s children nodes error', servicePath);
                        res.end('get children nodes error, servicePath is :' + servicePath);
                    }
                    if (null != children && undefined != children && 0 != children.length) {
                        let size = children.length;
                        //只有一个节点
                        if (1 == size) {
                            let nodePath = servicePath + '/' + children[0];
                            this.zkClient.getData(nodePath, (error, data) => {
                                if (error) {
                                    this.logger.error('get node error, nodePath is :' + nodePath);
                                    res.end('get %s node error');
                                }
                                let node = JSON.parse(Buffer.from(data).toString()) as ZkRegisterEntity;
                                //console.log(node.address);
                                this.proxy.web(req, res, {
                                    target: 'http://' + node.address
                                })
                            });
                        } else {
                            //如果有多个节点，随机取值
                            let nodePath = servicePath + '/' + children[parseInt(Math.random() * size + '')];
                            this.zkClient.getData(nodePath, (error, data) => {
                                if (error) {
                                    this.logger.error('get node data error, nodePath is :' + nodePath);
                                    res.end('get %s node error');
                                }
                                let node = JSON.parse(Buffer.from(data).toString())  as ZkRegisterEntity;
                                //console.log(node.address);
                                this.proxy.web(req, res, {
                                    target: 'http://' + node.address
                                })
                            });
                        }
                    } else {
                        res.end('未查询到 [%s] 服务子节点', serviceName);
                    }
                });

            } else {
                res.end('请求服务不存在');
            }
        });
    }

}

