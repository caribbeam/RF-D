# 🔄 SQL Mikro Entegrasyonu

## 📋 Genel Bakış

Mevcut SQL Mikro veritabanınızdaki ürün kodlarını (AFP002460 gibi) RFID sistemine entegre eder.

---

## 🎯 SENARYO

### Mevcut Durum:
```
SQL Mikro Veritabanı
├── STOKLAR Tablosu
│   ├── AFP002460 - Laptop Dell XPS 15
│   ├── AFP002461 - HP Pavilion 14
│   ├── AFP002462 - Lenovo ThinkPad T14
│   └── ... (binlerce ürün)
```

### Hedef:
```
1. SQL Mikro'dan ürün kodlarını çek
2. RFID sistemine aktar
3. Her ürüne RFID etiketi ata
4. Barkod etiket yazdır
5. Zamanla tüm ürünler RFID'ye geçecek
```

---

## 🔧 KURULUM

### 1. Bağımlılıklar (build.gradle)
```gradle
dependencies {
    // JTDS - SQL Server driver (Android için)
    implementation 'net.sourceforge.jtds:jtds:1.3.1'
    
    // Mevcut bağımlılıklar...
}
```

### 2. İzinler (AndroidManifest.xml)
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

---

## 💻 KULLANIM

### 1. SQL Server Bağlantısı Yapılandırma

```kotlin
val sqlManager = SqlServerManager(context, database)

// Bağlantı bilgilerini ayarla
sqlManager.configure(
    serverIp = "192.168.1.100",      // SQL Server IP
    serverPort = "1433",              // Port (varsayılan 1433)
    databaseName = "MIKRODB",         // Veritabanı adı
    username = "sa",                  // Kullanıcı adı
    password = "YourPassword123"      // Şifre
)
```

### 2. Tüm Ürünleri Senkronize Et

```kotlin
lifecycleScope.launch {
    // Bağlan
    val connected = sqlManager.connect()
    
    if (connected) {
        // Ürünleri çek ve aktar
        when (val result = sqlManager.syncProductsFromSqlMikro()) {
            is SyncResult.Success -> {
                println("✅ ${result.totalProducts} ürün çekildi")
                println("✅ ${result.importedProducts} yeni ürün eklendi")
                Toast.makeText(
                    context,
                    result.message,
                    Toast.LENGTH_LONG
                ).show()
            }
            is SyncResult.Error -> {
                println("❌ Hata: ${result.message}")
                Toast.makeText(
                    context,
                    "Senkronizasyon hatası: ${result.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        
        // Bağlantıyı kapat
        sqlManager.disconnect()
    } else {
        Toast.makeText(
            context,
            "SQL Server'a bağlanılamadı",
            Toast.LENGTH_LONG
        ).show()
    }
}
```

### 3. Tek Ürün Sorgula

```kotlin
lifecycleScope.launch {
    sqlManager.connect()
    
    // AFP002460 kodlu ürünü getir
    val product = sqlManager.getProductByCode("AFP002460")
    
    if (product != null) {
        println("Ürün Bulundu:")
        println("Kod: ${product.productCode}")
        println("Ad: ${product.productName}")
        println("Birim: ${product.unit}")
        println("Barkod: ${product.barcode}")
    } else {
        println("Ürün bulunamadı")
    }
    
    sqlManager.disconnect()
}
```

### 4. Ürün Kodu ile Arama

```kotlin
lifecycleScope.launch {
    sqlManager.connect()
    
    // AFP ile başlayan ürünleri getir
    val products = sqlManager.searchProductsByCode("AFP")
    
    println("${products.size} ürün bulundu:")
    products.forEach { product ->
        println("${product.productCode} - ${product.productName}")
    }
    
    sqlManager.disconnect()
}
```

---

## 📊 İŞ AKIŞI

### Senaryo 1: İlk Kurulum (Toplu Aktarım)

```
1. ADIM: SQL Mikro'dan Tüm Ürünleri Çek
   ├─ Ayarlar → SQL Mikro Entegrasyonu
   ├─ Bağlantı Bilgilerini Gir
   ├─ "Tüm Ürünleri Senkronize Et" butonuna bas
   └─ Sonuç: 5000 ürün çekildi, 5000 yeni ürün eklendi

2. ADIM: RFID Etiketleri Ata
   ├─ Ürün Girişi ekranına git
   ├─ Ürün Kodu: AFP002460 gir
   ├─ Sistem SQL Mikro'dan bilgileri doldurur
   ├─ RFID etiket oku
   ├─ Kaydet
   └─ Sonuç: AFP002460 → RFID E200123456789ABC eşleşti

3. ADIM: Barkod Etiket Yazdır
   ├─ "Barkod Yazdır" butonuna bas
   ├─ Zebra yazıcıdan etiket çıkar
   ├─ Etiket içeriği:
   │   ├─ Ürün Kodu: AFP002460
   │   ├─ Ürün Adı: Laptop Dell XPS 15
   │   ├─ Barkod: AFP002460
   │   └─ RFID EPC: E200123456789ABC
   └─ Ürüne yapıştır
```

