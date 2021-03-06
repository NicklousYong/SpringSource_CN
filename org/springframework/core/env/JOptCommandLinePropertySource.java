/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2014 the original author or authors.
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import joptsimple.OptionSet;
import joptsimple.OptionSpec;

import org.springframework.util.Assert;

/**
 * {@link CommandLinePropertySource} implementation backed by a JOpt {@link OptionSet}.
 *
 * <h2>Typical usage</h2>
 * Configure and execute an {@code OptionParser} against the {@code String[]} of arguments
 * supplied to the {@code main} method, and create a {@link JOptCommandLinePropertySource}
 * using the resulting {@code OptionSet} object:
 * <pre class="code">
 * public static void main(String[] args) {
 *     OptionParser parser = new OptionParser();
 *     parser.accepts("option1");
 *     parser.accepts("option2").withRequiredArg();
 *     OptionSet options = parser.parse(args);
 *     PropertySource<?> ps = new JOptCommandLinePropertySource(options);
 *     // ...
 * }</pre>
 *
 * See {@link CommandLinePropertySource} for complete general usage examples.
 *
 * <p>Requires JOpt version 4.3 or higher. Tested against JOpt up until 4.6.
 *
 * <p>
 *  {@link CommandLinePropertySource}实现由JOpt {@link OptionSet}
 * 
 * <h2>典型用法</h2>根据提供给{@code main}方法的参数的{@code String []}配置并执行{@code OptionParser},并使用所得到的{@link JOptCommandLinePropertySource}
 *  {@code OptionSet}对象：。
 * <pre class="code">
 *  public static void main(String [] args){OptionParser parser = new OptionParser(); parseraccepts( "选项1"); parseraccepts( "选项2")withRequiredArg(); OptionSet options = parserparse(args); PropertySource <?> ps = new JOptCommandLinePropertySource(options); //}
 *  </pre>。
 * 
 *  有关完整的一般用法示例,请参阅{@link CommandLinePropertySource}
 * 
 * @author Chris Beams
 * @author Juergen Hoeller
 * @author Dave Syer
 * @since 3.1
 * @see CommandLinePropertySource
 * @see joptsimple.OptionParser
 * @see joptsimple.OptionSet
 */
public class JOptCommandLinePropertySource extends CommandLinePropertySource<OptionSet> {

	/**
	 * Create a new {@code JOptCommandLinePropertySource} having the default name
	 * and backed by the given {@code OptionSet}.
	 * <p>
	 * 
	 *  <p>需要JOpt版本43或更高版本测试为JOpt直到46
	 * 
	 * 
	 * @see CommandLinePropertySource#COMMAND_LINE_PROPERTY_SOURCE_NAME
	 * @see CommandLinePropertySource#CommandLinePropertySource(Object)
	 */
	public JOptCommandLinePropertySource(OptionSet options) {
		super(options);
	}

	/**
	 * Create a new {@code JOptCommandLinePropertySource} having the given name
	 * and backed by the given {@code OptionSet}.
	 * <p>
	 *  创建一个新的{@code JOptCommandLinePropertySource},具有默认名称,并由给定的{@code OptionSet}
	 * 
	 */
	public JOptCommandLinePropertySource(String name, OptionSet options) {
		super(name, options);
	}


	@Override
	protected boolean containsOption(String name) {
		return this.source.has(name);
	}

	@Override
	public String[] getPropertyNames() {
		List<String> names = new ArrayList<String>();
		for (OptionSpec<?> spec : this.source.specs()) {
			List<String> aliases = new ArrayList<String>(spec.options());
			if (!aliases.isEmpty()) {
				// Only the longest name is used for enumerating
				names.add(aliases.get(aliases.size() - 1));
			}
		}
		return names.toArray(new String[names.size()]);
	}

	@Override
	public List<String> getOptionValues(String name) {
		List<?> argValues = this.source.valuesOf(name);
		List<String> stringArgValues = new ArrayList<String>();
		for (Object argValue : argValues) {
			stringArgValues.add(argValue instanceof String ? (String) argValue : argValue.toString());
		}
		if (stringArgValues.isEmpty()) {
			return (this.source.has(name) ? Collections.<String>emptyList() : null);
		}
		return Collections.unmodifiableList(stringArgValues);
	}

	@Override
	protected List<String> getNonOptionArgs() {
		List<?> argValues = this.source.nonOptionArguments();
		List<String> stringArgValues = new ArrayList<String>();
		for (Object argValue : argValues) {
			Assert.isInstanceOf(String.class, argValue, "Argument values must be of type String");
			stringArgValues.add((String) argValue);
		}
		return (stringArgValues.isEmpty() ? Collections.<String>emptyList() :
				Collections.unmodifiableList(stringArgValues));
	}

}
