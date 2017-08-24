package io.github.domig.medicalnotes.activity;

/**
 * Created by Win on 10.08.2017.
 */

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import io.github.domig.medicalnotes.R;

public class ListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    ListView listView;
    ImageView imageview;
    ArrayAdapter<String> aAdapter;
    TextView textView;

    //    Intent i = getIntent();
    String partSelected;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        partSelected = getIntent().getExtras().getString("part");

        setContentView(R.layout.activity_list);

        imageview = (ImageView) findViewById(R.id.imageview);
        listView = (ListView) findViewById(R.id.partsList);
        textView = (TextView) findViewById(R.id.bodypartTextView);

        loadPictureAndList(partSelected);

        listView.setOnItemClickListener(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void loadPictureAndList(String partSelected) {
        String[] array;
        switch(partSelected){
            case "head":
                imageview.setImageResource(R.drawable.head);
                array = getResources().getStringArray(R.array.head_array);
                aAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, array);
                listView.setAdapter(aAdapter);
                textView.setText(partSelected);
                break;
            case "torso":
                imageview.setImageResource(R.drawable.torso);
                array = getResources().getStringArray(R.array.torso_array);
                aAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, array);
                listView.setAdapter(aAdapter);
                textView.setText(partSelected);
                break;
            case "leftLeg":
                imageview.setImageResource(R.drawable.leftleg);
                array = getResources().getStringArray(R.array.leg_array);
                aAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, array);
                listView.setAdapter(aAdapter);
                textView.setText(partSelected);
                break;
            case "rightLeg":
                imageview.setImageResource(R.drawable.rightleg);
                array = getResources().getStringArray(R.array.leg_array);
                aAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, array);
                listView.setAdapter(aAdapter);
                textView.setText(partSelected);
                break;
            case "rightArm":
                imageview.setImageResource(R.drawable.rightarm);
                array = getResources().getStringArray(R.array.arm_array);
                aAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, array);
                textView.setText(partSelected);
                break;
            case "leftArm":
                imageview.setImageResource(R.drawable.leftarm);
                array = getResources().getStringArray(R.array.arm_array);
                aAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, array);
                textView.setText(partSelected);
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String lR;
        String s = (String)(listView.getItemAtPosition(position));
        Intent i2 = new Intent(this, EditActivity.class);

        Bundle extras = new Bundle();
        extras.putString("part", s);
        //i2.putExtra("part", s);

        if(partSelected.equals("leftArm") || partSelected.equals("leftLeg")){
            lR = "l";
            extras.putString("leftOrRight", lR);
        }

        if(partSelected.equals("rightArm") || partSelected.equals("rightLeg")){
            lR = "r";
            extras.putString("leftOrRight", lR);
        }
        i2.putExtras(extras);
        startActivity(i2);
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

