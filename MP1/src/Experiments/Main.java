package Experiments;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

import org.knowm.xchart.*;
import org.knowm.xchart.BitmapEncoder.BitmapFormat;
import org.knowm.xchart.XYSeries.XYSeriesRenderStyle;
import org.knowm.xchart.style.Styler.LegendPosition;

import Simulator.*;

public class Main {
    public static void main(String[] args) throws Exception {
        E1();
        E2();
        E3();
        E4();
    }

    private static void E1() throws Exception{

        final var chart = new XYChartBuilder().width(600).height(400).title("Experiment 1").xAxisTitle("Log_2(Size)").yAxisTitle("L1 Miss Rate").build();

        chart.getStyler().setLegendPosition(LegendPosition.InsideNE);
        chart.getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Line);
    
        Arguments arguments = new Arguments();
        arguments.TraceFile = new File("traces\\" + "gcc_trace.txt");
        arguments.Operations = TraceFileReader.GetOperations(arguments);

        double[] cacheSizes = new double[11];
        double[] X = new double[11];
        for(int i = 0; i < cacheSizes.length; i++) {
            cacheSizes[i] = Math.pow(2, i); 
            X[i] = Helpers.Log2(cacheSizes[i]*1024);
        }
 
        for(int i = 0; i < 5; i++) {
            double [] Y = new double[cacheSizes.length];
            int associativity = (int)cacheSizes[i];
            for(int j = 0; j < cacheSizes.length; j++) {
                int size = (int)cacheSizes[j]*1024;
                arguments.Load(32, size, associativity, 0, 0, 0, 0).Run(true);
                Y[j] = arguments.Cache.GetMissRate();
            }

            chart.addSeries((int)Math.pow(2, i) + "-way", X, Y);
        }

        PrintData("Experiment_1", chart);

