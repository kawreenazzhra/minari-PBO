-- Buat di terminal MySQL atau MySQL Workbench

-- 1. Buat database
CREATE DATABASE IF NOT EXISTS minari_db 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

-- 2. Buat user (jangan pakai root untuk production)
CREATE USER IF NOT EXISTS 'minari_app'@'localhost' IDENTIFIED BY 'StrongPassword123!';

-- 3. Berikan hak akses
GRANT ALL PRIVILEGES ON minari_db.* TO 'minari_app'@'localhost';

-- 4. Jika menggunakan remote connection:
CREATE USER IF NOT EXISTS 'minari_app'@'%' IDENTIFIED BY 'StrongPassword123!';
GRANT ALL PRIVILEGES ON minari_db.* TO 'minari_app'@'%';

-- 5. Refresh privileges
FLUSH PRIVILEGES;

-- 6. Verifikasi
SHOW DATABASES;
SELECT user, host FROM mysql.user;