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

import com.tt.badu3.Badu;
import com.tt.badu3.core.data.Vulnerability;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFHyperlinkRun;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTbl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;

/**
 * @author mk
 */
public class TtReporter extends BaseReporter
{
    private static final String MASTER_DOC_PATH = ".\\base\\masterTT.docx";
    private static final int ZAFIYET_TABLOSU = 7;

    @Override
    public void createReport(String path) throws IOException
    {
        Badu.log().info("MASTER_DOC_PATH: " + MASTER_DOC_PATH);

        OUT_PATH = path;
        parseJsonFiles();

        document = new XWPFDocument(new FileInputStream(MASTER_DOC_PATH));

        writeProjectInfo(); // # ile işaretli alanları dolduruyoruz
        handleHeaders();
        fillTableDokumanDetaylari();
        drawGraph();
        fillTableBulguOzeti();
        fillTableKapsam();
        fillTableTestEkibi();
        writeVulnerabilities();
        createDoc();
    }

    private void writeVulnerabilities()
    {
        final int TITLE_ROW = 0;
        final int ETKI_ROW = 1;
        //final int ERISIM_ROW = 2;
        //final int PROFIL_ROW = 3;
        final int TANIM_ROW = 2;
        final int COZUM_ROW = 4;
        final int EK_ROW = 5;
        final int DATA_COL = 1;

        Badu.log().info("ZAFIYET_TABLOSU: " + ZAFIYET_TABLOSU);
        XWPFTable table = getTable(7);
        int currentIndex = 7;

        for (Vulnerability v : vulnerabilities) {
            CTTbl tbl = document.getDocument().getBody().insertNewTbl(currentIndex);
            tbl.set(table.getCTTbl());
            XWPFTable newTable = new XWPFTable(tbl, document);
            List<XWPFTableRow> rows = newTable.getRows();
            rows.get(TITLE_ROW).getCell(0).setText(v.getTitle());
            Badu.log().info("Zafiyet: " + v.getTitle());
            rows.get(ETKI_ROW).getCell(DATA_COL).setText(v.getEtki());
            //rows.get(ERISIM_ROW).getCell(DATA_COL).setText(v.getAccessPoint());
            //rows.get(PROFIL_ROW).getCell(DATA_COL).setText(v.getProfile());
            rows.get(TANIM_ROW).getCell(DATA_COL).setText(v.getTanim());
            rows.get(COZUM_ROW).getCell(DATA_COL).setText(v.getCozum());

            XWPFTableCell refCell = newTable.getRows().get(EK_ROW).getCell(DATA_COL);
            XWPFParagraph p = refCell.getParagraphs().get(0);

            int count = v.getReferences().size();
            for (int i = 0; i < count; i++) {
                String reference = v.getReferences().get(i);
                XWPFHyperlinkRun hr = createHyperLinkRun(p, reference);
                if (i < count - 1) {
                    hr.addBreak();
                }
            }

            currentIndex++;

            XWPFParagraph pa = newTable.getRows().get(TITLE_ROW).getCell(1).addParagraph();
            XWPFRun run = pa.createRun();
            try {
                run.addPicture(new FileInputStream(new File(v.getImagePath())), XWPFDocument.PICTURE_TYPE_PNG, "img", Units.toEMU(80), Units.toEMU(17));
            }
            catch (InvalidFormatException | IOException ex) {
                Badu.log().log(Level.SEVERE, "Zafiyet tablosu yazılırken hata oluştu", ex);
            }
        }

        XWPFTable table2Del = document.getTables().get(currentIndex - vulnerabilities.size());
        int size = table2Del.getRows().size();
        for (int i = 0; i < size; i++) {
            table2Del.removeRow(0);
        }

        Badu.log().info("Zafiyet tablosu yazıldı.");
    }


}
