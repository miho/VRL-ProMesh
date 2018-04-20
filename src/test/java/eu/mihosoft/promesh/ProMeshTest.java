/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.promesh;

import eu.mihosoft.vrl.promesh.ProMesh;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class ProMeshTest {

    @Test
    public void executeExampleScriptTest() throws IOException {

        File geometryFile = new File("/Users/miho/Dropbox/tmp/VRL-JOGLTest/models/jcsg-metric-threads-01.stl");

        ProMesh promesh = ProMesh.execute("print(\"hello\")",
                geometryFile,
                ProMesh.newTmpFile("ugx")
        ).print().waitFor(5 * 60 * 1000 /* 5 min timeout */);

        System.out.println("ProMesh closed with exit code " + promesh.getProcess().exitValue());

    }
}
