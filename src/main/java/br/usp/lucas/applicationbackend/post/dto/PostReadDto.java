package br.usp.lucas.applicationbackend.post.dto;

import br.usp.lucas.applicationbackend.user.dto.UserReadDto;

import java.util.Objects;

public class PostReadDto {
    private int id;
    private String title;
    private String body;
    private UserReadDto user;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public UserReadDto getUser() {
        return user;
    }

    public void setUser(UserReadDto user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PostReadDto that = (PostReadDto) o;
        return id == that.id &&
                Objects.equals(title, that.title) &&
                Objects.equals(body, that.body) &&
                Objects.equals(user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, body, user);
    }

    @Override
    public String toString() {
        return "PostReadDto{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", body='" + body + '\'' +
                ", user=" + user +
                '}';
    }
}
