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

package org.springframework.beans.factory.support;

import java.lang.reflect.Method;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.core.ResolvableType;
import org.springframework.util.ClassUtils;

/**
 * Basic {@link AutowireCandidateResolver} that performs a full generic type
 * match with the candidate's type if the dependency is declared as a generic type
 * (e.g. Repository&lt;Customer&gt;).
 *
 * <p>This is the base class for
 * {@link org.springframework.beans.factory.annotation.QualifierAnnotationAutowireCandidateResolver},
 * providing an implementation all non-annotation-based resolution steps at this level.
 *
 * <p>
 *  如果依赖关系被声明为通用类型(例如Repository&lt; Customer&gt;),那么执行完整通用类型的基本{@link AutowireCandidateResolver}与候选者的类型相
 * 匹配。
 * 
 * <p>这是{@link orgspringframeworkbeansfactoryannotationQualifierAnnotationAutowireCandidateResolver}的基类,
 * 提供了在此级别的所有非注释分辨率步骤的实现。
 * 
 * 
 * @author Juergen Hoeller
 * @since 4.0
 */
public class GenericTypeAwareAutowireCandidateResolver implements AutowireCandidateResolver, BeanFactoryAware {

	private BeanFactory beanFactory;


	@Override
	public void setBeanFactory(BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	protected final BeanFactory getBeanFactory() {
		return this.beanFactory;
	}


	@Override
	public boolean isAutowireCandidate(BeanDefinitionHolder bdHolder, DependencyDescriptor descriptor) {
		if (!bdHolder.getBeanDefinition().isAutowireCandidate()) {
			// if explicitly false, do not proceed with any other checks
			return false;
		}
		return (descriptor == null || checkGenericTypeMatch(bdHolder, descriptor));
	}

	/**
	 * Match the given dependency type with its generic type information against the given
	 * candidate bean definition.
	 * <p>
	 *  将给定的依赖关系类型与其通用类型信息匹配给定的候选bean定义
	 * 
	 */
	protected boolean checkGenericTypeMatch(BeanDefinitionHolder bdHolder, DependencyDescriptor descriptor) {
		ResolvableType dependencyType = descriptor.getResolvableType();
		if (dependencyType.getType() instanceof Class) {
			// No generic type -> we know it's a Class type-match, so no need to check again.
			return true;
		}

		ResolvableType targetType = null;
		boolean cacheType = false;
		RootBeanDefinition rbd = null;
		if (bdHolder.getBeanDefinition() instanceof RootBeanDefinition) {
			rbd = (RootBeanDefinition) bdHolder.getBeanDefinition();
		}
		if (rbd != null) {
			targetType = rbd.targetType;
			if (targetType == null) {
				cacheType = true;
				// First, check factory method return type, if applicable
				targetType = getReturnTypeForFactoryMethod(rbd, descriptor);
				if (targetType == null) {
					RootBeanDefinition dbd = getResolvedDecoratedDefinition(rbd);
					if (dbd != null) {
						targetType = dbd.targetType;
						if (targetType == null) {
							targetType = getReturnTypeForFactoryMethod(dbd, descriptor);
						}
					}
				}
			}
		}

		if (targetType == null) {
			// Regular case: straight bean instance, with BeanFactory available.
			if (this.beanFactory != null) {
				Class<?> beanType = this.beanFactory.getType(bdHolder.getBeanName());
				if (beanType != null) {
					targetType = ResolvableType.forClass(ClassUtils.getUserClass(beanType));
				}
			}
			// Fallback: no BeanFactory set, or no type resolvable through it
			// -> best-effort match against the target class if applicable.
			if (targetType == null && rbd != null && rbd.hasBeanClass() && rbd.getFactoryMethodName() == null) {
				Class<?> beanClass = rbd.getBeanClass();
				if (!FactoryBean.class.isAssignableFrom(beanClass)) {
					targetType = ResolvableType.forClass(ClassUtils.getUserClass(beanClass));
				}
			}
		}

		if (targetType == null) {
			return true;
		}
		if (cacheType) {
			rbd.targetType = targetType;
		}
		if (descriptor.fallbackMatchAllowed() && targetType.hasUnresolvableGenerics()) {
			return true;
		}
		// Full check for complex generic type match...
		return dependencyType.isAssignableFrom(targetType);
	}

	protected RootBeanDefinition getResolvedDecoratedDefinition(RootBeanDefinition rbd) {
		BeanDefinitionHolder decDef = rbd.getDecoratedDefinition();
		if (decDef != null && this.beanFactory instanceof ConfigurableListableBeanFactory) {
			ConfigurableListableBeanFactory clbf = (ConfigurableListableBeanFactory) this.beanFactory;
			if (clbf.containsBeanDefinition(decDef.getBeanName())) {
				BeanDefinition dbd = clbf.getMergedBeanDefinition(decDef.getBeanName());
				if (dbd instanceof RootBeanDefinition) {
					return (RootBeanDefinition) dbd;
				}
			}
		}
		return null;
	}

	protected ResolvableType getReturnTypeForFactoryMethod(RootBeanDefinition rbd, DependencyDescriptor descriptor) {
		// Should typically be set for any kind of factory method, since the BeanFactory
		// pre-resolves them before reaching out to the AutowireCandidateResolver...
		Class<?> preResolved = rbd.resolvedFactoryMethodReturnType;
		if (preResolved != null) {
			return ResolvableType.forClass(preResolved);
		}
		else {
			Method resolvedFactoryMethod = rbd.getResolvedFactoryMethod();
			if (resolvedFactoryMethod != null) {
				if (descriptor.getDependencyType().isAssignableFrom(resolvedFactoryMethod.getReturnType())) {
					// Only use factory method metadata if the return type is actually expressive enough
					// for our dependency. Otherwise, the returned instance type may have matched instead
					// in case of a singleton instance having been registered with the container already.
					return ResolvableType.forMethodReturnType(resolvedFactoryMethod);
				}
			}
			return null;
		}
	}


	/**
	 * This implementation always returns {@code null}, leaving suggested value support up
	 * to subclasses.
	 * <p>
	 *  这个实现总是返回{@code null},建议值支持到子类
	 * 
	 */
	@Override
	public Object getSuggestedValue(DependencyDescriptor descriptor) {
		return null;
	}

	/**
	 * This implementation always returns {@code null}, leaving lazy resolution support up
	 * to subclasses.
	 * <p>
	 *  这个实现总是返回{@code null},使得延迟分辨率支持到子类
	 */
	@Override
	public Object getLazyResolutionProxyIfNecessary(DependencyDescriptor descriptor, String beanName) {
		return null;
	}

}
