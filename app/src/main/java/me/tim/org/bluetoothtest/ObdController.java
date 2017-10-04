package me.tim.org.bluetoothtest;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.github.pires.obd.commands.SpeedCommand;
import com.github.pires.obd.commands.engine.RPMCommand;
import com.github.pires.obd.commands.protocol.EchoOffCommand;
import com.github.pires.obd.commands.protocol.LineFeedOffCommand;
import com.github.pires.obd.commands.protocol.SelectProtocolCommand;
import com.github.pires.obd.commands.protocol.TimeoutCommand;
import com.github.pires.obd.commands.temperature.AmbientAirTemperatureCommand;
import com.github.pires.obd.enums.ObdProtocols;

import java.io.IOException;
import java.util.UUID;

import me.tim.org.bluetoothtest.Bluetooth.BluetoothSocketWrapper;

/**
 * Created by Nekkyou on 4-10-2017.
 */

public class ObdController {
    private String deviceAddress;
    private BluetoothController bluetoothController;

    public ObdController(String device, BluetoothController bluetoothController) {
        this.deviceAddress = device;
        this.bluetoothController = bluetoothController;

        initialize();
    }


    private void initialize() {
        bluetoothController.connect(deviceAddress);
        // execute commands
        BluetoothSocketWrapper socket = bluetoothController.getSocket();
        if (socket != null) {
            try {
                new EchoOffCommand().run(socket.getInputStream(), socket.getOutputStream());
                new LineFeedOffCommand().run(socket.getInputStream(), socket.getOutputStream());
                new TimeoutCommand(100).run(socket.getInputStream(), socket.getOutputStream());
                new SelectProtocolCommand(ObdProtocols.AUTO).run(socket.getInputStream(), socket.getOutputStream());
            } catch (Exception e) {
                // handle errors
            }
        } else {
            System.out.println("Socket not connected.");
        }
    }

    public String getRPM() {
        String formattedRpm = "";
        BluetoothSocketWrapper socket = bluetoothController.getSocket();
        RPMCommand rpmCommand = new RPMCommand();
        SpeedCommand speedCommand = new SpeedCommand();
        while(!Thread.currentThread().isInterrupted()) {
            try {
                rpmCommand.run(socket.getInputStream(), socket.getOutputStream());
                speedCommand.run(socket.getInputStream(), socket.getOutputStream());

                //Handle results
                formattedRpm = rpmCommand.getFormattedResult();
                Log.d(this.getClass().getName(), "RPM: " + formattedRpm);
                Log.d(this.getClass().getName(), "Speed: " + speedCommand.getFormattedResult());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return formattedRpm;
    }


}
