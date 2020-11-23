/*
 *  Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package io.ballerina.cli.cmd;

import io.ballerina.cli.TaskExecutor;
import io.ballerina.cli.task.CleanTargetDirTask;
import io.ballerina.cli.task.CompileTask;
import io.ballerina.cli.task.CreateBaloTask;
import io.ballerina.cli.task.CreateExecutableTask;
import io.ballerina.cli.task.CreateTargetDirTask;
import io.ballerina.cli.task.RunTestsTask;
import io.ballerina.cli.utils.FileUtils;
import io.ballerina.projects.Project;
import io.ballerina.projects.ProjectKind;
import io.ballerina.projects.directory.BuildProject;
import io.ballerina.projects.directory.SingleFileProject;
import io.ballerina.projects.util.ProjectConstants;
import io.ballerina.runtime.api.constants.RuntimeConstants;
import io.ballerina.runtime.internal.launch.LaunchUtils;
import org.ballerinalang.compiler.CompilerPhase;
import org.ballerinalang.tool.BLauncherCmd;
import org.wso2.ballerinalang.compiler.util.CompilerContext;
import org.wso2.ballerinalang.compiler.util.CompilerOptions;
import picocli.CommandLine;

import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

import static io.ballerina.cli.cmd.Constants.BUILD_COMMAND;
import static io.ballerina.runtime.api.constants.RuntimeConstants.SYSTEM_PROP_BAL_DEBUG;
import static org.ballerinalang.compiler.CompilerOptionName.COMPILER_PHASE;
import static org.ballerinalang.compiler.CompilerOptionName.EXPERIMENTAL_FEATURES_ENABLED;
import static org.ballerinalang.compiler.CompilerOptionName.LOCK_ENABLED;
import static org.ballerinalang.compiler.CompilerOptionName.OFFLINE;
import static org.ballerinalang.compiler.CompilerOptionName.PRESERVE_WHITESPACE;
import static org.ballerinalang.compiler.CompilerOptionName.SKIP_TESTS;
import static org.ballerinalang.compiler.CompilerOptionName.TEST_ENABLED;

/**
 * This class represents the "ballerina build" command.
 *
 * @since 2.0.0
 */
@CommandLine.Command(name = BUILD_COMMAND, description = "Ballerina build - Build Ballerina module(s) and generate " +
                                                         "executable output.")
public class BuildCommand implements BLauncherCmd {

    private final PrintStream outStream;
    private final PrintStream errStream;
    private Path projectPath;
    private boolean exitWhenFinish;
    private boolean skipCopyLibsFromDist;

    public BuildCommand() {
        this.projectPath = Paths.get(System.getProperty(ProjectConstants.USER_DIR));
        this.outStream = System.out;
        this.errStream = System.err;
        this.exitWhenFinish = true;
        this.skipCopyLibsFromDist = false;
    }

    public BuildCommand(Path projectPath, PrintStream outStream, PrintStream errStream, boolean exitWhenFinish,
                        boolean skipCopyLibsFromDist) {
        this.projectPath = projectPath;
        this.outStream = outStream;
        this.errStream = errStream;
        this.exitWhenFinish = exitWhenFinish;
        this.skipCopyLibsFromDist = skipCopyLibsFromDist;
    }

    public BuildCommand(Path projectPath, PrintStream outStream, PrintStream errStream, boolean exitWhenFinish,
                        boolean skipCopyLibsFromDist, String output) {
        this.projectPath = projectPath;
        this.outStream = outStream;
        this.errStream = errStream;
        this.exitWhenFinish = exitWhenFinish;
        this.skipCopyLibsFromDist = skipCopyLibsFromDist;
        this.output = output;
    }

    @CommandLine.Option(names = {"--compile", "-c"}, description = "Compile the source without generating " +
                                                                   "executable(s).")
    private boolean compile;

