# SmartMov

**SmartMov** is a learning and productivity platform that helps users turn learning goals into structured daily progress.

Users can create learning targets, track daily progress, manage learning resources, and record focused learning sessions. The application is deployed on AWS with a Spring Boot backend, MySQL database, and private cloud storage.

## 🚀 Live Application

**Production:**
PS: AWS Deployment is removed since the hackathon is over.

The production application is publicly accessible over HTTPS.

## 🎯 Problem

Learning consistently is difficult when goals, resources, study time, and progress are scattered across different tools.

SmartMov brings these activities together into one application:

* Define learning targets
* Set daily learning time
* Track progress
* Organize learning resources
* Record learning sessions
* Secure user-owned data

## ✨ Key Features

### Authentication

* User registration and login
* JWT-based authentication
* Stateless authentication
* BCrypt password hashing

### Learning Targets

* Create learning targets
* Define categories and daily learning time
* Update targets
* Track completed learning time
* User-specific target ownership

### Progress Tracking

* Record learning progress
* Associate progress with individual targets
* Track completed learning time

### Learning Sessions

* Start learning sessions
* Finish learning sessions
* Calculate session duration
* Associate sessions with user-owned targets

### Resources

* Create and manage learning resources
* Support resource types
* File upload support
* User ownership and authorization
* Production file storage using Amazon S3

### Security

* JWT authentication
* User ownership checks
* Protected API endpoints
* CORS configuration
* Environment-based secrets
* Private Amazon S3 storage
* AWS IAM role-based access for the backend

## 🏗️ AWS Architecture

```text
                         Public Internet
                               │
                               │ HTTPS
                               ▼
                    ┌─────────────────────┐
                    │    AWS Amplify      │
                    │  Static Frontend    │
                    └──────────┬──────────┘
                               │
                               │ HTTPS
                               ▼
                    ┌─────────────────────┐
                    │   API Gateway       │
                    │    HTTP API         │
                    └──────────┬──────────┘
                               │
                               │ HTTP
                               ▼
                    ┌─────────────────────┐
                    │   Amazon EC2        │
                    │ Spring Boot Backend │
                    └──────┬────────┬─────┘
                           │        │
                    JDBC   │        │ AWS SDK
                           ▼        ▼
                  ┌────────────┐  ┌─────────────────────┐
                  │ Amazon RDS │  │ Private Amazon S3   │
                  │   MySQL    │  │ Uploaded Resources  │
                  └────────────┘  └─────────────────────┘
```

### AWS Services

| Service            | Purpose                                         |
| ------------------ | ----------------------------------------------- |
| AWS Amplify        | Production frontend hosting                     |
| Amazon API Gateway | Public HTTPS API endpoint                       |
| Amazon EC2         | Spring Boot backend                             |
| Amazon RDS         | MySQL database                                  |
| Amazon S3          | Private file/object storage                     |
| AWS IAM            | Access control and EC2 permissions              |
| AWS CLI            | AWS resource management and deployment workflow |
| AWS Toolkit        | AWS development workflow in VS Code             |

## 🔄 Production Request Flow

1. User opens the SmartMov application through AWS Amplify.
2. The frontend sends API requests over HTTPS to Amazon API Gateway.
3. API Gateway forwards requests to the Spring Boot application running on EC2.
4. Spring Boot authenticates users using JWT.
5. Application data is stored in Amazon RDS MySQL.
6. Uploaded resources are stored in a private Amazon S3 bucket.
7. The EC2 instance accesses S3 using an IAM role rather than hardcoded AWS credentials.

## 🛠️ Technology Stack

### Backend

* Java 21
* Spring Boot 4.1.1
* Spring Security
* JWT
* Spring Data JPA
* Hibernate
* MySQL
* Flyway
* Maven
* AWS SDK for Java

### Frontend

* HTML5
* CSS3
* JavaScript
* AWS Amplify

### Cloud

* AWS Amplify
* Amazon API Gateway
* Amazon EC2
* Amazon RDS
* Amazon S3
* AWS IAM
* AWS CLI
* AWS Toolkit

## 🔐 Security Architecture

SmartMov uses several layers of security:

