package ks.web;

import java.util.HashMap;
import java.util.Map;

public class ModelAndView {

	private String name;
	
	private final Map<String, Object> model = new HashMap<String, Object>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map<String, Object> getModel() {
		return model;
	}
	
	public void put(String key, Object value) {
		this.model.put(key, value);
	}
}
