var validateFileName;

importJs(

	[
		'js/char.js'
	],

	function() {

		validateFileName = function(string) {

			if (string.length == 0) throw "Filename can't be empty";

        	for (let i = 0; i < string.length; i++) {

        		let char = string[i];

        		if (isLowerCaseLatinLetter(char)) 	continue;
        		if (isDigit(char)) 					continue;
        		if (char == '-') 					continue;

        		else throw "\"" + string + "\" is not a valid filename. Only lowercase latin letters, digits and - are valid characters";

        	}

        };

	}

);

