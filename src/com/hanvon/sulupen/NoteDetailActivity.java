package com.hanvon.sulupen;




import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;



public class NoteDetailActivity extends Activity implements OnClickListener
{
    private Button mConfirmButton;
    private TextView mTextView ;  
    private int flagIntent = -1;
  
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_note_detail);
        initView();

	}
	
	private void initView() 
	{
		mConfirmButton=(Button) findViewById(R.id.save_button);
		mTextView = (TextView) findViewById(R.id.etNoteContent);
		
		mConfirmButton.setOnClickListener(this);
	}
	
	private void initDatas()
	{
		Intent intent = getIntent();
		if (intent != null) 
		{
			flagIntent = intent.getFlags();
			if (flagIntent == MainActivity.FLAG_CREATE) 
			{
				
			}
		}
	}
	
	@Override
	public void onClick(View v) 
	{
	     switch (v.getId())
	        {
	            case R.id.save_button:
	            	
	            break;
	        }
	}
	     
	            	
}
