package org.filteredpush.kuration.util;

/**
 * Created by tianhong on 3/4/15.
 */
public class SciNameCacheValue extends CacheValue {

    String taxon;
    String author;
    boolean hasResult;

    public SciNameCacheValue setHasResult(boolean hasResult){
        this.hasResult = hasResult;
        return this;
    }

    public SciNameCacheValue setTaxon(String taxon){
        this.taxon = taxon;
        return this;
    }

    public SciNameCacheValue setAuthor(String author){
        this.author = author;
        return this;
    }

    public String getAuthor(){
        return author;
    }

    public String getTaxon(){
        return taxon;
    }

    public boolean getHasResult(){
        return hasResult;
    }

    //@Override   todo: not sure if need to override
    public boolean equals (SciNameCacheValue newValue){
        if(super.equals(newValue) && taxon.equals(newValue.getTaxon()) && author.equals((newValue.getAuthor()))) return true;
        else return false;

    }
}