* Passwords are hashed using BCrypt.
* Authentication uses signed JWTs.
* Protected API endpoints require authentication.
* User-owned targets, resources, and sessions are checked against the authenticated user.
* Database credentials are supplied through environment variables.
* JWT secrets are supplied through environment variables.
* Production S3 storage is private.
* EC2 accesses the private S3 bucket through an IAM role.
* AWS credentials are not hardcoded into the application source code.
* Secrets and private deployment files are excluded from Git using `.gitignore`.

## 🗄️ Database

The production application uses **Amazon RDS for MySQL**.

Database schema management is handled using Flyway.

The initial schema is located at:

```text
SmartMov-backend/
└── src/
    └── main/
        └── resources/
            └── db/
                └── migration/
                    └── V1__initial_schema.sql
```

## 📁 Project Structure

```text
SmartMov/
│
├── .github/
│   └── modernize/
│
├── SmartMov-backend/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   └── resources/
│   │   │       └── db/migration/
│   │   └── test/
│   │
│   ├── pom.xml
│   ├── mvnw
│   ├── mvnw.cmd
│   └── .env.example
│
├── SmartMov-frontend/
│   ├── index.html
│   ├── about.html
│   ├── session-window.html
│   ├── script.js
│   ├── styles.css
│   ├── config.js
│   └── config.production.example.js
│
├── .gitignore
└── README.md
```

## 💻 Local Development

### Backend

Requirements:

* Java 21
* Maven
* MySQL

The backend reads configuration from environment variables.

Use the provided template:

```text
SmartMov-backend/.env.example
```

Do **not** commit your actual `.env` file.

From the backend directory:

```powershell
cd SmartMov-backend
.\mvnw spring-boot:run
```

The backend runs locally on:

```text
http://localhost:8080
```

### Frontend

The frontend is a static HTML/CSS/JavaScript application.

For local development, configure the API base URL in:

```text
SmartMov-frontend/config.js
```

The production configuration points to the API Gateway endpoint.

## 🧪 Testing

The backend contains service-level tests covering core application functionality.

Tests can be executed with:

```powershell
cd SmartMov-backend
.\mvnw test
```

## ☁️ Production Deployment

The production environment is currently structured as:

```text
AWS Amplify
      ↓
API Gateway HTTP API
      ↓
EC2
      ↓
RDS MySQL
      +
Private S3
```

The Spring Boot backend runs as a Linux `systemd` service on Amazon EC2.

The application automatically restarts after backend failures and starts automatically when the EC2 instance reboots.

## 🤖 Development Workflow

SmartMov was developed with an AI-assisted development workflow.

AI assistance was used during development for tasks such as:

* Understanding Spring Boot implementation choices
* Reviewing application architecture
* Debugging backend issues
* Designing authentication and authorization logic
* Reviewing AWS deployment configuration
* Troubleshooting AWS integration issues
* Iterating on application functionality

AWS development tooling was also configured during the project, including:

* AWS CLI
* AWS Toolkit for VS Code
* AWS Agent Toolkit and its installed AWS skills

The development process combined AI-assisted coding and troubleshooting with direct AWS console and CLI work.

## 🔑 Configuration and Secrets

Sensitive configuration is intentionally kept outside the repository.

The repository does **not** contain:

* AWS private keys
* Database passwords
* JWT secrets
* `.env` files
* Uploaded user files
* Maven build output
* Deployment ZIP artifacts

Environment variable templates are provided through `.env.example`.

## 📌 Current Deployment

| Component          | Production Service        |
| ------------------ | ------------------------- |
| Frontend           | AWS Amplify               |
| API                | Amazon API Gateway        |
| Backend            | Amazon EC2                |
| Database           | Amazon RDS MySQL          |
| File Storage       | Private Amazon S3         |
| Authentication     | JWT                       |
| Database Migration | Flyway                    |
| Access Control     | Spring Security + AWS IAM |

## 🌱 Project Goal

SmartMov is designed to make structured learning easier by connecting:

**Goals → Daily Progress → Resources → Learning Sessions**

The project also serves as a practical demonstration of building, securing, and deploying a Java/Spring Boot application using AWS services.

## 📄 License

This project was created mainly for AWS Zero-to-Shipped Hackathon.
