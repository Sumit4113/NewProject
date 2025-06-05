
// Sample contacts data
let contacts = [
	{ id: 1, firstName: 'Alice', lastName: 'Miller', email: 'alice@example.com', phone: '+1-555-0101', company: 'Tech Corp' },
	{ id: 2, firstName: 'Bob', lastName: 'Smith', email: 'bob@example.com', phone: '+1-555-0102', company: 'Design Studio' },
	{ id: 3, firstName: 'Carol', lastName: 'Johnson', email: 'carol@example.com', phone: '+1-555-0103', company: 'Marketing Inc' },
	{ id: 4, firstName: 'David', lastName: 'Brown', email: 'david@example.com', phone: '+1-555-0104', company: 'Finance Ltd' },
	{ id: 5, firstName: 'Emma', lastName: 'Wilson', email: 'emma@example.com', phone: '+1-555-0105', company: 'Startup Hub' },
	{ id: 6, firstName: 'Frank', lastName: 'Davis', email: 'frank@example.com', phone: '+1-555-0106', company: 'Consulting Group' }
];

// DOM elements
const menuToggle = document.getElementById('menuToggle');
const sidebar = document.getElementById('sidebar');
const mainContent = document.getElementById('mainContent');
const overlay = document.getElementById('overlay');
const navItems = document.querySelectorAll('.nav-item[data-section]');
const sections = document.querySelectorAll('.section');
const addContactForm = document.getElementById('addContactForm');
const contactList = document.getElementById('contactList');

// Toggle sidebar
function toggleSidebar() {
	sidebar.classList.toggle('active');
	overlay.classList.toggle('active');

	if (window.innerWidth > 768) {
		mainContent.classList.toggle('sidebar-open');
	}
}

// Close sidebar
function closeSidebar() {
	sidebar.classList.remove('active');
	overlay.classList.remove('active');

	if (window.innerWidth > 768) {
		mainContent.classList.remove('sidebar-open');
	}
}

// Show section
function showSection(sectionId) {
	sections.forEach(section => {
		section.classList.remove('active');
	});

	navItems.forEach(item => {
		item.classList.remove('active');
	});

	document.getElementById(sectionId).classList.add('active');
	document.querySelector(`[data-section="${sectionId}"]`).classList.add('active');

	if (window.innerWidth <= 768) {
		closeSidebar();
	}
}

// Render contacts
function renderContacts() {
	contactList.innerHTML = '';

	contacts.forEach(contact => {
		const initials = (contact.firstName[0] + contact.lastName[0]).toUpperCase();

		const contactCard = document.createElement('div');
		contactCard.className = 'contact-card';
		contactCard.innerHTML = `
                  <div class="contact-header">
                      <div class="contact-avatar">${initials}</div>
                      <div class="contact-info">
                          <h3>${contact.firstName} ${contact.lastName}</h3>
                          <p>${contact.email}</p>
                          <p>${contact.phone}</p>
                          ${contact.company ? `<p style="color: #667eea; font-weight: 500;">${contact.company}</p>` : ''}
                      </div>
                  </div>
                  <div class="contact-actions">
                      <button class="btn btn-outline btn-small" onclick="editContact(${contact.id})">
                          <span>✏️</span> Edit
                      </button>
                      <button class="btn btn-outline btn-small" onclick="deleteContact(${contact.id})">
                          <span>🗑️</span> Delete
                      </button>
                      <button class="btn btn-primary btn-small" onclick="callContact('${contact.phone}')">
                          <span>📞</span> Call
                      </button>
                  </div>
              `;

		contactList.appendChild(contactCard);
	});
}

// Add contact
function addContact(contactData) {
	const newContact = {
		id: Date.now(),
		...contactData
	};

	contacts.push(newContact);
	renderContacts();

	// Show success message
	alert('Contact added successfully!');
}

// Edit contact
function editContact(id) {
	const contact = contacts.find(c => c.id === id);
	if (contact) {
		// For demo purposes, just show an alert
		alert(`Editing ${contact.firstName} ${contact.lastName}`);
	}
}

// Delete contact
function deleteContact(id) {
	if (confirm('Are you sure you want to delete this contact?')) {
		contacts = contacts.filter(c => c.id !== id);
		renderContacts();
	}
}

// Call contact
function callContact(phone) {
	alert(`Calling ${phone}...`);
}

// Event listeners
menuToggle.addEventListener('click', toggleSidebar);
overlay.addEventListener('click', closeSidebar);

// Navigation
navItems.forEach(item => {
	item.addEventListener('click', (e) => {
		e.preventDefault();
		const sectionId = item.getAttribute('data-section');
		showSection(sectionId);
	});
});

// Add contact form
addContactForm.addEventListener('submit', (e) => {
	e.preventDefault();

	const formData = new FormData(addContactForm);
	const contactData = {
		firstName: formData.get('firstName'),
		lastName: formData.get('lastName'),
		email: formData.get('email'),
		phone: formData.get('phone'),
		company: formData.get('company'),
		jobTitle: formData.get('jobTitle'),
		address: formData.get('address'),
		notes: formData.get('notes')
	};

	addContact(contactData);
	addContactForm.reset();
	showSection('contacts');
});

// Handle window resize
window.addEventListener('resize', () => {
	if (window.innerWidth > 768) {
		overlay.classList.remove('active');
	} else {
		mainContent.classList.remove('sidebar-open');
	}
});

// Initialize
renderContacts();

// Auto-open sidebar on desktop
if (window.innerWidth > 768) {
	sidebar.classList.add('active');
	mainContent.classList.add('sidebar-open');
}
