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

package org.springframework.aop.support;

import org.aopalliance.intercept.MethodInvocation;

import org.springframework.aop.DynamicIntroductionAdvice;
import org.springframework.aop.IntroductionInterceptor;
import org.springframework.aop.ProxyMethodInvocation;
import org.springframework.util.Assert;

/**
 * Convenient implementation of the
 * {@link org.springframework.aop.IntroductionInterceptor} interface.
 *
 * <p>Subclasses merely need to extend this class and implement the interfaces
 * to be introduced themselves. In this case the delegate is the subclass
 * instance itself. Alternatively a separate delegate may implement the
 * interface, and be set via the delegate bean property.
 *
 * <p>Delegates or subclasses may implement any number of interfaces.
 * All interfaces except IntroductionInterceptor are picked up from
 * the subclass or delegate by default.
 *
 * <p>The {@code suppressInterface} method can be used to suppress interfaces
 * implemented by the delegate but which should not be introduced to the owning
 * AOP proxy.
 *
 * <p>An instance of this class is serializable if the delegate is.
 *
 * <p>
 *  方便实现{@link orgspringframeworkaopIntroductionInterceptor}界面
 * 
 * <p>子类只需要扩展此类并实现要引入的接口自身在这种情况下,委托是子类实例本身或者一个单独的委托可以实现接口,并通过委托bean属性设置
 * 
 *  <p>代理或子类可以实现任何数量的接口除了IntroductionInterceptor之外的所有接口都是从子类或默认委派中提取的
 * 
 *  <p> {@code suppressInterface}方法可用于抑制代理实现的接口,但不应将其引入到拥有的AOP代理
 * 
 *  <p>如果代理是这个类的一个实例是可序列化的
 * 
 * 
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since 16.11.2003
 * @see #suppressInterface
 * @see DelegatePerTargetObjectIntroductionInterceptor
 */
@SuppressWarnings("serial")
public class DelegatingIntroductionInterceptor extends IntroductionInfoSupport
		implements IntroductionInterceptor {

	/**
	 * Object that actually implements the interfaces.
	 * May be "this" if a subclass implements the introduced interfaces.
	 * <p>
	 *  实际实现接口的对象如果子类实现引入的接口,可能是"this"
	 * 
	 */
	private Object delegate;


	/**
	 * Construct a new DelegatingIntroductionInterceptor, providing
	 * a delegate that implements the interfaces to be introduced.
	 * <p>
	 * 构造一个新的DelegatingIntroductionInterceptor,提供一个实现要引入的接口的委托
	 * 
	 * 
	 * @param delegate the delegate that implements the introduced interfaces
	 */
	public DelegatingIntroductionInterceptor(Object delegate) {
		init(delegate);
	}

	/**
	 * Construct a new DelegatingIntroductionInterceptor.
	 * The delegate will be the subclass, which must implement
	 * additional interfaces.
	 * <p>
	 *  构造新的DelegatingIntroductionInterceptor委托将是子类,必须实现其他接口
	 * 
	 */
	protected DelegatingIntroductionInterceptor() {
		init(this);
	}


	/**
	 * Both constructors use this init method, as it is impossible to pass
	 * a "this" reference from one constructor to another.
	 * <p>
	 *  这两个构造函数都使用这个init方法,因为它不可能将一个"this"引用从一个构造函数传递给另一个构造函数
	 * 
	 * 
	 * @param delegate the delegate object
	 */
	private void init(Object delegate) {
		Assert.notNull(delegate, "Delegate must not be null");
		this.delegate = delegate;
		implementInterfacesOnObject(delegate);

		// We don't want to expose the control interface
		suppressInterface(IntroductionInterceptor.class);
		suppressInterface(DynamicIntroductionAdvice.class);
	}


	/**
	 * Subclasses may need to override this if they want to perform custom
	 * behaviour in around advice. However, subclasses should invoke this
	 * method, which handles introduced interfaces and forwarding to the target.
	 * <p>
	 *  子类可能需要重写这个,如果他们想在大概的通知中执行自定义行为。但是,子类应该调用这个方法,它处理引入的接口并转发到目标
	 * 
	 */
	@Override
	public Object invoke(MethodInvocation mi) throws Throwable {
		if (isMethodOnIntroducedInterface(mi)) {
			// Using the following method rather than direct reflection, we
			// get correct handling of InvocationTargetException
			// if the introduced method throws an exception.
			Object retVal = AopUtils.invokeJoinpointUsingReflection(this.delegate, mi.getMethod(), mi.getArguments());

			// Massage return value if possible: if the delegate returned itself,
			// we really want to return the proxy.
			if (retVal == this.delegate && mi instanceof ProxyMethodInvocation) {
				Object proxy = ((ProxyMethodInvocation) mi).getProxy();
				if (mi.getMethod().getReturnType().isInstance(proxy)) {
					retVal = proxy;
				}
			}
			return retVal;
		}

		return doProceed(mi);
	}

	/**
	 * Proceed with the supplied {@link org.aopalliance.intercept.MethodInterceptor}.
	 * Subclasses can override this method to intercept method invocations on the
	 * target object which is useful when an introduction needs to monitor the object
	 * that it is introduced into. This method is <strong>never</strong> called for
	 * {@link MethodInvocation MethodInvocations} on the introduced interfaces.
	 * <p>
	 * 继承所提供的{@link orgaopallianceinterceptMethodInterceptor}子类可以覆盖此方法来拦截目标对象上的方法调用,当介绍需要监视引入的对象时,这是有用的。
	 * 此方法是<strong>永不</strong>对于引入的接口的{@link MethodInvocation MethodInvocations}。
	 */
	protected Object doProceed(MethodInvocation mi) throws Throwable {
		// If we get here, just pass the invocation on.
		return mi.proceed();
	}

}
