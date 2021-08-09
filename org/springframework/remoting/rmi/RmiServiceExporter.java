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

package org.springframework.remoting.rmi;

import java.rmi.AlreadyBoundException;
import java.rmi.NoSuchObjectException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.UnicastRemoteObject;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * RMI exporter that exposes the specified service as RMI object with the specified name.
 * Such services can be accessed via plain RMI or via {@link RmiProxyFactoryBean}.
 * Also supports exposing any non-RMI service via RMI invokers, to be accessed via
 * {@link RmiClientInterceptor} / {@link RmiProxyFactoryBean}'s automatic detection
 * of such invokers.
 *
 * <p>With an RMI invoker, RMI communication works on the {@link RmiInvocationHandler}
 * level, needing only one stub for any service. Service interfaces do not have to
 * extend {@code java.rmi.Remote} or throw {@code java.rmi.RemoteException}
 * on all methods, but in and out parameters have to be serializable.
 *
 * <p>The major advantage of RMI, compared to Hessian and Burlap, is serialization.
 * Effectively, any serializable Java object can be transported without hassle.
 * Hessian and Burlap have their own (de-)serialization mechanisms, but are
 * HTTP-based and thus much easier to setup than RMI. Alternatively, consider
 * Spring's HTTP invoker to combine Java serialization with HTTP-based transport.
 *
 * <p>Note: RMI makes a best-effort attempt to obtain the fully qualified host name.
 * If one cannot be determined, it will fall back and use the IP address. Depending
 * on your network configuration, in some cases it will resolve the IP to the loopback
 * address. To ensure that RMI will use the host name bound to the correct network
 * interface, you should pass the {@code java.rmi.server.hostname} property to the
 * JVM that will export the registry and/or the service using the "-D" JVM argument.
 * For example: {@code -Djava.rmi.server.hostname=myserver.com}
 *
 * <p>
 * 将指定的服务公开为具有指定名称的RMI对象的RMI导出器可以通过简单的RMI或通过{@link RmiProxyFactoryBean}访问指定的服务。
 * 还支持通过RMI调用者显示任何非RMI服务,以通过{@link RmiClientInterceptor}访问/ {@link RmiProxyFactoryBean}自动检测此类调用者。
 * 
 *  <p>使用RMI调用者,RMI通信在{@link RmiInvocationHandler}级别上工作,只需要一个存根就可以为任何服务提供服务。
 * 服务接口不必在所有方法上扩展{@code javarmiRemote}或抛出{@code javarmiRemoteException}但是进出参数必须是可序列化的。
 * 
 * <p>与Hessian和Burlap相比,RMI的主要优点是有序地进行序列化,任何可序列化的Java对象都可以传输而不麻烦Hessian和Burlap具有自己的(去)序列化机制,但是基于HTTP,因此更
 * 容易要比RMI进行设置或者也可以考虑使用Spring的HTTP调用程序来将Java序列化与基于HTTP的传输结合起来。
 * 
 * <p>注意：RMI尽力尝试获取完全限定的主机名如果无法确定,则它会回退并使用IP地址根据您的网络配置,在某些情况下,将将IP解析为环回地址为了确保RMI将使用绑定到正确网络接口的主机名,您应该将{@code javarmiserverhostname}
 * 属性传递给将使用"-D"JVM参数导出注册表和/或服务的JVM For For示例：{@code -Djavarmiserverhostname = myservercom}。
 * 
 * 
 * @author Juergen Hoeller
 * @since 13.05.2003
 * @see RmiClientInterceptor
 * @see RmiProxyFactoryBean
 * @see java.rmi.Remote
 * @see java.rmi.RemoteException
 * @see org.springframework.remoting.caucho.HessianServiceExporter
 * @see org.springframework.remoting.caucho.BurlapServiceExporter
 * @see org.springframework.remoting.httpinvoker.HttpInvokerServiceExporter
 */
public class RmiServiceExporter extends RmiBasedExporter implements InitializingBean, DisposableBean {

	private String serviceName;

	private int servicePort = 0;  // anonymous port

	private RMIClientSocketFactory clientSocketFactory;

	private RMIServerSocketFactory serverSocketFactory;

	private Registry registry;

	private String registryHost;

	private int registryPort = Registry.REGISTRY_PORT;

	private RMIClientSocketFactory registryClientSocketFactory;

