/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.web.servlet.support;

import org.springframework.util.ObjectUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

/**
 * Base class for {@link org.springframework.web.WebApplicationInitializer}
 * implementations that register a
 * {@link org.springframework.web.servlet.DispatcherServlet DispatcherServlet}
 * configured with annotated classes, e.g. Spring's
 * {@link org.springframework.context.annotation.Configuration @Configuration} classes.
 *
 * <p>Concrete implementations are required to implement {@link #getRootConfigClasses()}
 * and {@link #getServletConfigClasses()} as well as {@link #getServletMappings()}.
 * Further template and customization methods are provided by
 * {@link AbstractDispatcherServletInitializer}.
 *
 * <p>This is the preferred approach for applications that use Java-based
 * Spring configuration.
 *
 * <p>
 * 注册配置有注释类的{@link orgspringframeworkwebservletDispatcherServlet DispatcherServlet}的{@link orgspringframeworkwebWebApplicationInitializer}
 * 实现的基类,例如Spring的{@link orgspringframeworkcontextannotationConfiguration @Configuration}类。
 * 
 *  需要具体的实现来实现{@link #getRootConfigClasses()}和{@link #getServletConfigClasses()}以及{@link #getServletMappings()}
 * 。
 * {@link AbstractDispatcherServletInitializer}提供了更多的模板和自定义方法。
 * 
 *  <p>这是使用基于Java的Spring配置的应用程序的首选方法
 * 
 * 
 * @author Arjen Poutsma
 * @author Chris Beams
 * @since 3.2
 */
public abstract class AbstractAnnotationConfigDispatcherServletInitializer
		extends AbstractDispatcherServletInitializer {

	/**
	 * {@inheritDoc}
	 * <p>This implementation creates an {@link AnnotationConfigWebApplicationContext},
	 * providing it the annotated classes returned by {@link #getRootConfigClasses()}.
	 * Returns {@code null} if {@link #getRootConfigClasses()} returns {@code null}.
	 * <p>
	 * {@inheritDoc} <p>此实现创建一个{@link AnnotationConfigWebApplicationContext},为其提供由{@link #getRootConfigClasses())返回的注释类返回{@code null}
	 * 如果{@link #getRootConfigClasses()}返回{@代码null}。
	 * 
	 */
	@Override
	protected WebApplicationContext createRootApplicationContext() {
		Class<?>[] configClasses = getRootConfigClasses();
		if (!ObjectUtils.isEmpty(configClasses)) {
			AnnotationConfigWebApplicationContext rootAppContext = new AnnotationConfigWebApplicationContext();
			rootAppContext.register(configClasses);
			return rootAppContext;
		}
		else {
			return null;
		}
	}

	/**
	 * {@inheritDoc}
	 * <p>This implementation creates an {@link AnnotationConfigWebApplicationContext},
	 * providing it the annotated classes returned by {@link #getServletConfigClasses()}.
	 * <p>
	 *  {@inheritDoc} <p>此实现创建一个{@link AnnotationConfigWebApplicationContext},为其提供由{@link #getServletConfigClasses()}
	 * 返回的注释类。
	 * 
	 */
	@Override
	protected WebApplicationContext createServletApplicationContext() {
		AnnotationConfigWebApplicationContext servletAppContext = new AnnotationConfigWebApplicationContext();
		Class<?>[] configClasses = getServletConfigClasses();
		if (!ObjectUtils.isEmpty(configClasses)) {
			servletAppContext.register(configClasses);
		}
		return servletAppContext;
	}

	/**
	 * Specify {@link org.springframework.context.annotation.Configuration @Configuration}
	 * and/or {@link org.springframework.stereotype.Component @Component} classes to be
	 * provided to the {@linkplain #createRootApplicationContext() root application context}.
	 * <p>
	 *  指定要提供给{@linkplain #createRootApplicationContext()根应用程序上下文的{@link orgspringframeworkcontextannotationConfiguration @Configuration}
	 * 和/或{@link orgspringframeworkstereotypeComponent @Component}类)。
	 * 
	 * 
	 * @return the configuration classes for the root application context, or {@code null}
	 * if creation and registration of a root context is not desired
	 */
	protected abstract Class<?>[] getRootConfigClasses();

	/**
	 * Specify {@link org.springframework.context.annotation.Configuration @Configuration}
	 * and/or {@link org.springframework.stereotype.Component @Component} classes to be
	 * provided to the {@linkplain #createServletApplicationContext() dispatcher servlet
	 * application context}.
	 * <p>
	 * 指定要提供给{@linkplain #createServletApplicationContext()调度程序servlet应用程序环境的{@link orgspringframeworkcontextannotationConfiguration @Configuration}
	 * 和/或{@link orgspringframeworkstereotypeComponent @Component}类)。
	 * 
	 * @return the configuration classes for the dispatcher servlet application context or
	 * {@code null} if all configuration is specified through root config classes.
	 */
	protected abstract Class<?>[] getServletConfigClasses();

}
