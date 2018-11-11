import express from 'express';
import log4js from 'log4js';
import { format } from 'util';
import ProxyController from './controller/proxy-controller';

export class Server {

    public log4js: any;
    public app: any;

    constructor() {
        this.initLog4js();
        this.initExpress();
    }

    /**
     * 初始化日志
     */
    initLog4js(): void {
        this.log4js = log4js;
        let date = new Date();
        let logFileName = format("%s-%s-%s.log", date.getFullYear(), date.getMonth() + 1, date.getDate());
        this.log4js.configure({
            appenders: {
                out: { type: 'stdout' },//设置是否在控制台打印日志
                info: { type: 'file', filename: format('./logs/%s', logFileName) },
            },
            categories: {
                default: { appenders: ['out', 'info'], level: 'debug' }//去掉'out'。控制台不打印日志
            }
        });
    }

    //初始化express
    initExpress(): void {

        this.app = express();
        //日志
        let logger = log4js.getLogger("default");
        this.app.use(log4js.connectLogger(logger, {
            level: 'info',
            // include the Express request ID in the logs
            //format: (req, res, format) => format(`:remote-addr - ${req.id} - ":method :url HTTP/:http-version" :status :content-length ":referrer" ":user-agent"`)
            format: (req, res, format) => format(`method => [:method] requestUrl => [:url] responseStatus => [:status] responseTime : [${res.responseTime} ms]`)
        }));

        //静态文件
        this.app.use(express.static('public'));
        this.app.use(express.json());
        //大概是express 对ts支持的问题,异常处理..
        // this.app.use((err:any, req:any, res:any, next:any) => {
        //     if (res.headersSent) {
        //         return next(err)
        //     }
        //     res.status(200)
        //     res.render('error', { error: err })
        // });
        //路由
        this.app.all('*', new ProxyController().router);
        this.app.listen(3000);
    }
}

new Server();