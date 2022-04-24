package com.common.component.net;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

/**
 查单词
 <p>
 Created by Tony on 2018/8/8.
 */

public class WordDetail implements Parcelable {

    public String createTime;
    public String explains;
    public int    id;
    public String initial;
    public String phonetic;
    public String status;
    public String ukPhonetic;
    public String ukSpeech;
    public String updateTime;
    public String usPhonetic;
    public String usSpeech;
    public String word;
    public String wordDesc;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.createTime);
        dest.writeString(this.explains);
        dest.writeInt(this.id);
        dest.writeString(this.initial);
        dest.writeString(this.phonetic);
        dest.writeString(this.status);
        dest.writeString(this.ukPhonetic);
        dest.writeString(this.ukSpeech);
        dest.writeString(this.updateTime);
        dest.writeString(this.usPhonetic);
        dest.writeString(this.usSpeech);
        dest.writeString(this.word);
        dest.writeString(this.wordDesc);
    }

    public WordDetail() {
    }

    protected WordDetail(Parcel in) {
        this.createTime = in.readString();
        this.explains = in.readString();
        this.id = in.readInt();
        this.initial = in.readString();
        this.phonetic = in.readString();
        this.status = in.readString();
        this.ukPhonetic = in.readString();
        this.ukSpeech = in.readString();
        this.updateTime = in.readString();
        this.usPhonetic = in.readString();
        this.usSpeech = in.readString();
        this.word = in.readString();
        this.wordDesc = in.readString();
    }

    public static final Creator<WordDetail> CREATOR = new Creator<WordDetail>() {
        @Override
        public WordDetail createFromParcel(Parcel source) {
            return new WordDetail(source);
        }

        @Override
        public WordDetail[] newArray(int size) {
            return new WordDetail[size];
        }
    };

    @NonNull
    @Override
    public String toString() {
        return "WordDetails{" +
                "createTime='" + createTime + '\'' +
                ", explains='" + explains + '\'' +
                ", id=" + id +
                ", initial='" + initial + '\'' +
                ", phonetic='" + phonetic + '\'' +
                ", status='" + status + '\'' +
                ", ukPhonetic='" + ukPhonetic + '\'' +
                ", ukSpeech='" + ukSpeech + '\'' +
                ", updateTime='" + updateTime + '\'' +
                ", usPhonetic='" + usPhonetic + '\'' +
                ", usSpeech='" + usSpeech + '\'' +
                ", word='" + word + '\'' +
                ", wordDesc='" + wordDesc + '\'' +
                '}';
    }
}
