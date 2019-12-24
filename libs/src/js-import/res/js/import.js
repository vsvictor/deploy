var toImport = [];
var imported = [];
var importCallbacks = [];

var importJs = function(urls, callback) {

	if (callback) importCallbacks.push(callback);

	for (let i = 0; i < urls.length; i++) { let url = urls[i];

		if (toImport.includes(url)) continue;
		if (imported.includes(url)) continue;

		toImport.push(url);

		var script = document.createElement('script');

        script.onload = function() {

        	let index = toImport.indexOf(url);
            if (index !== -1) toImport.splice(index, 1);

            imported.push(url);

            while (true) {

            	if (importCallbacks.length == 0) break;

            	if (toImport.length > 0) break;

            	importCallbacks.pop()();

            }

        };

        script.src = url;

        document.head.appendChild(script);

	}

};