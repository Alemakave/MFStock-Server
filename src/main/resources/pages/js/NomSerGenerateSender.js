async function mfstockGenerateNomSerSticker() {
    let nomName = document.getElementById("input-nom-name").value;
    let nomCode = document.getElementById("input-nom-code").value;
    let nomSer = document.getElementById("input-nom-serial").value;
    let stickerCopies = document.getElementById("input-sticker-copies").value;
    fetch(document.URL, {
        method: 'POST',
        body: JSON.stringify({
            NomCode: nomCode,
            NomName: nomName,
            NomSerial: nomSer,
            StickerCopies: stickerCopies
        }),
        headers: {
            'Content-Type': 'application/json'
        }
    });
}