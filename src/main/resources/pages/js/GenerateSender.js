const nomStickerType = "NOM";
const nomSerStickerType = "NOM_SERIAL";
const employeeType = "EMPLOYEE";
const cellType = "CELL";
const orderNumberType = "ORDER_NUMBER";

loadScript("/js/printPreview.js");
loadStyle("/css/sticker-print-preview.css");

function formatVariableToJava(str) {
  if (!str) return str;

  let variableParts = str.split("-");
  let result = "";

  for (var i = 0; i < variableParts.length; i++) {
    result += variableParts[i][0].toUpperCase() + variableParts[i].slice(1);
  }

  return result;
}

async function printSticker() {
    var printerSelector = document.getElementById("input-select-printer");
    var stickerInputBlock = document.getElementById("sticker-input-block");

    if (printerSelector.selectedIndex > 0) {
        var json = "{";
        if (stickerInputBlock.children[0].id.startsWith("input-")) {
            var inputs = stickerInputBlock.children;
            json += "\"SelectPrinter\": \"" + inputs['input-select-printer'].value + "\",";
            json += "\"Sticker\": {";
            for (var i = 0; i < inputs.length; i++) {
                if (inputs[i].id === "input-select-printer") {
                    continue;
                }
                json += "\"" + formatVariableToJava(stickerInputBlock.children[i].id.substring("input-".length)) + "\": \"" + stickerInputBlock.children[i].value + "\"";
                if (i < inputs.length - 1) {
                    json += ", ";
                }
            }
            if (json.endsWith(", ")) {
                json = json.substring(0, json.length - 2);
            }
            json += "}";
        }
        json += "}"
        json = json.replaceAll("\\", "\\\\")
        await fetch(document.URL, {
            method: 'POST',
            body: json,
            headers: {
                'Content-Type': 'application/json'
            }
        });
    } else {
        var printDataMap = { stickers: [] };

        if (window.location.pathname === "/mfstock-generate-nom-sticker") {
            var stickerCount = parseInt(stickerInputBlock.children[2].value.trim());

            if (stickerCount < 1) {
                return;
            }

            printDataMap.stickers.push(buildStickerJsonData(nomStickerType,
                stickerInputBlock.children[1].value,
                stickerInputBlock.children[0].value,
                stickerCount
            ));
        } else if (window.location.pathname === "/mfstock-generate-nom-ser-sticker") {
            var stickerCount = parseInt(stickerInputBlock.children[3].value.trim());

            if (stickerCount < 1) {
                return;
            }

            printDataMap.stickers.push(buildStickerJsonData(nomSerStickerType,
                stickerInputBlock.children[1].value,
                stickerInputBlock.children[0].value,
                stickerInputBlock.children[2].value,
                stickerCount
            ));
        } else if (window.location.pathname === "/mfstock-generate-employee-sticker") {
            printDataMap.stickers.push(buildStickerJsonData(employeeType,
                stickerInputBlock.children[0].value,
                stickerInputBlock.children[1].value,
                stickerInputBlock.children[2].value
            ));
        } else if (window.location.pathname === "/mfstock-generate-cell-sticker") {
            printDataMap.stickers.push(buildStickerJsonData(cellType,
                stickerInputBlock.children[0].value,
                stickerInputBlock.children[1].value
            ));
        } else if (window.location.pathname === "/mfstock-generate-order-number-sticker") {
            var stickerCount = parseInt(stickerInputBlock.children[1].value);

            if (stickerCount < 0) {
                return;
            }

            printDataMap.stickers.push(buildStickerJsonData(orderNumberType,
                stickerInputBlock.children[0].value,
                stickerInputBlock.children[1].value
            ));
        }

        var generateStickerWebPath = window.location.pathname.replace("mfstock-generate", "mfstock-show-print") + "s"

        var response = await fetch(window.location.origin + generateStickerWebPath, {
            method: 'POST',
            body: JSON.stringify(printDataMap),
            headers: {
                'Content-Type': 'application/json'
            }
        });

        openPrintPreview(response, true);
    }
}

