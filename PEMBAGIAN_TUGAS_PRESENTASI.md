# PEMBAGIAN TUGAS PRESENTASI - MINARI E-COMMERCE

## 👥 Anggota Tim
1. **Kamu** (Koordinator)
2. **Aya**
3. **Neja**
4. **Elsa**
5. **Cacan**

---

## 📋 BAGIAN 1: DEMO APLIKASI (15-20 menit)

### 1. **Kamu** - Opening & Customer Flow (5 menit)
**Tugas:**
- Pembukaan presentasi (intro project, tech stack)
- Demo fitur Customer:
  - Browse produk di halaman home
  - Search & filter produk
  - Add to cart (sebagai Guest)
  - Register akun baru
  - Login dan lihat cart tersinkronisasi

**Poin Penting:**
- Jelaskan Hybrid Cart System (Session vs Database)
- Tunjukkan perbedaan Guest vs Logged-in User

---

### 2. **Aya** - Checkout & User Profile (4 menit)
**Tugas:**
- Demo proses Checkout:
  - Pilih/tambah alamat pengiriman
  - Pilih metode pembayaran
  - Konfirmasi pesanan
- Demo User Profile:
  - Edit profil (foto, tanggal lahir)
  - Lihat Order History
  - Lihat Loyalty Points

**Poin Penting:**
- Jelaskan auto-award loyalty points (Rp 10.000 = 1 poin)
- Tunjukkan fitur password toggle

---

### 3. **Neja** - Wishlist & Admin Dashboard (4 menit)
**Tugas:**
- Demo Wishlist:
  - Tambah produk ke wishlist
  - Remove dari wishlist
  - Add to cart dari wishlist
- Demo Admin Dashboard:
  - Login sebagai admin
  - Lihat statistik penjualan
  - Grafik penjualan per bulan

**Poin Penting:**
- Jelaskan Role-Based Access Control (RBAC)
- Tunjukkan optimasi query dashboard

---

### 4. **Elsa** - Admin Product & Category Management (4 menit)
**Tugas:**
- Demo Manajemen Produk:
  - Tambah produk baru
  - Upload gambar via Cloudinary
  - Edit produk existing
  - Update stok
- Demo Manajemen Kategori:
  - Tambah kategori baru
  - Assign produk ke kategori

**Poin Penting:**
- Jelaskan integrasi Cloudinary untuk image storage
- Tunjukkan validasi form

---

### 5. **Cacan** - Admin Order Management & Closing (3 menit)
**Tugas:**
- Demo Manajemen Order:
  - Lihat daftar pesanan
  - Update status order (Pending → Paid → Shipped → Delivered)
  - Tunjukkan auto-award points saat status jadi Delivered
- Closing:
  - Summary fitur-fitur utama
  - Q&A preparation

**Poin Penting:**
- Jelaskan flow order lifecycle
- Tunjukkan payment status badge dengan warna

---

## 📊 BAGIAN 2: PENJELASAN CLASS DIAGRAM (10-15 menit)

### 1. **Kamu** - Entity Layer & Inheritance (3 menit)
**Tugas:**
- Jelaskan struktur Entity utama:
  - `User` (abstract class)
  - `Admin`, `RegisteredCustomer`, `Guest` (inheritance)
  - `Product`, `ProductCategory`
  - `Order`, `OrderItem`
- Tunjukkan relasi One-to-Many, Many-to-One

**Poin Penting:**
- Fokus pada **Inheritance** (User hierarchy)
- Jelaskan `@Inheritance(strategy = JOINED)`

---

### 2. **Aya** - Repository & Service Layer (3 menit)
**Tugas:**
- Jelaskan Repository Pattern:
  - Interface `ProductRepository`, `OrderRepository`
  - Custom query methods
- Jelaskan Service Layer:
  - `ProductService`, `OrderService`
  - Business logic encapsulation

