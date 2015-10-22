package cl.flores.nicolas.spheroedu.activities;

import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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
import cl.flores.nicolas.spheroedu.interfaces.MessageInterface;
import cl.flores.nicolas.spheroedu.threads.CommunicationThread;

public class ExerciseActivity extends AppCompatActivity implements MessageInterface {
    private ArrayList<CommunicationThread> communicationThreads;
    private ArrayList<ConvenienceRobot> robots;
    private String name;
    private boolean master;
    private ResponseListener responseListener = new ResponseListener() {
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

                if (locatorData.getPositionY() >= 10.0f) {
                    ConvenienceRobot sphero = new ConvenienceRobot(robot);
                    sphero.stop();
                    finish();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);

        communicationThreads = new ArrayList<>();
        CommunicationManager manager = CommunicationManager.getInstance();
        robots = manager.getRobots();
        for (ConvenienceRobot robot : robots) {
            robot.addResponseListener(responseListener);
        }

        ArrayList<BluetoothSocket> sockets = manager.getSockets();
        for (BluetoothSocket socket : sockets) {
            CommunicationThread thread = new CommunicationThread(socket, this);
            thread.start();
            communicationThreads.add(thread);
        }

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            name = extras.getString(Constants.BUNDLE_PARAM_USER_NAME);
            master = extras.getBoolean(Constants.BUNDLE_PARAM_MASTER, false);
        }
        if (!master) {
            Button button = (Button) findViewById(R.id.stabilization_btn);
            button.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        for (CommunicationThread thread : communicationThreads) {
            thread.cancel();
        }
        for (ConvenienceRobot robot : robots) {
            robot.removeResponseListener(responseListener);
            robot.sleep();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        for (ConvenienceRobot robot : robots) {
            robot.enableLocator(false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (master) {
            for (ConvenienceRobot robot : robots) {
                int index = robots.indexOf(robot);

                float[] rgb = SpheroColors.getColorByIndex(index);
                robot.setLed(rgb[0], rgb[1], rgb[2]);
                robot.setBackLedBrightness(SpheroColors.backLightOn);
                robot.enableStabilization(false);
            }
            for (CommunicationThread thread : communicationThreads) {
                JSONObject message = new JSONObject();
                try {
                    message.put(Constants.JSON_NAME, name);
                    message.put(Constants.JSON_MESSAGE, "Calibrando Sphero");
                } catch (JSONException e) {
                    Log.e(Constants.LOG_TAG, "Error writing JSON", e);
                }
                thread.write(message.toString());
            }
        } else {
            for (CommunicationThread thread : communicationThreads) {
                JSONObject message = new JSONObject();
                try {
                    message.put(Constants.JSON_NAME, name);
                    message.put(Constants.JSON_MESSAGE, "Hola Master");
                } catch (JSONException e) {
                    Log.e(Constants.LOG_TAG, "Error writing JSON", e);
                }
                thread.write(message.toString());
            }
        }
    }

    @Override
    public void getMessage(String message) {
        String decoded = "";
        try {
            JSONObject jsonObject = new JSONObject(message);
            if (jsonObject.has(Constants.JSON_NAME)) {
                decoded = jsonObject.getString(Constants.JSON_NAME) + ": ";
                decoded += jsonObject.getString(Constants.JSON_MESSAGE);
            }
        } catch (JSONException e) {
            Log.e(Constants.LOG_TAG, "Error parsing JSON", e);
        }
        Looper.prepare();
        Toast.makeText(this, decoded, Toast.LENGTH_SHORT).show();
        Looper.loop();
    }

    public void setStabilization(View v) {
        for (ConvenienceRobot robot : robots) {
            robot.enableStabilization(true);
            robot.setZeroHeading();
            robot.setBackLedBrightness(SpheroColors.backLightOff);

            robot.enableLocator(true);
            robot.drive(0.0f, .15f);
        }
        for (CommunicationThread thread : communicationThreads) {
            JSONObject message = new JSONObject();
            try {
                message.put(Constants.JSON_NAME, name);
                message.put(Constants.JSON_MESSAGE, "Spheros calibrados");
            } catch (JSONException e) {
                Log.e(Constants.LOG_TAG, "Error writing JSON", e);
            }
            thread.write(message.toString());
        }
    }
}
