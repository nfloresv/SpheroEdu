package cl.flores.nicolas.spheroedu.Utils;

/**
 * Class to manage the led of the sphero
 */
public class SpheroColors {
    public static final float backLightOff = 0.0f;
    public static final float backLightOn = 1.0f;
    public static final String connectedColor = "#05EBC5";
    private static final String[] colors = {"#059E3D", "#051F9E", "#D20808", "#7D24B0", "#FF6F00"};

    public static String getColorByIndex(int index) {
        if (index < colors.length) {
            return colors[index];
        }
        return "#27CCC4";
    }
}
