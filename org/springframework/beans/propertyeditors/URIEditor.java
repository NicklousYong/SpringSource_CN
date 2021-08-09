/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2016 the original author or authors.
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

package org.springframework.beans.propertyeditors;

import java.beans.PropertyEditorSupport;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.core.io.ClassPathResource;
import org.springframework.util.ClassUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;

/**
 * Editor for {@code java.net.URI}, to directly populate a URI property
 * instead of using a String property as bridge.
 *
 * <p>Supports Spring-style URI notation: any fully qualified standard URI
 * ("file:", "http:", etc) and Spring's special "classpath:" pseudo-URL,
 * which will be resolved to a corresponding URI.
 *
 * <p>By default, this editor will encode Strings into URIs. For instance,
 * a space will be encoded into {@code %20}. This behavior can be changed
 * by calling the {@link #URIEditor(boolean)} constructor.
 *
 * <p>Note: A URI is more relaxed than a URL in that it does not require
 * a valid protocol to be specified. Any scheme within a valid URI syntax
 * is allowed, even without a matching protocol handler being registered.
 *
 * <p>
 *  编辑器{@code javanetURI},直接填充URI属性,而不是使用String属性作为桥
 * 
 * 支持Spring样式的URI表示法：任何完全合格的标准URI("file：","http："等)和Spring的特殊"classpath："伪URL,将被解析为相应的URI
 * 
 *  <p>默认情况下,此编辑器将字符串编码为URI例如,空格将被编码为{@code％20}。可以通过调用{@link #URIEditor(boolean)}构造函数来更改此行为
 * 
 *  <p>注意：URI比URL更宽松,因为它不需要指定有效的协议。即使没有配置协议处理程序被注册,也允许使用有效的URI语法中的任何方案
 * 
 * 
 * @author Juergen Hoeller
 * @since 2.0.2
 * @see java.net.URI
 * @see URLEditor
 */
public class URIEditor extends PropertyEditorSupport {

	private final ClassLoader classLoader;

	private final boolean encode;



	/**
	 * Create a new, encoding URIEditor, converting "classpath:" locations into
	 * standard URIs (not trying to resolve them into physical resources).
	 * <p>
	 *  创建一个新的编码URIEditor,将"classpath："位置转换为标准URI(不尝试将其解析为物理资源)
	 * 
	 */
	public URIEditor() {
		this(true);
	}

	/**
	 * Create a new URIEditor, converting "classpath:" locations into
	 * standard URIs (not trying to resolve them into physical resources).
	 * <p>
	 * 创建一个新的URIEditor,将"classpath："位置转换为标准URI(不要将其解析为物理资源)
	 * 
	 * 
	 * @param encode indicates whether Strings will be encoded or not
	 */
	public URIEditor(boolean encode) {
		this.classLoader = null;
		this.encode = encode;
	}

	/**
	 * Create a new URIEditor, using the given ClassLoader to resolve
	 * "classpath:" locations into physical resource URLs.
	 * <p>
	 *  创建一个新的URIEditor,使用给定的ClassLoader将"classpath："位置解析为物理资源URL
	 * 
	 * 
	 * @param classLoader the ClassLoader to use for resolving "classpath:" locations
	 * (may be {@code null} to indicate the default ClassLoader)
	 */
	public URIEditor(ClassLoader classLoader) {
		this(classLoader, true);
	}

	/**
	 * Create a new URIEditor, using the given ClassLoader to resolve
	 * "classpath:" locations into physical resource URLs.
	 * <p>
	 *  创建一个新的URIEditor,使用给定的ClassLoader将"classpath："位置解析为物理资源URL
	 * 
	 * 
	 * @param classLoader the ClassLoader to use for resolving "classpath:" locations
	 * (may be {@code null} to indicate the default ClassLoader)
	 * @param encode indicates whether Strings will be encoded or not
	 */
	public URIEditor(ClassLoader classLoader, boolean encode) {
		this.classLoader = (classLoader != null ? classLoader : ClassUtils.getDefaultClassLoader());
		this.encode = encode;
	}


	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		if (StringUtils.hasText(text)) {
			String uri = text.trim();
			if (this.classLoader != null && uri.startsWith(ResourceUtils.CLASSPATH_URL_PREFIX)) {
				ClassPathResource resource =
						new ClassPathResource(uri.substring(ResourceUtils.CLASSPATH_URL_PREFIX.length()), this.classLoader);
				try {
					String url = resource.getURL().toString();
					setValue(createURI(url));
				}
				catch (IOException ex) {
					throw new IllegalArgumentException("Could not retrieve URI for " + resource + ": " + ex.getMessage());
				}
				catch (URISyntaxException ex) {
					throw new IllegalArgumentException("Invalid URI syntax: " + ex);
				}
			}
			else {
				try {
					setValue(createURI(uri));
				}
				catch (URISyntaxException ex) {
					throw new IllegalArgumentException("Invalid URI syntax: " + ex);
				}
			}
		}
		else {
			setValue(null);
		}
	}

	/**
	 * Create a URI instance for the given (resolved) String value.
	 * <p>The default implementation encodes the value into a RFC
	 * 2396 compliant URI.
	 * <p>
	 *  为给定(已解析的)String值创建一个URI实例<p>默认实现将该值编码为符合RFC 2396标准的URI
	 * 
	 * @param value the value to convert into a URI instance
	 * @return the URI instance
	 * @throws java.net.URISyntaxException if URI conversion failed
	 */
	protected URI createURI(String value) throws URISyntaxException {
		int colonIndex = value.indexOf(':');
		if (this.encode && colonIndex != -1) {
			int fragmentIndex = value.indexOf('#', colonIndex + 1);
			String scheme = value.substring(0, colonIndex);
			String ssp = value.substring(colonIndex + 1, (fragmentIndex > 0 ? fragmentIndex : value.length()));
			String fragment = (fragmentIndex > 0 ? value.substring(fragmentIndex + 1) : null);
			return new URI(scheme, ssp, fragment);
		}
		else {
			// not encoding or the value contains no scheme - fallback to default
			return new URI(value);
		}
	}


	@Override
	public String getAsText() {
		URI value = (URI) getValue();
		return (value != null ? value.toString() : "");
	}

}
