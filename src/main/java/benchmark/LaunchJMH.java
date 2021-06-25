package benchmark;

import com.jeroenreijn.examples.Launch;
import org.openjdk.jmh.annotations.*;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.concurrent.TimeUnit;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

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

    static ConfigurableApplicationContext context;
    static MockMvc mockMvc;



    @Setup(Level.Trial)
    public synchronized void startupSpring() {
        try {
            if (context == null) {
                context = SpringApplication.run(Launch.class);
                WebApplicationContext webApplicationContext = (WebApplicationContext) context;
                mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                        .build();
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
            //Force JMH crash
            throw new RuntimeException(e);
        }
    }

    private String benchmarkTemplate(int templateIdx) {
        try {
            ResultActions res = mockMvc.perform(
                get("/" + templates[templateIdx])
                    .accept(MediaType.ALL_VALUE)
            );
            return res.andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            //Force JMH crash
            throw new RuntimeException(e);
        }
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
    @Benchmark
    public String benchmarkHtmlFlow() {
        return benchmarkTemplate(12);
    }
//    @Benchmark
//    public void benchmarkTrimou(LaunchJMH state, Blackhole bh) {
//        benchmarkTemplate(13);
//    }
    @Benchmark
    public String benchmarkRocker() {
        return benchmarkTemplate(14);
    }
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
