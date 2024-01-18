async function mfstockGenerateNomSerSticker() {
    let nomName = document.getElementById("input-nom-name").value;
    let nomCode = document.getElementById("input-nom-code").value;
    let nomParty = document.getElementById("input-nom-party").value;
    let stickerCopies = document.getElementById("input-sticker-copies").value;
    fetch(document.URL, {
        method: 'POST',
        body: JSON.stringify({
            NomCode: nomCode,
            NomName: nomName,
            NomParty: nomParty,
            StickerCopies: stickerCopies
        }),
        headers: {
            'Content-Type': 'application/json'
        }
    });
}