package com.projectmanagement.dto.comment;

/**
 * DTO for user information in comment responses
 */
public class CommentUserResponse {

    private Long id;
    private String name;

    // Constructors
    public CommentUserResponse() {}

    public CommentUserResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "CommentUserResponse{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
} 