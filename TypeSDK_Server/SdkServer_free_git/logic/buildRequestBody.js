/**
 * Created by TypeSDK 2016/10/10.
 */
var multiparty = require('multiparty');
var querystring = require('querystring');

function commonParseBody(req, retf) {
    var header = {},
        contentType = "";

    header = req.headers;
    for (key in header) {
        if (key == 'content-type') contentType = header[key];
    }

    switch (true) {
        case /application\/x-www-form-urlencoded/.test(contentType):
            //Body
            buildBody(req, function(obj){
                var t = 0;
                if(typeof obj == 'object'){

                    var tmp;
                    for(var key in obj){
                        if (!tmp) tmp = obj[key];
                        t ++;
                    }
                    if(t != 0){
                        if(t==1 && tmp === ''){
                            console.log(key);
                            try {
                                var tmpobj = querystring.parse(key);
                                if (tmpobj == undefined)
                                {
                                    retf(-1, '空的请求参数', {});
                                }
                                else
                                {
                                    obj = tmpobj;
                                   // retf(0, "解析Body成功", obj)
                                }
                                    }
                            catch(e){
                                    console.log('error:' + e);
                                    retf(-1, '空的请求参数', {});
                                    }
                            retf(0, "解析Body成功", obj);
                        }
                        else
                        {
                            retf(0, "解析Body成功", obj);
                        }
                    }else{
                        //空请求
                        retf(-1, '空的请求参数', {});
                    }
                }
            });
            break;
        case /application\/json/.test(contentType):
            //Body
            buildBody(req, function(obj){
                var t = 0;
                if(typeof obj == 'object'){
                    for(var key in obj){
                        t ++;
                    }
                    if(t != 0){
                        retf(0, "解析Body成功", obj);
                    }else{
                        //空请求
                        retf(-1, '空的请求参数', {});
                    }
                }
            });
            break;
        case /application\/xml/.test(contentType):
            //Buffer 类
            buildBuffer(req, function(str){
                if(typeof str == "string" && str.length > 0){
                    retf(0, '解析Buffer成功', str);
                }else{
                    //空请求
                    retf(-1, '空的请求参数', '');
                }
            });
            break;
        case /text\/plain/.test(contentType):
            //Buffer 类
            buildBuffer(req, function(str){
                if(typeof str == "string" && str.length > 0){
                    retf(0, '解析Buffer成功', str);
                }else{
                    //空请求
                    retf(-1, '空的请求参数', '');
                }
            });
            break;
        case /multipart\/form-data/.test(contentType):
            //表单类
            buildForm(req, function(obj){
                var t = 0;
                if(typeof obj == 'object'){
                    for(var key in obj){
                        t ++;
                    }
                    if(t != 0){
                        retf(0, "解析Form成功", obj);
                    }else{
                        //空请求
                        retf(-1, '空的请求参数', {});
                    }
                }
            });
            break;
        case /application\/x-tar/.test(contentType):
            //Buffer 类
            buildBuffer(req, function(str){
                if(typeof str == "string" && str.length > 0){
                    retf(0, '解析Buffer成功', str);
                }else{
                    //空请求
                    retf(-1, '空的请求参数', '');
                }
            });
            break;
        default:
            retf(-2, '找不到Mime-Type', '');
            break;
    }
}


/**
 * 解析Buffer字符串
 * */
function buildBuffer(req, cb){
    var bufferArr = req._readableState.buffer;
    var str = '';
    if(typeof bufferArr != 'undefined' && bufferArr.length > 0){
        req.on("data", function(trunk){
            str += trunk;
        });
        req.on("end", function(){
            cb(str);
        });
    }else{
        cb(str);
    }
}

/**
 * 解析Form表单
 * */
function buildForm(req, cb){
    var form = new multiparty.Form();
    var formBody = {};
    form.parse(req, function(err, fields, files){
        if (typeof files == "undefined") {
            retObj.v.code = -1;
            retObj.v.msg = "ROUTE ERROR";
            cb(retObj.v);
        }

        Object.keys(fields).forEach(function (name) {
            formBody[name] = fields[name][0];
        });
        cb(formBody);
    });
}

/**
 * 直接获取Body
 * */
function buildBody(req, cb){
    var data = req.body;
    cb(data);
}


exports.commonParseBody =  commonParseBody;
exports.buildBuffer = buildBuffer;
exports.buildForm = buildForm;
exports.buildBody = buildBody;