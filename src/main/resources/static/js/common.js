// common.js

// Simple API fetch function
async function apiFetch(url, method = "GET", body = null) {
  const options = { method, headers: { "Content-Type": "application/json" } };
  if (body) options.body = JSON.stringify(body);

  const response = await fetch(url, options);
  return await response.json();
}

// Utility to check if user is logged in
function getJWT(userType) {
  const jwt = localStorage.getItem(userType + "JWT");
  if (jwt) {
    // Clean the JWT - remove quotes and trim whitespace
    return jwt.replace(/^"|"$/g, '').trim();
  }
  return jwt;
}

// Resolve stored imagePath to a browser-loadable URL.
// Handles cases where pages are opened via file:// (origin = "null").
function resolveImageUrl(imagePath) {
  const p = (imagePath || "").trim();
  if (!p) return "";

  // Absolute URL already
  if (/^https?:\/\//i.test(p)) return p;

  const origin = (window.location && window.location.origin && window.location.origin !== "null")
    ? window.location.origin
    : "http://localhost:8081";

  // If it starts with '/', treat it as server-relative.
  if (p.startsWith("/")) return origin + p;

  // Otherwise treat as relative to the server root.
  return origin + "/" + p;
}

function getLocalPlaceholderImageUrl() {
  const origin = (window.location && window.location.origin && window.location.origin !== "null")
    ? window.location.origin
    : "http://localhost:8081";
  return origin + "/images/placeholder.svg";
}

function logout(userType) {
  // Remove JWT
  localStorage.removeItem(`${userType}JWT`);

  // Optional feedback
  alert(`${userType.charAt(0).toUpperCase() + userType.slice(1)} logged out successfully!`);

  // Universal redirect to root index.html
  // This ensures it works even if the page is inside /buyer/ or /seller/
  const origin = window.location.origin;       // e.g., http://localhost:8080
  const basePath = "/";                         // root folder of static files
  const indexPage = "index.html";               // homepage file
  window.location.href = origin + basePath + indexPage;
}




