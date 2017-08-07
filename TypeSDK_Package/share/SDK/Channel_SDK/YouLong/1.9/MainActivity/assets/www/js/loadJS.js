 var begin_Total_Time=Date.now();
 var begin_Time;
function loadCss(path,id,callback) {
	begin_Time=Date.now();
	if (!path || path.length === 0) {
		throw new Error('argument "path" is required !');
	}
	var head = document.getElementsByTagName('head')[0];
	var link = document.createElement('link');
	link.rel = 'stylesheet';
	link.type = 'text/css';
	if(id){
		link.id = id;
	}
	link.href = path;
	head.appendChild(link);
	
	//判断CSS是否加载完成（创建div#hack4loaded，并添加到页面，然后定时读取div的样式，如果样式是css里面设置的就说明加载成功，此方法不存在跨域问题，所有浏览器都OK。（css里面加上一个样式: #hack4loaded{display:none}））
	var div  = document.createElement('div');
	    div.id = 'hack4loaded';
	    var getStyle = function(name){ //获取css里面设置的元素样式
	                if(div.currentStyle){ // IE/OP
	                        return div.currentStyle[name];
	                }else{ // FF/CM
	                        return div.ownerDocument.defaultView.getComputedStyle(div, null)[name];
	                }
	    };
	document.body.appendChild(div); // 添加到页面
	//创建定时器，读取创建的div的样式是否是已经在css里面设置好的
	var _timer1= setInterval( function() {
	        //console.log(getStyle('display'));
	        if ('none' === getStyle('display')) { //样式和css里面设置的一样，说明加载成功
	                clearInterval( _timer1 ); // 清除定时器
	                clearTimeout( _timer2 );
	                div.parentNode.removeChild(div); //移除div
	                if(callback){
	    				callback();
	    			}
	        }
	}, 50 ),
	// 创建超时定时器，防止css文件加载失败的情况
	_timer2 = setTimeout( function() {
	        clearInterval( _timer1 ); // 清除定时器
	        clearTimeout( _timer2 );
	        if(typeof collectTraceInfo =='function'){
	    		collectTraceInfo('fail to load css');// 这个没加载成功的可能性很大，再就是网速太慢超时了
	    	}
	        //如果这种方式加载CSS失败，就采取原来方式进行加载，保证不由于CSS加载不完成，不能进行业务  add by zsg
	    	var pageTitle=head.getElementsByTagName('title')[0];
	    	head.insertBefore(link,pageTitle.nextSibling);
	    	callback();
	}, 10000 );
}

function loadScript(path,callback) {
	begin_Time=Date.now();
	var head = document.getElementsByTagName('head')[0];
//	var pageTitle=head.getElementsByTagName('title')[0];
	var script = document.createElement('script');
	script.type = 'text/javascript';
	if(script.readyState){//IE
		script.onreadystatechange=function(){
			if(script.readyState=='loaded'||script.readyState=='complete'){
				script.onreadystatechange=null;
				if(callback){
					callback();
				}
				
			}
		};
	}else{//其他浏览器
		script.onload=function(){
			if(callback){
				callback();
			}
		};
	}
	script.src = path;
	head.appendChild(script);
//	head.insertBefore(script,pageTitle.nextSibling);
}

//var load_css=false,load_global=false,load_iscroll=false,load_select=false,load_m=false,load_control=false,load_common=false,load_page=false,load_data=false,load_sdk=false;
var load_css=false,load_AllJS=false;
var isComplete=false;//是否加载main方法
function loadJsAndCssComplete(jsAndCssName){
//	var isLoaded=load_css&&load_global&&load_iscroll&&load_select&&load_m&&load_control&&load_common&&load_page&&load_data&&load_sdk;
	var isLoaded=load_css&&load_AllJS;
	if(typeof collectTraceInfo =='function'){
		collectTraceInfo(jsAndCssName+",loading complete cost time:"+(Date.now()-begin_Time)+"ms,total loaded status="+isLoaded);
	}
	if(isLoaded){
		main(htmlVersion);//加载完成,执行页面渲染
		isComplete=true;//加载和渲染完成
		if(typeof collectTraceInfo =='function'){
			collectTraceInfo("loadJsAndCssComplete cost time:"+(Date.now()-begin_Total_Time)+"ms,total loaded status="+isLoaded);
		}
	}
}



