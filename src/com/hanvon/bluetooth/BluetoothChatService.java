package com.hanvon.bluetooth;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import com.hanvon.sulupen.utils.LogUtil;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class BluetoothChatService {

	/**
	 * 从BluetoothChatService发送处理程序的消息类型
	 */
	public static final int BLUETOOTH_MESSAGE_READ = 2;
	public static final int MESSAGE_STATE_CHANGE = 1;
	public static final int MESSAGE_WRITE = 3;
	public static final int MESSAGE_DEVICE_NAME = 4;
	public static final int MESSAGE_TOAST = 5;
	public static final String DEVICE_NAME = "device_name";
	public static final String TOAST = "toast";

	public static final int STATE_NONE = 0;
	public static final int STATE_LISTEN = 1;
	public static final int STATE_CONNECTING = 2;
	public static final int STATE_CONNECTED = 3;
	private static ProgressDialog mProgressDialog;
	private Context context;
	/**
	 * 当前连接的设备名和mac地址
	 */
	public static String curDeviceName = "", curDeviceAddress = "";
	// 测试数据
	private static final String TAG = "BluetoothChatService";
	private static final boolean D = true;
	private static final String NAME = "EpenBluetooth";
	/**
	 * 声明一个唯一的UUID 串口服务
	 */
	private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

	private final BluetoothAdapter mAdapter;
	private final Handler mHandler;
	private AcceptThread mAcceptThread;
	private ConnectThread mConnectThread;
	private BTConnectedThread mConnectedThread;
	private int mState;

	public BluetoothChatService(Context context, Handler handler) {
		mAdapter = BluetoothAdapter.getDefaultAdapter();
		mState = STATE_NONE;
		mHandler = handler;
		this.context = context;
	}
	
	private synchronized void setState(int state) {
		LogUtil.d("setState() " + mState + " -> " + state);
		mState = state;
		// 通知Activity更新UI
		mHandler.obtainMessage(MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
	}

	/**
	 * 返回当前连接状态
	 * 
	 */
	public synchronized int getState() {
		return mState;
	}
	
	/**
	 * @function:关闭所有线程，断开socket连接
	 */
	public synchronized void stop() {
		if (D)
			LogUtil.d("stop");
		setState(STATE_NONE);
		if (mConnectThread != null) {
			mConnectThread.cancel();
			mConnectThread = null;
		}

		if (mConnectedThread != null) {
			mConnectedThread.cancel();
			mConnectedThread = null;
		}

		if (mAcceptThread != null) {
			mAcceptThread.cancel();
			mAcceptThread = null;
		}
	}
	/**
	 * 
	 * @desc 设备开始连接
	 * @param device
	 */
	public synchronized void connect(BluetoothDevice device) {

			LogUtil.i("开始连接设备: " + device);
		// showProgressDialog();
		if (mState == STATE_CONNECTING) {
			if (mConnectThread != null) {
				mConnectThread.cancel();
				mConnectThread = null;
			}
		}

		if (mConnectedThread != null) {
			mConnectedThread.cancel();
			mConnectedThread = null;
		}

		mConnectThread = new ConnectThread(device);
		mConnectThread.start();
		setState(STATE_CONNECTING);
	}

	
	private class AcceptThread extends Thread {
		private final BluetoothServerSocket mmServerSocket;

		@SuppressLint("NewApi") public AcceptThread() {
			BluetoothServerSocket tmp = null;
			int version = Build.VERSION.SDK_INT;
			if (version >= 10) {
				try {
					tmp = mAdapter.listenUsingInsecureRfcommWithServiceRecord(NAME, MY_UUID);
				} catch (IOException e) {
					LogUtil.e("listenUsingRfcommWithServiceRecord() failed:" + e.toString());
				}
			} else {
				try {
					tmp = mAdapter.listenUsingRfcommWithServiceRecord(NAME,MY_UUID);
				} catch (IOException e) {
					LogUtil.e("listenUsingRfcommWithServiceRecord() failed:" + e.toString());
				}
			}

			mmServerSocket = tmp;
		}

		public void run() {
			LogUtil.d("begin mAcceptThread");
			setName("AcceptThread");
			BluetoothSocket socket = null;

			while (mState != STATE_CONNECTED) {
				try {
					if (mmServerSocket != null)
						socket = mmServerSocket.accept();
				} catch (IOException e) {
					LogUtil.e(e.toString());
					break;
				}
				// 如果连接被接受
				if (socket != null) {
					synchronized (BluetoothChatService.this) {
						switch (mState) {
						case STATE_LISTEN:
						case STATE_CONNECTING:
							// 开始连接线程
							connected(socket, socket.getRemoteDevice());
							break;
						case STATE_NONE:
							break;
						case STATE_CONNECTED:
							// 没有准备好或已经连接
							try {
								socket.close();
							} catch (IOException e) {
								Log.e(TAG, "不能关闭这些连接");
							}
							break;
						}
					}
				}
			}

			LogUtil.i("结束mAcceptThread");
		}

		public void cancel() {
			LogUtil.i("cancel " + this);
			try {
				if (mmServerSocket != null)
					mmServerSocket.close();
			} catch (IOException e) {
				LogUtil.e("cancel failed");
			}
		}
	}
	
	public synchronized void connected(BluetoothSocket socket,
			BluetoothDevice device) {
		if (D)
			LogUtil.d("连接");

		if (mConnectThread != null) {
			mConnectThread.cancel();
			mConnectThread = null;
		}

		if (mConnectedThread != null) {
			mConnectedThread.cancel();
			mConnectedThread = null;
		}
		if (mAcceptThread != null) {
			mAcceptThread.cancel();
			mAcceptThread = null;
		}
		curDeviceName = device.getName();
		curDeviceAddress = device.getAddress();
		setState(STATE_CONNECTED);
		mConnectedThread = new BTConnectedThread(socket);
		mConnectedThread.start();
	}
	
	private void connectionFailed() {
		setState(STATE_NONE);
		Message msg = mHandler.obtainMessage(MESSAGE_TOAST);
		Bundle bundle = new Bundle();
		bundle.putString(TOAST, "无法连接设备");
		msg.setData(bundle);
		mHandler.sendMessage(msg);
	}

	private void connectionLost() {
		setState(STATE_NONE);
		Message msg = mHandler.obtainMessage(MESSAGE_TOAST);
		Bundle bundle = new Bundle();
		bundle.putString(TOAST, "设备断开连接");
		msg.setData(bundle);
		mHandler.sendMessage(msg);
	}
	
	public synchronized void start() {
		if (D)
			Log.d(TAG, "start");
		if (mConnectedThread != null) {
			mConnectedThread.cancel();
			mConnectedThread = null;
		}

		if (mConnectThread != null) {
			mConnectThread.cancel();
			mConnectThread = null;
		}

		if (mAcceptThread == null) {
			mAcceptThread = new AcceptThread();
			mAcceptThread.start();
		}

		setState(STATE_LISTEN);
	}
	
	private class ConnectThread extends Thread {
		private BluetoothSocket mmSocket = null;
		private final BluetoothDevice mmDevice;

		@TargetApi(Build.VERSION_CODES.GINGERBREAD_MR1)
		public ConnectThread(BluetoothDevice device) {
			mmDevice = device;
			BluetoothSocket tmp = null;

			int sdk = Build.VERSION.SDK_INT;
			if (sdk >= 10) {
				try {
					tmp = device.createInsecureRfcommSocketToServiceRecord(MY_UUID);
					LogUtil.i(">10");
				} catch (IOException e) {
					LogUtil.e(e.getMessage());
					e.printStackTrace();
				}
			} else {
				try {
					tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
					LogUtil.i("<10");
				} catch (IOException e) {
					LogUtil.e(e.getMessage());
					e.printStackTrace();
				}
			}

			mmSocket = tmp;

		}

		public void run() {
			LogUtil.i("开始mConnectThread");
			setName("ConnectThread");

			try {
				mmSocket.connect();
			} catch (IOException e) {
				LogUtil.e("无法连接到设备：" + e.toString());
				connectionFailed();
				try {
					mmSocket.close();
				} catch (IOException e2) {
					LogUtil.e("关闭连接失败");
				}
				BluetoothChatService.this.start();
				return;
			}

			synchronized (BluetoothChatService.this) {
				mConnectThread = null;
			}
			connected(mmSocket, mmDevice);
		}

		public void cancel() {
			try {
				mmSocket.close();
			} catch (IOException e) {
				LogUtil.e("关闭连接失败");
			}
		}
	}

	public class BTConnectedThread extends Thread {
		private final BluetoothSocket mmSocket;
		private final InputStream mmInStream;
		private final OutputStream mmOutStream;
		private DataInputStream dInStream;
		private DataOutputStream dOutStream;
		int isMsg = 0;
		byte[] dataStartSign = new byte[4];
		byte[] dataPackageSize = new byte[4];

		public BTConnectedThread(BluetoothSocket socket) {
			LogUtil.d("创建 ConnectedThread");
			mmSocket = socket;

			InputStream tmpIn = null;
			OutputStream tmpOut = null;

			// 得到BluetoothSocket输入和输出流

			try {
				tmpIn = socket.getInputStream();
				tmpOut = socket.getOutputStream();
			} catch (IOException e) {
				LogUtil.e("temp sockets not created");
			}

			mmInStream = tmpIn;
			mmOutStream = tmpOut;
			dInStream = new DataInputStream(mmInStream);
			dOutStream = new DataOutputStream(mmOutStream);
		}

		public void run() {
			LogUtil.i("BEGIN mConnectedThread and mState:"+mState);

			
			// 循环监听消息
			while (mState == STATE_CONNECTED) {
				receiveData();
			}
		}
		
		private synchronized void emptyInvalidData() {
			LogUtil.i("emptyInvalidData in");
			byte[] dataBytes_This = new byte[512];
			String resultStr = "";
			int recvdNum_This = 0;

			while (true) {
				try {
					recvdNum_This = dInStream.read(dataBytes_This);
					LogUtil.i("recNum_This=" + recvdNum_This);
				} catch (IOException e) {
					e.printStackTrace();
				}

				if (recvdNum_This >= 4) {
					try {
						String dataString_This = new String(dataBytes_This, 0,
								recvdNum_This, BluetoothDataPackage.charsetName);
				//		LogUtil.i("dataString_This=" + dataString_This);
						resultStr = dataString_This.substring(dataString_This.length() - 4);
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				} else {
					try {
						String str = new String(dataBytes_This, 0,recvdNum_This, BluetoothDataPackage.charsetName);
				//		LogUtil.i("str=" + str);
						resultStr = resultStr.concat(str);
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				// check end sign "TEOF"
				if (resultStr.length() < 4)
					continue;

				if (resultStr.endsWith(BluetoothDataPackage.DATA_END_SIGN) == true) {
					return;
				}
			}
		}

		private synchronized void receiveData() {
			int dataType = 0;
			int dataSize = 0;
			int startSign = -1;
			try {
				startSign = dInStream.readInt();
				dataSize = dInStream.readInt();
				LogUtil.i("startSign = " + startSign + " ## dataSize = "
						+ dataSize + "<" + Integer.MAX_VALUE);

				if (startSign != BluetoothDataPackage.DATA_START_SIGN || dataSize < 0
						|| dataSize > Integer.MAX_VALUE) {
					LogUtil.i("call emptyInvalidData");
					emptyInvalidData();
					return;
				}
				dataType = dInStream.readByte();
				LogUtil.i("dataType = " + dataType);
				switch (dataType) {
				// 文件类型
				case 1:
					long receiveLen = 0;
					int len = 0;
					int percent = 0;
					byte[] buffer = new byte[1024 * 2];
					int fileHeadlen = dInStream.readInt(); // 文件头长度

					byte[] fhByte = new byte[fileHeadlen];
					dInStream.read(fhByte); // 读取文件头内容

					long fileLen = dataSize - fileHeadlen; // 文件长度

					String fileHead = new String(fhByte,BluetoothDataPackage.charsetName);
					JSONObject fileHeadData = null;
					String fileName = "";
					try {
						fileHeadData = new JSONObject(fileHead);
						fileName = fileHeadData.getString("fileName");
						LogUtil.i(fileName);
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					// creat file and directory
					String fileSavePath = BluetoothDataPackage.fileSavePath + File.separator + fileName;
					File file = new File(fileSavePath);
					File parentFile = new File(file.getParent());
					if (!parentFile.exists()) {
						parentFile.mkdirs();
					}
					FileOutputStream fos = new FileOutputStream(file, false);

					while (receiveLen < fileLen) {
						len = dInStream.read(buffer);
						if (receiveLen + len > fileLen) {
							fos.write(buffer, 0, (int) (fileLen - receiveLen));
						} else {
							fos.write(buffer, 0, len);
						}

						receiveLen += len;
						percent = (int) (receiveLen * 100 / fileLen);
						mHandler.obtainMessage(BLUETOOTH_MESSAGE_READ,
								dataType, percent).sendToTarget();
					}
					fos.flush();
					fos.close();

					// read end sign
					dInStream.readFully(buffer, 0, 4);
					LogUtil.i("have read the end sign!!");

					mHandler.obtainMessage(BLUETOOTH_MESSAGE_READ, dataType,
							-1, fileHeadData).sendToTarget();
					break;
				// 非文件类型 （数据包类型）
				case 2:

					int pSize = (int) dataSize;
					if (pSize < Integer.MAX_VALUE)
						LogUtil.i("pSize:" + pSize);
					int bufferLen = 64;
					byte[] dataBytes = new byte[pSize];
					LogUtil.i("分配内存");
					int size = 0;
					int receivedSize = 0;
					JSONObject jsonData = null;
					long data_receive_start_time;
					long data_receive_end_time;
					data_receive_start_time = System.currentTimeMillis();
					while (receivedSize < pSize) {
						size = dInStream.read(dataBytes, receivedSize, pSize
								- receivedSize);
						receivedSize += size;
					}
					data_receive_end_time = System.currentTimeMillis();
					LogUtil.i("receiving data done," + "totalLen:" + receivedSize + "time lapse:"
							+ (data_receive_end_time - data_receive_start_time));

					if (receivedSize == pSize) {
						LogUtil.i("receivedSize==pSize");
						try {
							jsonData = new JSONObject(new String(dataBytes,
									BluetoothDataPackage.charsetName));
							// dataBytes=null;
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

					// read end sign
					dInStream.readFully(dataBytes, 0, 4);
					LogUtil.i("have read the end sign!!");

					mHandler.obtainMessage(BLUETOOTH_MESSAGE_READ, dataType,
							-1, jsonData).sendToTarget();
					LogUtil.i("handlerMsg");
					break;
				default:
					LogUtil.i("call emptyInvalidData");
					emptyInvalidData();
					break;
				}

			} catch (IOException e) {
				LogUtil.e("error:" + e.toString() + "eMsg:" + e.getMessage());
				connectionLost();
				try {
					BluetoothChatService.this.start();
				} catch (Exception e2) {
				}
			} finally {
				System.gc();
			}

		}
	
		/**
		 * 发送数据
		 * 
		 * @param dataType
		 * @param dataStr
		 */
		private void sendData(byte dataType, String dataStr) {
			// 传文件
			boolean isSendSuccess = true;
			LogUtil.i("type:"+dataType+" and dataStr:"+dataStr);
			if (dataType == 1) {
				byte[] buffer = new byte[256];
				int size = 0;
				long sendLen = 0;
				int percent = 0;
				try {
					File file = new File(dataStr);
					FileInputStream fis = new FileInputStream(file);
					
					byte[] fhByte = BluetoothDataPackage.epenFileHead(file).getBytes(
							BluetoothDataPackage.charsetName);
					int fileLen = fis.available();// 文件长度
					int totalLen = 4 + fhByte.length + fileLen;
					
					dOutStream.writeInt(BluetoothDataPackage.DATA_START_SIGN);// 开始标志
					dOutStream.writeInt(totalLen); // 写入 总长度
					dOutStream.writeByte(dataType); // 写入 数据类型
					
					dOutStream.writeInt(fhByte.length); // 写入 文件头长度
					dOutStream.write(fhByte); // 写入 文件头
					dOutStream.flush();
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					LogUtil.i("000 fileLen:" + fileLen);
					while ((size = fis.read(buffer)) != -1) {
						dOutStream.write(buffer, 0, size);
						try {
							Thread.sleep(40);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						dOutStream.flush();
						sendLen += size;
						LogUtil.i("sent size:" + size);
					}
					fis.close();
					LogUtil.i("111 sent fileLen:" + sendLen);
					
					mHandler.obtainMessage(MESSAGE_WRITE, dataType, 105)
							.sendToTarget();

				} catch (FileNotFoundException e) {
					isSendSuccess = false;
					LogUtil.i(dataStr+" file is not find!");
					e.printStackTrace();
				} catch (IOException e) {
					isSendSuccess = false;
					LogUtil.i(" when read file file is error!");
					e.printStackTrace();
				}

			}
			// 非文件数据
			if (dataType == 2) {
				try {
					byte[] dataByte = dataStr.getBytes(BluetoothDataPackage.charsetName);
					int totalLen = dataByte.length;
					dOutStream.writeInt(BluetoothDataPackage.DATA_START_SIGN);// 开始标志
					dOutStream.writeInt(totalLen);
					dOutStream.writeByte(dataType);
					dOutStream.write(dataByte);
					dOutStream.flush();
				} catch (UnsupportedEncodingException e1) {
					isSendSuccess = false;
					e1.printStackTrace();
				} catch (IOException e) {
					isSendSuccess = false;
					e.printStackTrace();
					LogUtil.e("发送数据异常");
				}

			}

			if (isSendSuccess){
			    // send end sign
			    try {
				    byte[] dataByte = BluetoothDataPackage.DATA_END_SIGN
						    .getBytes(BluetoothDataPackage.charsetName);
				    dOutStream.write(dataByte);
				    dOutStream.flush();
				    LogUtil.i("write : " + BluetoothDataPackage.DATA_END_SIGN);
			    } catch (IOException e2) {
				    e2.printStackTrace();
			    }
			}
		}

		public void cancel() {
			try {
				mmSocket.close();
			} catch (IOException e) {
				LogUtil.e("close() of connect socket failed");
			}
		}
	}
	
	public synchronized void sendBTData(int dataType, String dataStr) {
		// BTConnectedThread r;
		// synchronized (this) {
		// if (mState != STATE_CONNECTED) return;
		// r = mConnectedThread;
		// }
		LogUtil.i("INTO sendBTData dataType:"+dataType+"    dataStr:"+dataStr);
		mConnectedThread.sendData((byte) dataType, dataStr);
	}
}
