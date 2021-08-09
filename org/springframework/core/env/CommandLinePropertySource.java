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

package org.springframework.core.env;

import java.util.Collection;
import java.util.List;

import org.springframework.util.StringUtils;

/**
 * Abstract base class for {@link PropertySource} implementations backed by command line
 * arguments. The parameterized type {@code T} represents the underlying source of command
 * line options. This may be as simple as a String array in the case of
 * {@link SimpleCommandLinePropertySource}, or specific to a particular API such as JOpt's
 * {@code OptionSet} in the case of {@link JOptCommandLinePropertySource}.
 *
 * <h3>Purpose and General Usage</h3>
 *
 * For use in standalone Spring-based applications, i.e. those that are bootstrapped via
 * a traditional {@code main} method accepting a {@code String[]} of arguments from the
 * command line. In many cases, processing command-line arguments directly within the
 * {@code main} method may be sufficient, but in other cases, it may be desirable to
 * inject arguments as values into Spring beans. It is this latter set of cases in which
 * a {@code CommandLinePropertySource} becomes useful. A {@code CommandLinePropertySource}
 * will typically be added to the {@link Environment} of the Spring
 * {@code ApplicationContext}, at which point all command line arguments become available
 * through the {@link Environment#getProperty(String)} family of methods. For example:
 *
 * <pre class="code">
 * public static void main(String[] args) {
 *     CommandLinePropertySource clps = ...;
 *     AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
 *     ctx.getEnvironment().getPropertySources().addFirst(clps);
 *     ctx.register(AppConfig.class);
 *     ctx.refresh();
 * }</pre>
 *
 * With the bootstrap logic above, the {@code AppConfig} class may {@code @Inject} the
 * Spring {@code Environment} and query it directly for properties:
 *
 * <pre class="code">
 * &#064;Configuration
 * public class AppConfig {
 *
 *     &#064;Inject Environment env;
 *
 *     &#064;Bean
 *     public void DataSource dataSource() {
 *         MyVendorDataSource dataSource = new MyVendorDataSource();
 *         dataSource.setHostname(env.getProperty("db.hostname", "localhost"));
 *         dataSource.setUsername(env.getRequiredProperty("db.username"));
 *         dataSource.setPassword(env.getRequiredProperty("db.password"));
 *         // ...
 *         return dataSource;
 *     }
 * }</pre>
 *
 * Because the {@code CommandLinePropertySource} was added to the {@code Environment}'s
 * set of {@link MutablePropertySources} using the {@code #addFirst} method, it has
 * highest search precedence, meaning that while "db.hostname" and other properties may
 * exist in other property sources such as the system environment variables, it will be
 * chosen from the command line property source first. This is a reasonable approach
 * given that arguments specified on the command line are naturally more specific than
 * those specified as environment variables.
 *
 * <p>As an alternative to injecting the {@code Environment}, Spring's {@code @Value}
 * annotation may be used to inject these properties, given that a {@link
 * PropertySourcesPropertyResolver} bean has been registered, either directly or through
 * using the {@code <context:property-placeholder>} element. For example:
 *
 * <pre class="code">
 * &#064;Component
 * public class MyComponent {
 *
 *     &#064;Value("my.property:defaultVal")
 *     private String myProperty;
 *
 *     public void getMyProperty() {
 *         return this.myProperty;
 *     }
 *
 *     // ...
 * }</pre>
 *
 * <h3>Working with option arguments</h3>
 *
 * <p>Individual command line arguments are represented as properties through the usual
 * {@link PropertySource#getProperty(String)} and
 * {@link PropertySource#containsProperty(String)} methods. For example, given the
 * following command line:
 *
 * <pre class="code">--o1=v1 --o2</pre>
 *
 * 'o1' and 'o2' are treated as "option arguments", and the following assertions would
 * evaluate true:
 *
 * <pre class="code">
 * CommandLinePropertySource<?> ps = ...
 * assert ps.containsProperty("o1") == true;
 * assert ps.containsProperty("o2") == true;
 * assert ps.containsProperty("o3") == false;
 * assert ps.getProperty("o1").equals("v1");
 * assert ps.getProperty("o2").equals("");
 * assert ps.getProperty("o3") == null;
 * </pre>
 *
 * Note that the 'o2' option has no argument, but {@code getProperty("o2")} resolves to
 * empty string ({@code ""}) as opposed to {@code null}, while {@code getProperty("o3")}
 * resolves to {@code null} because it was not specified. This behavior is consistent with
 * the general contract to be followed by all {@code PropertySource} implementations.
 *
 * <p>Note also that while "--" was used in the examples above to denote an option
 * argument, this syntax may vary across individual command line argument libraries. For
 * example, a JOpt- or Commons CLI-based implementation may allow for single dash ("-")
 * "short" option arguments, etc.
 *
 * <h3>Working with non-option arguments</h3>
 *
 * <p>Non-option arguments are also supported through this abstraction. Any arguments
 * supplied without an option-style prefix such as "-" or "--" are considered "non-option
 * arguments" and available through the special {@linkplain
 * #DEFAULT_NON_OPTION_ARGS_PROPERTY_NAME "nonOptionArgs"} property.  If multiple
 * non-option arguments are specified, the value of this property will be a
 * comma-delimited string containing all of the arguments. This approach ensures a simple
 * and consistent return type (String) for all properties from a {@code
 * CommandLinePropertySource} and at the same time lends itself to conversion when used
 * in conjunction with the Spring {@link Environment} and its built-in {@code
 * ConversionService}. Consider the following example:
 *
 * <pre class="code">--o1=v1 --o2=v2 /path/to/file1 /path/to/file2</pre>
 *
 * In this example, "o1" and "o2" would be considered "option arguments", while the two
 * filesystem paths qualify as "non-option arguments".  As such, the following assertions
 * will evaluate true:
 *
 * <pre class="code">
 * CommandLinePropertySource<?> ps = ...
 * assert ps.containsProperty("o1") == true;
 * assert ps.containsProperty("o2") == true;
 * assert ps.containsProperty("nonOptionArgs") == true;
 * assert ps.getProperty("o1").equals("v1");
 * assert ps.getProperty("o2").equals("v2");
 * assert ps.getProperty("nonOptionArgs").equals("/path/to/file1,/path/to/file2");
 * </pre>
 *
 * <p>As mentioned above, when used in conjunction with the Spring {@code Environment}
 * abstraction, this comma-delimited string may easily be converted to a String array or
 * list:
 *
 * <pre class="code">
 * Environment env = applicationContext.getEnvironment();
 * String[] nonOptionArgs = env.getProperty("nonOptionArgs", String[].class);
 * assert nonOptionArgs[0].equals("/path/to/file1");
 * assert nonOptionArgs[1].equals("/path/to/file2");
 * </pre>
 *
 * <p>The name of the special "non-option arguments" property may be customized through
 * the {@link #setNonOptionArgsPropertyName(String)} method. Doing so is recommended as
 * it gives proper semantic value to non-option arguments. For example, if filesystem
 * paths are being specified as non-option arguments, it is likely preferable to refer to
 * these as something like "file.locations" than the default of "nonOptionArgs":
 *
 * <pre class="code">
 * public static void main(String[] args) {
 *     CommandLinePropertySource clps = ...;
 *     clps.setNonOptionArgsPropertyName("file.locations");
 *
 *     AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
 *     ctx.getEnvironment().getPropertySources().addFirst(clps);
 *     ctx.register(AppConfig.class);
 *     ctx.refresh();
 * }</pre>
 *
 * <h3>Limitations</h3>
 *
 * This abstraction is not intended to expose the full power of underlying command line
 * parsing APIs such as JOpt or Commons CLI. It's intent is rather just the opposite: to
 * provide the simplest possible abstraction for accessing command line arguments
 * <em>after</em> they have been parsed. So the typical case will involve fully configuring
 * the underlying command line parsing API, parsing the {@code String[]} of arguments
 * coming into the main method, and then simply providing the parsing results to an
 * implementation of {@code CommandLinePropertySource}. At that point, all arguments can
 * be considered either 'option' or 'non-option' arguments and as described above can be
 * accessed through the normal {@code PropertySource} and {@code Environment} APIs.
 *
 * <p>
 * 参数化类型{@code T}表示命令行选项的底层源。
 * 在{@link SimpleCommandLinePropertySource}的情况下,这可能与String数组一样简单,或特定于特定API,例如JOpt的{@code OptionSet},在{@link JOptCommandLinePropertySource}
 * 的情况下。
 * 参数化类型{@code T}表示命令行选项的底层源。
 * 
 *  <h3>目的和一般用途</h3>
 * 
 * 用于独立的基于Spring的应用程序,即通过传统的{@code main}方法引导的方法,它们从命令行接受参数的{@code String []}在许多情况下,直接在命令行中处理命令行参数{@code main}
 * 方法可能是足够的,但在其他情况下,可能需要将参数注入到Spring bean中。
 * 这是后一种情况,{@code CommandLinePropertySource}变得有用A {@code CommandLinePropertySource}通常会被添加到Spring {@code ApplicationContext}
 * 的{@link Environment}中,所有的命令行参数都可以通过{@link Environment#getProperty(String))方法获得。
 * 例如：。
 * 
 * <pre class="code">
 * public static void main(String [] args){CommandLinePropertySource clps =; AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(); ctxgetEnvironment()getPropertySources()addfirst仅(的CLPs); ctxregister(AppConfigclass); ctxrefresh(); }
 *  </PRE>。
 * 
 *  通过上面的引导逻辑,{@code AppConfig}类可以{@code @Inject} Spring {@code Environment}并直接查询属性：
 * 
 * <pre class="code">
 *  @Configuration public class AppConfig {
 * 
 *  @Inject环境env;
 * 
 * @Bean public void DataSource dataSource(){MyVendorDataSource dataSource = new MyVendorDataSource(); dataSourcesetHostname(envgetProperty("dbhostname","localhost")); dataSourcesetUsername(envgetRequiredProperty( "数据库用户名")); dataSourcesetPassword(envgetRequiredProperty( "DBPASSWORD")); // return dataSource; }
 * } </pre>。
 * 
 * 因为{@code CommandLinePropertySource}使用{@code #addFirst}方法添加到{@code Environment}的{@link MutablePropertySources}
 * 集合中,因此具有最高的搜索优先级,这意味着"dbhostname"和其他属性可能存在于其他属性源(如系统环境变量)中,它将从命令行属性源中首先选择这是一个合理的方法,因为在命令行上指定的参数自然比指定为
 * 环境变量的参数更具体。
 * 
 * <p>作为注入{@code Environment}的替代方法,Spring的{@code @Value}注释可以用于注入这些属性,只要已经注册了{@link PropertySourcesPropertyResolver}
 *  bean,可以直接或通过使用{@code <context：property-placeholder>}元素例如：。
 * 
 * <pre class="code">
 *  @Component public class MyComponent {
 * 
 *  @Value("myproperty：defaultVal")private String myProperty;
 * 
 *  public void getMyProperty(){return thismyProperty; }
 * 
 *  //} </pre>
 * 
 *  <h3>使用选项参数</h3>
 * 
 *  <p>单个命令行参数通过通常的{@link PropertySource#getProperty(String)}和{@link PropertySource#containsProperty(String)}
 * 方法表示为属性例如,给出以下命令行：。
 * 
 * <pre class ="code">  -  o1 = v1  -  o2 </pre>
 * 
 *  'o1'和'o2'被视为"选项参数",并且以下断言将评估为true：
 * 
 * <pre class="code">
 *  psLinePropertySource <?> ps = assert pscontainsProperty("o1")== true; assert pscontainsProperty("o2"
 * )== true; assert pscontainsProperty("o3")== false; assert psgetProperty("o1")equals("v1"); assert psg
 * etProperty("o2")equals(""); assert psgetProperty("o3")== null;。
 * </pre>
 * 
 *  请注意,'o2'选项没有参数,但{@code getProperty("o2")}解析为空字符串({@code""})而不是{@code null},而{@code getProperty(" o3")}
 * 解析为{@code null},因为未指定此行为与所有{@code PropertySource}实现所遵循的一般合同一致。
 * 
 * <p>另请注意,虽然在上面的示例中使用" - "来表示一个选项参数,但这种语法可能会在单独的命令行参数库中有所不同例如,基于JOpt或Commons CLI的实现可能允许单个短划线(" - ")"短"选
 * 项参数等。
 * 
 *  <h3>使用非选项参数</h3>
 * 
 * <p>通过此抽象也支持非选项参数。
 * 没有选项样式前缀(如" - "或" - ")提供的任何参数都被视为"非选项参数",可通过特殊的{@linkplain #DEFAULT_NON_OPTION_ARGS_PROPERTY_NAME "nonOptionArgs"}
 * 属性如果指定了多个非选项参数,则此属性的值将是包含所有参数的以逗号分隔的字符串。
 * <p>通过此抽象也支持非选项参数。
 * 
 * @author Chris Beams
 * @since 3.1
 * @see PropertySource
 * @see SimpleCommandLinePropertySource
 * @see JOptCommandLinePropertySource
 */
