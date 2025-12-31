# PANDUAN SETUP SONARQUBE

## Pilihan 1: SonarCloud (Recommended - Gratis & Online)

### Langkah-langkah:

1. **Buat Akun SonarCloud**
   - Kunjungi: https://sonarcloud.io
   - Login dengan GitHub account
   - Klik "Analyze new project"

2. **Import Project dari GitHub**
   - Pilih repository: `kawreenazzhra/minari-PBO`
   - Set organization name (misal: `kawreenazzhra`)
   - Generate token

3. **Update pom.xml**
   Edit bagian `<properties>` di `pom.xml`:
   ```xml
   <sonar.organization>kawreenazzhra</sonar.organization>
   <sonar.projectKey>kawreenazzhra_minari-PBO</sonar.projectKey>
   ```

4. **Jalankan Analysis**
   ```bash
   mvn clean verify sonar:sonar -Dsonar.token=YOUR_TOKEN_HERE
   ```

5. **Lihat Hasil**
   - Buka dashboard SonarCloud
   - Screenshot hasil untuk laporan

---

## Pilihan 2: SonarQube Local (Perlu Install Server)

### Langkah-langkah:

1. **Download SonarQube**
   - Download dari: https://www.sonarsource.com/products/sonarqube/downloads/
   - Extract ke folder (misal: C:\sonarqube)

2. **Jalankan SonarQube Server**
   ```bash
   cd C:\sonarqube\bin\windows-x86-64
   StartSonar.bat
   ```
   - Tunggu sampai server ready
   - Akses: http://localhost:9000
   - Login default: admin/admin

3. **Buat Project di SonarQube**
   - Klik "Create Project"
   - Project key: `minari-ecommerce`
   - Generate token

4. **Update pom.xml**
   Edit bagian `<properties>` di `pom.xml`:
   ```xml
   <sonar.host.url>http://localhost:9000</sonar.host.url>
   <sonar.projectKey>minari-ecommerce</sonar.projectKey>
   ```

5. **Jalankan Analysis**
   ```bash
   mvn clean verify sonar:sonar -Dsonar.token=YOUR_TOKEN_HERE
   ```

6. **Lihat Hasil**
   - Buka http://localhost:9000
   - Pilih project "minari-ecommerce"
   - Screenshot hasil untuk laporan

---

## Screenshot yang Perlu Diambil:

1. **Overview Dashboard** - Menampilkan:
   - Reliability Rating
   - Security Rating
   - Maintainability Rating
   - Coverage %
   - Duplications %

2. **Issues Tab** - Menampilkan:
   - Bugs
   - Vulnerabilities
   - Code Smells

3. **Code Coverage** - Menampilkan:
   - Overall coverage percentage
   - Coverage per file/package

---

## Troubleshooting:

### Error: "No plugin found for prefix 'sonar'"
✅ **SOLVED** - Plugin sudah ditambahkan ke pom.xml

### Error: "Unauthorized"
- Pastikan token yang digunakan benar
- Token bisa di-generate dari SonarCloud/SonarQube dashboard

### Error: "Project not found"
- Pastikan `sonar.projectKey` di pom.xml sama dengan yang di dashboard
- Pastikan `sonar.organization` benar (untuk SonarCloud)

---

## Tips:

- **SonarCloud** lebih mudah karena tidak perlu install server
- **SonarQube Local** lebih private tapi perlu install
- Hasil analysis akan sama untuk kedua pilihan
- Untuk submission, screenshot dari SonarCloud sudah cukup
