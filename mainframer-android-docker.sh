#!/bin/bash

set -e

COMMAND=$1

IMAGE_NAME="mainframer-$( basename $(pwd))"

function executeRemotely {
    if [ "$#" -ne 1 ]; then
        echo "A command must be passed to executeRemotely."
        exit 0
    fi

    BUILD_COMMAND=$1
    BUILD_COMMAND_SUCCESSFUL="false"

    echo "Running '$BUILD_COMMAND' on $REMOTE_MACHINE."
    echo ""

    set +e
    if ssh "$REMOTE_MACHINE" "echo 'set -e && cd '$PROJECT_DIR_ON_REMOTE_MACHINE' && echo \"$BUILD_COMMAND\" && echo "" && $BUILD_COMMAND' | bash" ; then
        BUILD_COMMAND_SUCCESSFUL="true"
    fi
    set -e
    echo ""

    if [ "$BUILD_COMMAND_SUCCESSFUL" == "true" ]; then
        echo "Execution done."
    else
        echo "Execution failed."
        exit 1
    fi
}

function build {
    TARGET_SDK_CONFIG_PROPERTY="android_target_sdk"
    BUILD_TOOLS_CONFIG_PROPERTY="android_build_tools"
    SDK_TOOLS_CONFIG_PROPERTY="android_sdk_tools"

    source ./mainframer-init.sh

    TARGET_SDK=$(readConfigProperty "$TARGET_SDK_CONFIG_PROPERTY")
    BUILD_TOOLS=$(readConfigProperty "$BUILD_TOOLS_CONFIG_PROPERTY")
    SDK_TOOLS=$(readConfigProperty "$SDK_TOOLS_CONFIG_PROPERTY")

    echo "Building..."
    echo "Target SDK:  $TARGET_SDK"
    echo "Build tools: $BUILD_TOOLS"
    echo "SDK tools:   $SDK_TOOLS"
    echo ""
    eval $(docker-machine env vm)
    syncBeforeRemoteCommand
    #eval $(docker-machine env vm)
    executeRemotely "docker build -t $IMAGE_NAME . --build-arg ANDROID_TARGET_SDK=$TARGET_SDK --build-arg ANDROID_BUILD_TOOLS=$BUILD_TOOLS --build-arg ANDROID_SDK_TOOLS=$SDK_TOOLS"
}

function runbg {
    echo "Running in background (-d detached mode)"
    source ./mainframer-init.sh
    #eval $(docker-machine env vm)
    #./mainframer.sh "docker run -i --rm -v android:/root/.android -v sdk:/android-sdk-linux -v gradle:/root/.gradle -v $PROJECT_DIR_ON_REMOTE_MACHINE:/project:rw --name $IMAGE_NAME $IMAGE_NAME /project/gradlew ${@:2} -p /project"
    syncBeforeRemoteCommand
    ./mainframer.sh "docker run -i -d -v android:/root/.android -v sdk:/android-sdk-linux -v gradle:/root/.gradle -v $PROJECT_DIR_ON_REMOTE_MACHINE:/project:rw --name $IMAGE_NAME $IMAGE_NAME"
}

function run {
    echo "Running"
    source ./mainframer-init.sh
    syncBeforeRemoteCommand
    ./mainframer.sh "docker run -i --rm -v android:/root/.android -v sdk:/android-sdk-linux -v gradle:/root/.gradle -v $PROJECT_DIR_ON_REMOTE_MACHINE:/project:rw --name $IMAGE_NAME $IMAGE_NAME /project/gradlew ${@:2} -p /project"
}

