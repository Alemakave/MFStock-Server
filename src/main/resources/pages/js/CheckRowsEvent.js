var checkboxes = document.getElementsByClassName("table-cell");
for (let i = 0; i < checkboxes.length; i++) {
	if (checkboxes[i].childElementCount > 0 && checkboxes[i].children[0].hasAttribute("type") && checkboxes[i].children[0].type == 'checkbox') {
		checkboxes[i].addEventListener('click', (event) => {
			if (event.target.tagName == "DIV") {
				checkboxes[i].children[0].click();
			}
		});

		checkboxes[i].children[0].addEventListener('change', (event) => {
            updateSelectRows();
		});
	}
}

var selectAllInColumnCheckBox = document.getElementsByClassName("selectAllInColumn")[0];
selectAllInColumnCheckBox.addEventListener('change', (event) => {
    var rowCheckBox = document.getElementsByClassName("selectRow");

    for (var i = 0; i < rowCheckBox.length; i++) {
        rowCheckBox[i].checked = selectAllInColumnCheckBox.checked;
    }

    updateSelectRows();
});
selectAllInColumnCheckBox.parentElement.addEventListener('click', (event) => {
    if (event.target.tagName == "DIV") {
        selectAllInColumnCheckBox.click();
    }
});

function updateSelectRows() {
    var rowCheckBox = document.getElementsByClassName("selectRow");

    for (var i = 0; i < rowCheckBox.length; i++) {
        if (rowCheckBox[i].checked) {
            rowCheckBox[i].parentElement.parentElement.style.background = "#bbb";
        } else {
            rowCheckBox[i].parentElement.parentElement.style.background = "";
        }
    }
}