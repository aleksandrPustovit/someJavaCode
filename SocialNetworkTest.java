package twitter;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

public class SocialNetworkTest {

    /*
     * TODO: your testing strategies for these methods should go here.
     * Make sure you have partitions.
     */
    private static final Instant d1 = Instant.parse("2016-02-17T10:00:00Z");
    private static final Instant d2 = Instant.parse("2016-02-17T11:00:00Z");
    
    private static final Tweet tweet1 = new Tweet(1, "alyssa", "is it @alyssa reasonable to @miron talk about miron rivest so much?", d1);
    private static final Tweet tweet2 = new Tweet(2, "bbitdiddle", "For a bad @alyssa implementation, the grader test passes if a", d2);
    private static final Tweet tweet3 = new Tweet(3, "testmen", "Running your tests on @alyssa your implementation", d2);
    private static final Tweet tweet4 = new Tweet(4, "jobmen", "When @miron you run the @alyssa grader for P", d2);
    private static final Tweet tweet5 = new Tweet(5, "miron", "How to for @alyssa each the hashmap?", d2);
    
    
    private static final Tweet tweet99 = new Tweet(9, "bbitdiddle", "@A_LYSSP_ hi hi hi", Instant.now());
    private static final Tweet tweet33 = new Tweet(2, "bbitdiddle", "RT @a_lyssp_: no friends :(", Instant.now());
    private static final Tweet tweet22 = new Tweet(1, "a_lyssp_", "RT @a_lyssp_: no friends :(", Instant.now());
    
    public static Set<String> setToLowerCase(Set<String> strings) {
        Set<String> lowerSet = new HashSet<String>();
        for (String s: strings) {
            lowerSet.add(s.toLowerCase());
        }
        return lowerSet;
    }
    
    public static boolean setIsCaseInsensitiveUnique(Set<String> strings) {
        Set<String> lowerSet = new HashSet<String>();
        for (String s: strings) {
            if (lowerSet.contains(s.toLowerCase())) {
                return false;
            }
            lowerSet.add(s.toLowerCase());
        }
        return true;
    }
    
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; 
    }
    
    @Test
    public void testGuessFollowsGraphEmpty() {
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(new ArrayList<>());
        
        assertTrue("expected empty graph", followsGraph.isEmpty());
    }

    
    @Test
    public void testInfluencersEmpty() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        
        assertTrue("expected empty list", influencers.isEmpty());
    }

    @Test
    public void testInfluencersTwoUsersOneFollowing() {
        /*
         * Single tweet with one mention: should be length one, with the person who has been
         * mentioned ranking higher than the other person.
         */
        Map<String, Set<String>> network = new HashMap<String, Set<String>>();
        network.put("bbitdiddle", new HashSet<String>(Arrays.asList("a_lyssp_")));
        
        List<String> influencers = SocialNetwork.influencers(network);
        
        System.out.println("gg " + influencers );
        System.out.println("b" + influencers.get(0).toLowerCase().equals("a_lyssp_"));
        assertFalse("expected non-empty list", influencers.isEmpty());
    }
    
    @Test
    public void testGuessFollowsGraphUserMentionsSelf() {
        /*
         * Users should not be inferred as following themselves.
         */
        Map<String, Set<String>> network = SocialNetwork.guessFollowsGraph(Arrays.asList(tweet22));
        
        if (!network.isEmpty()) {
            assertEquals("map size", 1, network.keySet().size());
            for (String user : network.keySet()) {

                assertFalse("unexpected user following self", setToLowerCase(network.get(user)).contains(user.toLowerCase()));
            }
        }
    }
    
    
    @Test
    public void testGuessFollowsGraphSingleTweetAtMention() {
        /*
         * An at mention should result in a connection in the correct direction
         * for follows.
         */
        Map<String, Set<String>> network = SocialNetwork.guessFollowsGraph(Arrays.asList(tweet33));
        Map<String, Set<String>> canonicalNetwork = new HashMap<String, Set<String>>();

        Set<String> canonicalUsers = new HashSet<String>(Arrays.asList("a_lyssp_", "bbitdiddle"));
        Set<String> expectedKeyMap = new HashSet<String>(Arrays.asList("bbitdiddle"));

        canonicalNetwork.put("bbitdiddle", new HashSet<String>(Arrays.asList("a_lyssp_")));
        
        assertFalse("expected non-empty map", network.isEmpty());
        for (String user : network.keySet()) {
            if (canonicalNetwork.containsKey(user.toLowerCase())) {
                assertTrue("unexpected user(s) in map", canonicalUsers.contains(user.toLowerCase()));
                assertTrue("missing follow(s)", setToLowerCase(network.get(user)).containsAll(canonicalNetwork.get(user.toLowerCase())));
            }
        }

        assertTrue("missing key(s) in map", setToLowerCase(network.keySet()).containsAll(expectedKeyMap));
    }
    
    @Test
    public void testGuessFollowsGraphMustBeCaseInsensitive() {
        /*
         * Usernames should not be case-sensitive.
         */
        Map<String, Set<String>> network = SocialNetwork.guessFollowsGraph(Arrays.asList(tweet33, tweet99));
        Map<String, Set<String>> canonicalNetwork = new HashMap<String, Set<String>>();
        
        canonicalNetwork.put("bbitdiddle", new HashSet<String>(Arrays.asList("a_lyssp_")));
        assertFalse("expected non-empty map", network.isEmpty());
        assertTrue("expected case-insensitive usernames", setIsCaseInsensitiveUnique(network.keySet()));
        for (String user : network.keySet()) {
            assertTrue("expected case-insensitive usernames", setIsCaseInsensitiveUnique(network.get(user)));
            if (canonicalNetwork.containsKey(user.toLowerCase())) {
                assertTrue("missing follow(s)", setToLowerCase(network.get(user)).containsAll(canonicalNetwork.get(user.toLowerCase())));
            }
        }
        
    }
}
