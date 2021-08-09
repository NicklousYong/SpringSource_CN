/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2014 the original author or authors.
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

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.FactoryBeanNotInitializedException;

/**
 * {@link FactoryBean} which returns a value which is the result of a static or instance
 * method invocation. For most use cases it is better to just use the container's
 * built-in factory method support for the same purpose, since that is smarter at
 * converting arguments. This factory bean is still useful though when you need to
 * call a method which doesn't return any value (for example, a static class method
 * to force some sort of initialization to happen). This use case is not supported
 * by factory methods, since a return value is needed to obtain the bean instance.
 *
 * <p>Note that as it is expected to be used mostly for accessing factory methods,
 * this factory by default operates in a <b>singleton</b> fashion. The first request
 * to {@link #getObject} by the owning bean factory will cause a method invocation,
 * whose return value will be cached for subsequent requests. An internal
 * {@link #setSingleton singleton} property may be set to "false", to cause this
 * factory to invoke the target method each time it is asked for an object.
 *
 * <p><b>NOTE: If your target method does not produce a result to expose, consider
 * {@link MethodInvokingBean} instead, which avoids the type determination and
 * lifecycle limitations that this {@link MethodInvokingFactoryBean} comes with.</b>
 *
 * <p>This invoker supports any kind of target method. A static method may be specified
 * by setting the {@link #setTargetMethod targetMethod} property to a String representing
 * the static method name, with {@link #setTargetClass targetClass} specifying the Class
 * that the static method is defined on. Alternatively, a target instance method may be
 * specified, by setting the {@link #setTargetObject targetObject} property as the target
 * object, and the {@link #setTargetMethod targetMethod} property as the name of the
 * method to call on that target object. Arguments for the method invocation may be
 * specified by setting the {@link #setArguments arguments} property.
 *
 * <p>This class depends on {@link #afterPropertiesSet()} being called once
 * all properties have been set, as per the InitializingBean contract.
 *
 * <p>An example (in an XML based bean factory definition) of a bean definition
 * which uses this class to call a static factory method:
 *
 * <pre class="code">
 * &lt;bean id="myObject" class="org.springframework.beans.factory.config.MethodInvokingFactoryBean">
 *   &lt;property name="staticMethod" value="com.whatever.MyClassFactory.getInstance"/>
 * &lt;/bean></pre>
 *
 * <p>An example of calling a static method then an instance method to get at a
 * Java system property. Somewhat verbose, but it works.
 *
 * <pre class="code">
 * &lt;bean id="sysProps" class="org.springframework.beans.factory.config.MethodInvokingFactoryBean">
 *   &lt;property name="targetClass" value="java.lang.System"/>
 *   &lt;property name="targetMethod" value="getProperties"/>
 * &lt;/bean>
 *
 * &lt;bean id="javaVersion" class="org.springframework.beans.factory.config.MethodInvokingFactoryBean">
 *   &lt;property name="targetObject" value="sysProps"/>
 *   &lt;property name="targetMethod" value="getProperty"/>
 *   &lt;property name="arguments" value="java.version"/>
 * &lt;/bean></pre>
 *
 * <p>
 * {@link FactoryBean}返回一个值,该值是静态或实例方法调用的结果对于大多数用例,最好只是使用容器的内置工厂方法支持来实现相同的目的,因为这在转换参数时更加明智这个工厂bean仍然有用,但
 * 是当您需要调用不返回任何值的方法(例如,一种静态类方法来强制某种初始化发生)工厂方法不支持此用例,因为需要返回值来获取bean实例。
 * 
 * 请注意,由于预计主要用于访问工厂方法,默认情况下,该工厂将以<b>单例</b>方式运行。
 * 拥有bean工厂的{@link #getObject}的第一个请求将是导致方法调用,其返回值将被缓存用于后续请求内部{@link #setSingleton singleton}属性可能设置为"fals
 * e",以使该工厂在每次询问对象时调用目标方法。
 * 请注意,由于预计主要用于访问工厂方法,默认情况下,该工厂将以<b>单例</b>方式运行。
 * 
 *  注意：如果您的目标方法不会产生公开的结果,请考虑{@link MethodInvokingBean},这样可以避免此{@link MethodInvokingFactoryBean}附带的类型确定和生
 * 命周期限制</b>。
 * 
 * <p>此调用者支持任何类型的目标方法可以通过将{@link #setTargetMethod targetMethod}属性设置为表示静态方法名称的String来指定静态方法,{@link #setTargetClass targetClass}
 * 指定Class定义静态方法或者,可以通过将{@link #setTargetObject targetObject}属性设置为目标对象,将{@link #setTargetMethod targetMethod}
 * 属性设置为要调用的方法的名称来指定目标实例方法该目标对象可以通过设置{@link #setArguments arguments}属性来指定方法调用的参数。
 * 
 * 所有属性都被设置,这个类依赖于{@link #afterPropertiesSet()},根据InitializingBean合约
 * 
 *  <p>使用此类调用静态工厂方法的bean定义的示例(在基于XML的bean工厂定义中)
 * 
 * <pre class="code">
 * &lt;bean id="myObject" class="org.springframework.beans.factory.config.MethodInvokingFactoryBean">
 * &lt;property name="staticMethod" value="com.whatever.MyClassFactory.getInstance"/>
 *  &LT; /豆腐> </PRE>
 * 
 * 
 * @author Colin Sampaleanu
 * @author Juergen Hoeller
 * @since 21.11.2003
 * @see MethodInvokingBean
 * @see org.springframework.util.MethodInvoker
 */
