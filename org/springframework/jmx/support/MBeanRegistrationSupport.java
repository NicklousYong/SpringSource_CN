/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2014 the original author or authors.
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

package org.springframework.jmx.support;

import java.util.LinkedHashSet;
import java.util.Set;
import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.ObjectInstance;
import javax.management.ObjectName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.core.Constants;
import org.springframework.util.Assert;

/**
 * Provides supporting infrastructure for registering MBeans with an
 * {@link javax.management.MBeanServer}. The behavior when encountering
 * an existing MBean at a given {@link ObjectName} is fully configurable
 * allowing for flexible registration settings.
 *
 * <p>All registered MBeans are tracked and can be unregistered by calling
 * the #{@link #unregisterBeans()} method.
 *
 * <p>Sub-classes can receive notifications when an MBean is registered or
 * unregistered by overriding the {@link #onRegister(ObjectName)} and
 * {@link #onUnregister(ObjectName)} methods respectively.
 *
 * <p>By default, the registration process will fail if attempting to
 * register an MBean using a {@link javax.management.ObjectName} that is
 * already used.
 *
 * <p>By setting the {@link #setRegistrationPolicy(RegistrationPolicy) registrationPolicy}
 * property to {@link RegistrationPolicy#IGNORE_EXISTING} the registration process
 * will simply ignore existing MBeans leaving them registered. This is useful in settings
 * where multiple applications want to share a common MBean in a shared {@link MBeanServer}.
 *
 * <p>Setting {@link #setRegistrationPolicy(RegistrationPolicy) registrationPolicy} property
 * to {@link RegistrationPolicy#REPLACE_EXISTING} will cause existing MBeans to be replaced
 * during registration if necessary. This is useful in situations where you can't guarantee
 * the state of your {@link MBeanServer}.
 *
 * <p>
 * 提供用于使用{@link javaxmanagementMBeanServer}注册MBean的支持基础架构在给定的{@link ObjectName}遇到现有MBean时的行为是完全可配置的,允许灵活
 * 的注册设置。
 * 
 *  <p>所有注册的MBean都被跟踪,可以通过调用#{@ link #unregisterBeans()}方法来取消注册
 * 
 *  通过分别覆盖{@link #onRegister(ObjectName)}和{@link #onUnregister(ObjectName))方法,子类可以在MBean注册或未注册时接收通知
 * 
 *  <p>默认情况下,如果尝试使用已经使用的{@link javaxmanagementObjectName}注册MBean,注册过程将失败
 * 
 * <p>通过将{@link #setRegistrationPolicy(RegistrationPolicy)registrationPolicy}属性设置为{@link RegistrationPolicy#IGNORE_EXISTING}
 * ,注册过程将简单地忽略将其注册的现有MBeans。
 * 这对于多个应用程序想要共享一个MBean的设置很有用在共享的{@link MBeanServer}。
 * 
 *  <p>将{@link #setRegistrationPolicy(RegistrationPolicy)registrationPolicy}属性设置为{@link RegistrationPolicy#REPLACE_EXISTING}
 * 将导致现有MBean在注册期间被替换,这在您无法保证{ @link MBeanServer}。
 * 
 * 
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @author Phillip Webb
 * @since 2.0
 * @see #setServer
 * @see #setRegistrationPolicy
 * @see org.springframework.jmx.export.MBeanExporter
 */
public class MBeanRegistrationSupport {

	/**
	 * Constant indicating that registration should fail when
	 * attempting to register an MBean under a name that already exists.
	 * <p>This is the default registration behavior.
	 * <p>
	 * 常量表示尝试在已存在的名称下注册MBean时注册失败<p>这是默认的注册行为
	 * 
	 * 
	 * @deprecated since Spring 3.2, in favor of {@link RegistrationPolicy#FAIL_ON_EXISTING}
	 */
	@Deprecated
	public static final int REGISTRATION_FAIL_ON_EXISTING = 0;

	/**
	 * Constant indicating that registration should ignore the affected MBean
	 * when attempting to register an MBean under a name that already exists.
	 * <p>
	 *  常数表示在尝试在已经存在的名称下注册MBean时,注册应该忽略受影响的MBean
	 * 
	 * 
	 * @deprecated since Spring 3.2, in favor of {@link RegistrationPolicy#IGNORE_EXISTING}
	 */
	@Deprecated
	public static final int REGISTRATION_IGNORE_EXISTING = 1;

	/**
	 * Constant indicating that registration should replace the affected MBean
	 * when attempting to register an MBean under a name that already exists.
	 * <p>
	 *  常数表示在尝试在已经存在的名称下注册MBean时,注册应该替换受影响的MBean
	 * 
	 * 
	 * @deprecated since Spring 3.2, in favor of {@link RegistrationPolicy#REPLACE_EXISTING}
	 */
	@Deprecated
	public static final int REGISTRATION_REPLACE_EXISTING = 2;


	/**
	 * Constants for this class.
	 * <p>
	 *  这个班的常数
	 * 
	 */
	private static final Constants constants = new Constants(MBeanRegistrationSupport.class);

