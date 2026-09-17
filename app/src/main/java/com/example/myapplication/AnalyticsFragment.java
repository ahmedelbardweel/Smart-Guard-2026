package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.List;

public class AnalyticsFragment extends Fragment {

    private TextView txtKpiTotal, txtKpiToday, txtKpiIdle, txtRiskBadge, txtPrediction;
    private DonutChartView chartDonut;
    private BarChartView chartHourly;
    private SparklineView chartWeek;
    private View panelKpi;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_analytics, container, false);

        txtKpiTotal = view.findViewById(R.id.txtKpiTotal);
        txtKpiToday = view.findViewById(R.id.txtKpiToday);
        txtKpiIdle = view.findViewById(R.id.txtKpiIdle);
        txtRiskBadge = view.findViewById(R.id.txtRiskBadge);
        txtPrediction = view.findViewById(R.id.txtPrediction);
        chartDonut = view.findViewById(R.id.chartDonut);
        chartHourly = view.findViewById(R.id.chartHourly);
        chartWeek = view.findViewById(R.id.chartWeek);
        panelKpi = view.findViewById(R.id.panelKpi);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadStats();
    }

    private void loadStats() {
        new Thread(() -> {
            List<MovementLog> logs = AppDatabase.getInstance(requireContext()).movementLogDao().getAllLogs();
            PredictionEngine.Insight insight = PredictionEngine.analyze(logs);
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> bindInsight(insight));
            }
        }).start();
    }

    private void bindInsight(PredictionEngine.Insight insight) {
        if (!isAdded()) return;
        MotionFx.countTo(txtKpiTotal, insight.totalMotions);
        MotionFx.countTo(txtKpiToday, insight.todayMotions);
        MotionFx.countTo(txtKpiIdle, insight.totalIdle);
        txtRiskBadge.setText(insight.riskLevel + " · " + insight.riskPercent + "%");
        txtPrediction.setText(insight.summary);
        chartDonut.setValues(insight.totalMotions, insight.totalIdle);
        chartHourly.setValues(insight.hourly);
        chartWeek.setData(insight.last7Days, insight.last7Labels);
        MotionFx.stagger(40,
                txtRiskBadge,
                txtPrediction,
                panelKpi,
                chartDonut,
                chartHourly,
                chartWeek);
    }
}
