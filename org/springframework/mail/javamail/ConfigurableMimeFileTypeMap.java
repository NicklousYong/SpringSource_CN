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

package org.springframework.mail.javamail;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.activation.FileTypeMap;
import javax.activation.MimetypesFileTypeMap;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * Spring-configurable {@code FileTypeMap} implementation that will read
 * MIME type to file extension mappings from a standard JavaMail MIME type
 * mapping file, using a standard {@code MimetypesFileTypeMap} underneath.
 *
 * <p>The mapping file should be in the following format, as specified by the
 * Java Activation Framework:
 *
 * <pre class="code">
 * # map text/html to .htm and .html files
 * text/html  html htm HTML HTM</pre>
 *
 * Lines starting with {@code #} are treated as comments and are ignored. All
 * other lines are treated as mappings. Each mapping line should contain the MIME
 * type as the first entry and then each file extension to map to that MIME type
 * as subsequent entries. Each entry is separated by spaces or tabs.
 *
 * <p>By default, the mappings in the {@code mime.types} file located in the
 * same package as this class are used, which cover many common file extensions
 * (in contrast to the out-of-the-box mappings in {@code activation.jar}).
 * This can be overridden using the {@code mappingLocation} property.
 *
 * <p>Additional mappings can be added via the {@code mappings} bean property,
 * as lines that follow the {@code mime.types} file format.
 *
 * <p>
 *  Spring可配置的{@code FileTypeMap}实现,将使用标准的{@code MimetypesFileTypeMap}从标准Ja​​vaMail MIME类型映射文件读取MIME类型到文
 * 件扩展名映射。
 * 
 * <p>映射文件应采用以下格式,由Java Activation Framework指定：
 * 
 * <pre class="code">
 *  #map text / html to htm and html files text / html html htm HTML HTM </pre>
 * 
 *  以{@code#}开头的行将被视为注释,并被忽略所有其他行都被视为映射每个映射行应包含MIME类型作为第一个条目,然后将每个文件扩展名映射到该MIME类型作为后续条目每个条目由空格或制表符分隔
 * 
 *  <p>默认情况下,使用位于与此类相同的包中的{@code mimetypes}文件中的映射,涵盖许多常见的文件扩展名(与{@code中的开箱即用映射相反) activationjar})这可以使用{@code mappingLocation}
 * 属性覆盖。
 * 
 * <p>其他映射可以通过{@code mappings} bean属性添加,按照{@code mimetypes}文件格式
 * 
 * 
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @since 1.2
 * @see #setMappingLocation
 * @see #setMappings
 * @see javax.activation.MimetypesFileTypeMap
 */
public class ConfigurableMimeFileTypeMap extends FileTypeMap implements InitializingBean {

	/**
	 * The {@code Resource} to load the mapping file from.
	 * <p>
	 *  加载映射文件的{@code Resource}
	 * 
	 */
	private Resource mappingLocation = new ClassPathResource("mime.types", getClass());

	/**
	 * Used to configure additional mappings.
	 * <p>
	 *  用于配置其他映射
	 * 
	 */
	private String[] mappings;

	/**
	 * The delegate FileTypeMap, compiled from the mappings in the mapping file
	 * and the entries in the {@code mappings} property.
	 * <p>
	 *  代理FileTypeMap,从映射文件中的映射和{@code映射}属性中的条目编译
	 * 
	 */
	private FileTypeMap fileTypeMap;


	/**
	 * Specify the {@code Resource} from which mappings are loaded.
	 * <p>Needs to follow the {@code mime.types} file format, as specified
	 * by the Java Activation Framework, containing lines such as:<br>
	 * {@code text/html  html htm HTML HTM}
	 * <p>
	 *  指定加载映射的{@code资源} <p>需要按照Java Activation Framework指定的{@code mimetypes}文件格式,包含以下行：<br> {@code text / html html htm HTML HTM}
	 * 。
	 * 
	 */
	public void setMappingLocation(Resource mappingLocation) {
		this.mappingLocation = mappingLocation;
	}

	/**
	 * Specify additional MIME type mappings as lines that follow the
	 * {@code mime.types} file format, as specified by the
	 * Java Activation Framework, for example:<br>
	 * {@code text/html  html htm HTML HTM}
	 * <p>
	 *  指定附加的MIME类型映射作为Java激活框架指定的{@code mimetypes}文件格式的行,例如：<br> {@code text / html html htm HTML HTM}
	 * 
	 */
	public void setMappings(String... mappings) {
		this.mappings = mappings;
	}


	/**
	 * Creates the final merged mapping set.
	 * <p>
	 * 创建最终合并的映射集
	 * 
	 */
	@Override
	public void afterPropertiesSet() {
		getFileTypeMap();
	}

	/**
	 * Return the delegate FileTypeMap, compiled from the mappings in the mapping file
	 * and the entries in the {@code mappings} property.
	 * <p>
	 *  返回代理FileTypeMap,从映射文件中的映射和{@code映射}属性中的条目编译
	 * 
	 * 
	 * @see #setMappingLocation
	 * @see #setMappings
	 * @see #createFileTypeMap
	 */
	protected final FileTypeMap getFileTypeMap() {
		if (this.fileTypeMap == null) {
			try {
				this.fileTypeMap = createFileTypeMap(this.mappingLocation, this.mappings);
			}
			catch (IOException ex) {
				throw new IllegalStateException(
						"Could not load specified MIME type mapping file: " + this.mappingLocation, ex);
			}
		}
		return this.fileTypeMap;
	}

	/**
	 * Compile a {@link FileTypeMap} from the mappings in the given mapping file
	 * and the given mapping entries.
	 * <p>The default implementation creates an Activation Framework {@link MimetypesFileTypeMap},
	 * passing in an InputStream from the mapping resource (if any) and registering
	 * the mapping lines programmatically.
	 * <p>
	 *  从给定的映射文件和给定的映射条目的映射中编译{@link FileTypeMap} <p>默认实现创建一个激活框架{@link MimetypesFileTypeMap},从映射资源(如果有的话)传入
	 * 一个InputStream,并注册以编程方式映射线。
	 * 
	 * 
	 * @param mappingLocation a {@code mime.types} mapping resource (can be {@code null})
	 * @param mappings MIME type mapping lines (can be {@code null})
	 * @return the compiled FileTypeMap
	 * @throws IOException if resource access failed
	 * @see javax.activation.MimetypesFileTypeMap#MimetypesFileTypeMap(java.io.InputStream)
	 * @see javax.activation.MimetypesFileTypeMap#addMimeTypes(String)
	 */
	protected FileTypeMap createFileTypeMap(Resource mappingLocation, String[] mappings) throws IOException {
		MimetypesFileTypeMap fileTypeMap = null;
		if (mappingLocation != null) {
			InputStream is = mappingLocation.getInputStream();
			try {
				fileTypeMap = new MimetypesFileTypeMap(is);
			}
			finally {
				is.close();
			}
		}
		else {
			fileTypeMap = new MimetypesFileTypeMap();
		}
		if (mappings != null) {
			for (String mapping : mappings) {
				fileTypeMap.addMimeTypes(mapping);
			}
		}
		return fileTypeMap;
	}


	/**
	 * Delegates to the underlying FileTypeMap.
	 * <p>
	 *  委托到底层FileTypeMap
	 * 
	 * 
	 * @see #getFileTypeMap()
	 */
	@Override
	public String getContentType(File file) {
		return getFileTypeMap().getContentType(file);
	}

	/**
	 * Delegates to the underlying FileTypeMap.
	 * <p>
	 *  委托到底层FileTypeMap
	 * 
	 * @see #getFileTypeMap()
	 */
	@Override
	public String getContentType(String fileName) {
		return getFileTypeMap().getContentType(fileName);
	}

}
