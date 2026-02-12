# 📱 Basit Kullanım Rehberi - Adım Adım

## 🎯 ŞU AN NELER YAPABİLİRSİNİZ?

### ✅ ÇALIŞAN ÖZELLİKLER (VPN'siz)

```
1. ✅ Giriş yapma (admin/admin123)
2. ✅ Manuel ürün ekleme (YAPTIĞINIZ)
3. ✅ Ürün listesini görme
4. ✅ Ürün arama
5. ✅ RFID okuma (demo mod)
6. ✅ Envanter sayım
7. ✅ Konum yönetimi
```

### ❌ VPN GEREKTİREN ÖZELLİKLER

```
❌ SQL Mikro'dan ürün çekme
❌ SQL Mikro'ya ürün gönderme
❌ Otomatik senkronizasyon
```

---

## 📱 EKRAN EKRAN KULLANIM

### 1️⃣ ANA EKRAN (İlk Açılış)

```
┌─────────────────────────┐
│   Depo RFID Yönetim     │
├─────────────────────────┤
│  📦 Ürün Girişi         │  ← Ürün ekle
│  📋 Ürün Listesi        │  ← Ürünleri gör
│  📡 RFID Okuma          │  ← RFID tara
│  📊 Envanter Sayım      │  ← Sayım yap
│  🖨️  Barkod Yazdır      │  ← Etiket bas
│  ⚙️  Ayarlar            │  ← SQL Mikro
└─────────────────────────┘
```

---

### 2️⃣ ÜRÜN GİRİŞİ (YAPTIĞINIZ)

```
┌─────────────────────────┐
│   Ürün Girişi           │
├─────────────────────────┤
│ Ürün Kodu: TEST001      │
│ Ürün Adı: Test Ürünü    │
│ Miktar: 10              │
│ Birim: Adet ▼           │
│ Konum:                  │
│  Koridor: A             │
│  Raf: 1                 │
│  Seviye: 2              │
│                         │
│ [KAYDET]                │
└─────────────────────────┘
```

**Ne Yapabilirsiniz:**
- ✅ Ürün kodu girin (örn: TEST001)
- ✅ Ürün adı girin
- ✅ Miktar girin
- ✅ Birim seçin (Adet, Koli, Palet, Kg, Ton, Litre, M3)
- ✅ Konum girin (Koridor-Raf-Seviye)
- ✅ Kaydet butonuna tıklayın

---

### 3️⃣ ÜRÜN LİSTESİ

```
┌─────────────────────────┐
│   Ürün Listesi          │
│ [🔍 Ara...]      [⚙️]   │
├─────────────────────────┤
│ TEST001                 │
│ Test Ürünü              │
│ 10 Adet | A-1-2         │
├─────────────────────────┤
│ TEST002                 │
│ Başka Ürün              │
│ 5 Koli | B-2-1          │
└─────────────────────────┘
```

**Ne Yapabilirsiniz:**
- ✅ Tüm ürünleri görün
- ✅ Arama yapın (ürün kodu veya adı)
- ✅ Ürüne tıklayın → Detayları görün
- ✅ Ürünü düzenleyin
- ✅ Ürünü silin

---

### 4️⃣ RFID OKUMA (Demo Mod)

```
┌─────────────────────────┐
│   RFID Okuma            │
│ Durum: Hazır            │
├─────────────────────────┤
│ [TARAMAYI BAŞLAT]       │
│                         │
│ Okunan Etiketler: 0     │
│                         │
│ (Boş liste)             │
│                         │
│ [DURDUR] [TEMİZLE]      │
└─────────────────────────┘
```

**Demo Modda:**
- ✅ "Taramayı Başlat" butonuna tıklayın
- ✅ Demo etiketler her 100ms'de görünür
- ✅ Etiket formatı: E200XXXXXXXXXXXX
- ✅ RSSI (sinyal gücü) gösterilir
- ✅ Okuma sayısı gösterilir

**Gerçek RFID Okuyucu ile:**
- ⏳ Chainway C5 cihazı gerekli
- ⏳ SDK entegrasyonu yapılacak
- ⏳ Gerçek etiketler okunacak

---

### 5️⃣ ENVANTER SAYIM

