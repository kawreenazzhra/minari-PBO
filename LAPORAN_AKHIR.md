# LAPORAN AKHIR PROJECT MINARI E-COMMERCE

## 1. INFORMASI PROJECT

**Nama Project:** Minari E-Commerce  
**Mata Kuliah:** Pemrograman Berorientasi Objek (PBO)  
**Tahun Akademik:** 2024/2025

---

## 2. ANGGOTA KELOMPOK

| No | Nama | NIM | Kontribusi |
|----|------|-----|------------|
| 1  | [Nama Anggota 1] | [NIM] | [Deskripsi kontribusi] |
| 2  | [Nama Anggota 2] | [NIM] | [Deskripsi kontribusi] |
| 3  | [Nama Anggota 3] | [NIM] | [Deskripsi kontribusi] |

> **Catatan:** Silakan isi informasi anggota kelompok di atas.

---

## 3. DAFTAR FITUR

### 🛍️ Fitur untuk Pelanggan (Customer)

#### A. Katalog & Pencarian Produk
- **Browse Produk:** Halaman home menampilkan produk unggulan dan terbaru
- **Pencarian:** Cari produk berdasarkan nama atau deskripsi
- **Filter Kategori:** Filter produk berdasarkan kategori yang tersedia
- **Detail Produk:** Lihat informasi lengkap produk termasuk harga, stok, dan deskripsi
- **Label Ukuran:** Menampilkan informasi "All Size (Fit to L)" pada produk

#### B. Keranjang Belanja (Hybrid Cart System)
- **Guest Cart:** Keranjang tersimpan di Session untuk pengunjung tanpa login
- **User Cart:** Keranjang tersimpan di Database untuk user yang login (sinkron antar device)
- **Update Quantity:** Ubah jumlah barang di keranjang
- **Kalkulasi Real-time:** Total harga dihitung otomatis
- **Validasi Stok:** Sistem memvalidasi ketersediaan stok sebelum checkout

#### C. Sistem Checkout
- **Manajemen Alamat:** Tambah, edit, dan pilih alamat pengiriman
- **Simulasi Pembayaran:** Integrasi simulasi payment gateway
- **Konfirmasi Pesanan:** Review pesanan sebelum submit
- **Order History:** Lihat riwayat pesanan yang pernah dilakukan

#### D. User Profile & Account
- **Registrasi:** Daftar akun baru dengan validasi email unik
- **Login/Logout:** Sistem autentikasi aman dengan Spring Security
- **Password Toggle:** Fitur show/hide password saat login dan registrasi
- **Edit Profil:** Update informasi pribadi (nama, foto, tanggal lahir, nomor HP)
- **Loyalty Points:** Dapatkan poin loyalitas otomatis setiap pembelian (Rp 10.000 = 1 poin)
- **Wishlist:** Simpan produk favorit untuk dibeli nanti

### 👨‍💼 Fitur untuk Admin

#### A. Dashboard Analytics
- **Statistik Penjualan:** Total revenue dan jumlah transaksi
- **Grafik Penjualan:** Visualisasi penjualan per bulan
- **Recent Orders:** Daftar pesanan terbaru
- **Recent Reviews:** Review pelanggan terbaru
- **Performance Optimization:** Query database yang dioptimasi untuk performa cepat

#### B. Manajemen Produk
- **CRUD Produk:** Tambah, edit, hapus produk
- **Upload Gambar:** Upload foto produk via Cloudinary
- **Manajemen Stok:** Atur dan update stok produk
- **Manajemen Harga:** Set dan update harga produk
- **Kategori Produk:** Assign produk ke kategori tertentu

#### C. Manajemen Order
- **Daftar Pesanan:** Lihat semua pesanan yang masuk
- **Update Status:** Ubah status pesanan (Pending → Paid → Shipped → Delivered)
- **Auto Award Points:** Sistem otomatis memberikan loyalty points saat order selesai
- **Payment Status Badge:** Indikator visual status pembayaran dengan warna

#### D. Manajemen Kategori
- **CRUD Kategori:** Tambah, edit, hapus kategori produk
- **Kategori Produk:** Organisasi produk berdasarkan kategori

#### E. Manajemen Promosi
- **CRUD Promosi:** Tambah, edit, hapus promosi/diskon
- **Kategori Promosi:** Set promosi untuk kategori tertentu
- **Minimum Purchase:** Atur minimum pembelian untuk promosi

### ⚙️ Fitur Teknis

#### A. Security
- **Role-Based Access Control (RBAC):** Pemisahan akses Admin dan Customer
- **Password Encryption:** BCrypt hashing untuk keamanan password
- **CSRF Protection:** Proteksi terhadap Cross-Site Request Forgery
- **Session Management:** Manajemen session yang aman

