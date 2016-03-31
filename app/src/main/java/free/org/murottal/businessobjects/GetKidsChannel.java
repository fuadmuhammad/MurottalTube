package free.org.murottal.businessobjects;

import java.io.IOException;

import free.org.murottal.businessobjects.GetYouTubeVideoBySearch;

/**
 * Created by muhammad.fuad on 3/31/2016.
 */
public class GetKidsChannel extends GetYouTubeVideoBySearch {

    private String query;
    public GetKidsChannel(String s){
        super();
        query = s;
    }
    @Override
    public void init() throws IOException {
        super.init();
        videosList.setQ(query);
    }
}

