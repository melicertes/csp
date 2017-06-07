package com.intrasoft.csp.integration;

import org.junit.internal.TextListener;
import org.junit.runner.JUnitCore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by iskitsas on 4/27/17.
 */
public class MainAppTest {
    private static final Logger LOG = LoggerFactory.getLogger(MainAppTest.class);
    public static void main(String[] args) throws ClassNotFoundException {
        LOG.info("Running tests!");

        JUnitCore engine = new JUnitCore();
        engine.addListener(new TextListener(System.out)); // required to print reports
        Class[] classArr = new Class[args.length];

        for(int i=0; i< args.length; i++){
            String arg = args[i];
            classArr[i] = Class.forName(arg);
        }

        if(classArr.length>0) {
            engine.run(classArr);
        }else{
            LOG.info("Nothing to execute..");
        }
    }

}
