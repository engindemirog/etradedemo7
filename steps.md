# 🚀 Geliştirici Rehberi — etradedemo7

> Bu doküman, projeye yeni katılan geliştiricilerin hızlıca adapte olması için hazırlanmıştır.
> Adım adım proje yapısını, çalıştırma şeklini ve yeni modül ekleme sürecini anlatır.

---

## 📌 Ön Gereksinimler

| Araç | Minimum Versiyon |
|---|---|
| **Java JDK** | 17 |
| **Maven** | 3.9+ |
| **IDE** | IntelliJ IDEA / VS Code (Spring Boot & Lombok plugin yüklü olmalı) |

> ⚠️ Lombok kullanıldığı için IDE'nizde **Lombok plugin** kurulu ve **annotation processing** aktif olmalıdır.

---

## 1. Projeyi Ayağa Kaldırma

```bash
# Proje kök dizininde terminali açın
./mvnw spring-boot:run
```

Başarılı çalıştıktan sonra erişim noktaları:

| Adres | Açıklama |
|---|---|
| `http://localhost:8080/swagger-ui.html` | Swagger UI — API'yi test et |
| `http://localhost:8080/h2-console` | H2 Veritabanı Konsolu |
| `http://localhost:8080/api-docs` | OpenAPI JSON dokümanı |

### H2 Console Bağlantı Bilgileri

| Alan | Değer |
|---|---|
| JDBC URL | `jdbc:h2:file:./data/etradedemo7db` |
| User Name | `sa` |
| Password | *(boş bırakın)* |

---

## 2. Proje Yapısını Anla

```
com.turkcell.etradedemo7
│
├── entities/                       # 1️⃣ ENTITIES — Veritabanı tabloları
│   ├── BaseEntity.java             #    Ortak alanlar (id, tarihler, isActive)
│   └── Product.java                #    Ürün entity'si
│
├── dataAccess/                     # 2️⃣ DATA ACCESS — Repository arayüzleri
│   └── ProductRepository.java     #    JpaRepository<Product, Integer>
│
├── business/                       # 3️⃣ BUSINESS — İş mantığı
│   ├── abstracts/                  #    Servis interface'leri
│   │   └── ProductService.java
│   ├── concretes/                  #    Servis implementasyonları
│   │   └── ProductServiceImpl.java
│   └── dtos/                       #    Request & Response DTO'ları
│       ├── requests/product/
│       │   ├── CreateProductRequest.java
│       │   └── UpdateProductRequest.java
│       └── responses/product/
│           ├── GetAllProductsResponse.java
│           ├── GetProductResponse.java
│           ├── CreatedProductResponse.java
│           └── UpdatedProductResponse.java
│
├── api/                            # 4️⃣ API — REST Controller'lar
│   └── ProductsController.java
│
├── core/                           # 🔧 CORE — Ortak altyapı
│   └── configuration/
│       └── OpenApiConfig.java      #    Swagger yapılandırması
│
└── Etradedemo7Application.java     # ▶️ Uygulama giriş noktası
```

### Katman Akışı

```
İstek → Controller (API) → Service (Business) → Repository (DataAccess) → Entity → Veritabanı
Yanıt ← Controller (API) ← Service (Business) ← Repository (DataAccess) ← Entity ← Veritabanı
```

> **Kural:** Her katman yalnızca bir alt katmanı çağırır. Katlan atlama yasaktır.

---

## 3. Mevcut API Endpoint'leri

### Product API (`/api/products`)

| HTTP | Endpoint | Açıklama | Request Body | Response |
|---|---|---|---|---|
| `GET` | `/api/products` | Tüm ürünleri listele | — | `List<GetAllProductsResponse>` |
| `GET` | `/api/products/{id}` | ID ile ürün detayı | — | `GetProductResponse` |
| `POST` | `/api/products` | Yeni ürün ekle | `CreateProductRequest` | `CreatedProductResponse` |
| `PUT` | `/api/products` | Ürün güncelle | `UpdateProductRequest` | `UpdatedProductResponse` |
| `DELETE` | `/api/products/{id}` | Ürün sil (soft delete) | — | `204 No Content` |

