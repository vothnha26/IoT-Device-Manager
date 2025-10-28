// Notification WebSocket Handler
let notificationStompClient = null;
let notificationConnected = false;

// Kết nối WebSocket cho notifications
function connectNotificationWebSocket() {
    const currentUserId = getCurrentUserId(); // Lấy userId từ session
    
    if (!currentUserId) {
        console.warn('User not logged in, skipping notification WebSocket connection');
        return;
    }
    
    const socket = new SockJS('/ws');
    notificationStompClient = Stomp.over(socket);
    
    // Disable debug logging
    notificationStompClient.debug = null;
    
    notificationStompClient.connect({}, function(frame) {
        console.log('Notification WebSocket Connected: ' + frame);
        notificationConnected = true;
        
        // Subscribe to user's notification topic
        notificationStompClient.subscribe('/topic/notifications/' + currentUserId, function(message) {
            const notification = JSON.parse(message.body);
            handleNewNotification(notification);
        });
        
        // Subscribe to notification status updates
        notificationStompClient.subscribe('/topic/notifications/status', function(message) {
            console.log('Notification status: ' + message.body);
        });
        
        // Send subscribe confirmation
        notificationStompClient.send('/app/notifications/subscribe', {}, JSON.stringify({
            userId: currentUserId
        }));
        
        // Load initial notifications
        loadNotifications();
        updateNotificationBadge();
        
    }, function(error) {
        console.error('Notification WebSocket connection error: ', error);
        notificationConnected = false;
        
        // Retry connection after 5 seconds
        setTimeout(connectNotificationWebSocket, 5000);
    });
}

// Disconnect notification WebSocket
function disconnectNotificationWebSocket() {
    if (notificationStompClient !== null && notificationConnected) {
        notificationStompClient.disconnect();
        notificationConnected = false;
        console.log('Notification WebSocket Disconnected');
    }
}

// Xử lý thông báo mới
function handleNewNotification(notification) {
    console.log('New notification received:', notification);
    
    // Show toast/alert
    showNotificationToast(notification);
    
    // Update notification list
    addNotificationToList(notification);
    
    // Update badge count
    updateNotificationBadge();
    
    // Play notification sound (optional)
    playNotificationSound();
}

// Hiển thị toast thông báo
function showNotificationToast(notification) {
    // Tạo toast element
    const toast = document.createElement('div');
    toast.className = 'notification-toast';
    toast.innerHTML = `
        <div class="notification-toast-header">
            <i class="bi bi-bell-fill"></i>
            <strong>${notification.tieuDe}</strong>
            <button class="notification-toast-close">&times;</button>
        </div>
        <div class="notification-toast-body">
            ${notification.noiDung}
        </div>
    `;
    
    // Add to body
    document.body.appendChild(toast);
    
    // Show toast
    setTimeout(() => toast.classList.add('show'), 100);
    
    // Close button handler
    toast.querySelector('.notification-toast-close').addEventListener('click', () => {
        toast.classList.remove('show');
        setTimeout(() => toast.remove(), 300);
    });
    
    // Auto hide after 5 seconds
    setTimeout(() => {
        toast.classList.remove('show');
        setTimeout(() => toast.remove(), 300);
    }, 5000);
}

// Thêm thông báo vào danh sách
function addNotificationToList(notification) {
    const notificationList = document.getElementById('notificationList');
    if (!notificationList) return;
    
    const notificationItem = createNotificationItem(notification);
    notificationList.insertBefore(notificationItem, notificationList.firstChild);
}

// Tạo notification item HTML
function createNotificationItem(notification) {
    const item = document.createElement('div');
    item.className = 'notification-item' + (notification.daDoc ? '' : ' unread');
    item.dataset.notificationId = notification.maThongBao;
    
    const iconClass = getNotificationIcon(notification.loaiThongBao);
    const timeAgo = getTimeAgo(notification.thoiGianTao);
    
    item.innerHTML = `
        <div class="notification-icon ${notification.loaiThongBao}">
            <i class="${iconClass}"></i>
        </div>
        <div class="notification-content">
            <div class="notification-title">${notification.tieuDe}</div>
            <div class="notification-message">${notification.noiDung}</div>
            <div class="notification-time">${timeAgo}</div>
        </div>
        <div class="notification-actions">
            ${!notification.daDoc ? '<button class="btn-mark-read" title="Đánh dấu đã đọc"><i class="bi bi-check"></i></button>' : ''}
            <button class="btn-delete" title="Xóa"><i class="bi bi-trash"></i></button>
        </div>
    `;
    
    // Add click handler for mark as read
    if (!notification.daDoc) {
        item.querySelector('.btn-mark-read').addEventListener('click', (e) => {
            e.stopPropagation();
            markNotificationAsRead(notification.maThongBao);
        });
    }
    
    // Add click handler for delete
    item.querySelector('.btn-delete').addEventListener('click', (e) => {
        e.stopPropagation();
        deleteNotification(notification.maThongBao);
    });
    
    // Add click handler for notification item (redirect to URL)
    if (notification.urlLienKet) {
        item.style.cursor = 'pointer';
        item.addEventListener('click', () => {
            if (!notification.daDoc) {
                markNotificationAsRead(notification.maThongBao);
            }
            window.location.href = notification.urlLienKet;
        });
    }
    
    return item;
}

