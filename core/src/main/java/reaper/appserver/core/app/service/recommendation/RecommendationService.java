package reaper.appserver.core.app.service.recommendation;

import org.apache.log4j.Logger;
import reaper.appserver.config.ConfLoader;
import reaper.appserver.config.ConfResource;
import reaper.appserver.core.app.service.http.BasicJsonParser;
import reaper.appserver.core.app.service.http.HttpService;
import reaper.appserver.core.framework.exceptions.BadRequest;
import reaper.appserver.core.framework.exceptions.ServerError;
import reaper.appserver.log.LogUtil;

import java.util.*;

public class RecommendationService
{
    private static Logger log = LogUtil.getLogger(RecommendationService.class);

    private static Map<Category, List<String>> categoryMap;

    private static final int RECOMMENDATION_COUNT = Integer.parseInt(ConfLoader.getConf(ConfResource.RECOMMENDATION).get("recommendation.count"));

    private static final String GOOGLE_API_KEY = ConfLoader.getConf(ConfResource.RECOMMENDATION).get("recommendation.google.api_key");
    private static final String GOOGLE_PLACES_SEARCH_URL = ConfLoader.getConf(ConfResource.RECOMMENDATION).get("recommendation.google.url");
    private static int GOOGLE_PLACES_SEARCH_RADIUS = Integer.parseInt(ConfLoader.getConf(ConfResource.RECOMMENDATION).get("recommendation.google.search_radius"));

    private HttpService httpService;

    static
    {
        categoryMap = new HashMap<>();

        categoryMap.put(Category.GENERAL, new ArrayList<>());
        categoryMap.put(Category.EAT_OUT, Arrays.asList("food", "restaurant", "bar", "cafe", "bakery"));
        categoryMap.put(Category.DRINKS, Arrays.asList("bar", "night_club", "restaurant"));
        categoryMap.put(Category.CAFE, Arrays.asList("cafe"));
        categoryMap.put(Category.MOVIES, Arrays.asList("movie_theater"));
        categoryMap.put(Category.OUTDOORS, Arrays.asList("amusement_park", "aquarium", "art_gallery", "bowling_alley",
                "campground", "casino", "gym", "health", "library", "museum", "park", "spa", "stadium", "zoo"));
        categoryMap.put(Category.PARTY, new ArrayList<>());
        categoryMap.put(Category.LOCAL_EVENTS, new ArrayList<>());
        categoryMap.put(Category.SHOPPING, Arrays.asList("shopping_mall", "clothing_store", "electronics_store",
                "florist", "jewelry_store", "shoe_store"));
    }

    public RecommendationService()
    {
        httpService = new HttpService();
    }

    public List<Recommendation> getRecommendations(String latitudeStr, String longitudeStr, String categoryStr)
    {
        if (latitudeStr == null || latitudeStr.isEmpty() || longitudeStr == null || longitudeStr.isEmpty())
        {
            throw new ServerError("Unable to get recommendations; location is null/empty");
        }

        if (categoryStr == null || categoryStr.isEmpty())
        {
            throw new ServerError("Unable to get recommendations; event category is null/empty");
        }

        Double latitude = null;
        Double longitude = null;
        try
        {
            latitude = Double.parseDouble(latitudeStr);
            longitude = Double.parseDouble(longitudeStr);
        }
        catch (Exception e)
        {
            throw new ServerError("Unable to get recommendations; invalid location coordinates");
        }

        Category category = null;
        try
        {
            category = Category.valueOf(categoryStr);
        }
        catch (Exception e)
        {
            throw new BadRequest("Unable to get recommendations; invalid event category");
        }

        List<Recommendation> recommendations = getGooglePlacesRecommendations(latitude, longitude, category);
        return recommendations;
    }

    private List<Recommendation> getGooglePlacesRecommendations(double latitude, double longitude, Category category)
    {
        List<String> categoryTags = new ArrayList<>();
        if (categoryMap.containsKey(category))
        {
            categoryTags = categoryMap.get(category);
        }

        if (categoryTags.size() > 0)
        {
            String typeQueryStr = String.join("%7C", categoryTags);

            StringBuilder urlBuilder = new StringBuilder(GOOGLE_PLACES_SEARCH_URL);
            urlBuilder.append("?location=");
            urlBuilder.append(latitude);
            urlBuilder.append(",");
            urlBuilder.append(longitude);
            urlBuilder.append("&radius=");
            urlBuilder.append(GOOGLE_PLACES_SEARCH_RADIUS);
            urlBuilder.append("&types=");
            urlBuilder.append(typeQueryStr);
            urlBuilder.append("&key=");
            urlBuilder.append(GOOGLE_API_KEY);
            String url = urlBuilder.toString();

            log.info("[Google Places API request] " + url.replace(GOOGLE_API_KEY, "..."));

            try
            {
                String jsonResponse = httpService.get(url);
                String status = BasicJsonParser.getValue(jsonResponse, "status");

                if (status.equalsIgnoreCase("OK"))
                {
                    List<Recommendation> recommendations = new ArrayList<>();

                    List<String> results = BasicJsonParser.getList(jsonResponse, "results");

                    for (String result : results)
                    {
                        Recommendation recommendation = parseGooglePlacesResult(result);
                        if (!recommendations.contains(recommendation))
                        {
                            recommendations.add(recommendation);
                        }

                        if (recommendations.size() == RECOMMENDATION_COUNT)
                        {
                            break;
                        }
                    }

                    return recommendations;
                }
                else
                {
                    log.error("Unable to fetch Google Places Recommendation [ Response Code = " + status + "]");
                }
            }
            catch (Exception e)
            {
                log.error("Unable to fetch Google Places Recommendation [" + e.getMessage() + "]");
            }
        }

        return new ArrayList<>();
    }

    private Recommendation parseGooglePlacesResult(String json)
    {
        String name = BasicJsonParser.getValue(json, "name");
        String description = BasicJsonParser.getValue(json, "vicinity");
        String rating = BasicJsonParser.getValue(json, "rating");
        String iconurl = BasicJsonParser.getValue(json, "icon");

        String locationJson = BasicJsonParser.getValue(BasicJsonParser.getValue(json, "geometry"), "location");
        String latitude = BasicJsonParser.getValue(locationJson, "lat");
        String longitude = BasicJsonParser.getValue(locationJson, "lng");

        return new Recommendation(name, latitude, longitude, description, rating, iconurl);
    }
}
