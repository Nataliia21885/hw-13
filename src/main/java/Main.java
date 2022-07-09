import Task_Information.Tasks;
import Users_Information.Address;
import Users_Information.Company;
import Users_Information.Geo;
import Users_Information.User;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;

public class Main {
    public static final Gson GSON = new Gson();
    public static final int DEFAULT_USER_ID = 1;
    public static final String DEFAULT_USERNAME = "Nataliia Hrybyniuk";

    public static void main(String[] args) throws IOException, InterruptedException {

        // создание нового объекта
        System.out.println("Create a new user");
        User userToCreate = createNewUser();
        System.out.println("userToCreate  = " + userToCreate);
        User createdUser = GSON.fromJson(HttpClientUtil.newUser(userToCreate), User.class);
        System.out.println("createdUser = " + createdUser);
        System.out.println("---------------------------------------------------------------");

        // обновление объекта
        System.out.println("Upgrade the user");
        User upgradedUser = new User();
        upgradedUser.setName(createdUser.getName());
        upgradedUser.setUsername("New Username");
        upgradedUser.setEmail(createdUser.getEmail());
        upgradedUser.setAddress(createdUser.getAddress());
        upgradedUser.setPhone(createdUser.getPhone());
        upgradedUser.setWebsite(createdUser.getWebsite());
        upgradedUser.setCompany(createdUser.getCompany());
        String s = HttpClientUtil.upgradeUser(DEFAULT_USER_ID,upgradedUser);
        User checkUpdatedUser = GSON.fromJson(s,User.class);
        System.out.println(checkUpdatedUser);
        System.out.println("---------------------------------------------------------------");

        //удаление объекта
        System.out.println("Delete the User");
        createdUser.setId(DEFAULT_USER_ID);
        System.out.println("Delete operation " + HttpClientUtil.deleteUser(createdUser));
        System.out.println("---------------------------------------------------------------");

        //получение информации обо всех пользователях
        System.out.println("Getting all Users");
        List<User> allUsers = HttpClientUtil.getAllUsers();
        allUsers.forEach(System.out::println);
        System.out.println("---------------------------------------------------------------");

        //получение информации о пользователе с определенным id
        System.out.println("Get user by Id:1 ");
        System.out.println("User by Id number = " + HttpClientUtil.getUserById(DEFAULT_USER_ID));
        System.out.println("---------------------------------------------------------------");

        //получение информации о пользователе с опредленным username
        System.out.println("Get information of User: Kurtis Weissnat");
        System.out.println("UserByName = " + HttpClientUtil.getUserByUserName(DEFAULT_USERNAME));
        System.out.println("---------------------------------------------------------------");

        //метод, который выводит все комментарии к последнему посту определенного пользователя и записывает их в файл
        System.out.println("Get all comment to User with Id ");
        System.out.println(HttpClientUtil.getAllCommentsToLastPostOfUser(createdUser));
        System.out.println("---------------------------------------------------------------");

        //метод, который выводит все открытые задачи для пользователя Х.
        System.out.println("Get all opened tasks for User with Id 1 ");
        System.out.println("\nList of opened tasks:\n");
        List<Tasks> allOpenTasks = HttpClientUtil.getListOfOpenTasksForUser(createdUser);
        allOpenTasks.forEach(System.out::println);
    }

    private static User createNewUser() {
        User user = new User();
        user.setId(DEFAULT_USER_ID);
        user.setName("Nataliia");
        user.setUsername("Nataliia");
        user.setEmail("nataliia@test.com");
        user.setAddress(createAddress());
        user.setPhone("8 - 098 - 884 - 7299");
        user.setWebsite("java.ua");
        user.setCompany(createCompany());
        return user;
    }

        private static Address createAddress() {
        Address address = new Address();
        address.setStreet("Kablukova Street");
        address.setSuite("21");
        address.setCity("Kyiv");
        address.setZipcode("13500");
        Geo geo = new Geo();
        geo.setLng("50.43723601197116");
        geo.setLat("30.407760193983723");
        address.setGeo(geo);
        return address;
    }

    private static Company createCompany() {
        Company company = new Company();
        company.setName("NSDgroup");
        company.setCatchPhrase("Interior design development");
        company.setBs("interior designer services");
        return company;
    }

}
