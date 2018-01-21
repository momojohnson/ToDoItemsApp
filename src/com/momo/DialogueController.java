package com.momo;

import com.momo.datamodel.StoreTodoData;
import com.momo.datamodel.ToDoItem;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;

public class DialogueController {

    @FXML
    private DatePicker dtDeadLine; // date variable for dialogue

    @FXML
    private TextArea arDetails; // detail variable

    @FXML
    private TextField txtDescription; // description variable for dialogue

    public void initialize(){
        dtDeadLine.setValue(LocalDate.now());
    }

    // Adds a to do item to the observable list
    public ToDoItem addTodoItem(){
        if(isValidInput()){
            String detailString = arDetails.getText();
            String textDescription = txtDescription.getText();
            LocalDate dueDate = dtDeadLine.getValue();

            ToDoItem item = new ToDoItem(textDescription, detailString, dueDate);
            return item;
        }
        return null;
        }

    // Show items to be edited in the edit dialogue box
    public void showItemToEdit(ToDoItem itemToEdit){
        dtDeadLine.setValue(itemToEdit.getDate());
        arDetails.setText(itemToEdit.getDetails());
        txtDescription.setText(itemToEdit.getDescription());

    }

    // Update a to do item in the list view through observables in the singleton class
    public void updateTodoItem(ToDoItem oldItem){
        String txtDescription = this.txtDescription.getText();
        String txtDetails = arDetails.getText();
        LocalDate dueDate = dtDeadLine.getValue();

        ToDoItem editedItem = new ToDoItem(txtDescription, txtDetails, dueDate);
        StoreTodoData.getInstance().editToItem(oldItem, editedItem);
    }

   private <T extends TextInputControl> boolean isValidData(T control){
        if(control.getText().isEmpty()|| control.getText().trim().isEmpty()){
            return false;
        }
        return true;
   }


    private boolean isValidInput(){
        if(!isValidData(txtDescription)){
            return false;
        }
        if(!isValidData(arDetails)){
            return false;
        }
        return true;
    }

}
