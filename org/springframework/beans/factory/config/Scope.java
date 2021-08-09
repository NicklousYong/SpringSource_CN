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

package org.springframework.beans.factory.config;

import org.springframework.beans.factory.ObjectFactory;

/**
 * Strategy interface used by a {@link ConfigurableBeanFactory},
 * representing a target scope to hold bean instances in.
 * This allows for extending the BeanFactory's standard scopes
 * {@link ConfigurableBeanFactory#SCOPE_SINGLETON "singleton"} and
 * {@link ConfigurableBeanFactory#SCOPE_PROTOTYPE "prototype"}
 * with custom further scopes, registered for a
 * {@link ConfigurableBeanFactory#registerScope(String, Scope) specific key}.
 *
 * <p>{@link org.springframework.context.ApplicationContext} implementations
 * such as a {@link org.springframework.web.context.WebApplicationContext}
 * may register additional standard scopes specific to their environment,
 * e.g. {@link org.springframework.web.context.WebApplicationContext#SCOPE_REQUEST "request"}
 * and {@link org.springframework.web.context.WebApplicationContext#SCOPE_SESSION "session"},
 * based on this Scope SPI.
 *
 * <p>Even if its primary use is for extended scopes in a web environment,
 * this SPI is completely generic: It provides the ability to get and put
 * objects from any underlying storage mechanism, such as an HTTP session
 * or a custom conversation mechanism. The name passed into this class's
 * {@code get} and {@code remove} methods will identify the
 * target object in the current scope.
 *
 * <p>{@code Scope} implementations are expected to be thread-safe.
 * One {@code Scope} instance can be used with multiple bean factories
 * at the same time, if desired (unless it explicitly wants to be aware of
 * the containing BeanFactory), with any number of threads accessing
 * the {@code Scope} concurrently from any number of factories.
 *
 * <p>
 * 由{@link ConfigurableBeanFactory}使用的策略界面,表示用于保存bean实例的目标作用域。
 * 这允许扩展BeanFactory的标准范围{@link ConfigurableBeanFactory#SCOPE_SINGLETON"singleton"}和{@link ConfigurableBeanFactory#SCOPE_PROTOTYPE"prototype"}
 * 自定义进一步的范围,注册了一个{@link ConfigurableBeanFactory#registerScope(String,Scope)专用密钥}。
 * 由{@link ConfigurableBeanFactory}使用的策略界面,表示用于保存bean实例的目标作用域。
 * 
 * <p> {@ link orgspringframeworkcontextApplicationContext}等实现,例如{@link orgspringframeworkwebcontextWebApplicationContext}
 * 可以注册特定于其环境的附加标准范围,例如{@link orgspringframeworkwebcontextWebApplicationContext#SCOPE_REQUEST"request"}和
 * {@link orgspringframeworkwebcontextWebApplicationContext#SCOPE_SESSION"session"},基于此范围SPI。
 * 
 * 即使主要用于Web环境中的扩展范围,该SPI也是完全通用的：它提供从任何底层存储机制获取和放置对象的能力,例如HTTP会话或自定义会话机制名称传入此类的{@code get}和{@code remove}
 * 方法将标识当前作用域中的目标对象。
 * 
 *  <p> {@ Code Scope}实现预计是线程安全的。
 * 如果需要,一个{@code Scope}实例可以同时使用多个bean工厂(除非它明确地想要知道包含的BeanFactory) ,任意数量的线程从任意数量的工厂同时访问{@code Scope}。
 * 
 * 
 * @author Juergen Hoeller
 * @author Rob Harrop
 * @since 2.0
 * @see ConfigurableBeanFactory#registerScope
 * @see CustomScopeConfigurer
 * @see org.springframework.aop.scope.ScopedProxyFactoryBean
 * @see org.springframework.web.context.request.RequestScope
 * @see org.springframework.web.context.request.SessionScope
 */
public interface Scope {

	/**
	 * Return the object with the given name from the underlying scope,
	 * {@link org.springframework.beans.factory.ObjectFactory#getObject() creating it}
	 * if not found in the underlying storage mechanism.
	 * <p>This is the central operation of a Scope, and the only operation
	 * that is absolutely required.
	 * <p>
	 * 从基础范围返回具有给定名称的对象,{@link orgspringframeworkbeansfactoryObjectFactory#getObject()创建它}如果没有在底层存储机制中找到<p>这
	 * 是Scope的中心操作,唯一的操作是绝对的需要。
	 * 
	 * 
	 * @param name the name of the object to retrieve
	 * @param objectFactory the {@link ObjectFactory} to use to create the scoped
	 * object if it is not present in the underlying storage mechanism
	 * @return the desired object (never {@code null})
	 */
	Object get(String name, ObjectFactory<?> objectFactory);

