/**
 * Dashboard Component
 * Handles interactions specifically for the main dashboard view.
 */

import { $ } from '../utils.js';

/**
 * Initialize Dashboard interactions
 * This connects the logic to the UI elements
 */
export function initDashboard(data) {
    console.log("Dashboard interactions initialized");
    
    // 1. Update Stats (Top counters)
    updateStats(data.stats);

    // 2. Setup Filter Buttons (If you have them in Figma)
    setupFilters();
}

/**
 * Update the statistic numbers on the dashboard
 */
function updateStats(stats) {
    if (!stats) return;

    // Assuming you have elements with these IDs in your HTML
    const totalProjectsEl = $('#total-projects');
    const activeTasksEl = $('#active-tasks');

    if (totalProjectsEl) totalProjectsEl.textContent = stats.totalProjects;
    if (activeTasksEl) activeTasksEl.textContent = stats.activeTasks;
}

/**
 * Handle filtering of project cards
 */
function setupFilters() {
    const filterBtns = document.querySelectorAll('.filter-btn');
    
    filterBtns.forEach(btn => {
        btn.addEventListener('click', (e) => {
            // Remove active class from all, add to clicked
            filterBtns.forEach(b => b.classList.remove('active'));
            e.target.classList.add('active');

            const category = e.target.dataset.category;
            console.log(`Filtering projects by: ${category}`);
            // Logic to re-render or hide cards goes here
        });
    });
}

/**
 * Show Project Details (When a card is clicked)
 */
export function handleCardClick(projectId) {
    console.log(`Opening details for Project ID: ${projectId}`);
    // You could open a Modal/Popup here as seen in many Figma designs
    alert(`Project Details for ID: ${projectId} - Coming Soon!`);
}