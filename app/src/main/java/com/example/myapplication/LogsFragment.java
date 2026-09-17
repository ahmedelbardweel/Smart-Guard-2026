package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;

import java.util.List;

public class LogsFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView txtEmptyLogs;
    private MaterialToolbar toolbar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_logs, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewLogs);
        txtEmptyLogs = view.findViewById(R.id.txtEmptyLogs);
        toolbar = view.findViewById(R.id.toolbar);

        toolbar.setOnMenuItemClickListener(this::onMenuItemClick);
        toolbar.getMenu().findItem(R.id.action_clear_logs).setIcon(
            androidx.core.content.ContextCompat.getDrawable(requireContext(), R.drawable.ic_delete)
        );
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadLogs();
    }

    private boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.action_clear_logs) {
            confirmClearLogs();
            return true;
        }
        return false;
    }

    private void loadLogs() {
        new Thread(() -> {
            List<MovementLog> logs = AppDatabase.getInstance(requireContext()).movementLogDao().getAllLogs();
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> bindLogs(logs));
            }
        }).start();
    }

    private void bindLogs(List<MovementLog> logs) {
        if (!isAdded()) return;
        recyclerView.setAdapter(new LogsAdapter(logs));
        recyclerView.scheduleLayoutAnimation();
        boolean empty = logs.isEmpty();
        txtEmptyLogs.setVisibility(empty ? View.VISIBLE : View.GONE);
        toolbar.getMenu().findItem(R.id.action_clear_logs).setVisible(!empty);
        if (empty) {
            MotionFx.fadeUp(txtEmptyLogs, 80);
        }
        toolbar.setSubtitle(empty ? getString(R.string.logs_subtitle) : getString(R.string.logs_count, logs.size()));
    }

    private void confirmClearLogs() {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.logs_clear_title)
                .setMessage(R.string.logs_clear_message)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(R.string.logs_clear_confirm, (dialog, which) -> clearLogs())
                .show();
    }

    private void clearLogs() {
        new Thread(() -> {
            AppDatabase.getInstance(requireContext()).movementLogDao().deleteAll();
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    bindLogs(java.util.Collections.emptyList());
                    Toast.makeText(requireContext(), R.string.logs_cleared, Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }
}
