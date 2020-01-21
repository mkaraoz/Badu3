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

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.tt.badu3.Badu;
import com.tt.badu3.core.chart.Charter;
import com.tt.badu3.core.data.JsonParser;
import com.tt.badu3.core.data.Metadata;
import com.tt.badu3.core.data.Vulnerability;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.model.XWPFHeaderFooterPolicy;
import org.apache.poi.xwpf.usermodel.UnderlinePatterns;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFHeader;
import org.apache.poi.xwpf.usermodel.XWPFHyperlinkRun;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.apache.xmlbeans.XmlException;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRow;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;

/**
 * @author mk
 */
public abstract class BaseReporter
{
    private static final int DOKUMAN_DETAY_TABLOSU = 0;
    private static final int BULGU_OZETI_TABLOSU = 2;
    private static final int KAPSAM_TABLOSU = 3;
    private static final int TEST_EKIBI_TABLOSU = 4;

    String OUT_PATH;
    private Metadata metadata;
    List<Vulnerability> vulnerabilities;
    XWPFDocument document;
    private XWPFParagraph graphParagraph;

    public abstract void createReport(String path) throws IOException;

    void parseJsonFiles() throws FileNotFoundException
    {
        Gson gson = new Gson();
        JsonObject metadataJson = gson.fromJson(new FileReader(OUT_PATH + "\\metadata.json"), JsonObject.class);
        JsonObject vulnsJson = gson.fromJson(new FileReader(OUT_PATH + "\\vulns.json"), JsonObject.class);

        Badu.log().info("Metada ve Vulnerability json dosyaları yazıldı.");
        Badu.log().info("Metada: " + OUT_PATH + "\\metadata.json");
        Badu.log().info("Vulns: " + OUT_PATH + "\\vulns.json");

        metadata = JsonParser.parseMetadata(metadataJson);
        vulnerabilities = JsonParser.parseVulnerabilities(vulnsJson);
    }

    void writeProjectInfo()
    {
        for (XWPFParagraph p : document.getParagraphs()) {
            List<XWPFRun> runs = p.getRuns();
            if (runs != null) {
                for (XWPFRun r : runs) {
                    String text = r.getText(0);
                    if (text != null && text.contains("#FIRMA")) {
                        text = text.replace("#FIRMA", metadata.getProjectName());
                        r.setText(text, 0);
                        Badu.log().info("#FIRMA bilgisi güncellendi.");
                    }

                    if (text != null && text.contains("#START#")) {
                        text = text.replace("#START#", metadata.getStartDate());
                        r.setText(text, 0);
                        Badu.log().info("#START# -> Teste başlama tarihi güncellendi.");
                    }

                    if (text != null && text.contains("#END#")) {
                        text = text.replace("#END#", metadata.getEndDate());
                        r.setText(text, 0);
                        Badu.log().info("#END# -> Test bitiş tarihi güncellendi.");
                    }

                    if (text != null && text.contains("#21KUTU")) {
                        text = text.replace("#21KUTU", metadata.getMethod());
                        r.setText(text, 0);
                        Badu.log().info("#21KUTU -> Test yöntemi (beyaz/kara kutu) güncellendi.");
                    }

                    if (text != null && text.contains("#HIZMET-TURU#")) {
                        text = text.replace("#HIZMET-TURU#", metadata.getServiceType());
                        r.setText(text, 0);
                        Badu.log().info("#HIZMET-TURU# -> alanı güncellendi.");
                    }

                    if (text != null && text.contains("#BULGU_PIE_CHART")) {
                        graphParagraph = p;
                    }
                }
            }
        }
        Badu.log().info("Meta-data alanları yazıldı.");
    }

    void handleHeaders()
    {
        XWPFHeaderFooterPolicy headerPolicy = new XWPFHeaderFooterPolicy(document);
        // get header object
        XWPFHeader header = headerPolicy.getDefaultHeader();
        // get table object
        XWPFTable table = header.getTables().get(0);
        // set customer name in header
        XWPFParagraph topRightParagraph = table.getRow(0).getCell(1).getParagraphs().get(0);
        topRightParagraph.getRuns().get(0).setText(metadata.getHeaderName() + " Güvenlik Test Raporu", 0);
        Badu.log().info("Tüm sayfalar için header tablosu 1. satır (Güvenlik Test Raporu) güncellendi");
        // set date on header
        String date = DateUtil.getCurrentDate();
        XWPFParagraph midRightParagraph = table.getRow(1).getCell(1).getParagraphs().get(0);
        midRightParagraph.getRuns().get(0).setText(date, 0);
        Badu.log().info("Tüm sayfalar için header tablosu 2. satır (Rapor tarihi) güncellendi");

        Badu.log().info("Header tablosu güncellemesi tamamlandı");
    }

    void fillTableDokumanDetaylari()
    {
        XWPFTable table = getTable(DOKUMAN_DETAY_TABLOSU);
        table.getRow(0).getCell(1).setText(metadata.getDocTable_reportedBy());
        table.getRow(1).getCell(1).setText(metadata.getDocTable_approvedBy());
        table.getRow(2).getCell(1).setText(metadata.getDocTable_customerRepresentative());
        table.getRow(3).getCell(1).setText(metadata.getDocTable_releaseNo());
        table.getRow(4).getCell(1).setText(metadata.getDocTable_releaseDate());

        Badu.log().info("Doküman detayları tablosu yazıldı.");
    }

