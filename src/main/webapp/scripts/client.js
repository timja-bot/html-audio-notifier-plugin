/*
 * Polls the plugin for new sounds to play.
 */
var HtmlAudioNotifierClient = Class.create();
HtmlAudioNotifierClient.prototype = {
	
	initialize: function(plugin, uiElement) {
		this.plugin = plugin;
		this.uiElement = uiElement;
		this.player = new AudioPlayer();
		this.executor = null;
		
		var enabled = this.isEnabled();
		
		if (enabled == null) {
			var that = this;
			this.plugin.isEnabledByDefault(function(t) {
				that.enable(t.responseObject());
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
		this.executor = new PeriodicalExecuter(function() { that.poll(that) }, 1); // TODO 5?
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

	poll: function(client) {
		client.plugin.nextSounds(client.getPrevSoundId(), function(t) {
			var result = t.responseObject();
			
			if (!result) {
				return;
			}
			
			result.sounds.each(function(sound) {
				client.player.enqueue(sound.src);
				client.setPrevSoundId(sound.id);
			});
		});
	},
	
	getPrevSoundId: function() {
		if (this.prevSoundId === undefined) {
			this.prevSoundId = readCookie("htmlAudioClientPrevSound");
		}
		return this.prevSoundId;
	},
	
	setPrevSoundId: function(prevSoundId) {
		this.prevSoundId = prevSoundId;
		createCookie("htmlAudioClientPrevSound", prevSoundId, 1); // TODO expire this sucker muuuch sooner, ~5 minutes?
	}
};
