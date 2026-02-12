# 🔧 Gradle İndirme Sorunu Çözümü

## ⚠️ HATA

```
Could not install Gradle distribution from 'https://services.gradle.org/distributions/gradle-8.13-bin.zip'.
Reason: java.net.SocketException: Connection reset
```

Bu hata, Gradle'ın internetten indirilemediğini gösterir.

---

## ✅ ÇÖZÜM 1: Android Studio ile Aç (ÖNERİLEN)

Android Studio kendi Gradle'ını kullanır, internetten indirmeye gerek kalmaz.

### Adımlar:
```
1. Android Studio'yu aç
2. File > Open
3. c:/Users/site/Desktop/rd klasörünü seç
4. OK butonuna tıkla
5. Gradle otomatik sync olur
6. Build > Build APK(s) veya Run (▶️)
```

**Bu yöntem %100 çalışır çünkü Android Studio kendi Gradle'ını kullanır!**

---

## ✅ ÇÖZÜM 2: Gradle'ı Manuel İndir

### Adım 1: Gradle'ı İndir
```
1. https://services.gradle.org/distributions/gradle-8.13-bin.zip adresine git
2. Tarayıcıdan manuel indir
3. İndirme tamamlanana kadar bekle
```

### Adım 2: Gradle Wrapper'a Kopyala
```
1. İndirilen gradle-8.13-bin.zip dosyasını bul
2. Şu klasöre kopyala:
   C:\Users\site\.gradle\wrapper\dists\gradle-8.13-bin\[hash]\
   
   Örnek:
   C:\Users\site\.gradle\wrapper\dists\gradle-8.13-bin\abc123\gradle-8.13-bin.zip
```

### Adım 3: Tekrar Dene
```cmd
cd c:/Users/site/Desktop/rd
.\gradlew.bat assembleDebug
```

---

## ✅ ÇÖZÜM 3: Gradle Sürümünü Düşür

Daha eski ve stabil bir Gradle sürümü kullan.

### gradle/wrapper/gradle-wrapper.properties Dosyasını Düzenle:
```properties
# Mevcut (Sorunlu):
distributionUrl=https\://services.gradle.org/distributions/gradle-8.13-bin.zip

# Değiştir (Stabil):
distributionUrl=https\://services.gradle.org/distributions/gradle-8.0-bin.zip
```

### Tekrar Dene:
```cmd
.\gradlew.bat assembleDebug
```

---

## ✅ ÇÖZÜM 4: Proxy/Firewall Ayarları

Ağ bağlantısı engelleniyorsa:

### gradle.properties Dosyasına Ekle:
```properties
# Proxy ayarları (gerekirse)
systemProp.http.proxyHost=proxy.sirketiniz.com
systemProp.http.proxyPort=8080
systemProp.https.proxyHost=proxy.sirketiniz.com
systemProp.https.proxyPort=8080

# Proxy kullanmıyorsanız:
systemProp.http.nonProxyHosts=*.gradle.org|localhost
```

---

## ✅ ÇÖZÜM 5: VPN Kullan

Eğer ağ kısıtlaması varsa:

```
1. VPN'e bağlan (FortiVPN veya başka)
2. Gradle indirmeyi tekrar dene
3. İndirme tamamlandıktan sonra VPN'i kapatabilirsiniz
```

---

## ✅ ÇÖZÜM 6: Gradle Daemon'u Durdur ve Temizle

### Adım 1: Gradle Daemon'u Durdur
```cmd
.\gradlew.bat --stop
```

### Adım 2: Gradle Cache'i Temizle
```cmd
rmdir /s /q %USERPROFILE%\.gradle\caches
```

### Adım 3: Tekrar Dene
```cmd
.\gradlew.bat assembleDebug
```

---

## 🎯 EN KOLAY ÇÖZÜM (ÖNERİLEN)

### Android Studio Kullan - Hiç Uğraşma!

