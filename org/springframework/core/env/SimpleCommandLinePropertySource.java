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

package org.springframework.core.env;

import java.util.List;

/**
 * {@link CommandLinePropertySource} implementation backed by a simple String array.
 *
 * <h3>Purpose</h3>
 * This {@code CommandLinePropertySource} implementation aims to provide the simplest
 * possible approach to parsing command line arguments.  As with all {@code
 * CommandLinePropertySource} implementations, command line arguments are broken into two
 * distinct groups: <em>option arguments</em> and <em>non-option arguments</em>, as
 * described below <em>(some sections copied from Javadoc for {@link SimpleCommandLineArgsParser})</em>:
 *
 * <h3>Working with option arguments</h3>
 * Option arguments must adhere to the exact syntax:
 * <pre class="code">--optName[=optValue]</pre>
 * That is, options must be prefixed with "{@code --}", and may or may not specify a value.
 * If a value is specified, the name and value must be separated <em>without spaces</em>
 * by an equals sign ("=").
 *
 * <h4>Valid examples of option arguments</h4>
 * <pre class="code">
 * --foo
 * --foo=bar
 * --foo="bar then baz"
 * --foo=bar,baz,biz</pre>
 *
 * <h4>Invalid examples of option arguments</h4>
 * <pre class="code">
 * -foo
 * --foo bar
 * --foo = bar
 * --foo=bar --foo=baz --foo=biz</pre>
 *
 * <h3>Working with non-option arguments</h3>
 * Any and all arguments specified at the command line without the "{@code --}" option
 * prefix will be considered as "non-option arguments" and made available through the
 * {@link #getNonOptionArgs()} method.
 *
 * <h2>Typical usage</h2>
 * <pre class="code">
 * public static void main(String[] args) {
 *     PropertySource<?> ps = new SimpleCommandLinePropertySource(args);
 *     // ...
 * }</pre>
 *
 * See {@link CommandLinePropertySource} for complete general usage examples.
 *
 * <h3>Beyond the basics</h3>
 *
 * <p>When more fully-featured command line parsing is necessary, consider using
 * the provided {@link JOptCommandLinePropertySource}, or implement your own
 * {@code CommandLinePropertySource} against the command line parsing library of your
 * choice!
 *
 * <p>
 *  {@link CommandLinePropertySource}实现由简单的String数组支持
 * 
 * <h3>目的</h3>此{@code CommandLinePropertySource}实现旨在提供最简单的解析命令行参数的方法与所有{@code CommandLinePropertySource}
 * 实现一样,命令行参数分为两个不同的组：<em>选项参数</em>和<em>非选项参数</em>,如下所述<em>(从Javadoc为{@link SimpleCommandLineArgsParser}
 * 复制的部分)</em>：。
 * 
 *  <h3>使用选项参数</h3>选项参数必须遵守确切的语法：<pre class ="code">  -  optName [= optValue] </pre>也就是说,选项必须以"{@代码 - }"
 * ,并且可能指定或不指定值如果指定了值,则必须使用等号("=")将名称和值与<空格</em>分开,。
 * 
 * <h4>选项参数的有效示例</h4>
 * <pre class="code">
 *  --foo --foo = bar --foo ="bar then baz"--foo = bar,baz,biz </pre>
 * 
 *  <h4>无效的选项参数示例</h4>
 * <pre class="code">
 *  -foo --foo bar --foo = bar --foo = bar --foo = baz --foo = biz </pre>
 * 
 *  <h3>使用非选项参数</h3>在没有"{@code  - }"选项前缀的命令行中指定的任何和所有参数将被视为"非选项参数",并通过{ @link #getNonOptionArgs()}方法
 * 
 *  <h2>典型用法</h2>
 * 
 * @author Chris Beams
 * @since 3.1
 * @see CommandLinePropertySource
 * @see JOptCommandLinePropertySource
 */
public class SimpleCommandLinePropertySource extends CommandLinePropertySource<CommandLineArgs> {

	/**
	 * Create a new {@code SimpleCommandLinePropertySource} having the default name
	 * and backed by the given {@code String[]} of command line arguments.
	 * <p>
	 * <pre class="code">
	 *  public static void main(String [] args){PropertySource <?> ps = new SimpleCommandLinePropertySource(args); //}
	 *  </pre>。
	 * 
	 *  有关完整的一般用法示例,请参阅{@link CommandLinePropertySource}
	 * 
	 *  <h3>超越基础</h3>
	 * 
	 * <p>当需要更全面的命令行解析时,请考虑使用提供的{@link JOptCommandLinePropertySource},或者根据您选择的命令行解析库实现自己的{@code CommandLinePropertySource}
	 * 。
	 * 
	 * @see CommandLinePropertySource#COMMAND_LINE_PROPERTY_SOURCE_NAME
	 * @see CommandLinePropertySource#CommandLinePropertySource(Object)
	 */
	public SimpleCommandLinePropertySource(String... args) {
		super(new SimpleCommandLineArgsParser().parse(args));
	}

	/**
	 * Create a new {@code SimpleCommandLinePropertySource} having the given name
	 * and backed by the given {@code String[]} of command line arguments.
	 * <p>
	 * 
	 */
	public SimpleCommandLinePropertySource(String name, String[] args) {
		super(name, new SimpleCommandLineArgsParser().parse(args));
	}

	/**
	 * Get the property names for the option arguments.
	 * <p>
	 *  创建一个新的{@code SimpleCommandLinePropertySource},具有默认名称,并由给定的{@code String []}命令行参数支持。
	 * 
	 */
	@Override
	public String[] getPropertyNames() {
		return source.getOptionNames().toArray(new String[source.getOptionNames().size()]);
	}

	@Override
	protected boolean containsOption(String name) {
		return this.source.containsOption(name);
	}

	@Override
	protected List<String> getOptionValues(String name) {
		return this.source.getOptionValues(name);
	}

	@Override
	protected List<String> getNonOptionArgs() {
		return this.source.getNonOptionArgs();
	}

}
