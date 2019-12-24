var getUrlParams = function() {

	let urlParams = {};

	var query = window.location.search.substring(1);
	var vars = query.split('&');
	for (var i = 0; i < vars.length; i++) {
		var pair = vars[i].split('=');
		urlParams[pair[0]] = pair[1];
	}

	return(urlParams);

}

var getHashUrlParams = function() {

	let urlParams = {};

	var query = window.location.hash.substring(1);
	var vars = query.split('&');
	for (var i = 0; i < vars.length; i++) {
		var pair = vars[i].split('=');
		urlParams[pair[0]] = pair[1];
	}

	window.location.hash = '';

	return(urlParams);

}