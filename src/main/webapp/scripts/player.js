
/**
 * Simple html audio player. Capable of playing multiple sounds by putting them on a queue, and popping
 * them off whenever the audio-element is available.
 */
var AudioPlayer = Class.create();
AudioPlayer.prototype = {
	
	initialize: function() {
		this.queue = Array();
		this.playing = false;
		this.player = document.createElement('audio');
		
		// start playing only when completely loaded
		this.player.addEventListener('canplaythrough', function() {
			console.log('> playing ' + this.getAttribute('src')); // TODO remove me
			this.play();
		}, false);
		
		// register event-listeners for playing enqueued sounds
		var that = this;
		['emptied', 'ended', 'error', 'abort'].each(function(e) {
			that.player.addEventListener(e,
				function() { audioEndedListener(that) },
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
		
		this.player.setAttribute('src', this.queue.shift());
		this.player.load();
		this.playing = true;
	}
};


/**
 * Event-listener that records the end-of-playing and starts playing enqueued sounds.
 */
function audioEndedListener(that) {
	that.playing = false;
	that.playNext();
}
