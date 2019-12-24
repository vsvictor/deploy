var httpRequestWithPassword;

importJs(

	[
		'js/http-client.js',
		'js/password-prompt.js'
	],

	function() {

		httpRequestWithPassword = function(url, method, body, callback, errorCallback) {

			httpRequest(
				url,
				method,
				{
					'Authorization': 'Bearer ' + promptPassword()
				},
				body,
				callback,
				function(status) {
                	if (status == 403) resetPassword();
                	if (errorCallback) errorCallback(status);
                }
			);

		}

	}

);