# 📦 Depo RFID Yönetim Sistemi - Kullanım Rehberi

## 🎯 Sistem İş Akışı

### 1️⃣ YENİ ÜRÜN GELİŞİ (İlk Kayıt)

#### Adım 1: Ürün Bilgilerini Girin
```
Ana Ekran → "Ürün Girişi" Butonu
```

**Girilecek Bilgiler:**
- ✅ Ürün Adı (örn: "Laptop Dell XPS 15")
- ✅ Ürün Kodu (örn: "DELL-XPS-001")
- ✅ Miktar (örn: 10 adet)
- ✅ Birim (örn: Adet, Koli, Palet)
- ✅ **Konum Bilgileri:**
  - Koridor (örn: A, B, C)
  - Raf (örn: 1, 2, 3)
  - Seviye (örn: Üst, Orta, Alt)
- ✅ Açıklama (opsiyonel)

**Örnek:**
```
Ürün Adı: Laptop Dell XPS 15
Ürün Kodu: DELL-XPS-001
Miktar: 10
Birim: Adet
Koridor: A
Raf: 3
Seviye: Orta
Açıklama: Yeni model, 2024
```

#### Adım 2: RFID Etiketi Yazdırın
```
Ürün Girişi Ekranı → "Barkod Yazdır" Butonu
```

**Sistem Otomatik Yapar:**
1. ✅ Benzersiz RFID EPC kodu oluşturur
2. ✅ Ürün bilgilerini veritabanına kaydeder
3. ✅ Zebra yazıcıdan RFID etiket çıktısı alır

**Etiket İçeriği:**
```
┌─────────────────────────┐
│  DELL-XPS-001          │
│  Laptop Dell XPS 15    │
│  Konum: A-3-Orta       │
│  ▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓       │ (Barkod)
│  EPC: E200123456...    │
└─────────────────────────┘
```

#### Adım 3: Etiketi Ürüne/Palete Yapıştırın
- Etiketi koliye/palete yapıştırın
- Ürünü belirtilen konuma (A-3-Orta) yerleştirin

---

### 2️⃣ DEVAM EDEN ÜRÜN GELİŞİ (Aynı Ürün)

#### Senaryo: Aynı üründen 5 koli daha geldi

**Yöntem 1: Hızlı Arama (Önerilen)**
```
Ana Ekran → "Ürün Listesi" → Arama
```

1. Ürün kodunu girin: `DELL-XPS-001`
2. Sistem ürünü bulur ve gösterir
3. "Barkod Yazdır" butonuna basın
4. Zebra yazıcıdan 5 adet etiket çıkar
5. Etiketleri yeni kolilere yapıştırın

**Yöntem 2: RFID ile Arama**
```
Ana Ekran → "RFID Okuma" → Mevcut Etiketi Okut
```

1. Eski bir etiketi RFID okuyucu ile okutun
2. Sistem ürünü tanır ve gösterir
3. "Barkod Yazdır" → 5 adet etiket çıkar

**Yöntem 3: Manuel Ürün Girişi**
```
Ana Ekran → "Ürün Girişi"
```

1. Aynı bilgileri tekrar girin
2. Sistem aynı ürün kodunu tanır
3. Miktarı günceller (10 + 5 = 15 adet)
4. Yeni etiketler yazdırır

---

### 3️⃣ ENVANTER SAYIMI

#### Adım 1: Sayım Başlatın
```
Ana Ekran → "Envanter Sayımı" → "Sayıma Başla"
```

#### Adım 2: RFID ile Okuma Yapın
```
"RFID Okuma" Butonu → Tarama Başlat
```

**Sistem Otomatik Yapar:**
1. ✅ Tüm RFID etiketlerini okur (84 etiket/dakika)
2. ✅ Okunan ürünleri listeler
3. ✅ Her ürünün:
   - Adı
   - Kodu
   - Konumu (Koridor-Raf-Seviye)
   - Miktarı
   - Son okuma zamanı

**Örnek Sayım Sonucu:**
```
┌──────────────────────────────────────────┐
│ Envanter Sayımı - 15.01.2024 14:30     │
├──────────────────────────────────────────┤
│ ✅ DELL-XPS-001 | A-3-Orta | 15 adet   │
│ ✅ HP-PRO-002   | B-2-Üst  | 8 adet    │
│ ✅ LENOVO-003   | A-1-Alt  | 12 adet   │
│ ⚠️  ASUS-004    | C-4-Orta | 3 adet    │ (Düşük stok)
│ ❌ ACER-005     | -        | 0 adet    │ (Tükendi)
└──────────────────────────────────────────┘
```

