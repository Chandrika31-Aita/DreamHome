// ---------------------------------------------------------
// seller.js — Clean & Fixed Version
// ---------------------------------------------------------

// ---------------------------------------------------------
// Helper: Get Clean JWT from localStorage
// ---------------------------------------------------------
function getCleanJWT() {
  const token = localStorage.getItem("sellerJWT");
  if (!token) return null;
  return token.trim().replace(/^"|"$/g, "");
}

// ---------------------------------------------------------
// Base helper for API calls
// ---------------------------------------------------------
async function apiFetch(url, method = "GET", data = null, auth = false) {
  const jwt = getCleanJWT();
  const headers = { "Content-Type": "application/json" };
  if (auth && jwt) headers["Authorization"] = "Bearer " + jwt;

  const options = { method, headers };
  if (data) options.body = JSON.stringify(data);

  const res = await fetch(url, options);
  if (!res.ok) {
    const text = await res.text();
    throw new Error(text || "API request failed");
  }
  return res.json();
}

// ---------------------------------------------------------
// Seller Login
// ---------------------------------------------------------
async function sellerLogin() {
  const email = document.getElementById("sellerEmail").value.trim();
  const password = document.getElementById("sellerPassword").value.trim();

  if (!email || !password) {
    alert("Please enter both email and password.");
    return;
  }

  try {
    const res = await apiFetch("/api/seller/login-seller", "POST", { email, password });
    if (res.jwt) {
      localStorage.setItem("sellerJWT", res.jwt.trim());
      alert("Login successful!");
      window.location.href = "seller-dashboard.html";
    } else {
      alert("Invalid credentials. Please try again.");
    }
  } catch (err) {
    console.error("Login error:", err);
    alert("Login failed. Check console for details.");
  }
}

// ---------------------------------------------------------
// Seller Registration
// ---------------------------------------------------------
async function sellerRegister() {
  const sellerName = document.getElementById("sellerName").value.trim();
  const email = document.getElementById("sellerEmailReg").value.trim();
  const password = document.getElementById("sellerPasswordReg").value.trim();
  const aadharNumber = document.getElementById("sellerAadhar").value.trim();

  if (!sellerName || !email || !password || !aadharNumber) {
    alert("All fields are required.");
    return;
  }

  try {
    const res = await apiFetch("/api/seller/register-seller", "POST", {
      sellerName,
      email,
      password,
      aadharNumber
    });

    if (res.message) {
      alert(res.message);
      window.location.href = "login.html";
    } else {
      alert("Registration failed. Please try again.");
    }
  } catch (err) {
    console.error("Registration error:", err);
    alert("Registration failed. Check console for details.");
  }
}

// ---------------------------------------------------------
// Navigation between Upload & View Pages
// ---------------------------------------------------------
function showUploadedHouses() {
  document.getElementById("uploadPage").classList.add("hidden");
  document.getElementById("viewPage").classList.remove("hidden");
  loadSellerHouses();
}

function showUploadPage() {
  document.getElementById("viewPage").classList.add("hidden");
  document.getElementById("uploadPage").classList.remove("hidden");
}


function clearUploadFields() {
  const fields = [
    "houseModel", "sqft", "price", "city", "state",
    "contactNumber", "status", "adminStatus", "imagePath"
  ];

  fields.forEach(id => {
    const input = document.getElementById(id);
    if (input) input.value = "";
  });
}

// ---------------------------------------------------------
// Upload House with Image (multipart/form-data)
// ---------------------------------------------------------
async function uploadHouse() {
  try {
    const jwt = getCleanJWT();
    if (!jwt) {
      alert("Please login first!");
      window.location.href = "login.html";
      return;
    }

    const houseModel = document.getElementById("houseModel").value.trim();
    const sqft = document.getElementById("sqft").value.trim();
    const price = document.getElementById("price").value.trim();
    const city = document.getElementById("city").value.trim();
    const state = document.getElementById("state").value.trim();
    const contactNumber = document.getElementById("contactNumber").value.trim();
    const status = document.getElementById("status").value.trim() || "Available";
    const adminStatus = "pending"

    const imageInput = document.getElementById("imagePath");

    if (!houseModel || !sqft || !price || !city || !state || !contactNumber) {
      alert("Please fill in all mandatory fields.");
      return;
    }

    if (!imageInput || imageInput.files.length === 0) {
      alert("Please select an image file.");
      return;
    }

    const imageFile = imageInput.files[0];

    const formData = new FormData();
    formData.append("houseModel", houseModel);
    formData.append("sqft", sqft);
    formData.append("price", price);
    formData.append("city", city);
    formData.append("state", state);
    formData.append("contactNumber", contactNumber);
    formData.append("status", status);
    formData.append("adminStatus", adminStatus);
    formData.append("image", imageFile);

    const res = await fetch("/api/houseDetails/upload-house", {
          method: "POST",
          headers: { "Authorization": "Bearer " + jwt },
          body: formData
    });

    const data = await res.json();
    if (res.ok) {
          alert("House uploaded successfully!");
          clearUploadFields();
          showUploadedHouses();
    }else {
          console.error("Upload failed:", data);
          alert("Upload failed: " + (data.error || JSON.stringify(data)));
    }

  }catch (err) {
        console.error("Upload error:", err);
        alert("Upload failed. Check console for details.");
  }
}

