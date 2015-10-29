package cl.flores.nicolas.spheroedu.activities;

import android.bluetooth.BluetoothSocket;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Space;

import com.orbotix.ConvenienceRobot;
import com.orbotix.async.DeviceSensorAsyncMessage;
import com.orbotix.common.ResponseListener;
import com.orbotix.common.Robot;
import com.orbotix.common.internal.AsyncMessage;
import com.orbotix.common.internal.DeviceResponse;
import com.orbotix.common.sensor.DeviceSensorsData;
import com.orbotix.common.sensor.LocatorData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cl.flores.nicolas.spheroedu.R;
import cl.flores.nicolas.spheroedu.Utils.CommunicationManager;
import cl.flores.nicolas.spheroedu.Utils.Constants;
import cl.flores.nicolas.spheroedu.Utils.RobotManager;
import cl.flores.nicolas.spheroedu.Utils.RobotWrapper;
import cl.flores.nicolas.spheroedu.Utils.SpheroColors;
import cl.flores.nicolas.spheroedu.interfaces.MessageInterface;
import cl.flores.nicolas.spheroedu.threads.CommunicationThread;

public class ExerciseActivity extends AppCompatActivity implements MessageInterface {
    private final ArrayList<CommunicationThread> communicationThreads;
    private final RobotManager manager;
    private final ResponseListener responseListener;
    private String name;
    private boolean master;
    private NumberPicker np;
    private int position;

    public ExerciseActivity() {
        super();
        position = 0;
        communicationThreads = new ArrayList<>();
        responseListener = new ResponseListener() {
            @Override
            public void handleResponse(DeviceResponse deviceResponse, Robot robot) {
                Log.d(Constants.LOG_TAG, "Simple response");
            }

            @Override
            public void handleStringResponse(String s, Robot robot) {
                Log.d(Constants.LOG_TAG, "Message asynchronous: " + s);
            }

            @Override
            public void handleAsyncMessage(AsyncMessage asyncMessage, Robot robot) {
                if (asyncMessage instanceof DeviceSensorAsyncMessage) {
                    DeviceSensorAsyncMessage sensorsData = (DeviceSensorAsyncMessage) asyncMessage;
                    ArrayList<DeviceSensorsData> sensorDataArray = sensorsData.getAsyncData();
                    DeviceSensorsData dsd = sensorDataArray.get(sensorDataArray.size() - 1);
                    LocatorData locatorData = dsd.getLocatorData();

                    String location = "Sphero '%1$s' " +
                            "posición (x=%2$.2f, y=%3$.2f) - " +
                            "Velocidad (x=%4$.2f, y=%5$.2f)";
                    String format = String.format(location, robot.getName(), locatorData.getPositionX(),
                            locatorData.getPositionY(), locatorData.getVelocityX(),
                            locatorData.getVelocityY());
                    Log.d(Constants.LOG_TAG, format);
                }
            }
        };

        CommunicationManager communicationManager = CommunicationManager.getInstance();

        ArrayList<ConvenienceRobot> robots = communicationManager.getRobots();
        manager = new RobotManager(robots, Constants.EXCERCISE_JSON);

        ArrayList<BluetoothSocket> sockets = communicationManager.getSockets();
        for (BluetoothSocket socket : sockets) {
            CommunicationThread thread = new CommunicationThread(socket, this);
            thread.start();
            communicationThreads.add(thread);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            name = extras.getString(Constants.BUNDLE_PARAM_USER_NAME);
            master = extras.getBoolean(Constants.BUNDLE_PARAM_MASTER, false);
        }
        if (!master) {
            Button button = (Button) findViewById(R.id.stabilization_btn);
            button.setVisibility(View.GONE);
        }

        np = (NumberPicker) findViewById(R.id.numberPicker);
        np.setMinValue(-10);
        np.setMaxValue(10);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        for (CommunicationThread thread : communicationThreads) {
            thread.cancel();
        }
        for (RobotWrapper wrapper : manager.getIndependentWrapper()) {
            ConvenienceRobot robot = wrapper.getRobot();
            robot.removeResponseListener(responseListener);
        }
        manager.sleep();
    }

