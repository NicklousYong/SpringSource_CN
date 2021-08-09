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

package org.springframework.beans.factory;

/**
 * Interface to be implemented by objects used within a {@link BeanFactory}
 * which are themselves factories. If a bean implements this interface,
 * it is used as a factory for an object to expose, not directly as a bean
 * instance that will be exposed itself.
 *
 * <p><b>NB: A bean that implements this interface cannot be used as a
 * normal bean.</b> A FactoryBean is defined in a bean style, but the
 * object exposed for bean references ({@link #getObject()} is always
 * the object that it creates.
 *
 * <p>FactoryBeans can support singletons and prototypes, and can
 * either create objects lazily on demand or eagerly on startup.
 * The {@link SmartFactoryBean} interface allows for exposing
 * more fine-grained behavioral metadata.
 *
 * <p>This interface is heavily used within the framework itself, for
 * example for the AOP {@link org.springframework.aop.framework.ProxyFactoryBean}
 * or the {@link org.springframework.jndi.JndiObjectFactoryBean}.
 * It can be used for application components as well; however,
 * this is not common outside of infrastructure code.
 *
 * <p><b>NOTE:</b> FactoryBean objects participate in the containing
 * BeanFactory's synchronization of bean creation. There is usually no
 * need for internal synchronization other than for purposes of lazy
 * initialization within the FactoryBean itself (or the like).
 *
 * <p>
 * 由{@link BeanFactory}中使用的对象实现的接口,这些对象本身就是工厂如果一个bean实现了这个接口,它就被用作一个对象公开的工厂,而不是直接作为一个将暴露在本身的bean实例
 * 
 *  注意：实现此接口的bean不能用作普通bean。</b>一个FactoryBean是以bean样式定义的,但是对于bean引用({@link #getObject()})始终是它创建的对象
 * 
 *  FactoryBeans可以支持单例和原型,并且可以根据需要懒惰地创建对象或者在启动时热切地{@link SmartFactoryBean}界面允许暴露更多细粒度的行为元数据
 * 
 * <p>这个界面在框架本身内大量使用,例如对于AOP {@link orgspringframeworkaopframeworkProxyFactoryBean}或{@link orgspringframeworkjndiJndiObjectFactoryBean}
 * ,它也可以用于应用程序组件;然而,这在基础设施代码之外是不常见的。
 * 
 *  <p> <b>注意：</b> FactoryBean对象参与包含BeanFactory的bean创建的同步通常不需要内部同步,而不是为了在FactoryBean本身中进行延迟初始化(等等)
 * 
 * 
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since 08.03.2003
 * @see org.springframework.beans.factory.BeanFactory
 * @see org.springframework.aop.framework.ProxyFactoryBean
 * @see org.springframework.jndi.JndiObjectFactoryBean
 */
public interface FactoryBean<T> {

	/**
	 * Return an instance (possibly shared or independent) of the object
	 * managed by this factory.
	 * <p>As with a {@link BeanFactory}, this allows support for both the
	 * Singleton and Prototype design pattern.
	 * <p>If this FactoryBean is not fully initialized yet at the time of
	 * the call (for example because it is involved in a circular reference),
	 * throw a corresponding {@link FactoryBeanNotInitializedException}.
	 * <p>As of Spring 2.0, FactoryBeans are allowed to return {@code null}
	 * objects. The factory will consider this as normal value to be used; it
	 * will not throw a FactoryBeanNotInitializedException in this case anymore.
	 * FactoryBean implementations are encouraged to throw
	 * FactoryBeanNotInitializedException themselves now, as appropriate.
	 * <p>
	 * 返回由此工厂管理的对象的实例(可能是共享的或独立的)<p>与{@link BeanFactory}一样,这允许支持单例和原型设计模式<p>如果此FactoryBean尚未完全初始化在调用时(例如因为它参
	 * 与循环引用),抛出相应的{@link FactoryBeanNotInitializedException} <p>从Spring 20开始,FactoryBeans被允许返回{@code null}对
	 * 象工厂将考虑这是正常使用的价值;在这种情况下,它不会抛出FactoryBeanNotInitializedException现在FactoryBean实现现在抛出FactoryBeanNotInitia
	 * lizedException本身。
	 * 
	 * 
	 * @return an instance of the bean (can be {@code null})
	 * @throws Exception in case of creation errors
	 * @see FactoryBeanNotInitializedException
	 */
	T getObject() throws Exception;

