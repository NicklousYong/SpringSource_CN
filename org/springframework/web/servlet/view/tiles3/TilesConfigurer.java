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

package org.springframework.web.servlet.view.tiles3;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import javax.el.ArrayELResolver;
import javax.el.BeanELResolver;
import javax.el.CompositeELResolver;
import javax.el.ListELResolver;
import javax.el.MapELResolver;
import javax.el.ResourceBundleELResolver;
import javax.servlet.ServletContext;
import javax.servlet.jsp.JspFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tiles.TilesContainer;
import org.apache.tiles.TilesException;
import org.apache.tiles.definition.DefinitionsFactory;
import org.apache.tiles.definition.DefinitionsReader;
import org.apache.tiles.definition.dao.BaseLocaleUrlDefinitionDAO;
import org.apache.tiles.definition.dao.CachingLocaleUrlDefinitionDAO;
import org.apache.tiles.definition.digester.DigesterDefinitionsReader;
import org.apache.tiles.el.ELAttributeEvaluator;
import org.apache.tiles.el.ScopeELResolver;
import org.apache.tiles.el.TilesContextBeanELResolver;
import org.apache.tiles.el.TilesContextELResolver;
import org.apache.tiles.evaluator.AttributeEvaluator;
import org.apache.tiles.evaluator.AttributeEvaluatorFactory;
import org.apache.tiles.evaluator.BasicAttributeEvaluatorFactory;
import org.apache.tiles.evaluator.impl.DirectAttributeEvaluator;
import org.apache.tiles.extras.complete.CompleteAutoloadTilesContainerFactory;
import org.apache.tiles.extras.complete.CompleteAutoloadTilesInitializer;
import org.apache.tiles.factory.AbstractTilesContainerFactory;
import org.apache.tiles.factory.BasicTilesContainerFactory;
import org.apache.tiles.impl.mgmt.CachingTilesContainer;
import org.apache.tiles.locale.LocaleResolver;
import org.apache.tiles.preparer.factory.PreparerFactory;
import org.apache.tiles.request.ApplicationContext;
import org.apache.tiles.request.ApplicationContextAware;
import org.apache.tiles.request.ApplicationResource;
import org.apache.tiles.startup.DefaultTilesInitializer;
import org.apache.tiles.startup.TilesInitializer;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.ClassUtils;
import org.springframework.web.context.ServletContextAware;

