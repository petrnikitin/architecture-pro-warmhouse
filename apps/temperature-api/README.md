# Temperature API - Spring Boot Microservice

Mock-сервис внешнего API для получения температурных данных, созданный в рамках **Задания 5** проекта "Тёплый дом".

## Технологии

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Web** - REST API
- **Spring Boot Actuator** - health checks
- **Maven** - сборка и управление зависимостями
- **Lombok** - уменьшение boilerplate кода

## Структура проекта

```
temperature-api/
├── src/main/java/com/warmhouse/temperature/
│   ├── TemperatureApiApplication.java      # Main класс Spring Boot
│   ├── controller/
│   │   └── TemperatureController.java      # REST endpoints
│   ├── service/
│   │   └── TemperatureService.java         # Бизнес-логика генерации температуры
│   └── dto/
│       └── TemperatureResponse.java        # DTO для ответа (совместим с Go)
├── src/main/resources/
│   └── application.yml                      # Spring конфигурация
├── Dockerfile                               # Multi-stage Docker build
├── pom.xml                                  # Maven dependencies
├── TEST_REQUESTS.md                         # Детальные примеры всех запросов
└── README.md                                # Этот файл
```

## API Endpoints

### GET /temperature?location={location}

Получение температуры по названию локации.

**Параметры:**
- `location` (optional) - название комнаты/города
- `sensorId` (optional) - ID датчика

**Пример:**
```bash
curl "http://localhost:8081/temperature?location=Living%20Room"
```

**Ответ:**
```json
{
  "value": 22.5,
  "unit": "celsius",
  "timestamp": "2025-11-18T10:30:45.123Z",
  "location": "Living Room",
  "status": "active",
  "sensor_id": "1",
  "sensor_type": "temperature",
  "description": "Temperature sensor in Living Room",
  "condition": "Clear",
  "humidity": 65,
  "windSpeed": 8
}
```

### GET /temperature/{id}

Получение температуры по ID датчика (используется Go-монолитом).

**Параметры:**
- `id` (path) - ID датчика (1, 2, 3, ...)

**Пример:**
```bash
curl "http://localhost:8081/temperature/1"
```

### GET /health

Простая проверка работоспособности.

**Ответ:** `UP`

### GET /actuator/health

Детальная проверка работоспособности (Spring Actuator).

**Ответ:**
```json
{
  "status": "UP"
}
```

## Маппинг Location ↔ Sensor ID

Реализован согласно требованиям задания:

| Location    | Sensor ID |
|-------------|-----------|
| Living Room | 1         |
| Bedroom     | 2         |
| Kitchen     | 3         |
| Other       | 0         |

## Генерация данных

**Температура:**
- Диапазон: от -30°C до +40°C
- Точность: 1 знак после запятой
- Случайная при каждом запросе

**Дополнительные данные:**
- Погодные условия: Clear, Cloudy, Rainy, Snowy, Foggy, Windy
- Влажность: 20-90%
- Скорость ветра: 0-20 м/с
- Статус: active, normal, ok, operational

## Запуск локально

### Через Maven:
```bash
./mvnw spring-boot:run
```

### Через Java:
```bash
./mvnw clean package
java -jar target/temperature-api-1.0.0.jar
```

### Через Docker:
```bash
docker build -t temperature-api .
docker run -p 8081:8081 temperature-api
```

### Через Docker Compose (рекомендуется):
```bash
cd ../
docker-compose up --build
```

## Тестирование

См. подробные примеры в **TEST_REQUESTS.md**

Быстрая проверка:
```bash
# Health check
curl http://localhost:8081/health

# Температура по локации
curl "http://localhost:8081/temperature?location=Living%20Room" | jq

# Температура по sensor_id
curl "http://localhost:8081/temperature/1" | jq

# Рандомность (должна меняться)
for i in {1..5}; do curl -s "http://localhost:8081/temperature/1" | jq '.value'; done
```

## Интеграция с Go-монолитом

Temperature API автоматически интегрируется с Go-приложением `smart_home`:

1. Go-монолит вызывает `GET /temperature/{sensorId}` при запросе списка датчиков
2. Temperature API возвращает случайную температуру в формате, совместимом с Go
3. Go-монолит обновляет значение температуры в реальном времени

**Проверка интеграции:**
```bash
# Создать температурный датчик в Go-приложении
curl -X POST http://localhost:8080/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d '{"location": "Living Room", "type": "temperature", "status": "active"}'

# Получить датчики (с live температурой от temperature-api)
curl http://localhost:8080/api/v1/sensors | jq

# Повторить - температура должна меняться
curl http://localhost:8080/api/v1/sensors | jq '.[].value'
```

## Docker

**Dockerfile особенности:**
- Multi-stage build (Maven сборка + JRE runtime)
- Базовый образ: `eclipse-temurin:17-jre-alpine` (минимальный размер)
- Непривилегированный пользователь `spring:spring`
- Health check через wget
- JVM оптимизация для контейнера

**Переменные окружения:**
- `SERVER_PORT` - порт приложения (default: 8081)
- `SPRING_PROFILES_ACTIVE` - активный Spring профиль

## Логирование

Логи доступны через:
```bash
# В Docker
docker logs smarthome-temperature-api

# Follow логов
docker logs -f smarthome-temperature-api
```

Формат логов:
```
2025-11-18 10:30:45 - Received temperature request for sensor ID: '1'
2025-11-18 10:30:45 - Generated temperature 22.5 for location 'Living Room' (sensor_id: 1)
2025-11-18 10:30:45 - Returning temperature 22.5 for sensor_id: 1
```

## Решение проблем

**Порт уже занят:**
```bash
# Изменить порт
SERVER_PORT=8082 mvn spring-boot:run
```

**Сборка не удаётся:**
```bash
# Очистить Maven кеш
./mvnw clean
rm -rf ~/.m2/repository
./mvnw clean package
```

**Контейнер не стартует:**
```bash
# Пересоздать образ
docker-compose down
docker-compose build --no-cache temperature-api
docker-compose up temperature-api
```

## Соответствие требованиям задания

✅ Реализовано на **Java Spring Boot** (согласно условию)  
✅ Endpoint `/temperature?location=` возвращает **рандомное значение** температуры  
✅ Реализован **маппинг location ↔ sensorId** (по заданию)  
✅ Упаковано в **Docker**  
✅ Добавлено в **docker-compose**  
✅ Порт по умолчанию **8081**  
✅ Формат ответа **совместим с Go-монолитом**  
✅ При каждом вызове возвращается **разная температура**  
✅ Работает с **Postman коллекцией** smarthome-api.postman_collection.json  

## Авторы

Проект "Тёплый дом" - Задание 5  
Temperature API Microservice - Spring Boot Implementation
