package ks.web;

import java.io.PrintWriter;
import java.io.StringReader;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * @author xueqin.lin
 *
 */
public abstract class AbstractHandler implements Handler {

	@SuppressWarnings("unchecked")
	protected <T> T read(HttpServletRequest request, HttpServletResponse response, Class<T> type) throws Exception {
        if (request == null) {
            return type.newInstance();
        }
        String method = request.getMethod();
        Object req = null;
        if ("POST".equalsIgnoreCase(method)) {
            req = Json.fromJson(request.getReader(), type);
        } else if ("GET".equalsIgnoreCase(method)) {
        	String jsonString = request.getParameter("_");
        	if (StringUtils.isNoneBlank(jsonString)) {
        		req = Json.fromJson(new StringReader(jsonString), type);
        	} else {
        		Map<String, String[]> pMap = request.getParameterMap();
                req = type.newInstance();
                BeanUtils.populate(req, pMap);
        	}
            
        } else {
            throw new UnsupportedOperationException("unsupport method " + method);
        }
        
        return (T) req;
    }

    protected Object fetch(HttpServletRequest request, HttpServletResponse response, Class<?> type) throws Exception {
    	if (HttpServletRequest.class.equals(type)) {
			return request;
		} else if (HttpServletResponse.class.equals(type)) {
			return response;
		} else if (HttpSession.class.equals(type)) {
			return request.getSession();
		}
    	
    	return null;
    }
	
	protected void write(HttpServletRequest request, HttpServletResponse response, Object obj) throws Exception {
	    if (obj instanceof Page) {
	        renderPage(request, response, ((Page)obj).getData());
	    } else {
	        response.setCharacterEncoding("utf8");
	        response.setContentType("application/json");
	        PrintWriter writer = response.getWriter();
	        writer.write(Json.toJson(obj));
	        writer.flush();
	    }
	}
	
	protected void renderPage(HttpServletRequest request, HttpServletResponse response, ModelAndView modelAndView) throws Exception {
		response.setCharacterEncoding("utf8");
		response.setContentType("text/html");
		PrintWriter writer = response.getWriter();
		writer.write(modelAndView.getName());
		writer.flush();
	}
}