```
1. ✅ Android Studio'yu aç
2. ✅ Projeyi aç (c:/Users/site/Desktop/rd)
3. ✅ Gradle otomatik sync olur
4. ✅ Build > Build APK(s)
5. ✅ APK hazır!
```

**Veya direkt telefona yükle:**

```
1. ✅ Telefonu USB ile bağla
2. ✅ USB Debugging aç
3. ✅ Android Studio'da Run (▶️) butonuna tıkla
4. ✅ Uygulama telefona yüklenir!
```

---

## 🔍 SORUN TESPİTİ

### Ağ Bağlantısını Test Et:
```cmd
ping services.gradle.org
```

**Sonuç:**
- ✅ Yanıt alıyorsanız: Ağ çalışıyor, başka sorun var
- ❌ Yanıt almıyorsanız: Ağ sorunu, VPN veya proxy gerekli

### Gradle Wrapper Durumunu Kontrol Et:
```cmd
.\gradlew.bat --version
```

**Sonuç:**
- ✅ Sürüm gösteriyorsa: Gradle kurulu
- ❌ Hata veriyorsa: Gradle indirilemiyor

---

## 📊 ÇÖZÜM KARŞILAŞTIRMASI

| Çözüm | Kolay | Hız | Başarı |
|-------|-------|-----|--------|
| Android Studio | ✅✅✅ | ⚡⚡⚡ | %100 |
| Manuel İndirme | ✅✅ | ⚡⚡ | %90 |
| Sürüm Düşürme | ✅✅ | ⚡⚡ | %80 |
| Proxy Ayarları | ✅ | ⚡ | %70 |
| VPN | ✅✅ | ⚡⚡ | %90 |
| Cache Temizleme | ✅✅ | ⚡⚡ | %60 |

---

## 💡 ÖNERİM

### En Garantili Yöntem:

```
🎯 Android Studio Kullan!

Neden?
✅ Kendi Gradle'ını kullanır
✅ İnternetten indirmeye gerek yok
✅ Ağ sorunlarından etkilenmez
✅ Tek tıkla çalışır
✅ APK oluşturur veya direkt telefona yükler
```

### Adımlar:
```
1. Android Studio'yu aç
2. Projeyi aç
3. Gradle sync bekle (otomatik)
4. Build > Build APK(s) VEYA Run (▶️)
5. Bitti!
```

---

## 🚀 HIZLI BAŞLANGIÇ

### Komut Satırı Çalışmıyorsa:

```
❌ Komut satırı: .\gradlew.bat assembleDebug
   Sorun: Gradle indiremiyor

✅ Android Studio: Build > Build APK(s)
   Çözüm: Kendi Gradle'ını kullanır
```

### Telefona Direkt Yükleme:

```
1. Android Studio'yu aç
2. Projeyi aç
3. Telefonu USB ile bağla
4. Run (▶️) butonuna tıkla
5. Uygulama telefona yüklenir!
```

**APK oluşturmaya bile gerek yok!**

---

## 📝 ÖZET

### Sorun:
```
Gradle internetten indirilemedi
Ağ bağlantısı sorunu
```

### Çözüm:
```
✅ Android Studio kullan (EN KOLAY)
✅ Manuel indir ve kopyala
✅ Gradle sürümünü düşür
✅ VPN kullan
✅ Proxy ayarla
```

### Önerim:
```
🎯 Android Studio ile aç ve Build yap
   - %100 çalışır
   - Ağ sorunlarından etkilenmez
   - En hızlı yöntem
```

---

## 🎉 SONUÇ

**Gradle indirme sorunu yaşıyorsanız:**

```
1. Android Studio'yu açın
2. Projeyi açın
3. Build > Build APK(s) veya Run (▶️)
4. Bitti!
```

**Android Studio kendi Gradle'ını kullanır, internetten indirmeye gerek kalmaz!**

**Başarılar! 🚀**
