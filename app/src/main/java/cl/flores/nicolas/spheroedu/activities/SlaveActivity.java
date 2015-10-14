package cl.flores.nicolas.spheroedu.activities;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import cl.flores.nicolas.spheroedu.R;
import cl.flores.nicolas.spheroedu.Utils.CommunicationManager;
import cl.flores.nicolas.spheroedu.Utils.Constants;
import cl.flores.nicolas.spheroedu.interfaces.SocketInterface;
import cl.flores.nicolas.spheroedu.threads.ServerBluetoothThread;

public class SlaveActivity extends AppCompatActivity implements SocketInterface {
    private final int bluetoothDuration = Constants.BLUETOOTH_DURAION;
    private final int REQUEST_DISCOVERABLE_BT = Constants.REQUEST_DISCOVERABLE_BT;
    private String name;
    private ServerBluetoothThread server;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            String bundle_params = getString(R.string.USER_NAME);
            name = savedInstanceState.getString(bundle_params);
        }
        setContentView(R.layout.activity_slave);

        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, bluetoothDuration);
        startActivityForResult(discoverableIntent, REQUEST_DISCOVERABLE_BT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_CANCELED && requestCode == REQUEST_DISCOVERABLE_BT) {
            Toast.makeText(this, R.string.bluetooth_error_message, Toast.LENGTH_LONG).show();
        } else if ((resultCode == Activity.RESULT_OK || resultCode == bluetoothDuration) && requestCode == REQUEST_DISCOVERABLE_BT) {
            String appName = getString(R.string.app_name);
            server = new ServerBluetoothThread(this, appName);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (server != null && !server.isAlive()) {
            server.start();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (server != null) {
            server.cancel();
        }
    }

    @Override
    public void setSocket(BluetoothSocket socket) {
        CommunicationManager manager = CommunicationManager.getInstance();
        manager.putSocket(socket);
        Intent exercise = new Intent(this, ExerciseActivity.class);
        exercise.putExtra("name", name);
        exercise.putExtra("master", false);
        startActivity(exercise);
        finish();
    }
}
