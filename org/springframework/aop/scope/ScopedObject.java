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

package org.springframework.aop.scope;

import org.springframework.aop.RawTargetAccess;

/**
 * An AOP introduction interface for scoped objects.
 *
 * <p>Objects created from the {@link ScopedProxyFactoryBean} can be cast
 * to this interface, enabling access to the raw target object
 * and programmatic removal of the target object.
 *
 * <p>
 *  用于范围对象的AOP介绍界面
 * 
 * <p>从{@link ScopedProxyFactoryBean}创建的对象可以转换到此界面,从而可以访问原始目标对象并编程删除目标对象
 * 
 * 
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since 2.0
 * @see ScopedProxyFactoryBean
 */
public interface ScopedObject extends RawTargetAccess {

	/**
	 * Return the current target object behind this scoped object proxy,
	 * in its raw form (as stored in the target scope).
	 * <p>The raw target object can for example be passed to persistence
	 * providers which would not be able to handle the scoped proxy object.
	 * <p>
	 *  返回此作用域对象代理后面的当前目标对象,其原始形式(存储在目标范围中)<p>原始目标对象可以例如传递给不能处理作用域代理对象的持久性提供者
	 * 
	 * 
	 * @return the current target object behind this scoped object proxy
	 */
	Object getTargetObject();

	/**
	 * Remove this object from its target scope, for example from
	 * the backing session.
	 * <p>Note that no further calls may be made to the scoped object
	 * afterwards (at least within the current thread, that is, with
	 * the exact same target object in the target scope).
	 * <p>
	 *  从目标范围中删除此对象,例如从后台会话<p>请注意,此后可能不再对作用域对象进行任何调用(至少在当前线程内,即目标中具有完全相同的目标对象)范围)
	 */
	void removeFromScope();

}
