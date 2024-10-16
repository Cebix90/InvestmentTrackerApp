# Investment Tracker App

**InvestmentTrackerApp** is a comprehensive tool designed to assist users in tracking and managing their investment portfolios. With this app, users can add and update their stock holdings, monitor the value of their portfolio in real-time, and review historical performance data for more informed investment decisions.

## Key Features

- **Portfolio Management**: Easily add and update stock information to reflect your real-time investments.
- **Real-Time Stock Data**: Integrated with external APIs to fetch real-time stock prices for up-to-date portfolio valuation.
- **Historical Performance Tracking**: Analyze your portfolio's performance over time with historical stock data.
- **Account Management**: Manage personal account details, including email and password, securely.
- **Data Security**: All user data, including passwords, are securely encrypted using Spring Security’s BCrypt.
- **User-Friendly Interface**: The app provides a simple and intuitive interface built using Thymeleaf, offering a smooth user experience.

## Technologies Used

- **Java 17**: Core programming language.
- **Spring Boot**: Framework for building backend services.
- **Hibernate (JPA)**: Manages data persistence for portfolio and user accounts.
- **PostgreSQL**: Database for securely storing user data and portfolio information.
- **Thymeleaf**: Templating engine used to render dynamic web pages.
- **WebClient (Spring WebFlux)**: Handles external API requests for real-time stock data.
- **JUnit**: For unit testing and ensuring the reliability of the application's components.

## Setup Instructions

### Prerequisites

- **Java 17** or higher
- **PostgreSQL** installed locally or on a server
- **Gradle** build tool

### Database Setup

1. Set up a PostgreSQL database. Use the following credentials in your `application.properties` file or customize them:

    ```properties
    spring.datasource.url=jdbc:postgresql://localhost:5432/investmenttracker
    spring.datasource.username=postgres
    spring.datasource.password=your_password
    spring.jpa.hibernate.ddl-auto=update
    ```

2. Ensure that the necessary database tables are created automatically by Hibernate based on the application's data model.

### Running the Application

1. Clone the repository:

    ```bash
    git clone https://github.com/yourusername/InvestmentTrackerApp.git
    cd InvestmentTrackerApp
    ```

2. Build and run the project using Gradle:

    ```bash
    ./gradlew bootRun
    ```

3. Open the application in your browser at `http://localhost:8080`.

## API Integration

To integrate with external APIs for real-time stock prices, update the API key in the `APIConstants` class:

```java
public class APIConstants {
    public static final String API_KEY = "YOUR_API_KEY_HERE";
}
```
Note: The basic version of the app (without a premium account) supports a maximum of 5 requests per minute.