    @Override
    protected void onPause() {
        super.onPause();
        for (RobotWrapper wrapper : manager.getIndependentWrapper()) {
            ConvenienceRobot robot = wrapper.getRobot();
            robot.enableLocator(false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (master) {
            // independent Robots
            for (RobotWrapper wrapper : manager.getIndependentWrapper()) {
                ConvenienceRobot robot = wrapper.getRobot();

                float[] rgb = wrapper.getColor();
                robot.setLed(rgb[0], rgb[1], rgb[2]);
                robot.setBackLedBrightness(SpheroColors.backLightOn);
                robot.enableStabilization(false);
            }

            // Dependent Robots
            for (int i = 0; i < communicationThreads.size(); ++i) {
                RobotWrapper wrapper = manager.getWrapper(i);
                ConvenienceRobot robot = wrapper.getRobot();

                if (robot != null) {
                    float[] rgb = wrapper.getColor();
                    robot.setLed(rgb[0], rgb[1], rgb[2]);
                }
            }
            RobotWrapper wrapper = manager.getWrapper(communicationThreads.size());
            ConvenienceRobot robot = wrapper.getRobot();
            if (robot != null) {
                float[] rgb = wrapper.getColor();
                robot.setLed(rgb[0], rgb[1], rgb[2]);
            }
            position = communicationThreads.size();

            // Communication
            for (CommunicationThread thread : communicationThreads) {
                int index = communicationThreads.indexOf(thread);
                RobotWrapper wrapper1 = manager.getWrapper(index);

                float[] rgb = wrapper1.getColor();

                JSONObject message = new JSONObject();
                JSONArray color = new JSONArray();
                try {
                    // Variables
                    message.put(Constants.JSON_NAME, name);
                    message.put(Constants.JSON_MESSAGE, "Sphero color and position");
                    message.put(Constants.JSON_POSITION, index);

                    // Array
                    color.put(rgb[0]);
                    color.put(rgb[1]);
                    color.put(rgb[2]);
                    message.put(Constants.JSON_COLOR_ARRAY, color);
                } catch (JSONException e) {
                    Log.e(Constants.LOG_TAG, "Error writing JSON", e);
                }
                thread.write(message.toString());
            }
        }

        // TODO Cambiar listener al exterior
        np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                for (CommunicationThread thread : communicationThreads) {
                    JSONObject message = new JSONObject();
                    try {
                        message.put(Constants.JSON_NAME, name);
                        message.put(Constants.JSON_MESSAGE, "Charge value change");
                        message.put(Constants.JSON_CHARGE_VALUE, newVal);
                        message.put(Constants.JSON_POSITION, position);
                    } catch (JSONException e) {
                        Log.e(Constants.LOG_TAG, "Error writing JSON", e);
                    }
                    thread.write(message.toString());
                }
            }
        });
    }

    @Override
    public void getMessage(String message) {
        try {
            JSONObject jsonObject = new JSONObject(message);

            // Sphero color and position
            if (jsonObject.has(Constants.JSON_COLOR_ARRAY)) {
                position = jsonObject.getInt(Constants.JSON_POSITION);

                JSONArray color = jsonObject.getJSONArray(Constants.JSON_COLOR_ARRAY);
                int red = (int) (color.getDouble(0) * 255);
                int green = (int) (color.getDouble(1) * 255);
                int blue = (int) (color.getDouble(2) * 255);

                Space spheroColor = (Space) findViewById(R.id.spheroColor);
                spheroColor.setBackgroundColor(Color.rgb(red, green, blue));
            }

            // Sphero stabilized
            if (jsonObject.has(Constants.JSON_STABILIZATION)) {
                int charge = jsonObject.getInt(Constants.JSON_STABILIZATION);
                np.setValue(charge);

                np.setVisibility(View.VISIBLE);
                Button button = (Button) findViewById(R.id.stabilization_btn);
                button.setVisibility(View.GONE);
            }
            // TODO readjust the sphero continously
            // TODO if sphero is in square position finish
        } catch (JSONException e) {
            Log.e(Constants.LOG_TAG, "Error parsing JSON", e);
        }
    }

    public void setStabilization(View v) {
        for (RobotWrapper wrapper : manager.getIndependentWrapper()) {
            ConvenienceRobot robot = wrapper.getRobot();

            robot.enableStabilization(true);
            robot.setZeroHeading();
            robot.setBackLedBrightness(SpheroColors.backLightOff);

            robot.enableLocator(true);
        }
        for (CommunicationThread thread : communicationThreads) {
            int index = communicationThreads.indexOf(thread);
            RobotWrapper wrapper = manager.getWrapper(index);

            double charge = wrapper.getCharge();
            JSONObject message = new JSONObject();

            try {
                message.put(Constants.JSON_NAME, name);
                message.put(Constants.JSON_MESSAGE, "Spheros stabilized");
                message.put(Constants.JSON_STABILIZATION, true);
                message.put(Constants.JSON_CHARGE_VALUE, charge);
            } catch (JSONException e) {
                Log.e(Constants.LOG_TAG, "Error writing JSON", e);
            }
            thread.write(message.toString());
        }

        np.setVisibility(View.VISIBLE);
        Button button = (Button) findViewById(R.id.stabilization_btn);
        button.setVisibility(View.GONE);
    }
}
