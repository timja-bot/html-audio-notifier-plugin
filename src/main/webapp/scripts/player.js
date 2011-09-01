var audioElement = document.createElement('audio');

//check if we can play at all?
/*if (audioElement.canPlayType) {
	alert('canPlay');
}*/

// TODO make some use of these guys?
audioElement.addEventListener('ended', function() {
	alert('we\'re done..');
}, false);

audioElement.addEventListener('error', function() {
	alert('failed to play sound..');
}, false);

// TODO can't play remote files?
function playAudio(source) {

	audioElement.setAttribute('src', source);
	
	// preload n' play?
	audioElement.load();
    audioElement.addEventListener('loadeddata', function() {
		this.play();
    }, false);
}
