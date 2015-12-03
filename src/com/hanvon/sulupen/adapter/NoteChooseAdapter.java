package com.hanvon.sulupen.adapter;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.hanvon.sulupen.R;
import com.hanvon.sulupen.db.bean.NoteBookRecord;
import com.hanvon.sulupen.db.bean.NoteRecord;
import com.hanvon.sulupen.db.dao.NoteRecordDao;

public class NoteChooseAdapter extends BaseAdapter
{
	private final String TAG = "NoteChooseAdapter";
	private Context mContext;
	private LayoutInflater mInflater;
	private List<NoteRecord> mDatas;
	private int[] mCheckArray;
	
	public NoteChooseAdapter(Context context, List<NoteRecord> data)
	{
		mContext = context;
		mDatas = data;
		mInflater = LayoutInflater.from(context);
		
		initCheckList();
		
		/*
		mCheckArray = new int[mDatas.size()];
		for (int i = 0; i < mDatas.size();i++)
		{
			mCheckArray[i] = 0;
		}
		*/
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
			Log.d(TAG, "new convertView !!1");
			convertView = mInflater.inflate(R.layout.choose_notes_list_item, parent, false);
			holder = new ViewHolder();
			holder.mNoteContent = (TextView) convertView.findViewById(R.id.tv_choose_note_content);
			holder.mNoteTitle = (TextView) convertView.findViewById(R.id.tv_choos_note_title);
			holder.mNoteCreateTime = (TextView) convertView.findViewById(R.id.tv_choose_note_create_time);
			holder.mNoteSelectIcon = (CheckBox) convertView.findViewById(R.id.cb_note_choice);
			holder.mNoteSelectIcon.setTag(position);
			holder.mNoteSelectIcon.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
			{ 
	            @Override
	            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) 
	            { 
	                // TODO Auto-generated method stub 
	            	int position =Integer.parseInt(buttonView.getTag().toString());
	                if(isChecked)
	                { 
	                   mCheckArray[position] = 1;
	                   Log.d(TAG, "item " + position + " is selected");
	                }
	                else
	                { 
	                	mCheckArray[position] = 0;
	                	Log.d(TAG, "item " + position + " is unselected");
	                } 
	            } 
	        }); 
			
			convertView.setTag(holder);
		}
		else
		{
			holder = (ViewHolder) convertView.getTag();
			Log.d(TAG, "get holder tag");
			//holder.mNoteSelectIcon.setChecked(checked)
			//int position =Integer.parseInt(view.getTag().toString());
		}
		
		holder.mNoteContent.setText((mDatas.get(position).getNoteContent()));
		holder.mNoteTitle.setText(mDatas.get(position).getNoteTitle());
		holder.mNoteCreateTime.setText(mDatas.get(position).getCreateTime());
		if (mCheckArray[position] == 1)
		{
			holder.mNoteSelectIcon.setChecked(true);
		}
		else
		{
			holder.mNoteSelectIcon.setChecked(false);
		}
		
		
		Log.d(TAG, "position is " + position + ": " + mDatas.get(position).toString());
		
		return convertView;
	}
	
	public void delSelectedNotes()
	{
		NoteRecordDao noteDao = new NoteRecordDao(mContext);
		for (int i = 0; i < mCheckArray.length;i ++)
		{
			if (mCheckArray[i] == 1)
			{
				Log.d(TAG, "delete note, item is " + i);
				NoteRecord note = mDatas.get(i);
				note.setIsDelete(1);
				noteDao.updataRecord(note);
				//noteDao.deleteRecord(mDatas.get(i));
				
			}
		}
		
		for (int i = 0; i < mCheckArray.length;i ++)
		{
			if (mCheckArray[i] == 1)
			{
				mDatas.remove(i);
			}
		}
		
		initCheckList();
		
		notifyDataSetChanged();
	}
	
	public void batchReNameNoteBook(NoteBookRecord notebook)
	{
		NoteRecordDao noteDao = new NoteRecordDao(mContext);
		for (int i = 0; i < mCheckArray.length;i ++)
		{
			if (mCheckArray[i] == 1)
			{
				Log.d(TAG, "change notebook, item is " + i);
				NoteRecord note = mDatas.get(i);
				note.setNoteBook(notebook);
				note.setUpLoad(2);
				noteDao.updataRecord(note);
			}
		}
		
		for (int i = 0; i < mCheckArray.length;i ++)
		{
			if (mCheckArray[i] == 1)
			{
				mDatas.remove(i);
			}
		}
		
		initCheckList();
		
		notifyDataSetChanged();
	}
	
	private void initCheckList()
	{
		mCheckArray = new int[mDatas.size()];
		for (int i = 0; i < mDatas.size();i++)
		{
			mCheckArray[i] = 0;
		}
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
        CheckBox mNoteSelectIcon;
	}
	
}
