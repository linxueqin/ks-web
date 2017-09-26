package ks.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author xueqin.lin
 * 处理程序管理器
 */
public interface HandlerManager {

	public String getUrlPrefix();
	
	void setUrlPrefix(String urlPrefix);

	HandlerMapping findHandlerMapping(String url);

	void registHandler(String url, Handler handler, String permission, String description) throws Exception;

	void registHandler(HandlerMapping handlerMapping) throws Exception;

	void registHandler(String moduleName, Object target) throws Exception;

	HandlerMapping[] listMapping();

	Class<?> findRequestType(String url);

	Class<?> findResponseType(String url);

	/**
	 * 设置由
	 * <code>void registHandler(String moduleName, Object target) throws Exception;</code>
	 * 生成的处理器的父类
	 * <br/>
	 * <b>此方法在注册Handler前调用</b>
	 * @param invokeHandlerType
	 */
	@SuppressWarnings("rawtypes")
    void setInvokeHandlerType(Class<? extends InvokeHandler> invokeHandlerType);

	/**
	 * 判断指定类型是否是自包装类型
	 * @param type
	 * @return
	 */
	boolean isWrapType(Class<?> type);
	
	/**
	 * 设置该类为自包装类, 如果该类出现在方法参数类型中, 则该参数将由
	 * <code>ks.web.AbstractHandler#fetch(HttpServletRequest request, HttpServletResponse response, Class<?> type) throws Exception</code>
	 * 处理
	 * <br/>
	 * <b>此方法在注册Handler前调用</b>
	 * @param type
	 */
	void addWrapType(Class<?> type);

	/**
	 * 设置抛出异常时,异常对应的代码及错误信息
	 * <br/>
	 * 如果错误信息未指定, 则信息为异常的getMessage()方法返回的值
	 * @param type
	 * @param code
	 * @param defaultMsg
	 */
	void registExceptionMapping(Class<? extends Throwable> type, int code, String defaultMsg);
	
	/**
	 * 设置JSON序列化器与反序列化器
	 * @param jsonProvider
	 */
	void setJsonProvider(JsonProvider jsonProvider);

	void handle(String baseUrl, HttpServletRequest request, HttpServletResponse response) throws Exception;
	
	/**
	 * 增加过滤器
	 * @param handlerFilter
	 */
	void addHandlerFilter(HandlerFilter handlerFilter);
}
