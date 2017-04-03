/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crowstormclient;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.ComboBoxListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import candlestickchartapp.CandleStickChart;
import candlestickchartapp.CandleStickExtraValues;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Parent;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;


/**
 *
 * @author rockhowse
 */
public class CrowStormClient extends Application {
    private static ObservableList data = 
        FXCollections.observableArrayList();  
    
    private ListView searchResults;
    private TextField searchTextField;
    
    private CandleStickChart chart;
    private NumberAxis xAxis;
    private NumberAxis yAxis; 
    
    // DAY, OPEN, CLOSE, HIGH, LOW, AVERAGE
    private static List<double []> chartData;
    XYChart.Series<Number,Number> prevSeries;
    
    public void getPricesForSymbol(String symbol) {
        String priceData;
        String[] priceLines;
        
        priceData = "NO_DATA_FOUND";
        
        if(chartData == null) {
            chartData = new ArrayList<>();
        // no need to tear-down, just re-use
        } else {
            chartData.clear();
        }
        
        double day;
        double open;
        double close;
        double high;
        double low;
        double average;
        
        // get data from the server and graph it
        try {
            priceData = IOUtils.toString(new URL("http://localhost:18080/symbol/csv/" + symbol), Charset.forName("UTF-8"));
            priceLines = priceData.split("\\r?\\n");
            
            for(String line : priceLines) {
                String [] columns = line.split(",");
                
                // filter first line
                if(columns[0].equals("Date")){
                    continue;
                }
                
                // day      ~ [0]
                day = Integer.parseInt(columns[0].split("-")[2]);
                
                // open     ~ [1]
                open = Double.parseDouble(columns[1]);
                
                // close    ~ [4]
                close = Double.parseDouble(columns[4]);
                
                // high     ~ [2]
                high = Double.parseDouble(columns[2]);
                
                // low      ~ [3]
                low = Double.parseDouble(columns[3]);
                
                // average  ~  do simple derp calculation using OHLC
                average = (open + close + high + low)/4.0;
              
                double [] dayData = {day, open, close, high, low, average};
                chartData.add(dayData);
                
            }
            
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    
   public Parent generateChart() {
       
        
        xAxis = new NumberAxis(0,32,1);
        xAxis.setMinorTickCount(0);
        yAxis = new NumberAxis();
        
        if(chart == null) {
            chart = new CandleStickChart(xAxis,yAxis);
        }
   
        // setup chart
        xAxis.setLabel("Day");
        yAxis.setLabel("Price");
        // add starting data
        
        XYChart.Series<Number,Number> series = new XYChart.Series<Number,Number>();
        
        if(chartData != null) {
            for (double [] day: chartData) {
                series.getData().add(
                    new XYChart.Data<Number,Number>(day[0],day[1],new CandleStickExtraValues(day[2],day[3],day[4],day[5]))
                );
            };
        }
        
        ObservableList<XYChart.Series<Number,Number>> data = chart.getData();
        
        if (data == null) {
            data = FXCollections.observableArrayList(series);
            chart.setData(data);
        } else {
            chart.getData().removeAll(prevSeries);
            chart.getData().add(series);
        }
        
        prevSeries = series;
        
        return chart;
    }    
             
    public void searchResults() {
        JSONObject json;
        
        try {
            
            json = new JSONObject(IOUtils.toString(new URL("http://localhost:18080/company/" + searchTextField.getText() +"/5"), Charset.forName("UTF-8")));
            data.clear();

            json.toMap().forEach( (k,v) -> data.add(k + "~" + (String) v));
            
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public ListView addSearchResults() {
        ListView searchResults = new ListView(data);
        searchResults.setPrefSize(200, 250);
          
        searchResults.setItems(data);
        searchResults.setCellFactory(ComboBoxListCell.forListView(data));   
        
        searchResults.getSelectionModel().selectedItemProperty().addListener(
            new ChangeListener<String>() {
                public void changed(ObservableValue<? extends String> ov, 
                    String old_val, String new_val) {
                    
                        String firstSymbol;
                        
                        // split on ~ for the symbols 
                        firstSymbol = new_val.split("~")[1];
                        
                        // if it has a | we need to trim and get the first one
                        if(firstSymbol.indexOf("|") >= 0) {
                            firstSymbol = firstSymbol.split("|")[0];
                        }
                        
                        getPricesForSymbol(firstSymbol);
                        
                        generateChart();
            }
        });       
        
        return searchResults;
    }
    
    public VBox addVBox() {
        VBox vbox = new VBox();
        
        // get the search results componant
        searchResults = addSearchResults();
        
        // get the chart componant
        // TODO: get OHLC chart componant working here
        generateChart();
        
        vbox.getChildren().addAll(searchResults, chart);
        return vbox;
    }
    
    public void addSearchHeader(HBox hbox) {
        HBox searchHBox = new HBox();
        
        searchTextField = new TextField();
        
        Button searchButton = new Button("Search");
        searchButton.setPrefSize(100, 20);
        
                // add action handler for the log in button
        searchButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                searchResults();
            }
        });
        
        searchHBox.getChildren().addAll(searchTextField, searchButton);
        searchHBox.setAlignment(Pos.CENTER_RIGHT); 
        
        hbox.getChildren().add(searchHBox);            
        HBox.setHgrow(searchHBox, Priority.ALWAYS);    // Give searchHBox any extra space
    }
    
    public HBox addHBox() {
        HBox hbox = new HBox();
        hbox.setId("hbox");
        hbox.setPadding(new Insets(15, 12, 15, 12));
        hbox.setSpacing(10);
        
        // crowstorm logo
        ImageView imageCrow = new ImageView(
            new Image(CrowStormClient.class.getResourceAsStream("graphics/crow_storm.png")));
        imageCrow.setFitHeight(50);
        imageCrow.setFitWidth(50);
        
        // crowstorm header
        Text title = new Text("CrowStorm");
        title.setId("text-title");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 14));
    
        hbox.getChildren().addAll(imageCrow, title);
        
        // add search componants on rigth side
        addSearchHeader(hbox);

        return hbox;
    }
    
    @Override
    public void start(Stage primaryStage) {
        
        primaryStage.setTitle("CrowStormClient v0.0.1");
        
        BorderPane border = new BorderPane();
        
        // top header
        HBox hbox = addHBox();
        border.setTop(hbox);
        
        // center search and chart
        VBox vbox = addVBox();
        border.setCenter(vbox);
        
        Scene scene = new Scene(border, 800, 600);
        scene.getStylesheets().add
            (CrowStormClient.class.getResource("CrowStormClient.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
