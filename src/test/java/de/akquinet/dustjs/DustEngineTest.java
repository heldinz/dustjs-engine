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

import org.apache.commons.io.FileUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.*;
import java.net.URL;

import static org.junit.Assert.assertEquals;

public class DustEngineTest {

	private static DustEngine engine;

	@BeforeClass
	public static void before() {
		DustOptions options = new DustOptions();
		engine = new DustEngine(options);
	}

	@Test
	public void compileString() {
        String expected = "(function(){dust.register(\"test\",body_0);function body_0(chk,ctx){return chk.write(" +
                "\"Hello \").reference(ctx.get(\"name\"),ctx,\"h\").write(\"! You have \").reference(ctx.get(\"count\")," +
                "ctx,\"h\").write(\" new messages.\");}return body_0;})();";
		assertEquals(expected, engine.compile("Hello {name}! You have {count} new messages.", "test"));
	}

    @Test
    public void compileFile() {
        String expected = "(function(){dust.register(\"test\",body_0);function body_0(chk,ctx){return chk.write(" +
                "\"Hello \").reference(ctx.get(\"name\"),ctx,\"h\").write(\"! You have \").reference(ctx.get(\"count\")," +
                "ctx,\"h\").write(\" new messages.\");}return body_0;})();";
        File file = new File(getResource("dust/test.dust").getPath());
        assertEquals(expected, engine.compile(file));
    }

    @Test
    public void compileFileToFile() throws IOException {
        String expected = "(function(){dust.register(\"test\",body_0);function body_0(chk,ctx){return chk.write(" +
                "\"Hello \").reference(ctx.get(\"name\"),ctx,\"h\").write(\"! You have \").reference(ctx.get(\"count\")," +
                "ctx,\"h\").write(\" new messages.\");}return body_0;})();";
        File input = new File(getResource("dust/test.dust").getPath());
        String templateName = input.getName().split("\\.")[0];
        File tempDir = new File(System.getProperty("java.io.tmpdir"));
        File output = File.createTempFile(templateName + ".js", null, tempDir);
        engine.compile(input, output);
        assertEquals(expected, FileUtils.readFileToString(output));
        output.delete();
    }

    private URL getResource(String path) {
		return getClass().getClassLoader().getResource("META-INF/" + path);
	}

}