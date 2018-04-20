package eu.mihosoft.vrl.promesh.util;

import eu.mihosoft.vrl.promesh.ProMesh;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

@Deprecated
public class ProMeshImpl implements ProMesh {

    private static File executableFile;
    private static File proMeshHomeFolder;
    private static String promeshVersion;
    private final Process promeshProcess;
    private static boolean initialized;
    private StreamGobbler errorGobbler;
    private StreamGobbler stdGobbler;


    static {
        // static init
    }
    private final File wd;

    private ProMeshImpl(Process proc, File wd) {
        this.promeshProcess = proc;
        this.wd = wd;
    }

    /**
     * Initializes property folder and executable.
     */
    private static void initialize() {

        // already initialized: we don't do anything
        if (initialized) {
            return;
        }

        String userHomeFolder = System.getProperty("user.home");

        File promeshConfigFolder = new File(userHomeFolder,".promesh").getAbsoluteFile();

        File promeshInstallPathFile = new File(promeshConfigFolder, "promesh_home");
        File promeshVersionFile = new File(promeshConfigFolder, "promesh_version");

        try {
            String proMeshHomeFolderString = new String(Files.readAllBytes(promeshInstallPathFile.toPath())).replaceAll("\\R","");
            proMeshHomeFolder = new File(proMeshHomeFolderString);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Cannot read promesh install location from " + promeshInstallPathFile, e);
        }

        try {
            promeshVersion = new String(Files.readAllBytes(promeshVersionFile.toPath()));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Cannot read promesh version from " + promeshVersionFile, e);
        }


        if(VSysUtil.isWindows()) {
            executableFile = new File(proMeshHomeFolder, "ProMesh4.exe");
        } else {
            executableFile = new File(proMeshHomeFolder, "ProMesh4");
        }

        initialized = true;
    }



    @Override
    public ProMeshImpl print(PrintStream out, PrintStream err) {
        errorGobbler = new StreamGobbler(err, promeshProcess.getErrorStream(), "");
        errorGobbler.start();
        stdGobbler = new StreamGobbler(out, promeshProcess.getInputStream(), "");
        stdGobbler.start();

        return this;
    }

    @Override
    public ProMeshImpl print() {
        errorGobbler = new StreamGobbler(System.err, promeshProcess.getErrorStream(), "");
        errorGobbler.start();

        stdGobbler = new StreamGobbler(System.out, promeshProcess.getInputStream(), "");
        stdGobbler.start();

        return this;
    }

