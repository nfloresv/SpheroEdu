package cl.flores.nicolas.spheroedu.activities;

import android.bluetooth.BluetoothSocket;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import cl.flores.nicolas.spheroedu.R;
import cl.flores.nicolas.spheroedu.Utils.CommunicationManager;
import cl.flores.nicolas.spheroedu.Utils.Constants;
import cl.flores.nicolas.spheroedu.threads.CommunicationThread;

public class SlaveExercise extends ExerciseActivity {
    private final CommunicationThread communicationThread;

    public SlaveExercise() {
        super();
        final Handler mHandler = new Handler(Looper.getMainLooper()) {
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

        BluetoothSocket socket = communicationManager.getSockets().get(0);
        CommunicationThread thread = new CommunicationThread(socket, mHandler);
        thread.start();
        communicationThread = thread;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Button button = (Button) findViewById(R.id.stabilization_btn);
        button.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        communicationThread.cancel();
    }

    @Override
    protected void getMessage(String message) {
        try {
            JSONObject jsonObject = new JSONObject(message);

            if (jsonObject.has(Constants.JSON_COLOR)) { // Sphero color and position
                position = jsonObject.getInt(Constants.JSON_SPHERO_NUMBER);

                String color = jsonObject.getString(Constants.JSON_COLOR);
                int rgb = Color.parseColor(color);
                spheroColor.setBackgroundColor(rgb);

                String sphero_position = getString(R.string.sphero_position);
                double positionX = jsonObject.getDouble(Constants.JSON_POSITION_X);
                double positionY = jsonObject.getDouble(Constants.JSON_POSITION_Y);
                sphero_position = String.format(sphero_position, positionX, positionY);
                TextView locationTv = (TextView) findViewById(R.id.locationTV);
                locationTv.setText(sphero_position);
            } else if (jsonObject.has(Constants.JSON_STABILIZATION)) { // Sphero stabilized
                int charge = jsonObject.getInt(Constants.JSON_CHARGE_VALUE);

                np.setValue(charge);
                LinearLayout ln = (LinearLayout) findViewById(R.id.linearLayout);
                ln.setVisibility(View.VISIBLE);
            }
        } catch (JSONException e) {
            Log.e(Constants.LOG_TAG, "Error parsing JSON", e);
        }
    }

    @Override
    public void setCharge(View v) {
        JSONObject message = new JSONObject();
        try {
            message.put(Constants.JSON_NAME, name);
            message.put(Constants.JSON_MESSAGE, "Charge value change");
            message.put(Constants.JSON_CHARGE_VALUE, charge);
            message.put(Constants.JSON_SPHERO_NUMBER, position);
        } catch (JSONException e) {
            Log.e(Constants.LOG_TAG, "Error writing JSON", e);
        }
        communicationThread.write(message.toString());
    }
}
