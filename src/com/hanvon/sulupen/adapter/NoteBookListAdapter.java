package com.hanvon.sulupen.adapter;

import java.util.List;  

import com.hanvon.sulupen.R;

import android.content.Context;  
import android.view.LayoutInflater;  
import android.view.View;  
import android.view.ViewGroup;  
import android.widget.BaseAdapter;  
import android.widget.TextView;

import com.hanvon.sulupen.db.bean.NoteBookRecord;

public class NoteBookListAdapter extends BaseAdapter  
{  
    private LayoutInflater mInflater;  
    private Context mContext;  
    private List<NoteBookRecord> mDatas;  
  
    public NoteBookListAdapter(Context context, List<NoteBookRecord> mDatas)  
    {  
        mInflater = LayoutInflater.from(context);  
        this.mContext = context;  
        this.mDatas = mDatas;  
    }  
  
    @Override  
    public int getCount()  
    {  
        return mDatas.size();  
    }  
  
    @Override  
    public Object getItem(int position)  
    {  
        return mDatas.get(position);  
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
        if (convertView == null)  
        {  
        	convertView = mInflater.inflate(R.layout.notebook_list_item, parent, false);
            viewHolder = new ViewHolder();  
            viewHolder.mNoteBookName = (TextView) convertView.findViewById(R.id.tv_notebook_name);
            viewHolder.mNotesNum = (TextView) convertView.findViewById(R.id.tv_notes_in_notebook);
            convertView.setTag(viewHolder);  
        } else  
        {  
            viewHolder = (ViewHolder) convertView.getTag();  
        }  
        viewHolder.mNoteBookName.setText(mDatas.get(position).getNoteBookName());  
        return convertView;  
    }  
  
    private final class ViewHolder  
    {  
        TextView mNoteBookName;
        TextView mNotesNum;
    }  
  
}
