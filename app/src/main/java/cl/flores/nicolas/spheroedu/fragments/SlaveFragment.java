package cl.flores.nicolas.spheroedu.fragments;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import cl.flores.nicolas.spheroedu.R;
import cl.flores.nicolas.spheroedu.interfaces.SocketInterface;
import cl.flores.nicolas.spheroedu.threads.ServerBluetoothThread;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SlaveFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SlaveFragment extends Fragment implements SocketInterface {
    private static final String ARG_NAME = "ARG_NAME";

    private int REQUEST_DISCOVERABLE_BT;
    private String name;
    private ServerBluetoothThread server;
    private BluetoothSocket socket;

    public SlaveFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param user_name name of the user.
     * @return A new instance of fragment SlaveFragment.
     */
    public static SlaveFragment newInstance(String user_name) {
        SlaveFragment fragment = new SlaveFragment();
        Bundle args = new Bundle();
        args.putString(ARG_NAME, user_name);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            name = getArguments().getString(ARG_NAME);
        }
        REQUEST_DISCOVERABLE_BT = getResources().getInteger(R.integer.REQUEST_DISCOVERABLE_BT);

        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivityForResult(discoverableIntent, REQUEST_DISCOVERABLE_BT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_CANCELED && requestCode == REQUEST_DISCOVERABLE_BT) {
            Toast.makeText(getContext(), R.string.bluetooth_error_message, Toast.LENGTH_LONG).show();
        } else if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_DISCOVERABLE_BT) {
            String appName = getString(R.string.app_name);
            String uuid = getString(R.string.APPLICATION_UUID);
            server = new ServerBluetoothThread(this, appName, uuid);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_slave, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (server != null) {
            server.start();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (server != null) {
            server.cancel();
        }
    }

    // TODO start excersice activity with name a socket
    @Override
    public void setSocket(BluetoothSocket socket) {
        this.socket = socket;
        server.cancel();
    }
}
