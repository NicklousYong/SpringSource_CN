/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.beans.factory.config;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.SimpleTypeConverter;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.FactoryBeanNotInitializedException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;

/**
 * Simple template superclass for {@link FactoryBean} implementations that
 * creates a singleton or a prototype object, depending on a flag.
 *
 * <p>If the "singleton" flag is {@code true} (the default),
 * this class will create the object that it creates exactly once
 * on initialization and subsequently return said singleton instance
 * on all calls to the {@link #getObject()} method.
 *
 * <p>Else, this class will create a new instance every time the
 * {@link #getObject()} method is invoked. Subclasses are responsible
 * for implementing the abstract {@link #createInstance()} template
 * method to actually create the object(s) to expose.
 *
 * <p>
 *  {@link FactoryBean}实现的简单模板超类,创建单例或原型对象,具体取决于标志
 * 
 * <p>如果"singleton"标志是{@code true}(默认值),则此类将在初始化时创建一次完全创建的对象,并随后在所有调用{@link #getObject( )} 方法
 * 
 *  <p>否则,此类将在每次调用{@link #getObject()}方法时创建一个新的实例子类负责实现抽象{@link #createInstance()}模板方法来实际创建对象) 揭露
 * 
 * 
 * @author Juergen Hoeller
 * @author Keith Donald
 * @since 1.0.2
 * @see #setSingleton
 * @see #createInstance()
 */
