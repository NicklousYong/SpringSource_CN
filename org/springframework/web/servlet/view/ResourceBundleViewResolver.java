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

package org.springframework.web.servlet.view;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.support.PropertiesBeanDefinitionReader;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.web.context.support.GenericWebApplicationContext;
import org.springframework.web.servlet.View;

/**
 * A {@link org.springframework.web.servlet.ViewResolver} implementation that uses
 * bean definitions in a {@link ResourceBundle}, specified by the bundle basename.
 *
 * <p>The bundle is typically defined in a properties file, located in the classpath.
 * The default bundle basename is "views".
 *
 * <p>This {@code ViewResolver} supports localized view definitions, using the
 * default support of {@link java.util.PropertyResourceBundle}. For example, the
 * basename "views" will be resolved as class path resources "views_de_AT.properties",
 * "views_de.properties", "views.properties" - for a given Locale "de_AT".
 *
 * <p>Note: This {@code ViewResolver} implements the {@link Ordered} interface
 * in order to allow for flexible participation in {@code ViewResolver} chaining.
 * For example, some special views could be defined via this {@code ViewResolver}
 * (giving it 0 as "order" value), while all remaining views could be resolved by
 * a {@link UrlBasedViewResolver}.
 *
 * <p>
 *  使用{@link ResourceBundle}中的bean定义的{@link orgspringframeworkwebservletViewResolver}实现,由bundle basename
 * 指定。
 * 
 * <p>捆绑包通常在属性文件中定义,位于类路径中默认的捆绑包basename为"views"
 * 
 *  <p>此{@code ViewResolver}支持本地化视图定义,使用{@link javautilPropertyResourceBundle}的默认支持。
 * 例如,basename"views"将被解析为类路径资源"views_de_ATproperties","views_deproperties","viewsproperties" - 对于给定的区域设
 * 置"de_AT"。
 *  <p>此{@code ViewResolver}支持本地化视图定义,使用{@link javautilPropertyResourceBundle}的默认支持。
 * 
 *  <p>注意：{@code ViewResolver}实现了{@link Ordered}界面,以便灵活地参与{@code ViewResolver}链接。
 * 例如,可以通过{@code ViewResolver}(将其设为0作为"订单"值),而所有剩余的视图可以由{@link UrlBasedViewResolver}。
 * 
 * 
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @see java.util.ResourceBundle#getBundle
 * @see java.util.PropertyResourceBundle
 * @see UrlBasedViewResolver
 */
