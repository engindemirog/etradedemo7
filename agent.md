# 📋 Proje Standartları — etradedemo7 (E-Ticaret Uygulaması)

> **Bu dosya, proje genelinde uyulması gereken mimari ve kodlama standartlarını tanımlar.**
> Tüm kod üretimlerinde bu kurallar referans alınmalıdır.

---

## 1. Proje Bilgileri

| Özellik              | Değer                          |
| -------------------- | ------------------------------ |
| **Proje Adı**        | etradedemo7                    |
| **Grup**             | com.turkcell                   |
| **Base Package**     | com.turkcell.etradedemo7       |
| **Spring Boot**      | 4.0.2                         |
| **Java Sürümü**      | 17                             |
| **Veritabanı**       | H2 (geliştirme ortamı)        |
| **Build Aracı**      | Maven                          |

---

## 2. Kullanılan Bağımlılıklar

- **Spring Boot Starter WebMVC** — REST API
- **Spring Boot Starter Data JPA** — Veritabanı erişimi (ORM)
- **Spring Boot Starter Validation** — Bean Validation (Jakarta)
- **H2 Database** — Gömülü geliştirme veritabanı
- **H2 Console** — Veritabanı yönetim arayüzü
- **Lombok** — Boilerplate kod azaltma

---

## 3. Katmanlı Mimari (N-Tier / Layered Architecture)

Proje klasik **N katmanlı mimariyi** takip eder. 4 ana katman vardır:

```
com.turkcell.etradedemo7
│
├── entities/                  # ENTITIES KATMANI — JPA Entity sınıfları
│
├── dataAccess/                # DATA ACCESS KATMANI — Repository arayüzleri
│
├── business/                  # BUSINESS KATMANI — İş mantığı
│   ├── abstracts/             #   Servis arayüzleri (interface)
│   ├── concretes/             #   Servis implementasyonları
│   └── dtos/                  #   Veri transfer nesneleri
│       ├── requests/{module}/ #     İstek DTO'ları (her modül için alt paket)
│       └── responses/{module}/#     Yanıt DTO'ları (her modül için alt paket)
│
├── api/                       # API KATMANI — REST Controller sınıfları
│
└── Etradedemo7Application.java
```

### Katman Sorumlulukları

| Katman         | Paket                              | Sorumluluk                                      |
| -------------- | ---------------------------------- | ----------------------------------------------- |
| **Entities**   | `entities`                         | Veritabanı tablolarını temsil eden JPA Entity'leri |
| **DataAccess** | `dataAccess`                       | Spring Data JPA Repository arayüzleri            |
| **Business**   | `business.abstracts` / `concretes` | İş kuralları, validasyonlar, DTO dönüşümleri     |
| **API**        | `api`                              | REST endpoint'leri, HTTP request/response yönetimi |

### Katman Bağımlılık Kuralları

```
API  →  Business  →  DataAccess  →  Entities
```

- **API** yalnızca **Business** katmanını çağırır.
- **Business** yalnızca **DataAccess** katmanını çağırır.
- **DataAccess** yalnızca **Entities** katmanını kullanır.
- Katmanlar arası ters bağımlılık **yasaktır** (örn: Entity → Service çağrısı yapılamaz).

---

## 4. Adlandırma Kuralları

### 4.1 Genel
- **Dil:** Sınıf adları, metot adları ve değişkenler **İngilizce** yazılır.
- **Package isimleri:** Küçük harf, tekil → `entity`, `repository`, `service`, `controller`

### 4.2 Sınıf İsimlendirme

| Katman         | Şablon                          | Örnek                        |
| -------------- | ------------------------------- | ---------------------------- |
| Entity         | `{Ad}`                          | `Product`, `Category`        |
| Repository     | `{Ad}Repository`                | `ProductRepository`          |
| Service (abs)  | `{Ad}Service`                   | `ProductService`             |
| Service (impl) | `{Ad}ServiceImpl`               | `ProductServiceImpl`         |
| Controller     | `{Ad}sController` (çoğul)       | `ProductsController`         |
| Request DTO    | `Create{Ad}Request`, `Update{Ad}Request` | `CreateProductRequest` |
| Response DTO   | `Get{Ad}Response`, `GetAll{Ad}sResponse` | `GetProductResponse`  |

### 4.3 REST Endpoint İsimlendirme
- Base path: `/api/` + çoğul isim → `/api/products`, `/api/categories`
- Küçük harf, kebab-case (gerekli ise): `/api/order-items`

---

## 5. Entity Kuralları

- Tüm entity'ler `BaseEntity` sınıfından **miras alır** (`extends BaseEntity`).
- `BaseEntity` (`@MappedSuperclass`) ortak alanları barındırır:
  - `id` (int, `@Id`, `@GeneratedValue(IDENTITY)`)
  - `createdDate` (LocalDateTime)
  - `updatedDate` (LocalDateTime)
  - `deletedDate` (LocalDateTime)
  - `isActive` (boolean)
- Alt entity'ler `@Entity`, `@Table`, `@Data`, `@EqualsAndHashCode(callSuper = true)`, `@NoArgsConstructor`, `@AllArgsConstructor` anotasyonlarını kullanır.
- Alan adları camelCase, tablo kolon adları için `@Column(name = "snake_case")` kullanılır.
- İlişkiler (`@OneToMany`, `@ManyToOne`, vb.) lazy fetch varsayılan olarak tercih edilir.

---

## 6. Repository Kuralları

- `JpaRepository<Entity, IdType>` extend edilir.
- Özel sorgular için Spring Data derived query method veya `@Query` anotasyonu kullanılır.
- Repository arayüzüne iş mantığı eklenmez.

