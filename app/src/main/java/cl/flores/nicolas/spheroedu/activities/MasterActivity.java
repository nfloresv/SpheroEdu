package cl.flores.nicolas.spheroedu.activities;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.orbotix.ConvenienceRobot;
import com.orbotix.DualStackDiscoveryAgent;
import com.orbotix.classic.RobotClassic;
import com.orbotix.common.DiscoveryException;
import com.orbotix.common.Robot;
import com.orbotix.common.RobotChangedStateListener;

import java.util.ArrayList;
import java.util.Set;

import cl.flores.nicolas.spheroedu.R;
import cl.flores.nicolas.spheroedu.Utils.Constants;
import cl.flores.nicolas.spheroedu.Utils.SpheroColors;
import cl.flores.nicolas.spheroedu.interfaces.SocketInterface;
import cl.flores.nicolas.spheroedu.threads.ClientBluetoothThread;

public class MasterActivity extends ListActivity implements RobotChangedStateListener, SocketInterface {
    private final BroadcastReceiver receiver;
    private final ArrayList<String> devices;
    private final ArrayList<ClientBluetoothThread> clientBluetoothThreads;
    private final ArrayList<BluetoothSocket> bluetoothSockets;
    private final ArrayList<ConvenienceRobot> spheros;
    private final int REQUEST_ENABLE_BT = Constants.REQUEST_ENABLE_BT;
    private String name;
    private ProgressDialog progressDialog;
    private ProgressDialog connectingDialog;
    private ArrayAdapter<String> adapter;
    private BluetoothAdapter bluetoothAdapter;
    private DualStackDiscoveryAgent discoveryAgent;
    private Thread dismissThread;

    public MasterActivity() {
        super();
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    adapter.add(device.getName() + "\n" + device.getAddress());
                }
            }
        };
        devices = new ArrayList<>();
        clientBluetoothThreads = new ArrayList<>();
        bluetoothSockets = new ArrayList<>();
        spheros = new ArrayList<>();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        SparseBooleanArray sp = getListView().getCheckedItemPositions();
        devices.clear();

        int count = 0;
        for (int j = 0; j < sp.size(); ++j) {
            if (sp.valueAt(j))
                ++count;
        }
        if (count > Constants.MAX_CONNECTED_DEVICES) {
            l.setItemChecked(position, false);
            Toast.makeText(this, R.string.max_devices, Toast.LENGTH_LONG).show();
        }
        for (int i = 0; i < sp.size(); i++) {
            if (sp.valueAt(i)) {
                int pos = sp.keyAt(i);
                String name_dir = adapter.getItem(pos);
                String dir = name_dir.split("\n")[1];
                devices.add(dir);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        for (ConvenienceRobot sphero : spheros) {
            sphero.sleep();
            sphero.disconnect();
        }

        for (ClientBluetoothThread thread : clientBluetoothThreads) {
            thread.cancel();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            String bundle_params = getString(R.string.USER_NAME);
            name = savedInstanceState.getString(bundle_params);
        }
        setContentView(R.layout.activity_master);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        discoveryAgent = DualStackDiscoveryAgent.getInstance();

        ArrayList<String> devices = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, devices);
        setListAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);

        discoveryAgent.addRobotStateListener(this);

        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        progressDialog = ProgressDialog.show(this, null, getString(R.string.discovering_loading), true, false);

        searchDevices();
        dismissThread.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        discoveryAgent.stopDiscovery();
        progressDialog.dismiss();
        dismissThread.interrupt();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_CANCELED && requestCode == REQUEST_ENABLE_BT) {
            Toast.makeText(this, R.string.bluetooth_error_message, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        this.unregisterReceiver(receiver);
        discoveryAgent.removeRobotStateListener(this);

        if (connectingDialog != null) {
            connectingDialog.dismiss();
        }
    }

    @Override
    public void setSocket(BluetoothSocket socket) {
        bluetoothSockets.add(socket);

        Looper.prepare();
        String res = getString(R.string.connected_devises);
        String message = String.format(res, bluetoothSockets.size(), devices.size());
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        Looper.loop();

        if (bluetoothSockets.size() == devices.size()) {
            // TODO get 3 socket and start exercise activity
            Looper.prepare();
            Toast.makeText(this, "Conectado", Toast.LENGTH_LONG).show();
            Looper.loop();
        }
    }

    @Override
    public void handleRobotChangedState(Robot robot, RobotChangedStateListener.RobotChangedStateNotificationType type) {
        switch (type) {
            case Offline:
                String offline = getString(R.string.sphero_offline);
                Toast.makeText(this, String.format(offline, robot.getName()), Toast.LENGTH_SHORT).show();
                break;
            case Online:
                if (robot instanceof RobotClassic) {
                    ConvenienceRobot sphero = new ConvenienceRobot(robot);
                    spheros.add(sphero);
                    String online = getString(R.string.sphero_connected);
                    Toast.makeText(this, String.format(online, robot.getName()), Toast.LENGTH_SHORT).show();
                    sphero.setZeroHeading();
                    float r = SpheroColors.decimalToFloat(42);
                    float g = SpheroColors.decimalToFloat(182);
                    float b = SpheroColors.decimalToFloat(7);
                    sphero.setLed(r, g, b);
                }
                break;
            case Disconnected:
                String disconnected = getString(R.string.sphero_offline);
                Toast.makeText(this, String.format(disconnected, robot.getName()), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void getPairedDevices() {
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                adapter.add(device.getName() + "\n" + device.getAddress());
            }
        }
    }

    private void searchDevices() {
        adapter.clear();

        try {
            discoveryAgent.startDiscovery(this);
        } catch (DiscoveryException e) {
            Log.e(getString(R.string.app_name), "Error starting discovery", e);
        }

        dismissThread = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(12 * 1000);
                    progressDialog.dismiss();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        getPairedDevices();
    }

    public void connectButton(View v) {
        if (devices.size() < Constants.MIN_CONNECTED_DEVICES) {
            Toast.makeText(this, R.string.min_devices, Toast.LENGTH_LONG).show();
            return;
        }

        connectingDialog = ProgressDialog.show(this, null, getString(R.string.connecting_loading), true, false);
        discoveryAgent.stopDiscovery();

        bluetoothSockets.clear();
        clientBluetoothThreads.clear();

        String appName = getString(R.string.app_name);
        for (String mac : devices) {
            BluetoothDevice device = bluetoothAdapter.getRemoteDevice(mac);
            ClientBluetoothThread thread = new ClientBluetoothThread(device, this, appName);
            clientBluetoothThreads.add(thread);
            thread.start();
        }
    }
}
