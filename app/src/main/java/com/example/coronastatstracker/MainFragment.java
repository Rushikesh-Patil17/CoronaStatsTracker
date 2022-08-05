package com.example.coronastatstracker;

import android.Manifest;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MainFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Cases>> {

    private static final String QUERY_URL = "https://api.apify.com/v2/key-value-stores/toDWvRj1JpTXiM8FF/records/LATEST?disableRedirect=true";
    private static final int LOADER_ID = 1001;

    private TextView confirmed, deceased, recovered;
    private TextView confirmed_mh, deceased_mh, recovered_mh;

    private NonScrollableListView listView;
    private CasesAdapter mAdapter;
    private TextView lastUpdatedView;
    private ProgressBar progressBar;
    View rootView;

    static String state;

    private FusedLocationProviderClient fusedLocationClient;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        startOperation();
    }

    @Override
    public void onStart() {
        super.onStart();
        startOperation();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_main, container, false);

        confirmed = rootView.findViewById(R.id.confirmed_cases);
        deceased = rootView.findViewById(R.id.deceased_cases);
        recovered = rootView.findViewById(R.id.recovered_cases);

        confirmed_mh = rootView.findViewById(R.id.confirmed_cases_mh);
        deceased_mh = rootView.findViewById(R.id.deceased_cases_mh);
        recovered_mh = rootView.findViewById(R.id.recovered_cases_mh);

        rootView.findViewById(R.id.refresh_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startOperation();
            }
        });

        lastUpdatedView = rootView.findViewById(R.id.last_updated);

        progressBar = rootView.findViewById(R.id.progress_bar);

        listView = rootView.findViewById(R.id.list);
        View header = getLayoutInflater().inflate(R.layout.listview_header, null);
        listView.addHeaderView(header);

        mAdapter = new CasesAdapter(getActivity(), new ArrayList<Cases>());
        listView.setAdapter(mAdapter);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        state = null;

        startOperation();

        return rootView;
    }

    private void getStateData() {
        LocationTask task = new LocationTask();
        task.execute();
    }

    private void startOperation() {
        getStateData();

        if (hasConnection()) {
            loadData();
        } else
            disableLoading();
    }

    private void disableLoading() {
        rootView.findViewById(R.id.layout).setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        rootView.findViewById(R.id.no_con_view).setVisibility(View.VISIBLE);
    }

    private void loadData() {
        LoaderManager loaderManager = Objects.requireNonNull(getActivity()).getLoaderManager();

        loaderManager.initLoader(LOADER_ID, null, this);

        progressBar.setVisibility(View.VISIBLE);
        rootView.findViewById(R.id.no_con_view).setVisibility(View.GONE);
    }

    @Override
    public Loader<List<Cases>> onCreateLoader(int id, Bundle args) {
        return new CasesLoader(requireActivity(), QUERY_URL);
    }

    @Override
    public void onLoadFinished(Loader<List<Cases>> loader, List<Cases> data) {
        if (data != null && hasConnection() && !data.isEmpty()) {
            mAdapter.clear();
            progressBar.setVisibility(View.GONE);
            rootView.findViewById(R.id.layout).setVisibility(View.VISIBLE);

            Cases in = data.get(0);
            Cases mh = getNativeState(data);
            Cases reassignedData = getReassignedData(data);

            if (in != null) {
                confirmed.setText(String.format("%s", in.getConfirmed()));
                recovered.setText(String.format("%s", in.getRecovered()));
                deceased.setText(String.format("%s", in.getDeceased()));
            }

            if (mh != null) {
                confirmed_mh.setText(String.format("%s", mh.getConfirmed()));
                recovered_mh.setText(String.format("%s", mh.getRecovered()));
                deceased_mh.setText(String.format("%s", mh.getDeceased()));

                TextView state_view = rootView.findViewById(R.id.state_name);
                state_view.setText(state);
            } else {
                rootView.findViewById(R.id.state_data).setVisibility(View.GONE);
                rootView.findViewById(R.id.divider).setVisibility(View.GONE);
            }


            if (reassignedData != null) {
                TextView reassigned_confirmed = rootView.findViewById(R.id.confirmed_cases_reassigned);
                TextView reassigned_recovered = rootView.findViewById(R.id.recovered_cases_reassigned);
                TextView reassigned_deceased = rootView.findViewById(R.id.deceased_cases_reassigned);

                reassigned_confirmed.setText(String.format("%s", reassignedData.getConfirmed()));
                reassigned_recovered.setText(String.format("%s", reassignedData.getRecovered()));
                reassigned_deceased.setText(String.format("%s", reassignedData.getDeceased()));

                data.remove(reassignedData);
            } else {
                rootView.findViewById(R.id.reassigned_data).setVisibility(View.GONE);
                rootView.findViewById(R.id.divider1).setVisibility(View.GONE);
            }

            Collections.sort(data);

            for (int i = 1; i < data.size(); i++) {
                if (data.get(i).getState().equalsIgnoreCase(state) || data.get(i).getState().equalsIgnoreCase("india"))
                    continue;

                mAdapter.add(data.get(i));
                mAdapter.notifyDataSetChanged();
            }

            if (QueryUtils.lastUpdated != null)
                lastUpdatedView.setText(QueryUtils.lastUpdated);
        } else {
            disableLoading();
        }
    }

    private Cases getReassignedData(List<Cases> cases) {
        Cases reassignedCases = null;

        for (Cases c : cases) {
            if (c.getState().equalsIgnoreCase("cases being reassigned to states")) {
                reassignedCases = c;
                break;
            }
        }

        return reassignedCases;
    }

    private Cases getNativeState(List<Cases> cases) {
        Cases nativeCases = null;

        for (Cases c : cases) {
            if (c.getState().equalsIgnoreCase(state)) {
                nativeCases = c;
                break;
            }
        }

        return nativeCases;
    }


    @Override
    public void onLoaderReset(Loader<List<Cases>> loader) {
        clearEverything();
    }

    private void clearEverything() {
        mAdapter.clear();
        lastUpdatedView.setText("");
        confirmed.setText("");
        recovered.setText("");
        deceased.setText("");
        confirmed_mh.setText("");
        recovered_mh.setText("");
        deceased_mh.setText("");

        state = null;
    }

    private boolean hasConnection() {
        ConnectivityManager cm =
                (ConnectivityManager) requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    private class LocationTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1001);
            } else {
                fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(requireActivity(), location -> {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                try {
                                    Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
                                    List<Address> address = geocoder.getFromLocation(location.getLatitude(),
                                            location.getLongitude(), 1);

                                    if (address.size() > 0) {
                                        state = address.get(0).getAdminArea();
                                        Toast.makeText(getActivity(), state, Toast.LENGTH_SHORT).show();
                                    }

                                } catch (IOException e) {
                                    Toast.makeText(getActivity(), "Exception encountered!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
            return null;
        }
    }
}
