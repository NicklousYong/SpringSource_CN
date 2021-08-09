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

package org.springframework.aop.framework;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.aopalliance.aop.Advice;

import org.springframework.aop.Advisor;
import org.springframework.aop.DynamicIntroductionAdvice;
import org.springframework.aop.IntroductionAdvisor;
import org.springframework.aop.IntroductionInfo;
import org.springframework.aop.TargetSource;
import org.springframework.aop.support.DefaultIntroductionAdvisor;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.target.EmptyTargetSource;
import org.springframework.aop.target.SingletonTargetSource;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;

/**
 * Base class for AOP proxy configuration managers.
 * These are not themselves AOP proxies, but subclasses of this class are
 * normally factories from which AOP proxy instances are obtained directly.
 *
 * <p>This class frees subclasses of the housekeeping of Advices
 * and Advisors, but doesn't actually implement proxy creation
 * methods, which are provided by subclasses.
 *
 * <p>This class is serializable; subclasses need not be.
 * This class is used to hold snapshots of proxies.
 *
 * <p>
 *  AOP代理配置管理器的基类这些本身不是AOP代理,但是该类的子类通常是直接从中获取AOP代理实例的工厂
 * 
 * <p>这个类释放了Advice和Advisors内部管理的子类,但实际上并没有实现由子类提供的代理创建方法
 * 
 *  <p>这个类是可序列化的子类不需要这个类用于保存代理的快照
 * 
 * 
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @see org.springframework.aop.framework.AopProxy
 */
public class AdvisedSupport extends ProxyConfig implements Advised {

	/** use serialVersionUID from Spring 2.0 for interoperability */
	private static final long serialVersionUID = 2651364800145442165L;


	/**
	 * Canonical TargetSource when there's no target, and behavior is
	 * supplied by the advisors.
	 * <p>
	 *  没有目标的Canonical TargetSource,顾问提供行为
	 * 
	 */
	public static final TargetSource EMPTY_TARGET_SOURCE = EmptyTargetSource.INSTANCE;


	/** Package-protected to allow direct access for efficiency */
	TargetSource targetSource = EMPTY_TARGET_SOURCE;

	/** Whether the Advisors are already filtered for the specific target class */
	private boolean preFiltered = false;

	/** The AdvisorChainFactory to use */
	AdvisorChainFactory advisorChainFactory = new DefaultAdvisorChainFactory();

	/** Cache with Method as key and advisor chain List as value */
	private transient Map<MethodCacheKey, List<Object>> methodCache;

	/**
	 * Interfaces to be implemented by the proxy. Held in List to keep the order
	 * of registration, to create JDK proxy with specified order of interfaces.
	 * <p>
	 *  要由代理实现的接口在列表中保留注册顺序,以指定的接口顺序创建JDK代理
	 * 
	 */
	private List<Class<?>> interfaces = new ArrayList<Class<?>>();

	/**
	 * List of Advisors. If an Advice is added, it will be wrapped
	 * in an Advisor before being added to this List.
	 * <p>
	 *  顾问列表如果添加了一个建议,它将被包装在顾问中,然后再添加到此列表中
	 * 
	 */
	private List<Advisor> advisors = new LinkedList<Advisor>();

	/**
	 * Array updated on changes to the advisors list, which is easier
	 * to manipulate internally.
	 * <p>
	 *  阵列更新到顾问列表的更改,这更容易在内部操作
	 * 
	 */
	private Advisor[] advisorArray = new Advisor[0];


	/**
	 * No-arg constructor for use as a JavaBean.
	 * <p>
	 *  用于JavaBean的无参数构造函数
	 * 
	 */
	public AdvisedSupport() {
		initMethodCache();
	}

	/**
	 * Create a AdvisedSupport instance with the given parameters.
	 * <p>
	 * 使用给定的参数创建一个AdvisedSupport实例
	 * 
	 * 
	 * @param interfaces the proxied interfaces
	 */
	public AdvisedSupport(Class<?>... interfaces) {
		this();
		setInterfaces(interfaces);
	}

	/**
	 * Initialize the method cache.
	 * <p>
	 *  初始化方法缓存
	 * 
	 */
	private void initMethodCache() {
		this.methodCache = new ConcurrentHashMap<MethodCacheKey, List<Object>>(32);
	}


