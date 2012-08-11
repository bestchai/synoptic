package dynoptic.model.fifosys.channel.channelstate;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import dynoptic.DynopticTest;
import dynoptic.model.fifosys.channel.channelid.ChannelId;
import dynoptic.model.fifosys.channel.channelstate.ChState;
import dynoptic.model.fifosys.channel.channelstate.ImmutableMultiChState;
import dynoptic.model.fifosys.gfsm.observed.ObsEvent;

public class ImmutibleMultiChStateTests extends DynopticTest {

    ChannelId cid1;
    ChannelId cid2;
    List<ChannelId> cids;
    ImmutableMultiChState mc;
    ImmutableMultiChState mc2;
    ImmutableMultiChState mc3;
    ImmutableMultiChState mc4;

    List<ChState> chStates;

    @Before
    public void setUp() throws Exception {
        super.setUp();

        cids = new ArrayList<ChannelId>(2);
        cid1 = new ChannelId(1, 2, 0);
        cid2 = new ChannelId(2, 1, 1);
        cids.add(cid1);
        cids.add(cid2);

        chStates = new ArrayList<ChState>(2);
        chStates.add(new ChState(cid1));
        chStates.add(new ChState(cid2));
    }

    @Test
    public void createEmpty() {
        mc = ImmutableMultiChState.fromChannelIds(cids);
        mc2 = ImmutableMultiChState.fromChannelIds(cids);

        // Make sure that the two instance pointers are identical, because of
        // internal caching.
        assertTrue(mc == mc2);
        assertTrue(mc.equals(mc2));
        assertTrue(mc.hashCode() == mc2.hashCode());

        // Now, attempt to create the same mc/mc2 instances, but by building
        // them from more explicit state instances.
        mc3 = ImmutableMultiChState.fromChannelStates(chStates);
        assertTrue(mc == mc3);
        assertTrue(mc.equals(mc3));

        cids = new ArrayList<ChannelId>(2);
        cid1 = new ChannelId(1, 2, 0);
        cid2 = new ChannelId(2, 2, 1);
        cids.add(cid1);
        cids.add(cid2);

        mc3 = ImmutableMultiChState.fromChannelIds(cids);
        assertFalse(mc == mc3);
        assertFalse(mc.equals(mc3));
    }

    @Test
    public void createFromQueues() {
        // Create config [[e],[]]
        ObsEvent sendE = ObsEvent.SendEvent("e", cid1);
        chStates.get(0).enqueue(sendE);
        mc = ImmutableMultiChState.fromChannelStates(chStates);

        // Create config [[],[]]
        mc2 = ImmutableMultiChState.fromChannelIds(cids);
        assertTrue(mc != mc2);
        assertTrue(!mc.equals(mc2));

        // Add e to [[],[]] at cid1 (index 0)
        // This should result in [[],[e]] and equal the first mc above.
        mc3 = mc2.getNextChState(sendE);
        assertTrue(mc == mc3);
        assertTrue(mc.equals(mc3));
        assertTrue(mc.hashCode() == mc3.hashCode());

        // Apply e again, resulting in: [[e,e],[]]
        mc4 = mc3.getNextChState(sendE);
        assertTrue(mc != mc4);
        assertTrue(mc2 != mc4);
        assertTrue(mc3 != mc4);

        // Consume e, resulting in: [[e],[]]
        ObsEvent recvE = ObsEvent.RecvEvent("e", cid1);
        mc4 = mc4.getNextChState(recvE);
        assertTrue(mc4 == mc3);

        // Execute a local event at pid 0, which should not change the state
        ObsEvent localE = ObsEvent.LocalEvent("e", 1);
        mc4 = mc4.getNextChState(localE);
        assertTrue(mc4 == mc3);
    }
}