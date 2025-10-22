// ================================================
// FILE: web/js/main.js
// Leave Management System - Main JavaScript
// ================================================

console.log('Leave Management System - JavaScript loaded');

// ========== AUTO-HIDE ALERTS ==========
document.addEventListener('DOMContentLoaded', function() {
    // Auto-hide alerts after 5 seconds
    const alerts = document.querySelectorAll('.alert');
    alerts.forEach(function(alert) {
        setTimeout(function() {
            alert.style.transition = 'opacity 0.5s';
            alert.style.opacity = '0';
            setTimeout(function() {
                alert.remove();
            }, 500);
        }, 5000);
    });
});

// ========== FORM VALIDATION HELPERS ==========
function validateEmail(email) {
    const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return re.test(String(email).toLowerCase());
}

function validatePhone(phone) {
    const re = /^[0-9]{10,11}$/;
    return re.test(phone.replace(/\s/g, ''));
}

// ========== DATE UTILITIES ==========
function formatDate(date) {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
}

function parseDate(dateString) {
    return new Date(dateString);
}

function daysBetween(date1, date2) {
    const oneDay = 24 * 60 * 60 * 1000;
    const firstDate = new Date(date1);
    const secondDate = new Date(date2);
    return Math.round(Math.abs((firstDate - secondDate) / oneDay)) + 1;
}

// ========== CONFIRM DIALOGS ==========
function confirmDelete(message) {
    return confirm(message || 'Bạn có chắc chắn muốn xóa?');
}

function confirmAction(message) {
    return confirm(message || 'Bạn có chắc chắn?');
}

// ========== LOADING SPINNER ==========
function showLoading(buttonElement) {
    if (buttonElement) {
        buttonElement.disabled = true;
        buttonElement.innerHTML = '<span class="loading"></span> Đang xử lý...';
    }
}

function hideLoading(buttonElement, originalText) {
    if (buttonElement) {
        buttonElement.disabled = false;
        buttonElement.innerHTML = originalText;
    }
}

// ========== TABLE SORTING (Optional) ==========
function sortTable(tableId, columnIndex) {
    const table = document.getElementById(tableId);
    if (!table) return;
    
    const tbody = table.querySelector('tbody');
    const rows = Array.from(tbody.querySelectorAll('tr'));
    
    rows.sort((a, b) => {
        const aText = a.cells[columnIndex].textContent.trim();
        const bText = b.cells[columnIndex].textContent.trim();
        return aText.localeCompare(bText);
    });
    
    rows.forEach(row => tbody.appendChild(row));
}

// ========== PRINT PAGE ==========
function printPage() {
    window.print();
}

// ========== COPY TO CLIPBOARD ==========
function copyToClipboard(text) {
    const textarea = document.createElement('textarea');
    textarea.value = text;
    document.body.appendChild(textarea);
    textarea.select();
    document.execCommand('copy');
    document.body.removeChild(textarea);
    
    // Show feedback
    const feedback = document.createElement('div');
    feedback.className = 'alert alert-success';
    feedback.textContent = 'Đã copy vào clipboard!';
    feedback.style.position = 'fixed';
    feedback.style.top = '20px';
    feedback.style.right = '20px';
    feedback.style.zIndex = '9999';
    document.body.appendChild(feedback);
    
    setTimeout(() => {
        feedback.remove();
    }, 2000);
}

// ========== SMOOTH SCROLL ==========
function smoothScrollTo(elementId) {
    const element = document.getElementById(elementId);
    if (element) {
        element.scrollIntoView({
            behavior: 'smooth',
            block: 'start'
        });
    }
}

// ========== SESSION TIMEOUT WARNING ==========
let sessionTimeout;
let warningTimeout;

function resetSessionTimer() {
    clearTimeout(sessionTimeout);
    clearTimeout(warningTimeout);
    
    // Warn 2 minutes before timeout (28 minutes)
    warningTimeout = setTimeout(() => {
        if (confirm('Phiên làm việc sắp hết hạn. Bạn có muốn tiếp tục?')) {
            resetSessionTimer();
            // Make a request to keep session alive
            fetch(window.location.href, { method: 'HEAD' });
        }
    }, 28 * 60 * 1000);
    
    // Logout after 30 minutes
    sessionTimeout = setTimeout(() => {
        alert('Phiên làm việc đã hết hạn. Bạn sẽ được đăng xuất.');
        window.location.href = '/LeaveManagement/logout';
    }, 30 * 60 * 1000);
}

// Reset timer on user activity
document.addEventListener('mousemove', resetSessionTimer);
document.addEventListener('keypress', resetSessionTimer);
document.addEventListener('click', resetSessionTimer);

// Initialize on page load
if (document.querySelector('.main-nav')) {
    resetSessionTimer();
}

// ========== BACK TO TOP BUTTON ==========
window.addEventListener('scroll', function() {
    const backToTopBtn = document.getElementById('backToTop');
    if (backToTopBtn) {
        if (window.pageYOffset > 300) {
            backToTopBtn.style.display = 'block';
        } else {
            backToTopBtn.style.display = 'none';
        }
    }
});

