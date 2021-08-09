/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2015 the original author or authors.
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

package org.springframework.web.servlet.view.groovy;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import groovy.text.markup.MarkupTemplateEngine;
import groovy.text.markup.TemplateConfiguration;
import groovy.text.markup.TemplateResolver;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;

/**
 * An extension of Groovy's {@link groovy.text.markup.TemplateConfiguration} and
 * an implementation of Spring MVC's {@link GroovyMarkupConfig} for creating
 * a {@code MarkupTemplateEngine} for use in a web application. The most basic
 * way to configure this class is to set the "resourceLoaderPath". For example:
 *
 * <pre class="code">
 *
 * // Add the following to an &#64;Configuration class
 *
 * &#64;Bean
 * public GroovyMarkupConfig groovyMarkupConfigurer() {
 *     GroovyMarkupConfigurer configurer = new GroovyMarkupConfigurer();
 *     configurer.setResourceLoaderPath("classpath:/WEB-INF/groovymarkup/");
 *     return configurer;
 * }
 * </pre>
 *
 * By default this bean will create a {@link MarkupTemplateEngine} with:
 * <ul>
 * <li>a parent ClassLoader for loading Groovy templates with their references
 * <li>the default configuration in the base class {@link TemplateConfiguration}
 * <li>a {@link groovy.text.markup.TemplateResolver} for resolving template files
 * </ul>
 *
 * You can provide the {@link MarkupTemplateEngine} instance directly to this bean
 * in which case all other properties will not be effectively ignored.
 *
 * <p>This bean must be included in the application context of any application
 * using the Spring MVC {@link GroovyMarkupView} for rendering. It exists purely
 * for the purpose of configuring Groovy's Markup templates. It is not meant to be
 * referenced by application components directly. It implements GroovyMarkupConfig
 * to be found by GroovyMarkupView without depending on a bean name. Each
 * DispatcherServlet can define its own GroovyMarkupConfigurer if desired.
 *
 * <p>Note that resource caching is enabled by default in {@link MarkupTemplateEngine}.
 * Use the {@link #setCacheTemplates(boolean)} to configure that as necessary.

 * <p>Spring's Groovy Markup template support requires Groovy 2.3.1 or higher.
 *
 * <p>
 * Groovy的{@link groovytextmarkupTemplateConfiguration}的扩展和Spring MVC的{@link GroovyMarkupConfig}的实现,用于创建
 * 用于Web应用程序的{@code MarkupTemplateEngine}配置此类的最基本的方法是设置"resourceLoaderPath"For例：。
 * 
 * <pre class="code">
 * 
 *  //将以下内容添加到@Configuration类中
 * 
 *  @Bean public GroovyMarkupConfig groovyMarkupConfigurer(){GroovyMarkupConfigurer configurer = new GroovyMarkupConfigurer(); configurersetResourceLoaderPath( "类路径：/ WEB-INF / groovymarkup /");返回configurer; }
 * 。
 * </pre>
 * 
 *  默认情况下,该bean将创建一个{@link MarkupTemplateEngine}：
 * <ul>
 * <li>用于加载Groovy模板及其引用的父类ClassLoader <li>用于解析模板文件的基类{@link TemplateConfiguration} <li> a {@link groovytextmarkupTemplateResolver}
 * 中的默认配置。
 * </ul>
 * 
 *  您可以将{@link MarkupTemplateEngine}实例直接提供给此bean,在这种情况下,所有其他属性将无法被有效忽略
 * 
 * <p>此bean必须包含在使用Spring MVC {@link GroovyMarkupView}的任何应用程序的应用程序上下文中,用于呈现它纯属于配置Groovy的标记模板的目的它不意味着直接由应用
 * 程序组件引用它实现GroovyMarkupConfig可以由GroovyMarkupView找到,而不依赖于bean名称每个DispatcherServlet都可以定义自己的GroovyMarkupCo
 * nfigurer,如果需要的话。
 * 
 *  <p>请注意,默认情况下,{@link MarkupTemplateEngine}中启用了资源缓存。使用{@link #setCacheTemplates(boolean)}根据需要进行配置
 * 
 * @author Brian Clozel
 * @author Rossen Stoyanchev
 * @since 4.1
 * @see GroovyMarkupView
 * @see <a href="http://groovy-lang.org/templating.html#_the_markuptemplateengine">
 *     Groovy Markup Template engine documentation</a>
 */
