package com.test.breakout.breakoutgame;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import com.test.breakout.breakoutgame.game.Game.State;
import com.test.breakout.breakoutgame.highScores.FileIO;
import com.test.breakout.breakoutgame.highScores.ScoresList;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends Activity implements OnItemSelectedListener {

	private ArrayList<HashMap<String, String>> list;
	private TextView mHighScoreTextView;
	private Button mNewGameButton;
	private Button mViewScoreButton;
	private Spinner mLevelSpinner;
	private CheckBox mSoundEffectsCheckBox;
	private SharedPreferences mSharedPrefs;
	//private int mRickRoll;
	private long mHighScore;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ListView listView=(ListView)findViewById(R.id.listView1);
		//FileIO f=new FileIO();
		//list=f.populateList();
		//System.out.println("file created from the main activity");

		int check = ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
		if (check != PackageManager.PERMISSION_GRANTED) {
			requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1024);
		}


		mHighScoreTextView = findViewById(R.id.mainHighScore);
		mNewGameButton = findViewById(R.id.newGameButton);
		mViewScoreButton = findViewById(R.id.resetScoreButton);
		mLevelSpinner = findViewById(R.id.levelSpinner);
		//mSoundEffectsCheckBox = findViewById(R.id.soundEffectsCheckBox);
		mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		mHighScore = mSharedPrefs.getLong("high_score", 0);
		
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
		        R.array.levels, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mLevelSpinner.setAdapter(adapter);
		mLevelSpinner.setOnItemSelectedListener(this);
		mLevelSpinner.setSelection(mSharedPrefs.getInt("difficult_prefs", 2)); // Default to difficult "normal"
		//mSoundEffectsCheckBox.setChecked(mSharedPrefs.getBoolean("sound_effects", true)); // Sound effects prefs
		//State.enableSoundEffects(mSoundEffectsCheckBox.isChecked());
		
		mNewGameButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getBaseContext(), GameActivity.class);
				startActivity(intent);
			}
		});
		
		mViewScoreButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int check = ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
				if (check == PackageManager.PERMISSION_GRANTED) {
					Intent intent = new Intent(getBaseContext(), ScoresList.class);
					startActivity(intent);
				} else {
					requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1024);
				}

			}
		});
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		/* Since the score may change between activities, update the
		 * score view here instead of onCreate(). */
		updateScoreTextView();
		//mRickRoll = 0;
	}
	

	
	private void updateScoreTextView() {
		mHighScore = mSharedPrefs.getLong("high_score", 0);
		mHighScoreTextView.setText(getString(R.string.high_score) +	 String.format("%08d", mHighScore));
	}

	private void resetHighScore() {
		AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

		builder.setTitle(R.string.reset_high_score);
		builder.setMessage(R.string.do_you_want_to_reset_the_high_score_to_zero);

		builder.setPositiveButton(R.string.yes, new Dialog.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				SharedPreferences.Editor editor = mSharedPrefs.edit();
				editor.remove("high_score");
				editor.apply();
				updateScoreTextView();
			}
		});

		builder.setNegativeButton(R.string.no, null);

		if(!isFinishing()) builder.show();
	}

/*	private void easterEggRickRoll() {
		 Never gonna give you up
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setTitle(R.string.psss_just_between_you_and_me);
		builder.setMessage(R.string.do_you_want_to_get_the_maximum_score_for_free);
		
		builder.setPositiveButton(R.string.yes, new Dialog.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String videoId = "dQw4w9WgXcQ";
				try{
					Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + videoId));
					startActivity(intent);                 
				} catch (ActivityNotFoundException ex) {
					Intent intent=new Intent(Intent.ACTION_VIEW,
							Uri.parse("http://www.youtube.com/watch?v=" + videoId));
					startActivity(intent);
				}
			}
		});
		
		builder.setNegativeButton(R.string.no, null);

		if(!isFinishing()) builder.show().setCanceledOnTouchOutside(false);

	}*/

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
		// Set difficult
		State.setDifficult(pos);
		SharedPreferences.Editor editor = mSharedPrefs.edit();
		editor.putInt("difficult_prefs", pos);
		editor.apply();
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		/* Nothing to do here */
	}
}
