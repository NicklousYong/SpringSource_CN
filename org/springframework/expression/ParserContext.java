/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2013 the original author or authors.
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

package org.springframework.expression;

/**
 * Input provided to an expression parser that can influence an expression
 * parsing/compilation routine.
 *
 * <p>
 *  提供给可以影响表达式解析/编译例程的表达式解析器的输入
 * 
 * 
 * @author Keith Donald
 * @author Andy Clement
 * @since 3.0
 */
public interface ParserContext {

	/**
	 * Whether or not the expression being parsed is a template. A template expression
	 * consists of literal text that can be mixed with evaluatable blocks. Some examples:
	 * <pre class="code">
	 * 	   Some literal text
	 *     Hello #{name.firstName}!
	 *     #{3 + 4}
	 * </pre>
	 * <p>
	 * 解析的表达式是否为模板模板表达式由可与可评估块混合的文本文本组成一些示例：
	 * <pre class="code">
	 *  一些字面文字Hello#{namefirstName}！ #{3 + 4}
	 * </pre>
	 * 
	 * @return true if the expression is a template, false otherwise
	 */
	boolean isTemplate();

	/**
	 * For template expressions, returns the prefix that identifies the start of an
	 * expression block within a string. For example: "${"
	 * <p>
	 *  对于模板表达式,返回标识字符串中表达式块开头的前缀例如："$ {"
	 * 
	 * 
	 * @return the prefix that identifies the start of an expression
	 */
	String getExpressionPrefix();

	/**
	 * For template expressions, return the prefix that identifies the end of an
	 * expression block within a string. For example: "}"
	 * <p>
	 *  对于模板表达式,返回标识字符串中表达式块结尾的前缀例如："}"
	 * 
	 * 
	 * @return the suffix that identifies the end of an expression
	 */
	String getExpressionSuffix();


	/**
	 * The default ParserContext implementation that enables template expression parsing
	 * mode. The expression prefix is #{ and the expression suffix is }.
	 * <p>
	 *  启用模板表达式解析模式的默认ParserContext实现表达式前缀为#{表达式后缀为}
	 * 
	 * @see #isTemplate()
	 */
	public static final ParserContext TEMPLATE_EXPRESSION = new ParserContext() {

		@Override
		public String getExpressionPrefix() {
			return "#{";
		}

		@Override
		public String getExpressionSuffix() {
			return "}";
		}

		@Override
		public boolean isTemplate() {
			return true;
		}

	};

}
