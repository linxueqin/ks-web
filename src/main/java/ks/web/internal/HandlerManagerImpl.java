package ks.web.internal;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ks.bean.Field;
import ks.util.TextUtil;
import ks.web.ExceptionMappings;
import ks.web.Handler;
import ks.web.HandlerFilter;
import ks.web.HandlerFilterChain;
import ks.web.HandlerManager;
import ks.web.HandlerMapping;
import ks.web.InvokeHandler;
import ks.web.Json;
import ks.web.JsonProvider;
import ks.web.annotation.Path;

/**
 * @author xueqin.lin
 *
 */
public class HandlerManagerImpl implements HandlerManager, HandlerFilterChain {

	private Map<String, HandlerMapping> handlerMappings = new ConcurrentHashMap<String, HandlerMapping>();

	private HandlerClassLoader hcl = new HandlerClassLoader();

	private String urlPrefix = "/api/";
	
	private List<HandlerFilter> handlerFilters = new ArrayList<>();
	
	private ThreadLocal<AtomicInteger> filterIndexThreadLocal = new ThreadLocal<>();
	
	private ThreadLocal<Handler> handlerThreadLocal = new ThreadLocal<>();

	public HandlerManagerImpl() {
		// TODO Auto-generated constructor stub
	}

	public HandlerMapping findHandlerMapping(String url) {
		url = TextUtil.formatURL(url);
		HandlerMapping handlerMapping = handlerMappings.get(url);
		if (handlerMapping != null) {
			return handlerMapping;
		}

		Iterator<HandlerMapping> iterator = handlerMappings.values().iterator();
		int maxLength = 0;
		HandlerMapping patternMapping = null;
		while (iterator.hasNext()) {
			HandlerMapping next = iterator.next();
			if (next.isPattern() && url.indexOf(next.getUrl()) == 0 && maxLength < next.getUrl().length()) {
				maxLength = next.getUrl().length();
				patternMapping = next;
			}
		}
		return patternMapping;
	}

	public Class<?> findRequestType(String url) {
		Handler handler = findHandler(url);
		if (handler != null) {
			try {
				return hcl.loadClass(url.replace("/", ".") + ".Request");
			} catch (ClassNotFoundException e) {
				return null;
			}
		}
		return null;
	}

	public Class<?> findResponseType(String url) {
		Handler handler = findHandler(url);
		if (handler != null) {
			try {
				return hcl.loadClass(url.replace("/", ".") + ".Response");
			} catch (ClassNotFoundException e) {
				return null;
			}
		}
		return null;
	}

	protected Handler findHandler(String url) {
		url = TextUtil.formatURL(url);
		HandlerMapping handlerMapping = findHandlerMapping(url);
		if (handlerMapping != null) {
			return handlerMapping.getHandler();
		}
		return null;
	}

	public synchronized void registHandler(String url, Handler handler, String permission, String description)
			throws Exception {
		url = formatAndCheckNotExists(this.urlPrefix + "/" + url);
		handlerMappings.put(url, new HandlerMapping(url, handler, permission, description));
	}

	public void registHandler(HandlerMapping handlerMapping) throws Exception {
		String url = formatAndCheckNotExists(this.urlPrefix + "/" + handlerMapping.getUrl());
		handlerMapping.setUrl(url);
		handlerMappings.put(url, handlerMapping);
	}

	public synchronized void registHandler(String url, MethodDesc methodDesc, Object target, String permission,
			String description) throws Exception {
		url = formatAndCheckNotExists(this.urlPrefix + "/" + url);

		String pkg = url.replace("/", ".");
		hcl.registProxy(pkg, methodDesc);
		Class<?> handlerType = hcl.loadClass(pkg + ".Handler");
		InvokeHandler<?> handler = (InvokeHandler<?>) handlerType.newInstance();
		handler.setTarget(target);

		handlerMappings.put(url, new HandlerMapping(url, handler, permission, description));
	}

	public synchronized void registHandler(String moduleName, Object target) throws Exception {
		Class<?> targetType = target.getClass();
		Method[] declaredMethods = targetType.getDeclaredMethods();
		for (Method method : declaredMethods) {

			if (!Modifier.isPublic(method.getModifiers())) {
				continue;
			}

			Path pathAnnotation = method.getAnnotation(Path.class);

			if (pathAnnotation == null) {
				continue;
			}

			MethodDesc methodDesc = MethodDescFactory.generate(method);

			String name = pathAnnotation.name();

			String[] parameters = pathAnnotation.parameters();
			Field[] parameterTypes = methodDesc.getParameterTypes();
			if (parameters.length == parameterTypes.length) {
				for (int i = 0; i < parameterTypes.length; i++) {
					parameterTypes[i].setName(parameters[i]);
				}
			}

			if (name == null || "".equals(name)) {
				name = method.getName();
			}

			String url = TextUtil.formatURL(moduleName + "/" + name);
			registHandler(url, methodDesc, target, pathAnnotation.permission(), pathAnnotation.description());
		}
	}

	public String getUrlPrefix() {
		return urlPrefix;
	}

	public void setUrlPrefix(String urlPrefix) {
		this.urlPrefix = urlPrefix;
	}

	public HandlerMapping[] listMapping() {
		return handlerMappings.values().toArray(new HandlerMapping[0]);
	}

	@SuppressWarnings("rawtypes")
    public void setInvokeHandlerType(Class<? extends InvokeHandler> parentHandlerType) {
		HandlerDumper.setInvokeHandlerType(parentHandlerType);
	}

	public void addWrapType(Class<?> type) {
		HandlerDumper.addWrapType(type);
	}

	public boolean isWrapType(Class<?> type) {
		return HandlerDumper.isWrapType(type);
	}

	public void registExceptionMapping(Class<? extends Throwable> type, int code, String defaultMsg) {
		ExceptionMappings.regist(type, code, defaultMsg);
	}

	public void setJsonProvider(JsonProvider jsonProvider) {
		Json.setProvider(jsonProvider);
	}

	public void handle(String baseUrl, HttpServletRequest request, HttpServletResponse response) throws Exception {
		HandlerMapping handlerMapping = findHandlerMapping(request.getRequestURI());

		if (handlerMapping == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		Handler handler = handlerMapping.getHandler();
		if (filterIndexThreadLocal.get() == null) {
		    filterIndexThreadLocal.set(new AtomicInteger(-1));
		} else {
		    filterIndexThreadLocal.get().set(-1);
		}
		handlerThreadLocal.set(handler);
		doFilter(baseUrl, request, response);
	}

	protected String formatAndCheckNotExists(String url) throws Exception {
		url = TextUtil.formatURL(url);
		if (handlerMappings.containsKey(url)) {
			throw new Exception(url + " already exists");
		}
		return url;
	}
	
	@Override
	public void addHandlerFilter(HandlerFilter handlerFilter) {
	    this.handlerFilters.add(handlerFilter);
	}

	@Override
	public void doFilter(String baseUrl, HttpServletRequest request, HttpServletResponse response) throws Exception {
	    int index = filterIndexThreadLocal.get().incrementAndGet();
        if (index < handlerFilters.size()) {
            handlerFilters.get(index).doFilter(baseUrl, request, response, this);
        } else {
            handlerThreadLocal.get().handle(baseUrl, request, response);
        }
	}
	
}
