package cl.flores.nicolas.spheroedu.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cl.flores.nicolas.spheroedu.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ErrorFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ErrorFragment extends Fragment {
    private static final String ARG_ERROR = "error_message";

    private String errorMessage;

    public ErrorFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param message error message to show on the view.
     * @return A new instance of fragment ErrorFragment.
     */
    public static ErrorFragment newInstance(String message) {
        ErrorFragment fragment = new ErrorFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ERROR, message);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            errorMessage = getArguments().getString(ARG_ERROR);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_error, container, false);

        TextView error = (TextView) rootView.findViewById(R.id.errorMessageTv);
        error.setText(errorMessage);

        return rootView;
    }


}
