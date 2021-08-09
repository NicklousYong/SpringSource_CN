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

package org.springframework.web;

import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.Set;
import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.HandlesTypes;

import org.springframework.core.annotation.AnnotationAwareOrderComparator;

/**
 * Servlet 3.0 {@link ServletContainerInitializer} designed to support code-based
 * configuration of the servlet container using Spring's {@link WebApplicationInitializer}
 * SPI as opposed to (or possibly in combination with) the traditional
 * {@code web.xml}-based approach.
 *
 * <h2>Mechanism of Operation</h2>
 * This class will be loaded and instantiated and have its {@link #onStartup}
 * method invoked by any Servlet 3.0-compliant container during container startup assuming
 * that the {@code spring-web} module JAR is present on the classpath. This occurs through
 * the JAR Services API {@link ServiceLoader#load(Class)} method detecting the
 * {@code spring-web} module's {@code META-INF/services/javax.servlet.ServletContainerInitializer}
 * service provider configuration file. See the
 * <a href="http://download.oracle.com/javase/6/docs/technotes/guides/jar/jar.html#Service%20Provider">
 * JAR Services API documentation</a> as well as section <em>8.2.4</em> of the Servlet 3.0
 * Final Draft specification for complete details.
 *
 * <h3>In combination with {@code web.xml}</h3>
 * A web application can choose to limit the amount of classpath scanning the Servlet
 * container does at startup either through the {@code metadata-complete} attribute in
 * {@code web.xml}, which controls scanning for Servlet annotations or through an
 * {@code <absolute-ordering>} element also in {@code web.xml}, which controls which
 * web fragments (i.e. jars) are allowed to perform a {@code ServletContainerInitializer}
 * scan. When using this feature, the {@link SpringServletContainerInitializer}
 * can be enabled by adding "spring_web" to the list of named web fragments in
 * {@code web.xml} as follows:
 *
 * <pre class="code">
 * {@code
 * <absolute-ordering>
 *   <name>some_web_fragment</name>
 *   <name>spring_web</name>
 * </absolute-ordering>
 * }</pre>
 *
 * <h2>Relationship to Spring's {@code WebApplicationInitializer}</h2>
 * Spring's {@code WebApplicationInitializer} SPI consists of just one method:
 * {@link WebApplicationInitializer#onStartup(ServletContext)}. The signature is intentionally
 * quite similar to {@link ServletContainerInitializer#onStartup(Set, ServletContext)}:
 * simply put, {@code SpringServletContainerInitializer} is responsible for instantiating
 * and delegating the {@code ServletContext} to any user-defined
 * {@code WebApplicationInitializer} implementations. It is then the responsibility of
 * each {@code WebApplicationInitializer} to do the actual work of initializing the
 * {@code ServletContext}. The exact process of delegation is described in detail in the
 * {@link #onStartup onStartup} documentation below.
 *
 * <h2>General Notes</h2>
 * In general, this class should be viewed as <em>supporting infrastructure</em> for
 * the more important and user-facing {@code WebApplicationInitializer} SPI. Taking
 * advantage of this container initializer is also completely <em>optional</em>: while
 * it is true that this initializer will be loaded and invoked under all Servlet 3.0+
 * runtimes, it remains the user's choice whether to make any
 * {@code WebApplicationInitializer} implementations available on the classpath. If no
 * {@code WebApplicationInitializer} types are detected, this container initializer will
 * have no effect.
 *
 * <p>Note that use of this container initializer and of {@code WebApplicationInitializer}
 * is not in any way "tied" to Spring MVC other than the fact that the types are shipped
 * in the {@code spring-web} module JAR. Rather, they can be considered general-purpose
 * in their ability to facilitate convenient code-based configuration of the
 * {@code ServletContext}. In other words, any servlet, listener, or filter may be
 * registered within a {@code WebApplicationInitializer}, not just Spring MVC-specific
 * components.
 *
 * <p>This class is neither designed for extension nor intended to be extended.
 * It should be considered an internal type, with {@code WebApplicationInitializer}
 * being the public-facing SPI.
 *
 * <h2>See Also</h2>
 * See {@link WebApplicationInitializer} Javadoc for examples and detailed usage
 * recommendations.<p>
 *
 * <p>
 * Servlet 30 {@link ServletContainerInitializer}旨在使用Spring的{@link WebApplicationInitializer} SPI支持基于代码的
 * servlet容器配置,而不是传统的{@code webxml}方法。
 * 
 *  <h2>操作机制</h2>在容器启动期间,此类将被加载和实例化,并且具有由任何Servlet 30兼容容器调用的{@link #onStartup}方法,假设{@code spring-web}模块J
 * AR存在于classpath这通过JAR服务API {@link ServiceLoader#load(Class))方法检测{@code spring-web}模块的{@code META-INF / services / javaxservletServletContainerInitializer}
 * 服务提供程序配置文件。
 * <a href="http://download.oracle.com/javase/6/docs/technotes/guides/jar/jar.html#Service%20Provider">
 * JAR Services API文档</a>以及Servlet 30 Final Draft规范的第824节</em>部分,以获取完整的详细信息
 * 
 *  <h3>与{@code webxml} </h3>结合使用Web应用程序可以通过{@code webxml}中的{@code metadata-complete}属性来限制在启动时扫描Servlet容
 * 器的类路径数量,它控制了在{@code webxml}中也可以通过{@code <absolute-ordering>}元素扫描Servlet注释,该元素控制哪些网页片段(ie jar)被允许执行{@code ServletContainerInitializer}
 * 扫描当使用此功能可以通过向{@code webxml}中的命名Web片段列表添加"spring_web"来启用{@link SpringServletContainerInitializer},如下所示
 * ：。
 * 
 * <pre class="code">
 *  {@码
 * <absolute-ordering>
 * <name> some_web_fragment </name> <name> spring_web </name>
 * </absolute-ordering>
 *  } </PRE>
 * 
 * <h2>与Spring的关系{@code WebApplicationInitializer} </h2> Spring的{@code WebApplicationInitializer} SPI只包含
 * 一种方法：{@link WebApplicationInitializer#onStartup(ServletContext)}该签名有意与{@link ServletContainerInitializer# onStartup(Set,ServletContext)}
 * ：简单地说,{@code SpringServletContainerInitializer}负责将{@code ServletContext}实例化和委派给任何用户定义的{@code WebApplicationInitializer}
 * 实现。
 * 
 * @author Chris Beams
 * @author Juergen Hoeller
 * @author Rossen Stoyanchev
 * @since 3.1
 * @see #onStartup(Set, ServletContext)
 * @see WebApplicationInitializer
 */