	/**
	 * {@code Log} instance for this class.
	 * <p>
	 *  这个类的{@code Log}实例
	 * 
	 */
	protected final Log logger = LogFactory.getLog(getClass());

	/**
	 * The {@code MBeanServer} instance being used to register beans.
	 * <p>
	 *  用于注册bean的{@code MBeanServer}实例
	 * 
	 */
	protected MBeanServer server;

	/**
	 * The beans that have been registered by this exporter.
	 * <p>
	 *  已由该出口商注册的豆类
	 * 
	 */
	private final Set<ObjectName> registeredBeans = new LinkedHashSet<ObjectName>();

	/**
	 * The policy used when registering an MBean and finding that it already exists.
	 * By default an exception is raised.
	 * <p>
	 *  注册MBean并发现它已经存在时使用的策略默认情况下,引发异常
	 * 
	 */
	private RegistrationPolicy registrationPolicy = RegistrationPolicy.FAIL_ON_EXISTING;


	/**
	 * Specify the {@code MBeanServer} instance with which all beans should
	 * be registered. The {@code MBeanExporter} will attempt to locate an
	 * existing {@code MBeanServer} if none is supplied.
	 * <p>
	 * 指定要注册所有bean的{@code MBeanServer}实例{@code MBeanExporter}将尝试找到现有的{@code MBeanServer}(如果没有提供)
	 * 
	 */
	public void setServer(MBeanServer server) {
		this.server = server;
	}

	/**
	 * Return the {@code MBeanServer} that the beans will be registered with.
	 * <p>
	 *  返回bean将被注册的{@code MBeanServer}
	 * 
	 */
	public final MBeanServer getServer() {
		return this.server;
	}

	/**
	 * Set the registration behavior by the name of the corresponding constant,
	 * e.g. "REGISTRATION_IGNORE_EXISTING".
	 * <p>
	 *  通过相应常数的名称设置注册行为,例如"REGISTRATION_IGNORE_EXISTING"
	 * 
	 * 
	 * @see #setRegistrationBehavior
	 * @see #REGISTRATION_FAIL_ON_EXISTING
	 * @see #REGISTRATION_IGNORE_EXISTING
	 * @see #REGISTRATION_REPLACE_EXISTING
	 * @deprecated since Spring 3.2, in favor of {@link #setRegistrationPolicy(RegistrationPolicy)}
	 */
	@Deprecated
	public void setRegistrationBehaviorName(String registrationBehavior) {
		setRegistrationBehavior(constants.asNumber(registrationBehavior).intValue());
	}

	/**
	 * Specify what action should be taken when attempting to register an MBean
	 * under an {@link javax.management.ObjectName} that already exists.
	 * <p>Default is REGISTRATION_FAIL_ON_EXISTING.
	 * <p>
	 *  指定在已经存在的{@link javaxmanagementObjectName}下尝试注册MBean时应采取的操作默认值为REGISTRATION_FAIL_ON_EXISTING
	 * 
	 * 
	 * @see #setRegistrationBehaviorName(String)
	 * @see #REGISTRATION_FAIL_ON_EXISTING
	 * @see #REGISTRATION_IGNORE_EXISTING
	 * @see #REGISTRATION_REPLACE_EXISTING
	 * @deprecated since Spring 3.2, in favor of {@link #setRegistrationPolicy(RegistrationPolicy)}
	 */
	@Deprecated
	public void setRegistrationBehavior(int registrationBehavior) {
		setRegistrationPolicy(RegistrationPolicy.valueOf(registrationBehavior));
	}

	/**
	 * The policy to use when attempting to register an MBean
	 * under an {@link javax.management.ObjectName} that already exists.
	 * <p>
	 *  尝试在已经存在的{@link javaxmanagementObjectName}下注册MBean时使用的策略
	 * 
	 * 
	 * @param registrationPolicy the policy to use
	 * @since 3.2
	 */
	public void setRegistrationPolicy(RegistrationPolicy registrationPolicy) {
		Assert.notNull(registrationPolicy, "RegistrationPolicy must not be null");
		this.registrationPolicy = registrationPolicy;
	}


