package com.shoot.database;

import com.shoot.PostHidden.LoadingNotification;
import com.shoot.go.ScreenPlay;
import javafx.application.Platform;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;
import java.sql.*;
import java.util.ArrayList;

/**
 * Created by Beloved on 23-Feb-18.
 */
public class StartDatabaseLite {
     Stage pStage;
    static Connection connection;
    final static String OFFICIAL_DATABASE="screenplay";
    final static String OFFICIAL_TABLE="tablesetting";
    final static String OFFICIAL_TABLE_AUTO_COMPLETE="tableautocomplete";
    final static String OFFICIAL_TABLE_PICTURE="tablepicture";
    public final static String getTableName(){
        return OFFICIAL_TABLE;
    }
    public final static String getTableAutoName(){
        return OFFICIAL_TABLE_AUTO_COMPLETE;
    }
     LoadingNotification loadingNotification;

    //==================================================================================================================
    //database for verification and heavy database stuff
    //as database is loading and verifying, progressbar is being updated
    public final  void initializeDatabase(Stage stage) throws ClassNotFoundException, SQLException, InterruptedException {
        loadingNotification=new LoadingNotification("Loading...",8,new ProgressBar());
        pStage=stage;
        Platform.runLater(this::stage);
        loadingNotification.setCount(0);
        Thread.sleep(1000);
        Class.forName("org.sqlite.JDBC");
        System.out.println("Driver loaded");
        loadingNotification.setCount(1);
        Thread.sleep(1000);
        Connection conn=  DriverManager.getConnection("jdbc:sqlite:"+OFFICIAL_DATABASE);
        connection=conn;
        loadingNotification.setCount(2);
        Thread.sleep(1000);
        validateTable( conn);
        loadingNotification.setCount(3);
        Thread.sleep(1000);
        setDefaultRow( conn);
        loadingNotification.setCount(4);
        Thread.sleep(1000);
        setCustomRow(conn);
        loadingNotification.setCount(5);
        Thread.sleep(1000);
        // auto complete table
        validateTableAutoComplete(conn);
        loadingNotification.setCount(6);
        Thread.sleep(1000);
        validateTablePictureSwitch(conn);
        loadingNotification.setCount(7);
        Thread.sleep(1000);
        setTablePictureSwitch(conn);
        Thread.sleep(1000);
        loadingNotification.setCount(8);
        Thread.sleep(1000);
        Platform.runLater(this::close);
        Platform.runLater(() -> {
            try {
                Thread.sleep(500);
                new ScreenPlay().main(new Stage());
            } catch (SQLException  | InterruptedException e) {
                e.printStackTrace();
            }
        });
    }
//======================================================================================================================
    /*
    database methods for verification
     */
    public   Connection getConnection(){
        return  connection;
    }
    public  void closeConnection() throws SQLException {
        getConnection().close();
    }
      private void  validateTable(Connection conn) throws SQLException {
        ResultSet rt=conn.getMetaData().getTables(null, null, OFFICIAL_TABLE,null);
        if(rt.next()){
            System.out.println("Table exist");
        }else{
            createTable(conn);
            System.out.println("Table created");
        }
    }

     private void  createTable(Connection conn) throws SQLException {
        Statement statement=  conn.createStatement();
        statement.executeUpdate("create table "+OFFICIAL_TABLE+"(id int(5),nameSetting varchar(50), color varchar(50) , file varchar(255),opacity double(15,2) )");
    }
     private void  setDefaultRow(Connection conn) throws SQLException {
        Statement st=  conn.createStatement();
        ResultSet resultSet= st.executeQuery("select nameSetting from "+OFFICIAL_TABLE+" where id='1'");
        if(resultSet.next()){
            System.out.println("default row exist");
        }else{
            System.out.println("default row does not exist. Already creating...");
            st.executeUpdate("insert into "+OFFICIAL_TABLE+"(id ,nameSetting , color  , file ,opacity)"+"values(1,'default','#ffffff','"+System.getProperty("user.home").replaceAll((char) 92+ ""+(char) 92,(char)47 +"")+"',0.4);");
        }
    }
    private void  setCustomRow(Connection conn) throws SQLException {
        Statement st=  conn.createStatement();
        ResultSet resultSet= st.executeQuery("select nameSetting from "+OFFICIAL_TABLE+" where id='2'");
        if(resultSet.next()){
            System.out.println("default row exist");
        }else{
            System.out.println("custom row does not exist. Already creating...");
            st.executeUpdate("insert into "+OFFICIAL_TABLE+"(id ,nameSetting , color  , file ,opacity)"+"values('2','custom','#ffffff','"+(System.getProperty("user.home").replaceAll((char) 92+ ""+(char) 92,(char)47 +""))+"','0.4');");
        }
    }
    public   void  setUpdateCustomRow(Connection conn,String updateStatement) throws SQLException {
        Statement st= conn.createStatement();
        st.executeUpdate(updateStatement);
        System.out.println("Update Successful");
    }
    public   String  getUpdateCustomRow(Connection conn,String selectStatement,int columnPosition) throws SQLException {
        Statement st=  conn.createStatement();
        ResultSet result=st.executeQuery(selectStatement);
        if(result.next()){
            System.out.println("Getting update");
            return result.getString(columnPosition);
        }
        return null;
    }

