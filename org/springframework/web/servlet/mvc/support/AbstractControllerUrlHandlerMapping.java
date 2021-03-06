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

package org.springframework.web.servlet.mvc.support;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.springframework.web.servlet.handler.AbstractDetectingUrlHandlerMapping;

/**
 * Base class for {@link org.springframework.web.servlet.HandlerMapping} implementations
 * that derive URL paths according to conventions for specific controller types.
 *
 * <p>
 *  根据特定控制器类型的约定导出URL路径的{@link orgspringframeworkwebservletHandlerMapping}实现的基类
 * 
 * 
 * @author Juergen Hoeller
 * @since 2.5.3
 * @see ControllerClassNameHandlerMapping
 * @see ControllerBeanNameHandlerMapping
 * @deprecated as of 4.3, in favor of annotation-driven handler methods
 */
@Deprecated
public abstract class AbstractControllerUrlHandlerMapping extends AbstractDetectingUrlHandlerMapping  {

	private ControllerTypePredicate predicate = new AnnotationControllerTypePredicate();

	private Set<String> excludedPackages = Collections.singleton("org.springframework.web.servlet.mvc");

	private Set<Class<?>> excludedClasses = Collections.emptySet();


	/**
	 * Set whether to activate or deactivate detection of annotated controllers.
	 * <p>
	 * 设置是否启用或禁用注释控制器的检测
	 * 
	 */
	public void setIncludeAnnotatedControllers(boolean includeAnnotatedControllers) {
		this.predicate = (includeAnnotatedControllers ?
				new AnnotationControllerTypePredicate() : new ControllerTypePredicate());
	}

	/**
	 * Specify Java packages that should be excluded from this mapping.
	 * Any classes in such a package (or any of its subpackages) will be
	 * ignored by this HandlerMapping.
	 * <p>Default is to exclude the entire "org.springframework.web.servlet.mvc"
	 * package, including its subpackages, since none of Spring's out-of-the-box
	 * Controller implementations is a reasonable candidate for this mapping strategy.
	 * Such controllers are typically handled by a separate HandlerMapping,
	 * e.g. a {@link org.springframework.web.servlet.handler.BeanNameUrlHandlerMapping},
	 * alongside this ControllerClassNameHandlerMapping for application controllers.
	 * <p>
	 *  指定应从此映射中排除的Java软件包此类软件包(或其任何子包)中的任何类将被此HandlerMapping忽略<p>默认值是排除整个"orgspringframeworkwebservletmvc"软
	 * 件包,包括其子包,因为没有Spring的开箱即用的控制器实现是这种映射策略的合理候选。
	 * 这种控制器通常由单独的HandlerMapping处理,例如{@link orgspringframeworkwebservlethandlerBeanNameUrlHandlerMapping},以及
	 * 用于应用程序控制器的ControllerClassNameHandlerMapping。
	 * 
	 */
	public void setExcludedPackages(String... excludedPackages) {
		this.excludedPackages = (excludedPackages != null) ?
				new HashSet<String>(Arrays.asList(excludedPackages)) : new HashSet<String>();
	}

	/**
	 * Specify controller classes that should be excluded from this mapping.
	 * Any such classes will simply be ignored by this HandlerMapping.
	 * <p>
	 * 指定应从此映射中排除的控制器类任何此类将被此HandlerMapping简单地忽略
	 * 
	 */
	public void setExcludedClasses(Class<?>... excludedClasses) {
		this.excludedClasses = (excludedClasses != null) ?
				new HashSet<Class<?>>(Arrays.asList(excludedClasses)) : new HashSet<Class<?>>();
	}


	/**
	 * This implementation delegates to {@link #buildUrlsForHandler},
	 * provided that {@link #isEligibleForMapping} returns {@code true}.
	 * <p>
	 *  如果{@link #isEligibleForMapping}返回{@code true},则此实现将委托给{@link #buildUrlsForHandler}
	 * 
	 */
	@Override
	protected String[] determineUrlsForHandler(String beanName) {
		Class<?> beanClass = getApplicationContext().getType(beanName);
		if (isEligibleForMapping(beanName, beanClass)) {
			return buildUrlsForHandler(beanName, beanClass);
		}
		else {
			return null;
		}
	}

	/**
	 * Determine whether the specified controller is excluded from this mapping.
	 * <p>
	 *  确定是否从此映射中排除了指定的控制器
	 * 
	 * 
	 * @param beanName the name of the controller bean
	 * @param beanClass the concrete class of the controller bean
	 * @return whether the specified class is excluded
	 * @see #setExcludedPackages
	 * @see #setExcludedClasses
	 */
	protected boolean isEligibleForMapping(String beanName, Class<?> beanClass) {
		if (beanClass == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("Excluding controller bean '" + beanName + "' from class name mapping " +
						"because its bean type could not be determined");
			}
			return false;
		}
		if (this.excludedClasses.contains(beanClass)) {
			if (logger.isDebugEnabled()) {
				logger.debug("Excluding controller bean '" + beanName + "' from class name mapping " +
						"because its bean class is explicitly excluded: " + beanClass.getName());
			}
			return false;
		}
		String beanClassName = beanClass.getName();
		for (String packageName : this.excludedPackages) {
			if (beanClassName.startsWith(packageName)) {
				if (logger.isDebugEnabled()) {
					logger.debug("Excluding controller bean '" + beanName + "' from class name mapping " +
							"because its bean class is defined in an excluded package: " + beanClass.getName());
				}
				return false;
			}
		}
		return isControllerType(beanClass);
	}

	/**
	 * Determine whether the given bean class indicates a controller type
	 * that is supported by this mapping strategy.
	 * <p>
	 *  确定给定的b​​ean类是否指示此映射策略支持的控制器类型
	 * 
	 * 
	 * @param beanClass the class to introspect
	 */
	protected boolean isControllerType(Class<?> beanClass) {
		return this.predicate.isControllerType(beanClass);
	}

	/**
	 * Determine whether the given bean class indicates a controller type
	 * that dispatches to multiple action methods.
	 * <p>
	 *  确定给定的b​​ean类是否指示调度到多个操作方法的控制器类型
	 * 
	 * 
	 * @param beanClass the class to introspect
	 */
	protected boolean isMultiActionControllerType(Class<?> beanClass) {
		return this.predicate.isMultiActionControllerType(beanClass);
	}


	/**
	 * Abstract template method to be implemented by subclasses.
	 * <p>
	 *  抽象模板方法由子类实现
	 * 
	 * @param beanName the name of the bean
	 * @param beanClass the type of the bean
	 * @return the URLs determined for the bean
	 */
	protected abstract String[] buildUrlsForHandler(String beanName, Class<?> beanClass);

}
