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

package org.springframework.context.support;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * {@link AbstractRefreshableApplicationContext} subclass that adds common handling
 * of specified config locations. Serves as base class for XML-based application
 * context implementations such as {@link ClassPathXmlApplicationContext} and
 * {@link FileSystemXmlApplicationContext}, as well as
 * {@link org.springframework.web.context.support.XmlWebApplicationContext} and
 * {@link org.springframework.web.portlet.context.XmlPortletApplicationContext}.
 *
 * <p>
 * {@link AbstractRefreshableApplicationContext}添加指定配置位置的常规处理的子类作为基于XML的应用程序上下文实现(如{@link ClassPathXmlApplicationContext}
 * 和{@link FileSystemXmlApplicationContext})以及{@link orgspringframeworkwebcontextsupportXmlWebApplicationContext}
 * 和{@链接orgspringframeworkwebportletcontextXmlPortletApplicationContext}。
 * 
 * 
 * @author Juergen Hoeller
 * @since 2.5.2
 * @see #setConfigLocation
 * @see #setConfigLocations
 * @see #getDefaultConfigLocations
 */
public abstract class AbstractRefreshableConfigApplicationContext extends AbstractRefreshableApplicationContext
		implements BeanNameAware, InitializingBean {

	private String[] configLocations;

	private boolean setIdCalled = false;


	/**
	 * Create a new AbstractRefreshableConfigApplicationContext with no parent.
	 * <p>
	 *  创建一个没有父对象的AbstractRefreshableConfigApplicationContext
	 * 
	 */
	public AbstractRefreshableConfigApplicationContext() {
	}

	/**
	 * Create a new AbstractRefreshableConfigApplicationContext with the given parent context.
	 * <p>
	 *  使用给定的父上下文创建一个新的AbstractRefreshableConfigApplicationContext
	 * 
	 * 
	 * @param parent the parent context
	 */
	public AbstractRefreshableConfigApplicationContext(ApplicationContext parent) {
		super(parent);
	}


	/**
	 * Set the config locations for this application context in init-param style,
	 * i.e. with distinct locations separated by commas, semicolons or whitespace.
	 * <p>If not set, the implementation may use a default as appropriate.
	 * <p>
	 * 在init-param样式中设置此应用程序上下文的配置位置,即用逗号,分号或空格分隔的不同位置<p>如果未设置,则实现可能会使用默认值
	 * 
	 */
	public void setConfigLocation(String location) {
		setConfigLocations(StringUtils.tokenizeToStringArray(location, CONFIG_LOCATION_DELIMITERS));
	}

	/**
	 * Set the config locations for this application context.
	 * <p>If not set, the implementation may use a default as appropriate.
	 * <p>
	 *  设置此应用程序上下文的配置位置<p>如果未设置,则实施可能会使用默认值
	 * 
	 */
	public void setConfigLocations(String... locations) {
		if (locations != null) {
			Assert.noNullElements(locations, "Config locations must not be null");
			this.configLocations = new String[locations.length];
			for (int i = 0; i < locations.length; i++) {
				this.configLocations[i] = resolvePath(locations[i]).trim();
			}
		}
		else {
			this.configLocations = null;
		}
	}

	/**
	 * Return an array of resource locations, referring to the XML bean definition
	 * files that this context should be built with. Can also include location
	 * patterns, which will get resolved via a ResourcePatternResolver.
	 * <p>The default implementation returns {@code null}. Subclasses can override
	 * this to provide a set of resource locations to load bean definitions from.
	 * <p>
	 *  返回一个资源位置数组,参考这个上下文应该构建的XML bean定义文件还可以包括位置模式,它将通过ResourcePatternResolver得到解决<p>默认实现返回{@code null}子类可
	 * 以覆盖此提供一组资源位置来加载bean定义。
	 * 
	 * 
	 * @return an array of resource locations, or {@code null} if none
	 * @see #getResources
	 * @see #getResourcePatternResolver
	 */
	protected String[] getConfigLocations() {
		return (this.configLocations != null ? this.configLocations : getDefaultConfigLocations());
	}

	/**
	 * Return the default config locations to use, for the case where no
	 * explicit config locations have been specified.
	 * <p>The default implementation returns {@code null},
	 * requiring explicit config locations.
	 * <p>
	 * 返回默认的配置位置,对于没有指定显式配置位置的情况<p>默认实现返回{@code null},需要显式配置位置
	 * 
	 * 
	 * @return an array of default config locations, if any
	 * @see #setConfigLocations
	 */
	protected String[] getDefaultConfigLocations() {
		return null;
	}

	/**
	 * Resolve the given path, replacing placeholders with corresponding
	 * environment property values if necessary. Applied to config locations.
	 * <p>
	 *  解决给定的路径,如有必要,将占位符替换为相应的环境属性值应用于配置位置
	 * 
	 * 
	 * @param path the original file path
	 * @return the resolved file path
	 * @see org.springframework.core.env.Environment#resolveRequiredPlaceholders(String)
	 */
	protected String resolvePath(String path) {
		return getEnvironment().resolveRequiredPlaceholders(path);
	}


	@Override
	public void setId(String id) {
		super.setId(id);
		this.setIdCalled = true;
	}

	/**
	 * Sets the id of this context to the bean name by default,
	 * for cases where the context instance is itself defined as a bean.
	 * <p>
	 *  默认情况下,将上下文的id设置为bean名称,对于上下文实例本身定义为bean的情况
	 * 
	 */
	@Override
	public void setBeanName(String name) {
		if (!this.setIdCalled) {
			super.setId(name);
			setDisplayName("ApplicationContext '" + name + "'");
		}
	}

	/**
	 * Triggers {@link #refresh()} if not refreshed in the concrete context's
	 * constructor already.
	 * <p>
	 *  触发器{@link #refresh()}如果不在具体上下文的构造函数中刷新
	 */
	@Override
	public void afterPropertiesSet() {
		if (!isActive()) {
			refresh();
		}
	}

}