     private void  validateTableAutoComplete(Connection conn) throws SQLException {
        ResultSet rt=conn.getMetaData().getTables(null, null, OFFICIAL_TABLE_AUTO_COMPLETE,null);
        if(rt.next()){
            System.out.println("Table exist");
        }else{
            createTableAutoComplete(conn);
            System.out.println("Table created");
        }
    }
     private void  createTableAutoComplete(Connection conn) throws SQLException {
        Statement statement=  conn.createStatement();
        statement.executeUpdate("create table "+OFFICIAL_TABLE_AUTO_COMPLETE+"(id integer PRIMARY KEY  AUTOINCREMENT, name varchar(50))");
    }
    public  void  setTableAutoComplete(Connection conn,String word) throws SQLException {
        if(checkElementExist(conn,word)){
            //do nothing
        }else {
            Statement st = conn.createStatement();
            st.executeUpdate("insert into " + OFFICIAL_TABLE_AUTO_COMPLETE + "(name)values(\"" + word + "\")");
        }
    }
    public   ArrayList<String>  getTableAutoComplete(Connection conn) throws SQLException {
        ArrayList<String> list=new ArrayList<>();
        Statement st=  conn.createStatement();
        ResultSet result=st.executeQuery(("select name from "+OFFICIAL_TABLE_AUTO_COMPLETE));
        while(result.next()){
            System.out.println("Getting strings");
            list.add(result.getString(1));
        }
        if(list.isEmpty()){
            list.add("Screen1");
        }
        return list;
    }
    private  boolean checkElementExist(Connection conn, String word) throws SQLException {
        if(getTableAutoComplete(conn).contains(word))
            return  true;
        return  false;
    }

         private void  validateTablePictureSwitch(Connection conn) throws SQLException {
        ResultSet rt=conn.getMetaData().getTables(null, null, OFFICIAL_TABLE_PICTURE,null);
        if(rt.next()){
            System.out.println("Table exist");
        }else{
            createTablePictureSwitch(conn);
            System.out.println("Table created");
        }
    }
   private  void  createTablePictureSwitch(Connection conn) throws SQLException {
        Statement statement=  conn.createStatement();
        statement.executeUpdate("create table "+OFFICIAL_TABLE_PICTURE+"(id int(5),pictureSwitch int(5),file varchar(255))");
    }
   private  void  setTablePictureSwitch(Connection conn) throws SQLException {
        Statement st=  conn.createStatement();
        ResultSet resultSet= st.executeQuery("select pictureSwitch from "+OFFICIAL_TABLE_PICTURE+" where id='0'");
        if(resultSet.next()){
            System.out.println("default row exist");
        }else{
            System.out.println("picture row does not exist. Already creating...");
            st.executeUpdate("insert into "+OFFICIAL_TABLE_PICTURE+"(id,pictureSwitch,file )"+"values('0','0','"+"a.jpg".replaceAll((char) 92+ ""+(char) 92,(char)47 +"")+"');");
        }
    }
    public   void  setUpdatedTablePictureSwitch(Connection conn,int _switch) throws SQLException {
        String updateStatement="update "+OFFICIAL_TABLE_PICTURE+" set pictureSwitch= '"+_switch+"' where id='0'";
        Statement st= conn.createStatement();
        st.executeUpdate(updateStatement);
        System.out.println("Update Successful");
    }
    public   int  getUpdatedTablePictureSwitch(Connection conn) throws SQLException {
        String statmentGet="select pictureSwitch from "+OFFICIAL_TABLE_PICTURE+" where id='0'";
        Statement st=  conn.createStatement();
        ResultSet result=st.executeQuery(statmentGet);
        if(result.next()){
            System.out.println("Getting update");
            return Integer.parseInt(result.getString(1));
        }
        return 0;
    }
    public   void  setUpdatedTablePicture(Connection conn,String picAddress) throws SQLException {
        String updateStatement="update "+OFFICIAL_TABLE_PICTURE+" set file= '"+picAddress.replaceAll((char) 92+ ""+(char) 92,(char)47 +"")+"' where id='0'";
        Statement st= conn.createStatement();
        st.executeUpdate(updateStatement);
        System.out.println("Update Successful");
    }
    public   String  getUpdatedTablePicture(Connection conn) throws SQLException {
        String statmentGet="select file from "+OFFICIAL_TABLE_PICTURE+" where id='0'";
        Statement st=  conn.createStatement();
        ResultSet result=st.executeQuery(statmentGet);
        if(result.next()){
            System.out.println("Getting update");
            return result.getString(1);
        }
        return "a.jpg" ;
    }
    @Deprecated
     void  createDatabase(Connection conn) throws SQLException {
        System.out.println("Create database");
        Statement statement=  conn.createStatement();
        statement.executeUpdate("create database "+OFFICIAL_DATABASE);
    }
    @Deprecated
     boolean hasCreatedDatabase(Connection conn) throws SQLException {
        ResultSet rs=conn.getMetaData().getCatalogs();

        ArrayList<String> databaseList=new ArrayList<>();
        while (rs.next()){
            databaseList.add(rs.getString(1));
        }

        if(databaseList.contains(OFFICIAL_DATABASE)){
            System.out.println("Database exist");
            return  true;
        }
        else{
            System.out.println("Database does not exist");
            return  false;
        }
    }
     void  stage(){
        try {
            loadingNotification.start(pStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
     void  close(){
        try {
            loadingNotification.closeStage();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

