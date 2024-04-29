async function print() {
    let orderNumber = document.getElementById("input-order-number").value;
    let orderCountCargoSpaces = parseInt(document.getElementById("input-order-count-cargo-spaces").value)
    fetch(document.URL, {
        method: 'POST',
        body: JSON.stringify({
            orderNumber: orderNumber,
            orderCountCargoSpaces: orderCountCargoSpaces
        }),
        headers: {
            'Content-Type': 'application/json'
        }
    });
}

async function mfstockGenerateEmployeeSticker() {
    await print();
}