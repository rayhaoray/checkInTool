package com.zlei.checkInTool;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;
import br.com.condesales.EasyFoursquareAsync;
import br.com.condesales.listeners.AccessTokenRequestListener;
import br.com.condesales.listeners.ImageRequestListener;
import br.com.condesales.listeners.UserInfoRequestListener;
import br.com.condesales.models.User;
import br.com.condesales.tasks.users.UserImageRequest;

public class AccountActivity extends Activity implements
        AccessTokenRequestListener, ImageRequestListener, OnClickListener{

    private static EasyFoursquareAsync async;
    private ImageButton userImage;
    private ViewSwitcher viewSwitcher;
    private TextView userName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        userImage = (ImageButton) findViewById(R.id.image_button);
        viewSwitcher = (ViewSwitcher) findViewById(R.id.viewSwitcher1);
        userName = (TextView) findViewById(R.id.textView1);
        //ask for access
        async = new EasyFoursquareAsync(this);
        async.requestAccess(this);
    }


    @Override
    public void onError(String errorMsg) {
        // Do something with the error message
        Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onAccessGrant(String accessToken) {
        async.getUserInfo(new UserInfoRequestListener() {

            @Override
            public void onError(String errorMsg) {
                // Some error getting user info
                Toast.makeText(AccountActivity.this, errorMsg, Toast.LENGTH_LONG)
                        .show();
            }

            @Override
            public void onUserInfoFetched(User user) {
                if (user.getBitmapPhoto() == null) {
                    UserImageRequest request = new UserImageRequest(
                            AccountActivity.this, AccountActivity.this);
                    request.execute(user.getPhoto());
                } else {
                    userImage.setImageBitmap(user.getBitmapPhoto());
                    userImage.setOnClickListener(AccountActivity.this);
                }
                userName.setText(user.getFirstName() + " " + user.getLastName());
                viewSwitcher.showNext();
                Toast.makeText(AccountActivity.this, "Got it!", Toast.LENGTH_LONG)
                        .show();
            }
        });
    }

    @Override
    public void onImageFetched(Bitmap bmp) {
        userImage.setImageBitmap(bmp);
    }

    public static EasyFoursquareAsync getAsync(){
        return async;
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

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        async = new EasyFoursquareAsync(this);
        async.revokeAccess();
        async.requestAccess(this);
    }
}
