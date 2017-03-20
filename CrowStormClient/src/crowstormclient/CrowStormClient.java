/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crowstormclient;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 *
 * @author rockhowse
 */
public class CrowStormClient extends Application {
    
    public void addSearchHeader(HBox hbox) {
        HBox searchHBox = new HBox();
        
        TextField searchTextField = new TextField();
        
        Button searchButton = new Button("Search");
        searchButton.setPrefSize(100, 20);
        
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
        HBox hbox = addHBox();
        border.setTop(hbox);
        
        Scene scene = new Scene(border, 1200, 800);
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
