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

import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.aop.Advisor;
import org.springframework.aop.AopInvocationException;
import org.springframework.aop.PointcutAdvisor;
import org.springframework.aop.RawTargetAccess;
import org.springframework.aop.TargetSource;
import org.springframework.aop.support.AopUtils;
import org.springframework.cglib.core.ClassGenerator;
import org.springframework.cglib.core.CodeGenerationException;
import org.springframework.cglib.core.SpringNamingPolicy;
import org.springframework.cglib.proxy.Callback;
import org.springframework.cglib.proxy.CallbackFilter;
import org.springframework.cglib.proxy.Dispatcher;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.Factory;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.cglib.proxy.NoOp;
import org.springframework.cglib.transform.impl.UndeclaredThrowableStrategy;
import org.springframework.core.SmartClassLoader;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;

/**
 * CGLIB-based {@link AopProxy} implementation for the Spring AOP framework.
 *
 * <p>Formerly named {@code Cglib2AopProxy}, as of Spring 3.2, this class depends on
 * Spring's own internally repackaged version of CGLIB 3.</i>.
 *
 * <p>Objects of this type should be obtained through proxy factories,
 * configured by an {@link AdvisedSupport} object. This class is internal
 * to Spring's AOP framework and need not be used directly by client code.
 *
 * <p>{@link DefaultAopProxyFactory} will automatically create CGLIB-based
 * proxies if necessary, for example in case of proxying a target class
 * (see the {@link DefaultAopProxyFactory attendant javadoc} for details).
 *
 * <p>Proxies created using this class are thread-safe if the underlying
 * (target) class is thread-safe.
 *
 * <p>
 *  基于CGLIB的Spring AOP框架的{@link AopProxy}实现
 * 
 *  <p>以前命名为{@code Cglib2AopProxy},从Spring 32开始,此类依赖于Spring自己内部重新包装的CGLIB 3版本,
 * 
 * <p>此类型的对象应通过代理工厂获得,由{@link AdvisedSupport}对象配置此类是Spring的AOP框架内部,不需要直接由客户端代码使用
 * 
 *  如果需要,<p> {@ link DefaultAopProxyFactory}将自动创建基于CGLIB的代理,例如在代理目标类的情况下(有关详细信息,请参阅{@link DefaultAopProxyFactory服务器javadoc}
 * )。
 * 
 *  如果底层(目标)类是线程安全的,使用此类创建的代理是线程安全的
 * 
 * 
 * @author Rod Johnson
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @author Ramnivas Laddad
 * @author Chris Beams
 * @author Dave Syer
 * @see org.springframework.cglib.proxy.Enhancer
 * @see AdvisedSupport#setProxyTargetClass
 * @see DefaultAopProxyFactory
 */
@SuppressWarnings("serial")
class CglibAopProxy implements AopProxy, Serializable {

	// Constants for CGLIB callback array indices
	private static final int AOP_PROXY = 0;
	private static final int INVOKE_TARGET = 1;
	private static final int NO_OVERRIDE = 2;
	private static final int DISPATCH_TARGET = 3;
	private static final int DISPATCH_ADVISED = 4;
	private static final int INVOKE_EQUALS = 5;
	private static final int INVOKE_HASHCODE = 6;


	/** Logger available to subclasses; static to optimize serialization */
	protected static final Log logger = LogFactory.getLog(CglibAopProxy.class);

	/** Keeps track of the Classes that we have validated for final methods */
	private static final Map<Class<?>, Boolean> validatedClasses = new WeakHashMap<Class<?>, Boolean>();


	/** The configuration used to configure this proxy */
	protected final AdvisedSupport advised;

	protected Object[] constructorArgs;

	protected Class<?>[] constructorArgTypes;

	/** Dispatcher used for methods on Advised */
	private final transient AdvisedDispatcher advisedDispatcher;

	private transient Map<String, Integer> fixedInterceptorMap;

	private transient int fixedInterceptorOffset;


	/**
	 * Create a new CglibAopProxy for the given AOP configuration.
	 * <p>
	 *  为给定的AOP配置创建一个新的CglibAopProxy
	 * 
	 * 
	 * @param config the AOP configuration as AdvisedSupport object
	 * @throws AopConfigException if the config is invalid. We try to throw an informative
	 * exception in this case, rather than let a mysterious failure happen later.
	 */
	public CglibAopProxy(AdvisedSupport config) throws AopConfigException {
		Assert.notNull(config, "AdvisedSupport must not be null");
		if (config.getAdvisors().length == 0 && config.getTargetSource() == AdvisedSupport.EMPTY_TARGET_SOURCE) {
			throw new AopConfigException("No advisors and no TargetSource specified");
		}
		this.advised = config;
		this.advisedDispatcher = new AdvisedDispatcher(this.advised);
	}

