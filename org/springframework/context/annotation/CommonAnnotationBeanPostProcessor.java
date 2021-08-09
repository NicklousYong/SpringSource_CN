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

package org.springframework.context.annotation;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceRef;

import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.InitDestroyAnnotationBeanPostProcessor;
import org.springframework.beans.factory.annotation.InjectionMetadata;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.config.EmbeddedValueResolver;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.MethodParameter;
import org.springframework.core.Ordered;
import org.springframework.jndi.support.SimpleJndiBeanFactory;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.util.StringValueResolver;

/**
 * {@link org.springframework.beans.factory.config.BeanPostProcessor} implementation
 * that supports common Java annotations out of the box, in particular the JSR-250
 * annotations in the {@code javax.annotation} package. These common Java
 * annotations are supported in many Java EE 5 technologies (e.g. JSF 1.2),
 * as well as in Java 6's JAX-WS.
 *
 * <p>This post-processor includes support for the {@link javax.annotation.PostConstruct}
 * and {@link javax.annotation.PreDestroy} annotations - as init annotation
 * and destroy annotation, respectively - through inheriting from
 * {@link InitDestroyAnnotationBeanPostProcessor} with pre-configured annotation types.
 *
 * <p>The central element is the {@link javax.annotation.Resource} annotation
 * for annotation-driven injection of named beans, by default from the containing
 * Spring BeanFactory, with only {@code mappedName} references resolved in JNDI.
 * The {@link #setAlwaysUseJndiLookup "alwaysUseJndiLookup" flag} enforces JNDI lookups
 * equivalent to standard Java EE 5 resource injection for {@code name} references
 * and default names as well. The target beans can be simple POJOs, with no special
 * requirements other than the type having to match.
 *
 * <p>The JAX-WS {@link javax.xml.ws.WebServiceRef} annotation is supported too,
 * analogous to {@link javax.annotation.Resource} but with the capability of creating
 * specific JAX-WS service endpoints. This may either point to an explicitly defined
 * resource by name or operate on a locally specified JAX-WS service class. Finally,
 * this post-processor also supports the EJB 3 {@link javax.ejb.EJB} annotation,
 * analogous to {@link javax.annotation.Resource} as well, with the capability to
 * specify both a local bean name and a global JNDI name for fallback retrieval.
 * The target beans can be plain POJOs as well as EJB 3 Session Beans in this case.
 *
 * <p>The common annotations supported by this post-processor are available in
 * Java 6 (JDK 1.6) as well as in Java EE 5/6 (which provides a standalone jar for
 * its common annotations as well, allowing for use in any Java 5 based application).
 *
 * <p>For default usage, resolving resource names as Spring bean names,
 * simply define the following in your application context:
 *
 * <pre class="code">
 * &lt;bean class="org.springframework.context.annotation.CommonAnnotationBeanPostProcessor"/&gt;</pre>
 *
 * For direct JNDI access, resolving resource names as JNDI resource references
 * within the Java EE application's "java:comp/env/" namespace, use the following:
 *
 * <pre class="code">
 * &lt;bean class="org.springframework.context.annotation.CommonAnnotationBeanPostProcessor"&gt;
 *   &lt;property name="alwaysUseJndiLookup" value="true"/&gt;
 * &lt;/bean&gt;</pre>
 *
 * {@code mappedName} references will always be resolved in JNDI,
 * allowing for global JNDI names (including "java:" prefix) as well. The
 * "alwaysUseJndiLookup" flag just affects {@code name} references and
 * default names (inferred from the field name / property name).
 *
 * <p><b>NOTE:</b> A default CommonAnnotationBeanPostProcessor will be registered
 * by the "context:annotation-config" and "context:component-scan" XML tags.
 * Remove or turn off the default annotation configuration there if you intend
 * to specify a custom CommonAnnotationBeanPostProcessor bean definition!
 * <p><b>NOTE:</b> Annotation injection will be performed <i>before</i> XML injection; thus
 * the latter configuration will override the former for properties wired through
 * both approaches.
 *
 * <p>
 * {@link orgspringframeworkbeansfactoryconfigBeanPostProcessor}实现,支持开箱即用的常见Java注释,特别是{@code javaxannotation}
 * 包中的JSR-250注释这些常见的Java注释在许多Java EE 5技术(例如JSF 12)中都支持,如就像Java 6的JAX-WS一样。
 * 
 *  <p>这个后处理器包括对{@link javaxannotationPostConstruct}和{@link javaxannotationPreDestroy}注释的支持,分别通过从{@link InitDestroyAnnotationBeanPostProcessor}
 * 继承了预先配置的注释类型,分别作为init注释和销毁注释。
 * 
 * <p>中心元素是注释驱动的注册bean注入的{@link javaxannotationResource}注释,默认情况下来自包含Spring BeanFactory,JNDI中只解析了{@code mappedName}
 * 引用{@link #setAlwaysUseJndiLookup"alwaysUseJndiLookup "flag"强制执行与{@code name}引用和默认名称相同的标准Java EE 5资源注入
 * 的JNDI查找。
 * 目标bean可以是简单的POJO,除了必须匹配的类型之外没有特殊要求。
 * 
 * <p>也支持JAX-WS {@link javaxxmlwsWebServiceRef}注释,类似于{@link javaxannotationResource},但具有创建特定JAX-WS服务端点的功
 * 能。
 * 这可以指定一个明确定义的资源,或者按照本地指定的JAX-WS服务类最后,此后处理器还支持与{@link javaxannotationResource}类似的EJB 3 {@link javaxejbEJB}
 * 注释,具有指定本地bean名称和全局JNDI的功能回退检索的名称在这种情况下,目标bean可以是纯POJO以及EJB 3 Session Bean。
 * 
 * 这种后处理器支持的常见注释可用于Java 6(JDK 16)以及Java EE 5/6(它为其通用注释提供了一个独立的jar,允许在任何Java 5中使用基于应用)
 * 
 *  <p>对于默认使用,将资源名称解析为Spring bean名称,只需在应用程序上下文中定义以下内容：
 * 
 * <pre class="code">
 *  &lt; bean class ="orgspringframeworkcontextannotationCommonAnnotationBeanPostProcessor"/&gt; </pre>。
 * 
 *  对于直接JNDI访问,在Java EE应用程序的"java：comp / env /"命名空间中将资源名称解析为JNDI资源引用,请使用以下命令：
 * 
 * <pre class="code">
 *  &lt; bean class ="orgspringframeworkcontextannotationCommonAnnotationBeanPostProcessor"&gt; &lt; pro
 * perty name ="alwaysUseJndiLookup"value ="true"/&gt; &LT; /豆腐&GT; </PRE>。
 * 
 * {@code mappedName}引用将始终在JNDI中解析,允许全局JNDI名称(包括"java："前缀)以及"alwaysUseJndiLookup"标志只会影响{@code name}引用和默认
 * 名称(从字段名称推断/属性名称)。
 * 
 *  <p> <b>注意：</b>默认的CommonAnnotationBeanPostProcessor将通过"context：annotation-config"和"context：component-
 * scan"XML标记注册。
 * 如果您打算删除或关闭默认注释配置指定一个自定义的CommonAnnotationBeanPostProcessor bean定义！ <p> <b>注意：</b>在注入XML之前<i>将执行注释注入;因此
 * 后一种配置将覆盖前者通过两种方法连接的属性。
 * 
 * 
 * @author Juergen Hoeller
 * @since 2.5
 * @see #setAlwaysUseJndiLookup
 * @see #setResourceFactory
 * @see org.springframework.beans.factory.annotation.InitDestroyAnnotationBeanPostProcessor
 * @see org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor
 */
