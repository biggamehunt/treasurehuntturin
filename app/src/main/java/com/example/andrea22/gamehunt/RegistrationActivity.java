package com.example.andrea22.gamehunt;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.example.andrea22.gamehunt.Database.DBHelper;
import com.example.andrea22.gamehunt.utility.RetrieveFeedTask;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p/>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class RegistrationActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
    }

    public void registration (View view) {
        EditText usernameview = (EditText) findViewById(R.id.registration_username);
        String username =usernameview.getText().toString();
        EditText passwordview = (EditText) findViewById(R.id.registration_password);
        String password = passwordview.getText().toString();
        EditText confirmview = (EditText) findViewById(R.id.confirm_password);
        String confirm = confirmview.getText().toString();
        EditText emailview = (EditText) findViewById(R.id.registration_email);
        String email = emailview.getText().toString();

        if(username.length() < 4){
            CharSequence text = getString(R.string.userLength_error);
            Toast toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
            toast.show();
        } else if(password.length() < 7){
            CharSequence text = getString(R.string.passLength_error);
            Toast toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
            toast.show();
        } else if(email.length() < 4) {
            CharSequence text = getString(R.string.emailLength_error);
            Toast toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
            toast.show();
        } else if (!password.equals(confirm)) {
            CharSequence text = getString(R.string.confirmpass_error);
            Toast toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
            toast.show();
        } else {

            try {

                try {
                    String u = "http://jbossews-treasurehunto.rhcloud.com/ProfileOperation?action=registration&username=" + username + "&password=" + password + "&email=" + email;
                    String res = new RetrieveFeedTask().execute(u).get();
                    if (!res.equals("0")) {
                        DBHelper myHelper = DBHelper.getInstance(getApplicationContext());
                        SQLiteDatabase db = myHelper.getWritableDatabase();
                        myHelper.createDB(db, res);
                        Intent intent = new Intent(this, HuntListActivity.class);
                        startActivity(intent);
                    } else {
                        CharSequence text = getString(R.string.reg_error);
                        int duration = Toast.LENGTH_SHORT;

                        Toast toast = Toast.makeText(this, text, duration);
                        toast.show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


            } catch (Exception e) {
                Log.d("test debug", "eccezione: " + e.getMessage());
            }
        }
    }

    public void turn_back_login (View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
    }


}