public class ResourceBundleViewResolver extends AbstractCachingViewResolver
		implements Ordered, InitializingBean, DisposableBean {

	/** The default basename if no other basename is supplied. */
	public final static String DEFAULT_BASENAME = "views";


	private int order = Integer.MAX_VALUE;  // default: same as non-Ordered

	private String[] basenames = new String[] {DEFAULT_BASENAME};

	private ClassLoader bundleClassLoader = Thread.currentThread().getContextClassLoader();

	private String defaultParentView;

	private Locale[] localesToInitialize;

	/* Locale -> BeanFactory */
	private final Map<Locale, BeanFactory> localeCache =
			new HashMap<Locale, BeanFactory>();

	/* List of ResourceBundle -> BeanFactory */
	private final Map<List<ResourceBundle>, ConfigurableApplicationContext> bundleCache =
			new HashMap<List<ResourceBundle>, ConfigurableApplicationContext>();


	public void setOrder(int order) {
		this.order = order;
	}

	@Override
	public int getOrder() {
		return this.order;
	}

	/**
	 * Set a single basename, following {@link java.util.ResourceBundle} conventions.
	 * The default is "views".
	 * <p>{@code ResourceBundle} supports different suffixes. For example,
	 * a base name of "views" might map to {@code ResourceBundle} files
	 * "views", "views_en_au" and "views_de".
	 * <p>Note that ResourceBundle names are effectively classpath locations: As a
	 * consequence, the JDK's standard ResourceBundle treats dots as package separators.
	 * This means that "test.theme" is effectively equivalent to "test/theme",
	 * just like it is for programmatic {@code java.util.ResourceBundle} usage.
	 * <p>
	 * 设置单个基础名称,遵循{@link javautilResourceBundle}约定默认为"views"<p> {@ code ResourceBundle}支持不同的后缀例如,基本名称"views"
	 * 可能映射到{@code ResourceBundle}文件"视图","views_en_au"和"views_de"<p>请注意,ResourceBundle名称实际上是类路径位置：因此,JDK的标准R
	 * esourceBundle将点视为包分隔符这意味着"testtheme"实际上等同于"test / theme"","\", \"views_en_au\" and \"views_de\" <p>No
	 * te that ResourceBundle names are effectively classpath locations: As a consequence, the JDK's standar
	 * d ResourceBundle treats dots as package separators This means that \"testtheme\" is effectively equiv
	 * alent to \"test/theme\就像编程的{@code javautilResourceBundle}用法一样。
	 * 
	 * 
	 * @see #setBasenames
	 * @see java.util.ResourceBundle#getBundle(String)
	 */
	public void setBasename(String basename) {
		setBasenames(basename);
	}

	/**
	 * Set an array of basenames, each following {@link java.util.ResourceBundle}
	 * conventions. The default is a single basename "views".
	 * <p>{@code ResourceBundle} supports different suffixes. For example,
	 * a base name of "views" might map to {@code ResourceBundle} files
	 * "views", "views_en_au" and "views_de".
	 * <p>The associated resource bundles will be checked sequentially
	 * when resolving a message code. Note that message definitions in a
	 * <i>previous</i> resource bundle will override ones in a later bundle,
	 * due to the sequential lookup.
	 * <p>Note that ResourceBundle names are effectively classpath locations: As a
	 * consequence, the JDK's standard ResourceBundle treats dots as package separators.
	 * This means that "test.theme" is effectively equivalent to "test/theme",
	 * just like it is for programmatic {@code java.util.ResourceBundle} usage.
	 * <p>
	 * 设置一个basenames数组,每个下面的{@link javautilResourceBundle}约定默认是单个basename"views"<p> {@ code ResourceBundle}支
	 * 持不同的后缀例如,基本名称"views"可能映射到{@code ResourceBundle} files"views","views_en_au"和"views_de"<p>在解析消息代码时,相关联的
	 * 资源束将被顺序检查。
	 * 注意,<i>以前的</i>资源束中的消息定义将覆盖后一个bundle,由于顺序查找<p>请注意,ResourceBundle名称实际上是类路径位置：因此,JDK的标准ResourceBundle将点视为
	 * 包分隔符这意味着"testtheme"实际上等效于"test / theme"","a later bundle, due to the sequential lookup <p>Note that R
	 * esourceBundle names are effectively classpath locations: As a consequence, the JDK's standard Resourc
	 * eBundle treats dots as package separators This means that \"testtheme\" is effectively equivalent to 
	 * \"test/theme\就像程序化的{@code javautilResourceBundle}用法。
	 * 
	 * 
	 * @see #setBasename
	 * @see java.util.ResourceBundle#getBundle(String)
	 */
	public void setBasenames(String... basenames) {
		this.basenames = basenames;
	}

	/**
	 * Set the {@link ClassLoader} to load resource bundles with.
	 * Default is the thread context {@code ClassLoader}.
	 * <p>
	 * 设置{@link ClassLoader}加载资源束,其中Default是线程上下文{@code ClassLoader}
	 * 
	 */
	public void setBundleClassLoader(ClassLoader classLoader) {
		this.bundleClassLoader = classLoader;
	}

	/**
	 * Return the {@link ClassLoader} to load resource bundles with.
	 * <p>Default is the specified bundle {@code ClassLoader},
	 * usually the thread context {@code ClassLoader}.
	 * <p>
	 *  返回{@link ClassLoader}以加载资源束与<p>默认是指定的包{@code ClassLoader},通常是线程上下文{@code ClassLoader}
	 * 
	 */
	protected ClassLoader getBundleClassLoader() {
		return this.bundleClassLoader;
	}

	/**
	 * Set the default parent for views defined in the {@code ResourceBundle}.
	 * <p>This avoids repeated "yyy1.(parent)=xxx", "yyy2.(parent)=xxx" definitions
	 * in the bundle, especially if all defined views share the same parent.
	 * <p>The parent will typically define the view class and common attributes.
	 * Concrete views might simply consist of an URL definition then:
	 * a la "yyy1.url=/my.jsp", "yyy2.url=/your.jsp".
	 * <p>View definitions that define their own parent or carry their own
	 * class can still override this. Strictly speaking, the rule that a
	 * default parent setting does not apply to a bean definition that
	 * carries a class is there for backwards compatibility reasons.
	 * It still matches the typical use case.
	 * <p>
	 * 设置在{@code ResourceBundle} <p>中定义的视图的默认父级,这样可以避免捆绑中重复的"yyy1(parent)= xxx","yyy2(parent)= xxx")定义,特别是如果
	 * 所有定义的视图共享相同parent <p>父代通常定义视图类和公共属性具体视图可能只是一个URL定义：a"yyy1url = / myjsp","yyy2url = / yourjsp"<p>查看定义自
	 * 己的定义父或携带自己的类仍然可以覆盖这个严格来说,默认父设置不适用于携带类的bean定义的规则是有向后兼容性原因它仍然符合典型的用例。
	 * 
	 */
	public void setDefaultParentView(String defaultParentView) {
		this.defaultParentView = defaultParentView;
	}

	/**
	 * Specify Locales to initialize eagerly, rather than lazily when actually accessed.
	 * <p>Allows for pre-initialization of common Locales, eagerly checking
	 * the view configuration for those Locales.
	 * <p>
	 * 指定区域设置来进行初始化,而不是在实际访问时懒惰<p>允许预先初始化常见的语言环境,强烈检查这些区域设置的视图配置
	 * 
	 */
	public void setLocalesToInitialize(Locale... localesToInitialize) {
		this.localesToInitialize = localesToInitialize;
	}

	/**
	 * Eagerly initialize Locales if necessary.
	 * <p>
	 *  如有必要,请急于初始化语言环境
	 * 
	 * 
	 * @see #setLocalesToInitialize
	 */
	@Override
	public void afterPropertiesSet() throws BeansException {
		if (this.localesToInitialize != null) {
			for (Locale locale : this.localesToInitialize) {
				initFactory(locale);
			}
		}
	}


	@Override
	protected View loadView(String viewName, Locale locale) throws Exception {
		BeanFactory factory = initFactory(locale);
		try {
			return factory.getBean(viewName, View.class);
		}
		catch (NoSuchBeanDefinitionException ex) {
			// Allow for ViewResolver chaining...
			return null;
		}
	}

	/**
	 * Initialize the View {@link BeanFactory} from the {@code ResourceBundle},
	 * for the given {@link Locale locale}.
	 * <p>Synchronized because of access by parallel threads.
	 * <p>
	 *  从{@code ResourceBundle}初始化视图{@link BeanFactory},对于给定的{@link Locale locale} <p>由于并行线程的访问而同步
	 * 
	 * 
	 * @param locale the target {@code Locale}
	 * @return the View factory for the given Locale
	 * @throws BeansException in case of initialization errors
	 */
	protected synchronized BeanFactory initFactory(Locale locale) throws BeansException {
		// Try to find cached factory for Locale:
		// Have we already encountered that Locale before?
		if (isCache()) {
			BeanFactory cachedFactory = this.localeCache.get(locale);
			if (cachedFactory != null) {
				return cachedFactory;
			}
		}

		// Build list of ResourceBundle references for Locale.
		List<ResourceBundle> bundles = new LinkedList<ResourceBundle>();
		for (String basename : this.basenames) {
			ResourceBundle bundle = getBundle(basename, locale);
			bundles.add(bundle);
		}

		// Try to find cached factory for ResourceBundle list:
		// even if Locale was different, same bundles might have been found.
		if (isCache()) {
			BeanFactory cachedFactory = this.bundleCache.get(bundles);
			if (cachedFactory != null) {
				this.localeCache.put(locale, cachedFactory);
				return cachedFactory;
			}
		}

		// Create child ApplicationContext for views.
		GenericWebApplicationContext factory = new GenericWebApplicationContext();
		factory.setParent(getApplicationContext());
		factory.setServletContext(getServletContext());

		// Load bean definitions from resource bundle.
		PropertiesBeanDefinitionReader reader = new PropertiesBeanDefinitionReader(factory);
		reader.setDefaultParentBean(this.defaultParentView);
		for (ResourceBundle bundle : bundles) {
			reader.registerBeanDefinitions(bundle);
		}

		factory.refresh();

		// Cache factory for both Locale and ResourceBundle list.
		if (isCache()) {
			this.localeCache.put(locale, factory);
			this.bundleCache.put(bundles, factory);
		}

		return factory;
	}

	/**
	 * Obtain the resource bundle for the given basename and {@link Locale}.
	 * <p>
	 *  获取给定基础名称和{@link Locale}的资源包
	 * 
	 * 
	 * @param basename the basename to look for
	 * @param locale the {@code Locale} to look for
	 * @return the corresponding {@code ResourceBundle}
	 * @throws MissingResourceException if no matching bundle could be found
	 * @see java.util.ResourceBundle#getBundle(String, java.util.Locale, ClassLoader)
	 */
	protected ResourceBundle getBundle(String basename, Locale locale) throws MissingResourceException {
		return ResourceBundle.getBundle(basename, locale, getBundleClassLoader());
	}


	/**
	 * Close the bundle View factories on context shutdown.
	 * <p>
	 *  关闭捆绑查看工厂上下文关闭
	 */
	@Override
	public void destroy() throws BeansException {
		for (ConfigurableApplicationContext factory : this.bundleCache.values()) {
			factory.close();
		}
		this.localeCache.clear();
		this.bundleCache.clear();
	}

}
