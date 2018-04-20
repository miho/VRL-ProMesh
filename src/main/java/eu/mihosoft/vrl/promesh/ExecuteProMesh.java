package eu.mihosoft.vrl.promesh;

import eu.mihosoft.vrl.annotation.ComponentInfo;
import eu.mihosoft.vrl.annotation.ParamInfo;
import eu.mihosoft.vrl.promesh.util.ProMeshImpl;

import java.io.File;
import java.io.Serializable;


@ComponentInfo(name="ProMesh Engine", category="ProMesh")
public class ExecuteProMesh implements  Serializable {
    private static final long serialVersionUID = 1L;

    public void startUI() {
        ProMesh.startUI().print();
    }

    public void startUI(@ParamInfo(name="Geometry File", style = "load-dialog") File geometry) {
        ProMesh.startUI(geometry).print();
    }

    public void runScript(@ParamInfo(name="Script", style = "silent") String s) {
        ProMesh.execute(s).print();
    }

    public void runScript(@ParamInfo(name="Script", style = "silent") String s,
                          @ParamInfo(name="Input Geometry", style="load-dialog") File inputFile,
                          @ParamInfo(name="Output Geometry", style="save-dialog") File outputFile) {
        ProMesh.execute(s,inputFile,outputFile).print();
    }
}
