package com.example.coronastatstracker;

import android.content.AsyncTaskLoader;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class CasesLoader extends AsyncTaskLoader<List<Cases>> {
    private final String mUrl;

    public CasesLoader(@NonNull Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Nullable
    @Override
    public List<Cases> loadInBackground() {
        if (mUrl == null)
            return null;
        return QueryUtils.fetchEarthquakeData(mUrl);
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

}
