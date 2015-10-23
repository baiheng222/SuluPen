package com.hanvon.sulupen;



import android.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;



public class NoteDetailActivity extends Activity implements OnClickListener
{

	
    private Button mConfirmButton;
    private TextView mTextView ;  
  
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(activity_note_detail);
        initView();

	}
	
	private void initView() {

		mConfirmButton=(Button) findViewById(R.id.save_button);
		mTextView = (TextView) findViewById(R.id.etNoteContent);
		
		mConfirmButton.setOnClickListener(this);
		
		
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
