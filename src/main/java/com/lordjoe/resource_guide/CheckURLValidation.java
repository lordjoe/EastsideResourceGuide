package com.lordjoe.resource_guide;

import com.lordjoe.resource_guide.util.URLValidator;

/**
 * com.lordjoe.resource_guide.CheckURLValidation
 * User: Steve
 * Date: 4/27/25
 */
public class CheckURLValidation {
    public static final CheckURLValidation[] EMPTY_ARRAY = {};

    public static final String[] BAD_URLS = {
            "https://app.leg.wa.gov/districtfinder",
            "https://app.leg.wa.gov/districtfinder",
            "https://ccsww.org/get-help/services-for-seniors-people-with-disabilities/volunteer-services/volunteer-services-king-county/",
            "https://elderoptionswa.com/safe%40home-1",
            "https://ccsww.org/get-help/services-for-seniors-people-with-disabilities/home-care/",
            "https://signaturehch.com/",
            "https://www.pugetsoundlaboragency.org/wheelchair-ramp-program.html",
            "https://soundgenerations.org/our-programs/minor-home-repair/",
            "https://www.pic.org/care-management.htm",
            "https://ccsww.org/get-help/services-for-seniors-people-with-disabilities/african-american-elders-program/",
            "https://soundgenerations.org/our-programs/senior-rights-assistance/",
            "https://consejocounseling.org/portfolio/valve-diseases/",
            "https://www.lni.wa.gov/claims/crime-victim-claims/who-can-file-and-what-is-covered/",
            "https://www.lifewire.org/get-help/legal-support/",
            "https://forms.gle/qNr8P9Cvhzq6w66k9).",
            "https://soundgenerations.org/get-help/insurance-legal/your-rights/",
            "https://soundgenerations.org/our-programs/grat/",
            "https://www.4tomorrow.today/programs",
            "http://www.elcentrodelaraza.org/what-we-do/education-and-asset-building",
            "https://carnationwa.govoffice2.com/vertical/sites/%7BBC2C8B0D-6FDD-43CB-A5E7-03E465DF30E5%7D/uploads/Utility_Account_Application_-_FILLABLE.pdf?&pri=0",
            "https://www.wholecatandkaboodle.com/adoptions",
            "https://www.kirklandwa.gov/Government/Departments/Parks-and-Community-Services/Register-for-a-R",
            "https://www.redmond.gov/409/Art-Studio-at-Grass-Lawn-Park",
            "https://ccsww.org/get-help/services-for-seniors-people-with-disabilities/volunteer-services/volunteer-services-king-county/",
            "https://soundgenerations.org/our-programs/transportation/hyde-shuttle/",
            "https://snofallslicensing.com/",
            "https://vets.force.com/VAVERA/s/.",
            "https://wsdot.wa.gov/travel/511-travel-information",
            "https://curohealthservices.com/locations/gentiva-hospice-seattle/",
            "https://www.hopelink.org/need-help/mobile-market",
            "https://www.lifelong.org/welcome-home",
            "http://www.motivationsrecoverycenter.com/",
            "https://wa.kaiserpermanente.org/html/public/specialties/cardiology",
            "https://confidentialstdtestingcenters-stdtestingservice.business.site/",
            "https://website-593855303609607720976-stdtestingservice.business.site",
            "http://sasgcc.org/support",
            "https://evergreenhealth.healthlines.org/wlp2/#!/classes/search",
            "https://hearingloss-wa.org/what-we-do/hope-hearing-other-peoples-experiences/",
            "https://sphsc.washington.edu/apply-services",
            "https://curohealthservices.com/locations/gentiva-hospice-seattle/"
    };

    public static void main(String[] args) {
        for (int i = 0; i < BAD_URLS.length; i++) {
            String url = BAD_URLS[i];
            System.out.println(url);
            boolean valid = URLValidator.isValidURL(url);
        }
    }
}
