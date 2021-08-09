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

import java.util.Hashtable;
import java.util.Properties;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.util.CollectionUtils;

/**
 * Helper class that simplifies JNDI operations. It provides methods to lookup and
 * bind objects, and allows implementations of the {@link JndiCallback} interface
 * to perform any operation they like with a JNDI naming context provided.
 *
 * <p>
 * 简化JNDI操作的助手类提供查找和绑定对象的方法,并允许{@link JndiCallback}接口的实现使用JNDI命名上下文来执行任何他们喜欢的操作
 * 
 * 
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @see JndiCallback
 * @see #execute
 */
public class JndiTemplate {

	protected final Log logger = LogFactory.getLog(getClass());

	private Properties environment;


	/**
	 * Create a new JndiTemplate instance.
	 * <p>
	 *  创建一个新的JndiTemplate实例
	 * 
	 */
	public JndiTemplate() {
	}

	/**
	 * Create a new JndiTemplate instance, using the given environment.
	 * <p>
	 *  使用给定的环境创建一个新的JndiTemplate实例
	 * 
	 */
	public JndiTemplate(Properties environment) {
		this.environment = environment;
	}


	/**
	 * Set the environment for the JNDI InitialContext.
	 * <p>
	 *  设置JNDI InitialContext的环境
	 * 
	 */
	public void setEnvironment(Properties environment) {
		this.environment = environment;
	}

	/**
	 * Return the environment for the JNDI InitialContext, if any.
	 * <p>
	 *  返回JNDI InitialContext的环境(如果有)
	 * 
	 */
	public Properties getEnvironment() {
		return this.environment;
	}


	/**
	 * Execute the given JNDI context callback implementation.
	 * <p>
	 *  执行给定的JNDI上下文回调实现
	 * 
	 * 
	 * @param contextCallback JndiCallback implementation
	 * @return a result object returned by the callback, or {@code null}
	 * @throws NamingException thrown by the callback implementation
	 * @see #createInitialContext
	 */
	public <T> T execute(JndiCallback<T> contextCallback) throws NamingException {
		Context ctx = getContext();
		try {
			return contextCallback.doInContext(ctx);
		}
		finally {
			releaseContext(ctx);
		}
	}

	/**
	 * Obtain a JNDI context corresponding to this template's configuration.
	 * Called by {@link #execute}; may also be called directly.
	 * <p>The default implementation delegates to {@link #createInitialContext()}.
	 * <p>
	 *  获取与此模板配置相对应的JNDI上下文{@link #execute};也可以直接调用<p>默认实现委托给{@link #createInitialContext()}
	 * 
	 * 
	 * @return the JNDI context (never {@code null})
	 * @throws NamingException if context retrieval failed
	 * @see #releaseContext
	 */
	public Context getContext() throws NamingException {
		return createInitialContext();
	}

	/**
	 * Release a JNDI context as obtained from {@link #getContext()}.
	 * <p>
	 *  从{@link #getContext()}获取的JNDI上下文释放
	 * 
	 * 
	 * @param ctx the JNDI context to release (may be {@code null})
	 * @see #getContext
	 */
	public void releaseContext(Context ctx) {
		if (ctx != null) {
			try {
				ctx.close();
			}
			catch (NamingException ex) {
				logger.debug("Could not close JNDI InitialContext", ex);
			}
		}
	}

	/**
	 * Create a new JNDI initial context. Invoked by {@link #getContext}.
	 * <p>The default implementation use this template's environment settings.
	 * Can be subclassed for custom contexts, e.g. for testing.
	 * <p>
	 * 创建一个新的JNDI初始上下文{@link #getContext}调用<p>默认实现使用此模板的环境设置可以为自定义上下文进行子类化,例如用于测试
	 * 
	 * 
	 * @return the initial Context instance
	 * @throws NamingException in case of initialization errors
	 */
	protected Context createInitialContext() throws NamingException {
		Hashtable<?, ?> icEnv = null;
		Properties env = getEnvironment();
		if (env != null) {
			icEnv = new Hashtable<Object, Object>(env.size());
			CollectionUtils.mergePropertiesIntoMap(env, icEnv);
		}
		return new InitialContext(icEnv);
	}