/**
 * Helper class to configure Tiles 3.x for the Spring Framework. See
 * <a href="http://tiles.apache.org">http://tiles.apache.org</a>
 * for more information about Tiles, which basically is a templating mechanism
 * for web applications using JSPs and other template engines.
 *
 * <p>The TilesConfigurer simply configures a TilesContainer using a set of files
 * containing definitions, to be accessed by {@link TilesView} instances. This is a
 * Spring-based alternative (for usage in Spring configuration) to the Tiles-provided
 * {@code ServletContextListener}
 * (e.g. {@link org.apache.tiles.extras.complete.CompleteAutoloadTilesListener}
 * for usage in {@code web.xml}.
 *
 * <p>TilesViews can be managed by any {@link org.springframework.web.servlet.ViewResolver}.
 * For simple convention-based view resolution, consider using {@link TilesViewResolver}.
 *
 * <p>A typical TilesConfigurer bean definition looks as follows:
 *
 * <pre class="code">
 * &lt;bean id="tilesConfigurer" class="org.springframework.web.servlet.view.tiles3.TilesConfigurer">
 *   &lt;property name="definitions">
 *     &lt;list>
 *       &lt;value>/WEB-INF/defs/general.xml&lt;/value>
 *       &lt;value>/WEB-INF/defs/widgets.xml&lt;/value>
 *       &lt;value>/WEB-INF/defs/administrator.xml&lt;/value>
 *       &lt;value>/WEB-INF/defs/customer.xml&lt;/value>
 *       &lt;value>/WEB-INF/defs/templates.xml&lt;/value>
 *     &lt;/list>
 *   &lt;/property>
 * &lt;/bean>
 * </pre>
 *
 * The values in the list are the actual Tiles XML files containing the definitions.
 * If the list is not specified, the default is {@code "/WEB-INF/tiles.xml"}.
 *
 * <p>Note that in Tiles 3 an underscore in the name of a file containing Tiles
 * definitions is used to indicate locale information, for example:
 *
 * <pre class="code">
 * &lt;bean id="tilesConfigurer" class="org.springframework.web.servlet.view.tiles3.TilesConfigurer">
 *   &lt;property name="definitions">
 *     &lt;list>
 *       &lt;value>/WEB-INF/defs/tiles.xml&lt;/value>
 *       &lt;value>/WEB-INF/defs/tiles_fr_FR.xml&lt;/value>
 *     &lt;/list>
 *   &lt;/property>
 * &lt;/bean>
 * </pre>
 *
 * <p>
 * 为Spring框架配置Tiles 3x的Helper类有关Tiles的更多信息,请参见<a href=\"http://tilesapacheorg\"> http：// tilesapacheorg 
 * </a>,这些信息基本上是使用JSP的Web应用程序的模板化机制,其他模板引擎。
 * 
 *  TilesConfigurer只使用一组包含定义的文件来配置TilesContainer,由{@link TilesView}实例访问。
 * 这是一个基于Spring的替代方案(用于Spring配置)到Tiles提供的{@code ServletContextListener }(例如{@link orgapachetilesextrascompleteCompleteAutoloadTilesListener}
 * 用于{@code webxml}。
 *  TilesConfigurer只使用一组包含定义的文件来配置TilesContainer,由{@link TilesView}实例访问。
 * 
 * <p> TilesViews可以由任何{@link orgspringframeworkwebservletViewResolver}管理。
 * 对于简单的基于约会的视图解析,请考虑使用{@link TilesViewResolver}。
 * 
 *  <p>典型的TilesConfigurer bean定义如下所示：
 * 
 * <pre class="code">
 * &lt;bean id="tilesConfigurer" class="org.springframework.web.servlet.view.tiles3.TilesConfigurer">
 * &lt;property name="definitions">
 * &lt;list>
 * &lt;value>/WEB-INF/defs/general.xml&lt;/value>
 * &lt;value>/WEB-INF/defs/widgets.xml&lt;/value>
 * &lt;value>/WEB-INF/defs/administrator.xml&lt;/value>
 * &lt;value>/WEB-INF/defs/customer.xml&lt;/value>
 * &lt;value>/WEB-INF/defs/templates.xml&lt;/value>
 * &lt;/list>
 * &lt;/property>
 * &lt;/bean>
 * </pre>
 * 
 *  列表中的值是包含定义的实际Tiles XML文件如果未指定列表,则默认为{@code"/ WEB-INF / tilesxml"}
 * 
 *  <p>请注意,在Tiles 3中,包含Tiles定义的文件的名称中的下划线用于指示区域设置信息,例如：
 * 
 * <pre class="code">
 * &lt;bean id="tilesConfigurer" class="org.springframework.web.servlet.view.tiles3.TilesConfigurer">
 * &lt;property name="definitions">
 * &lt;list>
 * &lt;value>/WEB-INF/defs/tiles.xml&lt;/value>
 * &lt;value>/WEB-INF/defs/tiles_fr_FR.xml&lt;/value>
 * &lt;/list>
 * &lt;/property>
 * &lt;/bean>
 * </pre>
 * 
 * 
 * @author mick semb wever
 * @author Rossen Stoyanchev
 * @author Juergen Hoeller
 * @since 3.2
 * @see TilesView
 * @see TilesViewResolver
 */
public class TilesConfigurer implements ServletContextAware, InitializingBean, DisposableBean {

	private static final boolean tilesElPresent =
			ClassUtils.isPresent("org.apache.tiles.el.ELAttributeEvaluator", TilesConfigurer.class.getClassLoader());


	protected final Log logger = LogFactory.getLog(getClass());

	private TilesInitializer tilesInitializer;

	private String[] definitions;

	private boolean checkRefresh = false;

	private boolean validateDefinitions = true;

	private Class<? extends DefinitionsFactory> definitionsFactoryClass;

	private Class<? extends PreparerFactory> preparerFactoryClass;

	private boolean useMutableTilesContainer = false;

	private ServletContext servletContext;


	/**
	 * Configure Tiles using a custom TilesInitializer, typically specified as an inner bean.
	 * <p>Default is a variant of {@link org.apache.tiles.startup.DefaultTilesInitializer},
	 * respecting the "definitions", "preparerFactoryClass" etc properties on this configurer.
	 * <p><b>NOTE: Specifying a custom TilesInitializer effectively disables all other bean
	 * properties on this configurer.</b> The entire initialization procedure is then left
	 * to the TilesInitializer as specified.
	 * <p>
	 */
	public void setTilesInitializer(TilesInitializer tilesInitializer) {
		this.tilesInitializer = tilesInitializer;
	}

