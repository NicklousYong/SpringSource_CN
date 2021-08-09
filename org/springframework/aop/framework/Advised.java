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

package org.springframework.aop.framework;

import org.aopalliance.aop.Advice;

import org.springframework.aop.Advisor;
import org.springframework.aop.TargetClassAware;
import org.springframework.aop.TargetSource;

/**
 * Interface to be implemented by classes that hold the configuration
 * of a factory of AOP proxies. This configuration includes the
 * Interceptors and other advice, Advisors, and the proxied interfaces.
 *
 * <p>Any AOP proxy obtained from Spring can be cast to this interface to
 * allow manipulation of its AOP advice.
 *
 * <p>
 *  要由持有AOP代理工厂配置的类实现的接口此配置包括拦截器和其他建议,顾问和代理接口
 * 
 * 从Spring获取的任何AOP代理都可以转换到这个接口,以便操纵它的AOP建议
 * 
 * 
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since 13.03.2003
 * @see org.springframework.aop.framework.AdvisedSupport
 */
public interface Advised extends TargetClassAware {

	/**
	 * Return whether the Advised configuration is frozen,
	 * in which case no advice changes can be made.
	 * <p>
	 *  返回建议的配置是否被冻结,在这种情况下,不能进行任何建议更改
	 * 
	 */
	boolean isFrozen();

	/**
	 * Are we proxying the full target class instead of specified interfaces?
	 * <p>
	 *  我们是代理完整的目标类而不是指定的接口?
	 * 
	 */
	boolean isProxyTargetClass();

	/**
	 * Return the interfaces proxied by the AOP proxy.
	 * <p>Will not include the target class, which may also be proxied.
	 * <p>
	 *  返回由AOP代理代理的接口<p>不包括目标类,也可以代理
	 * 
	 */
	Class<?>[] getProxiedInterfaces();

	/**
	 * Determine whether the given interface is proxied.
	 * <p>
	 *  确定给定的接口是否被代理
	 * 
	 * 
	 * @param intf the interface to check
	 */
	boolean isInterfaceProxied(Class<?> intf);

	/**
	 * Change the {@code TargetSource} used by this {@code Advised} object.
	 * <p>Only works if the configuration isn't {@linkplain #isFrozen frozen}.
	 * <p>
	 *  更改此{@code Advised}对象使用的{@code TargetSource} <p>仅在配置不是{@linkplain #isFrozen freeze}时才起作用
	 * 
	 * 
	 * @param targetSource new TargetSource to use
	 */
	void setTargetSource(TargetSource targetSource);

	/**
	 * Return the {@code TargetSource} used by this {@code Advised} object.
	 * <p>
	 *  返回此{@codeAdvised}对象使用的{@code TargetSource}
	 * 
	 */
	TargetSource getTargetSource();

	/**
	 * Set whether the proxy should be exposed by the AOP framework as a
	 * {@link ThreadLocal} for retrieval via the {@link AopContext} class.
	 * <p>It can be necessary to expose the proxy if an advised object needs
	 * to invoke a method on itself with advice applied. Otherwise, if an
	 * advised object invokes a method on {@code this}, no advice will be applied.
	 * <p>Default is {@code false}, for optimal performance.
	 * <p>
	 * 设置代理是否应该被AOP框架公开为{@link ThreadLocal},以便通过{@link AopContext}类进行检索。
	 * 如果建议对象需要调用自己的方法,则可能需要公开代理应用建议否则,如果建议对象在{@code this}上调用方法,则不会应用任何建议<p>默认为{@code false},以获得最佳性能。
	 * 
	 */
	void setExposeProxy(boolean exposeProxy);

	/**
	 * Return whether the factory should expose the proxy as a {@link ThreadLocal}.
	 * <p>It can be necessary to expose the proxy if an advised object needs
	 * to invoke a method on itself with advice applied. Otherwise, if an
	 * advised object invokes a method on {@code this}, no advice will be applied.
	 * <p>Getting the proxy is analogous to an EJB calling {@code getEJBObject()}.
	 * <p>
	 *  返回工厂是否应该将代理公开为{@link ThreadLocal} <p>如果建议对象需要使用建议应用调用自己的方法,则可能需要公开代理否则,如果建议对象调用方法{@code this},没有建议将被
	 * 应用<p>获取代理类似于EJB调用{@code getEJBObject()}。
	 * 
	 * 
	 * @see AopContext
	 */
	boolean isExposeProxy();

