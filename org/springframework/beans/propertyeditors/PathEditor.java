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
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceEditor;
import org.springframework.core.io.ResourceLoader;
import org.springframework.lang.UsesJava7;
import org.springframework.util.Assert;

/**
 * Editor for {@code java.nio.file.Path}, to directly populate a Path
 * property instead of using a String property as bridge.
 *
 * <p>Based on {@link Paths#get(URI)}'s resolution algorithm, checking
 * registered NIO file system providers, including the default file system
 * for "file:..." paths. Also supports Spring-style URL notation: any fully
 * qualified standard URL and Spring's special "classpath:" pseudo-URL, as
 * well as Spring's context-specific relative file paths. As a fallback, a
 * path will be resolved in the file system via {@code Paths#get(String)}
 * if no existing context-relative resource could be found.
 *
 * <p>
 *  编辑器{@code javaniofilePath},直接填充Path属性而不是使用String属性作为桥
 * 
 * <p>基于{@link Paths#get(URI)}的分辨率算法,检查注册的NIO文件系统提供程序,包括"file："路径的默认文件系统还支持Spring样式的URL表示法：任何完全符合条件的标准UR
 * L和Spring的特殊"classpath："伪URL,以及Spring的上下文相关文件路径作为一个回退,如果没有现有的上下文,路径将通过{@code Paths#get(String)}在文件系统中解
 * 析可以找到相关资源。
 * 
 * 
 * @author Juergen Hoeller
 * @since 4.3.2
 * @see java.nio.file.Path
 * @see Paths#get(URI)
 * @see ResourceEditor
 * @see org.springframework.core.io.ResourceLoader
 * @see FileEditor
 * @see URLEditor
 */
@UsesJava7
public class PathEditor extends PropertyEditorSupport {

	private final ResourceEditor resourceEditor;


	/**
	 * Create a new PathEditor, using the default ResourceEditor underneath.
	 * <p>
	 */
	public PathEditor() {
		this.resourceEditor = new ResourceEditor();
	}

	/**
	 * Create a new PathEditor, using the given ResourceEditor underneath.
	 * <p>
	 *  创建一个新的PathEditor,使用下面的默认ResourceEditor
	 * 
	 * 
	 * @param resourceEditor the ResourceEditor to use
	 */
	public PathEditor(ResourceEditor resourceEditor) {
		Assert.notNull(resourceEditor, "ResourceEditor must not be null");
		this.resourceEditor = resourceEditor;
	}


	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		boolean nioPathCandidate = !text.startsWith(ResourceLoader.CLASSPATH_URL_PREFIX);
		if (nioPathCandidate && !text.startsWith("/")) {
			try {
				URI uri = new URI(text);
				if (uri.getScheme() != null) {
					nioPathCandidate = false;
					// Let's try NIO file system providers via Paths.get(URI)
					setValue(Paths.get(uri).normalize());
					return;
				}
			}
			catch (URISyntaxException ex) {
				// Not a valid URI: Let's try as Spring resource location.
			}
			catch (FileSystemNotFoundException ex) {
				// URI scheme not registered for NIO:
				// Let's try URL protocol handlers via Spring's resource mechanism.
			}
		}

		this.resourceEditor.setAsText(text);
		Resource resource = (Resource) this.resourceEditor.getValue();
		if (resource == null) {
			setValue(null);
		}
		else if (!resource.exists() && nioPathCandidate) {
			setValue(Paths.get(text).normalize());
		}
		else {
			try {
				setValue(resource.getFile().toPath());
			}
			catch (IOException ex) {
				throw new IllegalArgumentException("Failed to retrieve file for " + resource, ex);
			}
		}
	}

	@Override
	public String getAsText() {
		Path value = (Path) getValue();
		return (value != null ? value.toString() : "");
	}

}
