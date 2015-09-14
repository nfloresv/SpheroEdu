package cl.flores.nicolas.spheroedu.fragments;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cl.flores.nicolas.spheroedu.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MasterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MasterFragment extends Fragment {
    private static final String ARG_NAME = "name";

    private String name;
    private ProgressDialog progressDialog;

    public MasterFragment() {
        // Required empty public constructor
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            name = getArguments().getString(ARG_NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_master, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        progressDialog = ProgressDialog.show(getContext(), null, getString(R.string.discovering_loading), true, false);
        Thread dismissThread = new Thread() {
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
        dismissThread.start();
    }

    @Override
    public void onStop() {
        super.onStop();
        progressDialog.dismiss();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
    }
}
