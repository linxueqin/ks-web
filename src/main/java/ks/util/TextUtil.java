/**
 * 
 */
package ks.util;

import java.lang.reflect.Type;

/**
 * @author lxq.bl
 *
 */
public class TextUtil {

	public static String formatURL(String url) {
		url = url.replaceAll("/+", "/");
		while(url.startsWith("/")) {
			url = url.substring(1, url.length());
		}
		
		while(url.endsWith("/")) {
			url = url.substring(0, url.length() - 1);
		}
		return "/" + url;
	}
	
	public static String upperFirst(String text) {
	    return text.substring(0, 1).toUpperCase() + text.substring(1);
	}
	
	public static String typeName(Type type) {
		if (type instanceof Class) {
			return TypeName.createTypeName((Class<?>)type).toString();
		} else {
			return type.toString();
		}
	}
}

class TypeName {
	
	public static TypeName createTypeName(Class<?> type) {
		TypeName typeName = new TypeName();
		typeName.recursion(type);
		return typeName;
	}
	
	private int count;
	
	private String name;
	
	/**
	 * @param type
	 */
	public TypeName() {
		super();
	}

	private void recursion(Class<?> type) {
		if (type.isArray()) {
			type = type.getComponentType();
			count++;
			recursion(type);
		} else {
			name = type.getName();
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(name);
		for(int i = 0; i < count; i++) {
			sb.append("[]");
		}
		return sb.toString();
	}
}
