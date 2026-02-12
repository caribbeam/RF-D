# 📱 Android Telefon ile Test Rehberi

## ADIM 1: APK OLUŞTURMA (Bilgisayarınızda)

### Yöntem A: Android Studio ile (Önerilen)
```
1. Android Studio'yu açın
2. Projeyi açın (c:/Users/site/Desktop/rd)
3. Build > Build Bundle(s) / APK(s) > Build APK(s)
4. Bekleyin (3-5 dakika)
5. APK hazır: app/build/outputs/apk/debug/app-debug.apk
```

### Yöntem B: Komut Satırı ile (Daha Hızlı)
```cmd
cd c:/Users/site/Desktop/rd
gradlew.bat assembleDebug
```
APK konumu: `app/build/outputs/apk/debug/app-debug.apk`

---

## ADIM 2: APK'YI TELEFONA AKTARMA

### Yöntem 1: USB Kablo ile
```
1. Telefonu USB ile bilgisayara bağlayın
2. Telefonda "Dosya aktarımı" modunu seçin
3. app-debug.apk dosyasını telefona kopyalayın
4. Telefonda Downloads klasörüne yapıştırın
```

### Yöntem 2: Google Drive ile
```
1. app-debug.apk'yı Google Drive'a yükleyin
2. Telefondan Google Drive'ı açın
3. APK'yı indirin
```

### Yöntem 3: Email ile
```
1. APK'yı kendinize email ile gönderin
2. Telefondan email'i açın
3. APK'yı indirin
```

---

## ADIM 3: APK KURULUMU (Telefonda)

### 1. Bilinmeyen Kaynaklara İzin Verin
```
Android 8+:
Ayarlar > Uygulamalar > Özel erişim > 
Bilinmeyen uygulamaları yükle > 
Dosya Yöneticisi > İzin ver

Android 7 ve altı:
Ayarlar > Güvenlik > 
Bilinmeyen kaynaklar > Aç
```

### 2. APK'yı Kurun
```
1. Dosya Yöneticisi'ni açın
2. Downloads klasörüne gidin
3. app-debug.apk'ya tıklayın
4. "Yükle" butonuna basın
5. Kurulum tamamlanana kadar bekleyin
```

---

## ADIM 4: VPN BAĞLANTISI (SQL Mikro için)

### Seçenek 1: USB Tethering (ÖNERİLEN)
```
1. Bilgisayarınızdan FortiVPN'e bağlanın
2. Telefonu USB ile bilgisayara bağlayın
3. Telefon Ayarları > Ağ ve İnternet > 
   Hotspot ve Tethering > USB Tethering > Aç
4. Telefon artık bilgisayarın VPN'ini kullanır
5. RFID uygulamasını açın
```

### Seçenek 2: FortiClient VPN Uygulaması
```
1. Play Store'dan "FortiClient VPN" indirin
2. Uygulamayı açın
3. VPN profilini yapılandırın:
   - Server: [FortiVPN sunucu adresi]
   - Port: 443
   - Username: [kullanıcı adınız]
   - Password: [şifreniz]
4. "Connect" butonuna basın
5. VPN bağlandıktan sonra RFID uygulamasını açın
```

### Seçenek 3: VPN'siz Test (Demo Modu)
```
VPN olmadan test etmek isterseniz:
1. Uygulamayı açın
2. SQL Mikro senkronizasyonu çalışmaz
3. Ama diğer tüm özellikler çalışır:
   - Manuel ürün ekleme
   - RFID okuma (demo mod)
   - Envanter sayım
   - Konum yönetimi
```

---

## ADIM 5: UYGULAMA AYARLARI

### İlk Açılışta
```
1. Uygulamayı açın
2. Giriş ekranı gelir
3. Varsayılan kullanıcı:
   - Kullanıcı adı: admin
   - Şifre: admin123
```

### Veritabanı Ayarları (VPN ile)
```
1. Ana ekranda "Ayarlar" butonuna basın
2. "SQL Mikro Bağlantısı" seçin
3. Veritabanı seçin:
   - OZTUZUN (varsayılan)
   - AFM
   - AFP
4. "Bağlantıyı Test Et" butonuna basın
5. "Ürünleri Senkronize Et" butonuna basın
```

---

## ADIM 6: TEST SENARYOLARI

### Test 1: Giriş Yapma
```
✅ Kullanıcı adı: admin
✅ Şifre: admin123
✅ Giriş butonuna bas
✅ Ana ekran açılmalı
```

### Test 2: Ürün Ekleme
```
✅ "Ürün Girişi" butonuna bas
✅ Ürün kodu: TEST001
✅ Ürün adı: Test Ürünü
✅ Miktar: 10
✅ Birim: Adet
✅ "Kaydet" butonuna bas
```

