package cz.matejprerovsky.covid19.services;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.ArrayUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Service
public class CovidDataService {

    private final static String URL = "https://onemocneni-aktualne.mzcr.cz/api/v2/covid-19/zakladni-prehled.csv";
    private Map<String, String> data;

    @PostConstruct
    @Scheduled(cron = "0 1 * * * *") // every hour at :01 minutes (i hope) bcs im not sure what's the time they update the data
    private void fetchData() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL))
                .build();
        HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
        String[] lines = httpResponse.body().split("\n");
        String[] names = lines[0].substring(1, lines[0].length()-1).split(",");
        String[] values = lines[1].substring(0, lines[1].length()-1).split(",");        // I have to remove last character because of CR at the end of returned string

        DecimalFormat formatter = new DecimalFormat("#, ###");

        data = IntStream.range(0, names.length)
                .boxed()
                .collect(Collectors.toMap(
                        x -> names[x],
                        x -> (isInteger(values[x])) ? formatter.format(Integer.parseInt(values[x])) : values[x]
                ));

        System.out.println(data);
    }

    public Map<String, String> getData() {
        return data;
    }
    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch(NumberFormatException e) {
            return false;
        } catch(NullPointerException e) {
            return false;
        }
        // only got here if we didn't return false
        return true;
    }
}
