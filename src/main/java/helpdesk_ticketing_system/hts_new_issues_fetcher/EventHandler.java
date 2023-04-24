package helpdesk_ticketing_system.hts_new_issues_fetcher;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;

public class EventHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private final MongoDb mongoDb;
    private final ObjectMapper objectMapper;
    private final Integer MAX_PAGINATION_LIMIT;

    public EventHandler() {
        mongoDb = new MongoDb();
        objectMapper = new ObjectMapper();
        MAX_PAGINATION_LIMIT = Integer.parseInt(System.getenv("max_page_data_limit"));
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {
        if(requestEvent==null)
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(HttpURLConnection.HTTP_INTERNAL_ERROR)
                    .withBody("Received Null Request from API Gateway");

        context.getLogger().log("request event : " + requestEvent + "\n");

        // extracting the query params
        Map<String,String> queryParams = requestEvent.getQueryStringParameters();
        context.getLogger().log("query params : " + queryParams + "\n");

        long postedOnStartRange; // required
        if(queryParams.containsKey("r_s")){
            postedOnStartRange = Long.parseLong(queryParams.get("r_s"));
        }
        else {
            try {
                return new APIGatewayProxyResponseEvent()
                        .withStatusCode(HttpURLConnection.HTTP_BAD_REQUEST)
                        .withBody(objectMapper.writeValueAsString("\"message\":\"Query Parameter 'r_s' is not present.\""));
            } catch (JsonProcessingException e) {
                context.getLogger().log(
                        "Exception Class : " + e.getClass().getName() + "\tMessage : " + e.getMessage() + "\n"
                );
                throw new RuntimeException(e);
            }
        }

        // optional
        Long postedOnEndRange=null;
        if (queryParams.containsKey("r_e"))
            postedOnEndRange = Long.parseLong(queryParams.get("r_e"));

        // optional
        Integer limit;
        if(queryParams.containsKey("limit")) {
            limit = Math.min(Integer.parseInt(queryParams.get("limit")), MAX_PAGINATION_LIMIT);
        }
        else
            limit = MAX_PAGINATION_LIMIT;

        List<Issue> issues = mongoDb.getIssues(postedOnStartRange,postedOnEndRange,limit,context);
        int count = issues.size();

        try
        {
            GetIssueResponse response = new GetIssueResponse();
            if(count>0)
            {
                response = new GetIssueResponse(
                        count,
                        issues,
                        issues.get(0).getPostedOn(),
                        issues.get(count-1).getPostedOn()
                );

                return new APIGatewayProxyResponseEvent()
                        .withStatusCode(HttpURLConnection.HTTP_OK)
                        .withBody(objectMapper.writeValueAsString(response));
            }
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(HttpURLConnection.HTTP_OK)
                    .withBody(objectMapper.writeValueAsString(response));
        }
        catch (JsonProcessingException e) {
            context.getLogger().log(
                    "Exception Class : " + e.getClass().getName() + "\tMessage : " + e.getMessage() + "\n"
            );
            throw new RuntimeException(e);
        }
    }
}
