function checkAndAppendPreviewContainer() {
    if (!document.getElementById("sticker-print-preview")) {
        var stickerPrintPreview = document.createElement("div");
        stickerPrintPreview.id = "sticker-print-preview";
        stickerPrintPreview.onclick = () => closePrintPreview();
        document.getElementById("content").appendChild(stickerPrintPreview);
    }
}

async function openPrintPreview(response, isShowPrintWindow = false) {
    checkAndAppendPreviewContainer();

    var printPreviewContainer = document.getElementById("sticker-print-preview");
    if (response.ok) {
        var responseContent = await response.text();

        printPreviewContainer.innerHTML += responseContent;

        var uploadBlockData = document.getElementById("upload");

        var stickers = printPreviewContainer.getElementsByClassName("sticker");

        var lastSticker = stickers[stickers.length - 1];

        if (lastSticker.getElementsByClassName("sticker-barcode").length > 0) {
            var lastStickerBarcode = lastSticker.getElementsByClassName("sticker-barcode");
            lastStickerBarcode[lastStickerBarcode.length - 1].onload = function () {
                printPreviewContainer.style.display = "block";
                if (uploadBlockData) {
                    uploadBlockData.style.display = "none";
                }

                if (isShowPrintWindow) {
                    setTimeout(function () {
                        print();
                    }, 200);
                }
            }
        } else {
            printPreviewContainer.style.display = "block";
            if (uploadBlockData) {
                uploadBlockData.style.display = "none";
            }

            if (isShowPrintWindow) {
                setTimeout(function () {
                    print();
                }, 200);
            }
        }
    }
}

function closePrintPreview() {
    var printPreviewContainer = document.getElementById("sticker-print-preview");
    var uploadBlockData = document.getElementById("upload");

    printPreviewContainer.style.display = "none";

    if (uploadBlockData) {
        uploadBlockData.style.display = "";
    }

    while (printPreviewContainer.childElementCount > 0) {
        printPreviewContainer.children[0].remove();
    }
}