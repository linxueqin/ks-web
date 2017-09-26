package ks.web.internal;

import java.io.Reader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import ks.web.JsonProvider;

/**
 * @author xueqin.lin
 *
 */
public class GsonProvider implements JsonProvider {

	private Gson gson = new GsonBuilder().setPrettyPrinting().create();
	
	public <T> T fromJson(Reader reader, Class<T> type) {
		return gson.fromJson(reader, type);
	}

	public String toJson(Object obj) {
		return gson.toJson(obj);
	}

}
