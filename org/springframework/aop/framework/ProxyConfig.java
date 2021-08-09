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

package org.springframework.aop.framework;

import java.io.Serializable;

import org.springframework.util.Assert;

/**
 * Convenience superclass for configuration used in creating proxies,
 * to ensure that all proxy creators have consistent properties.
 *
 * <p>
 *  方便的超类配置用于创建代理,以确保所有代理创建者具有一致的属性
 * 
 * 
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @see AdvisedSupport
 */
public class ProxyConfig implements Serializable {

	/** use serialVersionUID from Spring 1.2 for interoperability */
	private static final long serialVersionUID = -8409359707199703185L;


	private boolean proxyTargetClass = false;

	private boolean optimize = false;

	boolean opaque = false;

	boolean exposeProxy = false;

	private boolean frozen = false;


	/**
	 * Set whether to proxy the target class directly, instead of just proxying
	 * specific interfaces. Default is "false".
	 * <p>Set this to "true" to force proxying for the TargetSource's exposed
	 * target class. If that target class is an interface, a JDK proxy will be
	 * created for the given interface. If that target class is any other class,
	 * a CGLIB proxy will be created for the given class.
	 * <p>Note: Depending on the configuration of the concrete proxy factory,
	 * the proxy-target-class behavior will also be applied if no interfaces
	 * have been specified (and no interface autodetection is activated).
	 * <p>
	 * 设置是否直接代理目标类,而不是仅代理特定的接口默认为"false"<p>将其设置为"true"以强制TargetSource的暴露目标类的代理如果该目标类是接口,则JDK代理将为给定的接口创建如果该目标
	 * 类是任何其他类,将为给定的类创建一个CGLIB代理<p>注意：根据具体代理工厂的配置,代理目标类的行为也将如果没有指定接口(并且没有激活接口自动检测)。
	 * 
	 * 
	 * @see org.springframework.aop.TargetSource#getTargetClass()
	 */
	public void setProxyTargetClass(boolean proxyTargetClass) {
		this.proxyTargetClass = proxyTargetClass;
	}

	/**
	 * Return whether to proxy the target class directly as well as any interfaces.
	 * <p>
	 *  返回是否直接代理目标类以及任何接口
	 * 
	 */
	public boolean isProxyTargetClass() {
		return this.proxyTargetClass;
	}

	/**
	 * Set whether proxies should perform aggressive optimizations.
	 * The exact meaning of "aggressive optimizations" will differ
	 * between proxies, but there is usually some tradeoff.
	 * Default is "false".
	 * <p>For example, optimization will usually mean that advice changes won't
	 * take effect after a proxy has been created. For this reason, optimization
	 * is disabled by default. An optimize value of "true" may be ignored
	 * if other settings preclude optimization: for example, if "exposeProxy"
	 * is set to "true" and that's not compatible with the optimization.
	 * <p>
	 * 设置代理是否应该执行积极的优化"侵略性优化"的确切含义在代理之间会有所不同,但通常有一些权衡默认为"false"<p>例如,优化通常意味着建议更改将不会生效已创建代理为此,默认情况下禁用优化优化值"tr
	 * ue"的优化值可能会被忽略,如果其他设置排除优化：例如,如果"publicProxy"设置为"true",并且与优化不兼容。
	 * 
	 */
	public void setOptimize(boolean optimize) {
		this.optimize = optimize;
	}

	/**
	 * Return whether proxies should perform aggressive optimizations.
	 * <p>
	 *  返回代理是否应该执行积极的优化
	 * 
	 */
	public boolean isOptimize() {
		return this.optimize;
	}

	/**
	 * Set whether proxies created by this configuration should be prevented
	 * from being cast to {@link Advised} to query proxy status.
	 * <p>Default is "false", meaning that any AOP proxy can be cast to
	 * {@link Advised}.
	 * <p>
	 * 设置是否应该阻止通过此配置创建的代理被转换为{@link Advised}以查询代理状态<p>默认值为"false",这意味着任何AOP代理可以转换为{@link Advised}
	 * 
	 */
	public void setOpaque(boolean opaque) {
		this.opaque = opaque;
	}

	/**
	 * Return whether proxies created by this configuration should be
	 * prevented from being cast to {@link Advised}.
	 * <p>
	 *  返回是否应该阻止通过此配置创建的代理被投射到{@link Advised}
	 * 
	 */
	public boolean isOpaque() {
		return this.opaque;
	}

	/**
	 * Set whether the proxy should be exposed by the AOP framework as a
	 * ThreadLocal for retrieval via the AopContext class. This is useful
	 * if an advised object needs to call another advised method on itself.
	 * (If it uses {@code this}, the invocation will not be advised).
	 * <p>Default is "false", in order to avoid unnecessary extra interception.
	 * This means that no guarantees are provided that AopContext access will
	 * work consistently within any method of the advised object.
	 * <p>
	 *  设置代理是否应该被AOP框架暴露为ThreadLocal,以通过AopContext类进行检索如果一个建议的对象需要调用另一个建议的方法(如果它使用{@code this}),调用将不会建议)<p>默
	 * 认值为"false",以避免不必要的额外拦截这意味着AopContext访问将不会在建议对象的任何方法中始终保持一致。
	 * 
	 */
	public void setExposeProxy(boolean exposeProxy) {
		this.exposeProxy = exposeProxy;
	}

	/**
	 * Return whether the AOP proxy will expose the AOP proxy for
	 * each invocation.
	 * <p>
	 * 返回AOP代理是否会为每个调用公开AOP代理
	 * 
	 */
	public boolean isExposeProxy() {
		return this.exposeProxy;
	}

	/**
	 * Set whether this config should be frozen.
	 * <p>When a config is frozen, no advice changes can be made. This is
	 * useful for optimization, and useful when we don't want callers to
	 * be able to manipulate configuration after casting to Advised.
	 * <p>
	 *  设置此配置是否应该被冻结<p>当配置被冻结时,不会改变任何建议。这对于优化是有用的,当我们不希望调用者能够在转换为Advised之后能够操作配置时,这很有用
	 * 
	 */
	public void setFrozen(boolean frozen) {
		this.frozen = frozen;
	}

	/**
	 * Return whether the config is frozen, and no advice changes can be made.
	 * <p>
	 *  返回配置是否被冻结,并且不会改变任何建议
	 * 
	 */
	public boolean isFrozen() {
		return this.frozen;
	}


	/**
	 * Copy configuration from the other config object.
	 * <p>
	 *  从其他配置对象复制配置
	 * 
	 * @param other object to copy configuration from
	 */
	public void copyFrom(ProxyConfig other) {
		Assert.notNull(other, "Other ProxyConfig object must not be null");
		this.proxyTargetClass = other.proxyTargetClass;
		this.optimize = other.optimize;
		this.exposeProxy = other.exposeProxy;
		this.frozen = other.frozen;
		this.opaque = other.opaque;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("proxyTargetClass=").append(this.proxyTargetClass).append("; ");
		sb.append("optimize=").append(this.optimize).append("; ");
		sb.append("opaque=").append(this.opaque).append("; ");
		sb.append("exposeProxy=").append(this.exposeProxy).append("; ");
		sb.append("frozen=").append(this.frozen);
		return sb.toString();
	}

}
