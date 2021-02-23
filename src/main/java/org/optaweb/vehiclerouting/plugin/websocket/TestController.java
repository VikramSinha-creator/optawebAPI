package org.optaweb.vehiclerouting.plugin.websocket;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

	@GetMapping (value = "/test/testapi")
	@ResponseBody
	public String testAPI () {
		return "Hello";
	}
	
}
