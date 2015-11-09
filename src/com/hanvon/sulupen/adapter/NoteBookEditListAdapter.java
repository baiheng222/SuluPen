package com.hanvon.sulupen.adapter;

import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hanvon.sulupen.EditNoteBookActivity;
import com.hanvon.sulupen.R;
import com.hanvon.sulupen.RenameNoteBookActivity;
import com.hanvon.sulupen.db.bean.NoteBookRecord;

public class NoteBookEditListAdapter extends BaseAdapter
{
	private final String TAG = "NoteBookEditListAdapter";
	private EditNoteBookActivity mContext;
	private LayoutInflater mInflater;
	private List<NoteBookRecord> mDatas;
	
	public NoteBookEditListAdapter(EditNoteBookActivity context, List<NoteBookRecord> data)
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
		
		Log.d(TAG, "position is " + position + ": " + mDatas.get(position).toString());
		
		return convertView;
	}
	
	private void delNoteBook(final int pos)
	{
		//mDatas.remove(pos);
		//notifyDataSetChanged();
		
		//mContext.delNoteBook(pos);
		
		AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
		dialog.setMessage(mContext.getString(R.string.del_notebook_tip));
		dialog.setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() 
		{
			@Override
			public void onClick(DialogInterface dialog, int which) 
			{

			}
		});
				
		dialog.setPositiveButton(R.string.ensure, new DialogInterface.OnClickListener() 
		{
			@Override
			public void onClick(DialogInterface dialog, int which) 
			{
				NoteBookRecord m = mDatas.get(pos); 
				mContext.mNoteBookRecordDao.deleteRecord(m);
				mDatas.remove(pos);
				notifyDataSetChanged();
			}
		});
		
		dialog.show();
		
	}
	
	
	/*
	protected void dialog() 
	{
	　　  AlertDialog.Builder builder = new Builder(mContext);
	　　  builder.setMessage("确认退出吗？");
	　　  builder.setTitle("提示");
	　　  builder.setPositiveButton("确认", new OnClickListener() {
	　　   @Override
	　　   public void onClick(DialogInterface dialog, int which) {
	　　    dialog.dismiss();
	　　    Main.this.finish();
	　　   }
	　　  });
	　　  builder.setNegativeButton("取消", new OnClickListener() {
	　　   @Override
	　　   public void onClick(DialogInterface dialog, int which) {
	　　    dialog.dismiss();
	　　   }
	　　  });
	　　  builder.create().show();
	　　 }
	*/
	
	private void renameNoteBook(int pos)
	{
		Intent intent = new Intent(mContext, RenameNoteBookActivity.class);
		NoteBookRecord note = mDatas.get(pos);
		if (null == note)
		{
			Log.d(TAG, "note is null");
			return;
		}
		intent.putExtra("NoteBook", mDatas.get(pos));
		mContext.startActivity(intent);
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
					delNoteBook(position);
				break;
				
				case R.id.iv_rename_notebook_icon:
					Log.d(TAG, "rename icon clicked");
					renameNoteBook(position);
				break;
			}
		}
	}
}
