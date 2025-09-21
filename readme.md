# Product Management API

This is a Java Spring Boot application for managing products, backed by a PostgreSQL database. It provides RESTful endpoints for creating, retrieving, searching, updating, and deleting products, as well as generating summaries. The application is containerized using Docker Compose.

## Prerequisites
- Java 21 (for development)
- Gradle
- Docker and Docker Compose installed

## How to Build and Run the Project
1. Clone the repository:
   ```
   git clone https://github.com/nemanja9/demo
   cd demo
   ```

2. Build the Spring Boot JAR:
   ```
   ./gradlew bootJar
   ```

3. Build and start the containers:
   ```
   docker compose up --build
   ```
    - This builds the Java app image and starts both the app (on http://localhost:8080) and PostgreSQL (on localhost:5432).
    - To run in detached mode: `docker compose up -d --build`
    - To stop: `docker compose down`

4. Access the app: Once running, the API is available at http://localhost:8080.

## API Endpoints
All endpoints are under `/api/v1/products`. Authentication is not implemented in this demo application.

**Swagger Documentation:** Access at http://localhost:8080/swagger-ui.html for interactive API docs.
### 1. Create a New Product
- **Method**: POST
- **URL**: `/api/v1/products`
- **Description**: Creates a new product.
- **Request Body**: JSON (ProductCreateDto - adjust fields based on your DTO, e.g., name, price, quantity)
- **Example Curl**:
  ```
  curl -X POST http://localhost:8080/api/v1/products \
    -H 'Content-Type: application/json' \
    -d '{
      "name": "Sample Product",
      "price": 19.99,
      "quantity": 100
    }'
  ```
- **Expected Response**: 201 Created, with Location header (e.g., `/api/v1/products/1`) and product details in body.
- **In Postman**:
    - Method: POST
    - URL: http://localhost:8080/api/v1/products
    - Body: Raw JSON (as above)

### 2. Get All Products (Paginated)
- **Method**: GET
- **URL**: `/api/v1/products`
- **Description**: Retrieves a paginated list of all products.
- **Query Params**: `page` (default 0), `size` (default 20), `sort` (e.g., `name,asc`)
- **Example Curl**:
  ```
  curl -X GET 'http://localhost:8080/api/v1/products?page=0&size=10&sort=name,asc'
  ```
- **Expected Response**: 200 OK, with paginated JSON response.
- **In Postman**:
    - Method: GET
    - URL: http://localhost:8080/api/v1/products
    - Params: Add key-value pairs for page, size, sort.

### 3. Search Products by Query
- **Method**: GET
- **URL**: `/api/v1/products/search`
- **Description**: Searches products based on a query string.
- **Query Params**: `query` (required, e.g., product name or keyword)
- **Example Curl**:
  ```
  curl -X GET 'http://localhost:8080/api/v1/products/search?query=sample'
  ```
- **Expected Response**: 200 OK, with list of matching products.
- **In Postman**:
    - Method: GET
    - URL: http://localhost:8080/api/v1/products/search
    - Params: query = sample

### 4. Update Product Quantity
- **Method**: PUT
- **URL**: `/api/v1/products/{id}/quantity`
- **Description**: Updates the quantity of a specific product.
- **Path Param**: `id` (product ID)
- **Query Params**: `quantity` (new quantity value)
- **Example Curl**:
  ```
  curl -X PUT 'http://localhost:8080/api/v1/products/1/quantity?quantity=50'
  ```
- **Expected Response**: 200 OK, with updated product details.
- **In Postman**:
    - Method: PUT
    - URL: http://localhost:8080/api/v1/products/1/quantity
    - Params: quantity = 50

### 5. Delete a Product
- **Method**: DELETE
- **URL**: `/api/v1/products/{id}`
- **Description**: Deletes a product by ID.
- **Path Param**: `id` (product ID)
- **Example Curl**:
  ```
  curl -X DELETE http://localhost:8080/api/v1/products/1
  ```
- **Expected Response**: 204 No Content.
- **In Postman**:
    - Method: DELETE
    - URL: http://localhost:8080/api/v1/products/1

### 6. Get Product Summary
- **Method**: GET
- **URL**: `/api/v1/products/summary`
- **Description**: Retrieves a summary of products (e.g., statistics, totals).
- **Example Curl**:
  ```
  curl -X GET http://localhost:8080/api/v1/products/summary
  ```
- **Expected Response**: 200 OK, with summary JSON.
- **In Postman**:
    - Method: GET
    - URL: http://localhost:8080/api/v1/products/summary

## Troubleshooting
- Check logs: `docker compose logs app` or `docker compose logs postgres`
- If the app fails to connect to DB, ensure the environment variables in `docker-compose.yml` match with `application.properties`.
- For validation errors (e.g., 400 Bad Request), check DTO constraints.