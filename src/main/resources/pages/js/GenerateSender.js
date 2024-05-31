function formatVariableToJava(str) {
  if (!str) return str;

  let variableParts = str.split("-");
  let result = "";

  for (var i = 0; i < variableParts.length; i++) {
    result += variableParts[i][0].toUpperCase() + variableParts[i].slice(1);
  }

  return result;
}

async function print() {
    var stickerInputBlock = document.getElementById("sticker-input-block");
    var json = "{";
    if (stickerInputBlock.children[0].id.startsWith("input-")) {
        var inputs = stickerInputBlock.children;
        json += "\"SelectPrinter\": \"" + inputs['input-select-printer'].value + "\",";
        json += "\"Sticker\": {";
        for (var i = 0; i < inputs.length; i++) {
            if (inputs[i].id == "input-select-printer") {
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
}

async function printSelectedNomenclatures() {
    var inputLabels = document.getElementsByClassName("input");
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

    var selectRows = document.getElementsByClassName("selectRow");
    for (var i = 0; i < selectRows.length; i++) {
        var selectRow = selectRows[i];
        var colorBuffer =  selectRow.parentElement.parentElement.style.background;
        selectRow.parentElement.parentElement.style.background = "#AAAAAA";

        if (!selectRow.checked) {
            selectRow.parentElement.parentElement.style.background = colorBuffer;
            continue;
        }

        for (var j = 0; j < printDataIdsMap.length; j++) {
            var content;
            var contentElement = selectRow.parentElement.parentElement.children[printDataIdsMap[j]];
            if (contentElement.children.length == 0) {
                content = contentElement.textContent.trim();
            } else {
                if (contentElement.children[0].tagName == "INPUT") {
                    content = contentElement.children[0].value;
                }
            }
            console.log(content);
            document.getElementsByClassName("column_" + printDataIdsMap[j])[0].value = content;
        }

        await print();

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
}