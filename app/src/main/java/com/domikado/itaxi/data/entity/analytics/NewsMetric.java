package com.domikado.itaxi.data.entity.analytics;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "metric_news")
public class NewsMetric extends Model {

    @Column(name = "session_id")
    long sessionId;

    @Column(name = "news_id")
    long newsId;

    @Column(name = "read_at")
    long timestamp;

    public void setSessionId(long sessionId) {
        this.sessionId = sessionId;
    }
    public void setNewsId(long newsId) {
        this.newsId = newsId;
    }
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
