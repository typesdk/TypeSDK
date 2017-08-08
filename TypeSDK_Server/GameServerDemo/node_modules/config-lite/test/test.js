var should = require('should');
var path = require('path');

describe('default config', function () {
  it('default', function () {
    require('..').name.should.be.exactly('config/default');
    delete require.cache[path.join(__dirname, '../index.js')];
  });
  it('production', function () {
    process.env.NODE_ENV = 'production';
    require('..').name.should.be.exactly('config/production');
    delete require.cache[path.join(__dirname, '../index.js')];
    delete process.env.NODE_ENV;
  });
  it('test', function () {
    process.env.NODE_ENV = 'test';
    require('..').name.should.be.exactly('config/test');
    delete require.cache[path.join(__dirname, '../index.js')];
    delete process.env.NODE_ENV;
  });
  it('CONFIG_BASEDIR', function () {
    process.env.NODE_ENV = 'test';
    process.env.CONFIG_BASEDIR = path.join(__dirname, 'config2');
    require('..').name.should.be.exactly('config/test');
    delete require.cache[path.join(__dirname, '../index.js')];
    delete process.env.NODE_ENV;
    delete process.env.CONFIG_BASEDIR;
  });
});

describe('custom config', function () {
  it('default', function () {
    process.env.CONFIG_DIR = 'config2';
    require('..').name.should.be.exactly('config2/default');
    require('..').age.should.be.exactly(100);
    delete require.cache[path.join(__dirname, '../index.js')];
    delete process.env.NODE_ENV;
  });
  it('production', function () {
    process.env.CONFIG_DIR = 'config2';
    process.env.NODE_ENV = 'production';
    require('..').name.should.be.exactly('config2/production');
    require('..').age.should.be.exactly(100);
    delete require.cache[path.join(__dirname, '../index.js')];
    delete process.env.NODE_ENV;
  });
  it('test', function () {
    process.env.CONFIG_DIR = 'config2';
    process.env.NODE_ENV = 'test';
    require('..').name.should.be.exactly('config2/test');
    require('..').age.should.be.exactly(100);
    delete require.cache[path.join(__dirname, '../index.js')];
    delete process.env.NODE_ENV;
  });
  it('CONFIG', function () {
    process.env.NODE_ENV = 'test';
    process.env.CONFIG = '{"name":"CONFIG"}';
    require('..').name.should.be.exactly('CONFIG');
    require('..').age.should.be.exactly(100);
    delete require.cache[path.join(__dirname, '../index.js')];
    delete process.env.NODE_ENV;
    delete process.env.CONFIG;
  });
  it('CONFIG_BASEDIR', function () {
    process.env.CONFIG_DIR = 'config2';
    process.env.NODE_ENV = 'test';
    process.env.CONFIG_BASEDIR = path.join(__dirname, 'config');
    require('..').name.should.be.exactly('config2/test');
    require('..').age.should.be.exactly(100);
    delete require.cache[path.join(__dirname, '../index.js')];
    delete process.env.NODE_ENV;
    delete process.env.CONFIG_BASEDIR;
  });
});