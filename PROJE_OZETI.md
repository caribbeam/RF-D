# 📦 Chainway C5 UHF Depo Yönetim Sistemi - Proje Özeti

## 🎯 Proje Amacı

Bu proje, **Chainway C5 UHF El Terminali** için geliştirilmiş profesyonel bir depo yönetim sistemidir. Depoya gelen ürünlerin RFID etiketleri ile takibini, barkod yazdırmayı, envanter sayımını ve CRM/ERP entegrasyonunu sağlar.

## ✨ Özellikler

### ✅ Tamamlanan Özellikler

1. **RFID Okuma Modülü**
   - UHF RFID etiket okuma
   - Toplu okuma (inventory)
   - Tekli okuma
   - Sinyal gücü (RSSI) gösterimi
   - Demo modu (test için)

2. **Ürün Girişi**
   - RFID etiket kaydı
   - Barkod kaydı
   - Ürün bilgileri (ad, miktar, lokasyon, açıklama)
   - Veritabanına kaydetme

3. **Veritabanı Yönetimi**
   - SQLite/Room Database
   - Ürün tablosu
   - Stok hareketleri tablosu
   - RFID okuma kayıtları
   - Offline çalışma desteği

4. **Barkod Yazıcı Desteği**
   - Bluetooth bağlantı
   - Zebra yazıcı desteği (ZPL)
   - TSC yazıcı desteği (TSPL)
   - RFID etiket yazdırma

5. **Modern UI/UX**
   - Material Design 3
   - Türkçe dil desteği
   - Kullanıcı dostu arayüz
   - Responsive tasarım

### 🚧 Geliştirme Aşamasında

1. **Envanter Sayımı** (iskelet hazır)
2. **Ürün Listesi** (iskelet hazır)
3. **Ayarlar Ekranı** (iskelet hazır)
4. **Barkod Okuma** (ZXing entegrasyonu)
5. **Raporlama Modülü**

## 📁 Proje Yapısı

```
warehouse-rfid-app/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/warehouse/rfid/
│   │   │   │   ├── MainActivity.kt                 # Ana menü
│   │   │   │   ├── ui/
│   │   │   │   │   ├── ProductEntryActivity.kt    # Ürün girişi
│   │   │   │   │   ├── RFIDScanActivity.kt        # RFID okuma
│   │   │   │   │   ├── InventoryActivity.kt       # Envanter
│   │   │   │   │   ├── PrinterActivity.kt         # Yazıcı
│   │   │   │   │   ├── ProductListActivity.kt     # Ürün listesi
│   │   │   │   │   └── SettingsActivity.kt        # Ayarlar
│   │   │   │   ├── data/
│   │   │   │   │   ├── database/
│   │   │   │   │   │   ├── AppDatabase.kt         # Ana veritabanı
│   │   │   │   │   │   ├── ProductDao.kt          # Veritabanı işlemleri
│   │   │   │   │   │   └── ProductEntity.kt       # Veri modelleri
│   │   │   │   │   └── api/                       # API entegrasyonu (hazır)
│   │   │   │   ├── rfid/
│   │   │   │   │   └── RFIDManager.kt             # RFID yöneticisi
│   │   │   │   └── printer/
│   │   │   │       └── PrinterManager.kt          # Yazıcı yöneticisi
│   │   │   ├── res/
│   │   │   │   ├── layout/                        # UI tasarımları
│   │   │   │   ├── values/                        # Renkler, metinler
│   │   │   │   └── xml/                           # Yapılandırma
│   │   │   └── AndroidManifest.xml                # Uygulama manifest
│   │   └── build.gradle                           # Bağımlılıklar
│   └── proguard-rules.pro                         # ProGuard kuralları
├── gradle/
├── README.md                                       # Proje açıklaması
├── KURULUM_REHBERI.md                             # Detaylı kurulum
├── PROJE_OZETI.md                                 # Bu dosya
├── settings.gradle
├── build.gradle
└── gradle.properties
```

## 🛠️ Kullanılan Teknolojiler

### Android
- **Kotlin**: Ana programlama dili
- **Android SDK**: API Level 24+ (Android 7.0+)
- **Material Design 3**: Modern UI bileşenleri

### Veritabanı
- **Room Database**: SQLite ORM
- **LiveData**: Reaktif veri akışı
- **Coroutines**: Asenkron işlemler

### Ağ İletişimi
- **Retrofit**: REST API client
- **OkHttp**: HTTP client
- **Gson**: JSON serialization

### Diğer
- **ZXing**: Barkod okuma (entegre edilecek)
- **Chainway SDK**: UHF RFID okuyucu (opsiyonel)

## 🚀 Hızlı Başlangıç

### 1. Projeyi Açın
```bash
# Android Studio'da:
File > Open > rd klasörünü seçin
```

### 2. Gradle Sync
- Otomatik olarak başlayacak
- Bağımlılıklar indirilecek

### 3. Emülatörde Çalıştırın
```bash
Run > Run 'app' (Shift+F10)
```

### 4. Test Edin
- Ana menü açılacak
- Demo modu aktif olacak
- RFID okuma simülasyon ile çalışacak

