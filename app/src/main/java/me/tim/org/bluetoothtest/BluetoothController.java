package me.tim.org.bluetoothtest;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.ParcelUuid;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import me.tim.org.bluetoothtest.Bluetooth.BluetoothConnector;
import me.tim.org.bluetoothtest.Bluetooth.BluetoothSocketWrapper;

/**
 * Created by Nekkyou on 3-10-2017.
 */

public class BluetoothController {

    private BluetoothAdapter adapter;
    private Context context;
    private BluetoothSocketWrapper socket;

    public BluetoothController(Context context) {
        this.context = context;
        this.adapter = BluetoothAdapter.getDefaultAdapter();
    }

    public boolean verifyBluetooth() {
        if (adapter == null) {
            return false;
        }

        if (adapter.isEnabled()) {
            return true;
        }
        return false;
    }

    public Set<BluetoothDevice> getPairedDevices() {
        return adapter.getBondedDevices();
    }

    public BluetoothDevice getDevice(String address) {
        return adapter.getRemoteDevice(address);
    }

    public void simpleConnect(String address) {
        BluetoothDevice device = getDevice(address);

        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

        try {
            BluetoothSocket btSocket = device.createInsecureRfcommSocketToServiceRecord(uuid);
            btSocket.connect();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void connect(String address) {
        BluetoothDevice device = getDevice(address);

        ParcelUuid[] parcelUuids = device.getUuids();
        ArrayList<UUID> uuids = new ArrayList<>();
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
        uuids.add(uuid);

        for (ParcelUuid pu: parcelUuids) {
            uuids.add(pu.getUuid());
        }

        BluetoothConnector connector = new BluetoothConnector(device, false, adapter, uuids);
        try {
            socket = connector.connect();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public BluetoothSocketWrapper getSocket() {
        return socket;
    }

}
