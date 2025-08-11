package com.lordjoe.resource_guide;

import com.lordjoe.resource_guide.dao.ResourceType;
import com.lordjoe.resource_guide.model.CommunityResource;

import java.util.*;

/**
 * Represents a Category that can hold Resources, SubCategories, and Blocks.
 */
public class Catagory extends GuideItem {

    public Catagory(int id,String name) {
        super(id,name, ResourceType.Category,null   );
    }

    protected Catagory(int id,String name,ResourceType type,Integer parentId) {
        super(id,name, type, parentId   );
    }

      public Resource getResource(String name) {
            GuideItem ret = getChild(name);
            if(ret instanceof Resource)
                return (Resource) ret;
            else
                return null;
      }

    public Catagory getSubCatagory(String name) {
        GuideItem ret = getChild(name);
        if(ret instanceof Catagory)
            return (Catagory) ret;
        else
            return null;
     }


    public void addSubCatagory(SubCatagory subcat) {

        addChild(subcat);
    }

    public void addResource(CommunityResource ress) {
        Resource r = Resource.getInstance(ress.getId(),ress.getName(),this);
        r.populateFrom(ress);

    }

    public List<SubCatagory> getSubCatagories() {
        List<SubCatagory> result = new ArrayList<>();
        for (GuideItem subCatagory : getChildren()) {
            if(subCatagory instanceof SubCatagory)
                result.add((SubCatagory) subCatagory);
        }
        Collections.sort(result);
        return result;
    }

//    public SubCatagory getOrCreateSubCatagory(String name) {
//        return subcatagories.computeIfAbsent(name, n -> new SubCatagory(n, this));
//    }


    @Override
    public Catagory getCatagory() {
        return this;
    }
}
