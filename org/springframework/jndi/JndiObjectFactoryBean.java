/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2015 the original author or authors.
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

package org.springframework.jndi;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import javax.naming.Context;
import javax.naming.NamingException;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.SimpleTypeConverter;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.util.ClassUtils;

/**
 * {@link org.springframework.beans.factory.FactoryBean} that looks up a
 * JNDI object. Exposes the object found in JNDI for bean references,
 * e.g. for data access object's "dataSource" property in case of a
 * {@link javax.sql.DataSource}.
 *
 * <p>The typical usage will be to register this as singleton factory
 * (e.g. for a certain JNDI-bound DataSource) in an application context,
 * and give bean references to application services that need it.
 *
 * <p>The default behavior is to look up the JNDI object on startup and cache it.
 * This can be customized through the "lookupOnStartup" and "cache" properties,
 * using a {@link JndiObjectTargetSource} underneath. Note that you need to specify
 * a "proxyInterface" in such a scenario, since the actual JNDI object type is not
 * known in advance.
 *
 * <p>Of course, bean classes in a Spring environment may lookup e.g. a DataSource
 * from JNDI themselves. This class simply enables central configuration of the
 * JNDI name, and easy switching to non-JNDI alternatives. The latter is
 * particularly convenient for test setups, reuse in standalone clients, etc.
 *
 * <p>Note that switching to e.g. DriverManagerDataSource is just a matter of
 * configuration: Simply replace the definition of this FactoryBean with a
 * {@link org.springframework.jdbc.datasource.DriverManagerDataSource} definition!
 *
 * <p>
 * 查找JNDI对象的{@link orgspringframeworkbeansfactoryFactoryBean}在JNDI中找到用于bean引用的对象,例如对于数据访问对象的"dataSource"
 * 属性,如果是{@link javaxsqlDataSource}。
 * 
 *  <p>典型的用法是在应用程序上下文中注册为单例工厂(例如对于某个JNDI绑定的DataSource),并将bean引用到需要它的应用程序服务
 * 
 *  <p>默认行为是在启动时查找JNDI对象并缓存它可以通过"lookupOnStartup"和"缓存"属性进行自定义,使用下面的{@link JndiObjectTargetSource}注意,您需要指
 * 定"proxyInterface "在这种情况下,由于实际的JNDI对象类型不是预先知道的。
 * 
 * <p>当然,Spring环境中的bean类可以从JNDI本身查找一个DataSource。这个类简单地实现了JNDI名称的中央配置,并且很容易地切换到非JNDI选项。
 * 后者对于测试设置是非常方便的,独立客户端等。
 * 
 *  注意,切换到例如DriverManagerDataSource只是一个配置问题：只需将该FactoryBean的定义与{@link orgspringframeworkjdbcdatasourceDriverManagerDataSource}
 * 定义相替换！。
 * 
 * 
 * @author Juergen Hoeller
 * @since 22.05.2003
 * @see #setProxyInterface
 * @see #setLookupOnStartup
 * @see #setCache
 * @see JndiObjectTargetSource
 */
