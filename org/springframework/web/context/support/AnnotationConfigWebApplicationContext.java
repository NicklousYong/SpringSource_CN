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

package org.springframework.web.context.support;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.AnnotatedBeanDefinitionReader;
import org.springframework.context.annotation.AnnotationConfigRegistry;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.ScopeMetadataResolver;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.context.ContextLoader;

/**
 * {@link org.springframework.web.context.WebApplicationContext WebApplicationContext}
 * implementation which accepts annotated classes as input - in particular
 * {@link org.springframework.context.annotation.Configuration @Configuration}-annotated
 * classes, but also plain {@link org.springframework.stereotype.Component @Component}
 * classes and JSR-330 compliant classes using {@code javax.inject} annotations. Allows
 * for registering classes one by one (specifying class names as config location) as well
 * as for classpath scanning (specifying base packages as config location).
 *
 * <p>This is essentially the equivalent of
 * {@link org.springframework.context.annotation.AnnotationConfigApplicationContext
 * AnnotationConfigApplicationContext} for a web environment.
 *
 * <p>To make use of this application context, the
 * {@linkplain ContextLoader#CONTEXT_CLASS_PARAM "contextClass"} context-param for
 * ContextLoader and/or "contextClass" init-param for FrameworkServlet must be set to
 * the fully-qualified name of this class.
 *
 * <p>As of Spring 3.1, this class may also be directly instantiated and injected into
 * Spring's {@code DispatcherServlet} or {@code ContextLoaderListener} when using the
 * new {@link org.springframework.web.WebApplicationInitializer WebApplicationInitializer}
 * code-based alternative to {@code web.xml}. See its Javadoc for details and usage examples.
 *
 * <p>Unlike {@link XmlWebApplicationContext}, no default configuration class locations
 * are assumed. Rather, it is a requirement to set the
 * {@linkplain ContextLoader#CONFIG_LOCATION_PARAM "contextConfigLocation"}
 * context-param for {@link ContextLoader} and/or "contextConfigLocation" init-param for
 * FrameworkServlet.  The param-value may contain both fully-qualified
 * class names and base packages to scan for components. See {@link #loadBeanDefinitions}
 * for exact details on how these locations are processed.
 *
 * <p>As an alternative to setting the "contextConfigLocation" parameter, users may
 * implement an {@link org.springframework.context.ApplicationContextInitializer
 * ApplicationContextInitializer} and set the
 * {@linkplain ContextLoader#CONTEXT_INITIALIZER_CLASSES_PARAM "contextInitializerClasses"}
 * context-param / init-param. In such cases, users should favor the {@link #refresh()}
 * and {@link #scan(String...)} methods over the {@link #setConfigLocation(String)}
 * method, which is primarily for use by {@code ContextLoader}.
 *
 * <p>Note: In case of multiple {@code @Configuration} classes, later {@code @Bean}
 * definitions will override ones defined in earlier loaded files. This can be leveraged
 * to deliberately override certain bean definitions via an extra Configuration class.
 *
 * <p>
 * {@link orgspringframeworkwebcontextWebApplicationContext WebApplicationContext}实现,它接受注释类作为输入 - 特别是{@link orgspringframeworkcontextannotationConfiguration @Configuration}
 * 注释类,而且还使用{@code javaxinject}的简单{@link orgspringframeworkstereotypeComponent @Component}类和JSR-330兼容类注释
 * 允许逐个注册类(将类名称指定为配置位置)以及类路径扫描(将基本包指定为配置位置)。
 * 
 *  <p>这实际上是相当于Web环境的{@link orgspringframeworkcontextannotationAnnotationConfigApplicationContext AnnotationConfigApplicationContext}
 * 。
 * 
 * <p>要使用此应用程序上下文,必须将FrameworkServlet的ContextLoader和/或"contextClass"init-param的上下文参数的上下文参数设置为此类的完全限定名称的{@linkplain ContextLoader#CONTEXT_CLASS_PARAM"contextClass"}
 * 。
 * 
 *  <p>截至Spring 31,当使用新的{@link orgspringframeworkwebWebApplicationInitializer WebApplicationInitializer}
 * 代码替代{@code webxml}时,此类也可以直接实例化并注入Spring的{@code DispatcherServlet}或{@code ContextLoaderListener}有关详细信息
 * 和使用示例,请参见其Javadoc。
 * 
 * <p>与{@link XmlWebApplicationContext}不同,不假定默认的配置类位置相反,需要为{@link ContextLoader}和/或"contextConfigLocatio
 * n设置{@linkplain ContextLoader#CONFIG_LOCATION_PARAM"contextConfigLocation"}上下文参数"FrameworkServlet的init
 * -param param-value可能包含完全限定类名和基础包来扫描组件有关如何处理这些位置的详细信息,请参阅{@link #loadBeanDefinitions}。
 * 
 * <p>作为设置"contextConfigLocation"参数的替代方法,用户可以实现一个{@link orgspringframeworkcontextApplicationContextInitializer ApplicationContextInitializer}
 * 并设置{@linkplain ContextLoader#CONTEXT_INITIALIZER_CLASSES_PARAM"contextInitializerClasses"} context-pa
 * ram / init-param在这种情况下,用户应该通过{@link #setConfigLocation(String)}方法支持{@link #refresh()}和{@link #scan(String)}
 * 方法,该方法主要由{@code ContextLoader}。
 * 
 *  注意：如果有多个{@code @Configuration}类,以后的{@code @Bean}定义将覆盖早期加载文件中定义的定义。可以利用这些定义,通过一个额外的配置类故意覆盖某些bean定义
 * 
 * 
 * @author Chris Beams
 * @author Juergen Hoeller
 * @since 3.0
 * @see org.springframework.context.annotation.AnnotationConfigApplicationContext
 */
