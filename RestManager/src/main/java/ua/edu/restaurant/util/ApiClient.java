package ua.edu.restaurant.util;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

public class ApiClient {
    private static final String BASE_URL = "http://localhost:8080/api";

    static {
        // Дозволяємо PATCH метод для HttpURLConnection
        try {
            Field methodsField = HttpURLConnection.class.getDeclaredField("methods");
            methodsField.setAccessible(true);
            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(methodsField, methodsField.getModifiers() & ~Modifier.FINAL);
            String[] oldMethods = (String[]) methodsField.get(null);
            Set<String> methodsSet = new LinkedHashSet<>(Arrays.asList(oldMethods));
            methodsSet.add("PATCH");
            methodsField.set(null, methodsSet.toArray(new String[0]));
        } catch (Exception e) {
            // Java 17+ — використовуємо обхідний шлях через X-HTTP-Method-Override
        }
    }

    public static String get(String path) {
        return request("GET", path, null);
    }

    public static String post(String path, String json) {
        return request("POST", path, json);
    }

    public static String put(String path, String json) {
        return request("PUT", path, json);
    }

    public static String patch(String path, String json) {
        return request("PATCH", path, json);
    }

    public static String delete(String path) {
        return request("DELETE", path, null);
    }

    private static String request(String method, String path, String body) {
        try {
            URL url = new URL(BASE_URL + path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // Обхідний шлях для PATCH через POST + заголовок
            if (method.equals("PATCH")) {
                conn.setRequestMethod("POST");
                conn.setRequestProperty("X-HTTP-Method-Override", "PATCH");
            } else {
                conn.setRequestMethod(method);
            }

            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setConnectTimeout(3000);
            conn.setReadTimeout(5000);

            if (body != null) {
                conn.setDoOutput(true);
                try (OutputStream os = conn.getOutputStream()) {
                    os.write(body.getBytes(StandardCharsets.UTF_8));
                }
            } else if (method.equals("PATCH")) {
                conn.setDoOutput(true);
                try (OutputStream os = conn.getOutputStream()) {
                    os.write("{}".getBytes(StandardCharsets.UTF_8));
                }
            }

            int code = conn.getResponseCode();
            InputStream is = (code >= 200 && code < 300)
                    ? conn.getInputStream()
                    : conn.getErrorStream();

            if (is == null) return "";
            StringBuilder sb = new StringBuilder();
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(is, StandardCharsets.UTF_8))) {
                String line;
                while ((line = br.readLine()) != null) sb.append(line);
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("[API] Помилка з'єднання з сервером: " + e.getMessage() +
                    "\nПереконайтесь що WebserverApplication запущений на порту 8080!");
        }
    }
}