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

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import static org.junit.Assert.assertEquals;

public class CLITest {


    @Test
    public void compileFile() throws URISyntaxException {
        
        File test = new File("src/test/resources/META-INF/dust/test.dust");
        DustEngine.main(new String[] {test.getAbsolutePath()});

    }

    @Test
    public void compileFileToFile() throws IOException, URISyntaxException {
        File tmp = new File("target/tmp");
        tmp.mkdirs();
        File test = new File("src/test/resources/META-INF/dust/test.dust");
        File out = new File(tmp, "out.js");

        DustEngine.main(new String[] {test.getAbsolutePath(), out.getAbsolutePath()});


        String expected = "(function(){dust.register(\"test\",body_0);function body_0(chk,ctx){return chk.write(" +
                "\"Hello \").reference(ctx.get(\"name\"),ctx,\"h\").write(\"! You have \").reference(ctx.get(\"count\")," +
                "ctx,\"h\").write(\" new messages.\");}return body_0;})();";


        assertEquals(expected, FileUtils.readFileToString(out));
    }

}