public abstract class CommandLinePropertySource<T> extends EnumerablePropertySource<T> {

	/** The default name given to {@link CommandLinePropertySource} instances: {@value} */
	public static final String COMMAND_LINE_PROPERTY_SOURCE_NAME = "commandLineArgs";

	/** The default name of the property representing non-option arguments: {@value} */
	public static final String DEFAULT_NON_OPTION_ARGS_PROPERTY_NAME = "nonOptionArgs";


	private String nonOptionArgsPropertyName = DEFAULT_NON_OPTION_ARGS_PROPERTY_NAME;


	/**
	 * Create a new {@code CommandLinePropertySource} having the default name
	 * {@value #COMMAND_LINE_PROPERTY_SOURCE_NAME} and backed by the given source object.
	 * <p>
	 * 此方法确保来自{@的所有属性的简单且一致的返回类型(String)代码CommandLinePropertySource},同时适用于与Spring {@link Environment}及其内置{@code ConversionService}
	 * 结合使用的转换。
	 * <p>通过此抽象也支持非选项参数。请考虑以下示例：。
	 * 
	 * <pre class ="code">  -  o1 = v1 --o2 = v2 / path / to / file1 / path / to / file2 </pre>
	 * 
	 *  在此示例中,"o1"和"o2"将被视为"选项参数",而两个文件系统路径将被视为"非选项参数"。因此,以下断言将评估为true：
	 * 
	 * <pre class="code">
	 *  psLinePropertySource <?> ps = assert pscontainsProperty("o1")== true; assert pscontainsProperty("o2"
	 * )== true; assert pscontainsProperty("nonOptionArgs")== true; assert psgetProperty("o1")equals("v1"); 
	 * assert psgetProperty("o2")equals("v2"); assert psgetProperty("nonOptionArgs")equals("/ path / to / fi
	 * le1,/ path / to / file2");。
	 * </pre>
	 * 
	 *  如上所述,当与Spring {@code Environment}抽象结合使用时,以逗号分隔的字符串可以很容易地转换为String数组或列表：
	 * 
	 * <pre class="code">
	 * 环境env = applicationContextgetEnvironment(); String [] nonOptionArgs = envgetProperty("nonOptionArgs",
	 * String [] class); assert nonOptionArgs [0] equals("/ path / to / file1"); assert nonOptionArgs [1] eq
	 * uals("/ path / to / file2");。
	 * </pre>
	 * 
	 *  <p>可以通过{@link #setNonOptionArgsPropertyName(String)}方法定制特殊"非选项参数"属性的名称建议使用此选项,因为它为非选项参数提供适当的语义值例如,如果
	 * 文件系统路径被指定为非选项参数,很可能将这些参数称为"defaultlocs",而不是默认的"nonOptionArgs"：。
	 * 
	 * <pre class="code">
	 *  public static void main(String [] args){CommandLinePropertySource clps =; clpssetNonOptionArgsPropertyName( "filelocations");。
	 * 
	 * AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(); ctxgetEnvironment(
	 * )getPropertySources()addfirst仅(的CLPs); ctxregister(AppConfigclass); ctxrefresh(); } </PRE>。
	 * 
	 *  <H3>限制</H3>
	 * 
	 * 这种抽象不是为了揭示底层命令行解析API(如JOpt或Commons CLI)的全部功能意图恰恰相反：为了在</em>之后访问命令行参数<em>提供最简单的可能抽象已被解析所以典型的情况将涉及完全配置底
	 */
	public CommandLinePropertySource(T source) {
		super(COMMAND_LINE_PROPERTY_SOURCE_NAME, source);
	}