	/**
	 * Set the given object as target.
	 * Will create a SingletonTargetSource for the object.
	 * <p>
	 *  将给定对象设置为目标将为对象创建一个SingletonTargetSource
	 * 
	 * 
	 * @see #setTargetSource
	 * @see org.springframework.aop.target.SingletonTargetSource
	 */
	public void setTarget(Object target) {
		setTargetSource(new SingletonTargetSource(target));
	}

	@Override
	public void setTargetSource(TargetSource targetSource) {
		this.targetSource = (targetSource != null ? targetSource : EMPTY_TARGET_SOURCE);
	}

	@Override
	public TargetSource getTargetSource() {
		return this.targetSource;
	}

	/**
	 * Set a target class to be proxied, indicating that the proxy
	 * should be castable to the given class.
	 * <p>Internally, an {@link org.springframework.aop.target.EmptyTargetSource}
	 * for the given target class will be used. The kind of proxy needed
	 * will be determined on actual creation of the proxy.
	 * <p>This is a replacement for setting a "targetSource" or "target",
	 * for the case where we want a proxy based on a target class
	 * (which can be an interface or a concrete class) without having
	 * a fully capable TargetSource available.
	 * <p>
	 *  将目标类设置为代理,表示代理应该转换为给定的类<p>在内部,将使用给定目标类的{@link orgspringframeworkaoptargetEmptyTargetSource}所需的代理类型将根
	 * 据实际创建代理<p>这是替代设置"targetSource"或"target"的情况,对于我们想要基于目标类(可以是接口或具体类)的代理而没有完全有能力的TargetSource可得到。
	 * 
	 * 
	 * @see #setTargetSource
	 * @see #setTarget
	 */
	public void setTargetClass(Class<?> targetClass) {
		this.targetSource = EmptyTargetSource.forClass(targetClass);
	}

	@Override
	public Class<?> getTargetClass() {
		return this.targetSource.getTargetClass();
	}

	@Override
	public void setPreFiltered(boolean preFiltered) {
		this.preFiltered = preFiltered;
	}

	@Override
	public boolean isPreFiltered() {
		return this.preFiltered;
	}

	/**
	 * Set the advisor chain factory to use.
	 * <p>Default is a {@link DefaultAdvisorChainFactory}.
	 * <p>
	 *  设置顾问链工厂使用<p>默认是{@link DefaultAdvisorChainFactory}
	 * 
	 */
	public void setAdvisorChainFactory(AdvisorChainFactory advisorChainFactory) {
		Assert.notNull(advisorChainFactory, "AdvisorChainFactory must not be null");
		this.advisorChainFactory = advisorChainFactory;
	}

	/**
	 * Return the advisor chain factory to use (never {@code null}).
	 * <p>
	 * 将顾问链工厂返回使用(从不{@code null})
	 * 
	 */
	public AdvisorChainFactory getAdvisorChainFactory() {
		return this.advisorChainFactory;
	}


	/**
	 * Set the interfaces to be proxied.
	 * <p>
	 *  将接口设置为代理
	 * 
	 */
	public void setInterfaces(Class<?>... interfaces) {
		Assert.notNull(interfaces, "Interfaces must not be null");
		this.interfaces.clear();
		for (Class<?> ifc : interfaces) {
			addInterface(ifc);
		}
	}

	/**
	 * Add a new proxied interface.
	 * <p>
	 *  添加一个新的代理界面
	 * 
	 * 
	 * @param intf the additional interface to proxy
	 */
	public void addInterface(Class<?> intf) {
		Assert.notNull(intf, "Interface must not be null");
		if (!intf.isInterface()) {
			throw new IllegalArgumentException("[" + intf.getName() + "] is not an interface");
		}
		if (!this.interfaces.contains(intf)) {
			this.interfaces.add(intf);
			adviceChanged();
		}
	}

	/**
	 * Remove a proxied interface.
	 * <p>Does nothing if the given interface isn't proxied.
	 * <p>
	 *  删除代理的界面<p>如果给定的界面不被代理,则不执行任何操作
	 * 
	 * 
	 * @param intf the interface to remove from the proxy
	 * @return {@code true} if the interface was removed; {@code false}
	 * if the interface was not found and hence could not be removed
	 */
	public boolean removeInterface(Class<?> intf) {
		return this.interfaces.remove(intf);
	}

	@Override
	public Class<?>[] getProxiedInterfaces() {
		return this.interfaces.toArray(new Class<?>[this.interfaces.size()]);
	}

