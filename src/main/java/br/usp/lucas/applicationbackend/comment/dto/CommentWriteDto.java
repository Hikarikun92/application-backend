package br.usp.lucas.applicationbackend.comment.dto;

import java.util.Objects;

public class CommentWriteDto {
    private Integer postId;
    private String title;
    private String body;
    private String email;

    public Integer getPostId() {
        return postId;
    }

    public void setPostId(Integer postId) {
        this.postId = postId;
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
        CommentWriteDto that = (CommentWriteDto) o;
        return Objects.equals(postId, that.postId) &&
                Objects.equals(title, that.title) &&
                Objects.equals(body, that.body) &&
                Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(postId, title, body, email);
    }

    @Override
    public String toString() {
        return "CommentWriteDto{" +
                "postId=" + postId +
                ", title='" + title + '\'' +
                ", body='" + body + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