	/**
	 * Create a new {@link CommandLinePropertySource} having the given name
	 * and backed by the given source object.
	 * <p>
	 * 层的命令行解析API,解析进入main方法的参数的{@code String []},然后简单地将解析结果提供给{@code CommandLinePropertySource }在这一点上,所有参数都
	 * 可以被认为是'选项'或'非选项'参数,如上所述可以通过普通的{@code PropertySource}和{@code Environment} API来访问。
	 * 
	 */
	public CommandLinePropertySource(String name, T source) {
		super(name, source);
	}


	/**
	 * Specify the name of the special "non-option arguments" property.
	 * The default is {@value #DEFAULT_NON_OPTION_ARGS_PROPERTY_NAME}.
	 * <p>
	 * 创建一个新的{@code CommandLinePropertySource},其默认名称为{@value #COMMAND_LINE_PROPERTY_SOURCE_NAME},并由给定的源对象支持。
	 * 
	 */
	public void setNonOptionArgsPropertyName(String nonOptionArgsPropertyName) {
		this.nonOptionArgsPropertyName = nonOptionArgsPropertyName;
	}

	/**
	 * This implementation first checks to see if the name specified is the special
	 * {@linkplain #setNonOptionArgsPropertyName(String) "non-option arguments" property},
	 * and if so delegates to the abstract {@link #getNonOptionArgs()} method
	 * checking to see whether it returns an empty collection. Otherwise delegates to and
	 * returns the value of the abstract {@link #containsOption(String)} method.
	 * <p>
	 *  创建一个具有给定名称并由给定源对象支持的新的{@link CommandLinePropertySource}
	 * 
	 */
	@Override
	public final boolean containsProperty(String name) {
		if (this.nonOptionArgsPropertyName.equals(name)) {
			return !this.getNonOptionArgs().isEmpty();
		}
		return this.containsOption(name);
	}

