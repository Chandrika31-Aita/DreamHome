// ===============================
// Buyer Login
// ===============================
async function buyerLogin() {
  const email = document.getElementById("buyerEmail").value.trim();
  const password = document.getElementById("buyerPassword").value.trim();

  if (!email || !password) {
    alert("Please enter both email and password.");
    return;
  }

  try {
    const res = await apiFetch("/api/buyer/login-buyer", "POST", { email, password });

    if (res.jwt) {
      localStorage.setItem("buyerJWT", res.jwt);
      window.location.href = "buyer-dashboard.html";
    } else {
      alert("Invalid credentials.");
    }
  } catch (err) {
    console.error("Buyer login error:", err);
    alert("Something went wrong. Please try again.");
  }
}

// ===============================
// Buyer Registration
// ===============================
async function buyerRegister() {
  const buyerName = document.getElementById("buyerName").value.trim();
  const email = document.getElementById("buyerEmailReg").value.trim();
  const password = document.getElementById("buyerPasswordReg").value.trim();

  if (!buyerName || !email || !password) {
    alert("All fields are required.");
    return;
  }

  try {
    const res = await apiFetch("/api/buyer/register-buyer", "POST", { buyerName, email, password });

    if (res.message) {
      alert(res.message);
      window.location.href = "login.html";
    } else {
      alert("Registration failed.");
    }
  } catch (err) {
    console.error("Buyer registration error:", err);
    alert("Error registering buyer.");
  }
}
// Load houses by city
async function loadHousesByCity() {
  const jwt = getJWT("buyer");
  if (!jwt) {
    alert("Missing JWT. Please login again.");
    window.location.href = "..buyer/login.html";
    return;
  }

  // Read city value from the input
  const cityInput = document.getElementById("cityFilter");
  const city = cityInput ? cityInput.value.trim() : "";

  if (!city) {
    alert("Please enter a city name.");
    return;
  }

  const url = `/api/houseDetails/retrieve-houses?city=${encodeURIComponent(city)}`;

  try {
    const res = await fetch(url, { method: "GET", headers: { "Authorization": jwt } });
    if (!res.ok) throw new Error("Failed to load houses");

    const data = await res.json();
    const houses = data.houses_list || [];
    const container = document.getElementById("approvedHouses");
    container.innerHTML = "";

    if (!houses.length) {
      container.innerHTML = "<p>No houses found for this city.</p>";
      return;
    }

    houses.forEach(house => {
      const card = document.createElement("div");
      card.classList.add("card");

      const resolved = (typeof resolveImageUrl === "function") ? resolveImageUrl(house.imagePath) : (house.imagePath || "").trim();
      const imageSrc = resolved || (typeof getLocalPlaceholderImageUrl === "function" ? getLocalPlaceholderImageUrl() : "/images/placeholder.svg"); // default placeholder
            const adminStatusColor = house.adminStatus?.trim() === "Approved" ? "green" :
                                     house.adminStatus?.trim() === "Rejected" ? "red" : "orange";


      card.innerHTML = `
        <img src="${imageSrc}" alt="House Image">
        <h3>${house.houseModel || 'House'}</h3>
        <p><strong>HouseId:</strong> ${house.houseId}</p>
        <p><strong>Sqft:</strong> ${house.sqft}</p>
        <p><strong>Price:</strong> ₹${house.price}</p>
        <p><strong>City:</strong> ${house.city}</p>
        <p><strong>State:</strong> ${house.state}</p>
        <p><strong>Contact:</strong> ${house.contactNumber}</p>
        <p><strong>Status:</strong> ${house.status}</p>
        <p><strong>AdminStatus:</strong> <span style="color:${adminStatusColor}">${house.adminStatus}</span></p>
      `;
      container.appendChild(card);
    });
  } catch (err) {
    console.error(err);
    alert("Error loading houses. Please try again later.");
  }
}
