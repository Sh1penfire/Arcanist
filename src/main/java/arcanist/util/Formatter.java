package arcanist.util;

public class Formatter {
    public static String percentSign(float value){
        return (value >= 0 ? "+" : "-") + (int) (value * 100) + "%";
    }
    public static String percent(float value){
        return (int) (value * 100) + "%";
    }
}
