package com.moodle.server;

import com.google.appengine.api.datastore.Blob;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class WaterTalkFiles {
    @PrimaryKey
    String filename;

    Blob data;

    public WaterTalkFiles(String fname,byte[] b){
        filename = fname;
        data = new Blob(b);
    }

    public Blob getData() {
        return data;
    }
}