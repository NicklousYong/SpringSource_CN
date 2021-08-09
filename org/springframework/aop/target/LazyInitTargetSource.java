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

package org.springframework.aop.target;

import org.springframework.beans.BeansException;

/**
 * {@link org.springframework.aop.TargetSource} that lazily accesses a
 * singleton bean from a {@link org.springframework.beans.factory.BeanFactory}.
 *
 * <p>Useful when a proxy reference is needed on initialization but
 * the actual target object should not be initialized until first use.
 * When the target bean is defined in an
 * {@link org.springframework.context.ApplicationContext} (or a
 * {@code BeanFactory} that is eagerly pre-instantiating singleton beans)
 * it must be marked as "lazy-init" too, else it will be instantiated by said
 * {@code ApplicationContext} (or {@code BeanFactory}) on startup.
 * <p>For example:
 *
 * <pre class="code">
 * &lt;bean id="serviceTarget" class="example.MyService" lazy-init="true"&gt;
 *   ...
 * &lt;/bean&gt;
 *
 * &lt;bean id="service" class="org.springframework.aop.framework.ProxyFactoryBean"&gt;
 *   &lt;property name="targetSource"&gt;
 *     &lt;bean class="org.springframework.aop.target.LazyInitTargetSource"&gt;
 *       &lt;property name="targetBeanName"&gt;&lt;idref local="serviceTarget"/&gt;&lt;/property&gt;
 *     &lt;/bean&gt;
 *   &lt;/property&gt;
 * &lt;/bean&gt;</pre>
 *
 * The "serviceTarget" bean will not get initialized until a method on the
 * "service" proxy gets invoked.
 *
 * <p>Subclasses can extend this class and override the {@link #postProcessTargetObject(Object)} to
 * perform some additional processing with the target object when it is first loaded.
 *
 * <p>
 *  {@link orgspringframeworkaopTargetSource},从{@link orgspringframeworkbeansfactoryBeanFactory}中懒惰地访问单例
 * bean。
 * 
 * <p>在初始化时需要代理引用时有用,但实际的目标对象在首次使用前不应被初始化当目标bean在{@link orgspringframeworkcontextApplicationContext}(或{@code BeanFactory}
 * )中定义时,实例化单例bean)也必须标记为"lazy-init",否则它将在启动时由{@code ApplicationContext}(或{@code BeanFactory})实例化。
 * 例如：。
 * 
 * <pre class="code">
 *  &lt; bean id ="serviceTarget"class ="exampleMyService"lazy-init ="true"&gt; &LT; /豆腐&GT;
 * 
 * &lt; bean id ="service"class ="orgspringframeworkaopframeworkProxyFactoryBean"&gt; &lt; property name
 * 
 * @author Juergen Hoeller
 * @author Rob Harrop
 * @since 1.1.4
 * @see org.springframework.beans.factory.BeanFactory#getBean
 * @see #postProcessTargetObject
 */
@SuppressWarnings("serial")
public class LazyInitTargetSource extends AbstractBeanFactoryBasedTargetSource {

	private Object target;


	@Override
	public synchronized Object getTarget() throws BeansException {
		if (this.target == null) {
			this.target = getBeanFactory().getBean(getTargetBeanName());
			postProcessTargetObject(this.target);
		}
		return this.target;
	}

	/**
	 * Subclasses may override this method to perform additional processing on
	 * the target object when it is first loaded.
	 * <p>
	 *  ="targetSource"&gt; &lt; bean class ="orgspringframeworkaoptargetLazyInitTargetSource"&gt; &lt; prop
	 * erty name ="targetBeanName"&gt;&lt; idref local ="serviceTarget"/&gt;&lt; / property&gt; &LT; /豆腐&GT;
	 *  &LT; /性&gt; &LT; /豆腐&GT; </PRE>。
	 * 
	 *  "serviceTarget"bean将无法初始化,直到"service"代理的方法被调用为止
	 * 
	 * 
	 * @param targetObject the target object that has just been instantiated (and configured)
	 */
	protected void postProcessTargetObject(Object targetObject) {
	}

}
