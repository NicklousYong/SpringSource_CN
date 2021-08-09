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

package org.springframework.scripting;

import java.io.IOException;

/**
 * Script definition interface, encapsulating the configuration
 * of a specific script as well as a factory method for
 * creating the actual scripted Java {@code Object}.
 *
 * <p>
 *  脚本定义界面,封装特定脚本的配置以及用于创建实际脚本化Java的工厂方法{@code Object}
 * 
 * 
 * @author Juergen Hoeller
 * @author Rob Harrop
 * @since 2.0
 * @see #getScriptSourceLocator
 * @see #getScriptedObject
 */
public interface ScriptFactory {

	/**
	 * Return a locator that points to the source of the script.
	 * Interpreted by the post-processor that actually creates the script.
	 * <p>Typical supported locators are Spring resource locations
	 * (such as "file:C:/myScript.bsh" or "classpath:myPackage/myScript.bsh")
	 * and inline scripts ("inline:myScriptText...").
	 * <p>
	 * 返回一个指向脚本源的定位器由实际创建脚本的后处理器解释<p>典型支持的定位器是Spring资源位置(例如"file：C：/ myScriptbsh"或"classpath：myPackage / myS
	 * criptbsh ")和内联脚本("inline：myScriptText")。
	 * 
	 * 
	 * @return the script source locator
	 * @see org.springframework.scripting.support.ScriptFactoryPostProcessor#convertToScriptSource
	 * @see org.springframework.core.io.ResourceLoader
	 */
	String getScriptSourceLocator();

	/**
	 * Return the business interfaces that the script is supposed to implement.
	 * <p>Can return {@code null} if the script itself determines
	 * its Java interfaces (such as in the case of Groovy).
	 * <p>
	 *  返回脚本应该实现的业务接口<p>如果脚本本身决定其Java接口(例如Groovy的情况),则可以返回{@code null}
	 * 
	 * 
	 * @return the interfaces for the script
	 */
	Class<?>[] getScriptInterfaces();

	/**
	 * Return whether the script requires a config interface to be
	 * generated for it. This is typically the case for scripts that
	 * do not determine Java signatures themselves, with no appropriate
	 * config interface specified in {@code getScriptInterfaces()}.
	 * <p>
	 *  返回脚本是否需要为其生成配置接口对于不自动确定Java签名的脚本,通常情况下,在{@code getScriptInterfaces())中未指定适当的配置接口
	 * 
	 * 
	 * @return whether the script requires a generated config interface
	 * @see #getScriptInterfaces()
	 */
	boolean requiresConfigInterface();

	/**
	 * Factory method for creating the scripted Java object.
	 * <p>Implementations are encouraged to cache script metadata such as
	 * a generated script class. Note that this method may be invoked
	 * concurrently and must be implemented in a thread-safe fashion.
	 * <p>
	 * 用于创建脚本化Java对象的工厂方法<p>鼓励实现缓存脚本元数据,例如生成的脚本类注意,此方法可以并发调用,并且必须以线程安全的方式实现
	 * 
	 * 
	 * @param scriptSource the actual ScriptSource to retrieve
	 * the script source text from (never {@code null})
	 * @param actualInterfaces the actual interfaces to expose,
	 * including script interfaces as well as a generated config interface
	 * (if applicable; may be {@code null})
	 * @return the scripted Java object
	 * @throws IOException if script retrieval failed
	 * @throws ScriptCompilationException if script compilation failed
	 */
	Object getScriptedObject(ScriptSource scriptSource, Class<?>... actualInterfaces)
			throws IOException, ScriptCompilationException;

	/**
	 * Determine the type of the scripted Java object.
	 * <p>Implementations are encouraged to cache script metadata such as
	 * a generated script class. Note that this method may be invoked
	 * concurrently and must be implemented in a thread-safe fashion.
	 * <p>
	 *  确定脚本化Java对象的类型<p>鼓励实现缓存脚本元数据(如生成的脚本类)注意,此方法可以并发调用,并且必须以线程安全的方式实现
	 * 
	 * 
	 * @param scriptSource the actual ScriptSource to retrieve
	 * the script source text from (never {@code null})
	 * @return the type of the scripted Java object, or {@code null}
	 * if none could be determined
	 * @throws IOException if script retrieval failed
	 * @throws ScriptCompilationException if script compilation failed
	 * @since 2.0.3
	 */
	Class<?> getScriptedObjectType(ScriptSource scriptSource)
			throws IOException, ScriptCompilationException;

	/**
	 * Determine whether a refresh is required (e.g. through
	 * ScriptSource's {@code isModified()} method).
	 * <p>
	 *  确定是否需要刷新(例如通过ScriptSource的{@code isModified()}方法)
	 * 
	 * @param scriptSource the actual ScriptSource to retrieve
	 * the script source text from (never {@code null})
	 * @return whether a fresh {@link #getScriptedObject} call is required
	 * @since 2.5.2
	 * @see ScriptSource#isModified()
	 */
	boolean requiresScriptedObjectRefresh(ScriptSource scriptSource);

}
