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

package org.springframework.jndi.support;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanNotOfRequiredTypeException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.core.ResolvableType;
import org.springframework.jndi.JndiLocatorSupport;
import org.springframework.jndi.TypeMismatchNamingException;

/**
 * Simple JNDI-based implementation of Spring's
 * {@link org.springframework.beans.factory.BeanFactory} interface.
 * Does not support enumerating bean definitions, hence doesn't implement
 * the {@link org.springframework.beans.factory.ListableBeanFactory} interface.
 *
 * <p>This factory resolves given bean names as JNDI names within the
 * J2EE application's "java:comp/env/" namespace. It caches the resolved
 * types for all obtained objects, and optionally also caches shareable
 * objects (if they are explicitly marked as
 * {@link #addShareableResource shareable resource}.
 *
 * <p>The main intent of this factory is usage in combination with Spring's
 * {@link org.springframework.context.annotation.CommonAnnotationBeanPostProcessor},
 * configured as "resourceFactory" for resolving {@code @Resource}
 * annotations as JNDI objects without intermediate bean definitions.
 * It may be used for similar lookup scenarios as well, of course,
 * in particular if BeanFactory-style type checking is required.
 *
 * <p>
 * 简单的基于JNDI的Spring的{@link orgspringframeworkbeansfactoryBeanFactory}接口的实现不支持枚举bean定义,因此不实现{@link orgspringframeworkbeansfactoryListableBeanFactory}
 * 接口。
 * 
 *  <p>此工厂将J2EE应用程序的"java：comp / env /"命名空间中的给定bean名称解析为JNDI名称它缓存所有获取对象的已解析类型,并可选地还缓存可共享对象(如果它们被明确标记为{@链接#addShareableResource可共享资源}
 * 。
 * 
 * <p>这个工厂的主要意图是与Spring的{@link orgspringframeworkcontextannotationCommonAnnotationBeanPostProcessor}结合使用
 * ,配置为"resourceFactory",用于将{@code @Resource}注释解析为没有中间bean定义的JNDI对象它可以用于类似的查找方案当然,特别是如果需要BeanFactory样式类型
 * 
 * @author Juergen Hoeller
 * @since 2.5
 * @see org.springframework.beans.factory.support.DefaultListableBeanFactory
 * @see org.springframework.context.annotation.CommonAnnotationBeanPostProcessor
 */
public class SimpleJndiBeanFactory extends JndiLocatorSupport implements BeanFactory {

	/** JNDI names of resources that are known to be shareable, i.e. can be cached */
	private final Set<String> shareableResources = new HashSet<String>();

	/** Cache of shareable singleton objects: bean name --> bean instance */
	private final Map<String, Object> singletonObjects = new HashMap<String, Object>();

	/** Cache of the types of nonshareable resources: bean name --> bean type */
	private final Map<String, Class<?>> resourceTypes = new HashMap<String, Class<?>>();


	public SimpleJndiBeanFactory() {
		setResourceRef(true);
	}


	/**
	 * Add the name of a shareable JNDI resource,
	 * which this factory is allowed to cache once obtained.
	 * <p>
	 * 检查。
	 * 
	 * 
	 * @param shareableResource the JNDI name
	 * (typically within the "java:comp/env/" namespace)
	 */
	public void addShareableResource(String shareableResource) {
		this.shareableResources.add(shareableResource);
	}

	/**
	 * Set a list of names of shareable JNDI resources,
	 * which this factory is allowed to cache once obtained.
	 * <p>
	 *  添加一个可共享的JNDI资源的名称,这个工厂允许缓存一次获得
	 * 
	 * 
	 * @param shareableResources the JNDI names
	 * (typically within the "java:comp/env/" namespace)
	 */
	public void setShareableResources(String... shareableResources) {
		this.shareableResources.addAll(Arrays.asList(shareableResources));
	}


	//---------------------------------------------------------------------
	// Implementation of BeanFactory interface
	//---------------------------------------------------------------------


	@Override
	public Object getBean(String name) throws BeansException {
		return getBean(name, Object.class);
	}

