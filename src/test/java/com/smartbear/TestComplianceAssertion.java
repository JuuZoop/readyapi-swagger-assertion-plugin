package com.smartbear;

import com.eviware.soapui.config.TestAssertionConfig;
import com.eviware.soapui.impl.support.http.HttpRequestTestStep;
import com.eviware.soapui.impl.wsdl.WsdlProjectPro;
import com.eviware.soapui.impl.wsdl.testcase.WsdlTestCase;
import com.eviware.soapui.impl.wsdl.testcase.WsdlTestCaseRunner;
import com.eviware.soapui.impl.wsdl.teststeps.RestRequestStepResult;
import com.eviware.soapui.impl.wsdl.teststeps.WsdlMessageExchangeTestStepResult;
import com.eviware.soapui.impl.wsdl.teststeps.registry.HttpRequestStepFactory;
import com.eviware.soapui.model.testsuite.Assertable;
import com.eviware.soapui.support.types.StringToObjectMap;
import com.eviware.soapui.support.xml.XmlObjectConfigurationBuilder;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;

import static junit.framework.TestCase.assertEquals;

public class TestComplianceAssertion {

    @Test
    public void testComplianceAssertion() throws Exception {

        // create the project
        WsdlProjectPro project = new WsdlProjectPro();
        WsdlTestCase wsdlTestCase = project.addNewTestSuite("TestSuite").addNewTestCase("TestCase");
        HttpRequestTestStep testStep =
            (HttpRequestTestStep) wsdlTestCase.addTestStep(HttpRequestStepFactory.HTTPREQUEST_TYPE, "Request","https://api.swaggerhub.com/apis", "GET" );

        // run it
        WsdlTestCaseRunner runner = wsdlTestCase.run(new StringToObjectMap(), false);
        RestRequestStepResult result = (RestRequestStepResult) runner.getResults().get( 0 );

        // create the assertion
        TestAssertionConfig config = TestAssertionConfig.Factory.newInstance();
        XmlObjectConfigurationBuilder builder = new XmlObjectConfigurationBuilder();
        builder.add("swaggerUrl", "https://api.swaggerhub.com/swagger.json");
        config.setConfiguration( builder.finish() );

        SwaggerComplianceAssertion assertion = new SwaggerComplianceAssertion(config, testStep);

        // test it
        Assertable.AssertionStatus status = assertion.assertResponse(result.getMessageExchanges()[0], runner.getRunContext());
        assertEquals(Arrays.toString(assertion.getErrors()), Assertable.AssertionStatus.VALID, status);
    }
}
