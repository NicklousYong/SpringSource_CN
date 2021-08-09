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

package org.springframework.web.servlet.view.script;

import java.nio.charset.Charset;
import javax.script.ScriptEngine;

/**
 * Interface to be implemented by objects that configure and manage a
 * JSR-223 {@link ScriptEngine} for automatic lookup in a web environment.
 * Detected and used by {@link ScriptTemplateView}.
 *
 * <p>
 *  用于配置和管理JSR-223的对象实现的接口{@link ScriptEngine}用于Web环境中的自动查找由{@link ScriptTemplateView}检测并使用
 * 
 * 
 * @author Sebastien Deleuze
 * @since 4.2
 */
public interface ScriptTemplateConfig {

	/**
	 * Return the {@link ScriptEngine} to use by the views.
	 * <p>
	 * 返回{@link ScriptEngine}以供视图使用
	 * 
	 */
	ScriptEngine getEngine();

	/**
	 * Return the engine name that will be used to instantiate the {@link ScriptEngine}.
	 * <p>
	 *  返回将用于实例化{@link ScriptEngine}的引擎名称
	 * 
	 */
	String getEngineName();

	/**
	 * Return whether to use a shared engine for all threads or whether to create
	 * thread-local engine instances for each thread.
	 * <p>
	 *  返回是否为所有线程使用共享引擎,还是为每个线程创建线程本地引擎实例
	 * 
	 */
	Boolean isSharedEngine();

	/**
	 * Return the scripts to be loaded by the script engine (library or user provided).
	 * <p>
	 *  返回要由脚本引擎加载的脚本(提供的库或用户)
	 * 
	 */
	String[] getScripts();

	/**
	 * Return the object where the render function belongs (optional).
	 * <p>
	 *  返回渲染功能所属的对象(可选)
	 * 
	 */
	String getRenderObject();

	/**
	 * Return the render function name (mandatory).
	 * <p>
	 *  返回渲染函数名称(必填)
	 * 
	 */
	String getRenderFunction();

	/**
	 * Return the content type to use for the response.
	 * <p>
	 *  返回要用于响应的内容类型
	 * 
	 * 
	 * @since 4.2.1
	 */
	String getContentType();

	/**
	 * Return the charset used to read script and template files.
	 * <p>
	 *  返回用于读取脚本和模板文件的字符集
	 * 
	 */
	Charset getCharset();

	/**
	 * Return the resource loader path(s) via a Spring resource location.
	 * <p>
	 *  通过Spring资源位置返回资源加载程序路径
	 */
	String getResourceLoaderPath();

}
