package br.usp.lucas.applicationbackend.user.dto;

import java.util.Objects;

public class UserWriteDto {
    private String name;
    private String username;
    private String email;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserWriteDto that = (UserWriteDto) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(username, that.username) &&
                Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, username, email);
    }

    @Override
    public String toString() {
        return "UserWriteDto{" +
                "name='" + name + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
