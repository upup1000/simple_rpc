package com.heeking.bean;

import java.util.HashMap;
import java.util.Map;

/**
 * bean 注册
 * @author zss
 */
public class BeanRegister {
	Map<String, Object> map = new HashMap<>();

	public void register(String className, Object obj) {
		map.put(className, obj);
	}

	public void cancelRegister(String className) {
		map.remove(className);
	}
	
	public Object getBean(String className) {
		return map.get(className);
	}
}
