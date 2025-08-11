package com.lordjoe.resource_guide;

import com.lordjoe.resource_guide.dao.CommunityResourceDAO;
import com.lordjoe.resource_guide.dao.ResourceType;
import com.lordjoe.resource_guide.model.CommunityResource;

/**
 * com.lordjoe.resource_guide.TestResources
 * User: Steve
 * Date: 5/5/25
 */
public class TestResources {
    public static final TestResources[] EMPTY_ARRAY = {};

    public static final String CATEGORY_DESCRIPTION = "This is Category Annotation";
    public static final String SECTION1_DESCRIPT = "This is the first Section";
    public static final String SECTION2_DESCRIPT = "This is the second Section";
    public static final String CATEGORY_BLOCK = "\n" +
            "<p>Adult Day Program Terms To Know</p>\n" +
            "<p><b>Adult Day Care (ADC)</b>: A program which provides day time socialization," +
            " meals, and activities primarily for persons with physical and/or mental limitations." +
            " Participants return home each evening. Often used as respite for those caring for" +
            " an older adult who cannot safely be left alone at home.</p>\n";

    public static String ASIAN_DESCRIPT = "Provides a broad range of social services" +
            " for elderly Asian Pacific Americans in King County" +
            " including communication assistance for 40 languages, " +
            "family and individual counseling, advocacy, meals, substance abuse " +
            "counseling, and case management. For mental health services, " +
            "ACRS accepts Medicaid, Medicare, and most private insurance plans." +
            " Other programs and classes are offered on a sliding-fee scale based" +
            " on household income";
    public static final String ASIAN_NAME = "Asian Counseling & Referral Service (ACRS)";

    private static int idIndex = 2;
    public static final Integer TestCatagoryId = idIndex++; // 2;
    public static final Integer Section1Id = idIndex++; // 3;
     public static final Integer AsianId = idIndex++; // 4;
    public static final Integer DuvallID = idIndex++; // 5;
    public static final Integer WoodinvilleId = idIndex++; // 5;
    public static final Integer YarrowPointID = idIndex++; // 5;
    public static final Integer BellvueID = idIndex++; // 5;
    public static final Integer Section2Id = idIndex++; // 6;
     public static final Integer ChineseID = idIndex++; // 7;
    public static final Integer NorthBellevueID =idIndex++; // 8;
    public static final Integer IsaquahID = idIndex++; //9;
    public static final Integer RedPostID =idIndex++; // 10;
    public static final Integer BothelPostID =idIndex++; // 11;


    public static CommunityResource ASIAN = CommunityResourceDAO.create(AsianId,ASIAN_NAME, ResourceType.Resource,Section1Id);
    public static CommunityResource DUVALL = CommunityResourceDAO.create(DuvallID,"Duvall City Hall", ResourceType.Resource,AsianId);
    public static CommunityResource WOODINVILLE = CommunityResourceDAO.create(WoodinvilleId,"Woodinville City Hall", ResourceType.Resource,AsianId);
    public static CommunityResource YARROW = CommunityResourceDAO.create(YarrowPointID,"Yarrow Point Town Hall", ResourceType.Resource,AsianId);
    public static CommunityResource BELLEVUE = CommunityResourceDAO.create(BellvueID,"Bellevue Mini City Hall – Crossroads Shopping Center", ResourceType.Resource,Section1Id);
    public static CommunityResource Chinese  = CommunityResourceDAO.create(ChineseID,"Chinese Information & Service Center (CISC)", ResourceType.Resource,Section2Id);
    public static CommunityResource NorthBellevue  = CommunityResourceDAO.create(NorthBellevueID,"North Bellevue Community Center", ResourceType.Resource,Section2Id);
    public static CommunityResource Isaquah = CommunityResourceDAO.create(IsaquahID,"Issaquah Library", ResourceType.Resource,Section1Id);
    public static CommunityResource RedPost = CommunityResourceDAO.create(RedPostID,"Redmond Post 161", ResourceType.Resource,IsaquahID);
    public static CommunityResource BothelPost = CommunityResourceDAO.create(BothelPostID,"Bothell Post 127", ResourceType.Resource,IsaquahID);
    public static  CommunityResource[] Test_Resources = {
            ASIAN,BELLEVUE,Chinese,NorthBellevue,Isaquah,RedPost,BothelPost
    } ;
    static {
        try {
            ASIAN.setDescription(ASIAN_DESCRIPT);
            ASIAN.setPhone("(206) 695-7600");
            ASIAN.setWebsite("www.acrs.org");
            ASIAN.setEmail("info@acrs.org");
            ASIAN.setAddress("655 156th Ave SE, Ste 255 Bellevue, WA  98007");

            WOODINVILLE.setPhone("(425) 489-2700");
            WOODINVILLE.setAddress("17301 133rd Ave NE Woodinville, WA 98072");

            YARROW.setPhone("(425) 454-6994");
            YARROW.setAddress("Duvall, WA 98109");


            DUVALL.setPhone("(425)  788-1185");
            DUVALL.setAddress("4030 95th Ave NE Yarrow Point, WA 98004");

            BELLEVUE.setDescription("Mini City Hall is a neighborhood service center located inside the Crossroads Mall that extends City services and community connections to East Bellevue’s diverse population. This service center specializes in personalized customer service, provided by a friendly staff, in five different languages.");
            BELLEVUE.setPhone("(425) 452-2800");
            BELLEVUE.setEmail("Minich@bellevuewa.gov");
            BELLEVUE.setWebsite("www.bellevuewa.gov/mini-city-hall");
            BELLEVUE.setAddress("15600 NE 8th St, Ste H-9 Bellevue, WA 98008");
            BELLEVUE.setHours("Mon-Sat, 10AM -6PM");

            Chinese.setDescription("Provides services to immigrants including immigrant transition, adult day center, caregiver support, in-home care, and case management. ");
            Chinese.setPhone("(206) 624-5633");
            Chinese.setPhone("www.cisc-seattle.org");
            Chinese.setEmail("info@cisc-seattle.org");
            Chinese.setWebsite("www.cisc-seattle.org");

            NorthBellevue.setDescription("a center");
            NorthBellevue.setHours("Wednesdays 8:45AM-3PM");
            NorthBellevue.setPhone("(206) 957-8525");
            NorthBellevue.setAddress("4063 148th Ave NE Bellevue, WA 98007");

            Isaquah.setDescription("a library");
            Isaquah.setHours("Wednesdays 10AM-1PM ");
            Isaquah.setAddress("10 W Sunset Way Issaquah, WA 98027");


            RedPost.setPhone("(425) 883-0161 ");
            RedPost.setWebsite("www.post161.org");
            RedPost.setAddress("4330 148th Ave NE Redmond, WA 98052");

            BothelPost.setPhone("(425) 483-5599 ");
            BothelPost.setEmail("bothellpost127@outlook.com");
            BothelPost.setAddress("21910 State Rte 9 SE Woodinville, WA 98072");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
