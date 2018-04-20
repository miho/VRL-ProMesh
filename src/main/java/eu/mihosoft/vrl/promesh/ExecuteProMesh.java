package eu.mihosoft.vrl.promesh;

import eu.mihosoft.vrl.annotation.ComponentInfo;
import eu.mihosoft.vrl.annotation.ParamInfo;
import eu.mihosoft.vrl.promesh.util.ProMeshImpl;

import java.io.File;
import java.io.Serializable;


@ComponentInfo(name="Intersection", category="ProMesh")
public class ExecuteProMesh implements  Serializable {
    private static final long serialVersionUID = 1L;

    public void startUI() {
        ProMesh.startUI();
    }

    public void startUI(@ParamInfo(name="Geometry File", style = "load-dialog") File geometry) {
        ProMesh.startUI(geometry).print();
    }
}
