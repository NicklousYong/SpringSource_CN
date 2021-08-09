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

package org.springframework.web.servlet.view.velocity;

import java.io.IOException;
import javax.servlet.ServletContext;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.VelocityException;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.web.context.ServletContextAware;

/**
 * JavaBean to configure Velocity for web usage, via the "configLocation"
 * and/or "velocityProperties" and/or "resourceLoaderPath" bean properties.
 * The simplest way to use this class is to specify just a "resourceLoaderPath";
 * you do not need any further configuration then.
 *
 * <pre class="code">
 * &lt;bean id="velocityConfig" class="org.springframework.web.servlet.view.velocity.VelocityConfigurer"&gt;
 *   &lt;property name="resourceLoaderPath">&lt;value&gt;/WEB-INF/velocity/&lt;/value>&lt;/property&gt;
 * &lt;/bean&gt;</pre>
 *
 * This bean must be included in the application context of any application
 * using Spring's {@link VelocityView} for web MVC. It exists purely to configure
 * Velocity; it is not meant to be referenced by application components (just
 * internally by VelocityView). This class implements {@link VelocityConfig}
 * in order to be found by VelocityView without depending on the bean name of
 * this configurer. Each DispatcherServlet may define its own VelocityConfigurer
 * if desired, potentially with different template loader paths.
 *
 * <p>Note that you can also refer to a pre-configured VelocityEngine
 * instance via the "velocityEngine" property, e.g. set up by
 * {@link org.springframework.ui.velocity.VelocityEngineFactoryBean},
 * This allows to share a VelocityEngine for web and email usage, for example.
 *
 * <p>This configurer registers the "spring.vm" Velocimacro library for web views
 * (contained in this package and thus in {@code spring.jar}), which makes
 * all of Spring's default Velocity macros available to the views.
 * This allows for using the Spring-provided macros such as follows:
 *
 * <pre class="code">
 * #springBind("person.age")
 * age is ${status.value}</pre>
 *
 * <p>
 * JavaBean通过"configLocation"和/或"velocityProperties"和/或"resourceLoaderPath"bean属性来配置Velocity用于Web的使用最简单的
 * 方法是仅指定一个"resourceLoaderPath";你不需要任何进一步的配置。
 * 
 * <pre class="code">
 *  &lt; bean id ="velocityConfig"class ="orgspringframeworkwebservletviewvelocityVelocityConfigurer"&gt
 * ; &lt; property name ="resourceLoaderPath">&lt; value&gt; / WEB-INF / velocity /&lt; / value>&lt; / p
 * roperty&gt; &LT; /豆腐&GT; </PRE>。
 * 
 * 这个bean必须包含在任何应用程序的应用程序上下文中,使用Spring的{@link VelocityView}用于web MVC。
 * 它纯属于配置Velocity;它不是被应用程序组件(VelocityView内部引用)引用。
 * 此类实现{@link VelocityConfig},以便由VelocityView找到,而不依赖于此configurer的bean名称每个DispatcherServlet可以定义自己的Velocit
 * yConfigurer,如果需要,可能使用不同的模板加载程序路径。
 * 它纯属于配置Velocity;它不是被应用程序组件(VelocityView内部引用)引用。
 * 
 *  注意,您还可以通过"velocityEngine"属性来引用预先配置的VelocityEngine实例,例如由{@link orgspringframeworkuivelocityVelocityEngineFactoryBean}
 * 设置,这样可以共享一个VelocityEngine来实现Web和电子邮件的使用,例如。
 * 
 * 
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Darren Davison
 * @see #setConfigLocation
 * @see #setVelocityProperties
 * @see #setResourceLoaderPath
 * @see #setVelocityEngine
 * @see VelocityView
 * @deprecated as of Spring 4.3, in favor of FreeMarker
 */
