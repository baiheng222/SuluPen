package com.hanvon.sulupen.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hanvon.sulupen.R;
import com.hanvon.sulupen.RenameNoteBookActivity;
import com.hanvon.sulupen.db.bean.NoteBookRecord;
import com.hanvon.sulupen.db.bean.NoteRecord;

public class NoteChooseAdapter extends BaseAdapter
{
	private final String TAG = "NoteChooseAdapter";
	private Context mContext;
	private LayoutInflater mInflater;
	private List<NoteRecord> mDatas;
	
	public NoteChooseAdapter(Context context, List<NoteRecord> data)
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
			convertView = mInflater.inflate(R.layout.choose_notes_list_item, parent, false);
			holder = new ViewHolder();
			holder.mNoteContent = (TextView) convertView.findViewById(R.id.tv_choose_note_content);
			holder.mNoteTitle = (TextView) convertView.findViewById(R.id.tv_choos_note_title);
			holder.mNoteCreateTime = (TextView) convertView.findViewById(R.id.tv_choose_note_create_time);
			holder.mNoteSelectIcon = (ImageView) convertView.findViewById(R.id.iv_choose_note);

			
			//holder.mNoteSelectIcon.setTag(position);
			//holder.mNoteSelectIcon.setOnClickListener(new IvClickListener());
			
			convertView.setTag(holder);
		}
		else
		{
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.mNoteContent.setText((mDatas.get(position).getNoteContent()));
		holder.mNoteTitle.setText(mDatas.get(position).getNoteTitle());
		holder.mNoteCreateTime.setText(mDatas.get(position).getCreateTime());
		
		Log.d(TAG, "position is " + position + ": " + mDatas.get(position).toString());
		
		return convertView;
	}
	
	private void delNoteBook(int pos)
	{
		
	}
	
	private void renameNoteBook(int pos)
	{
		/*
		Intent intent = new Intent(mContext, RenameNoteBookActivity.class);
		NoteBookRecord note = mDatas.get(pos);
		if (null == note)
		{
			Log.d(TAG, "note is null");
			return;
		}
		intent.putExtra("NoteBook", mDatas.get(pos));
		mContext.startActivity(intent);
		*/
	}
	
	private final class ViewHolder
	{
        TextView mNoteContent;
        TextView mNoteTitle;
        TextView mNoteCreateTime;
        ImageView mNoteSelectIcon;
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
				case R.id.iv_choose_note:
					Log.d(TAG, "del icon clicked");
					delNoteBook(position);
				break;
			}
		}
	}
}
