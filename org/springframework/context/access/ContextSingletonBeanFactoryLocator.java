/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2012 the original author or authors.
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

package org.springframework.context.access;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.access.BeanFactoryLocator;
import org.springframework.beans.factory.access.SingletonBeanFactoryLocator;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;

/**
 * <p>Variant of {@link org.springframework.beans.factory.access.SingletonBeanFactoryLocator}
 * which creates its internal bean factory reference as an
 * {@link org.springframework.context.ApplicationContext} instead of
 * SingletonBeanFactoryLocator's simple BeanFactory. For almost all usage scenarios,
 * this will not make a difference, since within that ApplicationContext or BeanFactory
 * you are still free to define either BeanFactory or ApplicationContext instances.
 * The main reason one would need to use this class is if bean post-processing
 * (or other ApplicationContext specific features are needed in the bean reference
 * definition itself).
 *
 * <p><strong>Note:</strong> This class uses <strong>classpath*:beanRefContext.xml</strong>
 * as the default resource location for the bean factory reference definition files.
 * It is not possible nor legal to share definitions with SingletonBeanFactoryLocator
 * at the same time.
 *
 * <p>
 * <p> {@link orgspringframeworkbeansfactoryaccessSingletonBeanFactoryLocator}的变体,它将其内部bean工厂引用创建为{@link orgspringframeworkcontextApplicationContext}
 * ,而不是SingletonBeanFactoryLocator的简单BeanFactory对于几乎所有的使用场景,这将不会有所作为,因为在ApplicationContext或BeanFactory中仍
 * 然可以定义BeanFactory或ApplicationContext实例需要使用此类的主要原因是如果bean后处理(或bean参考定义本身需要其他ApplicationContext特定功能)。
 * 
 * <p> <strong>注意：</strong>此类使用<strong> classpath *：beanRefContextxml </strong>作为bean工厂引用定义文件的默认资源位置与Sin
 * gletonBeanFactoryLocator共享定义是不可能和合法的同一时间。
 * 
 * 
 * @author Colin Sampaleanu
 * @author Juergen Hoeller
 * @see org.springframework.beans.factory.access.SingletonBeanFactoryLocator
 * @see org.springframework.context.access.DefaultLocatorFactory
 */
public class ContextSingletonBeanFactoryLocator extends SingletonBeanFactoryLocator {

	private static final String DEFAULT_RESOURCE_LOCATION = "classpath*:beanRefContext.xml";

	/** The keyed singleton instances */
	private static final Map<String, BeanFactoryLocator> instances = new HashMap<String, BeanFactoryLocator>();


	/**
	 * Returns an instance which uses the default "classpath*:beanRefContext.xml", as
	 * the name of the definition file(s). All resources returned by the current
	 * thread's context class loader's {@code getResources} method with this
	 * name will be combined to create a definition, which is just a BeanFactory.
	 * <p>
	 *  返回一个使用默认"classpath *：beanRefContextxml"的实例,作为定义文件的名称当前线程的上下文类加载器的{@code getResources}方法返回的所有资源都将被组合起
	 * 来创建一个定义,它只是一个BeanFactory。
	 * 
	 * 
	 * @return the corresponding BeanFactoryLocator instance
	 * @throws BeansException in case of factory loading failure
	 */
	public static BeanFactoryLocator getInstance() throws BeansException {
		return getInstance(null);
	}

