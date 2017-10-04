package me.tim.org.bluetoothtest;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 1;
    private BluetoothController bluetoothController;
    private ObdController obdController;

    private ArrayAdapter<String> adapter;
    private ArrayList<String> items;

    private String selectedAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bluetoothController = new BluetoothController(this);
        setHandlers();

        items = new ArrayList<>();
        adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.simple_list_black_1, items);

        final ListView lvItems = (ListView) findViewById(R.id.lvPaired);
        lvItems.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    private void setHandlers() {
        //Bluetooth button
        final Button btnBluetooth = (Button) findViewById(R.id.btnbluetooth);
        btnBluetooth.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!bluetoothController.verifyBluetooth()) {
                    //Bluetooth is off, asking for permission to turn it on
                    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                    if (!mBluetoothAdapter.isEnabled()) {
                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                    }
                } else {
                    //Bluetooth is on
                    pickDevice();
                }

            }
        });

        //RPM button
        final Button btnRpm = (Button) findViewById(R.id.btnrpm);
        final TextView tvRpm = (TextView) findViewById(R.id.tvrpm);
        btnRpm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (obdController != null) {
                    String rpm = obdController.getRPM();
                    tvRpm.setText(rpm);
                }
            }
        });

        //Paired button
        final Button btnPaired = (Button) findViewById(R.id.btnpaired);
        btnPaired.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                items.clear();
                final Set<BluetoothDevice> devices = bluetoothController.getPairedDevices();

                ArrayList<String> devicesString = new ArrayList<String>();
                for(BluetoothDevice bt: devices) {
                    items.add(bt.getName() + "\n" + bt.getAddress());
                }

                adapter.notifyDataSetChanged();
            }
        });


        final Button btnConnect = (Button) findViewById(R.id.btnconnect);
        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bluetoothController.simpleConnect(selectedAddress);
            }
        });

        final Button btnConnect2 = (Button) findViewById(R.id.btnconnect2);
        btnConnect2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bluetoothController.connect(selectedAddress);
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                //Bluetooth now enabled.
                pickDevice();
            } else {
                //Bluetooth not on.
                if (!bluetoothController.verifyBluetooth()) {
                    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                    if (!mBluetoothAdapter.isEnabled()) {
                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                    }
                }
            }
        } else {
            //Different request than bluetoth.
        }
    }

    private void pickDevice() {
        final Set<BluetoothDevice> devices = bluetoothController.getPairedDevices();

        ArrayList<String> devicesString = new ArrayList<String>();
        final ArrayList deviceAddresses = new ArrayList();
        for(BluetoothDevice bt: devices) {
            devicesString.add(bt.getName() + "\n" + bt.getAddress());
            deviceAddresses.add(bt.getAddress());
        }

        // show list
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.select_dialog_singlechoice,
                devicesString.toArray(new String[devicesString.size()]));

        alertDialog.setSingleChoiceItems(adapter, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
                int position = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                String deviceAddress = (String) deviceAddresses.get(position);

                selectedAddress = deviceAddress;
                final TextView tvAddress = (TextView) findViewById(R.id.tvaddress);
                tvAddress.setText(deviceAddress);
            }
        });

        alertDialog.setTitle("Choose Bluetooth device");
        alertDialog.show();

    }
}
