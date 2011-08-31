var audioElement = document.createElement('audio');

// TODO make some use of these guys?
audioElement.addEventListener('ended', function() {
	alert('we\'re done..');
}, false);

audioElement.addEventListener('error', function() {
	alert('failed to play sound..');
}, false);

// check if we can play at all?
/*if (audioElement.canPlayType) {
	alert('canPlay');
}*/

// TODO can't play remote files?
function playAudio(source) {

	audioElement.setAttribute('src', source);
	
	// simple play
	// audioElement.play();
	
	// preload n' play?
	audioElement.load();
    audioElement.addEventListener('loadeddata', function() {
		this.play();
    }, false);
}
