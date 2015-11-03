package cl.flores.nicolas.spheroedu.activities;

import android.bluetooth.BluetoothSocket;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.orbotix.ConvenienceRobot;
import com.orbotix.async.DeviceSensorAsyncMessage;
import com.orbotix.common.ResponseListener;
import com.orbotix.common.Robot;
import com.orbotix.common.internal.AsyncMessage;
import com.orbotix.common.internal.DeviceResponse;
import com.orbotix.common.sensor.DeviceSensorsData;
import com.orbotix.common.sensor.LocatorData;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cl.flores.nicolas.spheroedu.R;
import cl.flores.nicolas.spheroedu.Utils.CommunicationManager;
import cl.flores.nicolas.spheroedu.Utils.Constants;
import cl.flores.nicolas.spheroedu.Utils.SpheroColors;
import cl.flores.nicolas.spheroedu.Wrappers.RobotManager;
import cl.flores.nicolas.spheroedu.Wrappers.RobotWrapper;
import cl.flores.nicolas.spheroedu.threads.CommunicationThread;

public class ExerciseActivity extends AppCompatActivity implements NumberPicker.OnValueChangeListener {
    private final String[] values = {"-10", "-9", "-8", "-7", "-6", "-5", "-4", "-3",
            "-2", "-1", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
    private final ArrayList<CommunicationThread> communicationThreads;
    private final RobotManager manager;
    private final ResponseListener responseListener;
    private final Handler mHandler;
    private String name;
    private boolean master;
    private NumberPicker np;
    private int position;
    private boolean loop = false;
    private TextView spheroColor;

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

                    // TODO verificar robot por nombre
                    for (RobotWrapper wrapper : manager.getIndependentWrapper()) {
                        wrapper.setX(locatorData.getPositionX());
                        wrapper.setY(locatorData.getPositionY());
                    }

                    if (locatorData.getPositionY() >= 100 && !loop) {
                        ConvenienceRobot sphero = new ConvenienceRobot(robot);
                        sphero.stop();
                        sphero.drive(180f, .5f);
                        loop = true;
                    }

                    if (locatorData.getPositionY() <= 0 && loop) {
                        ConvenienceRobot sphero = new ConvenienceRobot(robot);
                        sphero.stop();
                        finish();
                    }
                }
            }
        };
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == Constants.MESSAGE_SEND) {
                    String message = (String) msg.obj;
                    getMessage(message);
                }
                super.handleMessage(msg);
            }
        };

        CommunicationManager communicationManager = CommunicationManager.getInstance();

        ArrayList<ConvenienceRobot> robots = communicationManager.getRobots();
        manager = new RobotManager(robots, Constants.EXCERCISE_JSON);

        ArrayList<BluetoothSocket> sockets = communicationManager.getSockets();
        for (BluetoothSocket socket : sockets) {
            CommunicationThread thread = new CommunicationThread(socket, mHandler);
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
        np.setMinValue(0);
        np.setMaxValue(20);
        np.setDisplayedValues(values);
        np.setWrapSelectorWheel(false);
        np.setOnValueChangedListener(this);

        spheroColor = (TextView) findViewById(R.id.spheroColor);

        for (RobotWrapper wrapper : manager.getIndependentWrapper()) {
            ConvenienceRobot robot = wrapper.getRobot();
            if (robot != null) {
                robot.addResponseListener(responseListener);
            }
        }
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
            if (robot != null) {
                robot.enableLocator(false);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (master) {
            // independent Robots
            for (RobotWrapper wrapper : manager.getIndependentWrapper()) {
                ConvenienceRobot robot = wrapper.getRobot();

                int rgb = Color.parseColor(wrapper.getColor());
                robot.setLed(Color.red(rgb) / 255f, Color.green(rgb) / 255f, Color.blue(rgb) / 255f);
                robot.setBackLedBrightness(SpheroColors.backLightOn);
                robot.enableStabilization(false);
            }

            // Dependent Robots
            for (int i = 0; i < communicationThreads.size(); ++i) {
                RobotWrapper wrapper = manager.getWrapper(i);
                ConvenienceRobot robot = wrapper.getRobot();

                if (robot != null) {
                    int rgb = Color.parseColor(wrapper.getColor());
                    robot.setLed(Color.red(rgb) / 255f, Color.green(rgb) / 255f, Color.blue(rgb) / 255f);
                }
            }
            RobotWrapper wrapper = manager.getWrapper(communicationThreads.size());
            ConvenienceRobot robot = wrapper.getRobot();
            int rgbColor = Color.parseColor(wrapper.getColor());
            if (robot != null) {
                robot.setLed(Color.red(rgbColor) / 255f, Color.green(rgbColor) / 255f, Color.blue(rgbColor) / 255f);
            }
            position = communicationThreads.size();
            spheroColor.setBackgroundColor(rgbColor);

            // Communication
            for (CommunicationThread thread : communicationThreads) {
                int index = communicationThreads.indexOf(thread);
                RobotWrapper wrapper1 = manager.getWrapper(index);

                String rgb = wrapper1.getColor();

                JSONObject message = new JSONObject();
                try {
                    message.put(Constants.JSON_NAME, name);
                    message.put(Constants.JSON_MESSAGE, "Sphero color and position");
                    message.put(Constants.JSON_POSITION, index);
                    message.put(Constants.JSON_COLOR, rgb);
                } catch (JSONException e) {
                    Log.e(Constants.LOG_TAG, "Error writing JSON", e);
                }
                thread.write(message.toString());
            }
        }
    }

    private void getMessage(String message) {
        try {
            JSONObject jsonObject = new JSONObject(message);

            if (jsonObject.has(Constants.JSON_COLOR)) { // Sphero color and position
                position = jsonObject.getInt(Constants.JSON_POSITION);

                String color = jsonObject.getString(Constants.JSON_COLOR);
                int rgb = Color.parseColor(color);
                spheroColor.setBackgroundColor(rgb);
            } else if (jsonObject.has(Constants.JSON_STABILIZATION)) { // Sphero stabilized
                int charge = jsonObject.getInt(Constants.JSON_CHARGE_VALUE);
                np.setValue(charge);
                np.setVisibility(View.VISIBLE);
            } else if (jsonObject.has(Constants.JSON_CHARGE_VALUE)) {
                int charge = jsonObject.getInt(Constants.JSON_CHARGE_VALUE);
                int pos = jsonObject.getInt(Constants.JSON_POSITION);
                RobotWrapper wrapper = manager.getWrapper(pos);
                wrapper.setCharge(charge);
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

            robot.drive(0f, .2f);
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

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        if (master) {
            RobotWrapper wrapper = manager.getWrapper(position);
            wrapper.setCharge(newVal);
        } else {
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
    }
}
