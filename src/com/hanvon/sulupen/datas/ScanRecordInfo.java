package com.hanvon.sulupen.datas;

import java.lang.reflect.Field;
import java.util.Map;

import android.os.Parcel;
import android.os.Parcelable;

import com.hanvon.sulupen.utils.LogUtil;

/**
 * @desc 扫描记录实体类
 * @author  PengWenCai
 * @time 2015-6-25 上午11:27:08
 * @version
 */
public class ScanRecordInfo implements Parcelable {
	private String s_id;
	/**
	 * 一条记录的标题
	 */
	private String title;
	/**
	 * 一条记录的内容
	 */
	private String content;
	/**
	 * 创造记录的时间
	 */
	private String createTime;
	/**
	 * 创造记录的地点
	 */
	private String createAddr;
	/**
	 * type分为1,2,3,4分别代表 文本，文件，图片，音频
	 */
	private String type;
	/**
	 * 该记录的主题
	 */
	private String topicId;

	/**
	 * 该记录是否备份
	 */
	// private boolean isBackup;
	private ScanRecordInfo(Parcel parcel) {
		s_id = parcel.readString();
		title = parcel.readString();
		content = parcel.readString();
		createTime = parcel.readString();
		createAddr = parcel.readString();
		type = parcel.readString();
		topicId = parcel.readString();
	}

	public ScanRecordInfo() {
		// TODO Auto-generated constructor stub
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getCreateAddr() {
		return createAddr;
	}

	public void setCreateAddr(String createAddr) {
		this.createAddr = createAddr;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getS_id() {
		return s_id;
	}

	public void setS_id(String s_id) {
		this.s_id = s_id;
	}

	public String getTopicId() {
		return topicId;
	}

	public void setTopicId(String topicId) {
		this.topicId = topicId;
	}

	// public boolean isBackup() {
	// return isBackup;
	// }
	// public void setBackup(boolean isBackup) {
	// this.isBackup = isBackup;
	// }
	/**
	 * 通过map赋值
	 * 
	 * @param map
	 */
	public void setData(Map<String, String> map) {
		Class classBody = this.getClass();
		Field[] fields = classBody.getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			String name = field.getName();
			String value = map.get(name);
			if (value != null) {
				try {
					@SuppressWarnings("rawtypes")
					Class typeClass = field.getType();
					fields[i].setAccessible(true);
					if (typeClass == int.class) {
						field.set(this, Integer.valueOf(value));
					} else if (typeClass == boolean.class) {
						field.set(this, Boolean.valueOf(value));
					} else if (typeClass == long.class) {
						field.set(this, Long.valueOf(value));
					} else if (typeClass == String.class) {
						field.set(this, value);
					}
				} catch (Exception e) {
					LogUtil.e(e.toString());
				}
			}
		}

	}

	public final static Parcelable.Creator<ScanRecordInfo> CREATOR = new Creator<ScanRecordInfo>() {

		@Override
		public ScanRecordInfo createFromParcel(Parcel source) {
			// TODO Auto-generated method stub
			return new ScanRecordInfo(source);
		}

		@Override
		public ScanRecordInfo[] newArray(int size) {
			// TODO Auto-generated method stub
			return new ScanRecordInfo[size];
		}
	};

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(s_id);
		dest.writeString(title);
		dest.writeString(content);
		dest.writeString(createTime);
		dest.writeString(createAddr);
		dest.writeString(type);
		dest.writeString(topicId);
	}

}
