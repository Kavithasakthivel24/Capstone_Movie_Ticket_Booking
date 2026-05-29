const API_BASE = '/api';

function getToken() {
    return localStorage.getItem('token');
}

function getUser() {
    const user = localStorage.getItem('user');
    return user ? JSON.parse(user) : null;
}

function isLoggedIn() {
    return !!getToken();
}

function isAdmin() {
    const user = getUser();
    return user && user.role === 'ROLE_ADMIN';
}

function logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    window.location.href = '/index.html';
}

function updateNavbar() {
    const authButtons = document.getElementById('authButtons');
    const userMenu = document.getElementById('userMenu');
    const userNameEl = document.getElementById('userName');

    if (isLoggedIn()) {
        const user = getUser();
        if (authButtons) authButtons.classList.add('d-none');
        if (userMenu) userMenu.classList.remove('d-none');
        if (userNameEl && user) userNameEl.textContent = user.name;
    } else {
        if (authButtons) authButtons.classList.remove('d-none');
        if (userMenu) userMenu.classList.add('d-none');
    }
}

function showToast(message, type = 'info') {
    const toast = document.getElementById('toast');
    const toastMessage = document.getElementById('toastMessage');
    if (toast && toastMessage) {
        toastMessage.textContent = message;
        const toastHeader = toast.querySelector('.toast-header');
        if (toastHeader) {
            toastHeader.className = `toast-header text-white ${type === 'success' ? 'bg-success' : type === 'error' ? 'bg-danger' : 'bg-primary'}`;
        }
        const bsToast = new bootstrap.Toast(toast);
        bsToast.show();
    }
}

function showSpinner() {
    let overlay = document.getElementById('loadingOverlay');
    if (!overlay) {
        overlay = document.createElement('div');
        overlay.id = 'loadingOverlay';
        overlay.className = 'spinner-overlay';
        overlay.innerHTML = '<div class="spinner-border text-danger" style="width:3rem;height:3rem;"></div>';
        document.body.appendChild(overlay);
    }
    overlay.style.display = 'flex';
}

function hideSpinner() {
    const overlay = document.getElementById('loadingOverlay');
    if (overlay) overlay.style.display = 'none';
}

async function apiRequest(url, options = {}) {
    const token = getToken();
    const headers = { 'Content-Type': 'application/json', ...options.headers };
    if (token) headers['Authorization'] = `Bearer ${token}`;

    const response = await fetch(API_BASE + url, { ...options, headers });

    if (response.status === 401) {
        logout();
        return null;
    }

    return response;
}

document.addEventListener('DOMContentLoaded', updateNavbar);
