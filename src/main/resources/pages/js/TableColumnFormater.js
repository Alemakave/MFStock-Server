function markInputBoxWithColumnIds() {
        var headerCells = document.getElementsByClassName("table-header-cell");
        var inputLabels = document.getElementsByClassName("input");
        var labels = document.getElementsByClassName("label");

        for (var i = 0; i < headerCells.length; i++) {
            for (var j = 0; j < inputLabels.length; j++) {
                if (!inputLabels[j].id.startsWith("input-nom")) {
                    continue;
                }

                var headerCellText = headerCells[i].textContent.toLowerCase().trim();
                var labelText = labels[j].textContent.toLowerCase().trim().replace(":", "");
                var inputLabelText = inputLabels[j].placeholder.toLowerCase();

                if (
                    (headerCellText && inputLabelText && headerCellText == inputLabelText)
                     || labelText == headerCellText
                ) {
                    inputLabels[j].classList.add("column_" + i);
                    break;
                }
            }
        }
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

function closeTable() {
    window.location.replace(window.location);
}

function containsValueStartWith(stringArray, stringPart) {
    for (var i = 0; i < stringArray.length; i++) {
        var stringFromStringArray = stringArray[i];

        if (stringFromStringArray.startsWith(stringPart)) {
            return true;
        }
    }

    return false;
}