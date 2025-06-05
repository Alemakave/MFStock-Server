function loadScript(url) {
    var loadedScripts = document.head.getElementsByTagName("script");

    for (var loadedScriptId = 0; loadedScriptId < loadedScripts.length; loadedScriptId++) {
        var loadedScriptUrl = loadedScripts[loadedScriptId].src;
        loadedScriptUrl = loadedScriptUrl.replace(window.location.origin, "");

        console.debug("Check script " + loadedScriptUrl + " === " + url + " : " + (loadedScriptUrl === url));
        if (loadedScriptUrl === url) {
            return;
        }
    }

    console.debug("Loading script: " + url);

    var script = document.createElement('script');
    script.src = url;
    document.head.appendChild(script);
}

function loadStyle(url) {
    var loadedStyle = document.head.getElementsByTagName("link");

    for (var loadedStyleId = 0; loadedStyleId < loadedStyle.length; loadedStyleId++) {
        var loadedStyleUrl = loadedStyle[loadedStyleId].href;
        loadedStyleUrl = loadedStyleUrl.replace(window.location.origin, "");

        console.debug("Check style " + loadedStyleUrl + " === " + url + " : " + (loadedStyleUrl === url));
        if (loadedStyleUrl === url) {
            return;
        }
    }

    console.debug("Loading style: " + url);

    var style = document.createElement("link");
    style.href = url;
    style.rel = "stylesheet";
    document.head.appendChild(style);
}