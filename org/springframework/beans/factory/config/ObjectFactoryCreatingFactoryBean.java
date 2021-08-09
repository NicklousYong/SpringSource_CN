/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2012 the original author or authors.
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

import java.io.Serializable;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.util.Assert;

/**
 * A {@link org.springframework.beans.factory.FactoryBean} implementation that
 * returns a value which is an {@link org.springframework.beans.factory.ObjectFactory}
 * that in turn returns a bean sourced from a {@link org.springframework.beans.factory.BeanFactory}.
 *
 * <p>As such, this may be used to avoid having a client object directly calling
 * {@link org.springframework.beans.factory.BeanFactory#getBean(String)} to get
 * a (typically prototype) bean from a
 * {@link org.springframework.beans.factory.BeanFactory}, which would be a
 * violation of the inversion of control principle. Instead, with the use
 * of this class, the client object can be fed an
 * {@link org.springframework.beans.factory.ObjectFactory} instance as a
 * property which directly returns only the one target bean (again, which is
 * typically a prototype bean).
 *
 * <p>A sample config in an XML-based
 * {@link org.springframework.beans.factory.BeanFactory} might look as follows:
 *
 * <pre class="code">&lt;beans&gt;
 *
 *   &lt;!-- Prototype bean since we have state --&gt;
 *   &lt;bean id="myService" class="a.b.c.MyService" scope="prototype"/&gt;
 *
 *   &lt;bean id="myServiceFactory"
 *       class="org.springframework.beans.factory.config.ObjectFactoryCreatingFactoryBean"&gt;
 *     &lt;property name="targetBeanName"&gt;&lt;idref local="myService"/&gt;&lt;/property&gt;
 *   &lt;/bean&gt;
 *
 *   &lt;bean id="clientBean" class="a.b.c.MyClientBean"&gt;
 *     &lt;property name="myServiceFactory" ref="myServiceFactory"/&gt;
 *   &lt;/bean&gt;
 *
 *&lt;/beans&gt;</pre>
 *
 * <p>The attendant {@code MyClientBean} class implementation might look
 * something like this:
 *
 * <pre class="code">package a.b.c;
 *
 * import org.springframework.beans.factory.ObjectFactory;
 *
 * public class MyClientBean {
 *
 *   private ObjectFactory&lt;MyService&gt; myServiceFactory;
 *
 *   public void setMyServiceFactory(ObjectFactory&lt;MyService&gt; myServiceFactory) {
 *     this.myServiceFactory = myServiceFactory;
 *   }
 *
 *   public void someBusinessMethod() {
 *     // get a 'fresh', brand new MyService instance
 *     MyService service = this.myServiceFactory.getObject();
 *     // use the service object to effect the business logic...
 *   }
 * }</pre>
 *
 * <p>An alternate approach to this application of an object creational pattern
 * would be to use the {@link ServiceLocatorFactoryBean}
 * to source (prototype) beans. The {@link ServiceLocatorFactoryBean} approach
 * has the advantage of the fact that one doesn't have to depend on any
 * Spring-specific interface such as {@link org.springframework.beans.factory.ObjectFactory},
 * but has the disadvantage of requiring runtime class generation. Please do
 * consult the {@link ServiceLocatorFactoryBean ServiceLocatorFactoryBean JavaDoc}
 * for a fuller discussion of this issue.
 *
 * <p>
 * 一个{@link orgspringframeworkbeansfactoryFactoryBean}实现,返回一个值,它是一个{@link orgspringframeworkbeansfactoryObjectFactory}
 * ,它反过来返回一个源自{@link orgspringframeworkbeansfactoryBeanFactory}。
 * 
 * 因此,这可以用于避免使客户端对象直接调用{@link orgspringframeworkbeansfactoryBeanFactory#getBean(String)}从{@link orgspringframeworkbeansfactoryBeanFactory}
 * 获取(通常是原型)bean,这将是违反控制原理的反转相反,通过使用此类,客户端对象可以作为一个属性提供一个{@link orgspringframeworkbeansfactoryObjectFactory}
 * 实例,该属性直接返回一个目标bean(再次,通常是一个原型bean)。
 * 
 *  <p>基于XML的{@link orgspringframeworkbeansfactoryBeanFactory}中的示例配置可能如下所示：
 * 
 *  <pre class ="code">&lt; beans&gt;
 * 
 * &lt;！ - 原型bean,因为我们有状态 - &gt; &lt; bean id ="myService"class ="abcMyService"scope ="prototype"/&gt;
 * 
 *  &lt; bean id ="myServiceFactory"class ="orgspringframeworkbeansfactoryconfigObjectFactoryCreatingFac
 * toryBean"&gt; &lt; property name ="targetBeanName"&gt;&lt; idref local ="myService"/&gt;&lt; / proper
 * ty&gt; &LT; /豆腐&GT;。
 * 
 *  &lt; bean ID ="clientBean"class ="abcMyClientBean"&gt; &lt; property name ="myServiceFactory"ref ="m
 * yServiceFactory"/&gt; &LT; /豆腐&GT;。
 * 
 *  LT; /豆类&GT; </PRE>
 * 
 *  <p>服务员{@code MyClientBean}类的实现可能如下所示：
 * 
 * 
 * @author Colin Sampaleanu
 * @author Juergen Hoeller
 * @since 1.0.2
 * @see org.springframework.beans.factory.ObjectFactory
 * @see ServiceLocatorFactoryBean
 */
