package com.example.andrea22.gamehunt;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Simone on 29/06/2016.
 */
public class SingleHuntView extends Activity {

    TextView singleTitle;
    TextView singleDate;
    ImageView singleImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.hunt);
        singleTitle = (TextView)findViewById(R.id.single_title);
        singleDate = (TextView)findViewById(R.id.single_date);
        singleImage = (ImageView)findViewById(R.id.single_image);
/**
        singleTitle.setText("TITOLO CACCIA");
        singleDate.setText("data caccia");
        singleImage.setImageResource(R.drawable.she);
 **/
    }
}