async function printSelectedNomenclatures() {
    var printerSelector = document.getElementById("input-select-printer");
    var inputLabels = document.getElementsByClassName("input");
    var selectRows = document.getElementsByClassName("selectRow");

    if (printerSelector.selectedIndex > 0) {
        var printDataIdsMap = [];

        for (var i = 0; i < inputLabels.length; i++) {
            var inputLabel = inputLabels[i];

            if (!containsValueStartWith(inputLabel.classList, "column_")) {
                continue;
            }

            var columnId = parseInt(inputLabel.classList[1].split("_")[1]);
            inputLabel.disabled = "disabled";
            printDataIdsMap.push(columnId);
        }

        for (var i = 0; i < selectRows.length; i++) {
            var selectRow = selectRows[i];
            var colorBuffer = selectRow.parentElement.parentElement.style.background;
            selectRow.parentElement.parentElement.style.background = "#AAAAAA";

            if (!selectRow.checked) {
                selectRow.parentElement.parentElement.style.background = colorBuffer;
                continue;
            }

            for (var j = 0; j < printDataIdsMap.length; j++) {
                var content;
                var contentElement = selectRow.parentElement.parentElement.children[printDataIdsMap[j]];
                if (contentElement.children.length === 0) {
                    content = contentElement.textContent.trim();
                } else {
                    if (contentElement.children[0].tagName === "INPUT") {
                        content = contentElement.children[0].value;
                    }
                }
                console.log(content);
                document.getElementsByClassName("column_" + printDataIdsMap[j])[0].value = content;
            }

            await printSticker();

            selectRow.parentElement.parentElement.style.background = colorBuffer;
        }

        let printFilenameAfterPrintStickers = document.getElementById("printFilenameAfterPrintStickers");
        if (printFilenameAfterPrintStickers.checked) {
            json = "{";
            json += "\"SelectPrinter\": \"" + inputs['input-select-printer'].value + "\",";
            json += "\"Sticker\": {";
            json += "\"OrderNumber\": \"" + document.getElementById("tableFilename").textContent.trim().split(".")[0] + "\", ";
            json += "\"OrderCountCargoSpaces\": 0";
            json += "}"
            json += "}"
            json = json.replaceAll("\\", "\\\\")

            await fetch("/mfstock-generate-order-number-sticker", {
                method: 'POST',
                body: json,
                headers: {
                    'Content-Type': 'application/json'
                }
            });
        }

        for (var j = 0; j < printDataIdsMap.length; j++) {
            var inputForPrintElement = document.getElementsByClassName("column_" + printDataIdsMap[j])[0];
            inputForPrintElement.disabled = "";
            if (inputForPrintElement.hasAttribute("value")) {
                inputForPrintElement.value = inputForPrintElement.getAttribute("value");
            } else {
                inputForPrintElement.value = "";
            }
        }
    } else {
        var printDataMap = { stickers: [] };

        for (var i = 0; i < selectRows.length; i++) {
            if (!selectRows[i].checked) {
                continue;
            }

            var tableRowCells = selectRows[i].parentElement.parentElement.children;

            if (window.location.pathname.startsWith("/mfstock-generate-nom")) {
                var nomCodeColumnIndex = parseInt(inputLabels["input-nom-code"].classList[1].substring(7));
                var nomNameColumnIndex = parseInt(inputLabels["input-nom-name"].classList[1].substring(7));

                if (window.location.pathname === "/mfstock-generate-nom-sticker") {
                    printDataMap.stickers.push(buildStickerJsonData(nomStickerType,
                        tableRowCells[nomCodeColumnIndex].textContent.trim(),
                        tableRowCells[nomNameColumnIndex].textContent.trim(),
                        1
                    ));
                } else if (window.location.pathname === "/mfstock-generate-nom-ser-sticker") {
                    var nomSerialColumnIndex = parseInt(inputLabels["input-nom-serial"].classList[1].substring(7));

                    printDataMap.stickers.push(buildStickerJsonData(nomSerStickerType,
                        tableRowCells[nomCodeColumnIndex].textContent.trim(),
                        tableRowCells[nomNameColumnIndex].textContent.trim(),
                        tableRowCells[nomSerialColumnIndex].textContent.trim(),
                        1
                    ));
                }
            } else if (window.location.pathname === "/mfstock-generate-cell-sticker") {
                var cellAddressColumnIndex = parseInt(inputLabels["input-cell-address"].classList[1].substring(7));
                var cellCodeColumnIndex = parseInt(inputLabels["input-cell-code"].classList[1].substring(7));

                printDataMap.stickers.push(buildStickerJsonData(cellType,
                    tableRowCells[cellAddressColumnIndex] .textContent.trim(),
                    tableRowCells[cellCodeColumnIndex].textContent.trim()
                ));
            }
        }

        var generateStickerWebPath = window.location.pathname.replace("mfstock-generate", "mfstock-show-print") + "s"

        var response = await fetch(window.location.origin + generateStickerWebPath, {
            method: 'POST',
            body: JSON.stringify(printDataMap),
            headers: {
                'Content-Type': 'application/json'
            }
        });

        openPrintPreview(response, true);
    }
}

function buildStickerJsonData(stickerType, param1, param2, param3, param4) {
    var stickerDto = {};

    stickerDto.Type = stickerType;
    var sticker = {};

    if (stickerType === nomStickerType) {
        sticker.NomCode = param1;
        sticker.NomName = param2;
        sticker.NomStickerCopies = param3;
    } else if (stickerType === nomSerStickerType) {
        sticker.NomCode = param1;
        sticker.NomName = param2;
        sticker.NomSerial = param3;
        sticker.NomStickerCopies = param4;
    } else if (stickerType === employeeType) {
        sticker.EmployeeName = param1;
        sticker.EmployeeCode = param2;
        sticker.EmployeePass = param3;
    } else if (stickerType === cellType) {
        sticker.CellAddress = param1;
        sticker.CellCode = param2;
    } else if (stickerType === orderNumberType) {
        sticker.OrderNumber = param1;
        sticker.OrderCountCargoSpaces = param2;
    }

    stickerDto.Sticker = sticker;

    return stickerDto;
}