    @Override
    public ProMeshImpl waitFor() {
        try {
            promeshProcess.waitFor();
            if(errorGobbler!=null) {
                errorGobbler.join();
                stdGobbler.join();
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(ProMeshImpl.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException("Cannot wait until process is finished", ex);
        }

        return this;
    }

    @Override
    public ProMeshImpl waitFor(long ms) {
        try {
            promeshProcess.waitFor(ms, TimeUnit.MILLISECONDS);
            promeshProcess.destroyForcibly();
            if(errorGobbler!=null) {
                errorGobbler.join();
                stdGobbler.join();
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(ProMeshImpl.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException("Cannot wait until process is finished", ex);
        }

        return this;
    }


    public static ProMeshImpl execute(File wd, String script) {
        File tmpDir;
        File scriptFile;
        try {
            tmpDir = Files.createTempDirectory("promesh-script-tmp").toFile();
            scriptFile = new File(tmpDir, "code.lua");
            Files.write(scriptFile.toPath(), script.getBytes("UTF-8"));
        } catch (IOException ex) {
            Logger.getLogger(ProMeshImpl.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException("Cannot execute script due to io exception", ex);
        }

        return execute(wd, scriptFile);
    }

    public static ProMeshImpl execute(File wd, String script, String... arguments) {
        File tmpDir;
        File scriptFile;
        try {
            tmpDir = Files.createTempDirectory("promesh-script-tmp").toFile();
            scriptFile = new File(tmpDir, "code.lua");
            Files.write(scriptFile.toPath(), script.getBytes("UTF-8"));
        } catch (IOException ex) {
            Logger.getLogger(ProMeshImpl.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException("Cannot execute script due to io exception", ex);
        }

        return execute(wd, scriptFile, arguments);
    }

    public static ProMeshImpl execute(File wd, File script, String... arguments) {

        initialize();

        Path scriptFile;

        try {
            scriptFile = Files.createTempFile("promesh_script", ".lua");

            String scriptCode = new String(
                    Files.readAllBytes(script.toPath()), "UTF-8");

            Files.write(scriptFile,
                    scriptCode.getBytes(Charset.forName("UTF-8")));

        } catch (IOException ex) {
            Logger.getLogger(ProMeshImpl.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException("Cannot create tmp script-file", ex);
        }

        String[] args = new String[3+arguments.length];

        args[0] = executableFile.getAbsolutePath();
        args[1] = "-script";
        args[2] = scriptFile.toAbsolutePath().toString();

        int aIdx = 3;
        for(String a : arguments) {
            args[aIdx++] = a;
        }


        Process proc = execute(false, wd, args);

        return new ProMeshImpl(proc, wd);
    }

    public static ProMeshImpl startUI(File geometry) {

        initialize();

        String[] args;

        args = new String[]{
                executableFile.getAbsolutePath(),
                geometry.getAbsolutePath()};

        Process proc = execute(false, proMeshHomeFolder, args);

        return new ProMeshImpl(proc, proMeshHomeFolder);
    }

    public static ProMeshImpl startUI() {

        initialize();

        String[] args;

        args = new String[]{
                executableFile.getAbsolutePath()
        };

        Process proc = execute(false, proMeshHomeFolder, args);

        return new ProMeshImpl(proc, proMeshHomeFolder);
    }


    @Override
    public File getWorkingDirectory() {
        return wd;
    }

    /**
     * Calls ProMesh with the specified arguments.
     *
     * @param arguments arguments
     * @param wd working directory (currently ignored)
     * @param waitFor indicates whether to wait for process execution
     * @return ProMesh process
     */
    public static Process execute(boolean waitFor, File wd, String... arguments) {

        initialize();

        if (arguments == null || arguments.length == 0) {
            arguments = new String[]{"-help"};
        }

        String[] cmd = new String[arguments.length + 1];

        System.out.println("path: " + executableFile.getAbsolutePath());

        cmd[0] = executableFile.getAbsolutePath();

        for (int i = 1; i < cmd.length; i++) {
            cmd[i] = arguments[i - 1];
        }

        Process proc = null;

        try {
            if(wd==null) {
                proc = Runtime.getRuntime().exec(cmd);
            } else {
                proc = Runtime.getRuntime().exec(cmd, null, wd);
            }
            if (waitFor) {
                proc.waitFor();
            }
        } catch (IOException | InterruptedException ex) {
            throw new RuntimeException("Error while executing ProMesh", ex);
        }

        return proc;
    }

    @Override
    public Process getProcess() {
        return promeshProcess;
    }

    /**
     * Destroys the currently running tcc process.
     */
    @Override
    public void destroy() {
        if (promeshProcess != null) {
            promeshProcess.destroy();
        }
    }

    /**
     * Returns the path to the ProMesh executable. If the executable has not
     * been initialized this will be done as well.
     *
     * @return the path to the ProMesh executable
     */
    private static File getExecutablePath(Path dir) {

        if (!VSysUtil.isOsSupported()) {
            throw new UnsupportedOperationException(
                    "The current OS is not supported: "
                            + System.getProperty("os.name"));
        }

        if (executableFile == null || !executableFile.isFile()) {

        }

        return executableFile;
    }


    /**
     * Saves the specified stream to file.
     *
     * @param in stream to save
     * @param f destination file
     * @throws IOException
     */
    public static void saveStreamToFile(InputStream in, File f) throws IOException {
        IOUtil.saveStreamToFile(in, f);
    }

    public static File getInstallationFolder() {
        initialize();

        return proMeshHomeFolder;
    }

    public static String getVersionString() {
        initialize();

        return promeshVersion;
    }

    public static File newTmpFile(String ending) {
        try {
            if(!ending.startsWith(".")) {
                ending = "." + ending;
            }
            return Files.createTempFile("promesh-arg-file",ending).toFile();
        } catch (IOException e) {
            throw new RuntimeException("Cannot create tmp file",e);
        }
    }
}
// based on http://stackoverflow.com/questions/14165517/processbuilder-forwarding-stdout-and-stderr-of-started-processes-without-blocki

class StreamGobbler extends Thread {

    private  volatile InputStream is;
    private  volatile String prefix;
    private  volatile PrintStream pw;

    StreamGobbler(PrintStream pw, InputStream is, String prefix) {
        this.is = is;
        this.prefix = prefix;
        this.pw = pw;
    }

    @Override
    public void run() {
        try {
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                pw.println(prefix + line);
            }
        } catch (IOException ioe) {
            ioe.printStackTrace(System.err);
        }
    }

}