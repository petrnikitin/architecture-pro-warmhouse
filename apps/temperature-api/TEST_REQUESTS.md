# Temperature API - Тестовые запросы

## 1. Health Check (Базовый)

### cURL
```bash
curl -X GET http://localhost:8081/health
```

### HTTPie
```bash
http GET http://localhost:8081/health
```

### PowerShell
```powershell
Invoke-WebRequest -Uri "http://localhost:8081/health" -Method GET
```

**Ожидаемый ответ:**
```
UP
```

---

## 2. Health Check (Spring Actuator)

### cURL
```bash
curl -X GET http://localhost:8081/actuator/health
```

### HTTPie
```bash
http GET http://localhost:8081/actuator/health
```

### PowerShell
```powershell
Invoke-RestMethod -Uri "http://localhost:8081/actuator/health" -Method GET
```

**Ожидаемый ответ:**
```json
{
  "status": "UP"
}
```

---

## 3. Получение температуры для города

### cURL
```bash
curl -X GET "http://localhost:8081/temperature?location=Moscow"
```

### HTTPie
```bash
http GET "http://localhost:8081/temperature?location=Moscow"
```

### PowerShell
```powershell
Invoke-RestMethod -Uri "http://localhost:8081/temperature?location=Moscow" -Method GET
```

**Ожидаемый ответ:**
```json
{
  "value": 15.3,
  "unit": "celsius",
  "timestamp": "2025-11-18T10:30:45.123456Z",
  "location": "Moscow",
  "status": "active",
  "sensor_id": "0",
  "sensor_type": "temperature",
  "description": "Temperature sensor in Moscow",
  "condition": "Cloudy",
  "humidity": 65,
  "windSpeed": 8
}
```

---

## 4. Получение температуры для комнаты (Living Room)

### cURL
```bash
curl -X GET "http://localhost:8081/temperature?location=Living%20Room"
```

### HTTPie
```bash
http GET "http://localhost:8081/temperature" location=="Living Room"
```

### PowerShell
```powershell
Invoke-RestMethod -Uri "http://localhost:8081/temperature?location=Living%20Room" -Method GET
```

**Ожидаемый ответ:**
```json
{
  "value": 22.5,
  "unit": "celsius",
  "timestamp": "2025-11-18T10:31:12.789012Z",
  "location": "Living Room",
  "status": "normal",
  "sensor_id": "1",
  "sensor_type": "temperature",
  "description": "Temperature sensor in Living Room",
  "condition": "Clear",
  "humidity": 45,
  "windSpeed": 3
}
```

---

## 5. Получение температуры для Bedroom

### cURL
```bash
curl -X GET "http://localhost:8081/temperature?location=Bedroom"
```

### HTTPie
```bash
http GET "http://localhost:8081/temperature?location=Bedroom"
```

### PowerShell
```powershell
Invoke-RestMethod -Uri "http://localhost:8081/temperature?location=Bedroom" -Method GET
```

---

## 6. Получение температуры для Kitchen

### cURL
```bash
curl -X GET "http://localhost:8081/temperature?location=Kitchen"
```

### HTTPie
```bash
http GET "http://localhost:8081/temperature?location=Kitchen"
```

### PowerShell
```powershell
Invoke-RestMethod -Uri "http://localhost:8081/temperature?location=Kitchen" -Method GET
```

---

## 7. Получение температуры по sensor_id (для Go-монолита)

### cURL
```bash
# Sensor ID 1 = Living Room
curl -X GET "http://localhost:8081/temperature/1"

# Sensor ID 2 = Bedroom
curl -X GET "http://localhost:8081/temperature/2"

# Sensor ID 3 = Kitchen
curl -X GET "http://localhost:8081/temperature/3"
```

### HTTPie
```bash
http GET "http://localhost:8081/temperature/1"
```

### PowerShell
```powershell
Invoke-RestMethod -Uri "http://localhost:8081/temperature/1" -Method GET
```

