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

package org.springframework.beans.factory.config;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Properties;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

/**
 * A {@link FactoryBean} implementation that takes an interface which must have one or more
 * methods with the signatures {@code MyType xxx()} or {@code MyType xxx(MyIdType id)}
 * (typically, {@code MyService getService()} or {@code MyService getService(String id)})
 * and creates a dynamic proxy which implements that interface, delegating to an
 * underlying {@link org.springframework.beans.factory.BeanFactory}.
 *
 * <p>Such service locators permit the decoupling of calling code from
 * the {@link org.springframework.beans.factory.BeanFactory} API, by using an
 * appropriate custom locator interface. They will typically be used for
 * <b>prototype beans</b>, i.e. for factory methods that are supposed to
 * return a new instance for each call. The client receives a reference to the
 * service locator via setter or constructor injection, to be able to invoke
 * the locator's factory methods on demand. <b>For singleton beans, direct
 * setter or constructor injection of the target bean is preferable.</b>
 *
 * <p>On invocation of the no-arg factory method, or the single-arg factory
 * method with a String id of {@code null} or empty String, if exactly
 * <b>one</b> bean in the factory matches the return type of the factory
 * method, that bean is returned, otherwise a
 * {@link org.springframework.beans.factory.NoSuchBeanDefinitionException}
 * is thrown.
 *
 * <p>On invocation of the single-arg factory method with a non-null (and
 * non-empty) argument, the proxy returns the result of a
 * {@link org.springframework.beans.factory.BeanFactory#getBean(String)} call,
 * using a stringified version of the passed-in id as bean name.
 *
 * <p>A factory method argument will usually be a String, but can also be an
 * int or a custom enumeration type, for example, stringified via
 * {@code toString}. The resulting String can be used as bean name as-is,
 * provided that corresponding beans are defined in the bean factory.
 * Alternatively, {@linkplain #setServiceMappings(java.util.Properties) a custom
 * mapping} between service IDs and bean names can be defined.
 *
 * <p>By way of an example, consider the following service locator interface.
 * Note that this interface is not dependent on any Spring APIs.
 *
 * <pre class="code">package a.b.c;
 *
 *public interface ServiceFactory {
 *
 *    public MyService getService();
 *}</pre>
 *
 * <p>A sample config in an XML-based
 * {@link org.springframework.beans.factory.BeanFactory} might look as follows:
 *
 * <pre class="code">&lt;beans>
 *
 *   &lt;!-- Prototype bean since we have state -->
 *   &lt;bean id="myService" class="a.b.c.MyService" singleton="false"/>
 *
 *   &lt;!-- will lookup the above 'myService' bean by *TYPE* -->
 *   &lt;bean id="myServiceFactory"
 *            class="org.springframework.beans.factory.config.ServiceLocatorFactoryBean">
 *     &lt;property name="serviceLocatorInterface" value="a.b.c.ServiceFactory"/>
 *   &lt;/bean>
 *
 *   &lt;bean id="clientBean" class="a.b.c.MyClientBean">
 *     &lt;property name="myServiceFactory" ref="myServiceFactory"/>
 *   &lt;/bean>
 *
 *&lt;/beans></pre>
 *
 * <p>The attendant {@code MyClientBean} class implementation might then
 * look something like this:
 *
 * <pre class="code">package a.b.c;
 *
 *public class MyClientBean {
 *
 *    private ServiceFactory myServiceFactory;
 *
 *    // actual implementation provided by the Spring container
 *    public void setServiceFactory(ServiceFactory myServiceFactory) {
 *        this.myServiceFactory = myServiceFactory;
 *    }
 *
 *    public void someBusinessMethod() {
 *        // get a 'fresh', brand new MyService instance
 *        MyService service = this.myServiceFactory.getService();
 *        // use the service object to effect the business logic...
 *    }
 *}</pre>
 *
 * <p>By way of an example that looks up a bean <b>by name</b>, consider
 * the following service locator interface. Again, note that this
 * interface is not dependent on any Spring APIs.
 *
 * <pre class="code">package a.b.c;
 *
 *public interface ServiceFactory {
 *
 *    public MyService getService (String serviceName);
 *}</pre>
 *
 * <p>A sample config in an XML-based
 * {@link org.springframework.beans.factory.BeanFactory} might look as follows:
 *
 * <pre class="code">&lt;beans>
 *
 *   &lt;!-- Prototype beans since we have state (both extend MyService) -->
 *   &lt;bean id="specialService" class="a.b.c.SpecialService" singleton="false"/>
 *   &lt;bean id="anotherService" class="a.b.c.AnotherService" singleton="false"/>
 *
 *   &lt;bean id="myServiceFactory"
 *            class="org.springframework.beans.factory.config.ServiceLocatorFactoryBean">
 *     &lt;property name="serviceLocatorInterface" value="a.b.c.ServiceFactory"/>
 *   &lt;/bean>
 *
 *   &lt;bean id="clientBean" class="a.b.c.MyClientBean">
 *     &lt;property name="myServiceFactory" ref="myServiceFactory"/>
 *   &lt;/bean>
 *
 *&lt;/beans></pre>
 *
 * <p>The attendant {@code MyClientBean} class implementation might then
 * look something like this:
 *
 * <pre class="code">package a.b.c;
 *
 *public class MyClientBean {
 *
 *    private ServiceFactory myServiceFactory;
 *
 *    // actual implementation provided by the Spring container
 *    public void setServiceFactory(ServiceFactory myServiceFactory) {
 *        this.myServiceFactory = myServiceFactory;
 *    }
 *
 *    public void someBusinessMethod() {
 *        // get a 'fresh', brand new MyService instance
 *        MyService service = this.myServiceFactory.getService("specialService");
 *        // use the service object to effect the business logic...
 *    }
 *
 *    public void anotherBusinessMethod() {
 *        // get a 'fresh', brand new MyService instance
 *        MyService service = this.myServiceFactory.getService("anotherService");
 *        // use the service object to effect the business logic...
 *    }
 *}</pre>
 *
 * <p>See {@link ObjectFactoryCreatingFactoryBean} for an alternate approach.
 *
 * <p>
 * 一个{@link FactoryBean}实现,其接口必须具有一个或多个签名{@code MyType xxx()}或{@code MyType xxx(MyIdType id)})的方法(通常为{@code MyService getService() }
 * 或{@code MyService getService(String id)}),并创建一个实现该接口的动态代理,委托给一个底层的{@link orgspringframeworkbeansfactoryBeanFactory}
 * 。
 * 
 * <p>这样的服务定位器允许通过使用适当的自定义定位器界面从{@link orgspringframeworkbeansfactoryBeanFactory} API中去除调用代码他们通常将用于<b>原型
 * bean </b>,即工厂方法应该为每个调用返回一个新的实例客户端通过设置器或构造器注入接收对服务定位器的引用,以便能够根据需要调用定位器的工厂方法<b>对于单例bean,直接设置器或构造器注入目标豆最
 * 好是</b>。
 * 
 * 在调用无参数工厂方法时,或者在String id为{@code null}或空字符串的单参数工厂方法中,如果工厂中正确的<b>一个</b> bean匹配返回类型的工厂方法,返回该bean,否则抛出{@link orgspringframeworkbeansfactoryNoSuchBeanDefinitionException}
 * 。
 * 
 *  <p>在使用非空(和非空)参数调用单参数工厂方法时,代理将返回{@link orgspringframeworkbeansfactoryBeanFactory#getBean(String)}调用的结
 * 果,使用字符串版本的传入id作为bean名称。
 * 
 * 一个工厂方法参数通常是一个String,但也可以是一个int或一个自定义枚举类型,例如通过{@code toString}进行字符串化。
 * 生成的String可以按原样用作bean名,前提是在bean factory中定义了相应的bean。
 * 或者,可以定义服务ID和bean名称之间的{@linkplain #setServiceMappings(javautilProperties)自定义映射}。
 * 
 *  <p>作为一个例子,请考虑以下服务定位器接口注意,此接口不依赖于任何Spring API
 * 
 *  <pre class ="code"> package abc;
 * 
 *  公共接口ServiceFactory {
 * 
 *  public MyService getService();
 * </pre>
 * 
 *  <p>基于XML的{@link orgspringframeworkbeansfactoryBeanFactory}中的示例配置可能如下所示：
 * 
 * <pre class="code">&lt;beans>
 * 
 * &lt;!-- Prototype bean since we have state -->
 * &lt;bean id="myService" class="a.b.c.MyService" singleton="false"/>
 * 
 * &lt;!-- will lookup the above 'myService' bean by *TYPE* -->
 *  &lt; bean id ="myServiceFactory"
 * class="org.springframework.beans.factory.config.ServiceLocatorFactoryBean">
 * &lt;property name="serviceLocatorInterface" value="a.b.c.ServiceFactory"/>
 * &lt;/bean>
 * 
 * &lt;bean id="clientBean" class="a.b.c.MyClientBean">
 * &lt;property name="myServiceFactory" ref="myServiceFactory"/>
 * &lt;/bean>
 * 
 * LT; /豆> </PRE>
 * 
 *  <p>服务员{@code MyClientBean}类的实现可能看起来像这样：
 * 
 *  <pre class ="code"> package abc;
 * 
 *  公共类MyClientBean {
 * 
 *  private ServiceFactory myServiceFactory;
 * 
 *  //由Spring容器提供的实际实现public void setServiceFactory(ServiceFactory myServiceFactory){thismyServiceFactory = myServiceFactory; }
 * 。
 * 
 *  public void someBusinessMethod(){//获取一个'新鲜的',全新的MyService实例MyService service = thismyServiceFactorygetService(); //使用服务对象来实现业务逻辑}
 * 。
 * </pre>
 * 
 *  <p>通过以名称</b>查找bean <b>的示例,请考虑以下服务定位器接口再次注意,此接口不依赖于任何Spring API
 * 
 *  <pre class ="code"> package abc;
 * 
 * 公共接口ServiceFactory {
 * 
 *  public MyService getService(String serviceName);
 * 
 * @author Colin Sampaleanu
 * @author Juergen Hoeller
 * @since 1.1.4
 * @see #setServiceLocatorInterface
 * @see #setServiceMappings
 * @see ObjectFactoryCreatingFactoryBean
 */
