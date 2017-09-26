##web 快速框架 ks-web

- 如果你正在做Java Web 开发, 你可以尝试使用ks-web
- 支持复杂多参数, 异常与编码转换处理,参数绑定 等......
- 如果服务正常返回,返回的格式为`{code:0, data:...}`
- 如果服务出现了异常,返回格式为`{code:1, msg:'异常信息'}`
- 如果调用了异常编码设置`handlerManager.registExceptionMapping(SecurityException.class, 300, "权限方面的异常");`,出现了相应的类型异常将返回`{code:300, msg: '异常信息'}`

提供服务的类
```java
package ks.web;

import ks.web.annotation.Path;

public class DemoController {

	@Path(name = "sayHello")
	public String sayHello() {
		return "hello";
	}
	
	@Path(name = "login")
	public void login(String userName, String password) throws Exception {
		if ("admin".equals(userName)) {
			throw new SecurityException("管理员禁止登录");
		}
	}
}
```
示例代码将使用Jetty做为服务器
```java
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
```
将主程序启动
访问 `http://127.0.0.1/api/demo/sayHello` 
返回 `{"data": "hello", "code": 0}`
访问 `http://127.0.0.1/api/demo/login?userName=guest&password=123`
返回 `{ "code": 0}`
访问 `http://127.0.0.1/api/demo/login?userName=admin`
返回 `{ "code": 300,"msg": "管理员禁止登录"}`

如果不增加 `handlerManager.registExceptionMapping(SecurityException.class, 300, "权限方面的异常");`  
`http://127.0.0.1/api/demo/login?userName=admin` 返回的异常编码是 `{ "code": 1,"msg": "管理员禁止登录"}`
