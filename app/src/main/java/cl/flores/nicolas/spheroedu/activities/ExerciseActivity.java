package cl.flores.nicolas.spheroedu.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TextView;

import cl.flores.nicolas.spheroedu.R;
import cl.flores.nicolas.spheroedu.Utils.Constants;

public abstract class ExerciseActivity extends AppCompatActivity implements NumberPicker.OnValueChangeListener {
    protected final String[] charges;
    protected String name;
    protected NumberPicker np;
    protected int position;
    protected TextView spheroColor;
    protected int charge;

    public ExerciseActivity() {
        super();
        position = 0;
        charges = new String[]{"-10", "-9", "-8", "-7", "-6", "-5", "-4", "-3",
                "-2", "-1", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            name = extras.getString(Constants.BUNDLE_PARAM_USER_NAME);
        }

        np = (NumberPicker) findViewById(R.id.numberPicker);
        np.setMinValue(0);
        np.setMaxValue(20);
        np.setDisplayedValues(charges);
        np.setWrapSelectorWheel(false);
        np.setOnValueChangedListener(this);

        spheroColor = (TextView) findViewById(R.id.spheroColor);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    protected abstract void getMessage(String message);

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        charge = Integer.parseInt(charges[newVal]);
    }

    public abstract void setCharge(View v);
}
