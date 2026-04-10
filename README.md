# Spring PetClinic Monorepo

A monorepo combining the Spring PetClinic REST backend and Angular frontend into a single project with two independently runnable components.

## Structure

```
spring-petclinic-monorepo/
├── backend/     # Spring Boot REST API  (spring-petclinic-rest)
└── frontend/    # Angular SPA           (spring-petclinic-angular)
```

## Running the Application

Open **two terminals** and run each command in its own terminal:

### Terminal 1 — Backend
```bash
cd backend
.\mvnw.cmd spring-boot:run
```
The API will be available at: http://localhost:9966/petclinic/api/

### Terminal 2 — Frontend
```bash
cd frontend
npm install       # only needed once
npm start
```
The UI will be available at: http://localhost:4200/

> The frontend is pre-configured to call the backend at `localhost:9966`. No extra configuration needed.

---

## Branch Workflow

| Branch              | Purpose                                       |
|---------------------|-----------------------------------------------|
| `main`              | Backend feature development (`backend/`)      |
| `frontend-features` | Frontend feature development (`frontend/`)    |

### Working on frontend features while using the main backend

1. In **Terminal 1**, ensure you are on the `main` branch and start the backend:
   ```bash
   git checkout main
   cd backend && .\mvnw.cmd spring-boot:run
   ```

2. In **Terminal 2**, switch to the frontend branch and start the frontend:
   ```bash
   git checkout frontend-features
   cd frontend && npm start
   ```

The frontend dev server (`ng serve`) will automatically proxy API calls to the backend running from `main`.

---

## Technology Stack

| Layer    | Technology                        | Port  |
|----------|-----------------------------------|-------|
| Backend  | Java 17, Spring Boot, H2 (in-mem) | 9966  |
| Frontend | Angular, TypeScript               | 4200  |
