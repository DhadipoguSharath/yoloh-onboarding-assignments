
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class RestApiClient {

    private static final String BASE_URL =
            "https://jsonplaceholder.typicode.com/users";

    private static final HttpClient client = HttpClient.newHttpClient();

    private static void sendRequest(String method, String url, String json)
            throws Exception {

        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json");

        switch (method) {
            case "GET":
                builder.GET();
                break;
            case "POST":
                builder.POST(HttpRequest.BodyPublishers.ofString(json));
                break;
            case "PUT":
                builder.PUT(HttpRequest.BodyPublishers.ofString(json));
                break;
            case "DELETE":
                builder.DELETE();
                break;
            default:
                throw new IllegalArgumentException("Unsupported method: " + method);
        }

        System.out.println("\nSending " + method + " request to " + url);

        HttpResponse<String> response = client.send(
                builder.build(),
                HttpResponse.BodyHandlers.ofString());

        int status = response.statusCode();

        System.out.println("Status Code: " + status);
        System.out.println(status >= 200 && status < 300
                ? "Result: Success"
                : "Result: Error");

        System.out.println("Response Body: " + response.body());
    }

    public static void main(String[] args) {
        String json = """
                {
                    "name": "Sharath",
                    "email": "sharath@example.com"
                }
                """;

        try {
            sendRequest("GET", BASE_URL + "/1", "");
            sendRequest("POST", BASE_URL, json);
            sendRequest("PUT", BASE_URL + "/1", json);
            sendRequest("DELETE", BASE_URL + "/1", "");
            sendRequest("GET", BASE_URL + "/999999", "");

        } catch (Exception e) {
            System.err.println("API request failed: " + e.getMessage());
        }
    }
}