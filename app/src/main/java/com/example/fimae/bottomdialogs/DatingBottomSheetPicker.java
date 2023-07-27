package com.example.fimae.bottomdialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fimae.R;
import com.example.fimae.adapters.Report.ReportAdapter;
import com.example.fimae.adapters.Report.ReportAdapterItem;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.slider.RangeSlider;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

public class DatingBottomSheetPicker  extends BottomSheetDialogFragment {
    String title;
    String subtitle;
    ArrayList<ReportAdapterItem> reportAdapterItems;

    int selectedRadioIndex;

    int minValue;
    int maxValue;
    int currentMinValue;
    int currentMaxValue;
    String rangeTitle;
    String unit;


    public interface OnDatingtDialogListener {
        void onDatingRadioSubmit(ReportAdapterItem reportAdapterItem);
        void onDatingRangeSliderSubmit(int min, int max);
    }

    OnDatingtDialogListener onDatingtDialogListener;
    public void setOnReportDialogListener(OnDatingtDialogListener onDatingtDialogListener) {
        this.onDatingtDialogListener = onDatingtDialogListener;
    }


    private DatingBottomSheetPicker() {
        super();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        boolean isRangeSlider = false;
        if (getArguments() != null) {
            title = getArguments().getString("title");
            subtitle = getArguments().getString("subtitle");
            reportAdapterItems = (ArrayList<ReportAdapterItem>) getArguments().getSerializable("reportAdapterItems");
            selectedRadioIndex = getArguments().getInt("selectedRadioIndex");
            minValue = getArguments().getInt("minValue");
            currentMinValue = getArguments().getInt("currentMinValue");
            currentMaxValue = getArguments().getInt("currentMaxValue");
            maxValue = getArguments().getInt("maxValue");
            unit = getArguments().getString("unit");
            rangeTitle = getArguments().getString("rangeTitle");
        }
        BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        View view = LayoutInflater.from(this.getContext()).inflate(R.layout.bottom_sheet_dating_picker, null);
        bottomSheetDialog.setContentView(view);
        MaterialButton button = view.findViewById(R.id.dating_bottom_sheet_button);
        RecyclerView listView = view.findViewById(R.id.dating_bottom_sheet_list_view);
        TextView textViewTitle = view.findViewById(R.id.dating_bottom_sheet_title);
        TextView textViewSubtitle = view.findViewById(R.id.dating_bottom_sheet_subtitle);
        ConstraintLayout rangeSliderLayout = view.findViewById(R.id.dating_bottom_sheet_range_layout);
        if (title != null && !title.trim().isEmpty())
            textViewTitle.setText(title);
        if (subtitle != null && !subtitle.trim().isEmpty())
            textViewSubtitle.setText(subtitle);
        if (reportAdapterItems == null){
            listView.setVisibility(View.GONE);
            rangeSliderLayout.setVisibility(View.VISIBLE);
            RangeSlider rangeSlider = view.findViewById(R.id.dating_bottom_sheet_range_slider);
            TextView textViewRangeTitle = view.findViewById(R.id.dating_bottom_sheet_range_title);
            TextView textViewRangeResult = view.findViewById(R.id.dating_bottom_sheet_range_result);
            textViewRangeTitle.setText(rangeTitle);
            rangeSlider.setValueFrom(minValue);
            rangeSlider.setValueTo(maxValue);
            rangeSlider.setValues((float) currentMinValue, (float) currentMaxValue);
            rangeSlider.setStepSize(1);
            textViewRangeResult.setText(currentMinValue + " - " + currentMaxValue + " " + unit);
            rangeSlider.addOnChangeListener((slider, value, fromUser) -> {
                textViewRangeResult.setText(slider.getValues().get(0).intValue() + " - " + slider.getValues().get(1).intValue() + " " + unit);
            });
            button.setOnClickListener(v -> {
                if (onDatingtDialogListener != null) {
                    onDatingtDialogListener.onDatingRangeSliderSubmit(rangeSlider.getValues().get(0).intValue(), rangeSlider.getValues().get(1).intValue());
                    bottomSheetDialog.dismiss();
                }
            });
        } else {
            rangeSliderLayout.setVisibility(View.GONE);
            //Create listView content report with checkboxes
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            listView.setLayoutManager(linearLayoutManager);
            ReportAdapter adapter = new ReportAdapter(reportAdapterItems);
            listView.setAdapter(adapter);
            button.setOnClickListener(v -> {
                if (onDatingtDialogListener != null) {
                    ReportAdapterItem reportAdapterItem = adapter.getSelectedReportAdapterItem();
                    if (reportAdapterItem != null) {
                        onDatingtDialogListener.onDatingRadioSubmit(reportAdapterItem);
                        bottomSheetDialog.dismiss();
                    } else
                        Toast.makeText(getContext(), "Vui lòng chọn", Toast.LENGTH_SHORT).show();
                }

            });
            adapter.setCurrentSelectedIndex(selectedRadioIndex);
        }
        button.setText("Chọn");

        return bottomSheetDialog;
    }


