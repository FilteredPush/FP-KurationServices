package fp.util;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: cobalt
 * Date: 02.10.2013
 * Time: 14:17
 * To change this template use File | Settings | File Templates.
 */
public class MemCache implements Cache {

    int nKeys;
    int nValues;

    public MemCache(int keys, int values) {
        nKeys = keys;
        nValues = values;
    }

    @Override
    public String lookup(List<String> key) {
        return null;
    }

    @Override
    public void insert(List<String> entry) {

    }
}
