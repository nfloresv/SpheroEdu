package cl.flores.nicolas.spheroedu;

import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import cl.flores.nicolas.spheroedu.fragments.DataFragment;
import cl.flores.nicolas.spheroedu.fragments.MasterFragment;
import cl.flores.nicolas.spheroedu.fragments.SlaveFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null) {
            Fragment data = DataFragment.newInstance();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().add(R.id.fragment, data).commit();
        } else {
            // TODO change layout to fragment
            LinearLayout error = (LinearLayout) findViewById(R.id.errorLl);
            error.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void masterButton(View view) {
        EditText nameET = (EditText) findViewById(R.id.nameET);
        String name = nameET.getText().toString();

        Fragment master = MasterFragment.newInstance(name);

        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.fragment, master).commit();
    }

    public void slaveButton(View view) {
        EditText nameET = (EditText) findViewById(R.id.nameET);
        String name = nameET.getText().toString();

        Fragment slave = SlaveFragment.newInstance(name);

        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.fragment, slave).commit();
    }
}
