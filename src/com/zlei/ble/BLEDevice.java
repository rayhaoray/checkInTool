package com.zlei.ble;

import android.bluetooth.BluetoothDevice;

import java.util.HashMap;

public class BLEDevice {
    HashMap<String, String> device = new HashMap<String, String>();

    public void setUUID(String uuid) {
        device.put("uuid", uuid);
    }

    public void setMajor(String major) {
        device.put("major", major);
    }

    public void setMinor(String minor) {
        device.put("minor", minor);
    }

    public void setRSSI(int rssi) {
        device.put("rssi", Integer.toString(rssi));
    }

    public void setMAC(BluetoothDevice address) {
        device.put("mac", address.getAddress());
    }

    public void setRawData(String rawData) {
        device.put("rawdata", rawData);
    }

    public String getUUID() {
        return device.get("uuid");
    }

    public String getMajor() {
        return device.get("major");
    }

    public String getMinor() {
        return device.get("minor");
    }

    public String getRSSI() {
        return device.get("rssi");
    }

    public String getMAC() {
        return device.get("mac");
    }

    public String getRawData() {
        return device.get("rawdata");
    }
}