```
┌─────────────────────────┐
│   Envanter Sayım        │
├─────────────────────────┤
│ Konum Seç:              │
│  Koridor: A ▼           │
│  Raf: 1 ▼               │
│  Seviye: 2 ▼            │
│                         │
│ [SAYIMI BAŞLAT]         │
│                         │
│ Sayılan Ürünler: 0      │
│                         │
│ [RAPOR OLUŞTUR]         │
└─────────────────────────┘
```

**Ne Yapabilirsiniz:**
- ✅ Konum seçin
- ✅ Sayımı başlatın
- ✅ RFID ile ürünleri tarayın
- ✅ Rapor oluşturun
- ✅ Fark analizi yapın

---

### 6️⃣ AYARLAR (SQL Mikro)

```
┌─────────────────────────┐
│   Ayarlar               │
├─────────────────────────┤
│ SQL Mikro Bağlantısı    │
│                         │
│ Sunucu: 192.168.1.100   │
│ Port: 1433              │
│ Veritabanı: OZTUZUN     │
│ Kullanıcı: sa           │
│ Şifre: ****             │
│                         │
│ [BAĞLANTIYI TEST ET]    │
│ [ÜRÜNLERİ SENKRONIZE]   │
└─────────────────────────┘
```

**SQL Mikro İçin:**
- ⚠️ VPN bağlantısı gerekli
- ⚠️ Şu an VPN'siz çalışmaz
- ✅ Manuel test yapabilirsiniz

---

## 🔌 SQL MİKRO BAĞLANTISI (VPN GEREKLİ)

### Şu An Durum:
```
❌ VPN yok → SQL Mikro'ya bağlanamaz
✅ Manuel ürün girişi → Çalışır
✅ Yerel veritabanı → Çalışır
✅ Tüm özellikler → Çalışır (SQL Mikro hariç)
```

### SQL Mikro'ya Bağlanmak İçin:

#### YÖNTEM 1: USB Tethering (ÖNERİLEN)
```
1. Bilgisayardan FortiVPN'e bağlan
2. Telefonu USB ile bağla
3. Telefon Ayarları > Ağ > USB Tethering > AÇ
4. Telefon bilgisayarın VPN'ini kullanır
5. Uygulamada: Ayarlar > SQL Mikro > Bağlantıyı Test Et
6. Başarılı! → Ürünleri Senkronize Et
```

#### YÖNTEM 2: FortiClient VPN (Telefonda)
```
1. Play Store > "FortiClient VPN" indir
2. VPN profilini yapılandır
3. VPN'e bağlan
4. Uygulamada: Ayarlar > SQL Mikro > Bağlantıyı Test Et
5. Başarılı! → Ürünleri Senkronize Et
```

#### YÖNTEM 3: VPN'siz Test (Şu An)
```
✅ Manuel ürün girişi yap
✅ Tüm özellikleri test et
✅ RFID okuma (demo mod)
✅ Envanter sayım
✅ Konum yönetimi
❌ SQL Mikro senkronizasyonu yapma
```

---

## 📋 TEST SENARYOLARI

### ✅ SENARYO 1: Manuel Ürün Ekleme (YAPTIĞINIZ)

```
1. Ana Ekran > Ürün Girişi
2. Ürün Kodu: TEST001
3. Ürün Adı: Test Ürünü
4. Miktar: 10
5. Birim: Adet
6. Konum: A-1-2
7. Kaydet
8. ✅ Başarılı!
```

### ✅ SENARYO 2: Ürün Listesi

```
1. Ana Ekran > Ürün Listesi
2. Eklediğiniz ürünü görün
3. Arama yapın: "TEST"
4. Ürüne tıklayın
5. Detayları görün
6. ✅ Başarılı!
```

### ✅ SENARYO 3: RFID Okuma (Demo)

```
1. Ana Ekran > RFID Okuma
2. Taramayı Başlat
3. Demo etiketler görünür
4. Her 100ms'de yeni etiket
5. Durdur
6. ✅ Başarılı!
```

### ✅ SENARYO 4: Envanter Sayım

```
1. Ana Ekran > Envanter Sayım
2. Konum seç: A-1-2
3. Sayımı Başlat
4. RFID ile tara (demo)
5. Rapor Oluştur
6. ✅ Başarılı!
```

### ⏳ SENARYO 5: SQL Mikro (VPN Gerekli)

