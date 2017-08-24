package io.github.domig.medicalnotes.activity;

/**
 * Created by Win on 10.08.2017.
 */

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import io.github.domig.medicalnotes.R;

public class EditActivity extends AppCompatActivity {

    TextView textView;
    TextView editText;
    TextView editText2;
    String s;
    String lR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        editText = (TextView) findViewById(R.id.descriptionTextView);
        textView = (TextView) findViewById(R.id.bodypartTextView);
        editText2 = (TextView) findViewById(R.id.drugsTextView);

        s = getIntent().getExtras().getString("part");
        lR = getIntent().getExtras().getString("leftOrRight");

        if(lR == null){
            lR = "x";
        }

        editText.setText(MainActivity.loadDescFromDB(s, lR));
        editText2.setText(MainActivity.loadDrugsFromDB(s, lR));

        textView.setText(s);
    }

    @Override
    public void onBackPressed() {
        MainActivity.save(s, lR, editText.getText().toString(), editText2.getText().toString());
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            // finish the activity
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
