package com.atlassian.graphqlgatewaydemo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.TEXT_HTML_VALUE;

@RestController
public class GraphQLPlaygroundController {

    @GetMapping(path = "/graphql",
            produces = TEXT_HTML_VALUE)
    @ResponseBody
    public Resource graphqlPlayground(@Value("classpath:/html/graphql-playground.html") Resource html) {
        return html;
    }
}
