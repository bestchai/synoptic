package synoptic.tests.units;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import synoptic.util.time.ITimeSeries;
import synoptic.util.time.ITotalTime;

public class TransitionTests {

    private ITimeSeries<ITotalTime> times;

    @Before
    public void resettimes() {
        times = new ITimeSeries();
    }

    // Does it compute the mode with only one value?
    @Test
    public void modeDeltaTestOneValue() {
        times.addDelta(new ITotalTime(1));
        assertEquals(new ITotalTime(1), times.computeMode());
    }

    // Does it compute the mode with no values?
    @Test
    public void modeDeltaTestEmpty() {
        assertNull(times.computeMode());
    }

    // Does it compute the mode with many arbitrary values
    // (one is the most used).
    @Test
    public void modeDeltaTestManyValues() {
        times.addDelta(new ITotalTime(1));
        times.addDelta(new ITotalTime(8));
        times.addDelta(new ITotalTime(2));
        times.addDelta(new ITotalTime(1));
        times.addDelta(new ITotalTime(8));
        times.addDelta(new ITotalTime(1));
        times.addDelta(new ITotalTime(16));
        times.addDelta(new ITotalTime(16));
        times.addDelta(new ITotalTime(1));
        times.addDelta(new ITotalTime(8));
        times.addDelta(new ITotalTime(1));
        assertEquals(new ITotalTime(1), times.computeMode());
    }

    // Does the median work with one value?
    @Test
    public void medianTestOneValue() {
        times.addDelta(new ITotalTime(1));
        assertEquals(new ITotalTime(1), times.computeMedian());
    }

    @Test
    public void medianTestEmpty() {
        assertNull(times.computeMedian());
    }

    @Test
    public void medianTestManyValues() {
        for (int i = 1; i <= 5; i++) {
            times.addDelta(new ITotalTime(i));
        }

        assertEquals(new ITotalTime(3), times.computeMedian());
    }
}