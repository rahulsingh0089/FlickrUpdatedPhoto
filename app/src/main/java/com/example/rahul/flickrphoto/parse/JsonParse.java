package com.example.rahul.flickrphoto.parse;

import android.util.Log;

import com.example.rahul.flickrphoto.model.PhotoDetail;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rahulsingh on 1/23/2018.
 */

public class JsonParse {

    public static String[] server;
    public static String[] secret;
    public static String[] id;
    public static String[] title;
    public static Integer[] form_id;
    private JSONArray photos = null;
    private String json;

    List<PhotoDetail> photosList ;

    public JsonParse(String json){

        this.json = json;
    }

    public void parseJSON(){
        JSONObject jsonObject=null,jsonObject1;

        try {
            jsonObject=new JSONObject(json);
            jsonObject1=jsonObject.getJSONObject("photos");
            photos = jsonObject1.getJSONArray("photo");


            secret = new String[photos.length()];
            server = new String[photos.length()];
            id = new String[photos.length()];
            form_id=new Integer[photos.length()];
            title=new String[photos.length()];
            photosList = new ArrayList<PhotoDetail>();

            //Parse jsonobject
            for(int i=0;i< photos.length();i++){
                PhotoDetail photoDetail =  new PhotoDetail();

                 jsonObject = photos.getJSONObject(i);

                secret[i] = jsonObject.getString("secret");
                server[i] = jsonObject.getString("server");
                id[i] = jsonObject.getString("id");
                form_id[i] = jsonObject.getInt("farm");
                title[i]=jsonObject.getString("title");

                photoDetail.setSecret(secret[i]);
                photoDetail.setServer_id( server[i]);
                photoDetail.setId( id[i]);
                photoDetail.setForm_id(form_id[i]);
                photoDetail.setTite(title[i]);
                photosList.add(photoDetail);

                Log.d("PhotosArg:",""+server[i]+","+secret[i]+","+id[i]+","+form_id[i]+","+title[i]);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
   public List<PhotoDetail> getPhotos()
    {
        return photosList;
    }


}

