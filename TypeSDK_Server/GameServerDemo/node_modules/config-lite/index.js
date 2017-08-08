'use strict';

var fs = require('fs');
var resolve = require('resolve');
var yaml = require('js-yaml');
var merge = require('merge-descriptors');
var argv = require('optimist').argv;
var chalk = require('chalk');

var filename = process.env.NODE_ENV || 'default';
var CONFIG_BASEDIR = process.env.CONFIG_BASEDIR || process.env.NODE_CONFIG_BASEDIR || process.cwd();
var CONFIG_DIR = process.env.CONFIG_DIR || process.env.NODE_CONFIG_DIR || 'config';
var CONFIG = merge(JSON.parse(process.env.CONFIG || process.env.NODE_CONFIG || '{}'), argv);

module.exports = {};

try {
  module.exports = loadConfig(filename);
} catch (e) {
  console.error(chalk.red('config-lite load `' + filename + '` failed'));
  console.error(chalk.red(e.stack));
}

try {
  module.exports = merge(module.exports, loadConfig('default'), false);
} catch (e) {
  console.error(chalk.red('config-lite load `default` failed'));
  console.error(chalk.red(e.stack));
}

function loadConfig(filename) {
  var filepath = resolve.sync(filename, {
    basedir: CONFIG_BASEDIR,
    extensions: ['.js', '.json', '.node', '.yaml', '.yml'],
    moduleDirectory: CONFIG_DIR
  });
  if (/\.ya?ml$/.test(filepath)) {
    return merge(CONFIG, yaml.safeLoad(fs.readFileSync(filepath , 'utf8')), false);
  } else {
    return merge(CONFIG, require(filepath), false);
  }
}