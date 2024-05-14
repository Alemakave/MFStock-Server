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
    fetch(document.URL, {
            method: 'POST',
            body: json,
            headers: {
                'Content-Type': 'application/json'
            }
        });
}