**Poin Penting:**
- Fokus pada **Abstraction** (Interface-based)
- Jelaskan **Polymorphism** (berbagai implementasi Repository)

---

### 3. **Neja** - Controller Layer & MVC (3 menit)
**Tugas:**
- Jelaskan Controller:
  - `HomeController`, `ProductController`
  - `CartController` (Hybrid logic)
  - `WebOrderController`
- Jelaskan MVC Pattern:
  - Model (Entity)
  - View (Thymeleaf)
  - Controller (Request handling)

**Poin Penting:**
- Fokus pada **Encapsulation** (Service layer sebagai facade)
- Jelaskan separation of concerns

---

### 4. **Elsa** - Cart System & Session Management (3 menit)
**Tugas:**
- Jelaskan Hybrid Cart Architecture:
  - `CartController` dual-mode logic
  - `ShoppingCartService` untuk database
  - `HttpSession` untuk guest
- Jelaskan class diagram untuk:
  - `ShoppingCart` entity
  - `CartItem` entity
  - `CartSessionItem` DTO

**Poin Penting:**
- Jelaskan kenapa pakai hybrid approach
- Tunjukkan DTO pattern untuk abstraction

---

### 5. **Cacan** - Security & Integration (3 menit)
**Tugas:**
- Jelaskan Security Configuration:
  - `SecurityConfig` class
  - Role-based access (`ROLE_ADMIN`, `ROLE_CUSTOMER`)
  - Password encryption (BCrypt)
- Jelaskan Integration:
  - Cloudinary (FileUploadService)
  - Database (H2 vs PostgreSQL)

**Poin Penting:**
- Jelaskan Spring Security flow
- Tunjukkan dependency injection pattern

---

## 💾 BAGIAN 3: PENJELASAN DATABASE (5 menit)

### **Kamu** - Database Architecture Overview

#### A. Database Strategy: Hybrid Approach

**Development Environment:**
```
Database: H2 (In-Memory)
Location: ./data/minari.mv.db
Keuntungan:
- Tidak perlu install database server
- Cepat untuk development
- Auto-create schema dari JPA entities
- Mudah di-reset (hapus file .mv.db)
```

**Production Environment:**
```
Database: PostgreSQL (Neon Tech - Serverless)
Connection: Via environment variables
Keuntungan:
- Serverless (auto-scaling)
- Cost-effective (scale to zero saat idle)
- Database branching untuk testing
- Persistent storage
```

#### B. Database Schema (Tabel Utama)

**1. Users Table (Inheritance: JOINED strategy)**
```sql
-- Parent table
users (id, email, username, password, full_name, phone, role, is_active)

-- Child tables
admin (id, ... admin-specific fields)
registered_customer (id, birth_date, loyalty_points, ...)
guest (id, session_id, ...)
```

**2. Products & Categories**
```sql
product_category (id, name, description)
products (id, name, price, stock_quantity, category_id, image_url)
```

**3. Shopping Cart (Persistent)**
```sql
shopping_cart (id, customer_id, created_at)
cart_items (id, cart_id, product_id, quantity)
```

**4. Orders & Transactions**
```sql
orders (id, order_number, customer_id, total_amount, status, created_at)
order_items (id, order_id, product_id, quantity, price)
payment (id, order_id, payment_method, status, amount)
```

**5. Supporting Tables**
```sql
address (id, customer_id, street, city, province, postal_code)
wishlist (id, customer_id, product_id)
promotion (id, name, discount_percentage, min_purchase, category_id)
```

#### C. Database Features

**1. Auto-Schema Generation**
```properties
spring.jpa.hibernate.ddl-auto=update
```
- JPA otomatis create/update tabel dari Entity classes
- Tidak perlu SQL script manual

**2. Connection Pooling (HikariCP)**
```properties
spring.datasource.hikari.maximum-pool-size=5
spring.datasource.hikari.connection-timeout=20000
```
- Optimasi koneksi database
- Performa lebih baik untuk concurrent users