	/**
	 * Set constructor arguments to use for creating the proxy.
	 * <p>
	 *  设置用于创建代理的构造函数参数
	 * 
	 * 
	 * @param constructorArgs the constructor argument values
	 * @param constructorArgTypes the constructor argument types
	 */
	public void setConstructorArguments(Object[] constructorArgs, Class<?>[] constructorArgTypes) {
		if (constructorArgs == null || constructorArgTypes == null) {
			throw new IllegalArgumentException("Both 'constructorArgs' and 'constructorArgTypes' need to be specified");
		}
		if (constructorArgs.length != constructorArgTypes.length) {
			throw new IllegalArgumentException("Number of 'constructorArgs' (" + constructorArgs.length +
					") must match number of 'constructorArgTypes' (" + constructorArgTypes.length + ")");
		}
		this.constructorArgs = constructorArgs;
		this.constructorArgTypes = constructorArgTypes;
	}


	@Override
	public Object getProxy() {
		return getProxy(null);
	}

	@Override
	public Object getProxy(ClassLoader classLoader) {
		if (logger.isDebugEnabled()) {
			logger.debug("Creating CGLIB proxy: target source is " + this.advised.getTargetSource());
		}

		try {
			Class<?> rootClass = this.advised.getTargetClass();
			Assert.state(rootClass != null, "Target class must be available for creating a CGLIB proxy");

			Class<?> proxySuperClass = rootClass;
			if (ClassUtils.isCglibProxyClass(rootClass)) {
				proxySuperClass = rootClass.getSuperclass();
				Class<?>[] additionalInterfaces = rootClass.getInterfaces();
				for (Class<?> additionalInterface : additionalInterfaces) {
					this.advised.addInterface(additionalInterface);
				}
			}

			// Validate the class, writing log messages as necessary.
			validateClassIfNecessary(proxySuperClass, classLoader);

			// Configure CGLIB Enhancer...
			Enhancer enhancer = createEnhancer();
			if (classLoader != null) {
				enhancer.setClassLoader(classLoader);
				if (classLoader instanceof SmartClassLoader &&
						((SmartClassLoader) classLoader).isClassReloadable(proxySuperClass)) {
					enhancer.setUseCache(false);
				}
			}
			enhancer.setSuperclass(proxySuperClass);
			enhancer.setInterfaces(AopProxyUtils.completeProxiedInterfaces(this.advised));
			enhancer.setNamingPolicy(SpringNamingPolicy.INSTANCE);
			enhancer.setStrategy(new ClassLoaderAwareUndeclaredThrowableStrategy(classLoader));

			Callback[] callbacks = getCallbacks(rootClass);
			Class<?>[] types = new Class<?>[callbacks.length];
			for (int x = 0; x < types.length; x++) {
				types[x] = callbacks[x].getClass();
			}
			// fixedInterceptorMap only populated at this point, after getCallbacks call above
			enhancer.setCallbackFilter(new ProxyCallbackFilter(
					this.advised.getConfigurationOnlyCopy(), this.fixedInterceptorMap, this.fixedInterceptorOffset));
			enhancer.setCallbackTypes(types);

			// Generate the proxy class and create a proxy instance.
			return createProxyClassAndInstance(enhancer, callbacks);
		}
		catch (CodeGenerationException ex) {
			throw new AopConfigException("Could not generate CGLIB subclass of class [" +
					this.advised.getTargetClass() + "]: " +
					"Common causes of this problem include using a final class or a non-visible class",
					ex);
		}
		catch (IllegalArgumentException ex) {
			throw new AopConfigException("Could not generate CGLIB subclass of class [" +
					this.advised.getTargetClass() + "]: " +
					"Common causes of this problem include using a final class or a non-visible class",
					ex);
		}
		catch (Exception ex) {
			// TargetSource.getTarget() failed
			throw new AopConfigException("Unexpected AOP exception", ex);
		}
	}

	protected Object createProxyClassAndInstance(Enhancer enhancer, Callback[] callbacks) {
		enhancer.setInterceptDuringConstruction(false);
		enhancer.setCallbacks(callbacks);
		return (this.constructorArgs != null ?
				enhancer.create(this.constructorArgTypes, this.constructorArgs) :
				enhancer.create());
	}

	/**
	 * Creates the CGLIB {@link Enhancer}. Subclasses may wish to override this to return a custom
	 * {@link Enhancer} implementation.
	 * <p>
	 *  创建CGLIB {@link Enhancer}子类可能希望覆盖此功能以返回自定义{@link Enhancer}实现
	 * 
	 */
	protected Enhancer createEnhancer() {
		return new Enhancer();
	}

	/**
	 * Checks to see whether the supplied {@code Class} has already been validated and
	 * validates it if not.
	 * <p>
	 * 检查提供的{@code Class}是否已经被验证,如果没有验证
	 * 
	 */
	private void validateClassIfNecessary(Class<?> proxySuperClass, ClassLoader proxyClassLoader) {
		if (logger.isInfoEnabled()) {
			synchronized (validatedClasses) {
				if (!validatedClasses.containsKey(proxySuperClass)) {
					doValidateClass(proxySuperClass, proxyClassLoader);
					validatedClasses.put(proxySuperClass, Boolean.TRUE);
				}
			}
		}
	}

