# Distributed Trading System

Run the application:

http://localhost:8081/login

mvn spring-boot:run



## Define the System Architecture
Microservices Architecture: Split the application into microservices, such as Order Processing, User Management, and Stock Management services. Each microservice will handle specific tasks independently.
Concurrency Management: Utilize Java’s Concurrency API, particularly thread pools, to manage multiple user transactions concurrently.

## Develop the Core Functionality

#### Microservices Architecture
**Order Processing Microservice:**
1. Implement the order processing logic where multiple users can buy or sell stocks concurrently. - TODO
2. Use ExecutorService to manage a pool of threads that handle these transactions. - TODO
3. Implement synchronized threads to safely update shared resources like stock availability. - TODO
4. Utilize thread pools to execute multiple stock trade requests in parallel, ensuring minimal latency. - TODO

Solution:
1. *synchronized blocks* ensure that shared resources like stock availability and prices are safely updated by one thread at a time.
2. *ExecutorService* is introduced for concurrent handling of stock transactions.


*REST Endpoints:*
* GET /orders: To show the stocks help by the user.
* POST /orders/sell: To place a sell order.
* GET /orders/{userId}: To get number of stocks held by the user.
* GET /{userID}/history: To get the user's order history


**Stock Management Microservice:**
1. Maintain real-time stock price data and update stock availability. - ToDo
2. Handle concurrent access to stock data using synchronization mechanisms.  - TODO
3. Use in-memory caching (e.g., Redis) to reduce load on the database and provide faster access to frequently requested stock data. - TODO

Solutions:
1. *Thread Safety:* The synchronized block ensures that only one thread can update the stock's price and quantity at a time.


*REST Endpoints:*

* GET /stocks: Fetch the list of all available stocks.
* PUT /stocks/{stockId}: Update stock data when an order is processed. - TODO
* POST /stocks/buy: To place a buy order.


**User Management Microservice:**
1. Handle user registration, authentication, and session management. - DONE
2. Ensure thread safety while accessing and updating user data. Using synchronized blocks or concurrent data structures. - TODO

*REST Endpoints:*

* GET/POST /register: For user registration.
* GET/POST /login: For user login and session management.
* GET /user: To fetch user profile data.
* GET /logout: Logout of current session



#### Concurrency Management:

1. Use **Java’s Concurrency API** with thread pools for efficient handling of multiple stock orders simultaneously.
2. Use **atomic variables for thread-safe operations**, especially when updating stock prices or executing trades.

#### Data Consistency:

1. For distributed systems, use a **distributed database, (MySQL)**,  to manage stock and user data across multiple instances of the microservices. - DONE
2. Ensure data consistency with **synchronization mechanisms** and transactions where necessary. - TODO


## Integrate Spring Boot for UI and REST APIs

**Spring Boot REST Controllers:**
1. Expose REST APIs for each microservice, enabling clients to interact with the system. - DONE
2. Implement endpoints to buy, sell, and check the status of stocks. - DONE

**Spring Boot-powered UI:**
1. Develop a dynamic front-end using Thymeleaf (or any preferred front-end framework) integrated with Spring Boot. - DONE
2. Display real-time metrics such as execution time, active threads, and the number of processed transactions. - TODO

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


### Total lines of code:

946