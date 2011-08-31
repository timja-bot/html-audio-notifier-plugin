var plugin;
var executor = null;


function initializeHtmlAudioClient(htmlAudioNotifier) {
	plugin = htmlAudioNotifier;
	
	// TODO check cookie if we're enabled
	// or contact the backend using a query..

	enableHtmlAudioClient();
}


function enableHtmlAudioClient() {
	stopExecutor();
	executor = new PeriodicalExecuter(poll, 5);
	toggleControls(true);
}


function stopExecutor() {
	if (executor != null) {
		executor.stop();
	}
	executor = null;
}


function poll() {
	// TODO keep the last request-id in cookie or something? don't want to loose it on page-refresh
	plugin.wazzup(function(t) {
		playAudio(t.responseObject());
	});
}


function toggleControls(enabled) {
	document.getElementById('htmlAudioNotifierControl').src = enabled
		? '../images/audio-volume-high.png'
		: '../images/audio-volume-muted.png';
}


function disableHtmlAudioClient() {
	stopExecutor();
	toggleControls(false);
	alert('disable'); // TODO disable the polling & write cooke
}
