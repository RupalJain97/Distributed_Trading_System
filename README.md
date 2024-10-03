# Distributed Trading System

Run the application:

http://localhost:8081/performance.html



## Define the System Architecture
Microservices Architecture: Split the application into microservices, such as Order Processing, User Management, and Stock Management services. Each microservice will handle specific tasks independently.
Concurrency Management: Utilize Java’s Concurrency API, particularly thread pools, to manage multiple user transactions concurrently.

## Develop the Core Functionality

#### Microservices Architecture
**Order Processing Microservice:**
1. Implement the order processing logic where multiple users can buy or sell stocks concurrently.
2. Use ExecutorService to manage a pool of threads that handle these transactions.
3. Implement Atomic variables to safely update shared resources like stock availability.
4. Utilize thread pools to execute multiple stock trade requests in parallel, ensuring minimal latency.

*REST Endpoints:*
* POST /orders/buy: To place a buy order.
* POST /orders/sell: To place a sell order.
* GET /orders/{userId}: To check order status.

**User Management Microservice:**
1. Handle user registration, authentication, and session management.
2. Ensure thread safety while accessing and updating user data. Using synchronized blocks or concurrent data structures.

*REST Endpoints:*

* POST /users/register: For user registration.
* POST /users/login: For user login and session management.
* GET /users/{userId}: To fetch user profile data.

**Stock Management Microservice:**
1. Maintain real-time stock price data and update stock availability.
2. Handle concurrent access to stock data using synchronization mechanisms. 
3. Provide an API for the Order Processing Service to retrieve stock prices and quantities.
4. Use in-memory caching (e.g., Redis) to reduce load on the database and provide faster access to frequently requested stock data.

*REST Endpoints:*

* GET /stocks: Fetch the list of all available stocks.
* GET /stocks/{stockId}: Get details (price, availability) of a specific stock.
* PUT /stocks/{stockId}: Update stock data when an order is processed.


#### Concurrency Management:

1. Use **Java’s Concurrency API** with thread pools for efficient handling of multiple stock orders simultaneously.
2. Use **atomic variables for thread-safe operations**, especially when updating stock prices or executing trades.

#### Data Consistency:

1. For distributed systems, use a **distributed database** (e.g., MongoDB, PostgreSQL) to manage stock and user data across multiple instances of the microservices.
2. Ensure data consistency with **synchronization mechanisms** and transactions where necessary.


## Integrate Spring Boot for UI and REST APIs

**Spring Boot REST Controllers:**
1. Expose REST APIs for each microservice, enabling clients to interact with the system.
2. Implement endpoints to buy, sell, and check the status of stocks.

**Spring Boot-powered UI:**
1. Develop a dynamic front-end using Thymeleaf (or any preferred front-end framework) integrated with Spring Boot.
2. Display real-time metrics such as execution time, active threads, and the number of processed transactions.

## Deploy the Application
**Dockerize** the Application: Create Docker images for each microservice and deploy them using Docker Compose or Kubernetes.
**AWS Deployment:** Use AWS services like ECS (Elastic Container Service) for deploying the containerized application. Implement AWS Lambda functions for serverless components if necessary.


## Application Architecture

#### Controllers:
These act as the entry points for various HTTP requests and communicate with services to perform business logic. 
1. **OrderController:** Handles operations like buying and selling stocks via EST endpoints.
2. **StockController:** Provides information about available stocks, such as current stock prices.
3. **UserController:** Manages user-related operations like login, registration, and retrieving user-specific data.

#### Models:
These represent the entities or data structures that the system manipulates. 
1. **OrderModel:**  Represents the order details, such as the type of transaction (buy/sell), stock symbol, and the quantity of stocks.
2. **StockModel:**  Contains information about individual stocks, such as stock symbol, price, and availability.
3. **UserModel:** Stores user data such as username, password, and account information.

#### Services:
These contain the core business logic. Controllers delegate requests to services to handle the actual work:

1. **OrderService:** Processes buy/sell orders, updating stock availability and ensuring data integrity (using thread-safe operations with Atomic variables and thread pools).
2. **StockService:** Manages real-time updates to stock data and responds to requests for current stock prices.
3. **UserService:** Handles user registration, login, and authentication logic.

#### HTML Templates:
These are frontend files used to render the UI in the browser. The views are served by the Spring Boot controllers and filled with dynamic content:
* login.html: Handles user login.
* register.html: For user registration.
* userDashboard.html: After login, users are redirected to this page, where they can view their profile or other personalized details.
* stock_dashboard.html: Displays the available stocks and their prices, allowing users to buy or sell them.
* orderProcessing.html: This page handles the order placement process (buy/sell stocks).

*Trading System Logic:*
The TradingSystem class likely acts as a utility or central service to coordinate multiple services. It could handle tasks like validating an order, updating user balances, or managing stock data across multiple services.