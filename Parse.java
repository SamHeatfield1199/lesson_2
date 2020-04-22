import javax.swing.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.Date;
import java.util.*;


class Auth {

    public static String salt = "abcd";

    public static String doHash(String passwordToHash, String  salt){
        String generatedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(salt.getBytes(StandardCharsets.UTF_8));//Если есть несколько блоков данных для включения в один и тот же дайджест сообщения, вызовите метод update()
            byte[] bytes = md.digest(passwordToHash.getBytes(StandardCharsets.UTF_8));//завершите вызовом digest(). Вот как выглядит вычисление дайджеста сообщения из нескольких блоков данных:
            StringBuilder sb = new StringBuilder();//для производительности
            for(int i=0; i< bytes.length ;i++){
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            generatedPassword = sb.toString();
        }
        catch (NoSuchAlgorithmException e){
            e.printStackTrace();
        }
        return generatedPassword;
    }

    public static boolean checkLogin(String login){
        boolean flag = false;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();
            String sql2 = "SELECT Login FROM users where Login = ?";
            try (Connection conn = getConnection()) {

                PreparedStatement preparedStatement = conn.prepareStatement(sql2);
                preparedStatement.setString(1, login);
                ResultSet resultSet = preparedStatement.executeQuery();
                resultSet.last();
                int rowCount = resultSet.getRow();
                if (rowCount != 0) {
                    System.out.println("Пользователь с таким именем уже существует");
                }else{
                    flag = true;
                }
                }
        }
        catch (Exception ex) {
            System.out.println("Connection failed...");
            System.out.println(ex);
        }
        return flag;


    }

    public static boolean registration(String login, String password) {
        boolean flag = false;

        try {

            Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();
            String sql1 = "INSERT users (Login, Password) Values (?,?)";

            password = doHash(password, salt);

            try (Connection conn = getConnection()) {

                    PreparedStatement preparedStatement = conn.prepareStatement(sql1);
                    preparedStatement.setString(1, login);
                    preparedStatement.setString(2, password);
                    preparedStatement.executeUpdate();

                    java.util.Date date = new Date();
                    Object param = new java.sql.Timestamp(date.getTime());

                    String sqln = "Select Id from users where Login = ?";
                    preparedStatement = conn.prepareStatement(sqln);
                    preparedStatement.setString(1, login);
                    ResultSet res = preparedStatement.executeQuery();
                    int id = 0;
                    while(res.next()) {
                    id = res.getInt("Id");
                    }

                    String sql3 = "INSERT log ( LoginId, Login, EventDate, EventMes) Values (?,?,?,?)";
                    preparedStatement = conn.prepareStatement(sql3);
                    preparedStatement.setObject(3, param);
                    preparedStatement.setInt(1, id);
                    preparedStatement.setString(2, login);
                    preparedStatement.setString(4, "Регистрация");
                    preparedStatement.executeUpdate();
                    flag = true;


            }
        } catch (Exception ex) {
            System.out.println("Connection failed...");
            System.out.println(ex);
        }
        return flag;

    }

    public static boolean doAuth(String login, String password) {

        boolean flag = false;

        password = doHash(password, salt);

        try {
            Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();
            String sql1 = "SELECT Login FROM users where Login = ? and Password = ?";

            try (Connection conn = Program1.getConnection()) {

                PreparedStatement preparedStatement = conn.prepareStatement(sql1);
                preparedStatement.setString(1, login);
                preparedStatement.setString(2, password);
                ResultSet res1 = preparedStatement.executeQuery();
                String idL =" ";
                while(res1.next()) {
                    idL = res1.getString("Login");
                }

                if (idL.equals(login)) {

                    java.util.Date date = new Date();
                    Object param = new java.sql.Timestamp(date.getTime());

                    String sqln = "Select Id from users where Login = ?";
                    preparedStatement = conn.prepareStatement(sqln);
                    preparedStatement.setString(1, login);
                    ResultSet res = preparedStatement.executeQuery();
                    int id =0;
                    while(res.next()) {
                        id = res.getInt("Id");
                    }

                    String sql3 = "INSERT log ( LoginId, Login, EventDate, EventMes) Values (?,?,?,?)";
                    preparedStatement = conn.prepareStatement(sql3);
                    preparedStatement.setObject(3, param);
                    preparedStatement.setInt(1, id);
                    preparedStatement.setString(2, login);
                    preparedStatement.setString(4, "Успешный вход ");
                    preparedStatement.executeUpdate();
                    //System.out.println("Пользователь не найден");
                    flag = true;

                } else {

                    java.util.Date date = new Date();
                    Object param = new java.sql.Timestamp(date.getTime());

                    String sqln = "Select Id from users where Login = ?";
                    preparedStatement = conn.prepareStatement(sqln);
                    preparedStatement.setString(1, login);
                    ResultSet res = preparedStatement.executeQuery();
                    int id =0;
                    while(res.next()) {
                        id = res.getInt("Id");
                    }

                    String sql3 = "INSERT log ( LoginId, Login, EventDate, EventMes) Values (?,?,?,?)";
                    preparedStatement = conn.prepareStatement(sql3);
                    preparedStatement.setObject(3, param);
                    preparedStatement.setInt(1, id);
                    preparedStatement.setString(2, login);
                    preparedStatement.setString(4, "Вход провален ");
                    preparedStatement.executeUpdate();
                }

            }
        } catch (Exception ex) {
            System.out.println("Connection failed...");
            System.out.println(ex);
        }
        return flag;
    }

    public static void date(String login, String value) {
        try (Connection conn = getConnection()) {

            PreparedStatement preparedStatement;
            java.util.Date date = new Date();
            Object param = new java.sql.Timestamp(date.getTime());

            String sqln = "Select Id from users where Login = ?";
            preparedStatement = conn.prepareStatement(sqln);
            preparedStatement.setString(1, login);
            ResultSet res = preparedStatement.executeQuery();
            int id =0;
            while(res.next()) {
                id = res.getInt("Id");
            }

            String sql3 = "INSERT log ( LoginId, Login, EventDate, EventMes) Values (?,?,?,?)";
            preparedStatement = conn.prepareStatement(sql3);
            preparedStatement.setObject(3, param);
            preparedStatement.setInt(1, id);
            preparedStatement.setString(2, login);
            preparedStatement.setString(4, "Параметр изменен на " + value);
            preparedStatement.executeUpdate();

        }
        catch(
    Exception ex)

    {
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

class Parse {

    public HashMap<String, String> Map;//наш массив

    public HashMap parse(String file) throws IOException {

        Map = new HashMap<String, String>();
        BufferedReader reader = new BufferedReader(new FileReader(file));//читаем файл
        String line;
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            //if (line != null && line.length() != 0 && !line.contains("#") && !line.contains(";")) {//проверяем на комментарий
            if( line.length() != 0 && line.charAt(0)!='#' && line.charAt(0)!=';'){
                if (!line.contains(" "))// строка не содержит пробел
                {
                    Map.put(line, null); //добавление ключа без значений
                } else {
                    String value = line.substring(line.indexOf(" ") + 1);//достаем значение
                    String key = line.substring(0, line.indexOf(" "));//достаем ключ
                    Map.put(key, value);
                }
            }
        }
        return Map;
    }

    public void setValue(String key, String value, String file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));//читаем файл
        //записываем в файл
        String line;
        ArrayList<String> array = new ArrayList<>();
        while ((line = reader.readLine()) != null) {

                //if(line.contains(key) && !line.contains("#") && !line.contains(";")){
            if(line.contains(key) && line.charAt(0)!='#' && line.charAt(0)!=';'){
                String com = " ";
                if(line.contains("#")){
                 com = line.substring(line.indexOf("#"));}
                if(line.contains(";")){ com = line.substring(line.indexOf(";"));}

                array.add(key+ " " + value + " " + com);
                }else{
                    array.add(line);
                }
        }
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        for(String newline : array){
            writer.write(newline + "\n");
        }
        writer.flush();
    }
    public void setKey(String param, String key, String file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));//читаем файл
        //записываем в файл
        String line;
        ArrayList<String> array = new ArrayList<>();
        while ((line = reader.readLine()) != null) {

            //if(line.contains(key) && !line.contains("#") && !line.contains(";")){
            if(line.contains(param) && line.charAt(0)!='#' && line.charAt(0)!=';'){
                String com = " ";
                if(line.contains("#")){
                    com = line.substring(line.indexOf("#"));}
                if(line.contains(";")){ com = line.substring(line.indexOf(";"));}

                array.add(key+ " " + com);
            }else{
                array.add(line);
            }
        }
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        for(String newline : array){
            writer.write(newline + "\n");
        }
        writer.flush();
    }

    public boolean containsKey(String line){
        return Map.containsKey(line);
    }//проверка на существование ключа

}

