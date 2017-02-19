package mysql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import base.Book;
import controllers.MainController;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class MysqlBase {

	private Connection con = null;
	private MainController mainControler;

	public Connection getConnection() throws ClassNotFoundException {
		Class.forName("com.mysql.jdbc.Driver");
		try {
			con= DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/base"
					+ "?autoReconnect=true&useSSL=false","root","1234");
		} catch (SQLException e) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("WARNING");
			alert.setHeaderText("Lost connection!");
			alert.showAndWait();
			try {
				mainControler.loadMenu();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		System.out.println("connect");
		
		return con;
	}

	public void closeConnection() throws SQLException{
		con.close();
		System.out.println("close");
	}
	
	public ArrayList<Book> getBooks() throws SQLException, ClassNotFoundException, IOException{
		Connection con=getConnection();
		PreparedStatement getbase=con.prepareStatement("SELECT * FROM Books"+
				 " ORDER BY BookID ASC");
		ResultSet rs=getbase.executeQuery();
		
		ArrayList<Book> list=new ArrayList<Book>();
		String title,author,ISBN,available;
		int ID;
		while(rs.next()){
			ID=rs.getInt("BookID");
			title=rs.getString("Title");
			author=rs.getString("Author");
			ISBN=rs.getString("ISBN");
			available=rs.getString("Available");
			Book book=new Book(ID,title,author,ISBN,available);
			list.add(book);
		}
		closeConnection();
		return list;		
	}
	
	public void deleteFromBooks(int BookID) throws ClassNotFoundException, SQLException, IOException
	{
		System.out.println(BookID);
		Connection con=getConnection();
		PreparedStatement delete = con.prepareStatement("DELETE FROM Books "
			+ " WHERE BookID="+BookID+"");
		delete.executeUpdate();
	}
	
	public void saveToBooks(Book book) throws ClassNotFoundException, SQLException, IOException{
		Connection con=getConnection();
		PreparedStatement save = con.prepareStatement("INSERT INTO Books(Title,Author,ISBN)"
			+ " VALUES('"+book.getTitle()+"','"+book.getAuthor()
			+"','"+book.getISBN()+"')");
		save.executeUpdate();	
		closeConnection();	
	}
	
	public void updateBooksRecord(int ID,String title,String author,String ISBN) 
			throws ClassNotFoundException, SQLException, IOException{
		Connection con=getConnection();
		PreparedStatement update = con.prepareStatement("UPDATE  Books"
				+ " SET Title='"+title+"',Author='"+author+"',ISBN='"+ISBN
				+ "' WHERE BookID="+ID);
		update.executeUpdate();
		closeConnection();
	}
	
	public void updateBookStatus(int ID,String status) 
			throws ClassNotFoundException, SQLException, IOException{
		Connection con=getConnection();
		PreparedStatement update = con.prepareStatement("UPDATE  Books"
				+ " SET Available='"+status
				+ "' WHERE BookID="+ID);
		update.executeUpdate();
		closeConnection();
	}
	
	
	public void createTable(String tableName) throws ClassNotFoundException, SQLException{
	
			Connection con=getConnection();
		try{
			PreparedStatement create=con.prepareStatement("CREATE TABLE IF NOT EXISTS "+tableName
					+ "(ID int,Title varchar(20),Author varchar(20),ISBN varchar(20),PRIMARY KEY(ID))"
					+ "");
			create.executeUpdate();
		}catch(Exception e){
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("ERROR");
			alert.setHeaderText("Nieprawidłowa nazwa!");
			alert.showAndWait();
		}
		closeConnection();
	}
	
	public ArrayList<String> getTables() throws SQLException, ClassNotFoundException{
		ArrayList<String> tables=new ArrayList<String>();
		Connection con=getConnection();
		PreparedStatement show=con.prepareStatement("SHOW tables");
		ResultSet rs=show.executeQuery();
		
		while(rs.next()){
			tables.add(rs.getString(1));
		}
		closeConnection();
		return tables;	
	}
	
	public void deleteTable(String tableName) throws ClassNotFoundException{
		Connection con = getConnection();
		try {
			PreparedStatement delete = con.prepareStatement("DROP TABLE "+tableName);
			delete.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}		
	}
	
	public String getMySqlTableName() throws IOException{
		String name = null;
		try {
			FileInputStream fis = new FileInputStream("lastMySqlTable");
			DataInputStream dis = new DataInputStream(fis);
			name=dis.readUTF();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}		
		return name;
	}
	
	
	
		
}
