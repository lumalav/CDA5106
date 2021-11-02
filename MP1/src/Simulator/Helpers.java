package Simulator;

/**
 * Helper class to calculate the Log_2
 */
public class Helpers {
    
    public static int Log2(int number) {
        return (int)(Math.log(number) / Math.log(2));
    }

    public static double Log2(double number) {
        return Math.log(number) / Math.log(2);
    }
}
