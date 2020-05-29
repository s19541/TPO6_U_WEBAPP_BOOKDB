package zad1;



import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.*;

public class MyDataBase {
    static final String driver = "org.apache.derby.jdbc.EmbeddedDriver";
    String url = "jdbc:derby:myDB;create=true";
    Connection connection;
   private void createConnection() {
        try {
            Class.forName(driver);
            connection = DriverManager.getConnection(url);
            if (connection != null)
                System.out.println("\nPomyslnie polaczono z baza danych");

        } catch (Exception e) {
            System.out.println("Problem z polaczeniem z bazą danych");
            e.printStackTrace();
            System.exit(2);
        }
    }
   public String createDb(String authorsPath,String booksPath){
        createConnection();
        try{
            try {
                connection.createStatement().execute("drop table BOOK");
                connection.createStatement().execute("drop table AUTHOR");
            }catch(Exception e){}
            connection.createStatement().execute("create table AUTHOR (\n" +
                    "        AuthorId int NOT NULL,\n" +
                    "        FirstName varchar(255) NOT NULL,\n" +
                    "        LastName varchar(255) NOT NULL,\n" +
                    "\tPRIMARY KEY(AuthorId)\n" +
                    "        )");
            connection.createStatement().execute("create table BOOK (\n" +
                    "        BookId int NOT NULL,\n" +
                    "\tTitle varchar(255) NOT NULL,\n" +
                    "\tAuthorId int NOT NULL,\n" +
                    "\tPrice real NOT NULL,\n" +
                    "\tPRIMARY KEY(BookId),\n" +
                    "\tFOREIGN KEY (AuthorId) REFERENCES AUTHOR(AuthorId)\n" +
                    "        )");
            System.out.println("Pomyslnie utworzono baze danych");
        }
        catch(Exception e){
            System.out.println("Problem z utworzeniem bazy danych");
            e.printStackTrace();
            return("Problem z utworzeniem bazy danych\n"+e.getMessage());
        }
        try{
            connection.createStatement().execute("CALL SYSCS_UTIL.SYSCS_IMPORT_TABLE (null,'AUTHOR','"+authorsPath+"',null,null,null,0)");
            connection.createStatement().execute("CALL SYSCS_UTIL.SYSCS_IMPORT_TABLE (null,'BOOK','"+booksPath+"',null,null,null,0)");
            System.out.println("Pomyslnie dodano rekordy do bazy danych");
        }
        catch(Exception e){
            System.out.println("Problem z dodaniem wartosci do bazy danych");
            e.printStackTrace();
            return("Problem z dodaniem wartosci do bazy danych\n"+e.getMessage());
        }
            return "Pomyslnie utworzono baze danych i dodano do niej wartosci";
    }
    public ResultSet getResultFromQuery(String query) throws SQLException{
        Statement statement = connection.createStatement(java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE, java.sql.ResultSet.CONCUR_UPDATABLE);
        return statement.executeQuery(query);
    }
}
