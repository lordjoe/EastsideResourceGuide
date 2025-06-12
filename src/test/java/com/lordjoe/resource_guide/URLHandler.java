package com.lordjoe.resource_guide;

import com.lordjoe.resource_guide.util.SecretFetcher;
import com.lordjoe.resource_guide.util.URLValidator;

import java.util.ArrayList;
import java.util.List;

    /**
     * com.lordjoe.resource_guide.TestParseAndLoad
     * User: Steve
     * Date: 5/3/25
     */
    public class URLHandler {
        public static final com.lordjoe.resource_guide.TestParseAndLoad[] EMPTY_ARRAY = {};


        public static void main(String[] args) throws Exception {

            assert SecretFetcher.getPasswordfromSecret().equals(SecretFetcher.getPasswordfromSecretB());

             Guide guide = Guide.Instance;
            guide.guaranteeLoaded();

            List<Resource> catagories = guide.getCommunityResources();
            List<String> websites = new ArrayList<String>();
            List<String> goodsites = new ArrayList<String>();
            List<String> badsites = new ArrayList<String>();
            for (Resource catagory : catagories) {
                if(catagory.getWebsite() != null)
                    websites.add(catagory.getWebsite());
            }
            for (String site : websites) {
                if(URLValidator.isValidURL(site))
                    goodsites.add(site);
                else
                    badsites.add(site);

            }
            System.out.println("BAD SITES");
            for (String badsite : badsites) {
                try {
                    System.out.println( badsite);
                } catch (Exception e) {
                    System.out.println();

                }
            }
            System.out.println("GOOD SITES");
            for (String badsite : goodsites) {
                try {
                    System.out.println( badsite);
                } catch (Exception e) {
                    System.out.println();
                }
            }



        }
    }
