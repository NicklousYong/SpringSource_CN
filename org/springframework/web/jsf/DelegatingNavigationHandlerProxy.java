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

import javax.faces.application.NavigationHandler;
import javax.faces.context.FacesContext;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.web.context.WebApplicationContext;

/**
 * JSF NavigationHandler implementation that delegates to a NavigationHandler
 * bean obtained from the Spring root WebApplicationContext.
 *
 * <p>Configure this handler proxy in your {@code faces-config.xml} file
 * as follows:
 *
 * <pre class="code">
 * &lt;application&gt;
 *   ...
 *   &lt;navigation-handler&gt;
 * 	   org.springframework.web.jsf.DelegatingNavigationHandlerProxy
 *   &lt;/navigation-handler&gt;
 *   ...
 * &lt;/application&gt;</pre>
 *
 * By default, the Spring ApplicationContext will be searched for the NavigationHandler
 * under the bean name "jsfNavigationHandler". In the simplest case, this is a plain
 * Spring bean definition like the following. However, all of Spring's bean configuration
 * power can be applied to such a bean, in particular all flavors of dependency injection.
 *
 * <pre class="code">
 * &lt;bean name="jsfNavigationHandler" class="mypackage.MyNavigationHandler"&gt;
 *   &lt;property name="myProperty" ref="myOtherBean"/&gt;
 * &lt;/bean&gt;</pre>
 *
 * The target NavigationHandler bean will typically extend the standard JSF
 * NavigationHandler class. However, note that decorating the original
 * NavigationHandler (the JSF provider's default handler) is <i>not</i> supported
 * in such a scenario, since we can't inject the original handler in standard
 * JSF style (that is, as constructor argument).
 *
 * <p>For <b>decorating the original NavigationHandler</b>, make sure that your
 * target bean extends Spring's <b>DecoratingNavigationHandler</b> class. This
 * allows to pass in the original handler as method argument, which this proxy
 * automatically detects. Note that a DecoratingNavigationHandler subclass
 * will still work as standard JSF NavigationHandler as well!
 *
 * <p>This proxy may be subclassed to change the bean name used to search for the
 * navigation handler, change the strategy used to obtain the target handler,
 * or change the strategy used to access the ApplicationContext (normally obtained
 * via {@link FacesContextUtils#getWebApplicationContext(FacesContext)}).
 *
 * <p>
 *  JSF NavigationHandler实现委托给从Spring根WebApplicationContext获取的NavigationHandler bean
 * 
 *  <p>在{@code faces-configxml}文件中配置此处理程序代理,如下所示：
 * 
 * <pre class="code">
 * &lt;应用&GT; &LT;导航处理程序&GT; orgspringframeworkwebjsfDelegatingNavigationHandlerProxy&lt; / navigation-h
 * andler&gt; &LT; /应用程序&gt; </PRE>。
 * 
 *  默认情况下,Spring ApplicationContext将在bean名称"jsfNavigationHandler"下搜索NavigationHandler在最简单的情况下,这是一个简单的Spr
 * ing bean定义,如下所示。
 * 但是,所有的Spring的bean配置能力都可以应用于这样一个bean,尤其是依赖注射的所有味道。
 * 
 * <pre class="code">
 *  &lt; bean name ="jsfNavigationHandler"class ="mypackageMyNavigationHandler"&gt; &lt; property name =
 * "myProperty"ref ="myOtherBean"/&gt; &LT; /豆腐&GT; </PRE>。
 * 
 * 目标NavigationHandler bean通常将扩展标准JSF NavigationHandler类然而,请注意,在这种情况下,装饰原始的NavigationHandler(JSF提供程序的默认处
 * 理程序)是<i>不</i>,因为我们无法注入原始处理程序以标准JSF样式(即,作为构造函数参数)。
 * 
 *  <p>对于装载原始NavigationHandler </b>的<b>,请确保您的目标bean扩展了Spring的<b> DecoratingNavigationHandler </b>类这允许将原始
 * 处理程序作为方法参数传递,该代理自动检测请注意,DecoratingNavigationHandler子类仍然可以作为标准的JSF NavigationHandler工作！。
 * 
 * <p>此代理可能被子类化以更改用于搜索导航处理程序的bean名称,更改用于获取目标处理程序的策略,或更改用于访问ApplicationContext的策略(通常通过{@link FacesContextUtils#getWebApplicationContext (FacesContext的)}
 * )。
 * 
 * 
 * @author Juergen Hoeller
 * @author Colin Sampaleanu
 * @since 1.2.7
 * @see DecoratingNavigationHandler
 */
public class DelegatingNavigationHandlerProxy extends NavigationHandler {

	/**
	 * Default name of the target bean in the Spring application context:
	 * "jsfNavigationHandler"
	 * <p>
	 *  Spring应用程序上下文中目标bean的默认名称："jsfNavigationHandler"
	 * 
	 */
	public final static String DEFAULT_TARGET_BEAN_NAME = "jsfNavigationHandler";

