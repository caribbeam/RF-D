# 📱 Android Studio Test Rehberi

## 🎯 AMAÇ
Android RFID Depo Yönetim Sistemi'ni Android Studio'da test etmek ve doğrulamak.

---

## 📋 ÖN HAZIRLIK

### 1. Gereksinimler
```
✅ Android Studio (Arctic Fox veya üzeri)
✅ JDK 11 veya üzeri
✅ Android SDK (API 24+)
✅ Emülatör veya Chainway C5 cihazı
✅ İnternet bağlantısı (Gradle sync için)
```

### 2. Projeyi Açma
```
1. Android Studio'yu başlat
2. File → Open
3. c:/Users/site/Desktop/rd klasörünü seç
4. Open'a tıkla
5. Gradle Sync başlayacak (5-10 dakika sürebilir)
```

---

## 🔧 GRADLE SYNC

### Beklenen Çıktı:
```
BUILD SUCCESSFUL in 2m 15s
```

### Olası Hatalar ve Çözümleri:

#### Hata 1: "SDK location not found"
```
Çözüm:
1. File → Project Structure
2. SDK Location → Android SDK location seç
3. Apply → OK
```

#### Hata 2: "Dependency resolution failed"
```
Çözüm:
1. File → Invalidate Caches / Restart
2. Invalidate and Restart
3. Gradle Sync tekrar çalışacak
```

#### Hata 3: "Kotlin version mismatch"
```
Çözüm:
build.gradle (Project) dosyasında:
kotlin_version = "1.9.0" (veya en son sürüm)
```

---

## 🧪 TEST ADIMLARI

### ADIM 1: Derleme Testi (5 dakika)

#### 1.1. Clean Build
```
Build → Clean Project
Beklenen: "BUILD SUCCESSFUL"
```

#### 1.2. Rebuild
```
Build → Rebuild Project
Beklenen: "BUILD SUCCESSFUL in Xm Ys"
```

#### 1.3. APK Oluştur
```
Build → Build Bundle(s) / APK(s) → Build APK(s)
Beklenen: "APK(s) generated successfully"
Konum: app/build/outputs/apk/debug/app-debug.apk
```

**✅ BAŞARILI İSE:** Derleme testi geçti, devam et
**❌ BAŞARISIZ İSE:** Hata mesajını not al, düzelt

---

### ADIM 2: Emülatör/Cihaz Testi (10 dakika)

#### 2.1. Emülatör Başlat
```
Tools → Device Manager → Create Device
Önerilen: Pixel 5, API 30 (Android 11)
```

#### 2.2. Uygulamayı Çalıştır
```
Run → Run 'app' (veya Shift+F10)
Beklenen: Uygulama emülatörde açılacak
```

#### 2.3. İlk Ekran Kontrolü
```
✅ LoginActivity açıldı mı?
✅ Kullanıcı adı/şifre alanları var mı?
✅ Giriş butonu çalışıyor mu?
```

**Test Kullanıcısı:**
```
Kullanıcı Adı: admin
Şifre: admin123
```

---

### ADIM 3: Ekran Testleri (15 dakika)

#### 3.1. MainActivity
```
✅ Ana menü kartları görünüyor mu?
✅ 6 kart var mı? (Ürün Girişi, RFID Okuma, vb.)
✅ Kartlara tıklanabiliyor mu?
```

#### 3.2. ProductEntryActivity
```
Test Adımları:
1. "Ürün Girişi" kartına tıkla
2. Ürün kodu gir: TEST-001
3. Ürün adı gir: Test Ürünü
4. Miktar: 10
5. Birim dropdown'u aç → "Adet" seç
6. Koridor dropdown'u aç → "A" seç
7. Raf dropdown'u aç → "1" seç
8. Seviye dropdown'u aç → "Üst" seç
9. "Kaydet" butonuna tıkla

Beklenen:
✅ Toast mesajı: "Yeni ürün kaydedildi!"
✅ Form temizlendi
```

#### 3.3. ProductListActivity
```
Test Adımları:
1. Geri dön (Ana menü)
2. "Ürün Listesi" kartına tıkla
3. TEST-001 ürünü listede görünüyor mu?
4. Arama ikonuna tıkla
5. "TEST" yaz
6. Filtreleme: "Stokta" chip'ine tıkla
7. Menu → Sırala → "İsme Göre"

Beklenen:
✅ Ürün listede görünüyor
✅ Arama çalışıyor
✅ Filtreleme çalışıyor
✅ Sıralama çalışıyor
```

#### 3.4. RFIDScanActivity
```
Test Adımları:
1. Geri dön (Ana menü)
2. "RFID Okuma" kartına tıkla
3. "Okumaya Başla" butonuna tıkla
4. Demo modda etiketler okunuyor mu?
5. Liste doldu mu?
6. Bir etikete tıkla
7. Dialog açıldı mı?
8. "TEST-001" yaz
9. "Eşleştir" butonuna tıkla

Beklenen:
✅ RFID okuma başladı
✅ Etiketler listede görünüyor
✅ Eşleştirme dialog'u açıldı
✅ Toast: "Tag eşleştirildi!"
```