**Ожидаемый ответ:**
```json
{
  "value": 23.7,
  "unit": "celsius",
  "timestamp": "2025-11-18T10:35:00.000Z",
  "location": "Living Room",
  "status": "ok",
  "sensor_id": "1",
  "sensor_type": "temperature",
  "description": "Temperature sensor in Living Room",
  "condition": "Snowy",
  "humidity": 78,
  "windSpeed": 12
}
```

---

## 8. Запрос без параметров (ошибка 400)

### cURL
```bash
curl -X GET "http://localhost:8081/temperature"
```

### HTTPie
```bash
http GET "http://localhost:8081/temperature"
```

### PowerShell
```powershell
Invoke-WebRequest -Uri "http://localhost:8081/temperature" -Method GET
```

**Ожидаемый ответ:**
```
HTTP/1.1 400 Bad Request
```

---

## 8. Множественные запросы (проверка рандомности)

### Bash Script
```bash
#!/bin/bash
echo "Запрос 1:"
curl -s "http://localhost:8081/temperature?location=Moscow" | jq '.temperature'

echo "Запрос 2:"
curl -s "http://localhost:8081/temperature?location=Moscow" | jq '.temperature'

echo "Запрос 3:"
curl -s "http://localhost:8081/temperature?location=Moscow" | jq '.temperature'

echo "Запрос 4:"
curl -s "http://localhost:8081/temperature?location=Moscow" | jq '.temperature'

echo "Запрос 5:"
curl -s "http://localhost:8081/temperature?location=Moscow" | jq '.temperature'
```

### PowerShell Script
```powershell
1..5 | ForEach-Object {
    Write-Host "Запрос $_:"
    $response = Invoke-RestMethod -Uri "http://localhost:8081/temperature?location=Moscow" -Method GET
    Write-Host "Температура: $($response.temperature)°C, Условия: $($response.condition)"
}
```

**Ожидаемый результат:** Каждый запрос должен возвращать разную температуру.

---

## 9. Различные города

### cURL
```bash
# Санкт-Петербург
curl -X GET "http://localhost:8081/temperature?location=Saint%20Petersburg"

# Новосибирск
curl -X GET "http://localhost:8081/temperature?location=Novosibirsk"

# Екатеринбург
curl -X GET "http://localhost:8081/temperature?location=Yekaterinburg"

# Казань
curl -X GET "http://localhost:8081/temperature?location=Kazan"
```

---

## 10. Полный тест с форматированием (jq)

### cURL + jq
```bash
curl -s "http://localhost:8081/temperature?location=Moscow" | jq '.'
```

### Результат с красивым форматированием:
```json
{
  "location": "Moscow",
  "temperature": -12.7,
  "unit": "celsius",
  "condition": "Snowy",
  "humidity": 78,
  "windSpeed": 15,
  "timestamp": "2025-11-18T10:35:22.456789Z",
  "source": "mock-weather-service"
}
```

---

## Postman Collection

Можно импортировать следующую коллекцию в Postman:

```json
{
  "info": {
    "name": "Temperature API Tests",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "Health Check",
      "request": {
        "method": "GET",
        "header": [],
        "url": {
          "raw": "http://localhost:8081/health",
          "protocol": "http",
          "host": ["localhost"],
          "port": "8081",
          "path": ["health"]
        }
      }
    },
    {
      "name": "Actuator Health",
      "request": {
        "method": "GET",
        "header": [],
        "url": {
          "raw": "http://localhost:8081/actuator/health",
          "protocol": "http",
          "host": ["localhost"],
          "port": "8081",
          "path": ["actuator", "health"]
        }
      }
    },
    {
      "name": "Get Temperature - Moscow",
      "request": {
        "method": "GET",
        "header": [],
        "url": {
          "raw": "http://localhost:8081/temperature?location=Moscow",
          "protocol": "http",
          "host": ["localhost"],
          "port": "8081",
          "path": ["temperature"],
          "query": [
            {
              "key": "location",
              "value": "Moscow"
            }
          ]
        }
      }
    },
    {
      "name": "Get Temperature - Living Room",
      "request": {
        "method": "GET",
        "header": [],
        "url": {
          "raw": "http://localhost:8081/temperature?location=Living Room",
          "protocol": "http",
          "host": ["localhost"],
          "port": "8081",
          "path": ["temperature"],
          "query": [
            {
              "key": "location",
              "value": "Living Room"
            }
          ]
        }
      }
    },
    {
      "name": "Get Temperature - Bedroom",
      "request": {
        "method": "GET",
        "header": [],
        "url": {
          "raw": "http://localhost:8081/temperature?location=Bedroom",
          "protocol": "http",
          "host": ["localhost"],
          "port": "8081",
          "path": ["temperature"],
          "query": [
            {
              "key": "location",
              "value": "Bedroom"
            }
          ]
        }
      }
    },
    {
      "name": "Get Temperature - Kitchen",
      "request": {
        "method": "GET",
        "header": [],
        "url": {
          "raw": "http://localhost:8081/temperature?location=Kitchen",
          "protocol": "http",
          "host": ["localhost"],
          "port": "8081",
          "path": ["temperature"],
          "query": [
            {
              "key": "location",
              "value": "Kitchen"
            }
          ]
        }
      }
    },
    {
      "name": "Get Temperature - No Location (Error)",
      "request": {
        "method": "GET",
        "header": [],
        "url": {
          "raw": "http://localhost:8081/temperature",
          "protocol": "http",
          "host": ["localhost"],
          "port": "8081",
          "path": ["temperature"]
        }
      }
    }
  ]
}
```

---

## Тестирование через Docker

Если сервис запущен в Docker, используйте те же команды. Убедитесь, что контейнер работает:

```bash
# Проверить статус контейнера
docker ps | grep temperature-api

# Посмотреть логи
docker logs smarthome-temperature-api

# Следить за логами в реальном времени
docker logs -f smarthome-temperature-api
```

---

## Автоматический тест (Bash)

```bash
#!/bin/bash

echo "=== Temperature API Test Suite ==="
echo ""

# Цвета для вывода
GREEN='\033[0;32m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Тест 1: Health Check
echo "Test 1: Health Check"
response=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8081/health)
if [ "$response" -eq 200 ]; then
    echo -e "${GREEN}✓ PASSED${NC}"
else
    echo -e "${RED}✗ FAILED (HTTP $response)${NC}"
fi
echo ""

# Тест 2: Actuator Health
echo "Test 2: Actuator Health"
response=$(curl -s http://localhost:8081/actuator/health | jq -r '.status')
if [ "$response" == "UP" ]; then
    echo -e "${GREEN}✓ PASSED${NC}"
else
    echo -e "${RED}✗ FAILED${NC}"
fi
echo ""

# Тест 3: Get Temperature
echo "Test 3: Get Temperature for Moscow"
temp=$(curl -s "http://localhost:8081/temperature?location=Moscow" | jq -r '.temperature')
if [ ! -z "$temp" ] && [ "$temp" != "null" ]; then
    echo -e "${GREEN}✓ PASSED (Temperature: ${temp}°C)${NC}"
else
    echo -e "${RED}✗ FAILED${NC}"
fi
echo ""

# Тест 4: Random Temperature
echo "Test 4: Random Temperature Check"
temp1=$(curl -s "http://localhost:8081/temperature?location=Moscow" | jq -r '.temperature')
sleep 1
temp2=$(curl -s "http://localhost:8081/temperature?location=Moscow" | jq -r '.temperature')
if [ "$temp1" != "$temp2" ]; then
    echo -e "${GREEN}✓ PASSED (${temp1}°C != ${temp2}°C)${NC}"
else
    echo -e "${RED}✗ FAILED (Temperatures are the same)${NC}"
fi
echo ""

# Тест 5: Missing Location Parameter
echo "Test 5: Missing Location Parameter (should fail)"
response=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8081/temperature)
if [ "$response" -eq 400 ]; then
    echo -e "${GREEN}✓ PASSED (HTTP 400)${NC}"
else
    echo -e "${RED}✗ FAILED (Expected 400, got $response)${NC}"
fi
echo ""

echo "=== Tests Complete ==="
```

Сохраните в `test_temperature_api.sh`, дайте права на выполнение и запустите:

```bash
chmod +x test_temperature_api.sh
./test_temperature_api.sh
```
