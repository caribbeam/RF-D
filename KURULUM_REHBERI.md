# 🚀 Chainway C5 UHF Depo Yönetim Sistemi - Kurulum Rehberi

## 📋 İçindekiler
1. [Gereksinimler](#gereksinimler)
2. [Kurulum Adımları](#kurulum-adımları)
3. [Emülatörde Test](#emülatörde-test)
4. [Gerçek Cihaza Yükleme](#gerçek-cihaza-yükleme)
5. [Chainway SDK Entegrasyonu](#chainway-sdk-entegrasyonu)
6. [CRM/ERP Entegrasyonu](#crmerp-entegrasyonu)
7. [Sorun Giderme](#sorun-giderme)

---

## 🔧 Gereksinimler

### Yazılım Gereksinimleri
- **Android Studio**: Arctic Fox (2020.3.1) veya üzeri
- **JDK**: 11 veya üzeri
- **Android SDK**: API Level 24 (Android 7.0) veya üzeri
- **Gradle**: 7.0 veya üzeri

### Donanım Gereksinimleri (Opsiyonel)
- **Chainway C5 UHF El Terminali** (gerçek cihaz testi için)
- **Barkod Yazıcı** (Zebra, TSC veya uyumlu model)
- **USB Kablo** (cihaz bağlantısı için)

---

## 📥 Kurulum Adımları

### 1. Android Studio Kurulumu

1. **Android Studio'yu İndirin**
   - [https://developer.android.com/studio](https://developer.android.com/studio)
   - İşletim sisteminize uygun versiyonu seçin

2. **Kurulumu Tamamlayın**
   - Kurulum sihirbazını takip edin
   - Android SDK'yı otomatik olarak yükleyin
   - Android Virtual Device (AVD) Manager'ı kurun

### 2. Projeyi Açma

1. **Android Studio'yu Başlatın**

2. **Projeyi Açın**
   ```
   File > Open > Proje klasörünü seçin (rd klasörü)
   ```

3. **Gradle Sync**
   - Android Studio otomatik olarak Gradle sync başlatacak
   - İlk seferde bağımlılıkları indirmesi 5-10 dakika sürebilir
   - İnternet bağlantınızın aktif olduğundan emin olun

4. **SDK Kontrolü**
   - Tools > SDK Manager
   - Android 7.0 (API 24) veya üzeri yüklü olmalı
   - Android SDK Build-Tools 30.0.0 veya üzeri

---

## 🖥️ Emülatörde Test

### Emülatör Oluşturma

1. **AVD Manager'ı Açın**
   ```
   Tools > Device Manager
   ```

2. **Yeni Cihaz Oluşturun**
   - "Create Device" butonuna tıklayın
   - **Önerilen**: Pixel 5 veya benzeri
   - **System Image**: Android 11 (API 30) veya üzeri
   - **RAM**: En az 2GB

3. **Emülatörü Başlatın**
   - Oluşturduğunuz cihazın yanındaki ▶️ butonuna tıklayın

### Uygulamayı Çalıştırma

1. **Run Butonuna Tıklayın**
   ```
   Run > Run 'app' veya Shift+F10
   ```

2. **Cihaz Seçin**
   - Açılan pencereden emülatörü seçin
   - "OK" butonuna tıklayın

3. **Uygulamayı Test Edin**
   - Ana menü açılacak
   - Demo modu aktif olacak (sarı uyarı)
   - RFID okuma simülasyon ile çalışacak

### Demo Modda Test Senaryoları

#### ✅ RFID Okuma Testi
1. Ana menüden "RFID Okuma" seçin
2. "Okumaya Başla" butonuna basın
3. Simüle edilmiş RFID etiketleri görünecek
4. Her saniye yeni etiketler eklenecek

#### ✅ Ürün Girişi Testi
1. Ana menüden "Ürün Girişi" seçin
2. "RFID Oku" butonuna basın (simüle etiket oluşturulur)
3. Ürün bilgilerini doldurun
4. "Kaydet" butonuna basın
5. Ürün veritabanına kaydedilecek

---

## 📱 Gerçek Cihaza Yükleme

### USB ile Yükleme

1. **USB Debugging'i Açın**
   ```
   Chainway C5 Cihazında:
   Ayarlar > Geliştirici Seçenekleri > USB Debugging (Açık)
   ```
   
   **Not**: Geliştirici Seçenekleri görünmüyorsa:
   ```
   Ayarlar > Telefon Hakkında > Yapı Numarası (7 kez tıklayın)
   ```

2. **Cihazı Bilgisayara Bağlayın**
   - USB kablosu ile bağlayın
   - Cihazda "USB Debugging'e izin ver" mesajı çıkacak
   - "İzin Ver" seçin
   - "Her zaman bu bilgisayardan izin ver" işaretleyin

3. **Cihaz Kontrolü**
   ```bash
   # Terminal/CMD'de çalıştırın:
   adb devices
   ```
   
   Çıktı:
   ```
   List of devices attached
   XXXXXXXXXX    device
   ```

4. **Uygulamayı Yükleyin**
   - Android Studio'da Run > Run 'app'
   - Cihaz listesinden Chainway C5'i seçin
   - Uygulama otomatik yüklenecek

### WiFi ile Yükleme (Opsiyonel)

1. **ADB over WiFi'yi Aktif Edin**
   ```bash
   # Önce USB ile bağlayın, sonra:
   adb tcpip 5555
   adb connect <CIHAZ_IP_ADRESI>:5555
   ```

2. **USB Kablosunu Çıkarın**
   - Artık WiFi üzerinden yükleme yapabilirsiniz

---

## 🔌 Chainway SDK Entegrasyonu

### SDK Dosyalarını Edinme

1. **Chainway'den SDK İsteyin**
   - Chainway resmi web sitesi: [www.chainway.net](http://www.chainway.net)
   - Teknik destek: support@chainway.net
   - SDK dosyası: `chainway-uhf-sdk.aar`

2. **SDK'yı Projeye Ekleyin**
   ```
   Proje klasörü/app/libs/ klasörüne kopyalayın
   ```

### build.gradle Güncellemesi

`app/build.gradle` dosyasında şu satırın yorumunu kaldırın:

```gradle
dependencies {
    // ...
    
    // Chainway SDK - Yorumu kaldırın:
    implementation files('libs/chainway-uhf-sdk.aar')
}
```

### RFIDManager.kt Güncellemesi

`app/src/main/java/com/warehouse/rfid/rfid/RFIDManager.kt` dosyasında:

```kotlin
// Demo modu kapatma
private var isDemoMode = false  // true'dan false'a değiştirin

// SDK import'ları ekleyin:
import com.chainway.uhf.UHFReader
import com.chainway.uhf.UHFReaderListener

// Gerçek SDK kodlarının yorumunu kaldırın
```

### Test Etme

1. Uygulamayı yeniden derleyin
2. Chainway C5'e yükleyin
3. RFID okuma modülünü test edin
4. Gerçek RFID etiketleri okuyun

---

## 🔗 CRM/ERP Entegrasyonu

### API Yapılandırması

1. **API Bilgilerini Girin**
   
   Yeni bir dosya oluşturun: `app/src/main/java/com/warehouse/rfid/config/ApiConfig.kt`

   ```kotlin
   package com.warehouse.rfid.config

   object ApiConfig {
       const val BASE_URL = "https://your-crm-system.com/api/"
       const val API_KEY = "your-api-key-here"
       const val TIMEOUT = 30L // saniye
   }
   ```

2. **API Servis Oluşturma**

   `app/src/main/java/com/warehouse/rfid/data/api/ApiService.kt`:

   ```kotlin
   package com.warehouse.rfid.data.api

   import retrofit2.http.*
   import com.warehouse.rfid.data.database.ProductEntity

   interface ApiService {
       
       @GET("products")
       suspend fun getProducts(): List<ProductEntity>
       
       @POST("products")
       suspend fun createProduct(@Body product: ProductEntity): ProductEntity
       
       @PUT("products/{id}")
       suspend fun updateProduct(
           @Path("id") id: Long,
           @Body product: ProductEntity
       ): ProductEntity
       
       @POST("inventory/sync")
       suspend fun syncInventory(@Body data: Map<String, Any>): Boolean
   }
   ```

3. **Retrofit Client Oluşturma**

   `app/src/main/java/com/warehouse/rfid/data/api/ApiClient.kt`:

   ```kotlin
   package com.warehouse.rfid.data.api

   import com.warehouse.rfid.config.ApiConfig
   import okhttp3.OkHttpClient
   import okhttp3.logging.HttpLoggingInterceptor
   import retrofit2.Retrofit
   import retrofit2.converter.gson.GsonConverterFactory
   import java.util.concurrent.TimeUnit

   object ApiClient {
       
       private val loggingInterceptor = HttpLoggingInterceptor().apply {
           level = HttpLoggingInterceptor.Level.BODY
       }
       
       private val client = OkHttpClient.Builder()
           .addInterceptor(loggingInterceptor)
           .addInterceptor { chain ->
               val request = chain.request().newBuilder()
                   .addHeader("Authorization", "Bearer ${ApiConfig.API_KEY}")
                   .addHeader("Content-Type", "application/json")
                   .build()
               chain.proceed(request)
           }
           .connectTimeout(ApiConfig.TIMEOUT, TimeUnit.SECONDS)
           .readTimeout(ApiConfig.TIMEOUT, TimeUnit.SECONDS)
           .build()
       
       private val retrofit = Retrofit.Builder()
           .baseUrl(ApiConfig.BASE_URL)
           .client(client)
           .addConverterFactory(GsonConverterFactory.create())
           .build()
       
       val apiService: ApiService = retrofit.create(ApiService::class.java)
   }
   ```

### Senkronizasyon

Ürünleri CRM/ERP sisteminize senkronize etmek için:

```kotlin
lifecycleScope.launch {
    try {
        // Yerel veritabanından senkronize edilmemiş ürünleri al
        val unsyncedProducts = database.productDao().getUnsyncedProducts()
        
        // Her ürünü API'ye gönder
        unsyncedProducts.forEach { product ->
            val response = ApiClient.apiService.createProduct(product)
            // Başarılı ise senkronize olarak işaretle
            database.productDao().markProductAsSynced(product.id)
        }
        
        Toast.makeText(this, "Senkronizasyon başarılı", Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {
        Toast.makeText(this, "Senkronizasyon hatası: ${e.message}", Toast.LENGTH_LONG).show()
    }
}
```

---

## 🐛 Sorun Giderme

### Gradle Sync Hataları

**Sorun**: "Failed to resolve dependencies"

**Çözüm**:
```bash
# Terminal'de:
./gradlew clean
./gradlew build --refresh-dependencies
```

### Cihaz Tanınmıyor

**Sorun**: `adb devices` boş liste gösteriyor

**Çözüm**:
1. USB kablosunu değiştirin
2. USB Debugging'i kapatıp açın
3. Bilgisayarı yeniden başlatın
4. USB Driver'ları güncelleyin

### RFID Okuma Çalışmıyor

**Sorun**: Gerçek cihazda RFID okuma yapamıyorum

**Çözüm**:
1. Chainway SDK doğru yüklendi mi kontrol edin
2. `RFIDManager.kt` dosyasında `isDemoMode = false` olmalı
3. Cihazın UHF modülü açık mı kontrol edin
4. RFID etiket frekansı doğru mu? (Türkiye: 865-868 MHz)

### Uygulama Çöküyor

**Sorun**: Uygulama açılırken çöküyor

**Çözüm**:
1. Logcat'i kontrol edin (Android Studio > Logcat)
2. İzinler verildi mi kontrol edin
3. Minimum Android sürümü: 7.0 (API 24)

### Bluetooth Yazıcı Bağlanmıyor

**Sorun**: Yazıcıya bağlanamıyorum

**Çözüm**:
1. Bluetooth izinleri verildi mi?
2. Yazıcı eşleştirildi mi? (Ayarlar > Bluetooth)
3. Yazıcı açık ve menzilde mi?
4. Yazıcı modeli destekleniyor mu? (Zebra, TSC)

---

## 📞 Destek ve İletişim

### Teknik Destek
- **GitHub Issues**: Proje sayfasında issue açın
- **Email**: support@example.com

### Chainway Desteği
- **Web**: [www.chainway.net](http://www.chainway.net)
- **Email**: support@chainway.net
- **Telefon**: +86 755 2697 9016

### Dokümantasyon
- [Android Developer Guide](https://developer.android.com/guide)
- [Kotlin Documentation](https://kotlinlang.org/docs/home.html)
- [Room Database Guide](https://developer.android.com/training/data-storage/room)

---

## 🎓 Eğitim Videoları (Önerilen)

1. **Android Studio Kurulumu**
   - YouTube: "Android Studio Installation Tutorial"

2. **Kotlin Temelleri**
   - [Kotlin Bootcamp for Programmers](https://developer.android.com/courses/kotlin-bootcamp/overview)

3. **Android Uygulama Geliştirme**
   - [Android Basics in Kotlin](https://developer.android.com/courses/android-basics-kotlin/course)

---

## ✅ Kontrol Listesi

Kurulum tamamlandıktan sonra:

- [ ] Android Studio kuruldu
- [ ] Proje açıldı ve Gradle sync başarılı
- [ ] Emülatörde uygulama çalıştı
- [ ] Demo modda RFID okuma test edildi
- [ ] Ürün girişi test edildi
- [ ] Chainway C5 cihazı bağlandı (varsa)
- [ ] Gerçek cihazda uygulama çalıştı
- [ ] Chainway SDK entegre edildi (varsa)
- [ ] CRM/ERP API yapılandırıldı (varsa)

---

**Başarılar! 🎉**

Herhangi bir sorunuz olursa lütfen iletişime geçin.
