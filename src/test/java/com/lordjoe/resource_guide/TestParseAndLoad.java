package com.lordjoe.resource_guide;

import com.lordjoe.resource_guide.dao.ResourceType;
import com.lordjoe.resource_guide.model.CommunityResource;
import com.lordjoe.resource_guide.util.DatabaseConnection;
import com.lordjoe.resource_guide.util.SecretFetcher;
import com.lordjoe.resource_guide.util.StringUtils;
import com.lordjoe.resource_guide.util.WordDocParser;

import java.io.File;
import java.util.List;

/**
 * com.lordjoe.resource_guide.TestParseAndLoad
 * User: Steve
 * Date: 5/3/25
 */
public class TestParseAndLoad {
    public static final TestParseAndLoad[] EMPTY_ARRAY = {};


    public static void main(String[] args) throws Exception {

        assert SecretFetcher.getPasswordfromSecret().equals(SecretFetcher.getPasswordfromSecretB());
        DatabaseConnection.setTEST();
        if (true) {
            Database.clearDatabase();
            Database.createDatabase();
            File dir = new File("test");
            if (dir.exists() && dir.isDirectory()) {
                File f = new File(dir, "TestCatagory.docx");
                WordDocParser.parseAndInsertDocx(f);
             }
        }
        Guide guide = Guide.Instance;
        guide.guaranteeLoaded();
        List<Catagory> catagories = guide.getCatagories();
        assert catagories.size() == 2;
        Catagory catagory = guide.getCatagoryByName("TestCatagory");
        assert catagory.getId() == TestResources.TestCatagoryId;
        assert catagory.getName().equals("TestCatagory");
        String descript = catagory.getDescription();
        assert StringUtils.equivalentText(descript,TestResources.CATEGORY_DESCRIPTION);
        List<Resource> blocks = catagory.getBlocks();
        assert blocks.size() == 1;
        Resource block = blocks.get(0);
        assert  StringUtils.equivalentText(block.getDescription(),TestResources.CATEGORY_BLOCK);

        List<SubCatagory> subCatagories = catagory.getSubCatagories();
        assert subCatagories.size() == 2;

        Catagory s1 = catagory.getSubCatagory("Section1");
        assert s1 != null;
        assert s1.getId() == TestResources.Section1Id;
        if (!StringUtils.equivalentText(s1.getDescription(), TestResources.SECTION1_DESCRIPT ))
            throw new UnsupportedOperationException("Bad Section 1 Description "); // ToDo;
        blocks = s1.getBlocks();
        assert blocks.size() == 0;

        List<Resource> resources = s1.getResources();
        assert resources.size() == 2;
        Resource r1 = s1.getResource("Asian Counseling & Referral Service (ACRS)");
        CommunityResource cr1 = new CommunityResource(r1);
        cr1.setType(ResourceType.Resource);
        if (!CommunityResource.equivalentText(cr1, TestResources.ASIAN, true))
            throw new UnsupportedOperationException("Fix This"); // ToDo;
        blocks = r1.getBlocks();
        assert blocks.size() == 0;

        Resource r2 = s1.getResource("Bellevue Mini City Hall – Crossroads Shopping Center");
        CommunityResource cr2 = new CommunityResource(r2);
        cr2.setType(ResourceType.Resource);
        if (!CommunityResource.equivalentText(cr2, TestResources.BELLEVUE, true))
            throw new UnsupportedOperationException("Fix This"); // ToDo;
        blocks = r2.getBlocks();
        assert blocks.size() == 0;

        Catagory s2 = catagory.getSubCatagory("Section2");
        assert s2 != null;
        assert s2.getId() == TestResources.Section2Id;
        if (!StringUtils.equivalentText(s2.getDescription(), TestResources.SECTION2_DESCRIPT ))
            throw new UnsupportedOperationException("Bad  Section 2 Description "); // ToDo;
        blocks = s2.getBlocks();
        assert blocks.size() == 0;

        Resource r3 = s2.getResource(TestResources.Chinese.getName());
        CommunityResource cr3 = new CommunityResource(r3);
        cr3.setType(ResourceType.Resource);
        if (!CommunityResource.equivalentText(cr3, TestResources.Chinese, true))
            throw new UnsupportedOperationException("Fix This"); // ToDo;
        r3 = s2.getResource(TestResources.NorthBellevue.getName());
        blocks = r3.getBlocks();
        assert blocks.size() == 0;

        cr3 = new CommunityResource(r3);
        cr3.setType(ResourceType.Resource);
        if (!CommunityResource.equivalentText(cr3, TestResources.NorthBellevue, true))
            throw new UnsupportedOperationException("Fix This"); // ToDo;

        r3 = s2.getResource(TestResources.Isaquah.getName());
        cr3 = new CommunityResource(r3);
        if (!CommunityResource.equivalentText(cr3, TestResources.Isaquah, true))
            throw new UnsupportedOperationException("Fix This"); // ToDo;
        blocks = r3.getBlocks();
        assert blocks.size() == 0;
        
        List<GuideItem> children = r3.getChildren();
        assert children.size() == 2;

        GuideItem child = r3.getChild("Redmond Post 161");
        assert child != null;
        assert child instanceof Resource;
        cr3 = new CommunityResource((Resource) child);

        if (!CommunityResource.equivalentText(cr3, TestResources.RedPost, true))
            throw new UnsupportedOperationException("Fix This"); // ToDo;

        child = r3.getChild("Bothell Post 127");
        assert child != null;
        assert child instanceof Resource;
        cr3 = new CommunityResource((Resource) child);

        if (!CommunityResource.equivalentText(cr3, TestResources.BothelPost, true))
            throw new UnsupportedOperationException("Fix This"); // ToDo;


    }
}