	/**
	 * Return the type of object that this FactoryBean creates,
	 * or {@code null} if not known in advance.
	 * <p>This allows one to check for specific types of beans without
	 * instantiating objects, for example on autowiring.
	 * <p>In the case of implementations that are creating a singleton object,
	 * this method should try to avoid singleton creation as far as possible;
	 * it should rather estimate the type in advance.
	 * For prototypes, returning a meaningful type here is advisable too.
	 * <p>This method can be called <i>before</i> this FactoryBean has
	 * been fully initialized. It must not rely on state created during
	 * initialization; of course, it can still use such state if available.
	 * <p><b>NOTE:</b> Autowiring will simply ignore FactoryBeans that return
	 * {@code null} here. Therefore it is highly recommended to implement
	 * this method properly, using the current state of the FactoryBean.
	 * <p>
	 * 返回此FactoryBean创建的对象的类型,或者{@code null}(如果未提前知道)<p>这允许检查特定类型的bean而不实例化对象,例如自动布线<p>在实现的情况下正在创建一个单例对象,该方法
	 * 应尽可能避免单例创建;它应该提前估计类型对于原型,在这里返回一个有意义的类型也是可取的<p>此方法可以在</i>之前调用</i>此FactoryBean已经完全初始化它不能依赖于初始化期间创建的状态;当
	 * 然,如果可用,它仍然可以使用这样的状态<p> <b>注意：</b>自动装配将简单地忽略返回{@code null}的FactoryBeans因此,强烈建议使用FactoryBean的当前状态来正确实现此
	 * 方法。
	 * 
	 * 
	 * @return the type of object that this FactoryBean creates,
	 * or {@code null} if not known at the time of the call
	 * @see ListableBeanFactory#getBeansOfType
	 */
	Class<?> getObjectType();

	/**
	 * Is the object managed by this factory a singleton? That is,
	 * will {@link #getObject()} always return the same object
	 * (a reference that can be cached)?
	 * <p><b>NOTE:</b> If a FactoryBean indicates to hold a singleton object,
	 * the object returned from {@code getObject()} might get cached
	 * by the owning BeanFactory. Hence, do not return {@code true}
	 * unless the FactoryBean always exposes the same reference.
	 * <p>The singleton status of the FactoryBean itself will generally
	 * be provided by the owning BeanFactory; usually, it has to be
	 * defined as singleton there.
	 * <p><b>NOTE:</b> This method returning {@code false} does not
	 * necessarily indicate that returned objects are independent instances.
	 * An implementation of the extended {@link SmartFactoryBean} interface
	 * may explicitly indicate independent instances through its
	 * {@link SmartFactoryBean#isPrototype()} method. Plain {@link FactoryBean}
	 * implementations which do not implement this extended interface are
	 * simply assumed to always return independent instances if the
	 * {@code isSingleton()} implementation returns {@code false}.
	 * <p>
	 * 这个工厂管理的对象是单身吗?也就是说,{@link #getObject()}总是返回相同的对象(可以被缓存的引用)? <p> <b>注意：如果FactoryBean表示持有单例对象,则从{@code getObject()}
	 * 返回的对象可能会被拥有的BeanFactory缓存。
	 * 因此,不要返回{@code true}除非FactoryBean始终公开相同的参考资料,否则FactoryBean本身的单身份状态通常由拥有的BeanFactory提供;通常,它必须被定义为单例。
	 * <p> <b>注意：</b>返回{@code false}的方法并不一定表示返回的对象是独立的实例扩展的{@link SmartFactoryBean}接口的实现可以通过其{@link SmartFactoryBean#isPrototype()}
	 * 
	 * @return whether the exposed object is a singleton
	 * @see #getObject()
	 * @see SmartFactoryBean#isPrototype()
	 */
	boolean isSingleton();

}