// ---------------------------------------------------------
// Load Seller Houses
// ---------------------------------------------------------
async function loadSellerHouses() {
  const jwt = getCleanJWT();
  if (!jwt) {
    alert("Please login first!");
    window.location.href = "login.html";
    return;
  }

  try {
    const res = await fetch("/api/houseDetails/retrieve-seller-houses", {
      method: "GET",
      headers: { "Authorization": "Bearer " + jwt }
    });

    if (!res.ok) {
      const text = await res.text();
      throw new Error(text);
    }

    const data = await res.json();
    const houses = Array.isArray(data.seller_houses_list) ? data.seller_houses_list : [];
    const container = document.getElementById("houseContainer");
    container.innerHTML = "";
    window.houseById = window.houseById || {};

    if (houses.length === 0) {
      container.innerHTML = "<p>No houses uploaded yet.</p>";
      return;
    }

    houses.forEach(house => {
      const resolved = (typeof resolveImageUrl === "function") ? resolveImageUrl(house.imagePath) : (house.imagePath || "").trim();
      const imageSrc = resolved || (typeof getLocalPlaceholderImageUrl === "function" ? getLocalPlaceholderImageUrl() : "/images/placeholder.svg");
      const adminStatusColor = house.adminStatus?.trim() === "Approved" ? "green" :
                               house.adminStatus?.trim() === "Rejected" ? "red" : "orange";

      const card = document.createElement("div");
      card.className = "card";
      window.houseById[String(house.houseId)] = house;
      card.innerHTML = `
        <img src="${imageSrc}" alt="House Image">
        <div class="house-info">
          <h3>${house.houseModel || "House"}</h3>
          <p><strong>HouseId:</strong> ${house.houseId}</p>
          <p><strong>Sqft:</strong> ${house.sqft}</p>
          <p><strong>Price:</strong> ₹${house.price}</p>
          <p><strong>City:</strong> ${house.city}</p>
          <p><strong>State:</strong> ${house.state}</p>
          <p><strong>Contact:</strong> ${house.contactNumber}</p>
          <p><strong>Status:</strong> ${house.status}</p>
          <p><strong>AdminStatus:</strong>
            <span style="color:${adminStatusColor}; font-weight:bold;">
              ${house.adminStatus}
            </span>
          </p>
          <div style="display:flex; gap:8px; margin-top:8px;">
            <button class="btn" onclick="openEditPage(${house.houseId})">✏️ Edit</button>
            <button class="btn btn-secondary" onclick="deleteHouse(${house.houseId})">🗑️ Delete</button>
          </div>
        </div>
      `;
      container.appendChild(card);
    });

  } catch (err) {
    console.error("Error loading seller houses:", err);
    alert("Failed to load your uploaded houses.");
  }
}

// View all houses for seller (uses global endpoint)
async function loadAllHousesForSeller() {
  const jwt = getCleanJWT();
  if (!jwt) {
    alert("Please login first!");
    window.location.href = "login.html";
    return;
  }

  const container = document.getElementById("houseContainer");
  container.innerHTML = "<p>Loading all properties...</p>";
  try {
    const res = await fetch("/api/houseDetails/retrieve-all-houses", {
      method: "GET",
      headers: { "Authorization": "Bearer " + jwt }
    });
    if (!res.ok) {
      const text = await res.text();
      throw new Error(text);
    }
    const data = await res.json();
    const houses = Array.isArray(data.houses_list) ? data.houses_list : [];
    container.innerHTML = "";
    window.houseById = window.houseById || {};
    if (houses.length === 0) {
      container.innerHTML = "<p>No properties available.</p>";
      return;
    }
    houses.forEach(house => {
      const card = document.createElement("div");
      card.className = "card";
      const resolved = (typeof resolveImageUrl === "function") ? resolveImageUrl(house.imagePath) : (house.imagePath || "").trim();
      const imageSrc = resolved || (typeof getLocalPlaceholderImageUrl === "function" ? getLocalPlaceholderImageUrl() : "/images/placeholder.svg");
      const adminStatusColor = house.adminStatus?.trim() === "Approved" ? "green" : house.adminStatus?.trim() === "Rejected" ? "red" : "orange";
      window.houseById[String(house.houseId)] = house;
      card.innerHTML = `
        <img src="${imageSrc}" alt="House Image">
        <div class="house-info">
          <h3>${house.houseModel || "House"}</h3>
          <p><strong>HouseId:</strong> ${house.houseId}</p>
          <p><strong>Sqft:</strong> ${house.sqft}</p>
          <p><strong>Price:</strong> ₹${house.price}</p>
          <p><strong>City:</strong> ${house.city}</p>
          <p><strong>State:</strong> ${house.state}</p>
          <p><strong>Contact:</strong> ${house.contactNumber}</p>
          <p><strong>Status:</strong> ${house.status}</p>
          <p><strong>AdminStatus:</strong>
            <span style="color:${adminStatusColor}; font-weight:bold;">
              ${house.adminStatus}
            </span>
          </p>
        </div>`;
      container.appendChild(card);
    });
  } catch (err) {
    console.error("Error loading all houses:", err);
    alert("Failed to load all properties.");
  }
}

