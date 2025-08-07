package com.projectmanagement.dto.attachment;

/**
 * DTO for uploader information in attachment responses
 */
public class AttachmentUploaderResponse {

    private Long id;
    private String name;

    // Constructors
    public AttachmentUploaderResponse() {}

    public AttachmentUploaderResponse(Long id, String name) {
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
        return "AttachmentUploaderResponse{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
} 