#### B. Database
- **Hybrid Database:** H2 (Development) dan PostgreSQL/Neon (Production)
- **Auto-switching:** Aplikasi otomatis switch database berdasarkan environment
- **Connection Pooling:** HikariCP untuk optimasi koneksi database
- **Data Seeding:** Auto-create default admin dan customer saat pertama kali run

#### C. Performance
- **Query Optimization:** Optimasi query untuk dashboard dan list pages
- **Lazy Loading:** Efficient data loading untuk performa optimal
- **Caching:** Thymeleaf template caching untuk production

#### D. Localization
- **Multi-language Support:** Notifikasi dalam Bahasa Indonesia dan Inggris
- **Consistent UX:** Pesan error dan success yang konsisten

---

## 4. PENERAPAN PRINSIP OOP

### A. Encapsulation (Enkapsulasi)
- **Private Fields:** Semua entity menggunakan field `private` dengan getter/setter public
- **Service Layer:** Logika bisnis tersembunyi di dalam Service layer
- **Example:**
  ```java
  public class Product {
      private Long id;
      private String name;
      private Double price;
      
      // Getters and Setters
      public Long getId() { return id; }
      public void setId(Long id) { this.id = id; }
  }
  ```

### B. Inheritance (Pewarisan)
- **User Hierarchy:** `User` sebagai abstract base class
  - `Admin` extends `User`
  - `RegisteredCustomer` extends `User`
  - `Guest` extends `User`
- **Mapping Strategy:** `@Inheritance(strategy = InheritanceType.JOINED)`
- **Example:**
  ```java
  @Entity
  @Inheritance(strategy = InheritanceType.JOINED)
  public abstract class User {
      @Id
      private Long id;
      private String email;
      // ...
  }
  
  @Entity
  public class Admin extends User {
      // Admin-specific fields
  }
  ```

### C. Polymorphism (Polimorfisme)
- **Repository Interfaces:** Berbagai implementasi untuk satu interface
- **Method Overriding:** Child class override method parent untuk behavior spesifik
- **Example:**
  ```java
  public interface ProductRepository extends JpaRepository<Product, Long> {
      List<Product> findByCategory(ProductCategory category);
      List<Product> searchByNameOrDescription(String query);
  }
  ```

### D. Abstraction (Abstraksi)
- **Interface-based Services:** Dependency injection menggunakan interface
- **DTO Pattern:** Data Transfer Objects untuk abstraksi struktur database
- **Example:**
  ```java
  @Service
  public class OrderService {
      @Autowired
      private OrderRepository orderRepository;
      
      public Order createOrder(OrderDTO dto) {
          // Business logic
      }
  }
  ```

---

## 5. ARSITEKTUR SISTEM

### A. Tech Stack
- **Backend:** Java 21 (LTS), Spring Boot 3.x
- **Frontend:** Thymeleaf, Bootstrap 5, Vanilla JavaScript (OOP)
- **Database:** PostgreSQL (Neon Tech - Production) / H2 (Development)
- **Build Tool:** Maven 3.9.11
- **Security:** Spring Security
- **Cloud Storage:** Cloudinary (untuk gambar produk)
- **Containerization:** Docker

### B. Design Pattern
- **MVC (Model-View-Controller):** Pemisahan concerns antara data, UI, dan logic
- **Repository Pattern:** Abstraksi akses database
- **Service Layer Pattern:** Encapsulation business logic
- **DTO Pattern:** Transfer data antar layer

---

## 6. CLASS DIAGRAM

Class diagram lengkap tersedia di file: `class_diagram_complete.puml`

Untuk melihat diagram, gunakan PlantUML viewer atau online tool seperti:
- http://www.plantuml.com/plantuml/uml/

File diagram mencakup:
- Entity classes (User, Product, Order, dll)
- Repository interfaces
- Service classes
- Controller classes
- Relationship antar class

---

## 7. SCREENSHOT APLIKASI

> **Catatan:** Silakan tambahkan screenshot berikut ke folder `screenshots/` dan embed di sini:

### Customer Features
1. **Halaman Home** - Tampilan landing page dengan produk unggulan
2. **Katalog Produk** - Grid view produk dengan filter kategori
3. **Detail Produk** - Informasi lengkap produk
4. **Keranjang Belanja** - Shopping cart dengan kalkulasi total
5. **Checkout** - Flow checkout dengan alamat dan pembayaran
6. **User Profile** - Halaman profil dengan loyalty points
7. **Order History** - Riwayat pesanan customer

