# 🏗️ Sistem Mimarisi ve Konum Yönetimi

## 🎯 SİSTEM MİMARİSİ ÖNERİSİ

### Mevcut Durum Analizi:
```
✅ SQL Mikro (Ana ERP Sistemi)
✅ Chainway C5 RFID El Terminalleri (5-10-20 adet)
✅ Web/Desktop CRM Portal (Planlanan)
✅ Depo Yönetimi İhtiyacı
```

---

## 💡 ÖNERİLEN MİMARİ (Hibrit Yaklaşım)

### Yaklaşım 1: RFID Cihaz + Web Portal (ÖNERİLEN) ⭐

```
┌─────────────────────────────────────────────────────────────┐
│                    SQL MİKRO (Ana ERP)                      │
│              (Ürün Kodları, Fiyatlar, Stok)                 │
└──────────────────┬──────────────────────────────────────────┘
                   │
                   │ Senkronizasyon (Günde 1-2 kez)
                   │
┌──────────────────▼──────────────────────────────────────────┐
│              WEB/DESKTOP CRM PORTAL                         │
│         (Merkezi Yönetim ve Raporlama)                      │
│                                                              │
│  ✅ Ürün Yönetimi (Ekleme, Düzenleme, Silme)               │
│  ✅ Konum Yönetimi (Raf değişiklikleri)                    │
│  ✅ Kullanıcı Yönetimi                                      │
│  ✅ Raporlar (Detaylı analizler)                           │
│  ✅ Dashboard (Grafikler, istatistikler)                   │
│  ✅ Ayarlar (Sistem konfigürasyonu)                        │
└──────────────────┬──────────────────────────────────────────┘
                   │
                   │ REST API / WebSocket
                   │ (Gerçek zamanlı senkronizasyon)
                   │
┌──────────────────▼──────────────────────────────────────────┐
│         CHAINWAY C5 RFID EL TERMİNALLERİ                   │
│              (Saha Operasyonları)                           │
│                                                              │
│  ✅ Ürün Giriş/Çıkış (RFID okuma)                          │
│  ✅ Envanter Sayımı (Toplu RFID okuma)                     │
│  ✅ Ürün Arama (RFID ile bul)                              │
│  ✅ Barkod Yazdırma (Zebra yazıcı)                         │
│  ✅ Offline Çalışma (Senkronizasyon sonra)                 │
│  ❌ Konum Değiştirme (Sadece Web'den)                      │
│  ❌ Ürün Ekleme/Silme (Sadece Web'den)                     │
└─────────────────────────────────────────────────────────────┘
```

---

## 🔄 İŞ AKIŞI ÖRNEKLERİ

### Senaryo 1: Yeni Ürün Girişi
```
1. WEB PORTAL (Ofis):
   ├─ Yeni ürün: AFP002460
   ├─ SQL Mikro'dan bilgileri çek
   ├─ Konum: A-3-Orta ata
   ├─ Min stok: 10 belirle
   └─ Kaydet → API'ye gönder

2. RFID CİHAZ (Depo):
   ├─ Ürün Girişi ekranı aç
   ├─ AFP002460 gir
   ├─ Bilgiler otomatik geldi (API'den)
   ├─ Miktar: 50 adet gir
   ├─ RFID oku: E200123456789ABC
   ├─ Kaydet
   └─ Barkod yazdır (50 etiket)

3. SİSTEM:
   ├─ RFID cihaz → API'ye gönder
   ├─ API → Veritabanına kaydet
   ├─ API → SQL Mikro'ya senkronize et
   └─ ✅ Tamamlandı
```

### Senaryo 2: Konum Değişikliği (X Rafından Y Rafına)
```
1. WEB PORTAL (Ofis):
   ├─ Ürün Listesi → AFP002460 bul
   ├─ Mevcut Konum: A-3-Orta
   ├─ Yeni Konum: B-5-Alt seç
   ├─ Kaydet
   └─ API'ye gönder

2. SİSTEM:
   ├─ API → Veritabanını güncelle
   ├─ API → RFID cihazlara bildir
   └─ ✅ Tamamlandı

3. RFID CİHAZ (Depo):
   ├─ Senkronizasyon yap
   ├─ AFP002460 bilgileri güncellendi
   ├─ Yeni konum: B-5-Alt
   └─ ✅ Güncel
```

