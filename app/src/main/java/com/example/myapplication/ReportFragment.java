package com.example.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.materialswitch.MaterialSwitch;

import java.util.List;

public class ReportFragment extends Fragment {

    private TextView txtReportBody, txtReportStatus, txtLastReport;
    private MaterialButton btnShare, btnSendReportNow;
    private MaterialSwitch switchWeeklyReport;
    private boolean pendingSendReport = false;
    private String currentReportText = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_report, container, false);

        txtReportBody = view.findViewById(R.id.txtReportBody);
        txtReportStatus = view.findViewById(R.id.txtReportStatus);
        txtLastReport = view.findViewById(R.id.txtLastReport);
        btnShare = view.findViewById(R.id.btnShareReport);
        btnSendReportNow = view.findViewById(R.id.btnSendReportNow);
        switchWeeklyReport = view.findViewById(R.id.switchWeeklyReport);

        setupWeeklyReportCard();
        loadReport();
        
        btnShare.setOnClickListener(v -> MotionFx.press(v, () -> {
            if (currentReportText.isEmpty()) return;
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("text/plain");
            share.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.report_title));
            share.putExtra(Intent.EXTRA_TEXT, currentReportText);
            startActivity(Intent.createChooser(share, getString(R.string.report_share)));
        }));
        
        btnSendReportNow.setOnClickListener(v -> MotionFx.press(v, this::sendWeeklyReportNow));

        return view;
    }

    private void setupWeeklyReportCard() {
        switchWeeklyReport.setChecked(ReportPrefs.isEnabled(requireContext()));
        switchWeeklyReport.setOnCheckedChangeListener((button, enabled) -> {
            if (enabled && !hasNotificationPermission()) {
                pendingSendReport = false;
                requestNotificationPermission();
            }
            ReportPrefs.setEnabled(requireContext(), enabled);
            ReportScheduler.applyEnabledState(requireContext(), enabled);
            refreshReportCard();
        });
        refreshReportCard();
    }

    private void refreshReportCard() {
        if (!isAdded()) return;
        boolean enabled = ReportPrefs.isEnabled(requireContext());
        txtReportStatus.setText(enabled ? getString(R.string.report_card_hint) : getString(R.string.report_disabled));
        txtLastReport.setText(ReportPrefs.lastSentLabel(requireContext()));
    }

    private void loadReport() {
        new Thread(() -> {
            List<MovementLog> logs = AppDatabase.getInstance(requireContext()).movementLogDao().getAllLogs();
            PredictionEngine.WeeklyReport report = PredictionEngine.buildWeeklyReport(logs);
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> bindReport(report.fullText));
            }
        }).start();
    }

    private void bindReport(String fullText) {
        if (!isAdded()) return;
        currentReportText = fullText;
        txtReportBody.setText(fullText);
    }
    
    private void sendWeeklyReportNow() {
        if (!hasNotificationPermission()) {
            pendingSendReport = true;
            requestNotificationPermission();
            Toast.makeText(requireContext(), R.string.report_permission_needed, Toast.LENGTH_SHORT).show();
            return;
        }
        new Thread(() -> {
            List<MovementLog> logs = AppDatabase.getInstance(requireContext()).movementLogDao().getAllLogs();
            PredictionEngine.WeeklyReport report = PredictionEngine.buildWeeklyReport(logs);
            ReportNotifier.send(requireContext(), report);
            ReportPrefs.markSent(requireContext());
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    refreshReportCard();
                    bindReport(report.fullText);
                });
            }
        }).start();
    }
    
    private boolean hasNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS)
                    == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 2);
        }
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 2 || pendingSendReport) {
            if (hasNotificationPermission() && pendingSendReport) {
                pendingSendReport = false;
                sendWeeklyReportNow();
            } else if (!hasNotificationPermission() && switchWeeklyReport.isChecked()) {
                Toast.makeText(requireContext(), R.string.report_permission_needed, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
