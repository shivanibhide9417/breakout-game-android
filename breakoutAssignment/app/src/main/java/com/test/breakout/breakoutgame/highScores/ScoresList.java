package com.test.breakout.breakoutgame.highScores;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.test.breakout.breakoutgame.R;

public class ScoresList extends Activity {


    //ArrayList of hashmap is used to store the values for every entry in the list row
    private ArrayList<HashMap<String, String>> list;

    //customize Action bar with menu with the 'add' button
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    // handle button click function for the 'add' button on the Action bar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.addEntry) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_view);
        ListView listView=(ListView)findViewById(R.id.listView1);
        FileIO f=new FileIO();
        list=f.populateList();


        //Sorting the list obtained
        HashMap<String, String> temporary;
        for (int c = 1; c < (list.size() - 1); c++) {
            for (int d = 1; d < (list.size() - c - 1); d++) {



                if (Integer.parseInt(list.get(d).get("Second")) ==Integer
                        .parseInt(list.get(d + 1).get("Second"))) {
                    try {
                        System.out.println("EQUAL");
                        Date date1=new SimpleDateFormat("dd/MM/yyyy").parse((list.get(d).get("Third")));
                        Date date2=new SimpleDateFormat("dd/MM/yyyy").parse((list.get(d + 1).get("Third")));
                        System.out.println("DATE1"+date1.toString());
                        System.out.println("DATE2"+date2.toString());
                        if (date1.compareTo(date2) < 0)
                        {temporary = list.get(d);
                            list.set(d, list.get(d + 1));
                            list.set(d + 1, temporary);
                            System.out.println("SWAP DONE");
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (Integer.parseInt(list.get(d).get("Second")) < Integer
                            .parseInt(list.get(d + 1).get("Second"))) {

                        temporary = list.get(d);
                        list.set(d, list.get(d + 1));
                        list.set(d + 1, temporary);
                    }

                }
            }
        }


        //pass the hashmap to the adapter
        ListViewAdapter adapter=new ListViewAdapter(this, list);
        listView.setAdapter(adapter);

    }

    //Repopulate list once the second activity is over
    @Override
    protected void onResume() {
        super.onResume();
        setContentView(R.layout.list_view);
        ListView listView=(ListView)findViewById(R.id.listView1);
        FileIO f=new FileIO();
        list=f.populateList();

        //Sorting the list obtained
        HashMap<String, String> temporary;
        for (int c = 1; c < (list.size() - 1); c++) {
            for (int d = 1; d < (list.size() - c ); d++) {
                try {
                    System.out.println("EQUAL");
                    Date date1=new SimpleDateFormat("dd/MM/yyyy").parse((list.get(d).get("Third")));
                    Date date2=new SimpleDateFormat("dd/MM/yyyy").parse((list.get(d + 1).get("Third")));
                    System.out.println("DATE1"+date1.toString());
                    System.out.println("DATE2"+date2.toString());
                    if (date1.compareTo(date2) < 0)
                    {temporary = list.get(d);
                        list.set(d, list.get(d + 1));
                        list.set(d + 1, temporary);
                        System.out.println("SWAP DONE");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (Integer.parseInt(list.get(d).get("Second")) < Integer
                        .parseInt(list.get(d + 1).get("Second"))) {

                    temporary = list.get(d);
                    list.set(d, list.get(d + 1));
                    list.set(d + 1, temporary);

                }
            }
        }
        for (int i=0; i<9; i++)
        //pass the hashmap to the adapter
        { ListViewAdapter adapter=new ListViewAdapter(this, list);
        listView.setAdapter(adapter);}

    }


}
