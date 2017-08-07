/**
 * Created by TypeSDK 2016/10/10.
 */

var express = require('express');
var multiparty = require('multiparty');

var router = express.Router();

var channelConfig = require('../logic/channelConfig.js');

router.get('/:game/:channel/:action/', function(req, res,next) {
  channelConfig.getChannelsByGameID(req.params.game, function (obj) {
      next();
  });
});

router.post('/:game/:channel/:action/', function(req, res,next) {
    channelConfig.getChannelsByGameID(req.params.game, function (obj) {
        next();
    });
});

module.exports = router;