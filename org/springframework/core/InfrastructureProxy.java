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

package org.springframework.core;

/**
 * Interface to be implemented by transparent resource proxies that need to be
 * considered as equal to the underlying resource, for example for consistent
 * lookup key comparisons. Note that this interface does imply such special
 * semantics and does not constitute a general-purpose mixin!
 *
 * <p>Such wrappers will automatically be unwrapped for key comparisons in
 * {@link org.springframework.transaction.support.TransactionSynchronizationManager}.
 *
 * <p>Only fully transparent proxies, e.g. for redirection or service lookups,
 * are supposed to implement this interface. Proxies that decorate the target
 * object with new behavior, such as AOP proxies, do <i>not</i> qualify here!
 *
 * <p>
 * 需要被视为等同于底层资源的透明资源代理实现的接口,例如用于一致的查找密钥比较注意,这个接口确实意味着这样的特殊语义,并不构成通用的混合！
 * 
 *  <p>在{@link orgspringframeworktransactionsupportTransactionSynchronizationManager}中,这样的包装器将自动被打开以进行密钥
 * 比较。
 * 
 * 
 * @author Juergen Hoeller
 * @since 2.5.4
 * @see org.springframework.transaction.support.TransactionSynchronizationManager
 */
public interface InfrastructureProxy {

	/**
	 * Return the underlying resource (never {@code null}).
	 * <p>
	 *  <p>只有完全透明的代理,例如用于重定向或服务查找,才能实现此接口代理使用新行为来修饰目标对象,例如AOP代理,<i>不符合此条件！
	 * 
	 */
	Object getWrappedObject();

}