public class ServiceLocatorFactoryBean implements FactoryBean<Object>, BeanFactoryAware, InitializingBean {

	private Class<?> serviceLocatorInterface;

	private Constructor<Exception> serviceLocatorExceptionConstructor;

	private Properties serviceMappings;

	private ListableBeanFactory beanFactory;

	private Object proxy;


	/**
	 * Set the service locator interface to use, which must have one or more methods with
	 * the signatures {@code MyType xxx()} or {@code MyType xxx(MyIdType id)}
	 * (typically, {@code MyService getService()} or {@code MyService getService(String id)}).
	 * See the {@link ServiceLocatorFactoryBean class-level Javadoc} for
	 * information on the semantics of such methods.
	 * <p>
	 * </pre>
	 * 
	 *  <p>基于XML的{@link orgspringframeworkbeansfactoryBeanFactory}中的示例配置可能如下所示：
	 * 
	 * <pre class="code">&lt;beans>
	 * 
	 * &lt;!-- Prototype beans since we have state (both extend MyService) -->
	 * &lt;bean id="specialService" class="a.b.c.SpecialService" singleton="false"/>
	 * &lt;bean id="anotherService" class="a.b.c.AnotherService" singleton="false"/>
	 * 
	 *  &lt; bean id ="myServiceFactory"
	 * class="org.springframework.beans.factory.config.ServiceLocatorFactoryBean">
	 * &lt;property name="serviceLocatorInterface" value="a.b.c.ServiceFactory"/>
	 * &lt;/bean>
	 * 
	 * &lt;bean id="clientBean" class="a.b.c.MyClientBean">
	 * &lt;property name="myServiceFactory" ref="myServiceFactory"/>
	 * &lt;/bean>
	 * 
	 *  LT; /豆> </PRE>
	 * 
	 *  <p>服务员{@code MyClientBean}类的实现可能看起来像这样：
	 * 
	 *  <pre class ="code"> package abc;
	 * 
	 *  公共类MyClientBean {
	 * 
	 *  private ServiceFactory myServiceFactory;
	 * 
	 *  //由Spring容器提供的实际实现public void setServiceFactory(ServiceFactory myServiceFactory){thismyServiceFactory = myServiceFactory; }
	 * 。
	 * 
	 * public void someBusinessMethod(){//获取一个'新鲜的',全新的MyService实例MyService service = thismyServiceFactorygetService("specialService"); //使用服务对象来实现业务逻辑}
	 * 。
	 */
	public void setServiceLocatorInterface(Class<?> interfaceType) {
		this.serviceLocatorInterface = interfaceType;
	}