	/**
	 * Set whether this proxy configuration is pre-filtered so that it only
	 * contains applicable advisors (matching this proxy's target class).
	 * <p>Default is "false". Set this to "true" if the advisors have been
	 * pre-filtered already, meaning that the ClassFilter check can be skipped
	 * when building the actual advisor chain for proxy invocations.
	 * <p>
	 * 设置此代理配置是否被预过滤,以便它只包含适用的顾问(匹配此代理的目标类)<p>默认值为"false"如果顾问已经被预先过滤,则将其设置为"true",这意味着当构建代理调用的实际顾问程序链时,可以跳过C
	 * lassFilter检查。
	 * 
	 * 
	 * @see org.springframework.aop.ClassFilter
	 */
	void setPreFiltered(boolean preFiltered);

	/**
	 * Return whether this proxy configuration is pre-filtered so that it only
	 * contains applicable advisors (matching this proxy's target class).
	 * <p>
	 *  返回此代理配置是否被预过滤,以便它只包含适用的顾问(匹配此代理的目标类)
	 * 
	 */
	boolean isPreFiltered();

	/**
	 * Return the advisors applying to this proxy.
	 * <p>
	 *  返回适用于此代理的顾问
	 * 
	 * 
	 * @return a list of Advisors applying to this proxy (never {@code null})
	 */
	Advisor[] getAdvisors();

	/**
	 * Add an advisor at the end of the advisor chain.
	 * <p>The Advisor may be an {@link org.springframework.aop.IntroductionAdvisor},
	 * in which new interfaces will be available when a proxy is next obtained
	 * from the relevant factory.
	 * <p>
	 *  在顾问链的最后添加顾问<p>顾问可能是{@link orgspringframeworkaopIntroductionAdvisor},当下一次从相关工厂获得代理时,新界面将可用
	 * 
	 * 
	 * @param advisor the advisor to add to the end of the chain
	 * @throws AopConfigException in case of invalid advice
	 */
	void addAdvisor(Advisor advisor) throws AopConfigException;

	/**
	 * Add an Advisor at the specified position in the chain.
	 * <p>
	 * 在链中的指定位置添加一个顾问
	 * 
	 * 
	 * @param advisor the advisor to add at the specified position in the chain
	 * @param pos position in chain (0 is head). Must be valid.
	 * @throws AopConfigException in case of invalid advice
	 */
	void addAdvisor(int pos, Advisor advisor) throws AopConfigException;

	/**
	 * Remove the given advisor.
	 * <p>
	 *  删除给定的顾问
	 * 
	 * 
	 * @param advisor the advisor to remove
	 * @return {@code true} if the advisor was removed; {@code false}
	 * if the advisor was not found and hence could not be removed
	 */
	boolean removeAdvisor(Advisor advisor);

	/**
	 * Remove the advisor at the given index.
	 * <p>
	 *  删除给定索引的顾问
	 * 
	 * 
	 * @param index index of advisor to remove
	 * @throws AopConfigException if the index is invalid
	 */
	void removeAdvisor(int index) throws AopConfigException;

	/**
	 * Return the index (from 0) of the given advisor,
	 * or -1 if no such advisor applies to this proxy.
	 * <p>The return value of this method can be used to index into the advisors array.
	 * <p>
	 *  返回给定顾问的索引(从0),如果没有这样的顾问适用于此代理,则返回-1 <p>此方法的返回值可用于索引到顾问数组
	 * 
	 * 
	 * @param advisor the advisor to search for
	 * @return index from 0 of this advisor, or -1 if there's no such advisor
	 */
	int indexOf(Advisor advisor);

