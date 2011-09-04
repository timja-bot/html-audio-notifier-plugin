
/*
 * TODO kvakkel in the skaft? 
 */
var HtmlAudioNotifierClient = Class.create();
HtmlAudioNotifierClient.prototype = {
		
	initialize: function(plugin, uiElement) {
		this.plugin = plugin;	// TODO necessary / does it work at all?
		this.uiElement = uiElement;
		this.player = new AudioPlayer();
		this.executor = null;
		
		var enabled = this.isEnabled();
		
		if (enabled == null) {
			this.plugin.isEnabledByDefault(function(t) {
				this.enable(t.responseObject());
			});
		} else {
			this.enable(enabled);
		}
	},
	
	isEnabled: function() {
		var val = readCookie("htmlAudioClientEnabled");
		return val == null
			? null
			: val == 'true';
	},
	
	toggle: function() {
		this.enable(this.executor == null);
	},

	enable: function(enabled) {
		
		if (enabled) {
			this.start();
		} else {
			this.stop();
		}
		
		this.showEnabledState(enabled)
		this.storeEnabledState(enabled);
	},

	start: function() {
		this.stop();
		
		var that = this;
		this.executor = new PeriodicalExecuter(
				function() { that.pollBuildResults(that) },
				5);
	},

	stop: function() {
		if (this.executor != null) {
			this.executor.stop();
		}
		this.executor = null;
	},

	showEnabledState: function(enabled) {
		this.uiElement.className = enabled
			? 'enabled'
			: 'disabled';	
	},

	storeEnabledState: function(enabled) {
		createCookie("htmlAudioClientEnabled", enabled, 30);
	},

	pollBuildResults: function(that) {
		// TODO keep the last request-id in cookie or something? don't want to loose it on page-refresh
			// or base it on something else.. ip/browser-header or something?
			// or just update the cookie each time a new sound is retrieved..
		//that.plugin.wazzup(function(t) {
		//	that.player..enqueue(t.responseObject());
		//});
			// TODO why can't we use 'this' here?
		that.plugin.wazzup(function(t) {
			that.player.enqueue(t.responseObject());
			that.player.enqueue('invalid crap');
			that.player.enqueue(t.responseObject());
			that.player.enqueue('invalid crap');
			that.player.enqueue(t.responseObject());
			that.player.enqueue(t.responseObject());
		});
		that.stop();
	}
};
