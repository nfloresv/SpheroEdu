package cl.flores.nicolas.spheroedu.Utils;

/**
 * Class to manage the led of the sphero
 */
public class SpheroColors {
    public static final float backLightOff = 0.0f;
    public static final float backLightOn = 1.0f;

    private static float decimalToFloat(int decimal) {
        return (float) decimal / 255.0f;
    }

    public static float[] getConnectedColor() {
        float r = decimalToFloat(9);
        float g = decimalToFloat(233);
        float b = decimalToFloat(195);

        return new float[]{r, g, b};
    }

    public static float[] getColorByIndex(int index) {
        int[] red = new int[]{5, 158, 68, 176, 255};
        int[] green = new int[]{158, 145, 63, 36, 111};
        int[] blue = new int[]{61, 5, 217, 73, 0};

        float r = decimalToFloat(red[index]);
        float g = decimalToFloat(green[index]);
        float b = decimalToFloat(blue[index]);

        return new float[]{r, g, b};
    }
}
