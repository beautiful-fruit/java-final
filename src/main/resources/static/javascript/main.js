const uploadStatusElement = document.getElementById("upload_status");
const uploadStatusText = ["No file", "Upload Success"];
uploadStatus = 0

document.getElementById("upload_form").addEventListener(
    "submit", async function (e) {
        e.preventDefault();
        const formData = new FormData(this);
        const file = formData.get("file");
        if (!file)
            return;
        const reader = new FileReader();
        reader.onload = async function () {
            const content = reader.result;
            const response = await fetch("/upload", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({file: content})
            })
        };
        reader.readAsText(file, "UTF-8");
    }
)


setInterval(() => {
    fetch("/upload_status", {method: "GET"}).then(
        res => res.text()
    ).then(
        (res) => {
            uploadStatusElement.innerText = res;
        }
    )
}, 1000);
