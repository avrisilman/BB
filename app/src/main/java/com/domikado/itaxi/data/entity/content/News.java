package com.domikado.itaxi.data.entity.content;

import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.domikado.itaxi.data.api.json.NewsJson;

@Table(name = "news")
public class News extends Model implements Parcelable {

    @Column(name = "server_id")
    private long serverId;

    @Column(name = "server_media_url")
    private String imageUrl;

    @Column(name = "title")
    private String title;

    @Column(name = "summary")
    private String summary;

    @Column(name = "content")
    private String content;

    @Column(name = "date")
    private String publishedDate;

    @Column(name = "source")
    private String source;

    @Column(name = "fingerprint")
    private String fingerprint;

    @Column(name = "filepath")
    private String filepath;

    @Column(name = "version")
    private String version;

    public long getServerId() {
        return serverId;
    }
    public String getFilepath() {
        return filepath;
    }
    public String getTitle() {
        return title;
    }
    public String getContent() {
        return content;
    }
    public String getPublishedDate() {
        return publishedDate;
    }
    public String getSource() {
        return source;
    }
    public String getVersion() {
        return version;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }
    public void setVersion(String version) {
        this.version = version;
    }

    public News() {
        super();
    }
    public News(NewsJson news) {
        super();
        this.serverId = news.getServerId();
        this.title = news.getTitle();
        this.summary = news.getSummary();
        this.content = news.getContent();
        this.publishedDate = news.getPublishedDate();
        this.source = news.getSource();
        this.fingerprint = news.getFingerprint();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.serverId);
        dest.writeString(this.title);
        dest.writeString(this.summary);
        dest.writeString(this.content);
        dest.writeString(this.imageUrl);
        dest.writeString(this.publishedDate);
        dest.writeString(this.source);
        dest.writeString(this.filepath);
        dest.writeString(this.version);
    }

    protected News(Parcel in) {
        this.serverId = in.readInt();
        this.title = in.readString();
        this.summary = in.readString();
        this.content = in.readString();
        this.imageUrl = in.readString();
        this.publishedDate = in.readString();
        this.source = in.readString();
        this.filepath = in.readString();
        this.version = in.readString();
    }

    public static final Creator<News> CREATOR = new Creator<News>() {
        public News createFromParcel(Parcel source) {
            return new News(source);
        }

        public News[] newArray(int size) {
            return new News[size];
        }
    };
}