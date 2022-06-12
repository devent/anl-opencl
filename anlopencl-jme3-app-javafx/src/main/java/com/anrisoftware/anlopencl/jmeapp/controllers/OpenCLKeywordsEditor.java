/*
 * Copyright (C) 2021-2022 Erwin Müller <erwin@muellerpublic.de>
 * Released as open-source under the Apache License, Version 2.0.
 *
 * ****************************************************************************
 * ANL-OpenCL :: JME3 - App - JavaFX
 * ****************************************************************************
 *
 * Copyright (C) 2021-2022 Erwin Müller <erwin@muellerpublic.de>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ****************************************************************************
 * ANL-OpenCL :: JME3 - App - JavaFX is a derivative work based on Josua Tippetts' C++ library:
 * http://accidentalnoise.sourceforge.net/index.html
 * ****************************************************************************
 *
 * Copyright (C) 2011 Joshua Tippetts
 *
 *   This software is provided 'as-is', without any express or implied
 *   warranty.  In no event will the authors be held liable for any damages
 *   arising from the use of this software.
 *
 *   Permission is granted to anyone to use this software for any purpose,
 *   including commercial applications, and to alter it and redistribute it
 *   freely, subject to the following restrictions:
 *
 *   1. The origin of this software must not be misrepresented; you must not
 *      claim that you wrote the original software. If you use this software
 *      in a product, an acknowledgment in the product documentation would be
 *      appreciated but is not required.
 *   2. Altered source versions must be plainly marked as such, and must not be
 *      misrepresented as being the original software.
 *   3. This notice may not be removed or altered from any source distribution.
 *
 *
 * ****************************************************************************
 * ANL-OpenCL :: JME3 - App - JavaFX bundles and uses the RandomCL library:
 * https://github.com/bstatcomp/RandomCL
 * ****************************************************************************
 *
 * BSD 3-Clause License
 *
 * Copyright (c) 2018, Tadej Ciglarič, Erik Štrumbelj, Rok Češnovar. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
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
    private static final String PREP_PATTERN = "\\#[^\\n]*";
    private static final String PAREN_PATTERN = "\\(|\\)";
    private static final String BRACE_PATTERN = "\\{|\\}";
    private static final String BRACKET_PATTERN = "\\[|\\]";
    private static final String SEMICOLON_PATTERN = "\\;";
    private static final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";
    private static final String COMMENT_PATTERN = "\\/\\/.*|\\/\\*(?:.|\\R)*?\\*\\/";
    private static final String NUMBER_PATTERN = "\\b[0-9]*\\b";
    private static final String VARIABLES_PATTERN = "\\$\\{?[a-zA-Z0-9_]+\\}?";

    private static final Pattern PATTERN = Pattern.compile( //
            "(?<KEYWORD>" + KEYWORD_PATTERN + ")" //
                    + "|(?<PREP>" + PREP_PATTERN + ")" //
                    + "|(?<PAREN>" + PAREN_PATTERN + ")" //
                    + "|(?<BRACE>" + BRACE_PATTERN + ")" //
                    + "|(?<BRACKET>" + BRACKET_PATTERN + ")" //
                    + "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")" //
                    + "|(?<STRING>" + STRING_PATTERN + ")" //
                    + "|(?<NUMBER>" + NUMBER_PATTERN + ")" //
                    + "|(?<COMMENT>" + COMMENT_PATTERN + ")" //
                    + "|(?<VAR>" + VARIABLES_PATTERN + ")" //
    );

    private static final String sampleCode = "#include <noise_gen.h>\n" + "#include <kernel.h>\n" + "\n"
            + "kernel void value_noise2D_noInterp(\n" + "global vector2 *input,\n" + "global REAL *output\n" + ") {\n"
            + "    int id0 = get_global_id(0);\n" + "    printf(\"id=%d\\n\", id0);\n" + "    // a comment\n"
            + "    output[id0] = value_noise2D(input[id0], 200, noInterp);\n" + "}\n" + "";

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
        executor.submit(task);
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
                                                    : matcher.group("NUMBER") != null ? "number"
                                                            : matcher.group("STRING") != null ? "string"
                                                                    : matcher.group("COMMENT") != null ? "comment"
                                                                            : matcher.group("PREP") != null ? "prep"
                                                                                    : matcher.group("VAR") != null
                                                                                            ? "var"
                                                                                    : null;
            /* never happens */ assert styleClass != null;
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }

}