public class ObjectFactoryCreatingFactoryBean extends AbstractFactoryBean<ObjectFactory<Object>> {

	private String targetBeanName;


	/**
	 * Set the name of the target bean.
	 * <p>The target does not <i>have</i> to be a non-singleton bean, but realistically
	 * always will be (because if the target bean were a singleton, then said singleton
	 * bean could simply be injected straight into the dependent object, thus obviating
	 * the need for the extra level of indirection afforded by this factory approach).
	 * <p>
	 *  <pre class ="code"> package abc;
	 * 
	 *  import orgspringframeworkbeansfactoryObjectFactory;
	 * 
	 *  public class MyClientBean {
	 * 
	 *  private ObjectFactory&lt; MyService&gt; myServiceFactory;
	 * 
	 * public void setMyServiceFactory(ObjectFactory&MyService&gt; myServiceFactory){thismyServiceFactory = myServiceFactory; }
	 * 。
	 * 
	 *  public void someBusinessMethod(){//获取一个'新鲜的',全新的MyService实例MyService service = thismyServiceFactorygetObject(); //使用服务对象来实现业务逻辑}
	 */
	public void setTargetBeanName(String targetBeanName) {
		this.targetBeanName = targetBeanName;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.hasText(this.targetBeanName, "Property 'targetBeanName' is required");
		super.afterPropertiesSet();
	}


	@Override
	public Class<?> getObjectType() {
		return ObjectFactory.class;
	}

	@Override
	protected ObjectFactory<Object> createInstance() {
		return new TargetBeanObjectFactory(getBeanFactory(), this.targetBeanName);
	}


	/**
	 * Independent inner class - for serialization purposes.
	 * <p>
	 * } </pre>。
	 * 
	 * <p>对象创建模式的这种应用程序的另一种方法是使用{@link ServiceLocatorFactoryBean}来源(原型)bean {@link ServiceLocatorFactoryBean}
	 * 方法的优点在于,不需要依赖于任何Spring特定的界面,例如{@link orgspringframeworkbeansfactoryObjectFactory},但缺点是需要运行时类生成请参阅{@link ServiceLocatorFactoryBean ServiceLocatorFactoryBean JavaDoc}
	 * ,以便对此问题进行更全面的讨论。
	 * 
	 */
	@SuppressWarnings("serial")
	private static class TargetBeanObjectFactory implements ObjectFactory<Object>, Serializable {

		private final BeanFactory beanFactory;

		private final String targetBeanName;

		public TargetBeanObjectFactory(BeanFactory beanFactory, String targetBeanName) {
			this.beanFactory = beanFactory;
			this.targetBeanName = targetBeanName;
		}

		@Override
		public Object getObject() throws BeansException {
			return this.beanFactory.getBean(this.targetBeanName);
		}
	}

}