        SaveFile(chart, "Experiment_1");
    }

    private static void SaveFile(XYChart chart, String name) throws IOException {
        //new SwingWrapper(chart).displayChart();
        BitmapEncoder.saveBitmap(chart, "./" + name, BitmapFormat.PNG);
    }

    private static void PrintData(String name, XYChart chart) {
        System.out.println(name);
        for(XYSeries serie : chart.getSeriesMap().values()) {
            System.out.println(serie.getName());
            double[] x = serie.getXData();
            double[] y = serie.getYData();

            for(var i = 0; i < x.length; i++) {
                System.out.println("x: " + x[i] + ", y: " + y[i]);
            }
        }
    }

    private static void E2() throws Exception {
        final XYChart chart = new XYChartBuilder().width(600).height(400).title("Experiment 2").xAxisTitle("Log_2(Size)").yAxisTitle("AAT").build();

        chart.getStyler().setLegendPosition(LegendPosition.InsideNE);
        chart.getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Line);

        Arguments arguments = new Arguments();
        arguments.TraceFile = new File("traces\\" + "gcc_trace.txt");
        arguments.Operations = TraceFileReader.GetOperations(arguments);

        double[] cacheSizes = new double[11];
        double[] X = new double[11];
        for(int i = 0; i < cacheSizes.length; i++) {
            cacheSizes[i] = Math.pow(2, i); 
            X[i] = Helpers.Log2(cacheSizes[i]*1024);
        }
 
        for(int i = 0; i < 5; i++) {
            double [] Y = new double[cacheSizes.length];
            
            int associativity = (int)cacheSizes[i];
            for(int j = 0; j < cacheSizes.length; j++) {
                int size = (int)cacheSizes[j]*1024;
                if(associativity == 8 && size == 1024) {
                    continue;
                }
                arguments.Load(32, size, associativity, 0, 0, 0, 0).Run(true);
                double at = GetAccessTime(32, size, associativity);
                Y[j] = arguments.Cache.GetAAT(at);
            }

            chart.addSeries((int)Math.pow(2, i) + "-way", X, Y);
        }

        PrintData("Experiment_2", chart);

        SaveFile(chart, "Experiment_2");
    }

    private static void E3() throws Exception {
        final XYChart chart = new XYChartBuilder().width(600).height(400).title("Experiment 3").xAxisTitle("Log_2(Size)").yAxisTitle("AAT").build();

        chart.getStyler().setLegendPosition(LegendPosition.InsideNE);
        chart.getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Line);

        Arguments arguments = new Arguments();
        arguments.TraceFile = new File("traces\\" + "gcc_trace.txt");
        arguments.Operations = TraceFileReader.GetOperations(arguments);

        double[] cacheSizes = new double[9];
        double[] X = new double[9];
        for(int i = 0; i < cacheSizes.length; i++) {
            cacheSizes[i] = Math.pow(2, i); 
            X[i] = Helpers.Log2(cacheSizes[i]*1024);
        }
 
        for(int i = 0; i < 3; i++) {
            double [] Y = new double[cacheSizes.length];
            
            int associativity = 4;
            for(int j = 0; j < cacheSizes.length; j++) {
                int size = (int)cacheSizes[j]*1024;
                arguments.Load(32, size, associativity, 0, 0, i, 0).Run(true);
                double at = GetAccessTime(32, size, associativity);
                Y[j] = arguments.Cache.GetAAT(at);
            }

            chart.addSeries(ReplacementPolicyType.FromInteger(i).toString(), X, Y);
        }

        PrintData("Experiment_3", chart);

        SaveFile(chart, "Experiment_3");
    }

    private static void E4() throws Exception {
        final XYChart chart = new XYChartBuilder().width(600).height(400).title("Experiment 4").xAxisTitle("Log_2(Size)").yAxisTitle("AAT").build();

        chart.getStyler().setLegendPosition(LegendPosition.InsideNE);
        chart.getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Line);

        Arguments arguments = new Arguments();
        arguments.TraceFile = new File("traces\\" + "gcc_trace.txt");
        arguments.Operations = TraceFileReader.GetOperations(arguments);

        double[] cacheSizes = new double[6];
        double[] X = new double[6];
        for(int i = 0; i < cacheSizes.length; i++) {
            cacheSizes[i] = Math.pow(2, i+1); 
            X[i] = Helpers.Log2(cacheSizes[i]*1024);
        }

        for(int i = 0; i < 2; i++) {
            double [] Y = new double[cacheSizes.length];
            for(int j = 0; j < cacheSizes.length; j++) {
                int cacheSize = (int)cacheSizes[j]*1024;
                arguments.Load(32, 1024, 4, cacheSize, 8, 0, i).Run(true);
                double at = GetAccessTime(32, cacheSize, 8);
                Y[j] = arguments.Cache.GetAAT(at);
            }

            chart.addSeries(InclusionProperty.FromInteger(i).toString(), X, Y);
        }

        PrintData("Experiment_4", chart);

        SaveFile(chart, "Experiment_4");
    }

    private static double GetAccessTime(int blockSize, int size, int associativity) throws Exception {
        BufferedReader br = null;
        Scanner sc = null;

        try {
            br = new BufferedReader(new FileReader(new File("traces\\cacti_table.csv")), 1000 * 8192);
            String thisLine;
            int index = 0;
            while ((thisLine = br.readLine()) != null) {

                if (thisLine == null || thisLine.isEmpty() || thisLine.length() < 1 || index == 0)  
                {
                    index++;
                    continue;
                }

                sc = new Scanner(thisLine);
                sc.useDelimiter(",");

                int counter = 0;

                int cacheSizeFound = 0, blockSizeFound = 0;

                String associativityFound="";

                double accessTime = 1;
                
                while (sc.hasNext()) {
                    
                    switch(counter) {
                        case 0:
                        cacheSizeFound = Integer.parseUnsignedInt(sc.next());
                        break;
                        case 2:
                        blockSizeFound = Integer.parseUnsignedInt(sc.next());
                        break;
                        case 3:
                        associativityFound = sc.next();
                        break;
                        case 4:
                        accessTime = Double.parseDouble(sc.next());
                        break;
                        default:
                        sc.next();
                        break;
                    }

                    if(counter >= 4) {
                        break;
                    }

                    counter++;
                }

                if(associativityFound.contains("FA")) {
                    continue;
                }

                if(Integer.parseInt(associativityFound) == associativity && size == cacheSizeFound && blockSize == blockSizeFound) {
                    return accessTime;
                } 
            }

            return 1;
        } catch (Exception exception) {
            System.out.println("Something happened while reading the trace file");
            exception.printStackTrace();
            throw exception;
        } finally {
            if (br != null) br.close();          
            if (sc != null) sc.close();
        }
    }
}
