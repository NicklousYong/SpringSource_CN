/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2016 the original author or authors.
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

package org.springframework.web.servlet;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.ui.ModelMap;
import org.springframework.util.CollectionUtils;

/**
 * Holder for both Model and View in the web MVC framework.
 * Note that these are entirely distinct. This class merely holds
 * both to make it possible for a controller to return both model
 * and view in a single return value.
 *
 * <p>Represents a model and view returned by a handler, to be resolved
 * by a DispatcherServlet. The view can take the form of a String
 * view name which will need to be resolved by a ViewResolver object;
 * alternatively a View object can be specified directly. The model
 * is a Map, allowing the use of multiple objects keyed by name.
 *
 * <p>
 *  Web MVC框架中的模型和视图的持有者请注意,这些完全不同于此类只能使控制器能够以单个返回值返回模型和视图
 * 
 * <p>表示由处理程序返回的模型和视图,由DispatcherServlet解析视图可以采用需要由ViewResolver对象解析的String视图名称的形式;或者可以直接指定View对象模型是一个Map
 * ,允许使用由名称键入的多个对象。
 * 
 * 
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Rob Harrop
 * @author Rossen Stoyanchev
 * @see DispatcherServlet
 * @see ViewResolver
 * @see HandlerAdapter#handle
 * @see org.springframework.web.servlet.mvc.Controller#handleRequest
 */
public class ModelAndView {

	/** View instance or view name String */
	private Object view;

	/** Model Map */
	private ModelMap model;

	/** Optional HTTP status for the response */
	private HttpStatus status;

	/** Indicates whether or not this instance has been cleared with a call to {@link #clear()} */
	private boolean cleared = false;


	/**
	 * Default constructor for bean-style usage: populating bean
	 * properties instead of passing in constructor arguments.
	 * <p>
	 *  bean样式用法的默认构造函数：填充bean属性,而不是传递构造函数参数
	 * 
	 * 
	 * @see #setView(View)
	 * @see #setViewName(String)
	 */
	public ModelAndView() {
	}

	/**
	 * Convenient constructor when there is no model data to expose.
	 * Can also be used in conjunction with {@code addObject}.
	 * <p>
	 *  当没有模型数据暴露时方便的构造函数也可以与{@code addObject}结合使用
	 * 
	 * 
	 * @param viewName name of the View to render, to be resolved
	 * by the DispatcherServlet's ViewResolver
	 * @see #addObject
	 */
	public ModelAndView(String viewName) {
		this.view = viewName;
	}

	/**
	 * Convenient constructor when there is no model data to expose.
	 * Can also be used in conjunction with {@code addObject}.
	 * <p>
	 *  当没有模型数据暴露时方便的构造函数也可以与{@code addObject}结合使用
	 * 
	 * 
	 * @param view View object to render
	 * @see #addObject
	 */
	public ModelAndView(View view) {
		this.view = view;
	}

	/**
	 * Creates new ModelAndView given a view name and a model.
	 * <p>
	 *  给出一个视图名称和一个模型,创建新的ModelAndView
	 * 
	 * 
	 * @param viewName name of the View to render, to be resolved
	 * by the DispatcherServlet's ViewResolver
	 * @param model Map of model names (Strings) to model objects
	 * (Objects). Model entries may not be {@code null}, but the
	 * model Map may be {@code null} if there is no model data.
	 */
	public ModelAndView(String viewName, Map<String, ?> model) {
		this.view = viewName;
		if (model != null) {
			getModelMap().addAllAttributes(model);
		}
	}

	/**
	 * Creates new ModelAndView given a View object and a model.
	 * <emphasis>Note: the supplied model data is copied into the internal
	 * storage of this class. You should not consider to modify the supplied
	 * Map after supplying it to this class</emphasis>
	 * <p>
	 * 创建一个新的ModelAndView给出一个View对象和一个模型<emphasis>注意：提供的模型数据被复制到这个类的内部存储中你不应该考虑在提供给这个类之后修改提供的Map </emphasis>
	 * 。
	 * 
	 * 
	 * @param view View object to render
	 * @param model Map of model names (Strings) to model objects
	 * (Objects). Model entries may not be {@code null}, but the
	 * model Map may be {@code null} if there is no model data.
	 */
	public ModelAndView(View view, Map<String, ?> model) {
		this.view = view;
		if (model != null) {
			getModelMap().addAllAttributes(model);
		}
	}

