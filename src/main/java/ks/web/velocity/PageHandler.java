package ks.web.velocity;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import ks.web.InvokeHandler;
import ks.web.ModelAndView;
import ks.web.Response;

public abstract class PageHandler<T extends Response> extends InvokeHandler<T> {

	private static VelocityEngine velocityEngine;

	@Override
	protected void renderPage(HttpServletRequest request, HttpServletResponse response, ModelAndView modelAndView)
			throws IOException {
		VelocityContext context = new VelocityContext(modelAndView.getModel());
		VelocityEngine ve = getVelocityEngine();
		Template template = ve.getTemplate(modelAndView.getName() + ".vm");
		PrintWriter writer = response.getWriter();
		template.merge(context, writer);
		writer.flush();
	}

	public static VelocityEngine getVelocityEngine() {
		if (velocityEngine == null) {
			velocityEngine = new VelocityEngine();
			Properties properties = new Properties();
			properties.put("file.resource.loader.class",
					"org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
			properties.put("input.encoding", "UTF-8");
			properties.put("output.encoding", "UTF-8");
			velocityEngine.init(properties);
		}
		return velocityEngine;
	}

}
