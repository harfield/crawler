#!/bin/bash

work_dir=$(cd $(dirname $0); pwd)

nohup java -cp ${work_dir}/../etc:${work_dir}/../lib/easycrawler-jar-with-dependencies.jar com.harfied.crawler.apps.Injector >/dev/null 2>&1 &
