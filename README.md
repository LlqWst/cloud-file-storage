# 💾 Cloud file storage

Многопользовательское файловое облако. Пользователи сервиса могут использовать его для загрузки и хранения файлов.

---

## 🛠️ Tech Stack

![Java](https://img.shields.io/badge/Java-23-orange?logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.6-brightgreen?logo=springboot)
![Testcontainers](https://img.shields.io/badge/Testcontainers-✓-blue?logo=testcontainers)
![Liquibase](https://img.shields.io/badge/Liquibase-✓-2962FF?logo=liquibase&logoColor=white)
![Hibernate](https://img.shields.io/badge/Hibernate-ORM-59666C?logo=hibernate)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-✓-4169E1?logo=postgresql&logoColor=white)
![Gradle](https://img.shields.io/badge/Gradle-✓-02303A?logo=gradle&logoColor=white)
![Docker Compose](https://img.shields.io/badge/Docker%20Compose-✓-2496ED?logo=docker&logoColor=white)
![MinIO](https://img.shields.io/badge/MinIO-S3-FF0000?logo=minio&logoColor=white)
![Redis](https://img.shields.io/badge/Redis-✓-DC382D?logo=redis&logoColor=white)
![Nginx](https://img.shields.io/badge/Nginx-✓-009639?logo=nginx&logoColor=white)
![Swagger](https://img.shields.io/badge/Swagger/OpenAPI-2.8.6-85EA2D?logo=swagger&logoColor=black)

---

## 🖥️ Принцип работы

### 🔑 Аутентификация
- **Sign up** → Регистрация и Sign in нового пользователя
- **Sign in и Log out** → аутентификация возложена на Spring Security
- **Session** → сессии хранятся в Redis

### ☁️ Cloud storage

#### **Возможности:**
- Создавать папки
- Загружать папки рекурсивно
- Загружать файлы
- Перемещать файлы/папки
- Переименовывать файлы/папки
- Скачивать файлы, папки скачиваются в формате zip
- MAX объем файла - 20Мб, MAX объем загрузки - 30МБ, MAX кол-во файлов за одну загрузку - 40

### 📋 Swagger (документация)
- Проект использует **Swagger/OpenAPI** для автоматической генерации документации REST API.
- **Swagger UI** (интерактивная документация, доступна после деплоя приложения): [swagger](http://localhost:8080/swagger-ui/index.html#/)

---

## 🧰 Как запустить

### 1️⃣ **Подготовка**
- **Docker-compose** installed
- **Clone Repository** — https://github.com/LlqWst/cloud-file-storage.git

### 2️⃣ **Конфигурация окружения**
Удалить `pub_` у файла `.pub_env`.
Было `.pub_env` стало `.env`

### 3️⃣ **Деплой через Docker-compose**

Для Linux/MAC/Windows:

Перейдите в папку с файлом `docker-compose`
```bash
cd /путь/к/проекту
```
Разверните проект
```bash
docker-compose up -d --build
```

- App available at [app](http://localhost:3000)
- Minio console at [minio](http://127.0.0.1:9001/login) login - admin_pub, pass - admin_pub
---

## 📚 Дополнительная информация
Проект был завершен в рамках [Java Backend Learning Roadmap](https://zhukovsd.github.io/java-backend-learning-course/projects/cloud-file-storage/)