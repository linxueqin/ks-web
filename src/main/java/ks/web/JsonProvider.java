package ks.web;

import java.io.Reader;

/**
 * @author xueqin.lin
 *
 */
public interface JsonProvider {

	<T> T fromJson(Reader reader, Class<T> type);
	
	String toJson(Object obj);
}
