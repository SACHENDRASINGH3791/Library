package controllers;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Random;

import base.Car;
import base.Save_Read;
import base.Save_Read_BaseList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.text.Text;
import mysql.MysqlBase;

public class OptionsWindowController {

	private MainController mainControler;
	private static ArrayList<Car> base;
	Save_Read sr = new Save_Read();
	private static ArrayList<String> baseList;
	Save_Read_BaseList srbl= new Save_Read_BaseList();
	File file;
	static int baseNumber=0;
	
    public int getBaseNumber() {
		return baseNumber;
	}
    
    @FXML
    private Text selectBaseInfo;   
	@FXML
    private ListView<String> list; 

	public void setMainControler(MainController mainControler) {
		this.mainControler = mainControler;
	}

	public static ArrayList<String> getBaseList() {
		return baseList;
	}
	
    @FXML
    void initialize() throws ClassNotFoundException, IOException {
    	selectBaseInfo.setVisible(false);;
    }
	
	@FXML
	void menuAction(ActionEvent event) throws IOException {
		mainControler.loadMenu();

	}

	@FXML
	void deleteBaseAction(ActionEvent event) throws ClassNotFoundException, IOException {
		try{
			baseList=srbl.getBase();
		}catch(FileNotFoundException e){
			emptyDatabaseInfo();
		}
		if(baseList.isEmpty()){
			emptyDatabaseInfo();
		}else{
			/////////////////////////////////////protection before deleting database
			Random random=new Random();
			int a=random.nextInt(899)+100;
			TextInputDialog dialog = new TextInputDialog();
			dialog.setTitle("UWAGA");
			dialog.setHeaderText("Czy napewno chcesz usun�� baz� danych?\nWpisz kod aby kontynu�owa�: "+a);
			dialog.setContentText("Kod:");
			Optional<String> result = dialog.showAndWait();
			if (result.isPresent()){
				String b=result.get();
				int code=Integer.parseInt(b);
				/////////////////////////////////////////////////////////////////////////
				if(a==code)
				{
					file=new File(baseList.get(list.getSelectionModel().getSelectedIndex()));
					baseList.remove(list.getSelectionModel().getSelectedIndex());
					file.delete();
					ObservableList<String> olist=FXCollections.observableArrayList(baseList);
					list.setItems(olist);
					srbl.saveList();
				}else{}
			}
		}
	}

	@FXML
	void newBaseAction(ActionEvent event) throws IOException, ClassNotFoundException {

		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle("Nowa baza");
		dialog.setHeaderText("Nowa baza danych");
		dialog.setContentText("Nazwa:");
		Optional<String> result = dialog.showAndWait();

		if (result.isPresent()) {
			String baseName = result.get();
			file = new File(baseName);
			base = new ArrayList<Car>();
			FileOutputStream fos = new FileOutputStream(baseName);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(base);
			oos.flush();
			oos.close();
			createLastBaseInfoFile(baseName);
			try {
				baseList=srbl.getBase();
			} catch (FileNotFoundException e) {
				file = new File("baseList");
				fos = new FileOutputStream("baseList");
				oos = new ObjectOutputStream(fos);
				baseList = new ArrayList<String>();
				oos.writeObject(baseList);
				oos.flush();
				oos.close();				
			}
			baseList.add(baseName);
			srbl.saveList();
			ObservableList<String> olist=FXCollections.observableArrayList(baseList);
			list.setItems(olist);
		}
	}

	@FXML
	void selectBaseAction(ActionEvent event) throws ClassNotFoundException, IOException {
		try{
			baseList=srbl.getBase();
		}catch(FileNotFoundException e){
			emptyDatabaseInfo();
		}
		if(baseList.isEmpty()){
			emptyDatabaseInfo();
		}else{
			baseNumber=list.getSelectionModel().getSelectedIndex();
			createLastBaseInfoFile(list.getSelectionModel().getSelectedItem());
			selectBaseInfo.setText("Wybrano baz� danych: "+sr.getBaseName());
			selectBaseInfo.setVisible(true);
		}
	}

