// Simple JavaScript for the login/register form animation
const loginBtn = document.getElementById('login-btn');
 const loginOverlay = document.getElementById('login-overlay');
 const registerOverlay = document.getElementById('register-overlay');
 const closeBtn = document.getElementById('close-btn');
 const closeRegisterBtn = document.getElementById('close-register-btn');
 const registerLink = document.getElementById('register-link');
 const loginLink = document.getElementById('login-link');

 // Show Login Modal
 loginBtn.addEventListener('click', () => {
   loginOverlay.style.display = 'flex';
   registerOverlay.style.display = 'none';
 });

 // Close Login Modal
 closeBtn.addEventListener('click', () => {
   loginOverlay.style.display = 'none';
 });

 // Show Register Modal
 registerLink.addEventListener('click', (e) => {
   e.preventDefault();
   loginOverlay.style.display = 'none';
   registerOverlay.style.display = 'flex';
 });

 // Back to Login Modal from Register
 loginLink.addEventListener('click', (e) => {
   e.preventDefault();
   registerOverlay.style.display = 'none';
   loginOverlay.style.display = 'flex';
 });

 // Close Register Modal
 closeRegisterBtn.addEventListener('click', () => {
   registerOverlay.style.display = 'none';
 });

 // Optional: Close modals when clicking outside
 window.addEventListener('click', (e) => {
   if (e.target === loginOverlay) loginOverlay.style.display = 'none';
   if (e.target === registerOverlay) registerOverlay.style.display = 'none';
 });