public class AnnotationConfigWebApplicationContext extends AbstractRefreshableWebApplicationContext
		implements AnnotationConfigRegistry {

	private BeanNameGenerator beanNameGenerator;

	private ScopeMetadataResolver scopeMetadataResolver;

	private final Set<Class<?>> annotatedClasses = new LinkedHashSet<Class<?>>();

	private final Set<String> basePackages = new LinkedHashSet<String>();


	/**
	 * Set a custom {@link BeanNameGenerator} for use with {@link AnnotatedBeanDefinitionReader}
	 * and/or {@link ClassPathBeanDefinitionScanner}.
	 * <p>Default is {@link org.springframework.context.annotation.AnnotationBeanNameGenerator}.
	 * <p>
	 * 设置一个用于{@link AnnotatedBeanDefinitionReader}和/或{@link ClassPathBeanDefinitionScanner}的自定义{@link BeanNameGenerator}
	 *  <p>默认是{@link orgspringframeworkcontextannotationAnnotationBeanNameGenerator}。
	 * 
	 * 
	 * @see AnnotatedBeanDefinitionReader#setBeanNameGenerator
	 * @see ClassPathBeanDefinitionScanner#setBeanNameGenerator
	 */
	public void setBeanNameGenerator(BeanNameGenerator beanNameGenerator) {
		this.beanNameGenerator = beanNameGenerator;
	}

	/**
	 * Return the custom {@link BeanNameGenerator} for use with {@link AnnotatedBeanDefinitionReader}
	 * and/or {@link ClassPathBeanDefinitionScanner}, if any.
	 * <p>
	 *  返回用于{@link AnnotatedBeanDefinitionReader}和/或{@link ClassPathBeanDefinitionScanner}的自定义{@link BeanNameGenerator}
	 * (如果有)。
	 * 
	 */
	protected BeanNameGenerator getBeanNameGenerator() {
		return this.beanNameGenerator;
	}

	/**
	 * Set a custom {@link ScopeMetadataResolver} for use with {@link AnnotatedBeanDefinitionReader}
	 * and/or {@link ClassPathBeanDefinitionScanner}.
	 * <p>Default is an {@link org.springframework.context.annotation.AnnotationScopeMetadataResolver}.
	 * <p>
	 *  设置与{@link AnnotatedBeanDefinitionReader}和/或{@link ClassPathBeanDefinitionScanner}一起使用的自定义{@link ScopeMetadataResolver}
	 *  <p>默认是{@link orgspringframeworkcontextannotationAnnotationScopeMetadataResolver}。
	 * 
	 * 
	 * @see AnnotatedBeanDefinitionReader#setScopeMetadataResolver
	 * @see ClassPathBeanDefinitionScanner#setScopeMetadataResolver
	 */
	public void setScopeMetadataResolver(ScopeMetadataResolver scopeMetadataResolver) {
		this.scopeMetadataResolver = scopeMetadataResolver;
	}

	/**
	 * Return the custom {@link ScopeMetadataResolver} for use with {@link AnnotatedBeanDefinitionReader}
	 * and/or {@link ClassPathBeanDefinitionScanner}, if any.
	 * <p>
	 *  返回用于{@link AnnotatedBeanDefinitionReader}和/或{@link ClassPathBeanDefinitionScanner}的自定义{@link ScopeMetadataResolver}
	 * (如果有)。
	 * 
	 */
	protected ScopeMetadataResolver getScopeMetadataResolver() {
		return this.scopeMetadataResolver;
	}


	/**
	 * Register one or more annotated classes to be processed.
	 * <p>Note that {@link #refresh()} must be called in order for the context
	 * to fully process the new classes.
	 * <p>
	 * 注册要处理的一个或多个注释类<p>请注意,必须调用{@link #refresh()}才能使上下文完全处理新类
	 * 
	 * 
	 * @param annotatedClasses one or more annotated classes,
	 * e.g. {@link org.springframework.context.annotation.Configuration @Configuration} classes
	 * @see #scan(String...)
	 * @see #loadBeanDefinitions(DefaultListableBeanFactory)
	 * @see #setConfigLocation(String)
	 * @see #refresh()
	 */
	public void register(Class<?>... annotatedClasses) {
		Assert.notEmpty(annotatedClasses, "At least one annotated class must be specified");
		this.annotatedClasses.addAll(Arrays.asList(annotatedClasses));
	}

	/**
	 * Perform a scan within the specified base packages.
	 * <p>Note that {@link #refresh()} must be called in order for the context
	 * to fully process the new classes.
	 * <p>
	 *  在指定的基本包中执行扫描<p>请注意,必须调用{@link #refresh()}才能使上下文完全处理新类
	 * 
	 * 
	 * @param basePackages the packages to check for annotated classes
	 * @see #loadBeanDefinitions(DefaultListableBeanFactory)
	 * @see #register(Class...)
	 * @see #setConfigLocation(String)
	 * @see #refresh()
	 */
	public void scan(String... basePackages) {
		Assert.notEmpty(basePackages, "At least one base package must be specified");
		this.basePackages.addAll(Arrays.asList(basePackages));
	}


	/**
	 * Register a {@link org.springframework.beans.factory.config.BeanDefinition} for
	 * any classes specified by {@link #register(Class...)} and scan any packages
	 * specified by {@link #scan(String...)}.
	 * <p>For any values specified by {@link #setConfigLocation(String)} or
	 * {@link #setConfigLocations(String[])}, attempt first to load each location as a
	 * class, registering a {@code BeanDefinition} if class loading is successful,
	 * and if class loading fails (i.e. a {@code ClassNotFoundException} is raised),
	 * assume the value is a package and attempt to scan it for annotated classes.
	 * <p>Enables the default set of annotation configuration post processors, such that
	 * {@code @Autowired}, {@code @Required}, and associated annotations can be used.
	 * <p>Configuration class bean definitions are registered with generated bean
	 * definition names unless the {@code value} attribute is provided to the stereotype
	 * annotation.
	 * <p>
	 * 为{@link #register(Class)}指定的任何类注册一个{@link orgspringframeworkbeansfactoryconfigBeanDefinition}并扫描{@link #scan(String)}
	 * 指定的任何包<p>对于{@link #setConfigLocation( String)}或{@link #setConfigLocations(String [])},尝试首先将每个位置加载为一个类
	 * ,如果类加载成功,则注册{@code BeanDefinition},并且如果类加载失败(即{@code ClassNotFoundException}),假设该值是一个包,并尝试将其扫描为带注释的类<p>
	 * 启用默认的注释配置后处理器集,使{@code @Autowired},{@code @Required}和可以使用相关的注释<p>配置类bean定义是使用生成的bean定义名称注册的,除非{@code value}
	 * 属性提供给原型注释。
	 * 
	 * 
	 * @param beanFactory the bean factory to load bean definitions into
	 * @see #register(Class...)
	 * @see #scan(String...)
	 * @see #setConfigLocation(String)
	 * @see #setConfigLocations(String[])
	 * @see AnnotatedBeanDefinitionReader
	 * @see ClassPathBeanDefinitionScanner
	 */
	@Override
	protected void loadBeanDefinitions(DefaultListableBeanFactory beanFactory) {
		AnnotatedBeanDefinitionReader reader = getAnnotatedBeanDefinitionReader(beanFactory);
		ClassPathBeanDefinitionScanner scanner = getClassPathBeanDefinitionScanner(beanFactory);

		BeanNameGenerator beanNameGenerator = getBeanNameGenerator();
		if (beanNameGenerator != null) {
			reader.setBeanNameGenerator(beanNameGenerator);
			scanner.setBeanNameGenerator(beanNameGenerator);
			beanFactory.registerSingleton(AnnotationConfigUtils.CONFIGURATION_BEAN_NAME_GENERATOR, beanNameGenerator);
		}

		ScopeMetadataResolver scopeMetadataResolver = getScopeMetadataResolver();
		if (scopeMetadataResolver != null) {
			reader.setScopeMetadataResolver(scopeMetadataResolver);
			scanner.setScopeMetadataResolver(scopeMetadataResolver);
		}

		if (!this.annotatedClasses.isEmpty()) {
			if (logger.isInfoEnabled()) {
				logger.info("Registering annotated classes: [" +
						StringUtils.collectionToCommaDelimitedString(this.annotatedClasses) + "]");
			}
			reader.register(this.annotatedClasses.toArray(new Class<?>[this.annotatedClasses.size()]));
		}

		if (!this.basePackages.isEmpty()) {
			if (logger.isInfoEnabled()) {
				logger.info("Scanning base packages: [" +
						StringUtils.collectionToCommaDelimitedString(this.basePackages) + "]");
			}
			scanner.scan(this.basePackages.toArray(new String[this.basePackages.size()]));
		}

		String[] configLocations = getConfigLocations();
		if (configLocations != null) {
			for (String configLocation : configLocations) {
				try {
					Class<?> clazz = getClassLoader().loadClass(configLocation);
					if (logger.isInfoEnabled()) {
						logger.info("Successfully resolved class for [" + configLocation + "]");
					}
					reader.register(clazz);
				}
				catch (ClassNotFoundException ex) {
					if (logger.isDebugEnabled()) {
						logger.debug("Could not load class for config location [" + configLocation +
								"] - trying package scan. " + ex);
					}
					int count = scanner.scan(configLocation);
					if (logger.isInfoEnabled()) {
						if (count == 0) {
							logger.info("No annotated classes found for specified class/package [" + configLocation + "]");
						}
						else {
							logger.info("Found " + count + " annotated classes in package [" + configLocation + "]");
						}
					}
				}
			}
		}
	}


	/**
	 * Build an {@link AnnotatedBeanDefinitionReader} for the given bean factory.
	 * <p>This should be pre-configured with the {@code Environment} (if desired)
	 * but not with a {@code BeanNameGenerator} or {@code ScopeMetadataResolver} yet.
	 * <p>
	 * 为给定的bean工厂构建一个{@link AnnotatedBeanDefinitionReader} <p>应该使用{@code Environment}(如果需要)预配置,而不是使用{@code BeanNameGenerator}
	 * 或{@code ScopeMetadataResolver}。
	 * 
	 * 
	 * @param beanFactory the bean factory to load bean definitions into
	 * @since 4.1.9
	 * @see #getEnvironment()
	 * @see #getBeanNameGenerator()
	 * @see #getScopeMetadataResolver()
	 */
	protected AnnotatedBeanDefinitionReader getAnnotatedBeanDefinitionReader(DefaultListableBeanFactory beanFactory) {
		return new AnnotatedBeanDefinitionReader(beanFactory, getEnvironment());
	}

	/**
	 * Build a {@link ClassPathBeanDefinitionScanner} for the given bean factory.
	 * <p>This should be pre-configured with the {@code Environment} (if desired)
	 * but not with a {@code BeanNameGenerator} or {@code ScopeMetadataResolver} yet.
	 * <p>
	 *  为给定的bean工厂构建一个{@link ClassPathBeanDefinitionScanner} <p>应该使用{@code Environment}(如果需要)预先配置,而不是使用{@code BeanNameGenerator}
	 * 或{@code ScopeMetadataResolver}。
	 * 
	 * @param beanFactory the bean factory to load bean definitions into
	 * @since 4.1.9
	 * @see #getEnvironment()
	 * @see #getBeanNameGenerator()
	 * @see #getScopeMetadataResolver()
	 */
	protected ClassPathBeanDefinitionScanner getClassPathBeanDefinitionScanner(DefaultListableBeanFactory beanFactory) {
		return new ClassPathBeanDefinitionScanner(beanFactory, true, getEnvironment());
	}

}