### Test 3: RFID Okuma (Demo)
```
✅ "RFID Okuma" butonuna bas
✅ "Taramayı Başlat" butonuna bas
✅ Demo RFID etiketleri görünmeli
✅ Her saniyede yeni etiketler eklenmeli
```

### Test 4: Ürün Listesi
```
✅ "Ürün Listesi" butonuna bas
✅ Eklediğiniz ürünler görünmeli
✅ Arama yapın: TEST
✅ Filtreleme yapın: Birim > Adet
```

### Test 5: Envanter Sayım
```
✅ "Envanter" butonuna bas
✅ Konum seçin: Koridor 1, Raf A, Seviye 1
✅ "Sayımı Başlat" butonuna bas
✅ RFID etiketleri okunmalı
✅ Rapor oluşturulmalı
```

### Test 6: SQL Mikro Senkronizasyonu (VPN ile)
```
✅ VPN'e bağlanın (USB Tethering veya FortiClient)
✅ Ayarlar > SQL Mikro Bağlantısı
✅ "Bağlantıyı Test Et" - Başarılı olmalı
✅ "Ürünleri Senkronize Et" - Ürünler çekilmeli
✅ Ürün Listesi'nde SQL Mikro ürünleri görünmeli
```

---

## SORUN GİDERME

### Sorun 1: APK Kurulmuyor
```
Çözüm:
1. Bilinmeyen kaynaklara izin verdiğinizden emin olun
2. Eski sürümü kaldırın (varsa)
3. Telefonu yeniden başlatın
4. Tekrar deneyin
```

### Sorun 2: Uygulama Açılmıyor
```
Çözüm:
1. Uygulamayı kaldırın
2. Telefonu yeniden başlatın
3. APK'yı tekrar kurun
4. Logcat'i kontrol edin (Android Studio)
```

### Sorun 3: SQL Mikro Bağlanamıyor
```
Çözüm:
1. VPN bağlantısını kontrol edin
2. USB Tethering açık mı?
3. Veritabanı bilgileri doğru mu?
4. Sunucu erişilebilir mi? (ping test)
```

### Sorun 4: RFID Okuma Çalışmıyor
```
Çözüm:
1. Demo modda çalışıyor mu? (Evet olmalı)
2. Gerçek Chainway C5 cihazında mı test ediyorsunuz?
3. Chainway SDK kurulu mu?
4. İzinler verildi mi?
```

---

## HIZLI TEST KOMUTU

### APK Oluştur ve Kur (Tek Komut)
```cmd
cd c:/Users/site/Desktop/rd
gradlew.bat assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

### Logları İzle
```cmd
adb logcat | findstr "RFID"
```

---

## PERFORMANS İPUÇLARI

### Android Studio Yavaşsa:
```
1. Gradle Daemon'u kullanın:
   gradlew.bat --daemon assembleDebug

2. Offline modda çalışın:
   gradlew.bat --offline assembleDebug

3. Paralel derleme:
   gradlew.bat --parallel assembleDebug

4. Build cache kullanın:
   gradlew.bat --build-cache assembleDebug
```

### Telefon Performansı:
```
1. Gereksiz uygulamaları kapatın
2. Telefonu yeniden başlatın
3. Depolama alanını temizleyin
4. Geliştirici seçeneklerini açın:
   - Animasyonları kapatın
   - GPU rendering açın
```

---

## VPN OLMADAN TEST

### Demo Modu Özellikleri:
```
✅ Ürün ekleme/düzenleme/silme
✅ RFID okuma (simülasyon)
✅ RFID eşleştirme
✅ Envanter sayım
✅ Konum yönetimi
✅ Kullanıcı yönetimi
✅ Raporlama

❌ SQL Mikro senkronizasyonu (VPN gerekli)
❌ Gerçek RFID okuma (Chainway C5 gerekli)
```

---

## ÖZET

### Hızlı Başlangıç:
```
1. APK oluştur: gradlew.bat assembleDebug
2. APK'yı telefona kopyala
3. Bilinmeyen kaynaklara izin ver
4. APK'yı kur
5. Uygulamayı aç
6. Giriş yap (admin/admin123)
7. Test et!
```

### VPN ile Test:
```
1. Bilgisayardan FortiVPN'e bağlan
2. Telefonu USB ile bağla
3. USB Tethering aç
4. Uygulamayı aç
5. SQL Mikro'ya bağlan
6. Ürünleri senkronize et
```

### VPN'siz Test:
```
1. Uygulamayı aç
2. Manuel ürün ekle
3. RFID demo modunu test et
4. Tüm özellikleri dene
```

---

## İLETİŞİM

Sorun yaşarsanız:
1. Logları kaydedin (adb logcat)
2. Ekran görüntüsü alın
3. Hata mesajını not edin
4. Bana bildirin

**Test başarılar! 🚀📱**
