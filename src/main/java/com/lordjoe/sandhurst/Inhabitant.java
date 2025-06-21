package com.lordjoe.sandhurst;

public class Inhabitant {
    private int id;
    private int resourceId; // Foreign key to House.id
    private String name;
    private String phone;
    private String email;
    private InhabitantType type;

    public Inhabitant(int id, int resourceId, String name, String phone, String email, InhabitantType type) {
        this.id = id;
        this.resourceId = resourceId;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public int getResourceId() {
        return resourceId;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public InhabitantType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "%s (%s)".formatted(name, type);
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setType(InhabitantType type) {
        this.type = type;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }

    public House getHouse() {
       return Neighborhood.Instance.getHouse(resourceId);
    }
}
