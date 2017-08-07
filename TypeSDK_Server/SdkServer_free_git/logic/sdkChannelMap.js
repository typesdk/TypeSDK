/**
 * Created by TypeSDK 2016/10/10.
 */

var channelmap = {};
channelmap['1'] = {name: "UC"};
channelmap['3'] = {name: "360"};
channelmap['5'] = {name: "Baidu"};
channelmap['6'] = {name: "YouKu"};
channelmap['7'] = {name: "XiaoMi"};
channelmap['9'] = {name: "HuaWei"};
channelmap['10'] = {name: "Oppo"};
channelmap['14'] = {name: "Vivo"};
channelmap['22'] = {name: "WanDouJia"};
channelmap['47'] = {name: "DangLe"};


var channelaliasmap = {};
channelaliasmap['uc'] = {name: "UC"};
channelaliasmap['360'] = {name: "360"};
channelaliasmap['baidu'] = {name: "Baidu"};
channelaliasmap['youku'] = {name: "YouKu"};
channelaliasmap['xiaomi'] = {name: "XiaoMi"};
channelaliasmap['huawei'] = {name: "HuaWei"};
channelaliasmap['oppo'] = {name: "Oppo"};
channelaliasmap['vivo'] = {name: "Vivo"};
channelaliasmap['wandoujia'] = {name: "WanDouJia"};
channelaliasmap['dangle'] = {name: "DangLe"};

var actionmap = {};

actionmap['login'] = {name: "Login"};
actionmap['pay'] = {name: "Pay"};

exports.gamemap = gamemap;
exports.channelmap = channelmap;
exports.channelaliasmap = channelaliasmap;
exports.actionmap = actionmap;

