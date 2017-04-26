package com.sastix.csp.integration;

import com.sastix.csp.integration.sandbox.server.internal.CspServerInternalSandboxTest;
import org.junit.internal.TextListener;
import org.junit.runner.JUnitCore;

/**
 * Created by iskitsas on 4/27/17.
 */
public class MainAppTest {
    public static void main(String[] args) throws ClassNotFoundException {
        System.out.println("Running tests!");

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
            System.out.println("Nothing to execute..");
        }
    }

}
