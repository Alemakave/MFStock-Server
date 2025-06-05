loadScript("/js/GenerateSender.js");

var tableRows = [...document.getElementsByClassName("table-row")];

tableRows.forEach((tableRow) =>
    tableRow.oncontextmenu = function (e) {
        var contextMenu = document.createElement("div");
        contextMenu.style.width = "250px";
        contextMenu.style.padding = "0 6px";
        contextMenu.style.paddingBottom = "6px";
        contextMenu.style.borderRadius = "6px";
        contextMenu.style.backgroundColor = "#2b2a33";
        contextMenu.style.position = "absolute";
        contextMenu.style.top = e.clientY + 3 + "px";
        contextMenu.style.left = e.clientX + 3 + "px";
        contextMenu.style.fontSize = "10pt";
        contextMenu.attributes["rowId"] = tableRow;
        contextMenu.classList.add("contextMenu");

        tableRow.classList.add("selected");

        contextMenu.appendChild(createContextMenuItem("Напечатать наклейку без СНИ",
            async () => {
            var printDataMap = {stickers: []};

            printDataMap.stickers.push(buildStickerJsonData(nomStickerType,
                contextMenu.attributes["rowId"].children[0].textContent.trim(),
                contextMenu.attributes["rowId"].children[1].textContent.trim(),
                1
            ));

            var generateStickerWebPath = "/mfstock-generate-nom-sticker".replace("mfstock-generate", "mfstock-show-print") + "s";

            hideContextMenu();

            var response = await fetch(window.location.origin + generateStickerWebPath, {
                method: 'POST',
                body: JSON.stringify(printDataMap),
                headers: {
                    'Content-Type': 'application/json'
                }
            });

            openPrintPreview(response, true);
        }));

        contextMenu.appendChild(createContextMenuItem("Напечатать наклейку с СНИ",
            async () => {
                var printDataMap = {stickers: []};

                printDataMap.stickers.push(buildStickerJsonData(nomSerStickerType,
                    contextMenu.attributes["rowId"].children[0].textContent.trim(),
                    contextMenu.attributes["rowId"].children[1].textContent.trim(),
                    contextMenu.attributes["rowId"].children[3].textContent.trim(),
                    1
                ));

                var generateStickerWebPath = "/mfstock-generate-nom-ser-sticker".replace("mfstock-generate", "mfstock-show-print") + "s";

                hideContextMenu();

                var response = await fetch(window.location.origin + generateStickerWebPath, {
                    method: 'POST',
                    body: JSON.stringify(printDataMap),
                    headers: {
                        'Content-Type': 'application/json'
                    }
                });

                openPrintPreview(response, true);
            }
        ));
/*
        contextMenu.appendChild(createContextMenuItem("Скопировать"),
            () => {
                navigator.clipboard.write()
            }
        );
*/
        document.body.appendChild(contextMenu);
        return false;
    }
);

function hideContextMenu() {
    var contextMenus = document.getElementsByClassName("contextMenu");

    for (var contextMenuId = 0; contextMenuId < contextMenus.length; contextMenuId++) {
        contextMenus[contextMenuId].attributes["rowId"].classList.remove("selected");
        contextMenus[contextMenuId].remove();
    }
}

function createContextMenuItem(textContent, onclick) {
    var contextMenuItem = document.createElement("div");

    contextMenuItem.textContent = textContent;
    contextMenuItem.style.color = "white";
    contextMenuItem.style.marginTop = "6px";
    contextMenuItem.style.padding = "6px";
    contextMenuItem.style.borderRadius = "6px";
    contextMenuItem.style.backgroundColor = "#2b2a33";
    contextMenuItem.style.cursor = "pointer";
    contextMenuItem.onmouseenter = (e) => e.target.style.backgroundColor = "#52525e";
    contextMenuItem.onmouseout = (e) => e.target.style.backgroundColor = "#2b2a33";
    contextMenuItem.onclick = onclick;

    return contextMenuItem;
}