@SuppressWarnings("serial")
public class CommonAnnotationBeanPostProcessor extends InitDestroyAnnotationBeanPostProcessor
		implements InstantiationAwareBeanPostProcessor, BeanFactoryAware, Serializable {

	// Common Annotations 1.1 Resource.lookup() available? Not present on JDK 6...
	private static final Method lookupAttribute = ClassUtils.getMethodIfAvailable(Resource.class, "lookup");

	private static Class<? extends Annotation> webServiceRefClass = null;

	private static Class<? extends Annotation> ejbRefClass = null;

	static {
		try {
			@SuppressWarnings("unchecked")
			Class<? extends Annotation> clazz = (Class<? extends Annotation>)
					ClassUtils.forName("javax.xml.ws.WebServiceRef", CommonAnnotationBeanPostProcessor.class.getClassLoader());
			webServiceRefClass = clazz;
		}
		catch (ClassNotFoundException ex) {
			webServiceRefClass = null;
		}
		try {
			@SuppressWarnings("unchecked")
			Class<? extends Annotation> clazz = (Class<? extends Annotation>)
					ClassUtils.forName("javax.ejb.EJB", CommonAnnotationBeanPostProcessor.class.getClassLoader());
			ejbRefClass = clazz;
		}
		catch (ClassNotFoundException ex) {
			ejbRefClass = null;
		}
	}


	private final Set<String> ignoredResourceTypes = new HashSet<String>(1);

	private boolean fallbackToDefaultTypeMatch = true;

	private boolean alwaysUseJndiLookup = false;

	private transient BeanFactory jndiFactory = new SimpleJndiBeanFactory();

	private transient BeanFactory resourceFactory;

	private transient BeanFactory beanFactory;

	private transient StringValueResolver embeddedValueResolver;

	private transient final Map<String, InjectionMetadata> injectionMetadataCache =
			new ConcurrentHashMap<String, InjectionMetadata>(256);


	/**
	 * Create a new CommonAnnotationBeanPostProcessor,
	 * with the init and destroy annotation types set to
	 * {@link javax.annotation.PostConstruct} and {@link javax.annotation.PreDestroy},
	 * respectively.
	 * <p>
	 * 创建一个新的CommonAnnotationBeanPostProcessor,其中init和destroy注释类型分别设置为{@link javaxannotationPostConstruct}和{@link javaxannotationPreDestroy}
	 * 。
	 * 
	 */
	public CommonAnnotationBeanPostProcessor() {
		setOrder(Ordered.LOWEST_PRECEDENCE - 3);
		setInitAnnotationType(PostConstruct.class);
		setDestroyAnnotationType(PreDestroy.class);
		ignoreResourceType("javax.xml.ws.WebServiceContext");
	}


	/**
	 * Ignore the given resource type when resolving {@code @Resource}
	 * annotations.
	 * <p>By default, the {@code javax.xml.ws.WebServiceContext} interface
	 * will be ignored, since it will be resolved by the JAX-WS runtime.
	 * <p>
	 *  解析{@code @Resource}注解<p>时忽略给定的资源类型默认情况下,{@code javaxxmlwsWebServiceContext}接口将被忽略,因为它将由JAX-WS运行时解析
	 * 
	 * 
	 * @param resourceType the resource type to ignore
	 */
	public void ignoreResourceType(String resourceType) {
		Assert.notNull(resourceType, "Ignored resource type must not be null");
		this.ignoredResourceTypes.add(resourceType);
	}

	/**
	 * Set whether to allow a fallback to a type match if no explicit name has been
	 * specified. The default name (i.e. the field name or bean property name) will
	 * still be checked first; if a bean of that name exists, it will be taken.
	 * However, if no bean of that name exists, a by-type resolution of the
	 * dependency will be attempted if this flag is "true".
	 * <p>Default is "true". Switch this flag to "false" in order to enforce a
	 * by-name lookup in all cases, throwing an exception in case of no name match.
	 * <p>
	 * 设置是否允许回溯到类型匹配,如果未指定明确的名称,则仍将首先检查默认名称(即字段名称或bean属性名称);如果存在该名称的bean,则将被采用然而,如果该名称没有bean存在,则如果此标志为"true"
	 * ,则会尝试依赖关系的逐个分辨率。
	 * <p>默认为"true"切换此标志为"false",以便在所有情况下执行副名称查找,如果没有名称匹配则抛出异常。
	 * 
	 * 
	 * @see org.springframework.beans.factory.config.AutowireCapableBeanFactory#resolveDependency
	 */
	public void setFallbackToDefaultTypeMatch(boolean fallbackToDefaultTypeMatch) {
		this.fallbackToDefaultTypeMatch = fallbackToDefaultTypeMatch;
	}

	/**
	 * Set whether to always use JNDI lookups equivalent to standard Java EE 5 resource
	 * injection, <b>even for {@code name} attributes and default names</b>.
	 * <p>Default is "false": Resource names are used for Spring bean lookups in the
	 * containing BeanFactory; only {@code mappedName} attributes point directly
	 * into JNDI. Switch this flag to "true" for enforcing Java EE style JNDI lookups
	 * in any case, even for {@code name} attributes and default names.
	 * <p>
	 * 设置是否始终使用等同于标准Java EE 5资源注入的JNDI查找,<b>即使是{@code name}属性和默认名称</b> <p>默认值为"false"：资源名称用于Spring bean在包含Be
	 * anFactory中查找;只有{@code mappedName}属性直接指向JNDI将此标志切换为"true",用于在任何情况下执行Java EE风格的JNDI查找,即使是{@code name}属性
	 * 和默认名称。
	 * 
	 * 
	 * @see #setJndiFactory
	 * @see #setResourceFactory
	 */
	public void setAlwaysUseJndiLookup(boolean alwaysUseJndiLookup) {
		this.alwaysUseJndiLookup = alwaysUseJndiLookup;
	}

	/**
	 * Specify the factory for objects to be injected into {@code @Resource} /
	 * {@code @WebServiceRef} / {@code @EJB} annotated fields and setter methods,
	 * <b>for {@code mappedName} attributes that point directly into JNDI</b>.
	 * This factory will also be used if "alwaysUseJndiLookup" is set to "true" in order
	 * to enforce JNDI lookups even for {@code name} attributes and default names.
	 * <p>The default is a {@link org.springframework.jndi.support.SimpleJndiBeanFactory}
	 * for JNDI lookup behavior equivalent to standard Java EE 5 resource injection.
	 * <p>
	 * 指定要注入到{@code @Resource} / {@code @WebServiceRef} / {@code @EJB}注释字段和setter方法的对象的工厂,<b>对于直接指向JNDI的{@code mappedName}
	 * 属性</b>如果"alwaysUseJndiLookup"设置为"true",为了强制执行JNDI查找,即使是{@code name}属性和默认名称,也将使用此工厂。
	 * <p>默认值为{@link orgspringframeworkjndisupportSimpleJndiBeanFactory} JNDI查找行为相当于标准的Java EE 5资源注入。
	 * 
	 * 
	 * @see #setResourceFactory
	 * @see #setAlwaysUseJndiLookup
	 */
	public void setJndiFactory(BeanFactory jndiFactory) {
		Assert.notNull(jndiFactory, "BeanFactory must not be null");
		this.jndiFactory = jndiFactory;
	}

	/**
	 * Specify the factory for objects to be injected into {@code @Resource} /
	 * {@code @WebServiceRef} / {@code @EJB} annotated fields and setter methods,
	 * <b>for {@code name} attributes and default names</b>.
	 * <p>The default is the BeanFactory that this post-processor is defined in,
	 * if any, looking up resource names as Spring bean names. Specify the resource
	 * factory explicitly for programmatic usage of this post-processor.
	 * <p>Specifying Spring's {@link org.springframework.jndi.support.SimpleJndiBeanFactory}
	 * leads to JNDI lookup behavior equivalent to standard Java EE 5 resource injection,
	 * even for {@code name} attributes and default names. This is the same behavior
	 * that the "alwaysUseJndiLookup" flag enables.
	 * <p>
	 * 指定要注入到{@code @Resource} / {@code @WebServiceRef} / {@code @EJB}注释字段和setter方法的对象的工厂,<b> {@code name}属性
	 * 和默认名称< b> <p>默认情况下,此后处理器定义的BeanFactory(如果有的话)查找资源名称作为Spring bean名称明确指定资源工厂用于此后处理器的程序化使用<p>指定Spring的{@链接orgspringframeworkjndisupportSimpleJndiBeanFactory}
	 * 导致与标准Java EE 5资源注入相当的JNDI查找行为,即使是{@code name}属性和默认名称这也是"alwaysUseJndiLookup"标志启用的行为。
	 * 
	 * 
	 * @see #setAlwaysUseJndiLookup
	 */
	public void setResourceFactory(BeanFactory resourceFactory) {
		Assert.notNull(resourceFactory, "BeanFactory must not be null");
		this.resourceFactory = resourceFactory;
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) {
		Assert.notNull(beanFactory, "BeanFactory must not be null");
		this.beanFactory = beanFactory;
		if (this.resourceFactory == null) {
			this.resourceFactory = beanFactory;
		}
		if (beanFactory instanceof ConfigurableBeanFactory) {
			this.embeddedValueResolver = new EmbeddedValueResolver((ConfigurableBeanFactory) beanFactory);
		}
	}


	@Override
	public void postProcessMergedBeanDefinition(RootBeanDefinition beanDefinition, Class<?> beanType, String beanName) {
		super.postProcessMergedBeanDefinition(beanDefinition, beanType, beanName);
		if (beanType != null) {
			InjectionMetadata metadata = findResourceMetadata(beanName, beanType, null);
			metadata.checkConfigMembers(beanDefinition);
		}
	}

	@Override
	public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
		return null;
	}

	@Override
	public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
		return true;
	}

	@Override
	public PropertyValues postProcessPropertyValues(
			PropertyValues pvs, PropertyDescriptor[] pds, Object bean, String beanName) throws BeansException {

		InjectionMetadata metadata = findResourceMetadata(beanName, bean.getClass(), pvs);
		try {
			metadata.inject(bean, beanName, pvs);
		}
		catch (Throwable ex) {
			throw new BeanCreationException(beanName, "Injection of resource dependencies failed", ex);
		}
		return pvs;
	}


	private InjectionMetadata findResourceMetadata(String beanName, final Class<?> clazz, PropertyValues pvs) {
		// Fall back to class name as cache key, for backwards compatibility with custom callers.
		String cacheKey = (StringUtils.hasLength(beanName) ? beanName : clazz.getName());
		// Quick check on the concurrent map first, with minimal locking.
		InjectionMetadata metadata = this.injectionMetadataCache.get(cacheKey);
		if (InjectionMetadata.needsRefresh(metadata, clazz)) {
			synchronized (this.injectionMetadataCache) {
				metadata = this.injectionMetadataCache.get(cacheKey);
				if (InjectionMetadata.needsRefresh(metadata, clazz)) {
					if (metadata != null) {
						metadata.clear(pvs);
					}
					try {
						metadata = buildResourceMetadata(clazz);
						this.injectionMetadataCache.put(cacheKey, metadata);
					}
					catch (NoClassDefFoundError err) {
						throw new IllegalStateException("Failed to introspect bean class [" + clazz.getName() +
								"] for resource metadata: could not find class that it depends on", err);
					}
				}
			}
		}
		return metadata;
	}

	private InjectionMetadata buildResourceMetadata(final Class<?> clazz) {
		LinkedList<InjectionMetadata.InjectedElement> elements = new LinkedList<InjectionMetadata.InjectedElement>();
		Class<?> targetClass = clazz;

		do {
			final LinkedList<InjectionMetadata.InjectedElement> currElements =
					new LinkedList<InjectionMetadata.InjectedElement>();

			ReflectionUtils.doWithLocalFields(targetClass, new ReflectionUtils.FieldCallback() {
				@Override
				public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
					if (webServiceRefClass != null && field.isAnnotationPresent(webServiceRefClass)) {
						if (Modifier.isStatic(field.getModifiers())) {
							throw new IllegalStateException("@WebServiceRef annotation is not supported on static fields");
						}
						currElements.add(new WebServiceRefElement(field, field, null));
					}
					else if (ejbRefClass != null && field.isAnnotationPresent(ejbRefClass)) {
						if (Modifier.isStatic(field.getModifiers())) {
							throw new IllegalStateException("@EJB annotation is not supported on static fields");
						}
						currElements.add(new EjbRefElement(field, field, null));
					}
					else if (field.isAnnotationPresent(Resource.class)) {
						if (Modifier.isStatic(field.getModifiers())) {
							throw new IllegalStateException("@Resource annotation is not supported on static fields");
						}
						if (!ignoredResourceTypes.contains(field.getType().getName())) {
							currElements.add(new ResourceElement(field, field, null));
						}
					}
				}
			});

			ReflectionUtils.doWithLocalMethods(targetClass, new ReflectionUtils.MethodCallback() {
				@Override
				public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
					Method bridgedMethod = BridgeMethodResolver.findBridgedMethod(method);
					if (!BridgeMethodResolver.isVisibilityBridgeMethodPair(method, bridgedMethod)) {
						return;
					}
					if (method.equals(ClassUtils.getMostSpecificMethod(method, clazz))) {
						if (webServiceRefClass != null && bridgedMethod.isAnnotationPresent(webServiceRefClass)) {
							if (Modifier.isStatic(method.getModifiers())) {
								throw new IllegalStateException("@WebServiceRef annotation is not supported on static methods");
							}
							if (method.getParameterTypes().length != 1) {
								throw new IllegalStateException("@WebServiceRef annotation requires a single-arg method: " + method);
							}
							PropertyDescriptor pd = BeanUtils.findPropertyForMethod(bridgedMethod, clazz);
							currElements.add(new WebServiceRefElement(method, bridgedMethod, pd));
						}
						else if (ejbRefClass != null && bridgedMethod.isAnnotationPresent(ejbRefClass)) {
							if (Modifier.isStatic(method.getModifiers())) {
								throw new IllegalStateException("@EJB annotation is not supported on static methods");
							}
							if (method.getParameterTypes().length != 1) {
								throw new IllegalStateException("@EJB annotation requires a single-arg method: " + method);
							}
							PropertyDescriptor pd = BeanUtils.findPropertyForMethod(bridgedMethod, clazz);
							currElements.add(new EjbRefElement(method, bridgedMethod, pd));
						}
						else if (bridgedMethod.isAnnotationPresent(Resource.class)) {
							if (Modifier.isStatic(method.getModifiers())) {
								throw new IllegalStateException("@Resource annotation is not supported on static methods");
							}
							Class<?>[] paramTypes = method.getParameterTypes();
							if (paramTypes.length != 1) {
								throw new IllegalStateException("@Resource annotation requires a single-arg method: " + method);
							}
							if (!ignoredResourceTypes.contains(paramTypes[0].getName())) {
								PropertyDescriptor pd = BeanUtils.findPropertyForMethod(bridgedMethod, clazz);
								currElements.add(new ResourceElement(method, bridgedMethod, pd));
							}
						}
					}
				}
			});

			elements.addAll(0, currElements);
			targetClass = targetClass.getSuperclass();
		}
		while (targetClass != null && targetClass != Object.class);

		return new InjectionMetadata(clazz, elements);
	}

	/**
	 * Obtain a lazily resolving resource proxy for the given name and type,
	 * delegating to {@link #getResource} on demand once a method call comes in.
	 * <p>
	 * 获取一个懒惰的资源代理,为给定的名称和类型,一旦一个方法调用进入,就按需要委派给{@link #getResource}
	 * 
	 * 
	 * @param element the descriptor for the annotated field/method
	 * @param requestingBeanName the name of the requesting bean
	 * @return the resource object (never {@code null})
	 * @since 4.2
	 * @see #getResource
	 * @see Lazy
	 */
	protected Object buildLazyResourceProxy(final LookupElement element, final String requestingBeanName) {
		TargetSource ts = new TargetSource() {
			@Override
			public Class<?> getTargetClass() {
				return element.lookupType;
			}
			@Override
			public boolean isStatic() {
				return false;
			}
			@Override
			public Object getTarget() {
				return getResource(element, requestingBeanName);
			}
			@Override
			public void releaseTarget(Object target) {
			}
		};
		ProxyFactory pf = new ProxyFactory();
		pf.setTargetSource(ts);
		if (element.lookupType.isInterface()) {
			pf.addInterface(element.lookupType);
		}
		ClassLoader classLoader = (this.beanFactory instanceof ConfigurableBeanFactory ?
				((ConfigurableBeanFactory) this.beanFactory).getBeanClassLoader() : null);
		return pf.getProxy(classLoader);
	}

	/**
	 * Obtain the resource object for the given name and type.
	 * <p>
	 *  获取给定名称和类型的资源对象
	 * 
	 * 
	 * @param element the descriptor for the annotated field/method
	 * @param requestingBeanName the name of the requesting bean
	 * @return the resource object (never {@code null})
	 * @throws BeansException if we failed to obtain the target resource
	 */
	protected Object getResource(LookupElement element, String requestingBeanName) throws BeansException {
		if (StringUtils.hasLength(element.mappedName)) {
			return this.jndiFactory.getBean(element.mappedName, element.lookupType);
		}
		if (this.alwaysUseJndiLookup) {
			return this.jndiFactory.getBean(element.name, element.lookupType);
		}
		if (this.resourceFactory == null) {
			throw new NoSuchBeanDefinitionException(element.lookupType,
					"No resource factory configured - specify the 'resourceFactory' property");
		}
		return autowireResource(this.resourceFactory, element, requestingBeanName);
	}

	/**
	 * Obtain a resource object for the given name and type through autowiring
	 * based on the given factory.
	 * <p>
	 *  获取给定名称的资源对象,并根据给定的工厂进行自动布线
	 * 
	 * 
	 * @param factory the factory to autowire against
	 * @param element the descriptor for the annotated field/method
	 * @param requestingBeanName the name of the requesting bean
	 * @return the resource object (never {@code null})
	 * @throws BeansException if we failed to obtain the target resource
	 */
	protected Object autowireResource(BeanFactory factory, LookupElement element, String requestingBeanName)
			throws BeansException {

		Object resource;
		Set<String> autowiredBeanNames;
		String name = element.name;

		if (this.fallbackToDefaultTypeMatch && element.isDefaultName &&
				factory instanceof AutowireCapableBeanFactory && !factory.containsBean(name)) {
			autowiredBeanNames = new LinkedHashSet<String>();
			resource = ((AutowireCapableBeanFactory) factory).resolveDependency(
					element.getDependencyDescriptor(), requestingBeanName, autowiredBeanNames, null);
		}
		else {
			resource = factory.getBean(name, element.lookupType);
			autowiredBeanNames = Collections.singleton(name);
		}

		if (factory instanceof ConfigurableBeanFactory) {
			ConfigurableBeanFactory beanFactory = (ConfigurableBeanFactory) factory;
			for (String autowiredBeanName : autowiredBeanNames) {
				if (beanFactory.containsBean(autowiredBeanName)) {
					beanFactory.registerDependentBean(autowiredBeanName, requestingBeanName);
				}
			}
		}

		return resource;
	}


	/**
	 * Class representing generic injection information about an annotated field
	 * or setter method, supporting @Resource and related annotations.
	 * <p>
	 *  表示关于注释字段或setter方法的通用注入信息的类,支持@Resource和相关注释
	 * 
	 */
	protected abstract class LookupElement extends InjectionMetadata.InjectedElement {

		protected String name;

		protected boolean isDefaultName = false;

		protected Class<?> lookupType;

		protected String mappedName;

		public LookupElement(Member member, PropertyDescriptor pd) {
			super(member, pd);
		}

		/**
		 * Return the resource name for the lookup.
		 * <p>
		 *  返回查找的资源名称
		 * 
		 */
		public final String getName() {
			return this.name;
		}

		/**
		 * Return the desired type for the lookup.
		 * <p>
		 *  返回所需的查找类型
		 * 
		 */
		public final Class<?> getLookupType() {
			return this.lookupType;
		}

		/**
		 * Build a DependencyDescriptor for the underlying field/method.
		 * <p>
		 *  构建基础字段/方法的依赖描述符
		 * 
		 */
		public final DependencyDescriptor getDependencyDescriptor() {
			if (this.isField) {
				return new LookupDependencyDescriptor((Field) this.member, this.lookupType);
			}
			else {
				return new LookupDependencyDescriptor((Method) this.member, this.lookupType);
			}
		}
	}


	/**
	 * Class representing injection information about an annotated field
	 * or setter method, supporting the @Resource annotation.
	 * <p>
	 *  代表关于注释字段或setter方法的注入信息的类,支持@Resource注释
	 * 
	 */
	private class ResourceElement extends LookupElement {

		private final boolean lazyLookup;

		public ResourceElement(Member member, AnnotatedElement ae, PropertyDescriptor pd) {
			super(member, pd);
			Resource resource = ae.getAnnotation(Resource.class);
			String resourceName = resource.name();
			Class<?> resourceType = resource.type();
			this.isDefaultName = !StringUtils.hasLength(resourceName);
			if (this.isDefaultName) {
				resourceName = this.member.getName();
				if (this.member instanceof Method && resourceName.startsWith("set") && resourceName.length() > 3) {
					resourceName = Introspector.decapitalize(resourceName.substring(3));
				}
			}
			else if (embeddedValueResolver != null) {
				resourceName = embeddedValueResolver.resolveStringValue(resourceName);
			}
			if (resourceType != null && Object.class != resourceType) {
				checkResourceType(resourceType);
			}
			else {
				// No resource type specified... check field/method.
				resourceType = getResourceType();
			}
			this.name = resourceName;
			this.lookupType = resourceType;
			String lookupValue = (lookupAttribute != null ?
					(String) ReflectionUtils.invokeMethod(lookupAttribute, resource) : null);
			this.mappedName = (StringUtils.hasLength(lookupValue) ? lookupValue : resource.mappedName());
			Lazy lazy = ae.getAnnotation(Lazy.class);
			this.lazyLookup = (lazy != null && lazy.value());
		}

		@Override
		protected Object getResourceToInject(Object target, String requestingBeanName) {
			return (this.lazyLookup ? buildLazyResourceProxy(this, requestingBeanName) :
					getResource(this, requestingBeanName));
		}
	}


	/**
	 * Class representing injection information about an annotated field
	 * or setter method, supporting the @WebServiceRef annotation.
	 * <p>
	 * 表示有关注释字段或setter方法的注入信息的类,支持@WebServiceRef注释
	 * 
	 */
	private class WebServiceRefElement extends LookupElement {

		private final Class<?> elementType;

		private final String wsdlLocation;

		public WebServiceRefElement(Member member, AnnotatedElement ae, PropertyDescriptor pd) {
			super(member, pd);
			WebServiceRef resource = ae.getAnnotation(WebServiceRef.class);
			String resourceName = resource.name();
			Class<?> resourceType = resource.type();
			this.isDefaultName = !StringUtils.hasLength(resourceName);
			if (this.isDefaultName) {
				resourceName = this.member.getName();
				if (this.member instanceof Method && resourceName.startsWith("set") && resourceName.length() > 3) {
					resourceName = Introspector.decapitalize(resourceName.substring(3));
				}
			}
			if (resourceType != null && Object.class != resourceType) {
				checkResourceType(resourceType);
			}
			else {
				// No resource type specified... check field/method.
				resourceType = getResourceType();
			}
			this.name = resourceName;
			this.elementType = resourceType;
			if (Service.class.isAssignableFrom(resourceType)) {
				this.lookupType = resourceType;
			}
			else {
				this.lookupType = resource.value();
			}
			this.mappedName = resource.mappedName();
			this.wsdlLocation = resource.wsdlLocation();
		}

		@Override
		protected Object getResourceToInject(Object target, String requestingBeanName) {
			Service service;
			try {
				service = (Service) getResource(this, requestingBeanName);
			}
			catch (NoSuchBeanDefinitionException notFound) {
				// Service to be created through generated class.
				if (Service.class == this.lookupType) {
					throw new IllegalStateException("No resource with name '" + this.name + "' found in context, " +
							"and no specific JAX-WS Service subclass specified. The typical solution is to either specify " +
							"a LocalJaxWsServiceFactoryBean with the given name or to specify the (generated) Service " +
							"subclass as @WebServiceRef(...) value.");
				}
				if (StringUtils.hasLength(this.wsdlLocation)) {
					try {
						Constructor<?> ctor = this.lookupType.getConstructor(URL.class, QName.class);
						WebServiceClient clientAnn = this.lookupType.getAnnotation(WebServiceClient.class);
						if (clientAnn == null) {
							throw new IllegalStateException("JAX-WS Service class [" + this.lookupType.getName() +
									"] does not carry a WebServiceClient annotation");
						}
						service = (Service) BeanUtils.instantiateClass(ctor,
								new URL(this.wsdlLocation), new QName(clientAnn.targetNamespace(), clientAnn.name()));
					}
					catch (NoSuchMethodException ex) {
						throw new IllegalStateException("JAX-WS Service class [" + this.lookupType.getName() +
								"] does not have a (URL, QName) constructor. Cannot apply specified WSDL location [" +
								this.wsdlLocation + "].");
					}
					catch (MalformedURLException ex) {
						throw new IllegalArgumentException(
								"Specified WSDL location [" + this.wsdlLocation + "] isn't a valid URL");
					}
				}
				else {
					service = (Service) BeanUtils.instantiateClass(this.lookupType);
				}
			}
			return service.getPort(this.elementType);
		}
	}


	/**
	 * Class representing injection information about an annotated field
	 * or setter method, supporting the @EJB annotation.
	 * <p>
	 *  表示关于注释字段或设置器方法的注入信息的类,支持@EJB注释
	 * 
	 */
	private class EjbRefElement extends LookupElement {

		private final String beanName;

		public EjbRefElement(Member member, AnnotatedElement ae, PropertyDescriptor pd) {
			super(member, pd);
			EJB resource = ae.getAnnotation(EJB.class);
			String resourceBeanName = resource.beanName();
			String resourceName = resource.name();
			this.isDefaultName = !StringUtils.hasLength(resourceName);
			if (this.isDefaultName) {
				resourceName = this.member.getName();
				if (this.member instanceof Method && resourceName.startsWith("set") && resourceName.length() > 3) {
					resourceName = Introspector.decapitalize(resourceName.substring(3));
				}
			}
			Class<?> resourceType = resource.beanInterface();
			if (resourceType != null && Object.class != resourceType) {
				checkResourceType(resourceType);
			}
			else {
				// No resource type specified... check field/method.
				resourceType = getResourceType();
			}
			this.beanName = resourceBeanName;
			this.name = resourceName;
			this.lookupType = resourceType;
			this.mappedName = resource.mappedName();
		}

		@Override
		protected Object getResourceToInject(Object target, String requestingBeanName) {
			if (StringUtils.hasLength(this.beanName)) {
				if (beanFactory != null && beanFactory.containsBean(this.beanName)) {
					// Local match found for explicitly specified local bean name.
					Object bean = beanFactory.getBean(this.beanName, this.lookupType);
					if (beanFactory instanceof ConfigurableBeanFactory) {
						((ConfigurableBeanFactory) beanFactory).registerDependentBean(this.beanName, requestingBeanName);
					}
					return bean;
				}
				else if (this.isDefaultName && !StringUtils.hasLength(this.mappedName)) {
					throw new NoSuchBeanDefinitionException(this.beanName,
							"Cannot resolve 'beanName' in local BeanFactory. Consider specifying a general 'name' value instead.");
				}
			}
			// JNDI name lookup - may still go to a local BeanFactory.
			return getResource(this, requestingBeanName);
		}
	}


	/**
	 * Extension of the DependencyDescriptor class,
	 * overriding the dependency type with the specified resource type.
	 * <p>
	 *  扩展DependencyDescriptor类,用指定的资源类型覆盖依赖关系类型
	 */
	private static class LookupDependencyDescriptor extends DependencyDescriptor {

		private final Class<?> lookupType;

		public LookupDependencyDescriptor(Field field, Class<?> lookupType) {
			super(field, true);
			this.lookupType = lookupType;
		}

		public LookupDependencyDescriptor(Method method, Class<?> lookupType) {
			super(new MethodParameter(method, 0), true);
			this.lookupType = lookupType;
		}

		@Override
		public Class<?> getDependencyType() {
			return this.lookupType;
		}
	}

}
