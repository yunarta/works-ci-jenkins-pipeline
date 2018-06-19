def call(Map config) {
    def command = config.command
    def avd = config.avd

    if (command == "start") {
        sh """set +e
        $ANDROID_HOME/emulator/emulator @$avd -no-window&"""

        echo "Emulator is started"

        sh '''set +e
        bootanim=""
        failcounter=0
        timeout_in_sec=360
        
        until [[ "$bootanim" =~ "stopped" ]]; do
          bootanim=`$ANDROID_HOME/platform-tools/adb -e shell getprop init.svc.bootanim 2>&1 &`
          echo $bootanim
          if [[ "$bootanim" =~ "device not found" || "$bootanim" =~ "device offline"
            || "$bootanim" =~ "running" ]]; then
            let "failcounter += 1"
            echo "Waiting for emulator to start"
            if [[ $failcounter -gt timeout_in_sec ]]; then
              echo "Timeout ($timeout_in_sec seconds) reached; failed to start emulator"
              exit -1
            fi
          fi
          sleep 1
        done'''

        echo "Emulator is ready"
    } else if (command == "stop") {
        sh """set +e
        $ANDROID_HOME/platform-tools/adb emu kill"""
        echo "Emulator is killed"

    }
}