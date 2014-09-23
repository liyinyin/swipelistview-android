swipelistview-android
=====================

Andriod ListView support CHOICE_MODE_MULTIPLE_MODAL

How to Use
---------------------

```xml
<com.lion.swipelistview.SwipeListView
        android:id="@+id/listview"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:cacheColorHint="@android:color/transparent"
        android:listSelector="@android:color/transparent"
        swipe:swipeLeftLayout="@layout/leftlayout"
        swipe:swipeRightLayout="@layout/rightlayout"
        swipe:swipeRightViewWidth="150" />
```

* `swipeLeftLayout` - **Required** - left view layout id.
* `swipeRightLayout` - **Required** - right view layout id.
* `swipeRightViewWidth` - Optional - right view width Default: '120'

MultiChoiceModeListener()

```

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
```

See details in testDemo.