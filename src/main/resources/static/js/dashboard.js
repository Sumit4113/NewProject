// Toggle Sidebar
const menuToggle = document.getElementById('menu-toggle');
const sidebar = document.getElementById('sidebar');
const mainContent = document.getElementById('main-content');

menuToggle.addEventListener('click', function() {
	sidebar.classList.toggle('active');
	mainContent.classList.toggle('sidebar-active');
	menuToggle.classList.toggle('active');
});

// Toggle Theme
const themeToggle = document.getElementById('theme-toggle');

themeToggle.addEventListener('click', function() {
	if (document.body.getAttribute('data-theme') === 'dark') {
		document.body.removeAttribute('data-theme');
	} else {
		document.body.setAttribute('data-theme', 'dark');
	}
});

// Initialize sidebar as active on page load
window.addEventListener('DOMContentLoaded', function() {
	sidebar.classList.add('active');
	mainContent.classList.add('sidebar-active');
	menuToggle.classList.add('active');
});