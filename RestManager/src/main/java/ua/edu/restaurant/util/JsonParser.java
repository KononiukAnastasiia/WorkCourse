package ua.edu.restaurant.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Простий JSON парсер без зовнішніх бібліотек.
 * Витягує значення з JSON рядків для консольного інтерфейсу.
 */
public class JsonParser {

    // Витягує рядкове значення поля з JSON
    public static String getString(String json, String key) {
        String search = "\"" + key + "\"";
        int idx = json.indexOf(search);
        if (idx < 0) return "";
        idx += search.length();
        // Пропускаємо пробіли і двокрапку
        while (idx < json.length() && (json.charAt(idx) == ':' || json.charAt(idx) == ' ')) idx++;
        if (idx >= json.length()) return "";
        if (json.charAt(idx) == '"') {
            // Рядкове значення
            idx++;
            StringBuilder sb = new StringBuilder();
            while (idx < json.length() && json.charAt(idx) != '"') {
                if (json.charAt(idx) == '\\') idx++; // escape
                if (idx < json.length()) sb.append(json.charAt(idx));
                idx++;
            }
            return sb.toString();
        } else {
            // Числове або boolean значення
            StringBuilder sb = new StringBuilder();
            while (idx < json.length() && json.charAt(idx) != ',' && json.charAt(idx) != '}') {
                sb.append(json.charAt(idx++));
            }
            return sb.toString().trim();
        }
    }

    // Витягує числове значення поля
    public static double getDouble(String json, String key) {
        String val = getString(json, key);
        try { return Double.parseDouble(val); } catch (Exception e) { return 0; }
    }

    public static int getInt(String json, String key) {
        String val = getString(json, key);
        try { return (int) Double.parseDouble(val); } catch (Exception e) { return 0; }
    }

    // Розбиває JSON масив на список об'єктів
    public static List<String> parseArray(String json) {
        List<String> result = new ArrayList<>();
        if (json == null || json.isBlank()) return result;
        json = json.trim();
        if (!json.startsWith("[")) return result;
        int depth = 0;
        int start = -1;
        for (int i = 0; i < json.length(); i++) {
            char c = json.charAt(i);
            if (c == '{') {
                if (depth == 0) start = i;
                depth++;
            } else if (c == '}') {
                depth--;
                if (depth == 0 && start >= 0) {
                    result.add(json.substring(start, i + 1));
                    start = -1;
                }
            }
        }
        return result;
    }

    // Формує JSON об'єкт з пар ключ-значення
    public static String obj(String... pairs) {
        StringBuilder sb = new StringBuilder("{");
        for (int i = 0; i < pairs.length - 1; i += 2) {
            if (i > 0) sb.append(",");
            sb.append("\"").append(pairs[i]).append("\":");
            String val = pairs[i + 1];
            try {
                Double.parseDouble(val);
                sb.append(val); // число — без лапок
            } catch (NumberFormatException e) {
                sb.append("\"").append(val.replace("\"", "\\\"")).append("\"");
            }
        }
        sb.append("}");
        return sb.toString();
    }
}
