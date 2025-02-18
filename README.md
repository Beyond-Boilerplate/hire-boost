
# HireBoost - Automated LinkedIn Engagement & Branding

HireBoost is a **Spring Boot & Temporal-based automation platform** 

---

## 🚀 **Getting Started**
Follow these steps to **set up and run** the project using **Docker Compose**.

### **1️⃣ Prerequisites**
Ensure you have the following installed:
- [Docker](https://docs.docker.com/get-docker/)
- [Docker Compose](https://docs.docker.com/compose/install/)
- `git` (for cloning the repository)

---

## 🏗️ **Setting Up the Project**

### **1️⃣ Clone the Repository**
```sh
git clone https://github.com/Beyond-Boilerplate/hire-boost.git
cd hireboost
```

### **2️⃣ Build the Project**
Before running the app, build it using Gradle:
```sh
./gradlew build
```

### **3️⃣ Start All Services (App + Temporal)**
Use Docker Compose to bring up the **entire system**:
```sh
docker-compose up --build
```

This will:
✅ Start **HireBoost App** on `http://localhost:8081`  
✅ Start **Temporal UI** on `http://localhost:8080`  
✅ Start **PostgreSQL Databases** for **HireBoost & Temporal**  
✅ Start **Elasticsearch & DB Viewer**

---

## 🔍 **Verifying Services**

### **1️⃣ Check Application Logs**
```sh
docker logs -f hireboost-app
```

### **2️⃣ Test Health Check Endpoint**
```sh
curl http://localhost:8081/actuator/health
```
Expected Output:
```json
{"status":"UP"}
```

### **3️⃣ Access Temporal UI**
[http://localhost:8080](http://localhost:8080)

### **4️⃣ Access Database Viewer (pgAdmin)**
[http://localhost:8082](http://localhost:8082)  
Login:
- **Email:** `admin@hireboost.com`
- **Password:** `admin`

---

## 🔄 **Stopping & Restarting the Project**

### **🛑 Stop Everything**
```sh
docker-compose down -v
```

### **♻️ Restart the Services**
```sh
docker-compose up --build
```

---

## 🛠 **Troubleshooting**
### 🔹 **Docker Compose Fails with Port Conflicts**
**Fix:** Stop any existing services running on conflicting ports:
```sh
docker ps
docker stop CONTAINER_ID
docker rm CONTAINER_ID
```

### 🔹 **Temporal Fails to Connect to PostgreSQL**
Ensure Temporal **uses the correct database host**:
```yaml
environment:
  - POSTGRES_SEEDS=temporal-postgresql
```

### 🔹 **Check Logs for Errors**
```sh
docker logs hireboost-app
docker logs temporal
```

---

./gradlew bootRun --args='--spring.profiles.active=api'
./gradlew bootRun --args='--spring.profiles.active=linkedin-post-worker'

## ✨ **Contributing**
1. **Fork the repository**
2. **Create a new feature branch** (`git checkout -b feature-name`)
3. **Commit changes** (`git commit -m "Add new feature"`)
4. **Push to GitHub** (`git push origin feature-name`)
5. **Create a Pull Request** 🚀

---

## 📜 **License**
This project is licensed under the **MIT License**.

---