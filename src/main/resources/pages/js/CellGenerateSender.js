async function mfstockGenerateCellSticker() {
    let cellAddress = document.getElementById("input-cell-address").value;
    let cellCode = document.getElementById("input-cell-code").value;
    fetch(document.URL, {
        method: 'POST',
        body: JSON.stringify({
            cellAddress: cellAddress,
            cellCode: cellCode
        }),
        headers: {
            'Content-Type': 'application/json'
        }
    });
}