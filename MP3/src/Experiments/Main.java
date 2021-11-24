package Experiments;
import Simulator.*;

import java.io.IOException;
import java.util.ArrayList;

import org.knowm.xchart.*;
import org.knowm.xchart.BitmapEncoder.BitmapFormat;
import org.knowm.xchart.XYSeries.XYSeriesRenderStyle;
import org.knowm.xchart.style.Styler.LegendPosition;

public class Main {
    public static void main(String[] args) throws Exception {
        E1();
        E2();
        E3();
    }

    public static void E1() throws Exception {

        Arguments arguments = new Arguments();

        String[] files = new String[] {"gcc", "jpeg", "perl"};

        for(int i = 0; i < 3; i++) {
            String name = files[i] + ", smith";
            final var chart = new XYChartBuilder().width(600).height(400).title(name).xAxisTitle("B").yAxisTitle("Branch Misprediction Rate").build();
            chart.getStyler().setLegendPosition(LegendPosition.InsideNE);
            chart.getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Line);
            ArrayList<Double> X = new ArrayList<>();
            ArrayList<Double> Y = new ArrayList<>();
            for(int j = 1; j <= 6; j++) {
                X.add((double)j);
                arguments.Load(PredictionPolicyType.Smith, j, -1, -1, -1, "traces\\" + files[i] + "_trace.txt")
                         .Run();
                Y.add(arguments.PredictionPolicy.GetMispredictionRate());
            }
            chart.addSeries(name, X, Y);
            PrintData(chart);
            SaveFile(chart, name);
        }
    }

    public static void E2() throws Exception {

        Arguments arguments = new Arguments();

        String[] files = new String[] {"gcc", "jpeg", "perl"};

        for(int i = 0; i < 3; i++) {
            String name = files[i] + ", bimodal";
            final var chart = new XYChartBuilder().width(600).height(400).title(name).xAxisTitle("M").yAxisTitle("Branch Misprediction Rate").build();
            chart.getStyler().setLegendPosition(LegendPosition.InsideNE);
            chart.getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Line);
            ArrayList<Double> X = new ArrayList<>();
            ArrayList<Double> Y = new ArrayList<>();
            for(int j = 7; j <= 12; j++) {
                X.add((double)j);
                arguments.Load(PredictionPolicyType.Bimodal, j, -1, -1, -1, "traces\\" + files[i] + "_trace.txt")
                         .Run();
                Y.add(arguments.PredictionPolicy.GetMispredictionRate());
            }
            chart.addSeries(name, X, Y);
            PrintData(chart);
            SaveFile(chart, name);
        }
    }

    public static void E3() throws Exception {

        Arguments arguments = new Arguments();

        String[] files = new String[] {"gcc", "jpeg", "perl"};

        for(int i = 0; i < 3; i++) {
            String name = files[i] + ", gshare";
            final var chart = new XYChartBuilder().width(600).height(400).title(name).xAxisTitle("M").yAxisTitle("Branch Misprediction Rate").build();
            chart.getStyler().setLegendPosition(LegendPosition.InsideNE);
            chart.getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Line);

            for(int n = 2; n <= 12; n=n+2) {
                ArrayList<Double> X = new ArrayList<>();
                ArrayList<Double> Y = new ArrayList<>();
                int m = 7;

                while(m <=12) {
                    if(n > m) {
                        m++;
                        continue;
                    }
                    X.add((double)m);
                    arguments.Load(PredictionPolicyType.Gshare, m, n, -1, -1, "traces\\" + files[i] + "_trace.txt")
                             .Run();
                    Y.add(arguments.PredictionPolicy.GetMispredictionRate()); 
                    m++;
                }
                chart.addSeries("N: " + n + ", " + name, X, Y);
            }
            PrintData(chart);
            SaveFile(chart, name);
        }
    }


    private static void SaveFile(XYChart chart, String name) throws IOException {
        BitmapEncoder.saveBitmap(chart, "./" + name, BitmapFormat.PNG);
    }

    private static void PrintData(XYChart chart) {
        for(XYSeries serie : chart.getSeriesMap().values()) {
            System.out.println(serie.getName());
            double[] x = serie.getXData();
            double[] y = serie.getYData();

            for(var i = 0; i < x.length; i++) {
                System.out.println("x: " + x[i] + ", y: " + y[i]);
            }
        }
    }
}
