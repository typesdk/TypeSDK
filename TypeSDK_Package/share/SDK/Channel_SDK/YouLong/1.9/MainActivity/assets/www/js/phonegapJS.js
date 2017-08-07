document.addEventListener("deviceready",onDeviceReadyTodo, false);
function onDeviceReadyTodo() {
	//立即执行资源加载
	loadingVersion();
	// Register the event listener
	document.addEventListener("backbutton", onBackKeyDown, false);
}

function onBackKeyDown(){
	if(hasPopup()) return;
	var currentPage = document.URL;
	//console.log("currentPage: " +currentPage);
	collectTraceInfo("currentPage: " +currentPage);
	if("file:///android_asset/www/index.html"==currentPage || "file:///android_asset/www/index.html#payment"==currentPage
		|| "file:///android_asset/www/index.html#success"==currentPage
		|| "file:///android_asset/www/index.html#success_async"==currentPage
		|| "file:///android_asset/www/index.html#error"==currentPage){
		try{
			//clientApp.close();
			collectTraceInfo("normal call notifyCustomer()");
			notifyCustomer();
		}catch(e){
			collectTraceInfo("exception call navigator.app.exitApp()");
			navigator.app.exitApp();
		}
		
	}else{
		if(navigator){
			//console.log("phoneGap back...");
			collectTraceInfo("phoneGap back...");
			navigator.app.backHistory();
		}else{
			//console.log("history back...");
			collectTraceInfo("history back...");
			goback();
		}
	}
}


function collectTraceInfo(msg){
	if(typeof(testMode)!="undefined"&&testMode){
		console.log(msg);
	}
	var d = new Date();
	var time = d.pattern("yyyy-MM-dd HH:mm:ss");
	if(typeof(clientApp)!='undefined'){
		clientApp.pushLog(time+" "+"console "+msg+"\n");
	}else{
		console.log("clientApp is undefined collectTraceInfo:"+msg);
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

/*
 * 设置手机短信验证码
 * */
function setSMS(msgCode){
	var smsCode=document.getElementById('mobileValidateCode');
	if(smsCode){
		smsCode.value=msgCode;
		if(typeof('setSubmitButtonValid') != 'funtion'){
			//判断此函数是否定义，已定义则执行，并检测按纽是否可以点击
			setSubmitButtonValid();
		}
	}
}
