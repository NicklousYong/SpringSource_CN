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
 * This exception wraps (as cause) a checked exception thrown by some method that SpEL
 * invokes. It differs from a SpelEvaluationException because this indicates the
 * occurrence of a checked exception that the invoked method was defined to throw.
 * SpelEvaluationExceptions are for handling (and wrapping) unexpected exceptions.
 *
 * <p>
 *  除非适用法律要求或以书面形式同意,根据许可证分发的软件以"按原样"分发,不附带任何明示或暗示的担保或条件,请参阅许可证中有关特定语言的权限和限制许可证
 * 
 * 
 * @author Andy Clement
 * @since 3.0.3
 */
@SuppressWarnings("serial")
public class ExpressionInvocationTargetException extends EvaluationException {

	public ExpressionInvocationTargetException(int position, String message, Throwable cause) {
		super(position, message, cause);
	}

	public ExpressionInvocationTargetException(int position, String message) {
		super(position, message);
	}

	public ExpressionInvocationTargetException(String expressionString, String message) {
		super(expressionString, message);
	}

	public ExpressionInvocationTargetException(String message, Throwable cause) {
		super(message, cause);
	}

	public ExpressionInvocationTargetException(String message) {
		super(message);
	}

}
