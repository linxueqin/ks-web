package ks.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author xueqin.lin
 * 抽象处理器<br/>
 * read方法将请求参数进行封装<br/>
 * fetch则根据HandlerDumper配置的自动封装类型，进行调用<br/>
 * 在具体的应用中 继承于AbstractHandler并配置为父类型, 并配置自己的自动包装类型
 */
public abstract class InvokeHandler<T extends Response> extends AbstractHandler {

    public abstract T invoke(HttpServletRequest request, HttpServletResponse response) throws Exception;
    
    @Override
    public void handle(String target, HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            T result = invoke(request, response);
            write(request, response, result);
        } catch (Exception e) {
            // 如果是页面的请求的异常,包装一次再抛出,方便过滤器处理
            if (this instanceof PageHandler) {
                throw new PageException(e);
            } else {
                throw e;
            }
        }
        
    }
    
    public abstract void setTarget(Object target);
}
