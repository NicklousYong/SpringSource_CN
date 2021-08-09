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

package org.springframework.util.xml;

import java.io.BufferedReader;
import java.io.CharConversionException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.springframework.util.StringUtils;

/**
 * Detects whether an XML stream is using DTD- or XSD-based validation.
 *
 * <p>
 *  检测XML流是否使用基于DTD或XSD的验证
 * 
 * 
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @since 2.0
 */
public class XmlValidationModeDetector {

	/**
	 * Indicates that the validation should be disabled.
	 * <p>
	 *  表示验证应该被禁用
	 * 
	 */
	public static final int VALIDATION_NONE = 0;

	/**
	 * Indicates that the validation mode should be auto-guessed, since we cannot find
	 * a clear indication (probably choked on some special characters, or the like).
	 * <p>
	 * 表示验证模式应该被自动猜测,因为我们找不到一个明确的指示(可能在某些特殊字符等上被扼制)
	 * 
	 */
	public static final int VALIDATION_AUTO = 1;

	/**
	 * Indicates that DTD validation should be used (we found a "DOCTYPE" declaration).
	 * <p>
	 *  表示应该使用DTD验证(我们发现了一个"DOCTYPE"声明)
	 * 
	 */
	public static final int VALIDATION_DTD = 2;

	/**
	 * Indicates that XSD validation should be used (found no "DOCTYPE" declaration).
	 * <p>
	 *  表示应使用XSD验证(找不到"DOCTYPE"声明)
	 * 
	 */
	public static final int VALIDATION_XSD = 3;


	/**
	 * The token in a XML document that declares the DTD to use for validation
	 * and thus that DTD validation is being used.
	 * <p>
	 *  XML文档中的令牌声明DTD用于验证,因此正在使用DTD验证
	 * 
	 */
	private static final String DOCTYPE = "DOCTYPE";

	/**
	 * The token that indicates the start of an XML comment.
	 * <p>
	 *  指示XML注释开始的令牌
	 * 
	 */
	private static final String START_COMMENT = "<!--";

	/**
	 * The token that indicates the end of an XML comment.
	 * <p>
	 *  表示XML注释结束的令牌
	 * 
	 */
	private static final String END_COMMENT = "-->";


	/**
	 * Indicates whether or not the current parse position is inside an XML comment.
	 * <p>
	 *  指示当前解析位置是否在XML注释内
	 * 
	 */
	private boolean inComment;


	/**
	 * Detect the validation mode for the XML document in the supplied {@link InputStream}.
	 * Note that the supplied {@link InputStream} is closed by this method before returning.
	 * <p>
	 *  在提供的{@link InputStream}中检测XML文档的验证模式请注意,提供的{@link InputStream}在返回之前由此方法关闭
	 * 
	 * 
	 * @param inputStream the InputStream to parse
	 * @throws IOException in case of I/O failure
	 * @see #VALIDATION_DTD
	 * @see #VALIDATION_XSD
	 */
	public int detectValidationMode(InputStream inputStream) throws IOException {
		// Peek into the file to look for DOCTYPE.
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		try {
			boolean isDtdValidated = false;
			String content;
			while ((content = reader.readLine()) != null) {
				content = consumeCommentTokens(content);
				if (this.inComment || !StringUtils.hasText(content)) {
					continue;
				}
				if (hasDoctype(content)) {
					isDtdValidated = true;
					break;
				}
				if (hasOpeningTag(content)) {
					// End of meaningful data...
					break;
				}
			}
			return (isDtdValidated ? VALIDATION_DTD : VALIDATION_XSD);
		}
		catch (CharConversionException ex) {
			// Choked on some character encoding...
			// Leave the decision up to the caller.
			return VALIDATION_AUTO;
		}
		finally {
			reader.close();
		}
	}


	/**
	 * Does the content contain the DTD DOCTYPE declaration?
	 * <p>
	 * 内容是否包含DTD DOCTYPE声明?
	 * 
	 */
	private boolean hasDoctype(String content) {
		return content.contains(DOCTYPE);
	}

	/**
	 * Does the supplied content contain an XML opening tag. If the parse state is currently
	 * in an XML comment then this method always returns false. It is expected that all comment
	 * tokens will have consumed for the supplied content before passing the remainder to this method.
	 * <p>
	 *  提供的内容是否包含XML打开标签如果解析状态当前处于XML注释中,则此方法总是返回false。预期所有注释令牌将在将余数传递给此方法之前为所提供的内容使用
	 * 
	 */
	private boolean hasOpeningTag(String content) {
		if (this.inComment) {
			return false;
		}
		int openTagIndex = content.indexOf('<');
		return (openTagIndex > -1 && (content.length() > openTagIndex + 1) &&
				Character.isLetter(content.charAt(openTagIndex + 1)));
	}

	/**
	 * Consumes all the leading comment data in the given String and returns the remaining content, which
	 * may be empty since the supplied content might be all comment data. For our purposes it is only important
	 * to strip leading comment content on a line since the first piece of non comment content will be either
	 * the DOCTYPE declaration or the root element of the document.
	 * <p>
	 *  消耗给定String中的所有主要注释数据,并返回剩余的内容,可能为空,因为所提供的内容可能是所有注释数据为了我们的目的,唯一重要的是剥离线上的主要注释内容,因为第一个非评论内容将是DOCTYPE声明或
	 * 文档的根元素。
	 * 
	 */
	private String consumeCommentTokens(String line) {
		if (!line.contains(START_COMMENT) && !line.contains(END_COMMENT)) {
			return line;
		}
		while ((line = consume(line)) != null) {
			if (!this.inComment && !line.trim().startsWith(START_COMMENT)) {
				return line;
			}
		}
		return line;
	}

	/**
	 * Consume the next comment token, update the "inComment" flag
	 * and return the remaining content.
	 * <p>
	 *  消耗下一个注释标记,更新"inComment"标志并返回剩余的内容
	 * 
	 */
	private String consume(String line) {
		int index = (this.inComment ? endComment(line) : startComment(line));
		return (index == -1 ? null : line.substring(index));
	}

	/**
	 * Try to consume the {@link #START_COMMENT} token.
	 * <p>
	 * 尝试使用{@link #START_COMMENT}令牌
	 * 
	 * 
	 * @see #commentToken(String, String, boolean)
	 */
	private int startComment(String line) {
		return commentToken(line, START_COMMENT, true);
	}

	private int endComment(String line) {
		return commentToken(line, END_COMMENT, false);
	}

	/**
	 * Try to consume the supplied token against the supplied content and update the
	 * in comment parse state to the supplied value. Returns the index into the content
	 * which is after the token or -1 if the token is not found.
	 * <p>
	 *  尝试根据提供的内容使用提供的令牌,并将注释解析状态更新为提供的值将索引返回到令牌后的内容,如果未找到令牌,则返回-1。
	 */
	private int commentToken(String line, String token, boolean inCommentIfPresent) {
		int index = line.indexOf(token);
		if (index > - 1) {
			this.inComment = inCommentIfPresent;
		}
		return (index == -1 ? index : index + token.length());
	}

}
