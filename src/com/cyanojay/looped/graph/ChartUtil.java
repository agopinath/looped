/**
 * Copyright (C) 2009, 2010 SC 4ViewSoft SRL
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
package com.cyanojay.looped.graph;

import java.util.Date;
import java.util.List;

import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.BasicStroke;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.graphics.Color;

/**
 * An abstract class for the demo charts to extend. It contains some methods for
 * building datasets and renderers.
 */
public class ChartUtil {

  /**
   * Builds an XY multiple dataset using the provided values.
   * 
   * @param titles the series titles
   * @param xValues the values for the X axis
   * @param yValues the values for the Y axis
   * @return the XY multiple dataset
   */
  public static XYMultipleSeriesDataset buildDataset(String[] titles, List<double[]> xValues,
      List<double[]> yValues) {
    XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
    addXYSeries(dataset, titles, xValues, yValues, 0);
    return dataset;
  }

  public static void addXYSeries(XYMultipleSeriesDataset dataset, String[] titles, List<double[]> xValues,
      List<double[]> yValues, int scale) {
    int length = titles.length;
    for (int i = 0; i < length; i++) {
      XYSeries series = new XYSeries(titles[i], scale);
      double[] xV = xValues.get(i);
      double[] yV = yValues.get(i);
      int seriesLength = xV.length;
      for (int k = 0; k < seriesLength; k++) {
        series.add(xV[k], yV[k]);
      }
      dataset.addSeries(series);
    }
  }

  /**
   * Builds an XY multiple series renderer.
   * 
   * @param colors the series rendering colors
   * @param styles the series point styles
   * @return the XY multiple series renderers
   */
  public static XYMultipleSeriesRenderer buildRenderer(int[] colors, PointStyle[] styles) {
    XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
    setRenderer(renderer, colors, styles);
    return renderer;
  }

  public static void setRenderer(XYMultipleSeriesRenderer renderer, int[] colors, PointStyle[] styles) {
    renderer.setAxisTitleTextSize(16);
    renderer.setChartTitleTextSize(20);
    renderer.setLabelsTextSize(15);
    renderer.setLegendTextSize(15);
    renderer.setPointSize(5f);
    renderer.setMargins(new int[] { 20, 30, 15, 20 });
    int length = colors.length;
    for (int i = 0; i < length; i++) {
      XYSeriesRenderer r = new XYSeriesRenderer();
      r.setColor(colors[i]);
      r.setPointStyle(styles[i]);
      renderer.addSeriesRenderer(r);
    }
  }

  /**
   * Sets a few of the series renderer settings.
   * 
   * @param renderer the renderer to set the properties to
   * @param title the chart title
   * @param xTitle the title for the X axis
   * @param yTitle the title for the Y axis
   * @param xMin the minimum value on the X axis
   * @param xMax the maximum value on the X axis
   * @param yMin the minimum value on the Y axis
   * @param yMax the maximum value on the Y axis
   * @param axesColor the axes color
   * @param labelsColor the labels color
   */
  public static void setChartSettings(XYMultipleSeriesRenderer renderer, String title, String xTitle,
      String yTitle, double xMin, double xMax, double yMin, double yMax, int axesColor,
      int labelsColor) {
    renderer.setChartTitle(title);
    renderer.setXTitle(xTitle);
    renderer.setYTitle(yTitle);
    renderer.setXAxisMin(xMin);
    renderer.setXAxisMax(xMax);
    renderer.setYAxisMin(yMin);
    renderer.setYAxisMax(yMax);
    renderer.setAxesColor(axesColor);
    renderer.setLabelsColor(labelsColor);
  }
  
  public static XYMultipleSeriesRenderer getMultiSeriesRenderer(int seriesCount, boolean showsCourseGrade, Date min, Date max) {
	    XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
	    renderer.setAxisTitleTextSize(16);
	    renderer.setChartTitleTextSize(20);
	    renderer.setLabelsTextSize(15);
	    renderer.setLegendTextSize(15);
	    renderer.setPointSize(4f);
	    renderer.setMargins(new int[] {30, 30, 30, 15});
	    
	    int[] colors = new int[] {
	    		Color.rgb(0, 102, 0), Color.BLUE, Color.rgb(230, 115, 0), Color.MAGENTA, 
	    		Color.BLACK, Color.rgb(0, 153, 153), Color.rgb(179, 0, 89), Color.YELLOW
	    };
 	    
	    int overallGradeColor = Color.RED;
	    
	    for(int i = 0; i < seriesCount; i++) {
	    	XYSeriesRenderer r = new XYSeriesRenderer();
		    r = new XYSeriesRenderer();
		    
		    if((i == seriesCount-1) && (seriesCount != 1) && showsCourseGrade) {
		    	r.setColor(overallGradeColor);
		    	
		    	r.setPointStyle(PointStyle.X);
		    	r.setStroke(BasicStroke.DOTTED);
		    } else {
			    int pointCol = (i < colors.length) ? 
	    				colors[i] : Color.rgb((int)(Math.random() * 256), 
	    								  (int)(Math.random() * 256), 
	    								  (int)(Math.random() * 256));
		    	r.setColor(pointCol);
		    	
		    }
		    
		    r.setLineWidth(2.0f);
		    r.setFillPoints(true);
		    r.setPointStyle(PointStyle.CIRCLE);
		    
		    renderer.addSeriesRenderer(r);
	    }
	    
	    renderer.setAxesColor(Color.BLACK);
	    renderer.setLabelsColor(Color.BLACK);
	    renderer.setShowGrid(true);
	    renderer.setZoomButtonsVisible(true);
	    
	    renderer.setXTitle("Time");
	    renderer.setYTitle("Grade in Percent (%)");  
	    
	    renderer.setXLabelsColor(Color.BLACK);
	    renderer.setYLabelsColor(0, Color.BLACK);
	    
	    renderer.setGridColor(Color.rgb(140, 140, 140));
	    
	    renderer.setApplyBackgroundColor(true); 
	    //renderer.setBackgroundColor(Color.BLACK);
	    renderer.setBackgroundColor(Color.TRANSPARENT);
	    //renderer.setMarginsColor(Color.argb(0x00, 0x01, 0x01, 0x01));
	    renderer.setMarginsColor(Color.rgb(219, 230, 188));
	    
	    double SIXMONTHS = 81300000 * 180;
	    double minX = min.getTime() - SIXMONTHS;
	    double maxX = max.getTime() + SIXMONTHS;
	    
	    renderer.setPanLimits(new double[] { minX, maxX, -10, 1000});
	    
	    return renderer;
  }
}
