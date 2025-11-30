package org.delcom.app.dto;

import java.util.UUID;

public class TodoForm {
    
    private UUID id;
    private String title;
    private String description;
    private boolean finished;
    private String confirmTitle;

    // Constructor Kosong
    public TodoForm() {
    }

    // Constructor Lengkap
    public TodoForm(UUID id, String title, String description, boolean finished, String confirmTitle) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.finished = finished;
        this.confirmTitle = confirmTitle;
    }

    // Getters & Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean getFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    // Helper method untuk kompatibilitas (Jaga-jaga)
    public boolean getIsFinished() {
        return finished;
    }

    public void setIsFinished(boolean finished) {
        this.finished = finished;
    }

    public String getConfirmTitle() {
        return confirmTitle;
    }

    public void setConfirmTitle(String confirmTitle) {
        this.confirmTitle = confirmTitle;
    }
}