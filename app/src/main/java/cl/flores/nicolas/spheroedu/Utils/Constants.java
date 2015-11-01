package cl.flores.nicolas.spheroedu.Utils;

/**
 * Class containing all the constants that are used in the application.
 */
public class Constants {
    //    Bluetooth Requests
    public static final int REQUEST_ENABLE_BT = 0x0101;
    public static final int REQUEST_DISCOVERABLE_BT = 0x0102;
    public static final int BLUETOOTH_DURATION = 300;

    //    Bluetooth UUID
    public static final String APPLICATION_UUID = "5CA3C87A-9331-4A73-AA42-737F971FDA37";

    //    Devices Connected
    public static final int MIN_CONNECTED_DEVICES = 1;
    public static final int MAX_CONNECTED_DEVICES = 3;

    //    Bundle
    public static final String BUNDLE_PARAM_USER_NAME = "USER_NAME";
    public static final String BUNDLE_PARAM_MASTER = "MASTER";

    //    Log
    public static final String LOG_TAG = "MEMORIA-NFLORES";

    //    JSON Keys
    public static final String JSON_NAME = "NAME";
    public static final String JSON_CHARGE_VALUE = "CHARGE";
    public static final String JSON_COLOR = "COLOR";
    public static final String JSON_POSITION = "POSITION";
    public static final String JSON_MESSAGE = "MESSAGE";
    public static final String JSON_STABILIZATION = "STABILIZATION";

//    Handler state
    public static final int MESSAGE_SEND = 0x0201;

    //    Exercise JSON Keys
    public static final String JSON_EXERCISE_INDEPENDENT = "INDEPENDENT";
    public static final String JSON_EXERCISE_SPHEROS_ARRAY = "SPHEROS";
    public static final String JSON_EXERCISE_X = "X";
    public static final String JSON_EXERCISE_Y = "Y";
    public static final String JSON_EXERCISE_CHARGE = "CHARGE";

    //    Exercise
    public static final String EXCERCISE_JSON = "{" +
                "\"INDEPENDENT\": 1," +
                "\"SPHEROS\": [" +
                    "{\"X\": 0,\"Y\": 0,\"CHARGE\": 1}," +
                    "{\"X\": 2,\"Y\": 2,\"CHARGE\": 4}," +
                    "{\"X\": 4,\"Y\": -4,\"CHARGE\": -4}" +
                "]" +
            "}";
}
