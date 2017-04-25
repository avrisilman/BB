package com.domikado.itaxi.data.entity.ads;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.domikado.itaxi.data.api.json.CallToActionJson;

@Table(name = "cta")
public class CallToAction extends Model {

    @Column(name = "server_id")
    private long serverId;

    @Column(name = "url")
    private String url;

    @Column(name = "file")
    private String file;

    @Column(name = "button_image")
    private String image;

    @Column(name = "button_image_file")
    private String imageFile;

    @Column(name = "media_fingerprint")
    private String mediaFingerprint;

    public CallToAction() {
        super();
    }

    public CallToAction(CallToActionJson callToAction) {
        this.serverId = callToAction.getId();
        this.url = callToAction.getUrl();
        this.image = callToAction.getButtonImage();
        this.mediaFingerprint = callToAction.getFingerprint();
    }

    public String getImageFile() {
        return imageFile;
    }
    public String getImage() {
        return image;
    }
    public String getFile() {
        return file;
    }
    public long getServerId() {
        return serverId;
    }
    public String getUrl() {
        return url;
    }

    public void setImageFile(String imageFile) {
        this.imageFile = imageFile;
    }
    public void setImage(String image) {
        this.image = image;
    }
    public void setFile(String file) {
        this.file = file;
    }
    public void setServerId(long id) {
        this.serverId = id;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public void setMediaFingerprint(String mediaFingerprint) {
        this.mediaFingerprint = mediaFingerprint;
    }
}
