#!/bin/sh
JARFile="lib/grannyz-image-uploader.jar"
PIDFile="tmp/grannyz-image-uploader.pid"
SPRING_OPTS="--spring.config.location=config/config.properties -Dspring.profiles.active=prod"
export JAVA_HOME=/lib64/jvm/java-8-openjdk

function check_if_process_is_running {
    if ps -p $(print_process) > /dev/null
    then
        return 0
    else
        return 1
    fi
}

function print_process {
    echo $(<"$PIDFile")
}

case "$1" in
    status)
        if [ ! -f $PIDFile ]
            then
                echo "Image Uploader process is not running"
                exit 1
        elif check_if_process_is_running
            then
                echo "Image Uploader process: $(print_process) is running"
        else
            echo "Image Uploader process is not running: $(print_process)"
        fi
    ;;
    stop)
        if [ ! -f $PIDFile ] && ! check_if_process_is_running
        then
            echo "Process $(print_process) already stopped"
        exit 0
        fi
        kill -TERM $(print_process)
        echo -ne "Waiting for Image Uploader to stop"
        NOT_KILLED=1
        for i in {1..20}; do
           if check_if_process_is_running
             then
                echo -ne "."
                  sleep 1
                    else
                        NOT_KILLED=0
                    fi
        done
        echo
        if [ $NOT_KILLED = 1 ]
            then
                echo "Cannot kill Image Uploader process $(print_process)"
                exit 1
        fi
             echo "Image Uploader is stopped"
    ;;
    start)
        if [ -f $PIDFile ] && check_if_process_is_running
            then
                echo "Image Uploader is $(print_process) already running"
            exit 1
        fi
        java -Xms4096m -Xmx6144m  -jar $JARFile $SPRING_OPTS & echo $! > ${PIDFile}
        echo "Image Uploader is started"
    ;;
    restart)
        $0 stop
        if [ $? = 1 ]
            then
                exit 1
        fi
        $0 start
    ;;
    *)
    echo "Usage: $0 {start|stop|restart|status}"
    exit 1
    esac
exit 0