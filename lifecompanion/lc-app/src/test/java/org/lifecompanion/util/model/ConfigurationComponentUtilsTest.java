package org.lifecompanion.util.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.lifecompanion.framework.utils.Pair;

public class ConfigurationComponentUtilsTest {
    @Test
    public void testSimilarityScore() {
        Assertions.assertTrue(
                ConfigurationComponentUtils.getSimilarityScoreFor("mais", "maïs", s -> Pair.of(s, 1.0))
                        <
                        ConfigurationComponentUtils.getSimilarityScoreFor("mais", "mais", s -> Pair.of(s, 1.0))
        );
    }
}
