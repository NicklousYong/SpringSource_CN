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

package org.springframework.web.method.support;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.support.BindingAwareModelMap;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.bind.support.SimpleSessionStatus;

/**
 * Records model and view related decisions made by
 * {@link HandlerMethodArgumentResolver}s and
 * {@link HandlerMethodReturnValueHandler}s during the course of invocation of
 * a controller method.
 *
 * <p>The {@link #setRequestHandled} flag can be used to indicate the request
 * has been handled directly and view resolution is not required.
 *
 * <p>A default {@link Model} is automatically created at instantiation.
 * An alternate model instance may be provided via {@link #setRedirectModel}
 * for use in a redirect scenario. When {@link #setRedirectModelScenario} is set
 * to {@code true} signalling a redirect scenario, the {@link #getModel()}
 * returns the redirect model instead of the default model.
 *
 * <p>
 *  在调用控制器方法的过程中记录模型并查看{@link HandlerMethodArgumentResolver}和{@link HandlerMethodReturnValueHandler}所做的相
 * 关决策。
 * 
 * <p> {@link #setRequestHandled}标志可用于指示请求已被直接处理,并且不需要查看分辨率
 * 
 *  <p>在实例化时自动创建默认的{@link模型}可以通过{@link #setRedirectModel}提供备用模型实例,以在重定向方案中使用{@link #setRedirectModelScenario}
 * 设置为{@code true}发送重定向方案,{@link #getModel()}返回重定向模型而不是默认模型。
 * 
 * 
 * @author Rossen Stoyanchev
 * @since 3.1
 */
public class ModelAndViewContainer {

	private boolean ignoreDefaultModelOnRedirect = false;

	private Object view;

	private final ModelMap defaultModel = new BindingAwareModelMap();

	private ModelMap redirectModel;

	private boolean redirectModelScenario = false;

	/* Names of attributes with binding disabled */
	private final Set<String> bindingDisabledAttributes = new HashSet<String>(4);

	private HttpStatus status;

	private final SessionStatus sessionStatus = new SimpleSessionStatus();

	private boolean requestHandled = false;


	/**
	 * By default the content of the "default" model is used both during
	 * rendering and redirect scenarios. Alternatively controller methods
	 * can declare an argument of type {@code RedirectAttributes} and use
	 * it to provide attributes to prepare the redirect URL.
	 * <p>Setting this flag to {@code true} guarantees the "default" model is
	 * never used in a redirect scenario even if a RedirectAttributes argument
	 * is not declared. Setting it to {@code false} means the "default" model
	 * may be used in a redirect if the controller method doesn't declare a
	 * RedirectAttributes argument.
	 * <p>The default setting is {@code false}.
	 * <p>
	 * 默认情况下,在渲染和重定向情况下都使用"默认"模型的内容。
	 * 或者,控制器方法可以声明类型为{@code RedirectAttributes}的参数,并使用该参数提供属性来准备重定向URL <p>将此标志设置为{@code true}保证在重定向方案中从不使用"
	 * 默认"模型,即使没有声明RedirectAttributes参数将其设置为{@code false},意味着如果控制器方法可能会在重定向中使用"默认"模式不声明RedirectAttributes参数<p>
	 * 默认设置为{@code false}。
	 * 默认情况下,在渲染和重定向情况下都使用"默认"模型的内容。
	 * 
	 */
	public void setIgnoreDefaultModelOnRedirect(boolean ignoreDefaultModelOnRedirect) {
		this.ignoreDefaultModelOnRedirect = ignoreDefaultModelOnRedirect;
	}

	/**
	 * Set a view name to be resolved by the DispatcherServlet via a ViewResolver.
	 * Will override any pre-existing view name or View.
	 * <p>
	 *  设置要由DispatcherServlet通过ViewResolver解析的视图名称将覆盖任何预先存在的视图名称或视图
	 * 
	 */
	public void setViewName(String viewName) {
		this.view = viewName;
	}

	/**
	 * Return the view name to be resolved by the DispatcherServlet via a
	 * ViewResolver, or {@code null} if a View object is set.
	 * <p>
	 * 通过ViewResolver返回要由DispatcherServlet解析的视图名称,如果设置了View对象,则返回{@code null}
	 * 
	 */
	public String getViewName() {
		return (this.view instanceof String ? (String) this.view : null);
	}

	/**
	 * Set a View object to be used by the DispatcherServlet.
	 * Will override any pre-existing view name or View.
	 * <p>
	 *  设置DispatcherServlet要使用的View对象将覆盖任何预先存在的视图名称或View
	 * 
	 */
	public void setView(Object view) {
		this.view = view;
	}

