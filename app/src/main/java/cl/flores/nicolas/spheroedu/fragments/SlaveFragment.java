package cl.flores.nicolas.spheroedu.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.net.Socket;

import cl.flores.nicolas.spheroedu.R;
import cl.flores.nicolas.spheroedu.interfaces.OnFragmentInteractionListener;
import cl.flores.nicolas.spheroedu.interfaces.SocketInterface;
import cl.flores.nicolas.spheroedu.threads.ServerBluetoothThread;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SlaveFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SlaveFragment extends Fragment implements SocketInterface {
    private static final String pName = "pName";

    private String name;
    private ServerBluetoothThread server;
    private Socket socket;

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
        args.putString(pName, user_name);
        fragment.setArguments(args);
        return fragment;
    }

    public SlaveFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            name = getArguments().getString(pName);
        }
        server = new ServerBluetoothThread(this);
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
        server.run();
    }

    @Override
    public void onStop() {
        super.onStop();
        server.cancel();
    }

    @Override
    public void setSocket(Socket socket) {
        this.socket = socket;
    }
}