### Senaryo 2: Günlük Kullanım (Yeni Ürün)

```
DURUM: Depoda olmayan yeni bir ürün geldi

1. Ürün Girişi ekranına git
2. Ürün Kodu: AFP005678 gir
3. Sistem kontrol eder:
   ├─ Yerel veritabanında var mı? → YOK
   ├─ SQL Mikro'da var mı? → VAR
   └─ Bilgileri otomatik doldurur:
       ├─ Ad: Yeni Laptop Model
       ├─ Birim: Adet
       └─ Barkod: 1234567890123

4. RFID etiket oku → E200999888777666
5. Miktar: 10 gir
6. Kaydet
7. Barkod Yazdır → 10 etiket çıkar
8. Sonuç: Yeni ürün sisteme eklendi ve RFID'ye geçti
```

### Senaryo 3: Periyodik Senkronizasyon

```
HER GÜN SABAH:

1. Otomatik senkronizasyon çalışır
2. SQL Mikro'dan yeni ürünler çekilir
3. Mevcut ürünlerin bilgileri güncellenir
4. Rapor oluşturulur:
   ├─ 50 yeni ürün eklendi
   ├─ 120 ürün güncellendi
   └─ Toplam: 5170 ürün
```

---

## 🔄 SENKRONIZASYON MANTIĞI

### Ürün Zaten Varsa:
```kotlin
// AFP002460 zaten sistemde
val existing = database.productDao().findByProductCode("AFP002460")

if (existing != null) {
    // Sadece isim ve açıklamayı güncelle
    // MİKTAR DEĞİŞMEZ! (Çünkü depo miktarı farklı)
    val updated = existing.copy(
        name = sqlProduct.productName,
        description = sqlProduct.description
    )
    database.productDao().update(updated)
}
```

### Ürün Yoksa:
```kotlin
// AFP005678 sistemde yok, yeni ekle
val product = ProductEntity(
    productCode = "AFP005678",
    name = "Yeni Laptop Model",
    quantity = 0,  // Başlangıçta 0
    unit = "Adet",
    rfidTag = null,  // Henüz atanmadı
    barcode = "1234567890123"
)
database.productDao().insert(product)
```

---

## 📋 SQL MIKRO TABLO YAPISI

### Örnek STOKLAR Tablosu:
```sql
CREATE TABLE STOKLAR (
    sto_kod VARCHAR(50),           -- AFP002460
    sto_isim VARCHAR(200),         -- Laptop Dell XPS 15
    sto_birim1_ad VARCHAR(20),     -- Adet
    sto_perakende_vergi VARCHAR(50), -- Barkod
    sto_marka VARCHAR(100),        -- Dell
    sto_model VARCHAR(100),        -- XPS 15
    sto_aciklama VARCHAR(500)      -- Açıklama
)
```

### Sorgu Örneği:
```sql
SELECT 
    sto_kod AS product_code,
    sto_isim AS product_name,
    sto_birim1_ad AS unit,
    sto_perakende_vergi AS barcode,
    sto_marka AS brand,
    sto_model AS model,
    sto_aciklama AS description
FROM STOKLAR
WHERE sto_kod = 'AFP002460'
```

---

## ⚙️ AYARLAR EKRANİ

### SQL Mikro Bağlantı Ayarları:
```
┌─────────────────────────────────────┐
│  SQL Mikro Entegrasyonu             │
├─────────────────────────────────────┤
│                                     │
│  Server IP:    [192.168.1.100    ] │
│  Port:         [1433             ] │
│  Veritabanı:   [MIKRODB          ] │
│  Kullanıcı:    [sa               ] │
│  Şifre:        [••••••••••       ] │
│                                     │
│  [Bağlantıyı Test Et]              │
│  [Tüm Ürünleri Senkronize Et]      │
│  [Otomatik Senkronizasyon: ✓]      │
│                                     │
│  Son Senkronizasyon:                │
│  15.01.2024 08:30                   │
│  5170 ürün                          │
│                                     │
└─────────────────────────────────────┘
```

---

## 🎯 KULLANIM ÖRNEKLERİ

