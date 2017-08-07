# urldecode
[![NPM Version](http://img.shields.io/npm/v/urldecode.svg?style=flat)](https://www.npmjs.org/package/urldecode)
[![NPM Downloads](https://img.shields.io/npm/dm/urldecode.svg?style=flat)](https://www.npmjs.org/package/urldecode)

`urldecode` adds try-catch on `decodeURLComponent`, and return `-1` when failing.

## Usage

```js
var decode = require('urldecode')
console.log(decode('http://127.0.0.1:3333/Become%20Efficient%20or%20Die.md'));
// http://127.0.0.1:3333/Become Efficient or Die.md
```

## LICENCE

MIT LICENCE
