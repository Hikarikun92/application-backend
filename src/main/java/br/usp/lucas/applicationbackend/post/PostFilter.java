package br.usp.lucas.applicationbackend.post;

import br.usp.lucas.applicationbackend.user.UserFilter;

import java.util.Objects;

public class PostFilter {
    private String title;
    private String body;
    private UserFilter user;

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

    public UserFilter getUser() {
        return user;
    }

    public void setUser(UserFilter user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PostFilter that = (PostFilter) o;
        return Objects.equals(title, that.title) &&
                Objects.equals(body, that.body) &&
                Objects.equals(user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, body, user);
    }

    @Override
    public String toString() {
        return "PostFilter{" +
                "title='" + title + '\'' +
                ", body='" + body + '\'' +
                ", user=" + user +
                '}';
    }
}