### Senaryo 3: Envanter Sayımı
```
1. RFID CİHAZ (Depo):
   ├─ Envanter Sayımı başlat
   ├─ A-3-Orta rafını tara
   ├─ 84 etiket okundu
   ├─ Liste oluşturuldu
   ├─ Kaydet
   └─ API'ye gönder

2. WEB PORTAL (Ofis):
   ├─ Envanter Raporu aç
   ├─ A-3-Orta rafı seç
   ├─ 84 ürün görüntülendi
   ├─ Eksik/Fazla analizi
   └─ Rapor al (PDF/Excel)
```

### Senaryo 4: Ürün Arama (Kayıp Ürün)
```
1. WEB PORTAL (Ofis):
   ├─ AFP002460 nerede?
   ├─ Konum: A-3-Orta
   ├─ RFID: E200123456789ABC
   └─ Bilgi personele ilet

2. RFID CİHAZ (Depo):
   ├─ Ürün Arama ekranı
   ├─ AFP002460 gir
   ├─ Konum: A-3-Orta gösterildi
   ├─ Rafa git
   ├─ RFID tara
   └─ ✅ Bulundu
```

---

## 🏢 KONUM YÖNETİMİ SİSTEMİ

### Konum Yapısı:
```
DEPO
├── A Koridoru
│   ├── A-1 Rafı
│   │   ├── A-1-Üst
│   │   ├── A-1-Orta
│   │   └── A-1-Alt
│   ├── A-2 Rafı
│   │   ├── A-2-Üst
│   │   ├── A-2-Orta
│   │   └── A-2-Alt
│   └── A-3 Rafı
│       ├── A-3-Üst
│       ├── A-3-Orta
│       └── A-3-Alt
├── B Koridoru
│   ├── B-1 Rafı
│   └── ...
└── C Koridoru
    └── ...
```

### Web Portal - Konum Yönetimi:
```
┌─────────────────────────────────────────────────┐
│  Konum Yönetimi                                 │
├─────────────────────────────────────────────────┤
│                                                 │
│  [Yeni Konum Ekle]  [Konum Düzenle]  [Sil]    │
│                                                 │
│  ┌─────────────────────────────────────────┐   │
│  │ Koridor  │ Raf  │ Seviye │ Durum       │   │
│  ├─────────────────────────────────────────┤   │
│  │ A        │ 1    │ Üst    │ ✅ Aktif    │   │
│  │ A        │ 1    │ Orta   │ ✅ Aktif    │   │
│  │ A        │ 1    │ Alt    │ ✅ Aktif    │   │
│  │ A        │ 2    │ Üst    │ ✅ Aktif    │   │
│  │ A        │ 2    │ Orta   │ ⚠️ Dolu     │   │
│  │ A        │ 2    │ Alt    │ ✅ Aktif    │   │
│  │ B        │ 1    │ Üst    │ ❌ Bakımda  │   │
│  └─────────────────────────────────────────┘   │
│                                                 │
│  Toplam: 150 Konum                             │
│  Dolu: 85 Konum                                │
│  Boş: 60 Konum                                 │
│  Bakımda: 5 Konum                              │
│                                                 │
└─────────────────────────────────────────────────┘
```

### Web Portal - Ürün Konum Değiştirme:
```
┌─────────────────────────────────────────────────┐
│  Ürün Konum Değiştirme                         │
├─────────────────────────────────────────────────┤
│                                                 │
│  Ürün Kodu:  [AFP002460                    ]   │
│  Ürün Adı:   Laptop Dell XPS 15                │
│  Miktar:     50 Adet                           │
│                                                 │
│  Mevcut Konum:                                 │
│  ┌───────────────────────────────────────┐     │
│  │  A-3-Orta                             │     │
│  │  (50 adet burada)                     │     │
│  └───────────────────────────────────────┘     │
│                                                 │
│  Yeni Konum:                                   │
│  Koridor:  [B          ▼]                      │
│  Raf:      [5          ▼]                      │
│  Seviye:   [Alt        ▼]                      │
│                                                 │
│  Taşınacak Miktar: [50              ]          │
│                                                 │
│  Not: [Yer değişikliği - Yeni alan      ]      │
│                                                 │
│  [İptal]  [Kaydet ve Taşı]                     │
│                                                 │
└─────────────────────────────────────────────────┘
```

