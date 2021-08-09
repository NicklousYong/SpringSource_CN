/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2010 the original author or authors.
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

package org.springframework.web.servlet.view.velocity;

import java.lang.reflect.Method;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.context.Context;
import org.apache.velocity.tools.view.ToolboxManager;
import org.apache.velocity.tools.view.context.ChainedContext;
import org.apache.velocity.tools.view.servlet.ServletToolboxManager;

import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

/**
 * {@link VelocityView} subclass which adds support for Velocity Tools toolboxes
 * and Velocity Tools ViewTool callbacks / Velocity Tools 1.3 init methods.
 *
 * <p>Specify a "toolboxConfigLocation", for example "/WEB-INF/toolbox.xml",
 * to automatically load a Velocity Tools toolbox definition file and expose
 * all defined tools in the specified scopes. If no config location is
 * specified, no toolbox will be loaded and exposed.
 *
 * <p>This view will always create a special Velocity context, namely an
 * instance of the ChainedContext class which is part of the view package
 * of Velocity tools. This allows to use tools from the view package of
 * Velocity Tools, like LinkTool, which need to be initialized with a special
 * context that implements the ViewContext interface (i.e. a ChainedContext).
 *
 * <p>This view also checks tools that are specified as "toolAttributes":
 * If they implement the ViewTool interface, they will get initialized with
 * the Velocity context. This allows tools from the view package of Velocity
 * Tools, such as LinkTool, to be defined as
 * {@link #setToolAttributes "toolAttributes"} on a VelocityToolboxView,
 * instead of in a separate toolbox XML file.
 *
 * <p>This is a separate class mainly to avoid a required dependency on
 * the view package of Velocity Tools in {@link VelocityView} itself.
 * As of Spring 3.0, this class requires Velocity Tools 1.3 or higher.
 *
 * <p>
 *  {@link VelocityView}子类,支持Velocity Tools工具箱和Velocity Tools ViewTool回调/ Velocity Tools 13 init方法
 * 
 * <p>指定一个"toolboxConfigLocation",例如"/ WEB-INF / toolboxxml",以自动加载Velocity Tools工具箱定义文件,并在指定的范围内公开所有定义的工
 * 具如果没有指定配置位置,则不会加载工具箱并暴露。
 * 
 *  <p>此视图将始终创建一个特殊的Velocity上下文,即ChainedContext类的一个实例,它是Velocity工具的视图包的一部分。
 * 这允许使用Velocity Tools视图包中的工具,如LinkTool,需要使用实现ViewContext接口的特殊上下文(即ChainedContext)进行初始化。
 * 
 * <p>此视图还检查指定为"toolAttributes"的工具：如果它们实现ViewTool接口,它们将使用Velocity上下文进行初始化这允许将Velocity Tools视图包(例如LinkToo
 * l)中的工具定义为VelocityToolboxView上的{@link #setToolAttributes"toolAttributes"},而不是单独的工具箱XML文件。
 * 
 *  <p>这是一个单独的类,主要是为了避免对{@link VelocityView}本身的Velocity Tools的视图包的必需依赖。
 * 从Spring 30开始,此类需要Velocity Tools 13或更高版本。
 * 
 * 
 * @author Juergen Hoeller
 * @since 1.1.3
 * @see #setToolboxConfigLocation
 * @see #initTool
 * @see org.apache.velocity.tools.view.context.ViewContext
 * @see org.apache.velocity.tools.view.context.ChainedContext
 * @deprecated as of Spring 4.3, in favor of FreeMarker
 */
@Deprecated
public class VelocityToolboxView extends VelocityView {

	private String toolboxConfigLocation;


	/**
	 * Set a Velocity Toolbox config location, for example "/WEB-INF/toolbox.xml",
	 * to automatically load a Velocity Tools toolbox definition file and expose
	 * all defined tools in the specified scopes. If no config location is
	 * specified, no toolbox will be loaded and exposed.
	 * <p>The specified location string needs to refer to a ServletContext
	 * resource, as expected by ServletToolboxManager which is part of
	 * the view package of Velocity Tools.
	 * <p>
	 * 
	 * @see org.apache.velocity.tools.view.servlet.ServletToolboxManager#getInstance
	 */
	public void setToolboxConfigLocation(String toolboxConfigLocation) {
		this.toolboxConfigLocation = toolboxConfigLocation;
	}

	/**
	 * Return the Velocity Toolbox config location, if any.
	 * <p>
	 * 设置速度工具箱配置位置,例如"/ WEB-INF / toolboxxml",以自动加载Velocity Tools工具箱定义文件,并在指定的范围内公开所有定义的工具如果未指定配置位置,则不会加载和暴露
	 * 工具箱<p>指定的位置字符串需要引用ServletContext资源,如ServletToolboxManager所预期的那样,它是Velocity Tools视图包的一部分。
	 * 
	 */
	protected String getToolboxConfigLocation() {
		return this.toolboxConfigLocation;
	}


	/**
	 * Overridden to create a ChainedContext, which is part of the view package
	 * of Velocity Tools, as special context. ChainedContext is needed for
	 * initialization of ViewTool instances.
	 * <p>
	 *  返回Velocity Toolbox配置位置,如果有的话
	 * 
	 * 
	 * @see #initTool
	 */
	@Override
	protected Context createVelocityContext(
			Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {

		// Create a ChainedContext instance.
		ChainedContext velocityContext = new ChainedContext(
				new VelocityContext(model), getVelocityEngine(), request, response, getServletContext());

		// Load a Velocity Tools toolbox, if necessary.
		if (getToolboxConfigLocation() != null) {
			ToolboxManager toolboxManager = ServletToolboxManager.getInstance(
					getServletContext(), getToolboxConfigLocation());
			Map<?, ?> toolboxContext = toolboxManager.getToolbox(velocityContext);
			velocityContext.setToolbox(toolboxContext);
		}

		return velocityContext;
	}

	/**
	 * Overridden to check for the ViewContext interface which is part of the
	 * view package of Velocity Tools. This requires a special Velocity context,
	 * like ChainedContext as set up by {@link #createVelocityContext} in this class.
	 * <p>
	 *  为了创建一个ChainedContext,它是Velocity Tools的视图包的一部分,因为ViewTool实例的初始化需要特殊的上下文ChainedContext
	 * 
	 */
	@Override
	protected void initTool(Object tool, Context velocityContext) throws Exception {
		// Velocity Tools 1.3: a class-level "init(Object)" method.
		Method initMethod = ClassUtils.getMethodIfAvailable(tool.getClass(), "init", Object.class);
		if (initMethod != null) {
			ReflectionUtils.invokeMethod(initMethod, tool, velocityContext);
		}
	}

}