	@Override
	public boolean isInterfaceProxied(Class<?> intf) {
		for (Class<?> proxyIntf : this.interfaces) {
			if (intf.isAssignableFrom(proxyIntf)) {
				return true;
			}
		}
		return false;
	}


	@Override
	public final Advisor[] getAdvisors() {
		return this.advisorArray;
	}

	@Override
	public void addAdvisor(Advisor advisor) {
		int pos = this.advisors.size();
		addAdvisor(pos, advisor);
	}

	@Override
	public void addAdvisor(int pos, Advisor advisor) throws AopConfigException {
		if (advisor instanceof IntroductionAdvisor) {
			validateIntroductionAdvisor((IntroductionAdvisor) advisor);
		}
		addAdvisorInternal(pos, advisor);
	}

	@Override
	public boolean removeAdvisor(Advisor advisor) {
		int index = indexOf(advisor);
		if (index == -1) {
			return false;
		}
		else {
			removeAdvisor(index);
			return true;
		}
	}

	@Override
	public void removeAdvisor(int index) throws AopConfigException {
		if (isFrozen()) {
			throw new AopConfigException("Cannot remove Advisor: Configuration is frozen.");
		}
		if (index < 0 || index > this.advisors.size() - 1) {
			throw new AopConfigException("Advisor index " + index + " is out of bounds: " +
					"This configuration only has " + this.advisors.size() + " advisors.");
		}

		Advisor advisor = this.advisors.get(index);
		if (advisor instanceof IntroductionAdvisor) {
			IntroductionAdvisor ia = (IntroductionAdvisor) advisor;
			// We need to remove introduction interfaces.
			for (int j = 0; j < ia.getInterfaces().length; j++) {
				removeInterface(ia.getInterfaces()[j]);
			}
		}

		this.advisors.remove(index);
		updateAdvisorArray();
		adviceChanged();
	}

	@Override
	public int indexOf(Advisor advisor) {
		Assert.notNull(advisor, "Advisor must not be null");
		return this.advisors.indexOf(advisor);
	}

	@Override
	public boolean replaceAdvisor(Advisor a, Advisor b) throws AopConfigException {
		Assert.notNull(a, "Advisor a must not be null");
		Assert.notNull(b, "Advisor b must not be null");
		int index = indexOf(a);
		if (index == -1) {
			return false;
		}
		removeAdvisor(index);
		addAdvisor(index, b);
		return true;
	}

	/**
	 * Add all of the given advisors to this proxy configuration.
	 * <p>
	 *  将所有给定的顾问添加到此代理配置
	 * 
	 * 
	 * @param advisors the advisors to register
	 */
	public void addAdvisors(Advisor... advisors) {
		addAdvisors(Arrays.asList(advisors));
	}

	/**
	 * Add all of the given advisors to this proxy configuration.
	 * <p>
	 *  将所有给定的顾问添加到此代理配置
	 * 
	 * 
	 * @param advisors the advisors to register
	 */
	public void addAdvisors(Collection<Advisor> advisors) {
		if (isFrozen()) {
			throw new AopConfigException("Cannot add advisor: Configuration is frozen.");
		}
		if (!CollectionUtils.isEmpty(advisors)) {
			for (Advisor advisor : advisors) {
				if (advisor instanceof IntroductionAdvisor) {
					validateIntroductionAdvisor((IntroductionAdvisor) advisor);
				}
				Assert.notNull(advisor, "Advisor must not be null");
				this.advisors.add(advisor);
			}
			updateAdvisorArray();
			adviceChanged();
		}
	}

	private void validateIntroductionAdvisor(IntroductionAdvisor advisor) {
		advisor.validateInterfaces();
		// If the advisor passed validation, we can make the change.
		Class<?>[] ifcs = advisor.getInterfaces();
		for (Class<?> ifc : ifcs) {
			addInterface(ifc);
		}
	}

	private void addAdvisorInternal(int pos, Advisor advisor) throws AopConfigException {
		Assert.notNull(advisor, "Advisor must not be null");
		if (isFrozen()) {
			throw new AopConfigException("Cannot add advisor: Configuration is frozen.");
		}
		if (pos > this.advisors.size()) {
			throw new IllegalArgumentException(
					"Illegal position " + pos + " in advisor list with size " + this.advisors.size());
		}
		this.advisors.add(pos, advisor);
		updateAdvisorArray();
		adviceChanged();
	}