### Admin Features
8. **Admin Dashboard** - Analytics dan statistik penjualan
9. **Manajemen Produk** - CRUD produk
10. **Manajemen Order** - List dan update status order
11. **Manajemen Kategori** - CRUD kategori

---

## 8. HASIL PENGUJIAN SONARQUBE

> **Catatan:** Silakan jalankan SonarQube analysis dan tambahkan screenshot hasil di sini.

### Cara Menjalankan SonarQube:
```bash
mvn clean verify sonar:sonar \
  -Dsonar.projectKey=minari-ecommerce \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.login=your-token
```

### Metrics yang Diharapkan:
- Code Coverage
- Code Smells
- Bugs
- Vulnerabilities
- Security Hotspots
- Duplications

---

## 9. KONFIGURASI APLIKASI

### A. Prasyarat
- Java 21 SDK
- Maven 3.9.11+
- (Opsional) PostgreSQL atau gunakan H2 default

### B. Cara Menjalankan Aplikasi

#### Menggunakan Maven:
```bash
# Clone repository
git clone https://github.com/kawreenazzhra/minari-PBO.git
cd minari-PBO

# Build project
mvn clean install

# Run aplikasi
mvn spring-boot:run
```

Aplikasi akan berjalan di: `http://localhost:8080`

#### Menggunakan Docker:
```bash
# Build Docker image
docker build -t minari-ecommerce .

# Run container
docker run -p 8080:8080 minari-ecommerce
```

### C. Konfigurasi Database

#### Development (Default - H2):
Tidak perlu konfigurasi tambahan. H2 database akan otomatis dibuat di folder `./data/minari`.

#### Production (PostgreSQL/Neon):
Set environment variables berikut:
```bash
SPRING_DATASOURCE_URL=jdbc:postgresql://your-host/your-db
SPRING_DATASOURCE_USERNAME=your-username
SPRING_DATASOURCE_PASSWORD=your-password
```

### D. Konfigurasi Cloudinary (Opsional)
Untuk upload gambar produk, set environment variables:
```bash
CLOUDINARY_CLOUD_NAME=your-cloud-name
CLOUDINARY_API_KEY=your-api-key
CLOUDINARY_API_SECRET=your-api-secret
```

---

## 10. AKUN DEMO

### Admin Account
- **Email:** `admin@minari.com`
- **Password:** `admin123`
- **Akses:** Full access ke admin dashboard

### Customer Account
- **Email:** `customer@minari.com`
- **Password:** `customer123`
- **Akses:** Customer features (shopping, checkout, profile)

### Guest Access
- **Akses:** Browse produk, add to cart (session-based)
- **Keterbatasan:** Tidak bisa checkout tanpa login

---

## 11. STRUKTUR PROJECT

```
minari-PBO/
├── src/
│   ├── main/
│   │   ├── java/com/minari/ecommerce/
│   │   │   ├── config/          # Konfigurasi (Security, Cloudinary, DataSeeder)
│   │   │   ├── controller/      # REST & Web Controllers
│   │   │   ├── dto/             # Data Transfer Objects
│   │   │   ├── entity/          # JPA Entities
│   │   │   ├── repository/      # Spring Data Repositories
│   │   │   ├── service/         # Business Logic Layer
│   │   │   └── MinariApplication.java
│   │   └── resources/
│   │       ├── static/          # CSS, JS, Images
│   │       ├── templates/       # Thymeleaf HTML Templates
│   │       └── application.properties
│   └── test/                    # Unit & Integration Tests
├── Dockerfile
├── pom.xml
├── README.md
├── PROJECT_STRUCTURE.md
└── LAPORAN_AKHIR.md (file ini)
```

---

## 12. CATATAN TAMBAHAN

### A. Database Export
File database export (`.sql`) tersedia terpisah karena ukuran file.

### B. Source Code
Source code yang dikumpulkan adalah versi terbaru dan sudah ditest dapat berjalan tanpa error.

### C. Build Folder
Folder `target/` dan `build/` tidak disertakan dalam kompresi untuk menghemat ukuran file.

### D. Link Repository
GitHub: https://github.com/kawreenazzhra/minari-PBO

---

## 13. KESIMPULAN

Project Minari E-Commerce berhasil mengimplementasikan sistem e-commerce lengkap dengan menerapkan prinsip-prinsip OOP (Encapsulation, Inheritance, Polymorphism, Abstraction) secara konsisten. Aplikasi ini dilengkapi dengan fitur-fitur modern seperti hybrid cart system, loyalty points, dan optimasi performa database.

---

**Tanggal Penyerahan:** 31 Desember 2024  
**Versi:** 1.0.0
