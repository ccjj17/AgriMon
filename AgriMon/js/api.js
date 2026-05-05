/**
 * API Service Layer
 * This file handles all data fetching requests.
 */

// Define the path to your data source (could be a URL or a local file)
const DATA_SOURCE = './data/mock-data.json';

/**
 * Fetch all dashboard data
 * @returns {Promise<Object>} The combined data object from our JSON
 */
export async function getDashboardData() {
    try {
        const response = await fetch(DATA_SOURCE);
        
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        const data = await response.json();
        return data;
    } catch (error) {
        console.error("Could not fetch dashboard data:", error);
        // Return a fallback object so the app doesn't crash
        return { projects: [], user: {}, stats: {} };
    }
}

/**
 * Fetch only projects (useful for filtering or searching later)
 */
export async function getProjects() {
    const data = await getDashboardData();
    return data.projects || [];
}

/**
 * Mock function to simulate saving data (for future use)
 */
export async function updateProjectStatus(id, newStatus) {
    console.log(`Sending update to server: Project ${id} is now ${newStatus}`);
    // In a real app, you'd use fetch(url, { method: 'POST', body: ... })
    return { success: true };
}