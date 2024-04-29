async function print() {
    let nomName = document.getElementById("input-nom-name").value;
    let nomCode = document.getElementById("input-nom-code").value;
    let stickerCopies = document.getElementById("input-sticker-copies").value;
    await fetch(document.URL, {
        method: 'POST',
        body: JSON.stringify({
            NomCode: nomCode,
            NomName: nomName,
            StickerCopies: stickerCopies
        }),
        headers: {
            'Content-Type': 'application/json'
        }
    });
}

async function mfstockGenerateNomSticker() {
    await print();
}