	public void createLastBaseInfoFile(String baseName) throws FileNotFoundException, IOException {
		file = new File("lastBaseInfo"); 
		FileOutputStream fos=new FileOutputStream(file);
		DataOutputStream dos = new DataOutputStream(fos);	//method to open last used database
		dos.writeUTF(baseName);
		dos.close();
	}
	
    @FXML
    void showDatabases(ActionEvent event) throws ClassNotFoundException, IOException {
    	try{
    		baseList=srbl.getBase();
    	}catch(FileNotFoundException e){
    		emptyDatabaseInfo();
    	}
    	if(baseList.isEmpty()){
    		emptyDatabaseInfo();
    	}else{
		    ObservableList<String> olist=FXCollections.observableArrayList(baseList);
			list.setItems(olist);}
    }
    
    @FXML
    void moveToMySqlAction(ActionEvent event) throws ClassNotFoundException, SQLException, IOException {
    	/*String tableName;
		Car car;
    	tableName=list.getSelectionModel().getSelectedItem();
    	mysqlBase.createTable(tableName);
    	base=sr.getBase(tableName);

    	for(int i=0;i<base.size();i++){ 
    		car=base.get(i);
    		car.setID(i+1);
    		System.out.println(car.getID());
    		mysqlBase.saveToMysqlBase(car);	
    	}*/
    }

	public void emptyDatabaseInfo() {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Informacja");
		alert.setHeaderText(null);
		alert.setContentText("Brak baz danych!");
		alert.showAndWait();
	}
	
	//////////////////////////////////MySql tables//////////////////////////////////////////
	
	private static ArrayList<String> MySqlTablesList;
	MysqlBase mysqlBase=new MysqlBase();
	
	public void showMysqlTables() throws SQLException, ClassNotFoundException {
		MySqlTablesList=mysqlBase.getTables();
    	ObservableList<String> olist=FXCollections.observableArrayList(MySqlTablesList);
    	MySqlTableList.setItems(olist);
	}
	
    @FXML
    private ListView<String> MySqlTableList;
	
	
    @FXML
    void showMySqlTableListAction(ActionEvent event) throws ClassNotFoundException, SQLException {
    	showMysqlTables();
    }
	
    @FXML
    void newMySqlTableAction(ActionEvent event) throws ClassNotFoundException, SQLException {
    	
    	TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle("Nowa tabela");
		dialog.setHeaderText("Nowa tabela");
		dialog.setContentText("Nazwa:");
		Optional<String> result = dialog.showAndWait();
    	
		if (result.isPresent()) {
			String name=result.get();
			mysqlBase.createTable(name);
			showMysqlTables();
		}	
    }
    
    @FXML
    void deleteMySqlTableAction(ActionEvent event) throws ClassNotFoundException, SQLException {
    	mysqlBase.deleteTable(MySqlTableList.getSelectionModel().getSelectedItem());
    	System.out.println(MySqlTableList.getSelectionModel().getSelectedItem());
    	showMysqlTables();
    }
	
    @FXML
    void selectMySqlTableAction(ActionEvent event) throws IOException {
    	File file =new File("lastMySqlTable");
    	FileOutputStream fis=new FileOutputStream(file);
    	DataOutputStream dos=new DataOutputStream(fis);
    	
    	String name=MySqlTableList.getSelectionModel().getSelectedItem();
    	dos.writeUTF(name); 	
    }
	
    @FXML
    void copyMySqlBaseToLocalAction(ActionEvent event) throws ClassNotFoundException, SQLException, IOException {
    	base=mysqlBase.getMysqlBase();
    	File file =new File(MySqlTableList.getSelectionModel().getSelectedItem());
    	FileOutputStream fis=new FileOutputStream(file);
    	ObjectOutputStream oos=new ObjectOutputStream(fis);
    	oos.writeObject(base);	
    	
    	baseList=srbl.getBase();
    	baseList.add(MySqlTableList.getSelectionModel().getSelectedItem());
    	srbl.saveList();
    }
    
	

}
