package com.hanvon.sulupen.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hanvon.sulupen.R;
import com.hanvon.sulupen.db.bean.NoteBookRecord;

import java.util.List;

public class NoteBookChangeAdapter extends BaseAdapter  
{  
    private LayoutInflater mInflater;  
    private Context mContext;  
    private List<NoteBookRecord> mDatas;
    private NoteBookRecord mCurrentNoteBook;
  
    public NoteBookChangeAdapter(Context context, List<NoteBookRecord> mDatas, NoteBookRecord current)  
    {  
        mInflater = LayoutInflater.from(context);  
        this.mContext = context;  
        this.mDatas = mDatas; 
        this.mCurrentNoteBook= current; 
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
            convertView = mInflater.inflate(R.layout.change_notebook_list_item, parent, false);
            viewHolder = new ViewHolder();  
            viewHolder.mSelectIcon = (ImageView) convertView.findViewById(R.id.iv_current_notebook);
            viewHolder.mNoteBookName = (TextView) convertView.findViewById(R.id.tv_notebook_name_of_change);
            convertView.setTag(viewHolder);  
        } 
        else  
        {  
            viewHolder = (ViewHolder) convertView.getTag();  
        }
        
        viewHolder.mNoteBookName.setText(mDatas.get(position).getNoteBookName());
        
        if (mDatas.get(position).getId() == mCurrentNoteBook.getId())
        {
            viewHolder.mSelectIcon.setVisibility(View.VISIBLE);
        }
        else
        {
            viewHolder.mSelectIcon.setVisibility(View.GONE);
        }
        
        return convertView;  
    }  
  
    
    private final class ViewHolder  
    {  
        ImageView mSelectIcon;
        TextView mNoteBookName;
    }
    
}