	/**
	 * Specify whether to apply Tiles 3.0's "complete-autoload" configuration.
	 * <p>See {@link org.apache.tiles.extras.complete.CompleteAutoloadTilesContainerFactory}
	 * for details on the complete-autoload mode.
	 * <p><b>NOTE: Specifying the complete-autoload mode effectively disables all other bean
	 * properties on this configurer.</b> The entire initialization procedure is then left
	 * to {@link org.apache.tiles.extras.complete.CompleteAutoloadTilesInitializer}.
	 * <p>
	 * 使用定制的TilesInitializer配置Tiles,通常指定为内部bean <p>默认值是{@link orgapachetilesstartupDefaultTilesInitializer}的
	 * 一个变体,遵循此配置程序的"定义","preparerFactoryClass"等属性<p> <b>注意：指定一个定制的TilesInitializer有效地禁用此配置程序上的所有其他bean属性</b>
	 * 然后,整个初始化过程将按照指定的方式保留到TilesInitializer。
	 * 
	 * 
	 * @see org.apache.tiles.extras.complete.CompleteAutoloadTilesContainerFactory
	 * @see org.apache.tiles.extras.complete.CompleteAutoloadTilesInitializer
	 */
	public void setCompleteAutoload(boolean completeAutoload) {
		if (completeAutoload) {
			try {
				this.tilesInitializer = new SpringCompleteAutoloadTilesInitializer();
			}
			catch (Throwable ex) {
				throw new IllegalStateException("Tiles-Extras 3.0 not available", ex);
			}
		}
		else {
			this.tilesInitializer = null;
		}
	}

	/**
	 * Set the Tiles definitions, i.e. the list of files containing the definitions.
	 * Default is "/WEB-INF/tiles.xml".
	 * <p>
	 * 指定是否应用Tiles 30的"完全自动加载"配置<p>有关完全自动加载模式的详细信息,请参阅{@link orgapachetilesextrascompleteCompleteAutoloadTilesContainerFactory}
	 *  <p> <b>注意：指定完全自动加载模式有效地禁用所有其他bean属性这个配置文件</b>然后将整个初始化过程留给{@link orgapachetilesextrascompleteCompleteAutoloadTilesInitializer}
	 * 。
	 * 
	 */
	public void setDefinitions(String... definitions) {
		this.definitions = definitions;
	}

	/**
	 * Set whether to check Tiles definition files for a refresh at runtime.
	 * Default is "false".
	 * <p>
	 *  设置Tiles定义,即包含定义的文件列表Default为"/ WEB-INF / tilesxml"
	 * 
	 */
	public void setCheckRefresh(boolean checkRefresh) {
		this.checkRefresh = checkRefresh;
	}

	/**
	 * Set whether to validate the Tiles XML definitions. Default is "true".
	 * <p>
	 *  设置是否检查Tiles定义文件以便在运行时进行刷新默认为"false"
	 * 
	 */
	public void setValidateDefinitions(boolean validateDefinitions) {
		this.validateDefinitions = validateDefinitions;
	}

	/**
	 * Set the {@link org.apache.tiles.definition.DefinitionsFactory} implementation to use.
	 * Default is {@link org.apache.tiles.definition.UnresolvingLocaleDefinitionsFactory},
	 * operating on definition resource URLs.
	 * <p>Specify a custom DefinitionsFactory, e.g. a UrlDefinitionsFactory subclass,
	 * to customize the creation of Tiles Definition objects. Note that such a
	 * DefinitionsFactory has to be able to handle {@link java.net.URL} source objects,
	 * unless you configure a different TilesContainerFactory.
	 * <p>
	 *  设置是否验证Tiles XML定义默认为"true"
	 * 
	 */
	public void setDefinitionsFactoryClass(Class<? extends DefinitionsFactory> definitionsFactoryClass) {
		this.definitionsFactoryClass = definitionsFactoryClass;
	}