function loadingJsAndCss(){
	//同步加载CSS和JS
	loadCss(baseUrl+"css/base.css?v="+cssVersion,"linkStyle",function(){
        load_css=true;
        loadJsAndCssComplete('base.css');
	});//先加载样式
	
	loadScript(baseUrl+"js/r.js?v="+jsVersion,function(){//再同步加载JS
		load_AllJS=true;
        loadJsAndCssComplete('r.js');
	});

	/*loadScript(baseUrl+"js/s.R.c/global.js?v="+jsVersion,function(){//再同步加载JS
		load_global=true;
        loadJsAndCssComplete('global.js');
	});
	loadScript(baseUrl+"js/s.R.c/iscroll.js?v="+jsVersion,function(){//再同步加载JS
		load_iscroll=true;
		loadJsAndCssComplete('iscroll.js');
	});
	loadScript(baseUrl+"js/s.R.c/select.js?v="+jsVersion,function(){//再同步加载JS
		load_select=true;
		loadJsAndCssComplete('select.js');
	});
	loadScript(baseUrl+"js/s.R.c/m.js?v="+jsVersion,function(){//再同步加载JS
		load_m=true;
        loadJsAndCssComplete('m.js');
	});
	loadScript(baseUrl+"js/s.R.c/control.js?v="+jsVersion,function(){//再同步加载JS
		load_control=true;
        loadJsAndCssComplete('control.js');
	});
	loadScript(baseUrl+"js/s.R.c/common.js?v="+jsVersion,function(){//再同步加载JS
		load_common=true;
        loadJsAndCssComplete('common.js');
	});
	loadScript(baseUrl+"js/s.R.c/page.js?v="+jsVersion,function(){//再同步加载JS
		load_page=true;
        loadJsAndCssComplete('page.js');
	});
	loadScript(baseUrl+"js/s.R.c/data.js?v="+jsVersion,function(){//再同步加载JS
		load_data=true;
        loadJsAndCssComplete('data.js');
	});
	loadScript(baseUrl+"js/s.R.c/sdk.js?v="+jsVersion,function(){//再同步加载JS
		load_sdk=true;
        loadJsAndCssComplete('sdk.js');
	});*/
	
	/*var start = Date.now();
	loadScript(baseUrl+"js/s.R.c/m.js?v="+jsVersion,function(){//再同步加载JS
		console.log("m.js net cost time:"+(Date.now()-start));
		start = Date.now();
		loadScript(baseUrl+"js/s.R.c/control.js?v="+jsVersion,function(){
			console.log("control.js cost time:"+(Date.now()-start));
			start = Date.now();
			loadScript(baseUrl+"js/s.R.c/data.js?v="+jsVersion,function(){
				console.log("data.js cost time:"+(Date.now()-start));
				start = Date.now();
				loadScript(baseUrl+"js/s.R.c/page.js?v="+jsVersion,function(){
					console.log("page.js cost time:"+(Date.now()-start));
					console.log('&&&&&&&&&&&&&&&&&&&&js loaded completed &&&&&&&&&&&&&&&&&&&&');
				});
			});
		});
		
	});
	*/
}

function loadingVersion(){
	//取资源版本	
	try {
		if(typeof collectTraceInfo =='function'){
	    	collectTraceInfo('loadingVersion');// 这个没加载成功的可能性很大，再就是网速太慢超时了
	    }
		var xhr=(!!window.XMLHttpRequest) ? (new XMLHttpRequest)
				: (new ActiveXObject("Microsoft.XMLHTTP"));
		xhr.open("get",baseUrl+ "Version.htm",true);
		xhr.onreadystatechange=function(){
			if(xhr.readyState == 4){
				if(xhr.status >= 200 && xhr.status <300 || xhr.status== 304){
					var r=eval('('+xhr.responseText+')');;
					jsVersion=r.jsVersion;
					htmlVersion=r.htmlVersion;
					cssVersion=r.cssVersion;
					if(typeof collectTraceInfo =='function'){
						collectTraceInfo("jsVersion version:"+jsVersion+",htmlVersion version:"+htmlVersion+",cssVersion version:"+cssVersion);
					}
					loadingJsAndCss();
				}
			}
		};
		xhr.send(null);
	} catch (e) {
		if(typeof collectTraceInfo =='function'){
			collectTraceInfo('loading Version Failure,reason:'+e.message);
		}
	}
}

var isInvokeJsOk=false;
/*
 * 加载服务端数据资源
 * */
function getLocalData(data,isSDK){
	if(isInvokeJsOk){
		return;
	}else if(typeof clientApp != 'undefined'&&clientApp.hasOwnProperty('setInvokeJs')){
		clientApp.setInvokeJs(true);
		isInvokeJsOk=true;
   　 }
	if(typeof collectTraceInfo =='function'){
		collectTraceInfo("getLocalData data="+data+",isSDK="+isSDK+", cost total time="+(Date.now()-begin_Total_Time)+"ms");
	}
    var loadInterval=undefined;//检测间隔时间
    var count=0;
    function interval(){
        if(typeof collectTraceInfo =='function'){
        	collectTraceInfo('getLocalData--interval--isCommplete:'+isComplete);
        }
        if(!isComplete){
            count++;
            if(!loadInterval){
                loadInterval=window.setInterval(interval,200);//每隔200毫秒做一次检测
            }
            if(count>=10){//重试10次还未完成，提示收单异常，请重新再试,并退出应用
                var msg="{\"msg\":\"网络异常,请重新再试!\"}";
                if(loadInterval){//清空检测索引
                    window.clearInterval(loadInterval);
                }
                closeClientApp(msg);
            }
        }else{
            if(loadInterval){//清空检测索引
                window.clearInterval(loadInterval);
            }
			try{
				getData(data,isSDK);
				if(typeof collectTraceInfo =='function'){
					collectTraceInfo("getData success  getLocalData--interval()执行的次数为："+count);
				}
			}catch(e){
				if(typeof collectTraceInfo =='function'){
					collectTraceInfo("getLocalData--interval()执行的次数为："+count+", error name="+e.name+",message="+e.message);
				}
				var msg="{\"msg\":\"参数异常,error:"+e.name+"\"}";
				 closeClientApp(msg);
			}
        }
    }
    interval();
}
//退出应用
function closeClientApp(msg){
	try{
		if(typeof collectTraceInfo =='function'){
			collectTraceInfo("getLocalData fail close APP msg："+msg);
		}
        if(typeof clientApp != 'undefined'){
            clientApp.close(msg);
        }
    }catch(e){
        if(typeof collectTraceInfo =='function'){
            collectTraceInfo("close App error="+e.name+",message="+e.message);
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