	/**
	 * Actually register the MBean with the server. The behavior when encountering
	 * an existing MBean can be configured using the {@link #setRegistrationBehavior(int)}
	 * and {@link #setRegistrationBehaviorName(String)} methods.
	 * <p>
	 * 实际上使用服务器注册MBean可以使用{@link #setRegistrationBehavior(int)}和{@link #setRegistrationBehaviorName(String)}
	 * 方法来配置遇到现有MBean时的行为。
	 * 
	 * 
	 * @param mbean the MBean instance
	 * @param objectName the suggested ObjectName for the MBean
	 * @throws JMException if the registration failed
	 */
	protected void doRegister(Object mbean, ObjectName objectName) throws JMException {
		ObjectName actualObjectName;

		synchronized (this.registeredBeans) {
			ObjectInstance registeredBean = null;
			try {
				registeredBean = this.server.registerMBean(mbean, objectName);
			}
			catch (InstanceAlreadyExistsException ex) {
				if (this.registrationPolicy == RegistrationPolicy.IGNORE_EXISTING) {
					if (logger.isDebugEnabled()) {
						logger.debug("Ignoring existing MBean at [" + objectName + "]");
					}
				}
				else if (this.registrationPolicy == RegistrationPolicy.REPLACE_EXISTING) {
					try {
						if (logger.isDebugEnabled()) {
							logger.debug("Replacing existing MBean at [" + objectName + "]");
						}
						this.server.unregisterMBean(objectName);
						registeredBean = this.server.registerMBean(mbean, objectName);
					}
					catch (InstanceNotFoundException ex2) {
						logger.error("Unable to replace existing MBean at [" + objectName + "]", ex2);
						throw ex;
					}
				}
				else {
					throw ex;
				}
			}

			// Track registration and notify listeners.
			actualObjectName = (registeredBean != null ? registeredBean.getObjectName() : null);
			if (actualObjectName == null) {
				actualObjectName = objectName;
			}
			this.registeredBeans.add(actualObjectName);
		}

		onRegister(actualObjectName, mbean);
	}

	/**
	 * Unregisters all beans that have been registered by an instance of this class.
	 * <p>
	 *  取消注册由该类的实例注册的所有bean
	 * 
	 */
	protected void unregisterBeans() {
		Set<ObjectName> snapshot;
		synchronized (this.registeredBeans) {
			snapshot = new LinkedHashSet<ObjectName>(this.registeredBeans);
		}
		if (!snapshot.isEmpty()) {
			logger.info("Unregistering JMX-exposed beans");
		}
		for (ObjectName objectName : snapshot) {
			doUnregister(objectName);
		}
	}

	/**
	 * Actually unregister the specified MBean from the server.
	 * <p>
	 *  实际上从服务器注销指定的MBean
	 * 
	 * 
	 * @param objectName the suggested ObjectName for the MBean
	 */
	protected void doUnregister(ObjectName objectName) {
		boolean actuallyUnregistered = false;

		synchronized (this.registeredBeans) {
			if (this.registeredBeans.remove(objectName)) {
				try {
					// MBean might already have been unregistered by an external process
					if (this.server.isRegistered(objectName)) {
						this.server.unregisterMBean(objectName);
						actuallyUnregistered = true;
					}
					else {
						if (logger.isWarnEnabled()) {
							logger.warn("Could not unregister MBean [" + objectName + "] as said MBean " +
									"is not registered (perhaps already unregistered by an external process)");
						}
					}
				}
				catch (JMException ex) {
					if (logger.isErrorEnabled()) {
						logger.error("Could not unregister MBean [" + objectName + "]", ex);
					}
				}
			}
		}

		if (actuallyUnregistered) {
			onUnregister(objectName);
		}
	}

	/**
	 * Return the {@link ObjectName ObjectNames} of all registered beans.
	 * <p>
	 *  返回所有注册的bean的{@link ObjectName ObjectNames}
	 * 
	 */
	protected final ObjectName[] getRegisteredObjectNames() {
		synchronized (this.registeredBeans) {
			return this.registeredBeans.toArray(new ObjectName[this.registeredBeans.size()]);
		}
	}


	/**
	 * Called when an MBean is registered under the given {@link ObjectName}. Allows
	 * subclasses to perform additional processing when an MBean is registered.
	 * <p>The default implementation delegates to {@link #onRegister(ObjectName)}.
	 * <p>
	 *  当MBean在给定的{@link ObjectName}下注册时被调用允许子类在注册MBean时执行其他处理<p>默认实现委托给{@link #onRegister(ObjectName)}
	 * 
	 * 
	 * @param objectName the actual {@link ObjectName} that the MBean was registered with
	 * @param mbean the registered MBean instance
	 */
	protected void onRegister(ObjectName objectName, Object mbean) {
		onRegister(objectName);
	}

	/**
	 * Called when an MBean is registered under the given {@link ObjectName}. Allows
	 * subclasses to perform additional processing when an MBean is registered.
	 * <p>The default implementation is empty. Can be overridden in subclasses.
	 * <p>
	 * 当MBean在给定的{@link ObjectName}下注册时被调用允许子类在注册MBean时执行其他处理<p>默认实现为空可以在子类中覆盖
	 * 
	 * 
	 * @param objectName the actual {@link ObjectName} that the MBean was registered with
	 */
	protected void onRegister(ObjectName objectName) {
	}

	/**
	 * Called when an MBean is unregistered under the given {@link ObjectName}. Allows
	 * subclasses to perform additional processing when an MBean is unregistered.
	 * <p>The default implementation is empty. Can be overridden in subclasses.
	 * <p>
	 *  在指定的{@link ObjectName}下取消注册MBean时调用此方法允许子类在MBean未注册时执行其他处理<p>默认实现为空可以在子类中覆盖
	 * 
	 * @param objectName the {@link ObjectName} that the MBean was registered with
	 */
	protected void onUnregister(ObjectName objectName) {
	}

}
