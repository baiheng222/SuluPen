package com.hanvon.sulupen.pinyin.recongnition;

import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.widget.ImageView;
import com.baidu.voicerecognition.android.Candidate;
import com.baidu.voicerecognition.android.VoiceRecognitionClient;
import com.baidu.voicerecognition.android.VoiceRecognitionClient.VoiceClientStatusChangeListener;
import com.baidu.voicerecognition.android.VoiceRecognitionConfig;
//import com.hanvon.sulupen.application.Configs;
import com.hanvon.sulupen.helper.NetWorkHelper;
import com.hanvon.sulupen.helper.PreferHelper;
import com.hanvon.sulupen.pinyin.FounctionContainer;
import com.hanvon.sulupen.pinyin.PinyinIME;
import com.hanvon.sulupen.application.Configs;
import com.hanvon.sulupen.R;
import com.hanvon.sulupen.utils.LogUtil;

/**
 * @desc 百度语音识别类
 * @author  PengWenCai
 * @time 2015-6-25 上午11:40:29
 * @version
 */
public class BaiduVoiceRecognition {
	public static final int STATUS_IDLE = 1;

	public static final int STATUS_RECORDING_START = STATUS_IDLE << 1;

	public static final int STATUS_SPEECH_START = STATUS_IDLE << 2;

	public static final int STATUS_SPEECH_END = STATUS_IDLE << 3;

	public static final int STATUS_FINISH = STATUS_IDLE << 4;
	public static final int STATUS_UPDATE_RESULTS = STATUS_IDLE << 5;
	public static final int STATUS_ERROR = STATUS_IDLE << 6;
	public static final int UPDATE_VOLUME = 0x1201;
	private Context context;
	private VoiceRecognitionClient mASREngine;
	private VoiceClientStatusChangeListener mListener = new MyVoiceRecogListener();
	VoiceRecognitionConfig voiceRecognitionConfig;

	/**
	 * 当前状态
	 */
	private int curState = STATUS_IDLE;
	/** 正在识别中 */
	public static boolean isRecognition = false;
	/**
	 * 音量更新间隔
	 */
	public static final int POWER_UPDATE_INTERVAL = 100;
	private String voiceResult = "";
	private Handler mHandler;
	public static final String LANGUAGE_TYPE = "language_type";
	private PinyinIME mService;

	/**
	 * 
	 * @param context
	 * @param handler
	 * @param mPinyinIME
	 */

	public BaiduVoiceRecognition(Context context, PinyinIME mPinyinIME) {
		this.context = context;
		mASREngine = VoiceRecognitionClient.getInstance(context);
		mASREngine.setTokenApis(Configs.APIKEY, Configs.SECRET_KEY);
		voiceRecognitionConfig = new VoiceRecognitionConfig();
		mHandler = new Handler();
		mService = mPinyinIME;
		PreferHelper.init(context);
		initConfig();
	}

	public VoiceRecognitionClient getmASREngine() {
		return mASREngine;
	}

	public boolean startListening() {
		int code = VoiceRecognitionClient.START_WORK_RESULT_WORKING;
		if ("".equals(NetWorkHelper.getNetState(context))) {
		
		} else {
			code = mASREngine.startVoiceRecognition(mListener,
					voiceRecognitionConfig);
			if (code != VoiceRecognitionClient.START_WORK_RESULT_WORKING) {
				// LogUtil.i("启动失败");
				// if(reStartTimes<=reStartCountTimes){
				// startListening();
				// reStartTimes++;
				//
				// }
				// else{
				// reStartTimes=0;
				//
				// }
			}
		}

		/**
		 * 启动失败
		 */

		return code == VoiceRecognitionClient.START_WORK_RESULT_WORKING;

	}
	public boolean stopListening() {
		mASREngine.speakFinish();
		return true;
	}

	public boolean cancelListening() {
		mASREngine.stopVoiceRecognition();
		FounctionContainer.resetState();
		return true;
	}

	private void initConfig(){
		voiceRecognitionConfig.enableVoicePower(true); // 音量反馈。
		voiceRecognitionConfig
				.enableBeginSoundEffect(R.raw.bdspeech_recognition_start); // 设置识别开始提示音
		voiceRecognitionConfig.enableEndSoundEffect(R.raw.bdspeech_speech_end); // 设置识别结束提示音
		voiceRecognitionConfig
				.setSampleRate(VoiceRecognitionConfig.SAMPLE_RATE_8K); // 设置采样率,需要与外部音频一致
		String languageString = PreferHelper.getString(LANGUAGE_TYPE,
				VoiceRecognitionConfig.LANGUAGE_CHINESE);
		voiceRecognitionConfig.setLanguage(languageString);
	}

	public void updateConfig(String type) {
		voiceRecognitionConfig.setLanguage(type);
		startListening();
	}

	/**
	 * 处理语音识别回调的监听器
	 * 
	 * @author pwc
	 * 
	 */
	class MyVoiceRecogListener implements VoiceClientStatusChangeListener {