---

## 4. Yeni Modül Ekleme Adımları (Örn: Category)

Projede yeni bir modül eklerken aşağıdaki sırayı **mutlaka** takip edin.

### 💬 Copilot'a Verilecek Prompt

Yeni bir modül ekletmek istediğinizde aşağıdaki prompt şablonunu kullanın:

```
agent.md ve steps.md dosyalarındaki standartları oku ve uygula.
Product modülünü referans alarak {ModülAdı} modülünü tüm katmanlarıyla oluştur.

Entity alanları:
- {alan1} ({tip}) — {açıklama}
- {alan2} ({tip}) — {açıklama}

İlişkiler:
- {İlişki açıklaması, varsa}

Tüm katmanları kodla: Entity, Repository, DTO'lar (request + response), 
Service (abstracts + concretes), Controller.
```

**Örnek — Category modülü için:**

```
agent.md ve steps.md dosyalarındaki standartları oku ve uygula.
Product modülünü referans alarak Category modülünü tüm katmanlarıyla oluştur.

Entity alanları:
- name (String) — Kategori adı
- description (String) — Kategori açıklaması

İlişkiler:
- Bir kategorinin birden fazla ürünü olabilir (OneToMany → Product)

Tüm katmanları kodla: Entity, Repository, DTO'lar (request + response), 
Service (abstracts + concretes), Controller.
```

---

### Adım 1 — Entity Oluştur

� **Prompt:**
```
agent.md standartlarını oku. BaseEntity'den miras alan Category entity'si oluştur.
Alanları: name (String), description (String).
Tablo adı: categories. Kolon isimleri snake_case olsun.
```

�📁 `entities/Category.java`

```java
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "categories")
public class Category extends BaseEntity {
    // BaseEntity'den id, createdDate, updatedDate, deletedDate, isActive gelir
    
    @Column(name = "name")
    private String name;
}
```

> ✅ `BaseEntity`'den miras al → ortak alanlar otomatik gelir.

---

### Adım 2 — Repository Oluştur

� **Prompt:**
```
agent.md standartlarını oku. Category entity'si için dataAccess katmanında
CategoryRepository oluştur. ProductRepository'yi referans al.
```

�📁 `dataAccess/CategoryRepository.java`

```java
public interface CategoryRepository extends JpaRepository<Category, Integer> {
}
```

> ✅ Temel CRUD metotları (save, findById, findAll, deleteById) otomatik gelir.

---

### Adım 3 — DTO'ları Oluştur

💬 **Prompt:**
```
agent.md standartlarını oku. Category modülü için Product DTO'larını referans alarak
tüm request ve response DTO'larını oluştur:
- CreateCategoryRequest, UpdateCategoryRequest
- GetAllCategoriesResponse, GetCategoryResponse, CreatedCategoryResponse, UpdatedCategoryResponse
Her DTO'da sadece o operasyon için gerekli alanlar bulunsun. Validasyon anotasyonlarını ekle.
```

Her operasyon için **ayrı** DTO oluşturulur:

📁 `business/dtos/requests/category/`
- `CreateCategoryRequest.java` — Validasyon anotasyonları ile
- `UpdateCategoryRequest.java` — id alanı dahil

📁 `business/dtos/responses/category/`
- `GetAllCategoriesResponse.java` — Özet bilgi (listeleme)
- `GetCategoryResponse.java` — Detaylı bilgi
- `CreatedCategoryResponse.java` — Oluşturma sonucu
- `UpdatedCategoryResponse.java` — Güncelleme sonucu

> ✅ Her DTO'da sadece o operasyon için gerekli alanlar bulunur.

---

### Adım 4 — Service Interface Oluştur

