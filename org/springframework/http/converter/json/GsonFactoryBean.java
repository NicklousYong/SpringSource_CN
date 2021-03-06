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

package org.springframework.http.converter.json;

import java.text.SimpleDateFormat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;


/**
 * A {@link FactoryBean} for creating a Google Gson 2.x {@link Gson} instance.
 *
 * <p>
 *  用于创建Google Gson 2x {@link Gson}实例的{@link FactoryBean}
 * 
 * 
 * @author Roy Clarkson
 * @author Juergen Hoeller
 * @since 4.1
 */
public class GsonFactoryBean implements FactoryBean<Gson>, InitializingBean {

	private boolean base64EncodeByteArrays = false;

	private boolean serializeNulls = false;

	private boolean prettyPrinting = false;

	private boolean disableHtmlEscaping = false;

	private String dateFormatPattern;

	private Gson gson;


	/**
	 * Whether to Base64-encode {@code byte[]} properties when reading and
	 * writing JSON.
	 * <p>When set to {@code true}, a custom {@link com.google.gson.TypeAdapter} will be
	 * registered via {@link GsonBuilder#registerTypeHierarchyAdapter(Class, Object)}
	 * which serializes a {@code byte[]} property to and from a Base64-encoded String
	 * instead of a JSON array.
	 * <p><strong>NOTE:</strong> Use of this option requires the presence of the
	 * Apache Commons Codec library on the classpath when running on Java 6 or 7.
	 * On Java 8, the standard {@link java.util.Base64} facility is used instead.
	 * <p>
	 * 当读取和写入JSON时,是否将Base64编码{@code byte []}属性设置为{@code true}时,自定义{@link comgooglegsonTypeAdapter}将通过{@link GsonBuilder#registerTypeHierarchyAdapter(Class, Object)}
	 * ,它将{@code byte []}属性序列化到Base64编码的字符串而不是JSON数组<p> <strong>注意：</strong>使用此选项需要Apache Commons在Java 6或7上运
	 * 行时,类路径上的编解码库在Java 8上,标准的{@link javautilBase64}工具被替代使用。
	 * 
	 * 
	 * @see GsonBuilderUtils#gsonBuilderWithBase64EncodedByteArrays()
	 */
	public void setBase64EncodeByteArrays(boolean base64EncodeByteArrays) {
		this.base64EncodeByteArrays = base64EncodeByteArrays;
	}

	/**
	 * Whether to use the {@link GsonBuilder#serializeNulls()} option when writing
	 * JSON. This is a shortcut for setting up a {@code Gson} as follows:
	 * <pre class="code">
	 * new GsonBuilder().serializeNulls().create();
	 * </pre>
	 * <p>
	 *  是否在编写JSON时使用{@link GsonBuilder#serializeNulls()}选项这是设置{@code Gson}的快捷方式,如下所示：
	 * <pre class="code">
	 *  new GsonBuilder()serializeNulls()create();
	 * </pre>
	 */
	public void setSerializeNulls(boolean serializeNulls) {
		this.serializeNulls = serializeNulls;
	}

	/**
	 * Whether to use the {@link GsonBuilder#setPrettyPrinting()} when writing
	 * JSON. This is a shortcut for setting up a {@code Gson} as follows:
	 * <pre class="code">
	 * new GsonBuilder().setPrettyPrinting().create();
	 * </pre>
	 * <p>
	 * 是否在编写JSON时使用{@link GsonBuilder#setPrettyPrinting()}这是一个设置{@code Gson}的快捷方式,如下所示：
	 * <pre class="code">
	 *  new GsonBuilder()setPrettyPrinting()create();
	 * </pre>
	 */
	public void setPrettyPrinting(boolean prettyPrinting) {
		this.prettyPrinting = prettyPrinting;
	}

	/**
	 * Whether to use the {@link GsonBuilder#disableHtmlEscaping()} when writing
	 * JSON. Set to {@code true} to disable HTML escaping in JSON. This is a
	 * shortcut for setting up a {@code Gson} as follows:
	 * <pre class="code">
	 * new GsonBuilder().disableHtmlEscaping().create();
	 * </pre>
	 * <p>
	 *  将JSON设置为{@code true}时,是否使用{@link GsonBuilder#disableHtmlEscaping()}来禁用JSON中的HTML转义这是一个设置{@code Gson}
	 * 的快捷方式,如下所示：。
	 * <pre class="code">
	 *  new GsonBuilder()disableHtmlEscaping()create();
	 * </pre>
	 */
	public void setDisableHtmlEscaping(boolean disableHtmlEscaping) {
		this.disableHtmlEscaping = disableHtmlEscaping;
	}

	/**
	 * Define the date/time format with a {@link SimpleDateFormat}-style pattern.
	 * This is a shortcut for setting up a {@code Gson} as follows:
	 * <pre class="code">
	 * new GsonBuilder().setDateFormat(dateFormatPattern).create();
	 * </pre>
	 * <p>
	 *  使用{@link SimpleDateFormat}样式模式定义日期/时间格式这是一个设置{@code Gson}的快捷方式,如下所示：
	 * <pre class="code">
	 *  new GsonBuilder()setDateFormat(dateFormatPattern)create();
	 * </pre>
	 */
	public void setDateFormatPattern(String dateFormatPattern) {
		this.dateFormatPattern = dateFormatPattern;
	}


	@Override
	public void afterPropertiesSet() {
		GsonBuilder builder = (this.base64EncodeByteArrays ?
				GsonBuilderUtils.gsonBuilderWithBase64EncodedByteArrays() : new GsonBuilder());
		if (this.serializeNulls) {
			builder.serializeNulls();
		}
		if (this.prettyPrinting) {
			builder.setPrettyPrinting();
		}
		if (this.disableHtmlEscaping) {
			builder.disableHtmlEscaping();
		}
		if (this.dateFormatPattern != null) {
			builder.setDateFormat(this.dateFormatPattern);
		}
		this.gson = builder.create();
	}


	/**
	 * Return the created Gson instance.
	 * <p>
	 */
	@Override
	public Gson getObject() {
		return this.gson;
	}

	@Override
	public Class<?> getObjectType() {
		return Gson.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

}
