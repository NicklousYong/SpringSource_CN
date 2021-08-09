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

package org.springframework.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.Properties;

/**
 * Default implementation of the {@link PropertiesPersister} interface.
 * Follows the native parsing of {@code java.util.Properties}.
 *
 * <p>Allows for reading from any Reader and writing to any Writer, for example
 * to specify a charset for a properties file. This is a capability that standard
 * {@code java.util.Properties} unfortunately lacked up until JDK 5:
 * You were only able to load files using the ISO-8859-1 charset there.
 *
 * <p>Loading from and storing to a stream delegates to {@code Properties.load}
 * and {@code Properties.store}, respectively, to be fully compatible with
 * the Unicode conversion as implemented by the JDK Properties class. As of JDK 6,
 * {@code Properties.load/store} will also be used for readers/writers,
 * effectively turning this class into a plain backwards compatibility adapter.
 *
 * <p>The persistence code that works with Reader/Writer follows the JDK's parsing
 * strategy but does not implement Unicode conversion, because the Reader/Writer
 * should already apply proper decoding/encoding of characters. If you use prefer
 * to escape unicode characters in your properties files, do <i>not</i> specify
 * an encoding for a Reader/Writer (like ReloadableResourceBundleMessageSource's
 * "defaultEncoding" and "fileEncodings" properties).
 *
 * <p>
 *  {@link PropertiesPersister}界面的默认实现遵循本地解析{@code javautilProperties}
 * 
 * <p>允许从任何读者阅读并写入任何作家,例如为属性文件指定字符集这是标准的{@code javautilProperties}在JDK 5之前不幸缺少的功能：您只能加载文件在那里使用ISO-8859-1
 * 字符集。
 * 
 *  <p>从流加载和存储到流分别将代理到{@code Propertiesload}和{@code Propertiesstore},以完全兼容JDK Properties类实现的Unicode转换。
 * 从JDK 6开始,{@code Propertiesload / store}也将用于读者/作家,有效地将此类转换为简单的向后兼容性适配器。
 * 
 * @author Juergen Hoeller
 * @since 10.03.2004
 * @see java.util.Properties
 * @see java.util.Properties#load
 * @see java.util.Properties#store
 */
public class DefaultPropertiesPersister implements PropertiesPersister {

	@Override
	public void load(Properties props, InputStream is) throws IOException {
		props.load(is);
	}

	@Override
	public void load(Properties props, Reader reader) throws IOException {
		props.load(reader);
	}

	@Override
	public void store(Properties props, OutputStream os, String header) throws IOException {
		props.store(os, header);
	}

	@Override
	public void store(Properties props, Writer writer, String header) throws IOException {
		props.store(writer, header);
	}

	@Override
	public void loadFromXml(Properties props, InputStream is) throws IOException {
		props.loadFromXML(is);
	}

	@Override
	public void storeToXml(Properties props, OutputStream os, String header) throws IOException {
		props.storeToXML(os, header);
	}

	@Override
	public void storeToXml(Properties props, OutputStream os, String header, String encoding) throws IOException {
		props.storeToXML(os, header, encoding);
	}

}