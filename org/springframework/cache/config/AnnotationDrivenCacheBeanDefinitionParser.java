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

package org.springframework.cache.config;

import org.w3c.dom.Element;

import org.springframework.aop.config.AopNamespaceUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.parsing.CompositeComponentDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.cache.interceptor.BeanFactoryCacheOperationSourceAdvisor;
import org.springframework.cache.interceptor.CacheInterceptor;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

/**
 * {@link org.springframework.beans.factory.xml.BeanDefinitionParser}
 * implementation that allows users to easily configure all the
 * infrastructure beans required to enable annotation-driven cache
 * demarcation.
 *
 * <p>By default, all proxies are created as JDK proxies. This may cause
 * some problems if you are injecting objects as concrete classes rather
 * than interfaces. To overcome this restriction you can set the
 * '{@code proxy-target-class}' attribute to '{@code true}', which will
 * result in class-based proxies being created.
 *
 * <p>If the JSR-107 API and Spring's JCache implementation are present,
 * the necessary infrastructure beans required to handle methods annotated
 * with {@code CacheResult}, {@code CachePut}, {@code CacheRemove} or
 * {@code CacheRemoveAll} are also registered.
 *
 * <p>
 *  {@link orgspringframeworkbeansfactoryxmlBeanDefinitionParser}实现,允许用户轻松配置启用注释驱动的缓存分界所需的所有基础架构bean
 * 
 * <p>默认情况下,所有代理都被创建为JDK代理如果您将对象注入具体类而不是接口,可能会导致一些问题为克服此限制,您可以将"{@code proxy-target-class}"属性设置为'{@code true}
 * ',这将导致基于类的代理被创建。
 * 
 *  <p>如果存在JSR-107 API和Spring的JCache实现,则需要处理使用{@code CacheResult},{@code CachePut},{@code CacheRemove}或{@code CacheRemoveAll}
 * 注释的方法所需的基础架构bean也注册。
 * 
 * 
 * @author Costin Leau
 * @author Stephane Nicoll
 * @since 3.1
 */
class AnnotationDrivenCacheBeanDefinitionParser implements BeanDefinitionParser {

	private static final String CACHE_ASPECT_CLASS_NAME =
			"org.springframework.cache.aspectj.AnnotationCacheAspect";

	private static final String JCACHE_ASPECT_CLASS_NAME =
			"org.springframework.cache.aspectj.JCacheCacheAspect";

	private static final boolean jsr107Present = ClassUtils.isPresent(
			"javax.cache.Cache", AnnotationDrivenCacheBeanDefinitionParser.class.getClassLoader());

	private static final boolean jcacheImplPresent = ClassUtils.isPresent(
			"org.springframework.cache.jcache.interceptor.DefaultJCacheOperationSource",
			AnnotationDrivenCacheBeanDefinitionParser.class.getClassLoader());


	/**
	 * Parses the '{@code <cache:annotation-driven>}' tag. Will
	 * {@link AopNamespaceUtils#registerAutoProxyCreatorIfNecessary
	 * register an AutoProxyCreator} with the container as necessary.
	 * <p>
	 *  解析"{@code <cache：annotation-driven>}"标签如果需要,将{@link AopNamespaceUtils#registerAutoProxyCreatorIfNecessary注册AutoProxyCreator}
	 * 与容器注册。
	 * 
	 */
	@Override
	public BeanDefinition parse(Element element, ParserContext parserContext) {
		String mode = element.getAttribute("mode");
		if ("aspectj".equals(mode)) {
			// mode="aspectj"
			registerCacheAspect(element, parserContext);
		}
		else {
			// mode="proxy"
			registerCacheAdvisor(element, parserContext);
		}

		return null;
	}

	private void registerCacheAspect(Element element, ParserContext parserContext) {
		SpringCachingConfigurer.registerCacheAspect(element, parserContext);
		if (jsr107Present && jcacheImplPresent) {
			JCacheCachingConfigurer.registerCacheAspect(element, parserContext);
		}
	}

	private void registerCacheAdvisor(Element element, ParserContext parserContext) {
		AopNamespaceUtils.registerAutoProxyCreatorIfNecessary(parserContext, element);
		SpringCachingConfigurer.registerCacheAdvisor(element, parserContext);
		if (jsr107Present && jcacheImplPresent) {
			JCacheCachingConfigurer.registerCacheAdvisor(element, parserContext);
		}
	}

