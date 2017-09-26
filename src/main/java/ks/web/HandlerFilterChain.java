package ks.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author xueqin.lin
 *
 */
public interface HandlerFilterChain {

    void doFilter(String baseUrl, HttpServletRequest request, HttpServletResponse response) throws Exception;
    
}
