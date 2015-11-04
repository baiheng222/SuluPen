package com.hanvon.sulupen.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.hanvon.sulupen.R;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 单选List Adapter
 */
public class SingleChoiceListAdapter extends BaseAdapter {

    class Holder {
        ImageView imgSingleChoice;
        TextView info;
        TextView title;
    }

    private final LayoutInflater inflater;
    // 是否显示单项按钮
    private boolean isVisibleChoiceImg = true;
    Context c;
    int currentID = 0;
    List<Map<String, Object>> list;

    public SingleChoiceListAdapter(Context context, boolean isVisibleChoiceImg, List<Map<String, Object>> list) {
        inflater = LayoutInflater.from(context);
        this.c = context;
        this.list = list;
        this.isVisibleChoiceImg = isVisibleChoiceImg;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder myHolder;
        if (convertView == null) {
            myHolder = new Holder();
            convertView = inflater.inflate(R.layout.single_choice_list_item, null);
            myHolder.title = (TextView) convertView.findViewById(R.id.title);
            myHolder.info = (TextView) convertView.findViewById(R.id.info);
            myHolder.imgSingleChoice = (ImageView) convertView.findViewById(R.id.imgSingleChoice);
            convertView.setTag(myHolder);
        } else {
            myHolder = (Holder) convertView.getTag();
        }
        // myHolder.iv.setBackgroundResource((Integer)
        // list.get(position).get("img"));
        if (isVisibleChoiceImg) {
            if (position == this.currentID) {
                myHolder.imgSingleChoice.setBackgroundDrawable(c.getResources().getDrawable(
                                R.drawable.single_choice_list_switch_selected));
            } else {
                myHolder.imgSingleChoice.setBackgroundDrawable(c.getResources().getDrawable(
                                R.drawable.single_choice_list_switch_normal));
            }
        } else {
            myHolder.imgSingleChoice.setVisibility(View.INVISIBLE);
        }
       String title = (String) list.get(position).get("title");
        String info = (String) list.get(position).get("info");
        if (!TextUtils.isEmpty(title)) {
            myHolder.title.setText(title);
        }
        if (!TextUtils.isEmpty(info)) {
            myHolder.info.setVisibility(View.VISIBLE);
            myHolder.info.setText(info);
        }

        return convertView;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    public void setCurrentID(int currentID) {
        this.currentID = currentID;
    }

    public void setList(ArrayList<Map<String, Object>> list) {
        this.list = list;
    }

}
