package com.test.breakout.breakoutgame.highScores;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.test.breakout.breakoutgame.MainActivity;
import com.test.breakout.breakoutgame.R;

import java.util.Calendar;
import java.util.Date;


public class create extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create);

        Intent intent = getIntent();
        long hs=intent.getLongExtra("highScoreVal",0);
        final String score1=Long.toString(hs);
        ((EditText) findViewById(R.id.editText2)).setText(score1);

        //Initializing UI elements by associating then with their xml Ids
        Button button_submit=(Button)findViewById(R.id.button_submit);
        Button button_cancel=(Button)findViewById(R.id.button_cancel);
        final EditText name = (EditText)findViewById(R.id.editText1);
        final EditText score = (EditText)findViewById(R.id.editText2);
        final DatePicker date = (DatePicker)findViewById(R.id.datePicker);



        //Action on clicking the save button (includes validation of fields and file write)
        button_submit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int personDateDay = date.getDayOfMonth();
                int personDateMonth = date.getMonth();
                int personDateYear = date.getYear();
                Calendar c = Calendar.getInstance();
                Date date=c.getTime();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);
                String dat = Integer.toString(personDateDay) + "/" + Integer.toString(personDateMonth) + "/" + Integer.toString(personDateYear);

                //validation for name field
                if(name.getText().toString().isEmpty() || name.getText().toString().contains(" ")) {
                    if (name.getText().toString().isEmpty()) {
                    name.setError("Please enter your name");
                    } else {
                        name.setError("Please enter without spaces and special characters");
                    }

                    return;
                }

                //validation for score field
                if(score.getText().toString().isEmpty()) {
                    score.setError("Please enter your score");
                    return;
                }


               //Validation for date picker
              if (!( personDateYear<=mYear))
              { Toast.makeText(create.this, "Please enter a valid date", Toast.LENGTH_SHORT).show();
                  return;
              }
              if (!( personDateMonth<=mMonth)&& personDateYear==mYear)
                { Toast.makeText(create.this, "Please enter a valid date", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!( personDateDay<=mDay)&& personDateYear==mYear&&personDateMonth==mMonth)
                { Toast.makeText(create.this, "Please enter a valid date", Toast.LENGTH_SHORT).show();
                    return;
                }



                //Write to file when validation is successful
               FileIO f=new FileIO();
               f.writeFile(name.getText().toString(),score1,dat);
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                //finish();
            }
        });

        button_cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Intent intent = new Intent(v.getContext(), MainActivity.class);
                //startActivity(intent);
                finish();
            }
        });
    }
}
