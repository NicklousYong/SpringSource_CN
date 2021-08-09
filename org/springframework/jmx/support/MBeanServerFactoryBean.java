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

package org.springframework.jmx.support;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jmx.MBeanServerNotFoundException;

/**
 * {@link FactoryBean} that obtains an {@link javax.management.MBeanServer} reference
 * through the standard JMX 1.2 {@link javax.management.MBeanServerFactory}
 * API (which is available on JDK 1.5 or as part of a JMX 1.2 provider).
 * Exposes the {@code MBeanServer} for bean references.
 *
 * <p>By default, {@code MBeanServerFactoryBean} will always create
 * a new {@code MBeanServer} even if one is already running. To have
 * the {@code MBeanServerFactoryBean} attempt to locate a running
 * {@code MBeanServer} first, set the value of the
 * "locateExistingServerIfPossible" property to "true".
 *
 * <p>
 * {@link FactoryBean}通过标准的JMX 12 {@link javaxmanagementMBeanServerFactory} API(在JDK 15中可用,或作为JMX 12提供程序
 * 的一部分)获取一个{@link javaxmanagementMBeanServer}引用。
 * 为Bean显示{@code MBeanServer}引用。
 * 
 *  <p>默认情况下,{@code MBeanServerFactoryBean}将始终创建一个新的{@code MBeanServer}即使已经在运行。
 * 要使{@code MBeanServerFactoryBean}尝试首先找到正在运行的{@code MBeanServer},请设置值的"locateExistingServerIfPossible"属
 * 性为"true"。
 *  <p>默认情况下,{@code MBeanServerFactoryBean}将始终创建一个新的{@code MBeanServer}即使已经在运行。
 * 
 * 
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @since 1.2
 * @see #setLocateExistingServerIfPossible
 * @see #locateMBeanServer
 * @see javax.management.MBeanServer
 * @see javax.management.MBeanServerFactory#findMBeanServer
 * @see javax.management.MBeanServerFactory#createMBeanServer
 * @see javax.management.MBeanServerFactory#newMBeanServer
 * @see MBeanServerConnectionFactoryBean
 * @see ConnectorServerFactoryBean
 */
public class MBeanServerFactoryBean implements FactoryBean<MBeanServer>, InitializingBean, DisposableBean {

	protected final Log logger = LogFactory.getLog(getClass());

	private boolean locateExistingServerIfPossible = false;

	private String agentId;

	private String defaultDomain;

	private boolean registerWithFactory = true;

	private MBeanServer server;

	private boolean newlyRegistered = false;


	/**
	 * Set whether or not the {@code MBeanServerFactoryBean} should attempt
	 * to locate a running {@code MBeanServer} before creating one.
	 * <p>Default is {@code false}.
	 * <p>
	 *  设置{@code MBeanServerFactoryBean}是否应在创建之前尝试找到正在运行的{@code MBeanServer} <p>默认值为{@code false}
	 * 
	 */
	public void setLocateExistingServerIfPossible(boolean locateExistingServerIfPossible) {
		this.locateExistingServerIfPossible = locateExistingServerIfPossible;
	}

	/**
	 * Set the agent id of the {@code MBeanServer} to locate.
	 * <p>Default is none. If specified, this will result in an
	 * automatic attempt being made to locate the attendant MBeanServer,
	 * and (importantly) if said MBeanServer cannot be located no
	 * attempt will be made to create a new MBeanServer (and an
	 * MBeanServerNotFoundException will be thrown at resolution time).
	 * <p>Specifying the empty String indicates the platform MBeanServer.
	 * <p>
	 * 设置{@code MBeanServer}的代理ID来定位<p>默认值为none如果指定,这将导致自动尝试找到话务员MBeanServer,并且(重要的是)如果所述MBeanServer不能找到,则不会
	 * 尝试创建一个新的MBeanServer(并且MBeanServerNotFoundException将在解析时抛出)<p>指定空字符串表示平台MBeanServer。
	 * 
	 * 
	 * @see javax.management.MBeanServerFactory#findMBeanServer(String)
	 */
	public void setAgentId(String agentId) {
		this.agentId = agentId;
	}

	/**
	 * Set the default domain to be used by the {@code MBeanServer},
	 * to be passed to {@code MBeanServerFactory.createMBeanServer()}
	 * or {@code MBeanServerFactory.findMBeanServer()}.
	 * <p>Default is none.
	 * <p>
	 *  设置要由{@code MBeanServer}使用的默认域,以传递给{@code MBeanServerFactorycreateMBeanServer()}或{@code MBeanServerFactoryfindMBeanServer()}
	 *  <p>默认值为none。
	 * 
	 * 
	 * @see javax.management.MBeanServerFactory#createMBeanServer(String)
	 * @see javax.management.MBeanServerFactory#findMBeanServer(String)
	 */
	public void setDefaultDomain(String defaultDomain) {
		this.defaultDomain = defaultDomain;
	}

