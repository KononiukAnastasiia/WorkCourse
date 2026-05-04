package ua.edu.restaurant.util;

import ua.edu.restaurant.exception.ValidationException;

import java.util.Scanner;

public class InputHelper {
    private static final Scanner scanner = new Scanner(System.in);

    public static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("  [!] Введіть ціле число.");
            }
        }
    }

    public static double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                double value = Double.parseDouble(scanner.nextLine().trim().replace(",", "."));
                if (value < 0) {
                    System.out.println("  [!] Значення не може бути від'ємним.");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("  [!] Введіть число (наприклад: 45.50).");
            }
        }
    }

    public static String readString(String prompt) {
        System.out.print(prompt);
        String value = scanner.nextLine().trim();
        if (value.isEmpty()) {
            throw new ValidationException("Значення не може бути порожнім.");
        }
        return value;
    }

    public static String readOptionalString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    public static void pause() {
        System.out.print("\n  Натисніть Enter для продовження...");
        scanner.nextLine();
    }
}