#### Adım 3: Analiz Yapın
```
Sayım Sonucu Ekranı → "Rapor Görüntüle"
```

**Sistem Gösterir:**
- ✅ Toplam ürün sayısı
- ✅ Toplam stok miktarı
- ⚠️ Düşük stoklu ürünler
- ❌ Tükenen ürünler
- 📊 Konum bazlı dağılım
- 📈 Trend analizi

---

### 4️⃣ EKSİK/TÜKENME YÖNETİMİ

#### Senaryo 1: Ürün Tükendi
```
Envanter Sayımı → "ACER-005: 0 adet" → "Sipariş Ver"
```

**Sistem:**
1. Ürünü "Tükendi" olarak işaretler
2. Sipariş listesine ekler
3. Bildirim gönderir

#### Senaryo 2: Düşük Stok
```
Envanter Sayımı → "ASUS-004: 3 adet" → "Uyarı"
```

**Sistem:**
1. Düşük stok uyarısı verir
2. Minimum stok seviyesini gösterir
3. Sipariş önerisi sunar

#### Senaryo 3: Eksik Ürün Ekleme
```
Ana Ekran → "Ürün Girişi" → Eksik Ürünü Girin
```

**Örnek:**
```
Ürün Adı: Acer Aspire 5
Ürün Kodu: ACER-005
Miktar: 20 (yeni sipariş)
Koridor: C
Raf: 4
Seviye: Orta
```

Sistem:
1. Ürünü veritabanına ekler/günceller
2. 20 adet RFID etiket yazdırır
3. Stok durumunu günceller

---

### 5️⃣ ÜRÜN ARAMA VE BULMA

#### Yöntem 1: Kod ile Arama
```
Ana Ekran → "Ürün Listesi" → Arama Kutusu
```

1. Ürün kodunu girin: `DELL-XPS-001`
2. Sistem anında bulur
3. Tüm bilgileri gösterir:
   - Konum: A-3-Orta
   - Miktar: 15 adet
   - Son güncelleme
   - RFID etiket sayısı

#### Yöntem 2: RFID ile Arama
```
Ana Ekran → "RFID Okuma" → Etiketi Okut
```

1. Herhangi bir etiketi okutun
2. Sistem ürünü tanır
3. Detayları gösterir

#### Yöntem 3: Konum ile Arama
```
Ana Ekran → "Ürün Listesi" → Filtrele
```

1. Koridor seçin: A
2. Raf seçin: 3
3. Sistem o konumdaki tüm ürünleri listeler

---

### 6️⃣ BARKOD YAZDIRMA İŞLEMLERİ

#### Tek Ürün için Etiket
```
Ürün Listesi → Ürün Seç → "Barkod Yazdır"
```

**Ayarlar:**
- Adet: 1-100
- Etiket boyutu: Standart/Küçük/Büyük
- Yazıcı: Zebra ZD620

#### Toplu Etiket Yazdırma
```
Ürün Listesi → Çoklu Seçim → "Toplu Yazdır"
```

**Örnek:**
- DELL-XPS-001: 5 adet
- HP-PRO-002: 3 adet
- LENOVO-003: 10 adet
→ Toplam 18 etiket yazdırılır

---

## 🔄 GÜNLÜK İŞ AKIŞI ÖRNEĞİ

### Sabah (08:00)
```
1. Sisteme giriş yap
2. "Envanter Sayımı" → Hızlı sayım yap
3. Düşük stokları kontrol et
4. Sipariş listesini gözden geçir
```

### Ürün Gelişi (10:00)
```
1. Yeni ürün geldi mi?
   → EVET: "Ürün Girişi" → Bilgileri gir
   → HAYIR: "Ürün Listesi" → Ara → Bul
   
2. "Barkod Yazdır" → Adet belirt
3. Zebra yazıcıdan etiketleri al
4. Etiketleri ürünlere yapıştır
5. Ürünleri konumlarına yerleştir
```

### Öğle Arası (12:00)
```
1. "RFID Okuma" → Hızlı kontrol
2. Yanlış konumlanmış ürün var mı?
3. Gerekirse düzeltme yap
```

