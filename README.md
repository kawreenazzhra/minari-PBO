# Minari E-Commerce

**Minari E-Commerce** is a robust, high-performance web application built with Java 21 and Spring Boot. It features a comprehensive shopping experience for customers and a powerful dashboard for administrators.

## 🚀 Tech Stack

- **Backend:** Java 21 (LTS), Spring Boot 3.x
- **Frontend:** Thymeleaf, Bootstrap 5, Vanilla JavaScript (OOP Architecture)
- **Database:** PostgreSQL (Neon Tech) [Production] / H2 [Development]
- **Build Tool:** Maven 3.9.11
- **Security:** Spring Security (Role-based access control)
- **Containerization:** Docker

## 🗄️ Database Architecture

This project uses a **Serverless PostgreSQL** database hosted on **Neon Tech** for production.

### Why Neon Tech?
- **Serverless:** Scales automatically with demand (calculates compute per request).
- **Branching:** Allows creating database branches for testing features without affecting production data.
- **Cost-Effective:** Scales down to zero when not in use.

### Configuration
The application automatically switches databases based on the environment:
- **Local Development:** Uses **H2 Database** (In-memory) for speed and simplicity.
- **Production (Cloud):** Connects to **Neon PostgreSQL** via environment variables (`SPRING_DATASOURCE_URL`).

## ✨ Key Features

### for Customers
- **Product Catalog:** Browse products by category, search with filters.
- **Shopping Cart:** Add items, update quantities, real-time total calculation.
- **Secure Checkout:** Multiple payment methods (Gateway integration simulation).
- **User Profile:** Manage shipping addresses, view order history.
- **Wishlist:** Save items for later.

### for Admins
- **Dashboard:** Overview of sales, orders, and products.
- **Product Management:** Add, edit, delete products.
- **Order Management:** View and update order statuses (Shipped, Delivered, etc.).
- **Category Management:** Manage product categories.

## 🛠️ Installation & Setup

### Prerequisites
- Java 21 SDK installed.
- Maven installed.
- MySQL database running (update `application.properties` with credentials).

### Running Locally
1. **Clone the repository:**
   ```bash
   git clone https://github.com/kawreenazzhra/minari-PBO.git
   cd minari-PBO
   ```

2. **Build the project:**
   ```bash
   mvn clean install
   ```

3. **Run the application:**
   ```bash
   mvn spring-boot:run
   ```
   The app will start at `http://localhost:8080`.

### Docker
1. **Build the image:**
   ```bash
   docker build -t minari-ecommerce .
   ```
2. **Run the container:**
   ```bash
   docker run -p 8080:8080 minari-ecommerce
   ```

## 📂 Project Structure
```
src/main/
├── java/com/minari/ecommerce/  # Backend Logic (Controllers, Services, Repositories)
└── resources/
    ├── static/                 # CSS, JS, Images
    └── templates/              # Thymeleaf HTML Views
```

## 📄 License
Details regarding licensing...
