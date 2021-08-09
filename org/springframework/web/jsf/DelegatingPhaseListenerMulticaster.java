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

package org.springframework.web.jsf;

import java.util.Collection;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.web.context.WebApplicationContext;

/**
 * JSF PhaseListener implementation that delegates to one or more Spring-managed
 * PhaseListener beans coming from the Spring root WebApplicationContext.
 *
 * <p>Configure this listener multicaster in your {@code faces-config.xml} file
 * as follows:
 *
 * <pre class="code">
 * &lt;application&gt;
 *   ...
 *   &lt;phase-listener&gt;
 *     org.springframework.web.jsf.DelegatingPhaseListenerMulticaster
 *   &lt;/phase-listener&gt;
 *   ...
 * &lt;/application&gt;</pre>
 *
 * The multicaster will delegate all {@code beforePhase} and {@code afterPhase}
 * events to all target PhaseListener beans. By default, those will simply be obtained
 * by type: All beans in the Spring root WebApplicationContext that implement the
 * PhaseListener interface will be fetched and invoked.
 *
 * <p>Note: This multicaster's {@code getPhaseId()} method will always return
 * {@code ANY_PHASE}. <b>The phase id exposed by the target listener beans
 * will be ignored; all events will be propagated to all listeners.</b>
 *
 * <p>This multicaster may be subclassed to change the strategy used to obtain
 * the listener beans, or to change the strategy used to access the ApplicationContext
 * (normally obtained via {@link FacesContextUtils#getWebApplicationContext(FacesContext)}).
 *
 * <p>
 *  JSF PhaseListener实现,委托给来自Spring根的一个或多个Spring管理的PhaseListener bean WebApplicationContext
 * 
 * <p>在您的{@code faces-configxml}文件中配置此监听器multaster,如下所示：
 * 
 * <pre class="code">
 *  &lt;应用&GT; &LT;相听者GT; orgspringframeworkwebjsfDelegatingPhaseListenerMulticaster&lt; / phase-listene
 * r&gt; &LT; /应用程序&gt; </PRE>。
 * 
 *  多核系统将将所有{@code beforePhase}和{@code afterPhase}事件委托给所有目标PhaseListener bean默认情况下,这些将仅通过类型获取：Spring根Web
 * ApplicationContext中实现PhaseListener接口的所有bean将被获取和调用。
 * 
 *  注意：这个多数据库的{@code getPhaseId()}方法将始终返回{@code ANY_PHASE} <b>目标侦听器bean公开的阶段ID将被忽略;所有事件将传播到所有听众</b>
 * 
 * 
 * @author Juergen Hoeller
 * @author Colin Sampaleanu
 * @since 1.2.7
 */
@SuppressWarnings("serial")
public class DelegatingPhaseListenerMulticaster implements PhaseListener {

	@Override
	public PhaseId getPhaseId() {
		return PhaseId.ANY_PHASE;
	}

	@Override
	public void beforePhase(PhaseEvent event) {
		for (PhaseListener listener : getDelegates(event.getFacesContext())) {
			listener.beforePhase(event);
		}
	}

	@Override
	public void afterPhase(PhaseEvent event) {
		for (PhaseListener listener : getDelegates(event.getFacesContext())) {
			listener.afterPhase(event);
		}
	}


	/**
	 * Obtain the delegate PhaseListener beans from the Spring root WebApplicationContext.
	 * <p>
	 * <p>这个多进程可能被子类化以改变用于获取监听器bean的策略,或者改变用于访问ApplicationContext的策略(通常通过{@link FacesContextUtils#getWebApplicationContext(FacesContext)}
	 * 获取)。
	 * 
	 * 
	 * @param facesContext the current JSF context
	 * @return a Collection of PhaseListener objects
	 * @see #getBeanFactory
	 * @see org.springframework.beans.factory.ListableBeanFactory#getBeansOfType(Class)
	 */
	protected Collection<PhaseListener> getDelegates(FacesContext facesContext) {
		ListableBeanFactory bf = getBeanFactory(facesContext);
		return BeanFactoryUtils.beansOfTypeIncludingAncestors(bf, PhaseListener.class, true, false).values();
	}

	/**
	 * Retrieve the Spring BeanFactory to delegate bean name resolution to.
	 * <p>The default implementation delegates to {@code getWebApplicationContext}.
	 * Can be overridden to provide an arbitrary ListableBeanFactory reference to
	 * resolve against; usually, this will be a full Spring ApplicationContext.
	 * <p>
	 *  从Spring根WebApplicationContext获取代理PhaseListener bean
	 * 
	 * 
	 * @param facesContext the current JSF context
	 * @return the Spring ListableBeanFactory (never {@code null})
	 * @see #getWebApplicationContext
	 */
	protected ListableBeanFactory getBeanFactory(FacesContext facesContext) {
		return getWebApplicationContext(facesContext);
	}

	/**
	 * Retrieve the web application context to delegate bean name resolution to.
	 * <p>The default implementation delegates to FacesContextUtils.
	 * <p>
	 *  检索Spring BeanFactory以将bean名称解析委托给<p>默认实现委托给{@code getWebApplicationContext}可以被覆盖以提供任意的ListableBeanFa
	 * ctory引用来解决;通常,这将是一个完整的Spring ApplicationContext。
	 * 
	 * 
	 * @param facesContext the current JSF context
	 * @return the Spring web application context (never {@code null})
	 * @see FacesContextUtils#getRequiredWebApplicationContext
	 */
	protected WebApplicationContext getWebApplicationContext(FacesContext facesContext) {
		return FacesContextUtils.getRequiredWebApplicationContext(facesContext);
	}

}