	/**
	 * Set the exception class that the service locator should throw if service
	 * lookup failed. The specified exception class must have a constructor
	 * with one of the following parameter types: {@code (String, Throwable)}
	 * or {@code (Throwable)} or {@code (String)}.
	 * <p>If not specified, subclasses of Spring's BeansException will be thrown,
	 * for example NoSuchBeanDefinitionException. As those are unchecked, the
	 * caller does not need to handle them, so it might be acceptable that
	 * Spring exceptions get thrown as long as they are just handled generically.
	 * <p>
	 * 
	 *  public void anotherBusinessMethod(){//获取一个'新鲜的',全新的MyService实例MyService service = thismyServiceFactorygetService("anotherService"); //使用服务对象来实现业务逻辑}
	 * 。
	 * </pre>
	 * 
	 *  <p>有关替代方法,请参见{@link ObjectFactoryCreatingFactoryBean}
	 * 
	 * 
	 * @see #determineServiceLocatorExceptionConstructor
	 * @see #createServiceLocatorException
	 */
	public void setServiceLocatorExceptionClass(Class<? extends Exception> serviceLocatorExceptionClass) {
		if (serviceLocatorExceptionClass != null && !Exception.class.isAssignableFrom(serviceLocatorExceptionClass)) {
			throw new IllegalArgumentException(
					"serviceLocatorException [" + serviceLocatorExceptionClass.getName() + "] is not a subclass of Exception");
		}
		this.serviceLocatorExceptionConstructor =
				determineServiceLocatorExceptionConstructor(serviceLocatorExceptionClass);
	}