    static public DatingBottomSheetPicker.Builder builder() {
        return new DatingBottomSheetPicker.Builder();
    }

    //Create builder for report dialog
    public static class Builder {
        private String title;
        private String subtitle;
        private ArrayList<ReportAdapterItem> reportAdapterItems;
        private DatingBottomSheetPicker.OnDatingtDialogListener onDatingtDialogListener;
        int selectedRadioIndex;

        int minValue;
        int maxValue;

        int currentMinValue;
        int currentMaxValue;
        String unit;
        String rangeTitle;
        public DatingBottomSheetPicker.Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public DatingBottomSheetPicker.Builder setSubtitle(String subtitle) {
            this.subtitle = subtitle;
            return this;
        }
        public DatingBottomSheetPicker.Builder setReportAdapterItems(ArrayList<ReportAdapterItem> reportAdapterItems) {
            this.reportAdapterItems = reportAdapterItems;
            return this;
        }
        public DatingBottomSheetPicker.Builder setReportAdapterItems(ArrayList<ReportAdapterItem> reportAdapterItems, int selectedRadioIndex) {
            this.reportAdapterItems = reportAdapterItems;
            this.selectedRadioIndex = selectedRadioIndex;
            return this;
        }

        public DatingBottomSheetPicker.Builder setOnDatingtDialogListener(OnDatingtDialogListener onDatingtDialogListener) {
            this.onDatingtDialogListener = onDatingtDialogListener;
            return this;
        }

        public DatingBottomSheetPicker.Builder setUpRangeSlider(int minValue, int maxValue, String unit, String rangeTitle) {
            this.minValue = minValue;
            this.maxValue = maxValue;
            this.currentMinValue = minValue;
            this.currentMaxValue = maxValue;
            this.unit = unit;
            this.rangeTitle = rangeTitle;
            return this;
        }
        public DatingBottomSheetPicker.Builder setUpRangeSlider(int minValue, int maxValue, String unit, String rangeTitle, int currentMinValue, int currentMaxValue) {
            this.minValue = minValue;
            this.maxValue = maxValue;
            this.unit = unit;
            this.rangeTitle = rangeTitle;
            this.currentMinValue = currentMinValue;
            this.currentMaxValue = currentMaxValue;
            return this;
        }

        public DatingBottomSheetPicker build() {
            DatingBottomSheetPicker bottomSheetPicker = new DatingBottomSheetPicker();
            Bundle bundle = new Bundle();
            bundle.putString("title", title);
            bundle.putString("subtitle", subtitle);
            bundle.putSerializable("reportAdapterItems", reportAdapterItems);
            bundle.putInt("selectedRadioIndex", selectedRadioIndex);
            bundle.putInt("minValue", minValue);
            bundle.putInt("maxValue", maxValue);
            bundle.putString("unit", unit);
            bundle.putInt("currentMinValue", currentMinValue);
            bundle.putInt("currentMaxValue", currentMaxValue);
            bottomSheetPicker.setOnReportDialogListener(onDatingtDialogListener);
            bottomSheetPicker.setArguments(bundle);
            return bottomSheetPicker;
        }
    }
}