---

## 📱 RFID CİHAZ ÖZELLİKLERİ

### Yapabilecekler:
```
✅ Ürün Giriş/Çıkış
   - RFID okuma
   - Miktar girişi
   - Barkod yazdırma
   - Offline çalışma

✅ Envanter Sayımı
   - Toplu RFID okuma (84+ etiket)
   - Liste oluşturma
   - Eksik/Fazla tespit
   - Rapor gönderme

✅ Ürün Arama
   - RFID ile arama
   - Konum gösterme
   - Hızlı bulma

✅ Stok Sorgulama
   - Anlık stok görüntüleme
   - Konum bilgisi
   - Min stok uyarıları

✅ Barkod Yazdırma
   - Zebra yazıcı entegrasyonu
   - RFID etiket yazdırma
   - Toplu yazdırma
```

### Yapamayacaklar (Sadece Web'den):
```
❌ Konum Değiştirme
   → Web Portal'dan yapılır
   → Daha kontrollü
   → Onay mekanizması

❌ Ürün Ekleme/Silme
   → Web Portal'dan yapılır
   → Yetki kontrolü
   → Daha güvenli

❌ Kullanıcı Yönetimi
   → Web Portal'dan yapılır
   → Admin yetkisi gerekir

❌ Sistem Ayarları
   → Web Portal'dan yapılır
   → Merkezi yönetim

❌ Detaylı Raporlar
   → Web Portal'dan yapılır
   → Grafikler, analizler
   → PDF/Excel export
```

---

## 🔌 API ENTEGRASYONU

### REST API Endpoints:

#### 1. Ürün İşlemleri:
```
GET    /api/products              - Tüm ürünler
GET    /api/products/{code}       - Tek ürün
POST   /api/products              - Yeni ürün
PUT    /api/products/{code}       - Ürün güncelle
DELETE /api/products/{code}       - Ürün sil
```

#### 2. Konum İşlemleri:
```
GET    /api/locations             - Tüm konumlar
GET    /api/locations/{id}        - Tek konum
POST   /api/locations             - Yeni konum
PUT    /api/locations/{id}        - Konum güncelle
POST   /api/products/{code}/move  - Ürün taşı
```

#### 3. Stok İşlemleri:
```
POST   /api/stock/in              - Stok girişi
POST   /api/stock/out             - Stok çıkışı
GET    /api/stock/movements       - Stok hareketleri
POST   /api/stock/inventory       - Envanter sayımı
```

#### 4. Senkronizasyon:
```
GET    /api/sync/products         - Ürünleri senkronize et
GET    /api/sync/locations        - Konumları senkronize et
POST   /api/sync/upload           - Offline verileri yükle
```

### RFID Cihaz - API Kullanımı:
```kotlin
// Ürün bilgisi çek
val product = apiService.getProduct("AFP002460")

// Stok girişi yap
val stockIn = StockInRequest(
    productCode = "AFP002460",
    quantity = 50,
    location = "A-3-Orta",
    rfidTag = "E200123456789ABC",
    user = "Ahmet Yılmaz"
)
apiService.stockIn(stockIn)

// Envanter gönder
val inventory = InventoryRequest(
    location = "A-3-Orta",
    tags = listOf("E200...", "E200...", ...),
    user = "Ahmet Yılmaz"
)
apiService.uploadInventory(inventory)
```

---

## 🎯 ÖNERİLEN ÇALIŞMA MODELİ

### Günlük İş Akışı:

#### SABAH (08:00):
```
WEB PORTAL (Ofis):
├─ SQL Mikro'dan senkronizasyon
├─ Yeni ürünler kontrol
├─ Konum değişiklikleri planla
└─ Günlük hedefler belirle

RFID CİHAZ (Depo):
├─ Giriş yap
├─ Senkronizasyon yap
├─ Güncel verileri al
└─ İşe başla
```

#### GÜNDÜZ (09:00-17:00):
```
RFID CİHAZ (Depo):
├─ Ürün giriş/çıkış işlemleri
├─ RFID okuma
├─ Barkod yazdırma
├─ Envanter sayımı
└─ Offline çalışma (internet yoksa)

WEB PORTAL (Ofis):
├─ Gerçek zamanlı takip
├─ Konum değişiklikleri
├─ Raporlar
└─ Analiz
```

