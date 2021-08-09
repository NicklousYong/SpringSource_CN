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

import java.nio.charset.Charset;
import java.util.Base64;
import javax.xml.bind.DatatypeConverter;

import org.springframework.lang.UsesJava8;

/**
 * A simple utility class for Base64 encoding and decoding.
 *
 * <p>Adapts to either Java 8's {@link java.util.Base64} class or Apache Commons Codec's
 * {@link org.apache.commons.codec.binary.Base64} class. With neither Java 8 nor Commons
 * Codec present, {@link #encode}/{@link #decode} calls will throw an IllegalStateException.
 * However, as of Spring 4.2, {@link #encodeToString} and {@link #decodeFromString} will
 * nevertheless work since they can delegate to the JAXB DatatypeConverter as a fallback.
 * However, this does not apply when using the "UrlSafe" methods for RFC 4648 "URL and
 * Filename Safe Alphabet"; a delegate is required.
 *
 * <p><em>Note:</em> Apache Commons Codec does not add padding ({@code =}) when encoding
 * with the URL and Filename Safe Alphabet.
 *
 * <p>
 *  一个用于Base64编码和解码的简单实用类
 * 
 * 适用于Java 8的{@link javautilBase64}类或Apache Commons Codec的{@link orgapachecommonscodecbinaryBase64}类不存在J
 * ava 8和Commons Codec,{@link #encode} / {@ link #decode}调用将会抛出IllegalStateException但是,从Spring 42开始,{@link #encodeToString}
 * 和{@link #decodeFromString}仍然可以正常工作,因为它们可以作为回退委托给JAXB DatatypeConverter但是,当对RFC使用"UrlSafe"方法时,这不适用4648
 * "URL and Filename Safe Alphabet";需要代表。
 * 
 *  注意：</em> Apache Commons Codec在使用URL和文件名安全字母编码时不添加填充({@code =})
 * 
 * 
 * @author Juergen Hoeller
 * @author Gary Russell
 * @since 4.1
 * @see java.util.Base64
 * @see org.apache.commons.codec.binary.Base64
 * @see javax.xml.bind.DatatypeConverter#printBase64Binary
 * @see javax.xml.bind.DatatypeConverter#parseBase64Binary
 */
public abstract class Base64Utils {

	private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");


	private static final Base64Delegate delegate;

	static {
		Base64Delegate delegateToUse = null;
		// JDK 8's java.util.Base64 class present?
		if (ClassUtils.isPresent("java.util.Base64", Base64Utils.class.getClassLoader())) {
			delegateToUse = new JdkBase64Delegate();
		}
		// Apache Commons Codec present on the classpath?
		else if (ClassUtils.isPresent("org.apache.commons.codec.binary.Base64", Base64Utils.class.getClassLoader())) {
			delegateToUse = new CommonsCodecBase64Delegate();
		}
		delegate = delegateToUse;
	}

	/**
	 * Assert that Byte64 encoding between byte arrays is actually supported.
	 * <p>
	 *  断言实际支持字节数组之间的Byte64编码
	 * 
	 * 
	 * @throws IllegalStateException if neither Java 8 nor Apache Commons Codec is present
	 */
	private static void assertDelegateAvailable() {
		Assert.state(delegate != null,
				"Neither Java 8 nor Apache Commons Codec found - Base64 encoding between byte arrays not supported");
	}


	/**
	 * Base64-encode the given byte array.
	 * <p>
	 * Base64编码给定的字节数组
	 * 
	 * 
	 * @param src the original byte array (may be {@code null})
	 * @return the encoded byte array (or {@code null} if the input was {@code null})
	 * @throws IllegalStateException if Base64 encoding between byte arrays is not
	 * supported, i.e. neither Java 8 nor Apache Commons Codec is present at runtime
	 */
	public static byte[] encode(byte[] src) {
		assertDelegateAvailable();
		return delegate.encode(src);
	}

	/**
	 * Base64-decode the given byte array.
	 * <p>
	 *  Base64解码给定的字节数组
	 * 
	 * 
	 * @param src the encoded byte array (may be {@code null})
	 * @return the original byte array (or {@code null} if the input was {@code null})
	 * @throws IllegalStateException if Base64 encoding between byte arrays is not
	 * supported, i.e. neither Java 8 nor Apache Commons Codec is present at runtime
	 */
	public static byte[] decode(byte[] src) {
		assertDelegateAvailable();
		return delegate.decode(src);
	}

	/**
	 * Base64-encode the given byte array using the RFC 4648
	 * "URL and Filename Safe Alphabet".
	 * <p>
	 *  Base64使用RFC 4648"URL和文件名安全字母表"对给定字节数组进行编码
	 * 
	 * 
	 * @param src the original byte array (may be {@code null})
	 * @return the encoded byte array (or {@code null} if the input was {@code null})
	 * @throws IllegalStateException if Base64 encoding between byte arrays is not
	 * supported, i.e. neither Java 8 nor Apache Commons Codec is present at runtime
	 * @since 4.2.4
	 */
	public static byte[] encodeUrlSafe(byte[] src) {
		assertDelegateAvailable();
		return delegate.encodeUrlSafe(src);
	}

