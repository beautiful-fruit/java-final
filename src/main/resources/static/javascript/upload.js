function deleteContainer(uuid) {
  fetch("/delete", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({ "uuid": uuid }),
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
    fetch("/upload_status", { method: "GET" }).then(
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

        containers = data.containers;
        username = data.username;

        const containerStatus = document.getElementById("container-status");
        containerStatus.innerHTML = "";
        containerStatus.appendChild(
          Object.assign(document.createElement("thread"), {
            className:
              "bg-gray-50 text-left uppercase text-xs font-semibold text-gray-600 tracking-wider",
          }),
        );
        topbar = document.createElement("tr");
        topbar.innerHTML = `
                    <th class="px-4 py-3">UUID</th>
                    <th class="px-4 py-3">Display Name</th>
                    <th class="px-4 py-3">CPU Usage (%)</th>
                    <th class="px-4 py-3">Memory (MiB)</th>
                    <th class="px-4 py-3">Disk Space (GiB)</th>
                `;
        containerStatus.appendChild(topbar);
        containers.forEach((container) => {
          card = document.createElement("tr");
          card.className =
            "hover:bg-blue-50 transition duration-150 cursor-pointer border-b hover:border-t hover:shadow-inner";
          card.innerHTML = `
                        <td class="px-4 py-3">${container.uuid}</td>
                         <td class="px-4 py-3">WebServer1</td>
                        <td class="px-4 py-3">${container.cpu * 10}%</td>
                        <td class="px-4 py-3">${container.memory} MB</td>
                        <td class="px-4 py-3">${container.disk} MB</td>
                        <button onclick="deleteContainer('${container.uuid}')">delete</button>
                    `;
          containerStatus.appendChild(card);
        });
      },
    );
  } catch (error) {
    console.error("fail to fetch data");
  }
}, 1000);
