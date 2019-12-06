package sample;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import jdk.jshell.*;
import jdk.jshell.Snippet.Status;
import jdk.jshell.SnippetEvent;
import jdk.jshell.SourceCodeAnalysis;
import java.lang.*;
import java.util.Locale;


/**
 * The backend JShell class supporting the functionalities of the interaction pane plugin.
 * @author Yihe Guo, Yue Shu
 */
class ExampleJShell {
    JShell js = JShell.create();

    public String useJshell(String input) {


        List<SnippetEvent> events = js.eval(input);

        for (SnippetEvent e : events) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream(baos);
            PrintStream oldStdout = System.out;

            System.setOut(ps);
            Snippet s = e.snippet();
            js.diagnostics(s).forEach(
                    d -> System.out.println(d.getMessage(Locale.getDefault()))
            );
            if (baos.size() > 0) {
                System.out.flush();
                System.setOut(oldStdout);
                return baos.toString();
            }

            StringBuilder sb = new StringBuilder();

            if (e.causeSnippet() == null) {
                // We have a snippet creation event
                switch (e.status()) {
                    case VALID:
                        break;
                    case RECOVERABLE_DEFINED:
                        sb.append("With unresolved references ");
                        break;
                    case RECOVERABLE_NOT_DEFINED:
                        sb.append("Possibly reparable, failed ");
                        break;
                    case REJECTED:
                        sb.append("ERROR: print error type and details here");
                        break;
                }
                if (e.previousStatus() == Status.NONEXISTENT) {
                } else {
                }
                if (e.value() != null) {
                    sb.append(e.value());
                }
                System.out.flush();
                System.setOut(oldStdout);
                return sb.toString();

            }
        }
        return "";
    }

    public void evaluate(String stringPath) {
        // Handle snippet events. We can print value or take action if evaluation
        // failed.
        js.onSnippetEvent(snippetEvent -> snippetEventHandler(snippetEvent));
        Path path = Paths.get(stringPath);
        System.out.println(stringPath);
        try {
            String scriptContent = new String(Files.readAllBytes(path));
            String s = scriptContent;
            while (true) {
                // Read source line by line till semicolin
                SourceCodeAnalysis.CompletionInfo an = js.sourceCodeAnalysis().analyzeCompletion(s);
                if (!an.completeness().isComplete()) {
                    break;
                }
                // If there are any method declaration or class declaration in new lines,
                // resolve it
                // otherwise execution errors will be thrown
                js.eval(trimNewlines(an.source()));
                // Exit if there are no more expressions to evaluate. EOF
                if (an.remaining().isEmpty()) {
                    break;
                }
                // If there is semicolon, execute next seq
                s = an.remaining();
            }
        } catch (IOException e) {

        }
    }

    public void snippetEventHandler(SnippetEvent snippetEvent) {
        String value = snippetEvent.value();
        if (value != null && value.trim().length() > 0) {
            // Prints output of code evaluation if the value is not "null"
            if (value.equals("null")) {
                System.out.println();
            } else {
                System.out.println(value);
            }
        }

        // If there are any errors, print and exit
        if (Snippet.Status.REJECTED.equals(snippetEvent.status())) {
            System.out.println("Invalid Statement: " + snippetEvent.snippet().toString()
                    + "\nIgnoring execution of above statement.");
        }
    }

    public String checkType(String inputCode){
        List<SnippetEvent> events = js.eval(inputCode);
        for (SnippetEvent e : events) {
            Snippet sp = e.snippet();
            if (sp.kind() == Snippet.Kind.VAR) {
                return((VarSnippet)sp).typeName();
            }
        }
        return "";
    }

    private String trimNewlines(String s) {
        int b = 0;
        while (b < s.length() && s.charAt(b) == '\n') {
            b++;
        }
        int e = s.length() - 1;
        while (e >= 0 && s.charAt(e) == '\n') {
            e--;
        }
        return s.substring(b, e + 1);
    }


}