	private RMIServerSocketFactory registryServerSocketFactory;

	private boolean alwaysCreateRegistry = false;

	private boolean replaceExistingBinding = true;

	private Remote exportedObject;

	private boolean createdRegistry = false;


	/**
	 * Set the name of the exported RMI service,
	 * i.e. {@code rmi://host:port/NAME}
	 * <p>
	 *  设置导出的RMI服务的名称,即{@code rmi：// host：port / NAME}
	 * 
	 */
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	/**
	 * Set the port that the exported RMI service will use.
	 * <p>Default is 0 (anonymous port).
	 * <p>
	 *  设置导出的RMI服务将使用的端口<p>默认值为0(匿名端口)
	 * 
	 */
	public void setServicePort(int servicePort) {
		this.servicePort = servicePort;
	}

	/**
	 * Set a custom RMI client socket factory to use for exporting the service.
	 * <p>If the given object also implements {@code java.rmi.server.RMIServerSocketFactory},
	 * it will automatically be registered as server socket factory too.
	 * <p>
	 * 设置用于导出服务的自定义RMI客户端套接字工厂<p>如果给定的对象还实现了{@code javarmiserverRMIServerSocketFactory},它将自动注册为服务器套接字工厂
	 * 
	 * 
	 * @see #setServerSocketFactory
	 * @see java.rmi.server.RMIClientSocketFactory
	 * @see java.rmi.server.RMIServerSocketFactory
	 * @see UnicastRemoteObject#exportObject(Remote, int, RMIClientSocketFactory, RMIServerSocketFactory)
	 */
	public void setClientSocketFactory(RMIClientSocketFactory clientSocketFactory) {
		this.clientSocketFactory = clientSocketFactory;
	}

	/**
	 * Set a custom RMI server socket factory to use for exporting the service.
	 * <p>Only needs to be specified when the client socket factory does not
	 * implement {@code java.rmi.server.RMIServerSocketFactory} already.
	 * <p>
	 *  设置用于导出服务的自定义RMI服务器套接字工厂<p>仅当客户端套接字工厂未实现{@code javarmiserverRMIServerSocketFactory}时才需要指定
	 * 
	 * 
	 * @see #setClientSocketFactory
	 * @see java.rmi.server.RMIClientSocketFactory
	 * @see java.rmi.server.RMIServerSocketFactory
	 * @see UnicastRemoteObject#exportObject(Remote, int, RMIClientSocketFactory, RMIServerSocketFactory)
	 */
	public void setServerSocketFactory(RMIServerSocketFactory serverSocketFactory) {
		this.serverSocketFactory = serverSocketFactory;
	}

	/**
	 * Specify the RMI registry to register the exported service with.
	 * Typically used in combination with RmiRegistryFactoryBean.
	 * <p>Alternatively, you can specify all registry properties locally.
	 * This exporter will then try to locate the specified registry,
	 * automatically creating a new local one if appropriate.
	 * <p>Default is a local registry at the default port (1099),
	 * created on the fly if necessary.
	 * <p>
	 * 指定RMI注册表以注册导出的服务,通常与RmiRegistryFactoryBean结合使用<p>或者,您可以在本地指定所有注册表属性。
	 * 此导出器将尝试查找指定的注册表,如果适用,自动创建新的本地注册表<p>默认是默认端口(1099)上的本地注册表,必要时即时创建。
	 * 
	 * 
	 * @see RmiRegistryFactoryBean
	 * @see #setRegistryHost
	 * @see #setRegistryPort
	 * @see #setRegistryClientSocketFactory
	 * @see #setRegistryServerSocketFactory
	 */
	public void setRegistry(Registry registry) {
		this.registry = registry;
	}

	/**
	 * Set the host of the registry for the exported RMI service,
	 * i.e. {@code rmi://HOST:port/name}
	 * <p>Default is localhost.
	 * <p>
	 *  设置导出的RMI服务的注册表主机,即{@code rmi：// HOST：port / name} <p>默认是localhost
	 * 
	 */
	public void setRegistryHost(String registryHost) {
		this.registryHost = registryHost;
	}