	/**
	 * Return the View object, or {@code null} if we using a view name
	 * to be resolved by the DispatcherServlet via a ViewResolver.
	 * <p>
	 *  如果我们使用视图名称由DispatcherServlet通过ViewResolver解析,则返回View对象,或{@code null}
	 * 
	 */
	public Object getView() {
		return this.view;
	}

	/**
	 * Whether the view is a view reference specified via a name to be
	 * resolved by the DispatcherServlet via a ViewResolver.
	 * <p>
	 *  视图是否是通过由DispatcherServlet通过ViewResolver解析的名称指定的视图引用
	 * 
	 */
	public boolean isViewReference() {
		return (this.view instanceof String);
	}

	/**
	 * Return the model to use -- either the "default" or the "redirect" model.
	 * The default model is used if {@code redirectModelScenario=false} or
	 * there is no redirect model (i.e. RedirectAttributes was not declared as
	 * a method argument) and {@code ignoreDefaultModelOnRedirect=false}.
	 * <p>
	 *  返回要使用的模型 - "默认"或"重定向"模型如果{@code redirectModelScenario = false}或没有重定向模型(即RedirectAttributes未声明为方法参数),
	 * 则使用默认模型和{ @code ignoreDefaultModelOnRedirect = false}。
	 * 
	 */
	public ModelMap getModel() {
		if (useDefaultModel()) {
			return this.defaultModel;
		}
		else {
			if (this.redirectModel == null) {
				this.redirectModel = new ModelMap();
			}
			return this.redirectModel;
		}
	}

	/**
	 * Register an attribute for which data binding should not occur, for example
	 * corresponding to an {@code @ModelAttribute(binding=false)} declaration.
	 * <p>
	 * 注册不应发生数据绑定的属性,例如对应于{@code @ModelAttribute(binding = false)}声明
	 * 
	 * 
	 * @param attributeName the name of the attribute
	 * @since 4.3
	 */
	public void setBindingDisabled(String attributeName) {
		this.bindingDisabledAttributes.add(attributeName);
	}

	/**
	 * Whether binding is disabled for the given model attribute.
	 * <p>
	 *  绑定是否被禁用给定的模型属性
	 * 
	 * 
	 * @since 4.3
	 */
	public boolean isBindingDisabled(String name) {
		return this.bindingDisabledAttributes.contains(name);
	}

	/**
	 * Whether to use the default model or the redirect model.
	 * <p>
	 *  是否使用默认模型或重定向模型
	 * 
	 */
	private boolean useDefaultModel() {
		return (!this.redirectModelScenario || (this.redirectModel == null && !this.ignoreDefaultModelOnRedirect));
	}

	/**
	 * Return the "default" model created at instantiation.
	 * <p>In general it is recommended to use {@link #getModel()} instead which
	 * returns either the "default" model (template rendering) or the "redirect"
	 * model (redirect URL preparation). Use of this method may be needed for
	 * advanced cases when access to the "default" model is needed regardless,
	 * e.g. to save model attributes specified via {@code @SessionAttributes}.
	 * <p>
	 *  返回实例化时创建的"默认"模型<p>通常建议使用{@link #getModel()},返回"默认"模型(模板呈现)或"重定向"模型(重定向URL准备)当需要访问"默认"模型时,高级情况下可能需要使用
	 * 此方法,无论如何保存通过{@code @SessionAttributes}指定的模型属性,。
	 * 
	 * 
	 * @return the default model (never {@code null})
	 * @since 4.1.4
	 */
	public ModelMap getDefaultModel() {
		return this.defaultModel;
	}

	/**
	 * Provide a separate model instance to use in a redirect scenario.
	 * The provided additional model however is not used unless
	 * {@link #setRedirectModelScenario(boolean)} gets set to {@code true} to signal
	 * a redirect scenario.
	 * <p>
	 * 提供在重定向方案中使用的单独的模型实例除非{@link #setRedirectModelScenario(boolean)}设置为{@code true}以指示重定向方案,否则不会使用提供的附加模型。
	 * 
	 */
	public void setRedirectModel(ModelMap redirectModel) {
		this.redirectModel = redirectModel;
	}

	/**
	 * Whether the controller has returned a redirect instruction, e.g. a
	 * "redirect:" prefixed view name, a RedirectView instance, etc.
	 * <p>
	 *  控制器是否返回重定向指令,例如"redirect："前缀视图名称,RedirectView实例等
	 * 
	 */
	public void setRedirectModelScenario(boolean redirectModelScenario) {
		this.redirectModelScenario = redirectModelScenario;
	}

	/**
	 * Return the {@link SessionStatus} instance to use that can be used to
	 * signal that session processing is complete.
	 * <p>
	 *  返回{@link SessionStatus}实例以用于指示会话处理完成
	 * 
	 */
	public SessionStatus getSessionStatus() {
		return this.sessionStatus;
	}

