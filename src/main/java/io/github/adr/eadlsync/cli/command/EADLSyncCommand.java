package io.github.adr.eadlsync.cli.command;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.stream.Collectors;

import com.beust.jcommander.Parameter;
import io.github.adr.eadlsync.cli.CLI;
import io.github.adr.eadlsync.exception.EADLSyncException;
import io.github.adr.eadlsync.model.config.Config;
import io.github.adr.eadlsync.model.repo.CodeRepo;
import io.github.adr.eadlsync.model.repo.IRepo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.exceptions.UnirestException;
import io.github.adr.eadlsync.util.ystatement.YStatementConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Super class for any eadl-sync commands. Provides methods to read/write the config and the last commit id.
 */
class EADLSyncCommand {

    private static final Logger LOG = LoggerFactory.getLogger(EADLSyncCommand.class);
    static final Path PROJECT_ROOT = Paths.get(".").toAbsolutePath().normalize();
    static final Path EADL_ROOT = PROJECT_ROOT.resolve(".eadlsync");
    static final Path EADL_CONFIG = EADL_ROOT.resolve("config");
    static final Path EADL_REVISION = PROJECT_ROOT.resolve(".eadlsync-commitid");
    IRepo repo = null;
    Config config = null;

    @Parameter(names = {"-h", "--help"}, description = "Show the usage of this command", help = true)
    private boolean help = false;

    boolean readConfig() throws IOException {
        // we assume we only get called in the root directory of a project
        if (!Files.exists(EADL_CONFIG)) {
            printNotInitialized();
            return false;
        } else if (!Files.isReadable(EADL_CONFIG)) {
            CLI.println("Could not read config file, please check permissions.");
            return false;
        }
        ObjectMapper mapper = new ObjectMapper();
        try {
            this.config = mapper.readValue(EADL_CONFIG.toFile(), Config.class);
        } catch (IOException e) {
            LOG.error("Error reading eadl config file.", e);
            throw e;
        }
        return true;
    }

    String readCommitId() throws IOException {
        if (!Files.exists(EADL_REVISION)) {
            printNotInitialized();
            return "";
        } else {
            return Files.readAllLines(EADL_REVISION).stream().collect(Collectors.joining());
        }
    }

    void updateConfig() throws IOException {
        // we assume we only get called in the root directory of a project
        if (!Files.exists(EADL_CONFIG)) {
            printNotInitialized();
            return;
        } else if (!Files.isWritable(EADL_CONFIG)) {
            CLI.println("Could not write config file, please check permissions.");
            return;
        }
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writeValue(EADL_CONFIG.toFile(), config);
        } catch (IOException e) {
            LOG.error("Error writing eadl config to file.", e);
        }
    }

    void updateCommitId(String commitId) throws IOException{
        if (YStatementConstants.COMMIT_ID_PATTERN.matcher(commitId).matches()) {
            try {
                Files.write(EADL_REVISION, commitId.getBytes(), StandardOpenOption.CREATE);
                CLI.println(String.format("\tsync id -> %s", commitId));
            } catch (IOException e) {
                LOG.error("Error writing new commit revision to file.", e);
                throw e;
            }
        } else {
            CLI.println(String.format("Commit was rejected by the se-repo\n\t%s", commitId));
        }
    }

    void initializeRepo() throws IOException, UnirestException {
        if (config == null) readConfig();
        Path src = Paths.get(config.getConfigCore().getProjectRoot());
        repo = new CodeRepo(src, config.getConfigCore().getBaseUrl(), config.getConfigCore()
                .getProjectName(), readCommitId());

    }

    boolean notBlank(String value) {
        return value != null && !value.isEmpty();
    }

    private void printNotInitialized() {
        CLI.println("Eadlsync not initialized in this directory.");
        CLI.println(InitCommand.class);
    }

    void printHasToSyncNoConflict() {
        CLI.println("The local decisions and the ones in the se-repo have both changed and can be automatically merged.");
        CLI.println(SyncCommand.class);
    }

    void printHasToSyncConflict() {
        CLI.println("The local decisions and the ones in the se-repo have both changed and cannot be automatically merged.");
        CLI.println(SyncCommand.class);
    }

    void printHasToPull() {
        CLI.println("The decisions of the se-repo have changed.");
        CLI.println(PullCommand.class);
    }

    void printHasToCommit() {
        CLI.println("The local decisions have changed.");
        CLI.println(CommitCommand.class);
    }

    void printUpToDate() {
        CLI.println("The local decisions are in sync with the decisions of the se-repo.");
    }

    void printEadlSyncException(EADLSyncException execption) {
        switch (execption.getState()) {
            case CONFLICT:
                printHasToSyncConflict();
                break;
            case COMMIT:
                printHasToCommit();
                break;
            case PULL_FIRST:
                printHasToPull();
                break;
            case UP_TO_DATE:
                printUpToDate();
                break;
            case SYNCED:
                printUpToDate();
                break;
            case NONE:
            default:
                CLI.println(execption.getMessage());
                break;
        }
    }

    public boolean isHelp() {
        return help;
    }

}
