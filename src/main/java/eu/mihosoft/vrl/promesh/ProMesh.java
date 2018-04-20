/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.promesh;


import eu.mihosoft.vrl.promesh.util.ProMeshImpl;

import java.io.File;
import java.io.PrintStream;

/**
 * Executes ProMesh scripts and the ProMesh UI.
 * 
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public interface ProMesh {

    /**
     * Destroys the currently running tcc process.
     */
    void destroy();

    /**
     * Returns the process of the current ProMesh process.
     * @return the process of the current ProMesh process
     */
    Process getProcess();

    /**
     * Returns the working directory
     * @return the working directory
     */
    File getWorkingDirectory();

    /**
     * Prints the ProMesh output to the specified print streams.
     * @param out standard output stream
     * @param err error output stream
     * @return this ProMesh interpreter
     */
    ProMesh print(PrintStream out, PrintStream err);

    /**
     * Prints the ProMesh output to the standard output.
     * @return this ProMesh interpreter
     */
    ProMesh print();

    /**
     * Waits until the ProMesh process terminates.
     * @return this ProMesh interpreter
     */
    ProMesh waitFor();

    /**
     * Waits until the ProMesh process terminates or the execution time exceeds the specified waiting time.
     * @param ms time to wait for execution
     * @return this ProMesh interpreter
     */
    ProMesh waitFor(long ms);

    /**
     * Starts ProMesh UI.
     *
     * @param geometryFile geometry file to open
     *
     * @return this ProMesh interpreter
     */
    static ProMesh startUI(File geometryFile) {
        return ProMeshImpl.startUI(geometryFile);
    }

    /**
     * Starts ProMesh UI.
     *
     * @return this ProMesh interpreter
     */
    static ProMesh startUI() {
        return ProMeshImpl.startUI();
    }
    
    /**
     * Executes ProMesh with the specified Lua script.
     *
     * @param wd working directory (currently ignored)
     * @param script script code that shall be executed
     * @return this ProMesh interpreter
     */
    static ProMesh execute(File wd, String script) {
        return ProMeshImpl.execute(wd, script);
    }
    
    /**
     * Executes ProMesh with the specified C script.
     * 
     * @param wd working directory (currently ignored)
     * @param script Lua script that shall be executed
     * @return this ProMesh interpreter
     */
    static ProMesh execute(File wd, File script) {
        return ProMeshImpl.execute(wd, script);
    }
    
    /**
     * Executes ProMesh with the specified arguments.
     *
     * @param arguments arguments
     * @param wd working directory (currently ignored)
     * @return ProMesh process
     */
    static Process execute(File wd, String... arguments) {
        return ProMeshImpl.execute(false, wd, arguments);
    }

    /**
     * Executes ProMesh with the specified Lua script.
     *
     * @param script script code that shall be executed
     * @param arguments arguments
     * @return this ProMesh interpreter
     */
    static ProMesh execute(String script, String... arguments) {
        return ProMeshImpl.execute((File)null, script, arguments);
    }

    /**
     * Executes ProMesh with the specified Lua script.
     *
     * @param script script code that shall be executed
     * @param input input file
     * @param output output file
     * @return this ProMesh interpreter
     */
    static ProMesh execute(String script, File input, File output) {
        return ProMeshImpl.execute((File)null,
                script,
                "-in", input.getAbsolutePath(),
                "-out", output.getAbsolutePath());
    }

    /**
     * Creates a new tmp file in the systems default location for tmp files.
     * @param ending file ending, e.g. ({@code stl} or {@code ugx}).
     * @return a new tmp file
     */
    static File newTmpFile(String ending) {
        return ProMeshImpl.newTmpFile(ending);
    }


    /**
     * Executes ProMesh with the specified Lua script.
     *
     * @param script script code that shall be executed
     * @param arguments arguments
     * @return this ProMesh interpreter
     */
    static ProMesh execute(File wd, File script, String... arguments) {
        return ProMeshImpl.execute(wd, script, arguments);
    }

    /**
     * Executes ProMesh with the specified arguments.
     *
     * @param arguments arguments
     * @return ProMesh process
     */
    static Process run(String... arguments) {
        return ProMeshImpl.execute(false, null, arguments);
    }

    /**
     * Returns the ProMesh installation folder.
     *
     * @return the ProMesh installation folder
     */
    static File getInstallationFolder() {
        return ProMeshImpl.getInstallationFolder();
    }


    /**
     * Returns the ProMesh version as string.
     *
     * @return the ProMesh version as string
     */
    static String getVersionString() { return ProMeshImpl.getVersionString();}

}
