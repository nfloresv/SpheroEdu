package cl.flores.nicolas.spheroedu;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import cl.flores.nicolas.spheroedu.activities.MasterActivity;
import cl.flores.nicolas.spheroedu.activities.SlaveActivity;
import cl.flores.nicolas.spheroedu.fragments.DataFragment;
import cl.flores.nicolas.spheroedu.fragments.ErrorFragment;

public class MainActivity extends AppCompatActivity {
    private String bundle_param;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bundle_param = getString(R.string.USER_NAME);

        FragmentTransaction manager = getSupportFragmentManager().beginTransaction();

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null) {
            Fragment data = DataFragment.newInstance();
            manager.replace(R.id.fragment, data);
        } else {
            String error_message = getString(R.string.bluetooth_error_message);
            Fragment error = ErrorFragment.newInstance(error_message);
            manager.replace(R.id.fragment, error);
        }
        manager.commit();
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

        if (name.isEmpty()) {
            Toast.makeText(this, R.string.name_restriction, Toast.LENGTH_SHORT).show();
            return;
        }

        Intent master = new Intent(this, MasterActivity.class);
        master.putExtra(bundle_param, name);
        startActivity(master);
        finish();
    }

    public void slaveButton(View view) {
        EditText nameET = (EditText) findViewById(R.id.nameET);
        String name = nameET.getText().toString();

        if (name.isEmpty()) {
            Toast.makeText(this, R.string.name_restriction, Toast.LENGTH_SHORT).show();
            return;
        }

        Intent slave = new Intent(this, SlaveActivity.class);
        slave.putExtra(bundle_param, name);
        startActivity(slave);
        finish();
    }
}
