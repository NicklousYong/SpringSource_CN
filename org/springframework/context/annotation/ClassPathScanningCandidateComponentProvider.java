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

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.env.Environment;
import org.springframework.core.env.EnvironmentCapable;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/**
 * A component provider that scans the classpath from a base package. It then
 * applies exclude and include filters to the resulting classes to find candidates.
 *
 * <p>This implementation is based on Spring's
 * {@link org.springframework.core.type.classreading.MetadataReader MetadataReader}
 * facility, backed by an ASM {@link org.springframework.asm.ClassReader ClassReader}.
 *
 * <p>
 *  从基础包扫描类路径的组件提供程序然后对所生成的类应用exclude和include过滤器以查找候选项
 * 
 * <p>此实现基于Spring的{@link orgspringframeworkcoretypeclassreadingMetadataReader MetadataReader}工具,由ASM {@link orgspringframeworkasmClassReader ClassReader}
 * 支持,。
 * 
 * 
 * @author Mark Fisher
 * @author Juergen Hoeller
 * @author Ramnivas Laddad
 * @author Chris Beams
 * @since 2.5
 * @see org.springframework.core.type.classreading.MetadataReaderFactory
 * @see org.springframework.core.type.AnnotationMetadata
 * @see ScannedGenericBeanDefinition
 */
public class ClassPathScanningCandidateComponentProvider implements EnvironmentCapable, ResourceLoaderAware {

	static final String DEFAULT_RESOURCE_PATTERN = "**/*.class";

	protected final Log logger = LogFactory.getLog(getClass());

	private Environment environment;

	private ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();

	private MetadataReaderFactory metadataReaderFactory =
			new CachingMetadataReaderFactory(this.resourcePatternResolver);

	private String resourcePattern = DEFAULT_RESOURCE_PATTERN;

	private final List<TypeFilter> includeFilters = new LinkedList<TypeFilter>();

	private final List<TypeFilter> excludeFilters = new LinkedList<TypeFilter>();

	private ConditionEvaluator conditionEvaluator;


	/**
	 * Create a ClassPathScanningCandidateComponentProvider with a {@link StandardEnvironment}.
	 * <p>
	 *  保护的最终日志记录器= LogFactorygetLog(getClass());
	 * 
	 *  私人环境环境;
	 * 
	 *  private ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
	 * 。
	 * 
	 *  private MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(thisresourceP
	 * atternResolver);。
	 * 
	 *  private String resourcePattern = DEFAULT_RESOURCE_PATTERN;
	 * 
	 *  private final List <TypeFilter> includeFilters = new LinkedList <TypeFilter>();
	 * 
	 *  private final List <TypeFilter> excludeFilters = new LinkedList <TypeFilter>();
	 * 
	 *  私人ConditionEvaluator conditionEvaluator;
	 * 
	 * / **使用{@link StandardEnvironment}创建一个ClassPathScanningCandidateComponentProvider
	 * 
	 * 
	 * @param useDefaultFilters whether to register the default filters for the
	 * {@link Component @Component}, {@link Repository @Repository},
	 * {@link Service @Service}, and {@link Controller @Controller}
	 * stereotype annotations
	 * @see #registerDefaultFilters()
	 */
	public ClassPathScanningCandidateComponentProvider(boolean useDefaultFilters) {
		this(useDefaultFilters, new StandardEnvironment());
	}

	/**
	 * Create a ClassPathScanningCandidateComponentProvider with the given {@link Environment}.
	 * <p>
	 *  使用给定的{@link Environment}创建一个ClassPathScanningCandidateComponentProvider
	 * 
	 * 
	 * @param useDefaultFilters whether to register the default filters for the
	 * {@link Component @Component}, {@link Repository @Repository},
	 * {@link Service @Service}, and {@link Controller @Controller}
	 * stereotype annotations
	 * @param environment the Environment to use
	 * @see #registerDefaultFilters()
	 */
	public ClassPathScanningCandidateComponentProvider(boolean useDefaultFilters, Environment environment) {
		if (useDefaultFilters) {
			registerDefaultFilters();
		}
		Assert.notNull(environment, "Environment must not be null");
		this.environment = environment;
	}