	/**
	 * Set the {@link org.apache.tiles.preparer.factory.PreparerFactory} implementation to use.
	 * Default is {@link org.apache.tiles.preparer.factory.BasicPreparerFactory}, creating
	 * shared instances for specified preparer classes.
	 * <p>Specify {@link SimpleSpringPreparerFactory} to autowire
	 * {@link org.apache.tiles.preparer.ViewPreparer} instances based on specified
	 * preparer classes, applying Spring's container callbacks as well as applying
	 * configured Spring BeanPostProcessors. If Spring's context-wide annotation-config
	 * has been activated, annotations in ViewPreparer classes will be automatically
	 * detected and applied.
	 * <p>Specify {@link SpringBeanPreparerFactory} to operate on specified preparer
	 * <i>names</i> instead of classes, obtaining the corresponding Spring bean from
	 * the DispatcherServlet's application context. The full bean creation process
	 * will be in the control of the Spring application context in this case,
	 * allowing for the use of scoped beans etc. Note that you need to define one
	 * Spring bean definition per preparer name (as used in your Tiles definitions).
	 * <p>
	 * 将{@link orgapachetilesdefinitionDefinitionsFactory}实现设置为使用默认值为{@link orgapachetilesdefinitionUnresolvingLocaleDefinitionsFactory}
	 * ,在定义资源URL上运行<p>指定自定义DefinitionFactory,例如UrlDefinitionsFactory子类,以自定义创建Tiles定义对象请注意,此类DefinitionFactor
	 * y具有能够处理{@link javanetURL}源对象,除非您配置不同的TilesContainerFactory。
	 * 
	 * 
	 * @see SimpleSpringPreparerFactory
	 * @see SpringBeanPreparerFactory
	 */
	public void setPreparerFactoryClass(Class<? extends PreparerFactory> preparerFactoryClass) {
		this.preparerFactoryClass = preparerFactoryClass;
	}

	/**
	 * Set whether to use a MutableTilesContainer (typically the CachingTilesContainer
	 * implementation) for this application. Default is "false".
	 * <p>
	 * 将{@link orgapachetilespreparerfactoryPreparerFactory}实现设置为使用Default是{@link orgapachetilespreparerfactoryBasicPreparerFactory}
	 * ,为指定的准备工作类创建共享实例<p>指定{@link SimpleSpringPreparerFactory}根据指定的preparer类自动布线{@link orgapachetilespreparerViewPreparer}
	 * 实例,应用Spring容器回调以及应用配置的Spring BeanPostProcessors如果Spring的上下文注释配置已激活,ViewPreparer类中的注释将被自动检测并应用<p>指定{@link SpringBeanPreparerFactory}
	 * 以指定的preparer <i>名称< / i>而不是类,从DispatcherServlet的应用程序上下文中获取相应的Spring bean在这种情况下,完整的bean创建过程将控制Spring应用
	 * 程序上下文,允许使用范围bean等。
	 * 请注意,您需要为每个preparer名称定义一个Spring bean定义(如"Tiles"中所使用的)。
	 * 
	 * 
	 * @see org.apache.tiles.mgmt.MutableTilesContainer
	 * @see org.apache.tiles.impl.mgmt.CachingTilesContainer
	 */
	public void setUseMutableTilesContainer(boolean useMutableTilesContainer) {
		this.useMutableTilesContainer = useMutableTilesContainer;
	}

	@Override
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	/**
	 * Creates and exposes a TilesContainer for this web application,
	 * delegating to the TilesInitializer.
	 * <p>
	 * 设置是否对此应用程序使用MutableTilesContainer(通常为CachingTilesContainer实现)默认为"false"
	 * 
	 * 
	 * @throws TilesException in case of setup failure
	 */
	@Override
	public void afterPropertiesSet() throws TilesException {
		ApplicationContext preliminaryContext = new SpringWildcardServletTilesApplicationContext(this.servletContext);
		if (this.tilesInitializer == null) {
			this.tilesInitializer = new SpringTilesInitializer();
		}
		this.tilesInitializer.initialize(preliminaryContext);
	}

	/**
	 * Removes the TilesContainer from this web application.
	 * <p>
	 *  创建并公开此Web应用程序的TilesContainer,委托给TilesInitializer
	 * 
	 * 
	 * @throws TilesException in case of cleanup failure
	 */
	@Override
	public void destroy() throws TilesException {
		this.tilesInitializer.destroy();
	}


	private class SpringTilesInitializer extends DefaultTilesInitializer {

		@Override
		protected AbstractTilesContainerFactory createContainerFactory(ApplicationContext context) {
			return new SpringTilesContainerFactory();
		}
	}


	private class SpringTilesContainerFactory extends BasicTilesContainerFactory {

		@Override
		protected TilesContainer createDecoratedContainer(TilesContainer originalContainer, ApplicationContext context) {
			return (useMutableTilesContainer ? new CachingTilesContainer(originalContainer) : originalContainer);
		}

