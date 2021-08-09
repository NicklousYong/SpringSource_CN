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

import java.util.LinkedHashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionDefaults;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.core.env.Environment;
import org.springframework.core.env.EnvironmentCapable;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.Assert;
import org.springframework.util.PatternMatchUtils;

/**
 * A bean definition scanner that detects bean candidates on the classpath,
 * registering corresponding bean definitions with a given registry ({@code BeanFactory}
 * or {@code ApplicationContext}).
 *
 * <p>Candidate classes are detected through configurable type filters. The
 * default filters include classes that are annotated with Spring's
 * {@link org.springframework.stereotype.Component @Component},
 * {@link org.springframework.stereotype.Repository @Repository},
 * {@link org.springframework.stereotype.Service @Service}, or
 * {@link org.springframework.stereotype.Controller @Controller} stereotype.
 *
 * <p>Also supports Java EE 6's {@link javax.annotation.ManagedBean} and
 * JSR-330's {@link javax.inject.Named} annotations, if available.
 *
 * <p>
 *  一个bean定义扫描器,用于检测类路径上的bean候选项,使用给定的注册表({@code BeanFactory}或{@code ApplicationContext})注册相应的bean定义)
 * 
 * <p>通过可配置的类型过滤器检测候选类默认过滤器包括使用Spring的{@link orgspringframeworkstereotypeComponent @Component},{@link orgspringframeworkstereotypeRepository @Repository}
 * ,{@link orgspringframeworkstereotypeService @Service}或{@link orgspringframeworkstereotypeController @Controller}
 * 构造型。
 * 
 *  <p>还支持Java EE 6的{@link javaxannotationManagedBean}和JSR-330的{@link javaxinjectNamed}注释(如果可用)
 * 
 * 
 * @author Mark Fisher
 * @author Juergen Hoeller
 * @author Chris Beams
 * @since 2.5
 * @see AnnotationConfigApplicationContext#scan
 * @see org.springframework.stereotype.Component
 * @see org.springframework.stereotype.Repository
 * @see org.springframework.stereotype.Service
 * @see org.springframework.stereotype.Controller
 */
public class ClassPathBeanDefinitionScanner extends ClassPathScanningCandidateComponentProvider {

	private final BeanDefinitionRegistry registry;

	private BeanDefinitionDefaults beanDefinitionDefaults = new BeanDefinitionDefaults();

	private String[] autowireCandidatePatterns;

	private BeanNameGenerator beanNameGenerator = new AnnotationBeanNameGenerator();

	private ScopeMetadataResolver scopeMetadataResolver = new AnnotationScopeMetadataResolver();

	private boolean includeAnnotationConfig = true;


	/**
	 * Create a new {@code ClassPathBeanDefinitionScanner} for the given bean factory.
	 * <p>
	 *  为给定的bean工厂创建一个新的{@code ClassPathBeanDefinitionScanner}
	 * 
	 * 
	 * @param registry the {@code BeanFactory} to load bean definitions into, in the form
	 * of a {@code BeanDefinitionRegistry}
	 */
	public ClassPathBeanDefinitionScanner(BeanDefinitionRegistry registry) {
		this(registry, true);
	}

