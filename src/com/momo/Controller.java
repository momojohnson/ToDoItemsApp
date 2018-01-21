package com.momo;

import com.momo.datamodel.StoreTodoData;
import com.momo.datamodel.ToDoItem;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.Predicate;

public class Controller {


    @FXML
    private ListView<ToDoItem> toDoListView; // list view variable
    @FXML
    private TextArea detailsTextArea; // text area variable

    @FXML
    private Label dateLabel; // date label variable

    private DateTimeFormatter dateFormat; // date formatter variable
    @FXML
    private BorderPane borderPane; // border pane variable

    @FXML
    private ContextMenu listContextMenu; // context menu variable

    @FXML
    private ToggleButton filterTodoItems; // Filter toggle button

    private FilteredList<ToDoItem> filteredList; // A Filter List to filter to do items

    private Predicate<ToDoItem> allItems; // boolean variable for all item
    private Predicate<ToDoItem> todayItem; // boolean variable for today's item

    public void initialize(){
        // List context menu to allow deletion
        listContextMenu = new ContextMenu();
        MenuItem deleteMenuItem = new MenuItem("Delete");

        // lambda event handler that delete an item from the listview
        deleteMenuItem.setOnAction((ActionEvent event) ->{
                ToDoItem item = toDoListView.getSelectionModel().getSelectedItem();
                deleteItem(item);

        });


        listContextMenu.getItems().setAll(deleteMenuItem);
         // Listens for changes on the list views
        toDoListView.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends ToDoItem> observable, ToDoItem oldValue, ToDoItem newValue)-> {
            if(newValue != null){
                    ToDoItem item = toDoListView.getSelectionModel().getSelectedItem();
                    detailsTextArea.setText(item.getDetails());
                    dateFormat = DateTimeFormatter.ofPattern("EEEE, MMMM-dd-yyyy");
                    dateLabel.setText("Due date: "+dateFormat.format(item.getDate()));
                }
        });

        // A lambda predicate that return true

        allItems = (ToDoItem toDoItem) ->  true;

        // A lambda function that compare to do item with today's date

        todayItem = (ToDoItem todoItem) -> todoItem.getDate().equals(LocalDate.now());

        //A list use to filter items being display on the ListView
        filteredList = new FilteredList<>(StoreTodoData.getInstance().getTodoItems(), allItems);


        // A sorted list used to sort our items base on due dates. Due and past dues items are shown first
        SortedList<ToDoItem> sortedList = new SortedList<>(filteredList,
                new Comparator<ToDoItem>() {
                    @Override
                    public int compare(ToDoItem o1, ToDoItem o2) {
                        return o1.getDate().compareTo(o2.getDate());
                    }
                });
        //toDoListView.setItems(StoreTodoData.getInstance().getTodoItems());
        toDoListView.setItems(sortedList);
        toDoListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        toDoListView.getSelectionModel().selectFirst();

        // A callback event that sets color  of to do items
        toDoListView.setCellFactory(new javafx.util.Callback<ListView<ToDoItem>, ListCell<ToDoItem>>() {
            @Override
            public ListCell<ToDoItem> call(ListView<ToDoItem> param) {

                ListCell<ToDoItem> cell = new ListCell<ToDoItem>(){
                    @Override
                    protected void updateItem(ToDoItem item, boolean empty) {
                        super.updateItem(item, empty);
                        if(empty){
                            setText(null);
                        }else {
                            setText(item.getDescription());
                            if(item.getDate().isBefore(LocalDate.now().plusDays(1))){
                                setTextFill(Color.RED);
                            }
                            else if(item.getDate().equals(LocalDate.now().plusDays(1))){
                                setTextFill(Color.BLUE);
                            }
                        }
                    }
                };
                // Setting of the list context menu using the cell of the listview
                cell.emptyProperty().addListener((obs, isEmpty, isNowEmpty) -> {
                    if(isNowEmpty){
                        cell.setContextMenu(null);
                    }else {
                        cell.setContextMenu(listContextMenu);
                    }
                });
                return cell;

            }
        });


    }


    // Load add item dialog box
    @FXML
    public void showNewItemDialog(){
       Dialog<ButtonType> dialog = new Dialog<>();
       dialog.setTitle("Add to do item");
       dialog.setHeaderText("Use the form below to enter a to do item");
       dialog.initOwner(borderPane.getScene().getWindow());
       FXMLLoader fxmlLoader = new FXMLLoader();
       fxmlLoader.setLocation(getClass().getResource("todoItemController.fxml"));

       try{
        dialog.getDialogPane().setContent(fxmlLoader.load());

       }catch (IOException ex){
           System.out.println("Couldn't load dialogue");
           ex.printStackTrace();
           return;
       }

       dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
       dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        Optional<ButtonType>  result = dialog.showAndWait();
        if(result.isPresent() && result.get().equals(ButtonType.OK)){
            DialogueController controller = fxmlLoader.getController();
            ToDoItem newItem = controller.addTodoItem();
            if(newItem == null){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Error");
                alert.setContentText("Dodo item isn't saved. Please ensure that all fields are filled out.");
                alert.showAndWait();
                return;
            }
            StoreTodoData.getInstance().addToListItem(newItem);
            toDoListView.getSelectionModel().select(newItem);
           // toDoListView.getItems().setAll(StoreTodoData.getInstance().getTodoItems());

        }
    }

    // Delete an item from the observable of in the singleton class

    public void deleteItem(ToDoItem item){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText(String.format("Delete: %s ", item.getDescription()));
        alert.setTitle("Delete Todo Item");
        alert.setContentText("Are you sure you want to delete "+item.getDescription());
        Optional<ButtonType> result = alert.showAndWait();
        if(result.isPresent() &&(result.get() == ButtonType.OK)){
            StoreTodoData.getInstance().deleteItemFromList(item);
        }
    }
    // Handle key press to delete an item from the listview