// Lấy icon dựa trên loại thông báo
function getNotificationIcon(loaiThongBao) {
    const icons = {
        'KHU_VUC': 'bi bi-geo-alt-fill',
        'THIET_BI': 'bi bi-cpu-fill',
        'GOI_CUOC': 'bi bi-credit-card-fill',
        'LICH_TRINH': 'bi bi-clock-fill',
        'CANH_BAO': 'bi bi-exclamation-triangle-fill',
        'THONG_TIN': 'bi bi-info-circle-fill',
        'default': 'bi bi-bell-fill'
    };
    return icons[loaiThongBao] || icons['default'];
}

// Tính thời gian "time ago"
function getTimeAgo(timestamp) {
    const now = new Date();
    const time = new Date(timestamp);
    const diff = Math.floor((now - time) / 1000); // seconds
    
    if (diff < 60) return 'Vừa xong';
    if (diff < 3600) return Math.floor(diff / 60) + ' phút trước';
    if (diff < 86400) return Math.floor(diff / 3600) + ' giờ trước';
    if (diff < 2592000) return Math.floor(diff / 86400) + ' ngày trước';
    return time.toLocaleDateString('vi-VN');
}

// Load danh sách thông báo
function loadNotifications() {
    console.log('Loading notifications...');
    fetch('/api/notifications/latest?limit=10')
        .then(response => {
            console.log('Notifications API response status:', response.status);
            return response.json();
        })
        .then(notifications => {
            console.log('Received notifications:', notifications);
            const notificationList = document.getElementById('notificationList');
            if (!notificationList) {
                console.error('Notification list element not found!');
                return;
            }
            
            notificationList.innerHTML = '';
            
            if (notifications && notifications.length > 0) {
                console.log('Displaying', notifications.length, 'notifications');
                notifications.forEach(notification => {
                    notificationList.appendChild(createNotificationItem(notification));
                });
            } else {
                console.log('No notifications to display');
                // Show empty state
                const emptyState = document.createElement('div');
                emptyState.className = 'notification-empty';
                emptyState.innerHTML = `
                    <i class="bi bi-bell-slash"></i>
                    <p>Chưa có thông báo nào</p>
                `;
                notificationList.appendChild(emptyState);
            }
        })
        .catch(error => {
            console.error('Error loading notifications:', error);
            const notificationList = document.getElementById('notificationList');
            if (notificationList) {
                notificationList.innerHTML = `
                    <div class="notification-empty">
                        <i class="bi bi-exclamation-circle"></i>
                        <p>Không thể tải thông báo</p>
                    </div>
                `;
            }
        });
}

// Update notification badge count
function updateNotificationBadge() {
    fetch('/api/notifications/unread/count')
        .then(response => response.json())
        .then(data => {
            const count = data.count || 0;
            const badge = document.querySelector('.notification-badge');
            if (badge) {
                if (count > 0) {
                    badge.textContent = count > 99 ? '99+' : count;
                    badge.style.display = 'inline-block';
                } else {
                    badge.style.display = 'none';
                }
            }
        })
        .catch(error => console.error('Error updating notification badge:', error));
}

// Đánh dấu đã đọc
function markNotificationAsRead(notificationId) {
    fetch(`/api/notifications/${notificationId}/read`, {
        method: 'PUT'
    })
    .then(response => {
        if (response.ok) {
            const item = document.querySelector(`[data-notification-id="${notificationId}"]`);
            if (item) {
                item.classList.remove('unread');
                const markReadBtn = item.querySelector('.btn-mark-read');
                if (markReadBtn) markReadBtn.remove();
            }
            updateNotificationBadge();
        }
    })
    .catch(error => console.error('Error marking notification as read:', error));
}

