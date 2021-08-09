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
import java.net.URL;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceEditor;
import org.springframework.util.Assert;

/**
 * Editor for {@code java.net.URL}, to directly populate a URL property
 * instead of using a String property as bridge.
 *
 * <p>Supports Spring-style URL notation: any fully qualified standard URL
 * ("file:", "http:", etc) and Spring's special "classpath:" pseudo-URL,
 * as well as Spring's context-specific relative file paths.
 *
 * <p>Note: A URL must specify a valid protocol, else it will be rejected
 * upfront. However, the target resource does not necessarily have to exist
 * at the time of URL creation; this depends on the specific resource type.
 *
 * <p>
 *  编辑{@code javanetURL},直接填充URL属性,而不是使用String属性作为桥
 * 
 * <p>支持Spring样式的URL表示法：任何完全合格的标准URL("file：","http："等)和Spring的特殊"classpath："伪URL,以及Spring的上下文相关文件路径
 * 
 *  <p>注意：URL必须指定一个有效的协议,否则将被拒绝。但是,在创建URL时,目标资源不一定必须存在;这取决于具体的资源类型
 * 
 * 
 * @author Juergen Hoeller
 * @since 15.12.2003
 * @see java.net.URL
 * @see org.springframework.core.io.ResourceEditor
 * @see org.springframework.core.io.ResourceLoader
 * @see FileEditor
 * @see InputStreamEditor
 */
public class URLEditor extends PropertyEditorSupport {

	private final ResourceEditor resourceEditor;


	/**
	 * Create a new URLEditor, using a default ResourceEditor underneath.
	 * <p>
	 */
	public URLEditor() {
		this.resourceEditor = new ResourceEditor();
	}

	/**
	 * Create a new URLEditor, using the given ResourceEditor underneath.
	 * <p>
	 *  创建一个新的URLEditor,使用下面的默认ResourceEditor
	 * 
	 * 
	 * @param resourceEditor the ResourceEditor to use
	 */
	public URLEditor(ResourceEditor resourceEditor) {
		Assert.notNull(resourceEditor, "ResourceEditor must not be null");
		this.resourceEditor = resourceEditor;
	}


	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		this.resourceEditor.setAsText(text);
		Resource resource = (Resource) this.resourceEditor.getValue();
		try {
			setValue(resource != null ? resource.getURL() : null);
		}
		catch (IOException ex) {
			throw new IllegalArgumentException("Could not retrieve URL for " + resource + ": " + ex.getMessage());
		}
	}

	@Override
	public String getAsText() {
		URL value = (URL) getValue();
		return (value != null ? value.toExternalForm() : "");
	}

}