	/**
	 * Create a new {@code ClassPathBeanDefinitionScanner} for the given bean factory.
	 * <p>If the passed-in bean factory does not only implement the
	 * {@code BeanDefinitionRegistry} interface but also the {@code ResourceLoader}
	 * interface, it will be used as default {@code ResourceLoader} as well. This will
	 * usually be the case for {@link org.springframework.context.ApplicationContext}
	 * implementations.
	 * <p>If given a plain {@code BeanDefinitionRegistry}, the default {@code ResourceLoader}
	 * will be a {@link org.springframework.core.io.support.PathMatchingResourcePatternResolver}.
	 * <p>If the passed-in bean factory also implements {@link EnvironmentCapable} its
	 * environment will be used by this reader.  Otherwise, the reader will initialize and
	 * use a {@link org.springframework.core.env.StandardEnvironment}. All
	 * {@code ApplicationContext} implementations are {@code EnvironmentCapable}, while
	 * normal {@code BeanFactory} implementations are not.
	 * <p>
	 * 为给定的bean工厂创建一个新的{@code ClassPathBeanDefinitionScanner} <p>如果传入的bean工厂不仅实现{@code BeanDefinitionRegistry}
	 * 接口,而且还使用{@code ResourceLoader}接口,它将被用作默认{@code ResourceLoader}以及{@link orgspringframeworkcontextApplicationContext}
	 * 实现通常情况<p>如果给出了一个简单的{@code BeanDefinitionRegistry},默认的{@code ResourceLoader}将是{@link orgspringframeworkcoreiosupportPathMatchingResourcePatternResolver}
	 *  <p >如果传入的bean工厂也实现了{@link EnvironmentCapable},它的环境将被读者使用。
	 * 否则,读者将初始化并使用{@link orgspringframeworkcoreenvStandardEnvironment}所有{@code ApplicationContext}实现都是{@code EnvironmentCapable}
	 * ,而正常的{@code BeanFactory}实现不是。
	 * 
	 * 
	 * @param registry the {@code BeanFactory} to load bean definitions into, in the form
	 * of a {@code BeanDefinitionRegistry}
	 * @param useDefaultFilters whether to include the default filters for the
	 * {@link org.springframework.stereotype.Component @Component},
	 * {@link org.springframework.stereotype.Repository @Repository},
	 * {@link org.springframework.stereotype.Service @Service}, and
	 * {@link org.springframework.stereotype.Controller @Controller} stereotype annotations
	 * @see #setResourceLoader
	 * @see #setEnvironment
	 */
	public ClassPathBeanDefinitionScanner(BeanDefinitionRegistry registry, boolean useDefaultFilters) {
		this(registry, useDefaultFilters, getOrCreateEnvironment(registry));
	}

	/**
	 * Create a new {@code ClassPathBeanDefinitionScanner} for the given bean factory and
	 * using the given {@link Environment} when evaluating bean definition profile metadata.
	 * <p>If the passed-in bean factory does not only implement the {@code
	 * BeanDefinitionRegistry} interface but also the {@link ResourceLoader} interface, it
	 * will be used as default {@code ResourceLoader} as well. This will usually be the
	 * case for {@link org.springframework.context.ApplicationContext} implementations.
	 * <p>If given a plain {@code BeanDefinitionRegistry}, the default {@code ResourceLoader}
	 * will be a {@link org.springframework.core.io.support.PathMatchingResourcePatternResolver}.
	 * <p>
	 * 在给定的bean工厂创建一个新的{@code ClassPathBeanDefinitionScanner},并在评估bean定义配置文件元数据时使用给定的{@link Environment} <p>
	 * 如果传入的bean工厂不仅实现{@code BeanDefinitionRegistry}接口,还有{@link ResourceLoader}接口,它将被用作默认的{@code ResourceLoader}
	 *  {@link orgspringframeworkcontextApplicationContext}实现通常是这样的。
	 * 如果给出一个简单的{@code BeanDefinitionRegistry},默认{@code ResourceLoader}将是{@link orgspringframeworkcoreiosupportPathMatchingResourcePatternResolver}
	 * 。
	 * 
	 * 
	 * @param registry the {@code BeanFactory} to load bean definitions into, in the form
	 * of a {@code BeanDefinitionRegistry}
	 * @param useDefaultFilters whether to include the default filters for the
	 * {@link org.springframework.stereotype.Component @Component},
	 * {@link org.springframework.stereotype.Repository @Repository},
	 * {@link org.springframework.stereotype.Service @Service}, and
	 * {@link org.springframework.stereotype.Controller @Controller} stereotype annotations
	 * @param environment the Spring {@link Environment} to use when evaluating bean
	 * definition profile metadata
	 * @since 3.1
	 * @see #setResourceLoader
	 */
	public ClassPathBeanDefinitionScanner(BeanDefinitionRegistry registry, boolean useDefaultFilters, Environment environment) {
		super(useDefaultFilters, environment);

		Assert.notNull(registry, "BeanDefinitionRegistry must not be null");
		this.registry = registry;

		// Determine ResourceLoader to use.
		if (this.registry instanceof ResourceLoader) {
			setResourceLoader((ResourceLoader) this.registry);
		}
	}