	/**
	 * Provide a HTTP status that will be passed on to with the
	 * {@code ModelAndView} used for view rendering purposes.
	 * <p>
	 *  提供将用于视图呈现目的的{@code ModelAndView}传递给的HTTP状态
	 * 
	 * 
	 * @since 4.3
	 */
	public void setStatus(HttpStatus status) {
		this.status = status;
	}

	/**
	 * Return the configured HTTP status, if any.
	 * <p>
	 *  返回配置的HTTP状态(如果有)
	 * 
	 * 
	 * @since 4.3
	 */
	public HttpStatus getStatus() {
		return this.status;
	}

	/**
	 * Whether the request has been handled fully within the handler, e.g.
	 * {@code @ResponseBody} method, and therefore view resolution is not
	 * necessary. This flag can also be set when controller methods declare an
	 * argument of type {@code ServletResponse} or {@code OutputStream}).
	 * <p>The default value is {@code false}.
	 * <p>
	 * 请求是否在处理程序内完全处理,例如{@code @ResponseBody}方法,因此不需要视图分辨率当控制器方法声明类型为{@code ServletResponse}或{@code的参数时,也可以设置此标志OutputStream}
	 * )<p>默认值为{@code false}。
	 * 
	 */
	public void setRequestHandled(boolean requestHandled) {
		this.requestHandled = requestHandled;
	}

	/**
	 * Whether the request has been handled fully within the handler.
	 * <p>
	 *  请求是否已在处理程序中完全处理
	 * 
	 */
	public boolean isRequestHandled() {
		return this.requestHandled;
	}

	/**
	 * Add the supplied attribute to the underlying model.
	 * A shortcut for {@code getModel().addAttribute(String, Object)}.
	 * <p>
	 *  将提供的属性添加到底层模型{@code getModel()addAttribute(String,Object)}的快捷方式
	 * 
	 */
	public ModelAndViewContainer addAttribute(String name, Object value) {
		getModel().addAttribute(name, value);
		return this;
	}

	/**
	 * Add the supplied attribute to the underlying model.
	 * A shortcut for {@code getModel().addAttribute(Object)}.
	 * <p>
	 *  将提供的属性添加到底层模型{@code getModel()addAttribute(Object)}的快捷方式
	 * 
	 */
	public ModelAndViewContainer addAttribute(Object value) {
		getModel().addAttribute(value);
		return this;
	}

	/**
	 * Copy all attributes to the underlying model.
	 * A shortcut for {@code getModel().addAllAttributes(Map)}.
	 * <p>
	 *  将所有属性复制到底层模型{@code getModel()的快捷方式addAllAttributes(Map)}
	 * 
	 */
	public ModelAndViewContainer addAllAttributes(Map<String, ?> attributes) {
		getModel().addAllAttributes(attributes);
		return this;
	}

	/**
	 * Copy attributes in the supplied {@code Map} with existing objects of
	 * the same name taking precedence (i.e. not getting replaced).
	 * A shortcut for {@code getModel().mergeAttributes(Map<String, ?>)}.
	 * <p>
	 * 将提供的{@code Map}中的属性复制到具有相同名称的现有对象的优先级(即不被替换)。{@code getModel()mergeAttributes(Map <String,?>)}的快捷方式
	 * 
	 */
	public ModelAndViewContainer mergeAttributes(Map<String, ?> attributes) {
		getModel().mergeAttributes(attributes);
		return this;
	}

	/**
	 * Remove the given attributes from the model.
	 * <p>
	 *  从模型中删除给定的属性
	 * 
	 */
	public ModelAndViewContainer removeAttributes(Map<String, ?> attributes) {
		if (attributes != null) {
			for (String key : attributes.keySet()) {
				getModel().remove(key);
			}
		}
		return this;
	}

	/**
	 * Whether the underlying model contains the given attribute name.
	 * A shortcut for {@code getModel().containsAttribute(String)}.
	 * <p>
	 *  底层模型是否包含给定的属性名称{@code getModel()containsAttribute(String)}的快捷方式
	 * 
	 */
	public boolean containsAttribute(String name) {
		return getModel().containsAttribute(name);
	}


	/**
	 * Return diagnostic information.
	 * <p>
	 *  返回诊断信息
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("ModelAndViewContainer: ");
		if (!isRequestHandled()) {
			if (isViewReference()) {
				sb.append("reference to view with name '").append(this.view).append("'");
			}
			else {
				sb.append("View is [").append(this.view).append(']');
			}
			if (useDefaultModel()) {
				sb.append("; default model ");
			}
			else {
				sb.append("; redirect model ");
			}
			sb.append(getModel());
		}
		else {
			sb.append("Request handled directly");
		}
		return sb.toString();
	}

}
