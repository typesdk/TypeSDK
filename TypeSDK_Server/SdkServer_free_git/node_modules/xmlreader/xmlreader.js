/*
Copyright (c) 2013 Sam Decrock <sam.decrock@gmail.com>

MIT License

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

var sax = require("sax");

exports.read = function(xmlstring, callback){
	var saxparser = sax.parser(true);
	var rootobject = {};
	var object = rootobject;

	saxparser.onerror = function (err) {
		// an error happened.

		return callback(err);
	};

	saxparser.onopentag = function (node) {
		// opened a tag.  node has "name" and "attributes"

		// create a new object and fill it with a function that returns the attributes:
		var newobject = {
			attributes: function(name){
				return node.attributes;
			}
		};

		// add the parent() function so that we can use it later:
		addParentFunction(newobject, object);

		// add the functions count(), at() and each() to access the nodes as if they were multiple nodes of the same name:
		addCountFunction(newobject);
		addAtFunction(newobject);
		addEachFunction(newobject);

		// check if a node with that name already exists
		if(object[node.name]){
			// we're dealing with objects of the same name, let's wrap them in an array

			// check if there's already an array:
			if(!object[node.name].array){
				// no array, create one and at itself + newobject:
				var firstobject = object[node.name];
				object[node.name] = {};
				object[node.name].array = new Array(firstobject, newobject);
			}else{
				// alreay an array, just add the newobject to it:
				object[node.name].array.push(newobject);
			}

			// add 3 functions to work with that array:
			addCountFunction(object[node.name]);
			addAtFunction(object[node.name]);
			addEachFunction(object[node.name]);
		}else{
			// add the functions count() and at() to access the nodes from the array:
			object[node.name] = newobject;
		}

		// set the current object to the newobject:
		object = newobject;
	};

	saxparser.oncdata = function(cdata){
		// add the function text() to the object to return the cdata value:
		object.text = function(){
			return cdata;
		}
	};

	saxparser.ontext = function (text) {
		// add the function text() to the object to return the text value:
		!object.text ? object.text = function(){
			return text;
		} : null;
	};

	saxparser.onclosetag = function (node) {
		// set the object back to its parent:
		object = object.parent();
	}

	saxparser.onend = function () {
		return callback(null, rootobject);
	};

	// Functions that add functions like count(), at() and parent()
	// We need closures for this:
	function addCountFunction(object){
		if(object.array){
			object.count = function(){
				return object.array.length;
			}
		}else{
			object.count = function(){
				return 1;
			}
		}
	}

	function addAtFunction(object){
		if(object.array){
			object.at = function(index){
				return object.array[index];
			}
		}else{
			object.at = function(index){
				return object;
			}
		}
	}

	function addEachFunction(object){
		if(object.array){
			object.each = function(callback){
				for(var i in object.array){
					callback(i, object.array[i]);
				}
				return;
			}
		}else{
			object.each = function(callback){
				return callback(0, object);
			}
		}
	}



	function addParentFunction(object, parent){
		object.parent = function(){
			return parent;
		}
	}

	// pass the xml string to the awesome sax parser:
	saxparser.write(xmlstring).close();
}