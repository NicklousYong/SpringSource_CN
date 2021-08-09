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

import java.rmi.NoSuchObjectException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Properties;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jndi.JndiTemplate;

/**
 * Service exporter which binds RMI services to JNDI.
 * Typically used for RMI-IIOP (CORBA).
 *
 * <p>Exports services via the {@link javax.rmi.PortableRemoteObject} class.
 * You need to run "rmic" with the "-iiop" option to generate corresponding
 * stubs and skeletons for each exported service.
 *
 * <p>Also supports exposing any non-RMI service via RMI invokers, to be accessed
 * via {@link JndiRmiClientInterceptor} / {@link JndiRmiProxyFactoryBean}'s
 * automatic detection of such invokers.
 *
 * <p>With an RMI invoker, RMI communication works on the {@link RmiInvocationHandler}
 * level, needing only one stub for any service. Service interfaces do not have to
 * extend {@code java.rmi.Remote} or throw {@code java.rmi.RemoteException}
 * on all methods, but in and out parameters have to be serializable.
 *
 * <p>The JNDI environment can be specified as "jndiEnvironment" bean property,
 * or be configured in a {@code jndi.properties} file or as system properties.
 * For example:
 *
 * <pre class="code">&lt;property name="jndiEnvironment"&gt;
 * 	 &lt;props>
 *		 &lt;prop key="java.naming.factory.initial"&gt;com.sun.jndi.cosnaming.CNCtxFactory&lt;/prop&gt;
 *		 &lt;prop key="java.naming.provider.url"&gt;iiop://localhost:1050&lt;/prop&gt;
 *	 &lt;/props&gt;
 * &lt;/property&gt;</pre>
 *
 * <p>
 *  将RMI服务绑定到JNDI的服务导出器通常用于RMI-IIOP(CORBA)
 * 
 * <p>通过{@link javaxrmiPortableRemoteObject}类导出服务您需要使用"-iiop"选项运行"rmic",为每个导出的服务生成相应的存根和骨架
 * 
 *  <p>还支持通过RMI调用者公开任何非RMI服务,以通过{@link JndiRmiClientInterceptor} / {@link JndiRmiProxyFactoryBean}自动检测此类
 * 调用者。
 * 
 *  <p>使用RMI调用者,RMI通信在{@link RmiInvocationHandler}级别上工作,只需要一个存根就可以为任何服务提供服务。
 * 服务接口不必在所有方法上扩展{@code javarmiRemote}或抛出{@code javarmiRemoteException}但是进出参数必须是可序列化的。
 * 
 * <p> JNDI环境可以指定为"jndiEnvironment"bean属性,或者可以在{@code jndiproperties}文件或系统属性中进行配置例如：
 * 
 *  <pre class ="code">&lt; property name ="jndiEnvironment"&gt;
 * &lt;props>
 *  &lt; prop key ="javanamingfactoryinitial"&gt; comsunjndicosnamingCNCtxFactory&lt; / prop&gt; &lt; pr
 * op key ="javanamingproviderurl"&gt; iiop：// localhost：1050&lt; / prop&gt; &LT; /道具&GT; &LT; /性&gt; </PRE>
 * 。
 * 
 * 
 * @author Juergen Hoeller
 * @since 1.1
 * @see #setService
 * @see #setJndiTemplate
 * @see #setJndiEnvironment
 * @see #setJndiName
 * @see JndiRmiClientInterceptor
 * @see JndiRmiProxyFactoryBean
 * @see javax.rmi.PortableRemoteObject#exportObject
 */
public class JndiRmiServiceExporter extends RmiBasedExporter implements InitializingBean, DisposableBean {

	private JndiTemplate jndiTemplate = new JndiTemplate();

	private String jndiName;

	private Remote exportedObject;


	/**
	 * Set the JNDI template to use for JNDI lookups.
	 * You can also specify JNDI environment settings via "jndiEnvironment".
	 * <p>
	 * 
	 * @see #setJndiEnvironment
	 */
	public void setJndiTemplate(JndiTemplate jndiTemplate) {
		this.jndiTemplate = (jndiTemplate != null ? jndiTemplate : new JndiTemplate());
	}

	/**
	 * Set the JNDI environment to use for JNDI lookups.
	 * Creates a JndiTemplate with the given environment settings.
	 * <p>
	 *  设置用于JNDI查找的JNDI模板您还可以通过"jndiEnvironment"指定JNDI环境设置
	 * 
	 * 
	 * @see #setJndiTemplate
	 */
	public void setJndiEnvironment(Properties jndiEnvironment) {
		this.jndiTemplate = new JndiTemplate(jndiEnvironment);
	}

	/**
	 * Set the JNDI name of the exported RMI service.
	 * <p>
	 *  将JNDI环境设置为用于JNDI查找使用给定的环境设置创建JndiTemplate
	 * 
	 */
	public void setJndiName(String jndiName) {
		this.jndiName = jndiName;
	}


	@Override
	public void afterPropertiesSet() throws NamingException, RemoteException {
		prepare();
	}

	/**
	 * Initialize this service exporter, binding the specified service to JNDI.
	 * <p>
	 *  设置导出的RMI服务的JNDI名称
	 * 
	 * 
	 * @throws NamingException if service binding failed
	 * @throws RemoteException if service export failed
	 */
	public void prepare() throws NamingException, RemoteException {
		if (this.jndiName == null) {
			throw new IllegalArgumentException("Property 'jndiName' is required");
		}

		// Initialize and cache exported object.
		this.exportedObject = getObjectToExport();
		PortableRemoteObject.exportObject(this.exportedObject);

		rebind();
	}

	/**
	 * Rebind the specified service to JNDI, for recovering in case
	 * of the target registry having been restarted.
	 * <p>
	 *  初始化此服务导出器,将指定的服务绑定到JNDI
	 * 
	 * 
	 * @throws NamingException if service binding failed
	 */
	public void rebind() throws NamingException {
		if (logger.isInfoEnabled()) {
			logger.info("Binding RMI service to JNDI location [" + this.jndiName + "]");
		}
		this.jndiTemplate.rebind(this.jndiName, this.exportedObject);
	}

	/**
	 * Unbind the RMI service from JNDI on bean factory shutdown.
	 * <p>
	 * 将指定的服务重新绑定到JNDI,以便在目标注册表重新启动的情况下进行恢复
	 * 
	 */
	@Override
	public void destroy() throws NamingException, NoSuchObjectException {
		if (logger.isInfoEnabled()) {
			logger.info("Unbinding RMI service from JNDI location [" + this.jndiName + "]");
		}
		this.jndiTemplate.unbind(this.jndiName);
		PortableRemoteObject.unexportObject(this.exportedObject);
	}

}