    @CommandLine.Option(names = {"--output", "-o"}, description = "Write the output to the given file. The provided " +
                                                                  "output file name may or may not contain the " +
                                                                  "'.jar' extension.")
    private String output;

    @CommandLine.Option(names = {"--offline"}, description = "Build/Compile offline without downloading " +
                                                              "dependencies.")
    private boolean offline;

    @CommandLine.Option(names = {"--skip-lock"}, description = "Skip using the lock file to resolve dependencies.")
    private boolean skipLock;

    @CommandLine.Option(names = {"--skip-tests"}, description = "Skip test compilation and execution.")
    private boolean skipTests;

    @CommandLine.Parameters
    private List<String> argList;

    @CommandLine.Option(names = {"--help", "-h"}, hidden = true)
    private boolean helpFlag;

    @CommandLine.Option(names = "--experimental", description = "Enable experimental language features.")
    private boolean experimentalFlag;

    @CommandLine.Option(names = "--debug", description = "run tests in remote debugging mode")
    private String debugPort;

    private static final String buildCmd = "ballerina build [-o <output>] [--sourceroot] [--offline] [--skip-tests]\n" +
            "                    [--skip-lock] {<ballerina-file | module-name> | -a | --all} [--] [(--key=value)...]";

    @CommandLine.Option(names = "--test-report", description = "enable test report generation")
    private boolean testReport;

    @CommandLine.Option(names = "--code-coverage", description = "enable code coverage")
    private boolean coverage;

    @CommandLine.Option(names = "--observability-included", description = "package observability in the executable " +
            "JAR file(s).")
    private boolean observabilityIncluded;

