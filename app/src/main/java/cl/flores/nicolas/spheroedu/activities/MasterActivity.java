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

import com.orbotix.Sphero;
import com.orbotix.classic.DiscoveryAgentClassic;
import com.orbotix.classic.RobotClassic;
import com.orbotix.common.DiscoveryException;
import com.orbotix.common.Robot;
import com.orbotix.common.RobotChangedStateListener;

import java.util.ArrayList;
import java.util.Set;

import cl.flores.nicolas.spheroedu.R;
import cl.flores.nicolas.spheroedu.interfaces.SocketInterface;
import cl.flores.nicolas.spheroedu.threads.ClientBluetoothThread;

public class MasterActivity extends ListActivity implements RobotChangedStateListener, SocketInterface {
    private final BroadcastReceiver receiver;
    private final ArrayList<String> devices;
    private final ArrayList<ClientBluetoothThread> clientBluetoothThreads;
    private final ArrayList<BluetoothSocket> bluetoothSockets;
    private String name;
    private ProgressDialog progressDialog;
    private ProgressDialog connectingDialog;
    private ArrayAdapter<String> adapter;
    private int REQUEST_ENABLE_BT;
    private BluetoothAdapter bluetoothAdapter;
    private DiscoveryAgentClassic agentClassic;
    private Thread dismissThread;
    private Sphero sphero;

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
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        SparseBooleanArray sp = getListView().getCheckedItemPositions();
        devices.clear();
        int MAX_DEVICES = getResources().getInteger(R.integer.MAX_CONNECTED_DEVICES);

        int count = 0;
        for (int j = 0; j < sp.size(); ++j) {
            if (sp.valueAt(j))
                ++count;
        }
        if (count > MAX_DEVICES) {
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            String bundle_params = getString(R.string.USER_NAME);
            name = savedInstanceState.getString(bundle_params);
        }
        setContentView(R.layout.activity_master);

        REQUEST_ENABLE_BT = getResources().getInteger(R.integer.REQUEST_ENABLE_BT);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        agentClassic = DiscoveryAgentClassic.getInstance();

        ArrayList<String> devices = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, devices);
        setListAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);

        agentClassic.addRobotStateListener(this);

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
        agentClassic.stopDiscovery();
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
        agentClassic.removeRobotStateListener(this);

        for (ClientBluetoothThread thread : clientBluetoothThreads) {
            thread.cancel();
        }
        if (connectingDialog != null) {
            connectingDialog.dismiss();
        }

        if (sphero != null) {
            sphero.disconnect();
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
        Toast.makeText(this, "Receive", Toast.LENGTH_SHORT).show();
        switch (type) {
            case Online:
                Toast.makeText(this, "Robot online", Toast.LENGTH_SHORT).show();
                if (robot instanceof RobotClassic) {
                    sphero = new Sphero(robot);
                    Toast.makeText(this, "Shpero", Toast.LENGTH_SHORT).show();
                }
                break;
            case Disconnected:
                Toast.makeText(this, "Robot disconnected", Toast.LENGTH_SHORT).show();
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
            agentClassic.startDiscovery(this);
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
        int MIN_DEVICES = getResources().getInteger(R.integer.MIN_CONNECTED_DEVICES);
        if (devices.size() < MIN_DEVICES) {
            Toast.makeText(this, R.string.min_devices, Toast.LENGTH_LONG).show();
            return;
        }

//        connectingDialog = ProgressDialog.show(getContext(), null, getString(R.string.connecting_loading), true, false);
        agentClassic.stopDiscovery();

        bluetoothSockets.clear();
        clientBluetoothThreads.clear();

        String appName = getString(R.string.app_name);
        String uuid = getString(R.string.APPLICATION_UUID);
        for (String mac : devices) {
            BluetoothDevice device = bluetoothAdapter.getRemoteDevice(mac);
            ClientBluetoothThread thread = new ClientBluetoothThread(device, this, appName, uuid);
            clientBluetoothThreads.add(thread);
            thread.start();
        }
    }
}
