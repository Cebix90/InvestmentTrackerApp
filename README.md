# InvestmentTrackerApp

**InvestmentTrackerApp** is an application designed to help users track their investment portfolio. It allows users to add and update stock information, monitor the current portfolio value, and review historical performance. Users can also manage their account details, such as email and password.

To integrate with external APIs for real-time stock prices, you can replace the API key. Locate the `API_KEY` in the `APIConstants` class (`com.cebix.investmenttrackerapp.constants.APIConstants`) and update it with your own key:

```java
public class APIConstants {
    public static final String API_KEY = "YOUR_API_KEY_HERE";
}
```
Note: The basic version of the app (without a premium account) supports a maximum of 5 requests per minute.