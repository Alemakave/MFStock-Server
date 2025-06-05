loadScript("/js/ContextMenu.js");

loadStyle("/css/qr-generator.css");

var prevRange = null;

function getRangeObject(win) {
    win = win || window;
    try {
        return win.getSelection().getRangeAt(0);
    } catch (e) { /*If no text is selected an exception might be thrown*/ }
}

var mousePos = { x: -1, y: -1 };
window.onmousemove = function (e) {
    mousePos.x = e.clientX;
    mousePos.y = e.clientY;
}

window.onmouseup = function(e) {
    var range = getRangeObject();
    if (!e.target.classList.contains("qr-generator-button")) {
        while (document.getElementsByClassName("qr-generator-button").length > 0) {
            document.getElementsByClassName("qr-generator-button")[0].remove();
        }

        if (!e.target.classList.contains("contextMenu") && e.target.parentElement && !e.target.parentElement.classList.contains("contextMenu")) {
            hideContextMenu();
        }
        while (document.getElementsByClassName("selected").length > 0) {
            document.getElementsByClassName("selected")[0].classList.remove("selected");
        }

        if (prevRange !== range && range !== undefined && range.toString().trim().length > 0) {
            if (!e.target.classList.contains("table-cell")) {
                return;
            }

            e.target.classList.add("selected");

            var generateQrButton = document.createElement("div");
            generateQrButton.classList.add("qr-generator-button");
            generateQrButton.style.left = (mousePos.x + document.documentElement.scrollLeft + 10) + "px";
            generateQrButton.style.top = (mousePos.y + document.documentElement.scrollTop + 10) + "px";
            generateQrButton.onclick = function() {
                console.log(e);
                console.log(range);
                generateQrButton.style.width = "150px";
                generateQrButton.style.height = "150px";
                generateQrButton.classList.add("qr-code");
                var qrCode = document.createElement("img");
                if (range.startContainer.nodeName === "#text") {
                    qrCode.src = "/mfstock-generate-qr-code?data=" + range.startContainer.data.trim().replace("#", "%23");
                } else {
                    qrCode.src = "/mfstock-generate-qr-code?data=" + range.startContainer.textContent.trim().replace("#", "%23");
                }
                qrCode.style.width = "100%";
                qrCode.style.height = "100%";
                generateQrButton.append(qrCode);
                generateQrButton.style.backgroundImage = "none";
            }
            document.body.append(generateQrButton);
            prevRange = range;
        }
    }
}