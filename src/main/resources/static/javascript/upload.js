function showModal(uuid) {
    document.getElementById("modal").classList.remove("hidden");

    const uuidElement = document.getElementById("modal-uuid");
    const uuidConfirmElement = document.getElementById("modal-uuid-confirm");
    const confirmButton = document.getElementById("modal-uuid-confirm");

    uuidElement.innerText = uuid;
    confirmButton.classList.add("disable");

    function checkUUID() {
        if (uuidElement.innerText === uuidConfirmElement.value) {
            confirmButton.classList.remove("disable");
        } else {
            confirmButton.classList.add("disable");
        }
    }
}

setInterval(() => {
    try {
        fetch("/upload_status", {method: "GET"}).then(
            (res) => res.json(),
        ).then(
            (data) => {
                const userStatus = document.getElementById("user-status");
                userStatus.innerHTML = "";
                content = document.createElement("div");
                content.innerHTML = `
                    <p><strong>Username:</strong> ${data.username}</p>
                    <p><strong>CPU:</strong> ${data.cpu * 10}%</p>
                    <p><strong>Memory:</strong> ${data.memory} MB</p>
                    <p><strong>Disk:</strong> ${data.disk} MB</p>
                `;
                userStatus.appendChild(content);

                const containerStatus = document.getElementById("container-status");
                containerStatus.innerHTML = "";

                const rows = data.containers.map((container) => {
                    const row = document.createElement("tr");
                    row.className =
                        "hover:bg-blue-50 transition duration-150 cursor-pointer border-b hover:border-t hover:shadow-inner";
                    row.innerHTML = `
                        <td class="px-4 py-3 text-center">${container.uuid}</td>
                         <td class="px-4 py-3 text-center">WebServer1</td>
                        <td class="px-4 py-3 text-center">${container.cpu * 10}%</td>
                        <td class="px-4 py-3 text-center">${container.memory} MB</td>
                        <td class="px-4 py-3 text-center">${container.disk} MB</td>
                    `;
                    row.addEventListener("click", () => {
                        showModal(container.uuid)
                    });
                    return row;
                });
                containerStatus.append(...rows);
            },
        );
    } catch (error) {
        console.error("fail to fetch data");
    }
}, 1000);
