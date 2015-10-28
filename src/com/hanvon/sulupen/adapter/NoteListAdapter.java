package com.hanvon.sulupen.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hanvon.sulupen.R;
//import com.hanvon.sulupen.R;
import com.hanvon.sulupen.db.bean.NoteRecord;

import java.util.List;


public class NoteListAdapter extends BaseAdapter
{
    private Context mContext;
    private List<NoteRecord> mNoteList;
    private LayoutInflater mInflater;
    
    NoteListAdapter(Context context, List<NoteRecord> mDatas)
    {
        mContext = context;
        mNoteList = mDatas;
        mInflater = LayoutInflater.from(context);
    }
    
    
    @Override
    public int getCount()
    {
        return mNoteList.size();
    }
    
    @Override   
    public Object getItem(int position)
    {
        return mNoteList.get(position);
    }
    
    @Override
    public long getItemId(int position)
    {
        return position;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder viewHolder = null;
        if (null == convertView)
        {
            convertView = mInflater.inflate(R.layout.note_list_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.mNoteContent = (TextView) convertView.findViewById(R.id.tv_note_content);
            viewHolder.mNoteTitle = (TextView) convertView.findViewById(R.id.tv_note_title);
            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        
        viewHolder.mNoteTitle.setText("title");
        viewHolder.mNoteContent.setText("content");
        
        return convertView;
    }
    
    public final class ViewHolder
    {
        TextView mNoteContent;
        TextView mNoteTitle;
    }
}
