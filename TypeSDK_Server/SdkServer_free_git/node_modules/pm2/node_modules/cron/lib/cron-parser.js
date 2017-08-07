var _fields = ['seconds', 'minutes', 'hours', 'dayOfMonth', 'month', 'dayOfWeek'];
var _daysInMonth = [30, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31];
var _constraints = [
	[0, 59],
	[0, 59],
	[0, 23],
	[1, 31],
	[1, 12],
	[0, 7]
];
var _predef = {
	'@yearly': '0 0 1 1 *',
	'@monthly': '0 0 1 * *',
	'@weekly': '0 0 * * 0',
	'@daily': '0 0 * * *',
	'@hourly': '0 * * * *'
};
var _aliases = {
	dayOfWeek: {
		sun: 0,
		mon: 1,
		tue: 2,
		wed: 3,
		thu: 4,
		fri: 5,
		sat: 6
	},
	month: {
		jan: 1,
		feb: 2,
		mar: 3,
		apr: 4,
		may: 5,
		jun: 6,
		jul: 7,
		aug: 8,
		sep: 9,
		oct: 10,
		nov: 11,
		dec: 12
	}
};

var _validRegexes = [ validChars, validChars, validChars ];

var validChars = /[\d-,*\/]+/g;
//var validAdditionalChars = /[\dL\#\?-,*\/]+/g;

var CronDate = function(v) {
	var date = (v) ? new Date(v) : new Date();

	//TODO: may add convenience methods here for adds

	return date;
}

var CronParser = function() {
	return {
		fields: {},
		next: function() {
		},

		parse: function(str) {
			function _isWildcard(value) {
				return /\*/.test(value);
			}

			function _getRange(start, stop, step) {
				if (!isNaN(start))
					start = parseInt(start, 10);
				if (!isNaN(stop))
					stop = parseInt(stop, 10);
				if (!isNaN(step))
					step = parseInt(step, 10);

				var values = [];
				for (var i = start; i <= stop; i += step) {
					values.push(i);
				}

				return values;
			}

			function _processValue(value, constraints) {
				var min = constraints[0], max = constraints[1];
				var isWildcard = _isWildcard(value);

				var val;
				if (isWildcard && value.length > 1) {
					//is wildcard but has a step value
					var splitSlash = value.split('/');
					val = _getRange(min, max, splitSlash[1]);
				} else if (isWildcard) {
					//is wildcard which means any

					val = _getRange(min, max, 1);
				} else if (value.indexOf('-') >= 0) {
					var splitRange = value.split('-');
					val = _getRange(splitRange[0], splitRange[1], 1);
				} else {
					//is some basic value or special character ie. L,#
					if (!isNaN(value)) {
						val = [parseInt(value, 10)];
					} else { 
						val = [value];
					}
				}

				return val;
			}

			function _parseField(field, value, constraints) {
				//split by ,
				var splitComma = value.split(',');
				for (var i = 0; i < splitComma.length; i++) {
					var val = splitComma[i];

					//pulled from https://github.com/harrisiirak/cron-parser/blob/master/lib/expression.js#L131
					switch (field) {
						case 'month':
						case 'dayOfWeek':
							var aliases = _aliases[field];

							val = val.replace(/[a-z]{1,3}/gi, function(match) {
								match = match.toLowerCase();

								if (typeof aliases[match] !== undefined) {
									return aliases[match];
								} else {
									throw new Error('Cannot resolve alias "' + match + '"')
								}
							});
							break;
					}

					if (!this.fields[field])
						this.fields[field] = [];
					this.fields[field] = this.fields[field].concat(_processValue(val, constraints));
				}
			}

			var lowerStr = str.toLowerCase();
			if (lowerStr in _predef) {
				str = _predef[lowerStr];
			}

			var split = str.split(' ');
			var sL = split.length;
			if (sL === 1) {
				throw new Error('Couldn\'t parse input.');
			}
			if (sL === 5)
				split.unshift('0');
			for (var i = 0; i < split.length; i++) {
				_parseField.call(this, _fields[i], split[i], _constraints[i]);
			}

			return this;
		}
	};
};

module.exports = CronParser;
