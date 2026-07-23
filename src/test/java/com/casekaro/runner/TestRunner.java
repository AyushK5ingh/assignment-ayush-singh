package com.casekaro.runner;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.*;

/**
 * Cucumber Test Runner using JUnit 5 Platform Suite.
 * Discovers and runs all .feature files under src/test/resources/features.
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "com.casekaro.stepdefs,com.casekaro.hooks")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty")
public class TestRunner {
}