@Deprecated
public class VelocityConfigurer extends org.springframework.ui.velocity.VelocityEngineFactory
		implements VelocityConfig, InitializingBean, ResourceLoaderAware, ServletContextAware {

	/** the name of the resource loader for Spring's bind macros */
	private static final String SPRING_MACRO_RESOURCE_LOADER_NAME = "springMacro";

	/** the key for the class of Spring's bind macro resource loader */
	private static final String SPRING_MACRO_RESOURCE_LOADER_CLASS = "springMacro.resource.loader.class";

	/** the name of Spring's default bind macro library */
	private static final String SPRING_MACRO_LIBRARY = "org/springframework/web/servlet/view/velocity/spring.vm";


	private VelocityEngine velocityEngine;

	private ServletContext servletContext;


	/**
	 * Set a pre-configured VelocityEngine to use for the Velocity web
	 * configuration: e.g. a shared one for web and email usage, set up via
	 * {@link org.springframework.ui.velocity.VelocityEngineFactoryBean}.
	 * <p>Note that the Spring macros will <i>not</i> be enabled automatically in
	 * case of an external VelocityEngine passed in here. Make sure to include
	 * {@code spring.vm} in your template loader path in such a scenario
	 * (if there is an actual need to use those macros).
	 * <p>If this is not set, VelocityEngineFactory's properties
	 * (inherited by this class) have to be specified.
	 * <p>
	 * <p>此配置程序为Web视图注册"springvm"Velocimacro库(包含在此包中,因此在{@code springjar}中)),这使得所有的Spring的默认Velocity宏都可用于视图。
	 * 这允许使用Spring提供宏如下：。
	 * 
	 * <pre class="code">
	 *  #springBind("personage")age是$ {statusvalue} </pre>
	 * 
	 */
	public void setVelocityEngine(VelocityEngine velocityEngine) {
		this.velocityEngine = velocityEngine;
	}

	@Override
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	/**
	 * Initialize VelocityEngineFactory's VelocityEngine
	 * if not overridden by a pre-configured VelocityEngine.
	 * <p>
	 * 设置一个预先配置的VelocityEngine用于Velocity Web配置：例如通过{@link orgspringframeworkuivelocityVelocityEngineFactoryBean}
	 * 设置的共享的Web和电子邮件使用注意,Spring宏将<i>不</i>在外部VelocityEngine传入的情况下自动启用在这种情况下(如果实际需要使用这些宏),请务必在您的模板加载程序路径中包含{@code springvm}
	 *  <p>如果未设置,必须指定VelocityEngineFactory的属性(由此类继承)。
	 * 
	 * 
	 * @see #createVelocityEngine
	 * @see #setVelocityEngine
	 */
	@Override
	public void afterPropertiesSet() throws IOException, VelocityException {
		if (this.velocityEngine == null) {
			this.velocityEngine = createVelocityEngine();
		}
	}

	/**
	 * Provides a ClasspathResourceLoader in addition to any default or user-defined
	 * loader in order to load the spring Velocity macros from the class path.
	 * <p>
	 *  初始化VelocityEngineFactory的VelocityEngine,如果不被预先配置的VelocityEngine覆盖
	 * 
	 * 
	 * @see org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader
	 */
	@Override
	protected void postProcessVelocityEngine(VelocityEngine velocityEngine) {
		velocityEngine.setApplicationAttribute(ServletContext.class.getName(), this.servletContext);
		velocityEngine.setProperty(
				SPRING_MACRO_RESOURCE_LOADER_CLASS, ClasspathResourceLoader.class.getName());
		velocityEngine.addProperty(
				VelocityEngine.RESOURCE_LOADER, SPRING_MACRO_RESOURCE_LOADER_NAME);
		velocityEngine.addProperty(
				VelocityEngine.VM_LIBRARY, SPRING_MACRO_LIBRARY);

		if (logger.isInfoEnabled()) {
			logger.info("ClasspathResourceLoader with name '" + SPRING_MACRO_RESOURCE_LOADER_NAME +
					"' added to configured VelocityEngine");
		}
	}

	@Override
	public VelocityEngine getVelocityEngine() {
		return this.velocityEngine;
	}

}
