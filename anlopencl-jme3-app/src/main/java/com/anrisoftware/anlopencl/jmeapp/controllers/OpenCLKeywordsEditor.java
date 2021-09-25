package com.anrisoftware.anlopencl.jmeapp.controllers;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.reactfx.Subscription;

import javafx.concurrent.Task;

/**
 * Copy from <code>JavaKeywordsAsyncDemo.java</code> with keywords matching the
 * OpenCL language.
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
public class OpenCLKeywordsEditor {

    private static final String[] KEYWORDS = new String[] { "#define", "#elif", "#else", "#endif", "#error", "#if",
            "#ifdef", "#ifndef", "#include", "#include_next", "#import", "#line", "#pragma", "#undef", "__asm",
            "__based", "__cdecl", "__declspec", "__except", "__far", "__fastcall", "__finally", "__fortran", "__huge",
            "__inline", "__int16", "__int32", "__int64", "__int8", "__interrupt", "__leave", "__loadds", "__near",
            "__pascal", "__saveregs", "__segment", "__segname", "__self", "__stdcall", "__try", "__uuidof", "alloc",
            "auto", "autorelease", "bool", "break", "case", "char", "const", "continue", "default", "defined", "do",
            "double", "else", "enum", "extern", "float", "for", "goto", "id", "if", "init", "int", "long", "register",
            "release", "retain", "return", "short", "signed", "sizeof", "static", "struct", "switch", "typedef",
            "union", "unsigned", "void", "volatile", "while", "__multiple_inheritance", "__single_inheritance",
            "__virtual_inheritance", "catch", "class", "const_cast", "delete", "dynamic_cast", "explicit", "export",
            "false", "friend", "inline", "mutable", "namespace", "new", "operator", "private", "protected", "public",
            "reinterpret_cast", "static_cast", "template", "this", "throw", "true", "try", "typeid", "typename",
            "using", "virtual", "wchar_t", "dllexport", "dllimport", "naked", "thread", "uuid", "kernel", "global",
            "local" };

    private static final String KEYWORD_PATTERN = "\\b(" + String.join("|", KEYWORDS) + ")\\b";
    private static final String PREP_PATTERN = "\\#[^\\n]*|";
    private static final String PAREN_PATTERN = "\\(|\\)";
    private static final String BRACE_PATTERN = "\\{|\\}";
    private static final String BRACKET_PATTERN = "\\[|\\]";
    private static final String SEMICOLON_PATTERN = "\\;";
    private static final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";
    private static final String COMMENT_PATTERN = "//[^\n]*" + "|" + "/\\*(.|\\R)*?\\*/";

    private static final Pattern PATTERN = Pattern.compile( //
            "(?<KEYWORD>" + KEYWORD_PATTERN + ")" //
                    + "|(?<PREP>" + PREP_PATTERN + ")" //
                    + "|(?<PAREN>" + PAREN_PATTERN + ")" //
                    + "|(?<BRACE>" + BRACE_PATTERN + ")" //
                    + "|(?<BRACKET>" + BRACKET_PATTERN + ")" //
                    + "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")" //
                    + "|(?<STRING>" + STRING_PATTERN + ")" //
                    + "|(?<COMMENT>" + COMMENT_PATTERN + ")" //
    );

    private static final String sampleCode = "#include <noise_gen.h>\n" + "#include <kernel.h>\n" + "\n"
            + "kernel void value_noise2D_noInterp(\n" + "global vector2 *input,\n" + "global REAL *output\n" + ") {\n"
            + "    int id0 = get_global_id(0);\n" + "    output[id0] = value_noise2D(input[id0], 200, noInterp);\n"
            + "}\n" + "";

    private CodeArea codeArea;

    private ExecutorService executor;

    public OpenCLKeywordsEditor() {
        executor = Executors.newFixedThreadPool(1, new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread t = Executors.defaultThreadFactory().newThread(r);
                t.setDaemon(true);
                return t;
            }
        });
        codeArea = new CodeArea();
        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
        @SuppressWarnings("unused")
        Subscription cleanupWhenDone = codeArea.multiPlainChanges().successionEnds(Duration.ofMillis(500))
                .supplyTask(this::computeHighlightingAsync).awaitLatest(codeArea.multiPlainChanges()).filterMap(t -> {
                    if (t.isSuccess()) {
                        return Optional.of(t.get());
                    } else {
                        t.getFailure().printStackTrace();
                        return Optional.empty();
                    }
                }).subscribe(this::applyHighlighting);

        // call when no longer need it: `cleanupWhenFinished.unsubscribe();`
        codeArea.replaceText(0, 0, sampleCode);
    }

    public CodeArea getCodeArea() {
        return codeArea;
    }

    private Task<StyleSpans<Collection<String>>> computeHighlightingAsync() {
        String text = codeArea.getText();
        Task<StyleSpans<Collection<String>>> task = new Task<>() {
            @Override
            protected StyleSpans<Collection<String>> call() throws Exception {
                return computeHighlighting(text);
            }
        };
        executor.execute(task);
        return task;
    }

    private void applyHighlighting(StyleSpans<Collection<String>> highlighting) {
        codeArea.setStyleSpans(0, highlighting);
    }

    private static StyleSpans<Collection<String>> computeHighlighting(String text) {
        Matcher matcher = PATTERN.matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
        while (matcher.find()) {
            String styleClass = matcher.group("KEYWORD") != null ? "keyword"
                    : matcher.group("PAREN") != null ? "paren"
                            : matcher.group("BRACE") != null ? "brace"
                                    : matcher.group("BRACKET") != null ? "bracket"
                                            : matcher.group("SEMICOLON") != null ? "semicolon"
                                                    : matcher.group("STRING") != null ? "string"
                                                            : matcher.group("COMMENT") != null ? "comment"
                                                                    : matcher.group("PREP") != null ? "prep" : null;
            /* never happens */ assert styleClass != null;
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }

}
