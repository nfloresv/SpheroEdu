package cl.flores.nicolas.spheroedu.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cl.flores.nicolas.spheroedu.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class DataFragment extends Fragment {
    private static final String ARG_REQUEST = "ARG_REQUEST";

    private int REQUEST_ENABLE_BT;

    public DataFragment() {
    }

    public static DataFragment newInstance(int request_bluetooth) {
        DataFragment fragment = new DataFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_REQUEST, request_bluetooth);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            REQUEST_ENABLE_BT = getArguments().getInt(ARG_REQUEST);
        }
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == Activity.RESULT_CANCELED && requestCode == REQUEST_ENABLE_BT) {
//            Toast.makeText(getContext(), R.string.bluetooth_error_message, Toast.LENGTH_LONG).show();
//        }
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_data, container, false);
    }

//    @Override
//    public void onStart() {
//        super.onStart();
//        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//        if (!bluetoothAdapter.isEnabled()) {
//            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
//        }
//    }
}
