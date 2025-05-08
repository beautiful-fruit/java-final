const uploadStatusElement = document.getElementById("upload_status");
const uploadStatusText = ["No file", "Upload Success"];
uploadStatus = 0;

document.getElementById("upload_form").addEventListener(
    "submit",
    async function (e) {
        e.preventDefault();
        const formData = new FormData(this);
        const file = formData.get("file");
        const cpu = formData.get("cpu");
        const memory = formData.get("memory");
        const disk = formData.get("disk");
        if (!file) {
            return;
        }
        const reader = new FileReader();
        reader.onload = async function () {
            const content = reader.result;
            fetch("/upload", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({
                    file: content,
                    "cpu": cpu,
                    "memory": memory,
                    "disk": disk,
                }),
            }).then(
                (res) => res.text(),
            ).then(
                (text) => {
                    alert(text);
                },
            );
        };
        reader.readAsText(file, "UTF-8");
    },
);

function deleteContainer(uuid) {
    fetch("/delete", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify({"uuid": uuid}),
    }).then(
        (res) => res.text(),
    ).then(
        (text) => {
            alert(text);
        },
    );
}

setInterval(() => {
    try {
        fetch("/upload_status", {method: "GET"}).then(
            (res) => res.json(),
        ).then(
            (data) => {
                const userStatus = document.getElementById("user-status");
                userStatus.innerHTML = "";
                userStatus.appendChild(Object.assign(document.createElement("h1"), {
                    textContent: "User Status",
                }));
                card = document.createElement("div");
                card.className = "container-card";
                card.innerHTML = `
                    <div class="metric">Username: ${data.username}</div>
                    <div class="metric">CPU: ${data.cpu} x 0.1</div>
                    <div class="metric">Memory: ${data.memory} MB</div>
                    <div class="metric">Disk: ${data.disk} MB</div>
                `;
                userStatus.appendChild(card);
                containers = data.containers;
                username = data.username;

                const containerStatus = document.getElementById("container-status");
                containerStatus.innerHTML = "";

                if (containers.length == 0) {
                    containerStatus.appendChild(
                        Object.assign(document.createElement("h1"), {
                            textContent: "No Container",
                        }),
                    );
                } else {
                    containerStatus.appendChild(
                        Object.assign(document.createElement("h1"), {
                            textContent: "Containers",
                        }),
                    );
                }
                grid = document.createElement("div");
                grid.className = "container-grid";
                containers.forEach((container) => {
                    card = document.createElement("div");
                    card.className = "container-card";
                    card.innerHTML = `
                        <div class="metric">UUID: ${container.uuid}</div>
                        <div class="metric">CPU: ${container.cpu} x 0.1</div>
                        <div class="metric">Memory: ${container.memory} MB</div>
                        <div class="metric">Disk: ${container.disk} MB</div>
                        <button onclick="deleteContainer('${container.uuid}')">delete</button>
                    `;
                    grid.appendChild(card);
                });
                containerStatus.appendChild(grid);
            },
        );
    } catch (error) {
        console.error("fail to fetch data");
    }
}, 1000);