// Expose for inline onclick usage
window.loadAllHousesForSeller = loadAllHousesForSeller;




// ---------------------------------------------------------
// Dashboard Initialization
// ---------------------------------------------------------
function initSellerDashboard() {
  const jwt = getCleanJWT();
  if (!jwt) {
    alert("Please login first!");
    window.location.href = "login.html";
  }
  try {
    const msg = localStorage.getItem('flashMessage');
    if (msg) {
      alert(msg);
      localStorage.removeItem('flashMessage');
      localStorage.removeItem('flashType');
    }
  } catch (e) {}
}

// ---------------------------------------------------------
// Shared modal functions (used by dashboard inline modal)
// ---------------------------------------------------------
function openEditModal(houseId) {
  // The actual modal lives in seller-dashboard.html; here we just populate if present
  const modal = document.getElementById("editModal");
  if (!modal) return; // not on dashboard page
  const house = window.houseById && window.houseById[String(houseId)];
  if (!house) return;
  const setVal = (id, val) => { const el = document.getElementById(id); if (el) el.value = val ?? ""; };
  setVal("edit_houseId", house.houseId);
  setVal("edit_houseModel", house.houseModel);
  setVal("edit_price", house.price);
  setVal("edit_sqft", house.sqft);
  setVal("edit_city", house.city);
  setVal("edit_state", house.state);
  setVal("edit_contactNumber", house.contactNumber);
  setVal("edit_status", house.status);
  modal.classList.remove("hidden");
}

function closeEditModal() {
  const modal = document.getElementById("editModal");
  if (modal) modal.classList.add("hidden");
}

function openEditPage(houseId) {
  try { localStorage.setItem('editingHouseId', String(houseId)); } catch (e) {}
  window.location.href = `edit-house.html?houseId=${encodeURIComponent(houseId)}`;
}

async function submitUpdateHouse() {
  const modal = document.getElementById("editModal");
  if (!modal) return;

  const jwt = getCleanJWT();
  if (!jwt) {
    alert("Please login first!");
    window.location.href = "login.html";
    return;
  }

  const houseId = document.getElementById("edit_houseId").value;
  const body = {
    houseModel: document.getElementById("edit_houseModel").value.trim(),
    price: Number(document.getElementById("edit_price").value),
    sqft: Number(document.getElementById("edit_sqft").value),
    city: document.getElementById("edit_city").value.trim(),
    state: document.getElementById("edit_state").value.trim(),
    contactNumber: document.getElementById("edit_contactNumber").value.trim(),
    status: (document.getElementById("edit_status").value || "Available").trim(),
    imagePath: (window.houseById && window.houseById[String(houseId)] && window.houseById[String(houseId)].imagePath) || "",
    adminStatus: (window.houseById && window.houseById[String(houseId)] && window.houseById[String(houseId)].adminStatus) || "pending"
  };

  try {
    console.log("Sending update request..."); // Debugging line

    const res = await fetch(`/api/houseDetails/update-house?houseId=${encodeURIComponent(houseId)}`, {
      method: "PUT",
      headers: { "Content-Type": "application/json", "Authorization": "Bearer " + jwt },
      body: JSON.stringify(body)
    });

    console.log("Response status:", res.status); // Debugging line

    if (!res.ok) {
      const text = await res.text();
      console.error("Error:", text); // Log the error if response is not ok
      throw new Error(text || "Update failed");
    }

    const data = await res.json();
    console.log("API Response:", data); // Debugging line

    // Show success message upon successful response
    alert("Property updated successfully!");

    // Close the modal and reload the houses
    closeEditModal();
    loadSellerHouses();

  } catch (err) {
    console.error("Update error:", err);
    alert("Failed to update property.");
  }
}


async function deleteHouse(houseId) {
  const jwt = getCleanJWT();
  if (!jwt) {
    alert("Please login first!");
    window.location.href = "login.html";
    return;
  }
  if (!confirm("Are you sure you want to delete this property?")) return;
  try {
    const res = await fetch(`/api/houseDetails/delete-house?houseId=${encodeURIComponent(houseId)}`, {
      method: "DELETE",
      headers: { "Authorization": "Bearer " + jwt }
    });
    if (!res.ok) {
      const text = await res.text();
      throw new Error(text || "Delete failed");
    }
    await res.json();
    alert("Property deleted successfully.");
    loadSellerHouses();
  } catch (err) {
    console.error("Delete error:", err);
    alert("Failed to delete property.");
  }
}
