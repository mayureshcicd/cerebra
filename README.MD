# Cerebra File Processing Application

Welcome to the **Cerebra File Processing Application**, a robust and scalable Spring Boot-based solution designed to handle file uploads, processing, and management with ease. This application leverages modern technologies and best practices to ensure high performance, security, and a seamless user experience.

---

## 🚀 Key Features

### **Core Development**
- **Java 17**: The backbone of the application, ensuring modern and efficient development.
- **Spring Boot 3.3**: Simplifies the development of production-ready applications with minimal configuration.
- **Spring Security**: Provides comprehensive security features to protect your application and data.
- **JSON Web Tokens (JWT)**: Secure authentication and authorization using JWT tokens.

### **Real-Time Notifications**
- **STOMP WebSocket & SockJS Client**: Enables real-time notifications from the backend to the frontend, ensuring users stay updated.
- **Spring WebSocket**: Facilitates seamless communication between the server and clients.

### **Data Handling**
- **Apache POI**: Efficiently reads and processes Excel and CSV files.
- **Spring Data JPA**: Simplifies database interactions with powerful CRUD operations.
- **PostgreSQL**: A reliable and scalable relational database for storing application data.

### **Performance & Monitoring**
- **Spring Cache & Caffeine**: Enhances performance by caching data for faster access.
- **Micrometer & Actuator**: Monitors application performance, tracks file uploads, processing times, and errors.

### **Asynchronous Processing**
- **Async & TaskExecutor**: Ensures non-blocking, asynchronous processing for heavy transactions, improving responsiveness.

### **Validation & Logging**
- **Starter Validation**: Validates request payloads (DTOs) to ensure data integrity.
- **AOP (Aspect-Oriented Programming)**: Centralized logging for better maintainability and debugging.

### **Frontend Design**
- **HTML, CSS, Bootstrap**: Creates a visually appealing and responsive user interface.
- **jQuery**: Handles UI events (e.g., clicks, changes) and performs CRUD operations using AJAX.
- **DataTables**: Renders file processing lists in an interactive and user-friendly table.

---

## ⚙️ Configuration Steps

### **Database Setup**
1. **Create Database & Schema**:
   - Database: `cerebraDB`
   - Schema: `cerebra`

2. **Configure `application.yml`**:
   - Set up the PostgreSQL data source:
     ```yaml
     url: jdbc:postgresql://localhost:5432/cerebraDB?sslmode=require&currentSchema=cerebra&tcpKeepAlive=true
     username: <VALID-POSTGRES-USERNAME>
     password: <VALID-POSTGRES-PASSWORD>
     ```

3. **Set Administrator Email**:
   - In `application.yml`, configure the `userEmail` (line 58) with the administrator's email address.
   - **Why?**: The administrator will receive login credentials when the application starts.
     ```yaml
     userEmail: VALID-ADMINISTRATOR-EMAIL-ADDRESS
     ```   

---

## 👥 Roles & Permissions

### **Administrator**
- View all uploaded files by all users.
- Delete any uploaded file.
- Cannot upload files.

### **User**
- Upload files and view their processing status.
- Cannot view or delete files uploaded by other users.

---

## 🔗 Important Links

- **Swagger UI**: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
- **Web Application**: [http://localhost:8080/index.html](http://localhost:8080/index.html)
- **Performance Metrics**:
  - Success Metrics: [http://localhost:8080/actuator/metrics/file.processing.success](http://localhost:8080/actuator/metrics/file.processing.success)
  - Failure Metrics: [http://localhost:8080/actuator/metrics/file.processing.failure](http://localhost:8080/actuator/metrics/file.processing.failure)
  - Processing Time: [http://localhost:8080/actuator/metrics/file.processing.time](http://localhost:8080/actuator/metrics/file.processing.time)

---

## 🛠️ Processing

The application uses **non-blocking asynchronous processing** to handle heavy file uploads and transactions efficiently. This ensures that the system remains responsive even under high load.

---

## 📹 Demo Video

Watch the demo video below to understand the application's functionality:

[Watch Demo Video on Google Drive](https://drive.google.com/file/d/1ZJzlI5gAFDZwuITRCb-oryoH-Vkdnw71/preview)



---

## 🙏 Acknowledgments

- **Spring Boot**: For providing a powerful framework to build modern applications.
- **PostgreSQL**: For offering a reliable and scalable database solution.
- **Apache POI**: For simplifying Excel and CSV file processing.

 
