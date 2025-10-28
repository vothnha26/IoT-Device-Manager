#include <WiFi.h>
#include <WebSocketsClient.h>
#include <HTTPClient.h>
#include <ArduinoJson.h>
#include <DHT.h>

// ====== WiFi ======
const char* WIFI_SSID = "codechick";
const char* WIFI_PASS = "12042005";

// ====== Server (Spring Boot) ======
const char* WS_HOST = "10.21.39.39";
const uint16_t WS_PORT = 8080;
const char* WS_PATH = "/ws/device";
const char* HTTP_HOST = "http://10.21.39.39:8080";

// ====== GPIO ======
const int LED1_PIN = 2;   // GPIO2 cho Device ID 2
const int LED2_PIN = 4;   // GPIO4 cho Device ID 3
const int DHT_PIN = 15;   // GPIO15 cho DHT11 (Device ID 4)
const int DO_PIN = 32;    // GPIO32 cho Light Sensor Digital Out (Device ID 18)
const int AO_PIN = 34;    // GPIO34 cho Light Sensor Analog Out (Device ID 18)
const bool LED_ACTIVE_HIGH = true;

// ====== DHT11 Setup ======
#define DHTTYPE DHT11
DHT dht(DHT_PIN, DHTTYPE);

// WebSocket clients cho LED
WebSocketsClient ws1;  // Device ID 2 -> GPIO2
WebSocketsClient ws2;  // Device ID 3 -> GPIO4

// Timer cho đọc cảm biến
unsigned long lastSensorRead = 0;
const unsigned long SENSOR_INTERVAL = 5000;  // 5 giây - dễ test

// ====== Hàm điều khiển LED ======
void setLed(int pin, bool on) {
  digitalWrite(pin, LED_ACTIVE_HIGH ? (on ? HIGH : LOW) : (on ? LOW : HIGH));
  Serial.printf("💡 GPIO%d → %s\n", pin, on ? "ON" : "OFF");
}

// ====== Gửi trạng thái lên server ======
void publishState(WebSocketsClient* ws, int deviceId, bool ledOn) {
  String msg = ledOn ? "hoat_dong" : "tat";
  ws->sendTXT(msg);
  Serial.printf("[Device %d] 📤 Sent: %s\n", deviceId, msg.c_str());
}

// ====== Handler WebSocket chung cho LED ======
void handleWebSocketEvent(int deviceId, int pin, WebSocketsClient* ws, 
                          WStype_t type, uint8_t* payload, size_t length) {
  switch (type) {
    case WStype_CONNECTED: {
      Serial.printf("[Device %d] ✅ Connected (GPIO%d)\n", deviceId, pin);
      
      // Gửi trạng thái hiện tại
      bool currentState = digitalRead(pin) == (LED_ACTIVE_HIGH ? HIGH : LOW);
      publishState(ws, deviceId, currentState);
      break;
    }
      
    case WStype_DISCONNECTED:
      Serial.printf("[Device %d] ❌ Disconnected\n", deviceId);
      break;
      
    case WStype_TEXT: {
      String cmd = String((const char*)payload, length);
      cmd.trim();
      cmd.toLowerCase();
      
      Serial.printf("[Device %d] 📥 Received: %s\n", deviceId, cmd.c_str());

      if (cmd == "hoat_dong") {
        Serial.printf("[Device %d] 💡 Turning GPIO%d ON\n", deviceId, pin);
        setLed(pin, true);
        publishState(ws, deviceId, true);
      } else if (cmd == "tat") {
        Serial.printf("[Device %d] 🌑 Turning GPIO%d OFF\n", deviceId, pin);
        setLed(pin, false);
        publishState(ws, deviceId, false);
      } else {
        Serial.printf("[Device %d] ⚠️  Unknown command: %s\n", deviceId, cmd.c_str());
      }
      break;
    }
    
    default:
      break;
  }
}

// Wrappers - Device 2 → GPIO2, Device 3 → GPIO4
void onWsEvent1(WStype_t type, uint8_t* payload, size_t length) {
  handleWebSocketEvent(2, LED1_PIN, &ws1, type, payload, length);
}

void onWsEvent2(WStype_t type, uint8_t* payload, size_t length) {
  handleWebSocketEvent(3, LED2_PIN, &ws2, type, payload, length);
}

// ====== Gửi dữ liệu cảm biến lên server ======
void sendSensorData(int deviceId, String fieldName, float value) {
  if (WiFi.status() != WL_CONNECTED) {
    Serial.println("⚠️  WiFi not connected, skip sensor data");
    return;
  }

  if (isnan(value)) {
    Serial.printf("⚠️  [Device %d] Invalid sensor reading, skip\n", deviceId);
    return;
  }

  HTTPClient http;
  String url = String(HTTP_HOST) + "/api/data-logs/" + String(deviceId);
  
  http.begin(url);
  http.setTimeout(5000); // 5s timeout
  http.addHeader("Content-Type", "application/json");

  // Tạo JSON payload
  StaticJsonDocument<256> doc;
  doc["maThietBi"] = deviceId;
  doc["tenTruong"] = fieldName;
  doc["kieuGiaTri"] = 1;  // 1 = số
  doc["giaTriSo"] = value;
  
  String payload;
  serializeJson(doc, payload);

  int httpCode = http.POST(payload);
  
  if (httpCode > 0) {
    if (httpCode == 200 || httpCode == 201) {
      Serial.printf("📊 [Device %d] Sensor data sent: %s = %.2f\n", deviceId, fieldName.c_str(), value);
    } else {
      Serial.printf("⚠️  [Device %d] HTTP error: %d\n", deviceId, httpCode);
    }
  } else {
    Serial.printf("❌ [Device %d] HTTP request failed: %s\n", deviceId, http.errorToString(httpCode).c_str());
  }
  
  http.end();
}

