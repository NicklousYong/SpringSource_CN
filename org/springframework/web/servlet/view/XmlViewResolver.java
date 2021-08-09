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

package org.springframework.web.servlet.view;

import java.util.Locale;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.xml.ResourceEntityResolver;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.io.Resource;
import org.springframework.web.context.support.GenericWebApplicationContext;
import org.springframework.web.servlet.View;

/**
 * A {@link org.springframework.web.servlet.ViewResolver} implementation that uses
 * bean definitions in a dedicated XML file for view definitions, specified by
 * resource location. The file will typically be located in the WEB-INF directory;
 * the default is "/WEB-INF/views.xml".
 *
 * <p>This {@code ViewResolver} does not support internationalization at the level
 * of its definition resources. Consider {@link ResourceBundleViewResolver} if you
 * need to apply different view resources per locale.
 *
 * <p>Note: This {@code ViewResolver} implements the {@link Ordered} interface
 * in order to allow for flexible participation in {@code ViewResolver} chaining.
 * For example, some special views could be defined via this {@code ViewResolver}
 * (giving it 0 as "order" value), while all remaining views could be resolved by
 * a {@link UrlBasedViewResolver}.
 *
 * <p>
 * 在专用XML文件中使用bean定义的{@link orgspringframeworkwebservletViewResolver}实现,由资源位置指定的视图定义该文件通常位于WEB-INF目录中;默认
 * 为"/ WEB-INF / viewsxml"。
 * 
 *  <p>此{@code ViewResolver}在其定义资源级别不支持国际化如果您需要每个语言环境应用不同的视图资源,请考虑{@link ResourceBundleViewResolver}
 * 
 * <p>注意：{@code ViewResolver}实现了{@link Ordered}界面,以便灵活地参与{@code ViewResolver}链接。
 * 例如,可以通过{@code ViewResolver}(将其设为0作为"订单"值),而所有剩余的视图可以由{@link UrlBasedViewResolver}。
 * 
 * 
 * @author Juergen Hoeller
 * @since 18.06.2003
 * @see org.springframework.context.ApplicationContext#getResource
 * @see ResourceBundleViewResolver
 * @see UrlBasedViewResolver
 */
public class XmlViewResolver extends AbstractCachingViewResolver
		implements Ordered, InitializingBean, DisposableBean {

	/** Default if no other location is supplied */
	public final static String DEFAULT_LOCATION = "/WEB-INF/views.xml";


	private int order = Integer.MAX_VALUE;  // default: same as non-Ordered

	private Resource location;

	private ConfigurableApplicationContext cachedFactory;


	public void setOrder(int order) {
		this.order = order;
	}

	@Override
	public int getOrder() {
		return this.order;
	}

	/**
	 * Set the location of the XML file that defines the view beans.
	 * <p>The default is "/WEB-INF/views.xml".
	 * <p>
	 *  设置定义视图bean的XML文件的位置<p>默认值为"/ WEB-INF / viewsxml"
	 * 
	 * 
	 * @param location the location of the XML file.
	 */
	public void setLocation(Resource location) {
		this.location = location;
	}

	/**
	 * Pre-initialize the factory from the XML file.
	 * Only effective if caching is enabled.
	 * <p>
	 *  从XML文件预初始化工厂仅在启用缓存时有效
	 * 
	 */
	@Override
	public void afterPropertiesSet() throws BeansException {
		if (isCache()) {
			initFactory();
		}
	}


	/**
	 * This implementation returns just the view name,
	 * as XmlViewResolver doesn't support localized resolution.
	 * <p>
	 *  此实现只返回视图名称,因为XmlViewResolver不支持本地化解决方案
	 * 
	 */
	@Override
	protected Object getCacheKey(String viewName, Locale locale) {
		return viewName;
	}

	@Override
	protected View loadView(String viewName, Locale locale) throws BeansException {
		BeanFactory factory = initFactory();
		try {
			return factory.getBean(viewName, View.class);
		}
		catch (NoSuchBeanDefinitionException ex) {
			// Allow for ViewResolver chaining...
			return null;
		}
	}

	/**
	 * Initialize the view bean factory from the XML file.
	 * Synchronized because of access by parallel threads.
	 * <p>
	 *  从XML文件初始化视图bean工厂由于并行线程的访问而同步
	 * 
	 * 
	 * @throws BeansException in case of initialization errors
	 */
	protected synchronized BeanFactory initFactory() throws BeansException {
		if (this.cachedFactory != null) {
			return this.cachedFactory;
		}

		Resource actualLocation = this.location;
		if (actualLocation == null) {
			actualLocation = getApplicationContext().getResource(DEFAULT_LOCATION);
		}

		// Create child ApplicationContext for views.
		GenericWebApplicationContext factory = new GenericWebApplicationContext();
		factory.setParent(getApplicationContext());
		factory.setServletContext(getServletContext());

		// Load XML resource with context-aware entity resolver.
		XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(factory);
		reader.setEnvironment(getApplicationContext().getEnvironment());
		reader.setEntityResolver(new ResourceEntityResolver(getApplicationContext()));
		reader.loadBeanDefinitions(actualLocation);

		factory.refresh();

		if (isCache()) {
			this.cachedFactory = factory;
		}
		return factory;
	}


	/**
	 * Close the view bean factory on context shutdown.
	 * <p>
	 *  关闭视图bean工厂上下文关闭
	 */
	@Override
	public void destroy() throws BeansException {
		if (this.cachedFactory != null) {
			this.cachedFactory.close();
		}
	}

}
