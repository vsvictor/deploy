var createFileInputButton = function(container, buttonCreator, mode, resultCallback) {

	let button = buttonCreator();
	container.appendChild(button);

	let fileInput = document.createElement('input');

	fileInput.type = 'file';

	fileInput.style.position 	= 'absolute';
	fileInput.style.left 		= '0px';
	fileInput.style.top 		= '0px';
	fileInput.style.width 		= '100%';
	fileInput.style.height 		= '100%';
	fileInput.style.overflow 	= 'hidden';
	fileInput.style.opacity 	= '0';

	fileInput.onchange = function() {
		let fileReader = new FileReader();
    	fileReader.onload = function() {
    		resultCallback(fileReader.result);
    	};
    	if (mode == 'TEXT') {
    		fileReader.readAsText(fileInput.files[0]);
    	} else
		if (mode == 'BINARY') {
			fileReader.readAsArrayBuffer(fileInput.files[0]);
		}
    };

	container.appendChild(fileInput);

}