#### AKŞAM (17:00):
```
RFID CİHAZ (Depo):
├─ Son senkronizasyon
├─ Offline verileri yükle
├─ Günlük rapor gönder
└─ Çıkış yap

WEB PORTAL (Ofis):
├─ Günlük rapor al
├─ SQL Mikro'ya senkronize et
├─ Yarın için plan
└─ Sistem yedekle
```

---

## 💻 TEKNİK DETAYLAR

### Veritabanı Yapısı:

#### RFID Cihaz (SQLite):
```sql
-- Yerel cache
CREATE TABLE products (
    id INTEGER PRIMARY KEY,
    product_code TEXT UNIQUE,
    name TEXT,
    quantity INTEGER,
    location TEXT,
    rfid_tag TEXT,
    last_sync TIMESTAMP
);

-- Offline işlemler
CREATE TABLE pending_operations (
    id INTEGER PRIMARY KEY,
    operation_type TEXT,
    data TEXT,
    created_at TIMESTAMP,
    synced INTEGER DEFAULT 0
);
```

#### Web Portal (PostgreSQL/MySQL):
```sql
-- Ana ürün tablosu
CREATE TABLE products (
    id BIGINT PRIMARY KEY,
    product_code VARCHAR(50) UNIQUE,
    name VARCHAR(255),
    quantity INTEGER,
    location_id BIGINT,
    rfid_tag VARCHAR(100),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- Konum tablosu
CREATE TABLE locations (
    id BIGINT PRIMARY KEY,
    corridor VARCHAR(10),
    shelf VARCHAR(10),
    level VARCHAR(20),
    capacity INTEGER,
    current_usage INTEGER,
    status VARCHAR(20)
);

-- Stok hareketleri
CREATE TABLE stock_movements (
    id BIGINT PRIMARY KEY,
    product_id BIGINT,
    movement_type VARCHAR(20),
    quantity INTEGER,
    from_location_id BIGINT,
    to_location_id BIGINT,
    user_id BIGINT,
    terminal_id VARCHAR(50),
    created_at TIMESTAMP
);
```

---

## 🚀 UYGULAMA PLANI

### Faz 1: RFID Cihaz (Mevcut) ✅
```
✅ Ürün giriş/çıkış
✅ RFID okuma
✅ Barkod yazdırma
✅ Offline çalışma
✅ Yerel veritabanı
```

### Faz 2: API Geliştirme (Öncelik) 🔄
```
📋 REST API oluştur
📋 Endpoints tanımla
📋 Authentication ekle
📋 RFID cihaz entegrasyonu
📋 Test et
```

### Faz 3: Web Portal (Sonraki) 📅
```
📋 Dashboard
📋 Ürün yönetimi
📋 Konum yönetimi
📋 Kullanıcı yönetimi
📋 Raporlar
📋 Ayarlar
```

### Faz 4: SQL Mikro Entegrasyonu ✅
```
✅ Bağlantı kuruldu
✅ Senkronizasyon hazır
✅ Ürün kodları entegre
```

---

## 📊 SONUÇ VE ÖNERİLER

### Önerilen Mimari:
```
✅ RFID Cihaz: Saha operasyonları
✅ Web Portal: Merkezi yönetim
✅ API: Köprü (RFID ↔ Web)
✅ SQL Mikro: Ana ERP
```

### Avantajlar:
```
✅ Kolay yönetim (Web'den)
✅ Hızlı operasyon (RFID'den)
✅ Merkezi kontrol
✅ Offline çalışma
✅ Gerçek zamanlı senkronizasyon
✅ Detaylı raporlama
✅ Güvenli (Rol bazlı)
```

### Konum Yönetimi:
```
✅ Web Portal'dan yapılır
✅ Daha kontrollü
✅ Onay mekanizması
✅ Geçmiş takibi
✅ RFID cihaz sadece görüntüler
```

### Sonraki Adımlar:
```
1. API geliştirmeye başla
2. RFID cihaz - API entegrasyonu
3. Web Portal geliştir
4. Test et
5. Canlıya al
```

---

**Bu mimari tam olarak ihtiyacınıza uygun!** 🎯✅🚀