	/**
	 * Look up the object with the given name in the current JNDI context.
	 * <p>
	 *  在当前JNDI上下文中查找具有给定名称的对象
	 * 
	 * 
	 * @param name the JNDI name of the object
	 * @return object found (cannot be {@code null}; if a not so well-behaved
	 * JNDI implementations returns null, a NamingException gets thrown)
	 * @throws NamingException if there is no object with the given
	 * name bound to JNDI
	 */
	public Object lookup(final String name) throws NamingException {
		if (logger.isDebugEnabled()) {
			logger.debug("Looking up JNDI object with name [" + name + "]");
		}
		return execute(new JndiCallback<Object>() {
			@Override
			public Object doInContext(Context ctx) throws NamingException {
				Object located = ctx.lookup(name);
				if (located == null) {
					throw new NameNotFoundException(
							"JNDI object with [" + name + "] not found: JNDI implementation returned null");
				}
				return located;
			}
		});
	}

	/**
	 * Look up the object with the given name in the current JNDI context.
	 * <p>
	 *  在当前JNDI上下文中查找具有给定名称的对象
	 * 
	 * 
	 * @param name the JNDI name of the object
	 * @param requiredType type the JNDI object must match. Can be an interface or
	 * superclass of the actual class, or {@code null} for any match. For example,
	 * if the value is {@code Object.class}, this method will succeed whatever
	 * the class of the returned instance.
	 * @return object found (cannot be {@code null}; if a not so well-behaved
	 * JNDI implementations returns null, a NamingException gets thrown)
	 * @throws NamingException if there is no object with the given
	 * name bound to JNDI
	 */
	@SuppressWarnings("unchecked")
	public <T> T lookup(String name, Class<T> requiredType) throws NamingException {
		Object jndiObject = lookup(name);
		if (requiredType != null && !requiredType.isInstance(jndiObject)) {
			throw new TypeMismatchNamingException(
					name, requiredType, (jndiObject != null ? jndiObject.getClass() : null));
		}
		return (T) jndiObject;
	}

	/**
	 * Bind the given object to the current JNDI context, using the given name.
	 * <p>
	 *  使用给定的名称将给定的对象绑定到当前的JNDI上下文
	 * 
	 * 
	 * @param name the JNDI name of the object
	 * @param object the object to bind
	 * @throws NamingException thrown by JNDI, mostly name already bound
	 */
	public void bind(final String name, final Object object) throws NamingException {
		if (logger.isDebugEnabled()) {
			logger.debug("Binding JNDI object with name [" + name + "]");
		}
		execute(new JndiCallback<Object>() {
			@Override
			public Object doInContext(Context ctx) throws NamingException {
				ctx.bind(name, object);
				return null;
			}
		});
	}

	/**
	 * Rebind the given object to the current JNDI context, using the given name.
	 * Overwrites any existing binding.
	 * <p>
	 *  将给定对象重新绑定到当前的JNDI上下文,使用给定的名称覆盖任何现有的绑定
	 * 
	 * 
	 * @param name the JNDI name of the object
	 * @param object the object to rebind
	 * @throws NamingException thrown by JNDI
	 */
	public void rebind(final String name, final Object object) throws NamingException {
		if (logger.isDebugEnabled()) {
			logger.debug("Rebinding JNDI object with name [" + name + "]");
		}
		execute(new JndiCallback<Object>() {
			@Override
			public Object doInContext(Context ctx) throws NamingException {
				ctx.rebind(name, object);
				return null;
			}
		});
	}

	/**
	 * Remove the binding for the given name from the current JNDI context.
	 * <p>
	 *  从当前JNDI上下文中删除给定名称的绑定
	 * 
	 * @param name the JNDI name of the object
	 * @throws NamingException thrown by JNDI, mostly name not found
	 */
	public void unbind(final String name) throws NamingException {
		if (logger.isDebugEnabled()) {
			logger.debug("Unbinding JNDI object with name [" + name + "]");
		}
		execute(new JndiCallback<Object>() {
			@Override
			public Object doInContext(Context ctx) throws NamingException {
				ctx.unbind(name);
				return null;
			}
		});
	}

}