	/**
	 * Set the port of the registry for the exported RMI service,
	 * i.e. {@code rmi://host:PORT/name}
	 * <p>Default is {@code Registry.REGISTRY_PORT} (1099).
	 * <p>
	 *  设置导出的RMI服务的注册表端口,即{@code rmi：// host：PORT / name} <p>默认值为{@code RegistryREGISTRY_PORT}(1099)
	 * 
	 * 
	 * @see java.rmi.registry.Registry#REGISTRY_PORT
	 */
	public void setRegistryPort(int registryPort) {
		this.registryPort = registryPort;
	}

	/**
	 * Set a custom RMI client socket factory to use for the RMI registry.
	 * <p>If the given object also implements {@code java.rmi.server.RMIServerSocketFactory},
	 * it will automatically be registered as server socket factory too.
	 * <p>
	 * 设置用于RMI注册表的自定义RMI客户端套接字工厂<p>如果给定的对象还实现了{@code javarmiserverRMIServerSocketFactory},它将自动注册为服务器套接字工厂
	 * 
	 * 
	 * @see #setRegistryServerSocketFactory
	 * @see java.rmi.server.RMIClientSocketFactory
	 * @see java.rmi.server.RMIServerSocketFactory
	 * @see LocateRegistry#getRegistry(String, int, RMIClientSocketFactory)
	 */
	public void setRegistryClientSocketFactory(RMIClientSocketFactory registryClientSocketFactory) {
		this.registryClientSocketFactory = registryClientSocketFactory;
	}

	/**
	 * Set a custom RMI server socket factory to use for the RMI registry.
	 * <p>Only needs to be specified when the client socket factory does not
	 * implement {@code java.rmi.server.RMIServerSocketFactory} already.
	 * <p>
	 *  设置用于RMI注册表的自定义RMI服务器套接字工厂<p>仅当客户端套接字工厂未实现{@code javarmiserverRMIServerSocketFactory}时才需要指定
	 * 
	 * 
	 * @see #setRegistryClientSocketFactory
	 * @see java.rmi.server.RMIClientSocketFactory
	 * @see java.rmi.server.RMIServerSocketFactory
	 * @see LocateRegistry#createRegistry(int, RMIClientSocketFactory, RMIServerSocketFactory)
	 */
	public void setRegistryServerSocketFactory(RMIServerSocketFactory registryServerSocketFactory) {
		this.registryServerSocketFactory = registryServerSocketFactory;
	}

	/**
	 * Set whether to always create the registry in-process,
	 * not attempting to locate an existing registry at the specified port.
	 * <p>Default is "false". Switch this flag to "true" in order to avoid
	 * the overhead of locating an existing registry when you always
	 * intend to create a new registry in any case.
	 * <p>
	 *  设置是否始终在进程中创建注册表,而不是尝试在指定端口找到现有的注册表。<p>默认值为"false"将此标志切换为"true",以避免在找到现有注册表时的开销总是打算在任何情况下创建一个新的注册表
	 * 
	 */
	public void setAlwaysCreateRegistry(boolean alwaysCreateRegistry) {
		this.alwaysCreateRegistry = alwaysCreateRegistry;
	}

	/**
	 * Set whether to replace an existing binding in the RMI registry,
	 * that is, whether to simply override an existing binding with the
	 * specified service in case of a naming conflict in the registry.
	 * <p>Default is "true", assuming that an existing binding for this
	 * exporter's service name is an accidental leftover from a previous
	 * execution. Switch this to "false" to make the exporter fail in such
	 * a scenario, indicating that there was already an RMI object bound.
	 * <p>
	 * 设置是否替换RMI注册表中的现有绑定,即是否在注册表中的命名冲突时简单地覆盖具有指定服务的现有绑定<p>默认值为"true",假设现有绑定为这个出口商的服务名称是从以前执行的意外剩余切换到"假",以使出
	 * 口商在这种情况下失败,表明已经有一个RMI对象绑定。
	 * 
	 */
	public void setReplaceExistingBinding(boolean replaceExistingBinding) {
		this.replaceExistingBinding = replaceExistingBinding;
	}


	@Override
	public void afterPropertiesSet() throws RemoteException {
		prepare();
	}

