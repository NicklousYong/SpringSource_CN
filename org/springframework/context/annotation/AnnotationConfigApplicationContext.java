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

package org.springframework.context.annotation;

import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.util.Assert;

/**
 * Standalone application context, accepting annotated classes as input - in particular
 * {@link Configuration @Configuration}-annotated classes, but also plain
 * {@link org.springframework.stereotype.Component @Component} types and JSR-330 compliant
 * classes using {@code javax.inject} annotations. Allows for registering classes one by
 * one using {@link #register(Class...)} as well as for classpath scanning using
 * {@link #scan(String...)}.
 *
 * <p>In case of multiple {@code @Configuration} classes, @{@link Bean} methods defined in
 * later classes will override those defined in earlier classes. This can be leveraged to
 * deliberately override certain bean definitions via an extra {@code @Configuration}
 * class.
 *
 * <p>See @{@link Configuration} Javadoc for usage examples.
 *
 * <p>
 * 独立的应用程序上下文,接受注释类作为输入,特别是{@link Configuration @Configuration}注释类,还包括使用{@code javaxinject}注释的简单{@link orgspringframeworkstereotypeComponent @Component}
 * 类型和JSR-330兼容类允许注册使用{@link #register(Class)}以及使用{@link #scan(String)}进行类路径扫描逐个类。
 * 
 *  <p>在多个{@code @Configuration}类的情况下,@ {@ link Bean}在后面的类中定义的方法将覆盖早期类中定义的方法。这可以通过额外的{@code @配置}类
 * 
 *  <p>有关使用示例,请参阅@ {@ link Configuration} Javadoc
 * 
 * 
 * @author Chris Beams
 * @author Juergen Hoeller
 * @since 3.0
 * @see #register
 * @see #scan
 * @see AnnotatedBeanDefinitionReader
 * @see ClassPathBeanDefinitionScanner
 * @see org.springframework.context.support.GenericXmlApplicationContext
 */
public class AnnotationConfigApplicationContext extends GenericApplicationContext implements AnnotationConfigRegistry {

	private final AnnotatedBeanDefinitionReader reader;

	private final ClassPathBeanDefinitionScanner scanner;


	/**
	 * Create a new AnnotationConfigApplicationContext that needs to be populated
	 * through {@link #register} calls and then manually {@linkplain #refresh refreshed}.
	 * <p>
	 * 创建一个新的AnnotationConfigApplicationContext,需要通过{@link #register}调用填充,然后手动{@linkplain #refresh refreshs}
	 * 。
	 * 
	 */
	public AnnotationConfigApplicationContext() {
		this.reader = new AnnotatedBeanDefinitionReader(this);
		this.scanner = new ClassPathBeanDefinitionScanner(this);
	}

	/**
	 * Create a new AnnotationConfigApplicationContext with the given DefaultListableBeanFactory.
	 * <p>
	 *  使用给定的DefaultListableBeanFactory创建一个新的AnnotationConfigApplicationContext
	 * 
	 * 
	 * @param beanFactory the DefaultListableBeanFactory instance to use for this context
	 */
	public AnnotationConfigApplicationContext(DefaultListableBeanFactory beanFactory) {
		super(beanFactory);
		this.reader = new AnnotatedBeanDefinitionReader(this);
		this.scanner = new ClassPathBeanDefinitionScanner(this);
	}

	/**
	 * Create a new AnnotationConfigApplicationContext, deriving bean definitions
	 * from the given annotated classes and automatically refreshing the context.
	 * <p>
	 *  创建一个新的AnnotationConfigApplicationContext,从给定的注释类中派生bean定义,并自动刷新上下文
	 * 
	 * 
	 * @param annotatedClasses one or more annotated classes,
	 * e.g. {@link Configuration @Configuration} classes
	 */
	public AnnotationConfigApplicationContext(Class<?>... annotatedClasses) {
		this();
		register(annotatedClasses);
		refresh();
	}

	/**
	 * Create a new AnnotationConfigApplicationContext, scanning for bean definitions
	 * in the given packages and automatically refreshing the context.
	 * <p>
	 *  创建一个新的AnnotationConfigApplicationContext,扫描给定包中的bean定义并自动刷新上下文
	 * 
	 * 
	 * @param basePackages the packages to check for annotated classes
	 */
	public AnnotationConfigApplicationContext(String... basePackages) {
		this();
		scan(basePackages);
		refresh();
	}