		@Override
		public void onClientStatusChange(int status, Object obj) {
			switch (status) {
			// 语音识别实际开始，真正开始识别的时间点，需要提示用户说话
			case VoiceRecognitionClient.CLIENT_STATUS_START_RECORDING:
				isRecognition = true;
				mHandler.removeCallbacks(mUpdateVolume);
				mHandler.postDelayed(mUpdateVolume, POWER_UPDATE_INTERVAL);
				curState = VoiceRecognitionClient.CLIENT_STATUS_START_RECORDING;
				break;
			// 检测到语音起点
			case VoiceRecognitionClient.CLIENT_STATUS_SPEECH_START:
				curState = VoiceRecognitionClient.CLIENT_STATUS_SPEECH_START;
				// mHandler.sendEmptyMessage(curState);
				break;
			// 已经检测到语音终点，等待网络返回
			case VoiceRecognitionClient.CLIENT_STATUS_SPEECH_END:
				curState = VoiceRecognitionClient.CLIENT_STATUS_SPEECH_END;
				// mHandler.sendEmptyMessage(curState);
				break;
			// 语音识别完成，显示obj中的结果
			case VoiceRecognitionClient.CLIENT_STATUS_FINISH:
				isRecognition = false;
				curState = VoiceRecognitionClient.CLIENT_STATUS_FINISH;
				updateRecognitionResult(obj, 0);
				LogUtil.i("");
				// Message msg=new Message();
				// msg.obj=obj;
				// msg.what=curState;
				// mHandler.sendMessage(msg);
				break;
			// 处理连续上屏
			case VoiceRecognitionClient.CLIENT_STATUS_UPDATE_RESULTS:
				updateRecognitionResult(obj, 1);
				LogUtil.i("");
				curState = VoiceRecognitionClient.CLIENT_STATUS_UPDATE_RESULTS;
				// Message msg1=new Message();
				// msg1.obj=obj;
				// msg1.what=curState;
				// mHandler.sendMessage(msg1);
				break;
			// 用户取消
			case VoiceRecognitionClient.CLIENT_STATUS_USER_CANCELED:
				curState = VoiceRecognitionClient.CLIENT_STATUS_USER_CANCELED;
				isRecognition = false;
				// mHandler.sendEmptyMessage(curState);
				break;
			default:
				break;
			}

		}

		@Override
		public void onError(int errorType, int errorCode) {
			isRecognition = false;
			curState = STATUS_ERROR;
			LogUtil.e("errorType:" + errorType + "&&errorCode:" + errorCode);
			switch (errorType) {
			/**
			 * 客户端异常
			 */
			case VoiceRecognitionClient.ERROR_CLIENT:
				curState = VoiceRecognitionClient.ERROR_CLIENT;
				if (VoiceRecognitionClient.ERROR_CLIENT_NO_SPEECH == errorCode) {
					LogUtil.i("没有说话");
				} else if (VoiceRecognitionClient.ERROR_CLIENT_TOO_SHORT == errorCode) {
					LogUtil.i("语音太短");
				}

				break;
			/**
			 * 网络异常
			 */
			case VoiceRecognitionClient.ERROR_NETWORK:
				curState = VoiceRecognitionClient.ERROR_NETWORK;
				if (VoiceRecognitionClient.ERROR_NETWORK_UNUSABLE == errorCode) {
					LogUtil.i("网络不可用");
				} else if (VoiceRecognitionClient.ERROR_NETWORK_CONNECT_ERROR == errorCode) {
					LogUtil.i("连接失败");
				}
				break;
			/**
			 * 设备异常
			 */
			case VoiceRecognitionClient.ERROR_RECORDER:
				curState = VoiceRecognitionClient.ERROR_RECORDER;
				if (VoiceRecognitionClient.ERROR_RECORDER_UNAVAILABLE == errorCode) {
					LogUtil.i("设备不可用");
				}
				break;
			/**
			 * 服务器异常
			 */
			case VoiceRecognitionClient.ERROR_SERVER:
				curState = VoiceRecognitionClient.ERROR_SERVER;
				if (VoiceRecognitionClient.ERROR_SERVER_RECOGNITION_ERROR == errorCode) {
					LogUtil.i("识别错误");
				} else if (VoiceRecognitionClient.ERROR_SERVER_SPEECH_QUALITY_ERROR == errorCode) {
					LogUtil.i("语音质量错误");
				}
				break;
			default:
				break;
			}

		}

		@Override
		public void onNetworkStatusChange(int arg0, Object arg1) {
		}

	}

	/**
	 * 将识别结果更新到UI上，搜索模式结果类型为List<String>,输入模式结果类型为List<List<Candidate>>
	 * 
	 * @param result
	 */
	public void updateRecognitionResult(Object result, int flag) {
		if (result != null && result instanceof List) {
			List results = (List) result;
			if (results.size() > 0) {
				if (results.get(0) instanceof List) {
					List<List<Candidate>> sentences = (List<List<Candidate>>) result;
					StringBuffer sb = new StringBuffer();
					for (List<Candidate> candidates : sentences) {
						if (candidates != null && candidates.size() > 0) {
							sb.append(candidates.get(0).getWord());
						}
					}
					if (flag == 1) {
						mService.SetComposingText(sb.toString());
					} else {
						mService.commitResultText(sb.toString());
					}

				} else {
					if (flag == 1) {
						mService.SetComposingText(results.get(0).toString());
					} else {
						mService.commitResultText(results.get(0).toString());
					}
				}
			}
		}
	}

	/**
	 * 音量更新任务
	 */
	private Runnable mUpdateVolume = new Runnable() {
		public void run() {
			if (isRecognition) {
				long vol = mASREngine.getCurrentDBLevelMeter();
				// curVolume=(int)vol;
				// Message msg=new Message();
				// msg.arg1=curVolume;
				// msg.what=UPDATE_VOLUME;
				// mHandler.sendMessage(msg);
				mHandler.removeCallbacks(mUpdateVolume);
				mHandler.postDelayed(mUpdateVolume, POWER_UPDATE_INTERVAL);
			}
		}
	};

	public String getVoiceResult() {
		return voiceResult;
	}

	public void setVoiceResult(String voiceResult) {
		this.voiceResult = voiceResult;
	}

	public int getCurState() {
		return curState;
	}

	public void setCurState(int curState) {
		this.curState = curState;
	}

	}

