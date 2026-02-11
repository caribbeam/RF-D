# 📦 Birim Yönetimi Sistemi

## 🎯 GENEL BAKIŞ

Ürünler farklı birimlerde gelebilir: Adet, Koli, Palet, Kg, Ton, Litre, Metre vb.
Sistem tüm birimleri destekler ve otomatik hesaplama yapar.

---

## 📊 DESTEKLENEN BİRİMLER

### Varsayılan Birimler:
```
✅ Adet    - Tekli ürünler
✅ Koli    - Koliler
✅ Palet   - Paletler
✅ Kg      - Kilogram
✅ Ton     - Ton
✅ Litre   - Litre
✅ Metre   - Metre
✅ M²      - Metrekare
✅ M³      - Metreküp
```

### Özel Birimler:
```
✅ Kutu
✅ Paket
✅ Sandık
✅ Rulo
✅ Takım
✅ Set
✅ Çift
```

---

## 💡 KULLANIM ÖRNEKLERİ

### Örnek 1: Laptop (Adet)
```
Ürün Kodu: AFP002460
Ürün Adı: Laptop Dell XPS 15
Birim: Adet
Miktar: 50 adet

Giriş:
├─ 50 adet laptop geldi
├─ Her birine RFID etiketi yapıştırıldı
└─ Toplam: 50 adet
```

### Örnek 2: Laptop (Koli)
```
Ürün Kodu: AFP002460
Ürün Adı: Laptop Dell XPS 15
Birim: Koli
Miktar: 5 koli

Açıklama:
├─ 1 Koli = 10 adet laptop
├─ 5 Koli = 50 adet laptop
├─ Her koliye RFID etiketi yapıştırıldı
└─ Toplam: 5 koli (50 adet)

Stok Kartı:
├─ Birim: Koli
├─ Miktar: 5 koli
└─ Not: 1 koli = 10 adet
```

### Örnek 3: Laptop (Palet)
```
Ürün Kodu: AFP002460
Ürün Adı: Laptop Dell XPS 15
Birim: Palet
Miktar: 2 palet

Açıklama:
├─ 1 Palet = 10 koli = 100 adet laptop
├─ 2 Palet = 20 koli = 200 adet laptop
├─ Her palete RFID etiketi yapıştırıldı
└─ Toplam: 2 palet (200 adet)

Stok Kartı:
├─ Birim: Palet
├─ Miktar: 2 palet
└─ Not: 1 palet = 10 koli = 100 adet
```

### Örnek 4: Kablo (Metre)
```
Ürün Kodu: AFP003500
Ürün Adı: Ethernet Kablosu Cat6
Birim: Metre
Miktar: 500 metre

Giriş:
├─ 500 metre kablo geldi
├─ Ruloya RFID etiketi yapıştırıldı
└─ Toplam: 500 metre
```

### Örnek 5: Boya (Litre)
```
Ürün Kodu: AFP004200
Ürün Adı: Duvar Boyası Beyaz
Birim: Litre
Miktar: 100 litre

Giriş:
├─ 100 litre boya geldi (20 teneke x 5 litre)
├─ Her tenekeye RFID etiketi yapıştırıldı
└─ Toplam: 100 litre
```

---

## 🔄 BİRİM DÖNÜŞÜMÜ

### Senaryo: Farklı Birimlerle Giriş/Çıkış

#### Durum 1: Adet ile Giriş, Koli ile Çıkış
```
İLK GİRİŞ (Adet):
├─ Ürün: AFP002460 - Laptop
├─ Birim: Adet
├─ Miktar: 100 adet
└─ Stok: 100 adet

ÇIKIŞ (Koli):
├─ Müşteri: 5 koli istedi
├─ 1 Koli = 10 adet
├─ 5 Koli = 50 adet
├─ Çıkış: 50 adet
└─ Kalan: 50 adet

SİSTEMDE:
├─ Birim: Adet (değişmez)
├─ Miktar: 50 adet
└─ Not: "5 koli (50 adet) çıkış yapıldı"
```

#### Durum 2: Koli ile Giriş, Adet ile Çıkış
```
İLK GİRİŞ (Koli):
├─ Ürün: AFP002460 - Laptop
├─ Birim: Koli
├─ Miktar: 10 koli
└─ Stok: 10 koli (100 adet)

ÇIKIŞ (Adet):
├─ Müşteri: 25 adet istedi
├─ 25 adet = 2.5 koli
├─ Çıkış: 2.5 koli
└─ Kalan: 7.5 koli (75 adet)

SİSTEMDE:
├─ Birim: Koli (değişmez)
├─ Miktar: 7.5 koli
└─ Not: "25 adet (2.5 koli) çıkış yapıldı"
```

---

## 💻 SİSTEMDE NASIL ÇALIŞIR?

### 1. Ürün Girişi Ekranı

