package com.test.breakout.breakoutgame.forms;

import android.util.Log;
import com.test.breakout.breakoutgame.game.Game.State;

public class MobileBrick extends Brick {
	
	private final int mFramesToWait;	//number of frames update to wait until the brick move again.
	private int mToWait;		//countdown that says when the the mobile brick can move (mToWait == 0)
	private float mSpeedX;		//the mobile brick only moves in the X axis. It stores the increment in the movement.
	private boolean mCollided;	//flat to say when the brick hit the wall or another brick.
	
	//used to index the global vector of bricks (mBricks) in Game
	private final int mIndexI;
	private final int mIndexJ;
	

	public int getIndexI() {
		return mIndexI;
	}
	
	public int getIndexJ() {
		return mIndexJ;
	}
	
	public void move() {
		if (mToWait == 0) {
			if (mCollided) mSpeedX *= 2; //I want to get out very quickly from the collision area
			Log.v(TAG, "Moving MobileBrick[" + mIndexI + "][" + mIndexJ + "], speed: " + mSpeedX + ", posX: " + mPosX);
			mPosX += mSpeedX;
			if (mCollided) {
				mSpeedX /= 2; //restore the normal value
				mCollided = false;
			}
			mToWait = mFramesToWait;
		}
		mToWait--;
	}
	

	 // only invert the direction when the ball is ready to move. This is necessary because a lot of frame updates can happen between two movements
	  //of the brick, so the brick could change its direction a lot of times which could lead the brick to get the wrong direction.

	public void invertDirection() {
		if (mToWait == 0) {
			mSpeedX *= -1;
		}
	}
	

	// only detect collision between the bricks when this brick is ready to move.


	public boolean detectCollisionWithBrick(Brick other) {
		if (mToWait > 0) return false;
		
		if (this.getTopY() >= other.getBottomY() && this.getBottomY() <= other.getTopY() &&	//this isn't necessary, but we keep it to make compatible with other detection codes.
				this.getRightX() >= other.getLeftX() && this.getLeftX() <= other.getRightX()) {
			if (this.getLeftX() < other.getLeftX()) {	//collided with the right brick
				if (mSpeedX < 0) mSpeedX *= -1;			//the brick should be moving to the right as it collided with the right brick
			} else {									//collided with the left brick
				if (mSpeedX > 0) mSpeedX *= -1;			//the brick should be moving to the left as it collided with the left brick
			}
			mCollided = true;
			return true;
		} else return false;
	}



	public MobileBrick(float[] colors, float posX, float posY, Type type, int wait, int i, int j, float speedX) {
		super(colors, posX, posY, type);

		mCollided = false;
		mIndexI = i;
		mIndexJ = j;

		//two ways to set brick's speed- the number of frames without moving the ball and the increment in the movement.
		mToWait = mFramesToWait = wait;
		mSpeedX = speedX;
	}



	// only detect collision between the brick and wall when the brick is ready to move.
	public boolean detectCollisionWithWall() {
		if (mToWait > 0) return false;
		
		if ((this.getRightX() >= State.getScreenHigherX())        	// collided with the right wall
				|| (this.getLeftX() <= State.getScreenLowerX())) {	// collided with the left wall
			if (this.getRightX() >= State.getScreenHigherX()) {		// collided with the right wall
				if (mSpeedX < 0) mSpeedX *= -1;						//the brick should be moving to the right as it collided with the right wall
			} else  {												//collided with the left wall
				if (mSpeedX > 0) mSpeedX *= -1;						//the brick should be moving to the left as it collided with the left wall
			}
			mCollided = true;
			return true;
		} else return false;
	}
	
	public boolean equal(int i, int j) {
		return (mIndexI == i) && (mIndexJ == j);
	}
	
	

}
