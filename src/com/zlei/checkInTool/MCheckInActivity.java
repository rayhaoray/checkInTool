package com.zlei.checkInTool;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MCheckInActivity extends Activity {

    private MVenues currentVenue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        currentVenue = MPLACESActivity.getVenues().get(intent.getIntExtra("selectedVenue", 0));
        TextView venueName_text = (TextView) this.findViewById(R.id.venue_name);
        TextView venueId_text = (TextView) this.findViewById(R.id.venue_id);
        TextView venueMajor_text = (TextView) this.findViewById(R.id.venue_major);
        TextView venueLocation_text = (TextView) this.findViewById(R.id.venue_location);
        TextView venueHereNow_text = (TextView) this.findViewById(R.id.venue_herenow);
        TextView venueStats_text = (TextView) this.findViewById(R.id.venue_stats);
        Button checkin_btn = (Button) this.findViewById(R.id.check_in_button);

        venueName_text.setText(currentVenue.getName());
        venueId_text.setText(currentVenue.getName());
        venueLocation_text.setText("Lat: " + currentVenue.getLat() + "\nLng: " + currentVenue.getLng());
        //venueMajor_text.setText(currentVenue.getMayor().getUser().getBio());
        venueHereNow_text.setText(currentVenue.getAddress());
        //venueStats_text.setText(getStats(currentVenue));
        checkin_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //checkin(currentVenue.getId());
            }
        });
    }

    private void checkin(String venueId) {
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