� **Prompt:**
```
agent.md standartlarını oku. ProductService'i referans alarak CategoryService
interface'ini business/abstracts altında oluştur. Tüm CRUD metotlarını tanımla.
Her metot ilgili DTO'yu alsın ve dönsün.
```

�📁 `business/abstracts/CategoryService.java`

```java
public interface CategoryService {
    List<GetAllCategoriesResponse> getAll();
    GetCategoryResponse getById(int id);
    CreatedCategoryResponse add(CreateCategoryRequest request);
    UpdatedCategoryResponse update(UpdateCategoryRequest request);
    void delete(int id);
}
```

---

### Adım 5 — Service Implementasyonu Oluştur

� **Prompt:**
```
agent.md standartlarını oku. ProductServiceImpl'i referans alarak CategoryServiceImpl'i
business/concretes altında oluştur. Constructor injection kullan.
DTO↔Entity dönüşümlerini yap. Silme işlemi soft delete olsun.
Bulunamayan kayıtlarda exception fırlat.
```

�📁 `business/concretes/CategoryServiceImpl.java`

```java
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    // metot implementasyonları...
}
```

> ✅ Constructor injection (`@RequiredArgsConstructor`) kullan.
> ✅ DTO alır, DTO döner. Entity dışarıya sızmaz.
> ✅ Silme işlemi soft delete: `isActive = false`, `deletedDate = now()`.

---

### Adım 6 — Controller Oluştur

� **Prompt:**
```
agent.md standartlarını oku. ProductsController'ı referans alarak CategoriesController'ı
api katmanında oluştur. Endpoint: /api/categories.
GET (getAll, getById), POST (add), PUT (update), DELETE (delete) metotlarını yaz.
@Valid ile validasyon tetiklensin. Uygun @ResponseStatus anotasyonlarını ekle.
```

�📁 `api/CategoriesController.java`

```java
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoriesController {
    private final CategoryService categoryService;
    // endpoint metotları...
}
```

> ✅ Controller'da iş mantığı yok — sadece servis çağırır.
> ✅ `@Valid` ile validasyon tetiklenir.

---

## 5. Önemli Kurallar Özeti

| Kural | Açıklama |
|---|---|
| **Entity → BaseEntity** | Her entity `BaseEntity`'den miras alır |
| **Soft Delete** | Kayıtlar fiziksel silinmez; `isActive=false` yapılır |
| **DTO Zorunluluğu** | Entity hiçbir zaman API'ye açılmaz |
| **Constructor Injection** | `@Autowired` yerine `@RequiredArgsConstructor` |
| **Validasyon** | Request DTO'larında Jakarta Validation anotasyonları |
| **İngilizce** | Tüm sınıf, metot ve değişken isimleri İngilizce |
| **Katman Bağımlılığı** | `API → Business → DataAccess → Entities` (ters yasak) |

---

## 6. Sık Kullanılan Komutlar

```bash
# Projeyi derle
./mvnw clean compile

# Projeyi çalıştır
./mvnw spring-boot:run

# Testleri çalıştır
./mvnw test

# Paket oluştur
./mvnw clean package
```

---

## 7. Sorun Giderme

| Problem | Çözüm |
|---|---|
| Lombok metotları bulunamıyor | IDE'de Lombok plugin yükle + Annotation Processing aktif et |
| H2 Console açılmıyor | `application.yaml`'da `h2.console.enabled: true` olduğunu kontrol et |
| Tablo oluşmuyor | `ddl-auto: update` olduğunu doğrula, entity'de `@Entity` anotasyonu var mı kontrol et |
| Port zaten kullanımda | `application.yaml`'da `server.port` değerini değiştir |
| Swagger açılmıyor | `springdoc-openapi` bağımlılığının `pom.xml`'de olduğunu kontrol et |

---

> 📖 Detaylı proje standartları için [agent.md](agent.md) dosyasına bakın.