function gradlew() {
    source ./mainframer-init.sh
    launch=""
    sync=""
    command=""
   while [[ $# -gt 0 ]]; do
        case "$1" in
            gradlew)
              shift
              command="docker exec -i $IMAGE_NAME /project/gradlew $1 -p /project -Dorg.gradle.daemon=true"
              ;;
            -s | --sync)
                sync="./gradlew $PROJECT_MODULE:generateDebugSources -Dorg.gradle.daemon=true"
                ;;
            -l | --launch)
                shift
                if [ "$1" == "all" ]; then
                    launch="adb devices | tail -n +2 | cut -sf 1 | xargs -I X adb -s X"
                else
                    launch="adb -s $1"
                fi
                echo "launch device $launch"
                ;;
            -h | --help)
                usage
                exit
                ;;
            -* | --*)
                echo "Invalid command line option: $1"
                usage
                exit
                ;;
            *)
                ;;
        esac
        #echo "arg: $1 : $#"
     shift
     done
     #echo "launch: $launch, sync:$sync"

     exit=1
     if [ -n "$command"  ]; then
        if ! ./mainframer.sh $command; then
          printf "\n$command returned some error.\n" >&2;
        else
          exit= 0
          echo "\n$command done.\n"
        fi
    fi

    result=`awk 'END {print $NF}' $(PWD)$REMOTE_LAST_COMMAND_FILE`
    echo "Remote command success: $result"
    if [ "$result" != "true" ]; then
      exit 1
    fi

     if [ -n "$launch" ]; then
          printf "\nInstalling ${OUTPUT_APK_PATH} to devices $launch $sync...\n"
          adb_command="adb devices | tail -n +2 | cut -sf 1 | xargs -I X"
          if eval "$launch install -r ${OUTPUT_APK_PATH}"; then
              printf "\nLaunching ${LAUNCHING_ACTIVITY}...\n"
              if eval "$launch shell am start -n ${LAUNCHING_ACTIVITY} -a android.intent.action.MAIN -c android.intent.category.LAUNCHER"; then
                printf "\nDone.\n"
              fi
          fi
     fi
     if [ -n "$sync" ]; then
        printf "\nExecuting $sync ...\n"
        eval $sync
        printf "\nDone.\n"
     fi
}

function exec {
    echo "Executing"
    source ./mainframer-init.sh
    syncBeforeRemoteCommand

     command="./mainframer.sh 'docker exec -i -w /project $IMAGE_NAME ${@:2}'"
     if ! $command; then
        echo "$@ returned some error"
     else
        echo "$@ done."
     fi
}

function prune {
    echo "Stopping"
    source ./mainframer-init.sh
    executeRemotely "docker container stop $IMAGE_NAME"
    executeRemotely "docker container prune -f"
    #executeRemotely "docker volume prune -f"
}

function usage {
    echo "Usage: $0 <COMMAND> <remote command | gradle task> [OPTIONS]"
    echo "COMMAND:"
    echo "  build   - build docker image"
    echo "  run     - run <gralde task> create and execute the specified gradle task on the  docker container"
    echo "  runbg   - create and run the  docker container in the background (-d option)"
    echo "  gradlew - gradlew <gradle task name> execute the specified gradle task on an existing docker container"
    echo "  exec    - exec <command> execute the specified command on an existing docker container"
    echo "  prune   - stop all existing container (see docker container prune)"
    echo "OPTIONS:"
    echo "--launch (-l) [device-id|all] - install and launch the result apk of the graldlew execution on"
    echo "  specified or all connected device(s) (only with 'gradlew' command)"
    echo "--sync   (-s) -  synclocal local sources after execution (only with 'gradlew' command)"
    echo "--help   (-h) -  print this usage"

}


case "$COMMAND" in
        build)
            build $@
            exit 0
            ;;

        run)
            run $@
            exit 0
            ;;
        runbg)
            runbg $@
            exit 0
            ;;
        gradlew)
          gradlew "$@"
          exit 0
          ;;
        exec)
            exec $@
            exit 0
            ;;
        launch)
            launch $@
            exit 0
            ;;
        prune)
            prune $@
            exit 0
            ;;
        *)
            usage

esac

exit 1
