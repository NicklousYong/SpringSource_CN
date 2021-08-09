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

package org.springframework.aop.target;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.aop.TargetSource;

/**
 * {@link org.springframework.aop.TargetSource} implementation that will
 * lazily create a user-managed object.
 *
 * <p>Creation of the lazy target object is controlled by the user by implementing
 * the {@link #createObject()} method. This {@code TargetSource} will invoke
 * this method the first time the proxy is accessed.
 *
 * <p>Useful when you need to pass a reference to some dependency to an object
 * but you don't actually want the dependency to be created until it is first used.
 * A typical scenario for this is a connection to a remote resource.
 *
 * <p>
 *  {@link orgspringframeworkaopTargetSource}实现,将懒惰地创建一个用户管理的对象
 * 
 * <p>通过实现{@link #createObject()}方法,用户控制懒惰目标对象的创建此{@code TargetSource}将在首次访问代理时调用此方法
 * 
 *  <p>当您需要传递对某个对象的一些依赖关系的引用时,但实际上并不希望在首次使用之前创建依赖关系。这种情况的典型情况是与远程资源的连接
 * 
 * 
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @since 1.2.4
 * @see #isInitialized()
 * @see #createObject()
 */
public abstract class AbstractLazyCreationTargetSource implements TargetSource {

	/** Logger available to subclasses */
	protected final Log logger = LogFactory.getLog(getClass());

	/** The lazily initialized target object */
	private Object lazyTarget;


	/**
	 * Return whether the lazy target object of this TargetSource
	 * has already been fetched.
	 * <p>
	 *  返回此TargetSource的延迟目标对象是否已被提取
	 * 
	 */
	public synchronized boolean isInitialized() {
		return (this.lazyTarget != null);
	}

	/**
	 * This default implementation returns {@code null} if the
	 * target is {@code null} (it is hasn't yet been initialized),
	 * or the target class if the target has already been initialized.
	 * <p>Subclasses may wish to override this method in order to provide
	 * a meaningful value when the target is still {@code null}.
	 * <p>
	 * 如果目标是{@code null}(它还没有被初始化),或者目标类已经被初始化,那么这个默认的实现返回{@code null} <p>子类可能希望覆盖这个方法为了在目标仍然{@code null}时提供
	 * 有意义的价值。
	 * 
	 * 
	 * @see #isInitialized()
	 */
	@Override
	public synchronized Class<?> getTargetClass() {
		return (this.lazyTarget != null ? this.lazyTarget.getClass() : null);
	}

	@Override
	public boolean isStatic() {
		return false;
	}

	/**
	 * Returns the lazy-initialized target object,
	 * creating it on-the-fly if it doesn't exist already.
	 * <p>
	 *  返回延迟初始化的目标对象,如果它不存在,则可以即时创建它
	 * 
	 * 
	 * @see #createObject()
	 */
	@Override
	public synchronized Object getTarget() throws Exception {
		if (this.lazyTarget == null) {
			logger.debug("Initializing lazy target object");
			this.lazyTarget = createObject();
		}
		return this.lazyTarget;
	}

	@Override
	public void releaseTarget(Object target) throws Exception {
		// nothing to do
	}


	/**
	 * Subclasses should implement this method to return the lazy initialized object.
	 * Called the first time the proxy is invoked.
	 * <p>
	 *  子类应该实现这个方法来返回延迟初始化的对象。第一次调用代理时被调用
	 * 
	 * @return the created object
	 * @throws Exception if creation failed
	 */
	protected abstract Object createObject() throws Exception;

}
