package com.example.coronastatstracker;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {

    MyDialog dialog;
    FloatingActionButton fab;
    int pos = 0;
    CategoryAdapter adapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the content of the activity to use the activity_main.xml layout file
        setContentView(R.layout.activity_category);

        Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        // Find the view pager that will allow the user to swipe between fragments
        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);

        // Create an adapter that knows which fragment should be shown on each page
        adapter = new CategoryAdapter(getSupportFragmentManager());

        // Set the adapter onto the view pager
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    fab.setImageResource(R.drawable.ic_phone);
                    pos = 0;
                } else if (position == 1) {
                    fab.setImageResource(R.drawable.ic_visit);
                    pos = 1;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        // Find the tab layout that shows the tabs
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setSelectedTabIndicatorHeight(10);

        // Connect the tab layout with the view pager. This will
        //   1. Update the tab layout when the view pager is swiped
        //   2. Update the view pager when a tab is selected
        //   3. Set the tab layout's tab names with the view pager's adapter's titles
        //      by calling onPageTitle()


        fab = findViewById(R.id.fab);

        fab.setOnClickListener(v -> {

            if (pos == 0) {
                dialog = new MyDialog();
                dialog.show(getSupportFragmentManager(), "my_dialog");
            } else if (pos == 1) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://www.mohfw.gov.in"));

                if (intent.resolveActivity(getPackageManager()) != null) {
                    Fragment fragment = adapter.getItem(0);
                    startActivityFromFragment(fragment, intent, 1000);
                } else {
                    Toast.makeText(MainActivity.this, "No Suitable App Found!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        tabLayout.setupWithViewPager(viewPager);
    }

    public static class MyDialog extends AppCompatDialogFragment {
        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
            LayoutInflater inflater = requireActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.dialog_layout, null);

            view.findViewById(R.id.india_helpline).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    call("+911123978046");
                }
            });

            final String hel = HelplineUtils.getFromArea(MainFragment.state);

            if (hel == null)
                view.findViewById(R.id.area_helpline).setVisibility(View.GONE);

            else {
                TextView state = view.findViewById(R.id.area_state);
                TextView helpline = view.findViewById(R.id.helpline_state);

                state.setText(MainFragment.state);
                helpline.setText(hel);

                view.findViewById(R.id.area_helpline).setOnClickListener(v -> call(hel));
            }

            builder.setTitle("Select Helpline");
            builder.setView(view);
            return builder.create();
        }

        private void call(String no) {
            Intent i = new Intent(Intent.ACTION_CALL);
            i.setData(Uri.parse("tel:" + no));
            requireActivity().startActivity(i);

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (dialog != null)
            dialog.dismiss();
    }
}