	/**
	 * Set mappings between service ids (passed into the service locator)
	 * and bean names (in the bean factory). Service ids that are not defined
	 * here will be treated as bean names as-is.
	 * <p>The empty string as service id key defines the mapping for {@code null} and
	 * empty string, and for factory methods without parameter. If not defined,
	 * a single matching bean will be retrieved from the bean factory.
	 * <p>
	 * 设置要使用的服务定位器界面,它必须具有一个或多个签名{@code MyType xxx()}或{@code MyType xxx(MyIdType id)})(通常为{@code MyService getService()}
	 * )或{ @code MyService getService(String id)})有关这些方法的语义的信息,请参阅{@link ServiceLocatorFactoryBean类Javadoc}。
	 * 
	 * 
	 * @param serviceMappings mappings between service ids and bean names,
	 * with service ids as keys as bean names as values
	 */
	public void setServiceMappings(Properties serviceMappings) {
		this.serviceMappings = serviceMappings;
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		if (!(beanFactory instanceof ListableBeanFactory)) {
			throw new FatalBeanException(
					"ServiceLocatorFactoryBean needs to run in a BeanFactory that is a ListableBeanFactory");
		}
		this.beanFactory = (ListableBeanFactory) beanFactory;
	}

	@Override
	public void afterPropertiesSet() {
		if (this.serviceLocatorInterface == null) {
			throw new IllegalArgumentException("Property 'serviceLocatorInterface' is required");
		}

		// Create service locator proxy.
		this.proxy = Proxy.newProxyInstance(
				this.serviceLocatorInterface.getClassLoader(),
				new Class<?>[] {this.serviceLocatorInterface},
				new ServiceLocatorInvocationHandler());
	}


	/**
	 * Determine the constructor to use for the given service locator exception
	 * class. Only called in case of a custom service locator exception.
	 * <p>The default implementation looks for a constructor with one of the
	 * following parameter types: {@code (String, Throwable)}
	 * or {@code (Throwable)} or {@code (String)}.
	 * <p>
	 * 如果服务查找失败,请设置服务定位器应抛出的异常类指定的异常类必须具有以下参数类型之一的构造函数：{@code(String,Throwable)}或{@code(Throwable)}或{@ code(String)}
	 *  <p>如果没有指定,将抛出Spring的BeansException的子类,例如NoSuchBeanDefinitionException因为这些未被检查,调用者不需要处理它们,所以可以接受的是,只要
	 * Spring异常被抛出他们只是一般处理。
	 * 
	 * 
	 * @param exceptionClass the exception class
	 * @return the constructor to use
	 * @see #setServiceLocatorExceptionClass
	 */
	@SuppressWarnings("unchecked")
	protected Constructor<Exception> determineServiceLocatorExceptionConstructor(Class<? extends Exception> exceptionClass) {
		try {
			return (Constructor<Exception>) exceptionClass.getConstructor(new Class<?>[] {String.class, Throwable.class});
		}
		catch (NoSuchMethodException ex) {
			try {
				return (Constructor<Exception>) exceptionClass.getConstructor(new Class<?>[] {Throwable.class});
			}
			catch (NoSuchMethodException ex2) {
				try {
					return (Constructor<Exception>) exceptionClass.getConstructor(new Class<?>[] {String.class});
				}
				catch (NoSuchMethodException ex3) {
					throw new IllegalArgumentException(
							"Service locator exception [" + exceptionClass.getName() +
							"] neither has a (String, Throwable) constructor nor a (String) constructor");
				}
			}
		}
	}