	/**
	 * {@inheritDoc}
	 * <p>Delegates given environment to underlying {@link AnnotatedBeanDefinitionReader}
	 * and {@link ClassPathBeanDefinitionScanner} members.
	 * <p>
	 *  {@inheritDoc} <p>将给定环境委派给底层的{@link AnnotatedBeanDefinitionReader}和{@link ClassPathBeanDefinitionScanner}
	 * 成员。
	 * 
	 */
	@Override
	public void setEnvironment(ConfigurableEnvironment environment) {
		super.setEnvironment(environment);
		this.reader.setEnvironment(environment);
		this.scanner.setEnvironment(environment);
	}

	/**
	 * Provide a custom {@link BeanNameGenerator} for use with {@link AnnotatedBeanDefinitionReader}
	 * and/or {@link ClassPathBeanDefinitionScanner}, if any.
	 * <p>Default is {@link org.springframework.context.annotation.AnnotationBeanNameGenerator}.
	 * <p>Any call to this method must occur prior to calls to {@link #register(Class...)}
	 * and/or {@link #scan(String...)}.
	 * <p>
	 * 提供一个用于{@link AnnotatedBeanDefinitionReader}和/或{@link ClassPathBeanDefinitionScanner}的自定义{@link BeanNameGenerator}
	 * ,如果有任何<p>默认是{@link orgspringframeworkcontextannotationAnnotationBeanNameGenerator} <p>任何对此方法的调用必须在调用之
	 * 前进行到{@link #register(Class)}和/或{@link #scan(String)}。
	 * 
	 * 
	 * @see AnnotatedBeanDefinitionReader#setBeanNameGenerator
	 * @see ClassPathBeanDefinitionScanner#setBeanNameGenerator
	 */
	public void setBeanNameGenerator(BeanNameGenerator beanNameGenerator) {
		this.reader.setBeanNameGenerator(beanNameGenerator);
		this.scanner.setBeanNameGenerator(beanNameGenerator);
		getBeanFactory().registerSingleton(
				AnnotationConfigUtils.CONFIGURATION_BEAN_NAME_GENERATOR, beanNameGenerator);
	}

	/**
	 * Set the {@link ScopeMetadataResolver} to use for detected bean classes.
	 * <p>The default is an {@link AnnotationScopeMetadataResolver}.
	 * <p>Any call to this method must occur prior to calls to {@link #register(Class...)}
	 * and/or {@link #scan(String...)}.
	 * <p>
	 *  将{@link ScopeMetadataResolver}设置为用于检测到的bean类<p>默认值为{@link AnnotationScopeMetadataResolver} <p>任何调用此方
	 * 法必须在调用{@link #register(Class)}和/或{@link #scan(String)}。
	 * 
	 */
	public void setScopeMetadataResolver(ScopeMetadataResolver scopeMetadataResolver) {
		this.reader.setScopeMetadataResolver(scopeMetadataResolver);
		this.scanner.setScopeMetadataResolver(scopeMetadataResolver);
	}


	/**
	 * Register one or more annotated classes to be processed.
	 * <p>Note that {@link #refresh()} must be called in order for the context
	 * to fully process the new classes.
	 * <p>
	 *  注册要处理的一个或多个注释类<p>请注意,必须调用{@link #refresh()}才能使上下文完全处理新类
	 * 
	 * 
	 * @param annotatedClasses one or more annotated classes,
	 * e.g. {@link Configuration @Configuration} classes
	 * @see #scan(String...)
	 * @see #refresh()
	 */
	public void register(Class<?>... annotatedClasses) {
		Assert.notEmpty(annotatedClasses, "At least one annotated class must be specified");
		this.reader.register(annotatedClasses);
	}

	/**
	 * Perform a scan within the specified base packages.
	 * <p>Note that {@link #refresh()} must be called in order for the context
	 * to fully process the new classes.
	 * <p>
	 * 在指定的基本包中执行扫描<p>请注意,必须调用{@link #refresh()}才能使上下文完全处理新类
	 * 
	 * @param basePackages the packages to check for annotated classes
	 * @see #register(Class...)
	 * @see #refresh()
	 */
	public void scan(String... basePackages) {
		Assert.notEmpty(basePackages, "At least one base package must be specified");
		this.scanner.scan(basePackages);
	}


	@Override
	protected void prepareRefresh() {
		this.scanner.clearCache();
		super.prepareRefresh();
	}

}
