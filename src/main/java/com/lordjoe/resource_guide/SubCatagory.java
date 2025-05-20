package com.lordjoe.resource_guide;

/**
 * Represents a SubCategory under a Category.
 */
public class SubCatagory extends Catagory {
    private final Catagory parent;

    public SubCatagory(int id,String name, Catagory parent) {
        super(id,name);
        this.parent = parent;
        parent.addSubCatagory(this);
    }

    @Override
    public Catagory getCatagory() {
        return parent;
    }
}
