# Minari E-Commerce

**Minari E-Commerce** adalah aplikasi web berkinerja tinggi yang dibangun dengan Java 21 dan Spring Boot. Aplikasi ini menyediakan pengalaman belanja yang lengkap bagi pelanggan dan dashboard yang canggih untuk administrator.

## 🚀 Tech Stack

- **Backend:** Java 21 (LTS), Spring Boot 3.x
- **Frontend:** Thymeleaf, Bootstrap 5, Vanilla JavaScript (OOP Architecture)
- **Database:** PostgreSQL (Neon Tech) [Production] / H2 [Development]
- **Build Tool:** Maven 3.9.11
- **Security:** Spring Security (RBAC - Role Based Access Control)
- **Containerization:** Docker

## 🗄️ Arsitektur Database

Project ini menggunakan **Serverless PostgreSQL** yang di-hosting di **Neon Tech** untuk production.

### Mengapa Neon Tech?
- **Serverless:** Scaling otomatis sesuai permintaan (biaya dihitung per request).
- **Branching:** Memungkinkan pembuatan "cabang" database untuk testing fitur tanpa mengganggu data production.
- **Cost-Effective:** Scale down ke nol saat tidak digunakan.

### Konfigurasi
Aplikasi secara otomatis mengganti koneksi database berdasarkan environment:
- **Local Development:** Menggunakan **H2 Database** (In-memory) agar cepat dan ringan.
- **Production (Cloud):** Terhubung ke **Neon PostgreSQL** via environment variable (`SPRING_DATASOURCE_URL`).

## 🧠 Penerapan Prinsip OOP

Project ini menerapkan prinsip **Object-Oriented Programming (OOP)** secara ketat untuk memastikan kemudahan maintain dan scalability:

1.  **Encapsulation (Enkapsulasi):**
    *   Penggunaan field `private` dengan getter/setter public pada class Entity (contoh: `User.java`, `Product.java`).
    *   Service layer bertindak sebagai *facade*, menyembunyikan logika bisnis yang kompleks dari Controller.

2.  **Inheritance (Pewarisan):**
    *   `User` adalah *abstract base class* yang diturunkan menjadi `Customer`, `Admin`, dan `Guest` (mapping menggunakan `@Inheritance(strategy = JOINED)`).
    *   Class JavaScript UI menggunakan inheritance untuk aturan validasi (contoh: `ValidationRule` base class).

3.  **Polymorphism (Polimorfisme):**
    *   Interface seperti `ProductRepository` memungkinkan implementasi dasar yang berbeda.
    *   Overriding method pada child class untuk menyediakan perilaku spesifik (contoh: logika login khusus Admin vs Customer).

4.  **Abstraction (Abstraksi):**
    *   Penggunaan injeksi Service berbasis Interface (`@Autowired`).
    *   DTO (Data Transfer Objects) mengabstraksi struktur database internal dari respon API.

## ✨ Fitur Utama

### Untuk Pelanggan (Customer)
- **Katalog Produk:** Jelajahi produk per kategori, cari dengan filter.
- **Keranjang Belanja:** Tambah barang, update jumlah, kalkulasi total real-time.
- **Checkout Aman:** Simulasi integrasi Gateway pembayaran.
- **Profil Pengguna:** Kelola alamat pengiriman, lihat riwayat pesanan.
- **Wishlist:** Simpan barang untuk nanti.

### Untuk Admin
- **Dashboard:** Ringkasan penjualan, pesanan, dan produk.
- **Manajemen Produk:** Tambah, edit, hapus produk.
- **Manajemen Order:** Lihat dan update status pesanan (Shipped, Delivered, dll).
- **Manajemen Kategori:** Kelola kategori produk.

## 🛠️ Instalasi & Setup

### Prasyarat
- Java 21 SDK terinstal.
- Maven terinstal.
- Database MySQL/PostgreSQL (opsional, default pakai H2).

### Menjalankan di Lokal
1.  **Clone repository:**
    ```bash
    git clone https://github.com/kawreenazzhra/minari-PBO.git
    cd minari-PBO
    ```

2.  **Build project:**
    ```bash
    mvn clean install
    ```

3.  **Jalankan aplikasi:**
    ```bash
    mvn spring-boot:run
    ```
    Aplikasi akan berjalan di `http://localhost:8080`.

### Menggunakan Docker
1.  **Build image:**
    ```bash
    docker build -t minari-ecommerce .
    ```
2.  **Jalankan container:**
    ```bash
    docker run -p 8080:8080 minari-ecommerce
    ```

## ☁️ Deployment

Aplikasi ini sudah ter-kontainerisasi dengan **Docker**, membuatnya mudah di-deploy ke platform cloud manapun yang mendukung container (Koyeb, Railway, Render, AWS App Runner).

### Environment Variables Wajib
Saat deploy, pastikan Anda mengatur environment variable berikut di dashboard cloud provider Anda:

| Variable | Deskripsi | Contoh |
|----------|-----------|--------|
| `SPRING_DATASOURCE_URL` | Koneksi String Neon PostgreSQL | `jdbc:postgresql://ep-xyz.neon.tech/neondb...` |
| `SPRING_DATASOURCE_USERNAME` | Username Database | `neondb_owner` |
| `SPRING_DATASOURCE_PASSWORD` | Password Database | `******` |
| `CLOUDINARY_CLOUD_NAME` | Nama Cloudinary | `minari-storage` |
| `CLOUDINARY_API_KEY` | API Key Cloudinary | `123456789` |
| `CLOUDINARY_API_SECRET` | Secret Cloudinary | `abcdef-ghijkl` |

### Deploy dengan Docker
1.  **Push ke GitHub.**
2.  **Hubungkan Repository** ke cloud provider pilihan (misal: Koyeb/Railway).
3.  **Set Environment Variables.**
4.  **Deploy!** 🚀

## 📂 Struktur Project
```
src/main/
├── java/com/minari/ecommerce/  # Logika Backend (Controllers, Services, Repositories)
└── resources/
    ├── static/                 # CSS, JS, Gambar
    └── templates/              # View HTML Thymeleaf
```

## 📄 Lisensi
[Tambahkan detail lisensi di sini]