	/**
	 * Parse the cache resolution strategy to use. If a 'cache-resolver' attribute
	 * is set, it is injected. Otherwise the 'cache-manager' is set. If {@code setBoth}
	 * is {@code true}, both service are actually injected.
	 * <p>
	 * 解析使用的缓存解析策略如果设置了"缓存解析器"属性,则会注入它,否则将设置"cache-manager"如果{@code setBoth}为{@code true},则两个服务都实际注入
	 * 
	 */
	private static void parseCacheResolution(Element element, BeanDefinition def, boolean setBoth) {
		String name = element.getAttribute("cache-resolver");
		if (StringUtils.hasText(name)) {
			def.getPropertyValues().add("cacheResolver", new RuntimeBeanReference(name.trim()));
		}
		if (!StringUtils.hasText(name) || setBoth) {
			def.getPropertyValues().add("cacheManager",
					new RuntimeBeanReference(CacheNamespaceHandler.extractCacheManager(element)));
		}
	}

	private static BeanDefinition parseErrorHandler(Element element, BeanDefinition def) {
		String name = element.getAttribute("error-handler");
		if (StringUtils.hasText(name)) {
			def.getPropertyValues().add("errorHandler", new RuntimeBeanReference(name.trim()));
		}
		return def;
	}


	/**
	 * Configure the necessary infrastructure to support the Spring's caching annotations.
	 * <p>
	 *  配置必要的基础架构以支持Spring的缓存注释
	 * 
	 */
	private static class SpringCachingConfigurer {

		private static void registerCacheAdvisor(Element element, ParserContext parserContext) {
			if (!parserContext.getRegistry().containsBeanDefinition(CacheManagementConfigUtils.CACHE_ADVISOR_BEAN_NAME)) {
				Object eleSource = parserContext.extractSource(element);

				// Create the CacheOperationSource definition.
				RootBeanDefinition sourceDef = new RootBeanDefinition("org.springframework.cache.annotation.AnnotationCacheOperationSource");
				sourceDef.setSource(eleSource);
				sourceDef.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
				String sourceName = parserContext.getReaderContext().registerWithGeneratedName(sourceDef);

				// Create the CacheInterceptor definition.
				RootBeanDefinition interceptorDef = new RootBeanDefinition(CacheInterceptor.class);
				interceptorDef.setSource(eleSource);
				interceptorDef.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
				parseCacheResolution(element, interceptorDef, false);
				parseErrorHandler(element, interceptorDef);
				CacheNamespaceHandler.parseKeyGenerator(element, interceptorDef);
				interceptorDef.getPropertyValues().add("cacheOperationSources", new RuntimeBeanReference(sourceName));
				String interceptorName = parserContext.getReaderContext().registerWithGeneratedName(interceptorDef);

				// Create the CacheAdvisor definition.
				RootBeanDefinition advisorDef = new RootBeanDefinition(BeanFactoryCacheOperationSourceAdvisor.class);
				advisorDef.setSource(eleSource);
				advisorDef.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
				advisorDef.getPropertyValues().add("cacheOperationSource", new RuntimeBeanReference(sourceName));
				advisorDef.getPropertyValues().add("adviceBeanName", interceptorName);
				if (element.hasAttribute("order")) {
					advisorDef.getPropertyValues().add("order", element.getAttribute("order"));
				}
				parserContext.getRegistry().registerBeanDefinition(CacheManagementConfigUtils.CACHE_ADVISOR_BEAN_NAME, advisorDef);

				CompositeComponentDefinition compositeDef = new CompositeComponentDefinition(element.getTagName(), eleSource);
				compositeDef.addNestedComponent(new BeanComponentDefinition(sourceDef, sourceName));
				compositeDef.addNestedComponent(new BeanComponentDefinition(interceptorDef, interceptorName));
				compositeDef.addNestedComponent(new BeanComponentDefinition(advisorDef, CacheManagementConfigUtils.CACHE_ADVISOR_BEAN_NAME));
				parserContext.registerComponent(compositeDef);
			}
		}

		/**
		 * Registers a
		 * <pre class="code">
		 * <bean id="cacheAspect" class="org.springframework.cache.aspectj.AnnotationCacheAspect" factory-method="aspectOf">
		 *   <property name="cacheManager" ref="cacheManager"/>
		 *   <property name="keyGenerator" ref="keyGenerator"/>
		 * </bean>
		 * </pre>
		 * <p>
		 *  注册a
		 * <pre class="code">
		 * <bean id="cacheAspect" class="org.springframework.cache.aspectj.AnnotationCacheAspect" factory-method="aspectOf">
		 * <property name="cacheManager" ref="cacheManager"/>
		 * <property name="keyGenerator" ref="keyGenerator"/>
		 */
		private static void registerCacheAspect(Element element, ParserContext parserContext) {
			if (!parserContext.getRegistry().containsBeanDefinition(CacheManagementConfigUtils.CACHE_ASPECT_BEAN_NAME)) {
				RootBeanDefinition def = new RootBeanDefinition();
				def.setBeanClassName(CACHE_ASPECT_CLASS_NAME);
				def.setFactoryMethodName("aspectOf");
				parseCacheResolution(element, def, false);
				CacheNamespaceHandler.parseKeyGenerator(element, def);
				parserContext.registerBeanComponent(new BeanComponentDefinition(def, CacheManagementConfigUtils.CACHE_ASPECT_BEAN_NAME));
			}
		}
	}


