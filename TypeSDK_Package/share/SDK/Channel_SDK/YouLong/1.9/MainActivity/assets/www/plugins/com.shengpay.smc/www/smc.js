cordova.define("com.shengpay.smc.Smc", function(require, exports, module) {var smcExport = {
    showSafeKeyboard : function(onSuccess, onError, params){
        console.log("at showSafeKeyboard");
        cordova.exec(onSuccess, onError, "Smc", "showSafeKeyboard", params);
    },
	encryptParam : function(onSuccess, onError, params){
        console.log("at encryptParam");
        cordova.exec(onSuccess, onError, "Smc", "encryptParam", params);
    }
};

module.exports = smcExport;
});
