import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class Program1 {

    public static void main(String[] args) {
        //java -classpath c:\Java\mysql-connector-java-8.0.11.jar;c:\Java Program
        try{
            Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();
            String sqlCommand = "CREATE TABLE users (Id INT PRIMARY KEY AUTO_INCREMENT, Login VARCHAR(20), Password VARCHAR(20));";

            try (Connection conn = getConnection()){
                Statement statement = conn.createStatement();
                // создание таблицы
                //statement.executeUpdate("CREATE TABLE users (Id INT PRIMARY KEY AUTO_INCREMENT, Login VARCHAR(20), Password VARCHAR(20))");
                /*создадим метод для ввода пароля и логина и будем сюда добавлять их
                int rows = statement.executeUpdate("INSERT users(Login, Password) VALUES ('Sam', 'dvhdjd')," +
                        "('ergreg', 'euruhfei'), ('frigg', 'fgedhj')");*/

                System.out.println("Database has been created!");
            }
        }
        catch(Exception ex){
            System.out.println("Connection failed...");

            System.out.println(ex);
        }
    }
    public static Connection getConnection() throws SQLException, IOException{

        Properties props = new Properties();
        try(InputStream in = Files.newInputStream(Paths.get("D:\\5 семестр\\Программирование\\lesson 2\\src\\datadata.txt"))){
            props.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String url = props.getProperty("url");
        String username = props.getProperty("username");
        String password = props.getProperty("password");
        return DriverManager.getConnection(url, username, password);
    }
}