	/**
	 * Checks for final methods on the given {@code Class}, as well as package-visible
	 * methods across ClassLoaders, and writes warnings to the log for each one found.
	 * <p>
	 *  检查给定的{@code Class}上的最终方法,以及跨ClassLoaders的包可见方法,并为每个发现的日志写入警告
	 * 
	 */
	private void doValidateClass(Class<?> proxySuperClass, ClassLoader proxyClassLoader) {
		if (Object.class != proxySuperClass) {
			Method[] methods = proxySuperClass.getDeclaredMethods();
			for (Method method : methods) {
				int mod = method.getModifiers();
				if (!Modifier.isStatic(mod)) {
					if (Modifier.isFinal(mod)) {
						logger.info("Unable to proxy method [" + method + "] because it is final: " +
								"All calls to this method via a proxy will NOT be routed to the target instance.");
					}
					else if (!Modifier.isPublic(mod) && !Modifier.isProtected(mod) && !Modifier.isPrivate(mod) &&
							proxyClassLoader != null && proxySuperClass.getClassLoader() != proxyClassLoader) {
						logger.info("Unable to proxy method [" + method + "] because it is package-visible " +
								"across different ClassLoaders: All calls to this method via a proxy will " +
								"NOT be routed to the target instance.");
					}
				}
			}
			doValidateClass(proxySuperClass.getSuperclass(), proxyClassLoader);
		}
	}

	private Callback[] getCallbacks(Class<?> rootClass) throws Exception {
		// Parameters used for optimisation choices...
		boolean exposeProxy = this.advised.isExposeProxy();
		boolean isFrozen = this.advised.isFrozen();
		boolean isStatic = this.advised.getTargetSource().isStatic();

		// Choose an "aop" interceptor (used for AOP calls).
		Callback aopInterceptor = new DynamicAdvisedInterceptor(this.advised);

		// Choose a "straight to target" interceptor. (used for calls that are
		// unadvised but can return this). May be required to expose the proxy.
		Callback targetInterceptor;
		if (exposeProxy) {
			targetInterceptor = isStatic ?
					new StaticUnadvisedExposedInterceptor(this.advised.getTargetSource().getTarget()) :
					new DynamicUnadvisedExposedInterceptor(this.advised.getTargetSource());
		}
		else {
			targetInterceptor = isStatic ?
					new StaticUnadvisedInterceptor(this.advised.getTargetSource().getTarget()) :
					new DynamicUnadvisedInterceptor(this.advised.getTargetSource());
		}

		// Choose a "direct to target" dispatcher (used for
		// unadvised calls to static targets that cannot return this).
		Callback targetDispatcher = isStatic ?
				new StaticDispatcher(this.advised.getTargetSource().getTarget()) : new SerializableNoOp();

		Callback[] mainCallbacks = new Callback[] {
				aopInterceptor,  // for normal advice
				targetInterceptor,  // invoke target without considering advice, if optimized
				new SerializableNoOp(),  // no override for methods mapped to this
				targetDispatcher, this.advisedDispatcher,
				new EqualsInterceptor(this.advised),
				new HashCodeInterceptor(this.advised)
		};

		Callback[] callbacks;

		// If the target is a static one and the advice chain is frozen,
		// then we can make some optimisations by sending the AOP calls
		// direct to the target using the fixed chain for that method.
		if (isStatic && isFrozen) {
			Method[] methods = rootClass.getMethods();
			Callback[] fixedCallbacks = new Callback[methods.length];
			this.fixedInterceptorMap = new HashMap<String, Integer>(methods.length);

			// TODO: small memory optimisation here (can skip creation for methods with no advice)
			for (int x = 0; x < methods.length; x++) {
				List<Object> chain = this.advised.getInterceptorsAndDynamicInterceptionAdvice(methods[x], rootClass);
				fixedCallbacks[x] = new FixedChainStaticTargetInterceptor(
						chain, this.advised.getTargetSource().getTarget(), this.advised.getTargetClass());
				this.fixedInterceptorMap.put(methods[x].toString(), x);
			}

			// Now copy both the callbacks from mainCallbacks
			// and fixedCallbacks into the callbacks array.
			callbacks = new Callback[mainCallbacks.length + fixedCallbacks.length];
			System.arraycopy(mainCallbacks, 0, callbacks, 0, mainCallbacks.length);
			System.arraycopy(fixedCallbacks, 0, callbacks, mainCallbacks.length, fixedCallbacks.length);
			this.fixedInterceptorOffset = mainCallbacks.length;
		}
		else {
			callbacks = mainCallbacks;
		}
		return callbacks;
	}

