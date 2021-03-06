package com.whereyoudey.service;

import com.whereyoudey.service.helper.Result;
import java.io.IOException;
import java.rmi.RemoteException;

import com.whereyoudey.webservice.ArrayOfString;
import com.whereyoudey.webservice.Search_Stub;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import org.kxml2.io.KXmlParser;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import searchhelper.SearchHelper_Stub;

public class SearchService {

    public static final int MAX_RESULTS = 10;

    public Result[] searchBusinessData(String name, String location, ArrayOfString filter) throws RemoteException, XmlPullParserException, IOException {
        Result[] result = null;
        Search_Stub service = new Search_Stub();
        String[] searchResult = service.SearchData(name, location, filter).getString();
        String resultAsXml = searchResult[0];
        System.out.println(resultAsXml);
        result = processResponse(resultAsXml, "SearchResults", "Result");
        return result;
    }

    private Result[] processResponse(String result, final String rootTag, final String elementTag) throws XmlPullParserException, IOException {
        final byte[] resultBytes = result.getBytes();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(resultBytes);
        InputStreamReader inputStreamReader = new InputStreamReader(byteArrayInputStream);
        KXmlParser parser = new KXmlParser();
        parser.setInput(inputStreamReader);

        parser.nextTag();
        parser.require(XmlPullParser.START_TAG, null, rootTag);
        parser.nextTag();

        int eventCode = parser.getEventType();
        String tagName = "";
        Result resultObj = null;
        int resultCount = 0;
        Result[] resultList = new Result[MAX_RESULTS];

        boolean processingResult = false;
        boolean reachedMaxResults = false;

        while (eventCode != XmlPullParser.END_DOCUMENT && !reachedMaxResults) {
            switch (parser.getEventType()) {
                case XmlPullParser.START_TAG:
                    tagName = parser.getName();
                    if (elementTag.equals(tagName)) {
                        resultObj = new Result();
                        processingResult = true;
                    }
                    break;
                case XmlPullParser.END_TAG:
                    tagName = parser.getName();
                    if (elementTag.equals(tagName)) {
                        resultList[resultCount] = resultObj;
                        processingResult = false;
                        resultCount++;
                        if (resultCount == MAX_RESULTS) {
                            reachedMaxResults = true;
                        }
                    }
                    break;
                case XmlPullParser.TEXT:
                    String text = parser.getText();
                    if (processingResult) {
                        resultObj.addProperty(tagName, text);
                    }
                    break;
            }
            parser.next();
            eventCode = parser.getEventType();
        }
        return resultList;
    }

    public Result[] searchEvents(String city) {
        Result[] events = new Result[0];
        try {
            Search_Stub search_Stub = new Search_Stub();
            String eventsAsXml = search_Stub.GetCityEvents(city);
            System.out.println(eventsAsXml);
            events = processResponse(eventsAsXml, "SearchResults", "Result");
        } catch (XmlPullParserException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return events;
    }

    public Result[] searchMovies(String city) {
        Result[] movies = new Result[0];
        try {
            Search_Stub search_Stub = new Search_Stub();
            String moviesAsXmlS = search_Stub.GetCityTheaters(city);
            System.out.println(moviesAsXmlS);
            movies = processResponse(moviesAsXmlS, "SearchResults", "Result");
        } catch (XmlPullParserException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return movies;
    }

    public Result[] getBanners() {
        Result[] banners = new Result[0];
        try {
            SearchHelper_Stub stub = new SearchHelper_Stub();
            final String mainBannersAsXmlS = stub.getMainBanners();
            System.out.println("Banners -> " + mainBannersAsXmlS);
            banners = processResponse(mainBannersAsXmlS, "SearchResults", "Result");
        } catch (XmlPullParserException ex) {
            ex.printStackTrace();
        } catch (RemoteException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return banners;
    }

    public Result getDetailsFromHelper(String bannerId) {
        try {
            SearchHelper_Stub stub = new SearchHelper_Stub();
            final String bannerDetailsAsXml = stub.getBusinessDetail(bannerId);
            final Result[] banners = processResponse(bannerDetailsAsXml, "SearchResults", "Result");
            if (banners.length > 0) {
                return banners[0];
            }
        } catch (XmlPullParserException ex) {
            ex.printStackTrace();
        } catch (RemoteException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
