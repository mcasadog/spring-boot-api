# Spring Boot API Project

A comprehensive RESTful API built with Java and Spring Boot, featuring JWT authentication, role-based authorization, and complete CRUD operations.

## Features

- **Modern Architecture**: Built with Spring Boot 3.2.4 and Java 17
- **RESTful API Design**: Follows REST principles with proper HTTP methods and status codes
- **Security**: JWT-based authentication and role-based authorization
- **Database Integration**: PostgreSQL with JPA/Hibernate and Flyway migrations
- **Documentation**: API documentation with Springdoc OpenAPI (Swagger)
- **Testing**: Comprehensive unit and integration tests
- **Exception Handling**: Global exception handling with standardized error responses
- **CI/CD**: GitHub Actions workflow for continuous integration

## Project Structure

```
src/
├── main/
│   ├── java/com/example/api/
│   │   ├── config/         # Configuration classes
│   │   ├── controller/     # REST controllers
│   │   ├── dto/            # Data Transfer Objects
│   │   ├── exception/      # Exception handling
│   │   ├── model/          # Entity models
│   │   ├── repository/     # Data repositories
│   │   ├── security/       # Security configuration
│   │   ├── service/        # Business logic
│   │   └── SpringBootApiApplication.java
│   └── resources/
│       ├── db/migration/   # Flyway database migrations
│       └── application.yml # Application configuration
└── test/
    └── java/com/example/api/
        ├── controller/     # Controller tests
        ├── integration/    # Integration tests
        ├── security/       # Security tests
        └── service/        # Service tests
```

## API Endpoints

### Authentication

- `POST /api/auth/login` - Authenticate user and get JWT token
- `POST /api/auth/register` - Register a new user

### Users

- `GET /api/users` - Get all users (Admin only)
- `GET /api/users/{id}` - Get user by ID (Admin or self)
- `POST /api/users` - Create a new user
- `PUT /api/users/{id}` - Update user (Admin or self)
- `DELETE /api/users/{id}` - Delete user (Admin only)
- `POST /api/users/{username}/roles/{roleName}` - Add role to user (Admin only)
- `DELETE /api/users/{username}/roles/{roleName}` - Remove role from user (Admin only)

### Products

- `GET /api/products` - Get all products (paginated)
- `GET /api/products/active` - Get all active products (paginated)
- `GET /api/products/{id}` - Get product by ID
- `GET /api/products/category/{category}` - Get products by category
- `GET /api/products/search?name={name}` - Search products by name
- `GET /api/products/price-range?minPrice={min}&maxPrice={max}` - Get products by price range
- `POST /api/products` - Create a new product (Admin only)
- `PUT /api/products/{id}` - Update product (Admin only)
- `DELETE /api/products/{id}` - Delete product (Admin only)
- `PATCH /api/products/{id}/activate` - Activate product (Admin only)
- `PATCH /api/products/{id}/deactivate` - Deactivate product (Admin only)

### Orders

- `GET /api/orders` - Get all orders (Admin only)
- `GET /api/orders/paged` - Get all orders paginated (Admin only)
- `GET /api/orders/{id}` - Get order by ID (Admin or owner)
- `GET /api/orders/user/{userId}` - Get orders by user ID (Admin or self)
- `GET /api/orders/status/{status}` - Get orders by status (Admin only)
- `GET /api/orders/date-range` - Get orders by date range (Admin only)
- `POST /api/orders` - Create a new order (Authenticated users)
- `PUT /api/orders/{id}` - Update order (Admin only)
- `PATCH /api/orders/{id}/status` - Update order status (Admin only)
- `DELETE /api/orders/{id}` - Delete order (Admin only)

### Order Items

- `GET /api/order-items` - Get all order items (Admin only)
- `GET /api/order-items/{id}` - Get order item by ID (Admin or owner)
- `GET /api/order-items/order/{orderId}` - Get order items by order ID (Admin or owner)
- `GET /api/order-items/product/{productId}` - Get order items by product ID (Admin only)
- `POST /api/order-items` - Create a new order item (Admin only)
- `PUT /api/order-items/{id}` - Update order item (Admin only)
- `DELETE /api/order-items/{id}` - Delete order item (Admin only)

## Getting Started

See [DEPLOYMENT.md](DEPLOYMENT.md) for detailed setup and deployment instructions.

### Quick Start

1. Clone the repository
2. Configure PostgreSQL database
3. Build the application: `mvn clean install`
4. Run the application: `mvn spring-boot:run`
5. Access Swagger UI: http://localhost:8080/api/swagger-ui.html

## Technologies Used

- **Spring Boot**: Core framework
- **Spring Security**: Authentication and authorization
- **Spring Data JPA**: Data access layer
- **Hibernate Validator**: Data validation
- **PostgreSQL**: Database
- **Flyway**: Database migrations
- **JWT**: JSON Web Token for authentication
- **Lombok**: Reduces boilerplate code
- **Springdoc OpenAPI**: API documentation
- **JUnit 5**: Testing framework
- **Mockito**: Mocking for tests
- **GitHub Actions**: CI/CD pipeline

## License

This project is licensed under the MIT License - see the LICENSE file for details.
