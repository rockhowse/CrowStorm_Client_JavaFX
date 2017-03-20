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


/**
 *
 * @author rockhowse
 */
public class CrowStormClient extends Application {

    public static ObservableList data = 
        FXCollections.observableArrayList();  
    
    ListView searchResults;
    TextField searchTextField;
             
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
        
        return searchResults;
    }
    
    public VBox addVBox() {
        VBox vbox = new VBox();
        
        // get the search results componant
        searchResults = addSearchResults();
        
        // get the chart componant
        // TODO: get OHLC chart componant working here
        
        vbox.getChildren().addAll(searchResults);
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
