package com.se421.paths.regression.test.suites;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.se421.paths.regression.test.cases.XinuPathCountingTests;

/**
 * Runs all regression tests
 * 
 * Execute this class with Run As -> JUnit Plug-in Test
 * 
 * @author Ben Holland
 */
@RunWith(Suite.class)

@SuiteClasses({  
	XinuPathCountingTests.class 
	})

public class AllTests {}
