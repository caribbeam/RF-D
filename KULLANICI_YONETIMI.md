# 👥 Kullanıcı Yönetimi Sistemi

## 📋 Genel Bakış

Çoklu terminal desteği ile kullanıcı takibi ve yetkilendirme sistemi.

---

## 🎯 ÖZELLİKLER

### 1. Kullanıcı Sistemi
```
✅ Kullanıcı adı + Şifre (SHA-256 hash)
✅ Ad Soyad
✅ Personel Numarası (benzersiz)
✅ Rol (Admin, Operatör, Görüntüleyici)
✅ Departman, Telefon, Email
✅ Aktif/Pasif durum
✅ Son giriş zamanı
✅ Terminal ID takibi
```

### 2. Oturum Yönetimi
```
✅ Giriş/Çıkış kayıtları
✅ Oturum süresi
✅ Yapılan işlem sayısı
✅ Terminal bazlı takip
✅ Aktif oturum kontrolü
```

### 3. Aktivite Logu
```
✅ Tüm işlemler kaydedilir
✅ Kullanıcı bilgisi
✅ İşlem tipi (Giriş, Ürün Girişi, Çıkış, Sayım, vb.)
✅ Ürün kodu ve miktar
✅ Terminal ID
✅ Zaman damgası
✅ Senkronizasyon durumu
```

---

## 👤 KULLANICI ROLLERİ

### Admin (Yönetici)
```
✅ Tüm yetkiler
✅ Kullanıcı oluşturma/düzenleme
✅ Sistem ayarları
✅ Raporlar
✅ Veri silme
```

### Operator (Operatör)
```
✅ Ürün girişi
✅ Ürün çıkışı
✅ Envanter sayımı
✅ RFID okuma
✅ Etiket yazdırma
❌ Kullanıcı yönetimi
❌ Sistem ayarları
```

### Viewer (Görüntüleyici)
```
✅ Ürün listesi görüntüleme
✅ Stok durumu görüntüleme
✅ Raporları görüntüleme
❌ Veri girişi/düzenleme
❌ Silme işlemleri
```

---

## 🔐 GİRİŞ SİSTEMİ

### Varsayılan Admin Hesabı
```
Kullanıcı Adı: admin
Şifre: admin123
Personel No: ADMIN001
Rol: Admin
```

### İlk Giriş
```kotlin
1. Uygulama açılır
2. Login ekranı gösterilir
3. Kullanıcı adı ve şifre girilir
4. Giriş yapılır
5. Terminal ID otomatik atanır
6. Oturum başlar
7. Ana ekrana yönlendirilir
```

### Güvenlik
```
✅ Şifreler SHA-256 ile hash'lenir
✅ Düz metin şifre saklanmaz
✅ Oturum kontrolü
✅ Yetkisiz erişim engellenir
✅ Geri tuşu devre dışı (giriş zorunlu)
```

---

## 📱 ÇOKLU TERMİNAL DESTEĞİ

### Terminal ID
```kotlin
// Android cihaz ID'si kullanılır
val terminalId = Settings.Secure.getString(
    context.contentResolver, 
    Settings.Secure.ANDROID_ID
)

// Örnek: "a1b2c3d4e5f6g7h8"
```

### Kullanım Senaryosu
```
Depo: 5 adet Chainway C5 terminal
Terminal 1: Ahmet (Operatör) - Giriş bölümü
Terminal 2: Mehmet (Operatör) - Çıkış bölümü
Terminal 3: Ayşe (Operatör) - Sayım
Terminal 4: Fatma (Admin) - Yönetim
Terminal 5: Ali (Viewer) - Kontrol

Her terminal kendi ID'si ile işlem yapar.
Tüm işlemler merkezi veritabanında toplanır.
```

---

## 📊 AKTİVİTE TAKİBİ

### Kaydedilen İşlemler

#### 1. Giriş/Çıkış
```kotlin
ActivityType.LOGIN
- Kullanıcı: Ahmet Yılmaz
- Terminal: Terminal-001
- Zaman: 08:30:00
- Açıklama: "Ahmet Yılmaz giriş yaptı"
```

