# Restaurant Management System Threads

**Hazırlayanlar:** Fatma Nur Kurt, Esin Özdemir
---

## Proje Amacı

Bu proje, işletim sistemlerinin temel kavramlarından olan thread yönetimi ve senkronizasyon mekanizmalarını anlamak amacıyla geliştirilmiştir. Uygulama, bir restoranın günlük işleyişini simüle eden multithread tabanlı bir masaüstü uygulamasıdır. Proje ile gerçek zamanlı ortamda thread senkronizasyonu, kaynak yönetimi ve GUI tasarımı konularında pratik yapılması hedeflenmektedir.


---

## Temel Özellikler

- **Thread Kullanımı:**  
  Her müşteri, garson, aşçı ve kasa işlemi ayrı bir thread olarak çalışır.  
  - Müşteri thread’leri; restorana giriş, masa seçimi ve oturma işlemleri için oluşturulur.
  - Garson thread’leri; sipariş alma ve müşteriden sipariş alındıktan sonra siparişi aşçılara iletme işlemlerini gerçekleştirir.
  - Aşçı thread’leri; gelen siparişleri hazırlamak için çalışır.
  - Kasa thread’i; ödeme alma ve hesap kapatma işlemlerini tek bir thread üzerinden yönetir.

- **Senkronizasyon:**  
  - Müşteriler arasında masa seçimi ve oturma sırası senkronize edilir.
  - Aynı masaya birden fazla garsonun hizmet vermesi engellenir.
  - Aşçılar, aynı siparişi aynı anda hazırlamamaları için senkronize çalışır.
  - Kasa, her seferinde yalnızca bir siparişin ödemesini işleyerek kaynakların düzgün yönetilmesini sağlar.

- **Öncelikli Müşteri Avantajı:**  
  - Müşteriler iki kategoriye ayrılır: Normal ve 65 yaş üzeri (öncelikli).  
  - Öncelikli müşteriler, boş masa bulunması durumunda normal müşterilerin önüne geçerek yerleştirilir.

- **Zaman Süreleri:**  
  - Müşteri bekleme süresi: 20 saniye  
  - Sipariş alma süresi: 2 saniye  
  - Yemek hazırlama süresi: 3 saniye  
  - Yemek yeme süresi: 3 saniye  
  - Ödeme işlemi süresi: 1 saniye

- **Raporlama:**  
  Tüm simülasyon adımları ve işlemleri, anlık olarak bir metin dosyasına yazdırılarak takip edilebilir.

---

## Gereksinimler

- **Programlama Dili:** Java  
- **Teknolojiler:**  
  - Thread yönetimi: Java'nın Thread, Runnable, synchronized, Lock ve ReentrantLock sınıfları  
  - GUI: Java Swing  
- **Simülasyon:** Gerçek zamanlı kaynak yönetimi ve senkronizasyon örnekleri

---

## Kurulum ve Başlatma

1. **Proje Dosyalarını İndirme**
    Projeyi GitHub üzerinden yerel makinenize klonlayabilirsiniz. Git arayüzünüzü kullanarak aşağıdaki komutu uygulayabilirsiniz:

    ```bash
    git clone https://github.com/Dawnfairy/restaurant-management-system-multithreading.git

1. **Geliştirme Ortamını Ayarlayın:**

   1. **IDE Seçimi:**  
      Projeyi IntelliJ IDEA, Eclipse, VS Code veya tercih ettiğiniz herhangi bir Java IDE'sinde geliştirin.  
      Multithread işlemleri için gerekli kütüphaneler (Java'nın standart kütüphaneleri) projeye dahildir.

   2. **Projeyi Açma:**  
      IDE'nizde "File > Open" veya "File > Open Folder..." seçeneklerini kullanarak proje klasörünü açın.

2. **Projeyi Çalıştırma:**

   - IDE üzerinden “Run” veya “Debug” seçeneklerini kullanarak projeyi derleyip çalıştırın.
   - Uygulama açıldığında, ekranda yer alan simülasyonu başlatma butonuna tıklayarak sistemin adım adım ilerlemesini gözlemleyebilirsiniz.

---

## Kullanım

- **Simülasyon Başlatma:**  
  Uygulama arayüzü üzerinden simülasyon başlatılır.  
- **Adım Adım İşlemler:**  
  Müşteri girişi, sipariş alma, yemek hazırlama ve ödeme işlemleri, belirlenen zaman süreleri ve kullanıcı onayı ile adım adım gerçekleşir.  
- **Raporlama:**  
  Tüm adımlar, anlık olarak oluşturulan bir metin dosyasına yazdırılarak simülasyon sürecinin takip edilmesi sağlanır.

---

## İletişim

Proje ile ilgili sorular, geri bildirimler veya katkı talepleri için aşağıdaki kişilerle iletişime geçebilirsiniz:

  Fatma Nur Kurt - kurtfatmanur8@gmail.com
