package io.github.adr.eadlsync.cli.command;

import java.io.IOException;

import ch.hsr.isf.serepo.data.restinterface.commit.CommitMode;
import io.github.adr.eadlsync.data.TestDataProvider;
import io.github.adr.eadlsync.util.io.JavaDecisionParser;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 *
 */
public class CommitCommandTest extends CommandTest {

    private String commitId;

    @Before
    public void parseCommitCommand() throws IOException {
        commander.parse("commit", "-m", "some test commit");
        commitId = COMMIT_COMMAND.readCommitId();
    }

    @Test
    public void testNoChangesDoesNothing() throws Exception {
        COMMIT_COMMAND.commit();

        Assert.assertEquals(commitId, COMMIT_COMMAND.readCommitId());
    }

    @Test
    public void testLocalChangesCommitSuccessfully() throws Exception {
        JavaDecisionParser.writeModifiedYStatementToFile(TestDataProvider.createTestChangedModifiedYStatementJustificationWrapper());
        COMMIT_COMMAND.commit();

        Assert.assertNotEquals(commitId, COMMIT_COMMAND.readCommitId());
    }

    @Test
    public void testRemoteChangesCommitRejected() throws Exception {
        seRepoTestServer.createCommit(TestDataProvider.createChangedTestSeItemsWithContentModified(),
                CommitMode.ADD_UPDATE);
        COMMIT_COMMAND.commit();

        Assert.assertEquals(commitId, COMMIT_COMMAND.readCommitId());
    }

    @Test
    public void testLocalAndRemoteChangesCommitRejected() throws Exception {
        JavaDecisionParser.writeModifiedYStatementToFile(TestDataProvider.createTestChangedModifiedYStatementJustificationWrapper());
        seRepoTestServer.createCommit(TestDataProvider.createChangedTestSeItemsWithContentModified(),
                CommitMode.ADD_UPDATE);
        COMMIT_COMMAND.commit();

        Assert.assertEquals(commitId, COMMIT_COMMAND.readCommitId());
    }

}