#### 2. Ürün Girişi
```kotlin
ActivityType.PRODUCT_ENTRY
- Kullanıcı: Ahmet Yılmaz
- Ürün Kodu: DELL-XPS-001
- Miktar: 10
- Terminal: Terminal-001
- Zaman: 09:15:23
- Açıklama: "10 adet ürün girişi yapıldı"
```

#### 3. Ürün Çıkışı
```kotlin
ActivityType.PRODUCT_EXIT
- Kullanıcı: Mehmet Demir
- Ürün Kodu: DELL-XPS-001
- Miktar: 5
- Terminal: Terminal-002
- Zaman: 10:30:45
- Açıklama: "5 adet ürün çıkışı yapıldı"
```

#### 4. Envanter Sayımı
```kotlin
ActivityType.INVENTORY_COUNT
- Kullanıcı: Ayşe Kaya
- Terminal: Terminal-003
- Zaman: 14:00:00
- Açıklama: "Envanter sayımı başlatıldı - 84 etiket okundu"
```

---

## 💻 KULLANIM ÖRNEKLERİ

### 1. Kullanıcı Girişi
```kotlin
val userManager = UserManager(context, database)

lifecycleScope.launch {
    when (val result = userManager.login("ahmet", "12345")) {
        is LoginResult.Success -> {
            println("Hoş geldiniz, ${result.user.fullName}")
            // Ana ekrana git
        }
        is LoginResult.Error -> {
            println("Hata: ${result.message}")
        }
    }
}
```

### 2. Aktivite Kaydetme
```kotlin
// Ürün girişi yapıldığında
userManager.logActivity(
    activityType = ActivityType.PRODUCT_ENTRY,
    description = "10 adet DELL-XPS-001 ürün girişi",
    productCode = "DELL-XPS-001",
    quantity = 10
)
```

### 3. Yetki Kontrolü
```kotlin
if (userManager.hasPermission(Permission.ENTRY)) {
    // Ürün girişi yapabilir
    performProductEntry()
} else {
    Toast.makeText(context, "Yetkiniz yok", Toast.LENGTH_SHORT).show()
}
```

### 4. Yeni Kullanıcı Oluşturma
```kotlin
lifecycleScope.launch {
    val result = userManager.createUser(
        username = "mehmet",
        password = "12345",
        fullName = "Mehmet Demir",
        employeeId = "EMP002",
        role = UserRole.OPERATOR,
        department = "Depo",
        phone = "0555 123 4567"
    )
    
    when (result) {
        is CreateUserResult.Success -> {
            println("Kullanıcı oluşturuldu: ${result.user.fullName}")
        }
        is CreateUserResult.Error -> {
            println("Hata: ${result.message}")
        }
    }
}
```

### 5. Kullanıcı Çıkışı
```kotlin
lifecycleScope.launch {
    userManager.logout()
    // Login ekranına dön
}
```

---

## 📈 RAPORLAMA

### Kullanıcı Aktivite Raporu
```kotlin
// Bugünkü aktiviteler
val todayStart = /* bugünün başlangıcı */
val activities = database.userDao().getTodayActivities(todayStart)

// Kullanıcıya göre
val userActivities = database.userDao().getUserActivities(userId, limit = 100)

// İşlem tipine göre
val entries = database.userDao().getActivitiesByType(ActivityType.PRODUCT_ENTRY)
```

### En Aktif Kullanıcılar
```kotlin
val since = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000) // Son 7 gün
val mostActive = database.userDao().getMostActiveUsers(since, limit = 10)
```

### Oturum Raporları
```kotlin
// Aktif oturumlar
val activeSessions = database.userDao().getActiveSessions()

// Kullanıcının oturumları
val userSessions = database.userDao().getUserSessions(userId)
```

---

## 🔄 VERİ SENKRONIZASYONU

### Merkezi Sunucu ile Senkronizasyon
```kotlin
// Senkronize edilmemiş aktiviteler
val unsyncedActivities = database.userDao()
    .getActivitiesByDateRange(startTime, endTime)
    .filter { !it.synced }

// Sunucuya gönder
for (activity in unsyncedActivities) {
    sendToServer(activity)
    // Başarılı ise synced = true yap
}
```

