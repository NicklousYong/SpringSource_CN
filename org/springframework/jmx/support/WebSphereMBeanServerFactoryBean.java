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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.management.MBeanServer;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jmx.MBeanServerNotFoundException;

/**
 * {@link FactoryBean} that obtains a WebSphere {@link javax.management.MBeanServer}
 * reference through WebSphere's proprietary {@code AdminServiceFactory} API,
 * available on WebSphere 5.1 and higher.
 *
 * <p>Exposes the {@code MBeanServer} for bean references.
 * This FactoryBean is a direct alternative to {@link MBeanServerFactoryBean},
 * which uses standard JMX 1.2 API to access the platform's MBeanServer.
 *
 * <p>See the javadocs for WebSphere's
 * <a href="http://bit.ly/UzccDt">{@code AdminServiceFactory}</a>
 * and <a href="http://bit.ly/TRlX2r">{@code MBeanFactory}</a>.
 *
 * <p>
 *  {@link FactoryBean},通过WebSphere专有的{@code AdminServiceFactory} API获得WebSphere {@link javaxmanagementMBeanServer}
 * 引用,可在WebSphere 51及更高版本上使用。
 * 
 * <p>为bean引用使用{@code MBeanServer}此FactoryBean是{@link MBeanServerFactoryBean}的直接替代品,它使用标准的JMX 12 API访问平台
 * 的MBeanServer。
 * 
 * 
 * @author Juergen Hoeller
 * @author Rob Harrop
 * @since 2.0.3
 * @see javax.management.MBeanServer
 * @see MBeanServerFactoryBean
 */
public class WebSphereMBeanServerFactoryBean implements FactoryBean<MBeanServer>, InitializingBean {

	private static final String ADMIN_SERVICE_FACTORY_CLASS = "com.ibm.websphere.management.AdminServiceFactory";

	private static final String GET_MBEAN_FACTORY_METHOD = "getMBeanFactory";

	private static final String GET_MBEAN_SERVER_METHOD = "getMBeanServer";


	private MBeanServer mbeanServer;


	@Override
	public void afterPropertiesSet() throws MBeanServerNotFoundException {
		try {
			/*
			 * this.mbeanServer = AdminServiceFactory.getMBeanFactory().getMBeanServer();
			 * <p>
			 *  <p>请参阅WebSphere的<a href=\"http://bitly/UzccDt\"> {@code AdminServiceFactory} </a>和<a href=\"http://bitly/TRlX2r\">
			 *  {@code MBeanFactory的javadocs } </A>。
			 * 
			 */
			Class<?> adminServiceClass = getClass().getClassLoader().loadClass(ADMIN_SERVICE_FACTORY_CLASS);
			Method getMBeanFactoryMethod = adminServiceClass.getMethod(GET_MBEAN_FACTORY_METHOD);
			Object mbeanFactory = getMBeanFactoryMethod.invoke(null);
			Method getMBeanServerMethod = mbeanFactory.getClass().getMethod(GET_MBEAN_SERVER_METHOD);
			this.mbeanServer = (MBeanServer) getMBeanServerMethod.invoke(mbeanFactory);
		}
		catch (ClassNotFoundException ex) {
			throw new MBeanServerNotFoundException("Could not find WebSphere's AdminServiceFactory class", ex);
		}
		catch (InvocationTargetException ex) {
			throw new MBeanServerNotFoundException(
					"WebSphere's AdminServiceFactory.getMBeanFactory/getMBeanServer method failed", ex.getTargetException());
		}
		catch (Exception ex) {
			throw new MBeanServerNotFoundException(
					"Could not access WebSphere's AdminServiceFactory.getMBeanFactory/getMBeanServer method", ex);
		}
	}


	@Override
	public MBeanServer getObject() {
		return this.mbeanServer;
	}

	@Override
	public Class<? extends MBeanServer> getObjectType() {
		return (this.mbeanServer != null ? this.mbeanServer.getClass() : MBeanServer.class);
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

}