class Programm{

    public static void main(String[] args) throws IOException {
        Console con = System.console();
            String filе = "D:\\5 семестр\\Программирование\\lesson 2\\src\\conf";
            Parse list = new Parse();//экземпляр класса Parse

            HashMap<String, String> Map;//Наш мап
            Map = list.parse(filе) ;//парсим файл
            String param;//переменная для названия ключа

            int num;
            Scanner sc = new Scanner(System.in);

            for(;;) {

                do {

                System.out.println("Нажмите 1 для регистрации, 2 для входа");
                    while (!sc.hasNextInt()) {

                        System.out.println("That not a number!");
                        sc.next(); // this is important!

                    }

                    num = sc.nextInt();

                } while (num <= 0);

                sc.nextLine();

                if(num ==1){

                    boolean flag = false;
                    boolean flagL = false;
                    while (!flag){
                     Scanner scan = new Scanner((System.in));
                        System.out.println("Введите логин и пароль");
                        String login = " ";
                        while(!flagL){
                            System.out.println("Логин:");
                            login = scan.nextLine();
                            flagL = Auth.checkLogin(login);

                        }
                        System.out.println("Пароль:");
                        String pass = scan.nextLine();
                        while(pass.trim().equals("") || pass.contains(" ")){
                            System.out.println("Пароль:");
                            pass = scan.nextLine();
                        }
                        flag = Auth.registration(login, pass);
                    }

                }else {

                    if(num == 2){
                        boolean flag = false;
                        Scanner scan = new Scanner(System.in);
                        String login = " ";
                        String pass;
                        while(!flag){

                            System.out.println("Введите логин и пароль");
                            System.out.println("Логин:");
                            login = scan.nextLine();
                            System.out.println("Пароль:");
                            pass = scan.nextLine();
                            flag = Auth.doAuth(login, pass);
                            if (!flag)
                                System.out.println("Логин или пароль неверны");
                        }

                            for (String lines : Map.keySet() ) //  по keySet выводим название ключей
                            {
                                System.out.println(lines);
                            }
                            do {

                                System.out.println("Для вывода значения параметра нажмите 1, для изменения значения параметра нажмите 2");

                                while (!sc.hasNextInt()) {

                                    System.out.println("That not a number!");
                                    sc.next(); // this is important!

                                }

                                num = sc.nextInt();

                            } while (num <= 0);

                            sc.nextLine();

                            if (num != 1 && num != 2) {

                                System.out.println("вы ошиблись");

                            } else {

                                if (num == 1) {

                                    System.out.println("Введите название параметра \n Для выхода введите exit");

                                    param = sc.nextLine();
                                    if (param.equals("exit")) {

                                        break;
                                    }

                                    if (!list.containsKey(param)) {

                                        System.out.println("Нет такого названия");

                                    } else {

                                        System.out.println(Map.get(param));//возвращаем значение ключа

                                    }

                                } else {

                                    System.out.println("Введите название параметра \n Для выхода введите exit");
                                    param = sc.nextLine();

                                    if (param.equals("exit")) {

                                        break;
                                    }

                                    if (!list.containsKey(param)) {

                                        System.out.println("Нет такого названия");

                                    } else {

                                        System.out.println(param + " " + Map.get(param));
                                        if (Map.get(param).charAt(0) == '#' ||Map.get(param).charAt(0) == ';'|| Map.get(param) == null ||Map.get(param).replaceAll(" ", "").charAt(0) == '#' ||Map.get(param).replaceAll(" ", "").charAt(0) == ';' ){
                                            System.out.println("У параметра нет значения, введите новое название параметр");
                                            String key = sc.nextLine();
                                            list.setKey(param, key, filе);
                                            Auth.date(login, key);
                                            //add

                                        }else {
                                            System.out.println("Введите новое значение ключа. Если их несколько, введите через пробел. Если хотите ввести пустое значение нажмите пробел");
                                            String value = sc.nextLine();
                                            list.setValue(param, value, filе);
                                            Auth.date(login, value);
                                            //add
                                        }
                                        //System.out.println(value);

                                        }
                                    }
                                }

                        }
                    }
                }
            }
        }
