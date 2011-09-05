/*
 * Simple html audio player. Capable of playing multiple sounds by putting them on a queue, and popping
 * them off whenever the audio-element is available.
 */
var AudioPlayer = Class.create();
AudioPlayer.prototype = {
	
	initialize: function() {
		this.queue = Array();
		this.playing = false;
		this.player = document.createElement('audio');
		
		var that = this;
		
		// start playing only when completely loaded
		this.player.addEventListener('canplaythrough', function() {
			that.onLoaded();
		}, false);
		
		// register event-listeners for playing enqueued sounds
		['ended', 'error'].each(function(e) {
			that.player.addEventListener(e,
				function() { audioEndedListener(that, e); },
				false);
		});
	},
	
	enqueue: function(source) {
		this.queue.push(source);
		this.playNext();
	},
	
	playNext: function() {
		if (this.playing
				|| this.queue.size() == 0) {
			return;
		}
		
		this.playing = true;
		this.registerTimedRestart();
		this.player.setAttribute('src', this.queue.shift());
		this.player.load();
	},
	
	onLoaded: function() {
		console.log('> playing ' + this.player.getAttribute('src')); // TODO remove me
		this.clearTimedRestart();
		this.player.play();
	},
	
	/*
	 * Restarts playing after x seconds if timer not explicitly cleared, should hopefully catch most
	 * load-errors..
	 */
	registerTimedRestart: function() {
		this.clearTimedRestart();
		
		var that = this;
		this.timedRestart = setTimeout(function() {
			that.playing = false;
			that.playNext();
		}, 20000);
	},
	
	clearTimedRestart: function() {
		if (this.timedRestart) {
			clearTimeout(this.timedRestart);
			this.timedRestart = null;
		}
	}
};


/*
 * Event-listener that records the end-of-playing and starts playing enqueued sounds.
 */
function audioEndedListener(that, reason) {
	that.playing = false;
	that.clearTimedRestart();

	// play next from queue, but make sure the current event has been fully processed.
	setTimeout(function() {
		that.playNext();
	}, 0);
}
