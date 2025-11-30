package utils;

import java.util.Scanner;

/**
 * Утилиты для работы с консолью
 */
public class ConsoleUtils {
    private static Scanner scanner = new Scanner(System.in);
    
    public static void printHeader(String title) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("  " + title);
        System.out.println("=".repeat(50));
    }
    
    public static void printSuccess(String message) {
        System.out.println("✅ " + message);
    }
    
    public static void printError(String message) {
        System.out.println("❌ " + message);
    }
    
    public static void printWarning(String message) {
        System.out.println("⚠️ " + message);
    }
    
    public static void printInfo(String message) {
        System.out.println("ℹ️ " + message);
    }
    
    public static String readString(String prompt) {
        System.out.print(prompt + ": ");
        return scanner.nextLine();
    }
    
    public static int readInt(String prompt) {
        while (true) {
            try {
                System.out.print(prompt + ": ");
                int value = scanner.nextInt();
                scanner.nextLine(); // очистка буфера
                return value;
            } catch (Exception e) {
                System.out.println("❌ Ошибка: введите целое число");
                scanner.nextLine(); // очистка буфера
            }
        }
    }
    
    public static double readDouble(String prompt) {
        while (true) {
            try {
                System.out.print(prompt + ": ");
                double value = scanner.nextDouble();
                scanner.nextLine(); // очистка буфера
                return value;
            } catch (Exception e) {
                System.out.println("❌ Ошибка: введите число");
                scanner.nextLine(); // очистка буфера
            }
        }
    }
    
    public static void pressEnterToContinue() {
        System.out.print("\nНажмите Enter для продолжения...");
        scanner.nextLine();
    }
}