	/**
	 * Initialize this service exporter, registering the service as RMI object.
	 * <p>Creates an RMI registry on the specified port if none exists.
	 * <p>
	 *  初始化此服务导出器,将服务注册为RMI对象<p>如果不存在,请在指定端口上创建RMI注册表
	 * 
	 * 
	 * @throws RemoteException if service registration failed
	 */
	public void prepare() throws RemoteException {
		checkService();

		if (this.serviceName == null) {
			throw new IllegalArgumentException("Property 'serviceName' is required");
		}

		// Check socket factories for exported object.
		if (this.clientSocketFactory instanceof RMIServerSocketFactory) {
			this.serverSocketFactory = (RMIServerSocketFactory) this.clientSocketFactory;
		}
		if ((this.clientSocketFactory != null && this.serverSocketFactory == null) ||
				(this.clientSocketFactory == null && this.serverSocketFactory != null)) {
			throw new IllegalArgumentException(
					"Both RMIClientSocketFactory and RMIServerSocketFactory or none required");
		}

		// Check socket factories for RMI registry.
		if (this.registryClientSocketFactory instanceof RMIServerSocketFactory) {
			this.registryServerSocketFactory = (RMIServerSocketFactory) this.registryClientSocketFactory;
		}
		if (this.registryClientSocketFactory == null && this.registryServerSocketFactory != null) {
			throw new IllegalArgumentException(
					"RMIServerSocketFactory without RMIClientSocketFactory for registry not supported");
		}

		this.createdRegistry = false;

		// Determine RMI registry to use.
		if (this.registry == null) {
			this.registry = getRegistry(this.registryHost, this.registryPort,
				this.registryClientSocketFactory, this.registryServerSocketFactory);
			this.createdRegistry = true;
		}

		// Initialize and cache exported object.
		this.exportedObject = getObjectToExport();

		if (logger.isInfoEnabled()) {
			logger.info("Binding service '" + this.serviceName + "' to RMI registry: " + this.registry);
		}

		// Export RMI object.
		if (this.clientSocketFactory != null) {
			UnicastRemoteObject.exportObject(
					this.exportedObject, this.servicePort, this.clientSocketFactory, this.serverSocketFactory);
		}
		else {
			UnicastRemoteObject.exportObject(this.exportedObject, this.servicePort);
		}

		// Bind RMI object to registry.
		try {
			if (this.replaceExistingBinding) {
				this.registry.rebind(this.serviceName, this.exportedObject);
			}
			else {
				this.registry.bind(this.serviceName, this.exportedObject);
			}
		}
		catch (AlreadyBoundException ex) {
			// Already an RMI object bound for the specified service name...
			unexportObjectSilently();
			throw new IllegalStateException(
					"Already an RMI object bound for name '"  + this.serviceName + "': " + ex.toString());
		}
		catch (RemoteException ex) {
			// Registry binding failed: let's unexport the RMI object as well.
			unexportObjectSilently();
			throw ex;
		}
	}


	/**
	 * Locate or create the RMI registry for this exporter.
	 * <p>
	 *  找到或创建此出口商的RMI注册表
	 * 
	 * 
	 * @param registryHost the registry host to use (if this is specified,
	 * no implicit creation of a RMI registry will happen)
	 * @param registryPort the registry port to use
	 * @param clientSocketFactory the RMI client socket factory for the registry (if any)
	 * @param serverSocketFactory the RMI server socket factory for the registry (if any)
	 * @return the RMI registry
	 * @throws RemoteException if the registry couldn't be located or created
	 */
	protected Registry getRegistry(String registryHost, int registryPort,
			RMIClientSocketFactory clientSocketFactory, RMIServerSocketFactory serverSocketFactory)
			throws RemoteException {

		if (registryHost != null) {
			// Host explicitly specified: only lookup possible.
			if (logger.isInfoEnabled()) {
				logger.info("Looking for RMI registry at port '" + registryPort + "' of host [" + registryHost + "]");
			}
			Registry reg = LocateRegistry.getRegistry(registryHost, registryPort, clientSocketFactory);
			testRegistry(reg);
			return reg;
		}

		else {
			return getRegistry(registryPort, clientSocketFactory, serverSocketFactory);
		}
	}

