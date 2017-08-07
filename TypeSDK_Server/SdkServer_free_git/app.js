/**
 * Created by TypeSDK 2016/10/10.
 */

var express = require('express');
var path = require('path');
var favicon = require('serve-favicon');
var logger = require('morgan');
var cookieParser = require('cookie-parser');
var bodyParser = require('body-parser');

var sdk = require('./routes/sdkrouters');
var pre = require('./routes/prelogic');

var app = express();

// view engine setup
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'jade');

// uncomment after placing your favicon in /public
//app.use(favicon(path.join(__dirname, 'public', 'favicon.ico')));
app.use(logger('dev'));
app.use(function(req, res, next){
  var channelId = (req.url).split('/')[2];
  var action = (req.url).split('/')[3];

  //TT的支付回调
  if(channelId === '94' && action === 'Pay'){
    req.headers['content-type'] = "application/x-www-form-urlencoded";
  }
  next();
});
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: false }));
app.use(cookieParser());
app.use(express.static(path.join(__dirname, 'public')));

app.use('/',pre);
app.use('/', sdk);

// catch 404 and forward to error handler
app.use(function(req, res, next) {
  var err = new Error('Not Found');
  err.status = 404;
  //next(err);
  res.end('404 Not Found');
});

// error handlers

// development error handler
// will print stacktrace
if (app.get('env') === 'development') {
  app.use(function(err, req, res, next) {
    res.status(err.status || 500);
    //res.render('error', {
    //  message: err.message,
    //  error: err
    //});
    res.end('500 Server Error');
  });
}

// production error handler
// no stacktraces leaked to user
app.use(function(err, req, res, next) {
  res.status(err.status || 500);
  //res.render('error', {
  //  message: err.message,
  //  error: {}
  //});
  res.end('500 Server Error');
});


module.exports = app;