public class JndiObjectFactoryBean extends JndiObjectLocator
		implements FactoryBean<Object>, BeanFactoryAware, BeanClassLoaderAware {

	private Class<?>[] proxyInterfaces;

	private boolean lookupOnStartup = true;

	private boolean cache = true;

	private boolean exposeAccessContext = false;

	private Object defaultObject;

	private ConfigurableBeanFactory beanFactory;

	private ClassLoader beanClassLoader = ClassUtils.getDefaultClassLoader();

	private Object jndiObject;


	/**
	 * Specify the proxy interface to use for the JNDI object.
	 * <p>Typically used in conjunction with "lookupOnStartup"=false and/or "cache"=false.
	 * Needs to be specified because the actual JNDI object type is not known
	 * in advance in case of a lazy lookup.
	 * <p>
	 *  指定用于JNDI对象的代理接口<p>通常与"lookupOnStartup"= false和/或"cache"= false结合使用需要指定,因为实际的JNDI对象类型在事件中未提前知道懒惰查找
	 * 
	 * 
	 * @see #setProxyInterfaces
	 * @see #setLookupOnStartup
	 * @see #setCache
	 */
	public void setProxyInterface(Class<?> proxyInterface) {
		this.proxyInterfaces = new Class<?>[] {proxyInterface};
	}

	/**
	 * Specify multiple proxy interfaces to use for the JNDI object.
	 * <p>Typically used in conjunction with "lookupOnStartup"=false and/or "cache"=false.
	 * Note that proxy interfaces will be autodetected from a specified "expectedType",
	 * if necessary.
	 * <p>
	 * 指定用于JNDI对象的多个代理接口<p>通常与"lookupOnStartup"= false和/或"cache"= false结合使用注意,如果需要,代理接口将从指定的"expectedType"自动
	 * 检测。
	 * 
	 * 
	 * @see #setExpectedType
	 * @see #setLookupOnStartup
	 * @see #setCache
	 */
	public void setProxyInterfaces(Class<?>... proxyInterfaces) {
		this.proxyInterfaces = proxyInterfaces;
	}

	/**
	 * Set whether to look up the JNDI object on startup. Default is "true".
	 * <p>Can be turned off to allow for late availability of the JNDI object.
	 * In this case, the JNDI object will be fetched on first access.
	 * <p>For a lazy lookup, a proxy interface needs to be specified.
	 * <p>
	 *  设置是否在启动时查找JNDI对象默认为"true"<p>可以关闭以允许迟到的JNDI对象的可用性在这种情况下,将在第一次访问时获取JNDI对象<p>对于懒惰查找,需要指定代理接口
	 * 
	 * 
	 * @see #setProxyInterface
	 * @see #setCache
	 */
	public void setLookupOnStartup(boolean lookupOnStartup) {
		this.lookupOnStartup = lookupOnStartup;
	}

	/**
	 * Set whether to cache the JNDI object once it has been located.
	 * Default is "true".
	 * <p>Can be turned off to allow for hot redeployment of JNDI objects.
	 * In this case, the JNDI object will be fetched for each invocation.
	 * <p>For hot redeployment, a proxy interface needs to be specified.
	 * <p>
	 *  设置是否在定位后缓存JNDI对象默认值为"true"<p>可以关闭以允许热重新部署JNDI对象在这种情况下,将为每次调用获取JNDI对象<p>对于热重新部署,需要指定代理接口
	 * 
	 * 
	 * @see #setProxyInterface
	 * @see #setLookupOnStartup
	 */
	public void setCache(boolean cache) {
		this.cache = cache;
	}

	/**
	 * Set whether to expose the JNDI environment context for all access to the target
	 * object, i.e. for all method invocations on the exposed object reference.
	 * <p>Default is "false", i.e. to only expose the JNDI context for object lookup.
	 * Switch this flag to "true" in order to expose the JNDI environment (including
	 * the authorization context) for each method invocation, as needed by WebLogic
	 * for JNDI-obtained factories (e.g. JDBC DataSource, JMS ConnectionFactory)
	 * with authorization requirements.
	 * <p>
	 * 设置是否暴露JNDI环境上下文,以便对目标对象进行所有访问,即针对暴露的对象引用的所有方法调用。
	 * <p>默认值为"false",即仅公开对象查找的JNDI上下文将此标志切换到"为了对每个方法调用的JNDI环境(包括授权上下文),根据WebLogic为JNDI获取的工厂(例如JDBC DataSour
	 * ce,JMS ConnectionFactory)和授权要求。
	 * 设置是否暴露JNDI环境上下文,以便对目标对象进行所有访问,即针对暴露的对象引用的所有方法调用。
	 * 
	 */
	public void setExposeAccessContext(boolean exposeAccessContext) {
		this.exposeAccessContext = exposeAccessContext;
	}

	/**
	 * Specify a default object to fall back to if the JNDI lookup fails.
	 * Default is none.
	 * <p>This can be an arbitrary bean reference or literal value.
	 * It is typically used for literal values in scenarios where the JNDI environment
	 * might define specific config settings but those are not required to be present.
	 * <p>Note: This is only supported for lookup on startup.
	 * If specified together with {@link #setExpectedType}, the specified value
	 * needs to be either of that type or convertible to it.
	 * <p>
	 * 指定一个默认对象返回到如果JNDI查找失败Default is none <p>这可以是任意的bean引用或字面值它通常用于JNDI环境可能定义特定配置设置的场景中的文字值,不需要存在<p>注意：仅在启
	 * 动时查找支持如果与{@link #setExpectedType}一起指定,则指定的值需要是该类型或可转换为它。
	 * 
	 * 
	 * @see #setLookupOnStartup
	 * @see ConfigurableBeanFactory#getTypeConverter()
	 * @see SimpleTypeConverter
	 */
	public void setDefaultObject(Object defaultObject) {
		this.defaultObject = defaultObject;
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) {
		if (beanFactory instanceof ConfigurableBeanFactory) {
			// Just optional - for getting a specifically configured TypeConverter if needed.
			// We'll simply fall back to a SimpleTypeConverter if no specific one available.
			this.beanFactory = (ConfigurableBeanFactory) beanFactory;
		}
	}

	@Override
	public void setBeanClassLoader(ClassLoader classLoader) {
		this.beanClassLoader = classLoader;
	}


	/**
	 * Look up the JNDI object and store it.
	 * <p>
	 *  查找JNDI对象并存储它
	 * 
	 */
	@Override
	public void afterPropertiesSet() throws IllegalArgumentException, NamingException {
		super.afterPropertiesSet();

		if (this.proxyInterfaces != null || !this.lookupOnStartup || !this.cache || this.exposeAccessContext) {
			// We need to create a proxy for this...
			if (this.defaultObject != null) {
				throw new IllegalArgumentException(
						"'defaultObject' is not supported in combination with 'proxyInterface'");
			}
			// We need a proxy and a JndiObjectTargetSource.
			this.jndiObject = JndiObjectProxyFactory.createJndiObjectProxy(this);
		}
		else {
			if (this.defaultObject != null && getExpectedType() != null &&
					!getExpectedType().isInstance(this.defaultObject)) {
				TypeConverter converter = (this.beanFactory != null ?
						this.beanFactory.getTypeConverter() : new SimpleTypeConverter());
				try {
					this.defaultObject = converter.convertIfNecessary(this.defaultObject, getExpectedType());
				}
				catch (TypeMismatchException ex) {
					throw new IllegalArgumentException("Default object [" + this.defaultObject + "] of type [" +
							this.defaultObject.getClass().getName() + "] is not of expected type [" +
							getExpectedType().getName() + "] and cannot be converted either", ex);
				}
			}
			// Locate specified JNDI object.
			this.jndiObject = lookupWithFallback();
		}
	}

	/**
	 * Lookup variant that returns the specified "defaultObject"
	 * (if any) in case of lookup failure.
	 * <p>
	 *  在查找失败的情况下返回指定的"defaultObject"(如果有的话)的Lookup变体
	 * 
	 * 
	 * @return the located object, or the "defaultObject" as fallback
	 * @throws NamingException in case of lookup failure without fallback
	 * @see #setDefaultObject
	 */
	protected Object lookupWithFallback() throws NamingException {
		ClassLoader originalClassLoader = ClassUtils.overrideThreadContextClassLoader(this.beanClassLoader);
		try {
			return lookup();
		}
		catch (TypeMismatchNamingException ex) {
			// Always let TypeMismatchNamingException through -
			// we don't want to fall back to the defaultObject in this case.
			throw ex;
		}
		catch (NamingException ex) {
			if (this.defaultObject != null) {
				if (logger.isDebugEnabled()) {
					logger.debug("JNDI lookup failed - returning specified default object instead", ex);
				}
				else if (logger.isInfoEnabled()) {
					logger.info("JNDI lookup failed - returning specified default object instead: " + ex);
				}
				return this.defaultObject;
			}
			throw ex;
		}
		finally {
			if (originalClassLoader != null) {
				Thread.currentThread().setContextClassLoader(originalClassLoader);
			}
		}
	}


	/**
	 * Return the singleton JNDI object.
	 * <p>
	 *  返回单例JNDI对象
	 * 
	 */
	@Override
	public Object getObject() {
		return this.jndiObject;
	}

	@Override
	public Class<?> getObjectType() {
		if (this.proxyInterfaces != null) {
			if (this.proxyInterfaces.length == 1) {
				return this.proxyInterfaces[0];
			}
			else if (this.proxyInterfaces.length > 1) {
				return createCompositeInterface(this.proxyInterfaces);
			}
		}
		if (this.jndiObject != null) {
			return this.jndiObject.getClass();
		}
		else {
			return getExpectedType();
		}
	}

	@Override
	public boolean isSingleton() {
		return true;
	}


	/**
	 * Create a composite interface Class for the given interfaces,
	 * implementing the given interfaces in one single Class.
	 * <p>The default implementation builds a JDK proxy class for the
	 * given interfaces.
	 * <p>
	 * 为给定的接口创建复合接口类,在一个单独的类中实现给定的接口<p>默认实现为给定接口构建JDK代理类
	 * 
	 * 
	 * @param interfaces the interfaces to merge
	 * @return the merged interface as Class
	 * @see java.lang.reflect.Proxy#getProxyClass
	 */
	protected Class<?> createCompositeInterface(Class<?>[] interfaces) {
		return ClassUtils.createCompositeInterface(interfaces, this.beanClassLoader);
	}


	/**
	 * Inner class to just introduce an AOP dependency when actually creating a proxy.
	 * <p>
	 *  内部类在实际创建代理时仅引入AOP依赖关系
	 * 
	 */
	private static class JndiObjectProxyFactory {

		private static Object createJndiObjectProxy(JndiObjectFactoryBean jof) throws NamingException {
			// Create a JndiObjectTargetSource that mirrors the JndiObjectFactoryBean's configuration.
			JndiObjectTargetSource targetSource = new JndiObjectTargetSource();
			targetSource.setJndiTemplate(jof.getJndiTemplate());
			targetSource.setJndiName(jof.getJndiName());
			targetSource.setExpectedType(jof.getExpectedType());
			targetSource.setResourceRef(jof.isResourceRef());
			targetSource.setLookupOnStartup(jof.lookupOnStartup);
			targetSource.setCache(jof.cache);
			targetSource.afterPropertiesSet();

			// Create a proxy with JndiObjectFactoryBean's proxy interface and the JndiObjectTargetSource.
			ProxyFactory proxyFactory = new ProxyFactory();
			if (jof.proxyInterfaces != null) {
				proxyFactory.setInterfaces(jof.proxyInterfaces);
			}
			else {
				Class<?> targetClass = targetSource.getTargetClass();
				if (targetClass == null) {
					throw new IllegalStateException(
							"Cannot deactivate 'lookupOnStartup' without specifying a 'proxyInterface' or 'expectedType'");
				}
				Class<?>[] ifcs = ClassUtils.getAllInterfacesForClass(targetClass, jof.beanClassLoader);
				for (Class<?> ifc : ifcs) {
					if (Modifier.isPublic(ifc.getModifiers())) {
						proxyFactory.addInterface(ifc);
					}
				}
			}
			if (jof.exposeAccessContext) {
				proxyFactory.addAdvice(new JndiContextExposingInterceptor(jof.getJndiTemplate()));
			}
			proxyFactory.setTargetSource(targetSource);
			return proxyFactory.getProxy(jof.beanClassLoader);
		}
	}


	/**
	 * Interceptor that exposes the JNDI context for all method invocations,
	 * according to JndiObjectFactoryBean's "exposeAccessContext" flag.
	 * <p>
	 *  根据JndiObjectFactoryBean的"exposeAccessContext"标志,为所有方法调用公开了JNDI上下文的拦截器
	 */
	private static class JndiContextExposingInterceptor implements MethodInterceptor {

		private final JndiTemplate jndiTemplate;

		public JndiContextExposingInterceptor(JndiTemplate jndiTemplate) {
			this.jndiTemplate = jndiTemplate;
		}

		@Override
		public Object invoke(MethodInvocation invocation) throws Throwable {
			Context ctx = (isEligible(invocation.getMethod()) ? this.jndiTemplate.getContext() : null);
			try {
				return invocation.proceed();
			}
			finally {
				this.jndiTemplate.releaseContext(ctx);
			}
		}

		protected boolean isEligible(Method method) {
			return (Object.class != method.getDeclaringClass());
		}
	}

}
