package cl.flores.nicolas.spheroedu.activities;

import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.orbotix.ConvenienceRobot;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);

        communicationThreads = new ArrayList<>();
        CommunicationManager manager = CommunicationManager.getInstance();
        robots = manager.getRobots();
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        for (CommunicationThread thread : communicationThreads) {
            thread.cancel();
        }
        for (ConvenienceRobot robot : robots) {
            robot.sleep();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (master) {
            for (ConvenienceRobot robot : robots) {
                float r = SpheroColors.decimalToFloat(13);
                float g = SpheroColors.decimalToFloat(68);
                float b = SpheroColors.decimalToFloat(159);
                robot.setLed(r, g, b);
                robot.drive(0.0f, 1.0f);
            }
            for (CommunicationThread thread : communicationThreads) {
                thread.write("Moviendo sphero");
            }
            Thread stopThread = new Thread() {
                @Override
                public void run() {
                    try {
                        sleep(5 * 1000);
                        for (ConvenienceRobot robot : robots) {
                            robot.stop();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };
            stopThread.start();
        } else {
            for (CommunicationThread thread : communicationThreads) {
                thread.write("Hola master");
            }
        }
    }

    @Override
    public void getMessage(String message) {
        Looper.prepare();
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        Looper.loop();
    }
}
