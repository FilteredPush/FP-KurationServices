/**  NamedContext.java
 *
 * Copyright 2016 President and Fellows of Harvard College
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

package org.filteredpush.kuration.provenance;

import java.util.Arrays;
import java.util.List;

public class NamedContext {
    private String context;

    public NamedContext(String context) {
        this.context = context;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NamedContext that = (NamedContext) o;

        return context != null ? context.equals(that.context) : that.context == null;

    }

    @Override
    public int hashCode() {
        return context != null ? context.hashCode() : 0;
    }
}