---

## 7. Service Kuralları

- Her servisin bir **interface** (`abstracts/`) ve bir **implementasyon** (`concretes/`) sınıfı olur.
- İmplementasyon sınıfı `@Service` anotasyonu ile işaretlenir.
- Constructor injection kullanılır (`@RequiredArgsConstructor` ile Lombok desteği).
- Servis katmanı **DTO** alır, **DTO** döner. Entity dışarıya sızdırılmaz.
- İş kuralları (business rules) servis katmanında uygulanır.

---

## 8. Controller Kuralları

- `@RestController` ve `@RequestMapping("/api/{resource}")` kullanılır.
- HTTP metotları:
  - `GET` — Listeleme ve detay
  - `POST` — Oluşturma
  - `PUT` — Güncelleme
  - `DELETE` — Silme
- Request body için `@Valid` anotasyonu ile validasyon tetiklenir.
- Controller'da iş mantığı bulunmaz; servis çağrısı yapılır ve sonuç döndürülür.

---

## 9. DTO Kuralları (İleri Seviye Request/Response Pattern)

- Entity doğrudan API'ye açılmaz; **her operasyon için ayrı** Request ve Response DTO'su kullanılır.
- DTO'lar `business/dtos/` altında modül bazlı alt paketlerde tutulur.
- Validasyon anotasyonları (`@NotBlank`, `@NotNull`, `@Min`, `@Max`, `@Size` vb.) request DTO'larına eklenir.

### DTO Konumları
```
business/dtos/
├── requests/{module}/         # CreateXxxRequest, UpdateXxxRequest
└── responses/{module}/        # GetXxxResponse, GetAllXxxsResponse,
                               # CreatedXxxResponse, UpdatedXxxResponse
```

### DTO Adlandırma Tablosu

| Operasyon     | Request DTO              | Response DTO               |
| ------------- | ------------------------ | -------------------------- |
| **Oluşturma** | `Create{Ad}Request`      | `Created{Ad}Response`      |
| **Güncelleme**| `Update{Ad}Request`      | `Updated{Ad}Response`      |
| **Listeleme** | —                        | `GetAll{Ad}sResponse`      |
| **Detay**     | —                        | `Get{Ad}Response`          |
| **Silme**     | —                        | —                          |

### DTO İçerik Farkları
- **GetAllXxxsResponse:** Özet bilgi (listeleme için gerekli minimum alanlar)
- **GetXxxResponse:** Detaylı bilgi (tüm alanlar + audit alanları)
- **CreatedXxxResponse:** Oluşturulan kaydın bilgileri + `createdDate`
- **UpdatedXxxResponse:** Güncellenen kaydın bilgileri + `updatedDate`

---

## 10. Exception Handling

- Özel iş kuralı ihlalleri için `BusinessException` sınıfı kullanılır.
- Global exception handling `@RestControllerAdvice` ile yapılır.
- Hata yanıtı standart bir format izler:
  ```json
  {
    "type": "BUSINESS_ERROR | VALIDATION_ERROR",
    "message": "Açıklama",
    "details": { }
  }
  ```

---

## 11. Validation Kuralları

- Jakarta Bean Validation (`spring-boot-starter-validation`) kullanılır.
- Validasyon hataları `@RestControllerAdvice` üzerinden yakalanır ve standart hata formatında döndürülür.
- Her request DTO'sunda ilgili alanlar için uygun validasyon anotasyonları tanımlanır.

---

## 12. Lombok Kullanımı

- `@Data` — Entity ve DTO'larda getter/setter/toString/equals/hashCode
- `@NoArgsConstructor`, `@AllArgsConstructor` — Entity'lerde
- `@RequiredArgsConstructor` — Service ve Controller'larda constructor injection
- `@Builder` — Gerekli durumlarda builder pattern

---

## 13. Konfigürasyon

- Konfigürasyon dosyası: `application.yaml` (properties değil, yaml tercih edilir)
- H2 Console geliştirme ortamında aktif tutulur.
- JPA ayarları:
  - `ddl-auto: update` (geliştirme)
  - `show-sql: true` (geliştirme)

---

## 14. Kod Yazım Standartları

- **Tek sorumluluk prensibi:** Her sınıf tek bir sorumluluk taşır.
- **Magic number/string kullanılmaz:** Sabit değerler const olarak tanımlanır.
- **Null döndürülmez:** Bulunamayan kayıtlar için exception fırlatılır.
- **Yorum satırları:** Gerekmedikçe yorum eklenmez; kod kendini açıklar.
- **Metot uzunluğu:** Bir metot 20-25 satırı geçmemelidir.
- **Import:** Wildcard import (`*`) kullanılmaz.

---

## 15. API Yanıt Standartları

- Başarılı listeleme: `200 OK` + `List<ResponseDTO>`
- Başarılı tekil getirme: `200 OK` + `ResponseDTO`
- Başarılı oluşturma: `201 Created` (veya `200 OK`)
- Başarılı güncelleme: `200 OK`
- Başarılı silme: `200 OK` veya `204 No Content`
- Hata: Uygun HTTP status code + standart hata yanıtı

---

## 16. Geliştirme Adımları (Her Yeni Modül İçin)

1. **Entity** oluştur
2. **Repository** oluştur
3. **Request/Response DTO**'ları oluştur
4. **Service interface** oluştur
5. **Service implementasyonu** oluştur
6. **Controller** oluştur
7. `application.yaml` ayarlarını güncelle (gerekirse)

---

> ⚠️ **Not:** Bu standartlar proje boyunca tutarlılığı sağlamak için oluşturulmuştur.
> Her yeni geliştirmede bu dosya referans alınmalıdır.
