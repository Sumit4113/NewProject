document.addEventListener('DOMContentLoaded', function() {
    // Theme toggle functionality
    const toggleSwitch = document.querySelector('#checkbox');
    const currentTheme = localStorage.getItem('theme');
    
    if (currentTheme) {
        document.documentElement.setAttribute('data-theme', currentTheme);
        
        if (currentTheme === 'dark') {
            toggleSwitch.checked = true;
        }
    }
    
    function switchTheme(e) {
        if (e.target.checked) {
            document.documentElement.setAttribute('data-theme', 'dark');
            localStorage.setItem('theme', 'dark');
        } else {
            document.documentElement.setAttribute('data-theme', 'light');
            localStorage.setItem('theme', 'light');
        }
    }
    
    toggleSwitch.addEventListener('change', switchTheme, false);
    
    // Form animations
    const inputs = document.querySelectorAll('.form-input');
    
    if (inputs) {
        inputs.forEach(input => {
            // Check if input has value on page load (for browser autofill)
            if (input.value !== '') {
                input.classList.add('has-value');
                const label = input.nextElementSibling;
                if (label && label.classList.contains('form-label')) {
                    label.style.top = '-20px';
                    label.style.fontSize = '14px';
                    label.style.color = 'var(--primary-color)';
                }
            }
            
            // Add focus animations
            input.addEventListener('focus', function() {
                const line = this.nextElementSibling.nextElementSibling;
                if (line && line.classList.contains('form-line')) {
                    line.querySelector('::after').style.transform = 'scaleX(1)';
                }
            });
            
            // Check for input value on blur
            input.addEventListener('blur', function() {
                if (this.value !== '') {
                    this.classList.add('has-value');
                } else {
                    this.classList.remove('has-value');
                }
            });
        });
    }
    
    // Button hover effect
    const buttons = document.querySelectorAll('.auth-button');
    
    if (buttons) {
        buttons.forEach(button => {
            button.addEventListener('mouseenter', function() {
                const effect = this.querySelector('.button-effect');
                if (effect) {
                    effect.style.left = '100%';
                }
            });
            
            button.addEventListener('mouseleave', function() {
                const effect = this.querySelector('.button-effect');
                if (effect) {
                    effect.style.left = '-100%';
                }
            });
        });
    }
});

// Wait for DOM to be fully loaded
document.addEventListener('DOMContentLoaded', () => {
  // Elements
  const loginBtn = document.getElementById('login-btn');
  const loginOverlay = document.getElementById('login-overlay');
  const registerOverlay = document.getElementById('register-overlay');
  const closeBtn = document.getElementById('close-btn');
  const closeRegisterBtn = document.getElementById('close-register-btn');
  const registerLink = document.getElementById('register-link');
  const loginLink = document.getElementById('login-link');
  const themeToggle = document.getElementById('theme-toggle');
  const body = document.body;
  
  // Check for saved theme preference
  const savedTheme = localStorage.getItem('theme');
  if (savedTheme) {
    body.setAttribute('data-theme', savedTheme);
    if (savedTheme === 'dark') {
      themeToggle.checked = true;
    }
  }
  
  // Theme toggle functionality
  themeToggle.addEventListener('change', () => {
    if (themeToggle.checked) {
      body.setAttribute('data-theme', 'dark');
      localStorage.setItem('theme', 'dark');
    } else {
      body.setAttribute('data-theme', 'light');
      localStorage.setItem('theme', 'light');
    }
  });

  // Show Login Modal with animation
  loginBtn.addEventListener('click', () => {
    loginOverlay.style.display = 'flex';
    setTimeout(() => {
      loginOverlay.classList.add('active');
    }, 10);
    registerOverlay.classList.remove('active');
    registerOverlay.style.display = 'none';
  });

  // Close Login Modal with animation
  closeBtn.addEventListener('click', () => {
    loginOverlay.classList.remove('active');
    setTimeout(() => {
      loginOverlay.style.display = 'none';
    }, 500);
  });

  // Show Register Modal with animation
  if (registerLink) {
    registerLink.addEventListener('click', (e) => {
      e.preventDefault();
      loginOverlay.classList.remove('active');
      setTimeout(() => {
        loginOverlay.style.display = 'none';
        registerOverlay.style.display = 'flex';
        setTimeout(() => {
          registerOverlay.classList.add('active');
        }, 10);
      }, 500);
    });
  }

  // Back to Login Modal from Register with animation
  if (loginLink) {
    loginLink.addEventListener('click', (e) => {
      e.preventDefault();
      registerOverlay.classList.remove('active');
      setTimeout(() => {
        registerOverlay.style.display = 'none';
        loginOverlay.style.display = 'flex';
        setTimeout(() => {
          loginOverlay.classList.add('active');
        }, 10);
      }, 500);
    });
  }

  // Close Register Modal with animation
  closeRegisterBtn.addEventListener('click', () => {
    registerOverlay.classList.remove('active');
    setTimeout(() => {
      registerOverlay.style.display = 'none';
    }, 500);
  });

  // Close modals when clicking outside
  window.addEventListener('click', (e) => {
    if (e.target === loginOverlay) {
      loginOverlay.classList.remove('active');
      setTimeout(() => {
        loginOverlay.style.display = 'none';
      }, 500);
    }
    if (e.target === registerOverlay) {
      registerOverlay.classList.remove('active');
      setTimeout(() => {
        registerOverlay.style.display = 'none';
      }, 500);
    }
  });

  // Add animation to form inputs
  const formInputs = document.querySelectorAll('.form-group input, .form-group textarea');
  formInputs.forEach(input => {
    // Check if input has value (for browser autofill)
    if (input.value !== '') {
      input.classList.add('has-value');
    }
    
    input.addEventListener('focus', () => {
      input.parentElement.classList.add('focused');
    });
    
    input.addEventListener('blur', () => {
      input.parentElement.classList.remove('focused');
      if (input.value !== '') {
        input.classList.add('has-value');
      } else {
        input.classList.remove('has-value');
      }
    });
  });

  // Animate hero text on page load
  const animateHero = () => {
    const heroTitle = document.querySelector('.hero h1');
    const heroParagraph = document.querySelector('.hero p');
    const heroButton = document.querySelector('.hero .cta-btn');
    
    if (heroTitle) heroTitle.classList.add('animate-in');
    if (heroParagraph) {
      setTimeout(() => {
        heroParagraph.classList.add('animate-in');
      }, 300);
    }
    if (heroButton) {
      setTimeout(() => {
        heroButton.classList.add('animate-in');
      }, 600);
    }
  };
  
  // Animate elements when they come into view
  const animateOnScroll = () => {
    const elements = document.querySelectorAll('.animate-on-scroll');
    
    const observer = new IntersectionObserver((entries) => {
      entries.forEach(entry => {
        if (entry.isIntersecting) {
          entry.target.classList.add('animate-in');
          observer.unobserve(entry.target);
        }
      });
    }, { threshold: 0.1 });
    
    elements.forEach(element => {
      observer.observe(element);
    });
  };
  
  // Initialize animations
  animateHero();
  animateOnScroll();
});