document.getElementById("registerForm").addEventListener(
  "submit",
  function (event) {
    const username = document.getElementById("username").value;
    if (!/^[a-zA-Z0-9]+$/.test(username)) {
      alert("Username can only contain of letters and numbers");
      event.preventDefault();
    }
  },
);
