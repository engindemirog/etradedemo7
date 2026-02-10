# 📄 etradedemo7 — API Gereksinim Dökümanı

> **Versiyon:** 1.0  
> **Tarih:** 10 Şubat 2026  
> **Backend:** Spring Boot 4.0.2 — Java 17  
> **Base URL:** `http://localhost:8080`

---

## 📑 İçindekiler

1. [Genel Bilgiler](#1-genel-bilgiler)
2. [Veri Modeli & İlişkiler](#2-veri-modeli--ilişkiler)
3. [Hata Yönetimi (Error Handling)](#3-hata-yönetimi-error-handling)
4. [Category API](#4-category-api)
5. [Product API](#5-product-api)
6. [İş Kuralları (Business Rules)](#6-iş-kuralları-business-rules)
7. [Validasyon Kuralları](#7-validasyon-kuralları)
8. [React Entegrasyon Notları](#8-react-entegrasyon-notları)

---

## 1. Genel Bilgiler

| Özellik | Değer |
|---|---|
| **Base URL** | `http://localhost:8080` |
| **Content-Type** | `application/json` |
| **Swagger UI** | `http://localhost:8080/swagger-ui.html` |
| **OpenAPI JSON** | `http://localhost:8080/api-docs` |
| **Tarih Formatı** | ISO 8601 — `yyyy-MM-ddTHH:mm:ss.SSSSSSS` |
| **ID Tipi** | `integer` (auto-increment) |
| **Silme Stratejisi** | Soft Delete (`isActive = false`, `deletedDate` set edilir) |

---

## 2. Veri Modeli & İlişkiler

### 2.1 Category

| Alan | Tip | Açıklama |
|---|---|---|
| `id` | `integer` | Primary key (auto-increment) |
| `name` | `string` | Kategori adı |
| `description` | `string` | Kategori açıklaması |
| `createdDate` | `datetime` | Oluşturulma tarihi |
| `updatedDate` | `datetime` | Güncellenme tarihi |
| `deletedDate` | `datetime` | Silinme tarihi (soft delete) |
| `isActive` | `boolean` | Aktiflik durumu |

### 2.2 Product

| Alan | Tip | Açıklama |
|---|---|---|
| `id` | `integer` | Primary key (auto-increment) |
| `name` | `string` | Ürün adı |
| `description` | `string` | Ürün açıklaması |
| `unitPrice` | `decimal` | Birim fiyat |
| `stockQuantity` | `integer` | Stok miktarı |
| `categoryId` | `integer` | Bağlı olduğu kategori (FK) |
| `createdDate` | `datetime` | Oluşturulma tarihi |
| `updatedDate` | `datetime` | Güncellenme tarihi |
| `deletedDate` | `datetime` | Silinme tarihi (soft delete) |
| `isActive` | `boolean` | Aktiflik durumu |

### 2.3 İlişki

```
Category (1) ──────── (N) Product
```

- Bir kategorinin **birden fazla** ürünü olabilir.
- Bir ürün **bir** kategoriye aittir.
- Ürün oluşturma/güncelleme sırasında `categoryId` **zorunludur**.

---

## 3. Hata Yönetimi (Error Handling)

API iki tip hata yanıtı döner. Her iki tip de **HTTP 400 Bad Request** döner.

### 3.1 İş Kuralı Hatası (Business Error)

Bir iş kuralı ihlal edildiğinde döner.

```json
{
  "type": "BUSINESS_ERROR",
  "message": "Product already exists with name: iPhone 15"
}
```

**Response alanları:**

| Alan | Tip | Açıklama |
|---|---|---|
| `type` | `string` | Sabit: `"BUSINESS_ERROR"` |
| `message` | `string` | Hata açıklaması |

### 3.2 Validasyon Hatası (Validation Error)

Request body validasyonu başarısız olduğunda döner.

```json
{
  "type": "VALIDATION_ERROR",
  "message": "Validation failed.",
  "details": {
    "name": "Product name is required.",
    "unitPrice": "Unit price is required.",
    "categoryId": "Category id is required."
  }
}
```

**Response alanları:**

| Alan | Tip | Açıklama |
|---|---|---|
| `type` | `string` | Sabit: `"VALIDATION_ERROR"` |
| `message` | `string` | Sabit: `"Validation failed."` |
| `details` | `object` | `{ "alanAdı": "hataMesajı" }` formatında alan bazlı hatalar |

### 3.3 React'te Hata İşleme Örneği

```javascript
try {
  const response = await axios.post("/api/products", productData);
  // Başarılı
} catch (error) {
  const errorData = error.response.data;

  if (errorData.type === "VALIDATION_ERROR") {
    // Form alanlarına hata mesajlarını göster
    // errorData.details → { name: "...", unitPrice: "..." }
    setFieldErrors(errorData.details);
  } else if (errorData.type === "BUSINESS_ERROR") {
    // Genel hata mesajı göster (toast/alert)
    showToast(errorData.message);
  }
}
```

---

## 4. Category API

**Base Path:** `/api/categories`

### 4.1 Tüm Kategorileri Listele

```
GET /api/categories
```

**Response:** `200 OK`

```json
[
  {
    "id": 1,
    "name": "Elektronik"
  },
  {
    "id": 2,
    "name": "Giyim"
  }
]
```

**Response alanları:**

| Alan | Tip | Açıklama |
|---|---|---|
| `id` | `integer` | Kategori ID |
| `name` | `string` | Kategori adı |

> **Not:** Listeleme endpoint'i özet bilgi döner. Detay için `GET /api/categories/{id}` kullanılmalıdır.

---

### 4.2 Kategori Detayı

```
GET /api/categories/{id}
```

**Path Parametreleri:**

| Parametre | Tip | Zorunlu | Açıklama |
|---|---|---|---|
| `id` | `integer` | Evet | Kategori ID |

**Response:** `200 OK`

```json
{
  "id": 1,
  "name": "Elektronik",
  "description": "Elektronik ürünler kategorisi",
  "createdDate": "2026-02-10T14:30:00.0000000",
  "updatedDate": null,
  "active": true
}
```

**Response alanları:**

| Alan | Tip | Nullable | Açıklama |
|---|---|---|---|
| `id` | `integer` | Hayır | Kategori ID |
| `name` | `string` | Hayır | Kategori adı |
| `description` | `string` | Evet | Kategori açıklaması |
| `createdDate` | `datetime` | Evet | Oluşturulma tarihi |
| `updatedDate` | `datetime` | Evet | Güncellenme tarihi |
| `active` | `boolean` | Hayır | Aktiflik durumu |

**Hata durumları:**

| Durum | HTTP | Mesaj |
|---|---|---|
| ID bulunamadı | 400 | `Category not found with id: {id}` |

---

### 4.3 Kategori Oluştur

```
POST /api/categories
```

**Request Body:**

```json
{
  "name": "Elektronik",
  "description": "Elektronik ürünler kategorisi"
}
```

**Request alanları:**

| Alan | Tip | Zorunlu | Validasyon | Açıklama |
|---|---|---|---|---|
| `name` | `string` | Evet | min: 2, max: 100 karakter, boş bırakılamaz | Kategori adı |
| `description` | `string` | Hayır | max: 500 karakter | Kategori açıklaması |

**Response:** `201 Created`

```json
{
  "id": 1,
  "name": "Elektronik",
  "description": "Elektronik ürünler kategorisi",
  "createdDate": "2026-02-10T14:30:00.0000000"
}
```

**Response alanları:**

| Alan | Tip | Açıklama |
|---|---|---|
| `id` | `integer` | Oluşturulan kategori ID |
| `name` | `string` | Kategori adı |
| `description` | `string` | Kategori açıklaması |
| `createdDate` | `datetime` | Oluşturulma tarihi |

**Hata durumları:**

| Durum | HTTP | Mesaj |
|---|---|---|
| İsim zaten var | 400 | `Category already exists with name: {name}` |
| Validasyon hatası | 400 | `VALIDATION_ERROR` (detaylı) |

---

### 4.4 Kategori Güncelle

```
PUT /api/categories
```

**Request Body:**

```json
{
  "id": 1,
  "name": "Elektronik Cihazlar",
  "description": "Tüm elektronik ürünler"
}
```

**Request alanları:**

| Alan | Tip | Zorunlu | Validasyon | Açıklama |
|---|---|---|---|---|
| `id` | `integer` | Evet | Boş bırakılamaz | Güncellenecek kategori ID |
| `name` | `string` | Evet | min: 2, max: 100 karakter, boş bırakılamaz | Yeni kategori adı |
| `description` | `string` | Hayır | max: 500 karakter | Yeni açıklama |

**Response:** `200 OK`

```json
{
  "id": 1,
  "name": "Elektronik Cihazlar",
  "description": "Tüm elektronik ürünler",
  "updatedDate": "2026-02-10T15:00:00.0000000"
}
```

**Response alanları:**

| Alan | Tip | Açıklama |
|---|---|---|
| `id` | `integer` | Güncellenen kategori ID |
| `name` | `string` | Güncellenmiş kategori adı |
| `description` | `string` | Güncellenmiş açıklama |
| `updatedDate` | `datetime` | Güncellenme tarihi |

**Hata durumları:**

| Durum | HTTP | Mesaj |
|---|---|---|
| ID bulunamadı | 400 | `Category not found with id: {id}` |
| İsim başka bir kayıtta var | 400 | `Category already exists with name: {name}` |
| Validasyon hatası | 400 | `VALIDATION_ERROR` (detaylı) |

> **Not:** Güncelleme sırasında aynı kayda ait mevcut isim korunabilir (sadece başka kayıtlarla çakışma kontrol edilir).

---

### 4.5 Kategori Sil

```
DELETE /api/categories/{id}
```

**Path Parametreleri:**

| Parametre | Tip | Zorunlu | Açıklama |
|---|---|---|---|
| `id` | `integer` | Evet | Silinecek kategori ID |

**Response:** `204 No Content` (body yok)

**Hata durumları:**

| Durum | HTTP | Mesaj |
|---|---|---|
| ID bulunamadı | 400 | `Category not found with id: {id}` |

> **Not:** Silme işlemi **soft delete**'tir. Kayıt veritabanından silinmez; `isActive = false` ve `deletedDate` set edilir.

---

## 5. Product API

**Base Path:** `/api/products`

### 5.1 Tüm Ürünleri Listele

```
GET /api/products
```

**Response:** `200 OK`

```json
[
  {
    "id": 1,
    "name": "iPhone 15",
    "unitPrice": 59999.99,
    "stockQuantity": 150,
    "categoryId": 1,
    "categoryName": "Elektronik"
  },
  {
    "id": 2,
    "name": "Samsung Galaxy S24",
    "unitPrice": 49999.99,
    "stockQuantity": 200,
    "categoryId": 1,
    "categoryName": "Elektronik"
  }
]
```

**Response alanları:**

| Alan | Tip | Açıklama |
|---|---|---|
| `id` | `integer` | Ürün ID |
| `name` | `string` | Ürün adı |
| `unitPrice` | `decimal` | Birim fiyat |
| `stockQuantity` | `integer` | Stok miktarı |
| `categoryId` | `integer` | Bağlı kategori ID |
| `categoryName` | `string` | Bağlı kategori adı |

> **Not:** Listeleme özet bilgi döner. `description`, `createdDate`, `updatedDate` gibi alanlar için detay endpoint'i kullanın.

---

### 5.2 Ürün Detayı

```
GET /api/products/{id}
```

**Path Parametreleri:**

| Parametre | Tip | Zorunlu | Açıklama |
|---|---|---|---|
| `id` | `integer` | Evet | Ürün ID |

**Response:** `200 OK`

```json
{
  "id": 1,
  "name": "iPhone 15",
  "description": "Apple iPhone 15, 128GB, Siyah",
  "unitPrice": 59999.99,
  "stockQuantity": 150,
  "categoryId": 1,
  "categoryName": "Elektronik",
  "createdDate": "2026-02-10T14:30:00.0000000",
  "updatedDate": null,
  "active": true
}
```

**Response alanları:**

| Alan | Tip | Nullable | Açıklama |
|---|---|---|---|
| `id` | `integer` | Hayır | Ürün ID |
| `name` | `string` | Hayır | Ürün adı |
| `description` | `string` | Evet | Ürün açıklaması |
| `unitPrice` | `decimal` | Hayır | Birim fiyat |
| `stockQuantity` | `integer` | Hayır | Stok miktarı |
| `categoryId` | `integer` | Hayır | Bağlı kategori ID |
| `categoryName` | `string` | Hayır | Bağlı kategori adı |
| `createdDate` | `datetime` | Evet | Oluşturulma tarihi |
| `updatedDate` | `datetime` | Evet | Güncellenme tarihi |
| `active` | `boolean` | Hayır | Aktiflik durumu |

**Hata durumları:**

| Durum | HTTP | Mesaj |
|---|---|---|
| ID bulunamadı | 400 | `Product not found with id: {id}` |

---

### 5.3 Ürün Oluştur

```
POST /api/products
```

**Request Body:**

```json
{
  "name": "iPhone 15",
  "description": "Apple iPhone 15, 128GB, Siyah",
  "unitPrice": 59999.99,
  "stockQuantity": 150,
  "categoryId": 1
}
```

**Request alanları:**

| Alan | Tip | Zorunlu | Validasyon | Açıklama |
|---|---|---|---|---|
| `name` | `string` | Evet | min: 2, max: 100 karakter, boş bırakılamaz | Ürün adı |
| `description` | `string` | Hayır | max: 500 karakter | Ürün açıklaması |
| `unitPrice` | `decimal` | Evet | min: 0 | Birim fiyat |
| `stockQuantity` | `integer` | Hayır | min: 0, varsayılan: 0 | Stok miktarı |
| `categoryId` | `integer` | Evet | Geçerli bir category ID olmalı | Bağlı kategori |

**Response:** `201 Created`

```json
{
  "id": 1,
  "name": "iPhone 15",
  "description": "Apple iPhone 15, 128GB, Siyah",
  "unitPrice": 59999.99,
  "stockQuantity": 150,
  "categoryId": 1,
  "createdDate": "2026-02-10T14:30:00.0000000"
}
```

**Response alanları:**

| Alan | Tip | Açıklama |
|---|---|---|
| `id` | `integer` | Oluşturulan ürün ID |
| `name` | `string` | Ürün adı |
| `description` | `string` | Ürün açıklaması |
| `unitPrice` | `decimal` | Birim fiyat |
| `stockQuantity` | `integer` | Stok miktarı |
| `categoryId` | `integer` | Bağlı kategori ID |
| `createdDate` | `datetime` | Oluşturulma tarihi |

**Hata durumları:**

| Durum | HTTP | Mesaj |
|---|---|---|
| Ürün adı zaten var | 400 | `Product already exists with name: {name}` |
| Kategori bulunamadı | 400 | `Category not found with id: {categoryId}` |
| Validasyon hatası | 400 | `VALIDATION_ERROR` (detaylı) |

---

### 5.4 Ürün Güncelle

```
PUT /api/products
```

**Request Body:**

```json
{
  "id": 1,
  "name": "iPhone 15 Pro",
  "description": "Apple iPhone 15 Pro, 256GB, Titanium",
  "unitPrice": 74999.99,
  "stockQuantity": 100,
  "categoryId": 1
}
```

**Request alanları:**

| Alan | Tip | Zorunlu | Validasyon | Açıklama |
|---|---|---|---|---|
| `id` | `integer` | Evet | Boş bırakılamaz | Güncellenecek ürün ID |
| `name` | `string` | Evet | min: 2, max: 100 karakter, boş bırakılamaz | Yeni ürün adı |
| `description` | `string` | Hayır | max: 500 karakter | Yeni açıklama |
| `unitPrice` | `decimal` | Evet | min: 0 | Yeni birim fiyat |
| `stockQuantity` | `integer` | Hayır | min: 0 | Yeni stok miktarı |
| `categoryId` | `integer` | Evet | Geçerli bir category ID olmalı | Yeni kategori |

**Response:** `200 OK`

```json
{
  "id": 1,
  "name": "iPhone 15 Pro",
  "description": "Apple iPhone 15 Pro, 256GB, Titanium",
  "unitPrice": 74999.99,
  "stockQuantity": 100,
  "categoryId": 1,
  "updatedDate": "2026-02-10T15:00:00.0000000"
}
```

**Response alanları:**

| Alan | Tip | Açıklama |
|---|---|---|
| `id` | `integer` | Güncellenen ürün ID |
| `name` | `string` | Güncellenmiş ürün adı |
| `description` | `string` | Güncellenmiş açıklama |
| `unitPrice` | `decimal` | Güncellenmiş birim fiyat |
| `stockQuantity` | `integer` | Güncellenmiş stok miktarı |
| `categoryId` | `integer` | Güncellenmiş kategori ID |
| `updatedDate` | `datetime` | Güncellenme tarihi |

**Hata durumları:**

| Durum | HTTP | Mesaj |
|---|---|---|
| Ürün bulunamadı | 400 | `Product not found with id: {id}` |
| Ürün adı başka kayıtta var | 400 | `Product already exists with name: {name}` |
| Kategori bulunamadı | 400 | `Category not found with id: {categoryId}` |
| Validasyon hatası | 400 | `VALIDATION_ERROR` (detaylı) |

> **Not:** Güncelleme sırasında aynı kaydın mevcut ismi korunabilir.

---

### 5.5 Ürün Sil

```
DELETE /api/products/{id}
```

**Path Parametreleri:**

| Parametre | Tip | Zorunlu | Açıklama |
|---|---|---|---|
| `id` | `integer` | Evet | Silinecek ürün ID |

**Response:** `204 No Content` (body yok)

**Hata durumları:**

| Durum | HTTP | Mesaj |
|---|---|---|
| ID bulunamadı | 400 | `Product not found with id: {id}` |

> **Not:** Soft delete uygulanır.

---

## 6. İş Kuralları (Business Rules)

### 6.1 Category İş Kuralları

| # | Kural | Tetiklendiği İşlem | Hata Mesajı |
|---|---|---|---|
| C1 | Kategori ID veritabanında mevcut olmalı | Detay, Güncelleme, Silme | `Category not found with id: {id}` |
| C2 | Kategori adı benzersiz olmalı (oluşturma) | Oluşturma | `Category already exists with name: {name}` |
| C3 | Kategori adı benzersiz olmalı (güncelleme — kendi kaydı hariç) | Güncelleme | `Category already exists with name: {name}` |

### 6.2 Product İş Kuralları

| # | Kural | Tetiklendiği İşlem | Hata Mesajı |
|---|---|---|---|
| P1 | Ürün ID veritabanında mevcut olmalı | Detay, Güncelleme, Silme | `Product not found with id: {id}` |
| P2 | Ürün adı benzersiz olmalı (oluşturma) | Oluşturma | `Product already exists with name: {name}` |
| P3 | Ürün adı benzersiz olmalı (güncelleme — kendi kaydı hariç) | Güncelleme | `Product already exists with name: {name}` |
| P4 | Atanan kategori veritabanında mevcut olmalı | Oluşturma, Güncelleme | `Category not found with id: {categoryId}` |

---

## 7. Validasyon Kuralları

### 7.1 CreateProductRequest / UpdateProductRequest

| Alan | Kurallar | Hata Mesajı |
|---|---|---|
| `name` | Zorunlu, boş olamaz, 2–100 karakter | `Product name is required.` / `Product name must be between 2 and 100 characters.` |
| `description` | Opsiyonel, max 500 karakter | `Description must be at most 500 characters.` |
| `unitPrice` | Zorunlu, min 0 | `Unit price is required.` / `Unit price must be at least 0.` |
| `stockQuantity` | min 0 | `Stock quantity must be at least 0.` |
| `categoryId` | Zorunlu | `Category id is required.` |
| `id` | Zorunlu (sadece Update) | `Product id is required.` |

### 7.2 CreateCategoryRequest / UpdateCategoryRequest

| Alan | Kurallar | Hata Mesajı |
|---|---|---|
| `name` | Zorunlu, boş olamaz, 2–100 karakter | `Category name is required.` / `Category name must be between 2 and 100 characters.` |
| `description` | Opsiyonel, max 500 karakter | `Description must be at most 500 characters.` |
| `id` | Zorunlu (sadece Update) | `Category id is required.` |

---

## 8. React Entegrasyon Notları

### 8.1 Axios Konfigürasyonu

```javascript
import axios from "axios";

const api = axios.create({
  baseURL: "http://localhost:8080/api",
  headers: {
    "Content-Type": "application/json",
  },
});

export default api;
```

### 8.2 Servis Dosyaları

**categoryService.js**
```javascript
import api from "./api";

const CategoryService = {
  getAll:    ()           => api.get("/categories"),
  getById:   (id)         => api.get(`/categories/${id}`),
  add:       (category)   => api.post("/categories", category),
  update:    (category)   => api.put("/categories", category),
  delete:    (id)         => api.delete(`/categories/${id}`),
};

export default CategoryService;
```

**productService.js**
```javascript
import api from "./api";

const ProductService = {
  getAll:    ()           => api.get("/products"),
  getById:   (id)         => api.get(`/products/${id}`),
  add:       (product)    => api.post("/products", product),
  update:    (product)    => api.put("/products", product),
  delete:    (id)         => api.delete(`/products/${id}`),
};

export default ProductService;
```

### 8.3 TypeScript Interface'leri

```typescript
// ──── Category ────

interface GetAllCategoriesResponse {
  id: number;
  name: string;
}

interface GetCategoryResponse {
  id: number;
  name: string;
  description: string | null;
  createdDate: string | null;
  updatedDate: string | null;
  active: boolean;
}

interface CreateCategoryRequest {
  name: string;
  description?: string;
}

interface UpdateCategoryRequest {
  id: number;
  name: string;
  description?: string;
}

interface CreatedCategoryResponse {
  id: number;
  name: string;
  description: string | null;
  createdDate: string;
}

interface UpdatedCategoryResponse {
  id: number;
  name: string;
  description: string | null;
  updatedDate: string;
}

// ──── Product ────

interface GetAllProductsResponse {
  id: number;
  name: string;
  unitPrice: number;
  stockQuantity: number;
  categoryId: number;
  categoryName: string;
}

interface GetProductResponse {
  id: number;
  name: string;
  description: string | null;
  unitPrice: number;
  stockQuantity: number;
  categoryId: number;
  categoryName: string;
  createdDate: string | null;
  updatedDate: string | null;
  active: boolean;
}

interface CreateProductRequest {
  name: string;
  description?: string;
  unitPrice: number;
  stockQuantity: number;
  categoryId: number;
}

interface UpdateProductRequest {
  id: number;
  name: string;
  description?: string;
  unitPrice: number;
  stockQuantity: number;
  categoryId: number;
}

interface CreatedProductResponse {
  id: number;
  name: string;
  description: string | null;
  unitPrice: number;
  stockQuantity: number;
  categoryId: number;
  createdDate: string;
}

interface UpdatedProductResponse {
  id: number;
  name: string;
  description: string | null;
  unitPrice: number;
  stockQuantity: number;
  categoryId: number;
  updatedDate: string;
}

// ──── Error ────

interface BusinessErrorResponse {
  type: "BUSINESS_ERROR";
  message: string;
}

interface ValidationErrorResponse {
  type: "VALIDATION_ERROR";
  message: string;
  details: Record<string, string>;
}

type ApiErrorResponse = BusinessErrorResponse | ValidationErrorResponse;
```

### 8.4 Form Validasyonu (Frontend Tarafı)

Kullanıcı deneyimini iyileştirmek için backend validasyonlarına ek olarak frontend'de de aynı kurallar uygulanmalıdır:

**Category Formu:**

| Alan | Kural |
|---|---|
| `name` | Required, min 2 – max 100 karakter |
| `description` | Optional, max 500 karakter |

**Product Formu:**

| Alan | Kural |
|---|---|
| `name` | Required, min 2 – max 100 karakter |
| `description` | Optional, max 500 karakter |
| `unitPrice` | Required, min 0 |
| `stockQuantity` | min 0 |
| `categoryId` | Required (dropdown/select ile seçtirilmeli) |

### 8.5 Önerilen Sayfa Yapısı

```
/categories                → Kategori listesi (GET /api/categories)
/categories/new            → Yeni kategori formu (POST /api/categories)
/categories/:id            → Kategori detay (GET /api/categories/{id})
/categories/:id/edit       → Kategori düzenle (PUT /api/categories)

/products                  → Ürün listesi (GET /api/products)
/products/new              → Yeni ürün formu (POST /api/products)
/products/:id              → Ürün detay (GET /api/products/{id})
/products/:id/edit         → Ürün düzenle (PUT /api/products)
```

### 8.6 Önemli Notlar

1. **CORS:** Backend tüm origin'lere açık şekilde yapılandırılmıştır. React dev server'dan doğrudan `http://localhost:8080` adresine istek atılabilir.

2. **Kategori Dropdown:** Ürün oluşturma/güncelleme formunda `categoryId` seçimi için önce `GET /api/categories` çağrılıp kategori listesi dropdown olarak gösterilmelidir.

3. **Güncelleme (PUT):** Güncelleme endpoint'leri `id`'yi URL'de değil, request body içinde alır. `PUT /api/products` şeklinde ID'siz çağrılır.

4. **Silme Sonrası:** `DELETE` çağrısı `204 No Content` döner (body boştur). Silme sonrası listeyi yenilemek için tekrar `GET` çağrısı yapın.

5. **Tarih Formatı:** Backend ISO 8601 formatında tarih döner (`2026-02-10T14:30:00.0000000`). React tarafında `new Date(createdDate)` ile parse edilebilir veya `dayjs` / `date-fns` kullanılabilir.

6. **Decimal Alanlar:** `unitPrice` backend'den `number` olarak gelir. Formda input type `number` ve `step="0.01"` kullanılmalıdır.

---

> **Bu döküman, backend API'nin mevcut durumunu yansıtmaktadır.**  
> Yeni modüller veya değişiklikler eklendikçe güncellenmelidir.
