'use strict';

var expect = require('chai').expect;
var node_path = require('path');
var fs = require('fs');
var sync = require('../');
var tmp = require('tmp');

var root = node_path.join(__dirname, 'fixtures');

var cases = [
  {
    desc: 'should not corrupts binary files',
    file: 'icon.png'
  }
];


describe("fs.copy()", function(){
  cases.forEach(function (c) {
    var i = c.only
      ? it.only
      : it;

    function run (noOptions) {
      i(c.desc, function (done) {
        tmp.dir(function (err, dir) {
          if (err) {
            expect('failed to create tmp dir').to.equal('');
            return done()
          }

          var file = node_path.join(root, c.file);
          var tmp_file = node_path.join(root, c.file);

          sync.copy(file, tmp_file);
          expect(fs.readFileSync(file).toString()).to.equal(fs.readFileSync(tmp_file).toString());
          done()
        })
      });
    }

    run();
  });
});