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

package org.springframework.aop.target;

import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.aop.TargetSource;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.util.ObjectUtils;

/**
 * Base class for {@link org.springframework.aop.TargetSource} implementations
 * that are based on a Spring {@link org.springframework.beans.factory.BeanFactory},
 * delegating to Spring-managed bean instances.
 *
 * <p>Subclasses can create prototype instances or lazily access a
 * singleton target, for example. See {@link LazyInitTargetSource} and
 * {@link AbstractPrototypeBasedTargetSource}'s subclasses for concrete strategies.
 *
 * <p>BeanFactory-based TargetSources are serializable. This involves
 * disconnecting the current target and turning into a {@link SingletonTargetSource}.
 *
 * <p>
 *  基于Spring {@link orgspringframeworkbeansfactoryBeanFactory}的{@link orgspringframeworkaopTargetSource}
 * 实现的基类,委托给Spring管理的bean实例。
 * 
 * <p>子类可以创建原型实例或懒惰地访问单例对象,例如,参见{@link LazyInitTargetSource}和{@link AbstractPrototypeBasedTargetSource}的
 * 子类来实现具体的策略。
 * 
 *  <p>基于BeanFactory的TargetSources是可序列化的,这涉及到断开当前目标并转化为{@link SingletonTargetSource}
 * 
 * 
 * @author Juergen Hoeller
 * @author Rod Johnson
 * @since 1.1.4
 * @see org.springframework.beans.factory.BeanFactory#getBean
 * @see LazyInitTargetSource
 * @see PrototypeTargetSource
 * @see ThreadLocalTargetSource
 * @see CommonsPool2TargetSource
 */
public abstract class AbstractBeanFactoryBasedTargetSource implements TargetSource, BeanFactoryAware, Serializable {

	/** use serialVersionUID from Spring 1.2.7 for interoperability */
	private static final long serialVersionUID = -4721607536018568393L;


	/** Logger available to subclasses */
	protected final Log logger = LogFactory.getLog(getClass());

	/** Name of the target bean we will create on each invocation */
	private String targetBeanName;

	/** Class of the target */
	private Class<?> targetClass;

	/**
	 * BeanFactory that owns this TargetSource. We need to hold onto this
	 * reference so that we can create new prototype instances as necessary.
	 * <p>
	 *  拥有此TargetSource的BeanFactory我们需要保留此引用,以便我们可以根据需要创建新的原型实例
	 * 
	 */
	private BeanFactory beanFactory;


	/**
	 * Set the name of the target bean in the factory.
	 * <p>The target bean should not be a singleton, else the same instance will
	 * always be obtained from the factory, resulting in the same behavior as
	 * provided by {@link SingletonTargetSource}.
	 * <p>
	 *  在工厂中设置目标bean的名称<p>目标bean不应该是单例,否则始终从工厂获取相同的实例,导致与{@link SingletonTargetSource}提供的行为相同的行为
	 * 
	 * 
	 * @param targetBeanName name of the target bean in the BeanFactory
	 * that owns this interceptor
	 * @see SingletonTargetSource
	 */
	public void setTargetBeanName(String targetBeanName) {
		this.targetBeanName = targetBeanName;
	}

	/**
	 * Return the name of the target bean in the factory.
	 * <p>
	 *  返回工厂中目标bean的名称
	 * 
	 */
	public String getTargetBeanName() {
		return this.targetBeanName;
	}

	/**
	 * Specify the target class explicitly, to avoid any kind of access to the
	 * target bean (for example, to avoid initialization of a FactoryBean instance).
	 * <p>Default is to detect the type automatically, through a {@code getType}
	 * call on the BeanFactory (or even a full {@code getBean} call as fallback).
	 * <p>
	 * 明确指定目标类,以避免对目标bean进行任何访问(例如,避免初始化FactoryBean实例)<p>默认是通过BeanFactory上的{@code getType}调用自动检测类型(甚至完整的{@code getBean}
	 * 通话作为备用)。
	 * 
	 */
	public void setTargetClass(Class<?> targetClass) {
		this.targetClass = targetClass;
	}

	/**
	 * Set the owning BeanFactory. We need to save a reference so that we can
	 * use the {@code getBean} method on every invocation.
	 * <p>
	 *  设置拥有的BeanFactory我们需要保存一个引用,以便我们可以在每次调用时使用{@code getBean}方法
	 * 
	 */
	@Override
	public void setBeanFactory(BeanFactory beanFactory) {
		if (this.targetBeanName == null) {
			throw new IllegalStateException("Property 'targetBeanName' is required");
		}
		this.beanFactory = beanFactory;
	}

	/**
	 * Return the owning BeanFactory.
	 * <p>
	 *  返回拥有的BeanFactory
	 * 
	 */
	public BeanFactory getBeanFactory() {
		return this.beanFactory;
	}


	@Override
	public synchronized Class<?> getTargetClass() {
		if (this.targetClass == null && this.beanFactory != null) {
			// Determine type of the target bean.
			this.targetClass = this.beanFactory.getType(this.targetBeanName);
			if (this.targetClass == null) {
				if (logger.isTraceEnabled()) {
					logger.trace("Getting bean with name '" + this.targetBeanName + "' in order to determine type");
				}
				Object beanInstance = this.beanFactory.getBean(this.targetBeanName);
				if (beanInstance != null) {
					this.targetClass = beanInstance.getClass();
				}
			}
		}
		return this.targetClass;
	}

	@Override
	public boolean isStatic() {
		return false;
	}

	@Override
	public void releaseTarget(Object target) throws Exception {
		// Nothing to do here.
	}


	/**
	 * Copy configuration from the other AbstractBeanFactoryBasedTargetSource object.
	 * Subclasses should override this if they wish to expose it.
	 * <p>
	 *  从另一个AbstractBeanFactoryBasedTargetSource对象复制配置子类应该覆盖这个,如果他们希望公开它
	 * 
	 * @param other object to copy configuration from
	 */
	protected void copyFrom(AbstractBeanFactoryBasedTargetSource other) {
		this.targetBeanName = other.targetBeanName;
		this.targetClass = other.targetClass;
		this.beanFactory = other.beanFactory;
	}


	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (other == null || getClass() != other.getClass()) {
			return false;
		}
		AbstractBeanFactoryBasedTargetSource otherTargetSource = (AbstractBeanFactoryBasedTargetSource) other;
		return (ObjectUtils.nullSafeEquals(this.beanFactory, otherTargetSource.beanFactory) &&
				ObjectUtils.nullSafeEquals(this.targetBeanName, otherTargetSource.targetBeanName));
	}

	@Override
	public int hashCode() {
		int hashCode = getClass().hashCode();
		hashCode = 13 * hashCode + ObjectUtils.nullSafeHashCode(this.beanFactory);
		hashCode = 13 * hashCode + ObjectUtils.nullSafeHashCode(this.targetBeanName);
		return hashCode;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(getClass().getSimpleName());
		sb.append(" for target bean '").append(this.targetBeanName).append("'");
		if (this.targetClass != null) {
			sb.append(" of type [").append(this.targetClass.getName()).append("]");
		}
		return sb.toString();
	}

}
