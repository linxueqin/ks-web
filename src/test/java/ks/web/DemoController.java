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