	/**
	 * This implementation first checks to see if the name specified is the special
	 * {@linkplain #setNonOptionArgsPropertyName(String) "non-option arguments" property},
	 * and if so delegates to the abstract {@link #getNonOptionArgs()} method. If so
	 * and the collection of non-option arguments is empty, this method returns {@code
	 * null}. If not empty, it returns a comma-separated String of all non-option
	 * arguments. Otherwise delegates to and returns the result of the abstract {@link
	 * #getOptionValues(String)} method.
	 * <p>
	 *  指定特殊"非选项参数"属性的名称默认值为{@value #DEFAULT_NON_OPTION_ARGS_PROPERTY_NAME}
	 * 
	 */
	@Override
	public final String getProperty(String name) {
		if (this.nonOptionArgsPropertyName.equals(name)) {
			Collection<String> nonOptionArguments = this.getNonOptionArgs();
			if (nonOptionArguments.isEmpty()) {
				return null;
			}
			else {
				return StringUtils.collectionToCommaDelimitedString(nonOptionArguments);
			}
		}
		Collection<String> optionValues = this.getOptionValues(name);
		if (optionValues == null) {
			return null;
		}
		else {
			return StringUtils.collectionToCommaDelimitedString(optionValues);
		}
	}


	/**
	 * Return whether the set of option arguments parsed from the command line contains
	 * an option with the given name.
	 * <p>
	 *  此实现首先检查指定的名称是否是特殊的{@linkplain #setNonOptionArgsPropertyName(String)"非选项参数"属性},如果是,则委托给抽象的{@link #getNonOptionArgs()}
	 * 方法检查是否它返回一个空集合否则委托并返回抽象{@link #containsOption(String)}方法的值。
	 * 
	 */
	protected abstract boolean containsOption(String name);

