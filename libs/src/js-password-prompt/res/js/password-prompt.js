var promptPassword;
var resetPassword;

importJs(
	[
		'js/local-storage.js'
	],
	function() {

		promptPassword = function() {

			return LocalStorage.createIfNull('password', function() {

				let password = prompt("Please enter password");

				if (!password) {
					location.reload();
					throw "Page to be reloaded";
				}

				return password;

			});

		};

		resetPassword = function() {

			LocalStorage.remove('password');

			location.reload();
			throw "Page to be reloaded";

		}

	}
);