/*
 * Copyright 2019 mk.
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
 */
package com.tt.badu3.core.data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author mk
 */
class SeveritySorter {

    public static void stupidSort(List<Vulnerability> vulnerabilities)
    {
        List<Vulnerability> sorted = new ArrayList<>();
        for (Vulnerability vuln : vulnerabilities)
        {
            if (vuln.getSeverity().equals("Acil"))
                sorted.add(vuln);
        }

        for (Vulnerability vuln : vulnerabilities)
        {
            if (vuln.getSeverity().equals("Kritik"))
                sorted.add(vuln);
        }

        for (Vulnerability vuln : vulnerabilities)
        {
            if (vuln.getSeverity().equals("Yüksek"))
                sorted.add(vuln);
        }

        for (Vulnerability vuln : vulnerabilities)
        {
            if (vuln.getSeverity().equals("Orta"))
                sorted.add(vuln);
        }

        for (Vulnerability vuln : vulnerabilities)
        {
            if (vuln.getSeverity().equals("Düşük"))
                sorted.add(vuln);
        }

        vulnerabilities.clear();
        vulnerabilities.addAll(sorted);
    }
}
