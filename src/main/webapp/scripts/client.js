var plugin;
var executor = null;


function initializeHtmlAudioClient(htmlAudioNotifier) {
	plugin = htmlAudioNotifier;
	
	enabled = isEnabled();
	
	if (enabled == null) {
		plugin.isEnabledByDefault(function(t) {
			enableHtmlAudioClient(t.responseObject());
		});
	} else {
		enableHtmlAudioClient(enabled);
	}
}


function isEnabled() {
	val = readCookie("htmlAudioClientEnabled");
	return val == null
		? null
		: val == 'true';
}


function enableHtmlAudioClient(enabled) {
	
	if (enabled) {
		startPolling();
	} else {
		stopPolling();
	}
	
	showEnabledState(enabled)
	storeEnabledState(enabled);
}


function startPolling() {
	stopPolling();
	executor = new PeriodicalExecuter(pollBuildResults, 5);
}


function stopPolling() {
	if (executor != null) {
		executor.stop();
	}
	executor = null;
}


function showEnabledState(enabled) {
	document.getElementById('htmlAudioNotifierControl').className = enabled
		? 'enabled'
		: 'disabled';	
}


function storeEnabledState(enabled) {
	createCookie("htmlAudioClientEnabled", enabled, 30);
}


function pollBuildResults() {
	// TODO keep the last request-id in cookie or something? don't want to loose it on page-refresh
	plugin.wazzup(function(t) {
		playAudio(t.responseObject());
	});
}


function toggleHtmlAudioClient() {
	enableHtmlAudioClient(executor == null);
}
