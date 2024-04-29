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

        var loadingElement = document.createElement("div");
        loadingElement.style.cssText = "background-image: url(\"/get-image?name=loading.svg\"); width: 100px; height: 100px; background-repeat: no-repeat; background-size: contain;filter: invert(1) drop-shadow(2px 4px 6px black);transition: all 0.5s ease-in;animation-name: rotate;animation-duration: 3s;animation-iteration-count: infinite;animation-timing-function: linear;margin: auto;";
        filler.appendChild(loadingElement);

        document.body.appendChild(filler);
        await fetch("/mfstock-reload-db");
        document.getElementsByClassName("filler")[0].children[0].remove();
        filler.style.alignContent = "";

        var continueInfo = document.createElement("div");
        continueInfo.style.cssText = "background: white;width: 20vw;height: 10vw;margin: auto;border-radius: 2vw;filter: drop-shadow(gray 2px 4px 6px);align-content: center;margin-top: 15vh;";

        var continueInfoText = document.createElement("div");
        continueInfoText.style.cssText = "padding-bottom: 5vw;text-align: center;";
        continueInfoText.textContent = "БД перезагружена";
        continueInfo.appendChild(continueInfoText);

        var continueInfoCloseButton = document.createElement("div");
        continueInfoCloseButton.style.cssText = "width: 60px;height: 24px;line-height: 24px;margin: auto;background: gray;border-radius: 3px;text-align: center;padding: 2px;cursor: pointer;";
        continueInfoCloseButton.classList.add("filler-close-button");
        continueInfoCloseButton.textContent = "ОК (5)";

        var closeButtonTimerId = timer(
            (timerCurrentSecond) => {
                document.getElementsByClassName("filler-close-button")[0].textContent = "ОК (" + (5 - timerCurrentSecond - 1) + ")";
            },
            5,
            () => document.getElementsByClassName(filler.className)[0].remove()
        );

        continueInfoCloseButton.onclick = () => {
            clearInterval(closeButtonTimerId);
            document.getElementsByClassName(filler.className)[0].remove();
        };

        continueInfo.appendChild(continueInfoCloseButton);
        document.getElementsByClassName("filler")[0].appendChild(continueInfo);
    }
}

function timer(func, timeoutSecond, doneFunc) {
    var timerCurrentSecond = 0;
    var timerId = setInterval(() => {
            func(timerCurrentSecond);
            timerCurrentSecond++;
            if (timerCurrentSecond >= timeoutSecond) {
                clearInterval(timerId);
                doneFunc();
            }
    }, 1000);
    return timerId;
}