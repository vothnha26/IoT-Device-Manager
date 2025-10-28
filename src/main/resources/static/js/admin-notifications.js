(function(){
    // Simple admin notifications client using SockJS + STOMP
    const socket = new SockJS('/ws');
    const StompLib = window.Stomp || window.StompJs || window.StompJS;
    let client = null;

    function init() {
        const btn = document.getElementById('admin-notifications-btn');
        const dropdown = document.getElementById('admin-notifications-dropdown');
        if (btn && dropdown) {
            btn.addEventListener('click', (e) => {
                e.preventDefault();
                toggleDropdown();
            });
            document.addEventListener('click', (e) => {
                if (!document.getElementById('admin-notifications').contains(e.target)) {
                    hideDropdown();
                }
            });
        }
        connect();
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
            // mark as read (reset badge)
            const badge = document.getElementById('admin-notifications-badge');
            if (badge) { badge.style.display = 'none'; badge.dataset.count = '0'; }
        } else {
            el.style.display = 'none';
        }
    }

    function hideDropdown() {
        const el = document.getElementById('admin-notifications-dropdown');
        if (el) el.style.display = 'none';
    }

    // init on DOMContentLoaded
    document.addEventListener('DOMContentLoaded', init);
})();