	/**
	 * Return the BeanDefinitionRegistry that this scanner operates on.
	 * <p>
	 *  返回此扫描仪操作的BeanDefinitionRegistry
	 * 
	 */
	public final BeanDefinitionRegistry getRegistry() {
		return this.registry;
	}

	/**
	 * Set the defaults to use for detected beans.
	 * <p>
	 *  设置用于检测到的bean的默认值
	 * 
	 * 
	 * @see BeanDefinitionDefaults
	 */
	public void setBeanDefinitionDefaults(BeanDefinitionDefaults beanDefinitionDefaults) {
		this.beanDefinitionDefaults =
				(beanDefinitionDefaults != null ? beanDefinitionDefaults : new BeanDefinitionDefaults());
	}

	/**
	 * Return the defaults to use for detected beans (never {@code null}).
	 * <p>
	 * 返回默认值用于检测到的bean(从不{@code null})
	 * 
	 * 
	 * @since 4.1
	 */
	public BeanDefinitionDefaults getBeanDefinitionDefaults() {
		return this.beanDefinitionDefaults;
	}

	/**
	 * Set the name-matching patterns for determining autowire candidates.
	 * <p>
	 *  设置用于确定自动线路候选人的名称匹配模式
	 * 
	 * 
	 * @param autowireCandidatePatterns the patterns to match against
	 */
	public void setAutowireCandidatePatterns(String... autowireCandidatePatterns) {
		this.autowireCandidatePatterns = autowireCandidatePatterns;
	}

	/**
	 * Set the BeanNameGenerator to use for detected bean classes.
	 * <p>Default is a {@link AnnotationBeanNameGenerator}.
	 * <p>
	 *  将BeanNameGenerator设置为用于检测到的bean类<p>默认值为{@link AnnotationBeanNameGenerator}
	 * 
	 */
	public void setBeanNameGenerator(BeanNameGenerator beanNameGenerator) {
		this.beanNameGenerator = (beanNameGenerator != null ? beanNameGenerator : new AnnotationBeanNameGenerator());
	}

	/**
	 * Set the ScopeMetadataResolver to use for detected bean classes.
	 * Note that this will override any custom "scopedProxyMode" setting.
	 * <p>The default is an {@link AnnotationScopeMetadataResolver}.
	 * <p>
	 *  将ScopeMetadataResolver设置为检测到的bean类注意,这将覆盖任何自定义的"scopedProxyMode"设置<p>默认值为{@link AnnotationScopeMetadataResolver}
	 * 。
	 * 
	 * 
	 * @see #setScopedProxyMode
	 */
	public void setScopeMetadataResolver(ScopeMetadataResolver scopeMetadataResolver) {
		this.scopeMetadataResolver = (scopeMetadataResolver != null ? scopeMetadataResolver : new AnnotationScopeMetadataResolver());
	}

	/**
	 * Specify the proxy behavior for non-singleton scoped beans.
	 * Note that this will override any custom "scopeMetadataResolver" setting.
	 * <p>The default is {@link ScopedProxyMode#NO}.
	 * <p>
	 *  指定非单例范围bean的代理行为请注意,这将覆盖任何自定义"scopeMetadataResolver"设置<p>默认值为{@link ScopedProxyMode#NO}
	 * 
	 * 
	 * @see #setScopeMetadataResolver
	 */
	public void setScopedProxyMode(ScopedProxyMode scopedProxyMode) {
		this.scopeMetadataResolver = new AnnotationScopeMetadataResolver(scopedProxyMode);
	}

