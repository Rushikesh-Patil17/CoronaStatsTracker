package com.example.coronastatstracker;

import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;


public class SecondFragment extends Fragment {

    public SecondFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_second, container, false);
        CardView learn_more_cv = rootView.findViewById(R.id.learn_more);
        CardView safety_measures_cv = rootView.findViewById(R.id.safety_measures);
        CardView symptoms_cv = rootView.findViewById(R.id.symptoms_cardview);
        CardView wash_hands_cv = rootView.findViewById(R.id.wash_hands_cardview);

        learn_more_cv.setOnClickListener(v -> launchIntent("https://www.mohfw.gov.in/pdf/Poster_Corona_ad_Eng.pdf"));
        safety_measures_cv.setOnClickListener(v -> launchIntent("https://www.mohfw.gov.in/pdf/ProtectivemeasuresEng.pdf"));
        symptoms_cv.setOnClickListener(v -> launchIntent("https://rushikeshpatil.imfast.io/COVID-19%20Symptoms.pdf"));
        wash_hands_cv.setOnClickListener(v -> launchIntent("https://rushikeshpatil.imfast.io/Stop%20Spread%20of%20Germs.pdf"));

        return rootView;
    }

    private void launchIntent(String url) {
        Uri pdfUri = Uri.parse(url);

        Intent intent = new Intent(Intent.ACTION_VIEW, pdfUri);
        intent.setComponent(new ComponentName("com.google.android.apps.docs",
                "com.google.android.apps.viewer.PdfViewerActivity"));

        if (intent.resolveActivity(requireActivity().getPackageManager()) != null)
            startActivity(intent);

        else
            Toast.makeText(requireActivity(), "No Suitable App Found", Toast.LENGTH_SHORT).show();
    }

}
