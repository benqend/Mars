package com.mars.aop.proxy;

import java.lang.reflect.Method;
import java.util.Map;

import com.mars.core.constant.MarsConstant;
import com.mars.core.model.AopModel;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

/**
 * 代理类
 * @author yuye
 *
 */
public class CglibProxy implements MethodInterceptor {

	private Enhancer enhancer;
	
	private Map<String,AopModel> list;

	/**
	 * 获取代理对象
	 * @param clazz  bean的class
	 * @param list aop类的class
	 * @return 对象
	 */
	public Object getProxy(Class<?> clazz,Map<String, AopModel> list) {
		
		this.list = list;
		enhancer = new Enhancer();
		// 设置需要创建子类的类
		enhancer.setSuperclass(clazz);
		enhancer.setCallback(this);
		// 通过字节码技术动态创建子类实例
		return enhancer.create();
	}
	
	
	/**
	 * 绑定代理
	 */
	@Override
	public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
		Object obj = null;
		AopModel aopModel = list.get(method.getName());
		Class c = aopModel.getCls();
		if(c != null){
			obj = c.getDeclaredConstructor().newInstance();
			if(c.getName().equals(MarsConstant.TRACTION_CLASS)){
				Method m2 = c.getDeclaredMethod("startMethod",new Class[] {Object[].class, AopModel.class});
				m2.invoke(obj,new Object[] {args, aopModel});
			} else {
				Method m2 = c.getDeclaredMethod("startMethod",new Class[] {Object[].class});
				m2.invoke(obj,new Object[] {args});
			}
		}
		
		Object o1 = null;
		try {
			o1 = methodProxy.invokeSuper(o, args);
			
			if(c != null){
				Method m3 = c.getDeclaredMethod("endMethod",new Class[] {Object[].class}); 
				m3.invoke(obj,new Object[] {args});
			}

			return o1;
		} catch (Throwable e) {
			if(c != null){
				Method m4 = c.getDeclaredMethod("exp",new Class[] {Throwable.class});
				m4.invoke(obj,new Object[] {e});
			}

			throw e;
		}
	}
}
