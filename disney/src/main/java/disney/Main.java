package disney;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Main {
    private static final String REQUEST_URL = "https://reserve.tokyodisneyresort.jp/hotel/api/queryHotelPriceStock/";
    private static final String LINE_NOTIFY_URL = "https://notify-api.line.me/api/notify";
    private static final String LINE_NOTIFY_TOKEN = "a0EBIUdrvo2iFW3MXaq6tyZtMb2IOqNv4xSWqXobK2r";

    public static void main(String[] args) {
        try {
            String response = checkAvailability();
            if (response.contains("remainStockNum")) {
                sendLineNotify("空きがあります！詳細: " + response);
            } else {
                System.out.println("空きがありません。");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String checkAvailability() throws Exception {
        URL url = new URL(REQUEST_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("User-Agent", "PostmanRuntime/7.38.0");
        conn.setDoOutput(true);

        String parameters = "commodityCD=HOTDHSCL0005N&useDate=20240901&stayingDays=1&adultNum=2&childNum=0&roomsNum=1&stockQueryType=3&rrc3005ProcessingType=update";
        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = parameters.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        try (Scanner scanner = new Scanner(conn.getInputStream(), StandardCharsets.UTF_8.name())) {
            return scanner.useDelimiter("\\A").next();
        }
    }

    private static void sendLineNotify(String message) throws Exception {
        URL url = new URL(LINE_NOTIFY_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", "Bearer " + LINE_NOTIFY_TOKEN);
        conn.setDoOutput(true);

        String parameters = "message=" + message;
        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = parameters.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        try (Scanner scanner = new Scanner(conn.getInputStream(), StandardCharsets.UTF_8.name())) {
            System.out.println(scanner.useDelimiter("\\A").next());
        }
    }
}