	/**
	 * Create a service locator exception for the given cause.
	 * Only called in case of a custom service locator exception.
	 * <p>The default implementation can handle all variations of
	 * message and exception arguments.
	 * <p>
	 * 在服务ID(传递到服务定位器)和bean名称(在bean工厂中)之间设置映射在这里未定义的服务标识将被视为bean名称as-is <p>空字符串作为服务标识键定义映射对于{@code null}和空字符
	 * 串,对于没有参数的工厂方法,如果没有定义,将从bean工厂检索一个匹配的bean。
	 * 
	 * 
	 * @param exceptionConstructor the constructor to use
	 * @param cause the cause of the service lookup failure
	 * @return the service locator exception to throw
	 * @see #setServiceLocatorExceptionClass
	 */
	protected Exception createServiceLocatorException(Constructor<Exception> exceptionConstructor, BeansException cause) {
		Class<?>[] paramTypes = exceptionConstructor.getParameterTypes();
		Object[] args = new Object[paramTypes.length];
		for (int i = 0; i < paramTypes.length; i++) {
			if (String.class == paramTypes[i]) {
				args[i] = cause.getMessage();
			}
			else if (paramTypes[i].isInstance(cause)) {
				args[i] = cause;
			}
		}
		return BeanUtils.instantiateClass(exceptionConstructor, args);
	}


	@Override
	public Object getObject() {
		return this.proxy;
	}

	@Override
	public Class<?> getObjectType() {
		return this.serviceLocatorInterface;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}


	/**
	 * Invocation handler that delegates service locator calls to the bean factory.
	 * <p>
	 *  确定用于给定服务定位器异常类的构造函数仅在自定义服务定位器异常的情况下调用<p>默认实现使用以下参数类型之一来查找构造函数：{@code(String,Throwable)}或{@code(Throwable)}
	 * 或{@code(String)}。
	 * 
	 */
	private class ServiceLocatorInvocationHandler implements InvocationHandler {

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			if (ReflectionUtils.isEqualsMethod(method)) {
				// Only consider equal when proxies are identical.
				return (proxy == args[0]);
			}
			else if (ReflectionUtils.isHashCodeMethod(method)) {
				// Use hashCode of service locator proxy.
				return System.identityHashCode(proxy);
			}
			else if (ReflectionUtils.isToStringMethod(method)) {
				return "Service locator: " + serviceLocatorInterface.getName();
			}
			else {
				return invokeServiceLocatorMethod(method, args);
			}
		}

		private Object invokeServiceLocatorMethod(Method method, Object[] args) throws Exception {
			Class<?> serviceLocatorMethodReturnType = getServiceLocatorMethodReturnType(method);
			try {
				String beanName = tryGetBeanName(args);
				if (StringUtils.hasLength(beanName)) {
					// Service locator for a specific bean name
					return beanFactory.getBean(beanName, serviceLocatorMethodReturnType);
				}
				else {
					// Service locator for a bean type
					return beanFactory.getBean(serviceLocatorMethodReturnType);
				}
			}
			catch (BeansException ex) {
				if (serviceLocatorExceptionConstructor != null) {
					throw createServiceLocatorException(serviceLocatorExceptionConstructor, ex);
				}
				throw ex;
			}
		}

		/**
		 * Check whether a service id was passed in.
		 * <p>
		 * 为给定原因创建服务定位器异常仅在自定义服务定位器异常的情况下调用<p>默认实现可以处理消息和异常参数的所有变体
		 * 
		 */
		private String tryGetBeanName(Object[] args) {
			String beanName = "";
			if (args != null && args.length == 1 && args[0] != null) {
				beanName = args[0].toString();
			}
			// Look for explicit serviceId-to-beanName mappings.
			if (serviceMappings != null) {
				String mappedName = serviceMappings.getProperty(beanName);
				if (mappedName != null) {
					beanName = mappedName;
				}
			}
			return beanName;
		}

		private Class<?> getServiceLocatorMethodReturnType(Method method) throws NoSuchMethodException {
			Class<?>[] paramTypes = method.getParameterTypes();
			Method interfaceMethod = serviceLocatorInterface.getMethod(method.getName(), paramTypes);
			Class<?> serviceLocatorReturnType = interfaceMethod.getReturnType();

			// Check whether the method is a valid service locator.
			if (paramTypes.length > 1 || void.class == serviceLocatorReturnType) {
				throw new UnsupportedOperationException(
						"May only call methods with signature '<type> xxx()' or '<type> xxx(<idtype> id)' " +
						"on factory interface, but tried to call: " + interfaceMethod);
			}
			return serviceLocatorReturnType;
		}
	}

}
