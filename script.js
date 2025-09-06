// ChosenLib Documentation - script.js
// Interactive features for the documentation website.

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

// License Modal Functions
function showLicensePopup() {
    const modal = document.getElementById('licenseModal');
    if (modal) {
        modal.style.display = 'block';
        document.body.style.overflow = 'hidden'; // Prevent background scrolling
    }
}

function closeLicensePopup() {
    const modal = document.getElementById('licenseModal');
    if (modal) {
        modal.style.display = 'none';
        document.body.style.overflow = 'auto'; // Restore scrolling
    }
}

// Close modal when clicking outside of it
window.onclick = function(event) {
    const modal = document.getElementById('licenseModal');
    if (event.target === modal) {
        closeLicensePopup();
    }
}

// Close modal with Escape key
document.addEventListener('keydown', function(event) {
    if (event.key === 'Escape') {
        closeLicensePopup();
    }
});

// Mods Modal Functions
function showModsPopup() {
    const modal = document.getElementById('modsModal');
    if (modal) {
        modal.style.display = 'block';
        document.body.style.overflow = 'hidden';
    }
}
function closeModsPopup() {
    const modal = document.getElementById('modsModal');
    if (modal) {
        modal.style.display = 'none';
        document.body.style.overflow = 'auto';
    }
}
// Close mods modal when clicking outside
window.addEventListener('click', function(event) {
    const modal = document.getElementById('modsModal');
    if (event.target === modal) {
        closeModsPopup();
    }
});
// Close mods modal with Escape key
document.addEventListener('keydown', function(event) {
    if (event.key === 'Escape') {
        closeModsPopup();
    }
});

// Mobile Menu Functions
function toggleMobileMenu() {
    const navMenu = document.getElementById('navMenu');
    const hamburger = document.getElementById('hamburger');
    
    if (navMenu && hamburger) {
        navMenu.classList.toggle('active');
        hamburger.classList.toggle('active');
        
        // Prevent body scrolling when menu is open
        if (navMenu.classList.contains('active')) {
            document.body.style.overflow = 'hidden';
        } else {
            document.body.style.overflow = 'auto';
        }
    }
}

// Close mobile menu when clicking on a link
document.addEventListener('DOMContentLoaded', function() {
    const navLinks = document.querySelectorAll('.nav-menu a');
    const navMenu = document.getElementById('navMenu');
    const hamburger = document.getElementById('hamburger');
    
    navLinks.forEach(link => {
        link.addEventListener('click', function() {
            if (navMenu && hamburger) {
                navMenu.classList.remove('active');
                hamburger.classList.remove('active');
                document.body.style.overflow = 'auto';
            }
        });
    });
    
    // Close mobile menu when clicking outside
    document.addEventListener('click', function(event) {
        const navMenu = document.getElementById('navMenu');
        const hamburger = document.getElementById('hamburger');
        const navContainer = document.querySelector('.nav-container');
        
        if (navMenu && hamburger && navContainer) {
            if (!navContainer.contains(event.target) && navMenu.classList.contains('active')) {
                navMenu.classList.remove('active');
                hamburger.classList.remove('active');
                document.body.style.overflow = 'auto';
            }
        }
    });
});
