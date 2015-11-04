package com.hanvon.sulupen.ui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.hanvon.sulupen.ScanNoteActivity;
import com.hanvon.sulupen.R;
import com.hanvon.sulupen.adapter.ImageBucketAdapter;
import com.hanvon.sulupen.datas.ImageBucket;
import com.hanvon.sulupen.utils.CustomConstants;
import com.hanvon.sulupen.utils.ImageFetcher;
import com.hanvon.sulupen.utils.IntentConstants;
import com.hanvon.sulupen.utils.LogUtil;

/**
 * 选择相册
 * 
 */

public class ImageBucketChooseActivity extends Activity {
	private ImageFetcher mHelper;
	private List<ImageBucket> mDataList = new ArrayList<ImageBucket>();
	private ListView mListView;
	private ImageBucketAdapter mAdapter;
	private int availableSize;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		LogUtil.i("tong-------ImageBucketChooseActivity oncreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_image_bucket_choose);

		mHelper = ImageFetcher.getInstance(getApplicationContext());
		initData();
		initView();
		LogUtil.i("tong-------ImageBucketChooseActivity oncreate end");
	}

	private void initData() {
		mDataList = mHelper.getImagesBucketList(false);
		availableSize = getIntent().getIntExtra(
				IntentConstants.EXTRA_CAN_ADD_IMAGE_SIZE,
				CustomConstants.MAX_IMAGE_SIZE);
	}

	private void initView() {
		mListView = (ListView) findViewById(R.id.listview);
		mAdapter = new ImageBucketAdapter(this, mDataList);
		mListView.setAdapter(mAdapter);
		TextView titleTv = (TextView) findViewById(R.id.title);
		titleTv.setText("相册");
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				selectOne(position);

				Intent intent = new Intent(ImageBucketChooseActivity.this,
						ImageChooseActivity.class);
				intent.putExtra(IntentConstants.EXTRA_IMAGE_LIST,
						(Serializable) mDataList.get(position).imageList);
				intent.putExtra(IntentConstants.EXTRA_BUCKET_NAME,
						mDataList.get(position).bucketName);
				intent.putExtra(IntentConstants.EXTRA_CAN_ADD_IMAGE_SIZE,
						availableSize);

				startActivity(intent);
			
			}
		});

		TextView cancelTv = (TextView) findViewById(R.id.action);
		cancelTv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ImageBucketChooseActivity.this,
						ScanNoteActivity.class);
				startActivity(intent);
				
			}
		});
	}

	private void selectOne(int position) {
		int size = mDataList.size();
		for (int i = 0; i != size; i++) {
			if (i == position)
				mDataList.get(i).selected = true;
			else {
				mDataList.get(i).selected = false;
			}
		}
		mAdapter.notifyDataSetChanged();
	}

}
