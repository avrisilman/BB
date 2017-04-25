package com.domikado.itaxi.data;

public class RequestHistory {

    private String url;
    private String response;
    private long timestamp;

    private RequestHistory(String url, long timestamp, String response) {
        this.url = url;
        this.timestamp = timestamp;
        this.response = response;
    }

    public String getUrl() {
        return url;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getResponse() {
        return response;
    }

    public static class Builder {

        private String url;
        private String response;
        private long timestamp;

        public Builder setUrl(String url) {
            this.url = url;
            return this;
        }

        public Builder setTimestamp(long timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder setResponse(String response) {
            this.response = response;
            return this;
        }

        public RequestHistory createRequestHistory() {
            return new RequestHistory(url, timestamp, response);
        }
    }
}
