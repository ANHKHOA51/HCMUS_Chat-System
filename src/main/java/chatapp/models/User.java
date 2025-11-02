package chatapp.models;

import java.time.LocalDateTime;

public class User {
    private int id;
    private String userAccount;
    private String userName;
    private String address;
    private String birthday;
    private boolean gender;
    private String email;
    private String createAt;
    private String updateAt;
    private String status;

    public User() {

    }

    public User(int id, String userAccount, String userName, String address, String birthday, boolean gender,
            String email, String status) {
        this.id = id;
        this.userAccount = userAccount;
        this.userName = userName;
        this.address = address;
        this.birthday = birthday;
        this.gender = gender;
        this.email = email;
        this.status = status;

        this.createAt = "" + LocalDateTime.now();
        this.updateAt = "" + LocalDateTime.now();
    }

    public int getId() {
        return id;
    }

    public String getUserAccount() {
        return userAccount;
    }

    public String getUserName() {
        return userName;
    }

    public String getAddress() {
        return address;
    }

    public String getEmail() {
        return email;
    }

    public boolean getGender() {
        return gender;
    }

    public String getBirthday() {
        return birthday;
    }

    public String getCreateAt() {
        return createAt;
    }

    public String getUpdateAt() {
        return updateAt;
    }

    public String getStatus() {
        return status;
    }
}
