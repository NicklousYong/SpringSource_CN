/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2012 the original author or authors.
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

package org.springframework.core;

/**
 * Interface that can be implemented by exceptions etc that are error coded.
 * The error code is a String, rather than a number, so it can be given
 * user-readable values, such as "object.failureDescription".
 *
 * <p>An error code can be resolved by a MessageSource, for example.
 *
 * <p>
 * 
 * @author Rod Johnson
 * @see org.springframework.context.MessageSource
 */
public interface ErrorCoded {

	/**
	 * Return the error code associated with this failure.
	 * The GUI can render this any way it pleases, allowing for localization etc.
	 * <p>
	 *  可以通过异常等实现错误代码的接口错误代码是一个String而不是一个数字,因此可以给出用户可读的值,如"objectfailureDescription"
	 * 
	 * <p>例如,MessageSource可以解决错误代码
	 * 
	 * 
	 * @return a String error code associated with this failure,
	 * or {@code null} if not error-coded
	 */
	String getErrorCode();

}
