package com.lion.swipelistviewtest;

import java.util.ArrayList;

import com.lion.swipelistview.BaseSwipeAdapter;
import com.lion.swipelistview.SwipeListView;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    private SwipeListView mListView;
    private ArrayList<LeftData> mList = new ArrayList<LeftData>();
    private ListViewAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initData();
        initView();
    }
    
    class LeftData {
    	String name;
    	String desc;
    	public LeftData(String name, String desc) {
    		this.name = name;
    		this.desc = desc;
    	}
    }

    private void initData() {
        for (int i = 0; i < 20; i++) {
        	String name = Integer.toString(i) + ".dwg";
        	LeftData data = new LeftData(name, "description");
        	mList.add(data);
        }
    }

    private void initView() {
        mListView = (SwipeListView)findViewById(R.id.listview);
        mAdapter = new ListViewAdapter(this);
        mListView.setAdapter(mAdapter);
        mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        mListView.setMultiChoiceModeListener(new MultiChoiceModeListener(){

			@Override
			public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
				// TODO Auto-generated method stub
				Log.e("MultiChoiceModeListener", "onActionItemClicked");
				return false;
			}

			@Override
			public boolean onCreateActionMode(ActionMode mode, Menu menu) {
				if (mListView.onCreateActionMode()) {
					Log.e("MultiChoiceModeListener", "onCreateActionMode--before");
					return true;
				} else {
					Log.e("MultiChoiceModeListener", "onCreateActionMode--after");
					return false;
				}
			}

			@Override
			public void onDestroyActionMode(ActionMode mode) {
				Log.e("MultiChoiceModeListener", "onDestroyActionMode");
				mListView.onDestroyActionMode();
			}

			@Override
			public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
				Log.e("MultiChoiceModeListener", "onPrepareActionMode");
				return false;
			}

			@Override
			public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
				Log.e("MultiChoiceModeListener", "onItemCheckedStateChanged");
			}
        	
        });
        mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this, Integer.toString(position), Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private class ViewHolder {
		ImageView mImageView;
		TextView mFileNameText;
		TextView mFileDesView;
		CheckBox mCheckBox;
		
		ImageView mDelete;
		ImageView mFileInfo;
	}

    class ListViewAdapter extends BaseSwipeAdapter {

        public ListViewAdapter(Context context) {
            super(context, mListView);
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Object getItem(int position) {
            return mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
        	ViewHolder holder = null;
        	if (convertView == null) {
        		convertView = super.getView(position, convertView, parent);
        		
        		holder = new ViewHolder();
        		holder.mImageView = (ImageView) convertView.findViewById(R.id.formatimg);
        		holder.mFileNameText = (TextView) convertView.findViewById(R.id.filename);
        		holder.mFileDesView = (TextView) convertView.findViewById(R.id.size);
        		holder.mCheckBox = (CheckBox) convertView.findViewById(R.id.fileCheckBox);
    			
        		holder.mDelete = (ImageView) convertView.findViewById(R.id.delete);
        		holder.mFileInfo = (ImageView) convertView.findViewById(R.id.infoButton);
    			
        		convertView.setTag(holder);
        	} else {
    			holder = (ViewHolder) convertView.getTag();
    		}

        	holder.mDelete.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					Toast.makeText(MainActivity.this, "DELETE", Toast.LENGTH_SHORT).show();
					int position = mListView.getPositionForView(v);
					mListView.hiddenRightView();
					mList.remove(position);
					mAdapter.notifyDataSetChanged();
				}
        	});
        	
        	holder.mFileInfo.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					Toast.makeText(MainActivity.this, "INFO", Toast.LENGTH_SHORT).show();
				}
        	});
        	
        	
        	holder.mFileNameText.setText(((LeftData)getItem(position)).name);
        	holder.mFileDesView.setText(((LeftData)getItem(position)).desc);
            
            return convertView;
        }
    }

	@Override
	public void onBackPressed() {
		if (!mListView.hiddenRightView()) {
			super.onBackPressed();
		}
	}
}
