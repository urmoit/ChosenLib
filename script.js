// ChosenLib Documentation - script.js
// Add interactive features here in the future.

console.log('ChosenLib documentation loaded.');

// Search bar placeholder
const searchBar = document.getElementById('searchBar');
if (searchBar) {
    searchBar.addEventListener('input', (e) => {
        // Placeholder: In the future, filter docs based on e.target.value
        // For now, just log
        console.log('Search:', e.target.value);
    });
}
