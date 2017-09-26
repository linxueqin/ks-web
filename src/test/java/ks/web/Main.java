package ks.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;

import ks.web.filters.ExceptionProcessHandlerFilter;
import ks.web.internal.HandlerManagerImpl;

public class Main {

	public static void main(String[] args) throws Exception {
		final HandlerManager handlerManager = new HandlerManagerImpl();
		
		handlerManager.registHandler("demo", new DemoController());
		handlerManager.addHandlerFilter(new ExceptionProcessHandlerFilter());
		handlerManager.registExceptionMapping(SecurityException.class, 300, "权限方面的异常");
		
		Server server = new Server(80);

		server.setHandler(new AbstractHandler() {
						
			@Override
			public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
					throws IOException, ServletException {
				try {
					handlerManager.handle(target, request, response);
				} catch (Exception e) {
					throw new ServletException(e);
				}
			}
		});
		
		server.start();
	}

}
