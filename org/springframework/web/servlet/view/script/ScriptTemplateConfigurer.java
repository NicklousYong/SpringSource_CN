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
 * An implementation of Spring MVC's {@link ScriptTemplateConfig} for creating
 * a {@code ScriptEngine} for use in a web application.
 *
 * <pre class="code">
 *
 * // Add the following to an &#64;Configuration class
 * &#64;Bean
 * public ScriptTemplateConfigurer mustacheConfigurer() {
 *    ScriptTemplateConfigurer configurer = new ScriptTemplateConfigurer();
 *    configurer.setEngineName("nashorn");
 *    configurer.setScripts("mustache.js");
 *    configurer.setRenderObject("Mustache");
 *    configurer.setRenderFunction("render");
 *    return configurer;
 * }
 * </pre>
 *
 * <p><b>NOTE:</b> It is possible to use non thread-safe script engines with
 * templating libraries not designed for concurrency, like Handlebars or React running on
 * Nashorn, by setting the {@link #setSharedEngine sharedEngine} property to {@code false}.
 *
 * <p>
 *  Spring MVC的{@link ScriptTemplateConfig}的一个实现,用于创建一个用于Web应用程序的{@code ScriptEngine}
 * 
 * <pre class="code">
 * 
 * //将以下内容添加到@Configuration类@Bean public Sc​​riptTemplateConfigurer mustacheConfigurer(){ScriptTemplateConfigurer configurer = new ScriptTemplateConfigurer(); configurersetEngineName( "犀牛"); configurersetScripts( "mustachejs"); configurersetRenderObject( "小胡子"); configurersetRenderFunction( "渲染"); return configurer; }
 * 。
 * </pre>
 * 
 *  <p> <b>注意：</b>通过设置{@link #setSharedEngine sharedEngine}属性,可以使用非线程安全的脚本引擎,模板库不是为并发设计的,例如在Nashorn上运行的H
 * andlebars或React到{@code false}。
 * 
 * 
 * @author Sebastien Deleuze
 * @since 4.2
 * @see ScriptTemplateView
 */
public class ScriptTemplateConfigurer implements ScriptTemplateConfig {

	private ScriptEngine engine;

	private String engineName;

	private Boolean sharedEngine;

	private String[] scripts;

	private String renderObject;

	private String renderFunction;

	private String contentType;

	private Charset charset;

	private String resourceLoaderPath;


	/**
	 * Set the {@link ScriptEngine} to use by the view.
	 * The script engine must implement {@code Invocable}.
	 * You must define {@code engine} or {@code engineName}, not both.
	 * <p>When the {@code sharedEngine} flag is set to {@code false}, you should not specify
	 * the script engine with this setter, but with the {@link #setEngineName(String)}
	 * one (since it implies multiple lazy instantiations of the script engine).
	 * <p>
	 * 设置视图使用的{@link ScriptEngine}脚本引擎必须实现{@code Invocable}您必须定义{@code engine}或{@code engineName},而不是同时<p>当{@code sharedEngine}
	 * 标志设置为{@code false},您不应该使用此设置器指定脚本引擎,而是使用{@link #setEngineName(String)}一个(因为它意味着脚本引擎的多个延迟实例化)。
	 * 
	 * 
	 * @see #setEngineName(String)
	 */
	public void setEngine(ScriptEngine engine) {
		this.engine = engine;
	}

	@Override
	public ScriptEngine getEngine() {
		return this.engine;
	}

	/**
	 * Set the engine name that will be used to instantiate the {@link ScriptEngine}.
	 * The script engine must implement {@code Invocable}.
	 * You must define {@code engine} or {@code engineName}, not both.
	 * <p>
	 *  设置将用于实例化{@link ScriptEngine}的引擎名称脚本引擎必须实现{@code Invocable}您必须定义{@code engine}或{@code engineName},而不是
	 * 两者。
	 * 
	 * 
	 * @see #setEngine(ScriptEngine)
	 */
	public void setEngineName(String engineName) {
		this.engineName = engineName;
	}

	@Override
	public String getEngineName() {
		return this.engineName;
	}

	/**
	 * When set to {@code false}, use thread-local {@link ScriptEngine} instances instead
	 * of one single shared instance. This flag should be set to {@code false} for those
	 * using non thread-safe script engines with templating libraries not designed for
	 * concurrency, like Handlebars or React running on Nashorn for example.
	 * In this case, Java 8u60 or greater is required due to
	 * <a href="https://bugs.openjdk.java.net/browse/JDK-8076099">this bug</a>.
	 * <p>When this flag is set to {@code false}, the script engine must be specified using
	 * {@link #setEngineName(String)}. Using {@link #setEngine(ScriptEngine)} is not
	 * possible because multiple instances of the script engine need to be created lazily
	 * (one per thread).
	 * <p>
	 * 当设置为{@code false}时,使用线程本地{@link ScriptEngine}实例而不是一个单一共享实例对于那些使用非线程安全脚本引擎的标准应设置为{@code false}的模板库未设计用
	 * 于并发,例如在Nashorn上运行的Handlebars或React例如在这种情况下,由于<a href=\"https://bugsopenjdkjavanet/browse/JDK-8076099\">
	 * 此错误</a> <p需要Java 8u60或更高版本>当此标志设置为{@code false}时,脚本引擎必须使用{@link #setEngineName(String)}指定}使用{@link #setEngine(ScriptEngine)}
	 * 是不可能的,因为脚本引擎的多个实例需要被懒惰地创建(每个线程一个)。
	 * 
	 * 
	 * @see <a href="http://docs.oracle.com/javase/8/docs/api/javax/script/ScriptEngineFactory.html#getParameter-java.lang.String-">THREADING ScriptEngine parameter<a/>
	 */
	public void setSharedEngine(Boolean sharedEngine) {
		this.sharedEngine = sharedEngine;
	}

	@Override
	public Boolean isSharedEngine() {
		return this.sharedEngine;
	}

	/**
	 * Set the scripts to be loaded by the script engine (library or user provided).
	 * Since {@code resourceLoaderPath} default value is "classpath:", you can load easily
	 * any script available on the classpath.
	 * <p>For example, in order to use a JavaScript library available as a WebJars dependency
	 * and a custom "render.js" file, you should call
	 * {@code configurer.setScripts("/META-INF/resources/webjars/library/version/library.js",
	 * "com/myproject/script/render.js");}.
	 * <p>
	 * 设置要由脚本引擎(提供库或用户)加载的脚本由于{@code resourceLoaderPath}默认值为"classpath：",您可以轻松加载类路径中可用的任何脚本<p>例如,为了使用一个JavaS
	 * cript库可以作为WebJars依赖项和一个自定义的"renderjs"文件,你应该调用{@code configurersetScripts("/ META-INF / resources / webjars / library / version / libraryjs","com / myproject / script / renderjs" );}
	 * 。
	 * 
	 * 
	 * @see #setResourceLoaderPath
	 * @see <a href="http://www.webjars.org">WebJars</a>
	 */
	public void setScripts(String... scriptNames) {
		this.scripts = scriptNames;
	}

	@Override
	public String[] getScripts() {
		return this.scripts;
	}

	/**
	 * Set the object where the render function belongs (optional).
	 * For example, in order to call {@code Mustache.render()}, {@code renderObject}
	 * should be set to {@code "Mustache"} and {@code renderFunction} to {@code "render"}.
	 * <p>
	 *  设置渲染功能所属的对象(可选)例如,为了调用{@code Mustacherender()},{@code renderObject}应设置为{@code"Mustache"}和{@code renderFunction}
	 * 到{ @code"render"}。
	 * 
	 */
	public void setRenderObject(String renderObject) {
		this.renderObject = renderObject;
	}

	@Override
	public String getRenderObject() {
		return this.renderObject;
	}

	/**
	 * Set the render function name (mandatory).
	 *
	 * <p>This function will be called with the following parameters:
	 * <ol>
	 * <li>{@code String template}: the template content</li>
	 * <li>{@code Map model}: the view model</li>
	 * <li>{@code String url}: the template url (since 4.2.2)</li>
	 * </ol>
	 * <p>
	 *  设置渲染函数名称(必需)
	 * 
	 *  <p>使用以下参数调用此函数：
	 * <ol>
	 * <li> {@ code String template}：模板内容</li> <li> {@ code Map model}：视图模型</li> <li> {@ code String url}：模板
	 * 网址)</LI>。
	 * </ol>
	 */
	public void setRenderFunction(String renderFunction) {
		this.renderFunction = renderFunction;
	}

	@Override
	public String getRenderFunction() {
		return this.renderFunction;
	}

	/**
	 * Set the content type to use for the response.
	 * ({@code text/html} by default).
	 * <p>
	 *  设置用于响应的内容类型(默认为{@code text / html})
	 * 
	 * 
	 * @since 4.2.1
	 */
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	/**
	 * Return the content type to use for the response.
	 * <p>
	 *  返回要用于响应的内容类型
	 * 
	 * 
	 * @since 4.2.1
	 */
	@Override
	public String getContentType() {
		return this.contentType;
	}

	/**
	 * Set the charset used to read script and template files.
	 * ({@code UTF-8} by default).
	 * <p>
	 *  设置用于读取脚本和模板文件的字符集(默认为{@code UTF-8})
	 * 
	 */
	public void setCharset(Charset charset) {
		this.charset = charset;
	}

	@Override
	public Charset getCharset() {
		return this.charset;
	}

	/**
	 * Set the resource loader path(s) via a Spring resource location.
	 * Accepts multiple locations as a comma-separated list of paths.
	 * Standard URLs like "file:" and "classpath:" and pseudo URLs are supported
	 * as understood by Spring's {@link org.springframework.core.io.ResourceLoader}.
	 * Relative paths are allowed when running in an ApplicationContext.
	 * <p>Default is "classpath:".
	 * <p>
	 *  通过Spring资源位置设置资源加载程序路径接受多个位置作为逗号分隔的路径标准URL(如"file："和"classpath：")和伪URL支持,Spring的{@link orgspringframeworkcoreioResourceLoader}
	 * 在ApplicationContext中运行时允许使用相对路径。
	 */
	public void setResourceLoaderPath(String resourceLoaderPath) {
		this.resourceLoaderPath = resourceLoaderPath;
	}

	@Override
	public String getResourceLoaderPath() {
		return this.resourceLoaderPath;
	}

}
