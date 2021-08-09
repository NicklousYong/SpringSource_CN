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

package org.springframework.web.jsf;

import javax.faces.application.NavigationHandler;
import javax.faces.context.FacesContext;

/**
 * Base class for JSF NavigationHandler implementations that want
 * to be capable of decorating an original NavigationHandler.
 *
 * <p>Supports the standard JSF style of decoration (through a constructor argument)
 * as well as an overloaded {@code handleNavigation} method with explicit
 * NavigationHandler argument (passing in the original NavigationHandler). Subclasses
 * are forced to implement this overloaded {@code handleNavigation} method.
 * Standard JSF invocations will automatically delegate to the overloaded method,
 * with the constructor-injected NavigationHandler as argument.
 *
 * <p>
 *  希望能够装饰原始NavigationHandler的JSF NavigationHandler实现的基类
 * 
 * <p>支持标准的JSF风格的装饰(通过构造函数参数)以及重载的{@code handleNavigation}方法,具有显式的NavigationHandler参数(传入原始NavigationHand
 * ler)子类被强制实现此重载的{@code handleNavigation}方法标准JSF调用将自动委托给重载方法,其中构造函数注入的NavigationHandler作为参数。
 * 
 * 
 * @author Juergen Hoeller
 * @since 1.2.7
 * @see #handleNavigation(javax.faces.context.FacesContext, String, String, NavigationHandler)
 * @see DelegatingNavigationHandlerProxy
 */
public abstract class DecoratingNavigationHandler extends NavigationHandler {

	private NavigationHandler decoratedNavigationHandler;


	/**
	 * Create a DecoratingNavigationHandler without fixed original NavigationHandler.
	 * <p>
	 *  创建一个DecoratedNavigationHandler没有固定的原始NavigationHandler
	 * 
	 */
	protected DecoratingNavigationHandler() {
	}

	/**
	 * Create a DecoratingNavigationHandler with fixed original NavigationHandler.
	 * <p>
	 *  用固定的原始NavigationHandler创建一个DecoratingNavigationHandler
	 * 
	 * 
	 * @param originalNavigationHandler the original NavigationHandler to decorate
	 */
	protected DecoratingNavigationHandler(NavigationHandler originalNavigationHandler) {
		this.decoratedNavigationHandler = originalNavigationHandler;
	}

	/**
	 * Return the fixed original NavigationHandler decorated by this handler, if any
	 * (that is, if passed in through the constructor).
	 * <p>
	 *  返回由此处理程序装饰的固定原始NavigationHandler(如果有)(即,如果通过构造函数传入)
	 * 
	 */
	public final NavigationHandler getDecoratedNavigationHandler() {
		return this.decoratedNavigationHandler;
	}


	/**
	 * This implementation of the standard JSF {@code handleNavigation} method
	 * delegates to the overloaded variant, passing in constructor-injected
	 * NavigationHandler as argument.
	 * <p>
	 * 标准JSF {@code handleNavigation}方法的这种实现委托给重载变量,将构造函数注入的NavigationHandler作为参数传递
	 * 
	 * 
	 * @see #handleNavigation(javax.faces.context.FacesContext, String, String, javax.faces.application.NavigationHandler)
	 */
	@Override
	public final void handleNavigation(FacesContext facesContext, String fromAction, String outcome) {
		handleNavigation(facesContext, fromAction, outcome, this.decoratedNavigationHandler);
	}

	/**
	 * Special {@code handleNavigation} variant with explicit NavigationHandler
	 * argument. Either called directly, by code with an explicit original handler,
	 * or called from the standard {@code handleNavigation} method, as
	 * plain JSF-defined NavigationHandler.
	 * <p>Implementations should invoke {@code callNextHandlerInChain} to
	 * delegate to the next handler in the chain. This will always call the most
	 * appropriate next handler (see {@code callNextHandlerInChain} javadoc).
	 * Alternatively, the decorated NavigationHandler or the passed-in original
	 * NavigationHandler can also be called directly; however, this is not as
	 * flexible in terms of reacting to potential positions in the chain.
	 * <p>
	 * 直接使用明确的原始处理程序直接调用或从标准{@code handleNavigation}方法调用的特殊{@code handleNavigation}变体,可以作为普通的JSF定义的Navigatio
	 * nHandler <p>实现应该调用{@code callNextHandlerInChain}将委托给链中的下一个处理程序这将始终调用最合适的下一个处理程序(参见{@code callNextHandlerInChain}
	 *  javadoc)或者,还可以直接调用装饰的NavigationHandler或传入的原始NavigationHandler;然而,在对链中的潜在位置的反应方面,这并不灵活。
	 * 
	 * 
	 * @param facesContext the current JSF context
	 * @param fromAction the action binding expression that was evaluated to retrieve the
	 * specified outcome, or {@code null} if the outcome was acquired by some other means
	 * @param outcome the logical outcome returned by a previous invoked application action
	 * (which may be {@code null})
	 * @param originalNavigationHandler the original NavigationHandler,
	 * or {@code null} if none
	 * @see #callNextHandlerInChain
	 */
	public abstract void handleNavigation(
			FacesContext facesContext, String fromAction, String outcome, NavigationHandler originalNavigationHandler);


