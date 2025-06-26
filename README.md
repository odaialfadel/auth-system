# 🛡️ Auth-System: Login & Registration Microservice

A standalone microservice system built with **Java Spring Boot** and an **Angular** frontend to handle **user login** and **registration** securely and efficiently. Authentication and user identity management are handled via **Keycloak**, integrated with **PostgreSQL** for persistent storage and user data.

---

## 🚀 Features

- 🔐 User authentication and registration
- 🧾 JWT-based authentication powered by Keycloak
- 📦 Shared module for consistent request/response handling
- 🧩 Modular, scalable architecture
- 🐳 Docker-based setup for Keycloak and PostgreSQL
- ✅ CI/CD-ready and developer-friendly

---

## ⚙️ Tech Stack

| Layer         | Technology             |
|---------------|------------------------|
| Backend       | Java 21, Spring Boot   |
| Frontend      | Angular                |
| Auth Provider | Keycloak               |
| Database      | PostgreSQL             |
| DevOps        | Docker, Docker Compose |
| Build Tool    | Gradle 8+              |

---

## 📦 Prerequisites

Make sure the following are installed:

- [Java 21](https://adoptium.net/)
- [Gradle 8+](https://gradle.org/)
- [Docker](https://www.docker.com/) and [Docker Compose](https://docs.docker.com/compose/)
- [Node.js and npm](https://nodejs.org/)
- [IntelliJ IDEA](https://www.jetbrains.com/idea/) (Enable **Annotation Processing**)

> ✅ IntelliJ:  
> `Settings > Build, Execution, Deployment > Compiler > Annotation Processors > Enable annotation processing`

---

## 🧰 Project Structure

```
auth-system/
├── auth-service/               # Spring Boot backend
├── auth-frontend/              # Angular frontend
├── shared/                     # Shared DTOs and types
├── gradle/libs.versions.toml   # Centralized dependency versions
├── keycloak-data/              # Preconfigured Keycloak realm export
├── docker-compose.yml          # Docker config for Keycloak & PostgreSQL
├── README.md                   # Project documentation
```

---

## 🏁 Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/odaialfadel/auth-system.git
cd auth-system
```

### 2. Configure the Environment

- Adjust `docker-compose.yml` if needed (e.g., ports, usernames).
- Ensure the `application.yml` in the backend is synced with Docker services.

### 3. Start Required Services

Start PostgreSQL and Keycloak containers using the Keycloak profile:

```bash
docker-compose --profile keycloak up -d
```

> 🧠 Other services will be auto-started by the backend when needed.

### 4. Start the Backend (Spring Boot)

From `auth-service/`:

```bash
./gradlew bootRun
```

### 5. Start the Frontend (Angular)

From `auth-frontend/`:

```bash
npm install
npm start
```

---

## 🔒 Default Keycloak Setup (Development)

Access Keycloak Admin Console at:  
🔗 `http://localhost:8081`

| Field          | Value        |
|----------------|--------------|
| Admin Username | `admin`      |
| Admin Password | `admin`      |
| Realm          | `auth-realm` |
| Client ID      | `frontend`   |

> ⚠️ Change these values for production environments!

---

## 🧪 Developer Notes

- Backend: `http://localhost:8080`
- Frontend: `http://localhost:4200`
- Frontend config: `environment.ts` handles base URLs
- Backend handles service startup via embedded Docker logic (where supported)
- Shared module helps unify response/request structures between backend and frontend

---

## 📄 License

This project is licensed under the **MIT License**.  
See [LICENSE]() for full details.

---

## 🙌 Contributions

Pull requests are welcome!  
Please fork the repo, create a new branch, and submit your changes via a pull request.

---

## 📬 Contact

Need help? Found a bug?  
Open an issue at 👉 [GitHub Issues](https://github.com/odaialfadel/auth-service/issues)