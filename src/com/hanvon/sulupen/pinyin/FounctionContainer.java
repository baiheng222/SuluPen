package com.hanvon.sulupen.pinyin;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.PaintDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.baidu.voicerecognition.android.VoiceRecognitionConfig;
import com.hanvon.bluetooth.BluetoothService;
import com.hanvon.sulupen.R;
import com.hanvon.sulupen.ScanNoteActivity;
import com.hanvon.sulupen.adapter.LanguageListAdapter;
import com.hanvon.sulupen.datas.TransLateInfo;
import com.hanvon.sulupen.helper.PreferHelper;
import com.hanvon.sulupen.pinyin.recongnition.BaiduVoiceRecognition;
import com.hanvon.sulupen.utils.LogUtil;
//import android.widget.CheckBox;
//import android.widget.CompoundButton;

public class FounctionContainer extends FrameLayout implements OnClickListener,
		OnItemClickListener {
	private static PopupWindow popupWinMenu;
	private Context context;
	private ImageView ibnMissSkb, ibnEPen, ibnSkbSetting, ibnVoiceRecon;
	private BaiduVoiceRecognition voiceRecognition;
	private PinyinIME mPinyinIME;
	private int popWidth, popHeight;
	private TransLateInfo transLateInfo;
	private int type = 0;
	private RadioButton rbn_originalLan, rbn_targetLan;
	private LanguageListAdapter adapter;
	private static View foreground_layout;
	private RelativeLayout background_layout;
	private String languageType;
	private String fromParam = "auto", toParam = "auto";
	private static int curCheckedBnFlag = 0;
	private ImageView ivScanImage = null;
	Environment env = Environment.getInstance();

	public FounctionContainer(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		int measuredWidth = env.getScreenWidth();
		int measuredHeight = getPaddingTop();
		measuredHeight += env.getHeightForCandidates();
		widthMeasureSpec = MeasureSpec.makeMeasureSpec(measuredWidth,
				MeasureSpec.EXACTLY);
		heightMeasureSpec = MeasureSpec.makeMeasureSpec(measuredHeight,
				MeasureSpec.EXACTLY);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

	}
	/**
	 * 初始化 设置栏
	 * 
	 * @param service
	 */
	public void initialize(PinyinIME service) {
		mPinyinIME = service;
		ibnEPen = (ImageView) findViewById(R.id.ibn_edianPen);
		ibnVoiceRecon = (ImageView) findViewById(R.id.ibn_voice_recongnition);
		ibnMissSkb = (ImageView) findViewById(R.id.ibn_misSkb);
		ibnSkbSetting = (ImageView) findViewById(R.id.ibn_skb_setting);
		foreground_layout = (View) findViewById(R.id.foreground_layout);
		background_layout = (RelativeLayout) findViewById(R.id.background_layout);
		popWidth = env.getScreenWidth();
		popHeight = env.getSkbHeight();
		transLateInfo = new TransLateInfo(mPinyinIME);
		setListener();
	}

	private void setListener() {

		ibnEPen.setOnClickListener(this);
		ibnVoiceRecon.setOnClickListener(this);
		ibnMissSkb.setOnClickListener(this);
		ibnSkbSetting.setOnClickListener(this);
		foreground_layout.setOnClickListener(this);
		foreground_layout.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				LogUtil.e("foreground_layout");
				if (curCheckedBnFlag != 0) {
					LogUtil.e("true");
					return true;
				} else {
					LogUtil.e("false");
					return false;
				}

			}
		});
		background_layout.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				LogUtil.e("background_layout");
				if (curCheckedBnFlag != 0) {
					LogUtil.e("true");
					return true;
				} else {
					LogUtil.e("false");
					return false;
				}
			}
		});

	}

	public void showPopup(ImageView bn, View popView) {
		showForeGround();
		popupWinMenu = new PopupWindow(this);
		popupWinMenu.setWidth(popWidth);
		LogUtil.i("popW:Height----" + popWidth + ":" + popHeight);
		popupWinMenu.setHeight(popHeight);
		popupWinMenu.setOutsideTouchable(true);
		// popupWinMenu.setBackgroundDrawable(new BitmapDrawable());
		popupWinMenu
				.setBackgroundDrawable(new PaintDrawable(Color.TRANSPARENT));
		popupWinMenu.setContentView(popView);
		// popupWinMenu.showAsDropDown(bn,0, 0, Gravity.CENTER_HORIZONTAL);
		popupWinMenu.showAtLocation(bn, Gravity.CENTER_HORIZONTAL
				| Gravity.BOTTOM, 0, 0);
		popupWinMenu.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss() {
				LogUtil.i("onDismiss");

				switch (curCheckedBnFlag) {
				case 0:

					break;
				case 1:
					ivScanImage = null;
					ibnEPen.setBackgroundResource(R.drawable.skb_editor_selector);
					break;
				case 2:
					PreferHelper.saveString(TransLateInfo.Body.from, fromParam);
					PreferHelper.saveString(TransLateInfo.Body.to, toParam);
					ibnSkbSetting
							.setBackgroundResource(R.drawable.skb_setting_selector);
					break;
				case 3:
					voiceRecognition.cancelListening();
					ibnVoiceRecon
							.setBackgroundResource(R.drawable.skb_softkey_selector);
					break;
				case 4:

					break;

				case 5:

					break;

				default:
					break;
				}
				resetState();

			}
		});
	}

	private View initSettingDlgView() {
		PreferHelper.init(context);
		View view = LayoutInflater.from(context).inflate(R.layout.dlg_translate_choice, null);
		Button bn_trans = (Button) view.findViewById(R.id.bn_trans);
//		CheckBox checkBox = (CheckBox) view.findViewById(R.id.cbx_translate_switch);
		bn_trans.setOnClickListener(this);
		rbn_originalLan = (RadioButton) view.findViewById(R.id.rbn_originalLan);
		rbn_targetLan = (RadioButton) view.findViewById(R.id.rbn_targetLan);
		final RadioGroup group = (RadioGroup)view.findViewById(R.id.rg_languageType);
		final GridView gView = (GridView) view.findViewById(R.id.gv_languageList);
		rbn_originalLan.setChecked(false);
		rbn_targetLan.setChecked(true);
		type = 1;
		fromParam = PreferHelper.getString(TransLateInfo.Body.from,
				getResources().getString(R.string.auto));
		toParam = PreferHelper.getString(TransLateInfo.Body.to, getResources()
				.getString(R.string.auto));
		rbn_originalLan.setText(getResources().getString(
				R.string.orignal_language)
				+ fromParam);
		rbn_targetLan.setText(getResources()
				.getString(R.string.target_language) + toParam);
		boolean modeSwitch = PreferHelper.getBoolean(
				TransLateInfo.TARANS_MODE_SWITCH, true);
//		checkBox.setChecked(modeSwitch);
		if (modeSwitch) {
			group.setVisibility(View.VISIBLE);
			gView.setVisibility(View.VISIBLE);
		} else {
			group.setVisibility(View.GONE);
			gView.setVisibility(View.GONE);
		}
/*		checkBox.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					group.setVisibility(View.VISIBLE);
					gView.setVisibility(View.VISIBLE);
				} else {
					group.setVisibility(View.GONE);
					gView.setVisibility(View.GONE);
				}
				PreferHelper.saveBoolean(TransLateInfo.TARANS_MODE_SWITCH,
						isChecked);
			}
		});
*/
		group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup arg0, int id) {
				LogUtil.i("#############");
				if (id == R.id.rbn_originalLan) {
					type = 0;
				} else if (id == R.id.rbn_targetLan) {
					type = 1;
				}
			}
		});

		gView.setFocusable(true);
		adapter = new LanguageListAdapter(context, this,
				transLateInfo.getLanguageTypeList());
		gView.setAdapter(adapter);
		gView.setOnItemClickListener(this);
		return view;
	}

	/**
	 * 扫描图片对话框
	 * 
	 * @return
	 */
	private View initScanImageView() {

		View view = LayoutInflater.from(context).inflate(
				R.layout.dlg_scan_image, null);
		ivScanImage = (ImageView) view.findViewById(R.id.ivScanImage_input);
		return view;
	}

	/**
	 * 语音识别对话框
	 * 
	 * @return
	 */
	private View initVoiceReconView() {

		View view = LayoutInflater.from(context).inflate(
				R.layout.dlg_voice_input, null);
		final ImageView voiceBar = (ImageView) view
				.findViewById(R.id.volumeProgressbar);
		voiceRecognition = new BaiduVoiceRecognition(context, mPinyinIME);		
		voiceBar.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch(event.getAction())  {
				case MotionEvent.ACTION_DOWN:
					Log.i("FounctionContainer", "MotionEvent.ACTION_DOWN=="+MotionEvent.ACTION_DOWN);
					voiceRecognition.startListening();
					voiceBar.setBackgroundResource(R.drawable.voice_recording_press);
					break;
				case MotionEvent.ACTION_UP:
					Log.i("FounctionContainer", "MotionEvent.ACTION_UP=="+MotionEvent.ACTION_UP);
					voiceBar.setBackgroundResource(R.drawable.voice_recording_normal);
					voiceRecognition.stopListening();
					break;
				default:
					break;
				}
				return true;
			}
		});
		languageType = PreferHelper.getString(
				BaiduVoiceRecognition.LANGUAGE_TYPE,
				VoiceRecognitionConfig.LANGUAGE_CHINESE);

		languageType = VoiceRecognitionConfig.LANGUAGE_CHINESE;
		PreferHelper.saveString(
				BaiduVoiceRecognition.LANGUAGE_TYPE,
				languageType);
		voiceRecognition.updateConfig(languageType);
		return view;
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		// E典笔蓝牙连接操作
		case R.id.ibn_edianPen:
			// Intent serverIntent = new Intent(mPinyinIME,
			// DeviceListActivity.class);
			// serverIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			// mPinyinIME.startActivity(serverIntent);
			if (curCheckedBnFlag != 1) {
				curCheckedBnFlag = 1;
				// ibnEPen.setBackgroundResource(R.drawable.edit_press);
				showPopup(ibnEPen, initScanImageView());
				BluetoothService.scanRecordMode=3;
			} else {
				curCheckedBnFlag = 0;
			}
			break;

		// 隐藏输入法键盘功能
		case R.id.ibn_misSkb:
			mPinyinIME.resetToIdleState(false);
			mPinyinIME.requestHideSelf(0);
			curCheckedBnFlag = 6;
			break;

		// 语音识别
		case R.id.ibn_voice_recongnition:
			if (curCheckedBnFlag != 3) {
				curCheckedBnFlag = 3;
				ibnVoiceRecon.setBackgroundResource(R.drawable.voice_rec_press);
				showPopup(ibnVoiceRecon, initVoiceReconView());
			} else {
				curCheckedBnFlag = 0;
			}
			break;

		// 软键盘设置功能
		case R.id.ibn_skb_setting:
			if (curCheckedBnFlag != 2) {
				curCheckedBnFlag = 2;
				ibnSkbSetting
						.setBackgroundResource(R.drawable.skb_setting_press);
				showPopup(ibnSkbSetting, initSettingDlgView());
			} else {
				curCheckedBnFlag = 0;
			}

			break;
			// 软键盘中的翻译按钮，翻译功能
		case R.id.bn_trans:
			break;
		case R.id.foreground_layout:
			resetState();
			break;
		default:
			break;
		}

	}

	public static void resetState() {
		if (popupWinMenu != null && popupWinMenu.isShowing()) {
			popupWinMenu.dismiss();
			popupWinMenu = null;

		}
		disMissForeGround();
	}

	private void showForeGround() {
		foreground_layout.setVisibility(View.VISIBLE);

	}

	public static void disMissForeGround() {
		foreground_layout.setVisibility(View.INVISIBLE);
	}

	@Override
	public void onItemClick(AdapterView<?> gridView, View arg1, int position,
			long arg3) {
		LogUtil.i("onItemClick");
		doItemClick(position);
		// if(type==0){
		// fromString=transLateInfo.getLanguageTypeList().get(position);
		// rbn_originalLan.setText(getResources().getString(R.string.orignal_language)+fromString);
		// }
		// else {
		// toString=transLateInfo.getLanguageTypeList().get(position);
		// rbn_targetLan.setText(getResources().getString(R.string.target_language)+toString);
		// }
		// adapter.setSelection(position);
		// adapter.notifyDataSetChanged();
	}

	public void doItemClick(int position) {
		if (type == 0) {
			fromParam = transLateInfo.getLanguageTypeList().get(position);
			rbn_originalLan.setText(getResources().getString(
					R.string.orignal_language)
					+ fromParam);
		} else {
			toParam = transLateInfo.getLanguageTypeList().get(position);
			rbn_targetLan.setText(getResources().getString(
					R.string.target_language)
					+ toParam);
		}
		adapter.setSelection(position);
		adapter.notifyDataSetChanged();
	}

	public boolean popIsShowing() {
		return popupWinMenu != null && popupWinMenu.isShowing();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (KeyEvent.KEYCODE_BACK == keyCode) {
			if (popupWinMenu != null && popupWinMenu.isShowing()) {
				LogUtil.i("KEYCODE_BACK");
				popupWinMenu.dismiss();
				popupWinMenu = null;
				resetState();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public ImageView getEpenBn() {
		return ibnEPen;
	}

	/**
	 * 设置扫描图片
	 * 
	 * @param bmp
	 */
	public void setScanImage(Bitmap bmp) {
		LogUtil.i("tong-----curCheckedBnFlag :"+curCheckedBnFlag);
		if (curCheckedBnFlag == 1) {
			LogUtil.i("tong-----curCheckedBnFlag == 1");
			if (ivScanImage != null)
			{
				LogUtil.i("tong-----ivScanImage != null");
				ivScanImage.setImageBitmap(bmp);
			}
				
		}
		
		if (ScanNoteActivity.scanNoteAct != null)
		{
			LogUtil.i("tong---------ScanNoteDetailActivity");
			ScanNoteActivity.scanNoteAct.setScanImage(bmp);
		}
		
		
	
	}
}