	/**
	 * Creates new ModelAndView given a view name, model, and status.
	 * <p>
	 *  给出一个视图名称,模型和状态创建新的ModelAndView
	 * 
	 * 
	 * @param viewName name of the View to render, to be resolved
	 * by the DispatcherServlet's ViewResolver
	 * @param model Map of model names (Strings) to model objects
	 * (Objects). Model entries may not be {@code null}, but the
	 * model Map may be {@code null} if there is no model data.
	 * @param status an alternative status code to use for the response.
	 * @since 4.3
	 */
	public ModelAndView(String viewName, Map<String, ?> model, HttpStatus status) {
		this.view = viewName;
		if (model != null) {
			getModelMap().addAllAttributes(model);
		}
		this.status = status;
	}

	/**
	 * Convenient constructor to take a single model object.
	 * <p>
	 *  方便的构造函数来取一个模型对象
	 * 
	 * 
	 * @param viewName name of the View to render, to be resolved
	 * by the DispatcherServlet's ViewResolver
	 * @param modelName name of the single entry in the model
	 * @param modelObject the single model object
	 */
	public ModelAndView(String viewName, String modelName, Object modelObject) {
		this.view = viewName;
		addObject(modelName, modelObject);
	}

	/**
	 * Convenient constructor to take a single model object.
	 * <p>
	 *  方便的构造函数来取一个模型对象
	 * 
	 * 
	 * @param view View object to render
	 * @param modelName name of the single entry in the model
	 * @param modelObject the single model object
	 */
	public ModelAndView(View view, String modelName, Object modelObject) {
		this.view = view;
		addObject(modelName, modelObject);
	}


	/**
	 * Set a view name for this ModelAndView, to be resolved by the
	 * DispatcherServlet via a ViewResolver. Will override any
	 * pre-existing view name or View.
	 * <p>
	 *  设置此ModelAndView的视图名称,由DispatcherServlet通过ViewResolver解析将覆盖任何预先存在的视图名称或视图
	 * 
	 */
	public void setViewName(String viewName) {
		this.view = viewName;
	}

	/**
	 * Return the view name to be resolved by the DispatcherServlet
	 * via a ViewResolver, or {@code null} if we are using a View object.
	 * <p>
	 *  通过ViewResolver返回要由DispatcherServlet解析的视图名称,如果我们使用View对象,则返回{@code null}
	 * 
	 */
	public String getViewName() {
		return (this.view instanceof String ? (String) this.view : null);
	}

	/**
	 * Set a View object for this ModelAndView. Will override any
	 * pre-existing view name or View.
	 * <p>
	 *  为此ModelAndView设置View对象将覆盖任何预先存在的视图名称或View
	 * 
	 */
	public void setView(View view) {
		this.view = view;
	}

	/**
	 * Return the View object, or {@code null} if we are using a view name
	 * to be resolved by the DispatcherServlet via a ViewResolver.
	 * <p>
	 * 返回View对象,或{@code null},如果我们使用视图名称由DispatcherServlet通过ViewResolver解析
	 * 
	 */
	public View getView() {
		return (this.view instanceof View ? (View) this.view : null);
	}

	/**
	 * Indicate whether or not this {@code ModelAndView} has a view, either
	 * as a view name or as a direct {@link View} instance.
	 * <p>
	 *  指示此{@code ModelAndView}是否具有视图,作为视图名称或直接{@link View}实例
	 * 
	 */
	public boolean hasView() {
		return (this.view != null);
	}

	/**
	 * Return whether we use a view reference, i.e. {@code true}
	 * if the view has been specified via a name to be resolved by the
	 * DispatcherServlet via a ViewResolver.
	 * <p>
	 *  返回是否使用视图引用,即{@code true},如果视图已通过名称由DispatcherServlet通过ViewResolver解析
	 * 
	 */
	public boolean isReference() {
		return (this.view instanceof String);
	}

	/**
	 * Return the model map. May return {@code null}.
	 * Called by DispatcherServlet for evaluation of the model.
	 * <p>
	 *  返回模型图可以返回{@code null}调用DispatcherServlet进行模型的评估
	 * 
	 */
	protected Map<String, Object> getModelInternal() {
		return this.model;
	}

	/**
	 * Return the underlying {@code ModelMap} instance (never {@code null}).
	 * <p>
	 *  返回底层的{@code ModelMap}实例(从不{@code null})
	 * 
	 */
	public ModelMap getModelMap() {
		if (this.model == null) {
			this.model = new ModelMap();
		}
		return this.model;
	}

