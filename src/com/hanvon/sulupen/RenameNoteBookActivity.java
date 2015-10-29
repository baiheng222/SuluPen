package com.hanvon.sulupen;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;

public class RenameNoteBookActivity extends Activity implements OnClickListener
{
	private final String TAG = "RenameNoteBookActivity";
	
	private TextView mTvCancleRename;
	private TextView mTvDoneRename;
	
	@Override
	protected void onCreate(Bundle saveBundleInstance)
	{
		super.onCreate(saveBundleInstance);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
	}
	
	@Override
	public void onClick(View view)
	{
		
	}
}
