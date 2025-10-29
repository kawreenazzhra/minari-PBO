# 🛍️ Minari - E-commerce Pakaian Casual Wanita

**Spring Boot Project** untuk tugas besar PBO & Web Programming

## 🎯 Deskripsi
Website e-commerce pakaian casual wanita yang mengimplementasikan konsep OOP Java dan frontend modern.

## 🏗️ Tech Stack
- **Backend**: Spring Boot 3.5.7, Java 17
- **Frontend**: Thymeleaf, Bootstrap 5, CSS3
- **Database**: H2 (In-memory)
- **OOP Concepts**: Inheritance, Polymorphism, Encapsulation, Abstraction, Composition

## 📥 Clone & Setup

### Prerequisites
- Java 17 atau higher
- Maven 3.6+ (atau menggunakan Maven Wrapper yang sudah included)
- Git

### Cara Clone Repository
```bash
# Clone repository ini
git clone https://github.com/kawreenazzhra/minari-PBO.git

# Masuk ke direktori project
cd minari

## 📚 Konsep OOP yang Diimplementasi
- ✅ Abstract Class (User)
- ✅ Inheritance (Customer extends User)  
- ✅ Polymorphism (Method Overriding)
- ✅ Encapsulation (Private fields + Getters/Setters)
- ✅ Composition (ProductService → Products)

## 🚀 Menjalankan Project
# Windows
mvnw.cmd spring-boot:run

# Linux/Mac
./mvnw spring-boot:run

http://localhost:8080

## 📁 Struktur Project
minari/
├── src/main/java/com/minari/
│   ├── controller/     # Spring MVC Controllers
│   │   └── HomeController.java
│   ├── model/          # OOP Classes 
│   │   ├── User.java (Abstract)
│   │   ├── Customer.java (Inheritance)
│   │   └── Product.java (Encapsulation)
│   ├── service/        # Business Logic
│   │   └── ProductService.java
│   └── MinariApplication.java
├── src/main/resources/
│   ├── templates/      # HTML Files 
│   │   ├── index.html
│   │   ├── test-oop.html
│   │   └── products.html
│   ├── static/         # CSS/JS
│   │   └── css/style.css
│   └── application.properties
├── pom.xml
└── README.md

👥 Tim Pengembang
Aliyah Rahma - 103032300126
Anneiza Wirda Nursifana - 103032300038
Karina Azzahra - 103032300040
Elsa Risnauli Sianturi - 103032300010
Chantika Syafana Zultantia - 103032300058