package com.momo.datamodel;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;

public class StoreTodoData {

    private static StoreTodoData instance = new StoreTodoData();// An instance object of this singleton class
    private String fileName = "todoItems.txt";// Text file for the to do items
    private ObservableList<ToDoItem> todoItems; // Observable list for the to do items
    private DateTimeFormatter dateTimeFormatter; // Date formatter variable

    // returns the instance of the singleton class
    public static StoreTodoData getInstance(){
        return instance;
    }

    // Constructor of the this class
    private StoreTodoData(){
        dateTimeFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy");

    }
    // Returns observable list
    public ObservableList<ToDoItem> getTodoItems() {
        return todoItems;
    }

    // Adds to do item to list view
    public void addToListItem(ToDoItem item){
        todoItems.add(item);
    }
    // Loads to do items from text to observable list
    public void loadToDoItems() throws IOException{
        todoItems = FXCollections.observableArrayList();
        Path path = Paths.get(fileName);
        BufferedReader reader = Files.newBufferedReader(path);
        String input;
        try{

            while ((input=reader.readLine()) != null){
                String[] todoList = input.split("\t");
                String description = todoList[0];
                String details = todoList[1];
                String dateStr = todoList[2];
                LocalDate dueDate = LocalDate.parse(dateStr, dateTimeFormatter);

                ToDoItem toListItem = new ToDoItem(description, details, dueDate);
                todoItems.add(toListItem);
            }


        }catch (IOException exe){

        }finally {
            if(reader != null){
                reader.close();
            }
        }

    }

    // Stores to do items to a text file
    public void storeDoToItems() throws IOException{
        Path path = Paths.get(fileName);
        BufferedWriter writer = Files.newBufferedWriter(path);

        try{
            Iterator<ToDoItem> items = todoItems.iterator();
            while (items.hasNext()){
                ToDoItem toDoItemItem = items.next();
                writer.write(String.format("%s\t%s\t%s", toDoItemItem.getDescription(), toDoItemItem.getDetails(), toDoItemItem.getDate().format(dateTimeFormatter)));
                writer.newLine();
            }
        }finally {
            if(writer != null){
                writer.close();
            }
        }
    }
    // deletes a do item from a observable list
    public void deleteItemFromList(ToDoItem item){
        todoItems.remove(item);
    }

    // Edit a to do item
    public void editToItem(ToDoItem oldItem, ToDoItem newItem){
        int indexNumber = this.todoItems.indexOf(oldItem);
        if(indexNumber >= 0){
            this.todoItems.set(indexNumber, newItem);
        }

    }

}