public class GroovyMarkupConfigurer extends TemplateConfiguration
		implements GroovyMarkupConfig, ApplicationContextAware, InitializingBean {

	private String resourceLoaderPath = "classpath:";

	private MarkupTemplateEngine templateEngine;

	private ApplicationContext applicationContext;


	/**
	 * Set the Groovy Markup Template resource loader path(s) via a Spring resource
	 * location. Accepts multiple locations as a comma-separated list of paths.
	 * Standard URLs like "file:" and "classpath:" and pseudo URLs are supported
	 * as understood by Spring's {@link org.springframework.core.io.ResourceLoader}.
	 * Relative paths are allowed when running in an ApplicationContext.
	 *
	 * <p>
	 * 
	 *  Spring的Groovy Markup模板支持需要Groovy 231或更高版本
	 * 
	 */
	public void setResourceLoaderPath(String resourceLoaderPath) {
		this.resourceLoaderPath = resourceLoaderPath;
	}

	public String getResourceLoaderPath() {
		return this.resourceLoaderPath;
	}

	/**
	 * Set a pre-configured MarkupTemplateEngine to use for the Groovy Markup
	 * Template web configuration.
	 * <p>Note that this engine instance has to be manually configured, since all
	 * other bean properties of this configurer will be ignored.
	 * <p>
	 * 通过Spring资源位置设置Groovy标记模板资源加载程序路径接受多个位置作为逗号分隔的路径标准URL(如"file："和"classpath：")和伪URL是受支持的,Spring的{@ link orgspringframeworkcoreioResourceLoader}
	 * 在ApplicationContext中运行时允许相对路径。
	 * 
	 */
	public void setTemplateEngine(MarkupTemplateEngine templateEngine) {
		this.templateEngine = templateEngine;
	}

	public MarkupTemplateEngine getTemplateEngine() {
		return templateEngine;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	protected ApplicationContext getApplicationContext() {
		return this.applicationContext;
	}

	/**
	 * This method should not be used, since the considered Locale for resolving
	 * templates is the Locale for the current HTTP request.
	 * <p>
	 *  设置预配置的MarkupTemplateEngine用于Groovy标记模板Web配置<p>请注意,此引擎实例必须手动配置,因为此配置程序的所有其他bean属性将被忽略
	 * 
	 */
	@Override
	public void setLocale(Locale locale) {
		super.setLocale(locale);
	}


	@Override
	public void afterPropertiesSet() throws Exception {
		if (this.templateEngine == null) {
			this.templateEngine = createTemplateEngine();
		}
	}

	protected MarkupTemplateEngine createTemplateEngine() throws IOException {
		if (this.templateEngine == null) {
			ClassLoader templateClassLoader = createTemplateClassLoader();
			this.templateEngine = new MarkupTemplateEngine(templateClassLoader, this, new LocaleTemplateResolver());
		}
		return this.templateEngine;
	}

	/**
	 * Create a parent ClassLoader for Groovy to use as parent ClassLoader
	 * when loading and compiling templates.
	 * <p>
	 *  不应该使用此方法,因为所考虑的用于解析模板的区域设置是当前HTTP请求的区域设置
	 * 
	 */
	protected ClassLoader createTemplateClassLoader() throws IOException {
		String[] paths = StringUtils.commaDelimitedListToStringArray(getResourceLoaderPath());
		List<URL> urls = new ArrayList<URL>();
		for (String path : paths) {
			Resource[] resources = getApplicationContext().getResources(path);
			if (resources.length > 0) {
				for (Resource resource : resources) {
					if (resource.exists()) {
						urls.add(resource.getURL());
					}
				}
			}
		}
		ClassLoader classLoader = getApplicationContext().getClassLoader();
		return (urls.size() > 0 ? new URLClassLoader(urls.toArray(new URL[urls.size()]), classLoader) : classLoader);
	}

	/**
	 * Resolve a template from the given template path.
	 * <p>The default implementation uses the Locale associated with the current request,
	 * as obtained through {@link org.springframework.context.i18n.LocaleContextHolder LocaleContextHolder},
	 * to find the template file. Effectively the locale configured at the engine level is ignored.
	 * <p>
	 * 在加载和编译模板时,为Groovy创建一个父级ClassLoader以用作父ClassLoader
	 * 
	 * 
	 * @see LocaleContextHolder
	 * @see #setLocale
	 */
	protected URL resolveTemplate(ClassLoader classLoader, String templatePath) throws IOException {
		MarkupTemplateEngine.TemplateResource resource = MarkupTemplateEngine.TemplateResource.parse(templatePath);
		Locale locale = LocaleContextHolder.getLocale();
		URL url = classLoader.getResource(resource.withLocale(locale.toString().replace("-", "_")).toString());
		if (url == null) {
			url = classLoader.getResource(resource.withLocale(locale.getLanguage()).toString());
		}
		if (url == null) {
			url = classLoader.getResource(resource.withLocale(null).toString());
		}
		if (url == null) {
			throw new IOException("Unable to load template:" + templatePath);
		}
		return url;
	}


	/**
	 * Custom {@link TemplateResolver template resolver} that simply delegates to
	 * {@link #resolveTemplate(ClassLoader, String)}..
	 * <p>
	 *  从给定的模板路径解析模板<p>默认实现使用通过{@link orgspringframeworkcontexti18nLocaleContextHolder LocaleContextHolder}获
	 * 取的当前请求关联的区域设置来查找模板文件有效地忽略在引擎级配置的区域设置被忽略。
	 * 
	 */
	private class LocaleTemplateResolver implements TemplateResolver {

		private ClassLoader classLoader;

		@Override
		public void configure(ClassLoader templateClassLoader, TemplateConfiguration configuration) {
			this.classLoader = templateClassLoader;
		}

		@Override
		public URL resolveTemplate(String templatePath) throws IOException {
			return GroovyMarkupConfigurer.this.resolveTemplate(this.classLoader, templatePath);
		}
	}

}