	/**
	 * Remove the object with the given {@code name} from the underlying scope.
	 * <p>Returns {@code null} if no object was found; otherwise
	 * returns the removed {@code Object}.
	 * <p>Note that an implementation should also remove a registered destruction
	 * callback for the specified object, if any. It does, however, <i>not</i>
	 * need to <i>execute</i> a registered destruction callback in this case,
	 * since the object will be destroyed by the caller (if appropriate).
	 * <p><b>Note: This is an optional operation.</b> Implementations may throw
	 * {@link UnsupportedOperationException} if they do not support explicitly
	 * removing an object.
	 * <p>
	 * 从底层范围中删除带有{@code name}的对象<p>如果没有找到对象,返回{@code null};否则返回删除的{@code Object} <p>注意,一个实现还应该删除指定对象的注册的销毁回调
	 * (如果有的话),但是,<i>不需要<i>执行<在这种情况下,/ i>注册的销毁回调,因为对象将被调用者销毁(如果适用)<p> <b>注意：这是一个可选操作</b>实现可能会抛出{@link UnsupportedOperationException}
	 *  if它们不支持显式删除对象。
	 * 
	 * 
	 * @param name the name of the object to remove
	 * @return the removed object, or {@code null} if no object was present
	 * @see #registerDestructionCallback
	 */
	Object remove(String name);

	/**
	 * Register a callback to be executed on destruction of the specified
	 * object in the scope (or at destruction of the entire scope, if the
	 * scope does not destroy individual objects but rather only terminates
	 * in its entirety).
	 * <p><b>Note: This is an optional operation.</b> This method will only
	 * be called for scoped beans with actual destruction configuration
	 * (DisposableBean, destroy-method, DestructionAwareBeanPostProcessor).
	 * Implementations should do their best to execute a given callback
	 * at the appropriate time. If such a callback is not supported by the
	 * underlying runtime environment at all, the callback <i>must be
	 * ignored and a corresponding warning should be logged</i>.
	 * <p>Note that 'destruction' refers to automatic destruction of
	 * the object as part of the scope's own lifecycle, not to the individual
	 * scoped object having been explicitly removed by the application.
	 * If a scoped object gets removed via this facade's {@link #remove(String)}
	 * method, any registered destruction callback should be removed as well,
	 * assuming that the removed object will be reused or manually destroyed.
	 * <p>
	 * 注册要在范围内销毁指定对象(或整个范围的销毁,如果范围不破坏单个对象,而只是完全终止)的执行的回调)<p> <b>注意：这是一个可选的操作</b>这个方法只能用于具有实际销毁配置的作用域bean(Dis
	 * posableBean,destroy-method,DestructionAwareBeanPostProcessor)。
	 * 实现应该尽可能地在适当的时候执行给定的回调如果不支持这样的回调通过底层运行时环境,回调<i>必须被忽略,并且应该记录相应的警告</i><p>请注意,"破坏"是指作为范围本身生命周期的一部分自动销毁对象,
	 * 而不是由应用程序显式删除的各个作用域对象如果通过此外观的{@link #remove (String)}方法,也应该删除任何注册的销毁回调,假设删除的对象将被重用或手动销毁。
	 * 
	 * 
	 * @param name the name of the object to execute the destruction callback for
	 * @param callback the destruction callback to be executed.
	 * Note that the passed-in Runnable will never throw an exception,
	 * so it can safely be executed without an enclosing try-catch block.
	 * Furthermore, the Runnable will usually be serializable, provided
	 * that its target object is serializable as well.
	 * @see org.springframework.beans.factory.DisposableBean
	 * @see org.springframework.beans.factory.support.AbstractBeanDefinition#getDestroyMethodName()
	 * @see DestructionAwareBeanPostProcessor
	 */
	void registerDestructionCallback(String name, Runnable callback);

	/**
	 * Resolve the contextual object for the given key, if any.
	 * E.g. the HttpServletRequest object for key "request".
	 * <p>
	 * 解决给定键的上下文对象,如果有任何例如HttpServletRequest对象的键"请求"
	 * 
	 * 
	 * @param key the contextual key
	 * @return the corresponding object, or {@code null} if none found
	 */
	Object resolveContextualObject(String key);

	/**
	 * Return the <em>conversation ID</em> for the current underlying scope, if any.
	 * <p>The exact meaning of the conversation ID depends on the underlying
	 * storage mechanism. In the case of session-scoped objects, the
	 * conversation ID would typically be equal to (or derived from) the
	 * {@link javax.servlet.http.HttpSession#getId() session ID}; in the
	 * case of a custom conversation that sits within the overall session,
	 * the specific ID for the current conversation would be appropriate.
	 * <p><b>Note: This is an optional operation.</b> It is perfectly valid to
	 * return {@code null} in an implementation of this method if the
	 * underlying storage mechanism has no obvious candidate for such an ID.
	 * <p>
	 *  返回当前基础范围的<em>会话ID </em>(如果有)<p>会话ID的确切含义取决于底层存储机制在会话范围对象的情况下,会话ID通常为等于(或派生自){@link javaxservlethttpHttpSession#getId()session ID}
	 * ;在整个会话中的自定义会话的情况下,当前会话的具体ID将是适当的<p> <b>注意：这是一个可选操作</b>返回{@code null}在这种方法的实现中,如果底层存储机制对于这样的ID没有明显的候选者
	 * 
	 * @return the conversation ID, or {@code null} if there is no
	 * conversation ID for the current scope
	 */
	String getConversationId();

}
