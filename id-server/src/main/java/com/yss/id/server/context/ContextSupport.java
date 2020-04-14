package com.yss.id.server.context;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 服务上下文支持类，获取和移除MAP形式的上下文
 * 
 * @author LSP
 *
 */
public class ContextSupport {

	// 登录用户保存的信息，key key，value 参数信息
	private static Map<String, String> parameters = new ConcurrentHashMap<String, String>();

	/**
	 * 保存登录用户设置的参数
	 * 
	 * @param key
	 * @param value
	 */
	public  void setParameter(String key, String value) {

		
		parameters.put(key, value);
	}

	public  Map<String,String> getParameters(){

		return parameters;
	}
	/**
	 * 保存登录用户设置的参数
	 * 
	 * @param key
	 * @return
	 */
	public  String getParameter(String key) {

		return parameters.get(key);
	}

	/**
	 * 移除用户关联的参数
	 */
	public  void removeUserParameters(){
		parameters.clear();
	}

	/**
	 * 移除参数
	 * @param key
	 */
	public  void removeParameter(String key) {
		parameters.remove(key);
	}
}