	/**
	 * Returns an instance which uses the specified selector, as the name of the
	 * definition file(s). In the case of a name with a Spring "classpath*:" prefix,
	 * or with no prefix, which is treated the same, the current thread's context class
	 * loader's {@code getResources} method will be called with this value to get
	 * all resources having that name. These resources will then be combined to form a
	 * definition. In the case where the name uses a Spring "classpath:" prefix, or
	 * a standard URL prefix, then only one resource file will be loaded as the
	 * definition.
	 * <p>
	 * 返回一个使用指定的选择器的实例作为定义文件的名称在具有Spring"classpath *："前缀的名称的情况下,或者没有前缀,这被视为相同的当前线程的上下文将使用此值调用类加载器的{@code getResources}
	 * 方法以获取具有该名称的所有资源。
	 * 这些资源将被组合以形成定义。在名称使用Spring"classpath："前缀或标准URL的情况下前缀,那么只有一个资源文件将被加载作为定义。
	 * 
	 * 
	 * @param selector the location of the resource(s) which will be read and
	 * combined to form the definition for the BeanFactoryLocator instance.
	 * Any such files must form a valid ApplicationContext definition.
	 * @return the corresponding BeanFactoryLocator instance
	 * @throws BeansException in case of factory loading failure
	 */
	public static BeanFactoryLocator getInstance(String selector) throws BeansException {
		String resourceLocation = selector;
		if (resourceLocation == null) {
			resourceLocation = DEFAULT_RESOURCE_LOCATION;
		}

		// For backwards compatibility, we prepend "classpath*:" to the selector name if there
		// is no other prefix (i.e. "classpath*:", "classpath:", or some URL prefix).
		if (!ResourcePatternUtils.isUrl(resourceLocation)) {
			resourceLocation = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + resourceLocation;
		}

		synchronized (instances) {
			if (logger.isTraceEnabled()) {
				logger.trace("ContextSingletonBeanFactoryLocator.getInstance(): instances.hashCode=" +
						instances.hashCode() + ", instances=" + instances);
			}
			BeanFactoryLocator bfl = instances.get(resourceLocation);
			if (bfl == null) {
				bfl = new ContextSingletonBeanFactoryLocator(resourceLocation);
				instances.put(resourceLocation, bfl);
			}
			return bfl;
		}
	}


	/**
	 * Constructor which uses the specified name as the resource name
	 * of the definition file(s).
	 * <p>
	 *  使用指定名称作为定义文件的资源名称的构造方法
	 * 
	 * 
	 * @param resourceLocation the Spring resource location to use
	 * (either a URL or a "classpath:" / "classpath*:" pseudo URL)
	 */
	protected ContextSingletonBeanFactoryLocator(String resourceLocation) {
		super(resourceLocation);
	}

	/**
	 * Overrides the default method to create definition object as an ApplicationContext
	 * instead of the default BeanFactory. This does not affect what can actually
	 * be loaded by that definition.
	 * <p>The default implementation simply builds a
	 * {@link org.springframework.context.support.ClassPathXmlApplicationContext}.
	 * <p>
	 * 覆盖默认方法来创建定义对象作为ApplicationContext而不是默认的BeanFactory这不会影响该定义实际加载的内容<p>默认实现只是构建一个{@link orgspringframeworkcontextsupportClassPathXmlApplicationContext}
	 * 。
	 * 
	 */
	@Override
	protected BeanFactory createDefinition(String resourceLocation, String factoryKey) {
		return new ClassPathXmlApplicationContext(new String[] {resourceLocation}, false);
	}

	/**
	 * Overrides the default method to refresh the ApplicationContext, invoking
	 * {@link ConfigurableApplicationContext#refresh ConfigurableApplicationContext.refresh()}.
	 * <p>
	 *  覆盖默认方法来刷新ApplicationContext,调用{@link ConfigurableApplicationContext#refresh ConfigurableApplicationContextrefresh()}
	 * 。
	 * 
	 */
	@Override
	protected void initializeDefinition(BeanFactory groupDef) {
		if (groupDef instanceof ConfigurableApplicationContext) {
			((ConfigurableApplicationContext) groupDef).refresh();
		}
	}

	/**
	 * Overrides the default method to operate on an ApplicationContext, invoking
	 * {@link ConfigurableApplicationContext#refresh ConfigurableApplicationContext.close()}.
	 * <p>
	 *  覆盖在ApplicationContext上运行的默认方法,调用{@link ConfigurableApplicationContext#refresh ConfigurableApplicationContextclose()}
	 * 。
	 */
	@Override
	protected void destroyDefinition(BeanFactory groupDef, String selector) {
		if (groupDef instanceof ConfigurableApplicationContext) {
			if (logger.isTraceEnabled()) {
				logger.trace("Context group with selector '" + selector +
						"' being released, as there are no more references to it");
			}
			((ConfigurableApplicationContext) groupDef).close();
		}
	}

}
