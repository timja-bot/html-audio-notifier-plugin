/*
 * Polls the plugin for new sounds to play.
 */
var HtmlAudioNotifierClient = Class.create();
HtmlAudioNotifierClient.prototype = {

    initialize: function(rootUrl, uiElement) {
        this.rootUrl = rootUrl;
        this.uiElement = uiElement;
        this.player = new AudioPlayer();
        this.enabled = null;

        if (this.isEnabled != null) {
            this.enable(this.isEnabled());
        } else {
            this.enable(false);
            
            var that = this;
            new Ajax.Request(rootUrl + "html-audio/isEnabledByDefault", {
                method: 'post',
                onSuccess: function(t) {
                    var enabled = t.responseText.evalJSON(true).enabled;
                    that.enable(enabled);
                }
            });
        }
    },

    isEnabled: function() {
        if (this.enabled != null) {
            return this.enabled;
        }
        
        var val = readCookie("htmlAudioNotificationsEnabled");
        return val == null ? null : val == 'true';
    },

    toggle: function() {
        this.enable(!this.isEnabled());
    },

    enable: function(enabled) {
        this.showEnabledState(enabled)
        this.storeEnabledState(enabled);
        
        if (enabled) {
            this.schedule(this, true);
        }
    },

    showEnabledState: function(enabled) {
        this.uiElement.className = enabled ? 'isEnabled' : 'isDisabled';
    },

    storeEnabledState: function(enabled) {
        this.enabled = enabled;
        createCookie("htmlAudioNotificationsEnabled", enabled, 30);
    },

    schedule: function(client, immediately) {
        client.clearFailureHandler(client);
        
        setTimeout(function() {
            client.registerFailureHandler(client);
            client.poll(client);
        }, (immediately ? 5 : 50) * 100);
    },
    
    registerFailureHandler: function(client) {
        client.failureHandler = setTimeout(function() {
            client.schedule(client, false);
        }, 60 * 1000);
    },
    
    clearFailureHandler: function(client) {
        if (client.failureHandler != null) {
            clearTimeout(client.failureHandler)
            client.failureHandler = null;
        }
    },
    
    poll: function(client) {
        if (!client.isEnabled()) {
            return;
        }
        
        new Ajax.Request(client.rootUrl + "html-audio/next", {
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
                    client.player.enqueue(toAbsoluteUrl(client.rootUrl, src));
                });
                
                client.schedule(client, result.longPolling);
            },
            
            onFailure: function() {
                client.schedule(client);
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