	/**
	 * Return the model map. Never returns {@code null}.
	 * To be called by application code for modifying the model.
	 * <p>
	 *  返回模型图从不返回{@code null}由用于修改模型的应用程序代码调用
	 * 
	 */
	public Map<String, Object> getModel() {
		return getModelMap();
	}

	/**
	 * Set the HTTP status to use for the response.
	 * <p>
	 *  设置用于响应的HTTP状态
	 * 
	 * 
	 * @since 4.3
	 */
	public void setStatus(HttpStatus status) {
		this.status = status;
	}

	/**
	 * Return the configured HTTP status for the response, if any.
	 * <p>
	 * 返回响应的配置HTTP状态(如果有)
	 * 
	 * 
	 * @since 4.3
	 */
	public HttpStatus getStatus() {
		return this.status;
	}


	/**
	 * Add an attribute to the model.
	 * <p>
	 *  向模型添加属性
	 * 
	 * 
	 * @param attributeName name of the object to add to the model
	 * @param attributeValue object to add to the model (never {@code null})
	 * @see ModelMap#addAttribute(String, Object)
	 * @see #getModelMap()
	 */
	public ModelAndView addObject(String attributeName, Object attributeValue) {
		getModelMap().addAttribute(attributeName, attributeValue);
		return this;
	}

	/**
	 * Add an attribute to the model using parameter name generation.
	 * <p>
	 *  使用参数名生成向模型添加属性
	 * 
	 * 
	 * @param attributeValue the object to add to the model (never {@code null})
	 * @see ModelMap#addAttribute(Object)
	 * @see #getModelMap()
	 */
	public ModelAndView addObject(Object attributeValue) {
		getModelMap().addAttribute(attributeValue);
		return this;
	}

	/**
	 * Add all attributes contained in the provided Map to the model.
	 * <p>
	 *  将提供的Map中包含的所有属性添加到模型中
	 * 
	 * 
	 * @param modelMap a Map of attributeName -> attributeValue pairs
	 * @see ModelMap#addAllAttributes(Map)
	 * @see #getModelMap()
	 */
	public ModelAndView addAllObjects(Map<String, ?> modelMap) {
		getModelMap().addAllAttributes(modelMap);
		return this;
	}


	/**
	 * Clear the state of this ModelAndView object.
	 * The object will be empty afterwards.
	 * <p>Can be used to suppress rendering of a given ModelAndView object
	 * in the {@code postHandle} method of a HandlerInterceptor.
	 * <p>
	 *  清除此ModelAndView对象的状态此后对象将为空<p>可用于禁止在HandlerInterceptor的{@code postHandle}方法中给定的ModelAndView对象的渲染
	 * 
	 * 
	 * @see #isEmpty()
	 * @see HandlerInterceptor#postHandle
	 */
	public void clear() {
		this.view = null;
		this.model = null;
		this.cleared = true;
	}

	/**
	 * Return whether this ModelAndView object is empty,
	 * i.e. whether it does not hold any view and does not contain a model.
	 * <p>
	 *  返回此ModelAndView对象是否为空,即它是否不包含任何视图,并且不包含模型
	 * 
	 */
	public boolean isEmpty() {
		return (this.view == null && CollectionUtils.isEmpty(this.model));
	}

	/**
	 * Return whether this ModelAndView object is empty as a result of a call to {@link #clear}
	 * i.e. whether it does not hold any view and does not contain a model.
	 * <p>Returns {@code false} if any additional state was added to the instance
	 * <strong>after</strong> the call to {@link #clear}.
	 * <p>
	 * 通过调用{@link #clear}来返回此ModelAndView对象是否为空,即它是否不包含任何视图,并且不包含模型<p>如果添加了任何其他状态,则返回{@code false}调用{@link #clear}
	 * 之后的<strong>实例</strong>。
	 * 
	 * 
	 * @see #clear()
	 */
	public boolean wasCleared() {
		return (this.cleared && isEmpty());
	}


	/**
	 * Return diagnostic information about this model and view.
	 * <p>
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("ModelAndView: ");
		if (isReference()) {
			sb.append("reference to view with name '").append(this.view).append("'");
		}
		else {
			sb.append("materialized View is [").append(this.view).append(']');
		}
		sb.append("; model is ").append(this.model);
		return sb.toString();
	}

}
