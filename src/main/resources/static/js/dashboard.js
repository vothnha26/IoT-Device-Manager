let stompClientClassic = null;
let stompClientV7 = null;
let clientType = null; // 'classic' or 'v7'
let pendingSubs = [];
let chartTempObj = null;
let chartHumObj = null;
let pieSwitchObj = null;
let currentSimId = null;

function connect() {
    const socket = new SockJS('/stomp');
    const StompLib = window.Stomp || window.StompJs || window.StompJS;

    if (StompLib && StompLib.Client) {
        // @stomp/stompjs v7 style
        stompClientV7 = new StompLib.Client({
            webSocketFactory: () => socket,
            onConnect: (frame) => {
                console.log('Stomp v7 connected', frame);
                clientType = 'v7';
                // process pending subscriptions
                pendingSubs.forEach(s => doSubscribe(s));
                pendingSubs = [];
            },
            onStompError: (err) => console.error('STOMP error', err)
        });
        stompClientV7.activate();
    } else if (window.Stomp && typeof window.Stomp.over === 'function') {
        // classic StompJS
        stompClientClassic = window.Stomp.over(socket);
        stompClientClassic.connect({}, function (frame) {
            console.log('Stomp classic connected: ' + frame);
            clientType = 'classic';
        });
    } else {
        console.error('No STOMP library available in the page.');
    }
}

function subscribeToTopics(roomId, deviceId) {
    if (!stompClientClassic && !stompClientV7) connect();
    if (deviceId) doSubscribe({dest: '/topic/device/' + deviceId, listId: 'deviceEvents'});
    if (roomId) doSubscribe({dest: '/topic/room/' + roomId, listId: 'roomEvents'});
}

function doSubscribe(sub) {
    if (clientType === 'classic' && stompClientClassic) {
        stompClientClassic.subscribe(sub.dest, function (msg) {
            const body = JSON.parse(msg.body);
            addEvent(sub.listId, body);
        });
        return;
    }

    if (clientType === 'v7' && stompClientV7 && stompClientV7.active) {
        stompClientV7.subscribe(sub.dest, function(message) {
            // message.body is a string
            const body = JSON.parse(message.body);
            addEvent(sub.listId, body);
        });
        return;
    }

    // not connected yet; queue subscription
    pendingSubs.push(sub);
}

function addEvent(listId, payload) {
    const el = document.getElementById(listId);
    const li = document.createElement('li');
    li.textContent = `${new Date(payload.timestamp).toLocaleString()} | device:${payload.deviceId} | type:${payload.type} | value:${payload.value || ''} | state:${payload.state || ''}`;
    el.prepend(li);
    // keep max 200
    while (el.children.length > 200) el.removeChild(el.lastChild);

    // update charts if appropriate
    try {
        if (payload.type === 'temperature' && chartTempObj) {
            pushToChart(chartTempObj, payload);
        }
        if (payload.type === 'humidity' && chartHumObj) {
            pushToChart(chartHumObj, payload);
        }
        if (payload.type === 'switch' && pieSwitchObj) {
            // update pie by basic toggle counting (for demo we'll just randomize)
            // For real app, maintain state map per device
            // Here, we will increment the 'ON' slice slightly if state=ON
            if (payload.state === 'ON') {
                pieSwitchObj.data.datasets[0].data[0] = (pieSwitchObj.data.datasets[0].data[0] || 0) + 1;
            } else {
                pieSwitchObj.data.datasets[0].data[1] = (pieSwitchObj.data.datasets[0].data[1] || 0) + 1;
            }
            pieSwitchObj.update();
        }
    } catch (e) {
        console.error('Chart update error', e);
    }
}

document.getElementById('subscribeBtn').addEventListener('click', () => {
    const roomId = document.getElementById('roomId').value;
    const deviceId = document.getElementById('deviceId').value;
    subscribeToTopics(roomId ? parseInt(roomId) : null, deviceId ? parseInt(deviceId) : null);
});

// auto connect so user can immediately publish test telemetry
connect();

// Initialize charts references after DOMContentLoaded
document.addEventListener('DOMContentLoaded', function () {
    const t = document.getElementById('chartTemp');
    if (t && t._chartjs) chartTempObj = t._chartjs;
    // if chart.js created global Chart objects we assigned earlier, try to grab them
    // Fallback: recreate chart objects references
    try {
        if (!chartTempObj) chartTempObj = Chart.getChart('chartTemp');
        if (!chartHumObj) chartHumObj = Chart.getChart('chartHumidity');
        if (!pieSwitchObj) pieSwitchObj = Chart.getChart('pieSwitch');
    } catch (e) {
        console.debug('Chart reference init error', e);
    }

    // Wire simulator buttons
    document.getElementById('simStart')?.addEventListener('click', startSim);
    document.getElementById('simStop')?.addEventListener('click', stopSim);

    // Wire control buttons
    document.querySelectorAll('.btn-control').forEach(b => b.addEventListener('click', controlDevice));
});

function pushToChart(chartObj, payload) {
    const timeLabel = new Date(payload.timestamp).toLocaleTimeString();
    const ds = chartObj.data.datasets[0];
    chartObj.data.labels.push(timeLabel);
    ds.data.push(payload.value || 0);
    // keep last 60 points
    if (chartObj.data.labels.length > 60) {
        chartObj.data.labels.shift();
        ds.data.shift();
    }
    chartObj.update();
}

async function startSim() {
    const deviceId = document.getElementById('simDeviceId').value;
    const roomId = document.getElementById('simRoomId').value;
    const type = document.getElementById('simType').value;
    const rate = document.getElementById('simRate').value;
    const url = `/api/test/sim/start?deviceId=${encodeURIComponent(deviceId)}&roomId=${encodeURIComponent(roomId)}&type=${encodeURIComponent(type)}&ratePerSec=${encodeURIComponent(rate)}&durationSeconds=0`;
    try {
        const res = await fetch(url, { method: 'POST' });
        const json = await res.json();
        currentSimId = json.simulationId;
        document.getElementById('simId').textContent = currentSimId;
    } catch (e) {
        console.error('startSim error', e);
    }
}

async function stopSim() {
    if (!currentSimId) return;
    const url = `/api/test/sim/stop?id=${encodeURIComponent(currentSimId)}`;
    try {
        const res = await fetch(url, { method: 'POST' });
        const json = await res.json();
        document.getElementById('simId').textContent = '-';
        currentSimId = null;
    } catch (e) {
        console.error('stopSim error', e);
    }
}

async function controlDevice(evt) {
    const btn = evt.currentTarget;
    const deviceId = btn.getAttribute('data-device-id');
    // For demo we toggle by sending a fake telemetry state change
    const url = `/api/test/fake-telemetry?deviceId=${encodeURIComponent(deviceId)}&type=switch&state=ON`;
    try {
        await fetch(url, { method: 'POST' });
    } catch (e) {
        console.error('controlDevice error', e);
    }
}
