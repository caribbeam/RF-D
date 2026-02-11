# 🔧 Sorun Giderme Rehberi

## ⚠️ Mevcut Durum

Proje başarıyla oluşturuldu ve Gradle build başarılı oldu (**BUILD SUCCESSFUL**), ancak çalıştırma sırasında bellek sorunu yaşanıyor.

---

## 🐛 Karşılaşılan Sorun

**Hata:** `There is insufficient memory for the Java Runtime Environment to continue`

**Sebep:** Gradle daemon için ayrılan bellek (2048MB) sistem belleğinden fazla.

**Çözüm:** `gradle.properties` dosyasında bellek 1536MB'a düşürüldü.

---

## ✅ Çözüm Adımları

### Yöntem 1: Android Studio'yu Yeniden Başlatın (ÖNERİLEN)

1. **Android Studio'yu tamamen kapatın**
2. **Tüm Gradle daemon süreçlerini durdurun:**
   - Windows Görev Yöneticisi'ni açın (Ctrl+Shift+Esc)
   - "Ayrıntılar" sekmesine gidin
   - "java.exe" süreçlerini bulun ve sonlandırın
   
3. **Android Studio'yu tekrar açın**
4. **Projeyi açın:** `File > Open > c:/Users/site/Desktop/rd`
5. **Gradle sync otomatik başlayacak**
6. **Uygulamayı çalıştırın:** Yeşil ▶️ butonuna basın

---

### Yöntem 2: Manuel Gradle Daemon Temizleme

Eğer Yöntem 1 işe yaramazsa:

1. **Gradle cache'i temizleyin:**
   ```
   C:\Users\site\.gradle\caches
   C:\Users\site\.gradle\daemon
   ```
   Bu klasörleri silin veya yeniden adlandırın

2. **Android Studio'yu yeniden başlatın**

3. **Gradle sync yapın**

---

### Yöntem 3: Bellek Ayarlarını Daha Da Düşürün

Eğer hala sorun yaşıyorsanız, `gradle.properties` dosyasında:

```properties
org.gradle.jvmargs=-Xmx1024m -Dfile.encoding=UTF-8
```

Belleği 1024MB'a düşürün.

---

## 🚀 Uygulamayı Çalıştırma

### Emülatörde Test

1. Android Studio'da **Tools > Device Manager**
2. Bir emülatör oluşturun (yoksa):
   - **Create Device** butonuna tıklayın
   - **Phone > Pixel 5** seçin
   - **System Image:** Android 11 (API 30) veya üzeri
   - **Finish**

3. **Emülatörü başlatın**
4. **Run > Run 'app'** veya yeşil ▶️ butonuna basın

---

### Gerçek Cihazda Test (Chainway C5)

1. **USB Debugging'i açın:**
   - Ayarlar > Geliştirici Seçenekleri > USB Debugging
   
2. **Cihazı USB ile bağlayın**

3. **Android Studio'da cihazı seçin**

4. **Run > Run 'app'**

---

### APK Oluşturma (Manuel Yükleme)

1. **Build > Build Bundle(s) / APK(s) > Build APK(s)**

2. **APK konumu:**
   ```
   c:/Users/site/Desktop/rd/app/build/outputs/apk/debug/app-debug.apk
   ```

3. **APK'yı cihaza kopyalayın ve yükleyin**

---

## 📱 Uygulama Özellikleri

### Demo Modu
- Gerçek Chainway C5 cihazı olmadan test edebilirsiniz
- RFID okuma simüle edilir
- Tüm özellikler çalışır

### Gerçek Cihaz Modu
Chainway C5 aldığınızda:
1. Chainway SDK'sını temin edin
2. `app/libs/` klasörüne kopyalayın
3. `app/build.gradle` dosyasında SDK'yı aktif edin:
   ```gradle
   implementation files('libs/chainway-uhf-sdk.aar')
   ```
4. `RFIDManager.kt` dosyasında gerçek SDK kodunu aktif edin

---

## 🔗 CRM/ERP Entegrasyonu

### API Yapılandırması

`app/src/main/java/com/warehouse/rfid/data/api/ApiConfig.kt` dosyasını oluşturun:

```kotlin
object ApiConfig {
    const val BASE_URL = "https://your-crm-system.com/api/"
    const val API_KEY = "your-api-key"
}
```

### Desteklenen İşlemler
- Ürün senkronizasyonu
- Stok güncelleme
- Envanter raporlama
- Mal kabul/sevkiyat kayıtları

---

## 📞 Destek

Sorun yaşamaya devam ederseniz:

1. **Hata loglarını kontrol edin:**
   - Android Studio > Logcat sekmesi
   - Hata mesajlarını kopyalayın

2. **Sistem gereksinimlerini kontrol edin:**
   - Minimum 4GB RAM
   - 2GB boş disk alanı
   - Android Studio Arctic Fox veya üzeri

3. **Java versiyonunu kontrol edin:**
   - Android Studio kendi JDK'sını kullanır
   - File > Project Structure > SDK Location

---

## ✨ Başarı Durumu

✅ Proje oluşturuldu
✅ Gradle build başarılı
✅ Tüm dosyalar hazır
⚠️ Bellek optimizasyonu yapıldı
🔄 Android Studio yeniden başlatma gerekiyor

---

**Son Güncelleme:** Gradle bellek ayarı 1536MB'a düşürüldü
**Durum:** Çalıştırmaya hazır (Android Studio yeniden başlatma sonrası)
