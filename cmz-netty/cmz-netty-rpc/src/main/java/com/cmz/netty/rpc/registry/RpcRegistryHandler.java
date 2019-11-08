package com.cmz.netty.rpc.registry;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.cmz.netty.rpc.protocol.InvokerProtocol;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author chen.mz
 * @email 1034667543@qq.com
 * @create 2019年6月18日 下午8:47:49
 * @description 在 RegistryHandler 中实现注册的具体逻辑
 *              <p>
 *              因为所有模块创建在同一个项目中，为了简化，服务端没有采用远程调用，而是直接扫描本地 Class，然后利用反射调用
 */
public class RpcRegistryHandler extends ChannelInboundHandlerAdapter {

	// 用保存所有可用的服务
	public static ConcurrentHashMap<String, Object> registryMap = new ConcurrentHashMap<>();

	// 保存所有相关的服务类
	private List<String> classNames = new ArrayList<String>();

	public RpcRegistryHandler() {
		// 完成递归扫描
		scannerClass("com.cmz.netty.rpc.provider");
		doRegister();
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		Object result = new Object();
		InvokerProtocol request = (InvokerProtocol) msg;
		//当客户端建立连接时，需要从自定义协议中获取信息，拿到具体的服务和实参
		//使用反射调用
		if(registryMap.containsKey(request.getClassName())) {
			Object clazz = registryMap.get(request.getClassName());
			Method method = clazz.getClass().getMethod(request.getMethodName(), request.getParamTypes());
			result = method.invoke(clazz, request.getParamValues());
		}
		ctx.write(result);
		ctx.flush();
		ctx.close();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}

	/**
	 * 递归扫描给定包下面的文件，将class文件全类名添加到集合中
	 * 
	 * @param packageName
	 */
	private void scannerClass(String packageName) {
		URL url = this.getClass().getClassLoader().getResource(packageName.replaceAll("\\.", "/"));
		File dir = new File(url.getFile());
		for (File file : dir.listFiles()) {
			if (file.isDirectory()) {
				scannerClass(packageName + "." + file.getName());
			} else {
				classNames.add(packageName + "." + file.getName().replace(".class", "").trim());
			}
		}
	}

	/**
	 * 将服务实现类的实例对象注册到容器中
	 */
	private void doRegister() {
		if (classNames.size() == 0) {
			return;
		}
		for (String className : classNames) {
			try {
				Class<?> clazz = Class.forName(className);
				Class<?> clazzInterface = clazz.getInterfaces()[0];
				// 用接口名作为key，用接口实现类实例化对象作为value
				registryMap.put(clazzInterface.getName(), clazz.newInstance());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
