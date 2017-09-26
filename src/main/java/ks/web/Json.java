package ks.web;

import java.io.Reader;

import ks.web.internal.GsonProvider;

/**
 * @author xueqin.lin
 *
 */
public class Json {

	private static JsonProvider PROVIDER = new GsonProvider();
	
	public static <T> T fromJson(Reader reader, Class<T> type) {
		return PROVIDER.fromJson(reader, type);
	}
	
	public static String toJson(Object obj) {
		return PROVIDER.toJson(obj);
	}

	public static void setProvider(JsonProvider provider) {
		Json.PROVIDER = provider;
	}
	
	
}
