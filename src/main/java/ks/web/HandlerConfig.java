package ks.web;

import java.util.List;

/**
 * @author xueqin.lin
 *
 */
public class HandlerConfig {

	private List<String> wrappedTypes;
	
	private Class<?> parentHandlerType;

	public List<String> getWrappedTypes() {
		return wrappedTypes;
	}

	public void setWrappedTypes(List<String> wrappedTypes) {
		this.wrappedTypes = wrappedTypes;
	}

	public Class<?> getParentHandlerType() {
		return parentHandlerType;
	}

	public void setParentHandlerType(Class<?> parentHandlerType) {
		this.parentHandlerType = parentHandlerType;
	}
	
}
