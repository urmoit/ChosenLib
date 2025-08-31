// ChosenLib Documentation - script.js
// Add interactive features here in the future.

console.log('ChosenLib documentation loaded.');

// Dark mode toggle
const darkModeToggle = document.getElementById('darkModeToggle');
darkModeToggle.addEventListener('click', () => {
    document.body.classList.toggle('dark');
});

// Search bar placeholder
const searchBar = document.getElementById('searchBar');
if (searchBar) {
    searchBar.addEventListener('input', (e) => {
        // Placeholder: In the future, filter docs based on e.target.value
        // For now, just log
        console.log('Search:', e.target.value);
    });
}
