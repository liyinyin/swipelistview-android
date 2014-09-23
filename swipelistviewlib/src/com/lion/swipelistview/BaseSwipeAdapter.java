package com.lion.swipelistview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

public abstract class BaseSwipeAdapter extends BaseAdapter {
	private Context mContext;
	private SwipeListView mSwipeListView;
	
	public BaseSwipeAdapter(Context context, SwipeListView swipeListView) {
		this.mContext = context;
		this.mSwipeListView = swipeListView;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LinearLayout layout = new LinearLayout(mContext);
		convertView = layout;

		layout.addView(generateLeftView());
		layout.addView(generateRightView());
		return convertView;
	}

	protected View generateLeftView() {
		View view = LayoutInflater.from(mContext).inflate(mSwipeListView.mSwipeLeftLayout, null, false);

		LinearLayout.LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		view.setLayoutParams(lp);
		return view;
	}

	protected View generateRightView() {
		View view = LayoutInflater.from(mContext).inflate(mSwipeListView.mSwipeRightLayout, null, false);

		LinearLayout.LayoutParams lp = new LayoutParams(mSwipeListView.mRightViewWidth, LayoutParams.MATCH_PARENT);
		view.setLayoutParams(lp);
		return view;
	}
}