// Đánh dấu tất cả đã đọc
function markAllAsRead() {
    fetch('/api/notifications/read-all', {
        method: 'PUT'
    })
    .then(response => {
        if (response.ok) {
            document.querySelectorAll('.notification-item.unread').forEach(item => {
                item.classList.remove('unread');
                const markReadBtn = item.querySelector('.btn-mark-read');
                if (markReadBtn) markReadBtn.remove();
            });
            updateNotificationBadge();
        }
    })
    .catch(error => console.error('Error marking all as read:', error));
}

// Xóa thông báo
function deleteNotification(notificationId) {
    if (!confirm('Bạn có chắc muốn xóa thông báo này?')) return;
    
    fetch(`/api/notifications/${notificationId}`, {
        method: 'DELETE'
    })
    .then(response => {
        if (response.ok) {
            const item = document.querySelector(`[data-notification-id="${notificationId}"]`);
            if (item) {
                item.style.animation = 'slideOut 0.3s ease';
                setTimeout(() => item.remove(), 300);
            }
            updateNotificationBadge();
        }
    })
    .catch(error => console.error('Error deleting notification:', error));
}

// Play notification sound
function playNotificationSound() {
    // Optional: Add notification sound
    const audio = new Audio('/sounds/notification.mp3');
    audio.volume = 0.3;
    audio.play().catch(err => console.log('Cannot play notification sound:', err));
}

// Lấy userId từ session/JWT
function getCurrentUserId() {
    // Thử lấy từ localStorage hoặc parse JWT token
    const token = localStorage.getItem('jwt') || sessionStorage.getItem('jwt');
    if (token) {
        try {
            const payload = JSON.parse(atob(token.split('.')[1]));
            return payload.userId || payload.sub;
        } catch (e) {
            console.error('Error parsing JWT:', e);
        }
    }
    
    // Hoặc lấy từ meta tag trong HTML
    const userIdMeta = document.querySelector('meta[name="user-id"]');
    if (userIdMeta) {
        return userIdMeta.content;
    }
    
    // Hoặc lấy từ thuộc tính data của body
    return document.body.dataset.userId;
}

// Toggle notification dropdown
function toggleNotificationDropdown() {
    console.log('🔔 Toggle notification dropdown called');
    const dropdown = document.getElementById('notificationDropdown');
    const bellIcon = document.querySelector('.notification-bell');
    if (dropdown) {
        const isActive = dropdown.classList.contains('active');
        dropdown.classList.toggle('active');
        if (!isActive) {
            loadNotifications();
            // Position dropdown under the bell as an overlay
            positionNotificationDropdown();
            // Listen for viewport changes while open
            window.addEventListener('resize', positionNotificationDropdown);
            window.addEventListener('scroll', positionNotificationDropdown, true);
        } else {
            window.removeEventListener('resize', positionNotificationDropdown);
            window.removeEventListener('scroll', positionNotificationDropdown, true);
        }
    } else {
        console.error('❌ Notification dropdown element not found!');
    }
}

// Compute and set fixed position for the dropdown near the bell icon
function positionNotificationDropdown() {
    const dropdown = document.getElementById('notificationDropdown');
    const bellIcon = document.querySelector('.notification-bell');
    if (!dropdown || !bellIcon || !dropdown.classList.contains('active')) return;
    const rect = bellIcon.getBoundingClientRect();
    const gap = 8; // space between bell and dropdown
    const panelWidth = dropdown.offsetWidth || 370;
    const right = Math.max(16, window.innerWidth - rect.right);
    const top = rect.bottom + gap;
    dropdown.style.position = 'fixed';
    dropdown.style.top = `${top}px`;
    dropdown.style.right = `${right}px`;
}

// Close dropdown when clicking outside
document.addEventListener('click', function(event) {
    const dropdown = document.getElementById('notificationDropdown');
    const bellIcon = document.querySelector('.notification-bell');
    
    if (dropdown && !dropdown.contains(event.target) && !bellIcon.contains(event.target)) {
        dropdown.classList.remove('active');
        window.removeEventListener('resize', positionNotificationDropdown);
        window.removeEventListener('scroll', positionNotificationDropdown, true);
    }
});

// Initialize when DOM is ready
document.addEventListener('DOMContentLoaded', function() {
    console.log('Initializing notification system...');
    
    // Connect to notification WebSocket
    connectNotificationWebSocket();
    
    // Setup bell icon click handler - NOTE: bell already has onclick in HTML
    // Just ensure the function is available globally (already defined above)
    
    // Setup mark all as read button
    const markAllBtn = document.getElementById('markAllAsRead');
    if (markAllBtn) {
        markAllBtn.addEventListener('click', markAllAsRead);
    }
    
    console.log('Notification system initialized');
});

// Disconnect on page unload
window.addEventListener('beforeunload', function() {
    disconnectNotificationWebSocket();
});
