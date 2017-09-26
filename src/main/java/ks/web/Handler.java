package ks.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * @author xueqin.lin
 *
 */
public interface Handler {

    /**
     * 
     * @param baseUrl
     * @param request
     * @param response
     * @throws Exception
     */
    void handle(String baseUrl, HttpServletRequest request, HttpServletResponse response) throws Exception;
}
