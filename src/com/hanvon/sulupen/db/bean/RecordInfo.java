package com.hanvon.sulupen.db.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.UUID;

/**
 * Created by fan on 2015/11/27.
 */
public class RecordInfo //implements Parcelable
{
    private int mYear;
    private int mMonth;
    private int mDay;
    private UUID mRecordId;
    private String mNoteBookId;

    public int getYear()
    {
        return mYear;
    }

    public void setYear(int mYear)
    {
        this.mYear = mYear;
    }

    public int getMonth()
    {
        return mMonth;
    }

    public void setMonth(int mMonth)
    {
        this.mMonth = mMonth;
    }

    public int getDay()
    {
        return mDay;
    }

    public void setDay(int mDay)
    {
        this.mDay = mDay;
    }

    public UUID getRecordId()
    {
        return mRecordId;
    }

    public void setRecordId(UUID mRecordId)
    {
        this.mRecordId = mRecordId;
    }

    public String getNoteBookId()
    {
        return mNoteBookId;
    }

    public void setNoteBookId(String  mNoteBookId)
    {
        this.mNoteBookId = mNoteBookId;
    }

    /*
    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(this.mYear);
        dest.writeInt(this.mMonth);
        dest.writeInt(this.mDay);
        dest.writeInt(this.mRecordId);
        dest.writeInt(this.mNoteBookId);
    }

    public RecordInfo()
    {
    }

    protected RecordInfo(Parcel in)
    {
        this.mYear = in.readInt();
        this.mMonth = in.readInt();
        this.mDay = in.readInt();
        this.mRecordId = in.readInt();
        this.mNoteBookId = in.readInt();
    }

    public static final Parcelable.Creator<RecordInfo> CREATOR = new Parcelable.Creator<RecordInfo>()
    {
        public RecordInfo createFromParcel(Parcel source)
        {
            return new RecordInfo(source);
        }

        public RecordInfo[] newArray(int size)
        {
            return new RecordInfo[size];
        }
    };
    */
}
