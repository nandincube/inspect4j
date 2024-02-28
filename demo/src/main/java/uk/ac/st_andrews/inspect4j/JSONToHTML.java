package uk.ac.st_andrews.inspect4j;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.nio.file.FileSystems;

import org.python.util.PythonInterpreter;

public class JSONToHTML {
    private String fileName;
    private static final String SEP = FileSystems.getDefault().getSeparator();

    public JSONToHTML(String fileName) {
        this.fileName = fileName;
        createHTML();
    }

    private void createHTML() {
        System.out.println("FN: "+fileName);
        File f = new File("src"+SEP+"main"+SEP+"java"+SEP+"uk"+SEP+"ac"+SEP+"st_andrews"+SEP+"inspect4j"+SEP+"json_converter.py");
        String converter = f.getAbsolutePath().toString();

        PythonInterpreter interpreter = new PythonInterpreter();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        interpreter.setOut(outputStream);
        // Call the Python function defined in the file
        interpreter.exec("import sys");
        interpreter.exec("sys.argv = ['" + fileName + "']");
        interpreter.exec("exec(open().read('"+converter+"'))");

        // Get the captured output
        String pythonOutput = outputStream.toString();

        // Print or use the captured output
        System.out.println("Output of convert_json_to_html.py:");
        System.out.println(pythonOutput);
        interpreter.close();

    }
}