**3. Data Seeding**
```java
@Component
public class DataSeeder implements CommandLineRunner {
    // Auto-create admin & customer saat pertama run
}
```

**4. Query Optimization**
- Custom JPQL queries di Repository
- Lazy loading untuk relasi
- Pagination untuk list data

#### D. Database Migration (Development → Production)

**Cara Export/Import:**

**H2 to PostgreSQL:**
```bash
# 1. Export dari H2 (via H2 Console)
http://localhost:8080/h2-console
SCRIPT TO 'backup.sql'

# 2. Import ke PostgreSQL
psql -U username -d database_name -f backup.sql
```

**Environment Variables untuk Production:**
```bash
SPRING_DATASOURCE_URL=jdbc:postgresql://host/db
SPRING_DATASOURCE_USERNAME=username
SPRING_DATASOURCE_PASSWORD=password
SPRING_DATASOURCE_DRIVER_CLASS_NAME=org.postgresql.Driver
SPRING_JPA_DATABASE_PLATFORM=org.hibernate.dialect.PostgreSQLDialect
```

#### E. Database Advantages

**Kenapa Pakai Hybrid?**
1. **Development:** H2 cepat, tidak perlu setup
2. **Production:** PostgreSQL reliable, scalable
3. **Flexibility:** Ganti database tanpa ubah code (hanya env vars)

**Kenapa Pakai Neon Tech?**
1. **Serverless:** Bayar per usage, bukan per server
2. **Branching:** Bisa buat "copy" database untuk testing
3. **Auto-scaling:** Handle traffic spike otomatis
4. **Free tier:** Cocok untuk project kuliah/demo

---

## 📝 TIPS PRESENTASI

### Persiapan Sebelum Demo:
1. ✅ Pastikan aplikasi running di `localhost:8080`
2. ✅ Buka 2 browser (Chrome untuk Customer, Firefox untuk Admin)
3. ✅ Siapkan data dummy (produk, kategori sudah ada)
4. ✅ Test semua flow sebelum presentasi
5. ✅ Screenshot class diagram siap ditampilkan

### Saat Presentasi:
- **Jangan terburu-buru** - Jelaskan sambil demo
- **Highlight OOP principles** - Sebutkan saat menjelaskan
- **Siapkan backup** - Jika ada error, punya screenshot cadangan
- **Koordinasi timing** - Masing-masing 3-5 menit
- **Q&A** - Siapkan jawaban untuk pertanyaan umum

### Pertanyaan yang Mungkin Ditanya:
1. **"Kenapa pakai Hybrid Cart?"** → Jelaskan UX untuk Guest vs User
2. **"Apa bedanya H2 dan PostgreSQL?"** → Jelaskan use case masing-masing
3. **"Gimana cara deploy?"** → Jelaskan Docker & environment variables
4. **"Code coverage berapa persen?"** → Lihat hasil SonarQube
5. **"Kenapa pakai Spring Boot?"** → Jelaskan ecosystem & productivity

---

## ⏱️ TIMELINE PRESENTASI (Total: 30-35 menit)

| Waktu | Bagian | PIC |
|-------|--------|-----|
| 0-5 min | Demo Customer Flow | Kamu |
| 5-9 min | Demo Checkout & Profile | Aya |
| 9-13 min | Demo Wishlist & Admin Dashboard | Neja |
| 13-17 min | Demo Product/Category Management | Elsa |
| 17-20 min | Demo Order Management | Cacan |
| 20-23 min | Class Diagram: Entity & Inheritance | Kamu |
| 23-26 min | Class Diagram: Repository & Service | Aya |
| 26-29 min | Class Diagram: Controller & MVC | Neja |
| 29-32 min | Class Diagram: Cart & Session | Elsa |
| 32-35 min | Class Diagram: Security & Database | Cacan |

---

**Good luck dengan presentasinya! 🚀**