	/**
	 * Specify whether to register annotation config post-processors.
	 * <p>The default is to register the post-processors. Turn this off
	 * to be able to ignore the annotations or to process them differently.
	 * <p>
	 * 指定是否注册注释配置后处理器<p>默认是注册后处理器关闭它可以忽略注释或对它们进行不同的处理
	 * 
	 */
	public void setIncludeAnnotationConfig(boolean includeAnnotationConfig) {
		this.includeAnnotationConfig = includeAnnotationConfig;
	}


	/**
	 * Perform a scan within the specified base packages.
	 * <p>
	 *  在指定的基本包中执行扫描
	 * 
	 * 
	 * @param basePackages the packages to check for annotated classes
	 * @return number of beans registered
	 */
	public int scan(String... basePackages) {
		int beanCountAtScanStart = this.registry.getBeanDefinitionCount();

		doScan(basePackages);

		// Register annotation config processors, if necessary.
		if (this.includeAnnotationConfig) {
			AnnotationConfigUtils.registerAnnotationConfigProcessors(this.registry);
		}

		return (this.registry.getBeanDefinitionCount() - beanCountAtScanStart);
	}

	/**
	 * Perform a scan within the specified base packages,
	 * returning the registered bean definitions.
	 * <p>This method does <i>not</i> register an annotation config processor
	 * but rather leaves this up to the caller.
	 * <p>
	 *  在指定的基本包中执行扫描,返回注册的bean定义<p>此方法<i>不</i>注册注释配置处理器,而是将其留给调用者
	 * 
	 * 
	 * @param basePackages the packages to check for annotated classes
	 * @return set of beans registered if any for tooling registration purposes (never {@code null})
	 */
	protected Set<BeanDefinitionHolder> doScan(String... basePackages) {
		Assert.notEmpty(basePackages, "At least one base package must be specified");
		Set<BeanDefinitionHolder> beanDefinitions = new LinkedHashSet<BeanDefinitionHolder>();
		for (String basePackage : basePackages) {
			Set<BeanDefinition> candidates = findCandidateComponents(basePackage);
			for (BeanDefinition candidate : candidates) {
				ScopeMetadata scopeMetadata = this.scopeMetadataResolver.resolveScopeMetadata(candidate);
				candidate.setScope(scopeMetadata.getScopeName());
				String beanName = this.beanNameGenerator.generateBeanName(candidate, this.registry);
				if (candidate instanceof AbstractBeanDefinition) {
					postProcessBeanDefinition((AbstractBeanDefinition) candidate, beanName);
				}
				if (candidate instanceof AnnotatedBeanDefinition) {
					AnnotationConfigUtils.processCommonDefinitionAnnotations((AnnotatedBeanDefinition) candidate);
				}
				if (checkCandidate(beanName, candidate)) {
					BeanDefinitionHolder definitionHolder = new BeanDefinitionHolder(candidate, beanName);
					definitionHolder = AnnotationConfigUtils.applyScopedProxyMode(scopeMetadata, definitionHolder, this.registry);
					beanDefinitions.add(definitionHolder);
					registerBeanDefinition(definitionHolder, this.registry);
				}
			}
		}
		return beanDefinitions;
	}

	/**
	 * Apply further settings to the given bean definition,
	 * beyond the contents retrieved from scanning the component class.
	 * <p>
	 *  对给定的bean定义应用进一步的设置,超出扫描组件类的内容
	 * 
	 * 
	 * @param beanDefinition the scanned bean definition
	 * @param beanName the generated bean name for the given bean
	 */
	protected void postProcessBeanDefinition(AbstractBeanDefinition beanDefinition, String beanName) {
		beanDefinition.applyDefaults(this.beanDefinitionDefaults);
		if (this.autowireCandidatePatterns != null) {
			beanDefinition.setAutowireCandidate(PatternMatchUtils.simpleMatch(this.autowireCandidatePatterns, beanName));
		}
	}