	/**
	 * Return the collection of values associated with the command line option having the
	 * given name.
	 * <ul>
	 * <li>if the option is present and has no argument (e.g.: "--foo"), return an empty
	 * collection ({@code []})</li>
	 * <li>if the option is present and has a single value (e.g. "--foo=bar"), return a
	 * collection having one element ({@code ["bar"]})</li>
	 * <li>if the option is present and the underlying command line parsing library
	 * supports multiple arguments (e.g. "--foo=bar --foo=baz"), return a collection
	 * having elements for each value ({@code ["bar", "baz"]})</li>
	 * <li>if the option is not present, return {@code null}</li>
	 * </ul>
	 * <p>
	 * 这个实现首先检查是否指定的名称是特殊的{@linkplain #setNonOptionArgsPropertyName(String)"非选项参数"属性},如果这样委托给抽象的{@link #getNonOptionArgs()}
	 * 方法如果是这样,非选项参数的集合为空,此方法返回{@code null}如果不为空,则返回所有非选项参数的逗号分隔的String否则委托并返回抽象结果{@link #getOptionValues( String)}
	 * 方法。
	 * 
	 */
	protected abstract List<String> getOptionValues(String name);

	/**
	 * Return the collection of non-option arguments parsed from the command line.
	 * Never {@code null}.
	 * <p>
	 *  返回从命令行解析的选项参数集是否包含给定名称的选项
	 * 
	 */
	protected abstract List<String> getNonOptionArgs();

}