	/**
	 * Base64-decode the given byte array using the RFC 4648
	 * "URL and Filename Safe Alphabet".
	 * <p>
	 *  Base64使用RFC 4648"URL和Filename Safe Alphabet"解码给定的字节数组。
	 * 
	 * 
	 * @param src the encoded byte array (may be {@code null})
	 * @return the original byte array (or {@code null} if the input was {@code null})
	 * @throws IllegalStateException if Base64 encoding between byte arrays is not
	 * supported, i.e. neither Java 8 nor Apache Commons Codec is present at runtime
	 * @since 4.2.4
	 */
	public static byte[] decodeUrlSafe(byte[] src) {
		assertDelegateAvailable();
		return delegate.decodeUrlSafe(src);
	}

	/**
	 * Base64-encode the given byte array to a String.
	 * <p>
	 *  Base64将给定的字节数组编码为一个String
	 * 
	 * 
	 * @param src the original byte array (may be {@code null})
	 * @return the encoded byte array as a UTF-8 String
	 * (or {@code null} if the input was {@code null})
	 */
	public static String encodeToString(byte[] src) {
		if (src == null) {
			return null;
		}
		if (src.length == 0) {
			return "";
		}

		if (delegate != null) {
			// Full encoder available
			return new String(delegate.encode(src), DEFAULT_CHARSET);
		}
		else {
			// JAXB fallback for String case
			return DatatypeConverter.printBase64Binary(src);
		}
	}

	/**
	 * Base64-decode the given byte array from an UTF-8 String.
	 * <p>
	 *  Base64从UTF-8字符串解码给定的字节数组
	 * 
	 * 
	 * @param src the encoded UTF-8 String (may be {@code null})
	 * @return the original byte array (or {@code null} if the input was {@code null})
	 */
	public static byte[] decodeFromString(String src) {
		if (src == null) {
			return null;
		}
		if (src.length() == 0) {
			return new byte[0];
		}

		if (delegate != null) {
			// Full encoder available
			return delegate.decode(src.getBytes(DEFAULT_CHARSET));
		}
		else {
			// JAXB fallback for String case
			return DatatypeConverter.parseBase64Binary(src);
		}
	}

	/**
	 * Base64-encode the given byte array to a String using the RFC 4648
	 * "URL and Filename Safe Alphabet".
	 * <p>
	 *  Base64使用RFC 4648"URL和Filename Safe Alphabet"将给定字节数组编码为String
	 * 
	 * 
	 * @param src the original byte array (may be {@code null})
	 * @return the encoded byte array as a UTF-8 String
	 * (or {@code null} if the input was {@code null})
	 * @throws IllegalStateException if Base64 encoding between byte arrays is not
	 * supported, i.e. neither Java 8 nor Apache Commons Codec is present at runtime
	 */
	public static String encodeToUrlSafeString(byte[] src) {
		assertDelegateAvailable();
		return new String(delegate.encodeUrlSafe(src), DEFAULT_CHARSET);
	}

	/**
	 * Base64-decode the given byte array from an UTF-8 String using the RFC 4648
	 * "URL and Filename Safe Alphabet".
	 * <p>
	 *  Base64使用RFC 4648"URL和文件名安全字母表"从UTF-8字符串解码给定字节数组
	 * 
	 * @param src the encoded UTF-8 String (may be {@code null})
	 * @return the original byte array (or {@code null} if the input was {@code null})
	 * @throws IllegalStateException if Base64 encoding between byte arrays is not
	 * supported, i.e. neither Java 8 nor Apache Commons Codec is present at runtime
	 */
	public static byte[] decodeFromUrlSafeString(String src) {
		assertDelegateAvailable();
		return delegate.decodeUrlSafe(src.getBytes(DEFAULT_CHARSET));
	}


	interface Base64Delegate {

		byte[] encode(byte[] src);

		byte[] decode(byte[] src);

		byte[] encodeUrlSafe(byte[] src);

		byte[] decodeUrlSafe(byte[] src);
	}


	@UsesJava8
	static class JdkBase64Delegate implements Base64Delegate {

		@Override
		public byte[] encode(byte[] src) {
			if (src == null || src.length == 0) {
				return src;
			}
			return Base64.getEncoder().encode(src);
		}

		@Override
		public byte[] decode(byte[] src) {
			if (src == null || src.length == 0) {
				return src;
			}
			return Base64.getDecoder().decode(src);
		}

		@Override
		public byte[] encodeUrlSafe(byte[] src) {
			if (src == null || src.length == 0) {
				return src;
			}
			return Base64.getUrlEncoder().encode(src);
		}

		@Override
		public byte[] decodeUrlSafe(byte[] src) {
			if (src == null || src.length == 0) {
				return src;
			}
			return Base64.getUrlDecoder().decode(src);
		}

	}


	static class CommonsCodecBase64Delegate implements Base64Delegate {

		private final org.apache.commons.codec.binary.Base64 base64 =
				new org.apache.commons.codec.binary.Base64();

		private final org.apache.commons.codec.binary.Base64 base64UrlSafe =
				new org.apache.commons.codec.binary.Base64(0, null, true);

		@Override
		public byte[] encode(byte[] src) {
			return this.base64.encode(src);
		}

		@Override
		public byte[] decode(byte[] src) {
			return this.base64.decode(src);
		}

		@Override
		public byte[] encodeUrlSafe(byte[] src) {
			return this.base64UrlSafe.encode(src);
		}

		@Override
		public byte[] decodeUrlSafe(byte[] src) {
			return this.base64UrlSafe.decode(src);
		}

	}

}
