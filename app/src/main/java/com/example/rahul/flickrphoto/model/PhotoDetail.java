package com.example.rahul.flickrphoto.model;

/**
 * Created by rahulsingh on 1/23/2018.
 */

public class PhotoDetail {
    private String id;
    private String server_id;
    private Integer form_id;
    private String secret;
    private String tite;

    public String getId() {
        return id;
    }

    public String getTite() {
        return tite;
    }

    public void setTite(String tite) {
        this.tite = tite;
    }

    public void setId(String id) {

        this.id = id;
    }

    public String getServer_id() {
        return server_id;
    }

    public void setServer_id(String server_id) {
        this.server_id = server_id;
    }

    public Integer getForm_id() {
        return form_id;
    }

    public void setForm_id(Integer form_id) {
        this.form_id = form_id;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }
}
