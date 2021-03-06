package com.zlei.checkInTool;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;

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
        Button go_mplaces_btn = (Button) this.findViewById(R.id.go_mplaces_button);

        venueName_text.setText(currentVenue.getName());
        venueId_text.setText(currentVenue.getId());
        //venueMajor_text.setText(currentVenue.getMayor().getUser().getBio());
        venueLocation_text.setText("Lat: " + currentVenue.getLocation().getLat() + "\nLng: " + currentVenue.getLocation().getLng());
        //venueHereNow_text.setText(currentVenue.getHereNow().getSummary());
        //venueStats_text.setText(getStats(currentVenue));
        checkin_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                EasyFoursquareAsync async = AccountActivity.getAsync();
                //if(async != null)
                //checkin(currentVenue.getId());
                //else
                //Toast.makeText(CheckInActivity.this, "Log in first!", Toast.LENGTH_LONG).show();
            }
        });

        DecimalFormat df = new DecimalFormat("0.000");
        String curLat = df.format(currentVenue.getLocation().getLat());
        String curLng = df.format(currentVenue.getLocation().getLng());
        final String[] curCoor = new String[3];
        curCoor[0] = curLat;
        curCoor[1] = curLng;
        curCoor[2] = "checkable";
        if (MVenuesActivity.venueNames.contains(currentVenue.getName()) || MVenuesActivity.venueCoordinates.contains(curCoor)) {
            Toast.makeText(CheckInActivity.this, "mPLACES Check In Available!", Toast.LENGTH_LONG).show();
            pushNotification("notifyCheckIn");
            go_mplaces_btn.setVisibility(View.VISIBLE);
            go_mplaces_btn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(CheckInActivity.this, MCheckInActivity.class);
                    i.putExtra("selectedVenue", MVenuesActivity.venueNames.indexOf(currentVenue.getName()));
                    //i.putExtra("selectedVenue", MVenuesActivity.venueCoordinates.indexOf(curCoor));
                    startActivity(i);
                }
            });
        } else
            Toast.makeText(CheckInActivity.this, "mPLACES not available!", Toast.LENGTH_SHORT).show();

    }

    private void checkin(String venueId) {
        CheckInCriteria criteria = new CheckInCriteria();
        criteria.setBroadcast(CheckInCriteria.BroadCastType.PUBLIC);
        criteria.setVenueId(venueId);

        MainActivity.getAsync().checkIn(new CheckInListener() {
            @Override
            public void onCheckInDone(Checkin checkin) {
                Toast.makeText(CheckInActivity.this, "Success!", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(String errorMsg) {
                Toast.makeText(CheckInActivity.this, "Error!", Toast.LENGTH_LONG).show();
            }
        }, criteria);
    }

    private String getStats(Venue venue) {
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
        if (id == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void pushNotification(String action) {
        NotificationCompat.Builder mBuilder;
        Intent resultIntent;
        if (action.equals("notifyCheckIn")) {
            mBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setContentTitle("CheckIn Available!")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentText("CheckIn mPLACES!");
            resultIntent = new Intent(this, MCheckInActivity.class);
            resultIntent.putExtra("selectedVenue", MVenuesActivity.venueNames.indexOf(currentVenue.getName()));
        } else {
            mBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setContentTitle("New Achievement!")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentText("Claim your achievement");
            resultIntent = new Intent(this, MainActivity.class);
        }
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        int mId = 0;
        mNotificationManager.notify(mId, mBuilder.build());
    }
}
