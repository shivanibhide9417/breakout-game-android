package com.test.breakout.breakoutgame;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.TextView;
import com.test.breakout.breakoutgame.game.TouchSurfaceView;
import com.test.breakout.breakoutgame.game.Constants.Config;
import com.test.breakout.breakoutgame.game.Game.State;
import com.test.breakout.breakoutgame.highScores.ScoresList;
import com.test.breakout.breakoutgame.highScores.create;

public class GameActivity extends Activity {

	private TouchSurfaceView mTouchSurfaceView;
	private Handler mHandler;
	private TextView mScoreTextView;
	private TextView mScoreMultiplierTextView;
	private TextView mLivesTextView;
	private TextView mHighScoreTextView;
	private TextView mReadyTextView;
	private SharedPreferences mSharedPrefs;
	private SharedPreferences.Editor mSharedPrefsEditor;
	private long mHighScore;
	private boolean mNewHighScore;
	private boolean mFinish;
	private SoundPool mSoundPool;
	private HashMap<String, Integer> mSoundIds;
	private View mDecorView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_game);

		mHandler = new Handler();
		mNewHighScore = false;
		mFinish = false;
		mDecorView = getWindow().getDecorView();
		
		mTouchSurfaceView = findViewById(R.id.opengl);
		// Initialize SharedPreferences, so we can save the user High Score
		mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		mSharedPrefsEditor = mSharedPrefs.edit();
		mHighScore = mSharedPrefs.getLong("high_score", 0);

		/* Initialize TextViews to show user game state (both high and current
		 * score, current score multiplier and number of lives remaining) and change
		 * color of them to give that retro style ;). */
		mScoreTextView = findViewById(R.id.score);
		mScoreTextView.setTextColor(Color.WHITE);
		mScoreMultiplierTextView = findViewById(R.id.scoreMultiplier);
		mScoreMultiplierTextView.setTextColor(Color.WHITE);
		mLivesTextView = findViewById(R.id.lives);
		mLivesTextView.setTextColor(Color.WHITE);
		mHighScoreTextView = findViewById(R.id.highScore);
		mHighScoreTextView.setTextColor(Color.GRAY);
		mReadyTextView = findViewById(R.id.ready);
		mReadyTextView.setTextColor(Color.WHITE);


		/* We can't update the UI from the GL thread, so we set a timer and update it on
		 * approximately each 10 updates from game state. Don't put this value too low since
		 * UI update is slow. */
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				if(!mFinish) updateUI();
			}
		}, 0, Config.MS_PER_UPDATE * 10);
	}

	@Override
	protected void onResume() {
		super.onResume();
		mTouchSurfaceView.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		/* The user can exit the app and even press back button to go back to the
		 * MainActivity. In both cases, the user can lose it's high score (if
		 * Android's OOM killer closes the app or the user press "New Game" on MainActivity.
		 * So save the score on pause */
		if(mNewHighScore) {
			mSharedPrefsEditor.putLong("high_score", mHighScore);
			mSharedPrefsEditor.commit();
		}
		// Pause the game if the user exits the app
		State.setGamePaused(true);
		mTouchSurfaceView.onPause();
	}
	
	/* Change to immersive mode. Since this is only supported on API 19 (KitKat),
	 * build this code only on newer versions of Android. */
	@TargetApi(Build.VERSION_CODES.KITKAT)
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
	    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
	    	if (hasFocus) {
	    		mDecorView.setSystemUiVisibility(
	    				View.SYSTEM_UI_FLAG_LAYOUT_STABLE
	    				| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
	    				| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
	    				| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
	    				| View.SYSTEM_UI_FLAG_FULLSCREEN
	    				| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
	    	} else {
				State.setGamePaused(true);
			}
	    }
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void showGameOverDialog(final long finalScore, boolean newHighScore) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setTitle(R.string.game_over);
		// Show a different message if the player beats the high score or not
		if(newHighScore){
		builder.setMessage(getString(R.string.new_high_score) + finalScore + "\n" +
				getString(R.string.do_you_want_to_restart_the_game));
		} else {
		builder.setMessage(getString(R.string.final_score) + finalScore + "\n" +
				getString(R.string.do_you_want_to_restart_the_game));
		}
		
		// If the user click Yes, restart this Activity so the user can play again
		builder.setPositiveButton(R.string.yes, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//restartGame();

				Intent intent = new Intent(getBaseContext(), create.class);
				intent.putExtra("highScoreVal",finalScore);
				startActivity(intent);

				/*int check = ActivityCompat.checkSelfPermission(GameActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
				if (check == PackageManager.PERMISSION_GRANTED) {

				} else {
					requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1024);
					Intent intent = new Intent(getBaseContext(), create.class);
					intent.putExtra("highScoreVal",finalScore);
					startActivity(intent);
				}
*/



            }
		});
		
		// If the user click No, go back to the MainActivity
		builder.setNegativeButton(R.string.no, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				finish();
			}
		});
		
		/* To guarantee that the game will not FC, check if we can show (i.e. the
		 * UI Activity is still on a valid state) the dialog before showing it.
		 * Better be safe than sorry.
		 * 
		 * And don't allow the user to just dismiss the dialog by clicking outside
		 * of it. */
		if(!isFinishing()) builder.show().setCanceledOnTouchOutside(false);
	}

	private void restartGame() {
        recreate();
    }
	
	private void updateUI() {
		mHandler.post(new Runnable() {
			@Override
			public void run() {

				/* Show a "Ready?" text in red for the user to know when the game is
				 * paused and ready waiting for the user input */
				if(State.getGamePaused()) {
					mReadyTextView.setVisibility(View.VISIBLE);
				} else {
					mReadyTextView.setVisibility(View.INVISIBLE);
				}

				mScoreTextView.setText(getString(R.string.score) + String.format("%08d", State.getScore()));
				mScoreMultiplierTextView.setText(getString(R.string.multiplier) + State.getScoreMultiplier() + "x");

				/* If the user beats the high score, keep updating the High Score text on the fly
				 * with green text to caught user attention. */
				if(State.getScore() > mHighScore) {
					mHighScore = State.getScore();
					mNewHighScore = true;
					mHighScoreTextView.setTextColor(Color.WHITE);
				}

				mHighScoreTextView.setText(getString(R.string.high_score) + String.format("%08d", mHighScore));
				mLivesTextView.setText(getString(R.string.lives) + State.getLives());
					
				if (State.getGameOver()) {
					/* Show user score and ask if he wants to play again */
					showGameOverDialog(State.getScore(), mNewHighScore);
					/* If the user beats his High Score, save his new high score on SharedPreferences
					 * and play a music as a way to congratulate him ;) */
					if(mNewHighScore) {
						mSharedPrefsEditor.putLong("high_score", mHighScore);
						mSharedPrefsEditor.commit();
											}
					/* We can't use State.getGameOver() as a condition to Timer since we need to pass
					 * at least one time more on updateUI() to show the Game Over dialog. We can't
					 * put showGameOverDialog() on the else condition of the Timer either, because
					 * Timer is not running on UI thread and even if it was, we would enter on a infinite
					 * loop and get a really annoying succession of dialogs and maybe victory fanfares ;). */ 
					mFinish = true;
				}
			}
		});
	}
	
}