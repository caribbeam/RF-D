# 📦 Chainway C5 UHF Depo Yönetim Sistemi

## 🎯 Proje Hakkında

Bu proje, Chainway C5 UHF El Terminali için geliştirilmiş bir depo yönetim sistemidir.

### Özellikler
- ✅ RFID UHF etiket okuma (simülasyon + gerçek cihaz desteği)
- ✅ Barkod yazdırma entegrasyonu
- ✅ Ürün girişi ve mal kabul
- ✅ Envanter sayımı
- ✅ Offline çalışma (SQLite)
- ✅ CRM/ERP entegrasyonu (REST API)
- ✅ Raporlama ve listeleme

## 🚀 Kurulum

### Gereksinimler
- Android Studio (Arctic Fox veya üzeri)
- JDK 11 veya üzeri
- Android SDK (API Level 24+)
- Chainway C5 cihazı (opsiyonel - emülatörde de çalışır)

### Adımlar

1. **Projeyi Android Studio'da Açın**
   ```bash
   File > Open > Proje klasörünü seçin
   ```

2. **Gradle Sync**
   - Android Studio otomatik olarak bağımlılıkları indirecektir

3. **Emülatörde Test**
   - Run > Run 'app'
   - Emülatör seçin ve çalıştırın

4. **Gerçek Cihaza Yükleme**
   - USB Debugging'i açın (Ayarlar > Geliştirici Seçenekleri)
   - USB ile bağlayın
   - Run > Run 'app' > Cihazı seçin

## 📱 Kullanım

### 1. Ürün Girişi
- Ana ekrandan "Ürün Girişi" seçin
- RFID okuma butonuna basın
- Ürün bilgilerini girin
- Kaydet

### 2. RFID Okuma
- "RFID Okuma" modülüne girin
- "Okumaya Başla" butonuna basın
- Etiketler otomatik okunacak
- Sonuçları görüntüleyin

### 3. Barkod Yazdırma
- Ürün seçin
- "Barkod Yazdır" butonuna basın
- Yazıcı bağlantısını yapın
- Yazdır

### 4. Envanter Sayımı
- "Envanter" modülüne girin
- Toplu okuma yapın
- Sonuçları karşılaştırın
- Rapor alın

## 🔧 CRM/ERP Entegrasyonu

### API Yapılandırması

`app/src/main/java/com/warehouse/config/ApiConfig.kt` dosyasında:

```kotlin
object ApiConfig {
    const val BASE_URL = "https://your-crm-system.com/api/"
    const val API_KEY = "your-api-key"
}
```

### Desteklenen Endpoint'ler
- `GET /products` - Ürün listesi
- `POST /products` - Yeni ürün
- `PUT /products/{id}` - Ürün güncelleme
- `POST /inventory` - Envanter güncelleme
- `GET /stock-movements` - Stok hareketleri

## 📂 Proje Yapısı

```
warehouse-rfid-app/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/warehouse/
│   │   │   │   ├── MainActivity.kt
│   │   │   │   ├── ui/
│   │   │   │   │   ├── ProductEntryActivity.kt
│   │   │   │   │   ├── RFIDScanActivity.kt
│   │   │   │   │   ├── InventoryActivity.kt
│   │   │   │   │   └── PrinterActivity.kt
│   │   │   │   ├── data/
│   │   │   │   │   ├── database/
│   │   │   │   │   │   ├── AppDatabase.kt
│   │   │   │   │   │   ├── ProductDao.kt
│   │   │   │   │   │   └── ProductEntity.kt
│   │   │   │   │   └── api/
│   │   │   │   │       ├── ApiService.kt
│   │   │   │   │       └── ApiClient.kt
│   │   │   │   ├── rfid/
│   │   │   │   │   ├── RFIDManager.kt
│   │   │   │   │   └── RFIDSimulator.kt
│   │   │   │   └── printer/
│   │   │   │       └── PrinterManager.kt
│   │   │   ├── res/
│   │   │   │   ├── layout/
│   │   │   │   ├── values/
│   │   │   │   └── drawable/
│   │   │   └── AndroidManifest.xml
│   │   └── build.gradle
│   └── build.gradle
├── gradle/
├── README.md
└── settings.gradle
```

## 🔌 Chainway SDK Entegrasyonu

Gerçek cihaz aldığınızda:

1. Chainway SDK'sını indirin (Chainway'den temin edilir)
2. `app/libs/` klasörüne `.aar` dosyasını kopyalayın
3. `build.gradle` dosyasına ekleyin:
   ```gradle
   implementation files('libs/chainway-uhf-sdk.aar')
   ```
4. `RFIDManager.kt` dosyasında gerçek SDK'yı aktif edin

## 📊 Veritabanı Şeması

### Products Tablosu
- id (Primary Key)
- rfid_tag (Unique)
- barcode
- name
- description
- quantity
- location
- created_at
- updated_at

### StockMovements Tablosu
- id (Primary Key)
- product_id (Foreign Key)
- movement_type (IN/OUT/COUNT)
- quantity
- user
- timestamp

## 🐛 Sorun Giderme

### RFID Okuma Çalışmıyor
- Cihazın UHF modülü açık mı kontrol edin
- Etiket frekansı doğru mu? (Türkiye: 865-868 MHz)
- SDK doğru yüklendi mi?

### Yazıcı Bağlanmıyor
- Bluetooth/WiFi açık mı?
- Yazıcı eşleştirildi mi?
- Yazıcı modeli destekleniyor mu?

### Uygulama Yüklenmiyor
- USB Debugging açık mı?
- Cihaz tanınıyor mu? (`adb devices`)
- Minimum Android sürümü: 7.0 (API 24)

## 📞 Destek

Sorularınız için:
- GitHub Issues
- Email: support@example.com

## 📄 Lisans

MIT License - Ticari kullanım için uygundur.

---

**Not:** Bu proje hem emülatörde test edilebilir hem de gerçek Chainway C5 cihazında çalışır.
