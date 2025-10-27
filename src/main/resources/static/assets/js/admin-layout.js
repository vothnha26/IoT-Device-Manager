// admin-layout.js
// Small helper to toggle admin sidebar on mobile and manage overlay.
(function () {
    'use strict';

    function ensureOverlay() {
        let overlay = document.querySelector('.admin-sidebar-overlay');
        if (!overlay) {
            overlay = document.createElement('div');
            overlay.className = 'admin-sidebar-overlay';
            document.body.appendChild(overlay);
        }
        return overlay;
    }

    function toggleSidebar(show) {
        const sidebar = document.querySelector('.admin-sidebar');
        const overlay = ensureOverlay();
        if (!sidebar) return;

        if (typeof show === 'boolean') {
            if (show) {
                sidebar.classList.add('show');
                overlay.classList.add('show');
                document.body.classList.add('admin-sidebar-open');
            } else {
                sidebar.classList.remove('show');
                overlay.classList.remove('show');
                document.body.classList.remove('admin-sidebar-open');
            }
            return;
        }

        // toggle
        const isOpen = sidebar.classList.contains('show');
        if (isOpen) {
            sidebar.classList.remove('show');
            overlay.classList.remove('show');
            document.body.classList.remove('admin-sidebar-open');
        } else {
            sidebar.classList.add('show');
            overlay.classList.add('show');
            document.body.classList.add('admin-sidebar-open');
        }
    }

    // Wire up buttons
    document.addEventListener('click', function (e) {
        const btn = e.target.closest('[data-toggle="admin-sidebar"]');
        if (btn) {
            e.preventDefault();
            toggleSidebar();
        }
    });

    // Hide when clicking overlay
    document.addEventListener('click', function (e) {
        if (e.target.classList && e.target.classList.contains('admin-sidebar-overlay')) {
            toggleSidebar(false);
        }
    });

    // Also close on escape
    document.addEventListener('keydown', function (e) {
        if (e.key === 'Escape') {
            const sidebar = document.querySelector('.admin-sidebar');
            if (sidebar && sidebar.classList.contains('show')) {
                toggleSidebar(false);
            }
        }
    });

    // Ensure overlay exists on DOM ready for mobile CSS rules to target
    document.addEventListener('DOMContentLoaded', function () {
        ensureOverlay();
    });
})();