	/**
	 * Method to be called by subclasses when intending to delegate to the next
	 * handler in the NavigationHandler chain. Will always call the most
	 * appropriate next handler, either the decorated NavigationHandler passed
	 * in as constructor argument or the original NavigationHandler as passed
	 * into this method - according to the position of this instance in the chain.
	 * <p>Will call the decorated NavigationHandler specified as constructor
	 * argument, if any. In case of a DecoratingNavigationHandler as target, the
	 * original NavigationHandler as passed into this method will be passed on to
	 * the next element in the chain: This ensures propagation of the original
	 * handler that the last element in the handler chain might delegate back to.
	 * In case of a standard NavigationHandler as target, the original handler
	 * will simply not get passed on; no delegating back to the original is
	 * possible further down the chain in that scenario.
	 * <p>If no decorated NavigationHandler specified as constructor argument,
	 * this instance is the last element in the chain. Hence, this method will
	 * call the original NavigationHandler as passed into this method. If no
	 * original NavigationHandler has been passed in (for example if this
	 * instance is the last element in a chain with standard NavigationHandlers
	 * as earlier elements), this method corresponds to a no-op.
	 * <p>
	 * 要在委托给NavigationHandler链中的下一个处理程序时由子类调用的方法将始终调用最合适的下一个处理程序,或者作为构造函数参数传递的装饰的NavigationHandler或传递给此方法的原始
	 * NavigationHandler  - 根据位置链接中的这个实例<p>将调用指定为constructor参数的装饰的NavigationHandler(如果有)如果将DecoratingNavigat
	 * ionHandler作为目标,则传递给此方法的原始NavigationHandler将传递给链中的下一个元素：确保原始处理程序的传播,处理程序链中的最后一个元素可能会委托回在标准的NavigationH
	 * andler作为目标的情况下,原始处理程序将不会被传递;在这种情况下,没有委托回到原来的链可能进一步下降<p>如果没有装饰的NavigationHandler指定为构造函数参数,则该实例是链中的最后一个
	 * 
	 * @param facesContext the current JSF context
	 * @param fromAction the action binding expression that was evaluated to retrieve the
	 * specified outcome, or {@code null} if the outcome was acquired by some other means
	 * @param outcome the logical outcome returned by a previous invoked application action
	 * (which may be {@code null})
	 * @param originalNavigationHandler the original NavigationHandler,
	 * or {@code null} if none
	 */
	protected final void callNextHandlerInChain(
			FacesContext facesContext, String fromAction, String outcome, NavigationHandler originalNavigationHandler) {

		NavigationHandler decoratedNavigationHandler = getDecoratedNavigationHandler();

		if (decoratedNavigationHandler instanceof DecoratingNavigationHandler) {
			// DecoratingNavigationHandler specified through constructor argument:
			// Call it with original NavigationHandler passed in.
			DecoratingNavigationHandler decHandler = (DecoratingNavigationHandler) decoratedNavigationHandler;
			decHandler.handleNavigation(facesContext, fromAction, outcome, originalNavigationHandler);
		}
		else if (decoratedNavigationHandler != null) {
			// Standard NavigationHandler specified through constructor argument:
			// Call it through standard API, without original NavigationHandler passed in.
			// The called handler will not be able to redirect to the original handler.
			decoratedNavigationHandler.handleNavigation(facesContext, fromAction, outcome);
		}
		else if (originalNavigationHandler != null) {
			// No NavigationHandler specified through constructor argument:
			// Call original handler, marking the end of this chain.
			originalNavigationHandler.handleNavigation(facesContext, fromAction, outcome);
		}
	}

}
