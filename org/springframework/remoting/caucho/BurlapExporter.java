/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2013 the original author or authors.
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

package org.springframework.remoting.caucho;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.caucho.burlap.io.BurlapInput;
import com.caucho.burlap.io.BurlapOutput;
import com.caucho.burlap.server.BurlapSkeleton;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.remoting.support.RemoteExporter;
import org.springframework.util.Assert;

/**
 * General stream-based protocol exporter for a Burlap endpoint.
 *
 * <p>Burlap is a slim, XML-based RPC protocol.
 * For information on Burlap, see the
 * <a href="http://www.caucho.com/burlap">Burlap website</a>.
 * This exporter requires Burlap 3.x.
 *
 * <p>
 *  用于Burlap端点的基于通用流的协议导出器
 * 
 * <p> Burlap是一个纤薄的基于XML的RPC协议有关Burlap的信息,请参阅<a href=\"http://wwwcauchocom/burlap\"> Burlap网站</a>此出口商需要B
 * urlap 3x。
 * 
 * 
 * @author Juergen Hoeller
 * @since 2.5.1
 * @see #invoke(java.io.InputStream, java.io.OutputStream)
 * @see BurlapServiceExporter
 * @see SimpleBurlapServiceExporter
 * @deprecated as of Spring 4.0, since Burlap hasn't evolved in years
 * and is effectively retired (in contrast to its sibling Hessian)
 */
@Deprecated
public class BurlapExporter extends RemoteExporter implements InitializingBean {

	private BurlapSkeleton skeleton;


	@Override
	public void afterPropertiesSet() {
		prepare();
	}

	/**
	 * Initialize this service exporter.
	 * <p>
	 *  初始化此服务导出器
	 * 
	 */
	public void prepare() {
		checkService();
		checkServiceInterface();
		this.skeleton = new BurlapSkeleton(getProxyForService(), getServiceInterface());
	}


	/**
	 * Perform an invocation on the exported object.
	 * <p>
	 *  对导出的对象执行调用
	 * 
	 * @param inputStream the request stream
	 * @param outputStream the response stream
	 * @throws Throwable if invocation failed
	 */
	public void invoke(InputStream inputStream, OutputStream outputStream) throws Throwable {
		Assert.notNull(this.skeleton, "Burlap exporter has not been initialized");
		ClassLoader originalClassLoader = overrideThreadContextClassLoader();
		try {
			this.skeleton.invoke(new BurlapInput(inputStream), new BurlapOutput(outputStream));
		}
		finally {
			try {
				inputStream.close();
			}
			catch (IOException ex) {
				// ignore
			}
			try {
				outputStream.close();
			}
			catch (IOException ex) {
				// ignore
			}
			resetThreadContextClassLoader(originalClassLoader);
		}
	}

}