### Örnek 1: Ürün Girişi (Mevcut Kod)
```
Kullanıcı: Ahmet
Terminal: Terminal-001

1. Ürün Girişi ekranı
2. Ürün Kodu: AFP002460 gir
3. Sistem:
   ├─ Yerel DB'de var mı? → VAR
   ├─ Bilgileri doldur:
   │   ├─ Ad: Laptop Dell XPS 15
   │   ├─ Mevcut Miktar: 25
   │   └─ Konum: A-3-Orta
   └─ RFID: E200123456789ABC (zaten atanmış)

4. Yeni Miktar: 10 gir
5. Kaydet
6. Sonuç: 25 + 10 = 35 adet
```

### Örnek 2: Ürün Girişi (Yeni Kod)
```
Kullanıcı: Mehmet
Terminal: Terminal-002

1. Ürün Girişi ekranı
2. Ürün Kodu: AFP005999 gir
3. Sistem:
   ├─ Yerel DB'de var mı? → YOK
   ├─ SQL Mikro'da var mı? → VAR
   ├─ Bilgileri çek ve doldur:
   │   ├─ Ad: Yeni Ürün XYZ
   │   ├─ Birim: Koli
   │   └─ Barkod: 9876543210123
   └─ RFID: Henüz yok

4. RFID Oku → E200NEWPRODUCT123
5. Miktar: 50 gir
6. Konum: B-5-Alt seç
7. Kaydet
8. Barkod Yazdır → 50 etiket
9. Sonuç: Yeni ürün eklendi, RFID atandı
```

### Örnek 3: Toplu Senkronizasyon
```
Yönetici: Fatma
Terminal: Terminal-004

1. Ayarlar → SQL Mikro Entegrasyonu
2. "Tüm Ürünleri Senkronize Et"
3. İşlem başladı...
4. İlerleme:
   ├─ 1000/5000 ürün işlendi
   ├─ 2000/5000 ürün işlendi
   ├─ 3000/5000 ürün işlendi
   ├─ 4000/5000 ürün işlendi
   └─ 5000/5000 ürün işlendi

5. Sonuç:
   ├─ Toplam: 5000 ürün
   ├─ Yeni: 150 ürün
   ├─ Güncellenen: 4850 ürün
   └─ Süre: 2 dakika 30 saniye
```

---

## 🔒 GÜVENLİK

### Bağlantı Güvenliği:
```
✅ Şifreler şifreli saklanır
✅ SSL/TLS desteği
✅ Sadece yetkili kullanıcılar senkronize edebilir
✅ Bağlantı logları tutulur
✅ Hata durumunda otomatik yeniden deneme
```

### Veri Güvenliği:
```
✅ Sadece okuma yetkisi (INSERT/UPDATE/DELETE yok)
✅ SQL injection koruması
✅ Timeout ayarları
✅ Bağlantı havuzu yönetimi
```

---

## 📈 RAPORLAMA

### Senkronizasyon Raporu:
```
Tarih: 15.01.2024 08:30
Kullanıcı: Fatma (Admin)
Terminal: Terminal-004

Sonuçlar:
├─ Toplam Ürün: 5000
├─ Yeni Eklenen: 150
├─ Güncellenen: 4850
├─ Hatalı: 0
├─ Süre: 2dk 30sn
└─ Durum: Başarılı ✅

RFID Durumu:
├─ RFID Atanmış: 3200 ürün (64%)
├─ RFID Bekleyen: 1800 ürün (36%)
└─ Hedef: %100 RFID'ye geçiş
```

---

## 🚀 SONUÇ

### Avantajlar:
```
✅ Mevcut SQL Mikro verileriniz korunur
✅ Ürün kodları (AFP002460) aynı kalır
✅ Zamanla RFID'ye geçiş yapılır
✅ İki sistem birlikte çalışır
✅ Yeni ürünler otomatik senkronize edilir
✅ Barkod etiketler ürün koduna göre basılır
```

### İş Akışı:
```
SQL Mikro (Ana Sistem)
    ↓
    ↓ Senkronizasyon
    ↓
RFID Sistemi (Depo)
    ↓
    ↓ RFID Etiket Atama
    ↓
Barkod Yazdırma
    ↓
    ↓ Ürüne Yapıştır
    ↓
Tam Entegrasyon ✅
```

---

## 📞 DESTEK

**Teknik Destek:** denetimsite@gmail.com
**Dokümantasyon:** SQL_MIKRO_ENTEGRASYONU.md

---

**Sistem artık SQL Mikro ile tam entegre!** 🔄📦✅
