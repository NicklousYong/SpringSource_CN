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

package org.springframework.oxm.support;

import java.io.IOException;

import org.xml.sax.InputSource;

import org.springframework.core.io.Resource;

/**
 * Convenient utility methods for dealing with SAX.
 *
 * <p>
 *  方便处理SAX的实用方法
 * 
 * 
 * @author Arjen Poutsma
 * @author Juergen Hoeller
 * @since 3.0
 */
public abstract class SaxResourceUtils {

	/**
	 * Create a SAX {@code InputSource} from the given resource.
	 * <p>Sets the system identifier to the resource's {@code URL}, if available.
	 * <p>
	 *  从给定资源创建SAX {@code InputSource} <p>将系统标识符设置为资源的{@code URL}(如果可用)
	 * 
	 * 
	 * @param resource the resource
	 * @return the input source created from the resource
	 * @throws IOException if an I/O exception occurs
	 * @see InputSource#setSystemId(String)
	 * @see #getSystemId(org.springframework.core.io.Resource)
	 */
	public static InputSource createInputSource(Resource resource) throws IOException {
		InputSource inputSource = new InputSource(resource.getInputStream());
		inputSource.setSystemId(getSystemId(resource));
		return inputSource;
	}

	/**
	 * Retrieve the URL from the given resource as System ID.
	 * <p>Returns {@code null} if it cannot be opened.
	 * <p>
	 * 从给定资源中检索URL作为系统ID <p>如果无法打开,返回{@code null}
	 */
	private static String getSystemId(Resource resource) {
		try {
			return resource.getURI().toString();
		}
		catch (IOException ex) {
			return null;
		}
	}

}
