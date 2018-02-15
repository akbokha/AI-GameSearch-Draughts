package nl.tue.s2id90.contest;

/**
 *
 * @author huub
 */
public interface Provider {

    /**
     *
     * @return
     */
    String getName();

    /**
     *
     */
    void start();

    /**
     *
     */
    void stop();
}
