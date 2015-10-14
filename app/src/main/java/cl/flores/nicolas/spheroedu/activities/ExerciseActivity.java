package cl.flores.nicolas.spheroedu.activities;

import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.orbotix.ConvenienceRobot;

import java.util.ArrayList;

import cl.flores.nicolas.spheroedu.R;
import cl.flores.nicolas.spheroedu.Utils.CommunicationManager;
import cl.flores.nicolas.spheroedu.threads.CommunicationThread;

public class ExerciseActivity extends AppCompatActivity {
    private ArrayList<CommunicationThread> communicationThreads;
    private ArrayList<ConvenienceRobot> robots;
    private String name;
    private boolean master;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);

        communicationThreads = new ArrayList<>();
        if (savedInstanceState != null) {
            name = savedInstanceState.getString("name");
            master = savedInstanceState.getBoolean("master");
            CommunicationManager manager = CommunicationManager.getInstance();
            robots = manager.getRobots();
            ArrayList<BluetoothSocket> sockets = manager.getSockets();
            for (BluetoothSocket socket : sockets) {
                CommunicationThread thread = new CommunicationThread(socket);
                thread.start();
                communicationThreads.add(thread);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        for (CommunicationThread thread : communicationThreads) {
            thread.cancel();
        }
    }
}