	private NavigationHandler originalNavigationHandler;


	/**
	 * Create a new DelegatingNavigationHandlerProxy.
	 * <p>
	 *  创建一个新的DelegatingNavigationHandlerProxy
	 * 
	 */
	public DelegatingNavigationHandlerProxy() {
	}

	/**
	 * Create a new DelegatingNavigationHandlerProxy.
	 * <p>
	 *  创建一个新的DelegatingNavigationHandlerProxy
	 * 
	 * 
	 * @param originalNavigationHandler the original NavigationHandler
	 */
	public DelegatingNavigationHandlerProxy(NavigationHandler originalNavigationHandler) {
		this.originalNavigationHandler = originalNavigationHandler;
	}


	/**
	 * Handle the navigation request implied by the specified parameters,
	 * through delegating to the target bean in the Spring application context.
	 * <p>The target bean needs to extend the JSF NavigationHandler class.
	 * If it extends Spring's DecoratingNavigationHandler, the overloaded
	 * {@code handleNavigation} method with the original NavigationHandler
	 * as argument will be used. Else, the standard {@code handleNavigation}
	 * method will be called.
	 * <p>
	 * 处理指定参数隐含的导航请求,通过委托Spring应用程序上下文中的目标bean <p>目标bean需要扩展JSF NavigationHandler类如果扩展了Spring的DecoratingNavi
	 * gationHandler,则重载的{@code handleNavigation}方法与原来的NavigationHandler作为参数将被使用Else,标准的{@code handleNavigation}
	 * 方法将被调用。
	 * 
	 */
	@Override
	public void handleNavigation(FacesContext facesContext, String fromAction, String outcome) {
		NavigationHandler handler = getDelegate(facesContext);
		if (handler instanceof DecoratingNavigationHandler) {
			((DecoratingNavigationHandler) handler).handleNavigation(
					facesContext, fromAction, outcome, this.originalNavigationHandler);
		}
		else {
			handler.handleNavigation(facesContext, fromAction, outcome);
		}
	}

	/**
	 * Return the target NavigationHandler to delegate to.
	 * <p>By default, a bean with the name "jsfNavigationHandler" is obtained
	 * from the Spring root WebApplicationContext, for every invocation.
	 * <p>
	 *  返回目标NavigationHandler以委托到<p>默认情况下,从Spring根WebApplicationContext获取名为"jsfNavigationHandler"的bean,用于每次调
	 * 用。
	 * 
	 * 
	 * @param facesContext the current JSF context
	 * @return the target NavigationHandler to delegate to
	 * @see #getTargetBeanName
	 * @see #getBeanFactory
	 */
	protected NavigationHandler getDelegate(FacesContext facesContext) {
		String targetBeanName = getTargetBeanName(facesContext);
		return getBeanFactory(facesContext).getBean(targetBeanName, NavigationHandler.class);
	}

	/**
	 * Return the name of the target NavigationHandler bean in the BeanFactory.
	 * Default is "jsfNavigationHandler".
	 * <p>
	 *  在BeanFactory中返回目标NavigationHandler bean的名称Default is"jsfNavigationHandler"
	 * 
	 * 
	 * @param facesContext the current JSF context
	 * @return the name of the target bean
	 */
	protected String getTargetBeanName(FacesContext facesContext) {
		return DEFAULT_TARGET_BEAN_NAME;
	}

	/**
	 * Retrieve the Spring BeanFactory to delegate bean name resolution to.
	 * <p>Default implementation delegates to {@code getWebApplicationContext}.
	 * Can be overridden to provide an arbitrary BeanFactory reference to resolve
	 * against; usually, this will be a full Spring ApplicationContext.
	 * <p>
	 * 检索Spring BeanFactory以将bean名称解析委托给<p>默认实现委托给{@code getWebApplicationContext}可以覆盖以提供任意BeanFactory引用来解决;
	 * 通常,这将是一个完整的Spring ApplicationContext。
	 * 
	 * 
	 * @param facesContext the current JSF context
	 * @return the Spring BeanFactory (never {@code null})
	 * @see #getWebApplicationContext
	 */
	protected BeanFactory getBeanFactory(FacesContext facesContext) {
		return getWebApplicationContext(facesContext);
	}

	/**
	 * Retrieve the web application context to delegate bean name resolution to.
	 * <p>Default implementation delegates to FacesContextUtils.
	 * <p>
	 * 
	 * @param facesContext the current JSF context
	 * @return the Spring web application context (never {@code null})
	 * @see FacesContextUtils#getRequiredWebApplicationContext
	 */
	protected WebApplicationContext getWebApplicationContext(FacesContext facesContext) {
		return FacesContextUtils.getRequiredWebApplicationContext(facesContext);
	}

}