public class MethodInvokingFactoryBean extends MethodInvokingBean implements FactoryBean<Object> {

	private boolean singleton = true;

	private boolean initialized = false;

	/** Method call result in the singleton case */
	private Object singletonObject;


	/**
	 * Set if a singleton should be created, or a new object on each
	 * {@link #getObject()} request otherwise. Default is "true".
	 * <p>
	 *  调用静态方法的一个例子是一个实例方法来获取一个Java系统属性有点冗长,但它有效
	 * 
	 * <pre class="code">
	 * &lt;bean id="sysProps" class="org.springframework.beans.factory.config.MethodInvokingFactoryBean">
	 * &lt;property name="targetClass" value="java.lang.System"/>
	 * &lt;property name="targetMethod" value="getProperties"/>
	 * &lt;/bean>
	 * 
	 * &lt;bean id="javaVersion" class="org.springframework.beans.factory.config.MethodInvokingFactoryBean">
	 * &lt;property name="targetObject" value="sysProps"/>
	 * &lt;property name="targetMethod" value="getProperty"/>
	 * &lt;property name="arguments" value="java.version"/>
	 */
	public void setSingleton(boolean singleton) {
		this.singleton = singleton;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		prepare();
		if (this.singleton) {
			this.initialized = true;
			this.singletonObject = invokeWithTargetException();
		}
	}


	/**
	 * Returns the same value each time if the singleton property is set
	 * to "true", otherwise returns the value returned from invoking the
	 * specified method on the fly.
	 * <p>
	 *  &LT; /豆腐> </PRE>
	 * 
	 */
	@Override
	public Object getObject() throws Exception {
		if (this.singleton) {
			if (!this.initialized) {
				throw new FactoryBeanNotInitializedException();
			}
			// Singleton: return shared object.
			return this.singletonObject;
		}
		else {
			// Prototype: new object on each call.
			return invokeWithTargetException();
		}
	}

	/**
	 * Return the type of object that this FactoryBean creates,
	 * or {@code null} if not known in advance.
	 * <p>
	 *  设置是否创建单例,或者每个{@link #getObject()}请求上的新对象默认为"true"
	 * 
	 */
	@Override
	public Class<?> getObjectType() {
		if (!isPrepared()) {
			// Not fully initialized yet -> return null to indicate "not known yet".
			return null;
		}
		return getPreparedMethod().getReturnType();
	}

	@Override
	public boolean isSingleton() {
		return this.singleton;
	}

}
