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
package com.tt.badu3.core.chart;

import com.tt.badu3.core.data.Severity;
import com.tt.badu3.core.data.TTColor;
import com.tt.badu3.core.data.Vulnerability;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.ui.RectangleInsets;
import org.jfree.data.general.DefaultPieDataset;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author mk
 */
public class Charter {

    private final File rootFolder;

    private Charter(File rootFolder) {
        this.rootFolder = rootFolder;
    }

    public static Charter getInstance(File rootFolder) {
        return new Charter(rootFolder);
    }

    public String createChart(List<Vulnerability> vulnerabilities, String chartName) throws IOException {

        Map<String, Integer> map = new TreeMap<>();
        for (Vulnerability v : vulnerabilities) {
            String severity = v.getSeverity();
            if (map.containsKey(severity)) {
                map.put(severity, map.get(severity) + 1);
            } else {
                map.put(severity, 1);
            }
        }

        DefaultPieDataset vulnDataSet = fillDataset(map);

        JFreeChart chart = ChartFactory.createPieChart3D(
                "Bulgu Özeti", // chart title
                vulnDataSet, // data
                true, // include legend
                true,
                false);
        chart.getTitle().setMargin(10, 10, 10, 10);

        // sets the label format
        // https://stackoverflow.com/questions/17501750/jfreechart-customize-piechart-to-show-absolute-values-and-percentages
        PieSectionLabelGenerator labelGenerator = new StandardPieSectionLabelGenerator(
                "{0}: {1} ({2})", new DecimalFormat("0"), new DecimalFormat("0%"));

        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setLabelGenerator(labelGenerator);
        plot.setLegendLabelToolTipGenerator(
                new StandardPieSectionLabelGenerator(
                        "Tooltip for legend item {0}"));
        plot.setSimpleLabels(true);
        plot.setInteriorGap(0.0);

        chart.getPlot().setBackgroundPaint(java.awt.Color.WHITE);
        chart.getPlot().setOutlinePaint(null);
        chart.setBorderVisible(false);
        chart.getLegend().setMargin(10, 10, 10, 10);
        chart.setPadding(new RectangleInsets(10, 10, 10, 10));

        plot.setSectionPaint(Severity.ACIL, java.awt.Color.decode("#" + TTColor.ACIL));
        plot.setSectionPaint(Severity.KRITIK, java.awt.Color.decode("#" + TTColor.KRITIK));
        plot.setSectionPaint(Severity.YUKSEK, java.awt.Color.decode("#" + TTColor.YUKSEK));
        plot.setSectionPaint(Severity.ORTA, java.awt.Color.decode("#" + TTColor.ORTA));
        plot.setSectionPaint(Severity.DUSUK, java.awt.Color.decode("#" + TTColor.DUSUK));

        return saveChart2File(chart, chartName);
    }

    private String saveChart2File(JFreeChart chart, String chartName) throws IOException {
        final String FILE_NAME = rootFolder.getAbsolutePath() + "/" + chartName + ".png";

        int width = 800;
        /* Width of the image */
        int height = 600;
        /* Height of the image */
        File pieChartPng = new File(FILE_NAME);

        ChartUtils.saveChartAsPNG(pieChartPng, chart, width, height);

        return pieChartPng.getAbsolutePath();
    }

    private DefaultPieDataset fillDataset(Map<String, Integer> map) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            dataset.setValue(entry.getKey(), entry.getValue());
        }
        return dataset;
    }
}
