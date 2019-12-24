loop = function(action) {
	try {
		while (true) action()
	} catch (thrown) {
		if (thrown == 'BREAK') return
		else throw thrown
	}
}