	/**
	 * Register the specified bean with the given registry.
	 * <p>Can be overridden in subclasses, e.g. to adapt the registration
	 * process or to register further bean definitions for each scanned bean.
	 * <p>
	 *  使用给定的注册表注册指定的bean <p>可以在子类中覆盖,例如,为了适应注册过程或为每个扫描的bean注册更多的bean定义
	 * 
	 * 
	 * @param definitionHolder the bean definition plus bean name for the bean
	 * @param registry the BeanDefinitionRegistry to register the bean with
	 */
	protected void registerBeanDefinition(BeanDefinitionHolder definitionHolder, BeanDefinitionRegistry registry) {
		BeanDefinitionReaderUtils.registerBeanDefinition(definitionHolder, registry);
	}


	/**
	 * Check the given candidate's bean name, determining whether the corresponding
	 * bean definition needs to be registered or conflicts with an existing definition.
	 * <p>
	 * 检查给定候选人的bean名称,确定是否需要注册相应的bean定义或与现有定义冲突
	 * 
	 * 
	 * @param beanName the suggested name for the bean
	 * @param beanDefinition the corresponding bean definition
	 * @return {@code true} if the bean can be registered as-is;
	 * {@code false} if it should be skipped because there is an
	 * existing, compatible bean definition for the specified name
	 * @throws ConflictingBeanDefinitionException if an existing, incompatible
	 * bean definition has been found for the specified name
	 */
	protected boolean checkCandidate(String beanName, BeanDefinition beanDefinition) throws IllegalStateException {
		if (!this.registry.containsBeanDefinition(beanName)) {
			return true;
		}
		BeanDefinition existingDef = this.registry.getBeanDefinition(beanName);
		BeanDefinition originatingDef = existingDef.getOriginatingBeanDefinition();
		if (originatingDef != null) {
			existingDef = originatingDef;
		}
		if (isCompatible(beanDefinition, existingDef)) {
			return false;
		}
		throw new ConflictingBeanDefinitionException("Annotation-specified bean name '" + beanName +
				"' for bean class [" + beanDefinition.getBeanClassName() + "] conflicts with existing, " +
				"non-compatible bean definition of same name and class [" + existingDef.getBeanClassName() + "]");
	}

	/**
	 * Determine whether the given new bean definition is compatible with
	 * the given existing bean definition.
	 * <p>The default implementation considers them as compatible when the existing
	 * bean definition comes from the same source or from a non-scanning source.
	 * <p>
	 *  确定给定的新bean定义是否与给定的现有bean定义兼容<p>当现有bean定义来自相同的源或非扫描源时,默认实现将它们视为兼容
	 * 
	 * 
	 * @param newDefinition the new bean definition, originated from scanning
	 * @param existingDefinition the existing bean definition, potentially an
	 * explicitly defined one or a previously generated one from scanning
	 * @return whether the definitions are considered as compatible, with the
	 * new definition to be skipped in favor of the existing definition
	 */
	protected boolean isCompatible(BeanDefinition newDefinition, BeanDefinition existingDefinition) {
		return (!(existingDefinition instanceof ScannedGenericBeanDefinition) ||  // explicitly registered overriding bean
				newDefinition.getSource().equals(existingDefinition.getSource()) ||  // scanned same file twice
				newDefinition.equals(existingDefinition));  // scanned equivalent class twice
	}


	/**
	 * Get the Environment from the given registry if possible, otherwise return a new
	 * StandardEnvironment.
	 * <p>
	 *  如果可能,从给定的注册表获取环境,否则返回一个新的StandardEnvironment
	 */
	private static Environment getOrCreateEnvironment(BeanDefinitionRegistry registry) {
		Assert.notNull(registry, "BeanDefinitionRegistry must not be null");
		if (registry instanceof EnvironmentCapable) {
			return ((EnvironmentCapable) registry).getEnvironment();
		}
		return new StandardEnvironment();
	}

}
