async function mfstockGenerateEmployeeSticker() {
    let employeeName = document.getElementById("input-employee-address").value;
    let employeeCode = document.getElementById("input-employee-code").value;
    let employeePass = document.getElementById("input-employee-code").value;
    fetch(document.URL, {
        method: 'POST',
        body: JSON.stringify({
            employeeName: employeeName,
            employeeCode: employeeCode,
            employeePass: employeePass
        }),
        headers: {
            'Content-Type': 'application/json'
        }
    });
}