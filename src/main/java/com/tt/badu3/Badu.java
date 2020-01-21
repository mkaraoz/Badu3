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

/**
 * Proje idea ile geliştirilmiştir.
 * <p>
 * Log dosyasının düzgün oluşturulabilmesi için alttaki parametre Run->Edit Configurations.. -> VM Options
 * kutusuna eklenmelidir.
 * -Djava.util.logging.SimpleFormatter.format="%1$tc %2$s%n%4$s: %5$s%6$s%n"
 * <p>
 * Komut satırından gelen parametreleri simüle etmek için Run->Edit Configurations.. -> Program Arguments
 * kutusuna alttaki parametre eklenmelidir.
 * -d C:\Users\TT\Desktop\b3-rc\raporlar\pika
 */
package com.tt.badu3;

import com.tt.badu3.core.reporter.BaseReporter;
import com.tt.badu3.core.reporter.ReporterFactory;
import com.tt.badu3.core.data.ReportType;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import static com.tt.badu3.core.data.ReportType.MUSTERI;
import static com.tt.badu3.core.data.ReportType.TT;

/**
 * @author mk
 */
public class Badu implements ExceptionHandler.ExceptionCallback
{
    private static String OUT_PATH;

    public static void main(String[] args)
    {
        Badu badu = new Badu();
        badu.init(args[0], args[1]);
    }

    private void init(String target, String path)
    {
        ExceptionHandler.registerExceptionHandler(this);
        try {
            ReportType reportType;
            if (target.equals("-i")) {
                reportType = TT;
            }
            else { // -d or anything
                reportType = MUSTERI;
            }

            OUT_PATH = path;

            Badu.log().info("Rapor oluşturma işlemi başladı.");
            Badu.log().info("Rapor tipi: " + target);
            Badu.log().info("Base path: " + OUT_PATH);

            BaseReporter reporter = ReporterFactory.getReporter(reportType);
            reporter.createReport(OUT_PATH);
        }
        catch (Exception ex) {
            call(ex);
        }
    }

    public static Logger log()
    {
        return Log.getLogger();
    }

    @Override
    public void call(Throwable t)
    {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        String sStackTrace = sw.toString(); // stack trace as a string
        Badu.log().log(Level.SEVERE, sStackTrace);
    }

    private static class Log
    {
        private static final Logger logger = Logger.getLogger("Badu3");

        static {
            try {
                FileHandler fh = new FileHandler(OUT_PATH + "/badu.log");
                logger.addHandler(fh);
                SimpleFormatter formatter = new SimpleFormatter();
                fh.setFormatter(formatter);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

        private Log()
        {
        }

        private static Logger getLogger()
        {
            return logger;
        }
    }
}
