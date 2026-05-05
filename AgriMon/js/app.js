// 1. Import tools from our utility file
import { $, loadData, formatDate } from './utils.js';

// 2. Main function that runs when the page loads
document.addEventListener('DOMContentLoaded', async () => {
    console.log("App Initialized...");

    // Load our mock data
    const data = await loadData('./data/mock-data.json');

    if (data) {
        renderUserInfo(data.user);
        renderProjectGrid(data.projects);
    }
});

/**
 * Render User Information (Header/Sidebar)
 */
function renderUserInfo(user) {
    const profileArea = $('.user-profile');
    if (profileArea) {
        // We update the UI with the name from JSON
        // You can add more logic here to change the avatar src
        console.log(`Welcome, ${user.name}`);
    }
}

/**
 * Render the Project Cards into the Dashboard Grid
 */
function renderProjectGrid(projects) {
    const grid = $('#dashboard-grid');
    if (!grid) return;

    // Clear the "Loading..." text
    grid.innerHTML = '';

    // Loop through each project and create a HTML card
    projects.forEach(project => {
        const cardHTML = `
            <div class="card">
                <div style="display: flex; justify-content: space-between; align-items: start; margin-bottom: 15px;">
                    <span class="badge ${project.statusClass}">${project.status}</span>
                    <small style="color: #888;">Due: ${formatDate(project.dueDate)}</small>
                </div>
                
                <h3 style="margin-bottom: 8px;">${project.title}</h3>
                <p style="color: #666; font-size: 14px; margin-bottom: 20px;">${project.category}</p>
                
                <div class="progress-container" style="background: #eee; height: 8px; border-radius: 4px; overflow: hidden;">
                    <div class="progress-bar" style="width: ${project.progress}%; background: var(--primary); height: 100%;"></div>
                </div>
                
                <div style="display: flex; justify-content: space-between; margin-top: 10px; font-size: 12px; font-weight: 600;">
                    <span>Progress</span>
                    <span>${project.progress}%</span>
                </div>
            </div>
        `;
        grid.innerHTML += cardHTML;
    });
}

document.getElementById('login-button').addEventListener('click', function() {
    alert('Login Successful! Redirecting to Agrimon Dashboard...');
    // 这里可以写跳转代码，比如：
    // window.location.href = 'dashboard.html'; 
});

// 1. 找到那个按钮
const loginBtn = document.getElementById('loginBtn');

// 2. 监听点击动作
loginBtn.addEventListener('click', function() {
    console.log("正在跳转到主页...");
    
    // 3. 执行跳转
    window.location.href = 'dashboard.html'; 
});