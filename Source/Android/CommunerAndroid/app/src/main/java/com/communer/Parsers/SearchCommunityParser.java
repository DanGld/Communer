package com.communer.Parsers;

import com.communer.Models.CommunitySearchObj;
import com.communer.Models.LocationObj;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by יובל on 13/10/2015.
 */
public class SearchCommunityParser {

    public static ArrayList<CommunitySearchObj> parseCommunities(JSONArray mCommunities) {
        ArrayList<CommunitySearchObj> communitiesArray = new ArrayList<CommunitySearchObj>();

        for (int i=0; i <mCommunities.length(); i++){
            try {
                JSONObject obj = mCommunities.getJSONObject(i);
                String id = obj.getString("communityID");
                String name = obj.getString("name");
                String imageURL = obj.getString("imageURL");

                JSONObject locObj = obj.getJSONObject("location");
                String locTitle = locObj.getString("title");
                String coords = locObj.getString("coords");
                LocationObj comLocaion = new LocationObj(locTitle,coords);

                String phone = obj.getString("phone");
                String email = obj.getString("email");
                String website = obj.getString("website");
                String type = obj.getString("type");

                communitiesArray.add(new CommunitySearchObj(id,name,imageURL,comLocaion,phone,email,website,type));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return communitiesArray;
    }
}
