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
package com.tt.badu3.core.reporter;

import com.tt.badu3.core.data.ReportType;

/**
 * @author mk
 */
public class ReporterFactory
{
    public static BaseReporter getReporter(ReportType rt)
    {
        switch (rt)
        {
            case TT:
                return new TtReporter();
            case MUSTERI:
                return new CustomerReporter();
            default:
                throw new RuntimeException("Ben bu rapor tipini tanımıyorum.");
        }
    }
}
