package com.cmz.netty.rpc.protocol;

import java.io.Serializable;

/**
 * @author chen.mz
 * @email 1034667543@qq.com
 * @create 2019年6月18日 下午8:12:06
 * @description 自定义协议
 *              <p>
 *              远程调用 Java 代码哪些内容是必须由网络来传输的呢？
 *              <p>
 *              譬如，服务名称？需要调用该服务的哪个方法？方法的实参是什么？这些信息都需要通过客户端传送到服务端去。
 *              <p>
 *              协议中主要包含的信息有类名、函数名、形参列表和实参列表，通过这些信息就可以定位到一个具体的业务逻辑实现
 */
public class InvokerProtocol implements Serializable {

	private static final long serialVersionUID = -8610647394108523970L;
	
	// 类名
	private String className;
	// 方法名
	private String methodName;
	// 参数类型
	private Class<?>[] paramTypes;
	// 参数列表
	private Object[] paramValues;

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public Class<?>[] getParamTypes() {
		return paramTypes;
	}

	public void setParamTypes(Class<?>[] paramTypes) {
		this.paramTypes = paramTypes;
	}

	public Object[] getParamValues() {
		return paramValues;
	}

	public void setParamValues(Object[] paramValues) {
		this.paramValues = paramValues;
	}

}