```
┌─────────────────────────────────────┐
│  Ürün Girişi                        │
├─────────────────────────────────────┤
│                                     │
│  Ürün Kodu:    [AFP002460        ] │
│  Ürün Adı:     [Laptop Dell XPS  ] │
│                                     │
│  Miktar:       [10               ] │
│  Birim:        [Koli          ▼ ] │
│                 ├─ Adet           │
│                 ├─ Koli  ✓        │
│                 ├─ Palet          │
│                 ├─ Kg             │
│                 └─ ...            │
│                                     │
│  Min. Stok:    [5                ] │
│                                     │
│  [RFID Oku]  [Kaydet]  [Yazdır]   │
│                                     │
└─────────────────────────────────────┘
```

### 2. Stok Kartı

```
┌─────────────────────────────────────┐
│  AFP002460 - Laptop Dell XPS 15     │
├─────────────────────────────────────┤
│                                     │
│  Mevcut Stok:  10 Koli              │
│  Birim:        Koli                 │
│  Min. Stok:    5 Koli               │
│  Durum:        ✅ Yeterli           │
│                                     │
│  Konum:        A-3-Orta             │
│  RFID:         E200123456789ABC     │
│                                     │
│  Açıklama:                          │
│  1 Koli = 10 Adet Laptop            │
│  Toplam: 10 Koli = 100 Adet         │
│                                     │
└─────────────────────────────────────┘
```

### 3. Stok Hareketi

```
┌─────────────────────────────────────┐
│  Stok Hareketleri                   │
│  AFP002460 - Laptop Dell XPS 15     │
├─────────────────────────────────────┤
│                                     │
│  15.01.2024 09:00 - Ahmet           │
│  GİRİŞ: +10 Koli                    │
│  Önceki: 0 Koli → Yeni: 10 Koli     │
│  Terminal: Terminal-001             │
│                                     │
│  15.01.2024 14:30 - Mehmet          │
│  ÇIKIŞ: -3 Koli                     │
│  Önceki: 10 Koli → Yeni: 7 Koli     │
│  Terminal: Terminal-002             │
│  Not: Müşteri siparişi              │
│                                     │
│  16.01.2024 08:15 - Ayşe            │
│  GİRİŞ: +5 Koli                     │
│  Önceki: 7 Koli → Yeni: 12 Koli     │
│  Terminal: Terminal-003             │
│                                     │
└─────────────────────────────────────┘
```

---

## 🏷️ BARKOD ETİKET YAZDIRMA

### Koli Etiketi:
```
┌─────────────────────────────────┐
│  AFP002460                      │
│  Laptop Dell XPS 15             │
│  ▐▐▐▐▐▐▐▐▐▐▐▐▐▐▐▐▐▐▐▐▐▐▐▐▐▐▐▐  │
│  AFP002460                      │
│                                 │
│  BİRİM: KOLİ                    │
│  İÇİNDEKİ: 10 ADET              │
│  KONUM: A-3-Orta                │
│  RFID: E200123456789ABC         │
│                                 │
│  TARİH: 15.01.2024              │
│  KOLİ NO: 1/10                  │
└─────────────────────────────────┘
```

### Palet Etiketi:
```
┌─────────────────────────────────┐
│  AFP002460                      │
│  Laptop Dell XPS 15             │
│  ▐▐▐▐▐▐▐▐▐▐▐▐▐▐▐▐▐▐▐▐▐▐▐▐▐▐▐▐  │
│  AFP002460                      │
│                                 │
│  BİRİM: PALET                   │
│  İÇİNDEKİ: 10 KOLİ = 100 ADET   │
│  KONUM: A-3-Orta                │
│  RFID: E200PALET123456          │
│                                 │
│  TARİH: 15.01.2024              │
│  PALET NO: 1/2                  │
│  AĞIRLIK: 150 KG                │
└─────────────────────────────────┘
```

---

## 📊 GERÇEK HAYAT ÖRNEKLERİ

### Örnek 1: Elektronik Mağazası
```
ÜRÜN: Laptop Dell XPS 15
KOD: AFP002460

TEDARIKÇIDEN GELDI:
├─ 2 Palet
├─ Her palet: 10 koli
├─ Her koli: 10 adet
└─ Toplam: 2 palet = 20 koli = 200 adet

SİSTEME GİRİŞ:
├─ Birim: Palet seçildi
├─ Miktar: 2 girildi
├─ Her palete RFID yapıştırıldı
└─ Kayıt: 2 palet

MÜŞTERIYE SATIŞ:
├─ Müşteri: 15 adet istedi
├─ Çıkış: 15 adet (1.5 koli, 0.15 palet)
├─ Kalan: 1.85 palet (18.5 koli, 185 adet)
└─ Sistem otomatik hesapladı
```

