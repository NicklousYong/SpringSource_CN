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

package org.springframework.jndi;

import javax.naming.Context;
import javax.naming.NamingException;

/**
 * Callback interface to be implemented by classes that need to perform an
 * operation (such as a lookup) in a JNDI context. This callback approach
 * is valuable in simplifying error handling, which is performed by the
 * JndiTemplate class. This is a similar to JdbcTemplate's approach.
 *
 * <p>Note that there is hardly any need to implement this callback
 * interface, as JndiTemplate provides all usual JNDI operations via
 * convenience methods.
 *
 * <p>
 * 
 * @author Rod Johnson
 * @see JndiTemplate
 * @see org.springframework.jdbc.core.JdbcTemplate
 */
public interface JndiCallback<T> {

	/**
	 * Do something with the given JNDI context.
	 * <p>Implementations don't need to worry about error handling
	 * or cleanup, as the JndiTemplate class will handle this.
	 * <p>
	 * 回调接口由需要在JNDI上下文中执行操作(例如查找)的类实现该回调方法在简化由JndiTemplate类执行的错误处理方面是有价值的。这与JdbcTemplate的方法类似
	 * 
	 *  注意,几乎没有必要实现这个回调接口,因为JndiTemplate通过方便的方法提供了所有通常的JNDI操作
	 * 
	 * 
	 * @param ctx the current JNDI context
	 * @throws NamingException if thrown by JNDI methods
	 * @return a result object, or {@code null}
	 */
	T doInContext(Context ctx) throws NamingException;

}

