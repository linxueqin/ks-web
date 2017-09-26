package ks.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author xueqin.lin
 *	响应类的处理类
 * @param <RespType>
 */
public abstract class RespHandler<RespType> extends AbstractHandler {

	public void handle(String baseUrl, HttpServletRequest request, HttpServletResponse response) throws Exception {
	    RespType result = invoke(baseUrl, request, response);
        TypeResponse<RespType> resp = new TypeResponse<RespType>();
        resp.setData(result);
        write(request, response, resp);
	}

	public abstract RespType invoke(String baseUrl, HttpServletRequest request, HttpServletResponse response)
			throws Exception;

}