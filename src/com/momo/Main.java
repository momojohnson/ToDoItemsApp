package com.momo;

import com.momo.datamodel.StoreTodoData;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("mainWindow.fxml"));
        setUserAgentStylesheet(STYLESHEET_MODENA);
        primaryStage.setTitle("Todo List App Java");
        primaryStage.setScene(new Scene(root, 900, 500));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void stop() throws Exception {
        // Save do items to text file when program stop or closes
        try{
            StoreTodoData.getInstance().storeDoToItems();
        }catch (IOException err){
            System.out.println(err.getMessage());
        }

    }

    @Override
    public void init() throws Exception {
        try{
            StoreTodoData.getInstance().loadToDoItems();
        }catch (IOException err){
            System.out.println(err.getMessage());
        }
    }
}
