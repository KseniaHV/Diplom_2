package user;
public class UserLogin {
     private String email;
     private String password;
        public UserLogin(){
        }
        public UserLogin(String email, String password) {
            this.email = email;
            this.password = password;
        }
        public static UserLogin from(User user){
            return new UserLogin(user.getEmail(), user.getPassword());
        }
        public static UserLogin incorrectData() {
            UserLogin invalidUser = new UserLogin("tufiii333@yandex.ru", "098765");
            return invalidUser;
    }

    public String getEmail() {
            return email;
        }
        public void setEmail(String email) {
            this.email = email;
        }
        public String getPassword() {
            return password;
        }
        public void setPassword(String password) {
            this.password = password;
        }
    }


