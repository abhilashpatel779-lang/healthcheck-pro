import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

public class HealthCheck {

    // Websites to monitor
    static Map<String, String> websites = new LinkedHashMap<>();

    static {
        websites.put("Google", "https://www.google.com");
        websites.put("GitHub", "https://github.com");
        websites.put("Example", "https://example.com");
        websites.put("Wikipedia", "https://www.wikipedia.org");
        websites.put("Python", "https://www.python.org");
    }

    // Check website
    public static String checkWebsite(String name, String website) {

        long startTime = System.currentTimeMillis();

        try {
            URL url = new URL(website);

            HttpURLConnection connection =
                    (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            int responseCode = connection.getResponseCode();

            long endTime = System.currentTimeMillis();

            long responseTime = endTime - startTime;

            if (responseCode >= 200 && responseCode < 400) {

                System.out.println(
                        name + " UP | " +
                        responseTime + " ms"
                );

                return name + "," +
                       website + ",UP," +
                       responseTime + "," +
                       LocalDateTime.now();

            } else {

                System.out.println(
                        name + " DOWN | " +
                        responseTime + " ms"
                );

                return name + "," +
                       website + ",DOWN," +
                       responseTime + "," +
                       LocalDateTime.now();
            }

        } catch (Exception e) {

            long endTime = System.currentTimeMillis();
            long responseTime = endTime - startTime;

            System.out.println(
                    name + " DOWN | " +
                    responseTime + " ms"
            );

            return name + "," +
                   website + ",DOWN," +
                   responseTime + "," +
                   LocalDateTime.now();
        }
    }

    // Save results to CSV
    public static void saveCSV(StringBuilder data) {

        try {

            FileWriter writer =
                    new FileWriter("monitoring_history.csv");

            writer.write(
                    "Website,URL,Status,Response Time(ms),Timestamp\n"
            );

            writer.write(data.toString());

            writer.close();

            System.out.println(
                    "\nCSV report saved: monitoring_history.csv"
            );

        } catch (IOException e) {

            System.out.println(
                    "Error creating CSV file."
            );
        }
    }

    // Create HTML dashboard
    public static void createHTML(StringBuilder data) {

        try {

            FileWriter writer =
                    new FileWriter("dashboard.html");

            writer.write("""
                    <!DOCTYPE html>
                    <html>
                    <head>
                    <title>HealthCheck Pro</title>

                    <style>
                    body {
                        font-family: Arial;
                        margin: 40px;
                    }

                    h1 {
                        text-align: center;
                    }

                    table {
                        width: 100%;
                        border-collapse: collapse;
                    }

                    th, td {
                        border: 1px solid black;
                        padding: 10px;
                        text-align: center;
                    }

                    th {
                        background-color: black;
                        color: white;
                    }
                    </style>

                    </head>

                    <body>

                    <h1>HealthCheck Pro Dashboard</h1>

                    <table>

                    <tr>
                    <th>Website</th>
                    <th>URL</th>
                    <th>Status</th>
                    <th>Response Time</th>
                    <th>Timestamp</th>
                    </tr>
                    """);

            String[] rows = data.toString().split("\n");

            for (String row : rows) {

                if (row.trim().isEmpty()) {
                    continue;
                }

                String[] values = row.split(",");

                writer.write("<tr>");

                for (String value : values) {
                    writer.write("<td>" + value + "</td>");
                }

                writer.write("</tr>");
            }

            writer.write("""
                    </table>

                    </body>
                    </html>
                    """);

            writer.close();

            System.out.println(
                    "HTML dashboard saved: dashboard.html"
            );

        } catch (IOException e) {

            System.out.println(
                    "Error creating HTML file."
            );
        }
    }

    // Main method
    public static void main(String[] args) {

        System.out.println(
                "================================"
        );

        System.out.println(
                "       HEALTHCHECK PRO"
        );

        System.out.println(
                "================================\n"
        );

        StringBuilder results =
                new StringBuilder();

        for (Map.Entry<String, String> website :
                websites.entrySet()) {

            String result =
                    checkWebsite(
                            website.getKey(),
                            website.getValue()
                    );

            results.append(result).append("\n");
        }

        saveCSV(results);

        createHTML(results);

        System.out.println(
                "\nMonitoring completed!"
        );
    }
}