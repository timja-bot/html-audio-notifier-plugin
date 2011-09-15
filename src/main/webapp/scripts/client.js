/*
 * Polls the plugin for new sounds to play.
 */
var HtmlAudioNotifierClient = Class.create();
HtmlAudioNotifierClient.prototype = {

    initialize: function(url, uiElement) {
        this.url = url;
        this.uiElement = uiElement;
        this.player = new AudioPlayer();
        this.executor = null;

        var enabled = this.isEnabled();

        if (enabled == null) {
            var that = this;

            new Ajax.Request(url + "/isEnabledByDefault", {
                method: 'post',
                onSuccess: function(t) {
                    var enabled = t.responseText.evalJSON(true).enabled;
                    that.enable(enabled);
                },
                onFailure: function() {
                    that.enable(false);
                }
            });
        } else {
            this.enable(enabled);
        }
    },

    isEnabled: function() {
        var val = readCookie("htmlAudioNotificationsEnabled");
        return val == null ? null : val == 'true';
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
        this.executor = new PeriodicalExecuter(function() {
            that.poll(that)
        }, 5);
    },

    stop: function() {
        if (this.executor != null) {
            this.executor.stop();
        }
        this.executor = null;
    },

    showEnabledState: function(enabled) {
        this.uiElement.className = enabled ? 'enabled' : 'disabled';
    },

    storeEnabledState: function(enabled) {
        createCookie("htmlAudioNotificationsEnabled", enabled, 30);
    },

    poll: function(client) {

        new Ajax.Request(client.url + "/next", {
            method: 'post',
            parameters: {
                previous: client.getPreviousNotification()
            },

            onSuccess: function(t) {
                var result = t.responseText.evalJSON(true);

                if (!result) {
                    return;
                }

                client.setPreviousNotification(result.currentNotification);
                result.notifications.each(function(src) {
                    client.player.enqueue(src);
                });
            }
        });
    },

    getPreviousNotification: function() {
        if (this.prevNotification === undefined) {
            this.prevNotification = readCookie("prevHtmlAudioNotification");
        }
        return this.prevNotification;
    },

    setPreviousNotification: function(n) {
        this.prevNotification = n;
        createCookie("prevHtmlAudioNotification", n, 1);
    }
};
