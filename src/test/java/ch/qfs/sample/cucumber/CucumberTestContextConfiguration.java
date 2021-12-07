package ch.qfs.sample.cucumber;

import ch.qfs.sample.JhipsterSampleApplicationApp;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;

@CucumberContextConfiguration
@SpringBootTest(classes = JhipsterSampleApplicationApp.class)
@WebAppConfiguration
public class CucumberTestContextConfiguration {}
