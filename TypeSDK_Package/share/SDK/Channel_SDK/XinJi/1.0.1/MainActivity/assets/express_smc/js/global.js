var baseUrl = expressSmcApp.getBaseUrl();
//var localHtmls = clientApp.loadHtmlFromLocal();
var testMode = expressSmcApp.isTestMode();
var jsVersion = 0;
var htmlVersion = 0;
var cssVersion = 0;
function collectTraceInfo(msg){
	if(typeof(testMode)!="undefined"&&testMode){
		console.log(msg);
	}
	var d = new Date();
	var time = d.pattern("yyyy-MM-dd HH:mm:ss");
	if(typeof(expressSmcApp)!='undefined'){
		expressSmcApp.pushLog(time+" "+"console "+msg+"\n");
	}else{
		console.log("expressSmcApp is undefined collectTraceInfo:"+msg);
	}
}

//退出应用
function closeClientApp(msg){
	try{
		if(typeof collectTraceInfo =='function'){
			collectTraceInfo("closeClientApp msg："+msg);
		}
        if(typeof expressSmcApp != 'undefined'){
        	expressSmcApp.close(msg);
        }
    }catch(e){
        if(typeof collectTraceInfo =='function'){
            collectTraceInfo("closeClientApp error="+e.name+",message="+e.message);
        }
        if(navigator&&navigator['app']){
        	try{
        		navigator.app.exitApp();
        	}catch(b){
        		 if(typeof collectTraceInfo =='function'){
                     collectTraceInfo("exception call navigator.app.exitApp(),reason"+b.message);
                 }
        	}
        }
    }
}

Date.prototype.pattern=function(fmt) {       
    var o = {       
    "M+" : this.getMonth()+1, //月份       
    "d+" : this.getDate(), //日       
    "h+" : this.getHours() == 0 ? 12 : this.getHours(), //小时       
    "H+" : this.getHours(), //小时       
    "m+" : this.getMinutes(), //分       
    "s+" : this.getSeconds(), //秒       
    "q+" : Math.floor((this.getMonth()+3)/3), //季度       
    "S" : this.getMilliseconds() //毫秒       
    };       
    var week = {       
    "0" : "\u65e5",       
    "1" : "\u4e00",       
    "2" : "\u4e8c",       
    "3" : "\u4e09",       
    "4" : "\u56db",       
    "5" : "\u4e94",       
    "6" : "\u516d"      
    };       
    if(/(y+)/.test(fmt)){       
        fmt=fmt.replace(RegExp.$1, (this.getFullYear()+"").substr(4 - RegExp.$1.length));       
    }       
    if(/(E+)/.test(fmt)){       
        fmt=fmt.replace(RegExp.$1, ((RegExp.$1.length>1) ? (RegExp.$1.length>2 ? "\u661f\u671f" : "\u5468") : "")+week[this.getDay()+""]);       
    }       
    for(var k in o){       
        if(new RegExp("("+ k +")").test(fmt)){       
            fmt = fmt.replace(RegExp.$1, (RegExp.$1.length==1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length)));       
        }       
    }       
    return fmt;       
}