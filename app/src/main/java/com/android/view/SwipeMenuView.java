package com.android.view;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.widget.SwipeMenuLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author baoyz
 * @date 2014-8-23
 * 
 */
public class SwipeMenuView extends LinearLayout implements OnClickListener {

	private SwipeMenuListView mListView;
	private SwipeMenuLayout mLayout;
	private SwipeMenu mMenu;
	private OnSwipeItemClickListener onItemClickListener;
	private int position;

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public SwipeMenuView(SwipeMenu menu, SwipeMenuListView listView) {
		super(menu.getContext());
		mListView = listView;
		mMenu = menu;
		List<SwipeMenuListView.SwipeMenuItem> items = menu.getMenuItems();
		int id = 0;
		for (SwipeMenuListView.SwipeMenuItem item : items) {
			addItem(item, id++);
		}
	}

	private void addItem(SwipeMenuListView.SwipeMenuItem item, int id) {
		LayoutParams params = new LayoutParams(item.getWidth(),
				LayoutParams.MATCH_PARENT);
		LinearLayout parent = new LinearLayout(getContext());
		parent.setId(id);
		parent.setGravity(Gravity.CENTER);
		parent.setOrientation(LinearLayout.VERTICAL);
		parent.setLayoutParams(params);
		parent.setBackgroundDrawable(item.getBackground());
		parent.setOnClickListener(this);
		addView(parent);

		if (item.getIcon() != null) {
			parent.addView(createIcon(item));
		}
		if (!TextUtils.isEmpty(item.getTitle())) {
			parent.addView(createTitle(item));
		}

	}

	private ImageView createIcon(SwipeMenuListView.SwipeMenuItem item) {
		ImageView iv = new ImageView(getContext());
		iv.setImageDrawable(item.getIcon());
		return iv;
	}

	private TextView createTitle(SwipeMenuListView.SwipeMenuItem item) {
		TextView tv = new TextView(getContext());
		tv.setText(item.getTitle());
		tv.setGravity(Gravity.CENTER);
		tv.setTextSize(item.getTitleSize());
		tv.setTextColor(item.getTitleColor());
		return tv;
	}

	@Override
	public void onClick(View v) {
		if (onItemClickListener != null && mLayout.isOpen()) {
			onItemClickListener.onItemClick(this, mMenu, v.getId());
		}
	}

	public OnSwipeItemClickListener getOnSwipeItemClickListener() {
		return onItemClickListener;
	}

	public void setOnSwipeItemClickListener(OnSwipeItemClickListener onItemClickListener) {
		this.onItemClickListener = onItemClickListener;
	}

	public void setLayout(SwipeMenuLayout mLayout) {
		this.mLayout = mLayout;
	}

	public static interface OnSwipeItemClickListener {
		void onItemClick(SwipeMenuView view, SwipeMenu menu, int index);
	}

	public static class SwipeMenu {

		private Context mContext;
		private List<SwipeMenuListView.SwipeMenuItem> mItems;
		private int mViewType;

		public SwipeMenu(Context context) {
			mContext = context;
			mItems = new ArrayList<SwipeMenuListView.SwipeMenuItem>();
		}

		public Context getContext() {
			return mContext;
		}

		public void addMenuItem(SwipeMenuListView.SwipeMenuItem item) {
			mItems.add(item);
		}

		public void removeMenuItem(SwipeMenuListView.SwipeMenuItem item) {
			mItems.remove(item);
		}

		public List<SwipeMenuListView.SwipeMenuItem> getMenuItems() {
			return mItems;
		}

		public SwipeMenuListView.SwipeMenuItem getMenuItem(int index) {
			return mItems.get(index);
		}

		public int getViewType() {
			return mViewType;
		}

		public void setViewType(int viewType) {
			this.mViewType = viewType;
		}

	}

}
