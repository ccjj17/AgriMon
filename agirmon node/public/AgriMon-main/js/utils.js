/**
 * Utility Functions for our Web App
 */

// 1. DOM Selector: 让你用 $('selector') 代替冗长的 document.querySelector
export const $ = (selector) => document.querySelector(selector);

// 2. Multi-DOM Selector: 选择多个元素
export const $$ = (selector) => document.querySelectorAll(selector);

// 3. Data Fetcher: 专门用来读取你刚才写的 mock-data.json
export async function loadData(url) {
    try {
        const response = await fetch(url);
        if (!response.ok) throw new Error('Network response was not ok');
        return await response.json();
    } catch (error) {
        console.error('Fetch error:', error);
        return null;
    }
}

// 4. Date Formatter: 把日期变成好看的英文格式 (例如: May 20, 2024)
export function formatDate(dateString) {
    const options = { year: 'numeric', month: 'short', day: 'numeric' };
    return new Date(dateString).toLocaleDateString('en-US', options);
}

// 5. Progress Bar Color: 根据进度百分比返回颜色 (可选)
export function getProgressColor(percent) {
    if (percent >= 100) return '#28a745'; // Green
    if (percent >= 50) return '#4A90E2';  // Blue
    return '#ffc107';                     // Yellow/Warning
}