package com.hanvon.sulupen.adapter;

import java.util.ArrayList;

import com.hanvon.sulupen.pinyin.FounctionContainer;
import com.hanvon.sulupen.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class LanguageListAdapter extends BaseAdapter {
private ArrayList<String> languageList; 
private Context context;
private int clickId=-1;
FounctionContainer founctionContainer;
public LanguageListAdapter(Context context,FounctionContainer container,ArrayList<String> list){
	this.context=context;
	this.languageList=list;
	this.founctionContainer=container;
}
	@Override
	public int getCount() {
		return languageList.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return languageList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}
	@Override
	public View getView(final int position, View convertView, ViewGroup group) {
		View view=LayoutInflater.from(context).inflate(R.layout.item_language_type,null);
		TextView tv=(TextView)view.findViewById(R.id.tv_language);
		if(clickId==position){
			tv.setBackgroundColor(context.getResources().getColor(R.color.gray));
		}
		else{
			tv.setBackgroundColor(context.getResources().getColor(R.color.lighter_gray));
		}
		tv.setText(languageList.get(position));
		tv.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
			  founctionContainer.doItemClick(position);
			}
		});
		return view;
	}
	public void setSelection(int pos){
	      clickId=pos;
	}

}
