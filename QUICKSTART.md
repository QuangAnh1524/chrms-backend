# CHRMS Backend - Quick Start

## 🚀 Start Backend (For FE Team)

```bash
# 1. Clone repo
git clone <your-repo>
cd chrms-backend

# 2. Start everything with one command
./start.sh

# OR manually:
docker-compose up -d

# 3. Wait ~30 seconds for app to start
# Check logs:
docker-compose logs -f app
```

## ✅ Test API

**Swagger UI:** http://localhost:8080/api/v1/swagger-ui.html

### Test Accounts
```
Patient:  patient1@test.com / password123
Doctor:   doctor1@test.com / password123
Admin:    admin@chrms.vn / password123
```

### Example API Calls

**1. Register:**
```bash
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "password123",
    "fullName": "Test User",
    "phone": "0912345678",
    "role": "PATIENT"
  }'
```

**2. Login:**
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "patient1@test.com",
    "password": "password123"
  }'
```

Response:
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "type": "Bearer",
    "userId": 2,
    "email": "patient1@test.com",
    "fullName": "Nguyễn Văn An",
    "role": "PATIENT"
  }
}
```

**3. Use Token for Authenticated Requests:**
```bash
curl -X GET http://localhost:8080/api/v1/patients/profile \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

## 📊 Database

- **PostgreSQL:** localhost:5432
- **Database:** chrms_db
- **Username:** chrms_user
- **Password:** chrms_pass123

Connect using any DB client (DBeaver, pgAdmin, etc.)

## 🛑 Stop Services

```bash
docker-compose down        # Stop all
docker-compose down -v     # Stop + remove data (clean start)
```

## 📝 Seed Data

Database comes pre-loaded with:
- 13 Hospitals (10 public + 3 private)
- 65 Departments
- 3 Test patients
- 4 Test doctors
- 10 Common medicines
- 1 Admin account

## 🐛 Troubleshooting

**Port already in use:**
```bash
# Change ports in docker-compose.yml
ports:
  - "8081:8080"  # Use 8081 instead
```

**Database not ready:**
```bash
# Wait longer or check logs
docker-compose logs postgres
```

**Build fails:**
```bash
# Clean and rebuild
docker-compose down -v
docker-compose build --no-cache
docker-compose up -d
```

## 📚 Full Documentation

See [ARCHITECTURE.md](ARCHITECTURE.md) for Clean Architecture structure.
