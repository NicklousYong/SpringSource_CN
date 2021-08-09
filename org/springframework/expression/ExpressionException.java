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

package org.springframework.expression;

/**
 * Super class for exceptions that can occur whilst processing expressions.
 *
 * <p>
 *  处理表达式时可能发生的异常的超类
 * 
 * 
 * @author Andy Clement
 * @since 3.0
 */
@SuppressWarnings("serial")
public class ExpressionException extends RuntimeException {

	protected String expressionString;

	protected int position;  // -1 if not known - but should be known in all reasonable cases


	/**
	 * Construct a new expression exception.
	 * <p>
	 *  构造新的表达式异常
	 * 
	 * 
	 * @param expressionString the expression string
	 * @param message a descriptive message
	 */
	public ExpressionException(String expressionString, String message) {
		super(message);
		this.position = -1;
		this.expressionString = expressionString;
	}

	/**
	 * Construct a new expression exception.
	 * <p>
	 *  构造新的表达式异常
	 * 
	 * 
	 * @param expressionString the expression string
	 * @param position the position in the expression string where the problem occurred
	 * @param message a descriptive message
	 */
	public ExpressionException(String expressionString, int position, String message) {
		super(message);
		this.position = position;
		this.expressionString = expressionString;
	}

	/**
	 * Construct a new expression exception.
	 * <p>
	 *  构造新的表达式异常
	 * 
	 * 
	 * @param position the position in the expression string where the problem occurred
	 * @param message a descriptive message
	 */
	public ExpressionException(int position, String message) {
		super(message);
		this.position = position;
	}

	/**
	 * Construct a new expression exception.
	 * <p>
	 * 构造一个新的表达式异常
	 * 
	 * 
	 * @param position the position in the expression string where the problem occurred
	 * @param message a descriptive message
	 * @param cause the underlying cause of this exception
	 */
	public ExpressionException(int position, String message, Throwable cause) {
		super(message,cause);
		this.position = position;
	}

	/**
	 * Construct a new expression exception.
	 * <p>
	 *  构造一个新的表达式异常
	 * 
	 * 
	 * @param message a descriptive message
	 */
	public ExpressionException(String message) {
		super(message);
	}

	/**
	 * Construct a new expression exception.
	 * <p>
	 *  构造新的表达式异常
	 * 
	 * 
	 * @param message a descriptive message
	 * @param cause the underlying cause of this exception
	 */
	public ExpressionException(String message, Throwable cause) {
		super(message,cause);
	}


	/**
	 * Return the expression string.
	 * <p>
	 *  返回表达式字符串
	 * 
	 */
	public final String getExpressionString() {
		return this.expressionString;
	}

	/**
	 * Return the position in the expression string where the problem occurred.
	 * <p>
	 *  返回发生问题的表达式字符串中的位置
	 * 
	 */
	public final int getPosition() {
		return this.position;
	}

	/**
	 * Return the exception message. Since Spring 4.0 this method returns the
	 * same result as {@link #toDetailedString()}.
	 * <p>
	 *  返回异常消息由于Spring 40此方法返回与{@link #toDetailedString())相同的结果
	 * 
	 * 
	 * @see java.lang.Throwable#getMessage()
	 */
	@Override
	public String getMessage() {
		return toDetailedString();
	}

	/**
	 * Return a detailed description of this exception, including the expression
	 * String and position (if available) as well as the actual exception message.
	 * <p>
	 *  返回此异常的详细说明,包括字符串和位置(如果可用)以及实际的异常消息
	 * 
	 */
	public String toDetailedString() {
		if (this.expressionString != null) {
			StringBuilder output = new StringBuilder();
			output.append("Expression '");
			output.append(this.expressionString);
			output.append("'");
			if (this.position != -1) {
				output.append(" @ ");
				output.append(this.position);
			}
			output.append(": ");
			output.append(getSimpleMessage());
			return output.toString();
		}
		else {
			return getSimpleMessage();
		}
	}

	/**
	 * Return the exception simple message without including the expression
	 * that caused the failure.
	 * <p>
	 *  返回异常简单消息,而不包括导致失败的表达式
	 */
	public String getSimpleMessage() {
		return super.getMessage();
	}

}
