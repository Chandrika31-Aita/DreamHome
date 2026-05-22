// Admin Login
async function adminLogin() {
  const email = document.getElementById("adminEmail").value.trim();
  const password = document.getElementById("adminPassword").value.trim();

  if (!email || !password) {
    alert("Please enter both email and password.");
    return;
  }

  try {
    const res = await apiFetch("/api/admin/login-admin", "POST", { email, password });

    if (res.jwt) {
      alert("Admin login successful!");
      localStorage.setItem("adminJWT", res.jwt);
      window.location.href = "admin-dashboard.html";
    } else {
      alert("Invalid credentials.");
    }
  } catch (err) {
    console.error("Admin login error:", err);
    alert("Something went wrong. Please try again.");
  }
}

// Admin Register
async function adminRegister() {
  const adminName = document.getElementById("adminName").value.trim();
  const email = document.getElementById("adminEmailReg").value.trim();
  const password = document.getElementById("adminPasswordReg").value.trim();

  if (!adminName || !email || !password) {
    alert("All fields are required.");
    return;
  }

  try {
    const res = await apiFetch("/api/admin/register-admin", "POST", {
      adminName,
      email,
      password
    });

    if (res.message) {
      alert(res.message);
      window.location.href = "login.html";
    } else {
      alert("Registration failed.");
    }
  } catch (err) {
    console.error("Admin registration error:", err);
    alert("Error registering admin.");
  }
}

// ================== DASHBOARD ==================

// Load all pending houses
async function loadPendingHouses() {
    const jwt = getJWT('admin');
    if (!jwt) {
        alert("Please login first!");
        window.location.href = "login.html";
        return;
    }

    try {
        const res = await fetch("/api/houseDetails/pending-houses", {
            headers: { "Authorization": jwt }
        });
        const data = await res.json();

        const container = document.getElementById("pendingContainer");
        container.innerHTML = "";

        if (!data.pending_houses || data.pending_houses.length === 0) {
            container.innerHTML = "<p>No pending houses.</p>";
            return;
        }

        data.pending_houses.forEach(house => {
            // Use a safe placeholder if house.imagePath is null or empty
            const resolved = (typeof resolveImageUrl === "function") ? resolveImageUrl(house.imagePath) : (house.imagePath || "").trim();
            const imageSrc = resolved || (typeof getLocalPlaceholderImageUrl === "function" ? getLocalPlaceholderImageUrl() : "/images/placeholder.svg"); // <-- reliable placeholder

            const card = document.createElement("div");
            card.classList.add("card");
            card.innerHTML = `
                <img src="${imageSrc}" alt="House Image">
                <h3>${house.houseModel}</h3>
                <p>HouseId: ${house.houseId}</p>
                <p>Sqft: ${house.sqft}</p>
                <p>Price: ₹${house.price}</p>
                <p>City: ${house.city}</p>
                <p>State: ${house.state}</p>
                <p>ContactNumber: ${house.contactNumber}</p>
                <p>Status: ${house.status}</p>

                <button onclick="approveHouse(${house.houseId})">Approve</button>
                <button onclick="rejectHouse(${house.houseId})">Reject</button>
            `;
            container.appendChild(card);
        });
    } catch (err) {
        console.error("Error loading pending houses:", err);
        alert("Failed to load pending houses.");
    }
}

// View all houses for admin (uses global endpoint)
async function loadAllHousesAdmin() {
  const jwt = getJWT('admin');
  if (!jwt) {
    alert("Please login first!");
    window.location.href = "login.html";
    return;
  }

  try {
    const res = await fetch("/api/houseDetails/retrieve-all-houses", {
      headers: { "Authorization": jwt }
    });
    if (!res.ok) throw new Error(`HTTP ${res.status}`);
    const data = await res.json();

    const container = document.getElementById("pendingContainer");
    container.innerHTML = "";
    const houses = Array.isArray(data.houses_list) ? data.houses_list : [];
    if (houses.length === 0) {
      container.innerHTML = "<p>No properties available.</p>";
      return;
    }
    houses.forEach(house => {
      const resolved = (typeof resolveImageUrl === "function") ? resolveImageUrl(house.imagePath) : (house.imagePath || "").trim();
      const imageSrc = resolved || (typeof getLocalPlaceholderImageUrl === "function" ? getLocalPlaceholderImageUrl() : "/images/placeholder.svg");
      const card = document.createElement("div");
      card.classList.add("card");
      card.innerHTML = `
        <img src="${imageSrc}" alt="House Image">
        <h3>${house.houseModel}</h3>
        <p>HouseId: ${house.houseId}</p>
        <p>Sqft: ${house.sqft}</p>
        <p>Price: ₹${house.price}</p>
        <p>City: ${house.city}</p>
        <p>State: ${house.state}</p>
        <p>ContactNumber: ${house.contactNumber}</p>
        <p>Status: ${house.status}</p>
        <p>AdminStatus: ${house.adminStatus}</p>
      `;
      container.appendChild(card);
    });
  } catch (err) {
    console.error("Error loading all houses:", err);
    alert("Failed to load all houses.");
  }
}

// Expose for inline onclick usage
window.loadAllHousesAdmin = loadAllHousesAdmin;

// Approve house function
async function approveHouse(houseId) {
    await updateAdminStatus(houseId, "Approved");
}

//Reject house function
async function rejectHouse(houseId) {
    const houseCard = document.getElementById(`house-${houseId}`);
    if (houseCard) {
        // Immediately update the status in the DOM
        const statusElement = houseCard.querySelector(".status");
        statusElement.textContent = "Rejected";  // Set status to "Rejected"

        // Disable the buttons to prevent further actions
        houseCard.querySelector("button:nth-of-type(1)").disabled = true;
        houseCard.querySelector("button:nth-of-type(2)").disabled = true;
    }

    // Update the backend status and refresh the house list
    await updateAdminStatus(houseId, "Rejected");
}

// Update admin status helper
async function updateAdminStatus(houseId, status) {
    const jwt = getJWT('admin');
    try {
        const res = await fetch(`/api/houseDetails/update-adminStatus?houseId=${houseId}`, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${jwt}`  // Include Bearer if required
            },
            body: JSON.stringify({ adminStatus: status })
        });

        if (!res.ok) {
            throw new Error(`HTTP error! Status: ${res.status}`);
        }

        const data = await res.json();
        console.log("Response Data:", data);  // Log for debugging

        alert(`House ${status.toLowerCase()} successfully!`);
        loadPendingHouses();  // Reload the pending houses list after status update
    } catch (err) {
        console.error("Error updating admin status:", err);
        alert("Failed to update admin status.");
    }
}