	/**
	 * Replace the given advisor.
	 * <p><b>Note:</b> If the advisor is an {@link org.springframework.aop.IntroductionAdvisor}
	 * and the replacement is not or implements different interfaces, the proxy will need
	 * to be re-obtained or the old interfaces won't be supported and the new interface
	 * won't be implemented.
	 * <p>
	 *  替换给定的顾问<p> <b>注意：</b>如果顾问是{@link orgspringframeworkaopIntroductionAdvisor},替换不是或实现不同的接口,则需要重新获取代理或旧接
	 * 口不支持,新界面将不会被实现。
	 * 
	 * 
	 * @param a the advisor to replace
	 * @param b the advisor to replace it with
	 * @return whether it was replaced. If the advisor wasn't found in the
	 * list of advisors, this method returns {@code false} and does nothing.
	 * @throws AopConfigException in case of invalid advice
	 */
	boolean replaceAdvisor(Advisor a, Advisor b) throws AopConfigException;

	/**
	 * Add the given AOP Alliance advice to the tail of the advice (interceptor) chain.
	 * <p>This will be wrapped in a DefaultPointcutAdvisor with a pointcut that always
	 * applies, and returned from the {@code getAdvisors()} method in this wrapped form.
	 * <p>Note that the given advice will apply to all invocations on the proxy,
	 * even to the {@code toString()} method! Use appropriate advice implementations
	 * or specify appropriate pointcuts to apply to a narrower set of methods.
	 * <p>
	 * 将给定的AOP联盟建议添加到建议(拦截器)链的尾部<p>这将包含在始终适用的切入点的DefaultPointcutAdvisor中,并从{@code getAdvisors()}方法返回,请注意,给定的
	 * 建议将适用于代理上的所有调用,甚至适用于{@code toString()}方法！使用适当的通知实现或指定适当的切入点以应用于较窄的一组方法。
	 * 
	 * 
	 * @param advice advice to add to the tail of the chain
	 * @throws AopConfigException in case of invalid advice
	 * @see #addAdvice(int, Advice)
	 * @see org.springframework.aop.support.DefaultPointcutAdvisor
	 */
	void addAdvice(Advice advice) throws AopConfigException;

	/**
	 * Add the given AOP Alliance Advice at the specified position in the advice chain.
	 * <p>This will be wrapped in a {@link org.springframework.aop.support.DefaultPointcutAdvisor}
	 * with a pointcut that always applies, and returned from the {@link #getAdvisors()}
	 * method in this wrapped form.
	 * <p>Note: The given advice will apply to all invocations on the proxy,
	 * even to the {@code toString()} method! Use appropriate advice implementations
	 * or specify appropriate pointcuts to apply to a narrower set of methods.
	 * <p>
	 * 在建议链中的指定位置添加给定的AOP联盟建议<p>这将包含在{@link orgspringframeworkaopsupportDefaultPointcutAdvisor}中,该切点始终适用,并从此
	 * 处的{@link #getAdvisors()}方法返回包装表单<p>注意：给定的建议将适用于代理上的所有调用,甚至适用于{@code toString()}方法！使用适当的通知实现或指定适当的切入点以
	 * 应用于较窄的一组方法。
	 * 
	 * 
	 * @param pos index from 0 (head)
	 * @param advice advice to add at the specified position in the advice chain
	 * @throws AopConfigException in case of invalid advice
	 */
	void addAdvice(int pos, Advice advice) throws AopConfigException;

	/**
	 * Remove the Advisor containing the given advice.
	 * <p>
	 *  删除包含给定建议的顾问
	 * 
	 * 
	 * @param advice the advice to remove
	 * @return {@code true} of the advice was found and removed;
	 * {@code false} if there was no such advice
	 */
	boolean removeAdvice(Advice advice);

	/**
	 * Return the index (from 0) of the given AOP Alliance Advice,
	 * or -1 if no such advice is an advice for this proxy.
	 * <p>The return value of this method can be used to index into
	 * the advisors array.
	 * <p>
	 *  返回给定的AOP联盟建议的索引(从0),如果没有这样的建议是这个代理的建议,则返回-1 <p>该方法的返回值可用于索引到顾问数组
	 * 
	 * 
	 * @param advice AOP Alliance advice to search for
	 * @return index from 0 of this advice, or -1 if there's no such advice
	 */
	int indexOf(Advice advice);

	/**
	 * As {@code toString()} will normally be delegated to the target,
	 * this returns the equivalent for the AOP proxy.
	 * <p>
	 * 由于{@code toString()}通常被委派给目标,所以返回AOP代理的等效项
	 * 
	 * @return a string description of the proxy configuration
	 */
	String toProxyConfigString();

}
