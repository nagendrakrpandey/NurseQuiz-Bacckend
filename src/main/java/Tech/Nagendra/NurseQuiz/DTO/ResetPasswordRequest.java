package Tech.Nagendra.NurseQuiz.DTO;

public class ResetPasswordRequest {

    private String password;
    private String confirmPassword;

    public ResetPasswordRequest() {
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}