#### 3.5. InventoryActivity
```
Test Adımları:
1. Geri dön (Ana menü)
2. "Envanter Sayımı" kartına tıkla
3. Koridor: "Tümü" seç
4. "Sayımı Başlat" butonuna tıkla
5. 10 saniye bekle
6. "Sayımı Durdur" butonuna tıkla
7. "Rapor Oluştur" butonuna tıkla

Beklenen:
✅ Sayım başladı
✅ Süre sayacı çalışıyor
✅ Okunan etiket sayısı artıyor
✅ Rapor dialog'u açıldı
✅ İstatistikler doğru
```

---

### ADIM 4: Veritabanı Testi (5 dakika)

#### 4.1. Database Inspector
```
View → Tool Windows → App Inspection
Database Inspector sekmesi
```

#### 4.2. Tabloları Kontrol Et
```
✅ products tablosu var mı?
✅ TEST-001 ürünü kayıtlı mı?
✅ stock_movements tablosu var mı?
✅ rfid_reads tablosu var mı?
✅ users tablosu var mı?
✅ admin kullanıcısı var mı?
```

#### 4.3. Sorgu Testi
```sql
-- Tüm ürünleri getir
SELECT * FROM products;

-- RFID'li ürünler
SELECT * FROM products WHERE rfid_tag IS NOT NULL;

-- Düşük stoklu ürünler
SELECT * FROM products WHERE quantity <= min_stock_level;

-- Stok hareketleri
SELECT * FROM stock_movements ORDER BY timestamp DESC LIMIT 10;
```

---

### ADIM 5: Performans Testi (5 dakika)

#### 5.1. RFID Okuma Hızı
```
Test:
1. RFIDScanActivity aç
2. Okumaya başla
3. 10 saniye bekle
4. Kaç etiket okundu?

Beklenen:
✅ 100+ etiket (Demo modda ~100 etiket/10 saniye)
✅ UI donması yok
✅ Smooth scroll
```

#### 5.2. Liste Performansı
```
Test:
1. ProductListActivity aç
2. 100+ ürün varsa scroll yap
3. Arama yap
4. Filtreleme yap

Beklenen:
✅ Smooth scroll
✅ Hızlı arama (<1 saniye)
✅ Hızlı filtreleme (<1 saniye)
```

---

## ✅ TEST SONUÇLARI

### Başarı Kriterleri:
```
✅ Derleme başarılı
✅ Uygulama açılıyor
✅ Login çalışıyor
✅ Tüm ekranlar açılıyor
✅ Ürün ekleme çalışıyor
✅ RFID okuma çalışıyor
✅ Veritabanı çalışıyor
✅ Performans iyi
```

### Başarı Oranı:
```
8/8 = %100 ✅ Mükemmel!
6-7/8 = %75-87 ✅ İyi
4-5/8 = %50-62 🔄 Düzeltme gerekli
<4/8 = <%50 ❌ Ciddi sorunlar var
```

---

## 🐛 SORUN GİDERME

### Sorun 1: "Unresolved reference: R"
```
Çözüm:
1. Build → Clean Project
2. Build → Rebuild Project
3. File → Invalidate Caches / Restart
```

### Sorun 2: "Cannot resolve symbol 'MaterialButton'"
```
Çözüm:
build.gradle (app) kontrol et:
implementation 'com.google.android.material:material:1.9.0'
```

### Sorun 3: "Room database migration failed"
```
Çözüm:
1. Uygulamayı kaldır
2. Tekrar yükle
3. Veya: .fallbackToDestructiveMigration() kullan
```

### Sorun 4: "RFID okuma çalışmıyor"
```
Normal:
Demo modda çalışıyor (Simülasyon)
Gerçek cihazda Chainway SDK gerekli
```

---

## 📊 TEST RAPORU ŞABLONU

```markdown
# Test Raporu

**Tarih:** [Tarih]
**Test Eden:** [İsim]
**Cihaz:** [Emülatör/Gerçek Cihaz]

## Sonuçlar:

### Derleme: ✅/❌
- Clean Build: ✅/❌
- Rebuild: ✅/❌
- APK Oluşturma: ✅/❌

### Ekranlar: ✅/❌
- LoginActivity: ✅/❌
- MainActivity: ✅/❌
- ProductEntryActivity: ✅/❌
- ProductListActivity: ✅/❌
- RFIDScanActivity: ✅/❌
- InventoryActivity: ✅/❌

### Özellikler: ✅/❌
- Ürün ekleme: ✅/❌
- Arama: ✅/❌
- Filtreleme: ✅/❌
- RFID okuma: ✅/❌
- RFID eşleştirme: ✅/❌
- Envanter sayım: ✅/❌

### Performans: ✅/❌
- RFID hızı: ✅/❌
- Liste scroll: ✅/❌
- Arama hızı: ✅/❌

## Genel Değerlendirme:
[Yorumunuz]

## Bulunan Hatalar:
1. [Hata 1]
2. [Hata 2]
...

## Öneriler:
1. [Öneri 1]
2. [Öneri 2]
...
```

---

## 🎯 SONUÇ

Test tamamlandıktan sonra:

1. **Başarılı ise:** APK'yı Chainway C5'e yükle, gerçek test yap
2. **Sorun varsa:** Hataları düzelt, tekrar test et
3. **Rapor oluştur:** Yukarıdaki şablonu kullan

**İyi testler!** 🚀
