var httpRequest = function(url, method, headers, body, callback, errorCallback) {
	var xhr = new XMLHttpRequest();
	xhr.open(method, url, true);
	for (var header in headers) {
		xhr.setRequestHeader(header, headers[header]);
	}
	xhr.onload = function() {
		if (xhr.status == 200) {
			callback(JSON.parse(xhr.responseText));
		} else {
			if (errorCallback) errorCallback(xhr.status);
		}
	};
	if (body instanceof ArrayBuffer) {
		xhr.send(body);
	} else {
		xhr.send(JSON.stringify(body));
	}
};