public abstract class AbstractFactoryBean<T>
		implements FactoryBean<T>, BeanClassLoaderAware, BeanFactoryAware, InitializingBean, DisposableBean {

	/** Logger available to subclasses */
	protected final Log logger = LogFactory.getLog(getClass());

	private boolean singleton = true;

	private ClassLoader beanClassLoader = ClassUtils.getDefaultClassLoader();

	private BeanFactory beanFactory;

	private boolean initialized = false;

	private T singletonInstance;

	private T earlySingletonInstance;


	/**
	 * Set if a singleton should be created, or a new object on each request
	 * otherwise. Default is {@code true} (a singleton).
	 * <p>
	 *  设置是否创建一个单例,或者每个请求上的新对象默认为{@code true}(单例)
	 * 
	 */
	public void setSingleton(boolean singleton) {
		this.singleton = singleton;
	}

	@Override
	public boolean isSingleton() {
		return this.singleton;
	}

	@Override
	public void setBeanClassLoader(ClassLoader classLoader) {
		this.beanClassLoader = classLoader;
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 * Return the BeanFactory that this bean runs in.
	 * <p>
	 *  返回此Bean运行的BeanFactory
	 * 
	 */
	protected BeanFactory getBeanFactory() {
		return this.beanFactory;
	}

	/**
	 * Obtain a bean type converter from the BeanFactory that this bean
	 * runs in. This is typically a fresh instance for each call,
	 * since TypeConverters are usually <i>not</i> thread-safe.
	 * <p>Falls back to a SimpleTypeConverter when not running in a BeanFactory.
	 * <p>
	 * 从BeanFactory中获取一个bean类型转换器,这个bean运行在这里通常是每个调用的一个新的实例,因为TypeConverters通常是<i>不</i>线程安全<p>在不运行时返回到Simple
	 * TypeConverter一个BeanFactory。
	 * 
	 * 
	 * @see ConfigurableBeanFactory#getTypeConverter()
	 * @see org.springframework.beans.SimpleTypeConverter
	 */
	protected TypeConverter getBeanTypeConverter() {
		BeanFactory beanFactory = getBeanFactory();
		if (beanFactory instanceof ConfigurableBeanFactory) {
			return ((ConfigurableBeanFactory) beanFactory).getTypeConverter();
		}
		else {
			return new SimpleTypeConverter();
		}
	}

	/**
	 * Eagerly create the singleton instance, if necessary.
	 * <p>
	 *  如果有必要,可以创建单例实例
	 * 
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		if (isSingleton()) {
			this.initialized = true;
			this.singletonInstance = createInstance();
			this.earlySingletonInstance = null;
		}
	}


	/**
	 * Expose the singleton instance or create a new prototype instance.
	 * <p>
	 *  公开单例实例或创建一个新的原型实例
	 * 
	 * 
	 * @see #createInstance()
	 * @see #getEarlySingletonInterfaces()
	 */
	@Override
	public final T getObject() throws Exception {
		if (isSingleton()) {
			return (this.initialized ? this.singletonInstance : getEarlySingletonInstance());
		}
		else {
			return createInstance();
		}
	}

	/**
	 * Determine an 'eager singleton' instance, exposed in case of a
	 * circular reference. Not called in a non-circular scenario.
	 * <p>
	 *  确定一个"渴望的单身人士"实例,如果是循环引用,则在非循环情况下不被调用
	 * 
	 */
	@SuppressWarnings("unchecked")
	private T getEarlySingletonInstance() throws Exception {
		Class<?>[] ifcs = getEarlySingletonInterfaces();
		if (ifcs == null) {
			throw new FactoryBeanNotInitializedException(
					getClass().getName() + " does not support circular references");
		}
		if (this.earlySingletonInstance == null) {
			this.earlySingletonInstance = (T) Proxy.newProxyInstance(
					this.beanClassLoader, ifcs, new EarlySingletonInvocationHandler());
		}
		return this.earlySingletonInstance;
	}

	/**
	 * Expose the singleton instance (for access through the 'early singleton' proxy).
	 * <p>
	 *  揭露单身人士(通过"早期单身人士"代理人进入)
	 * 
	 * 
	 * @return the singleton instance that this FactoryBean holds
	 * @throws IllegalStateException if the singleton instance is not initialized
	 */
	private T getSingletonInstance() throws IllegalStateException {
		if (!this.initialized) {
			throw new IllegalStateException("Singleton instance not initialized yet");
		}
		return this.singletonInstance;
	}

	/**
	 * Destroy the singleton instance, if any.
	 * <p>
	 *  破坏单身实例,如果有的话
	 * 
	 * 
	 * @see #destroyInstance(Object)
	 */
	@Override
	public void destroy() throws Exception {
		if (isSingleton()) {
			destroyInstance(this.singletonInstance);
		}
	}


	/**
	 * This abstract method declaration mirrors the method in the FactoryBean
	 * interface, for a consistent offering of abstract template methods.
	 * <p>
	 *  该抽象方法声明镜像FactoryBean接口中的方法,以便提供一致的抽象模板方法
	 * 
	 * 
	 * @see org.springframework.beans.factory.FactoryBean#getObjectType()
	 */
	@Override
	public abstract Class<?> getObjectType();

	/**
	 * Template method that subclasses must override to construct
	 * the object returned by this factory.
	 * <p>Invoked on initialization of this FactoryBean in case of
	 * a singleton; else, on each {@link #getObject()} call.
	 * <p>
	 * 子类必须覆盖的模板方法来构造此工厂返回的对象<p>在单例的情况下初始化FactoryBean时调用;在每个{@link #getObject()}调用上
	 * 
	 * 
	 * @return the object returned by this factory
	 * @throws Exception if an exception occurred during object creation
	 * @see #getObject()
	 */
	protected abstract T createInstance() throws Exception;

	/**
	 * Return an array of interfaces that a singleton object exposed by this
	 * FactoryBean is supposed to implement, for use with an 'early singleton
	 * proxy' that will be exposed in case of a circular reference.
	 * <p>The default implementation returns this FactoryBean's object type,
	 * provided that it is an interface, or {@code null} else. The latter
	 * indicates that early singleton access is not supported by this FactoryBean.
	 * This will lead to a FactoryBeanNotInitializedException getting thrown.
	 * <p>
	 *  返回一个由FactoryBean公开的单例对象应该实现的接口数组,用于在循环引用的情况下暴露的"早期单例代理"</p>默认实现返回此FactoryBean的对象类型它是一个接口,或{@code null}
	 *  else后者表示此FactoryBean不支持早期单例访问这将导致一个FactoryBeanNotInitializedException被抛出。
	 * 
	 * 
	 * @return the interfaces to use for 'early singletons',
	 * or {@code null} to indicate a FactoryBeanNotInitializedException
	 * @see org.springframework.beans.factory.FactoryBeanNotInitializedException
	 */
	protected Class<?>[] getEarlySingletonInterfaces() {
		Class<?> type = getObjectType();
		return (type != null && type.isInterface() ? new Class<?>[] {type} : null);
	}

	/**
	 * Callback for destroying a singleton instance. Subclasses may
	 * override this to destroy the previously created instance.
	 * <p>The default implementation is empty.
	 * <p>
	 * 用于销毁单例实例的回调子类可以覆盖此值以销毁先前创建的实例<p>默认实现为空
	 * 
	 * 
	 * @param instance the singleton instance, as returned by
	 * {@link #createInstance()}
	 * @throws Exception in case of shutdown errors
	 * @see #createInstance()
	 */
	protected void destroyInstance(T instance) throws Exception {
	}


	/**
	 * Reflective InvocationHandler for lazy access to the actual singleton object.
	 * <p>
	 *  反思InvocationHandler懒惰访问实际的单身对象
	 */
	private class EarlySingletonInvocationHandler implements InvocationHandler {

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			if (ReflectionUtils.isEqualsMethod(method)) {
				// Only consider equal when proxies are identical.
				return (proxy == args[0]);
			}
			else if (ReflectionUtils.isHashCodeMethod(method)) {
				// Use hashCode of reference proxy.
				return System.identityHashCode(proxy);
			}
			else if (!initialized && ReflectionUtils.isToStringMethod(method)) {
				return "Early singleton proxy for interfaces " +
						ObjectUtils.nullSafeToString(getEarlySingletonInterfaces());
			}
			try {
				return method.invoke(getSingletonInstance(), args);
			}
			catch (InvocationTargetException ex) {
				throw ex.getTargetException();
			}
		}
	}

}
