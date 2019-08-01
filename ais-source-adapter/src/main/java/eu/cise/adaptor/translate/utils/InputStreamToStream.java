/*
 * Copyright CISE AIS Adaptor (c) 2018-2019, European Union
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the <organization> nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package eu.cise.adaptor.translate.utils;

import eu.cise.adaptor.DelimiterType;

import java.io.InputStream;
import java.util.Scanner;
import java.util.Spliterator;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static eu.cise.adaptor.DelimiterType.KEEP;
import static eu.cise.adaptor.DelimiterType.STRIP;
import static java.lang.Long.MAX_VALUE;
import static java.util.Spliterator.NONNULL;
import static java.util.Spliterator.ORDERED;
import static java.util.Spliterators.spliterator;

public class InputStreamToStream {

    private static final String DELIMITER_DEFAULT = "\n";
    private final String prefix;
    private final Scanner scanner;
    private final Spliterator<String> split;
    private final AtomicLong count = new AtomicLong();

    public InputStreamToStream(InputStream is) {
        this(is, DELIMITER_DEFAULT, STRIP);
    }

    public InputStreamToStream(InputStream is, String delimiter, DelimiterType type) {
        this.scanner = new Scanner(is, "UTF-8").useDelimiter(delimiter);
        this.split = spliterator(scanner, MAX_VALUE, ORDERED | NONNULL);
        this.prefix = type.equals(KEEP) ? delimiter : "";

    }

    public Stream<String> stream() {
        return StreamSupport.stream(split, false)
            .onClose(scanner::close)
            .peek(m-> count.getAndIncrement())
            .map(m -> prefix + m);
    }
}
