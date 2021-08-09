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
 * Extension of the {@link FactoryBean} interface. Implementations may
 * indicate whether they always return independent instances, for the
 * case where their {@link #isSingleton()} implementation returning
 * {@code false} does not clearly indicate independent instances.
 *
 * <p>Plain {@link FactoryBean} implementations which do not implement
 * this extended interface are simply assumed to always return independent
 * instances if their {@link #isSingleton()} implementation returns
 * {@code false}; the exposed object is only accessed on demand.
 *
 * <p><b>NOTE:</b> This interface is a special purpose interface, mainly for
 * internal use within the framework and within collaborating frameworks.
 * In general, application-provided FactoryBeans should simply implement
 * the plain {@link FactoryBean} interface. New methods might be added
 * to this extended interface even in point releases.
 *
 * <p>
 * {@link FactoryBean}接口的扩展实现可能指示它们是否总是返回独立的实例,对于{@link #isSingleton()}实现返回{@code false}并不清楚指示独立实例的情况
 * 
 *  如果{@link #isSingleton()}实现返回{@code false},则简单地假设没有实现此扩展接口的普通{@link FactoryBean}实现始终返回独立实例;暴露的对象只能按需访
 * 问。
 * 
 * <p> <b>注意：</b>此接口是一个专用接口,主要用于框架内和协作框架内部使用。
 * 一般来说,应用程序提供的FactoryBeans应该简单地实现普通的{@link FactoryBean}界面New方法也可能添加到此扩展接口,即使在点释放。
 * 
 * 
 * @author Juergen Hoeller
 * @since 2.0.3
 * @see #isPrototype()
 * @see #isSingleton()
 */
public interface SmartFactoryBean<T> extends FactoryBean<T> {

	/**
	 * Is the object managed by this factory a prototype? That is,
	 * will {@link #getObject()} always return an independent instance?
	 * <p>The prototype status of the FactoryBean itself will generally
	 * be provided by the owning {@link BeanFactory}; usually, it has to be
	 * defined as singleton there.
	 * <p>This method is supposed to strictly check for independent instances;
	 * it should not return {@code true} for scoped objects or other
	 * kinds of non-singleton, non-independent objects. For this reason,
	 * this is not simply the inverted form of {@link #isSingleton()}.
	 * <p>
	 * 这个工厂管理的对象是原型吗?也就是说,{@link #getObject()}会返回一个独立的实例吗? <b> FactoryBean本身的原型状态通常由拥有的{@link BeanFactory}提供
	 * ;通常,它必须被定义为单例。
	 * <p>此方法应严格检查独立实例;它不应该为范围对象或其他类型的非单例,非独立对象返回{@code true}因此,这不仅仅是{@link #isSingleton()}的倒置形式。
	 * 
	 * 
	 * @return whether the exposed object is a prototype
	 * @see #getObject()
	 * @see #isSingleton()
	 */
	boolean isPrototype();

	/**
	 * Does this FactoryBean expect eager initialization, that is,
	 * eagerly initialize itself as well as expect eager initialization
	 * of its singleton object (if any)?
	 * <p>A standard FactoryBean is not expected to initialize eagerly:
	 * Its {@link #getObject()} will only be called for actual access, even
	 * in case of a singleton object. Returning {@code true} from this
	 * method suggests that {@link #getObject()} should be called eagerly,
	 * also applying post-processors eagerly. This may make sense in case
	 * of a {@link #isSingleton() singleton} object, in particular if
	 * post-processors expect to be applied on startup.
	 * <p>
	 * 这个FactoryBean是否期望初始化,也就是急切地初始化它自己,并期望它的单例对象(如果有的话)急切的初始化? <p>一个标准的FactoryBean不会急于初始化：它的{@link #getObject()}
	 * 只会被实际访问,即使在单例对象返回{@code true}的情况下,该方法表明{@应该热切地呼叫#getObject()},也应该热切地应用后处理器在{@link #isSingleton()singleton}
	 * 
	 * @return whether eager initialization applies
	 * @see org.springframework.beans.factory.config.ConfigurableListableBeanFactory#preInstantiateSingletons()
	 */
	boolean isEagerInit();

}
