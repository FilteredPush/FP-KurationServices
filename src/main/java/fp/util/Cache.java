package fp.util;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: cobalt
 * Date: 02.10.2013
 * Time: 14:13
 * To change this template use File | Settings | File Templates.
 */
public interface Cache {
    public String lookup(List<String> key);
    public void insert(List<String> entry);
}
