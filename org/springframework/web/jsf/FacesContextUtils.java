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

package org.springframework.web.jsf;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.springframework.util.Assert;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.util.WebUtils;

/**
 * Convenience methods to retrieve Spring's root {@link WebApplicationContext}
 * for a given JSF {@link FacesContext}. This is useful for accessing a
 * Spring application context from custom JSF-based code.
 *
 * <p>Analogous to Spring's WebApplicationContextUtils for the ServletContext.
 *
 * <p>
 *  为给定的JSF {@link FacesContext}检索Spring的根{@link WebApplicationContext}的便利方法这对从基于JSF的代码访问Spring应用程序上下文很有
 * 用。
 * 
 * <p>类似于Spring的WebApplicationContextUtils for ServletContext
 * 
 * 
 * @author Juergen Hoeller
 * @since 1.1
 * @see org.springframework.web.context.ContextLoader
 * @see org.springframework.web.context.support.WebApplicationContextUtils
 */
public abstract class FacesContextUtils {

	/**
	 * Find the root {@link WebApplicationContext} for this web app, typically
	 * loaded via {@link org.springframework.web.context.ContextLoaderListener}.
	 * <p>Will rethrow an exception that happened on root context startup,
	 * to differentiate between a failed context startup and no context at all.
	 * <p>
	 *  找到此网络应用程序的根{@link WebApplicationContext},通常通过{@link orgspringframeworkwebcontextContextLoaderListener}
	 * 加载<p>将重新启动根上下文启动时发生的异常,以区分失败的上下文启动和无上下文。
	 * 
	 * 
	 * @param fc the FacesContext to find the web application context for
	 * @return the root WebApplicationContext for this web app, or {@code null} if none
	 * @see org.springframework.web.context.WebApplicationContext#ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE
	 */
	public static WebApplicationContext getWebApplicationContext(FacesContext fc) {
		Assert.notNull(fc, "FacesContext must not be null");
		Object attr = fc.getExternalContext().getApplicationMap().get(
				WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
		if (attr == null) {
			return null;
		}
		if (attr instanceof RuntimeException) {
			throw (RuntimeException) attr;
		}
		if (attr instanceof Error) {
			throw (Error) attr;
		}
		if (!(attr instanceof WebApplicationContext)) {
			throw new IllegalStateException("Root context attribute is not of type WebApplicationContext: " + attr);
		}
		return (WebApplicationContext) attr;
	}

	/**
	 * Find the root {@link WebApplicationContext} for this web app, typically
	 * loaded via {@link org.springframework.web.context.ContextLoaderListener}.
	 * <p>Will rethrow an exception that happened on root context startup,
	 * to differentiate between a failed context startup and no context at all.
	 * <p>
	 *  找到此网络应用程序的根{@link WebApplicationContext},通常通过{@link orgspringframeworkwebcontextContextLoaderListener}
	 * 加载<p>将重新启动根上下文启动时发生的异常,以区分失败的上下文启动和无上下文。
	 * 
	 * 
	 * @param fc the FacesContext to find the web application context for
	 * @return the root WebApplicationContext for this web app
	 * @throws IllegalStateException if the root WebApplicationContext could not be found
	 * @see org.springframework.web.context.WebApplicationContext#ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE
	 */
	public static WebApplicationContext getRequiredWebApplicationContext(FacesContext fc) throws IllegalStateException {
		WebApplicationContext wac = getWebApplicationContext(fc);
		if (wac == null) {
			throw new IllegalStateException("No WebApplicationContext found: no ContextLoaderListener registered?");
		}
		return wac;
	}

	/**
	 * Return the best available mutex for the given session:
	 * that is, an object to synchronize on for the given session.
	 * <p>Returns the session mutex attribute if available; usually,
	 * this means that the HttpSessionMutexListener needs to be defined
	 * in {@code web.xml}. Falls back to the Session reference itself
	 * if no mutex attribute found.
	 * <p>The session mutex is guaranteed to be the same object during
	 * the entire lifetime of the session, available under the key defined
	 * by the {@code SESSION_MUTEX_ATTRIBUTE} constant. It serves as a
	 * safe reference to synchronize on for locking on the current session.
	 * <p>In many cases, the Session reference itself is a safe mutex
	 * as well, since it will always be the same object reference for the
	 * same active logical session. However, this is not guaranteed across
	 * different servlet containers; the only 100% safe way is a session mutex.
	 * <p>
	 * 返回给定会话的最佳可用互斥体：即,给定会话同步的对象<p>返回会话互斥体属性(如果可用);通常,这意味着HttpSessionMutexListener需要在{@code webxml}中定义,如果没有
	 * 发现mutex属性,则返回到Session引用本身<p>会话互斥体在会话的整个生命周期中保证是相同的对象,在{@code SESSION_MUTEX_ATTRIBUTE}常量定义的关键字下可用的键作为对
	 * 当前会话锁定进行同步的安全引用<p>在许多情况下,会话引用本身也是一个安全的互斥体,因为它总是作为相同活动逻辑会话的相同对象引用但是,不能保证跨不同的servlet容器;唯一的100％安全方式是会话互斥
	 * 
	 * @param fc the FacesContext to find the session mutex for
	 * @return the mutex object (never {@code null})
	 * @see org.springframework.web.util.WebUtils#SESSION_MUTEX_ATTRIBUTE
	 * @see org.springframework.web.util.HttpSessionMutexListener
	 */
	public static Object getSessionMutex(FacesContext fc) {
		Assert.notNull(fc, "FacesContext must not be null");
		ExternalContext ec = fc.getExternalContext();
		Object mutex = ec.getSessionMap().get(WebUtils.SESSION_MUTEX_ATTRIBUTE);
		if (mutex == null) {
			mutex = ec.getSession(true);
		}
		return mutex;
	}

}
