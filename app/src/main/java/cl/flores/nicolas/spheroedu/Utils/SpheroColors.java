package cl.flores.nicolas.spheroedu.Utils;

/**
 * Class to manage the led of the sphero
 */
public class SpheroColors {
    public static final float backLinghtOff = 0.0f;
    public static final float backLinghtOn = 1.0f;
    private static final float maxValue = 255.0f;

    public static float decimalToFloat(int decimal) {
        return (float) decimal / maxValue;
    }
}