### Akşam (17:00)
```
1. "Envanter Sayımı" → Tam sayım
2. Rapor oluştur
3. Eksikleri tespit et
4. Yarın için sipariş listesi hazırla
```

---

## 📱 EKRAN AKIŞI

### Ana Ekran
```
┌─────────────────────────────┐
│   Depo RFID Yönetim         │
├─────────────────────────────┤
│  📦 Ürün Girişi             │
│  📡 RFID Okuma              │
│  📊 Envanter Sayımı         │
│  🖨️  Barkod Yazdır          │
│  📋 Ürün Listesi            │
│  ⚙️  Ayarlar                │
└─────────────────────────────┘
```

### Ürün Girişi Ekranı
```
┌─────────────────────────────┐
│   Yeni Ürün Girişi          │
├─────────────────────────────┤
│  Ürün Adı: [________]       │
│  Ürün Kodu: [________]      │
│  Miktar: [___] Adet ▼       │
│                             │
│  📍 Konum Bilgileri         │
│  Koridor: [A] ▼             │
│  Raf: [3] ▼                 │
│  Seviye: [Orta] ▼           │
│                             │
│  Açıklama: [________]       │
│                             │
│  [Kaydet] [Barkod Yazdır]  │
└─────────────────────────────┘
```

### RFID Okuma Ekranı
```
┌─────────────────────────────┐
│   RFID Okuma                │
├─────────────────────────────┤
│  Durum: 🟢 Taranıyor...     │
│  Bulunan: 84 etiket         │
│                             │
│  📦 DELL-XPS-001 (15)       │
│     Konum: A-3-Orta         │
│     RSSI: -45 dBm           │
│                             │
│  📦 HP-PRO-002 (8)          │
│     Konum: B-2-Üst          │
│     RSSI: -52 dBm           │
│                             │
│  [Durdur] [Temizle]         │
└─────────────────────────────┘
```

---

## 🎯 HIZLI İPUÇLARI

### ✅ Yapılması Gerekenler
1. **Her ürün gelişinde** hemen etiket yazdırın
2. **Günde 2 kez** envanter sayımı yapın
3. **Etiketleri** düz yüzeye yapıştırın
4. **Konumları** doğru girin (Koridor-Raf-Seviye)
5. **Düşük stokları** takip edin

### ❌ Yapılmaması Gerekenler
1. Etiketleri metal yüzeye yapıştırmayın
2. Aynı ürün kodunu farklı ürünlere vermeyin
3. Konum bilgilerini boş bırakmayın
4. Eski etiketleri atmayın (arşivleyin)
5. Sayım yapmadan sipariş vermeyin

---

## 🔧 SORUN GİDERME

### Sorun: "Ürün bulunamadı"
**Çözüm:**
1. Ürün kodunu kontrol edin
2. "Ürün Listesi" → Tüm ürünleri görüntüleyin
3. RFID ile okuma yapın
4. Gerekirse yeni kayıt oluşturun

### Sorun: "Etiket yazdırılamadı"
**Çözüm:**
1. Zebra yazıcı açık mı?
2. Bluetooth bağlantısı var mı?
3. Etiket kağıdı var mı?
4. Yazıcıyı yeniden başlatın

### Sorun: "RFID okuma yavaş"
**Çözüm:**
1. Cihazı yeniden başlatın
2. RFID antenini temizleyin
3. Etiketlere daha yakın okutun
4. Metal nesnelerden uzak durun

---

## 📞 DESTEK

**Teknik Destek:**
- Email: denetimsite@gmail.com
- GitHub: https://github.com/caribbeam/RF-D

**Acil Durum:**
1. Uygulamayı yeniden başlatın
2. Cihazı yeniden başlatın
3. Yedek cihaza geçin
4. Destek ekibini arayın

---

## 🎓 EĞİTİM VİDEOLARI

1. **Temel Kullanım** (10 dk)
   - Ürün girişi
   - Etiket yazdırma
   - RFID okuma

2. **Envanter Yönetimi** (15 dk)
   - Sayım yapma
   - Rapor oluşturma
   - Analiz

3. **İleri Seviye** (20 dk)
   - Toplu işlemler
   - Konum yönetimi
   - Optimizasyon

---

**Sistem Hazır! Kullanmaya Başlayabilirsiniz!** 🚀
