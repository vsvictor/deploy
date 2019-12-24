var showPopup = function(popupCreator) {

	let popup;

	popup = popupCreator(

		{
			close: function() {
				popup.remove();
			}
		}

	);

	popup.style.position 	= 'fixed';
	popup.style.left 		= '64px';
	popup.style.top 		= '64px';

	document.body.appendChild(popup);

};