		@Override
		protected List<ApplicationResource> getSources(ApplicationContext applicationContext) {
			if (definitions != null) {
				List<ApplicationResource> result = new LinkedList<ApplicationResource>();
				for (String definition : definitions) {
					Collection<ApplicationResource> resources = applicationContext.getResources(definition);
					if (resources != null) {
						result.addAll(resources);
					}
				}
				return result;
			}
			else {
				return super.getSources(applicationContext);
			}
		}

		@Override
		protected BaseLocaleUrlDefinitionDAO instantiateLocaleDefinitionDao(ApplicationContext applicationContext,
				LocaleResolver resolver) {
			BaseLocaleUrlDefinitionDAO dao = super.instantiateLocaleDefinitionDao(applicationContext, resolver);
			if (checkRefresh && dao instanceof CachingLocaleUrlDefinitionDAO) {
				((CachingLocaleUrlDefinitionDAO) dao).setCheckRefresh(true);
			}
			return dao;
		}

		@Override
		protected DefinitionsReader createDefinitionsReader(ApplicationContext context) {
			DigesterDefinitionsReader reader = (DigesterDefinitionsReader) super.createDefinitionsReader(context);
			reader.setValidating(validateDefinitions);
			return reader;
		}

		@Override
		protected DefinitionsFactory createDefinitionsFactory(ApplicationContext applicationContext,
				LocaleResolver resolver) {

			if (definitionsFactoryClass != null) {
				DefinitionsFactory factory = BeanUtils.instantiate(definitionsFactoryClass);
				if (factory instanceof ApplicationContextAware) {
					((ApplicationContextAware) factory).setApplicationContext(applicationContext);
				}
				BeanWrapper bw = PropertyAccessorFactory.forBeanPropertyAccess(factory);
				if (bw.isWritableProperty("localeResolver")) {
					bw.setPropertyValue("localeResolver", resolver);
				}
				if (bw.isWritableProperty("definitionDAO")) {
					bw.setPropertyValue("definitionDAO", createLocaleDefinitionDao(applicationContext, resolver));
				}
				return factory;
			}
			else {
				return super.createDefinitionsFactory(applicationContext, resolver);
			}
		}

		@Override
		protected PreparerFactory createPreparerFactory(ApplicationContext context) {
			if (preparerFactoryClass != null) {
				return BeanUtils.instantiate(preparerFactoryClass);
			}
			else {
				return super.createPreparerFactory(context);
			}
		}

		@Override
		protected LocaleResolver createLocaleResolver(ApplicationContext context) {
			return new SpringLocaleResolver();
		}

		@Override
		protected AttributeEvaluatorFactory createAttributeEvaluatorFactory(ApplicationContext context,
				LocaleResolver resolver) {
			AttributeEvaluator evaluator;
			if (tilesElPresent && JspFactory.getDefaultFactory() != null) {
				evaluator = new TilesElActivator().createEvaluator();
			}
			else {
				evaluator = new DirectAttributeEvaluator();
			}
			return new BasicAttributeEvaluatorFactory(evaluator);
		}
	}


	private static class SpringCompleteAutoloadTilesInitializer extends CompleteAutoloadTilesInitializer {

		@Override
		protected AbstractTilesContainerFactory createContainerFactory(ApplicationContext context) {
			return new SpringCompleteAutoloadTilesContainerFactory();
		}
	}


	private static class SpringCompleteAutoloadTilesContainerFactory extends CompleteAutoloadTilesContainerFactory {

		@Override
		protected LocaleResolver createLocaleResolver(ApplicationContext applicationContext) {
			return new SpringLocaleResolver();
		}
	}


	private class TilesElActivator {

		public AttributeEvaluator createEvaluator() {
			ELAttributeEvaluator evaluator = new ELAttributeEvaluator();
			evaluator.setExpressionFactory(
					JspFactory.getDefaultFactory().getJspApplicationContext(servletContext).getExpressionFactory());
			evaluator.setResolver(new CompositeELResolverImpl());
			return evaluator;
		}
	}


	private static class CompositeELResolverImpl extends CompositeELResolver {

		public CompositeELResolverImpl() {
			add(new ScopeELResolver());
			add(new TilesContextELResolver(new TilesContextBeanELResolver()));
			add(new TilesContextBeanELResolver());
			add(new ArrayELResolver(false));
			add(new ListELResolver(false));
			add(new MapELResolver(false));
			add(new ResourceBundleELResolver());
			add(new BeanELResolver(false));
		}
	}

}
