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

package org.springframework.validation;

/**
 * A validator for application-specific objects.
 *
 * <p>This interface is totally divorced from any infrastructure
 * or context; that is to say it is not coupled to validating
 * only objects in the web tier, the data-access tier, or the
 * whatever-tier. As such it is amenable to being used in any layer
 * of an application, and supports the encapsulation of validation
 * logic as a first-class citizen in its own right.
 *
 * <p>Find below a simple but complete {@code Validator}
 * implementation, which validates that the various {@link String}
 * properties of a {@code UserLogin} instance are not empty
 * (that is they are not {@code null} and do not consist
 * wholly of whitespace), and that any password that is present is
 * at least {@code 'MINIMUM_PASSWORD_LENGTH'} characters in length.
 *
 * <pre class="code"> public class UserLoginValidator implements Validator {
 *
 *    private static final int MINIMUM_PASSWORD_LENGTH = 6;
 *
 *    public boolean supports(Class clazz) {
 *       return UserLogin.class.isAssignableFrom(clazz);
 *    }
 *
 *    public void validate(Object target, Errors errors) {
 *       ValidationUtils.rejectIfEmptyOrWhitespace(errors, "userName", "field.required");
 *       ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "field.required");
 *       UserLogin login = (UserLogin) target;
 *       if (login.getPassword() != null
 *             && login.getPassword().trim().length() < MINIMUM_PASSWORD_LENGTH) {
 *          errors.rejectValue("password", "field.min.length",
 *                new Object[]{Integer.valueOf(MINIMUM_PASSWORD_LENGTH)},
 *                "The password must be at least [" + MINIMUM_PASSWORD_LENGTH + "] characters in length.");
 *       }
 *    }
 * }</pre>
 *
 * <p>See also the Spring reference manual for a fuller discussion of
 * the {@code Validator} interface and it's role in an enterprise
 * application.
 *
 * <p>
 *  应用程序特定对象的验证器
 * 
 * <p>这个界面完全脱离任何基础设施或上下文;也就是说,它不耦合到仅验证Web层,数据访问层或任何层中的对象。因此,它可以在应用程序的任何层中使用,并且支持验证逻辑的封装作为一个一流的公民本身
 * 
 *  <p>在下面找到一个简单但完整的{@code Validator}实现,它验证{@code UserLogin}实例的各种{@link String}属性不为空(即它们不是{@code null}和不
 * 完全由空格组成),并且存在的任何密码长度至少为{@code'MINIMUM_PASSWORD_LENGTH'}个字符。
 * 
 *  <class ="code"> public class UserLoginValidator implements Validator {
 * 
 * private static final int MINIMUM_PASSWORD_LENGTH = 6;
 * 
 *  public boolean supports(Class clazz){return UserLoginclassisAssignableFrom(clazz); }
 * 
 *  public void validate(Object target,Errors errors){ValidationUtilsrejectIfEmptyOrWhitespace(errors,"userName","fieldrequired"); ValidationUtilsrejectIfEmptyOrWhitespace(errors,"password","fieldrequired"); UserLogin login =(UserLogin)target; if(logingetPassword()！= null && logingetPassword()trim()length()<MINIMUM_PASSWORD_LENGTH){errorsrejectValue("password","fieldminlength",new Object [] {IntegervalueOf(MINIMUM_PASSWORD_LENGTH)}",密码必须至少["+ MINIMUM_PASSWORD_LENGTH +"]个字符)"); }}} </pre>
 * 
 * @author Rod Johnson
 * @see Errors
 * @see ValidationUtils
 */
public interface Validator {

	/**
	 * Can this {@link Validator} {@link #validate(Object, Errors) validate}
	 * instances of the supplied {@code clazz}?
	 * <p>This method is <i>typically</i> implemented like so:
	 * <pre class="code">return Foo.class.isAssignableFrom(clazz);</pre>
	 * (Where {@code Foo} is the class (or superclass) of the actual
	 * object instance that is to be {@link #validate(Object, Errors) validated}.)
	 * <p>
	 * 。
	 * 
	 * <p>另请参阅Spring参考手册,以更全面地讨论{@code Validator}接口,并且它在企业应用程序中的作用
	 * 
	 * 
	 * @param clazz the {@link Class} that this {@link Validator} is
	 * being asked if it can {@link #validate(Object, Errors) validate}
	 * @return {@code true} if this {@link Validator} can indeed
	 * {@link #validate(Object, Errors) validate} instances of the
	 * supplied {@code clazz}
	 */
	boolean supports(Class<?> clazz);

	/**
	 * Validate the supplied {@code target} object, which must be
	 * of a {@link Class} for which the {@link #supports(Class)} method
	 * typically has (or would) return {@code true}.
	 * <p>The supplied {@link Errors errors} instance can be used to report
	 * any resulting validation errors.
	 * <p>
	 *  所提供的{@code clazz}的{@link验证者} {@link #validate(Object,Errors)验证}实例? <p>此方法通常</i>实现：<pre class ="code">
	 *  return FooclassisAssignableFrom(clazz); </pre>(其中{@code Foo}是类(或超类)实际的对象实例是{@link #validate(Object,Errors)validated}
	 * )。
	 * 
	 * 
	 * @param target the object that is to be validated (can be {@code null})
	 * @param errors contextual state about the validation process (never {@code null})
	 * @see ValidationUtils
	 */
	void validate(Object target, Errors errors);

}