@HandlesTypes(WebApplicationInitializer.class)
public class SpringServletContainerInitializer implements ServletContainerInitializer {

	/**
	 * Delegate the {@code ServletContext} to any {@link WebApplicationInitializer}
	 * implementations present on the application classpath.
	 * <p>Because this class declares @{@code HandlesTypes(WebApplicationInitializer.class)},
	 * Servlet 3.0+ containers will automatically scan the classpath for implementations
	 * of Spring's {@code WebApplicationInitializer} interface and provide the set of all
	 * such types to the {@code webAppInitializerClasses} parameter of this method.
	 * <p>If no {@code WebApplicationInitializer} implementations are found on the classpath,
	 * this method is effectively a no-op. An INFO-level log message will be issued notifying
	 * the user that the {@code ServletContainerInitializer} has indeed been invoked but that
	 * no {@code WebApplicationInitializer} implementations were found.
	 * <p>Assuming that one or more {@code WebApplicationInitializer} types are detected,
	 * they will be instantiated (and <em>sorted</em> if the @{@link
	 * org.springframework.core.annotation.Order @Order} annotation is present or
	 * the {@link org.springframework.core.Ordered Ordered} interface has been
	 * implemented). Then the {@link WebApplicationInitializer#onStartup(ServletContext)}
	 * method will be invoked on each instance, delegating the {@code ServletContext} such
	 * that each instance may register and configure servlets such as Spring's
	 * {@code DispatcherServlet}, listeners such as Spring's {@code ContextLoaderListener},
	 * or any other Servlet API componentry such as filters.
	 * <p>
	 * 然后,每个{@code WebApplicationInitializer }进行初始化{@code ServletContext}的实际工作在下面的{@link #onStartup onStartup}
	 * 文档中详细描述了具体的委派过程。
	 * 
	 * <h2>一般说明</h2>一般来说,这个类应该被视为支持基础架构的</em>,用于更重要和面向用户的{@code WebApplicationInitializer} SPI利用此容器初始化器也是完全<em>
	 * 可选</em>：尽管这个初始化程序将在所有Servlet 30+运行时都被加载和调用,但仍然是用户选择是否在类路径上使任何{@code WebApplicationInitializer}实现可用。
	 * 如果没有{ @code WebApplicationInitializer}类型被检测到,这个容器初始化程序将不起作用。
	 * 
	 * 请注意,使用此容器初始化程序和{@code WebApplicationInitializer}不会以任何方式"绑定"到Spring MVC,而不是类型在{@code spring-web}模块JAR中
	 * 发送的事实相反,他们可以被认为是通用目的,以便于方便基于代码的{@code ServletContext}配置。
	 * 换句话说,任何servlet,侦听器或过滤器都可以在{@code WebApplicationInitializer}中注册,而不仅仅是Spring MVC特定成分。
	 * 
	 *  <p>此类既不是为扩展而设计的,也不是要扩展的。它应该被认为是一种内部类型,{@code WebApplicationInitializer}是面向公众的SPI
	 * 
	 * @param webAppInitializerClasses all implementations of
	 * {@link WebApplicationInitializer} found on the application classpath
	 * @param servletContext the servlet context to be initialized
	 * @see WebApplicationInitializer#onStartup(ServletContext)
	 * @see AnnotationAwareOrderComparator
	 */
	@Override
	public void onStartup(Set<Class<?>> webAppInitializerClasses, ServletContext servletContext)
			throws ServletException {

		List<WebApplicationInitializer> initializers = new LinkedList<WebApplicationInitializer>();

		if (webAppInitializerClasses != null) {
			for (Class<?> waiClass : webAppInitializerClasses) {
				// Be defensive: Some servlet containers provide us with invalid classes,
				// no matter what @HandlesTypes says...
				if (!waiClass.isInterface() && !Modifier.isAbstract(waiClass.getModifiers()) &&
						WebApplicationInitializer.class.isAssignableFrom(waiClass)) {
					try {
						initializers.add((WebApplicationInitializer) waiClass.newInstance());
					}
					catch (Throwable ex) {
						throw new ServletException("Failed to instantiate WebApplicationInitializer class", ex);
					}
				}
			}
		}

		if (initializers.isEmpty()) {
			servletContext.log("No Spring WebApplicationInitializer types detected on classpath");
			return;
		}

		servletContext.log(initializers.size() + " Spring WebApplicationInitializers detected on classpath");
		AnnotationAwareOrderComparator.sort(initializers);
		for (WebApplicationInitializer initializer : initializers) {
			initializer.onStartup(servletContext);
		}
	}

}
