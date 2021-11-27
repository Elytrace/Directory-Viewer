import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class DirectoryViewer extends Application {

    private Stage primaryStage;

    private final TreeView<Node> fileList = new TreeView<>();
    private final BorderPane pane = new BorderPane();
    private String path;
    private final String iconPath = "C:\\Users\\hieum\\OneDrive\\Máy tính\\Internet Explorer\\TIN\\Java\\JavaFX\\src\\IconList\\";

    private final List<CheckBox> checkBoxList = new ArrayList<>();
    private final List<Label> folderNameList = new ArrayList<>();
    private final List<TreeItem<Node>> folderList = new ArrayList<>();

    public MenuBar createMenu() {
        MenuBar menubar = new MenuBar();

        Label edit = new Label("Edit...");
        Menu editMenu = new Menu("", edit);
        EditFunction(edit, editMenu);
        editMenu.setStyle("-fx-border-color: Black");

        Label open = new Label("Open Folder");
        OpenFunction(open, editMenu);
        Menu openMenu = new Menu("", open);
        openMenu.setStyle("-fx-border-color: Black");

        Label add = new Label("Create Folder");
        MenuItem addMenu = new Menu("", add);
        CreateFunction(add, addMenu);
        add.setStyle("-fx-text-fill: Black");

        Label delete = new Label("Delete Folder");
        MenuItem deleteMenu = new Menu("", delete);
        delete.setStyle("-fx-text-fill: Black");

        Label admit = new Label("Accept");
        Menu admitChange = new Menu("", admit);
        admitChange.setStyle("-fx-border-color: Black");

        DeleteFunction(delete, deleteMenu, admit, admitChange);

        admitChange.setVisible(false);
        editMenu.setVisible(false);
        editMenu.getItems().addAll(addMenu, deleteMenu);
        menubar.getMenus().addAll(openMenu, editMenu, admitChange);

        return menubar;
    }

    public void OpenFunction(Label open, Menu editMenu) {
        open.setOnMouseClicked(mouseEvent -> {
            for(CheckBox cb : checkBoxList)
                cb.setVisible(false);
            DirectoryChooser dc = new DirectoryChooser();
            dc.setInitialDirectory(new File(System.getProperty("user.home")));

            File choice = dc.showDialog(primaryStage);

            if(choice == null) {}
            else {
                editMenu.setVisible(true);
                path = choice.getAbsolutePath();
                fileList.setRoot(getItemInDirectory(choice));
            }
        });
    }

    public void EditFunction(Label edit, Menu editMenu) {
        edit.setOnMouseEntered(mouseEvent -> editMenu.show());
        edit.setOnMouseClicked(mouseEvent -> editMenu.show());
    }

    public void CreateFunction(Label add, MenuItem addMenu) {
        add.setOnMouseClicked(mouseEvent -> {
            addMenu.setDisable(true);
            for(CheckBox cb : checkBoxList)
                cb.setVisible(false);

            CheckBox cb = new CheckBox();
            checkBoxList.add(cb);
            cb.setVisible(false);

            ImageView icon = new ImageView();
            icon.setImage(new Image(iconPath + "FOLDER.png", 25, 25, false, true));

            Label label = new Label("");
            label.setFont(Font.font(null, 14));
            folderNameList.add(label);

            TextField textField = new TextField();

            HBox field = new HBox(cb, icon, label, textField);
            TreeItem<Node> item = new TreeItem<>(field);
            folderList.add(item);
            fileList.getRoot().getChildren().add(item);

            textField.setOnKeyPressed(keyEvent -> {
                if(keyEvent.getCode() == KeyCode.ENTER) {
                    if(textField.getText().isBlank())
                        fileList.getRoot().getChildren().remove(item);
//                    field.setStyle("-fx-border-color: Black");
                    label.setText(textField.getText());
                    label.setVisible(true);
                    textField.clear();
                    textField.setVisible(false);
                    textField.setPrefWidth(0);
                    label.setPrefWidth(146);
                    label.setStyle("-fx-border-color: Black");
                    try {
                        Files.createDirectories(Paths.get(path + "\\" + label.getText()));
                        addMenu.setDisable(false);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        });
    }

    public void DeleteFunction(Label delete, MenuItem deleteMenu, Label admit, Menu admitChange) {
        delete.setOnMouseClicked(mouseEvent -> {
            deleteMenu.setDisable(true);
            admitChange.setVisible(true);
            for(CheckBox cb : checkBoxList)
                cb.setVisible(true);
            ConfirmDelete(deleteMenu, admit, admitChange);
        });
    }

    public void ConfirmDelete(MenuItem deleteMenu, Label admit, Menu admitChange) {
        admit.setOnMouseClicked(mouseEvent1 -> {
            for(CheckBox cb : checkBoxList)
                if(cb.isSelected()) {
                    fileList.getRoot().getChildren().remove(folderList.get(checkBoxList.indexOf(cb)));
                    File folder = new File(path + "\\" + folderNameList.get(checkBoxList.indexOf(cb)).getText());
                    try {
                        deleteFolder(folder);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            for(CheckBox cb : checkBoxList)
                cb.setVisible(false);
            deleteMenu.setDisable(false);
            admitChange.setVisible(false);
        });
    }

    public void deleteFolder(File folder) throws IOException {
        for(File f : folder.listFiles()) {
            if(f.isDirectory())
                 deleteFolder(f);
            else Files.deleteIfExists(Paths.get(f.getAbsolutePath()));
        }
        Files.deleteIfExists(Paths.get(folder.getAbsolutePath()));
    }

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        primaryStage.setResizable(false);

        String tree_color = this.getClass().getResource("tree_color.css").toExternalForm();
        fileList.getStylesheets().add(tree_color);
        fileList.setPrefWidth(275);
        pane.setLeft(fileList);

        MenuBar menubar = createMenu();
        pane.setTop(menubar);

        Rectangle rec = new Rectangle(400, 600);
        rec.setStyle("-fx-fill: White");
        pane.setRight(rec);

        pane.setStyle("-fx-background-color: Navy");

        Scene scene = new Scene(pane, 700, 600);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Directory Viewer");
        primaryStage.show();
    }

    public TreeItem<Node> getItemInDirectory(File directory) {
        ImageView icon = new ImageView();
        icon.setImage(new Image(iconPath + "FOLDER.png", 25, 25, false, true));

        CheckBox cb = new CheckBox();
        checkBoxList.add(cb);
        cb.setVisible(false);

        TextField tf = new TextField();
        tf.setVisible(false);
        tf.setPrefSize(100, 20);

        Label folderName = new Label(directory.getName());
        folderNameList.add(folderName);
        folderName.setPrefWidth(200);
        folderName.setStyle("-fx-border-color: Black; -fx-fill: Steelblue");
        folderName.setFont(Font.font(null, 14));

        HBox treeItem = new HBox(cb, icon, folderName, tf);
        treeItem.setPrefWidth(220);
//        treeItem.setStyle("-fx-border-color: Black");

        TreeItem<Node> root = new TreeItem<>(treeItem);
        folderList.add(root);
        for(File f : directory.listFiles()) {
            if(f.isDirectory())
                root.getChildren().add(getItemInDirectory(f));

            else if(f.getName().toLowerCase().endsWith(".txt"))
                root.getChildren().add(new TreeItem<>(DisplayText(f)));
            else if(f.getName().toLowerCase().endsWith(".png"))
                root.getChildren().add(new TreeItem<>(DisplayImage(f, "PNG.png")));
            else if(f.getName().toLowerCase().endsWith(".jpg"))
                root.getChildren().add(new TreeItem<>(DisplayImage(f, "JPG.png")));
            else if(f.getName().toLowerCase().endsWith(".jpeg"))
                root.getChildren().add(new TreeItem<>(DisplayImage(f, "JPG.png")));

            else {
                CheckBox cbox = new CheckBox();
                cbox.setVisible(false);
                Label label = new Label(f.getName());
                label.setPrefWidth(150);
                label.setStyle("-fx-border-color: Black");
                root.getChildren().add(new TreeItem<>(new HBox(cbox, label)));
            }
        }
        return root;
    }

    public HBox DisplayText(File f) {
        CheckBox cb = new CheckBox();
        cb.setVisible(false);

        ImageView icon = new ImageView();
        icon.setImage(new Image(iconPath + "TXT.png", 25, 25, false, true));

        Label label = new Label(f.getName());
        label.setStyle("-fx-border-color: Black");
        label.setPrefWidth(150);
        label.setFont(Font.font(null, 14));

        label.setOnMouseClicked(mouseEvent ->  {
            pane.setRight(null);
            TextArea text = new TextArea();
            text.setPrefSize(400, 600);
            File file = new File(f.getAbsolutePath());
            Scanner sc = null;
            try {
                sc = new Scanner(file);
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }
            while(sc.hasNextLine())
                text.appendText(sc.nextLine() + "\n");

            text.setWrapText(true);
            text.setEditable(false);
            pane.setRight(text);
        });
        return new HBox(cb, icon, label);
    }

    public HBox DisplayImage(File f, String iconType) {
        CheckBox cb = new CheckBox();
        cb.setVisible(false);

        ImageView icon = new ImageView();
        icon.setImage(new Image(iconPath + iconType, 25, 25, false, true));

        Label label = new Label(f.getName());
        label.setStyle("-fx-border-color: Black");
        label.setPrefWidth(150);
        label.setFont(Font.font(null, 14));

        label.setOnMouseClicked(mouseEvent -> {
            pane.setRight(null);
            ImageView imageView = new ImageView();
            imageView.setImage(new Image(f.getAbsolutePath(), 400, 600, false, true));
            pane.setRight(imageView);
        });

        return new HBox(cb, icon, label);
    }

    public static void main(String[] args) {
        launch(args);
    }
}