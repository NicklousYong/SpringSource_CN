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

package org.springframework.web.servlet;

import javax.servlet.ServletException;

import org.springframework.util.Assert;

/**
 * Exception to be thrown on error conditions that should forward
 * to a specific view with a specific model.
 *
 * <p>Can be thrown at any time during handler processing.
 * This includes any template methods of pre-built controllers.
 * For example, a form controller might abort to a specific error page
 * if certain parameters do not allow to proceed with the normal workflow.
 *
 * <p>
 *  在特定模型的转发到特定视图的错误条件下抛出异常
 * 
 * 在处理程序处理过程中可以随时抛出这个问题包括预先构建的控制器的任何模板方法例如,如果某些参数不允许继续正常的工作流程,则表单控制器可能会中止到特定的错误页面
 * 
 * 
 * @author Juergen Hoeller
 * @since 22.11.2003
 */
@SuppressWarnings("serial")
public class ModelAndViewDefiningException extends ServletException {

	private ModelAndView modelAndView;


	/**
	 * Create new ModelAndViewDefiningException with the given ModelAndView,
	 * typically representing a specific error page.
	 * <p>
	 *  使用给定的ModelAndView创建新的ModelAndViewDefiningException,通常表示特定的错误页面
	 * 
	 * 
	 * @param modelAndView ModelAndView with view to forward to and model to expose
	 */
	public ModelAndViewDefiningException(ModelAndView modelAndView) {
		Assert.notNull(modelAndView, "ModelAndView must not be null in ModelAndViewDefiningException");
		this.modelAndView = modelAndView;
	}

	/**
	 * Return the ModelAndView that this exception contains for forwarding to.
	 * <p>
	 *  返回此异常包含的用于转发的ModelAndView
	 */
	public ModelAndView getModelAndView() {
		return modelAndView;
	}

}
