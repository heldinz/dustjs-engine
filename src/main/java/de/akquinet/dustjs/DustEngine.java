/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *	  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.akquinet.dustjs;

import org.apache.commons.cli.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mozilla.javascript.*;
import org.mozilla.javascript.tools.shell.Global;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;

public class DustEngine {

	private final Log logger = LogFactory.getLog(getClass());

	private Scriptable scope;
	private ClassLoader classLoader;
	private Function compileString;
    private Function compileFile;

	public DustEngine() {
		this(new DustOptions());
	}

	public DustEngine(DustOptions options) {
		try {
			logger.debug("Initializing dust Engine.");
			classLoader = getClass().getClassLoader();
			URL dust = options.getDust();
			URL env = classLoader.getResource("META-INF/env.js");
			URL engine = classLoader.getResource("META-INF/engine.js");
			Context cx = Context.enter();
			logger.debug("Using implementation version: " + cx.getImplementationVersion());
			cx.setOptimizationLevel(9);
			Global global = new Global();
			global.init(cx);
			scope = cx.initStandardObjects(global);
			cx.evaluateReader(scope, new InputStreamReader(env.openConnection().getInputStream()), env.getFile(), 1, null);
			cx.evaluateString(scope, "dustenv.charset = '" + options.getCharset() + "';", "charset", 1, null);
			cx.evaluateReader(scope, new InputStreamReader(dust.openConnection().getInputStream()), dust.getFile(), 1, null);
			cx.evaluateReader(scope, new InputStreamReader(engine.openConnection().getInputStream()), engine.getFile(), 1, null);
			compileString = (Function) scope.get("compileString", scope);
            compileFile = (Function) scope.get("compileFile", scope); 
			Context.exit();
		} catch (Exception e) {
			logger.error("Dust engine initialization failed.", e);
		}
	}

    public String compile(String source, String templateName) {
        long time = System.currentTimeMillis();
        String result = call(compileString, new Object[] {source, templateName});
        logger.debug("The compilation of '" + source + "' took " + (System.currentTimeMillis () - time) + " ms.");
        return result;
    }
	
	public String compile(File input) {
        long time = System.currentTimeMillis();
        logger.debug("Compiling File: " + "file:" + input.getAbsolutePath());
        String templateName = input.getName().split("\\.")[0];
        String result = call(compileFile, new Object[]{input, templateName});
        logger.debug("The compilation of '" + input + "' took " + (System.currentTimeMillis () - time) + " ms.");
        return result;
	}
	
	public void compile(File input, File output) {
		try {
			String content = compile(input);
			if (!output.exists()) {
				output.createNewFile();
			}
			BufferedWriter bw = new BufferedWriter(new FileWriter(output));
			bw.write(content);
			bw.close();
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
	}

	private synchronized String call(Function fn, Object[] args) {
		return (String) Context.call(null, fn, scope, scope, args);
	}
	
	public static void main(String[] args) throws URISyntaxException {
		Options cmdOptions = new Options();
		cmdOptions.addOption(DustOptions.CHARSET_OPTION, true, "Input file charset encoding. Defaults to UTF-8.");
		cmdOptions.addOption(DustOptions.DUST_OPTION, true, "Path to a custom dust.js for Rhino version.");
		try {
			CommandLineParser cmdParser = new GnuParser();
			CommandLine cmdLine = cmdParser.parse(cmdOptions, args);
			DustOptions options = new DustOptions();
			if (cmdLine.hasOption(DustOptions.CHARSET_OPTION)) {
				options.setCharset(cmdLine.getOptionValue(DustOptions.CHARSET_OPTION));
			}
			if (cmdLine.hasOption(DustOptions.DUST_OPTION)) {
				options.setDust(new File(cmdLine.getOptionValue(DustOptions.DUST_OPTION)).toURI().toURL());
			}
			DustEngine engine = new DustEngine(options);
			String[] files = cmdLine.getArgs();

            String src = null;
            if (files == null  || files.length == 0) {
                BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
                StringWriter sw = new StringWriter();
                char[] buffer = new char[1024];
                int n = 0;
                while (-1 != (n = in.read(buffer))) {
                    sw.write(buffer, 0, n);
                }
                src = sw.toString();
            }

			if (src != null  && !src.isEmpty()) {
				System.out.println(engine.compile(src, "test"));
                return;
			}

			if (files.length == 1) {
				System.out.println(engine.compile(new File(files[0])));
                return;
			}

			if (files.length == 2) {
				engine.compile(new File(files[0]), new File(files[1]));
                return;
			}
			
		} catch (IOException ioe) {
			System.err.println("Error opening input file.");
		} catch (ParseException pe) {
			System.err.println("Error parsing arguments.");
		}


		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("java -jar dust-engine.jar input [output] [options]", cmdOptions);
		System.exit(1);
	}

}