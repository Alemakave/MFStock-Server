function injectDatabaseFunctions() {
    var dbReloadButton = document.getElementsByClassName("header-element db-function-reload")[0];

    dbReloadButton.attributes.removeNamedItem("href");
    dbReloadButton.onclick = async function() {
        var filler = document.createElement("div");
        filler.classList.add("filler");
        filler.style.height = "100vh";
        filler.style.width = "100vw";
        filler.style.background = "#00000066";
        filler.style.position = "absolute";
        filler.style.top = "0";
        filler.style.left = "0";
        filler.style.alignContent = "center";

        var continueInfo = document.createElement("div");
        continueInfo.classList.add("reloadContainer");
        continueInfo.style.background = "white";
        continueInfo.style.width = "20vw";
        continueInfo.style.height = "10vw";
        continueInfo.style.margin = "15vh auto";
        continueInfo.style.borderRadius = "0.7vw";
        continueInfo.style.filter = "drop-shadow(gray 2px 4px 6px)";
        continueInfo.style.alignContent = "center";
        continueInfo.style.padding = "0.7vw 0";
        continueInfo.style.position = "relative";
        continueInfo.style.top = "-25vh";

        var continueInfoText = document.createElement("div");
        continueInfoText.classList.add("reloadInfo");
        continueInfoText.style.cssText = "text-align: center;";
        continueInfoText.textContent = "Перезагрузка БД";
        continueInfo.appendChild(continueInfoText);

        var loadingElement = document.createElement("div");
        loadingElement.classList.add("reloadingImage");
        loadingElement.style.cssText = "background-image: url(\"/get-image?name=loading.svg\");width: 100px;height: 100px;background-repeat: no-repeat;background-size: contain;transition: all 0.5s ease-in 0s;animation-name: rotate;animation-duration: 2s;animation-iteration-count: infinite;animation-timing-function: linear;margin: 1vw auto;";
        continueInfo.appendChild(loadingElement);

        document.body.appendChild(filler);
        document.getElementsByClassName("filler")[0].appendChild(continueInfo);

        await fetch("/mfstock-reload-db");

        continueInfoText.textContent = "БД перезагружена";
        loadingElement.style.animationName = "";
        loadingElement.style.backgroundImage = "url(\"/get-image?name=loading-complete.svg\")";

        timer(
            null,
            5,
            () => document.getElementsByClassName(filler.className)[0].remove()
        )
    }
}

function timer(func, timeoutSecond, doneFunc) {
    var timerCurrentSecond = 0;
    var timerId = setInterval(() => {
            if (func != null) {
                func(timerCurrentSecond);
            }
            timerCurrentSecond++;
            if (timerCurrentSecond >= timeoutSecond) {
                clearInterval(timerId);
                if (doneFunc != null) {
                    doneFunc();
                }
            }
    }, 1000);
    return timerId;
}