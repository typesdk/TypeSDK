/**
 * Created by TypeSDK 2016/10/10.
 */

var sync=function(funcs){
	this.debug=false;
	this.funcs=funcs;
	if (typeof(this.funcs)==typeof([])){
		this.run=this.runByArr;
		this.runIndex=1;
	}else{
		this.run=this.runByMap;
	}
};
sync.prototype={
    debug:false,
    funcs:null,
    runIndex:1
};
sync.prototype.runbyMap=function(key){
	if (!key || !this.funcs[key]){
		if (this.debug) console.log('====SYNC==== is done.');
		return;
	}else{
		if (this.debug) console.log('====SYNC==== Task.'+key+' start ');
		var task=this.funcs[key];
		var runnable=this;
		task(function(k){
			if (runnable.debug) console.log('====SYNC==== is going to run Task:'+k);
			runnable.runbyMap(k);
		});
	}
	
};
sync.prototype.runbyArr=function(idx){
	if (!idx){
		idx=1;
	}
	if (idx && idx>=this.funcs.length){		
		if (this.debug) console.log('====SYNC==== is done.');
		return;
	}else{
		if (this.debug) console.log('====SYNC==== Task.'+key+' start ');
		var task=this.funcs[idx];
		var runnable=this;
		task(function(key){
			if (key=='done'){
				if (runnable.debug) console.log('====SYNC==== is done.');
				runnable.runbyArr(runnable.funcs.length-1);
			}else if (key=='err'){
				if (runnable.debug) console.log('====SYNC==== is throwing err');
				runnable.runbyArr(0);
			}else{
				if (runnable.debug) console.log('====SYNC==== is going to run Task:'+(idx+1));
					runnable.runbyArr(idx+1);
			}
		});
	}
};

module.exports=sync;