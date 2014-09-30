package com.zlei.checkInTool;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import br.com.condesales.EasyFoursquareAsync;
import br.com.condesales.criterias.CheckInCriteria;
import br.com.condesales.listeners.CheckInListener;
import br.com.condesales.models.Checkin;
import br.com.condesales.models.Venue;

public class CheckInActivity extends Activity {

    private Venue currentVenue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        
        Intent intent = getIntent();
        currentVenue = VenuesActivity.getNearbyVenues().get(intent.getIntExtra("selectedVenue", 0));
        TextView venueName_text = (TextView) this.findViewById(R.id.venue_name);
        TextView venueId_text = (TextView) this.findViewById(R.id.venue_id);
        TextView venueMajor_text = (TextView) this.findViewById(R.id.venue_major);
        TextView venueLocation_text = (TextView) this.findViewById(R.id.venue_location);
        TextView venueHereNow_text = (TextView) this.findViewById(R.id.venue_herenow);
        TextView venueStats_text = (TextView) this.findViewById(R.id.venue_stats);
        Button checkin_btn = (Button) this.findViewById(R.id.check_in_button);
        
        venueName_text.setText(currentVenue.getName());
        venueId_text.setText(currentVenue.getId());
        //venueMajor_text.setText(currentVenue.getMayor().getUser().getBio());
        venueLocation_text.setText("Lat: " + currentVenue.getLocation().getLat() + "\nLng: " + currentVenue.getLocation().getLng());
        venueHereNow_text.setText(currentVenue.getHereNow().getSummary());
        venueStats_text.setText(getStats(currentVenue));
        checkin_btn.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                EasyFoursquareAsync async = AccountActivity.getAsync();
                if(async != null)
                    checkin(currentVenue.getId());
                else
                    Toast.makeText(CheckInActivity.this, "Log in first!", Toast.LENGTH_LONG).show();
            }
        });
    }
    
    private void checkin(String venueId) {
        CheckInCriteria criteria = new CheckInCriteria();
        criteria.setBroadcast(CheckInCriteria.BroadCastType.PUBLIC);
        criteria.setVenueId(venueId);

        MainActivity.getAsync().checkIn(new CheckInListener() {
            @Override
            public void onCheckInDone(Checkin checkin) {
                Toast.makeText(CheckInActivity.this, "Done!", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(String errorMsg) {
                Toast.makeText(CheckInActivity.this, "Error!", Toast.LENGTH_LONG).show();
            }
        }, criteria);
    }
    
    private String getStats(Venue venue){
        String stats = "Checkins Count: " + venue.getStats().getCheckinsCount() + "\n" 
                    + "Users Count: " + venue.getStats().getUsersCount() + "\n"
                    + "Tips Count: " + venue.getStats().getTipCount() + "\n"
                    + "Created At: " + venue.getCreatedAt() + "\n"
                    + "TimeZone: " + venue.getTimeZone();
        return stats;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home){
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
