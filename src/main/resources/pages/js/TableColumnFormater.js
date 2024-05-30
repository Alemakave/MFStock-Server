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