package com.hanvon.sulupen.adapter;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hanvon.sulupen.R;
import com.hanvon.sulupen.db.bean.NoteBookRecord;

public class NoteBookEditListAdapter extends BaseAdapter
{
	private final String TAG = "NoteBookEditListAdapter";
	private Context mContext;
	private LayoutInflater mInflater;
	private List<NoteBookRecord> mDatas;
	
	public NoteBookEditListAdapter(Context context, List<NoteBookRecord> data)
	{
		mContext = context;
		mDatas = data;
		mInflater = LayoutInflater.from(context);
	}
	
	@Override
	public int getCount()
	{
		return mDatas.size();
	}
	
	@Override
	public long getItemId(int p)
	{
		return p;
	}
	
	@Override
	public Object getItem(int p)
	{
		return mDatas.get(p);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		ViewHolder holder = null;
		if (null == convertView)
		{
			convertView = mInflater.inflate(R.layout.edit_notebook_list_item, parent, false);
			holder = new ViewHolder();
			holder.mIvDel = (ImageView) convertView.findViewById(R.id.iv_del_notebook_icon);
			holder.mIvReName = (ImageView) convertView.findViewById(R.id.iv_rename_notebook_icon);
			holder.mTvNoteBookName = (TextView) convertView.findViewById(R.id.tv_notebook_name);
			
			holder.mIvDel.setTag(position);
			holder.mIvReName.setTag(position);
			holder.mIvDel.setOnClickListener(new IvClickListener());
			holder.mIvReName.setOnClickListener(new IvClickListener());
			
			convertView.setTag(holder);
		}
		else
		{
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.mTvNoteBookName.setText(mDatas.get(position).getNoteBookName());
		
		return convertView;
	}
	
	private final class ViewHolder
	{
		ImageView mIvDel;
		TextView  mTvNoteBookName;
		ImageView mIvReName;
	}
	
	class IvClickListener implements OnClickListener
	{
		@Override 
		public void onClick(View view)
		{
			int position =Integer.parseInt(view.getTag().toString());
			Log.d(TAG, "item " +  position + " clicked");
			switch(view.getId())
			{
				case R.id.iv_del_notebook_icon:
					Log.d(TAG, "del icon clicked");
				break;
				
				case R.id.iv_rename_notebook_icon:
					Log.d(TAG, "rename icon clicked");
				break;
			}
		}
	}
}
