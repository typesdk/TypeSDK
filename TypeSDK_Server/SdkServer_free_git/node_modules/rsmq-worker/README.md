![RSMQ-Worker](https://trello-attachments.s3.amazonaws.com/5481963992d9ba3848568a1b/600x194/61b3b6117eeb0881c919c6996adb2620/rsmq_worker_small.png)

[![Build Status](https://secure.travis-ci.org/mpneuried/rsmq-worker.png?branch=master)](http://travis-ci.org/mpneuried/rsmq-worker)
[![Build Status](https://david-dm.org/mpneuried/rsmq-worker.png)](https://david-dm.org/mpneuried/rsmq-worker)
[![NPM version](https://badge.fury.io/js/rsmq-worker.png)](http://badge.fury.io/js/rsmq-worker)

[![Join the chat at https://gitter.im/mpneuried/rsmq-worker](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/mpneuried/rsmq-worker?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

Helper to simply implement a worker [RSMQ ( Redis Simple Message Queue )](https://github.com/smrchy/rsmq).

[![NPM](https://nodei.co/npm/rsmq-worker.png?downloads=true&stars=true)](https://nodei.co/npm/rsmq-worker/)

## Install

```sh
  npm install rsmq-worker
```

## Initialize

```js
  new RSMQWorker( queuename, options );
```


**Example:**

```js
  var RSMQWorker = require( "rsmq-worker" );
  var worker = new RSMQWorker( "myqueue" );

  worker.on( "message", function( msg, next ){
  	// process your message
  	next()
  });

  // optional error listeners
  worker.on('error', function( err, msg ){
      console.log( "ERROR", err, msg.id );
  });
  worker.on('exceeded', function( msg ){
      console.log( "EXCEEDED", msg.id );
  });
  worker.on('timeout', function( msg ){
      console.log( "TIMEOUT", msg.id, msg.rc );
  });

  worker.start();
```

**Config** 

- **queuename**: *( `String` required )* The queuename to pull the messages
- **options** *( `Object` optional )* The configuration object
	- **options.interval**: *( `Number[]` optional; default = `[ 0, 1, 5, 10 ]` )* An Array of increasing wait times in seconds
	- **options.maxReceiveCount**: *( `Number` optional; default = `10` )* Receive count until a message will be exceeded
	- **options.invisibletime**: *( `Number` optional; default = `30` )* A time in seconds to hide a message after it has been received.
	- **options.defaultDelay**: *( `Number` optional; default = `1` )* The default delay in seconds for for sending new messages to the queue.
	- **options.autostart**: *( `Boolean` optional; default = `false` )* Autostart the worker on init
	- **options.timeout**: *( `Number` optional; default = `3000` )* Message processing timeout in `ms`. So you have to call the `next()` method of `message` at least after e.g. 3000ms. If set to `0` it'll wait until infinity.
	- **options.customExceedCheck**: *( `Function` optional; )* A custom function, with the raw message *(see message format)* as argument to build a custom exceed check. If you return a `true` the message will not exceed. On return `false` the regular check for `maxReceiveCount` will be used.
	- **options.alwaysLogErrors**: *( `Boolean` optional; default = `false` )* An error will be logged to the console even if an error listener has been attached.
	- **options.rsmq**: *( `RedisSMQ` optional; default = `null` )* A already existing rsmq instance to use instead of creating a new client
	- **options.redis**: *( `RedisClient` optional; default = `null` )* A already existing redis client instance to use if no `rsmq` instance has been defined 
	- **options.redisPrefix**: *( `String` optional; default = `""` )* The redis prefix/namespace for rsmq if no `rsmq` instance has been defined. This has to match the option `ns` of RSMQ.
	- **options.host**: *( `String` optional; default = `"localhost"` )* Host to connect to redis if no `rsmq` or `redis` instance has been defined 
	- **options.port**: *( `Number` optional; default = `6379` )* Port to connect to redis if no `rsmq` or `redis` instance has been defined 
	- **options.options**: *( `Object` optional; default = `{}` )* Options to connect to redis if no `rsmq` or `redis` instance has been defined 


## Raw message format

A message ( e.g. received by the event `data` or `customExceedCheck` ) contains the following keys:

- **msg.message** : *( `String` )* The queue message content. You can use complex content by using a stringified JSON.
- **msg.id** : *( `String` )* The rsmq internal message id
- **msg.sent** : *( `Number` )* Timestamp of when this message was sent / created.
- **msg.fr** : *( `Number` )* Timestamp of when this message was first received.
- **msg.rc** : *( `Number` )* Number of times this message was received.

## Methods

### `.start()`

If you haven't defined the config `autostart` to `true` you have to call the `.start()` method.

**Return**

*( Self )*: The instance itself for chaining.

### `.stop()`

Just stop the receive interval.
This will not cut the connection to rsmq/redis.
If you want you script to end call `.quit()`

**Return**

*( Self )*: The instance itself for chaining.

### `.send( msg [, delay ][, cb ] )`

Helper function to simply send a message in the configured queue.

**Arguments**

* `msg` : *( `String` required )*: The rsmq message. In best practice it's a stringified JSON with additional data.
* `delay` : *( `Number` optional; default = `0` )*: The message delay to hide this message for the next `x` seconds.
* `cb` : *( `Function` optional )*: An optional callback to get a secure response for a successful send.

**Return**

*( Self )*: The instance itself for chaining.

### `.del( id [, cb ] )`

Helper function to simply delete a message after it has been processed.

**Arguments**

* `id` : *( `String` required )*: The rsmq message id.
* `cb` : *( `Function` optional )*: A optional callback to get a secure response for a successful delete.

**Return**

*( Self )*: The instance itself for chaining.

### `.changeInterval( interval )`

Change the interval timeouts in operation.

**Arguments**

* `interval` : *( `Number|Array` required )*: The new interval.

**Return**

*( Self )*: The instance itself for chaining.

### `.quit()`

Stop the worker and close the connection.
After this it's no longer possible to reuse the worker-instance.
It's just intended to kill all timers and connections so your script will end.

## Events

### `message`

Main event to catch and process a message.
If you do not set a handler for this Event nothing will happen.

**Example:**

```js
worker.on( "message", function( message, next, msgid ){
	// process message ... 
	next();
});
```

**Arguments** 

- **message** : *( `String` )* The queue message content to process. You can use complex content by using a stringified JSON.
- **next** : *( `Function` )* A function you have to call when your message has been processed.  
  **Arguments** 
  * `delete`: *( `Boolean|Error` optional; default = true )* `Error`: If you return an error it will emitted as an error event; `Boolean`: It's possible to prevent the worker from auto-delete the message on end. This is useful if you want to pop up a message multiple times. To implement this, please check the config `options.customExceedCheck`
- **msgid** : *( `String` )* The message id. This is useful if you want to delete a message manually.

### `ready`

Fired until the worker is connected to rsmq/redis and has been initialized with the given queuename.

### `data`

The raw event when a message has been received.

**Arguments** 

- **msg** : *( `String` )* The raw rsmq message. ( See section Raw message format )

### `deleted`

Fired after a message has been deleted.

**Arguments** 

- **id** : *( `String` )* The rsmq message id

### `exceeded`

Fired after a message has been exceeded and immediately will be deleted.

**Arguments** 

- **msg** : *( `String` )* The raw rsmq message. ( See section Raw message format )

### `timeout`

Fired if a message processing exceeds the configured timeout.

**Arguments** 

- **msg** : *( `String` )* The raw rsmq message. ( See section Raw message format )

### `error`

Fired if a message processing throws an error.

**Arguments** 

- **err** : *( `Error|Any` )* The thrown error
- **msg** : *( `String` )* The raw rsmq message. ( See section Raw message format )

## Advanced example

This is an advanced example showing some features in action.

```js
	var fs = require( "fs" );
	var RSMQWorker = require( "rsmq-worker" );

	var fnCheck = function( msg ){
		// check function to not exceed the message if the content is `createmessages`
		if( msg.message === "createmessages" ){
			return true
		}
		return false
	}

	
	var worker = new RSMQWorker( "myqueue", {
		interval: [ .1, 1 ],				// wait 100ms between every receive and step up to 1,3 on empty receives
		invisibletime: 2,						// hide received message for 5 sec
		maxReceiveCount: 2,					// only receive a message 2 times until delete
		autostart: true,						// start worker on init
		customExceedCheck: fnCheck	// set the custom exceed check
	});

	// Listen to errors
	worker.on('error', function( err, msg ){
	    console.log( "ERROR", err, msg.id );
	});
	worker.on('timeout', function( msg ){
	    console.log( "TIMEOUT", msg.id, msg.rc );
	});
	
	// handle exceeded messages
	// grab the internal rsmq instance
	var rsmq = worker._getRsmq();
	worker.on('exceeded', function( msg ){
		console.log( "EXCEEDED", msg.id );
		// NOTE: make sure this queue exists
		rsmq.sendMessage( "YOUR_EXCEEDED_QUEUE", msq, function( err, resp ){
			if( err ){
				console.error( "write-to-exceeded-queue", err )
			}
		});
	});

	// listen to messages
	worker.on( "message", function( message, next, id ){
		
		console.log( "message", message );
		
		if( message === "createmessages" ){
			next( false )
			worker.send( JSON.stringify( { type: "writefile", filename: "./test.txt", txt: "Foo Bar" } ) );
			worker.send( JSON.stringify( { type: "deletefile", filename: "./test.txt" } ) );
			return	
		}

		var _data = JSON.parse( message )
		switch( _data.type ){
			case "writefile": 
				fs.writeFile( _data.filename, _data.txt, function( err ){
					if( err ){
						next( err );
					}else{
						next()
					}
				});
				break;
			case "deletefile": 
				fs.unlink( _data.filename, function( err ){
					if( err ){
						next( err );
					}else{
						next()
					}
				});
				break;
		}
		
	});

	worker.send( "createmessages" );
```

## Todos/Ideas

- MORE tests!
- Optional parallel execution. To do multiple receives in parallel.
- Automatic write of exceeded messages to a configured queue.

## Release History
|Version|Date|Description|
|:--:|:--:|:--|
|0.4.2|2016-05-06|Added the `.quit()` function [Issue#11](https://github.com/mpneuried/rsmq-worker/issues/11). Thanks to [Sam Fung](https://github.com/5amfung )|
|0.4.1|2016-04-05|Fixed missing isNumber function|
|0.4.0|2016-03-30|Updated dependencies (especially lodash to 4.x). Fixed a config bug caused by the array merge from `extend` [Issue#7](https://github.com/mpneuried/rsmq-worker/issues/7). Thanks to [Peter Hanneman](https://github.com/timelessvirtues )|
|0.3.8|2015-11-04|Fixed stop behavior. [Pull#5](https://github.com/mpneuried/rsmq-worker/pull/5). Thanks to [Exinferis](https://github.com/exinferis)|
|0.3.7|2015-09-02|Added tests to check the behavior during errors within message processing; Added option `alwaysLogErrors` to prevent console logs if an error event handler was attached. [Issue #3](https://github.com/mpneuried/rsmq-worker/issues/3)|
|0.3.6|2015-09-02|Updated dependencies; optimized readme (thanks to [Tobias Lidskog](https://github.com/tobli) for the [pull #4](https://github.com/mpneuried/rsmq-worker/pull/4))|
|0.3.5|2015-04-27|again ... fixed argument dispatch for `.send()`|
|0.3.4|2015-04-27|fixed argument dispatch for `.send()` and added optional cb for `.del()`|
|0.3.3|2015-03-27|added `changeInterval` to modify the interval in operation|
|0.3.2|2015-02-23|changed default prefix/namespace;|
|0.3.0|2015-02-16|It's now possible to return an error as first argument of `next`. This will lead to an error emit + optimized readme|
|0.2.2|2015-01-27|added option `defaultDelay` and optimized arguments of the `send` method; fixed travis.yml|
|0.2.0|2015-01-27|Added timeout, better error handling and send callback|
|0.1.2|2015-01-20|Reorganized code, added code docs and optimized readme|
|0.1.1|2015-01-17|Added test scripts and optimized repository file list|
|0.1.0|2015-01-16|First working and documented version
|0.0.1|2015-01-14|Initial commit|

[![NPM](https://nodei.co/npm-dl/rsmq-worker.png?months=6)](https://nodei.co/npm/rsmq-worker/)

> Initially Generated with [generator-mpnodemodule](https://github.com/mpneuried/generator-mpnodemodule)

## Other projects

|Name|Description|
|:--|:--|
|[**rsmq**](https://github.com/smrchy/rsmq)|A really simple message queue based on Redis|
|[**rsmq-cli**](https://github.com/mpneuried/rsmq-cli)|a terminal client for rsmq|
|[**rest-rsmq**](https://github.com/smrchy/rest-rsmq)|REST interface for.|
|[**redis-notifications**](https://github.com/mpneuried/redis-notifications)|A redis based notification engine. It implements the rsmq-worker to safely create notifications and recurring reports.|
|[**node-cache**](https://github.com/tcs-de/nodecache)|Simple and fast NodeJS internal caching. Node internal in memory cache like memcached.|
|[**redis-sessions**](https://github.com/smrchy/redis-sessions)|An advanced session store for NodeJS and Redis|
|[**obj-schema**](https://github.com/mpneuried/obj-schema)|Simple module to validate an object by a predefined schema|
|[**connect-redis-sessions**](https://github.com/mpneuried/connect-redis-sessions)|A connect or express middleware to simply use the [redis sessions](https://github.com/smrchy/redis-sessions). With [redis sessions](https://github.com/smrchy/redis-sessions) you can handle multiple sessions per user_id.|
|[**systemhealth**](https://github.com/mpneuried/systemhealth)|Node module to run simple custom checks for your machine or it's connections. It will use [redis-heartbeat](https://github.com/mpneuried/redis-heartbeat) to send the current state to redis.|
|[**task-queue-worker**](https://github.com/smrchy/task-queue-worker)|A powerful tool for background processing of tasks that are run by making standard http requests.|
|[**soyer**](https://github.com/mpneuried/soyer)|Soyer is small lib for serverside use of Google Closure Templates with node.js.|
|[**grunt-soy-compile**](https://github.com/mpneuried/grunt-soy-compile)|Compile Goggle Closure Templates ( SOY ) templates inclding the handling of XLIFF language files.|
|[**backlunr**](https://github.com/mpneuried/backlunr)|A solution to bring Backbone Collections together with the browser fulltext search engine Lunr.js|

## The MIT License (MIT)

Copyright © 2015 Mathias Peter, http://www.tcs.de

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the “Software”), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
