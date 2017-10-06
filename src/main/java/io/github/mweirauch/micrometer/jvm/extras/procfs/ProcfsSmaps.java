/*
 * Copyright © 2016 Michael Weirauch (michael.weirauch@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.mweirauch.micrometer.jvm.extras.procfs;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

public class ProcfsSmaps extends ProcfsEntry {

    public enum KEY implements ValueKey {
        /**
         * Virtual set size
         */
        VSS,
        /**
         * Resident set size
         */
        RSS,
        /**
         * Proportional set size
         */
        PSS,
        /**
         * Paged out memory
         */
        SWAP,
        /**
         * Paged out memory accounting shared pages. Since Linux 4.3.
         */
        SWAPPSS
    }

    private static final int KILOBYTE = 1024;

    public ProcfsSmaps() {
        super(ProcfsReader.getInstance("smaps"));
    }

    /* default */ ProcfsSmaps(ProcfsReader reader) {
        super(reader);
    }

    @Override
    protected void reset() {
        EnumSet.allOf(KEY.class).forEach(key -> values.put(key, new AtomicLong(-1)));
    }

    @Override
    protected void handle(Collection<String> lines) {
        Objects.requireNonNull(lines);

        for (final String line : lines) {
            if (line.startsWith("Size:")) {
                inc(KEY.VSS, parseKiloBytes(line) * KILOBYTE);
            } else if (line.startsWith("Rss:")) {
                inc(KEY.RSS, parseKiloBytes(line) * KILOBYTE);
            } else if (line.startsWith("Pss:")) {
                inc(KEY.PSS, parseKiloBytes(line) * KILOBYTE);
            } else if (line.startsWith("Swap:")) {
                inc(KEY.SWAP, parseKiloBytes(line) * KILOBYTE);
            } else if (line.startsWith("SwapPss:")) {
                inc(KEY.SWAPPSS, parseKiloBytes(line) * KILOBYTE);
            }
        }
    }

    private static long parseKiloBytes(String line) {
        Objects.requireNonNull(line);

        return Long.parseLong(line.split("\\s+")[1]);
    }

}