	/**
	 * Process a return value. Wraps a return of {@code this} if necessary to be the
	 * {@code proxy} and also verifies that {@code null} is not returned as a primitive.
	 * <p>
	 *  处理返回值如果需要,将{@code this}的返回结果作为{@code代理}并且还验证{@code null}不是作为原语返回
	 * 
	 */
	private static Object processReturnType(Object proxy, Object target, Method method, Object retVal) {
		// Massage return value if necessary
		if (retVal != null && retVal == target && !RawTargetAccess.class.isAssignableFrom(method.getDeclaringClass())) {
			// Special case: it returned "this". Note that we can't help
			// if the target sets a reference to itself in another returned object.
			retVal = proxy;
		}
		Class<?> returnType = method.getReturnType();
		if (retVal == null && returnType != Void.TYPE && returnType.isPrimitive()) {
			throw new AopInvocationException(
					"Null return value from advice does not match primitive return type for: " + method);
		}
		return retVal;
	}


	@Override
	public boolean equals(Object other) {
		return (this == other || (other instanceof CglibAopProxy &&
				AopProxyUtils.equalsInProxy(this.advised, ((CglibAopProxy) other).advised)));
	}

	@Override
	public int hashCode() {
		return CglibAopProxy.class.hashCode() * 13 + this.advised.getTargetSource().hashCode();
	}


	/**
	 * Serializable replacement for CGLIB's NoOp interface.
	 * Public to allow use elsewhere in the framework.
	 * <p>
	 *  可序列化的替代CGLIB的NoOp接口公开允许在框架的其他地方使用
	 * 
	 */
	public static class SerializableNoOp implements NoOp, Serializable {
	}


	/**
	 * Method interceptor used for static targets with no advice chain. The call
	 * is passed directly back to the target. Used when the proxy needs to be
	 * exposed and it can't be determined that the method won't return
	 * {@code this}.
	 * <p>
	 *  用于没有建议链的静态目标的方法拦截器调用直接传递回目标当代理需要暴露时使用,并且无法确定该方法不会返回{@code this}
	 * 
	 */
	private static class StaticUnadvisedInterceptor implements MethodInterceptor, Serializable {

		private final Object target;

		public StaticUnadvisedInterceptor(Object target) {
			this.target = target;
		}