### Örnek 2: İnşaat Malzemesi
```
ÜRÜN: Çimento
KOD: AFP005000

TEDARIKÇIDEN GELDI:
├─ 1 Kamyon
├─ 20 Palet
├─ Her palet: 40 torba
├─ Her torba: 50 kg
└─ Toplam: 20 palet = 800 torba = 40 ton

SİSTEME GİRİŞ SEÇENEK 1 (Palet):
├─ Birim: Palet
├─ Miktar: 20
└─ Kayıt: 20 palet

SİSTEME GİRİŞ SEÇENEK 2 (Ton):
├─ Birim: Ton
├─ Miktar: 40
└─ Kayıt: 40 ton

MÜŞTERIYE SATIŞ:
├─ Müşteri: 5 ton istedi
├─ 5 ton = 100 torba = 2.5 palet
├─ Çıkış: 5 ton
├─ Kalan: 35 ton (17.5 palet)
└─ Sistem otomatik hesapladı
```

### Örnek 3: Gıda Deposu
```
ÜRÜN: Pirinç
KOD: AFP006500

TEDARIKÇIDEN GELDI:
├─ 10 Palet
├─ Her palet: 20 çuval
├─ Her çuval: 50 kg
└─ Toplam: 10 palet = 200 çuval = 10 ton

SİSTEME GİRİŞ:
├─ Birim: Ton
├─ Miktar: 10
└─ Kayıt: 10 ton

PERAKENDE SATIŞ:
├─ Müşteri 1: 5 kg
├─ Müşteri 2: 10 kg
├─ Müşteri 3: 25 kg
├─ Toplam çıkış: 40 kg = 0.04 ton
├─ Kalan: 9.96 ton
└─ Sistem otomatik hesapladı
```

---

## 🔧 TEKNİK DETAYLAR

### Veritabanında Saklama:
```kotlin
data class ProductEntity(
    val productCode: String,    // AFP002460
    val name: String,            // Laptop Dell XPS 15
    val quantity: Int,           // 10
    val unit: String,            // "Koli"
    val description: String?     // "1 Koli = 10 Adet"
)
```

### Birim Dropdown:
```kotlin
val units = arrayOf(
    "Adet",
    "Koli",
    "Palet",
    "Kg",
    "Ton",
    "Litre",
    "Metre",
    "M²",
    "M³",
    "Kutu",
    "Paket",
    "Sandık",
    "Rulo",
    "Takım",
    "Set",
    "Çift"
)

// Dropdown'da göster
val adapter = ArrayAdapter(context, android.R.layout.simple_dropdown_item_1line, units)
actvUnit.setAdapter(adapter)
```

### Stok Hareketi Kaydı:
```kotlin
val movement = StockMovementEntity(
    productId = product.id,
    productCode = "AFP002460",
    movementType = MovementType.IN,
    quantity = 10,              // 10 koli
    unit = "Koli",              // Birim bilgisi
    previousQuantity = 0,
    newQuantity = 10,
    user = "Ahmet Yılmaz",
    notes = "10 koli (100 adet) giriş yapıldı"
)
```

---

## 💡 ÖNERİLER

### 1. Açıklama Alanını Kullanın:
```
Ürün: AFP002460 - Laptop
Birim: Koli
Açıklama: "1 Koli = 10 Adet Laptop"

Bu sayede herkes ne kadar olduğunu bilir.
```

### 2. Tutarlı Birim Kullanın:
```
✅ DOĞRU:
- Tüm laptop girişleri: Koli
- Tüm çimento girişleri: Ton
- Tüm kablo girişleri: Metre

❌ YANLIŞ:
- Bazen Koli, bazen Adet
- Karışıklık olur
```

### 3. Dönüşüm Tablosu Oluşturun:
```
AFP002460 - Laptop:
├─ 1 Palet = 10 Koli = 100 Adet
├─ 1 Koli = 10 Adet
└─ 1 Adet = 1 Adet

AFP005000 - Çimento:
├─ 1 Palet = 40 Torba = 2 Ton
├─ 1 Torba = 50 Kg
└─ 1 Ton = 1000 Kg
```

---

## 🎯 SONUÇ

### Sistem Özellikleri:
```
✅ Tüm birimler destekleniyor
✅ Dropdown'dan seçim yapılıyor
✅ Her ürün kendi birimi ile kaydediliyor
✅ Stok hareketleri birim ile kaydediliyor
✅ Barkod etiketinde birim görünüyor
✅ Raporlarda birim görünüyor
✅ Açıklama alanında dönüşüm yazılabiliyor
```

### Kullanım:
```
1. Ürün geldiğinde birimini seç (Adet/Koli/Palet)
2. Miktarı gir
3. Açıklamaya dönüşümü yaz (opsiyonel)
4. RFID oku
5. Kaydet
6. Barkod yazdır
7. Yapıştır
✅ Hazır!
```

---

**Sistem tüm birimleri destekliyor!** 📦🎯✅
