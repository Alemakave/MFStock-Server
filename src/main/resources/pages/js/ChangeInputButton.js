const INPUT_STATE = "input";
const UPLOAD_STATE = "upload";

var currentInputType = UPLOAD_STATE;
function changeInputType() {
    if (currentInputType == INPUT_STATE) {
        document.getElementById("input-image").style.display = "block";
        document.getElementById("upload-image").style.display = "none";
        document.getElementById("sticker-block").style.display = "";
        document.getElementById("upload-block").style.display = "none";
        currentInputType = UPLOAD_STATE;
    } else {
        document.getElementById("upload-image").style.display = "block";
        document.getElementById("input-image").style.display = "none";
        document.getElementById("upload-block").style.display = "";
        document.getElementById("sticker-block").style.display = "none";
        currentInputType = INPUT_STATE;
    }
}