    void drawGraph()
    {
        try {
            Badu.log().info("Grafik çizme işlemi başladı...");
            XWPFRun rChartImageR = graphParagraph.getRuns().get(0);
            rChartImageR.setText("", 0);
            String chartFilePath = Charter.getInstance(new File(OUT_PATH)).createChart(vulnerabilities, metadata.getProjectName());
            Path chartPath = Paths.get(chartFilePath);
            rChartImageR.addPicture(Files.newInputStream(chartPath), XWPFDocument.PICTURE_TYPE_PNG, chartPath.getFileName().toString(), Units.toEMU(400), Units.toEMU(300));
            Badu.log().info("Grafik dosya yolu: " + chartPath.getFileName().toString());

            Badu.log().info("Grafik çizildi.");
        }
        catch (IOException | InvalidFormatException ex) {
            Badu.log().log(Level.SEVERE, "Grafik çizme işlemi sırasında hata oluştu", ex);
        }
    }

    void fillTableBulguOzeti()
    {
        // get the empty table
        Badu.log().info("Bulgu özeti yazılıyor.");
        Badu.log().info("Bulgu özeti tablosu: " + BULGU_OZETI_TABLOSU);
        XWPFTable table = getTable(BULGU_OZETI_TABLOSU);
        //the row ID of the empty row (template row)
        int tempateRowId = 0;
        // the empty row
        XWPFTableRow rowTemplate = table.getRow(tempateRowId);

        for (Vulnerability v : vulnerabilities) {
            CTRow ctrow = null;
            try {
                ctrow = CTRow.Factory.parse(rowTemplate.getCtRow().newInputStream());
            }
            catch (XmlException | IOException ex) {
                Badu.log().log(Level.SEVERE, "Bulgu özeti yazılırken hata oluştu", ex);
            }
            XWPFTableRow newRow = new XWPFTableRow(ctrow, table);
            newRow.getCell(0).setText(v.getTitle());
            newRow.getCell(1).setText(v.getSeverity());
            table.addRow(newRow);
        }

        table.removeRow(0);

        Badu.log().info("Bulgu özeti yazıldı.");
    }

    void fillTableKapsam()
    {
        Badu.log().info("Kapsam tablosu yazılıyor.");
        Badu.log().info("Kapsam tablosu: " + KAPSAM_TABLOSU);
        XWPFTable table = getTable(KAPSAM_TABLOSU);
        //the row ID of the empty row (template row)
        int tempateRowId = 0;
        // the empty row
        XWPFTableRow rowTemplate = table.getRow(tempateRowId);

        List<String> scope = metadata.getScopeList();

        // test type
        rowTemplate.getCell(0).setText(metadata.getServiceType());
        XWPFTableCell nameCell = rowTemplate.getCell(1);
        XWPFParagraph paragraph = nameCell.getParagraphs().get(0);
        paragraph.insertNewRun(0);
        XWPFRun run = paragraph.getRuns().get(0);
        int count = scope.size();
        for (int i = 0; i < count; i++) {
            run.setText(scope.get(i));
            Badu.log().info("Kapsam: " + scope.get(i));
            if (i < count - 1) {
                run.addBreak();
            }
        }

        Badu.log().info("Kapsam tablosu yazıldı.");
    }

    void fillTableTestEkibi()
    {
        Badu.log().info("Test ekibi tablosu yazılıyor.");
        Badu.log().info("Test ekibi tablosu: " + TEST_EKIBI_TABLOSU);

        XWPFTable table = getTable(TEST_EKIBI_TABLOSU);
        //the row ID of the empty row (template row)
        int tempateRowId = 0;
        // the empty row
        XWPFTableRow rowTemplate = table.getRow(tempateRowId);

        for (String tester : metadata.getTestTeam()) {
            CTRow ctrow = null;
            try {
                ctrow = CTRow.Factory.parse(rowTemplate.getCtRow().newInputStream());
            }
            catch (XmlException | IOException ex) {
                Badu.log().log(Level.SEVERE, "Test ekibi tabloya yazılırken hata oluştu", ex);
            }
            XWPFTableRow newRow = new XWPFTableRow(ctrow, table);
            Badu.log().info("Test Kaynağı: " + tester);
            newRow.getCell(0).setText(tester);
            newRow.getCell(1).setText("Güvenlik Test Uzmanı");
            table.addRow(newRow);
        }

        table.removeRow(0);

        Badu.log().info("Test ekibi tablosu yazıldı.");
    }

    void createDoc() throws IOException
    {
        //Write the Document in file system
        FileOutputStream out = new FileOutputStream(new File(OUT_PATH + "/tt_" + metadata.getProjectName() + "_rapor.docx"));
        document.write(out);
        Badu.log().info(metadata.getProjectName() + " uygulaması sızma testi raporu başarı ile üretildi.");
        System.out.println(metadata.getProjectName() + " uygulaması sızma testi raporu başarı ile üretildi.");
    }

    XWPFTable getTable(int index)
    {
        return document.getTables().get(index);
    }

    XWPFHyperlinkRun createHyperLinkRun(XWPFParagraph p, String reference)
    {
        if (reference.startsWith("[")) {
            int splitPoint = reference.indexOf("]");
            String left = reference.substring(0, splitPoint + 1).trim();
            left = left.substring(1, left.length() - 1);
            String right = reference.substring(splitPoint + 1).trim();
            XWPFHyperlinkRun hyperlinkRun = p.createHyperlinkRun(right);
            hyperlinkRun.setText(left);
            hyperlinkRun.setColor("0000FF");
            hyperlinkRun.setUnderline(UnderlinePatterns.SINGLE);
            return hyperlinkRun;
        }
        else {
            XWPFHyperlinkRun hyperlinkRun = p.createHyperlinkRun(reference);
            hyperlinkRun.setText(reference);
            hyperlinkRun.setColor("0000FF");
            hyperlinkRun.setUnderline(UnderlinePatterns.SINGLE);
            return hyperlinkRun;
        }
    }
}