---

## 🛡️ GÜVENLİK ÖNERİLERİ

### 1. Şifre Politikası
```
✅ Minimum 6 karakter
✅ İlk girişte şifre değiştirme zorunlu
✅ Periyodik şifre değiştirme
✅ Güçlü şifre kullanımı
```

### 2. Oturum Yönetimi
```
✅ Otomatik çıkış (inaktivite)
✅ Tek oturum kontrolü
✅ Şüpheli aktivite tespiti
✅ IP/Terminal takibi
```

### 3. Yetkilendirme
```
✅ Rol bazlı erişim kontrolü
✅ Hassas işlemler için onay
✅ Audit log tutma
✅ Yetkisiz erişim engelleme
```

---

## 📋 VERİTABANI YAPISI

### users Tablosu
```sql
CREATE TABLE users (
    id INTEGER PRIMARY KEY,
    username TEXT UNIQUE NOT NULL,
    password TEXT NOT NULL,  -- SHA-256 hash
    full_name TEXT NOT NULL,
    employee_id TEXT UNIQUE NOT NULL,
    role TEXT NOT NULL,  -- ADMIN, OPERATOR, VIEWER
    department TEXT,
    phone TEXT,
    email TEXT,
    is_active INTEGER DEFAULT 1,
    created_at INTEGER NOT NULL,
    last_login INTEGER,
    terminal_id TEXT
);
```

### user_sessions Tablosu
```sql
CREATE TABLE user_sessions (
    id INTEGER PRIMARY KEY,
    user_id INTEGER NOT NULL,
    username TEXT NOT NULL,
    full_name TEXT NOT NULL,
    terminal_id TEXT NOT NULL,
    login_time INTEGER NOT NULL,
    logout_time INTEGER,
    session_duration INTEGER,
    actions_count INTEGER DEFAULT 0
);
```

### user_activities Tablosu
```sql
CREATE TABLE user_activities (
    id INTEGER PRIMARY KEY,
    user_id INTEGER NOT NULL,
    username TEXT NOT NULL,
    full_name TEXT NOT NULL,
    activity_type TEXT NOT NULL,
    description TEXT NOT NULL,
    product_code TEXT,
    quantity INTEGER,
    terminal_id TEXT NOT NULL,
    timestamp INTEGER NOT NULL,
    synced INTEGER DEFAULT 0
);
```

---

## 🎯 KULLANIM SENARYOLARI

### Senaryo 1: Sabah Vardiyası
```
08:00 - Ahmet (Operatör) Terminal-001'de giriş yapar
08:15 - 50 ürün girişi yapar
10:30 - 20 ürün çıkışı yapar
12:00 - Öğle molası (çıkış yapmaz, oturum açık)
13:00 - Devam eder
17:00 - Çıkış yapar
```

### Senaryo 2: Envanter Sayımı
```
14:00 - Ayşe (Operatör) Terminal-003'te giriş yapar
14:05 - Envanter sayımı başlatır
14:10 - A koridorunu tarar (25 ürün)
14:20 - B koridorunu tarar (30 ürün)
14:30 - C koridorunu tarar (29 ürün)
14:40 - Sayımı tamamlar (84 ürün)
14:45 - Rapor oluşturur
15:00 - Çıkış yapar
```

### Senaryo 3: Yönetici Kontrolü
```
09:00 - Fatma (Admin) Terminal-004'te giriş yapar
09:10 - Günlük raporları inceler
09:30 - Düşük stok uyarılarını kontrol eder
10:00 - Yeni kullanıcı oluşturur (Mehmet - Operatör)
10:30 - Sistem ayarlarını günceller
11:00 - Çıkış yapar
```

---

## 🚀 SONUÇ

Kullanıcı yönetimi sistemi ile:
- ✅ Her işlem kimin tarafından yapıldığı bilinir
- ✅ Çoklu terminal desteği vardır
- ✅ Yetkilendirme kontrolü sağlanır
- ✅ Detaylı aktivite takibi yapılır
- ✅ Güvenli oturum yönetimi vardır
- ✅ Merkezi raporlama mümkündür

**Sistem artık kurumsal kullanıma hazır!** 🎉
