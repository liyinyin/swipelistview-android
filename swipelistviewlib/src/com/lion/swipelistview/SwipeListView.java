package com.lion.swipelistview;

import java.lang.ref.WeakReference;
import com.lion.swipelistview.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;

public class SwipeListView extends ListView {
	private Boolean mIsHorizontal;
	private View mPreItemView;
	private View mCurrentItemView;
	private float mFirstX;
	private float mFirstY;

	public int mRightViewWidth = 0;
	private boolean mIsShown;

	public int mSwipeLeftLayout = 0;
	public int mSwipeRightLayout = 0;
	public boolean bCanMultiChoice = true; // CHOICE_MODE_MULTIPLE_MODAL
	public boolean bIsInMultiModal = false; // in CHOICE_MODE_MULTIPLE_MODAL

	public SwipeListView(Context context) {
		super(context);
	}

	public SwipeListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(attrs);
	}

	public SwipeListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(attrs);
	}

	private void init(AttributeSet attrs) {
		if (attrs != null) {
			TypedArray styled = getContext().obtainStyledAttributes(attrs, R.styleable.SwipeListView);
			mSwipeLeftLayout = styled.getResourceId(R.styleable.SwipeListView_swipeLeftLayout, 0);
			mSwipeRightLayout = styled.getResourceId(R.styleable.SwipeListView_swipeRightLayout, 0);
			mRightViewWidth = styled.getInteger(R.styleable.SwipeListView_swipeRightViewWidth, 120);
			styled.recycle();
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		float lastX = ev.getX();
		float lastY = ev.getY();

		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			bCanMultiChoice = true;
			mIsHorizontal = null;

			mFirstX = lastX;
			mFirstY = lastY;
			int motionPosition = pointToPosition((int) mFirstX, (int) mFirstY);
			if (motionPosition >= 0) {
				View currentItemView = getChildAt(motionPosition - getFirstVisiblePosition());
				mPreItemView = mCurrentItemView;
				mCurrentItemView = currentItemView;
			}
			break;

		case MotionEvent.ACTION_MOVE:
			if (bIsInMultiModal) { // ListView in CHOICE_MODE_MULTIPLE_MODAL
				break;
			}

			float dx = lastX - mFirstX;
			float dy = lastY - mFirstY;

			if (mIsHorizontal == null) { // swipe direction not Horizontal or Vertical
				if (confirmSwipeDirectionSuccessed(dx, dy)) {
					bCanMultiChoice = false;
					if (mIsHorizontal) {
						if (mIsShown && mPreItemView != mCurrentItemView) {
							hiddenRightView(mPreItemView);
						}

						if (mIsShown && mPreItemView == mCurrentItemView) {
							dx = dx - mRightViewWidth;
						}

						if (dx < 0 && dx > -mRightViewWidth) {
							mCurrentItemView.scrollTo((int) (-dx), 0);
						}
						return true;
					} else {
						if (mIsShown) {
							hiddenRightView(mPreItemView);
						}
					}
				}
			}
			break;

		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			if (mIsShown) {
				bCanMultiChoice = false;
				hiddenRightView(mPreItemView);
			}

			if (mIsHorizontal != null && mIsHorizontal) {
				bCanMultiChoice = false;
				if (mFirstX - lastX > mRightViewWidth / 2) {
					showRightView(mCurrentItemView);
				} else {
					hiddenRightView(mCurrentItemView);
				}
				return true;
			}
			break;
		}

		return super.onTouchEvent(ev);
	}

	private boolean confirmSwipeDirectionSuccessed(float dx, float dy) {
		boolean successed = true;
		if (Math.abs(dx) > 35 && Math.abs(dx) > 2 * Math.abs(dy)) {
			mIsHorizontal = true;
		} else if (Math.abs(dy) > 35 && Math.abs(dy) > 2 * Math.abs(dx)) {
			mIsHorizontal = false;
		} else {
			successed = false;
		}
		return successed;
	}

	public boolean onCreateActionMode() {
		if (bCanMultiChoice && !mIsShown) {
			bIsInMultiModal = true;
		} else {
			bIsInMultiModal = false;
		}
		return (bCanMultiChoice && !mIsShown);
	}

	public void onDestroyActionMode() {
		bIsInMultiModal = false;
	}

	private void showRightView(View view) {
		Message msg = new MoveHandler(this).obtainMessage();
		msg.obj = view;
		msg.arg1 = view.getScrollX();
		msg.arg2 = mRightViewWidth;
		msg.sendToTarget();

		mIsShown = true;
	}

	private void hiddenRightView(View view) {
		if (mCurrentItemView == null) {
			return;
		}
		Message msg = new MoveHandler(this).obtainMessage();
		msg.obj = view;
		msg.arg1 = view.getScrollX();
		msg.arg2 = 0;
		msg.sendToTarget();

		mIsShown = false;
	}

	/**
	 * external method to hidden RightView
	 * 
	 * @return if hidden occur return true, else return false;
	 */
	public boolean hiddenRightView() {
		if (mCurrentItemView == null) {
			return false;
		} else {
			if (mCurrentItemView.getScrollX() == 0) {
				return false;
			} else {
				Message msg = new MoveHandler(this).obtainMessage();
				msg.obj = mCurrentItemView;
				msg.arg1 = mCurrentItemView.getScrollX();
				msg.arg2 = 0;
				msg.sendToTarget();
				mIsShown = false;
				return true;
			}
		}
	}

	/**
	 * show or hide right layout animation
	 */
	static class MoveHandler extends Handler {
		private final static int SWIPE_ANIMATION_DELAY = 10; // ANIMATION DELAY 0.01s
		private int mStepX = 0;
		private final static int STEP_X_GAP = 10; // X step Gap

		private int fromX;
		private int toX;
		private View mView;

		private boolean mIsInAnimation = false;

		private WeakReference<SwipeListView> ref;

		public MoveHandler(SwipeListView ref) {
			this.ref = new WeakReference<SwipeListView>(ref);
		}

		private void swipeAnimationOver() {
			mIsInAnimation = false;
			mStepX = 0;
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (mStepX == 0) {
				if (mIsInAnimation) {
					return;
				}
				mIsInAnimation = true;

				mView = (View) msg.obj;
				fromX = msg.arg1;
				toX = msg.arg2;

				mStepX = (int) ((toX - fromX) / STEP_X_GAP);
				if (Math.abs(toX - fromX) < STEP_X_GAP) {
					mView.scrollTo(toX, 0);
					swipeAnimationOver();
					return;
				}
			}

			fromX += mStepX;
			boolean isLastStep = (mStepX > 0 && fromX > toX) || (mStepX < 0 && fromX < toX);
			if (isLastStep) {
				fromX = toX;
			}

			mView.scrollTo(fromX, 0);
			ref.get().invalidate();

			if (!isLastStep) {
				this.sendEmptyMessageDelayed(0, SWIPE_ANIMATION_DELAY);
			} else {
				swipeAnimationOver();
			}
		}
	}
}