	@Override
	public <T> T getBean(String name, Class<T> requiredType) throws BeansException {
		try {
			if (isSingleton(name)) {
				return doGetSingleton(name, requiredType);
			}
			else {
				return lookup(name, requiredType);
			}
		}
		catch (NameNotFoundException ex) {
			throw new NoSuchBeanDefinitionException(name, "not found in JNDI environment");
		}
		catch (TypeMismatchNamingException ex) {
			throw new BeanNotOfRequiredTypeException(name, ex.getRequiredType(), ex.getActualType());
		}
		catch (NamingException ex) {
			throw new BeanDefinitionStoreException("JNDI environment", name, "JNDI lookup failed", ex);
		}
	}

	@Override
	public <T> T getBean(Class<T> requiredType) throws BeansException {
		return getBean(requiredType.getSimpleName(), requiredType);
	}

	@Override
	public Object getBean(String name, Object... args) throws BeansException {
		if (args != null) {
			throw new UnsupportedOperationException(
					"SimpleJndiBeanFactory does not support explicit bean creation arguments");
		}
		return getBean(name);
	}

	@Override
	public <T> T getBean(Class<T> requiredType, Object... args) throws BeansException {
		if (args != null) {
			throw new UnsupportedOperationException(
					"SimpleJndiBeanFactory does not support explicit bean creation arguments");
		}
		return getBean(requiredType);
	}

	@Override
	public boolean containsBean(String name) {
		if (this.singletonObjects.containsKey(name) || this.resourceTypes.containsKey(name)) {
			return true;
		}
		try {
			doGetType(name);
			return true;
		}
		catch (NamingException ex) {
			return false;
		}
	}

	@Override
	public boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
		return this.shareableResources.contains(name);
	}

	@Override
	public boolean isPrototype(String name) throws NoSuchBeanDefinitionException {
		return !this.shareableResources.contains(name);
	}

	@Override
	public boolean isTypeMatch(String name, ResolvableType typeToMatch) throws NoSuchBeanDefinitionException {
		Class<?> type = getType(name);
		return (type != null && typeToMatch.isAssignableFrom(type));
	}

	@Override
	public boolean isTypeMatch(String name, Class<?> typeToMatch) throws NoSuchBeanDefinitionException {
		Class<?> type = getType(name);
		return (typeToMatch == null || (type != null && typeToMatch.isAssignableFrom(type)));
	}

	@Override
	public Class<?> getType(String name) throws NoSuchBeanDefinitionException {
		try {
			return doGetType(name);
		}
		catch (NameNotFoundException ex) {
			throw new NoSuchBeanDefinitionException(name, "not found in JNDI environment");
		}
		catch (NamingException ex) {
			return null;
		}
	}

	@Override
	public String[] getAliases(String name) {
		return new String[0];
	}


	@SuppressWarnings("unchecked")
	private <T> T doGetSingleton(String name, Class<T> requiredType) throws NamingException {
		synchronized (this.singletonObjects) {
			if (this.singletonObjects.containsKey(name)) {
				Object jndiObject = this.singletonObjects.get(name);
				if (requiredType != null && !requiredType.isInstance(jndiObject)) {
					throw new TypeMismatchNamingException(
							convertJndiName(name), requiredType, (jndiObject != null ? jndiObject.getClass() : null));
				}
				return (T) jndiObject;
			}
			T jndiObject = lookup(name, requiredType);
			this.singletonObjects.put(name, jndiObject);
			return jndiObject;
		}
	}

	private Class<?> doGetType(String name) throws NamingException {
		if (isSingleton(name)) {
			Object jndiObject = doGetSingleton(name, null);
			return (jndiObject != null ? jndiObject.getClass() : null);
		}
		else {
			synchronized (this.resourceTypes) {
				if (this.resourceTypes.containsKey(name)) {
					return this.resourceTypes.get(name);
				}
				else {
					Object jndiObject = lookup(name, null);
					Class<?> type = (jndiObject != null ? jndiObject.getClass() : null);
					this.resourceTypes.put(name, type);
					return type;
				}
			}
		}
	}

}
