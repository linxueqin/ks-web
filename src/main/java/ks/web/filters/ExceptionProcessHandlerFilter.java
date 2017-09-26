/**
 * 
 */
package ks.web.filters;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ks.web.CodeMsg;
import ks.web.ExceptionMappings;
import ks.web.HandlerFilter;
import ks.web.HandlerFilterChain;
import ks.web.Json;
import ks.web.PageException;
import ks.web.Response;

/**
 * @author xueqin.lin
 *
 */
public class ExceptionProcessHandlerFilter implements HandlerFilter {

    /*
     * (non-Javadoc)
     * 
     * @see ks.web.HandlerFilter#doFilter(java.lang.String,
     * javax.servlet.http.HttpServletRequest,
     * javax.servlet.http.HttpServletResponse, ks.web.HandlerFilterChain)
     */
    @Override
    public void doFilter(String baseUrl, HttpServletRequest request, HttpServletResponse response,
            HandlerFilterChain chain) throws Exception {
        try {
            chain.doFilter(baseUrl, request, response);
        } catch (PageException e) {
            response.setCharacterEncoding("utf8");
            response.setContentType("text/html");
            PrintWriter writer = response.getWriter();
            e.printStackTrace(writer);
            writer.flush();
        } catch (Exception e) {
            Response failResponse = new Response();
            CodeMsg codeMsg = ExceptionMappings.findCodeMsg(e);
            failResponse.setCode(codeMsg.getCode());
            failResponse.setMsg(e.getMessage() != null ? e.getMessage()  : codeMsg.getMsg());
            response.setCharacterEncoding("utf8");
            response.setContentType("application/json");
            PrintWriter writer = response.getWriter();
            writer.write(Json.toJson(failResponse));
            writer.flush();
        }
    }

}