		@Override
		public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
			Object retVal = methodProxy.invoke(this.target, args);
			return processReturnType(proxy, this.target, method, retVal);
		}
	}


	/**
	 * Method interceptor used for static targets with no advice chain, when the
	 * proxy is to be exposed.
	 * <p>
	 * 用于没有建议链的静态目标的方法拦截器,当代理被暴露时
	 * 
	 */
	private static class StaticUnadvisedExposedInterceptor implements MethodInterceptor, Serializable {

		private final Object target;

		public StaticUnadvisedExposedInterceptor(Object target) {
			this.target = target;
		}

		@Override
		public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
			Object oldProxy = null;
			try {
				oldProxy = AopContext.setCurrentProxy(proxy);
				Object retVal = methodProxy.invoke(this.target, args);
				return processReturnType(proxy, this.target, method, retVal);
			}
			finally {
				AopContext.setCurrentProxy(oldProxy);
			}
		}
	}


	/**
	 * Interceptor used to invoke a dynamic target without creating a method
	 * invocation or evaluating an advice chain. (We know there was no advice
	 * for this method.)
	 * <p>
	 *  用于调用动态目标的拦截器,而不创建方法调用或评估一个建议链(我们知道这个方法没有任何建议)
	 * 
	 */
	private static class DynamicUnadvisedInterceptor implements MethodInterceptor, Serializable {

		private final TargetSource targetSource;

		public DynamicUnadvisedInterceptor(TargetSource targetSource) {
			this.targetSource = targetSource;
		}

		@Override
		public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
			Object target = this.targetSource.getTarget();
			try {
				Object retVal = methodProxy.invoke(target, args);
				return processReturnType(proxy, target, method, retVal);
			}
			finally {
				this.targetSource.releaseTarget(target);
			}
		}
	}


	/**
	 * Interceptor for unadvised dynamic targets when the proxy needs exposing.
	 * <p>
	 *  当代理需要暴露时,用于未修改的动态目标的拦截器
	 * 
	 */
	private static class DynamicUnadvisedExposedInterceptor implements MethodInterceptor, Serializable {

		private final TargetSource targetSource;

		public DynamicUnadvisedExposedInterceptor(TargetSource targetSource) {
			this.targetSource = targetSource;
		}

		@Override
		public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
			Object oldProxy = null;
			Object target = this.targetSource.getTarget();
			try {
				oldProxy = AopContext.setCurrentProxy(proxy);
				Object retVal = methodProxy.invoke(target, args);
				return processReturnType(proxy, target, method, retVal);
			}
			finally {
				AopContext.setCurrentProxy(oldProxy);
				this.targetSource.releaseTarget(target);
			}
		}
	}


	/**
	 * Dispatcher for a static target. Dispatcher is much faster than
	 * interceptor. This will be used whenever it can be determined that a
	 * method definitely does not return "this"
	 * <p>
	 *  一个静态目标分派器的调度器比拦截器快得多这个方法将被用来确定一个方法绝对不会返回"this"
	 * 
	 */
	private static class StaticDispatcher implements Dispatcher, Serializable {

		private Object target;

		public StaticDispatcher(Object target) {
			this.target = target;
		}

		@Override
		public Object loadObject() {
			return this.target;
		}
	}


	/**
	 * Dispatcher for any methods declared on the Advised class.
	 * <p>
	 *  调度员在Advised课程上声明的任何方法
	 * 
	 */
	private static class AdvisedDispatcher implements Dispatcher, Serializable {

		private final AdvisedSupport advised;

		public AdvisedDispatcher(AdvisedSupport advised) {
			this.advised = advised;
		}

		@Override
		public Object loadObject() throws Exception {
			return this.advised;
		}
	}


	/**
	 * Dispatcher for the {@code equals} method.
	 * Ensures that the method call is always handled by this class.
	 * <p>
	 *  {@code equals}方法的调度程序确保方法调用始终由此类处理
	 * 
	 */
	private static class EqualsInterceptor implements MethodInterceptor, Serializable {

		private final AdvisedSupport advised;

		public EqualsInterceptor(AdvisedSupport advised) {
			this.advised = advised;
		}

		@Override
		public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) {
			Object other = args[0];
			if (proxy == other) {
				return true;
			}
			if (other instanceof Factory) {
				Callback callback = ((Factory) other).getCallback(INVOKE_EQUALS);
				if (!(callback instanceof EqualsInterceptor)) {
					return false;
				}
				AdvisedSupport otherAdvised = ((EqualsInterceptor) callback).advised;
				return AopProxyUtils.equalsInProxy(this.advised, otherAdvised);
			}
			else {
				return false;
			}
		}
	}


	/**
	 * Dispatcher for the {@code hashCode} method.
	 * Ensures that the method call is always handled by this class.
	 * <p>
	 *  {@code hashCode}方法的调度程序确保方法调用始终由此类处理
	 * 
	 */
	private static class HashCodeInterceptor implements MethodInterceptor, Serializable {

		private final AdvisedSupport advised;

		public HashCodeInterceptor(AdvisedSupport advised) {
			this.advised = advised;
		}

		@Override
		public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) {
			return CglibAopProxy.class.hashCode() * 13 + this.advised.getTargetSource().hashCode();
		}
	}


	/**
	 * Interceptor used specifically for advised methods on a frozen, static proxy.
	 * <p>
	 * 拦截器专门用于冻结静态代理上的建议方法
	 * 
	 */
	private static class FixedChainStaticTargetInterceptor implements MethodInterceptor, Serializable {

		private final List<Object> adviceChain;

		private final Object target;

		private final Class<?> targetClass;

		public FixedChainStaticTargetInterceptor(List<Object> adviceChain, Object target, Class<?> targetClass) {
			this.adviceChain = adviceChain;
			this.target = target;
			this.targetClass = targetClass;
		}

		@Override
		public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
			MethodInvocation invocation = new CglibMethodInvocation(proxy, this.target, method, args,
					this.targetClass, this.adviceChain, methodProxy);
			// If we get here, we need to create a MethodInvocation.
			Object retVal = invocation.proceed();
			retVal = processReturnType(proxy, this.target, method, retVal);
			return retVal;
		}
	}


	/**
	 * General purpose AOP callback. Used when the target is dynamic or when the
	 * proxy is not frozen.
	 * <p>
	 *  通用AOP回调当目标是动态的或代理未被冻结时使用
	 * 
	 */
	private static class DynamicAdvisedInterceptor implements MethodInterceptor, Serializable {

		private final AdvisedSupport advised;

		public DynamicAdvisedInterceptor(AdvisedSupport advised) {
			this.advised = advised;
		}

		@Override
		public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
			Object oldProxy = null;
			boolean setProxyContext = false;
			Class<?> targetClass = null;
			Object target = null;
			try {
				if (this.advised.exposeProxy) {
					// Make invocation available if necessary.
					oldProxy = AopContext.setCurrentProxy(proxy);
					setProxyContext = true;
				}
				// May be null. Get as late as possible to minimize the time we
				// "own" the target, in case it comes from a pool...
				target = getTarget();
				if (target != null) {
					targetClass = target.getClass();
				}
				List<Object> chain = this.advised.getInterceptorsAndDynamicInterceptionAdvice(method, targetClass);
				Object retVal;
				// Check whether we only have one InvokerInterceptor: that is,
				// no real advice, but just reflective invocation of the target.
				if (chain.isEmpty() && Modifier.isPublic(method.getModifiers())) {
					// We can skip creating a MethodInvocation: just invoke the target directly.
					// Note that the final invoker must be an InvokerInterceptor, so we know
					// it does nothing but a reflective operation on the target, and no hot
					// swapping or fancy proxying.
					Object[] argsToUse = AopProxyUtils.adaptArgumentsIfNecessary(method, args);
					retVal = methodProxy.invoke(target, argsToUse);
				}
				else {
					// We need to create a method invocation...
					retVal = new CglibMethodInvocation(proxy, target, method, args, targetClass, chain, methodProxy).proceed();
				}
				retVal = processReturnType(proxy, target, method, retVal);
				return retVal;
			}
			finally {
				if (target != null) {
					releaseTarget(target);
				}
				if (setProxyContext) {
					// Restore old proxy.
					AopContext.setCurrentProxy(oldProxy);
				}
			}
		}

		@Override
		public boolean equals(Object other) {
			return (this == other ||
					(other instanceof DynamicAdvisedInterceptor &&
							this.advised.equals(((DynamicAdvisedInterceptor) other).advised)));
		}

		/**
		 * CGLIB uses this to drive proxy creation.
		 * <p>
		 *  CGLIB使用它来驱动代理创建
		 * 
		 */
		@Override
		public int hashCode() {
			return this.advised.hashCode();
		}

		protected Object getTarget() throws Exception {
			return this.advised.getTargetSource().getTarget();
		}

		protected void releaseTarget(Object target) throws Exception {
			this.advised.getTargetSource().releaseTarget(target);
		}
	}


	/**
	 * Implementation of AOP Alliance MethodInvocation used by this AOP proxy.
	 * <p>
	 *  AOP代理使用的AOP Alliance MethodInvocation的实现
	 * 
	 */
	private static class CglibMethodInvocation extends ReflectiveMethodInvocation {

		private final MethodProxy methodProxy;

		private final boolean publicMethod;

		public CglibMethodInvocation(Object proxy, Object target, Method method, Object[] arguments,
				Class<?> targetClass, List<Object> interceptorsAndDynamicMethodMatchers, MethodProxy methodProxy) {

			super(proxy, target, method, arguments, targetClass, interceptorsAndDynamicMethodMatchers);
			this.methodProxy = methodProxy;
			this.publicMethod = Modifier.isPublic(method.getModifiers());
		}

		/**
		 * Gives a marginal performance improvement versus using reflection to
		 * invoke the target when invoking public methods.
		 * <p>
		 *  在调用公共方法时,使用反射来调用目标时,会提高边际性能
		 * 
		 */
		@Override
		protected Object invokeJoinpoint() throws Throwable {
			if (this.publicMethod) {
				return this.methodProxy.invoke(this.target, this.arguments);
			}
			else {
				return super.invokeJoinpoint();
			}
		}
	}


	/**
	 * CallbackFilter to assign Callbacks to methods.
	 * <p>
	 *  CallbackFilter将回调函数分配给方法
	 * 
	 */
	private static class ProxyCallbackFilter implements CallbackFilter {

		private final AdvisedSupport advised;

		private final Map<String, Integer> fixedInterceptorMap;

		private final int fixedInterceptorOffset;

		public ProxyCallbackFilter(AdvisedSupport advised, Map<String, Integer> fixedInterceptorMap, int fixedInterceptorOffset) {
			this.advised = advised;
			this.fixedInterceptorMap = fixedInterceptorMap;
			this.fixedInterceptorOffset = fixedInterceptorOffset;
		}

		/**
		 * Implementation of CallbackFilter.accept() to return the index of the
		 * callback we need.
		 * <p>The callbacks for each proxy are built up of a set of fixed callbacks
		 * for general use and then a set of callbacks that are specific to a method
		 * for use on static targets with a fixed advice chain.
		 * <p>The callback used is determined thus:
		 * <dl>
		 * <dt>For exposed proxies</dt>
		 * <dd>Exposing the proxy requires code to execute before and after the
		 * method/chain invocation. This means we must use
		 * DynamicAdvisedInterceptor, since all other interceptors can avoid the
		 * need for a try/catch block</dd>
		 * <dt>For Object.finalize():</dt>
		 * <dd>No override for this method is used.</dd>
		 * <dt>For equals():</dt>
		 * <dd>The EqualsInterceptor is used to redirect equals() calls to a
		 * special handler to this proxy.</dd>
		 * <dt>For methods on the Advised class:</dt>
		 * <dd>the AdvisedDispatcher is used to dispatch the call directly to
		 * the target</dd>
		 * <dt>For advised methods:</dt>
		 * <dd>If the target is static and the advice chain is frozen then a
		 * FixedChainStaticTargetInterceptor specific to the method is used to
		 * invoke the advice chain. Otherwise a DyanmicAdvisedInterceptor is
		 * used.</dd>
		 * <dt>For non-advised methods:</dt>
		 * <dd>Where it can be determined that the method will not return {@code this}
		 * or when {@code ProxyFactory.getExposeProxy()} returns {@code false},
		 * then a Dispatcher is used. For static targets, the StaticDispatcher is used;
		 * and for dynamic targets, a DynamicUnadvisedInterceptor is used.
		 * If it possible for the method to return {@code this} then a
		 * StaticUnadvisedInterceptor is used for static targets - the
		 * DynamicUnadvisedInterceptor already considers this.</dd>
		 * </dl>
		 * <p>
		 *  CallbackFilteraccept()的实现返回我们需要的回调索引<p>每个代理的回调由一组常用的固定回调构成,然后是一组特定于静态使用方法的回调具有固定建议链的目标<p>所使用的回调由此确定：
		 * 。
		 * <dl>
		 * <dt>对于暴露的代理</dt> <dd>公开代理需要在方法/链调用之前和之后执行的代码这意味着我们必须使用DynamicAdvisedInterceptor,因为所有其他拦截器可以避免使用try / 
		 * catch块< / dd> <dt>对于Objectfinalize()：</dt> <dd>不使用此方法的覆盖</dd> <dt>对于equals()：</dt> <dd> EqualsInterce
		 * ptor用于重定向equals()调用此代理的特殊处理程序</dd> <dt>对于Advised类的方法：</dt> <dd> AdvisedDispatcher用于将调用直接发送到目标</dd> <dt >
		 * 对于建议的方法：</dt> <dd>如果目标是静态的,并且建议链被冻结,那么该方法专用的FixedChainStaticTargetInterceptor用于调用该建议链否则使用DyanmicAdvis
		 */
		@Override
		public int accept(Method method) {
			if (AopUtils.isFinalizeMethod(method)) {
				logger.debug("Found finalize() method - using NO_OVERRIDE");
				return NO_OVERRIDE;
			}
			if (!this.advised.isOpaque() && method.getDeclaringClass().isInterface() &&
					method.getDeclaringClass().isAssignableFrom(Advised.class)) {
				if (logger.isDebugEnabled()) {
					logger.debug("Method is declared on Advised interface: " + method);
				}
				return DISPATCH_ADVISED;
			}
			// We must always proxy equals, to direct calls to this.
			if (AopUtils.isEqualsMethod(method)) {
				logger.debug("Found 'equals' method: " + method);
				return INVOKE_EQUALS;
			}
			// We must always calculate hashCode based on the proxy.
			if (AopUtils.isHashCodeMethod(method)) {
				logger.debug("Found 'hashCode' method: " + method);
				return INVOKE_HASHCODE;
			}
			Class<?> targetClass = this.advised.getTargetClass();
			// Proxy is not yet available, but that shouldn't matter.
			List<?> chain = this.advised.getInterceptorsAndDynamicInterceptionAdvice(method, targetClass);
			boolean haveAdvice = !chain.isEmpty();
			boolean exposeProxy = this.advised.isExposeProxy();
			boolean isStatic = this.advised.getTargetSource().isStatic();
			boolean isFrozen = this.advised.isFrozen();
			if (haveAdvice || !isFrozen) {
				// If exposing the proxy, then AOP_PROXY must be used.
				if (exposeProxy) {
					if (logger.isDebugEnabled()) {
						logger.debug("Must expose proxy on advised method: " + method);
					}
					return AOP_PROXY;
				}
				String key = method.toString();
				// Check to see if we have fixed interceptor to serve this method.
				// Else use the AOP_PROXY.
				if (isStatic && isFrozen && this.fixedInterceptorMap.containsKey(key)) {
					if (logger.isDebugEnabled()) {
						logger.debug("Method has advice and optimisations are enabled: " + method);
					}
					// We know that we are optimising so we can use the FixedStaticChainInterceptors.
					int index = this.fixedInterceptorMap.get(key);
					return (index + this.fixedInterceptorOffset);
				}
				else {
					if (logger.isDebugEnabled()) {
						logger.debug("Unable to apply any optimisations to advised method: " + method);
					}
					return AOP_PROXY;
				}
			}
			else {
				// See if the return type of the method is outside the class hierarchy
				// of the target type. If so we know it never needs to have return type
				// massage and can use a dispatcher.
				// If the proxy is being exposed, then must use the interceptor the
				// correct one is already configured. If the target is not static, then
				// cannot use a dispatcher because the target cannot be released.
				if (exposeProxy || !isStatic) {
					return INVOKE_TARGET;
				}
				Class<?> returnType = method.getReturnType();
				if (targetClass == returnType) {
					if (logger.isDebugEnabled()) {
						logger.debug("Method " + method +
								"has return type same as target type (may return this) - using INVOKE_TARGET");
					}
					return INVOKE_TARGET;
				}
				else if (returnType.isPrimitive() || !returnType.isAssignableFrom(targetClass)) {
					if (logger.isDebugEnabled()) {
						logger.debug("Method " + method +
								" has return type that ensures this cannot be returned- using DISPATCH_TARGET");
					}
					return DISPATCH_TARGET;
				}
				else {
					if (logger.isDebugEnabled()) {
						logger.debug("Method " + method +
								"has return type that is assignable from the target type (may return this) - " +
								"using INVOKE_TARGET");
					}
					return INVOKE_TARGET;
				}
			}
		}

		@Override
		public boolean equals(Object other) {
			if (this == other) {
				return true;
			}
			if (!(other instanceof ProxyCallbackFilter)) {
				return false;
			}
			ProxyCallbackFilter otherCallbackFilter = (ProxyCallbackFilter) other;
			AdvisedSupport otherAdvised = otherCallbackFilter.advised;
			if (this.advised == null || otherAdvised == null) {
				return false;
			}
			if (this.advised.isFrozen() != otherAdvised.isFrozen()) {
				return false;
			}
			if (this.advised.isExposeProxy() != otherAdvised.isExposeProxy()) {
				return false;
			}
			if (this.advised.getTargetSource().isStatic() != otherAdvised.getTargetSource().isStatic()) {
				return false;
			}
			if (!AopProxyUtils.equalsProxiedInterfaces(this.advised, otherAdvised)) {
				return false;
			}
			// Advice instance identity is unimportant to the proxy class:
			// All that matters is type and ordering.
			Advisor[] thisAdvisors = this.advised.getAdvisors();
			Advisor[] thatAdvisors = otherAdvised.getAdvisors();
			if (thisAdvisors.length != thatAdvisors.length) {
				return false;
			}
			for (int i = 0; i < thisAdvisors.length; i++) {
				Advisor thisAdvisor = thisAdvisors[i];
				Advisor thatAdvisor = thatAdvisors[i];
				if (!equalsAdviceClasses(thisAdvisor, thatAdvisor)) {
					return false;
				}
				if (!equalsPointcuts(thisAdvisor, thatAdvisor)) {
					return false;
				}
			}
			return true;
		}

		private boolean equalsAdviceClasses(Advisor a, Advisor b) {
			Advice aa = a.getAdvice();
			Advice ba = b.getAdvice();
			if (aa == null || ba == null) {
				return (aa == ba);
			}
			return (aa.getClass() == ba.getClass());
		}

		private boolean equalsPointcuts(Advisor a, Advisor b) {
			// If only one of the advisor (but not both) is PointcutAdvisor, then it is a mismatch.
			// Takes care of the situations where an IntroductionAdvisor is used (see SPR-3959).
			return (!(a instanceof PointcutAdvisor) ||
					(b instanceof PointcutAdvisor &&
							ObjectUtils.nullSafeEquals(((PointcutAdvisor) a).getPointcut(), ((PointcutAdvisor) b).getPointcut())));
		}

		@Override
		public int hashCode() {
			int hashCode = 0;
			Advisor[] advisors = this.advised.getAdvisors();
			for (Advisor advisor : advisors) {
				Advice advice = advisor.getAdvice();
				if (advice != null) {
					hashCode = 13 * hashCode + advice.getClass().hashCode();
				}
			}
			hashCode = 13 * hashCode + (this.advised.isFrozen() ? 1 : 0);
			hashCode = 13 * hashCode + (this.advised.isExposeProxy() ? 1 : 0);
			hashCode = 13 * hashCode + (this.advised.isOptimize() ? 1 : 0);
			hashCode = 13 * hashCode + (this.advised.isOpaque() ? 1 : 0);
			return hashCode;
		}
	}


	/**
	 * CGLIB GeneratorStrategy variant which exposes the application ClassLoader
	 * as thread context ClassLoader for the time of class generation
	 * (in order for ASM to pick it up when doing common superclass resolution).
	 * <p>
	 * edInterceptor </dd> <dt>对于非建议的方法：</dt> <dd>可以确定该方法不会返回{@code this}或{@code ProxyFactorygetExposeProxy()}
	 * )返回{@code false},然后使用Dispatcher对于静态目标,使用StaticDispatcher;对于动态目标,使用DynamicUnadvisedInterceptor如果该方法可能返
	 * 回{@code this},那么StaticUnadvisedInterceptor用于静态目标 -  DynamicUnadvisedInterceptor已经认为这个</dd>。
	 * </dl>
	 */
	private static class ClassLoaderAwareUndeclaredThrowableStrategy extends UndeclaredThrowableStrategy {

		private final ClassLoader classLoader;

		public ClassLoaderAwareUndeclaredThrowableStrategy(ClassLoader classLoader) {
			super(UndeclaredThrowableException.class);
			this.classLoader = classLoader;
		}

		@Override
		public byte[] generate(ClassGenerator cg) throws Exception {
			if (this.classLoader == null) {
				return super.generate(cg);
			}

			Thread currentThread = Thread.currentThread();
			ClassLoader threadContextClassLoader;
			try {
				threadContextClassLoader = currentThread.getContextClassLoader();
			}
			catch (Throwable ex) {
				// Cannot access thread context ClassLoader - falling back...
				return super.generate(cg);
			}

			boolean overrideClassLoader = !this.classLoader.equals(threadContextClassLoader);
			if (overrideClassLoader) {
				currentThread.setContextClassLoader(this.classLoader);
			}
			try {
				return super.generate(cg);
			}
			finally {
				if (overrideClassLoader) {
					// Reset original thread context ClassLoader.
					currentThread.setContextClassLoader(threadContextClassLoader);
				}
			}
		}
	}

}