// ====== Kết nối WiFi ======
void connectWiFi() {
  WiFi.mode(WIFI_STA);
  WiFi.begin(WIFI_SSID, WIFI_PASS);
  
  Serial.println("\n========================================");
  Serial.printf("🔄 Connecting to: %s\n", WIFI_SSID);
  Serial.println("========================================");
  
  int attempts = 0;
  while (WiFi.status() != WL_CONNECTED && attempts < 60) {
    delay(500);
    Serial.print(".");
    if (++attempts % 40 == 0) Serial.printf("\n   (%d/60)\n", attempts);
  }
  
  if (WiFi.status() == WL_CONNECTED) {
    Serial.printf("\n✅ Connected! IP: %s\n", WiFi.localIP().toString().c_str());
  } else {
    Serial.println("\n❌ Failed! Restarting...");
    delay(5000);
    ESP.restart();
  }
}

// ====== Khởi tạo WebSocket ======
void beginWebSocket(WebSocketsClient* ws, int deviceId, void (*handler)(WStype_t, uint8_t*, size_t)) {
  String url = String(WS_PATH) + "?deviceId=" + String(deviceId);
  ws->begin(WS_HOST, WS_PORT, url.c_str());
  ws->onEvent(handler);
  ws->setReconnectInterval(2000);
  ws->enableHeartbeat(15000, 3000, 2);
  Serial.printf("[Device %d] 🔗 %s:%d%s\n", deviceId, WS_HOST, WS_PORT, url.c_str());
}

// ====== SETUP ======
void setup() {
  Serial.begin(115200);
  delay(1000);
  
  Serial.println("\n╔════════════════════════════════════╗");
  Serial.println("║   ESP32 IoT Device Controller   ║");
  Serial.println("╚════════════════════════════════════╝");
  
  // Khởi tạo GPIO cho LED
  pinMode(LED1_PIN, OUTPUT);
  pinMode(LED2_PIN, OUTPUT);
  setLed(LED1_PIN, false);
  setLed(LED2_PIN, false);
  
  // Khởi tạo Light Sensor pins
  pinMode(DO_PIN, INPUT);   // Digital output từ cảm biến
  pinMode(AO_PIN, INPUT);   // Analog output từ cảm biến
  
  // Khởi tạo DHT11
  dht.begin();
  
  Serial.printf("📌 Device 2 → GPIO%d, Device 3 → GPIO%d\n", LED1_PIN, LED2_PIN);
  Serial.printf("🌡️  DHT11 Sensor (Device 4) → GPIO%d\n", DHT_PIN);
  Serial.printf("💡 Light Sensor (Device 18) → DO:GPIO%d, AO:GPIO%d\n\n", DO_PIN, AO_PIN);

  connectWiFi();
  Serial.println();

  // Khởi tạo WebSocket: Device 2 → GPIO2, Device 3 → GPIO4
  beginWebSocket(&ws1, 2, onWsEvent1);
  beginWebSocket(&ws2, 3, onWsEvent2);
  
  Serial.println("\n✅ Setup complete!\n");
}

// ====== LOOP ======
void loop() {
  // Xử lý WebSocket cho LED
  ws1.loop();
  ws2.loop();

  // Kiểm tra WiFi
  if (WiFi.status() != WL_CONNECTED) {
    Serial.println("⚠️  WiFi lost, reconnecting...");
    connectWiFi();
  }

  // Đọc và gửi dữ liệu cảm biến định kỳ
  unsigned long currentMillis = millis();
  if (currentMillis - lastSensorRead >= SENSOR_INTERVAL) {
    lastSensorRead = currentMillis;
    
    // Đọc nhiệt độ và độ ẩm từ DHT11
    float humidity = dht.readHumidity();
    float temperature = dht.readTemperature();
    
    Serial.println("\n🌡️  DHT11 Reading:");
    Serial.printf("   Temperature: %.2f°C\n", temperature);
    Serial.printf("   Humidity: %.2f%%\n", humidity);
    
    // Gửi dữ liệu lên server (Device ID 4)
    sendSensorData(4, "nhiet_do", temperature);
    delay(200);  // Chờ ngắn giữa 2 request
    sendSensorData(4, "do_am", humidity);
    
    // Đọc dữ liệu từ Light Sensor
    int digitalValue = digitalRead(DO_PIN);  // 0 = sáng, 1 = tối
    int analogValue = analogRead(AO_PIN);    // 0-4095 (12-bit ADC)
    
    Serial.println("\n💡 Light Sensor Reading:");
    Serial.printf("   Digital: %d (%s)\n", digitalValue, digitalValue == 0 ? "Bright" : "Dark");
    Serial.printf("   Analog: %d (0-4095)\n", analogValue);
    
    // Gửi dữ liệu lên server (Device ID 18)
    delay(200);
    sendSensorData(18, "do_sang_digital", digitalValue);
    delay(200);
    sendSensorData(18, "do_sang_analog", analogValue);
  }
}