	/**
	 * Set whether to register the {@code MBeanServer} with the
	 * {@code MBeanServerFactory}, making it available through
	 * {@code MBeanServerFactory.findMBeanServer()}.
	 * <p>
	 *  设置是否使用{@code MBeanServerFactory}注册{@code MBeanServer},使其可以通过{@code MBeanServerFactoryfindMBeanServer()}
	 * 。
	 * 
	 * 
	 * @see javax.management.MBeanServerFactory#createMBeanServer
	 * @see javax.management.MBeanServerFactory#findMBeanServer
	 */
	public void setRegisterWithFactory(boolean registerWithFactory) {
		this.registerWithFactory = registerWithFactory;
	}


	/**
	 * Creates the {@code MBeanServer} instance.
	 * <p>
	 * 创建{@code MBeanServer}实例
	 * 
	 */
	@Override
	public void afterPropertiesSet() throws MBeanServerNotFoundException {
		// Try to locate existing MBeanServer, if desired.
		if (this.locateExistingServerIfPossible || this.agentId != null) {
			try {
				this.server = locateMBeanServer(this.agentId);
			}
			catch (MBeanServerNotFoundException ex) {
				// If agentId was specified, we were only supposed to locate that
				// specific MBeanServer; so let's bail if we can't find it.
				if (this.agentId != null) {
					throw ex;
				}
				logger.info("No existing MBeanServer found - creating new one");
			}
		}

		// Create a new MBeanServer and register it, if desired.
		if (this.server == null) {
			this.server = createMBeanServer(this.defaultDomain, this.registerWithFactory);
			this.newlyRegistered = this.registerWithFactory;
		}
	}

	/**
	 * Attempt to locate an existing {@code MBeanServer}.
	 * Called if {@code locateExistingServerIfPossible} is set to {@code true}.
	 * <p>The default implementation attempts to find an {@code MBeanServer} using
	 * a standard lookup. Subclasses may override to add additional location logic.
	 * <p>
	 *  如果{@code locateExistingServerIfPossible}设置为{@code true} <p>,则尝试查找现有的{@code MBeanServer}。
	 * 默认实现尝试使用标准查找找到{@code MBeanServer}子类可以覆盖以添加其他位置逻辑。
	 * 
	 * 
	 * @param agentId the agent identifier of the MBeanServer to retrieve.
	 * If this parameter is {@code null}, all registered MBeanServers are
	 * considered.
	 * @return the {@code MBeanServer} if found
	 * @throws org.springframework.jmx.MBeanServerNotFoundException
	 * if no {@code MBeanServer} could be found
	 * @see #setLocateExistingServerIfPossible
	 * @see JmxUtils#locateMBeanServer(String)
	 * @see javax.management.MBeanServerFactory#findMBeanServer(String)
	 */
	protected MBeanServer locateMBeanServer(String agentId) throws MBeanServerNotFoundException {
		return JmxUtils.locateMBeanServer(agentId);
	}

	/**
	 * Create a new {@code MBeanServer} instance and register it with the
	 * {@code MBeanServerFactory}, if desired.
	 * <p>
	 *  创建一个新的{@code MBeanServer}实例,并将其注册到{@code MBeanServerFactory},如果需要的话
	 * 
	 * 
	 * @param defaultDomain the default domain, or {@code null} if none
	 * @param registerWithFactory whether to register the {@code MBeanServer}
	 * with the {@code MBeanServerFactory}
	 * @see javax.management.MBeanServerFactory#createMBeanServer
	 * @see javax.management.MBeanServerFactory#newMBeanServer
	 */
	protected MBeanServer createMBeanServer(String defaultDomain, boolean registerWithFactory) {
		if (registerWithFactory) {
			return MBeanServerFactory.createMBeanServer(defaultDomain);
		}
		else {
			return MBeanServerFactory.newMBeanServer(defaultDomain);
		}
	}


	@Override
	public MBeanServer getObject() {
		return this.server;
	}

	@Override
	public Class<? extends MBeanServer> getObjectType() {
		return (this.server != null ? this.server.getClass() : MBeanServer.class);
	}

	@Override
	public boolean isSingleton() {
		return true;
	}


	/**
	 * Unregisters the {@code MBeanServer} instance, if necessary.
	 * <p>
	 *  如果需要,请取消注册{@code MBeanServer}实例
	 */
	@Override
	public void destroy() {
		if (this.newlyRegistered) {
			MBeanServerFactory.releaseMBeanServer(this.server);
		}
	}

}
