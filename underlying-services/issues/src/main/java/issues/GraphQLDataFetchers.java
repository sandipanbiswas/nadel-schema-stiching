package issues;

import com.google.common.collect.ImmutableMap;
import graphql.schema.DataFetcher;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class GraphQLDataFetchers {

    private static List<Map<String, String>> issues = Arrays.asList(
            ImmutableMap.of("id", "issue-1",
                    "description", "The first issue",
                    "assigneeId", "user-1",
                    "authorId", "1"
            ),
            ImmutableMap.of("id", "issue-2",
                    "description", "The second issue",
                    "assigneeId", "user-2",
                    "authorId", "2"
            )
    );

    private static List<Map<String, String>> authors = Arrays.asList(
            ImmutableMap.of("id", "1",
                    "name", "Author1"
            ),
            ImmutableMap.of("id", "2",
                    "name", "author2"
            )
    );

    public DataFetcher getIssues() {
        return dataFetchingEnvironment -> issues;
    }

    public DataFetcher getAuthor() {
        return dataFetchingEnvironment -> {
            Map<String,String> issue = dataFetchingEnvironment.getSource();
            String authorId = issue.get("authorId");
            return authors.stream().filter(author -> author.get("id").equals(authorId)).findFirst().orElse(null);
        };
    }
}
