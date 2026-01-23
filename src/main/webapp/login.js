/* ===================================
   MODERN LOGIN - JAVASCRIPT
   =================================== */

document.addEventListener('DOMContentLoaded', () => {
    
    // ===================================
    // THEME MANAGEMENT
    // ===================================
    const themeToggle = document.getElementById('themeToggle');
    const root = document.documentElement;
    
    // Initialize theme
    const savedTheme = localStorage.getItem('sam_theme') || 'dark';
    updateThemeIcon(savedTheme);
    
    // Theme toggle handler
    if (themeToggle) {
        themeToggle.addEventListener('click', () => {
            const currentTheme = root.getAttribute('data-theme') === 'dark' ? 'dark' : 'light';
            const newTheme = currentTheme === 'dark' ? 'light' : 'dark';
            
            root.setAttribute('data-theme', newTheme);
            localStorage.setItem('sam_theme', newTheme);
            updateThemeIcon(newTheme);
            
            // Add ripple effect
            createRipple(themeToggle);
        });
    }
    
    function updateThemeIcon(theme) {
        if (!themeToggle) return;
        const icon = themeToggle.querySelector('i');
        if (icon) {
            icon.className = theme === 'dark' ? 'ri-sun-line' : 'ri-moon-line';
        }
    }
    
    
    // ===================================
    // PAGE LOADER
    // ===================================
    const pageLoader = document.getElementById('pageLoader');
    
    setTimeout(() => {
        if (pageLoader) {
            pageLoader.style.opacity = '0';
            setTimeout(() => {
                pageLoader.remove();
            }, 500);
        }
    }, 1800);
    
    
    // ===================================
    // PASSWORD VISIBILITY TOGGLE
    // ===================================
    const togglePassword = document.getElementById('togglePassword');
    const passwordInput = document.getElementById('password');
    
    if (togglePassword && passwordInput) {
        togglePassword.addEventListener('click', () => {
            const type = passwordInput.type === 'password' ? 'text' : 'password';
            passwordInput.type = type;
            
            const icon = togglePassword.querySelector('i');
            if (icon) {
                icon.className = type === 'password' ? 'ri-eye-off-line' : 'ri-eye-line';
            }
            
            // Add click animation
            togglePassword.style.transform = 'scale(0.9)';
            setTimeout(() => {
                togglePassword.style.transform = 'scale(1)';
            }, 150);
        });
    }
    
    
    // ===================================
    // LOGIN FORM SUBMISSION
    // ===================================
    const loginBtn = document.getElementById('loginBtn');
    const usernameInput = document.getElementById('username');
    const hiddenForm = document.getElementById('hiddenForm');
    const hiddenUsername = document.getElementById('hiddenUsername');
    const hiddenPassword = document.getElementById('hiddenPassword');
    
    if (loginBtn && usernameInput && passwordInput && hiddenForm) {
        loginBtn.addEventListener('click', (e) => {
            e.preventDefault();
            
            // Validate inputs
            if (!usernameInput.value.trim() || !passwordInput.value.trim()) {
                showValidationError();
                return;
            }
            
            // Add loading state
            loginBtn.disabled = true;
            const btnContent = loginBtn.querySelector('.btn-content');
            const originalContent = btnContent.innerHTML;
            btnContent.innerHTML = '<i class="ri-loader-4-line" style="animation: spin 1s linear infinite;"></i><span>Logging in...</span>';
            
            // Copy values to hidden form
            hiddenUsername.value = usernameInput.value;
            hiddenPassword.value = passwordInput.value;
            
            // Submit after a short delay for visual feedback
            setTimeout(() => {
                hiddenForm.submit();
            }, 800);
        });
        
        // Enter key submission
        [usernameInput, passwordInput].forEach(input => {
            input.addEventListener('keypress', (e) => {
                if (e.key === 'Enter') {
                    loginBtn.click();
                }
            });
        });
    }
    
    
    // ===================================
    // INPUT ANIMATIONS
    // ===================================
    const inputs = document.querySelectorAll('.input-wrapper input');
    
    inputs.forEach(input => {
        // Focus animation
        input.addEventListener('focus', () => {
            const wrapper = input.closest('.input-wrapper');
            wrapper.style.transform = 'scale(1.01)';
        });
        
        input.addEventListener('blur', () => {
            const wrapper = input.closest('.input-wrapper');
            wrapper.style.transform = 'scale(1)';
        });
        
        // Input animation
        input.addEventListener('input', () => {
            const icon = input.parentElement.querySelector('.input-icon');
            if (icon && input.value) {
                icon.style.color = 'var(--primary)';
            } else if (icon) {
                icon.style.color = 'var(--text-muted)';
            }
        });
    });
    
    
    // ===================================
    // ALERT AUTO-DISMISS
    // ===================================
    const alerts = document.querySelectorAll('.alert');
    alerts.forEach(alert => {
        setTimeout(() => {
            alert.style.animation = 'slideUp 0.3s ease-out forwards';
            setTimeout(() => {
                alert.remove();
            }, 300);
        }, 5000);
    });
    
    
    // ===================================
    // UTILITY FUNCTIONS
    // ===================================
    
    // Ripple effect
    function createRipple(element) {
        const ripple = document.createElement('span');
        ripple.style.position = 'absolute';
        ripple.style.borderRadius = '50%';
        ripple.style.background = 'rgba(255, 255, 255, 0.3)';
        ripple.style.width = '100px';
        ripple.style.height = '100px';
        ripple.style.marginTop = '-50px';
        ripple.style.marginLeft = '-50px';
        ripple.style.top = '50%';
        ripple.style.left = '50%';
        ripple.style.animation = 'ripple 0.6s ease-out';
        ripple.style.pointerEvents = 'none';
        
        element.style.position = 'relative';
        element.style.overflow = 'hidden';
        element.appendChild(ripple);
        
        setTimeout(() => {
            ripple.remove();
        }, 600);
    }
    
    // Validation error animation
    function showValidationError() {
        const inputs = [usernameInput, passwordInput];
        inputs.forEach(input => {
            if (!input.value.trim()) {
                const wrapper = input.closest('.input-wrapper');
                wrapper.style.animation = 'shake 0.5s ease';
                wrapper.style.borderColor = 'var(--error)';
                
                setTimeout(() => {
                    wrapper.style.animation = '';
                    wrapper.style.borderColor = '';
                }, 500);
            }
        });
    }
    
    
    // ===================================
    // CURSOR TRAIL EFFECT (Optional)
    // ===================================
    let cursorTrail = [];
    const maxTrailLength = 20;
    
    document.addEventListener('mousemove', (e) => {
        if (window.innerWidth < 768) return; // Disable on mobile
        
        const trail = document.createElement('div');
        trail.className = 'cursor-trail';
        trail.style.left = e.pageX + 'px';
        trail.style.top = e.pageY + 'px';
        document.body.appendChild(trail);
        
        cursorTrail.push(trail);
        
        setTimeout(() => {
            trail.remove();
        }, 500);
        
        if (cursorTrail.length > maxTrailLength) {
            const oldest = cursorTrail.shift();
            if (oldest && oldest.parentNode) {
                oldest.remove();
            }
        }
    });
    
});

