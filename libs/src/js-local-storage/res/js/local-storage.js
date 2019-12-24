var LocalStorage = {

	get: function(key) {
		return localStorage.getItem(key);
	},

	set: function(key, item) {
    	return localStorage.setItem(key, item);
    },

	remove: function(key) {
		localStorage.removeItem(key, null);
	},

	createIfNull: function(key, creator) {
		let item = localStorage.getItem(key);
		if (!item) {
			item = creator();
			localStorage.setItem(key, item);
		}
		return item;
	}

};