package com.jeroenreijn.benchmark;

import com.jeroenreijn.examples.Launch;
import com.jeroenreijn.examples.controller.PresentationsController;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.ui.ModelMap;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * Some code on this class has been sampled from https://stackoverflow.com/a/41499972
 */
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MINUTES)
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
    private static final HttpServletRequest[] mockRequests = Arrays.stream(templates)
            .map(templateStr -> new MockHttpServletRequest("GET", "/" + templateStr))
            .toArray(MockHttpServletRequest[]::new);

    public static void main(String[] args) throws Exception {
        Options opt = new OptionsBuilder()
                .include(LaunchJMH.class.getName() + ".*")
                .timeUnit(TimeUnit.MILLISECONDS)
                .threads(1)

                .shouldFailOnError(true)
                .shouldDoGC(true)
                .build();

        new Runner(opt).run();
    }

    static ConfigurableApplicationContext context;

    private PresentationsController controller;

    @Setup(Level.Trial)
    public synchronized void startupSpring() {
        try {
            String args = "";
            if (context == null) {
                context = SpringApplication.run(Launch.class, args);
            }
            controller = context.getBean(PresentationsController.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @TearDown(Level.Trial)
    public synchronized void shutdownSpring() {
        try {
            if(context != null) {
                SpringApplication.exit(context);
                context = null;
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void benchmarkTemplate(int templateIdx) {
        try {
            controller.showList(mockRequests[templateIdx], templates[templateIdx], new ModelMap());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    @Benchmark
//    public void benchmarkJsp(LaunchJMH state, Blackhole bh) {
//        benchmarkTemplate(0);
//    }
//    @Benchmark
//    public void benchmarkFreemarker(LaunchJMH state, Blackhole bh) {
//        benchmarkTemplate(1);
//    }
//    @Benchmark
//    public void benchmarkVelocity(LaunchJMH state, Blackhole bh) {
//        benchmarkTemplate(2);
//    }
    @Benchmark
    public void benchmarkThymeleaf(LaunchJMH state, Blackhole bh) {
        benchmarkTemplate(3);
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
//    public void benchmarkHtmlFlow(LaunchJMH state, Blackhole bh) {
//        benchmarkTemplate(12);
//    }
//    @Benchmark
//    public void benchmarkTrimou(LaunchJMH state, Blackhole bh) {
//        benchmarkTemplate(13);
//    }
//    @Benchmark
//    public void benchmarkRocker(LaunchJMH state, Blackhole bh) {
//        benchmarkTemplate(14);
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