@FXML
public void handleKeyPressed(KeyEvent event){
    ToDoItem item = toDoListView.getSelectionModel().getSelectedItem();
    if(item != null){
        if(event.getCode().equals(KeyCode.DELETE)){
            deleteItem(item);
        }
    }


}

// Filter today's to do items
@FXML
public void handleFilterItems(){
        ToDoItem selectedItem = toDoListView.getSelectionModel().getSelectedItem();
        if(filterTodoItems.isSelected()){
            filteredList.setPredicate(todayItem);
            if(filteredList.isEmpty()){
                detailsTextArea.clear();
                dateLabel.setText("");
            }else if(filteredList.contains(selectedItem)){
                toDoListView.getSelectionModel().select(selectedItem);
            }else {
                toDoListView.getSelectionModel().selectFirst();
            }
        }else {
            filteredList.setPredicate(allItems);
            toDoListView.getSelectionModel().select(selectedItem);
        }
    }
    @FXML

    // Close the current window
    public void exitWindow(){
        Platform.exit();

    }

    // Edit to do item
    @FXML
    public void editToDoItems(){
        DialogueController controller;

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit todo Item");
        dialog.setHeaderText("Use the form below to enter a to do item");
        dialog.initOwner(borderPane.getScene().getWindow());
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("todoItemController.fxml"));

        try{

            dialog.getDialogPane().setContent(fxmlLoader.load());
            controller = fxmlLoader.getController();
            ToDoItem editItem = toDoListView.getSelectionModel().getSelectedItem();
            controller.showItemToEdit(editItem);
            dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
            dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
            Optional<ButtonType>  result = dialog.showAndWait();
            if(result.isPresent() && result.get().equals(ButtonType.OK)){

                controller.updateTodoItem(editItem);
                toDoListView.getSelectionModel().select(editItem);

            }

        }catch (IOException ex){

            ex.printStackTrace();
        }

    }
}
