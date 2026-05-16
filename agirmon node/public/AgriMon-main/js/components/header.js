/**
 * Header Component
 * Handles interactions for the top navigation bar.
 */

import { $ } from '../utils.js';
import { logout } from './auth.js';

/**
 * Initialize Header functions
 */
export function initHeader() {
    setupSearch();
    setupUserMenu();
    setupMobileToggle();
}

/**
 * Handle Search Input logic
 */
function setupSearch() {
    const searchInput = $('.search-bar input');
    if (!searchInput) return;

    searchInput.addEventListener('input', (e) => {
        const query = e.target.value.toLowerCase();
        console.log(`Searching for: ${query}`);
        // You can link this to a function that filters the cards in dashboard.js
    });
}

/**
 * Handle User Profile Dropdown
 */
function setupUserMenu() {
    const avatar = $('.avatar');
    if (!avatar) return;

    avatar.addEventListener('click', () => {
        const confirmLogout = confirm("Do you want to log out?");
        if (confirmLogout) {
            logout();
        }
    });
}

/**
 * Mobile Sidebar Toggle (For smaller screens)
 */
function setupMobileToggle() {
    const menuBtn = $('#menu-toggle'); // Assume you add a hamburger icon in HTML
    const sidebar = $('#sidebar');

    if (menuBtn && sidebar) {
        menuBtn.addEventListener('click', () => {
            sidebar.classList.toggle('active');
        });
    }
}