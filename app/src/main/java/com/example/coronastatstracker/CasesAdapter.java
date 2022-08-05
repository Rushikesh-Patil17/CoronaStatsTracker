package com.example.coronastatstracker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class CasesAdapter extends ArrayAdapter<Cases> {

    public CasesAdapter(Context context, List<Cases> cases) {
        super(context, 0, cases);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;

        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext())
                    .inflate(R.layout.cases_list_item, parent, false);
        }

        Cases cases = (Cases) getItem(position);

        TextView stateView = listItemView.findViewById(R.id.state);
        TextView confirmedView = listItemView.findViewById(R.id.confirmed);
        TextView recoveredView = listItemView.findViewById(R.id.recovered);
        TextView deceasedView = listItemView.findViewById(R.id.deceased);

        assert cases != null;
        stateView.setText(cases.getState());
        confirmedView.setText(String.format("%s", cases.getConfirmed()));
        recoveredView.setText(String.format("%s", cases.getRecovered()));
        deceasedView.setText(String.format("%s", cases.getDeceased()));
        return listItemView;
    }
}