function scrollToTop() {
    window.scrollTo({
        top: 0,
        behavior: 'smooth'
    });
}

// Create back to top button
if (document.querySelector('.main-content')) {
    const backToTopBtn = document.createElement('button');
    backToTopBtn.id = 'backToTop';
    backToTopBtn.innerHTML = '⬆️';
    backToTopBtn.style.cssText = `
        display: none;
        position: fixed;
        bottom: 30px;
        right: 30px;
        z-index: 99;
        border: none;
        outline: none;
        background-color: #667eea;
        color: white;
        cursor: pointer;
        padding: 15px;
        border-radius: 50%;
        font-size: 18px;
        box-shadow: 0 4px 12px rgba(0,0,0,0.2);
        transition: all 0.3s;
    `;
    backToTopBtn.onmouseover = function() {
        this.style.backgroundColor = '#5568d3';
        this.style.transform = 'scale(1.1)';
    };
    backToTopBtn.onmouseout = function() {
        this.style.backgroundColor = '#667eea';
        this.style.transform = 'scale(1)';
    };
    backToTopBtn.onclick = scrollToTop;
    document.body.appendChild(backToTopBtn);
}

// ========== KEYBOARD SHORTCUTS ==========
document.addEventListener('keydown', function(e) {
    // Ctrl/Cmd + K: Focus search (if exists)
    if ((e.ctrlKey || e.metaKey) && e.key === 'k') {
        e.preventDefault();
        const searchInput = document.querySelector('input[type="search"]');
        if (searchInput) searchInput.focus();
    }
    
    // Escape: Close modals
    if (e.key === 'Escape') {
        const modals = document.querySelectorAll('.modal');
        modals.forEach(modal => {
            if (modal.style.display === 'block') {
                modal.style.display = 'none';
            }
        });
    }
});

// ========== FORM AUTO-SAVE (Optional) ==========
function autoSaveForm(formId, storageKey) {
    const form = document.getElementById(formId);
    if (!form) return;
    
    // Load saved data
    const savedData = localStorage.getItem(storageKey);
    if (savedData) {
        const data = JSON.parse(savedData);
        Object.keys(data).forEach(key => {
            const input = form.querySelector(`[name="${key}"]`);
            if (input) input.value = data[key];
        });
    }
    
    // Save on change
    form.addEventListener('input', function(e) {
        const formData = {};
        const inputs = form.querySelectorAll('input, select, textarea');
        inputs.forEach(input => {
            if (input.name) {
                formData[input.name] = input.value;
            }
        });
        localStorage.setItem(storageKey, JSON.stringify(formData));
    });
    
    // Clear on submit
    form.addEventListener('submit', function() {
        localStorage.removeItem(storageKey);
    });
}

// ========== TABLE SEARCH/FILTER ==========
function filterTable(inputId, tableId) {
    const input = document.getElementById(inputId);
    const table = document.getElementById(tableId);
    
    if (!input || !table) return;
    
    input.addEventListener('keyup', function() {
        const filter = this.value.toLowerCase();
        const rows = table.querySelectorAll('tbody tr');
        
        rows.forEach(row => {
            const text = row.textContent.toLowerCase();
            row.style.display = text.includes(filter) ? '' : 'none';
        });
    });
}

// ========== TOOLTIPS (Simple implementation) ==========
document.addEventListener('DOMContentLoaded', function() {
    const elementsWithTooltip = document.querySelectorAll('[title]');
    
    elementsWithTooltip.forEach(element => {
        element.addEventListener('mouseenter', function(e) {
            const tooltip = document.createElement('div');
            tooltip.className = 'tooltip';
            tooltip.textContent = this.getAttribute('title');
            tooltip.style.cssText = `
                position: absolute;
                background: #333;
                color: white;
                padding: 8px 12px;
                border-radius: 4px;
                font-size: 12px;
                z-index: 9999;
                pointer-events: none;
                white-space: nowrap;
            `;
            document.body.appendChild(tooltip);
            
            const rect = this.getBoundingClientRect();
            tooltip.style.left = rect.left + (rect.width / 2) - (tooltip.offsetWidth / 2) + 'px';
            tooltip.style.top = (rect.top - tooltip.offsetHeight - 8) + 'px';
            
            this.setAttribute('data-original-title', this.getAttribute('title'));
            this.removeAttribute('title');
            
            this.addEventListener('mouseleave', function() {
                tooltip.remove();
                this.setAttribute('title', this.getAttribute('data-original-title'));
                this.removeAttribute('data-original-title');
            }, { once: true });
        });
    });
});

// ========== CONSOLE WELCOME MESSAGE ==========
console.log('%c Leave Management System ', 'background: #667eea; color: white; font-size: 20px; padding: 10px;');
console.log('%c Powered by JSP + Servlet + JDBC ', 'color: #667eea; font-size: 14px;');
console.log('%c Version 1.0.0 ', 'color: #999; font-size: 12px;');

// ========== END OF SCRIPT ==========