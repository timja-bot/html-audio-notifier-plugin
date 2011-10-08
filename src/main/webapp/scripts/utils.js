
function toAbsoluteUrl(rootUrl, url) {
    return url.indexOf("://") != -1
        ? url
        : rootUrl + "plugin/html-audio-notifier/sounds/" + url;
}