	/**
	 * Configure the necessary infrastructure to support the standard JSR-107 caching annotations.
	 * <p>
	 * </bean>
	 * </pre>
	 */
	private static class JCacheCachingConfigurer {

		private static void registerCacheAdvisor(Element element, ParserContext parserContext) {
			if (!parserContext.getRegistry().containsBeanDefinition(CacheManagementConfigUtils.JCACHE_ADVISOR_BEAN_NAME)) {
				Object eleSource = parserContext.extractSource(element);

				// Create the CacheOperationSource definition.
				BeanDefinition sourceDef = createJCacheOperationSourceBeanDefinition(element, eleSource);
				String sourceName = parserContext.getReaderContext().registerWithGeneratedName(sourceDef);

				// Create the CacheInterceptor definition.
				RootBeanDefinition interceptorDef =
						new RootBeanDefinition("org.springframework.cache.jcache.interceptor.JCacheInterceptor");
				interceptorDef.setSource(eleSource);
				interceptorDef.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
				interceptorDef.getPropertyValues().add("cacheOperationSource", new RuntimeBeanReference(sourceName));
				parseErrorHandler(element, interceptorDef);
				String interceptorName = parserContext.getReaderContext().registerWithGeneratedName(interceptorDef);

				// Create the CacheAdvisor definition.
				RootBeanDefinition advisorDef = new RootBeanDefinition(
						"org.springframework.cache.jcache.interceptor.BeanFactoryJCacheOperationSourceAdvisor");
				advisorDef.setSource(eleSource);
				advisorDef.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
				advisorDef.getPropertyValues().add("cacheOperationSource", new RuntimeBeanReference(sourceName));
				advisorDef.getPropertyValues().add("adviceBeanName", interceptorName);
				if (element.hasAttribute("order")) {
					advisorDef.getPropertyValues().add("order", element.getAttribute("order"));
				}
				parserContext.getRegistry().registerBeanDefinition(CacheManagementConfigUtils.JCACHE_ADVISOR_BEAN_NAME, advisorDef);

				CompositeComponentDefinition compositeDef = new CompositeComponentDefinition(element.getTagName(), eleSource);
				compositeDef.addNestedComponent(new BeanComponentDefinition(sourceDef, sourceName));
				compositeDef.addNestedComponent(new BeanComponentDefinition(interceptorDef, interceptorName));
				compositeDef.addNestedComponent(new BeanComponentDefinition(advisorDef, CacheManagementConfigUtils.JCACHE_ADVISOR_BEAN_NAME));
				parserContext.registerComponent(compositeDef);
			}
		}

		private static void registerCacheAspect(Element element, ParserContext parserContext) {
			if (!parserContext.getRegistry().containsBeanDefinition(CacheManagementConfigUtils.JCACHE_ASPECT_BEAN_NAME)) {
				Object eleSource = parserContext.extractSource(element);
				RootBeanDefinition def = new RootBeanDefinition();
				def.setBeanClassName(JCACHE_ASPECT_CLASS_NAME);
				def.setFactoryMethodName("aspectOf");
				BeanDefinition sourceDef = createJCacheOperationSourceBeanDefinition(element, eleSource);
				String sourceName =
						parserContext.getReaderContext().registerWithGeneratedName(sourceDef);
				def.getPropertyValues().add("cacheOperationSource", new RuntimeBeanReference(sourceName));

				parserContext.registerBeanComponent(new BeanComponentDefinition(sourceDef, sourceName));
				parserContext.registerBeanComponent(new BeanComponentDefinition(def, CacheManagementConfigUtils.JCACHE_ASPECT_BEAN_NAME));
			}
		}

		private static RootBeanDefinition createJCacheOperationSourceBeanDefinition(Element element, Object eleSource) {
			RootBeanDefinition sourceDef =
					new RootBeanDefinition("org.springframework.cache.jcache.interceptor.DefaultJCacheOperationSource");
			sourceDef.setSource(eleSource);
			sourceDef.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
			// JSR-107 support should create an exception cache resolver with the cache manager
			// and there is no way to set that exception cache resolver from the namespace
			parseCacheResolution(element, sourceDef, true);
			CacheNamespaceHandler.parseKeyGenerator(element, sourceDef);
			return sourceDef;
		}
	}

}
