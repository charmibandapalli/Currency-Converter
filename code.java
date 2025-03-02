import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;

public class CurrencyConverter {

    private static final String API_URL = "https://api.exchangerate-api.com/v4/latest/";

    public static double getExchangeRate(String fromCurrency, String toCurrency) throws IOException {
        String urlString = API_URL + fromCurrency;
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = reader.readLine()) != null) {
                response.append(inputLine);
            }
            reader.close();

            JSONObject json = new JSONObject(response.toString());
            JSONObject rates = json.getJSONObject("rates");
            return rates.getDouble(toCurrency);
        } else {
            throw new IOException("Failed to get exchange rate. Response code: " + responseCode);
        }
    }

    public static double convertCurrency(double amount, String fromCurrency, String toCurrency) throws IOException {
        double exchangeRate = getExchangeRate(fromCurrency, toCurrency);
        return amount * exchangeRate;
    }

    public static void main(String[] args) {
        try {
            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));

            System.out.print("Enter amount: ");
            double amount = Double.parseDouble(consoleReader.readLine());

            System.out.print("Enter from currency (e.g., USD): ");
            String fromCurrency = consoleReader.readLine().toUpperCase();

            System.out.print("Enter to currency (e.g., EUR): ");
            String toCurrency = consoleReader.readLine().toUpperCase();

            double convertedAmount = convertCurrency(amount, fromCurrency, toCurrency);
            System.out.printf("%.2f %s = %.2f %s%n", amount, fromCurrency, convertedAmount, toCurrency);

        } catch (IOException | NumberFormatException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