	/**
	 * Bring the array up to date with the list.
	 * <p>
	 *  将列表更新为列表
	 * 
	 */
	protected final void updateAdvisorArray() {
		this.advisorArray = this.advisors.toArray(new Advisor[this.advisors.size()]);
	}

	/**
	 * Allows uncontrolled access to the {@link List} of {@link Advisor Advisors}.
	 * <p>Use with care, and remember to {@link #updateAdvisorArray() refresh the advisor array}
	 * and {@link #adviceChanged() fire advice changed events} when making any modifications.
	 * <p>
	 *  允许不受控制地访问{@link Advisor}的{@link列表} <p>请谨慎使用,并记住{@link #updateAdvisorArray()刷新顾问数组}和{@link #adviceChanged()fire advice已更改事件}
	 * 进行任何修改。
	 * 
	 */
	protected final List<Advisor> getAdvisorsInternal() {
		return this.advisors;
	}


	@Override
	public void addAdvice(Advice advice) throws AopConfigException {
		int pos = this.advisors.size();
		addAdvice(pos, advice);
	}

	/**
	 * Cannot add introductions this way unless the advice implements IntroductionInfo.
	 * <p>
	 *  不能以这种方式添加介绍,除非该建议实现了IntroductionInfo
	 * 
	 */
	@Override
	public void addAdvice(int pos, Advice advice) throws AopConfigException {
		Assert.notNull(advice, "Advice must not be null");
		if (advice instanceof IntroductionInfo) {
			// We don't need an IntroductionAdvisor for this kind of introduction:
			// It's fully self-describing.
			addAdvisor(pos, new DefaultIntroductionAdvisor(advice, (IntroductionInfo) advice));
		}
		else if (advice instanceof DynamicIntroductionAdvice) {
			// We need an IntroductionAdvisor for this kind of introduction.
			throw new AopConfigException("DynamicIntroductionAdvice may only be added as part of IntroductionAdvisor");
		}
		else {
			addAdvisor(pos, new DefaultPointcutAdvisor(advice));
		}
	}

	@Override
	public boolean removeAdvice(Advice advice) throws AopConfigException {
		int index = indexOf(advice);
		if (index == -1) {
			return false;
		}
		else {
			removeAdvisor(index);
			return true;
		}
	}

