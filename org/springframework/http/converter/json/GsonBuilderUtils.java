/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2015 the original author or authors.
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

import java.lang.reflect.Type;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import org.springframework.util.Base64Utils;

/**
 * A simple utility class for obtaining a Google Gson 2.x {@link GsonBuilder}
 * which Base64-encodes {@code byte[]} properties when reading and writing JSON.
 *
 * <p>
 *  一个简单的实用工具类,用于在读取和写入JSON时获取一个Google Gson 2x {@link GsonBuilder},Base64编码{@code byte []}属性
 * 
 * 
 * @author Juergen Hoeller
 * @author Roy Clarkson
 * @since 4.1
 * @see GsonFactoryBean#setBase64EncodeByteArrays
 * @see org.springframework.util.Base64Utils
 */
public abstract class GsonBuilderUtils {

	/**
	 * Obtain a {@link GsonBuilder} which Base64-encodes {@code byte[]}
	 * properties when reading and writing JSON.
	 * <p>A custom {@link com.google.gson.TypeAdapter} will be registered via
	 * {@link GsonBuilder#registerTypeHierarchyAdapter(Class, Object)} which
	 * serializes a {@code byte[]} property to and from a Base64-encoded String
	 * instead of a JSON array.
	 * <p><strong>NOTE:</strong> Use of this option requires the presence of the
	 * Apache Commons Codec library on the classpath when running on Java 6 or 7.
	 * On Java 8, the standard {@link java.util.Base64} facility is used instead.
	 * <p>
	 * 获取一个{@link GsonBuilder},当读取和写入JSON时,获取B​​ase64编码{@code byte []}属性。
	 * <p>自定义{@link comgooglegsonTypeAdapter}将通过{@link GsonBuilder#registerTypeHierarchyAdapter(Class,Object)}
	 * 注册}它将{@code byte []}属性序列化为Base64编码的字符串而不是JSON数组<p> <strong>注意：</strong>使用此选项需要Apache Commons Codec库的存
	 */
	public static GsonBuilder gsonBuilderWithBase64EncodedByteArrays() {
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeHierarchyAdapter(byte[].class, new Base64TypeAdapter());
		return builder;
	}


	private static class Base64TypeAdapter implements JsonSerializer<byte[]>, JsonDeserializer<byte[]> {

		@Override
		public JsonElement serialize(byte[] src, Type typeOfSrc, JsonSerializationContext context) {
			return new JsonPrimitive(Base64Utils.encodeToString(src));
		}

		@Override
		public byte[] deserialize(JsonElement json, Type type, JsonDeserializationContext cxt) {
			return Base64Utils.decodeFromString(json.getAsString());
		}
	}

}
