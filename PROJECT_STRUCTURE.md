# Analisis Struktur Project: Minari E-Commerce

Dokumen ini menjelaskan struktur kode program Minari E-Commerce, berfokus pada alur pengguna (**Guest, Customer, Admin**), serta detail teknis mengenai **Hybrid Cart System**, **MVC Architecture**, dan **Database Strategy**.

## 1. Konsep Role & Akses

Sistem menggunakan **Spring Security** dengan strategi Role-Based Access Control (RBAC).

*   **Guest (Pengunjung)**:
    *   **Identifikasi**: Tidak login (Anonymous).
    *   **Hak Akses**: Home (`/`), Katalog Produk (`/products`), Pencarian (`/search`), Keranjang (`/cart` - via Session).
    *   **Keterbatasan**: Tidak bisa Checkout, tidak bisa lihat Profile.

*   **Customer (Pelanggan)**:
    *   **Identifikasi**: Login dengan Email/Password. Role: `ROLE_CUSTOMER`.
    *   **Hak Akses**: Semua akses Guest + Checkout (`/checkout`), Order History (`/orders`), Profile (`/profile`), Wishlist (`/wishlist`).
    *   **Penyimpanan**: Data keranjang disimpan permanen di Database.

*   **Admin (Pengelola)**:
    *   **Identifikasi**: Login admin. Role: `ROLE_ADMIN`.
    *   **Hak Akses**: Dashboard (`/admin`), Manajemen Produk/Kategori (`/admin/products`), Manajemen Order.

---

## 2. Struktur MVC (Model - View - Controller)

Pola arsitektur standard Spring Boot MVC.

### A. Controller (Otak Aplikasi)
Menangani request HTTP dan logika navigasi.

1.  **`HomeController.java`**
    *   Menangani halaman utama (`/`).
    *   Menampilkan *Featured Products* dan *New Arrivals*.
2.  **`ProductController.java`**
    *   Menampilkan detail produk (`/products/detail/{id}`).
    *   Menangani filter kategori dan pencarian.
3.  **`CartController.java`**
    *   **Dual Mode Handler**: Menangani logika keranjang untuk Guest (Session) dan User (Database) secara simultan.
    *   Menyediakan endpoint AJAX untuk update keranjang tanpa reload page.
4.  **`WebOrderController.java`**
    *   Menangani proses Checkout (Address Selection -> Payment -> Confirmation).
    *   Hanya bisa diakses oleh `ROLE_CUSTOMER`.

### B. View (Tampilan)
Menggunakan **Thymeleaf** template engine dengan Layout Dialect.

*   `home.html`: Halaman depan.
*   `products/detail.html`: Halaman detail produk.
*   `cart/view.html`: Tampilan keranjang.
*   `checkout/*.html`: Flow checkout (shipping, payment).

### C. Model (Data)
Entity JPA yang terhubung ke database.

---

## 3. Object Class & Database Structure

Berikut adalah entitas utama dalam sistem.

### Entity: `User` (Table: `users`)
Parent class untuk semua tipe pengguna (Inheritance Strategy: `JOINED`).

| Field | Keterangan |
| :--- | :--- |
| `id` | Primary Key |
| `email` | Unique Login ID |
| `role` | `CUSTOMER` / `ADMIN` |
| `isActive` | Status akun |

### Entity: `Product` (Table: `products`)
Master data produk yang dijual.

| Field | Keterangan |
| :--- | :--- |
| `id` | Primary Key |
| `price` | Harga Asli |
| `stockQuantity` | Stok Fisik |
| `category` | Relasi ke `ProductCategory` |

### Entity: `Order` (Table: `orders`)
Header transaksi pembelian.

| Field | Keterangan |
| :--- | :--- |
| `orderNumber` | String unik (misal: ORD-2024...) |
| `totalAmount` | Total nilai transaksi |
| `status` | `PENDING`, `PAID`, `SHIPPED` |
| `customer` | Relasi ke `User` |

---

## 4. Deep Dive: Hybrid Cart System (Session vs Database)

Fitur keranjang belanja menggunakan pendekatan **Hybrid** yang cerdas di `CartController.java`.

**Logic Flow (`addToCart`):**

1.  **Cek Principal (Login Status)**:
    ```java
    if (principal != null) { ... } else { ... }
    ```

2.  **Jika User Login (Database Mode)**:
    *   Panggil `ShoppingCartService`.
    *   Load/Create `ShoppingCart` entity dari DB.
    *   Simpan item ke tabel `cart_items`.
    *   **Kelebihan**: Data persisten antar device. Login di HP dan Laptop, isi keranjang sama.

3.  **Jika Guest (Session Mode)**:
    *   Ambil List dari `HttpSession` dengan key `"guestCart"`.
    *   Jika belum ada, buat `new ArrayList<CartSessionItem>()`.
    *   Manipulasi list di memory server (RAM).
    *   **Kelebihan**: Cepat, tidak membebani database untuk user yang belum tentu beli.

---

## 5. Deep Dive: Query Database (Repository)

Menggunakan **Spring Data JPA** & **JPQL**.

### `ProductRepository.java`

1.  **`findFeaturedProducts()`**
    *   **Logic**: Mengambil produk dengan rating > 4.0 dan stok > 0.
    *   **Fungsi**: Menampilkan produk unggulan di Home.

2.  **`searchByNameOrDescription(String query)`**
    *   **SQL**: `SELECT ... WHERE LOWER(name) LIKE %q% OR LOWER(description) LIKE %q%`
    *   **Fungsi**: Fitur Search bar di navbar.

3.  **`findByCategory(Category cat)`**
    *   **Fungsi**: Filter produk berdasarkan kategori yang dipilih.

---

## Kesimpulan Alur Data (End-to-End Customer)

1.  **Guest** buka web -> Session Cart aktif.
2.  **Guest** Login -> (Idealnya) Session Cart di-merge ke Database Cart.
3.  **Customer** Checkout:
    *   Validasi Stok (`Product.isInStock()`).
    *   Pilih Alamat (`Address` entity).
    *   Buat `Order` baru -> Copy data dari `CartItem` ke `OrderItem`.
    *   Hapus `ShoppingCart` (kosongkan keranjang).
    *   Update Stok Produk (`product.stockQuantity - qty`).
