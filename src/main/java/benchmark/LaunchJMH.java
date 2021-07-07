package benchmark;

import com.jeroenreijn.examples.Launch;
import org.openjdk.jmh.annotations.*;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

/**
 * Some code on this class has been sampled from https://stackoverflow.com/a/41499972
 */
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Thread)
public class LaunchJMH {
    private static final String[] templates = {
            "jsp",
            "freemarker",
            "velocity",
            "thymeleaf",
            "jade",
            "scalate",
            "mustache",
            "pebble",
            "handlebars",
            "jtwig",
            "httl",
            "chunk",
            "htmlFlow",
            "trimou",
            "rocker",
            "ickenham",
            "rythm",
            "groovy",
            "liqp"
    };

    private static ConfigurableApplicationContext context;
    private static MockMvc mockMvc;
    private static boolean isMock = false;
    private static String domainUrl = "/";

    @Setup(Level.Trial)
    public synchronized void startupSpring() {
        try {
            if (context == null) {
                context = SpringApplication.run(Launch.class);
                if (isMock) {
                    WebApplicationContext webApplicationContext = (WebApplicationContext) context;
                    mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                            .build();
                } else {
                    String port = context.getEnvironment().getProperty("local.server.port");
                    domainUrl = "http://localhost:" + port + domainUrl;
                }
            }
        } catch (Exception e) {
            //Force JMH crash
            throw new RuntimeException(e);
        }
    }

    @TearDown(Level.Trial)
    public synchronized void shutdownSpring() {
        try {
            if (context != null) {
                SpringApplication.exit(context);
                context = null;
            }
        } catch (Exception e) {
            //Force JMH to crash when there is an exception
            throw new RuntimeException(e);
        }
    }

    private String benchmarkTemplate(int templateIdx) {
        String url = domainUrl + templates[templateIdx];
        try {
            return isMock ? mockMvcGetRequest(url) : httpGetRequest(url);
        } catch (Exception e) {
            //Force JMH crash
            throw new RuntimeException(e);
        }
    }

    /**
     * Performs a request to an url.
     *
     * @param urlStr The request url string.
     * @return The response string.
     * @throws RuntimeException IOException.
     */
    private String httpGetRequest(String urlStr) throws IOException {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(urlStr);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/xml");
            InputStream inputStream = connection.getInputStream();
            return inputStreamToString(inputStream);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private String mockMvcGetRequest(String url) throws Exception {
        ResultActions res = mockMvc.perform(
                get(url)
                        .accept(MediaType.ALL_VALUE)
//                    .accept(MediaType.TEXT_HTML)

        ).andDo(print());
        MockHttpServletResponse response = res.andReturn().getResponse();
        String forwardedUrl = response.getForwardedUrl();
        if (forwardedUrl != null) {
            res = mockMvc.perform(
                    get(forwardedUrl)
                            .accept(MediaType.ALL_VALUE)
//                                .accept(MediaType.TEXT_HTML)

            ).andDo(print());
            response = res.andReturn().getResponse();
        }
        return response.getContentAsString();
    }

    /**
     * Helper method for {@link #httpGetRequest}'s {@link InputStream} to String conversion.
     * Most performant out of other options:
     * https://stackoverflow.com/questions/309424/how-do-i-read-convert-an-inputstream-into-a-string-in-java
     *
     * @param inputStream The input stream to convert to string.
     * @return The resulting string.
     */
    private String inputStreamToString(InputStream inputStream) throws IOException {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        for (int length; (length = inputStream.read(buffer)) != -1; ) {
            result.write(buffer, 0, length);
        }
        return result.toString(StandardCharsets.UTF_8.name());
    }

    @Benchmark
    public String benchmarkJsp() {
        return benchmarkTemplate(0);
    }
    @Benchmark
    public String benchmarkFreemarker() {
        return benchmarkTemplate(1);
    }
//    @Benchmark
//    public void benchmarkVelocity(LaunchJMH state, Blackhole bh) {
//        benchmarkTemplate(2);
//    }
    @Benchmark
    public String benchmarkThymeleaf() {
        return benchmarkTemplate(3);
    }
//    @Benchmark
//    public void benchmarkJade(LaunchJMH state, Blackhole bh) {
//        benchmarkTemplate(4);
//    }@Benchmark
//    public void benchmarkScalate(LaunchJMH state, Blackhole bh) {
//        benchmarkTemplate(5);
//    }
//    @Benchmark
//    public void benchmarkMustache(LaunchJMH state, Blackhole bh) {
//        benchmarkTemplate(6);
//    }
//    @Benchmark
//    public void benchmarkPebble(LaunchJMH state, Blackhole bh) {
//        benchmarkTemplate(7);
//    }
//    @Benchmark
//    public void benchmarkHandlebars(LaunchJMH state, Blackhole bh) {
//        benchmarkTemplate(8);
//    }
//    @Benchmark
//    public void benchmarkJtwig(LaunchJMH state, Blackhole bh) {
//        benchmarkTemplate(9);
//    }
//    @Benchmark
//    public void benchmarkHttl(LaunchJMH state, Blackhole bh) {
//        benchmarkTemplate(10);
//    }
//    @Benchmark
//    public void benchmarkChunk(LaunchJMH state, Blackhole bh) {
//        benchmarkTemplate(11);
//    }
//    @Benchmark
    public String benchmarkHtmlFlow() {
        return benchmarkTemplate(12);
    }
//    @Benchmark
//    public void benchmarkTrimou(LaunchJMH state, Blackhole bh) {
//        benchmarkTemplate(13);
//    }
//    @Benchmark
//    public String benchmarkRocker() {
//        return benchmarkTemplate(14);
//    }
//    @Benchmark
//    public void benchmarkIckenham(LaunchJMH state, Blackhole bh) {
//        benchmarkTemplate(15);
//    }
//    @Benchmark
//    public void benchmarkRythm(LaunchJMH state, Blackhole bh) {
//        benchmarkTemplate(16);
//    }
//    @Benchmark
//    public void benchmarkGroovy(LaunchJMH state, Blackhole bh) {
//        benchmarkTemplate(17);
//    }
//    @Benchmark
//    public void benchmarkLiqp(LaunchJMH state, Blackhole bh) {
//        benchmarkTemplate(18);
//    }

}