```
1. VPN'e bağlan (USB Tethering veya FortiClient)
2. Ana Ekran > Ayarlar
3. SQL Mikro Bağlantısı
4. Bağlantıyı Test Et
5. ✅ Başarılı → Ürünleri Senkronize Et
6. SQL Mikro'dan ürünler gelir
```

---

## 🎯 ŞİMDİ NE YAPABİLİRSİNİZ?

### VPN'siz (Şu An):

```
✅ 1. Daha fazla ürün ekleyin
   - Farklı ürün kodları
   - Farklı birimler
   - Farklı konumlar

✅ 2. Ürün listesini test edin
   - Arama yapın
   - Filtreleme yapın
   - Ürün düzenleyin

✅ 3. RFID okuma test edin
   - Demo modda tarama yapın
   - Etiketleri görün
   - Okuma hızını test edin

✅ 4. Envanter sayım yapın
   - Konum seçin
   - Sayım başlatın
   - Rapor oluşturun

✅ 5. Konum yönetimi test edin
   - Farklı koridorlar
   - Farklı raflar
   - Farklı seviyeler
```

### VPN ile (Sonra):

```
⏳ 1. SQL Mikro'ya bağlanın
   - USB Tethering veya FortiClient
   - Bağlantıyı test edin

⏳ 2. Ürünleri senkronize edin
   - SQL Mikro'dan ürünler gelir
   - AFP002460 formatında
   - Otomatik eşleşme

⏳ 3. Yeni ürünleri SQL Mikro'ya gönderin
   - RFID etiketli ürünler
   - Otomatik senkronizasyon
```

---

## 💡 ÖNEMLİ NOTLAR

### Şu An:
```
✅ Uygulama telefonda çalışıyor
✅ Manuel ürün girişi yapabiliyorsunuz
✅ Tüm özellikler çalışıyor (SQL Mikro hariç)
✅ Demo modda test yapabilirsiniz
❌ SQL Mikro için VPN gerekli
```

### SQL Mikro İçin:
```
⚠️ VPN bağlantısı şart
⚠️ USB Tethering en kolay yöntem
⚠️ FortiClient VPN alternatif
⚠️ VPN'siz SQL Mikro çalışmaz
```

### RFID Okuyucu İçin:
```
⚠️ Şu an demo modda çalışıyor
⚠️ Gerçek Chainway C5 cihazı gerekli
⚠️ SDK entegrasyonu yapılacak
⚠️ Demo mod test için yeterli
```

---

## 🚀 SONRAKI ADIMLAR

### 1. VPN'siz Test (Şimdi):
```
✅ Daha fazla ürün ekleyin
✅ Tüm ekranları test edin
✅ RFID okuma deneyin (demo)
✅ Envanter sayım yapın
✅ Hataları not edin
```

### 2. VPN ile Test (Sonra):
```
⏳ USB Tethering kurun
⏳ SQL Mikro'ya bağlanın
⏳ Ürünleri senkronize edin
⏳ Gerçek verileri test edin
```

### 3. Chainway C5 ile Test (En Son):
```
⏳ Chainway C5 cihazı alın
⏳ SDK entegrasyonu yapın
⏳ Gerçek RFID etiketleri okuyun
⏳ Tam sistem testi yapın
```

---

## 📞 YARDIM

### Sorularınız:
```
❓ "Diğer işleri anlayamadım"
   → Bu rehberde her ekran anlatıldı

❓ "SQL Mikro'dan veriler nasıl gelecek?"
   → VPN + Ayarlar > Ürünleri Senkronize Et

❓ "Şu an manuel test mi yapacağız?"
   → Evet! VPN'siz tüm özellikler çalışır
```

---

## 🎉 ÖZET

### Şu An Yapabilecekleriniz:
```
✅ Manuel ürün ekleme (YAPTIĞINIZ)
✅ Ürün listesi görme
✅ Ürün arama/düzenleme
✅ RFID okuma (demo)
✅ Envanter sayım
✅ Konum yönetimi
✅ Tüm özellikleri test etme
```

### SQL Mikro İçin:
```
⏳ VPN bağlantısı kurun
⏳ USB Tethering (en kolay)
⏳ Ayarlar > SQL Mikro > Senkronize Et
```

**Şimdilik VPN'siz tüm özellikleri test edebilirsiniz!** ✅

**Başarılar!** 🚀📱✨