    public void execute() {
        if (this.helpFlag) {
            String commandUsageInfo = BLauncherCmd.getCommandUsageInfo(BUILD_COMMAND);
            this.errStream.println(commandUsageInfo);
            return;
        }

        // Sets the debug port as a system property, which will be used when setting up debug args before running tests.
        if (!this.skipTests && this.debugPort != null) {
            System.setProperty(SYSTEM_PROP_BAL_DEBUG, this.debugPort);
        }

        String[] args;
        if (this.argList == null) {
            args = new String[0];
            this.projectPath = Paths.get(System.getProperty(ProjectConstants.USER_DIR));
        } else if (this.argList.get(0).startsWith(RuntimeConstants.BALLERINA_ARGS_INIT_PREFIX)) {
            args = argList.toArray(new String[0]);
            this.projectPath = Paths.get(System.getProperty(ProjectConstants.USER_DIR));
        } else {
            args = argList.subList(1, argList.size()).toArray(new String[0]);
            this.projectPath = Paths.get(argList.get(0));
        }

        String[] userArgs = LaunchUtils.getUserArgs(args, new HashMap<>());
        // check if there are too many arguments.
        if (userArgs.length > 0) {
            CommandUtil.printError(this.errStream, "too many arguments.", buildCmd, false);
            CommandUtil.exitError(this.exitWhenFinish);
            return;
        }

        // load project
        Project project;
        boolean isSingleFileBuild = false;
        if (FileUtils.hasExtension(this.projectPath)) {
            if (this.compile) {
                CommandUtil.printError(this.errStream,
                        "'-c' or '--compile' can only be used with modules.", null, false);
                CommandUtil.exitError(this.exitWhenFinish);
                return;
            }
            try {
                project = SingleFileProject.load(this.projectPath);
            } catch (RuntimeException e) {
                CommandUtil.printError(this.errStream, e.getMessage(), null, false);
                CommandUtil.exitError(this.exitWhenFinish);
                return;
            }
            isSingleFileBuild = true;
        } else {
            // Check if the output flag is set when building all the modules.
            if (null != this.output) {
                CommandUtil.printError(this.errStream,
                        "'-o' and '--output' are only supported when building a single Ballerina " +
                                "file.",
                        "ballerina build -o <output-file> <ballerina-file> ",
                        true);
                CommandUtil.exitError(this.exitWhenFinish);
                return;
            }
            try {
                project = BuildProject.load(this.projectPath);
            } catch (RuntimeException e) {
                CommandUtil.printError(this.errStream, e.getMessage(), null, false);
                CommandUtil.exitError(this.exitWhenFinish);
                return;
            }
        }

        // Skip code coverage for single bal files if option is set
        if (project.kind().equals(ProjectKind.SINGLE_FILE_PROJECT) && coverage) {
            coverage = false;
            this.outStream.println("Code coverage is not yet supported with single bal files. Ignoring the flag " +
                    "and continuing the test run...");
        }

        CompilerContext compilerContext = project.projectEnvironmentContext().getService(CompilerContext.class);
        CompilerOptions options = CompilerOptions.getInstance(compilerContext);
        options.put(COMPILER_PHASE, CompilerPhase.CODE_GEN.toString());
        options.put(OFFLINE, Boolean.toString(this.offline));
        options.put(LOCK_ENABLED, Boolean.toString(!this.skipLock));
        options.put(SKIP_TESTS, Boolean.toString(this.skipTests));
        options.put(TEST_ENABLED, Boolean.toString(!this.skipTests));
        options.put(EXPERIMENTAL_FEATURES_ENABLED, Boolean.toString(this.experimentalFlag));
        options.put(PRESERVE_WHITESPACE, "true");

        TaskExecutor taskExecutor = new TaskExecutor.TaskBuilder()
                .addTask(new CleanTargetDirTask(), isSingleFileBuild)   // clean the target directory(projects only)
                .addTask(new CreateTargetDirTask()) // create target directory
//                .addTask(new ResolveMavenDependenciesTask()) // resolve maven dependencies in Ballerina.toml
                .addTask(new CompileTask(outStream, errStream)) // compile the modules
//                .addTask(new CreateLockFileTask(), this.skipLock || isSingleFileBuild)  // create a lock file if
                                                            // the given skipLock flag does not exist(projects only)
                .addTask(new CreateBaloTask(outStream), isSingleFileBuild) // create the BALO ( build projects only)
//                .addTask(new CopyResourcesTask()) // merged with CreateJarTask
//                .addTask(new CopyObservabilitySymbolsTask(), isSingleFileBuild)
                .addTask(new RunTestsTask(outStream, errStream, args, testReport, coverage),
                        this.skipTests || isSingleFileBuild)
                    // run tests (projects only)
                .addTask(new CreateExecutableTask(outStream, this.output), this.compile) //create the executable jar
                .addTask(new CleanTargetDirTask(), !isSingleFileBuild)  // clean the target dir(single bals only)
                .build();

        taskExecutor.executeTasks(project);
        if (this.exitWhenFinish) {
            Runtime.getRuntime().exit(0);
        }
    }

    @Override
    public String getName() {
        return BUILD_COMMAND;
    }

    @Override
    public void printLongDesc(StringBuilder out) {
        out.append("Build a Ballerina project and produce an executable JAR file. The \n");
        out.append("executable \".jar\" file will be created in the <PROJECT-ROOT>/target/bin directory. \n");
        out.append("\n");
        out.append("Build a single Ballerina file. This creates an executable .jar file in the \n");
        out.append("current directory. The name of the executable file will be \n");
        out.append("<ballerina-file-name>.jar. \n");
        out.append("\n");
        out.append("If the output file is specified with the -o flag, the output \n");
        out.append("will be written to the given output file name. The -o flag will only \n");
        out.append("work for single files. \n");
    }

    @Override
    public void printUsage(StringBuilder out) {
        out.append("  ballerina build [-o <output-file>] [--offline] [--skip-tests] [--skip-lock] " +
                   "{<ballerina-file | module-name> | -a | --all} [--] [(--key=value)...]\n");
    }

    @Override
    public void setParentCmdParser(CommandLine parentCmdParser) {
    }
}
