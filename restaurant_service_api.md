
# API Documentation

This API is designed to manage restaurant services, including table management, product types, products, photos, orders, and order items. All endpoints follow the path prefix `api/v1/`.

---

## Tables

### 1. Get all tables
- **Method:** `GET`
- **Endpoint:** `/api/v1/tables`
- **Description:** Fetch all available tables in the restaurant.
- **Response Example:**
  ```json
  [
    {
      "id": 1,
      "number": 1
    },
    {
      "id": 2,
      "number": 2
    },
    ...
  ]
  ```

### 2. Get a specific table
- **Method:** `GET`
- **Endpoint:** `/api/v1/tables/{id}`
- **Description:** Fetch details for a specific table by its `id`.
- **Path Parameters:**
  - `id`: **ID** of the table.
- **Response Example:**
  ```json
  {
    "id": 1,
    "number": 1
  }
  ```

### 3. Create a table
- **Method:** `POST`
- **Endpoint:** `/api/v1/tables`
- **Description:** Create a new table in the restaurant.
- **Request Body:**
  ```json
  {
    "number": 5
  }
  ```
- **Response Example:**
  ```json
  {
    "id": 5,
    "number": 5
  }
  ```

---

## Product Types

### 1. Get all product types
- **Method:** `GET`
- **Endpoint:** `/api/v1/product_types`
- **Description:** Fetch all available product types.
- **Response Example:**
  ```json
  [
    {
      "id": 1,
      "name": "Напитки"
    },
    {
      "id": 2,
      "name": "Закуски"
    },
    ...
  ]
  ```

### 2. Create a product type
- **Method:** `POST`
- **Endpoint:** `/api/v1/product_types`
- **Description:** Create a new product type.
- **Request Body:**
  ```json
  {
    "name": "Основные блюда"
  }
  ```
- **Response Example:**
  ```json
  {
    "id": 3,
    "name": "Основные блюда"
  }
  ```

---

## Products

### 1. Get all products
- **Method:** `GET`
- **Endpoint:** `/api/v1/products`
- **Description:** Fetch all available products.
- **Response Example:**
  ```json
  [
    {
      "id": 1,
      "name": "Кола",
      "description": "Газированный напиток",
      "type_id": 1,
      "price": 17.50
    },
    {
      "id": 2,
      "name": "Чай",
      "description": "Чёрный или зелёный",
      "type_id": 1,
      "price": 20.00
    },
    ...
  ]
  ```

### 2. Get a specific product
- **Method:** `GET`
- **Endpoint:** `/api/v1/products/{id}`
- **Description:** Fetch details for a specific product by its `id`.
- **Path Parameters:**
  - `id`: **ID** of the product.
- **Response Example:**
  ```json
  {
    "id": 1,
    "name": "Кола",
    "description": "Газированный напиток",
    "type_id": 1,
    "price": 17.50
  }
  ```

### 3. Create a new product
- **Method:** `POST`
- **Endpoint:** `/api/v1/products`
- **Description:** Create a new product in the menu.
- **Request Body:**
  ```json
  {
    "name": "Суп",
    "description": "Куриный суп",
    "type_id": 3,
    "price": 35.00
  }
  ```
- **Response Example:**
  ```json
  {
    "id": 3,
    "name": "Суп",
    "description": "Куриный суп",
    "type_id": 3,
    "price": 35.00
  }
  ```

---

## Photos

### 1. Get product photos
- **Method:** `GET`
- **Endpoint:** `/api/v1/photos`
- **Description:** Fetch all photos for products.
- **Response Example:**
  ```json
  [
    {
      "id": 1,
      "product_id": 1,
      "url": "/static/images/cola.jpeg"
    },
    {
      "id": 2,
      "product_id": 2,
      "url": "/static/images/tea.jpeg"
    },
    ...
  ]
  ```

### 2. Upload a photo for a product
- **Method:** `POST`
- **Endpoint:** `/api/v1/photos`
- **Description:** Upload a new photo for a product.
- **Request Body:**
  ```json
  {
    "product_id": 1,
    "url": "/static/images/cola-new.jpeg"
  }
  ```
- **Response Example:**
  ```json
  {
    "id": 5,
    "product_id": 1,
    "url": "/static/images/cola-new.jpeg"
  }
  ```

---

## Orders

### 1. Get all orders
- **Method:** `GET`
- **Endpoint:** `/api/v1/orders`
- **Description:** Fetch all orders placed in the restaurant.
- **Response Example:**
  ```json
  [
    {
      "id": 1,
      "table_id": 1,
      "status": "PENDING",
      "payment_method": "CARD",
      "total_price": 100.50,
      "created_at": "2024-12-20T10:00:00",
      "updated_at": "2024-12-20T10:15:00"
    },
    ...
  ]
  ```

### 2. Get a specific order
- **Method:** `GET`
- **Endpoint:** `/api/v1/orders/{id}`
- **Description:** Fetch details for a specific order by its `id`.
- **Path Parameters:**
  - `id`: **ID** of the order.
- **Response Example:**
  ```json
  {
    "id": 1,
    "table_id": 1,
    "status": "PENDING",
    "payment_method": "CARD",
    "total_price": 100.50,
    "created_at": "2024-12-20T10:00:00",
    "updated_at": "2024-12-20T10:15:00"
  }
  ```

### 3. Create a new order
- **Method:** `POST`
- **Endpoint:** `/api/v1/orders`
- **Description:** Create a new order.
- **Request Body:**
  ```json
  {
    "table_id": 1,
    "status": "PENDING",
    "payment_method": "CASH",
    "total_price": 150.00
  }
  ```
- **Response Example:**
  ```json
  {
    "id": 3,
    "table_id": 1,
    "status": "PENDING",
    "payment_method": "CASH",
    "total_price": 150.00,
    "created_at": "2024-12-20T11:00:00",
    "updated_at": "2024-12-20T11:15:00"
  }
  ```

---

## Order Products

### 1. Get products in an order
- **Method:** `GET`
- **Endpoint:** `/api/v1/order_products`
- **Description:** Fetch all products added to orders.
- **Response Example:**
  ```json
  [
    {
      "id": 1,
      "order_id": 1,
      "product_id": 1,
      "quantity": 2
    },
    {
      "id": 2,
      "order_id": 1,
      "product_id": 2,
      "quantity": 1
    },
    ...
  ]
  ```

### 2. Add a product to an order
- **Method:** `POST`
- **Endpoint:** `/api/v1/order_products`
- **Description:** Add a product to a specific order.
- **Request Body:**
  ```json
  {
    "order_id": 1,
    "product_id": 1,
    "quantity": 2
  }
  ```
- **Response Example:**
  ```json
  {
    "id": 3,
    "order_id": 1,
    "product_id": 1,
    "quantity": 2
  }
  ```

---

## WebSocket

### 1. Subscribe to order updates
- **Method:** `WS`
- **Endpoint:** `/api/v1/orders/updates`
- **Description:** Subscribe to real-time updates for orders (e.g., status change, new orders).
- **Response Example:** `{"order_id": 1, "status": "COMPLETED"}`

---

### General Notes
- All requests and responses use **JSON** format.
- The API uses **HTTP status codes** to indicate success or failure:
  - `200 OK` for successful requests
  - `201 Created` for successful resource creation
  - `400 Bad Request` for invalid input
  - `404 Not Found` for non-existent resources
  - `500 Internal Server Error` for server issues
