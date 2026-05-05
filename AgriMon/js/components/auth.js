/**
 * Authentication Component
 * Handles login, logout, and session management.
 */

// 1. Keys for LocalStorage (to remember if user is logged in)
const AUTH_KEY = 'webapp_is_logged_in';
const USER_KEY = 'webapp_user_info';

/**
 * Check if the user is currently authenticated
 * @returns {boolean}
 */
export function isAuthenticated() {
    return localStorage.getItem(AUTH_KEY) === 'true';
}

/**
 * Perform Login
 * @param {string} email 
 * @param {string} password 
 * @returns {Promise<Object>}
 */
export async function login(email, password) {
    console.log(`Attempting login for: ${email}`);

    // Mocking a server delay
    return new Promise((resolve, reject) => {
        setTimeout(() => {
            // Simple mock validation
            if (email && password.length >= 6) {
                localStorage.setItem(AUTH_KEY, 'true');
                localStorage.setItem(USER_KEY, JSON.stringify({ email, name: 'Alex Johnson' }));
                resolve({ success: true, message: 'Login successful' });
            } else {
                reject({ success: false, message: 'Invalid credentials or password too short' });
            }
        }, 800);
    });
}

/**
 * Perform Logout
 */
export function logout() {
    localStorage.removeItem(AUTH_KEY);
    localStorage.removeItem(USER_KEY);
    // Refresh page to trigger redirect logic
    window.location.reload();
}

/**
 * Protect Page: Redirect to login if not authenticated
 * (You can call this in app.js)
 */
export function checkAuthRedirect() {
    if (!isAuthenticated()) {
        console.warn("User not logged in. Redirecting...");
        // If you have a login.html, you would redirect here:
        // window.location.href = 'login.html';
    }
}