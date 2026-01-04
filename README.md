# 💾 Cloud file storage

Многопользовательское файловое облако. Пользователи сервиса могут использовать его для загрузки и хранения файлов.

---

## 🛠️ Tech Stack

- **Java 23**
- **Spring BOOT 3.5.6**
- **Test containers**
- **Liquibase**
- **Hibernate ORM**
- **PostgreSQL**
- **Gradle**
- **Docker-compose**
- **S3 хранилище MinIO**
- **Redis**
- **Nginx**
- **Swagger/OpenAPI 2.8.6**

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