## 📱 Cihaz Gereksinimleri

### Minimum
- Android 7.0 (API 24)
- 2GB RAM
- 100MB depolama

### Önerilen
- Android 11+ (API 30+)
- 4GB RAM
- Chainway C5 UHF El Terminali

## 🔧 Yapılandırma

### Demo Modu
Varsayılan olarak **demo modu aktif**. Gerçek Chainway cihazı olmadan test edebilirsiniz.

### Gerçek Cihaz Modu
1. Chainway SDK'sını edinin
2. `app/libs/` klasörüne kopyalayın
3. `RFIDManager.kt` dosyasında `isDemoMode = false` yapın
4. Uygulamayı yeniden derleyin

### CRM/ERP Entegrasyonu
1. `ApiConfig.kt` dosyası oluşturun
2. API URL ve anahtarını girin
3. `ApiService.kt` dosyasını özelleştirin

## 📊 Veritabanı Şeması

### Products Tablosu
```sql
CREATE TABLE products (
    id INTEGER PRIMARY KEY,
    rfid_tag TEXT UNIQUE NOT NULL,
    barcode TEXT,
    name TEXT NOT NULL,
    description TEXT,
    quantity INTEGER NOT NULL,
    location TEXT,
    created_at INTEGER NOT NULL,
    updated_at INTEGER NOT NULL,
    synced INTEGER DEFAULT 0
);
```

### StockMovements Tablosu
```sql
CREATE TABLE stock_movements (
    id INTEGER PRIMARY KEY,
    product_id INTEGER NOT NULL,
    movement_type TEXT NOT NULL,
    quantity INTEGER NOT NULL,
    user TEXT NOT NULL,
    notes TEXT,
    timestamp INTEGER NOT NULL,
    synced INTEGER DEFAULT 0,
    FOREIGN KEY (product_id) REFERENCES products(id)
);
```

## 🔐 İzinler

Uygulama aşağıdaki izinleri kullanır:

- **INTERNET**: API bağlantısı
- **BLUETOOTH**: Yazıcı bağlantısı
- **BLUETOOTH_CONNECT**: Bluetooth cihaz bağlantısı (Android 12+)
- **BLUETOOTH_SCAN**: Bluetooth cihaz tarama (Android 12+)
- **ACCESS_FINE_LOCATION**: Bluetooth için konum
- **CAMERA**: Barkod okuma
- **VIBRATE**: Geri bildirim

## 📈 Gelecek Geliştirmeler

### Kısa Vadeli (1-2 Hafta)
- [ ] Envanter sayım modülü
- [ ] Ürün listesi ve arama
- [ ] Barkod okuma (ZXing)
- [ ] Ayarlar ekranı
- [ ] Raporlama

### Orta Vadeli (1-2 Ay)
- [ ] Çoklu kullanıcı desteği
- [ ] Rol tabanlı yetkilendirme
- [ ] Gelişmiş raporlar (PDF, Excel)
- [ ] Toplu ürün girişi
- [ ] QR kod desteği

### Uzun Vadeli (3-6 Ay)
- [ ] Bulut senkronizasyonu
- [ ] Çoklu depo desteği
- [ ] Mobil web arayüzü
- [ ] Push notification
- [ ] Analitik dashboard

## 🤝 Katkıda Bulunma

Projeye katkıda bulunmak isterseniz:

1. Fork yapın
2. Feature branch oluşturun (`git checkout -b feature/AmazingFeature`)
3. Commit yapın (`git commit -m 'Add some AmazingFeature'`)
4. Push edin (`git push origin feature/AmazingFeature`)
5. Pull Request açın

## 📝 Lisans

Bu proje MIT lisansı altında lisanslanmıştır. Ticari kullanım için uygundur.

## 📞 İletişim

### Proje Desteği
- **GitHub**: [Proje Sayfası]
- **Email**: support@example.com

### Chainway Desteği
- **Web**: www.chainway.net
- **Email**: support@chainway.net

## 🙏 Teşekkürler

- **Chainway**: C5 UHF cihaz desteği için
- **Android Community**: Açık kaynak kütüphaneler için
- **Material Design**: UI/UX tasarım rehberi için

---

## 📚 Ek Kaynaklar

### Dokümantasyon
- [README.md](README.md) - Genel bilgiler
- [KURULUM_REHBERI.md](KURULUM_REHBERI.md) - Detaylı kurulum
- [Android Developer Guide](https://developer.android.com/guide)

### Eğitim
- [Kotlin Bootcamp](https://developer.android.com/courses/kotlin-bootcamp/overview)
- [Android Basics](https://developer.android.com/courses/android-basics-kotlin/course)
- [Room Database](https://developer.android.com/training/data-storage/room)

### Video Kaynakları
- YouTube: "Android Studio Tutorial"
- YouTube: "Kotlin for Beginners"
- YouTube: "RFID Integration Android"

---

**Son Güncelleme**: 2024
**Versiyon**: 1.0.0
**Durum**: ✅ Temel özellikler tamamlandı, test için hazır

🎉 **Başarılar!**
