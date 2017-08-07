(function (root, factory) {
    if (typeof define === 'function' && define.amd) {
        // AMD
        define('fclone', [], factory);
    } else if (typeof module === 'object' && module.exports) {
			  //node
        module.exports = factory();
    } else {
        // Browser globals (root is window)
        root.fclone = factory();
    }
}(this, function () {
  "use strict";

var _typeof = typeof Symbol === "function" && typeof Symbol.iterator === "symbol" ? function (obj) { return typeof obj; } : function (obj) { return obj && typeof Symbol === "function" && obj.constructor === Symbol ? "symbol" : typeof obj; };

function fclone(obj, refs) {
  if (!obj || "object" !== (typeof obj === "undefined" ? "undefined" : _typeof(obj))) return obj;

  if (obj instanceof Date) {
    return new Date(obj);
  }

  if (typeof Buffer !== 'undefined' && Buffer.isBuffer(obj)) {
    return new Buffer(obj);
  }

  if (!refs) {
    refs = [];
  }

  var index = refs.indexOf(obj);

  if (~index) {
    return refs[index];
  }

  if (Array.isArray(obj)) {
    refs[refs.length] = obj;

    var _l = obj.length;
    var i = -1;
    var _copy = [];

    while (_l > ++i) {
      var _index = refs.indexOf(obj);
      _copy[i] = ~_index ? refs[_index] : fclone(obj[i], refs);
    }

    return _copy;
  }

  refs[refs.length] = obj;
  var copy = {};

  if (obj instanceof Error) {
    copy.name = obj.name;
    copy.message = obj.message;
    copy.stack = obj.stack;
  }

  var keys = Object.keys(obj);
  var l = keys.length;

  while (l--) {
    var k = keys[l];
    var _index2 = refs.indexOf(obj);
    copy[k] = ~_index2 ? refs[_index2] : fclone(obj[k], refs);
  }

  return copy;
}
  return fclone
}));