	/**
	 * Set the ResourceLoader to use for resource locations.
	 * This will typically be a ResourcePatternResolver implementation.
	 * <p>Default is PathMatchingResourcePatternResolver, also capable of
	 * resource pattern resolving through the ResourcePatternResolver interface.
	 * <p>
	 *  将ResourceLoader设置为用于资源位置这通常将是一个ResourcePatternResolver实现<p> Default是PathMatchingResourcePatternResol
	 * ver,还能够通过ResourcePatternResolver接口进行资源模式解析。
	 * 
	 * 
	 * @see org.springframework.core.io.support.ResourcePatternResolver
	 * @see org.springframework.core.io.support.PathMatchingResourcePatternResolver
	 */
	@Override
	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourcePatternResolver = ResourcePatternUtils.getResourcePatternResolver(resourceLoader);
		this.metadataReaderFactory = new CachingMetadataReaderFactory(resourceLoader);
	}

	/**
	 * Return the ResourceLoader that this component provider uses.
	 * <p>
	 *  返回此组件提供程序使用的ResourceLoader
	 * 
	 */
	public final ResourceLoader getResourceLoader() {
		return this.resourcePatternResolver;
	}

	/**
	 * Set the {@link MetadataReaderFactory} to use.
	 * <p>Default is a {@link CachingMetadataReaderFactory} for the specified
	 * {@linkplain #setResourceLoader resource loader}.
	 * <p>Call this setter method <i>after</i> {@link #setResourceLoader} in order
	 * for the given MetadataReaderFactory to override the default factory.
	 * <p>
	 * 将{@link MetadataReaderFactory}设置为使用<p>默认是指定的{@linkplain #setResourceLoader资源加载器}的{@link CachingMetadataReaderFactory}
	 *  <p>在</i>之后调用此setter方法<i> {@link# setResourceLoader},以便给定的MetadataReaderFactory覆盖默认工厂。
	 * 
	 */
	public void setMetadataReaderFactory(MetadataReaderFactory metadataReaderFactory) {
		this.metadataReaderFactory = metadataReaderFactory;
	}

	/**
	 * Return the MetadataReaderFactory used by this component provider.
	 * <p>
	 *  返回此组件提供程序使用的MetadataReaderFactory
	 * 
	 */
	public final MetadataReaderFactory getMetadataReaderFactory() {
		return this.metadataReaderFactory;
	}

	/**
	 * Set the Environment to use when resolving placeholders and evaluating
	 * {@link Conditional @Conditional}-annotated component classes.
	 * <p>The default is a {@link StandardEnvironment}.
	 * <p>
	 *  设置环境以在解决占位符时使用,并评估{@link Conditional @Conditional}注释的组件类<p>默认值为{@link StandardEnvironment}
	 * 
	 * 
	 * @param environment the Environment to use
	 */
	public void setEnvironment(Environment environment) {
		Assert.notNull(environment, "Environment must not be null");
		this.environment = environment;
		this.conditionEvaluator = null;
	}

	@Override
	public final Environment getEnvironment() {
		return this.environment;
	}

	/**
	 * Returns the {@link BeanDefinitionRegistry} used by this scanner, if any.
	 * <p>
	 *  返回此扫描仪使用的{@link BeanDefinitionRegistry}(如果有)
	 * 
	 */
	protected BeanDefinitionRegistry getRegistry() {
		return null;
	}

	/**
	 * Set the resource pattern to use when scanning the classpath.
	 * This value will be appended to each base package name.
	 * <p>
	 *  设置扫描类路径时使用的资源模式此值将附加到每个基本包名称
	 * 
	 * 
	 * @see #findCandidateComponents(String)
	 * @see #DEFAULT_RESOURCE_PATTERN
	 */
	public void setResourcePattern(String resourcePattern) {
		Assert.notNull(resourcePattern, "'resourcePattern' must not be null");
		this.resourcePattern = resourcePattern;
	}

	/**
	 * Add an include type filter to the <i>end</i> of the inclusion list.
	 * <p>
	 * 将包含类型过滤器添加到包含列表的<i>结束</i>
	 * 
	 */
	public void addIncludeFilter(TypeFilter includeFilter) {
		this.includeFilters.add(includeFilter);
	}

	/**
	 * Add an exclude type filter to the <i>front</i> of the exclusion list.
	 * <p>
	 *  将排除类型过滤器添加到排除列表的<i>前端</i>
	 * 
	 */
	public void addExcludeFilter(TypeFilter excludeFilter) {
		this.excludeFilters.add(0, excludeFilter);
	}

	/**
	 * Reset the configured type filters.
	 * <p>
	 *  重置配置的类型过滤器
	 * 
	 * 
	 * @param useDefaultFilters whether to re-register the default filters for
	 * the {@link Component @Component}, {@link Repository @Repository},
	 * {@link Service @Service}, and {@link Controller @Controller}
	 * stereotype annotations
	 * @see #registerDefaultFilters()
	 */
	public void resetFilters(boolean useDefaultFilters) {
		this.includeFilters.clear();
		this.excludeFilters.clear();
		if (useDefaultFilters) {
			registerDefaultFilters();
		}
	}

	/**
	 * Register the default filter for {@link Component @Component}.
	 * <p>This will implicitly register all annotations that have the
	 * {@link Component @Component} meta-annotation including the
	 * {@link Repository @Repository}, {@link Service @Service}, and
	 * {@link Controller @Controller} stereotype annotations.
	 * <p>Also supports Java EE 6's {@link javax.annotation.ManagedBean} and
	 * JSR-330's {@link javax.inject.Named} annotations, if available.
	 *
	 * <p>
	 *  注册{@link Component @Component}的默认过滤器<p>这将隐式注册所有具有{@link Component @Component}元注释的注释,包括{@link Repository @Repository}
	 * ,{@link Service @Service }和{@link Controller @Controller}构造型注释<p>还支持Java EE 6的{@link javaxannotationManagedBean}
	 * 和JSR-330的{@link javaxinjectNamed}注释(如果可用)。
	 * 
	 */
	@SuppressWarnings("unchecked")
	protected void registerDefaultFilters() {
		this.includeFilters.add(new AnnotationTypeFilter(Component.class));
		ClassLoader cl = ClassPathScanningCandidateComponentProvider.class.getClassLoader();
		try {
			this.includeFilters.add(new AnnotationTypeFilter(
					((Class<? extends Annotation>) ClassUtils.forName("javax.annotation.ManagedBean", cl)), false));
			logger.debug("JSR-250 'javax.annotation.ManagedBean' found and supported for component scanning");
		}
		catch (ClassNotFoundException ex) {
			// JSR-250 1.1 API (as included in Java EE 6) not available - simply skip.
		}
		try {
			this.includeFilters.add(new AnnotationTypeFilter(
					((Class<? extends Annotation>) ClassUtils.forName("javax.inject.Named", cl)), false));
			logger.debug("JSR-330 'javax.inject.Named' annotation found and supported for component scanning");
		}
		catch (ClassNotFoundException ex) {
			// JSR-330 API not available - simply skip.
		}
	}


	/**
	 * Scan the class path for candidate components.
	 * <p>
	 *  扫描候选组件的类路径
	 * 
	 * 
	 * @param basePackage the package to check for annotated classes
	 * @return a corresponding Set of autodetected bean definitions
	 */
	public Set<BeanDefinition> findCandidateComponents(String basePackage) {
		Set<BeanDefinition> candidates = new LinkedHashSet<BeanDefinition>();
		try {
			String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
					resolveBasePackage(basePackage) + "/" + this.resourcePattern;
			Resource[] resources = this.resourcePatternResolver.getResources(packageSearchPath);
			boolean traceEnabled = logger.isTraceEnabled();
			boolean debugEnabled = logger.isDebugEnabled();
			for (Resource resource : resources) {
				if (traceEnabled) {
					logger.trace("Scanning " + resource);
				}
				if (resource.isReadable()) {
					try {
						MetadataReader metadataReader = this.metadataReaderFactory.getMetadataReader(resource);
						if (isCandidateComponent(metadataReader)) {
							ScannedGenericBeanDefinition sbd = new ScannedGenericBeanDefinition(metadataReader);
							sbd.setResource(resource);
							sbd.setSource(resource);
							if (isCandidateComponent(sbd)) {
								if (debugEnabled) {
									logger.debug("Identified candidate component class: " + resource);
								}
								candidates.add(sbd);
							}
							else {
								if (debugEnabled) {
									logger.debug("Ignored because not a concrete top-level class: " + resource);
								}
							}
						}
						else {
							if (traceEnabled) {
								logger.trace("Ignored because not matching any filter: " + resource);
							}
						}
					}
					catch (Throwable ex) {
						throw new BeanDefinitionStoreException(
								"Failed to read candidate component class: " + resource, ex);
					}
				}
				else {
					if (traceEnabled) {
						logger.trace("Ignored because not readable: " + resource);
					}
				}
			}
		}
		catch (IOException ex) {
			throw new BeanDefinitionStoreException("I/O failure during classpath scanning", ex);
		}
		return candidates;
	}


	/**
	 * Resolve the specified base package into a pattern specification for
	 * the package search path.
	 * <p>The default implementation resolves placeholders against system properties,
	 * and converts a "."-based package path to a "/"-based resource path.
	 * <p>
	 * 将指定的基本包解析为包搜索路径的模式规范<p>默认实现将占位符解析为系统属性,并将基于""的包路径转换为基于"/"的资源路径
	 * 
	 * 
	 * @param basePackage the base package as specified by the user
	 * @return the pattern specification to be used for package searching
	 */
	protected String resolveBasePackage(String basePackage) {
		return ClassUtils.convertClassNameToResourcePath(this.environment.resolveRequiredPlaceholders(basePackage));
	}

	/**
	 * Determine whether the given class does not match any exclude filter
	 * and does match at least one include filter.
	 * <p>
	 *  确定给定的类是否与任何排除过滤器不匹配,并且匹配至少一个包含过滤器
	 * 
	 * 
	 * @param metadataReader the ASM ClassReader for the class
	 * @return whether the class qualifies as a candidate component
	 */
	protected boolean isCandidateComponent(MetadataReader metadataReader) throws IOException {
		for (TypeFilter tf : this.excludeFilters) {
			if (tf.match(metadataReader, this.metadataReaderFactory)) {
				return false;
			}
		}
		for (TypeFilter tf : this.includeFilters) {
			if (tf.match(metadataReader, this.metadataReaderFactory)) {
				return isConditionMatch(metadataReader);
			}
		}
		return false;
	}

	/**
	 * Determine whether the given class is a candidate component based on any
	 * {@code @Conditional} annotations.
	 * <p>
	 *  根据任何{@code @Conditional}注释确定给定类是否为候选组件
	 * 
	 * 
	 * @param metadataReader the ASM ClassReader for the class
	 * @return whether the class qualifies as a candidate component
	 */
	private boolean isConditionMatch(MetadataReader metadataReader) {
		if (this.conditionEvaluator == null) {
			this.conditionEvaluator = new ConditionEvaluator(getRegistry(), getEnvironment(), getResourceLoader());
		}
		return !this.conditionEvaluator.shouldSkip(metadataReader.getAnnotationMetadata());
	}

	/**
	 * Determine whether the given bean definition qualifies as candidate.
	 * <p>The default implementation checks whether the class is concrete
	 * (i.e. not abstract and not an interface). Can be overridden in subclasses.
	 * <p>
	 *  确定给定的b​​ean定义是否符合候选<p>默认实现检查类是否具体(即不是抽象而不是接口)可以在子类中覆盖
	 * 
	 * 
	 * @param beanDefinition the bean definition to check
	 * @return whether the bean definition qualifies as a candidate component
	 */
	protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
		return (beanDefinition.getMetadata().isConcrete() && beanDefinition.getMetadata().isIndependent());
	}


	/**
	 * Clear the underlying metadata cache, removing all cached class metadata.
	 * <p>
	 *  清除底层元数据缓存,删除所有缓存的类元数据
	 */
	public void clearCache() {
		if (this.metadataReaderFactory instanceof CachingMetadataReaderFactory) {
			((CachingMetadataReaderFactory) this.metadataReaderFactory).clearCache();
		}
	}

}
