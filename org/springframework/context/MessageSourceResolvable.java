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

package org.springframework.context;

/**
 * Interface for objects that are suitable for message resolution in a
 * {@link MessageSource}.
 *
 * <p>Spring's own validation error classes implement this interface.
 *
 * <p>
 *  适用于{@link MessageSource}中消息解析的对象的界面
 * 
 *  <p> Spring自己的验证错误类实现了这个接口
 * 
 * 
 * @author Juergen Hoeller
 * @see MessageSource#getMessage(MessageSourceResolvable, java.util.Locale)
 * @see org.springframework.validation.ObjectError
 * @see org.springframework.validation.FieldError
 */
public interface MessageSourceResolvable {

	/**
	 * Return the codes to be used to resolve this message, in the order that
	 * they should get tried. The last code will therefore be the default one.
	 * <p>
	 * 返回用于解决此消息的代码,按照他们应该尝试的顺序最后一个代码将是默认的
	 * 
	 * 
	 * @return a String array of codes which are associated with this message
	 */
	String[] getCodes();

	/**
	 * Return the array of arguments to be used to resolve this message.
	 * <p>
	 *  返回要用于解析此消息的参数数组
	 * 
	 * 
	 * @return an array of objects to be used as parameters to replace
	 * placeholders within the message text
	 * @see java.text.MessageFormat
	 */
	Object[] getArguments();

	/**
	 * Return the default message to be used to resolve this message.
	 * <p>
	 *  返回要用于解决此消息的默认消息
	 * 
	 * @return the default message, or {@code null} if no default
	 */
	String getDefaultMessage();

}
