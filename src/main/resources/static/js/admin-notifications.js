(function(){
    // Simple admin notifications client using SockJS + STOMP
    const socket = new SockJS('/ws');
    const StompLib = window.Stomp || window.StompJs || window.StompJS;
    let client = null;

    function init() {
        const btn = document.getElementById('admin-notifications-btn');
        const dropdown = document.getElementById('admin-notifications-dropdown');
        const container = document.getElementById('admin-notifications');
        
        // Only initialize if the notification elements exist on the page
        if (!container) {
            console.log('Admin notifications not on this page, skipping initialization');
            return;
        }
        
        if (btn && dropdown) {
            btn.addEventListener('click', (e) => {
                e.preventDefault();
                toggleDropdown();
            });
            document.addEventListener('click', (e) => {
                if (!container.contains(e.target)) {
                    hideDropdown();
                }
            });
        }
        
        // Load initial unread count
        loadUnreadCount();
        
        connect();
    }
    
    function loadUnreadCount() {
        fetch('/api/notifications/unread/count', {
            method: 'GET',
            credentials: 'include'
        })
        .then(response => response.json())
        .then(data => {
            const badge = document.getElementById('admin-notifications-badge');
            if (badge && data.count > 0) {
                badge.textContent = data.count;
                badge.dataset.count = data.count;
                badge.style.display = 'inline-block';
            }
        })
        .catch(err => console.error('Error loading unread count:', err));
    }

    function connect() {
        if (!StompLib) return console.error('STOMP library not found');
        if (StompLib.Client) {
            client = new StompLib.Client({
                webSocketFactory: () => socket,
                onConnect: () => {
                    subscribe();
                },
                onStompError: (err) => console.error('STOMP error', err)
            });
            client.activate();
        } else if (window.Stomp && typeof window.Stomp.over === 'function') {
            client = window.Stomp.over(socket);
            client.connect({}, function(frame) { subscribe(); });
        } else {
            console.error('No STOMP available for admin notifications');
        }
    }

    function subscribe() {
        if (!client) return;
        const dest = '/topic/admin/payments';
        if (client.subscribe) {
            if (client.subscribe.length === 1) {
                // stomp v7
                client.subscribe(dest, function(message) {
                    handleMessage(JSON.parse(message.body));
                });
            } else {
                client.subscribe(dest, function(message) {
                    const body = message.body ? JSON.parse(message.body) : {};
                    handleMessage(body);
                });
            }
        }
    }

    function handleMessage(payload) {
        try {
            const list = document.getElementById('admin-notifications-list');
            const badge = document.getElementById('admin-notifications-badge');
            if (!list || !badge) return;

            // ensure array
            const time = new Date().toLocaleString();
            const text = payload && payload.type ? `${time} | ${payload.type} | Đơn #${payload.orderId} | ${payload.amount || ''}` : `${time} | Thanh toán mới`;

            // remove placeholder
            if (list.children.length === 1 && list.children[0].classList.contains('text-muted')) {
                list.innerHTML = '';
            }

            const li = document.createElement('li');
            li.className = 'py-2 border-bottom';
            li.innerHTML = `<div><strong>Đơn #${payload.orderId}</strong></div><div class="small text-muted">${text}</div>`;
            list.prepend(li);

            // update badge
            const count = parseInt(badge.dataset.count || '0', 10) + 1;
            badge.dataset.count = String(count);
            badge.textContent = String(count);
            badge.style.display = 'inline-block';
        } catch (e) {
            console.error('Error handling admin notification', e);
        }
    }

    function toggleDropdown() {
        const el = document.getElementById('admin-notifications-dropdown');
        if (!el) return;
        if (el.style.display === 'none' || el.style.display === '') {
            el.style.display = 'block';
            
            // Load notifications when opening dropdown
            loadNotifications();
            
            // mark as read (reset badge)
            const badge = document.getElementById('admin-notifications-badge');
            if (badge) { badge.style.display = 'none'; badge.dataset.count = '0'; }
        } else {
            el.style.display = 'none';
        }
    }
    
    function loadNotifications() {
        fetch('/api/notifications/unread', {
            method: 'GET',
            credentials: 'include'
        })
        .then(response => response.json())
        .then(notifications => {
            const list = document.getElementById('admin-notifications-list');
            if (!list) return;
            
            if (notifications.length === 0) {
                list.innerHTML = '<p class="text-muted small mb-0">Chưa có thông báo</p>';
                return;
            }
            
            list.innerHTML = '';
            notifications.forEach(notif => {
                const time = new Date(notif.thoiGianTao).toLocaleString('vi-VN');
                const li = document.createElement('div');
                li.className = 'py-2 border-bottom';
                li.innerHTML = `
                    <div><strong>${notif.tieuDe || 'Thông báo'}</strong></div>
                    <div class="small">${notif.noiDung || ''}</div>
                    <div class="small text-muted">${time}</div>
                `;
                list.appendChild(li);
            });
        })
        .catch(err => console.error('Error loading notifications:', err));
    }

    function hideDropdown() {
        const el = document.getElementById('admin-notifications-dropdown');
        if (el) el.style.display = 'none';
    }

    // init on DOMContentLoaded
    document.addEventListener('DOMContentLoaded', init);
})();
