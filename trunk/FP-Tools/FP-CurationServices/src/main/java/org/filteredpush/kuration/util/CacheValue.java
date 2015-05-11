package org.filteredpush.kuration.util;

/**
 * Created by tianhong on 3/4/15.
 */
public class CacheValue {

    CurationStatus status;
    String comment;
    String source;
    public CacheValue setStatus(CurationStatus status){
        this.status = status;
        return this;
    }

    public CacheValue setComment(String comment){
        this.comment = comment;
        return this;
    }

    public CacheValue setSource(String source){
        this.source = source;
        return this;
    }

    public CurationStatus getStatus(){
        return status;
    }

    public String getComment(){
        return comment;
    }

    public String getSource(){
        return source;
    }

    public boolean equals(CacheValue newValue){
        if(comment.equals(newValue.getComment()) && source.equals(newValue.getSource()) && status.equals(newValue.getStatus())) return true;
        else return false;
    }
}
