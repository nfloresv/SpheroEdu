package cl.flores.nicolas.spheroedu.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

import cl.flores.nicolas.spheroedu.R;
import cl.flores.nicolas.spheroedu.interfaces.SocketInterface;
import cl.flores.nicolas.spheroedu.threads.ClientBluetoothThread;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MasterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MasterFragment extends ListFragment implements View.OnClickListener, SocketInterface {
    private static final String ARG_NAME = "name";

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
    private Thread dismissThread;


    public MasterFragment() {
        // Required empty public constructor
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

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param user_name user name.
     * @return A new instance of fragment MasterFragment.
     */
    public static MasterFragment newInstance(String user_name) {
        MasterFragment fragment = new MasterFragment();
        Bundle args = new Bundle();
        args.putString(ARG_NAME, user_name);
        fragment.setArguments(args);
        return fragment;
    }

    private void getPairedDevices() {
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                adapter.add(device.getName() + "\n" + device.getAddress());
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            name = getArguments().getString(ARG_NAME);
        }
        REQUEST_ENABLE_BT = getResources().getInteger(R.integer.REQUEST_ENABLE_BT);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        ArrayList<String> devices = new ArrayList<>();
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_multiple_choice, devices);
    }

    @Override
    public void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        getContext().registerReceiver(receiver, filter);

        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_master, container, false);

        setListAdapter(adapter);

        Button connectBtn = (Button) rootView.findViewById(R.id.connectBtn);
        connectBtn.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        SparseBooleanArray sp = getListView().getCheckedItemPositions();
        devices.clear();

        int count = 0;
        for (int j = 0; j < sp.size(); ++j) {
            if (sp.valueAt(j))
                ++count;
        }
        if (count > 3) {
            l.setItemChecked(position, false);
            Toast.makeText(getContext(), R.string.max_devices, Toast.LENGTH_LONG).show();
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
    public void onResume() {
        super.onResume();
        progressDialog = ProgressDialog.show(getContext(), null, getString(R.string.discovering_loading), true, false);

        searchDevices();
        dismissThread.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        bluetoothAdapter.cancelDiscovery();
        progressDialog.dismiss();
        dismissThread.interrupt();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_CANCELED && requestCode == REQUEST_ENABLE_BT) {
            Toast.makeText(getContext(), R.string.bluetooth_error_message, Toast.LENGTH_LONG).show();
        }
    }

    private void searchDevices() {
        adapter.clear();
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
        bluetoothAdapter.startDiscovery();
    }

    @Override
    public void onStop() {
        super.onStop();
        getContext().unregisterReceiver(receiver);
        for (ClientBluetoothThread thread : clientBluetoothThreads) {
            thread.cancel();
        }
        connectingDialog.dismiss();
    }

    @Override
    public void onClick(View v) {
        connectingDialog = ProgressDialog.show(getContext(), null, getString(R.string.connecting_loading), true, false);
        bluetoothAdapter.cancelDiscovery();

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

    @Override
    public void setSocket(BluetoothSocket socket) {
        bluetoothSockets.add(socket);
        // TODO get 3 socket and start exercise activity
    }
}
