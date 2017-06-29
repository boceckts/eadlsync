package com.eadlsync.cli.command;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.eadlsync.cli.CLI;
import com.eadlsync.exception.EADLSyncException;

import static com.eadlsync.cli.command.ResetCommand.DESCRIPTION;

/**
 * Reset command used to reset all decisions of the local code repository to the decisions of the configured se-repo project.
 *
 * @option help
 * @option commitId of the commit to reset to
 */
@Parameters(commandDescription = DESCRIPTION)
public class ResetCommand extends EADLSyncCommand {

    public static final String NAME = "reset";
    public static final String DESCRIPTION = "use 'eadlsync reset <commit-id>' to reset the local decisions to the decisions of the selected commit from the se-repo";

    @Parameter(names = {"-h", "--help"}, description = "Show the usage of this command", help = true)
    private boolean help = false;

    @Parameter(required = true)
    private String commitId;

    public void resetLocalChanges() throws Exception {
        if (readConfig()) {

            readDecisions();

            try {
                CLI.println(String.format("Reset to %s from se-repo", commitId));
                CLI.println(String.format("\tproject '%s' at %s", config.getCore().getProjectName(), config.getCore().getBaseUrl()));
                repo.reset(commitId);
                updateCommitId(commitId);
                CLI.println(String.format("\tsync id -> %s", commitId));
            } catch (EADLSyncException e) {
                printEadlSyncException(e);
            }
        }
    }

    public boolean isHelp() {
        return help;
    }
}