	/**
	 * Locate or create the RMI registry for this exporter.
	 * <p>
	 *  找到或创建此出口商的RMI注册表
	 * 
	 * 
	 * @param registryPort the registry port to use
	 * @param clientSocketFactory the RMI client socket factory for the registry (if any)
	 * @param serverSocketFactory the RMI server socket factory for the registry (if any)
	 * @return the RMI registry
	 * @throws RemoteException if the registry couldn't be located or created
	 */
	protected Registry getRegistry(
			int registryPort, RMIClientSocketFactory clientSocketFactory, RMIServerSocketFactory serverSocketFactory)
			throws RemoteException {

		if (clientSocketFactory != null) {
			if (this.alwaysCreateRegistry) {
				logger.info("Creating new RMI registry");
				return LocateRegistry.createRegistry(registryPort, clientSocketFactory, serverSocketFactory);
			}
			if (logger.isInfoEnabled()) {
				logger.info("Looking for RMI registry at port '" + registryPort + "', using custom socket factory");
			}
			synchronized (LocateRegistry.class) {
				try {
					// Retrieve existing registry.
					Registry reg = LocateRegistry.getRegistry(null, registryPort, clientSocketFactory);
					testRegistry(reg);
					return reg;
				}
				catch (RemoteException ex) {
					logger.debug("RMI registry access threw exception", ex);
					logger.info("Could not detect RMI registry - creating new one");
					// Assume no registry found -> create new one.
					return LocateRegistry.createRegistry(registryPort, clientSocketFactory, serverSocketFactory);
				}
			}
		}

		else {
			return getRegistry(registryPort);
		}
	}

	/**
	 * Locate or create the RMI registry for this exporter.
	 * <p>
	 *  找到或创建此出口商的RMI注册表
	 * 
	 * 
	 * @param registryPort the registry port to use
	 * @return the RMI registry
	 * @throws RemoteException if the registry couldn't be located or created
	 */
	protected Registry getRegistry(int registryPort) throws RemoteException {
		if (this.alwaysCreateRegistry) {
			logger.info("Creating new RMI registry");
			return LocateRegistry.createRegistry(registryPort);
		}
		if (logger.isInfoEnabled()) {
			logger.info("Looking for RMI registry at port '" + registryPort + "'");
		}
		synchronized (LocateRegistry.class) {
			try {
				// Retrieve existing registry.
				Registry reg = LocateRegistry.getRegistry(registryPort);
				testRegistry(reg);
				return reg;
			}
			catch (RemoteException ex) {
				logger.debug("RMI registry access threw exception", ex);
				logger.info("Could not detect RMI registry - creating new one");
				// Assume no registry found -> create new one.
				return LocateRegistry.createRegistry(registryPort);
			}
		}
	}

	/**
	 * Test the given RMI registry, calling some operation on it to
	 * check whether it is still active.
	 * <p>Default implementation calls {@code Registry.list()}.
	 * <p>
	 * 测试给定的RMI注册表,调用一些操作来检查它是否仍然活动<p>默认实现调用{@code Registrylist()}
	 * 
	 * 
	 * @param registry the RMI registry to test
	 * @throws RemoteException if thrown by registry methods
	 * @see java.rmi.registry.Registry#list()
	 */
	protected void testRegistry(Registry registry) throws RemoteException {
		registry.list();
	}


	/**
	 * Unbind the RMI service from the registry on bean factory shutdown.
	 * <p>
	 *  在bean factory关闭后,从注册表中解除绑定RMI服务
	 * 
	 */
	@Override
	public void destroy() throws RemoteException {
		if (logger.isInfoEnabled()) {
			logger.info("Unbinding RMI service '" + this.serviceName +
					"' from registry" + (this.createdRegistry ? (" at port '" + this.registryPort + "'") : ""));
		}
		try {
			this.registry.unbind(this.serviceName);
		}
		catch (NotBoundException ex) {
			if (logger.isWarnEnabled()) {
				logger.warn("RMI service '" + this.serviceName + "' is not bound to registry"
						+ (this.createdRegistry ? (" at port '" + this.registryPort + "' anymore") : ""), ex);
			}
		}
		finally {
			unexportObjectSilently();
		}
	}

	/**
	 * Unexport the registered RMI object, logging any exception that arises.
	 * <p>
	 *  取消注册的RMI对象,记录出现的任何异常
	 */
	private void unexportObjectSilently() {
		try {
			UnicastRemoteObject.unexportObject(this.exportedObject, true);
		}
		catch (NoSuchObjectException ex) {
			if (logger.isWarnEnabled()) {
				logger.warn("RMI object for service '" + this.serviceName + "' isn't exported anymore", ex);
			}
		}
	}
}
