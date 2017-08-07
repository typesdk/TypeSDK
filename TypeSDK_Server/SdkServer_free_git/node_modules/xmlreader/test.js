var xmlreader = require('./xmlreader');

var someXml = 	'<response id="1" shop="aldi">'
			+ 		'This is some other content'
			+		'<who name="james">James May</who>'
			+ 		'<who name="sam">'
			+			'Sam Decrock'
			+			'<location>Belgium</location>'
			+		'</who>'
			+ 		'<who name="jack">Jack Johnsen</who>'
			+		'<games age="6">'
			+			'<game>Some great game</game>'
			+			'<game>Some other great game</game>'
			+		'</games>'
			+		'<title>'
            +			'<![CDATA[Some text between CDATA tags]]>'
            +		'</title>'
			+		'<note>These are some notes</note>'
			+	'</response>'

xmlreader.read(someXml, function (err, res){
	if(err) return console.log(err);

	// use .text() to get the content of a node:
	console.log( res.response.text() );

	// use .attributes() to get the attributes of a node:
	console.log( res.response.attributes().shop );

	console.log("");

	// using the .count() and the .at() function, you can loop through nodes with the same name:
	for(var i = 0; i < res.response.who.count(); i++){
		console.log( res.response.who.at(i).text() );
	}

	console.log("");

	// you can also use .each() to loop through the nodes of the same name:
	res.response.who.each(function (i, who){
		console.log( who.text() );
	});

	console.log("");

	console.log( res.response.who.at(1).text() ) ;
	console.log( res.response.who.at(1).location.text() );

	// you can also use .at() to get to nodes where there's only one of them:
	console.log( res.response.note.at(0).text() );

	console.log("");

	// or loop through them as if they were a series of nodes with the same name:
	res.response.note.each(function (i, note){
		console.log( note.text() );
	});

	console.log("");

	console.log( res.response.title.text() );

	console.log("");

	// you can also get the parent of a node using .parent():
	console.log( res.response.who.at(1).parent().attributes().id ) ;
});


