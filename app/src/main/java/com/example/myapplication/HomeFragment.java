package com.example.myapplication;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.pm.PackageManager;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class HomeFragment extends Fragment {

    Button btnConnect;
    TextView txtStatus, txtDeviceName, txtConnectionLabel, txtRecentEventTitle, txtRecentEventTime;
    View cardRecentEvent;
    CardView viewPulse;
    BluetoothAdapter bluetoothAdapter;
    BluetoothSocket socket;
    BluetoothDevice device;
    InputStream inputStream;

    Handler handler = new Handler(Looper.getMainLooper());
    boolean isConnected = false;

    MovementLogDao movementLogDao;
    final UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        btnConnect = view.findViewById(R.id.btnConnect);
        txtStatus = view.findViewById(R.id.txtStatus);
        txtDeviceName = view.findViewById(R.id.txtDeviceName);
        txtConnectionLabel = view.findViewById(R.id.txtConnectionLabel);
        txtRecentEventTitle = view.findViewById(R.id.txtRecentEventTitle);
        txtRecentEventTime = view.findViewById(R.id.txtRecentEventTime);
        viewPulse = view.findViewById(R.id.viewPulse);
        cardRecentEvent = view.findViewById(R.id.cardRecentEvent);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        movementLogDao = AppDatabase.getInstance(requireContext()).movementLogDao();

        checkInitialBluetoothState();
        requestPermissionsIfNeeded();


        btnConnect.setOnClickListener(v -> MotionFx.press(v, this::connectBluetooth));

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadRecentEvent();
    }

    private void loadRecentEvent() {
        new Thread(() -> {
            List<MovementLog> logs = movementLogDao.getAllLogs();
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    if (logs == null || logs.isEmpty()) {
                        txtRecentEventTitle.setText(R.string.no_recent_events);
                        txtRecentEventTime.setVisibility(View.GONE);
                    } else {
                        MovementLog last = logs.get(logs.size() - 1);
                        if ("Motion".equals(last.event)) {
                            txtRecentEventTitle.setText(R.string.event_motion);
                        } else if ("Quiet".equals(last.event)) {
                            txtRecentEventTitle.setText(R.string.event_quiet);
                        } else {
                            txtRecentEventTitle.setText(last.event);
                        }
                        txtRecentEventTime.setText(last.dateTime);
                        txtRecentEventTime.setVisibility(View.VISIBLE);
                    }
                });
            }
        }).start();
    }

    private void checkInitialBluetoothState() {
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            applyStatus(R.string.status_bt_off, R.string.chip_bt_off, R.color.amber);
        } else {
            applyStatus(R.string.status_waiting, R.string.chip_offline, R.color.rose);
        }
    }

    private boolean hasBluetoothConnectPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    private void requestPermissionsIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            java.util.ArrayList<String> needed = new java.util.ArrayList<>();
            if (ContextCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                needed.add(Manifest.permission.BLUETOOTH_CONNECT);
                needed.add(Manifest.permission.BLUETOOTH_SCAN);
            }
            if (!needed.isEmpty()) {
                requestPermissions(needed.toArray(new String[0]), 1);
            }
        }
    }

    private BluetoothDevice findPairedDevice() {
        if (!hasBluetoothConnectPermission()) {
            return null;
        }
        try {
            if (bluetoothAdapter != null) {
                for (BluetoothDevice d : bluetoothAdapter.getBondedDevices()) {
                    if (d.getName() != null && d.getName().contains("HC")) {
                        return d;
                    }
                }
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void connectBluetooth() {
        if (!hasBluetoothConnectPermission()) {
            requestPermissionsIfNeeded();
            return;
        }

        new Thread(() -> {
            try {
                if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
                    postToMain(() -> applyStatus(R.string.status_bt_off, R.string.chip_bt_off, R.color.amber));
                    return;
                }

                device = findPairedDevice();

                if (device == null) {
                    postToMain(() -> applyStatus(R.string.status_no_device, R.string.chip_none, R.color.rose));
                    return;
                }

                postToMain(() -> {
                    applyStatus(R.string.status_connecting, R.string.chip_linking, R.color.amber);
                    try {
                        txtDeviceName.setText(device.getName());
                    } catch (SecurityException ignored) {
                        txtDeviceName.setText(R.string.device_unknown);
                    }
                });

                socket = device.createRfcommSocketToServiceRecord(uuid);
                socket.connect();

                inputStream = socket.getInputStream();
                isConnected = true;

                postToMain(() -> {
                    applyStatus(R.string.status_connected, R.string.chip_online, R.color.gold);
                    btnConnect.setText(R.string.reconnect);
                });

                startListening();

            } catch (SecurityException se) {
                postToMain(() -> applyStatus(R.string.status_denied, R.string.chip_denied, R.color.rose));
            } catch (Exception e) {
                postToMain(() -> applyStatus(R.string.status_failed, R.string.chip_failed, R.color.rose));
                e.printStackTrace();
            }
        }).start();
    }

    private void startListening() {
        new Thread(() -> {
            byte[] buffer = new byte[1024];
            int bytes;
            StringBuilder msg = new StringBuilder();

            while (isConnected && getActivity() != null) {
                try {
                    bytes = inputStream.read(buffer);
                    String data = new String(buffer, 0, bytes);

                    msg.append(data);

                    if (data.contains("\n")) {
                        String finalMsg = msg.toString().trim();
                        msg.setLength(0);

                        if (finalMsg.contains("MOTION_DETECTED")) {
                            saveLogToDb("Motion");
                        } else if (finalMsg.contains("NO_MOTION")) {
                            saveLogToDb("Quiet");
                        }

                        handler.post(() -> updateUI(finalMsg));
                    }

                } catch (Exception e) {
                    isConnected = false;
                    handler.post(() -> applyStatus(R.string.status_disconnected, R.string.chip_lost, R.color.rose));
                }
            }
        }).start();
    }

    private void saveLogToDb(String event) {
        new Thread(() -> {
            String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(new Date());
            movementLogDao.insert(new MovementLog(event, currentTime));
            loadRecentEvent();
        }).start();
    }

    private void updateUI(String msg) {
        if (!isAdded()) return;
        if (msg.contains("MOTION_DETECTED")) {
            applyStatus(R.string.status_motion, R.string.chip_motion, R.color.rose);
        } else if (msg.contains("NO_MOTION")) {
            applyStatus(R.string.status_idle, R.string.chip_quiet, R.color.mint);
        } else {
            txtStatus.setText(msg);
        }
    }

    private void applyStatus(int statusRes, int labelRes, int colorRes) {
        if (!isAdded()) return;
        txtStatus.setText(statusRes);
        txtConnectionLabel.setText(labelRes);
        int color = ContextCompat.getColor(requireContext(), colorRes);
        txtConnectionLabel.setTextColor(color);
        viewPulse.setCardBackgroundColor(color);
    }

    private void postToMain(Runnable r) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(r);
        }
    }

    @Override
    public void onDestroyView() {
        MotionFx.stopPulse(viewPulse);
        super.onDestroyView();
    }
}