	@Override
	public int indexOf(Advice advice) {
		Assert.notNull(advice, "Advice must not be null");
		for (int i = 0; i < this.advisors.size(); i++) {
			Advisor advisor = this.advisors.get(i);
			if (advisor.getAdvice() == advice) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Is the given advice included in any advisor within this proxy configuration?
	 * <p>
	 *  该代理配置中的任何顾问中都会包含给定的建议吗?
	 * 
	 * 
	 * @param advice the advice to check inclusion of
	 * @return whether this advice instance is included
	 */
	public boolean adviceIncluded(Advice advice) {
		if (advice != null) {
			for (Advisor advisor : this.advisors) {
				if (advisor.getAdvice() == advice) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Count advices of the given class.
	 * <p>
	 * 计算给定类的建议
	 * 
	 * 
	 * @param adviceClass the advice class to check
	 * @return the count of the interceptors of this class or subclasses
	 */
	public int countAdvicesOfType(Class<?> adviceClass) {
		int count = 0;
		if (adviceClass != null) {
			for (Advisor advisor : this.advisors) {
				if (adviceClass.isInstance(advisor.getAdvice())) {
					count++;
				}
			}
		}
		return count;
	}


	/**
	 * Determine a list of {@link org.aopalliance.intercept.MethodInterceptor} objects
	 * for the given method, based on this configuration.
	 * <p>
	 *  根据此配置确定给定方法的{@link orgaopallianceinterceptMethodInterceptor}对象列表
	 * 
	 * 
	 * @param method the proxied method
	 * @param targetClass the target class
	 * @return List of MethodInterceptors (may also include InterceptorAndDynamicMethodMatchers)
	 */
	public List<Object> getInterceptorsAndDynamicInterceptionAdvice(Method method, Class<?> targetClass) {
		MethodCacheKey cacheKey = new MethodCacheKey(method);
		List<Object> cached = this.methodCache.get(cacheKey);
		if (cached == null) {
			cached = this.advisorChainFactory.getInterceptorsAndDynamicInterceptionAdvice(
					this, method, targetClass);
			this.methodCache.put(cacheKey, cached);
		}
		return cached;
	}

	/**
	 * Invoked when advice has changed.
	 * <p>
	 *  当建议改变时调用
	 * 
	 */
	protected void adviceChanged() {
		this.methodCache.clear();
	}

	/**
	 * Call this method on a new instance created by the no-arg constructor
	 * to create an independent copy of the configuration from the given object.
	 * <p>
	 *  在由no-arg构造函数创建的新实例上调用此方法,以从给定对象创建配置的独立副本
	 * 
	 * 
	 * @param other the AdvisedSupport object to copy configuration from
	 */
	protected void copyConfigurationFrom(AdvisedSupport other) {
		copyConfigurationFrom(other, other.targetSource, new ArrayList<Advisor>(other.advisors));
	}

	/**
	 * Copy the AOP configuration from the given AdvisedSupport object,
	 * but allow substitution of a fresh TargetSource and a given interceptor chain.
	 * <p>
	 *  从给定的AdvisedSupport对象复制AOP配置,但允许替换新的TargetSource和给定的拦截器链
	 * 
	 * 
	 * @param other the AdvisedSupport object to take proxy configuration from
	 * @param targetSource the new TargetSource
	 * @param advisors the Advisors for the chain
	 */
	protected void copyConfigurationFrom(AdvisedSupport other, TargetSource targetSource, List<Advisor> advisors) {
		copyFrom(other);
		this.targetSource = targetSource;
		this.advisorChainFactory = other.advisorChainFactory;
		this.interfaces = new ArrayList<Class<?>>(other.interfaces);
		for (Advisor advisor : advisors) {
			if (advisor instanceof IntroductionAdvisor) {
				validateIntroductionAdvisor((IntroductionAdvisor) advisor);
			}
			Assert.notNull(advisor, "Advisor must not be null");
			this.advisors.add(advisor);
		}
		updateAdvisorArray();
		adviceChanged();
	}

	/**
	 * Build a configuration-only copy of this AdvisedSupport,
	 * replacing the TargetSource
	 * <p>
	 *  构建此AdvisedSupport的仅配置副本,替换TargetSource
	 * 
	 */
	AdvisedSupport getConfigurationOnlyCopy() {
		AdvisedSupport copy = new AdvisedSupport();
		copy.copyFrom(this);
		copy.targetSource = EmptyTargetSource.forClass(getTargetClass(), getTargetSource().isStatic());
		copy.advisorChainFactory = this.advisorChainFactory;
		copy.interfaces = this.interfaces;
		copy.advisors = this.advisors;
		copy.updateAdvisorArray();
		return copy;
	}


	//---------------------------------------------------------------------
	// Serialization support
	//---------------------------------------------------------------------

	private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
		// Rely on default serialization; just initialize state after deserialization.
		ois.defaultReadObject();

		// Initialize transient fields.
		initMethodCache();
	}


	@Override
	public String toProxyConfigString() {
		return toString();
	}

	/**
	 * For debugging/diagnostic use.
	 * <p>
	 *  用于调试/诊断使用
	 * 
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(getClass().getName());
		sb.append(": ").append(this.interfaces.size()).append(" interfaces ");
		sb.append(ClassUtils.classNamesToString(this.interfaces)).append("; ");
		sb.append(this.advisors.size()).append(" advisors ");
		sb.append(this.advisors).append("; ");
		sb.append("targetSource [").append(this.targetSource).append("]; ");
		sb.append(super.toString());
		return sb.toString();
	}


	/**
	 * Simple wrapper class around a Method. Used as the key when
	 * caching methods, for efficient equals and hashCode comparisons.
	 * <p>
	 *  方法周围的简单包装类在缓存方法时用作关键字,用于高效的equals和hashCode比较
	 */
	private static final class MethodCacheKey implements Comparable<MethodCacheKey> {

		private final Method method;

		private final int hashCode;

		public MethodCacheKey(Method method) {
			this.method = method;
			this.hashCode = method.hashCode();
		}

		@Override
		public boolean equals(Object other) {
			return (this == other || (other instanceof MethodCacheKey &&
					this.method == ((MethodCacheKey) other).method));
		}

		@Override
		public int hashCode() {
			return this.hashCode;
		}

		@Override
		public String toString() {
			return this.method.toString();
		}

		@Override
		public int compareTo(MethodCacheKey other) {
			int result = this.method.getName().compareTo(other.method.getName());
			if (result == 0) {
				result = this.method.toString().compareTo(other.method.toString());
			}
			return result;
		}
	}

}
